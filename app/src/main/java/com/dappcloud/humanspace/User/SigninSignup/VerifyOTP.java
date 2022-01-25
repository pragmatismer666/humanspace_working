package com.dappcloud.humanspace.User.SigninSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.chaos.view.PinView;
import com.dappcloud.humanspace.R;
import com.dappcloud.humanspace.User.Infrastructure.Activities.UserDashboardActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyOTP extends AppCompatActivity {
    private PinView pinFromUser;
    private LottieAnimationView progress_bar;
    private Button verify;
    private TextView textPhoneNumber, resend_otp, error;

    FirebaseAuth mAuth;
    String verificationId, userPhone, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_verify_otp);

        mAuth = FirebaseAuth.getInstance();

        verificationId = getIntent().getStringExtra("codeSent");
        userPhone = getIntent().getStringExtra("phone");
        username = getIntent().getStringExtra("username");
        textPhoneNumber = findViewById(R.id.textPhoneNumber);
        textPhoneNumber.setText(String.format(
                userPhone.substring(0,3)+"......"+userPhone.substring(userPhone.length() - 3)
        ));

        pinFromUser = findViewById(R.id.pin_view);
        progress_bar = findViewById(R.id.progress_bar);
        error = findViewById(R.id.error);
        verify = findViewById(R.id.verify);
        resend_otp = findViewById(R.id.resend_otp);

        verify.setOnClickListener(v -> VerifyOtp());
        resend_otp.setOnClickListener(v -> ResendCode());
    }

    private void ResendCode() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                userPhone,
                60,
                TimeUnit.SECONDS,
                VerifyOTP.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        error.setVisibility(View.VISIBLE);
                        error.setText(e.getMessage());
                    }

                    @Override
                    public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        verificationId = newVerificationId;
                        Toast.makeText(VerifyOTP.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void VerifyOtp() {
        if (!ValidatePin()) {
            return;
        }

        String code = pinFromUser.getText().toString();
        if (username != null) {
            if (verificationId != null) {
                progress_bar.setVisibility(View.VISIBLE);
                verify.setVisibility(View.GONE);
                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                        verificationId,
                        code
                );
                FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener(task -> {
                            progress_bar.setVisibility(View.GONE);
                            verify.setVisibility(View.VISIBLE);
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(getApplicationContext(), UserDashboardActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                error.setVisibility(View.VISIBLE);
                                error.setText("The verification code entered is invalid");
                            }
                        });
            }
        } else {
            if (verificationId != null) {
                progress_bar.setVisibility(View.VISIBLE);
                verify.setVisibility(View.GONE);
                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                        verificationId,
                        code
                );
                FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener(task -> {
                            progress_bar.setVisibility(View.GONE);
                            verify.setVisibility(View.VISIBLE);
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(getApplicationContext(), SignUp2ndScreen.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("phone", userPhone);
                                startActivity(intent);
                            } else {
                                error.setVisibility(View.VISIBLE);
                                error.setText("The verification code entered is invalid");
                            }
                        });
            }

        }
    }

    private boolean ValidatePin() {
        String val = pinFromUser.getText().toString().trim();

        if (val.isEmpty()) {
            error.setVisibility(View.VISIBLE);
            error.setText("This field is required*");
            return false;
        } else {
            error.setVisibility(View.GONE);
            return true;
        }
    }
}
