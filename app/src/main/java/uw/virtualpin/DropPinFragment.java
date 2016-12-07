package uw.virtualpin;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import static android.app.Activity.RESULT_OK;

/*

Author: Tyler Brent
Group: Team 8

This class is designed to implement the DropPin feature for the app
vPin. It utilizes Google Maps and Google Locations Services. See manifest for
permissions.

 */

public class DropPinFragment extends Fragment implements OnMapReadyCallback, LocationListener {

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

    public DropPinFragment() {
        // Required empty public constructor
    }

    /**
     *
     * Overridden onCreateView initializes all the required variables for Drop Pin.
     *
     * @param inflater inflater for the fragment
     * @param container view container for the fragment
     * @param savedInstanceState saved state of the fragment
     * @return the view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_drop_pin, container, false);
        final Button uploadImageButton = (Button) view.findViewById(R.id.uploadImageButton);
        imageManager = new ImageManager();
        textGps = (TextView) view.findViewById(R.id.gps_location_text);
        messageText = (EditText) view.findViewById(R.id.messageText);
        imageView = (ImageView) view.findViewById(R.id.selectedImage);
        show_text = (TextView) view.findViewById(R.id.show_button_text);
        show_text = (TextView) view.findViewById(R.id.show_button_text);
        mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setupDropPinButton(view);
        setupEditTextShowHide(view);

        if (savedInstanceState == null) {
            Bundle extras = getActivity().getIntent().getExtras();
            if (extras == null) {
                username = null;
            } else {
                username = extras.getString("USERNAME");
            }
        } else {
            username = (String) savedInstanceState.getSerializable("USERNAME");
        }

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        return view;
    }

    /**
     * Overridden onStart initializes the Google Map fragment and the Location manager.
     * The LocationManager is a helper class designed to manage all things needed
     * to obtain location.
     */
    @Override
    public void onStart() {
        super.onStart();
        locationManager = new LocationManager(getActivity(), this);

        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Overridden onPause stops the location manager when it this fragment
     * is put on pause.
     */
    @Override
    public void onPause() {
        super.onPause();
        locationManager.stopLocationManager();
    }

    /**
     * Overridden onMapReady sets the current map.
     *
     * @param googleMap the google map fragment.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setMapLocation(locationManager.getLocation());

    }

    /**
     * Gives a visible representation of where the user is on the map
     * by zooming to the location and dropping a pin.
     *
     * @param location the location of the user.
     */
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

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        updateTextView(lat, lng);
    }

    /**
     * Updates the users location when they move.
     *
     * @param location the current location of the user.
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.e("Location Changed: ", location.toString());
        setMapLocation(location);
    }

    /**
     * Updates the text view that gives a visible representation of the users
     * coordinates.
     *
     * @param lat the latitude position of the user.
     * @param lng the longitude position of the user.
     */
    private void updateTextView(double lat, double lng) {
        String textLocation = "Location: (" + lat + ", " + lng + ")";
        final TextView textCoordinates = (TextView) getActivity().findViewById(R.id.gps_location_text);
        textCoordinates.setText(textLocation);
    }

    /**
     * Opens the image gallery on the users device so they may select an image.
     */
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    /**
     * On the resolution of opening the image gallery, this sets the imageView to the image chosen.
     *
     * @param requestCode the request code.
     * @param resultCode the result code.
     * @param data the data.
     */
    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    /**
     * This method sets up the button for when a user presses the drop pin button.
     * It resets the image and the message and calls the async task to upload the information.
     *
     * @param view the current view.
     */
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
                    Snackbar.make(getView(), "Please enter a message or upload a photo.", Snackbar.LENGTH_LONG).show();
                } else {

                    try {
                        pin = new Pin(username, locationManager.getLocation().getLatitude()
                                , locationManager.getLocation().getLongitude()
                                , messageText.getText().toString()
                                , imageString);

                        DropPinAsyncTask task = new DropPinAsyncTask();
                        task.execute(pin.buildPinURL(view));
                        messageText.setText("");
                        imageView.setImageResource(0);
                        closeSoftKeyboard();

                    } catch (Exception e) {
                        Snackbar.make(getView(), "Error loading location. Pin not created.", Snackbar.LENGTH_LONG);
                    }
                }
            }
        });
    }

    /**
     * Sets up the edit text to either collapse or show the other information on the screen.
     * This allows the user to see what they are typing by clearing the screen of clutter.
     *
     * @param view the current view.
     */
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

    /**
     * Sets views is what toggles the information on the screen as visible or not.
     *
     * @param on a flag for whether the information is being turned on or off.
     */
    private void setViews(Boolean on) {
        if(on) {
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().show(mapFragment).commit();
            textGps.setVisibility(View.VISIBLE);
            messageText.setVisibility(View.VISIBLE);
            show_text.setVisibility(View.GONE);

        } else {
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().hide(mapFragment).commit();
            textGps.setVisibility(View.GONE);
            show_text.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Closes the soft keyboard
     */
    private void closeSoftKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            setViews(true);
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * The async task that communicates to the web service to upload the pin data to the server.
     */
    private class DropPinAsyncTask extends AsyncTask<String, Integer, String> {
        Snackbar snackbar;

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                snackbar = Snackbar.make(getView(), "Uploading Pin, please wait...", Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    if(pin.getEncodedImage() != "NO_IMAGE") {

                        urlConnection.setDoOutput(true);
                        String data = URLEncoder.encode("image", "UTF-8")
                                + "=" + URLEncoder.encode(pin.getEncodedImage(), "UTF-8");

                        OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                        wr.write(data);
                        wr.flush();
                    }

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to complete your request, Reason: "
                            + e.getMessage();
                }
                finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.contains("true")) {
                snackbar = Snackbar.make(getView(), "Pin created.", Snackbar.LENGTH_SHORT);
                snackbar.show();
            } else {
                snackbar = Snackbar.make(getView(), "Error, please reload this page.", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
    }
}
