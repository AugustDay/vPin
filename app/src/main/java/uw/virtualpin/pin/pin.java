package uw.virtualpin.pin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Pin implements Serializable {
    private int mPinId;
    private String mCreator;
    private double mLatitude;
    private double mLongitude;
    private String mMessage;

    public static final String PINID = "pinID", CREATOR = "creator"
            , LATITUDE = "latitude", LONGITUDE = "longitude", MESSAGE = "message";

/*    private static PinItem createMessageItem(int position) {
        return new PinItem(String.valueOf(position), "Creator" + position, 47.2447776, -122.4385474, "Message content for message" + position);
                //, makeDetails(position));
    }*/

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    public Pin(int pinId, String creator, double latitude, double longitude, String message)
    {
        this.mPinId = pinId;
        this.mCreator = creator;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mMessage = message;
    }

    public int getPinId()
    {
        return mPinId;
    }

    public String getCreator()
    {
        return mCreator;
    }

    public double getLatitude()
    {
        return mLatitude;
    }

    public double getLongitude()
    {
        return mLongitude;
    }

    public String getMessage()
    {
        return mMessage;
    }

    public void setPinId(int pinId)
    {
        this.mPinId = pinId;
    }

    public void setCreator(String creator)
    {
        this.mCreator = creator;
    }

    public void setLatitude(double latitude)
    {
        this.mLatitude = latitude;
    }

    public void setLongitude(double longitude)
    {
        this.mLongitude = longitude;
    }

    public static String parseCourseJSON(String pinJSON, List<Pin> pinList) {
        String reason = null;
        if (pinJSON != null) {
            try {
                JSONArray arr = new JSONArray(pinJSON);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Pin pin = new Pin(obj.getInt(Pin.PINID), obj.getString(Pin.CREATOR)
                            , obj.getDouble(Pin.LATITUDE), obj.getDouble(Pin.LONGITUDE), obj.getString(Pin.MESSAGE));
                    pinList.add(pin);
                }
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }

        }
        return reason;
    }
}
