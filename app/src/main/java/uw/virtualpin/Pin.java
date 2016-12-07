package uw.virtualpin;

import android.view.View;
import android.widget.Toast;

import java.net.URLEncoder;

/**
 * Created by Tyler on 11/19/2016.
 */

public class Pin {

    public final static String PIN_URL =
            "http://cssgate.insttech.washington.edu/~_450team8/info.php?cmd=new_pin";
    private String id;
    private String message;
    private String userName;
    private double latitude;
    private double longitude;
    private String encodedImage;

    /**
     * Overloaded constructor that takes all the data for the pins.
     *
     * @param userName the username of the person who posted the pin.
     * @param latitude the latitude of the pin.
     * @param longitude the longitude of the pin.
     * @param message the message of the pin.
     * @param encodedImage the image related to the pin.
     */
    public Pin(String userName, double latitude, double longitude, String message, String encodedImage) {
        this.message = message;
        this.userName = userName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.encodedImage = encodedImage;
    }

    /**
     * Gets the pin id.
     *
     * @return pin id.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the pin id.
     *
     * @param id pin id.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the longitude of the pin.
     *
     * @return the longitude.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Returns the encoded image attached to the pin.
     *
     * @return the encoded image.
     */
    public String getEncodedImage() {
        return encodedImage;
    }

    /**
     * Sets the longitude of the pin.
     *
     * @param longitude the longitude.
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Returns the message attached to the pin.
     *
     * @return the message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message attached to the pin.
     *
     * @param message the message.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the username of the user who posted the pin.
     *
     * @return the username.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the username of the person who posted the pin.
     *
     * @param userName the username.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets the encoded image attached to the pin.
     *
     * @param encodedImage the encoded image.
     */
    public void setEncodedImage(String encodedImage) {
        this.encodedImage = encodedImage;
    }

    /**
     * Gets the latitude of the pin.
     *
     * @return the latitude.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude of the pin.
     *
     * @param latitude the latitude.
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Build the url needed to send the pin information to the web service.
     *
     * @param v the current view.
     * @return the url.
     */
    public String buildPinURL(View v) {
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
