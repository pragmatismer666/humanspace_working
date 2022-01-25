package com.dappcloud.humanspace.User.Infrastructure.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dappcloud.humanspace.AdapterClasses.UserAdapter.UserAdapter;
import com.dappcloud.humanspace.Databases.User;
import com.dappcloud.humanspace.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowFollowersActivity extends AppCompatActivity {

    private String id, title;
    private EditText search_bar;
    private TextView username, count, text;
    private ImageView verified;
    FirebaseUser firebaseUser;

    List<String> idList;
    RecyclerView recyclerView;
    UserAdapter userAdapter;
    List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_followers);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");

        verified = findViewById(R.id.verified);
        search_bar = findViewById(R.id.search_bar_edt);
        username = findViewById(R.id.username);
        count = findViewById(R.id.count);
        text = findViewById(R.id.text);

        text.setText(title);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList, false);
        recyclerView.setAdapter(userAdapter);

        idList = new ArrayList<>();

        switch (title) {
            case "Friends":
                getFriends();
                break;
            case "Following":
                getFollowing();
                break;
            case "Followers":
                getFollowers();
                break;
            case "Views":
                getStoryViews();
                break;
        }

    }

    private void getStoryViews() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(id)
                .child(getIntent().getStringExtra("storyId")).child("views");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                getUsername();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    idList.add(dataSnapshot.getKey());
                }
                showUsers();
                search_bar.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        searchUsers(s.toString().toLowerCase());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFriends() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Friends")
                .child(id).child("close");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                getUsername();
                count.setText("" + snapshot.getChildrenCount());
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    idList.add(dataSnapshot.getKey());
                }
                showUsers();
                search_bar.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        searchUsers(s.toString().toLowerCase());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowing() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(id).child("following");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                getUsername();
                count.setText("" + snapshot.getChildrenCount());
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    idList.add(dataSnapshot.getKey());
                }
                showUsers();
                search_bar.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        searchUsers(s.toString().toLowerCase());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(id).child("followers");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                getUsername();
                count.setText("" + snapshot.getChildrenCount());
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    idList.add(dataSnapshot.getKey());
                }
                showUsers();
                search_bar.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        searchUsers(s.toString().toLowerCase());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    for (String id : idList) {
                        if (user.getUserId().equals(id)) {
                            userList.add(user);
                        }
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUsername() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                username.setText(String.format(
                        user.getUsername()
                ));
                if (user.getIsVerified().equals("Yes")) {
                    verified.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchUsers(final String s) {
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    for (String id : idList) {
                        if (user.getUserId().equals(id)) {
                            userList.add(user);
                        }
                    }
                }

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void status(String status) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("Offline");
    }
}
