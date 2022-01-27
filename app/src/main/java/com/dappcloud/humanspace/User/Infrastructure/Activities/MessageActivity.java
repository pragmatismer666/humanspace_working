package com.dappcloud.humanspace.User.Infrastructure.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.dappcloud.humanspace.AdapterClasses.ChatModulesAdapter;
import com.dappcloud.humanspace.Maps.Utils.Utils;
import com.dappcloud.humanspace.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
/**
 * Messages and Chats
 */
public class MessageActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    TabLayout tab_layout;
    ViewPager2 view_pager2;
    ChatModulesAdapter chatModulesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        tab_layout = findViewById(R.id.tab_layout);
        view_pager2 = findViewById(R.id.view_pager2);

        FragmentManager fm = getSupportFragmentManager();
        chatModulesAdapter = new ChatModulesAdapter(fm, getLifecycle());
        view_pager2.setAdapter(chatModulesAdapter);

        tab_layout.addTab(tab_layout.newTab().setText("Chats"));
        tab_layout.addTab(tab_layout.newTab().setText("Requests"));
        tab_layout.addTab(tab_layout.newTab().setText("Online"));
        tab_layout.addTab(tab_layout.newTab().setText("Users"));

        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                view_pager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        view_pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tab_layout.selectTab(tab_layout.getTabAt(position));
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void status(String status) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("Offline");
    }
}
