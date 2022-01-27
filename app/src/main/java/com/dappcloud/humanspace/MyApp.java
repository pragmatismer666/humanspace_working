package com.dappcloud.humanspace;

import android.app.Application;
import android.content.Intent;

import com.dappcloud.humanspace.Maps.Utils.Utils;
import com.dappcloud.humanspace.User.Infrastructure.Activities.UserDashboardActivity;
import com.dappcloud.humanspace.User.SigninSignup.UsersStartUpScreen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mapbox.search.MapboxSearchSdk;
import com.mapbox.search.location.DefaultLocationProvider;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MapboxSearchSdk.initialize(this, getString(R.string.access_token), new DefaultLocationProvider(this));

//        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (firebaseUser != null) {
//            Utils utils = new Utils();
//            utils.currentUser(() -> {
//                utils.usersList(getApplicationContext());
//            });
//        }
    }


}
