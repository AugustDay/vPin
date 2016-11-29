package uw.virtualpin;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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

public class DropPinFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private EditText messageText;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClientGeo;
    private static final int PICK_IMAGE = 100;
    private Uri imageUri;
    private ImageView imageView;
    private ImageManager imageManager;
    private TextView textGps;
    private Pin pin;
    private String username;

    /**
     * Default constructor.
     */
    public DropPinFragment() {
        // Required empty public constructor
    }

    /**
     * Overloaded constructor taking three
     *
     * @param inflater inflater for the fragment.
     * @param container container for the fragment.
     * @param savedInstanceState current instance state.
     * @return inflated object.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_drop_pin, container, false);
        final TextView show_text = (TextView) view.findViewById(R.id.show_button_text);
        final Button uploadImageButton = (Button) view.findViewById(R.id.uploadImageButton);
        final Users users = new Users();
        imageManager = new ImageManager();
        messageText = (EditText) view.findViewById(R.id.messageText);
        imageView = (ImageView) view.findViewById(R.id.selectedImage);
        show_text.setVisibility(View.GONE);

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

        final Button pinButton = (Button) view.findViewById(R.id.postButton);
        pinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String imageString = "NO_IMAGE";

                if (imageView.getDrawable() != null) {
                    Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    imageString = imageManager.convertBitmapToByteArray(image);
                }

                if (imageString == "NO_IMAGE" && messageText.getText().toString().length() == 0) {
                    Toast.makeText(getActivity().getApplicationContext()
                            , "Please enter a message or upload a photo"
                            , Toast.LENGTH_LONG)
                            .show();
                } else {

                    try {
                        pin = new Pin(username, getLocation().getLatitude()
                                , getLocation().getLongitude()
                                , messageText.getText().toString()
                                , imageString);

                        DropPinAsyncTask task = new DropPinAsyncTask();
                        task.execute(pin.buildCourseURL(view));
                        messageText.setText("");
                        imageView.setImageResource(0);

                    } catch (Exception e) {
                        Toast.makeText(getActivity().getApplicationContext()
                                , "Error loading location, Pin not created."
                                , Toast.LENGTH_LONG)
                                .show();
                    }
                }
            }
        });

        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        textGps = (TextView) view.findViewById(R.id.gps_location_text);

        messageText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    FragmentManager fm = getFragmentManager();
                    fm.beginTransaction().hide(mapFragment).commit();
                    textGps.setVisibility(View.GONE);
                    show_text.setVisibility(View.VISIBLE);
                } else {
                }
            }
        });

        messageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().hide(mapFragment).commit();
                textGps.setVisibility(View.GONE);
                show_text.setVisibility(View.VISIBLE);
            }
        });

        show_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().show(mapFragment).commit();
                textGps.setVisibility(View.VISIBLE);
                messageText.setVisibility(View.VISIBLE);
                show_text.setVisibility(View.GONE);
            }
        });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        return view;
    }

    /**
     * Overloaded Override of the onActivityCreated. Once the activity is finished being
     * built this method creates a Google Api Client and the fragment to support
     * the Google Map.
     *
     * @param bundle bundle to be used.
     */
    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        mGoogleApiClientGeo = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        setMapLocation(getLocation());
    }



    /**
     * Connects the Google Api Client.
     */
    @Override
    public void onResume() {
        super.onResume();
        //setUpMapIfNeeded();
        mGoogleApiClientGeo.connect();
    }

    /**
     * Disconnects the Google Api Client.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClientGeo.isConnected()) {
            mGoogleApiClientGeo.disconnect();
        }
    }

    /**
     * Sets the local Google Map when the Map is ready.
     *
     * @param googleMap local variable of the Google Map.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    /**
     * Sets the current map location. This method utilizes the GetLocation() method.
     *
     * @param bundle current bundle unused.
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClientGeo, mLocationRequest, this);
    }

    /**
     * Gets the current location of the user.
     * Checks if permissions are granted, currently does nothing to
     * resolve lack of permissions.
     *
     * @return returns the location.
     */
    public Location getLocation() {

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClientGeo);
        return location;
    }

    /**
     * Sets the current map location on Google Maps and zooms to that location.
     * Adds a marker for a visual.
     *
     * @param location current location of the user.
     */
    private void setMapLocation(Location location) {
        if (location == null) {
            textGps.setText("Error loading location...");
            return;
        }

        double lat = location.getLatitude();
        double lng = location.getLongitude();

        LatLng coords = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(coords));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(coords));
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
     * Updates the text view holding a string representation of the location
     * object.
     *
     * @param lat the latitude of current location.
     * @param lng the longitude of current location.
     */
    private void updateTextView(double lat, double lng) {
        String textLocation = "Location: (" + lat + ", " + lng + ")";
        final TextView textCoordinates = (TextView) getActivity().findViewById(R.id.gps_location_text);
        textCoordinates.setText(textLocation);
    }

    /**
     * onConnectionSuspended current does nothing. This is intentional.
     *
     * @param i none.
     */
    @Override
    public void onConnectionSuspended(int i) {
    }

    /**
     * onConnectionFailed currently does nothing. This is intentional.
     *
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public void openGallery() {
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

    @Override
    public void onLocationChanged(Location location) {
        Log.e("Location Changed: ", location.toString());
        setMapLocation(location);
    }

    private class DropPinAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
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
                Toast.makeText(getActivity().getApplicationContext()
                        , "Pin created!"
                        , Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Oops! Something went wrong." + result
                        , Toast.LENGTH_LONG)
                        .show();
            }
        }
    }
}
