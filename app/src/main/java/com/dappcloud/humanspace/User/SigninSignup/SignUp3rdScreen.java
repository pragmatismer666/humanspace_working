package com.dappcloud.humanspace.User.SigninSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dappcloud.humanspace.R;

public class SignUp3rdScreen extends AppCompatActivity {

    private Button next;

    String userPhone, email, newsLater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up3rd_screen);

        next = findViewById(R.id.next);
        next.setOnClickListener(v -> nextScreen());
    }

    private void nextScreen() {
        userPhone = getIntent().getStringExtra("phone");
        email = getIntent().getStringExtra("email");
        newsLater = getIntent().getStringExtra("newsLater");

        Intent intent = new Intent(getApplicationContext(), SignUp4thScreen.class);
        intent.putExtra("phone", userPhone);
        intent.putExtra("email", email);
        intent.putExtra("newsLater", newsLater);
        startActivity(intent);
    }
}
