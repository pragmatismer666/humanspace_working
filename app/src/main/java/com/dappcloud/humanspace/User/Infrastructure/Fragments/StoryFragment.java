package com.dappcloud.humanspace.User.Infrastructure.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dappcloud.humanspace.AdapterClasses.GridImageAdapter;
import com.dappcloud.humanspace.AdapterClasses.StoryAllAdapter;
import com.dappcloud.humanspace.AdapterClasses.StoryCloseFreindsAdapter;
import com.dappcloud.humanspace.Databases.Post;
import com.dappcloud.humanspace.Databases.Story;
import com.dappcloud.humanspace.R;
import com.facebook.shimmer.ShimmerFrameLayout;
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

/**
 * Status / Voices Interactions
 */
public class StoryFragment extends Fragment {

    private Button search_btn;
    private ImageView search_icon;
    private TextView friends_txt, discover_txt, status, audios;
    private View status_line, audios_line;
    private LinearLayout new_user;

    private FirebaseUser firebaseUser;

    private RecyclerView recyclerView_story, recycler_view_storyCloseFriends, recycler_view_audios; //Status and Voices
    private StoryAllAdapter storyAdapter; //Status
    private StoryCloseFreindsAdapter storyCloseFriendAdapter; //Status
    private List<Story> storyList; //Status
    private List<Story> storyCloseFriendList; //Status

    private RecyclerView recycler_view_images; //New Posts
    private GridImageAdapter imagePostAdapter; //New Posts
    private List<Post> postList; //New Posts

    private List<String> followingList; //Status
    private List<String> closeFriendsList; //Status

    private RelativeLayout topBar;
    private LinearLayout header;
    private ShimmerFrameLayout shimmer;
    Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        search_btn = view.findViewById(R.id.search_users);
        search_icon = view.findViewById(R.id.search_icon);
        friends_txt = view.findViewById(R.id.friends_txt);
        discover_txt = view.findViewById(R.id.discover_txt);
        new_user = view.findViewById(R.id.new_user);
        status = view.findViewById(R.id.status);
        audios = view.findViewById(R.id.audios);
        status_line = view.findViewById(R.id.status_line);
        audios_line = view.findViewById(R.id.audios_line);
        recycler_view_audios = view.findViewById(R.id.recycler_view_audios);
        topBar = view.findViewById(R.id.topBar);
        header = view.findViewById(R.id.header);
        shimmer = view.findViewById(R.id.shimmer);
        recyclerView_story = view.findViewById(R.id.recycler_view_story);
        recycler_view_storyCloseFriends = view.findViewById(R.id.recycler_view_storyCloseFriends);
        recycler_view_images = view.findViewById(R.id.recycler_view_post);

        handler.postDelayed(() -> {
            shimmer.stopShimmer();
            shimmer.hideShimmer();
            shimmer.setVisibility(View.GONE);

            topBar.setVisibility(View.VISIBLE);
            header.setVisibility(View.VISIBLE);
            new_user.setVisibility(View.VISIBLE);
            recyclerView_story.setVisibility(View.VISIBLE);
            recycler_view_storyCloseFriends.setVisibility(View.VISIBLE);
            recycler_view_images.setVisibility(View.VISIBLE);
        }, 2500);

        postList = new ArrayList<>();
        followingStories();
        discoverUserPosts();
        closeFriendsStories();

        search_btn.setOnClickListener(v -> searchUsers());
        search_icon.setOnClickListener(v -> searchUsers());
        checkFollowing();
        readPost();
        checkCloseFriends();

        status.setOnClickListener(v -> getOnlyStatus());
        audios.setOnClickListener(v -> getOnlyAudios());

