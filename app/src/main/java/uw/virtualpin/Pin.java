package uw.virtualpin;

import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by Tyler on 11/19/2016.
 */

public class Pin implements Serializable {

    public final static String PIN_URL =
            "http://cssgate.insttech.washington.edu/~_450team8/info.php?cmd=new_pin";
    private String id;
    private String message;
    private String userName;
    private double latitude;
    private double longitude;
    private String encodedImage;
    private String dateTime;

    public static final String ID = "id", USER_NAME = "userName"
            , LATITUDE= "latitude", LONGITUDE = "longitude", ENCODED_IMAGE="encodedImage", MESSAGE = "message";

    public Pin(String userName, double latitude, double longitude, String message, String encodedImage) {
        this.message = message;
        this.userName = userName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.encodedImage = encodedImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getEncodedImage() {
        return encodedImage;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEncodedImage(String encodedImage) {
        this.encodedImage = encodedImage;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String buildCourseURL(View v) {
        StringBuilder stringBuilder = new StringBuilder(PIN_URL);

        try {

            stringBuilder.append("&username=");
            stringBuilder.append(userName);

            stringBuilder.append("&latitude=");
            stringBuilder.append(latitude);

            stringBuilder.append("&longitude=");
            stringBuilder.append(longitude);

            stringBuilder.append("&message=");
            stringBuilder.append(URLEncoder.encode(message, "UTF-8"));

        } catch (Exception e) {
            Toast.makeText(v.getContext(), "PIN could not be added\nError message: "
                    + e.getMessage(), Toast.LENGTH_LONG);
        }

        return stringBuilder.toString();
    }
}
