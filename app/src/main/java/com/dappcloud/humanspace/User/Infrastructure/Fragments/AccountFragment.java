package com.dappcloud.humanspace.User.Infrastructure.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dappcloud.humanspace.Databases.User;
import com.dappcloud.humanspace.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountFragment extends Fragment {

    private Button notifications;
    private Button activity;
    private Button saved_post;
    private Button switch_to_business;
    private Button request_verification;
    private FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        notifications = view.findViewById(R.id.notifications);
        activity = view.findViewById(R.id.activity);
        switch_to_business = view.findViewById(R.id.switch_to_business);
        request_verification = view.findViewById(R.id.request_verification);

        //isPersonal();
        notifications();
        activities();
        requests();
        switchToBusiness();




        return view;
    }

    private void isPersonal() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null) {
                    return;
                }
                User user = snapshot.getValue(User.class);

                if (user.getAccount().equals("Personal")) {
                    switch_to_business.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void notifications() {
        notifications.setOnClickListener(v -> ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new NotificationFragment()).addToBackStack(null).commit());
    }

    private void activities(){
        activity.setOnClickListener(v -> ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new UserActivitiesFragment()).addToBackStack(null).commit());
    }

    private void requests(){
        request_verification.setOnClickListener(v -> ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new VerificationRequestFragment()).addToBackStack(null).commit());
    }

    private void switchToBusiness(){
        switch_to_business.setOnClickListener(v -> ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new BusinessRequestFragment()).addToBackStack(null).commit());
    }
}
