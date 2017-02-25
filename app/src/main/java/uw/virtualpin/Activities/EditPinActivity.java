package uw.virtualpin.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private Button deleteButton;
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
        deleteButton = (Button) findViewById(R.id.deleteButton);
        asyncManager = new AsyncManager(findViewById(android.R.id.content), this);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        setupUpdateButton();
        setupDeleteButton();
    }

    public void onStart() {
        super.onStart();
        if(filled == false) {
            asyncManager.getPin(currentPin.id);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        MenuItem item = menu.findItem(R.id.searchAction);
        item.setIcon(getResources().getDrawable(R.drawable.left));
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String itemTitle = item.getTitle().toString();

        if(itemTitle.equalsIgnoreCase("Logout")) {
            Intent logoutIntent = new Intent(getApplicationContext(), LoginActivity.class);
            finishAffinity();
            startActivity(logoutIntent);
        }
        else if(itemTitle.equalsIgnoreCase("Profile")) {
            Intent profileIntent = new Intent(getApplicationContext(), ProfilePage.class);
            startActivity(profileIntent);
        }

        else if(itemTitle.equalsIgnoreCase("Search")) {
            Intent inboxIntent = new Intent(getApplicationContext(), InboxActivity.class);
            startActivity(inboxIntent);
        }

        return true;
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
        asyncManager = asyncManager.resetAsyncManager();
        Log.e("RESULT EDIT", result);
        if(!filled) {
            filled = true;
            parseJson(result);
        }
    }

    private void setupPinDetails() {
        creatorText.setText("Creator: " + currentPin.userName);
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

    private void setupDeleteButton() {
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog();
            }
        });
    }

    private void deleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this pin?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        asyncManager.deletePin(currentPin.id);
                        Intent intent = new Intent(getApplicationContext(), PinHistoryActivity.class);
                        startActivity(intent);
                        dialog.cancel();
                    }
                });

        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void setupUpdateButton() {
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
}
