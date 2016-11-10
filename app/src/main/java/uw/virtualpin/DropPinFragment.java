package uw.virtualpin;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/*

Author: Tyler Brent
Group: Team 8

This class is designed to implement the DropPin feature for the app
vPin. It utilizes Google Maps and Google Locations Services. See manifest for
permissions.

 */

public class DropPinFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

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

        return inflater.inflate(R.layout.fragment_drop_pin, container, false);
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
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Not working yet", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    /**
     * Connects the Google Api Client.
     */
    @Override
    public void onResume() {
        super.onResume();
        //setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    /**
     * Disconnects the Google Api Client.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
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

        setMapLocation(getLocation());
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
            //nothing
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            throw new NullPointerException("Error loading location.");
        }
        else {
            setMapLocation(location);
        }

        return location;
    }

    /**
     * Sets the current map location on Google Maps and zooms to that location.
     * Adds a marker for a visual.
     *
     * @param location current location of the user.
     */
    private void setMapLocation(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        LatLng coords = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(coords));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(coords));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}