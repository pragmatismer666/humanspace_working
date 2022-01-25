package com.dappcloud.humanspace.User.SigninSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dappcloud.humanspace.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.dappcloud.humanspace.Common.CheckConn.isConnectedToInternet;

public class ForgetPassword extends AppCompatActivity {

    TextInputLayout name;
    ProgressBar progressBar;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_forget_password);

        name = findViewById(R.id.recovery_username);
        next = findViewById(R.id.forget_password_next_btn);
        progressBar = findViewById(R.id.progress_bar);
    }

    public void backPress(View view) {
        finish();
    }

    public void callRecoveryOptionScreen(View view) {

        if (isConnectedToInternet(this)) {

            if (!validateUsername()) {
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            next.setVisibility(View.INVISIBLE);

            final String _username = name.getEditText().getText().toString().trim();
            Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username").equalTo(_username);
            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        name.setError(null);
                        name.setErrorEnabled(false);

                        String _phone = snapshot.child(_username).child("phone").getValue(String.class);
                        String _email = snapshot.child(_username).child("email").getValue(String.class);

                        Intent intent = new Intent(getApplicationContext(),RecoveryOption.class);
                        intent.putExtra("phone", _phone);
                        intent.putExtra("email", _email);

                        Pair[] pairs = new Pair[1];
                        pairs[0] = new Pair<View,String>(findViewById(R.id.forget_password_next_btn),"transition_next_btn");

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ForgetPassword.this,pairs);
                            startActivity(intent,options.toBundle());
                        }
                        else {
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        next.setVisibility(View.VISIBLE);
                        name.setError("No such user exist!");
                        name.requestFocus();
                        return;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                    next.setVisibility(View.VISIBLE);
                    Toast.makeText(ForgetPassword.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            progressBar.setVisibility(View.GONE);
            next.setVisibility(View.VISIBLE);
            //showCustomDialog();
            Toast.makeText(ForgetPassword.this, "Please make sure you are connected to the internet to sign in.", Toast.LENGTH_SHORT).show();
        }

    }

    public void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPassword.this);
        builder.setMessage("Please make sure you are connected to the internet to sign in.")
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), UsersStartUpScreen.class));
                        finish();
                    }
                });
    }

    private boolean validateUsername() {
        String val = name.getEditText().getText().toString().trim();
        String checkSpaces = "\\A\\w{1,20}\\z";

        if (val.isEmpty()) {
            name.setError("Please provide your username");
            return false;
        } else if (val.length() > 20) {
            name.setError("Username must not exceed 20 character!");
            return false;
        } else if (!val.matches(checkSpaces)) {
            name.setError("No white spaces are allowed!");
            return false;
        } else {
            name.setError(null);
            name.setErrorEnabled(false);
            return true;
        }
    }
}

