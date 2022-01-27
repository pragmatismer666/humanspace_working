package com.dappcloud.humanspace.User.Infrastructure.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dappcloud.humanspace.Maps.MapsActivity;
import com.dappcloud.humanspace.Maps.Utils.Common;
import com.dappcloud.humanspace.Maps.Utils.Utils;
import com.dappcloud.humanspace.User.Infrastructure.Fragments.PostsFragment;
import com.dappcloud.humanspace.User.Infrastructure.Fragments.UserProfileFragment;
import com.dappcloud.humanspace.R;
import com.dappcloud.humanspace.User.Infrastructure.Fragments.StoryFragment;
import com.dappcloud.humanspace.User.Infrastructure.Fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Dashboard Activity
 */
public class UserDashboardActivity extends AppCompatActivity {
    private RelativeLayout fragment_container;
    private FloatingActionButton fab;
    private BottomNavigationView bottomNavigationView;
    private FirebaseUser firebaseUser;
    Utils utils = new Utils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        fragment_container = findViewById(R.id.fragment_container);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> handlePostButton());

        bottomNavigationView = findViewById(R.id.bottom_nav_menu);
        bottomMenu();
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_post);
        bottomNavigationView.setBackground(null);
        handleUserInstance();
        checkGpsPermission();
    }

    private void checkGpsPermission() {
        if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if ( manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
                utils.initLocation(this);
            }
            else {
                Toast.makeText(this, "You need to have gps provider to use this application!", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(this, "You need to allow location permission to use this application.", Toast.LENGTH_LONG).show();
        }
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_post);
    }

    private void handlePostButton() {
        Fragment fragment = new PostsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
    }

    private void handleUserInstance() {
        Bundle intent = getIntent().getExtras();
        if (intent != null) {
            String publisher = intent.getString("publisher");
            SharedPreferences.Editor editor = getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
            editor.putString("profileid", publisher);
            editor.apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new UserProfileFragment()).commit();
        }
    }

    private void bottomMenu() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.bottom_nav_dashboard:
                    if ( Common.CurrentUser == null || Common.UsersList == null ) {
                        ProgressDialog progressDialog = new ProgressDialog(this);
                        progressDialog.setMessage("Data loading in first sign In.");
                        progressDialog.show();
                        Utils utils = new Utils();
                        utils.currentUser(() -> {
                            utils.usersList(getApplicationContext());
                            progressDialog.dismiss();
                            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);//To maps
                            startActivityForResult(intent, Common.CALL_ACTIVITY_RESULT);
                        });
                    }
                    else if ( Common.UsersList != null && Common.CurrentUser == null ) {
                        Toast.makeText(this, " This account session is not existed. Please reinstall app.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else {
                        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);//To maps
                        startActivityForResult(intent, Common.CALL_ACTIVITY_RESULT);
                    }
                    break;
                case R.id.bottom_nav_people:
                    fragment = new StoryFragment();
                    break;
                case R.id.bottom_nav_post:
                    fragment = new PostsFragment();
                    break;
                case R.id.bottom_nav_chats:
                    Intent chat = new Intent(getApplicationContext(), MessageActivity.class);//To chats
                    startActivity(chat);
                    break;
                case R.id.bottom_nav_profile:
                    fragment = new ProfileFragment();
                    break;
            }
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
            return true;
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Common.CALL_ACTIVITY_RESULT) {
            if(resultCode == RESULT_OK) {
                if ( data != null && data.hasExtra("fragmentId") ) {
                    switch (data.getStringExtra("fragmentId")) {
                        case "people":
                            bottomNavigationView.setSelectedItemId(R.id.bottom_nav_people);
                            break;
                        case "post":
                            bottomNavigationView.setSelectedItemId(R.id.bottom_nav_post);
                            break;
                        case "profile":
                            bottomNavigationView.setSelectedItemId(R.id.bottom_nav_profile);
                            break;
                        case "dashboard":
                            bottomNavigationView.setSelectedItemId(R.id.bottom_nav_post);
                            break;
                    }
                }
            }
        }
    }

    private void status(String status) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("Offline");
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
