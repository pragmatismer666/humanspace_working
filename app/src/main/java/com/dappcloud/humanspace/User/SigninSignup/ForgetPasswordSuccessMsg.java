package com.dappcloud.humanspace.User.SigninSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.dappcloud.humanspace.R;

public class ForgetPasswordSuccessMsg extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_forget_password_success_msg);
    }

    public void callSignInScreen(View view) {
        startActivity(new Intent(getApplicationContext(), SignIn.class));
        finish();
    }
}
