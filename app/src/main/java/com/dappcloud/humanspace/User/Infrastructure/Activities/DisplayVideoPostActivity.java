package com.dappcloud.humanspace.User.Infrastructure.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.dappcloud.humanspace.AdapterClasses.VideoPostAdapter;
import com.dappcloud.humanspace.Databases.Post;
import com.dappcloud.humanspace.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DisplayVideoPostActivity extends AppCompatActivity {

    private ViewPager2 viewPagerVideos;
    private VideoPostAdapter videoAdapter;
    private List<Post> postList;

    private FirebaseUser firebaseUser;
    String profileid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_video_post);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        viewPagerVideos = findViewById(R.id.viewPagerVideos);
        postList = new ArrayList<>();

        SharedPreferences prefs = getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");

        videoAdapter = new VideoPostAdapter(getApplicationContext(),postList);
        viewPagerVideos.setAdapter(videoAdapter);

        readVideoPosts();
    }

    private void readVideoPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (!post.getType().equals("Image") && !post.getType().equals("Text")) {
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                videoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
