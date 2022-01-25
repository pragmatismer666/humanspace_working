package com.dappcloud.humanspace.User.Infrastructure.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dappcloud.humanspace.AdapterClasses.PostsAdapter.VideoPostAdapter;
import com.dappcloud.humanspace.Databases.Post;
import com.dappcloud.humanspace.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class DisplayVideoFragment extends Fragment {

    private VideoPostAdapter postAdapter;
    private List<Post> postLists;

    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;
    String profileid, postid;

    public DisplayVideoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display_video, container, false);

        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");
        postid = prefs.getString("postid", "none");



        return view;
    }


}
