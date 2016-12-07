package uw.virtualpin;

import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/*

Author: Tyler Brent

This class represents the pin object after an item is clicked in the PostHistory list.
It is the expanded view of that pin. Here you can update any messsages you've left in the pin.
You cannot update the image, although that was a feature we tried to implement but ran out of time
due to a bug with the design.

Note: below is some commented out code that will be used to update the pin image. It is left intenionally
from a previous attempt.

 */

public class PinFragment extends Fragment {

    private static final String UPDATE_URL = "http://cssgate.insttech.washington.edu/~_450team8/info.php?cmd=update_pin&id=";
    private static final String GET_URL = "http://cssgate.insttech.washington.edu/~_450team8/info.php?cmd=get_pin&id=";
    ArrayList<String> pinDetails;
    private TextView creatorText;
    private TextView locationText;
    private EditText messageText;
    private ImageView imageView;
    private Button updateButton;
    private String encodedImage;
    private ImageManager imageManager;
    private GetPinAsyncTask getPinAsyncTask;
    private UpdatePinAsyncTask updatePinAsyncTask;
    private static final int PICK_IMAGE = 100;
    boolean filled;


    /**
     * Public contructor to initialize variables.
     */
    public PinFragment() {
        pinDetails = new ArrayList<>();
    }


    /**
     * Overloaded onCreateView is used to inialize variables. It also sets up the update message
     * button.
     *
     * @param inflater the inflater for the fragment.
     * @param container the container for the fragment.
     * @param savedInstanceState the saved state of the fragment.
     * @return the current view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_pin, container, false);
        pinDetails = getArguments().getStringArrayList("PINS");

        filled = false;
        imageManager = new ImageManager();
        creatorText = (TextView) view.findViewById(R.id.creatorTextHistory);
        locationText = (TextView) view.findViewById(R.id.locationTextHistory);
        messageText = (EditText) view.findViewById(R.id.messageTextHistory);
        imageView = (ImageView) view.findViewById(R.id.imageViewHistory);
        updateButton = (Button) view.findViewById(R.id.updateButton);

//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openGallery();
//            }
//        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageText.getText().toString();
                encodedImage = imageManager.convertBitmapToByteArray
                        (((BitmapDrawable) imageView.getDrawable()).getBitmap());
                String completeUrl = UPDATE_URL + pinDetails.get(0);

                if(message.equalsIgnoreCase(pinDetails.get(3))) {
                    Snackbar.make(view, "No message changes detected.", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if(message.length() == 0) {
                    Snackbar.make(view, "Cannot enter an empty message.", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if(message.length() > 0) {
                    completeUrl += "&message=" + message;
                }

                updatePinAsyncTask = new UpdatePinAsyncTask();
                updatePinAsyncTask.execute(completeUrl);
            }
        });

        return view;
    }

    /**
     * Overloaded onStart initializes the async task to populate the pin data.
     */
    public void onStart() {
        super.onStart();
        if(filled == false) {
            filled = true;
            getPinAsyncTask = new GetPinAsyncTask();
            getPinAsyncTask.execute(GET_URL + pinDetails.get(0));
        }
    }

    /**
     * Sets pin details sets all the pin details to their view objects.
     */
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

    /**
     * Parses the json string given by the async task.
     *
     * @param jsonString the json string.
     */
    private void parseJson(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            pinDetails.add(jsonObject.getString("image"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        setupPinDetails();
    }

//    private void openGallery() {
//        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//        startActivityForResult(gallery, PICK_IMAGE);
//    }
//
//    @Override
//    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
//            Uri imageUri = data.getData();
//            imageView.setImageURI(imageUri);
//        }
//    }

    /**
     * The async task to get the pin details from the web service.
     */
    private class GetPinAsyncTask extends AsyncTask<String, Integer, String> {

        Snackbar snackbar;

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    snackbar = Snackbar.make(getView(), "Laoding pin, please wait...", Snackbar.LENGTH_INDEFINITE);
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

            if(getPinAsyncTask.isCancelled()) {
                snackbar.dismiss();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {

            if (result.length() > 10) {

                Log.e("TEST", result);
                snackbar = Snackbar.make(getView(), "Pin retrieved.", Snackbar.LENGTH_SHORT);
                snackbar.show();
                parseJson(result);

            } else {
                snackbar = Snackbar.make(getView(), "Unable to retrieve pin history, please try again.", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }
    }

    private class UpdatePinAsyncTask extends AsyncTask<String, Integer, String> {

        Snackbar snackbar;

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    snackbar = Snackbar.make(getView(), "Updating pin, please wait...", Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();

                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    if(!encodedImage.equalsIgnoreCase("NO_IMAGE")) {

                        urlConnection.setDoOutput(true);
                        String data = URLEncoder.encode("image", "UTF-8")
                                + "=" + URLEncoder.encode(encodedImage, "UTF-8");

                        OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                        wr.write(data);
                        wr.flush();
                    }

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

            if(getPinAsyncTask.isCancelled()) {
                snackbar.dismiss();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {

            if(getPinAsyncTask.isCancelled()) {
                snackbar.dismiss();
                return;
            }

            if (result.length() > 10) {

                snackbar = Snackbar.make(getView(), "Successfully updated pin.", Snackbar.LENGTH_SHORT);
                snackbar.show();

            } else {
                snackbar = Snackbar.make(getView(), "Unable to update pin, please try again.", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }
    }
}
