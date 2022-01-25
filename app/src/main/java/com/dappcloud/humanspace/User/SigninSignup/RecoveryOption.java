package com.dappcloud.humanspace.User.SigninSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dappcloud.humanspace.R;

public class RecoveryOption extends AppCompatActivity {

    TextView phone, email;
    String userPhone, userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_recovery_option);

        phone = findViewById(R.id.phone_recovery);
        email = findViewById(R.id.email_recovery);

        userPhone = getIntent().getStringExtra("phone");
        phone.setText(String.format(
                userPhone.substring(0,3)+"......"+userPhone.substring(userPhone.length() - 3)
        ));
        userEmail = getIntent().getStringExtra("email");
        email.setText(String.format(
                userEmail.replaceAll("(^[^@]{3}|(?!^)\\G)[^@]", "$1*")
        ));
    }

    public void callSignInScreen(View view) {
        startActivity(new Intent(getApplicationContext(), SignIn.class));
        finish();
    }

    public void callOtpScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), VerifyOTP.class);
        intent.putExtra("phone", userPhone);
        intent.putExtra("whatToDO", "updateData");
        startActivity(intent);
        finish();

    }

    public void callEmailScreen(View view) {

    }
}

