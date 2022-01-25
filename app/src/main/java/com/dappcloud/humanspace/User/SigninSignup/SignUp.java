package com.dappcloud.humanspace.User.SigninSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.dappcloud.humanspace.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class SignUp extends AppCompatActivity {

    private CountryCodePicker codePicker;
    private EditText phone;
    private TextView error;
    private Button next;
    private LottieAnimationView progress_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_sign_up);

        codePicker = findViewById(R.id.codePicker);
        phone = findViewById(R.id.phone);
        error = findViewById(R.id.error);
        next = findViewById(R.id.next);
        progress_bar = findViewById(R.id.progress_bar);

        next.setOnClickListener(v -> SendOtp());
    }

    private void SendOtp() {
        if (!ValidatePhoneNumber()) {
            return;
        }

        progress_bar.setVisibility(View.VISIBLE);
        next.setVisibility(View.GONE);
        String _getUserPhone = phone.getText().toString();
        if (_getUserPhone.charAt(0) == '0') {
            error.setVisibility(View.VISIBLE);
            error.setText("Phone must not start with 0. Please enter the last 9 digits of your phone*");
            return;
        }

        codePicker.registerCarrierNumberEditText(phone);
        String userPhone = codePicker.getFullNumberWithPlus().replace("", "");

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                userPhone,
                60,
                TimeUnit.SECONDS,
                SignUp.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        progress_bar.setVisibility(View.GONE);
                        next.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        progress_bar.setVisibility(View.GONE);
                        next.setVisibility(View.VISIBLE);
                        error.setVisibility(View.VISIBLE);
                        error.setText(e.getMessage());
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        progress_bar.setVisibility(View.GONE);
                        next.setVisibility(View.VISIBLE);
                        Intent intent = new Intent(getApplicationContext(), VerifyOTP.class);
                        intent.putExtra("phone", userPhone);
                        intent.putExtra("codeSent", verificationId);
                        startActivity(intent);
                    }
                }
        );
    }

    private boolean ValidatePhoneNumber() {
        String val = phone.getText().toString().trim();

        if (val.isEmpty()) {
            error.setVisibility(View.VISIBLE);
            error.setText("This field is required*");
            return false;
        }
//        else if (val.length() != 9) {
//            error.setVisibility(View.VISIBLE);
//            error.setText("Your phone must be the last 9 digits*");
//            return false;
//        }
        else {
            error.setVisibility(View.GONE);
            return true;
        }
    }

}


