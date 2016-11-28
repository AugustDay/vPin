package uw.virtualpin;

import android.graphics.Bitmap;
import android.util.Base64;
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
    
    public String convertEncodedImageToBitmap(String encodedImage) {
        if(encodedImage == null) {
            Log.e("ERROR: ", "Encoded image string was null");
            return null;
        }
        
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        
        return bitmap;
    }
}
