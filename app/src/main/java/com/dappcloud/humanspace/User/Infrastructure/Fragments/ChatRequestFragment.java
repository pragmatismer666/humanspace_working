package com.dappcloud.humanspace.User.Infrastructure.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

import com.dappcloud.humanspace.AdapterClasses.UserAdapter.ContactAdapter;
import com.dappcloud.humanspace.Databases.Chat;
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
import java.util.Collections;
import java.util.List;
/**
 * All first time chats
 */
public class ChatRequestFragment extends Fragment {

    private LinearLayout new_user;
    private TextView first_timer;

    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private List<User> mUsers;
    private FirebaseUser firebaseUser;

    private List<String> usersList;

    EditText search_bar;
    ImageView back_press, more_options, open_search;
    RelativeLayout search, topBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_request, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        back_press = view.findViewById(R.id.back_press);
        more_options = view.findViewById(R.id.more_options);
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

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        mUsers = new ArrayList<>();

        editSearchBar();
        getUsers();

        return view;
    }

    private void getUsers() {
        usersList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getIsSeen().equals("No")) {
                        usersList.add(chat.getSender());
                    }
                }

                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readChats() {
        mUsers = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);

                    for (String id : usersList) {
                        if (user.getUserId().equals(id)) {
                            if (mUsers.size() != 0) {
                                for (User user1 : mUsers) {
                                    if (!user.getUserId().equals(user1.getUserId())) {
                                        mUsers.add(user);
                                        new_user.setVisibility(View.GONE);
                                    }
                                }
                            } else {
                                mUsers.add(user);
                                new_user.setVisibility(View.GONE);
                            }
                        }
                    }
                }
                Collections.reverse(mUsers);
                contactAdapter = new ContactAdapter(getContext(), mUsers, true);
                recyclerView.setAdapter(contactAdapter);
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
                    assert user != null;
                    assert firebaseUser != null;
                    if (!user.getUserId().equals(firebaseUser.getUid())) {
                        mUsers.add(user);
                    }
                }

                contactAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void editSearchBar(){
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

}