        return view;
    }

    private void getOnlyStatus() {
        recycler_view_audios.setVisibility(View.GONE);
        recycler_view_storyCloseFriends.setVisibility(View.VISIBLE);
        recycler_view_images.setVisibility(View.VISIBLE);
        audios.setTextColor(getResources().getColor(R.color.ae));
        audios_line.setVisibility(View.GONE);
        status.setTextColor(getResources().getColor(R.color.blue));
        status_line.setVisibility(View.VISIBLE);
        closeFriendsStories();
        discoverUserPosts();
        checkFollowing();
        readPost();
        checkCloseFriends();
    }

    private void getOnlyAudios() {
        recycler_view_audios.setVisibility(View.VISIBLE);
        recycler_view_storyCloseFriends.setVisibility(View.GONE);
        recycler_view_images.setVisibility(View.GONE);
        status.setTextColor(getResources().getColor(R.color.ae));
        status_line.setVisibility(View.GONE);
        audios.setTextColor(getResources().getColor(R.color.blue));
        audios_line.setVisibility(View.VISIBLE);
        new_user.setVisibility(View.GONE);
        friends_txt.setVisibility(View.GONE);
        discover_txt.setVisibility(View.GONE);
        //Show Audio Recycler
        //Read Voice Recordings
    }

    private void closeFriendsStories() {
        recycler_view_storyCloseFriends.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recycler_view_storyCloseFriends.setLayoutManager(linearLayoutManager2);
        storyCloseFriendList = new ArrayList<>();
        storyCloseFriendAdapter = new StoryCloseFreindsAdapter(getContext(), storyCloseFriendList);
        recycler_view_storyCloseFriends.setAdapter(storyCloseFriendAdapter);
    }

    private void discoverUserPosts() {
        recycler_view_images.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new GridLayoutManager(getContext(), 3);
        recycler_view_images.setLayoutManager(linearLayoutManager1);
        imagePostAdapter = new GridImageAdapter(getContext(), postList);
        recycler_view_images.setAdapter(imagePostAdapter);
    }

    private void followingStories() {
        recyclerView_story.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView_story.setLayoutManager(linearLayoutManager);
        storyList = new ArrayList<>();
        storyAdapter = new StoryAllAdapter(getContext(), storyList);
        recyclerView_story.setAdapter(storyAdapter);
    }

    private void checkFollowing() {
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    followingList.add(dataSnapshot.getKey());
                }
                readStory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkCloseFriends() {
        closeFriendsList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Friends")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("close");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                closeFriendsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    closeFriendsList.add(dataSnapshot.getKey());
                }
                readCloseFriendStory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readStory() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long timecurrent = System.currentTimeMillis();
                storyList.clear();
                storyList.add(new Story("", 0, 0, "", "",FirebaseAuth.getInstance().getCurrentUser().getUid()));
                for (String id : followingList) {
                    int countStory = 0;
                    Story story = null;
                    for (DataSnapshot dataSnapshot : snapshot.child(id).getChildren()) {
                        story = dataSnapshot.getValue(Story.class);
                        if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
                            countStory++;
                        }
                    }
                    if (countStory > 0) {
                        storyList.add(story);
                    }
                }
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readCloseFriendStory() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long timecurrent = System.currentTimeMillis();
                storyCloseFriendList.clear();
                for (String id : closeFriendsList) {
                    int countStory = 0;
                    Story story = null;
                    for (DataSnapshot dataSnapshot : snapshot.child(id).getChildren()) {
                        story = dataSnapshot.getValue(Story.class);
                        if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
                            countStory++;
                        }
                    }
                    if (countStory > 0) {
                        storyCloseFriendList.add(story);
                        friends_txt.setVisibility(View.VISIBLE);
                        new_user.setVisibility(View.GONE);
                    }
                }
                storyCloseFriendAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (!post.getPublisher().equals(firebaseUser.getUid())) {
                        if (!post.getType().equals("Video") && !post.getType().equals("Text")) {
                            postList.add(post);
                            discover_txt.setVisibility(View.VISIBLE);
                        }
                    }
                }
                Collections.shuffle(postList);
                new_user.setVisibility(View.GONE);
                imagePostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchUsers() {
        ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new SearchFragment()).addToBackStack(null).commit();
    }

}
