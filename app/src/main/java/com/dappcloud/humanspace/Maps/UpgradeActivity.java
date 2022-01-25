package com.dappcloud.humanspace.Maps;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dappcloud.humanspace.Maps.Utils.Common;
import com.dappcloud.humanspace.R;

public class UpgradeActivity extends AppCompatActivity {

    Button upgradeBtn, cancelBtn;
    TextView accountStatus,gaysTv,goldTv;

    String upgradeWord = "";
    Boolean gaysUpgradeFlag = false;
    Boolean goldUpgradeFlag = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"CutPasteId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);

        gaysTv = findViewById(R.id.gaysUpgrade);
        gaysTv.setOnClickListener(v->{
            if (!Common.CurrentUser.getCategory().equals(getString(R.string.gays))){
                if (!gaysUpgradeFlag) {
                    upgradeWord = upgradeWord + getString(R.string.gays);
                    gaysTv.setTextColor(getColor(R.color.upgraded));
                    gaysUpgradeFlag = true;
                }
                else {
                    upgradeWord = upgradeWord.replace(getString(R.string.gays),"");
                    gaysTv.setTextColor(getColor(R.color.white));
                    gaysUpgradeFlag = false;
                }
            }
        });

        goldTv = findViewById(R.id.goldUpgrade);
        goldTv.setOnClickListener(v->{
            if (!Common.CurrentUser.getCategory().equals(getString(R.string.gold))){
                if (!goldUpgradeFlag) {
                    upgradeWord = upgradeWord + getString(R.string.gold);
                    goldTv.setTextColor(getColor(R.color.upgraded));
                    goldUpgradeFlag = true;
                }
                else {
                    upgradeWord = upgradeWord.replace(getString(R.string.gold),"");
                    goldTv.setTextColor(getColor(R.color.white));
                    goldUpgradeFlag = false;
                }
            }
        });
        accountStatus = findViewById(R.id.description);
        if (Common.CurrentUser.getCategory().equals(getString(R.string.gays))){
            gaysTv.setTextColor(getColor(R.color.upgraded));
            gaysTv.setClickable(false);
            accountStatus.setText(accountStatus.getText().toString() + "\n" + getString(R.string.gaysUpgraded));
        }
        if ( Common.CurrentUser.getCategory().equals(getString(R.string.gold)) ) {
            goldTv.setTextColor(getColor(R.color.upgraded));
            goldTv.setClickable(false);
            accountStatus.setText(accountStatus.getText().toString() + "\n" + getString(R.string.goldUpgraded));
        }
        accountStatus.setTextColor(getColor(R.color.white));

        upgradeBtn = findViewById(R.id.upgradeBtn);
        upgradeBtn.setOnClickListener(v->{
            if ( upgradeWord.equals("") ) {
                Toast.makeText(this, "Please select upgrade selection at first.", Toast.LENGTH_LONG).show();
            }
            else {
                Intent upgradeIntent = new Intent(this, PayActivity.class);
                upgradeIntent.putExtra("upgrade",upgradeWord);
                startActivity(upgradeIntent);
                finish();
            }
        });
        cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(v->{
            startActivity(new Intent(this, MapsActivity.class));
            finish();
        });
    }
}
