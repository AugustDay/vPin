package uw.virtualpin.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import uw.virtualpin.Data.CurrentUser;
import uw.virtualpin.Data.Pin;
import uw.virtualpin.HelperClasses.AsyncManager;
import uw.virtualpin.HelperClasses.ImageManager;
import uw.virtualpin.HelperClasses.LocationManager;
import uw.virtualpin.Interfaces.OnCompletionListener;
import uw.virtualpin.R;

/*

Author: Tyler Brent
Group: Team 8

This class is designed to implement the DropPin feature for the app
vPin. It utilizes Google Maps and Google Locations Services. See manifest for
permissions.

 */

public class DropPinActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, OnCompletionListener {

    private EditText messageText;
    private GoogleMap mMap;
    private static final int PICK_IMAGE = 100;
    private Uri imageUri;
    private ImageView imageView;
    private ImageManager imageManager;
    private TextView textGps;
    private Pin pin;
    private String username;
    private LocationManager locationManager;
    private SupportMapFragment mapFragment;
    private TextView show_text;
    private AsyncManager asyncManager;

    public DropPinActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drop_pin);

        final Button uploadImageButton = (Button) findViewById(R.id.uploadImageButton);
        imageManager = new ImageManager();
        textGps = (TextView) findViewById(R.id.gps_location_text);
        messageText = (EditText) findViewById(R.id.messageText);
        imageView = (ImageView) findViewById(R.id.selectedImage);
        show_text = (TextView) findViewById(R.id.show_button_text);
        show_text = (TextView) findViewById(R.id.show_button_text);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        asyncManager = new AsyncManager(findViewById(android.R.id.content), this);

        setupDropPinButton(findViewById(android.R.id.content));
        setupEditTextShowHide(findViewById(android.R.id.content));

        CurrentUser currentUser = new CurrentUser();
        username = currentUser.username;

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        locationManager = new LocationManager(this, this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.stopLocationManager();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setMapLocation(locationManager.getLocation());

    }

    private void setMapLocation(Location location) {
        if (location == null) {
            textGps.setText("Error loading location...");
            return;
        }

        double lat = location.getLatitude();
        double lng = location.getLongitude();

        LatLng coords = new LatLng(lat, lng);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(coords));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(coords));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        updateTextView(lat, lng);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("Location Changed: ", location.toString());
        setMapLocation(location);
    }

    private void updateTextView(double lat, double lng) {
        String textLocation = "Location: (" + lat + ", " + lng + ")";
        final TextView textCoordinates = (TextView) this.findViewById(R.id.gps_location_text);
        textCoordinates.setText(textLocation);
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void setupDropPinButton(View view) {
        final Button pinButton = (Button) view.findViewById(R.id.postButton);
        pinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String imageString = "NO_IMAGE";

                if (imageView.getDrawable() != null) {
                    Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    imageString = imageManager.convertBitmapToByteArray(image);
                }

                if (imageString.equalsIgnoreCase("NO_IMAGE") && messageText.getText().toString().length() == 0) {
                    Snackbar.make(findViewById(android.R.id.content), "Please enter a message or upload a photo.", Snackbar.LENGTH_LONG).show();
                } else {

                    try {
                        pin = new Pin(username, locationManager.getLocation().getLatitude()
                                , locationManager.getLocation().getLongitude()
                                , messageText.getText().toString()
                                , imageString);

                        asyncManager.createPin(pin);
                        messageText.setText("");
                        imageView.setImageResource(0);
                        closeSoftKeyboard();

                    } catch (Exception e) {
                        Snackbar.make(findViewById(android.R.id.content), "Error loading location. Pin not created.", Snackbar.LENGTH_LONG);
                    }
                }
            }
        });
    }

    private void setupEditTextShowHide(View view) {
        show_text.setVisibility(View.GONE);

        messageText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setViews(false);
                } else {
                    setViews(true);
                }
            }
        });

        messageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViews(false);
            }
        });

        show_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViews(true);
            }
        });
    }

    private void setViews(Boolean on) {
        if(on) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().show(mapFragment).commit();
            textGps.setVisibility(View.VISIBLE);
            messageText.setVisibility(View.VISIBLE);
            show_text.setVisibility(View.GONE);

        } else {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().hide(mapFragment).commit();
            textGps.setVisibility(View.GONE);
            show_text.setVisibility(View.VISIBLE);
        }
    }

    private void closeSoftKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            setViews(true);
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onComplete(String result) {
        asyncManager = asyncManager.resetAsyncManager();
    }
}
