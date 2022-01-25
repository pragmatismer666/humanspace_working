package com.dappcloud.humanspace.User.SigninSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dappcloud.humanspace.R;

import java.util.Calendar;

public class SignUp2ndScreen extends AppCompatActivity {

    private EditText email;
    private CheckBox checkbox;
    private Button next;
    private TextView error;

    String userPhone;
    String newsLater = "No";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_sign_up2nd_screen);

        email = findViewById(R.id.email);
        checkbox = findViewById(R.id.checkbox);
        error = findViewById(R.id.error);
        next = findViewById(R.id.next);

        next.setOnClickListener(v -> nextScreen());

    }

    private void nextScreen() {
        if (!validateEmail()) {
            return;
        }

        if (checkbox.isChecked()) {
            newsLater = "Yes";
        } else {
            newsLater = "No";
        }

        userPhone = getIntent().getStringExtra("phone");
        String _newsLater = newsLater;
        String _email = email.getText().toString();

        Intent intent = new Intent(getApplicationContext(), SignUp3rdScreen.class);
        intent.putExtra("phone", userPhone);
        intent.putExtra("email", _email);
        intent.putExtra("newsLater", _newsLater);
        startActivity(intent);
    }


    private boolean validateEmail() {
        String val = email.getText().toString().trim();
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            error.setVisibility(View.VISIBLE);
            error.setText("This field must not be empty");
            return false;
        } else if (!val.matches(checkEmail)) {
            error.setVisibility(View.VISIBLE);
            error.setText("Invalid, please provide a properly formatted email address!");
            return false;
        } else {
            email.setError(null);
            error.setVisibility(View.GONE);
            return true;
        }
    }
}
