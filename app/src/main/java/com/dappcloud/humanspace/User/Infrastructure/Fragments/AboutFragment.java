package com.dappcloud.humanspace.User.Infrastructure.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dappcloud.humanspace.R;

public class AboutFragment extends Fragment {

    private TextView show_data_policy;
    private TextView data_policy;
    private TextView show_terms_of_use;
    private TextView terms_of_use;
    private TextView show_open_source_libraries;
    private TextView open_source_libraries;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        show_data_policy = view.findViewById(R.id.show_data_policy);
        data_policy = view.findViewById(R.id.data_policy);
        show_terms_of_use = view.findViewById(R.id.show_terms_of_use);
        terms_of_use = view.findViewById(R.id.terms_of_use);
        show_open_source_libraries = view.findViewById(R.id.show_open_source_libraries);
        open_source_libraries = view.findViewById(R.id.open_source_libraries);

        dataPolicy();
        termsOfUse();
        openSource();

        return view;
    }

    private void dataPolicy() {

        show_data_policy.setOnClickListener(v -> Toast.makeText(getContext(), "Data Policy", Toast.LENGTH_SHORT).show());
    }

    private void termsOfUse() {
        show_terms_of_use.setOnClickListener(v -> Toast.makeText(getContext(), "Terms of Use", Toast.LENGTH_SHORT).show());
    }

    private void openSource() {
        show_open_source_libraries.setOnClickListener(v -> Toast.makeText(getContext(), "Open Source Libraries", Toast.LENGTH_SHORT).show());
    }

}
