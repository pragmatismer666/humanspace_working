package com.dappcloud.humanspace.Maps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dappcloud.humanspace.Databases.User;
import com.dappcloud.humanspace.Maps.Utils.Common;
import com.dappcloud.humanspace.R;
import com.dappcloud.humanspace.User.Infrastructure.Activities.ChattingActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Button message_btn, follow_btn, like_btn, close_friend_follow, unfollow, close_friend_like, unlike;
    private TextView txtName, fullname_profile, account_type, bio_profile, city_profile, interest_profile, profession_profile, gender_profile, website_profile, username_follow, username_like;
    private ImageView imgProfile;
    private LinearLayout buttons;
    private BottomSheetDialog bottomSheetDialog;
    private String profileid;
    FirebaseUser firebaseUser;
    private CircleImageView image_profile_follow, image_profile_like;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_profile);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final String publisher = Common.UserSelected.getUserId();

        SharedPreferences.Editor editor = getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
        editor.putString("profileid", publisher);
        editor.apply();

        SharedPreferences prefs = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");

        message_btn = findViewById(R.id.message_btn);
        follow_btn = findViewById(R.id.follow_btn);
        like_btn = findViewById(R.id.like_btn);
        txtName = findViewById(R.id.txt_name);
        fullname_profile = findViewById(R.id.fullname_profile);
        account_type = findViewById(R.id.account_type);
        bio_profile = findViewById(R.id.bio_profile);
        imgProfile = findViewById(R.id.img_profile);
        city_profile = findViewById(R.id.city_profile);
        interest_profile = findViewById(R.id.interest_profile);
        profession_profile = findViewById(R.id.profession_profile);
        gender_profile = findViewById(R.id.gender_profile);
        website_profile = findViewById(R.id.website_profile);
        buttons = findViewById(R.id.buttons);

        setData();
        if (firebaseUser.getUid().equals(Common.CurrentUser.getUserId())) {
            buttons.setVisibility(View.GONE);
        }
        messageButton();
        followButton();
        likeButton();
    }

    private void setData() {

        //Here we check whether we are going to display currently loged in user profile or we are going to
        //show profile of a user which is click from the map
        if (Common.UserSelected == null) {
            txtName.setText(new StringBuilder()
                    .append(Common.CurrentUser.getFullName()));
            fullname_profile.setText(new StringBuilder()
                    .append(Common.CurrentUser.getFullName()));
            account_type.setText(new StringBuilder()
                    .append(Common.CurrentUser.getAccount()));
            bio_profile.setText(new StringBuilder()
                    .append(Common.CurrentUser.getBio()));
            city_profile.setText(new StringBuilder()
                    .append(Common.CurrentUser.getCity()));
            interest_profile.setText(new StringBuilder()
                    .append(Common.CurrentUser.getInterest()));
            profession_profile.setText(new StringBuilder()
                    .append(Common.CurrentUser.getProfession()));
            gender_profile.setText(new StringBuilder()
                    .append(Common.CurrentUser.getGender()));
            website_profile.setText(new StringBuilder()
                    .append(Common.CurrentUser.getWebsite()));

            Glide.with(this).load(Common.CurrentUser.getImageurl()).into(imgProfile);
        } else {

            txtName.setText(new StringBuilder()
                    .append(Common.UserSelected.getFullName()));
            fullname_profile.setText(new StringBuilder()
                    .append(Common.UserSelected.getFullName()));
            account_type.setText(new StringBuilder()
                    .append(Common.UserSelected.getAccount()));
            bio_profile.setText(new StringBuilder()
                    .append(Common.UserSelected.getBio()));
            city_profile.setText(new StringBuilder()
                    .append(Common.UserSelected.getCity()));
            interest_profile.setText(new StringBuilder()
                    .append(Common.UserSelected.getInterest()));
            profession_profile.setText(new StringBuilder()
                    .append(Common.UserSelected.getProfession()));
            gender_profile.setText(new StringBuilder()
                    .append(Common.UserSelected.getGender()));
            website_profile.setText(new StringBuilder()
                    .append(Common.UserSelected.getWebsite()));

            Glide.with(this).load(Common.UserSelected.getImageurl()).into(imgProfile);
        }
    }

    private void messageButton() {
        message_btn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ChattingActivity.class);
            intent.putExtra("profileid", profileid);
            getApplicationContext().startActivity(intent);
        });
    }

    private void followButton() {
        follow_btn.setOnClickListener(v -> {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow")
                    .child(firebaseUser.getUid()).child("following");

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(profileid).exists()) {
                        bottomSheetDialog = new BottomSheetDialog(getApplicationContext(), R.style.BottomSheetTheme);

                        final View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_follow_bottom_sheet,
                                (ViewGroup) v.findViewById(R.id.bottom_sheet_follow));

                        image_profile_follow = sheetView.findViewById(R.id.image_profile_follow);
                        username_follow = sheetView.findViewById(R.id.username_follow);
                        close_friend_follow = sheetView.findViewById(R.id.close_friend_follow);
                        unfollow = sheetView.findViewById(R.id.unfollow);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);

                                Glide.with(getApplicationContext()).load(user.getImageurl()).into(image_profile_follow);
                                username_follow.setText(String.format(
                                        user.getUsername()
                                ));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Friends")
                                .child(profileid).child("close");
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.child(firebaseUser.getUid()).exists()) {
                                    close_friend_follow.setText("Remove from Close Friends");

                                    sheetView.findViewById(R.id.close_friend_follow).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            FirebaseDatabase.getInstance().getReference().child("Friends").child(profileid)
                                                    .child("close").child(firebaseUser.getUid()).removeValue();

                                            close_friend_follow.setText("Add to Close Friends");
                                            Toast.makeText(getApplicationContext(), "Removed from Close Friends List", Toast.LENGTH_SHORT).show();
                                            bottomSheetDialog.dismiss();
                                        }
                                    });
                                } else {
                                    close_friend_follow.setText("Add to Close Friends");

                                    sheetView.findViewById(R.id.close_friend_follow).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            FirebaseDatabase.getInstance().getReference().child("Friends").child(profileid)
                                                    .child("close").child(firebaseUser.getUid()).setValue(true);

                                            Toast.makeText(getApplicationContext(), "Added to Close Friends List", Toast.LENGTH_SHORT).show();
                                            bottomSheetDialog.dismiss();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        unfollow.setText("Unfollow");
                        sheetView.findViewById(R.id.unfollow).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FirebaseDatabase.getInstance().getReference("Follow").child(firebaseUser.getUid())
                                        .child("following").child(profileid).removeValue();
                                FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                                        .child("followers").child(firebaseUser.getUid()).removeValue();
                                follow_btn.setText("Follow");

                                bottomSheetDialog.dismiss();
                            }
                        });

                        bottomSheetDialog.setContentView(sheetView);
                        bottomSheetDialog.show();
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                                .child("following").child(profileid).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                                .child("followers").child(firebaseUser.getUid()).setValue(true);

                        addFollowNotifications();
                        follow_btn.setText("Following");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });
    }

    private void likeButton() {
        like_btn.setOnClickListener(v -> {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Like")
                    .child(profileid).child("likes");

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(firebaseUser.getUid()).exists()) {
                        bottomSheetDialog = new BottomSheetDialog(getApplicationContext(), R.style.BottomSheetTheme);

                        final View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_like_bottom_sheet,
                                (ViewGroup) v.findViewById(R.id.bottom_sheet_like));

                        image_profile_like = sheetView.findViewById(R.id.image_profile_like);
                        username_like = sheetView.findViewById(R.id.username_like);
                        close_friend_like = sheetView.findViewById(R.id.close_friend_like);
                        unlike = sheetView.findViewById(R.id.unlike);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);

                                Glide.with(getApplicationContext()).load(user.getImageurl()).into(image_profile_like);
                                username_like.setText(String.format(
                                        user.getUsername()
                                ));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Friends")
                                .child(profileid).child("close");
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.child(firebaseUser.getUid()).exists()) {
                                    close_friend_like.setText("Remove from Close Friends");

                                    sheetView.findViewById(R.id.close_friend_like).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            FirebaseDatabase.getInstance().getReference().child("Friends").child(profileid)
                                                    .child("close").child(firebaseUser.getUid()).removeValue();

                                            close_friend_like.setText("Add to Close Friends");
                                            Toast.makeText(getApplicationContext(), "Removed from Close Friends List", Toast.LENGTH_SHORT).show();
                                            bottomSheetDialog.dismiss();
                                        }
                                    });
                                } else {
                                    close_friend_like.setText("Add to Close Friends");

                                    sheetView.findViewById(R.id.close_friend_like).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            FirebaseDatabase.getInstance().getReference().child("Friends").child(profileid)
                                                    .child("close").child(firebaseUser.getUid()).setValue(true);

                                            Toast.makeText(getApplicationContext(), "Added to Close Friends List", Toast.LENGTH_SHORT).show();
                                            bottomSheetDialog.dismiss();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        unlike.setText("Unlike");

                        sheetView.findViewById(R.id.unlike).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FirebaseDatabase.getInstance().getReference().child("Like").child(profileid)
                                        .child("likes").child(firebaseUser.getUid()).removeValue();
                                like_btn.setText("Like");

                                bottomSheetDialog.dismiss();
                            }
                        });

                        bottomSheetDialog.setContentView(sheetView);
                        bottomSheetDialog.show();
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Like").child(profileid)
                                .child("likes").child(firebaseUser.getUid()).setValue(true);

                        addLikeNotifications();
                        like_btn.setText("Liked");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });
    }

    private void addFollowNotifications() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userId", firebaseUser.getUid());
        hashMap.put("text", "started following you");

        reference.push().setValue(hashMap);
    }

    private void addLikeNotifications() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userId", firebaseUser.getUid());
        hashMap.put("text", "liked your account");

        reference.push().setValue(hashMap);
    }
}
