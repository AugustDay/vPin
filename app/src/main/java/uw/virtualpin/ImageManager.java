package uw.virtualpin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import java.io.ByteArrayOutputStream;

/*

Author: Tyler Brent

This class handles the encoding and decoding of images in preperation to either store or
retrieve them from the web service.

 */

public class ImageManager {

    /**
     * Default constructor
     */
    public ImageManager(){
    }

    /**
     * Converts the bitmap image to a base 64 string.
     *
     * @param image bitmap image to be converted
     * @return encodedImage base64 string
     */
    public String convertBitmapToByteArray(Bitmap image) {
        if(image == null) {
            return "NULL_IMAGE";
        }
        Bitmap bitmap = image;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] byteArray = stream.toByteArray();
        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        return encodedImage;
    }

    /**
     * Converts an encodedImage base 64 string into a bitmap.
     *
     * @param encodedImage base 64 encoded string.
     * @return bitmap of the converted string.
     */
    public Bitmap convertEncodedImageToBitmap(String encodedImage) {
        if(encodedImage == null) {
            Log.e("ERROR: ", "Encoded image string was null");
            return null;
        }
        
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        
        return bitmap;
    }
}
