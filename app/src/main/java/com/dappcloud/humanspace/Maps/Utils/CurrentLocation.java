package com.dappcloud.humanspace.Maps.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

public class CurrentLocation {

    private static Location myLocation = null;

    //Location Api since in Android 10 apps was crashing while fetching last location using Location Manager
    public static void initLocation(Activity ctx) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx);
        requestLocationUpdates(ctx, fusedLocationClient);
        fetchLastLocation(ctx, fusedLocationClient);
    }

    public static void requestLocationUpdates(Context context, FusedLocationProviderClient fusedLocationClient) {
        SettingsClient mSettingsClient = LocationServices.getSettingsClient(context);
        LocationSettingsRequest mLocationSettingsRequest;

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        mLocationSettingsRequest = builder.build();

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        setLocation(location, context);
                    }
                }
            }
        };

        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(locationSettingsResponse -> {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

                }).addOnFailureListener(e -> {
            Toast.makeText(context, "Please set high priority to fetch location " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    //Location Api since in Android 10 apps was crashing while fetching last location using Location Manager
    public static void fetchLastLocation(Activity context, FusedLocationProviderClient fusedLocationClient) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(context, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object
                        setLocation(location, context);
                    }
                });
    }

    //=====
    public static Location getLocation() {
        return myLocation;
    }

    //=====
    public static void setLocation(Location location, Context ctx) {
        if (location != null) {
            myLocation = location;
//            Toast.makeText(ctx, "Latitude = "+location.getLatitude()+" Longitude"+location.getLongitude(), Toast.LENGTH_SHORT).show();
//            Log.i( "MyKey","Location Set ===> "+"Latitude = "+location.getLatitude()+" Longitude"+location.getLongitude());
        }

    }
}
