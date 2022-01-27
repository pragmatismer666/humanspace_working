package com.dappcloud.humanspace.User.SigninSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.dappcloud.humanspace.User.Infrastructure.Activities.UserDashboardActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.dappcloud.humanspace.Common.CheckConn.isConnectedToInternet;

public class SignIn extends AppCompatActivity {

    private EditText username, phone;
    private CountryCodePicker codePicker;
    private Button signin_btn, sign_up;
    private LottieAnimationView progress_bar;
    private TextView username_error, error, sign_up_desc;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            startActivity(new Intent(SignIn.this, UserDashboardActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_sign_in);

        username = findViewById(R.id.username);
        phone = findViewById(R.id.phone);
        codePicker = findViewById(R.id.codePicker);
        signin_btn = findViewById(R.id.signin_btn);
        username_error = findViewById(R.id.username_error);
        error = findViewById(R.id.error);
        progress_bar = findViewById(R.id.progress_bar);
        sign_up_desc = findViewById(R.id.sign_up_desc);
        sign_up = findViewById(R.id.sign_up);

        signin_btn.setOnClickListener(v -> signIn());
        sign_up.setOnClickListener(v -> signUp());

    }

    private void signUp() {
        Intent intent = new Intent(getApplicationContext(), SignUp.class);
        startActivity(intent);
    }

    private void signIn() {
        if (!ValidatePhoneNumber() | !validateUsername()) {
            return;
        }

        String _getUserPhone = phone.getText().toString();
//        if (_getUserPhone.charAt(0) == '0') {
//            phone.setError("Phone must not start with 0. Please enter the last 9 digits of your phone*");
//            phone.requestFocus();
//            return;
//        }

        progress_bar.setVisibility(View.VISIBLE);
        signin_btn.setVisibility(View.GONE);
        sign_up_desc.setVisibility(View.GONE);
        sign_up.setVisibility(View.GONE);

        checkIfUserExist();

    }

    private void checkIfUserExist() {
        String user = username.getText().toString().trim();
        Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username").equalTo(user);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    SendOtp();
                    progress_bar.setVisibility(View.GONE);
                    signin_btn.setVisibility(View.VISIBLE);
                    sign_up_desc.setVisibility(View.VISIBLE);
                    sign_up.setVisibility(View.VISIBLE);
                    username_error.setVisibility(View.GONE);
                } else {
                    progress_bar.setVisibility(View.GONE);
                    signin_btn.setVisibility(View.VISIBLE);
                    sign_up_desc.setVisibility(View.VISIBLE);
                    sign_up.setVisibility(View.VISIBLE);
                    username_error.setVisibility(View.VISIBLE);
                    username_error.setText("Username does not exist please Create Account");
                    username.setError("Username does not exist please Create Account");
                    username.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void SendOtp() {

        codePicker.registerCarrierNumberEditText(phone);
        String userPhone = codePicker.getFullNumberWithPlus().replace("", "");
        String user = username.getText().toString().trim();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                userPhone,
                60,
                TimeUnit.SECONDS,
                SignIn.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        progress_bar.setVisibility(View.GONE);
                        signin_btn.setVisibility(View.VISIBLE);
                        sign_up_desc.setVisibility(View.VISIBLE);
                        sign_up.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        progress_bar.setVisibility(View.GONE);
                        signin_btn.setVisibility(View.VISIBLE);
                        sign_up_desc.setVisibility(View.VISIBLE);
                        sign_up.setVisibility(View.VISIBLE);
                        error.setVisibility(View.VISIBLE);
                        error.setText(e.getMessage());
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        progress_bar.setVisibility(View.GONE);
                        signin_btn.setVisibility(View.VISIBLE);
                        sign_up_desc.setVisibility(View.VISIBLE);
                        sign_up.setVisibility(View.VISIBLE);
                        Intent intent = new Intent(getApplicationContext(), VerifyOTP.class);
                        intent.putExtra("username", user);
                        intent.putExtra("phone", userPhone);
                        intent.putExtra("codeSent", verificationId);
                        startActivity(intent);
                    }
                }
        );
    }

    private boolean validateUsername() {
        String _username = username.getText().toString().trim();

        if (_username.isEmpty()) {
            username.setError("Please enter your username");
            username.requestFocus();
            return false;
        } else {
            username.setError(null);
            return true;
        }
    }

    private boolean ValidatePhoneNumber() {
        String val = phone.getText().toString().trim();

        if (val.isEmpty()) {
            phone.setError("This field is required*");
            phone.requestFocus();
            return false;
        }
//        else if (val.length() != 9) {
//            phone.setError("Your phone must be the last 9 digits*");
//            phone.requestFocus();
//            return false;
//        }
        else {
            phone.setError(null);
            return true;
        }
    }

}

