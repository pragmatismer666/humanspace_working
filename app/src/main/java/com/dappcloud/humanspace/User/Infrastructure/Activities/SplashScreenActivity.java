package com.dappcloud.humanspace.User.Infrastructure.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.dappcloud.humanspace.Maps.Utils.Utils;
import com.dappcloud.humanspace.R;
import com.dappcloud.humanspace.User.SigninSignup.SignIn;
import com.dappcloud.humanspace.User.SigninSignup.UsersStartUpScreen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {

    private static int SPLASH_TIMER = 3000;
    ImageView logo;
    TextView poweredBy;
    Animation sideAnim, bottomAnim;
    SharedPreferences onBoardingScreen, appEntry;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        logo = findViewById(R.id.logo);
        poweredBy = findViewById(R.id.powered_by);

        sideAnim = AnimationUtils.loadAnimation(this, R.anim.side_anim);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);

        logo.setAnimation(bottomAnim);
        poweredBy.setAnimation(sideAnim);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},111);
        }
        else {
            startHandler();
        }

    }

    private void startHandler() {
        new Handler().postDelayed(() -> {

            onBoardingScreen = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);
            boolean isFirstTime = onBoardingScreen.getBoolean("firstTime", true);
            appEntry = getSharedPreferences("appEntry", MODE_PRIVATE);
            boolean isFirstVisit = appEntry.getBoolean("firstVisit", true);

            if (isFirstTime) {
                SharedPreferences.Editor editor = onBoardingScreen.edit();
                editor.putBoolean("firstTime", false);
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), OnBoardingActivity.class);
                startActivity(intent);
            } else if (isFirstVisit) {
                SharedPreferences.Editor editor = appEntry.edit();
                editor.putBoolean("firstVisit", false);
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), UsersStartUpScreen.class);
                startActivity(intent);
            } else {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null) {
                    Utils utils = new Utils();
                    utils.currentUser(() -> {
                        utils.usersList(getApplicationContext());
                        startActivity(new Intent(SplashScreenActivity.this, UserDashboardActivity.class));
                        finish();
                    });
                } else {
                    Intent intent = new Intent(getApplicationContext(), SignIn.class);
                    startActivity(intent);
                }
            }

        }, SPLASH_TIMER);
    }
}

