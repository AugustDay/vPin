package uw.virtualpin.Activities;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uw.virtualpin.HelperClasses.AsyncManager;
import uw.virtualpin.HelperClasses.ImageManager;
import uw.virtualpin.Interfaces.OnCompletionListener;
import uw.virtualpin.R;

/*

Author: Tyler Brent

This class represents the pin object after an item is clicked in the PostHistory list.
It is the expanded view of that pin. Here you can update any messsages you've left in the pin.
You cannot update the image, although that was a feature we tried to implement but ran out of time
due to a bug with the design.

Note: below is some commented out code that will be used to update the pin image. It is left intenionally
from a previous attempt.

 */

public class PinActivity extends AppCompatActivity implements OnCompletionListener {

    ArrayList<String> pinDetails;
    private TextView creatorText;
    private TextView locationText;
    private EditText messageText;
    private ImageView imageView;
    private Button updateButton;
    private String encodedImage;
    private ImageManager imageManager;
    private static final int PICK_IMAGE = 100;
    private AsyncManager asyncManager;
    boolean filled;

    public PinActivity() {
        pinDetails = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pin);

        Bundle extras = getIntent().getExtras();
        pinDetails = extras.getStringArrayList("PINS");

        filled = false;
        imageManager = new ImageManager();
        creatorText = (TextView) findViewById(R.id.creatorTextHistory);
        locationText = (TextView) findViewById(R.id.locationTextHistory);
        messageText = (EditText) findViewById(R.id.messageTextHistory);
        imageView = (ImageView) findViewById(R.id.imageViewHistory);
        updateButton = (Button) findViewById(R.id.updateButton);
        asyncManager = new AsyncManager(findViewById(android.R.id.content), this);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageText.getText().toString();
                encodedImage = imageManager.convertBitmapToByteArray
                        (((BitmapDrawable) imageView.getDrawable()).getBitmap());

                if(message.equalsIgnoreCase(pinDetails.get(3))) {
                    Snackbar.make(findViewById(android.R.id.content), "No message changes detected.", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if(message.length() == 0) {
                    Snackbar.make(findViewById(android.R.id.content), "Cannot enter an empty message.", Snackbar.LENGTH_LONG).show();
                    return;
                }

                asyncManager.setImage(encodedImage);
                asyncManager.updatePin(pinDetails.get(0), message);
            }
        });
    }

    public void onStart() {
        super.onStart();
        if(filled == false) {
            AsyncManager aManager = new AsyncManager(findViewById(android.R.id.content), this);
            aManager.getPin(pinDetails.get(0));
        }
    }

    private void setupPinDetails() {
        try {
            creatorText.setText("Created by: " + pinDetails.get(1));
            locationText.setText("Location: " + pinDetails.get(2));
            messageText.setText(pinDetails.get(3));
            imageView.setImageBitmap(imageManager.convertEncodedImageToBitmap(pinDetails.get(4)));

        } catch (Exception e) {
            Snackbar.make(findViewById(android.R.id.content), "Error loading pin, please try again.", Snackbar.LENGTH_LONG);
        }
    }

    private void parseJson(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            pinDetails.add(jsonObject.getString("image"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        setupPinDetails();
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            Uri imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    @Override
    public void onComplete(String result) {
        if(!filled) {
            filled = true;
            parseJson(result);
        }
    }
}
