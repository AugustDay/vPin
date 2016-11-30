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

/**
 * Created by tyler on 11/29/2016.
 */

public class LocationManager implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClientGeo;
    private Activity activity;
    private LocationListener locationListener;

    public LocationManager(Activity activity, LocationListener locationListener) {
        this.locationListener = locationListener;
        this.activity = activity;
        setupGoogleMapClient();
    }

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

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void askLocationPermissions() {

        requestPermissions(activity,
                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                1);

        requestPermissions(activity,
                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                1);

    }

    private void setupGoogleMapClient() {

        mGoogleApiClientGeo = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClientGeo.connect();
    }
}
