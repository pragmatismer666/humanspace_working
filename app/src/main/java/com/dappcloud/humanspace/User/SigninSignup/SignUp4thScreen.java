package com.dappcloud.humanspace.User.SigninSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.dappcloud.humanspace.Common.SessionManager;
import com.dappcloud.humanspace.Databases.User;
import com.dappcloud.humanspace.Databases.UserLocation;
import com.dappcloud.humanspace.Maps.Utils.Common;
import com.dappcloud.humanspace.Maps.Utils.CurrentLocation;
import com.dappcloud.humanspace.R;
import com.dappcloud.humanspace.User.Infrastructure.Activities.FeaturedActivity;
import com.dappcloud.humanspace.User.Infrastructure.Activities.UserDashboardActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignUp4thScreen extends AppCompatActivity {

   private EditText name;
   private TextView error;
   private Button next;

    String userPhone, email, newsLater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_sign_up4th_screen);

        name = findViewById(R.id.name);
        error = findViewById(R.id.error);
        next = findViewById(R.id.next);

        next.setOnClickListener(v -> nextScreen());

    }

    private void nextScreen() {
        if (!validateName()) {
            return;
        }
        userPhone = getIntent().getStringExtra("phone");
        email = getIntent().getStringExtra("email");
        newsLater = getIntent().getStringExtra("newsLater");
        String _name = name.getText().toString();

        Intent intent = new Intent(getApplicationContext(), SignUp5thScreen.class);
        intent.putExtra("phone", userPhone);
        intent.putExtra("email", email);
        intent.putExtra("newsLater", newsLater);
        intent.putExtra("name", _name);
        startActivity(intent);

    }

    private boolean validateName() {
        String val = name.getText().toString().trim();

        if (val.isEmpty()) {
            error.setVisibility(View.VISIBLE);
            error.setText("This field must not be empty");
            return false;
        } else {
            name.setError(null);
            error.setVisibility(View.GONE);
            return true;
        }
    }


}

