package com.dappcloud.humanspace.User.SigninSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;

import com.dappcloud.humanspace.Maps.Utils.Utils;
import com.dappcloud.humanspace.R;
import com.dappcloud.humanspace.User.Infrastructure.Activities.UserDashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UsersStartUpScreen extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Utils utils = new Utils();
            utils.currentUser(() -> {
                utils.usersList(getApplicationContext());
                startActivity(new Intent(UsersStartUpScreen.this, UserDashboardActivity.class));
                finish();
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_users_start_up_screen);
    }

    public void callSignUpScreen(View view) {

        Intent intent = new Intent(getApplicationContext(),SignUp.class);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View,String>(findViewById(R.id.signup_btn),"transition_signup");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(UsersStartUpScreen.this,pairs);
            startActivity(intent,options.toBundle());
        }
        else {
            startActivity(intent);
        }
    }
}

