package com.dappcloud.humanspace.User.Infrastructure.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dappcloud.humanspace.AdapterClasses.PostAdapter;
import com.dappcloud.humanspace.Databases.Post;
import com.dappcloud.humanspace.Databases.User;
import com.dappcloud.humanspace.R;
import com.dappcloud.humanspace.User.Infrastructure.Activities.CommentsActivity;
import com.dappcloud.humanspace.User.Infrastructure.Activities.UserDashboardActivity;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class DisplayPostFragment extends Fragment {

    private ImageView verified, mpost, like, dislike, comment;
    private CircleImageView image_profile;
    private TextView name, username, likes_count, dislikes_count, comments_count, publisher, caption, time;


    private RecyclerView recycler_view_post;
    private PostAdapter postAdapter;
    private List<Post> postLists;

    private List<String> followingList;

    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;
    String profileid, postid;

    //Image Post Display Fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display_post, container, false);

        name = view.findViewById(R.id.name);
        verified = view.findViewById(R.id.verified);
        mpost = view.findViewById(R.id.post);
        like = view.findViewById(R.id.like);
        dislike = view.findViewById(R.id.dislike);
        comment = view.findViewById(R.id.comment);
        image_profile = view.findViewById(R.id.image_profile);
        username = view.findViewById(R.id.username);
        likes_count = view.findViewById(R.id.likes_count);
        dislikes_count = view.findViewById(R.id.dislikes_count);
        comments_count = view.findViewById(R.id.comments_count);
        publisher = view.findViewById(R.id.publisher);
        caption = view.findViewById(R.id.caption);
        time = view.findViewById(R.id.time);

        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");
        postid = prefs.getString("postid", "none");

        recycler_view_post = view.findViewById(R.id.recycler_view_post);
        recycler_view_post.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recycler_view_post.setLayoutManager(linearLayoutManager);
        postLists = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postLists);
        recycler_view_post.setAdapter(postAdapter);

        getSelectedUserName();
        readSelectedPost();
        readOtherPost();

        return view;
    }

    private void getSelectedUserName() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                name.setText(user.getFullName());
                if (user.getIsVerified().equals("Yes")) {
                    verified.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readSelectedPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null) {
                    return;
                }
                Post post = snapshot.getValue(Post.class);
                Glide.with(getContext()).load(post.getPostUrl()).into(mpost);
                long timeCurrent = System.currentTimeMillis();
                long createdAt = post.getCreatedAt();
                long lapse = timeCurrent - createdAt;

                long minute = 60000;
                long hour = 3600000;
                long day = 86400000;
                long twoDays = 172800000;
                long week = 604800000;
                long month = (long) 2.6280036e+12;
                long year = (long) 3.1536036e+12;

                if (lapse < minute) {
                    time.setText(" - a few seconds ago"); // less than 1min
                } else if (lapse >= minute && lapse < hour) {
                    time.setText(" - a few minutes ago"); // less than 1hour
                } else if (lapse >= hour && lapse < day) {
                    time.setText(" - a few hours ago"); // less than 1day
                } else if (lapse >= day && lapse < twoDays) {
                    time.setText(" - a day ago"); // less than 48hours
                } else if (lapse >= twoDays && lapse < week) {
                    time.setText(" - a few days ago"); // less than 1week
                } else if (lapse >= week && lapse < month) {
                    time.setText(" - a few weeks ago"); // less than 1month
                } else if (lapse == month) {
                    time.setText(" - a month ago"); // 1month
                } else if (lapse > month && lapse < year) {
                    time.setText(" - a few months ago"); //more than 1month
                } else {
                    time.setText(" - more than a year"); //more than 1year
                }

                if (post.getCaption().equals("")) {
                    caption.setVisibility(View.GONE);
                } else {
                    caption.setVisibility(View.VISIBLE);
                    caption.setText(post.getCaption());
                }

                publisherInfo(image_profile, username, publisher, post.getPublisher());
                isLiked(post.getPostId(), like);
                likesCount(likes_count, post.getPostId());
                isDisliked(post.getPostId(), dislike);
                disLikesCount(dislikes_count,post.getPostId());
                getComments(post.getPostId(), comments_count);

                like.setOnClickListener(v -> {
                    if (like.getTag().equals("like")) {
                        FirebaseDatabase.getInstance().getReference().child("PostLikes").child(post.getPostId())
                                .child(firebaseUser.getUid()).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("PostDislikes").child(post.getPostId())
                                .child(firebaseUser.getUid()).removeValue();
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("PostLikes").child(post.getPostId())
                                .child(firebaseUser.getUid()).removeValue();
                    }
                });
                dislike.setOnClickListener(v -> {
                    if (dislike.getTag().equals("dislike")) {
                        FirebaseDatabase.getInstance().getReference().child("PostDislikes").child(post.getPostId())
                                .child(firebaseUser.getUid()).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("PostLikes").child(post.getPostId())
                                .child(firebaseUser.getUid()).removeValue();
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("PostDislikes").child(post.getPostId())
                                .child(firebaseUser.getUid()).removeValue();
                    }
                });
                comment.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), CommentsActivity.class);
                    intent.putExtra("postId", post.getPostId());
                    intent.putExtra("publisher", post.getPublisher());
                    getContext().startActivity(intent);
                });
                comments_count.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), CommentsActivity.class);
                    intent.putExtra("postId", post.getPostId());
                    intent.putExtra("publisher", post.getPublisher());
                    getContext().startActivity(intent);
                });

                image_profile.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), UserDashboardActivity.class);
                    intent.putExtra("publisher", post.getPublisher());
                    getContext().startActivity(intent);
                });
                username.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), UserDashboardActivity.class);
                    intent.putExtra("publisher", post.getPublisher());
                    getContext().startActivity(intent);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readOtherPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postLists.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    assert post != null;
                    assert firebaseUser != null;
                    if (!post.getPublisher().equals(firebaseUser.getUid())) {
                        if (!post.getPostId().equals(postid)) {
                            if (!post.getType().equals("Video") && !post.getType().equals("Text")) {
                                postLists.add(post);
                            }
                        }
                    }
                }
                Collections.shuffle(postLists);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isLiked(String postid, ImageView imageView) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("PostLikes")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_favorite);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_favorite_border);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void likesCount(TextView likes, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("PostLikes")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount() + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isDisliked(String postid, ImageView imageView) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("PostDislikes")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_thumb_down_cliked);
                    imageView.setTag("disliked");
                } else {
                    imageView.setImageResource(R.drawable.ic_thumb_down);
                    imageView.setTag("dislike");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void disLikesCount(TextView dislikes, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("PostDislikes")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dislikes.setText(snapshot.getChildrenCount() + " dislikes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getComments(String postid, TextView comments) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PostComments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    comments.setText("View All " + snapshot.getChildrenCount() + " comments");
                } else {
                    comments.setText("Comment");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void publisherInfo(CircleImageView image_profile, TextView username, TextView publisher, String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                try {
                    Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                username.setText(user.getUsername());
                publisher.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
