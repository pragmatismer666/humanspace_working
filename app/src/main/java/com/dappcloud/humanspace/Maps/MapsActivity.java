package com.dappcloud.humanspace.Maps;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.dappcloud.humanspace.Maps.Utils.Common;
import com.dappcloud.humanspace.Maps.Utils.Utils;
import com.dappcloud.humanspace.Maps.search.SearchViewBottomSheetsMediator;
import com.dappcloud.humanspace.R;
import com.dappcloud.humanspace.User.Infrastructure.Activities.MessageActivity;
import com.dappcloud.humanspace.User.Infrastructure.Activities.UserDashboardActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import com.mapbox.search.result.SearchResult;
import com.mapbox.search.ui.view.SearchBottomSheetView;
import com.mapbox.search.ui.view.category.Category;
import com.mapbox.search.ui.view.category.SearchCategoriesBottomSheetView;
import com.mapbox.search.ui.view.place.SearchPlace;
import com.mapbox.search.ui.view.place.SearchPlaceBottomSheetView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Mapbox
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnScaleListener, MapboxMap.OnMapClickListener{

    private MapView mapView;
    private MapboxMap map;
    private MarkerViewManager markerViewManager;
    private GeoJsonSource geoJsonSource;

    LocationManager manager;
    Utils utils = new Utils();

    public ArrayList<String> searchList = new ArrayList<String>();
    private String selectedCategory;
    private float zoomStatus = 13.0F;
    private CircleImageView image_profile;
    ListView searchResultView;
    EditText searchEt;

    private Boolean maleIconFlag = false;
    private Boolean femaleIconFlag = false;
    private Boolean singleIconFlag = false;

    CircleImageView goldBtn;
    CircleImageView maleBtn;
    CircleImageView femaleBtn;
    CircleImageView singleBtn;
    private BottomNavigationView bottomNavigationView;
    // --------------------------- Search MapBox Api --------------
    ImageView hideSearch;
    SearchBottomSheetView searchBottomSheetView;
    SearchPlaceBottomSheetView searchPlaceView;
    SearchCategoriesBottomSheetView searchCategoriesView;

    SearchViewBottomSheetsMediator cardsMediator;
    ArrayList<Point> markerCoordinates = new ArrayList<Point>();
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("update_users")) {

            }
        }
    };

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_maps);
        // -------------------------------------------------------------------------- Search SDK API
        searchBottomSheetView = findViewById(R.id.search_view);
        searchBottomSheetView.initializeSearch(savedInstanceState, new SearchBottomSheetView.Configuration());
        hideSearch = findViewById(R.id.hideSearchUI);
        hideSearch.bringToFront();
        hideSearch.setOnClickListener(v->{
            searchBottomSheetView.setVisibility(View.GONE);
        });
        searchPlaceView = (SearchPlaceBottomSheetView)findViewById(R.id.search_place_view);
        searchPlaceView.setNavigateButtonVisible(false);
        searchPlaceView.setShareButtonVisible(false);
        searchPlaceView.setFavoriteButtonVisible(false);
        searchCategoriesView = findViewById(R.id.search_categories_view);

        cardsMediator = new SearchViewBottomSheetsMediator(searchBottomSheetView, searchPlaceView, searchCategoriesView);
        cardsMediator.addSearchBottomSheetsEventsListener(new SearchViewBottomSheetsMediator.SearchBottomSheetsEventsListener() {
            @Override
            public void onOpenPlaceBottomSheet(@NonNull SearchPlace place) {
                if ( markerCoordinates != null ){
                    markerCoordinates.clear();
                    markerCoordinates.add(place.getCoordinate());
                }
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(place.getCoordinate().latitude(), place.getCoordinate().longitude()), zoomStatus));
                utils.updateResultsOnMap(markerCoordinates, markerViewManager, MapsActivity.this, zoomStatus);
            }

            @Override
            public void onOpenCategoriesBottomSheet(@NonNull Category category) {}

            @Override
            public void onBackToMainBottomSheet() {
                if ( markerCoordinates != null ){
                    markerCoordinates.clear();
                }
                utils.updateResultsOnMap(markerCoordinates, markerViewManager, MapsActivity.this, zoomStatus);
            }
        });
        searchCategoriesView.addCategoryLoadingStateListener(new SearchCategoriesBottomSheetView.CategoryLoadingStateListener() {
            @Override
            public void onLoadingStart(@NonNull Category category) {}

            @Override
            public void onCategoryResultsLoaded(@NonNull Category category, @NonNull List<? extends SearchResult> list) {
                if ( markerCoordinates != null ){
                    markerCoordinates.clear();
                }
                if ( list.size() > 0 ) {
                    Point firstPoint = list.get(0).getCoordinate();
                    if ( firstPoint != null ) {
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(firstPoint.latitude(), firstPoint.longitude()), zoomStatus));
                    }
                    for (SearchResult searchResult : list) {
                       markerCoordinates.add(searchResult.getCoordinate());
                    }
                }
                utils.updateResultsOnMap(markerCoordinates, markerViewManager, MapsActivity.this, zoomStatus);
            }
            @Override
            public void onLoadingError(@NonNull Category category) {

            }
        });
        searchBottomSheetView.setVisibility(View.GONE);
        // -------------------------------------------------------------------------- Search SDK API
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
        if ( Common.CurrentUser == null || Common.UsersList == null ) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Check firebase connection.");
            progressDialog.show();
            Utils utils = new Utils();
            utils.currentUser(() -> {
                utils.usersList(getApplicationContext());
                progressDialog.dismiss();
                searchUI();
            });
        }
        else {
            searchUI();
        }
        selectedCategory = getString(R.string.all_categries);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this); //Init map
        setBottomBar();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"ResourceAsColor", "UseCompatLoadingForDrawables"})
    private void searchUI() {

        ArrayList<String> findMore = new ArrayList<>();
        findMore.add(getString(R.string.findMore));
        ArrayList<String> iconSearch = new ArrayList<>();
        goldBtn = findViewById(R.id.goldBtn);
        Glide.with(this).load(getDrawable(R.drawable.m_gold)).transform(new CircleCrop()).into(goldBtn);
        goldBtn.setCircleBackgroundColor(android.R.color.transparent);
        goldBtn.setOnClickListener(v->{
            startActivity(new Intent(this, UpgradeActivity.class));
            finish();
        });
        if ( Common.CurrentUser.getAccount().equals(getString(R.string.business))) {
            goldBtn.setVisibility(View.INVISIBLE);
        }
        maleBtn = findViewById(R.id.maleBtn);
        Glide.with(this).load(getDrawable(R.drawable.m_male)).transform(new CircleCrop()).into(maleBtn);
        maleBtn.setCircleBackgroundColor(getColor(R.color.noneBack));
        maleBtn.setOnClickListener(v->{
            if ( !maleIconFlag ) {
                changeUnPushedBack();
                iconSearch.add(getString(R.string.male).toUpperCase());
                utils.showHideMarkers(MapsActivity.this,iconSearch);
                maleBtn.setCircleBackgroundColor(getColor(R.color.pushedIcon));
                maleIconFlag = true;
            }
            else {
                iconSearch.add(getString(R.string.all_categries));
                utils.showHideMarkers(MapsActivity.this,iconSearch);
                maleBtn.setCircleBackgroundColor(getColor(R.color.noneBack));
                maleIconFlag = false;
            }
            iconSearch.clear();
        });
        femaleBtn = findViewById(R.id.femaleBtn);
        Glide.with(this).load(getDrawable(R.drawable.m_female)).transform(new CircleCrop()).into(femaleBtn);
        femaleBtn.setCircleBackgroundColor(getColor(R.color.noneBack));
        femaleBtn.setOnClickListener(v->{
            if ( !femaleIconFlag ) {
                changeUnPushedBack();
                iconSearch.add(getString(R.string.female).toUpperCase());
                utils.showHideMarkers(MapsActivity.this,iconSearch);
                femaleBtn.setCircleBackgroundColor(getColor(R.color.pushedIcon));
                femaleIconFlag = true;
            }
            else {
                iconSearch.add(getString(R.string.all_categries));
                utils.showHideMarkers(MapsActivity.this,iconSearch);
                femaleBtn.setCircleBackgroundColor(getColor(R.color.noneBack));
                femaleIconFlag = false;
            }
            iconSearch.clear();
        });
        singleBtn = findViewById(R.id.singleBtn);
        Glide.with(this).load(getDrawable(R.drawable.m_single)).transform(new CircleCrop()).into(singleBtn);
        singleBtn.setCircleBackgroundColor(getColor(R.color.noneBack));
        singleBtn.setOnClickListener(v->{
            if ( !singleIconFlag ) {
                changeUnPushedBack();
                iconSearch.add(getString(R.string.singles).toUpperCase());
                utils.showHideMarkers(MapsActivity.this,iconSearch);
                singleBtn.setCircleBackgroundColor(getColor(R.color.pushedIcon));
                singleIconFlag = true;
            }
            else {
                iconSearch.add(getString(R.string.all_categries));
                utils.showHideMarkers(MapsActivity.this,iconSearch);
                singleBtn.setCircleBackgroundColor(getColor(R.color.noneBack));
                singleIconFlag = false;
            }
            iconSearch.clear();
        });
        selectedCategory = getString(R.string.all_categries);
        searchList.addAll(Arrays.asList(Common.categories));
        if ( Common.CurrentUser.getCategory().equals(getString(R.string.gays))) {
            searchList.add(getString(R.string.gays));
        }
        searchResultView = findViewById(R.id.searchResultView);
        searchEt = findViewById(R.id.searchEt);
        searchEt.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if ( !searchList.contains(getString(R.string.findMore)) ) {
                    searchList.addAll(findMore);
                }
                updateListView(searchList);
                utils.showHideMarkers(MapsActivity.this, searchList);
            }
            else {
                updateListView(new ArrayList<>());
                utils.showHideMarkers(MapsActivity.this, new ArrayList<>());
            }
        });
        searchResultView.setOnItemClickListener((parent, view, position, id) -> {
            {
                String selected = searchResultView.getItemAtPosition(position).toString();
                if ( selected.equals(getString(R.string.findMore)) ) {
                    searchEt.setText("");
                    searchBottomSheetView.setVisibility(View.VISIBLE);
                } else {
                    searchEt.setText(selected);
                }
            }
        });
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String onCategory = s.toString();
                ArrayList<String> searchResult = new ArrayList<>();
                if ( onCategory.length() == 0 ){
                    searchResult.add(getString(R.string.all_categries));
                    searchEt.clearFocus();
                    // updateListView(new ArrayList<>());
                }
                else {
                    for ( int i = 0; i < searchList.size(); i++){
                        if ( searchList.get(i).toUpperCase().startsWith(onCategory.toUpperCase()) ){
                            searchResult.add(searchList.get(i).toUpperCase());
                        }
                    }
                    searchResult.addAll(findMore);
                    updateListView(searchResult);
                    utils.showHideMarkers(MapsActivity.this, searchResult);
                }
            }
        });
        ImageView searchView = findViewById(R.id.searchView);
        searchView.setOnClickListener(v->{
            if (searchEt.getText() != null) {
                if (checkSearchStrContain(searchList, searchEt.getText().toString())) {
                    try {
                        Date currentDate = Calendar.getInstance().getTime();
                        DatabaseReference existedRef = FirebaseDatabase.getInstance().getReference().child("Search").child(currentDate.toString() + "_" + Common.CurrentUser.getPhone().replace("+",""));
                        Map<String, String> searchLog = new HashMap<>();
                        searchLog.put(currentDate.toString() + "_" + Common.CurrentUser.getPhone().replace("+",""), searchEt.getText().toString());
                        existedRef.setValue(searchLog);
                    }
                    catch (Exception e){
                        System.out.println("search Save issue : " + e.getMessage());
                    }
                }
            }
            searchEt.setText("");
            Toast.makeText(this, getString(R.string.searchSave), Toast.LENGTH_LONG).show();
        });
    }

    private void updateListView(ArrayList<String> strings) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MapsActivity.this, android.R.layout.simple_list_item_1,strings);
        searchResultView.setAdapter(adapter);
        searchResultView.setBackgroundColor(getColor(R.color.white));
    }

    private void changeUnPushedBack() {
        if ( maleIconFlag ) {
            maleBtn.setCircleBackgroundColor(getColor(R.color.noneBack));
            maleIconFlag = false;
        }
        if ( femaleIconFlag ) {
            femaleBtn.setCircleBackgroundColor(getColor(R.color.noneBack));
            femaleIconFlag = false;
        }
        if ( singleIconFlag ) {
            singleBtn.setCircleBackgroundColor(getColor(R.color.noneBack));
            singleIconFlag = false;
        }
    }

    private boolean checkSearchStrContain(ArrayList<String> searchList, String text) {
        boolean exist = true;
        for ( int i = 0; i < searchList.size(); i++){
            if ( searchList.get(i).toUpperCase().startsWith(text.toUpperCase()) ){
                return false;
            }
        }
        return exist;
    }
    // ------------------------------------------------------------------- Finish search -----------

    @SuppressLint("NonConstantResourceId")
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
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), zoomStatus));
                }
                else if ( Common.CurrentUser.getLocation() != null ){
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Common.CurrentUser.getLocation(). getLat(), Common.CurrentUser.getLocation().getLng()), zoomStatus));
                }
                else {
                    Toast.makeText(this, "You need to have GPS location provider. This location is last location.", Toast.LENGTH_LONG).show();
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Common.CurrentUser.getLocation(). getLat(), Common.CurrentUser.getLocation().getLng()), zoomStatus));
                }
            }
            else {
                if (utils.getLocation() != null) {
                    Location loc = utils.getLocation();
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), zoomStatus));
                }
                else {
                    utils.initLocation(this);
                }
            }
        }
        else {
            Toast.makeText(this, "You don't have location permission, So you don't have location value.", Toast.LENGTH_LONG).show();
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-33.9, 18.5), zoomStatus));
        }
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        mapboxMap.addOnMapClickListener(this);
        mapboxMap.getUiSettings().setCompassEnabled(false);
        mapboxMap.getUiSettings().setAttributionEnabled(false);
        mapboxMap.getUiSettings().setLogoEnabled(false);
        map = mapboxMap;
        // Initialize the MarkerViewManager
        markerViewManager = new MarkerViewManager(mapView, map); //later this is used to draw user location on map
        if ( Common.CurrentUser != null) {
            geoJsonSource = new GeoJsonSource("source-id", Feature.fromGeometry(Point.fromLngLat(Common.CurrentUser.getLocation().getLng(),Common.CurrentUser.getLocation().getLng())));
            //We load the customized style that we created
            map.setStyle(new Style.Builder().fromUri("mapbox://styles/matshaba/ckn09kdl40x5217lib5pstr3m"), style -> {
                // map UI setting
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
                startActivity(new Intent(MapsActivity.this, UserDashboardActivity.class));
                finish();
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
        cardsMediator.onSaveInstanceState(outState);
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
        Log.d("Scale end", String.valueOf(zoomStatus));
        utils.changeMarkers(this, (float)(map.getCameraPosition().zoom));  // Change Marker size as real time
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        searchEt.clearFocus();
        return false;
    }
}
