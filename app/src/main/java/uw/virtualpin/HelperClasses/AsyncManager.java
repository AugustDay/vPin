package uw.virtualpin.HelperClasses;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import uw.virtualpin.Interfaces.OnCompletionListener;
import uw.virtualpin.Data.Pin;

/**
 * Created by tyler on 12/9/2016.
 */

public class AsyncManager extends AsyncTask<String, Integer, String> {

    private final static String URL = "http://cssgate.insttech.washington.edu/~adi1996/info.php?cmd=";
    private final static String CREATE_PIN_CMD = "new_pin";
    private final static String DELETE_PIN_CMD = "delete_pin";
    private final static String PIN_HISTORY_CMD = "pin_history";
    private final static String ALL_PINS_CMD = "select*users";
    private final static String GET_PIN_CMD = "get_pin";
    private final static String UPDATE_PIN_CMD = "update_pin";
    private final static String NEARBY_PINS = "nearby_pins";
    private Snackbar snackbar;
    private View view;
    private String image;
    private String asyncStartMessage;
    private String asyncFinishedMessage;
    private String asyncErrorMessage;
    private OnCompletionListener onCompletionListener;

    public AsyncManager(View view, OnCompletionListener onCompletionListener) {
        this.onCompletionListener = onCompletionListener;
        this.view = view;
        image = "NO_IMAGE";
        asyncStartMessage = "Starting process...";
        asyncFinishedMessage = "Process finished.";
        asyncErrorMessage = "Error processing request.";
    }

    public void createPin(Pin pin) {
        String extension =
                "&username="
                + pin.getUserName()
                + "&latitude="
                + pin.getLatitude()
                + "&longitude="
                + pin.getLongitude()
                + "&message="
                + pin.getMessage();

        Log.e("Location Changed: ", extension);

        image = pin.getEncodedImage();

        execute(URL + CREATE_PIN_CMD + extension);
    }

    public void deletePin(String pinId) {
        String extension = "&pinID=" + pinId;
        Log.e("DELETE", URL + DELETE_PIN_CMD + extension);
        execute(URL + DELETE_PIN_CMD + extension);
    }

    public void pinHistory(String username) {
        String extension = "&username=" + username;
        execute(URL + PIN_HISTORY_CMD + extension);
    }

    public void allPins() {
        execute(URL + ALL_PINS_CMD);
    }

    public void getPin(String pinId) {
        String extension = "&id=" + pinId;
        execute(URL + GET_PIN_CMD + extension);
    }

    public void updatePin(String pinId, String message) {
        String extension = "&id=" + pinId
                + "&message=" + message;
        execute(URL + UPDATE_PIN_CMD + extension);
    }

    public void updatePin(String pinId, String message, String encodedImage) {
        String extension = "&id=" + pinId
                + "&message=" + message;
        execute(URL + UPDATE_PIN_CMD + extension);
    }

    public void nearbyPins(String latitude, String longitude) {
        String extension = "&latitude=" + latitude
                + "&longitude=" + longitude;
        Log.e("URL", URL + NEARBY_PINS + extension);
        execute(URL + NEARBY_PINS + extension);
    }

    public void customAsyncRequest(String customUrl) {
        execute(customUrl);
    }

    public void setStartMessage(String message) {
        asyncStartMessage = message;
    }

    public void setFinishedMessage(String message) {
        asyncFinishedMessage = message;
    }

    public void setErrorMessage(String message) {
        asyncErrorMessage = message;
    }

    public void setImage(String encodedImage) {
        image = encodedImage;
    }

    public AsyncManager resetAsyncManager() {
        AsyncManager copy = new AsyncManager(this.view, this.onCompletionListener);
        return copy;
    }

    private boolean valid(String result) {
        if(result.contains("false")) {
            return false;
        }

        if(result.contains("error")) {
            return false;
        }

        if(result.contains("unable")) {
            return false;
        }

        return true;
    }

    @Override
    protected String doInBackground(String... urls) {
        String response = "";
        HttpURLConnection urlConnection = null;
        for (String url : urls) {

            snackbar = Snackbar.make(view, asyncStartMessage, Snackbar.LENGTH_INDEFINITE);
            snackbar.show();

            try {
                URL urlObject = new URL(url);
                urlConnection = (HttpURLConnection) urlObject.openConnection();

                if(!image.equalsIgnoreCase("NO_IMAGE")) {

                    urlConnection.setDoOutput(true);
                    String data = URLEncoder.encode("image", "UTF-8")
                            + "=" + URLEncoder.encode(image, "UTF-8");

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
                response = asyncErrorMessage
                        + e.getMessage();
            }
            finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        if (valid(result)) {
            snackbar = Snackbar.make(view, asyncFinishedMessage, Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else {
            snackbar = Snackbar.make(view, asyncErrorMessage, Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        image = "NO_IMAGE";
        Log.e("RESULT", result);
        onCompletionListener.onComplete(result);
    }
}
