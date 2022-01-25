package com.dappcloud.humanspace.User.Infrastructure.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.dappcloud.humanspace.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SecurityFragment extends Fragment {

    ImageView back_press;
    Button password;
    Button activity;
    Button emails;

    FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_security, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        back_press = view.findViewById(R.id.back_press);
        password = view.findViewById(R.id.password);
        activity = view.findViewById(R.id.activity);
        emails = view.findViewById(R.id.emails);

        back_press.setOnClickListener(v -> ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new ProfileFragment()).commit());
        password.setOnClickListener(v -> Toast.makeText(getContext(), "Password", Toast.LENGTH_SHORT).show());
        activity.setOnClickListener(v -> ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new LoginActivityFragment()).commit());
        emails.setOnClickListener(v -> Toast.makeText(getContext(), "Emails", Toast.LENGTH_SHORT).show());

        return view;
    }

}
