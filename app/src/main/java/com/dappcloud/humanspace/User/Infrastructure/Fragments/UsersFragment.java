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
import android.widget.Toast;

import com.dappcloud.humanspace.AdapterClasses.UserAdapter.ContactAdapter;
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
 * Messages - All users you are following
 */
public class UsersFragment extends Fragment {

    private LinearLayout new_user;
    private TextView first_timer;

    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private List<User> mUsers;
    private FirebaseUser firebaseUser;

    private List<String> followingList;

    private EditText search_bar;
    private ImageView open_search;
    private RelativeLayout search, topBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);

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

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUsers = new ArrayList<>();

        checkFollowing();
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

                readUsers();
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

                        for (String id : followingList) {
                            if (user.getUserId().equals(id)) {
                                mUsers.add(user);
                                new_user.setVisibility(View.GONE);
                            }
                        }

                    }

                    contactAdapter = new ContactAdapter(getContext(), mUsers, false);
                    recyclerView.setAdapter(contactAdapter);
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
                mUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);

                    for (String id : followingList) {
                        if (user.getUserId().equals(id)) {
                            mUsers.add(user);
                        }
                    }
                }

                contactAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
