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

import com.dappcloud.humanspace.AdapterClasses.UserAdapter.ContactAdapter;
import com.dappcloud.humanspace.Databases.ChatNotifications.Token;
import com.dappcloud.humanspace.Databases.Chat;
import com.dappcloud.humanspace.Databases.ChatList;
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
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Shows Chat lists
 */
public class ChatsFragment extends Fragment {

    private LinearLayout new_user;
    private TextView first_timer;

    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private List<User> mUsers;

    private FirebaseUser firebaseUser;
    private List<ChatList> usersList;

    private ImageView show_users, open_search;
    private EditText search_bar;
    private TextView chats_counter;
    private RelativeLayout search, topBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        show_users = view.findViewById(R.id.show_users);
        search_bar = view.findViewById(R.id.search_bar_edt);
        chats_counter = view.findViewById(R.id.chats_counter);
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
        show_users.setOnClickListener(v -> openUsersFragment());
        first_timer.setOnClickListener(v -> openUsersFragment());

        recyclerView = view.findViewById(R.id.chats_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        mUsers = new ArrayList<>();

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
        userChats();
        unreadChats();
        updateToken(FirebaseInstanceId.getInstance().getToken());

        return view;
    }

    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);
    }

    private void userChats() {
        usersList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatList chatList = dataSnapshot.getValue(ChatList.class);
                    usersList.add(chatList);
                }

                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void chatList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    for (ChatList chatList : usersList) {
                        if (user.getUserId().equals(chatList.getId())) {
                            mUsers.add(user);
                            new_user.setVisibility(View.GONE);
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

    private void unreadChats() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int unread = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getIsSeen().equals("No")) {
                        unread++;
                    }
                }
                if (unread > 0) {
                    chats_counter.setVisibility(View.VISIBLE);
                    chats_counter.setText("Requests (" + unread + ")");
                } else {
                    chats_counter.setVisibility(View.GONE);
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
                    for (ChatList chatList : usersList) {
                        if (user.getUserId().equals(chatList.getId())) {
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

    private void openUsersFragment() {
        ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.chat_container,
                new UsersFragment()).addToBackStack(null).commit();
    }

}
