package uw.virtualpin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class ImageManager {

    public ImageManager(){
    }

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
