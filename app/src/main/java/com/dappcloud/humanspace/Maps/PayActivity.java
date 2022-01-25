package com.dappcloud.humanspace.Maps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.dappcloud.humanspace.Databases.User;
import com.dappcloud.humanspace.Maps.Utils.Common;
import com.dappcloud.humanspace.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class PayActivity extends AppCompatActivity {

    CheckBox gayCheck,goldCheck;
    Button updateBtn,backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        gayCheck = findViewById(R.id.checkBox);
        goldCheck = findViewById(R.id.checkBox2);
        gayCheck.setVisibility(View.GONE);
        goldCheck.setVisibility(View.GONE);
        updateBtn = findViewById(R.id.button);
        backBtn = findViewById(R.id.button2);

        String upgradeKey = getIntent().getStringExtra("upgrade");
        if ( upgradeKey.contains(getString(R.string.gays))){
            gayCheck.setVisibility(View.VISIBLE);
        }
        if ( upgradeKey.contains(getString(R.string.gold))){
            goldCheck.setVisibility(View.VISIBLE);
        }

//        if (Common.CurrentUser.getCategory().equals(getString(R.string.gold))){
//            goldCheck.setChecked(true);
//        }
//        else if (Common.CurrentUser.getCategory().equals(getString(R.string.gays))){
//            gayCheck.setChecked(true);
//        }
        backBtn.setOnClickListener(v->{
            startActivity(new Intent(this, UpgradeActivity.class));
            finish();
        });
        updateBtn.setOnClickListener(v->{
            if ( gayCheck.isChecked() || goldCheck.isChecked() ) {
                if ( gayCheck.isChecked() ){
                    Common.CurrentUser.setCategory(getString(R.string.gays));
                }
                if ( goldCheck.isChecked() ) {
                    Common.CurrentUser.setCategory(getString(R.string.gold));
                }
                if (Common.CurrentUser!=null){
                    HashMap<String, Object> update = new HashMap<>();
                    update.put("category", Common.CurrentUser.getCategory());
                    FirebaseDatabase.getInstance().getReference(Common.USER_REF)
                            .child(Common.CurrentUser.getUserId())
                            .updateChildren(update);
                }
                for (User user : Common.UsersList) {
                    if (user.getUsername().equals(Common.CurrentUser.getUsername())){
                        user.setCategory(Common.CurrentUser.getCategory());
                        break;
                    }
                }
            }
            else {
                Toast.makeText(this, "You keep your personal account category.",Toast.LENGTH_LONG).show();
            }
            startActivity(new Intent(this, MapsActivity.class));
            finish();
        });

    }
}
