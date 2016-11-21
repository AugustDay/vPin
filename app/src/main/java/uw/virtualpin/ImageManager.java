package uw.virtualpin;

import android.graphics.Bitmap;
import android.util.Base64;
import java.io.ByteArrayOutputStream;

public class ImageManager {

    public ImageManager(){
    }

    public String convertBitmapToByteArray(Bitmap image) {
        Bitmap bitmap = image;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);
        byte[] byteArray = stream.toByteArray();
        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        return encodedImage;
    }
}