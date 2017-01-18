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

import uw.virtualpin.Data.CurrentPin;
import uw.virtualpin.HelperClasses.AsyncManager;
import uw.virtualpin.HelperClasses.ImageManager;
import uw.virtualpin.Interfaces.OnCompletionListener;
import uw.virtualpin.R;

public class EditPinActivity extends AppCompatActivity implements OnCompletionListener {

    ArrayList<String> pinDetails;
    private TextView creatorText;
    private TextView locationText;
    private EditText messageText;
    private ImageView imageView;
    private Button updateButton;
    private String encodedImage;
    private ImageManager imageManager;
    private CurrentPin currentPin;
    private static final int PICK_IMAGE = 100;
    private AsyncManager asyncManager;
    boolean filled;

    public EditPinActivity() {

        pinDetails = new ArrayList<>();
        currentPin = new CurrentPin();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pin);

        filled = false;
        imageManager = new ImageManager();
        creatorText = (TextView) findViewById(R.id.creatorTextEdit);
        locationText = (TextView) findViewById(R.id.locationTextEdit);
        messageText = (EditText) findViewById(R.id.messageTextEdit);
        imageView = (ImageView) findViewById(R.id.imageViewEdit);
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

                if(message.equalsIgnoreCase(currentPin.message)){
                    Snackbar.make(findViewById(android.R.id.content), "No message changes detected.", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if(message.length() == 0) {
                    Snackbar.make(findViewById(android.R.id.content), "Cannot enter an empty message.", Snackbar.LENGTH_LONG).show();
                    return;
                }

                asyncManager.setImage(encodedImage);
                asyncManager.updatePin(currentPin.id, message);
            }
        });
    }

    public void onStart() {
        super.onStart();
        if(filled == false) {
            AsyncManager aManager = new AsyncManager(findViewById(android.R.id.content), this);
            aManager.getPin(currentPin.id);
        }
    }

    private void setupPinDetails() {
        creatorText.setText("Created by: " + currentPin.userName);
        locationText.setText("Location: " + currentPin.coordinates);
        messageText.setText(currentPin.message);
        imageView.setImageBitmap(imageManager.convertEncodedImageToBitmap(currentPin.encodedImage));
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
