package uw.virtualpin.Data;

/**
 * Created by tyler on 1/17/2017.
 */

public class CurrentPin {
    public static String id;
    public static String message;
    public static String userName;
    public static double latitude;
    public static double longitude;
    public static String encodedImage;
    public static String coordinates;
    public static String dateTime;

    public CurrentPin() {
    }

    public CurrentPin(Pin pin) {
        this.id = pin.getId();
        this.userName = pin.getUserName();
        this.message = pin.getMessage();
        this.latitude = pin.getLatitude();
        this.longitude = pin.getLongitude();
        this.encodedImage = pin.getEncodedImage();
        this.coordinates = "(" + latitude + ", " + longitude + ")";
        this.dateTime = pin.getDateTime();
    }
}
