package uw.virtualpin;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/*

Author: Tyler Brent

This class handles creating the list of pins that the user has posted.

 */
public class PostHistoryFragment extends Fragment {

    private static final String URL = "http://cssgate.insttech.washington.edu/~_450team8/info.php?cmd=pin_history&username=";
    private ListView postsList;
    private ArrayList<Pin> pins;
    private String username;
    private PostHistoryAsyncTask task;


    /**
     * Public contructor that initializes variables.
     */
    public PostHistoryFragment() {
        pins = new ArrayList<>();
    }


    /**
     * Overloaded onCreateView inializes variables and starts the async task to retrieve the user
     * pin history.
     *
     * @param inflater the inflater for the fragment.
     * @param container the container for the fragment.
     * @param savedInstanceState the saved state of the fragment.
     * @return the current view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_post_history, container, false);
        postsList = (ListView) view.findViewById(R.id.postsList);

        if (savedInstanceState == null) {
            Bundle extras = getActivity().getIntent().getExtras();
            if (extras == null) {
                username = null;
            } else {
                username = extras.getString("USERNAME");
            }
        } else {
            username = (String) savedInstanceState.getSerializable("USERNAME");
        }

        task = new PostHistoryAsyncTask();
        task.execute(URL + username);

        return view;
    }

    /**
     * Overridden onPause cancels the async task. This saves the app time and the potential for crashing
     * if the user hits the back button before the async task is complete.
     */
    @Override
    public void onPause() {
        super.onPause();
        Snackbar.make(getView(), "Exiting Pin History", Snackbar.LENGTH_SHORT);
        task.cancel(true);
    }

    /**
     * Gets all the messages from the pin history.
     *
     * @return a list of the messages.
     */
    private ArrayList<String> getMessages() {
        ArrayList<String> pinMessages = new ArrayList<>();
        int messageNumber = 1;
        int maxWidth = 30;
        int padding = 3;

        for(Pin pin : pins) {
            String message = pin.getMessage();

            if(message.length() >= maxWidth) {
                StringBuilder stringBuilder = new StringBuilder(message);
                stringBuilder.setLength(maxWidth - padding);
                stringBuilder.append("...");
                message = stringBuilder.toString();
            }

            pinMessages.add(messageNumber + ": " + message);
            messageNumber++;
        }

        return pinMessages;
    }

    /**
     * Sets up the list view to display all the pins that the user has posted.
     *
     * @param listView the listview used to display the pin data.
     * @param stringList the list used to populate the list view.
     */
    private void setupListView(ListView listView, ArrayList<String> stringList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                R.layout.listview_item, stringList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                ArrayList<String> pinData = new ArrayList<String>();
                Pin pin = pins.get(position);

                pinData.add(pin.getId());
                pinData.add(pin.getUserName());
                pinData.add("(" + pin.getLatitude() + ", " + pin.getLongitude() + ")");
                pinData.add(pin.getMessage());

                PinFragment pinFragment = new PinFragment();
                Bundle args = new Bundle();
                args.putStringArrayList("PINS", pinData);
                pinFragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, pinFragment).commit();
            }
        });
    }

    /**
     * Parses the JSON object returned from the async task and sets up the pin object needed.
     * As well, all pin objects that are created are stored in a list for later use.
     *
     * @param jsonString the jsonString.
     * @param attempt the number of attempts to parse.
     */
    private void parseJSON(String jsonString, int attempt) {

        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int index = 0; index < jsonArray.length(); index++) {
                JSONObject jsonObject = jsonArray.getJSONObject(index);

                Pin pin = new Pin(jsonObject.getString("creator")
                        ,Double.parseDouble(jsonObject.getString("latitude"))
                        ,Double.parseDouble(jsonObject.getString("longitude"))
                        ,jsonObject.getString("message")
                        ,null);

                pin.setId(jsonObject.getString("pinID"));

                pins.add(pin);
            }
        } catch (JSONException e) {
            if(attempt >= 2) {

                Snackbar.make(getView(), "Error, please reload this page.", Snackbar.LENGTH_LONG);
                return;
            } else {
                attempt++;
                parseJSON(jsonString, attempt);
            }
        }

        setupListView(postsList, getMessages());
    }

    /**
     * The async task used to get the post history. The corresponding URL is a member variable above.
     */
    private class PostHistoryAsyncTask extends AsyncTask<String, Integer, String> {

    Snackbar snackbar;

    @Override
    protected String doInBackground(String... urls) {
        String response = "";
        HttpURLConnection urlConnection = null;
        for (String url : urls) {
            try {
                snackbar = Snackbar.make(getView(), "Loading Pins, please wait...", Snackbar.LENGTH_INDEFINITE);
                snackbar.show();

                URL urlObject = new URL(url);
                urlConnection = (HttpURLConnection) urlObject.openConnection();

                InputStream content = urlConnection.getInputStream();

                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }

            } catch (Exception e) {
                response = "Unable to complete your request, Reason: "
                        + e.getMessage();
            }
            finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
        }

        if(task.isCancelled()) {
            snackbar.dismiss();
        }

        return response;
    }

    @Override
    protected void onPostExecute(String result) {

        if (result.length() > 10) {

            snackbar = Snackbar.make(getView(), "Pin history retrieved", Snackbar.LENGTH_SHORT);
            snackbar.show();

            parseJSON(result, 0);

        } else {
            snackbar = Snackbar.make(getView(), "Unable to retrieve pin history, please try again.", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }
}
}
