package com.dappcloud.humanspace.User.Infrastructure.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dappcloud.humanspace.Databases.Story;
import com.dappcloud.humanspace.Databases.User;
import com.dappcloud.humanspace.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;

public class DisplayStoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    int counter = 0;
    long pressTime = 0L;
    long limit = 500L;

    StoriesProgressView storiesProgressView;
    ImageView image;
    CircleImageView story_photo;
    TextView story_username;

    LinearLayout r_seen;
    TextView seen_number, caption;
    ImageView add_story;
    ImageView story_delete;

    List<String> images;
    List<String> storyids;
    String userid;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_story);

        storiesProgressView = findViewById(R.id.stories);
        image = findViewById(R.id.image);
        story_photo = findViewById(R.id.story_photo);
        story_username = findViewById(R.id.story_username);

        r_seen = findViewById(R.id.r_seen);
        seen_number = findViewById(R.id.seen_number);
        caption = findViewById(R.id.caption);
        add_story = findViewById(R.id.add_story);
        story_delete = findViewById(R.id.story_delete);

        r_seen.setVisibility(View.GONE);
        story_delete.setVisibility(View.GONE);

        userid = getIntent().getStringExtra("userId");

        if (userid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            r_seen.setVisibility(View.VISIBLE);
            story_delete.setVisibility(View.VISIBLE);
        }

        getStories(userid);
        userInfo(userid);

        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(v -> storiesProgressView.reverse());
        reverse.setOnTouchListener(onTouchListener);

        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(v -> storiesProgressView.skip());
        skip.setOnTouchListener(onTouchListener);

        r_seen.setOnClickListener(v -> {
            Intent intent = new Intent(DisplayStoryActivity.this, ShowFollowersActivity.class);
            intent.putExtra("id", userid);
            intent.putExtra("storyId", storyids.get(counter));
            intent.putExtra("title", "Views");
            startActivity(intent);
        });

        add_story.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AddStoryActivity.class)));

        story_delete.setOnClickListener(v -> {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(userid)
                    .child(storyids.get(counter));
            reference.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(DisplayStoryActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    @Override
    public void onNext() {
        try {
            Glide.with(getApplicationContext()).load(images.get(++counter)).into(image);
        } catch (Exception e) {
            e.printStackTrace();
        }

        addView(storyids.get(counter));
        seenNumber(storyids.get(counter));
    }

    @Override
    public void onPrev() {
        if (counter - 1 < 0) return;
        try {
            Glide.with(getApplicationContext()).load(images.get(--counter)).into(image);
        } catch (Exception e) {
            e.printStackTrace();
        }

        seenNumber(storyids.get(counter));
    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        storiesProgressView.resume();
        super.onResume();
    }

    private void getStories(String userid) {
        images = new ArrayList<>();
        storyids = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                images.clear();
                storyids.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Story story = dataSnapshot.getValue(Story.class);
                    long timecurrent = System.currentTimeMillis();
                    if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
                        images.add(story.getImageurl());
                        storyids.add(story.getStoryId());
                    }
                }

                storiesProgressView.setStoriesCount(images.size());
                storiesProgressView.setStoryDuration(5000L);
                storiesProgressView.setStoriesListener(DisplayStoryActivity.this);
                storiesProgressView.startStories(counter);
                try {
                    Glide.with(getApplicationContext()).load(images.get(counter)).into(image);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                addView(storyids.get(counter));
                seenNumber(storyids.get(counter));
                storyCaption(storyids.get(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userInfo(String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                try {
                    Glide.with(getApplicationContext()).load(user.getImageurl()).into(story_photo);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                story_username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void storyCaption(String storyid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(userid)
                .child(storyid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Story story = snapshot.getValue(Story.class);
                caption.setText(story.getCaption());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addView(String storyid) {
        FirebaseDatabase.getInstance().getReference("Story").child(userid).child(storyid)
                .child("views").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
    }

    private void seenNumber(String storyid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(userid)
                .child(storyid).child("views");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                seen_number.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
