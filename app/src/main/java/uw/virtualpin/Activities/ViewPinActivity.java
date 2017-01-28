package uw.virtualpin.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    private ImageView upvote;
    private ImageView downvote;
    private ImageView favorite;

    public ViewPinActivity() {
        encodedImage = "NO_IMAGE";
        imageManager = new ImageManager();
        currentPin = new CurrentPin();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pin);

        asyncManager = new AsyncManager(findViewById(android.R.id.content), this);
        creatorText = (TextView) findViewById(R.id.creatorTextView);
        createdDateText = (TextView) findViewById(R.id.dateTextView);
        messageText = (TextView) findViewById(R.id.messageTextView);
        locationText = (TextView) findViewById(R.id.locationTextView);
        image = (ImageView) findViewById(R.id.imageViewView);
        upvote = (ImageView) findViewById(R.id.upvoteView);
        downvote = (ImageView) findViewById(R.id.downvoteView);
        favorite = (ImageView) findViewById(R.id.favoriteView);

        asyncManager.getPin(currentPin.id);
        setupDownvote();
        setupFavorite();
        setupUpvote();
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

    private void setupUpvote() {
        upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asyncManager.showStartMessage(false);
                asyncManager.setFinishedMessage("Pin upvoted.");
                asyncManager.upvotePin(currentPin.id);
                playPressedAnim(upvote);
            }
        });
    }

    private void setupDownvote() {
        downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asyncManager.showStartMessage(false);
                asyncManager.setFinishedMessage("Pin downvoted.");
                asyncManager.downvotePin(currentPin.id);
                playPressedAnim(downvote);
            }
        });
    }

    private void setupFavorite() {
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPressedAnim(favorite);
                //waiting for favorite implementation to database
            }
        });
    }

    private void playPressedAnim(View view) {
        Animation popout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.object_pressed_popout);
        view.startAnimation(popout);
    }

    @Override
    public void onComplete(String result) {
        asyncManager = asyncManager.resetAsyncManager();
        parseJson(result);
    }
}
