package com.dappcloud.humanspace.Maps.Utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.module.AppGlideModule;
import com.dappcloud.humanspace.Databases.User;
import com.dappcloud.humanspace.Databases.UserLocation;
import com.dappcloud.humanspace.Databases.ViewList;
import com.dappcloud.humanspace.Maps.ProfileActivity;
import com.dappcloud.humanspace.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.HeatmapLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.heatmapDensity;
import static com.mapbox.mapboxsdk.style.expressions.Expression.interpolate;
import static com.mapbox.mapboxsdk.style.expressions.Expression.linear;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.rgb;
import static com.mapbox.mapboxsdk.style.expressions.Expression.rgba;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.expressions.Expression.zoom;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleStrokeColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleStrokeWidth;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.heatmapColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.heatmapIntensity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.heatmapOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.heatmapRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.heatmapWeight;


class SearchResultView {

    View view;
    MarkerView markerView;

    public SearchResultView(View view, MarkerView markerView) {
        this.view = view;
        this.markerView = markerView;
    }

    public SearchResultView() {}

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public MarkerView getMarkerView() {
        return markerView;
    }

    public void setMarkerView(MarkerView markerView) {
        this.markerView = markerView;
    }
}


public class Utils extends AppGlideModule {

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    public ArrayList<ViewList> customViews = new ArrayList<ViewList>();
    public ArrayList<SearchResultView> searchResultLists = new ArrayList<SearchResultView>();

    private static Location myLocation = null;
    private String userSetting = "";

