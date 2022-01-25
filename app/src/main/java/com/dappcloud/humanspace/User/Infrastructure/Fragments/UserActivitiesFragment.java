package com.dappcloud.humanspace.User.Infrastructure.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivitiesFragment extends Fragment {

    ImageView back_press;
    CircleImageView image_profile;
    TextView username;
    TextView created_at;
    TextView signed_in;
    TextView provider;
    TextView email;
    FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_activities, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        back_press = view.findViewById(R.id.back_press);
        image_profile = view.findViewById(R.id.image_profile);
        username = view.findViewById(R.id.username);
        created_at = view.findViewById(R.id.created_at);
        signed_in = view.findViewById(R.id.signed_in);
        provider = view.findViewById(R.id.provider);
        email = view.findViewById(R.id.email);

        userDetails();
        back_press.setOnClickListener(v -> ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new AccountFragment()).commit());

        return view;
    }

    private void userDetails() {
        long created = firebaseUser.getMetadata().getCreationTimestamp();
        long signed = firebaseUser.getMetadata().getLastSignInTimestamp();
        Date dateCreated = new Date(created);
        Date dateSigned = new Date(signed);
        Format format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm");

        created_at.setText(format.format(dateCreated));
        signed_in.setText(format.format(dateSigned));

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null) {
                    return;
                }

                User user = snapshot.getValue(User.class);
                try {
                    Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                username.setText(String.format(
                        user.getUsername()
                ));
                email.setText(String.format(
                        user.getEmail()
                ));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}
