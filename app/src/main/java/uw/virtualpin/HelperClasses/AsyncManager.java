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
    private final static String NEARBY_PINS_CMD = "nearby_pins";
    private final static String UPVOTE_PIN_CMD = "upvote";
    private final static String DOWNVOTE_PIN_CMD = "downvote";
    private final static String FAVORITE_PIN_CMD = "favorite_pin";
    private final static String GET_USER_FAVORITES_CMD = "get_favorite_pins";
    private Snackbar snackbar;
    private View view;
    private String image;
    private String asyncStartMessage;
    private String asyncFinishedMessage;
    private String asyncErrorMessage;
    private OnCompletionListener onCompletionListener;
    private boolean showStartMessage;
    private boolean showFinishedMessage;


    public AsyncManager(View view, OnCompletionListener onCompletionListener) {
        this.onCompletionListener = onCompletionListener;
        this.view = view;
        image = "NO_IMAGE";
        asyncStartMessage = "Starting process...";
        asyncFinishedMessage = "Process finished.";
        asyncErrorMessage = "Error processing request.";
        showFinishedMessage = true;
        showStartMessage = true;
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

        image = pin.getEncodedImage();
        execute(URL + CREATE_PIN_CMD + extension);
    }

    public void deletePin(String pinId) {
        String extension = "&pinID=" + pinId;
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
        execute(URL + NEARBY_PINS_CMD + extension);
    }

    public void upvotePin(String pinId) {
        String extension = "&pinID=" + pinId;
        execute(URL + UPVOTE_PIN_CMD + extension);
    }

    public void downvotePin(String pinId) {
        String extension = "&pinID=" + pinId;
        execute(URL + DOWNVOTE_PIN_CMD + extension);
    }

    public void favoritePin(String username, String pinId) {
        String extension = "&username=" + username
                + "&pinID=" + pinId;
        execute(URL + FAVORITE_PIN_CMD + extension);
    }

    public void getUserFavoritePins(String username) {
        String extension = "&username=" + username;
        execute(URL + GET_USER_FAVORITES_CMD + extension);
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

    public void showStartMessage(Boolean status) {
        showStartMessage = status;
    }

    public void showFinishedMessage(Boolean status) {
        showFinishedMessage = status;
    }

    public void showMessages(Boolean status) {
        showStartMessage = status;
        showFinishedMessage = status;
    }

    public void setImage(String encodedImage) {
        image = encodedImage;
    }

    public AsyncManager resetAsyncManager() {
        AsyncManager copy = new AsyncManager(this.view, this.onCompletionListener);
        return copy;
    }

    private boolean valid(String result) {
        result = result.toLowerCase();

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

            if(showStartMessage) {
                snackbar = Snackbar.make(view, asyncStartMessage, Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
            }

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
        if (!valid(result)) {
            snackbar = Snackbar.make(view, asyncErrorMessage, Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        else if(showFinishedMessage) {
            snackbar = Snackbar.make(view, asyncFinishedMessage, Snackbar.LENGTH_SHORT);
            snackbar.show();
        }

        image = "NO_IMAGE";
        Log.e("RESULT (ASYNC_MANAGER)", result);
        onCompletionListener.onComplete(result);
    }
}
