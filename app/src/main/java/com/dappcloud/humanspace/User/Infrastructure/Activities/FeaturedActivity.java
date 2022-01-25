package com.dappcloud.humanspace.User.Infrastructure.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dappcloud.humanspace.AdapterClasses.AppEntryAdapter.FeaturedAdapter;
import com.dappcloud.humanspace.AdapterClasses.AppEntryAdapter.VerifiedAccountsAdapter;
import com.dappcloud.humanspace.AdapterClasses.UserAdapter.UserAdapter;
import com.dappcloud.humanspace.Databases.User;
import com.dappcloud.humanspace.User.SigninSignup.UsersStartUpScreen;
import com.dappcloud.humanspace.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FeaturedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;
    private FirebaseUser firebaseUser;

    private EditText search_bar;
    private ImageView call_dashboard;
    private TextView dashboard;

    private ShimmerFrameLayout shimmer;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        search_bar = findViewById(R.id.search_bar_edt);
        call_dashboard = findViewById(R.id.call_dashboard);
        dashboard = findViewById(R.id.dashboard);
        shimmer = findViewById(R.id.shimmer);
        recyclerView = findViewById(R.id.user_search_recycler);

        handler.postDelayed(() -> {
            shimmer.stopShimmer();
            shimmer.hideShimmer();
            shimmer.setVisibility(View.GONE);

            recyclerView.setVisibility(View.VISIBLE);
        }, 2000);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(FeaturedActivity.this));
        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(FeaturedActivity.this, mUsers, true);
        recyclerView.setAdapter(userAdapter);
        search_bar.requestFocus();

        call_dashboard.setOnClickListener(v -> callDashboard());
        dashboard.setOnClickListener(v -> callDashboard());
        readUsers();
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

    private void callDashboard() {
        Intent intent = new Intent(this, UserDashboardActivity.class);
        startActivity(intent);
    }

    private void searchUsers(final String s) {
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    assert firebaseUser != null;
                    if (!user.getUserId().equals(firebaseUser.getUid())) {
                        mUsers.add(user);
                    }
                }

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (search_bar.getText().toString().equals("")) {
                    mUsers.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        assert user != null;
                        assert firebaseUser != null;
                        if (!user.getUserId().equals(firebaseUser.getUid())) {
                            if (user.getIsFeature().equals("Yes")) {
                                mUsers.add(user);
                            }
                        }
                    }
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}

