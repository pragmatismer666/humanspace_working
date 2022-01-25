package com.dappcloud.humanspace.User.Infrastructure.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dappcloud.humanspace.AdapterClasses.UserAdapter.OnlineUsersAdapter;
import com.dappcloud.humanspace.Databases.User;
import com.dappcloud.humanspace.R;
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

/**
 * Messages - All online users you are following
 */
public class OnlineUsersFragment extends Fragment {

    private LinearLayout new_user;
    private TextView first_timer;

    private RecyclerView online_users_recycler;
    private OnlineUsersAdapter onlineUsersAdapter;
    private List<User> mUsers;

    private FirebaseUser firebaseUser;
    private EditText search_bar;
    private ImageView open_search;
    private RelativeLayout search, topBar;

    private List<String> followingList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_online_users, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        search_bar = view.findViewById(R.id.search_bar_edt);
        search = view.findViewById(R.id.search);
        open_search = view.findViewById(R.id.open_search);
        topBar = view.findViewById(R.id.topBar);
        new_user = view.findViewById(R.id.new_user);
        first_timer = view.findViewById(R.id.first_timer);

        open_search.setOnClickListener(v -> {
            search.setVisibility(View.GONE);
            topBar.setVisibility(View.VISIBLE);
            search_bar.requestFocus();
        });
        first_timer.setOnClickListener(v -> ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.chat_container,
                new SearchFragment()).addToBackStack(null).commit());

        online_users_recycler = view.findViewById(R.id.online_users_recycler);
        online_users_recycler.setHasFixedSize(true);
        online_users_recycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mUsers = new ArrayList<>();

        //checkFollowing();
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

        return view;
    }

    private void checkFollowing() {
        followingList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(firebaseUser.getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    followingList.add(dataSnapshot.getKey());
                }

                readOnlineUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readOnlineUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);

                    for (String id : followingList) {
                        if (user.getUserId().equals(id)) {
                            if (user.getStatus().equals("Online")) {
                                mUsers.add(user);
                                new_user.setVisibility(View.GONE);
                            }
                        }
                    }

                }

                onlineUsersAdapter = new OnlineUsersAdapter(getContext(), mUsers, false);
                online_users_recycler.setAdapter(onlineUsersAdapter);
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
                mUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);

                    for (String id : followingList) {
                        if (user.getUserId().equals(id)) {
                            mUsers.add(user);
                        }
                    }
                }

                onlineUsersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