    public void currentUser(GetUserListener getUserListener) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Common.CurrentUser = snapshot.getValue(User.class);
                if(getUserListener != null) {
                    getUserListener.onCompleted();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void usersList(Context context) {
        List<User> tempList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tempList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    tempList.add(user);
                }
                Common.UsersList = tempList;
                if(context != null) {
                    Intent intent = new Intent("update_users");
                    context.sendBroadcast(intent);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addMarkers(Context context, MarkerViewManager markerViewManager, float zoomStatus, String userSet, String category) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        clearMarkers(markerViewManager);
        customViews.clear();
        userSetting = userSet;
        for (User user : Common.UsersList) {
            try {
                if (user.getLocation() != null) {
                    if (userSet.equals("ghost") && user.getCategory().equals(context.getString(R.string.personal))) {
                        continue;
                    } else if (!category.equals(context.getString(R.string.all_categries))) {
                        if (user.getAccount().equals(context.getString(R.string.business)) && !category.equals(user.getCategory())) {
                            continue;
                        } else if (user.getAccount().equals(context.getString(R.string.personal)) && !category.equals(user.getGender())) {
                            continue;
                        }
                    } else if (!Common.CurrentUser.getCategory().equals(context.getString(R.string.gays)) && user.getCategory().equals(context.getString(R.string.gays))) {
                        continue;
                    }
                    // Use an XML layout to create a View object
                    @SuppressLint("InflateParams")
                    View customView = LayoutInflater.from(context).inflate(R.layout.layout_custom_marker, null);
                    customView.setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
                    ImageView imgUser = customView.findViewById(R.id.img_user);
                    Glide.with(context).load(user.getImageurl())
//                    Glide.with(context).load(R.drawable.category)
                            .transform(new CircleCrop())
                            .into(imgUser);
                    MarkerView markerView;
                    String username = user.getUsername();
                    LatLng addLoc = new LatLng((double) 0.0, (double) 0.0);
                    if (username.equals(Common.CurrentUser.getUsername())) {
                        if (userSet.equals("ghost")) {
                            continue;
                        } else if (userSet.equals("offset")) {
                            int r = pref.getInt("rVal", 0);
                            int s = pref.getInt("sVal", 0);
                            addLoc = new LatLng(computeOffset(user.getLocation(), r, s));
                            markerView = new MarkerView(new LatLng(user.getLocation().getLat() + addLoc.getLatitude(), user.getLocation().getLng() + addLoc.getLongitude()), customView);
                        } else {
                            markerView = new MarkerView(new LatLng(user.getLocation().getLat(), user.getLocation().getLng()), customView);
                        }
                    } else {
                        markerView = new MarkerView(new LatLng(user.getLocation().getLat(), user.getLocation().getLng()), customView);
                    }
                    markerViewManager.addMarker(markerView);
                    customView.setOnClickListener(view -> {
                        Common.UserSelected = user;
                        context.startActivity(new Intent(context, ProfileActivity.class));
                    });
                    ViewList m_viewList = new ViewList();
                    m_viewList.setUsername(user.getUsername());
                    m_viewList.setAccount(user.getAccount());
                    m_viewList.setCategory(user.getCategory());
                    m_viewList.setGender(user.getGender());
                    m_viewList.setView(customView);
                    m_viewList.setMarkerView(markerView);
                    m_viewList.setLat(addLoc.getLatitude());
                    m_viewList.setLng(addLoc.getLongitude());
                    customViews.add(m_viewList);
                    if (user.getCategory().equals(context.getString(R.string.car))) {
                        customView.setScaleY((float) (zoomStatus * zoomStatus * zoomStatus * zoomStatus / 90000.0 / 0.4));
                        customView.setScaleX((float) (zoomStatus * zoomStatus * zoomStatus * zoomStatus / 90000.0 / 0.4));
                    } else if (user.getAccount().equals(context.getString(R.string.personal))) {
                        if (user.getCategory().equals(context.getString(R.string.gold))) {
                            customView.setScaleY((float) (zoomStatus * zoomStatus * zoomStatus * zoomStatus / 90000.0 / 0.7));
                            customView.setScaleX((float) (zoomStatus * zoomStatus * zoomStatus * zoomStatus / 90000.0 / 0.7));
                        } else {
                            customView.setScaleY((float) (zoomStatus * zoomStatus * zoomStatus * zoomStatus / 90000.0 / 2.0));
                            customView.setScaleX((float) (zoomStatus * zoomStatus * zoomStatus * zoomStatus / 90000.0 / 2.0));
                        }
                    } else {
                        customView.setScaleY((float) (zoomStatus * zoomStatus * zoomStatus * zoomStatus / 90000.0));
                        customView.setScaleX((float) (zoomStatus * zoomStatus * zoomStatus * zoomStatus / 90000.0));
                    }
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }

        }
        markerViewManager.onCameraDidChange(true);

    }

    public LatLng computeOffset(UserLocation myLocation, int r, int s) {
        double earth_length = 40075040;
        double rangeLat = 360 * r / earth_length;
        double rangeLng = 360 * s / (earth_length * Math.cos(myLocation.getLat()));
//        return new LatLng(((Math.random()-0.5)*2)*rangeLat + myLocation.getLat(),((Math.random()-0.5)*2)*rangeLng + myLocation.getLng());
        return new LatLng(((Math.random()-0.5)*2)*rangeLat,((Math.random()-0.5)*2)*rangeLng);
    }

    public void clearMarkers(MarkerViewManager markerViewManager) {
        for (int i=0;i<customViews.toArray().length;i++){
            markerViewManager.removeMarker(customViews.get(i).getMarkerView());
        }
    }

    public void showHideMarkers(Context context, ArrayList<String> searchResult) {
        for (int i=0;i<customViews.toArray().length;i++){
            customViews.get(i).getView().setVisibility(View.GONE);
            if ( searchResult.equals(new ArrayList<>()) || searchResult.equals(null)) {
                customViews.get(i).getView().setVisibility(View.VISIBLE);
            }
            else if (searchResult.contains(context.getString(R.string.all_categries)) || searchResult.get(0).equals(context.getString(R.string.findMore))){
                customViews.get(i).getView().setVisibility(View.VISIBLE);
            }
            else {
                if ( customViews.get(i).getAccount().equals(context.getString(R.string.personal)) ) {
                    if ( searchResult.contains(customViews.get(i).getGender().toUpperCase()) ) {
                        customViews.get(i).getView().setVisibility(View.VISIBLE);
                    }
                    else if ( searchResult.contains(customViews.get(i).getCategory().toUpperCase())) {
                        customViews.get(i).getView().setVisibility(View.VISIBLE);
                    }
                }
                else if ( searchResult.contains(customViews.get(i).getCategory().toUpperCase()) ){
                    customViews.get(i).getView().setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void changeMarkers(Context context, float zoomLevel){
        if ( zoomLevel > 21.0 ) {
            for (int i=0;i<customViews.toArray().length;i++){
                if ( customViews.get(i).getCategory().equals(context.getString(R.string.car))) {
                    customViews.get(i).getView().setScaleX((float) (zoomLevel*zoomLevel*zoomLevel*zoomLevel/65000.0/0.3));
                    customViews.get(i).getView().setScaleY((float) (zoomLevel*zoomLevel*zoomLevel*zoomLevel/65000.0/0.3));
                }
                else if ( customViews.get(i).getAccount().equals(context.getString(R.string.personal)) ) {
                    if ( customViews.get(i).getCategory().equals(context.getString(R.string.gold)) ) {
                        customViews.get(i).getView().setScaleX((float) (zoomLevel*zoomLevel*zoomLevel*zoomLevel/65000.0/0.5));
                        customViews.get(i).getView().setScaleY((float) (zoomLevel*zoomLevel*zoomLevel*zoomLevel/65000.0/0.5));
                    }
                    else {
                        customViews.get(i).getView().setScaleX((float) (zoomLevel*zoomLevel*zoomLevel*zoomLevel/65000.0/1.3));
                        customViews.get(i).getView().setScaleY((float) (zoomLevel*zoomLevel*zoomLevel*zoomLevel/65000.0/1.3));
                    }
                }
                else {
                    customViews.get(i).getView().setScaleX((float) (zoomLevel*zoomLevel*zoomLevel*zoomLevel/65000.0/0.8));
                    customViews.get(i).getView().setScaleY((float) (zoomLevel*zoomLevel*zoomLevel*zoomLevel/65000.0/0.8));
                }
            }
            for ( SearchResultView searchResultView : searchResultLists ) {
                searchResultView.getView().setScaleX((float) (zoomLevel*zoomLevel*zoomLevel*zoomLevel/65000.0));
                searchResultView.getView().setScaleY((float) (zoomLevel*zoomLevel*zoomLevel*zoomLevel/65000.0));
            }
        }
        else {
            for (int i=0;i<customViews.toArray().length;i++){
                if ( customViews.get(i).getCategory().equals(context.getString(R.string.car))) {
                    customViews.get(i).getView().setScaleX((float) (zoomLevel*zoomLevel*zoomLevel*zoomLevel/90000.0/0.3));
                    customViews.get(i).getView().setScaleY((float) (zoomLevel*zoomLevel*zoomLevel*zoomLevel/90000.0/0.3));
                }
                else if ( customViews.get(i).getAccount().equals(context.getString(R.string.personal)) ) {
                    if ( customViews.get(i).getCategory().equals(context.getString(R.string.gold)) ) {
                        customViews.get(i).getView().setScaleX((float) (zoomLevel*zoomLevel*zoomLevel*zoomLevel/90000.0/0.5));
                        customViews.get(i).getView().setScaleY((float) (zoomLevel*zoomLevel*zoomLevel*zoomLevel/90000.0/0.5));
                    }
                    else {
                        customViews.get(i).getView().setScaleX((float) (zoomLevel*zoomLevel*zoomLevel*zoomLevel/90000.0/1.3));
                        customViews.get(i).getView().setScaleY((float) (zoomLevel*zoomLevel*zoomLevel*zoomLevel/90000.0/1.3));
                    }
                }
                else {
                    customViews.get(i).getView().setScaleX((float) (zoomLevel*zoomLevel*zoomLevel*zoomLevel/90000.0/0.8));
                    customViews.get(i).getView().setScaleY((float) (zoomLevel*zoomLevel*zoomLevel*zoomLevel/90000.0/0.8));
                }
            }
            for ( SearchResultView searchResultView : searchResultLists ) {
                searchResultView.getView().setScaleX((float) (zoomLevel*zoomLevel*zoomLevel*zoomLevel/85000.0));
                searchResultView.getView().setScaleY((float) (zoomLevel*zoomLevel*zoomLevel*zoomLevel/85000.0));
            }
        }
    }

    // Update Search SDK API result -------------------------------------------------------------------------------------------------------
    public void updateResultsOnMap(ArrayList<Point> markerCoordinates, MarkerViewManager markerViewManager, Context context, float zoomStatus){
        if (!searchResultLists.isEmpty()) {
            for ( SearchResultView searchResultView : searchResultLists) {
                markerViewManager.removeMarker(searchResultView.getMarkerView());
            }
            searchResultLists.clear();
        }
        if (!markerCoordinates.isEmpty()) {
            for ( Point point: markerCoordinates) {
                try {
                    View searchResultView = LayoutInflater.from(context).inflate(R.layout.layout_custom_marker, null);
                    searchResultView.setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
                    ImageView searchMarker = searchResultView.findViewById(R.id.img_user);
                    Glide.with(context).load(R.drawable.ic_location_on).into(searchMarker);
                    MarkerView searchMarkerView = new MarkerView(new LatLng(point.latitude(), point.longitude()), searchResultView);
                    markerViewManager.addMarker(searchMarkerView);
                    SearchResultView m_searchResultView = new SearchResultView();
                    m_searchResultView.setView(searchResultView);
                    m_searchResultView.setMarkerView(searchMarkerView);
                    searchResultLists.add(m_searchResultView);
                    searchResultView.setScaleY((float) (zoomStatus * zoomStatus * zoomStatus * zoomStatus / 85000.0 ));
                    searchResultView.setScaleX((float) (zoomStatus * zoomStatus * zoomStatus * zoomStatus / 85000.0 ));
                } catch ( Exception err) {
                    err.printStackTrace();
                }
            }
            markerViewManager.onCameraDidChange(true);
        }
    }

    // Location Api since in Android 10 apps was crashing while fetching last location using Location Manager -----------------------------
    public void initLocation(Activity ctx) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx);
        requestLocationUpdates(ctx, fusedLocationClient);
        fetchLastLocation(ctx, fusedLocationClient);
    }

    public void requestLocationUpdates(Context context, FusedLocationProviderClient fusedLocationClient) {
        SettingsClient mSettingsClient = LocationServices.getSettingsClient(context);
        LocationSettingsRequest mLocationSettingsRequest;

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(4000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        mLocationSettingsRequest = builder.build();

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        setLocation(location, customViews, userSetting);
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
                }).addOnFailureListener(e -> Toast.makeText(context, "Please set high priority to fetch location " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
    }

    public void fetchLastLocation(Activity context, FusedLocationProviderClient fusedLocationClient) {
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
                        setLocation(location, customViews, userSetting);
                    }
                });
    }

    public Location getLocation() {
        return myLocation;
    }

    public void setLocation(Location location, ArrayList<ViewList> customViews, String userSetting) {
        if (location != null) {
            myLocation = location;
            if (Common.CurrentUser != null){
                UserLocation myLoc = Common.CurrentUser.getLocation();
//                if ( (location.getLatitude() > myLoc.getLat() + 0.00005 && location.getLatitude() < myLoc.getLat() - 0.0001) || (location.getLongitude() > myLoc.getLng() + 0.0001 && location.getLongitude() < myLoc.getLng() - 0.0001) ) {
                    Common.CurrentUser.setLocation(new UserLocation(location.getLatitude(), location.getLongitude()));
                    UserLocation loc = new UserLocation(location.getLatitude(), location.getLongitude());
                    HashMap<String, Object> update = new HashMap<>();
                    update.put("location", loc);
                    FirebaseDatabase.getInstance().getReference(Common.USER_REF)
                            .child(Common.CurrentUser.getUserId())
                            .updateChildren(update);
                    for (int i=0;i<customViews.toArray().length;i++){
                        if (customViews.get(i).getUsername().equals(Common.CurrentUser.getUsername())){
                            ViewList m_customView = customViews.get(i);
                            UserLocation mLoc = Common.CurrentUser.getLocation();
                            m_customView.getMarkerView().setLatLng(new LatLng(mLoc.getLat() + m_customView.getLat(), mLoc.getLng() + m_customView.getLng()));
                            break;
                        }
                    }
//                }
            }
        }
    }

    // Add Headtmap on the map
    public void addHeatmap(GeoJsonSource geoJsonSource, Style style) {
        style.addSource(geoJsonSource);
        HeatmapLayer layer = new HeatmapLayer("heatmap-id", "source-id");
        layer.setMaxZoom(12);
        layer.setSourceLayer("heatmapSource-id");
        layer.setProperties(
                //  Color Change
                heatmapColor(
                        interpolate(
                                linear(), heatmapDensity(),
                                literal(0), rgba(33, 102, 172, 0.2),
                                literal(0.2), rgba(103, 169, 207, 0.4),
                                literal(0.4), rgb(209, 229, 240),
                                literal(0.6), rgb(253, 219, 199),
                                literal(0.8), rgb(239, 138, 98),
                                literal(1), rgb(178, 24, 43)
                        )
                ),
                heatmapWeight(
                        interpolate(
                                linear(), get("mag"),
                                stop(0, 0),
                                stop(6, 1)
                        )
                ),
                heatmapIntensity(
                        interpolate(
                                linear(), zoom(),
                                stop(0, 1),
                                stop(4, 3)
//                                stop(9, 3)
                        )
                ),
                heatmapRadius(
                        interpolate(
                                linear(), zoom(),
                                stop(0, 2),
                                stop(4, 13)
//                                stop(9, 20)
                        )
                ),
                heatmapOpacity(
                        interpolate(
                                linear(), zoom(),
                                stop(3, 1),
                                stop(4, 0)
//                                stop(7, 1),
//                                stop(9, 0)
                        )
                )
        );
        style.addLayerAbove(layer, "waterway-label");
        CircleLayer circleLayer = new CircleLayer("circle-id","source-id");
        circleLayer.setProperties(
                // Size circle radius by earthquake magnitude and zoom level
                circleRadius(
                        interpolate(
                                linear(), zoom(),
                                literal(7), interpolate(
                                        linear(), get("mag"),
                                        stop(1, 1),
                                        stop(4, 4)
//                                        stop(6, 4)
                                ),
                                literal(16), interpolate(
                                        linear(), get("mag"),
                                        stop(1, 5),
                                        stop(4, 13)
//                                        stop(6, 20)
                                )
                        )
                ),
                // Color circle by earthquake magnitude
                circleColor(
                        interpolate(
                                linear(), get("mag"),
                                literal(1), rgba(33, 102, 172, 0),
                                literal(2), rgb(103, 169, 207),
                                literal(3), rgb(209, 229, 240),
                                literal(4), rgb(253, 219, 199),
                                literal(5), rgb(239, 138, 98),
                                literal(6), rgb(178, 24, 43)
                        )
                ),
                // Transition from heatmap to circle layer by zoom level
                circleOpacity(
                        interpolate(
                                linear(), zoom(),
                                stop(7, 0),
                                stop(4, 1)
//                                stop(8, 1)
                        )
                ),
                circleStrokeColor("white"),
                circleStrokeWidth(1.0f)
        );
        style.addLayerBelow(circleLayer,"heatmap-id");
    }
}
