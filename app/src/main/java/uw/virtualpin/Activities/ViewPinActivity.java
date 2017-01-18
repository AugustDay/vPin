package uw.virtualpin.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import uw.virtualpin.Data.CurrentPin;
import uw.virtualpin.HelperClasses.AsyncManager;
import uw.virtualpin.HelperClasses.ImageManager;
import uw.virtualpin.Interfaces.OnCompletionListener;
import uw.virtualpin.R;

public class ViewPinActivity extends AppCompatActivity implements OnCompletionListener{

    private TextView creatorText;
    private TextView createdDateText;
    private TextView messageText;
    private TextView locationText;
    private ImageView image;
    private String encodedImage;
    private AsyncManager asyncManager;
    private CurrentPin currentPin;
    private ImageManager imageManager;

    public ViewPinActivity() {
        encodedImage = "NO_IMAGE";
        asyncManager = new AsyncManager(findViewById(android.R.id.content), this);
        imageManager = new ImageManager();
        currentPin = new CurrentPin();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pin);

        creatorText = (TextView) findViewById(R.id.creatorTextView);
        createdDateText = (TextView) findViewById(R.id.dateTextView);
        messageText = (TextView) findViewById(R.id.messageTextView);
        locationText = (TextView) findViewById(R.id.locationTextView);
        image = (ImageView) findViewById(R.id.imageViewView);
    }

    private void setupPinDetails() {
        creatorText.setText("Created by: " + currentPin.userName);
        locationText.setText("Location: " + currentPin.coordinates);
        messageText.setText(currentPin.message);
        image.setImageBitmap(imageManager.convertEncodedImageToBitmap(currentPin.encodedImage));
    }

    private void parseJson(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            currentPin.encodedImage = jsonObject.getString("image");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        setupPinDetails();
    }

    @Override
    public void onComplete(String result) {
        parseJson(result);
    }
}
