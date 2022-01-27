package com.dappcloud.humanspace.User.Infrastructure.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dappcloud.humanspace.AdapterClasses.CommentAdapter;
import com.dappcloud.humanspace.Databases.PostComment;
import com.dappcloud.humanspace.Databases.User;
import com.dappcloud.humanspace.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity {

    ImageView back_press, message;
    CircleImageView image_profile;
    EditText add_comment;
    TextView post;

    String postId;
    String publisher;
    FirebaseUser firebaseUser;

    private RecyclerView recycler_view;
    private CommentAdapter commentAdapter;
    private List<PostComment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        back_press = findViewById(R.id.back_press);
        message = findViewById(R.id.message);
        image_profile = findViewById(R.id.image_profile);
        add_comment = findViewById(R.id.add_comment);
        post = findViewById(R.id.post);

        recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(linearLayoutManager);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList);
        recycler_view.setAdapter(commentAdapter);

        back_press.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        publisher = intent.getStringExtra("publisher");

        post.setOnClickListener(v -> {
            if (!add_comment.getText().toString().equals("")) {
                addComment();
            }
        });
        userDetails();
        readComments();
    }

    private void addComment() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PostComments").child(postId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", add_comment.getText().toString().trim());
        hashMap.put("publisher", firebaseUser.getUid());

        reference.push().setValue(hashMap);
        add_comment.setText("");
    }

    private void userDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                try {
                    Glide.with(getApplicationContext()).load(user.getImageurl()).into(image_profile);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readComments() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PostComments").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PostComment comment = dataSnapshot.getValue(PostComment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
