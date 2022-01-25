package com.dappcloud.humanspace.User.SigninSignup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dappcloud.humanspace.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.dappcloud.humanspace.Common.CheckConn.isConnectedToInternet;

public class SetNewPassword extends AppCompatActivity {

    TextInputLayout newPassword, confirmPassword;
    Button updatePassword;
    ProgressBar progressBar;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_set_new_password);

        newPassword = findViewById(R.id.new_password);
        confirmPassword = findViewById(R.id.confirm_password);
        updatePassword = findViewById(R.id.update_password);
        progressBar = findViewById(R.id.progress_bar);

        username = getIntent().getStringExtra("username");
    }

    public void callSignInScreen(View view) {
        startActivity(new Intent(getApplicationContext(), SignIn.class));
        finish();
    }

    public void callSuccessScreen(View view) {

        if (isConnectedToInternet(this)) {
            if (!validateNewPassword() | !validateConfirmPassword() | !validatePassword()) {
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            updatePassword.setVisibility(View.INVISIBLE);

            String _newPassword = newPassword.getEditText().getText().toString().trim();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(username).child("password").setValue(_newPassword);

            startActivity(new Intent(getApplicationContext(), ForgetPasswordSuccessMsg.class));
            finish();

        } else {
            //showCustomDialog();
            //return;
            Toast.makeText(SetNewPassword.this, "Please make sure you are connected to the internet.", Toast.LENGTH_SHORT).show();
        }
    }

    public void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SetNewPassword.this);
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

    private boolean validateNewPassword() {
        String _password = newPassword.getEditText().getText().toString().trim();

        String checkPassword = "^" +
                //"(?=.*[0-9])" +            //at least one digit
                //"(?=.*[a-z])" +            //at least one lower case letter
                //"(?=.*[A-Z])" +            //at least one upper case letter
                //"(?=.*[a-zA-Z])" +         //any letter
                //"(?=.*[@#$%^&+=])" +       //at least one special character
                "(?=\\S+$)" +              //no white spaces
                ".{6,}" +                  //at least 6 characters
                "$";

        if (_password.isEmpty()) {
            newPassword.setError("Password field must not be empty");
            return false;
        } else if (!_password.matches(checkPassword)) {
            newPassword.setError("Password should contain at least 6 characters!");
            return false;
        } else {
            newPassword.setError(null);
            newPassword.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateConfirmPassword() {
        String _password = confirmPassword.getEditText().getText().toString().trim();

        if (_password.isEmpty()) {
            confirmPassword.setError("Password can not be empty");
            confirmPassword.requestFocus();
            return false;
        } else {
            confirmPassword.setError(null);
            confirmPassword.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword() {
        String _newPassword = newPassword.getEditText().getText().toString().trim();
        String _confirmPassword = confirmPassword.getEditText().getText().toString().trim();

        if (_confirmPassword != _newPassword) {
            confirmPassword.setError("Confirm Password does not match New Password");
            confirmPassword.requestFocus();
            return false;
        } else {
            confirmPassword.setError(null);
            confirmPassword.setErrorEnabled(false);
            return true;
        }
    }
}
