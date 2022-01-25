package com.dappcloud.humanspace.Maps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dappcloud.humanspace.Databases.User;
import com.dappcloud.humanspace.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.security.AccessController.getContext;

public class SettingActivity extends AppCompatActivity {

    CheckBox ghostMode, offset;
    EditText rValueEt, sValueEt;
    Button updateSetBtn;

    boolean ghostFlag = false;
    boolean offsetFlag = false;

    CircleImageView image_profile;
    TextView save_text;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_setting);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        setUserData();
        defineUIElement();
    }

    private void setUserData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null) {
                    return;
                }
                User user = snapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void defineUIElement() {
        ghostMode = findViewById(R.id.ghostCheck);
        offset = findViewById(R.id.offsetCheck);
        rValueEt = findViewById(R.id.rValueEt);
        sValueEt = findViewById(R.id.sValueEt);
        updateSetBtn = findViewById(R.id.updateSetBtn);
        image_profile = findViewById(R.id.image_profile);
        save_text = findViewById(R.id.save_text);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        ghostFlag = pref.getBoolean("ghost",false);
        offsetFlag = pref.getBoolean("offset",false);
        String r = String.valueOf(pref.getInt("rVal", 10));
        String s = String.valueOf(pref.getInt("sVal", 10));
        if (!r.equals("10")) {
            rValueEt.setText(r);
        }
        if (!s.equals("10")) {
            sValueEt.setText(s);
        }
        offset.setChecked(offsetFlag);
        ghostMode.setChecked(ghostFlag);
        if ( ghostFlag ) {
            offset.setEnabled(false);
            rValueEt.setEnabled(false);
            sValueEt.setEnabled(false);
        } else {
            offset.setEnabled(true);
            if ( offsetFlag ) {
                rValueEt.setEnabled(true);
                sValueEt.setEnabled(true);
            } else {
                rValueEt.setEnabled(false);
                sValueEt.setEnabled(false);
            }
        }

        ghostMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ghostFlag = isChecked;
            editor.putBoolean("ghost", isChecked);
            editor.apply();
            if ( isChecked ){
                save_text.setVisibility(View.VISIBLE);
                offset.setEnabled(false);
                rValueEt.setEnabled(false);
                sValueEt.setEnabled(false);
            } else {
                save_text.setVisibility(View.GONE);
                offset.setEnabled(true);
                if ( offsetFlag ) {
                    rValueEt.setEnabled(true);
                    sValueEt.setEnabled(true);
                } else {
                    rValueEt.setEnabled(false);
                    sValueEt.setEnabled(false);
                }
            }
        });
        offset.setOnCheckedChangeListener((buttonView, isChecked) -> {
            offsetFlag = isChecked;
            editor.putBoolean("offset", isChecked);
            editor.commit();
            if ( !isChecked ){
                save_text.setVisibility(View.VISIBLE);
                rValueEt.setEnabled(false);
                sValueEt.setEnabled(false);
            } else {
                save_text.setVisibility(View.GONE);
                rValueEt.setEnabled(true);
                sValueEt.setEnabled(true);
            }
        });
        updateSetBtn.setOnClickListener(v->{
            int r_int = 0, s_int = 0;
            if (!rValueEt.getText().toString().isEmpty()){
                r_int = Integer.parseInt(rValueEt.getText().toString());
                editor.putInt("rVal", Integer.parseInt(rValueEt.getText().toString()));
            }
            if (!sValueEt.getText().toString().isEmpty()){
                s_int = Integer.parseInt(sValueEt.getText().toString());
                editor.putInt("sVal", Integer.parseInt(sValueEt.getText().toString()));
            }
            if ( offsetFlag ) {
                if ( 5000000 >= r_int && r_int >= 10 && s_int <= 5000000 && s_int >= 10 ) {
                    editor.putInt("rVal", Integer.parseInt(rValueEt.getText().toString()));
                    editor.putInt("sVal", Integer.parseInt(sValueEt.getText().toString()));
                    editor.commit();
                    Intent intent = new Intent(this, MapsActivity.class);
                    startActivity(intent);
                    finish();
                } else if ( 5000000 < r_int || r_int < 10 ) {
                    rValueEt.setError("Input integer from 10 to 30m.");
                    rValueEt.requestFocus();
                } else if ( 5000000 < s_int || s_int < 10 ) {
                    sValueEt.setError("Input integer from 10 to 30m.");
                    sValueEt.requestFocus();
                } else {
                    Toast.makeText(this, " Please input integer from 10 to 5000km in R, S Value", Toast.LENGTH_LONG).show();
                }
            } else {
                Intent intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
