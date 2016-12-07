package uw.virtualpin;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import static android.support.v4.app.ActivityCompat.requestPermissions;

/*

Author: Tyler Brent

This class handles everything needed for locations in the app.

 */


//Note: make sure to call stopLocationManager() in onPause() for any activity or fragment that uses it.
public class LocationManager implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClientGeo;
    private Activity activity;
    private LocationListener locationListener;

    /**
     * Overloaded constructor to initialize variables.
     *
     * @param activity the activity this manager is being attached to.
     * @param locationListener the location listener being attached to this manager.
     */
    public LocationManager(Activity activity, LocationListener locationListener) {
        this.locationListener = locationListener;
        this.activity = activity;
        setupGoogleMapClient();
    }

    /**
     * Gets the location of the user.
     *
     * @return the location of the user.
     */
    public Location getLocation() {

        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            askLocationPermissions();
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClientGeo);
        return location;
    }

    /**
     * This method gets the required permissions from the user when the location manager is connected.
     *
     * @param bundle the bundle.
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            askLocationPermissions();
        }

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates
                (mGoogleApiClientGeo, mLocationRequest, locationListener);
    }

    /**
     * This method stops the location manager.
     */
    public void stopLocationManager() {
        mGoogleApiClientGeo.disconnect();
    }

    /**
     * This method disconnects the location manager when the connection is suspended.
     *
     * @param i connection status.
     */
    @Override
    public void onConnectionSuspended(int i) {
        LocationServices.FusedLocationApi.flushLocations(mGoogleApiClientGeo);
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClientGeo, locationListener);
    }

    /**
     * Required and intentionally left blank, does nothing.
     *
     * @param connectionResult result of the connection.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    /**
     * Asks the user for location permissions.
     */
    private void askLocationPermissions() {

        requestPermissions(activity,
                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                1);

        requestPermissions(activity,
                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                1);

    }

    /**
     * Initializes the google api client so that the location manager can get the user location.
     */
    private void setupGoogleMapClient() {

        mGoogleApiClientGeo = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClientGeo.connect();
    }
}
