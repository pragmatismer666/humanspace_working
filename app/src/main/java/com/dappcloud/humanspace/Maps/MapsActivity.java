package com.dappcloud.humanspace.Maps;


import android.animation.TypeEvaluator;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.dappcloud.humanspace.Maps.Utils.Common;
import com.dappcloud.humanspace.Maps.Utils.Utils;
import com.dappcloud.humanspace.R;
import com.dappcloud.humanspace.User.Infrastructure.Activities.ChattingActivity;
import com.dappcloud.humanspace.User.Infrastructure.Activities.MessageActivity;
import com.dappcloud.humanspace.User.Infrastructure.Activities.UserDashboardActivity;
import com.dappcloud.humanspace.User.Infrastructure.Fragments.PostsFragment;
import com.dappcloud.humanspace.User.Infrastructure.Fragments.ProfileFragment;
import com.dappcloud.humanspace.User.Infrastructure.Fragments.StoryFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.android.gestures.StandardScaleGestureDetector;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Mapbox
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnScaleListener{

    private MapView mapView;
    private MapboxMap map;
    private MarkerViewManager markerViewManager;
    private GeoJsonSource geoJsonSource;

    LocationManager manager;
    Utils utils = new Utils();
    public double zoomValue = 13.0;

    public ArrayList<String> searchList = new ArrayList<String>();
    private String selectedCategory;
    private float zoomStatus = 13.0F;
    private CircleImageView image_profile;
    EditText searchEt;

    private BottomNavigationView bottomNavigationView;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("update_users")) {

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_maps);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        image_profile = findViewById(R.id.image_profile);
        if ( Common.CurrentUser != null ){
            Glide.with(this).load(Common.CurrentUser.getImageurl()).into(image_profile);
        }
        image_profile.setOnClickListener(v-> {
            Intent profileIntent = new Intent(this, UserDashboardActivity.class);
            profileIntent.putExtra("profile","map");
            startActivity(profileIntent);
        });
        ImageButton setBtn = findViewById(R.id.setBtn);
        setBtn.setOnClickListener(v->{
            startActivity(new Intent(this, SettingActivity.class));
            finish();
        });
        selectedCategory = getString(R.string.all_categries);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this); //Init map
        // -----------------------   Search Part  -------------------------------
        setBottomBar();
    }

    private void setBottomBar() {
        bottomNavigationView = findViewById(R.id.bottom_nav_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            String mapBottomVal = "";
            if(item.getItemId() == R.id.bottom_nav_chats) {
                Intent chat = new Intent(getApplicationContext(), MessageActivity.class);//To chats
                startActivity(chat);
                finish();
            } else {
                switch (item.getItemId()) {
                    case R.id.bottom_nav_dashboard:
                        mapBottomVal = "dashboard";
                        break;
                    case R.id.bottom_nav_people:
                        mapBottomVal = "people";
                        break;
                    case R.id.bottom_nav_post:
                    mapBottomVal = "post";
                        break;
                    case R.id.bottom_nav_profile:
                    mapBottomVal = "profile";
                        break;
                }
                Intent intent = new Intent();
                intent.putExtra("fragmentId", mapBottomVal);
                setResult(RESULT_OK, intent);
                finish();
                // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
            return true;
        });
        bottomNavigationView.setBackground(null);
    }

    @SuppressWarnings("MissingPermission")
    private void initLocation() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, "You need to have GPS location provider. This location is last location.", Toast.LENGTH_LONG).show();
                if (utils.getLocation() != null) {
                    Location loc = utils.getLocation();
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), zoomValue));
                }
                else if ( Common.CurrentUser.getLocation() != null ){
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Common.CurrentUser.getLocation(). getLat(), Common.CurrentUser.getLocation().getLng()), zoomValue));
                }
                else {
                    Toast.makeText(this, "You need to have GPS location provider. This location is last location.", Toast.LENGTH_LONG).show();
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Common.CurrentUser.getLocation(). getLat(), Common.CurrentUser.getLocation().getLng()), zoomValue));
                }
            }
            else {
                if (utils.getLocation() != null) {
                    Location loc = utils.getLocation();
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), zoomValue));
                }
                else {
                    utils.initLocation(this);
                }
            }
        }
        else {
            Toast.makeText(this, "You don't have location permission, So you don't have location value.", Toast.LENGTH_LONG).show();
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-33.9, 18.5), zoomValue));
        }
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        map = mapboxMap;
        if ( Common.CurrentUser != null) {
            geoJsonSource = new GeoJsonSource("source-id", Feature.fromGeometry(Point.fromLngLat(Common.CurrentUser.getLocation().getLng(),Common.CurrentUser.getLocation().getLng())));
            //We load the customized style that we created
            map.setStyle(new Style.Builder().fromUri("mapbox://styles/matshaba/ckn09kdl40x5217lib5pstr3m"), style -> {
                // map UI setting
                mapboxMap.getUiSettings().setCompassEnabled(false);
                mapboxMap.getUiSettings().setAttributionEnabled(false);
                mapboxMap.getUiSettings().setLogoEnabled(false);
                // Initialize the MarkerViewManager
                markerViewManager = new MarkerViewManager(mapView, map); //later this is used to draw user location on map
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                // Add heatmap on the mapbox map
                utils.addHeatmap(geoJsonSource, style);

                String userSet = "non";
                if (pref.getBoolean("ghost",false)) {
                    userSet = "ghost";
                } else if ( pref.getBoolean("offset",false) ) {
                    userSet = "offset";
                }
                zoomStatus = (float)map.getCameraPosition().zoom;
                // Add user marker on mapbox map
                utils.addMarkers(MapsActivity.this, markerViewManager, zoomStatus, userSet, selectedCategory); //Helper function to add custom view as a marker on the map
                initLocation(); //Helper function to check if Location permission is given or not
            });
            map.addOnScaleListener(this);
        }
        else {
            if ( Common.CurrentUser == null || Common.UsersList == null ) {
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Updating user data. Please wait a few seconds.");
                progressDialog.show();
                Utils utils = new Utils();
                utils.currentUser(() -> {
                    utils.usersList(getApplicationContext());
                    progressDialog.dismiss();
                });
            }
            else if ( Common.UsersList != null && Common.CurrentUser == null ) {
                Toast.makeText(this, "There is problem to connect Database.", Toast.LENGTH_LONG).show();
                finish();
                startActivity(new Intent(MapsActivity.this, UserDashboardActivity.class));
            }
        }
    }

    //Since the Map box is having its own lifecycle then we will override all those function
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if ( markerViewManager != null ){
            utils.clearMarkers(markerViewManager);
        }
        if (markerViewManager != null)
            markerViewManager.onDestroy();
        mapView.getMapAsync(this);
        IntentFilter filter = new IntentFilter("update_users");
        registerReceiver(broadcastReceiver,filter);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ( markerViewManager != null ){
            utils.clearMarkers(markerViewManager);
        }
        if (markerViewManager != null)
            markerViewManager.onDestroy();
    }

    //  Zoom Event Capture
    @Override
    public void onScaleBegin(@NonNull StandardScaleGestureDetector detector) {
    }

    @Override
    public void onScale(@NonNull StandardScaleGestureDetector detector) {
    }

    @Override
    public void onScaleEnd(@NonNull StandardScaleGestureDetector detector) {
        Log.d("Scale end", String.valueOf(zoomValue));
        zoomStatus = (float) (map.getCameraPosition().zoom);
        zoomValue = zoomStatus;
        utils.changeMarkers(this, (float) zoomValue);  // Change Marker size as real time
    }

}
