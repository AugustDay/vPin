package uw.virtualpin;

import org.junit.Test;

import java.net.URLEncoder;

import uw.virtualpin.Data.Pin;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

/**
 * Created by Austin Ingraham on 12/7/2016.
 */

public class PinTest {
    private String USERNAME = "August";
    private String PIN_ID = "3";
    private double LATITUDE = 120.33;
    private double LONGITUDE = 40.55;
    private String MESSAGE = "This is a test message";
    private String ENCODED_IMAGE = "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";

    private Pin basicPinFactoryFullArguments() {
        return new Pin(USERNAME, LATITUDE, LONGITUDE, MESSAGE, ENCODED_IMAGE);
    }

    @Test
    public void testPinConstructorFullArguments() {
        assertNotNull(basicPinFactoryFullArguments());
    }

    @Test
    public void testPinBuildURL() {
        try {
            String correctURL = "http://cssgate.insttech.washington.edu/~_450team8/info.php?cmd=new_pin&username=" +
                    USERNAME + "&latitude=" + LATITUDE + "&longitude=" + LONGITUDE + "&message=" + URLEncoder.encode(MESSAGE, "UTF-8");
            Pin p = basicPinFactoryFullArguments();
            String url = p.buildPinURL(null);
            assertEquals(url, correctURL);
        } catch(Exception e) {
            fail();
        }
    }

    @Test
    public void testPinUsernameGetter() {
        try {
            Pin p = basicPinFactoryFullArguments();
            assertEquals(p.getUserName(), USERNAME);
        } catch(IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testPinLongitudeGetter() {
        try {
            Pin p = basicPinFactoryFullArguments();
            assertEquals(p.getLongitude(), LONGITUDE);
        } catch(IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testPinLatitudeGetter() {
        try {
            Pin p = basicPinFactoryFullArguments();
            assertEquals(p.getLatitude(), LATITUDE);
        } catch(IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testPinMessageGetter() {
        try {
            Pin p = basicPinFactoryFullArguments();
            assertEquals(p.getMessage(), MESSAGE);
        } catch(IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testPinEncodedImageGetter() {
        try {
            Pin p = basicPinFactoryFullArguments();
            assertEquals(p.getEncodedImage(), ENCODED_IMAGE);
        } catch(IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testPinUsernameSetter() {
        try {
            String newUsername = "Different Username";
            Pin p = basicPinFactoryFullArguments();
            p.setUserName(newUsername);
            assertEquals(p.getUserName(), newUsername);
        } catch(IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testPinMessageSetter() {
        try {
            String newMessage = "Different message";
            Pin p = basicPinFactoryFullArguments();
            p.setMessage(newMessage);
            assertEquals(p.getMessage(), newMessage);
        } catch(IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testPinLongitudeSetter() {
        try {
            Double newLongitude = 10.22;
            Pin p = basicPinFactoryFullArguments();
            p.setLongitude(newLongitude);
            assertEquals(p.getLongitude(), newLongitude);
        } catch(IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testPinLatitudeSetter() {
        try {
            Double newLatitude = 60.7;
            Pin p = basicPinFactoryFullArguments();
            p.setLatitude(newLatitude);
            assertEquals(p.getLatitude(), newLatitude);
        } catch(IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testPinEncodedImageSetter() {
        try {
            String newImage = "ENCODEDMESSAGE";
            Pin p = basicPinFactoryFullArguments();
            p.setEncodedImage(newImage);
            assertEquals(p.getEncodedImage(), newImage);
        } catch(IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testPinIDSetter() {
        try {
            Pin p = basicPinFactoryFullArguments();
            p.setId(PIN_ID);
            assertEquals(p.getId(), PIN_ID);
        } catch(IllegalArgumentException e) {
            fail();
        }
    }
}
