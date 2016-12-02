package uw.virtualpin;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PinFragment extends Fragment {

    private static final String URL = "http://cssgate.insttech.washington.edu/~_450team8/info.php?cmd=display_pin&id=";
    ArrayList<String> pinDetails;
    private TextView creatorText;
    private TextView locationText;
    private EditText messageText;
    private ImageView imageView;
    private ImageManager imageManager;
    private GetPinAsyncTask task;


    public PinFragment() {
        pinDetails = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_pin, container, false);
        pinDetails = getArguments().getStringArrayList("PINS");

        imageManager = new ImageManager();
        task = new GetPinAsyncTask();
        task.execute(URL + pinDetails.get(0));
        creatorText = (TextView) view.findViewById(R.id.creatorTextHistory);
        locationText = (TextView) view.findViewById(R.id.locationTextHistory);
        messageText = (EditText) view.findViewById(R.id.messageTextHistory);
        imageView = (ImageView) view.findViewById(R.id.imageViewHistory);

        Toast.makeText(getActivity().getApplicationContext(), pinDetails.toString(), Toast.LENGTH_LONG);

//        setupPinDetails();
        return view;
    }

    private void setupPinDetails() {
        try {

            creatorText.setText("Created by: " + pinDetails.get(1));
            locationText.setText("Location: " + pinDetails.get(2));
            messageText.setText(pinDetails.get(3));
            imageView.setImageBitmap(imageManager.convertEncodedImageToBitmap(pinDetails.get(4)));

        } catch (Exception e) {
            Snackbar.make(getView(), "Error loading pin, please try again.", Snackbar.LENGTH_LONG);
        }
    }

    private void parseJson(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            //JSONObject jsonObject = jsonArray.getJSONObject(0);
            //pinDetails.set(4, jsonObject.getString("image"));
            //setupPinDetails();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetPinAsyncTask extends AsyncTask<String, Integer, String> {

        Snackbar snackbar;

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    snackbar = Snackbar.make(getView(), "Loading Pin, please wait...", Snackbar.LENGTH_INDEFINITE);
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

            if(task.isCancelled()) {
                return;
            }

            if (result.length() > 10) {

                snackbar = Snackbar.make(getView(), "Pin Retrieved", Snackbar.LENGTH_SHORT);
                snackbar.show();
                parseJson(result);

            } else {
                snackbar = Snackbar.make(getView(), "Unable to retrieve pin history, please try again.", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }
    }
}
