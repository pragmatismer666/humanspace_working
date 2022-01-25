package com.dappcloud.humanspace.User.SigninSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dappcloud.humanspace.R;

import java.util.Calendar;

public class SignUp5thScreen extends AppCompatActivity {

    private DatePicker datePicker;
    private RadioGroup radioGroup;
    private RadioButton selectedGender;
    private Button next;
    private TextView error;

    String userPhone, email, newsLater, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up5th_screen);

        datePicker = findViewById(R.id.age_picker);
        radioGroup = findViewById(R.id.radio_group);
        error = findViewById(R.id.error);
        next = findViewById(R.id.next);

        next.setOnClickListener(v -> nextScreen());
    }

    private void nextScreen() {
        if (!validateGender() | !validateAge()) {
            return;
        }

        userPhone = getIntent().getStringExtra("phone");
        email = getIntent().getStringExtra("email");
        newsLater = getIntent().getStringExtra("newsLater");
        name = getIntent().getStringExtra("name");
        selectedGender =findViewById(radioGroup.getCheckedRadioButtonId());
        String _gender = selectedGender.getText().toString();

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth()+1;
        int year = datePicker.getYear();
        String _date = day+"/"+month+"/"+year;

        Intent intent = new Intent(getApplicationContext(), SignUp6thScreen.class);
        intent.putExtra("phone", userPhone);
        intent.putExtra("email", email);
        intent.putExtra("newsLater", newsLater);
        intent.putExtra("name", name);
        intent.putExtra("gender", _gender);
        intent.putExtra("date", _date);
        startActivity(intent);

    }

    private boolean validateGender() {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            error.setVisibility(View.VISIBLE);
            error.setText("Please Select Gender");
            return false;
        } else {
            return true;
        }
    }

    private boolean validateAge() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int userAge = datePicker.getYear();
        int isAgeValid = currentYear - userAge;

        if (isAgeValid < 12) {
            error.setVisibility(View.VISIBLE);
            error.setText("You must be at least 13 years of age!");
            Toast.makeText(this, "You must be at least 13 years of age!", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }
}
