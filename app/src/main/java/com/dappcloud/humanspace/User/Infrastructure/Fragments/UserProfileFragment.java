package com.dappcloud.humanspace.User.Infrastructure.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dappcloud.humanspace.AdapterClasses.MyPostAdapter;
import com.dappcloud.humanspace.AdapterClasses.TextPostAdapter;
import com.dappcloud.humanspace.Databases.Post;
import com.dappcloud.humanspace.Databases.User;
import com.dappcloud.humanspace.R;
import com.dappcloud.humanspace.User.Infrastructure.Activities.ChattingActivity;
import com.dappcloud.humanspace.User.Infrastructure.Activities.ShowFollowersActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
/**
 * Selected User Profile Message, Follow, Add Friend and Like Account
 */
public class UserProfileFragment extends Fragment {

    private TextView username_profile, fullname_profile, account_type, followers_profile, following_profile,
            friends_profile, bio_profile, profession_profile, city_profile, gender_profile, interest_profile, website_profile, username_follow, username_like;
    private Button message_btn, follow_btn, like_btn, close_friend_follow, unfollow, close_friend_like, unlike;
    private ImageView profile_menu_bottom_sheet, back_press, verified, image_profile;
    private CircleImageView image_profile_follow, image_profile_like;
    private LinearLayout show_followers, show_following, show_likes, body;
    private BottomSheetDialog bottomSheetDialog;
    private FirebaseUser firebaseUser;
    private String profileid;

    private TextView images, videos, text;
    private View images_line, videos_line, text_line;
    private AppBarLayout app_bar_layout;

    private ShimmerFrameLayout shimmer;
    Handler handler = new Handler();

    private RecyclerView recycler_view, recycler_view_text, recycler_view_videos;
    MyPostAdapter myPostAdapter;
    private TextPostAdapter textPostAdapter;
    List<Post> postList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        // profileid = prefs.getString("profileid", "none");
        profileid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        back_press = view.findViewById(R.id.back_press);
        username_profile = view.findViewById(R.id.username_profile);
        verified = view.findViewById(R.id.verified);

        fullname_profile = view.findViewById(R.id.fullname_profile);
        account_type = view.findViewById(R.id.account_type);
        followers_profile = view.findViewById(R.id.followers_profile);
        following_profile = view.findViewById(R.id.following_profile);
        friends_profile = view.findViewById(R.id.friends_profile);
        bio_profile = view.findViewById(R.id.bio_profile);
        profession_profile = view.findViewById(R.id.profession_profile);
        city_profile = view.findViewById(R.id.city_profile);
        gender_profile = view.findViewById(R.id.gender_profile);
        interest_profile = view.findViewById(R.id.interest_profile);
        website_profile = view.findViewById(R.id.website_profile);
        profile_menu_bottom_sheet = view.findViewById(R.id.profile_menu_bottom_sheet);
        message_btn = view.findViewById(R.id.message_btn);
        follow_btn = view.findViewById(R.id.follow_btn);
        like_btn = view.findViewById(R.id.like_btn);
        image_profile = view.findViewById(R.id.image_profile);
        show_followers = view.findViewById(R.id.show_followers);
        show_following = view.findViewById(R.id.show_following);
        show_likes = view.findViewById(R.id.show_likes);
        images = view.findViewById(R.id.images);
        videos = view.findViewById(R.id.videos);
        text = view.findViewById(R.id.text);
        images_line = view.findViewById(R.id.images_line);
        videos_line = view.findViewById(R.id.videos_line);
        text_line = view.findViewById(R.id.text_line);
        body = view.findViewById(R.id.body);
        app_bar_layout = view.findViewById(R.id.app_bar_layout);
        shimmer = view.findViewById(R.id.shimmer);

        handler.postDelayed(() -> {
            shimmer.stopShimmer();
            shimmer.hideShimmer();
            shimmer.setVisibility(View.GONE);

            app_bar_layout.setVisibility(View.VISIBLE);
            body.setVisibility(View.VISIBLE);
        }, 1500);

        //Show Only Images
        recycler_view = view.findViewById(R.id.recycler_view);
        recycler_view.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);
        recycler_view.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        myPostAdapter = new MyPostAdapter(getContext(), postList, new MyPostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Post item) {
            }
        });
        recycler_view.setAdapter(myPostAdapter);

        //Show Only Videos
        recycler_view_videos = view.findViewById(R.id.recycler_view);


        //Show Only Text
        recycler_view_text = view.findViewById(R.id.recycler_view_text);
        recycler_view_text.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext());
        linearLayoutManager2.setReverseLayout(true);
        linearLayoutManager2.setStackFromEnd(true);
        recycler_view_text.setLayoutManager(linearLayoutManager2);
        textPostAdapter = new TextPostAdapter(getContext(), postList);
        recycler_view_text.setAdapter(textPostAdapter);

        images.setOnClickListener(v -> {
            recycler_view_text.setVisibility(View.GONE);
            recycler_view_videos.setVisibility(View.GONE);
            videos.setTextColor(getResources().getColor(R.color.ae));
            text.setTextColor(getResources().getColor(R.color.ae));
            videos_line.setVisibility(View.GONE);
            text_line.setVisibility(View.GONE);
            images.setTextColor(getResources().getColor(R.color.blue));
            images_line.setVisibility(View.VISIBLE);
            recycler_view.setVisibility(View.VISIBLE);
            readImagePosts();
        });

        videos.setOnClickListener(v -> {
            recycler_view_text.setVisibility(View.GONE);
            recycler_view.setVisibility(View.GONE);
            images.setTextColor(getResources().getColor(R.color.ae));
            text.setTextColor(getResources().getColor(R.color.ae));
            images_line.setVisibility(View.GONE);
            text_line.setVisibility(View.GONE);
            videos.setTextColor(getResources().getColor(R.color.blue));
            videos_line.setVisibility(View.VISIBLE);
            recycler_view_videos.setVisibility(View.VISIBLE);
            readVideoPosts();
        });

        text.setOnClickListener(v -> {
            recycler_view.setVisibility(View.GONE);
            recycler_view_videos.setVisibility(View.GONE);
            videos.setTextColor(getResources().getColor(R.color.ae));
            images.setTextColor(getResources().getColor(R.color.ae));
            videos_line.setVisibility(View.GONE);
            images_line.setVisibility(View.GONE);
            text.setTextColor(getResources().getColor(R.color.blue));
            text_line.setVisibility(View.VISIBLE);
            recycler_view_text.setVisibility(View.VISIBLE);
            readTextPost();
        });

        userInfo();
        checkFollowing();
        checkFriends();
        getFollowers();
        getFollowing();
        getFriends();
        followButton();
        likeButton();
        readImagePosts();

        back_press.setOnClickListener(v -> ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new SearchFragment()).addToBackStack(null).commit());

        show_followers.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ShowFollowersActivity.class);
            intent.putExtra("id", profileid);
            intent.putExtra("title", "Followers");
            startActivity(intent);
        });

        show_following.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ShowFollowersActivity.class);
            intent.putExtra("id", profileid);
            intent.putExtra("title", "Following");
            startActivity(intent);
        });

        show_likes.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ShowFollowersActivity.class);
            intent.putExtra("id", profileid);
            intent.putExtra("title", "Likes");
            startActivity(intent);
        });

        message_btn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ChattingActivity.class);
            intent.putExtra("profileid", profileid);
            getContext().startActivity(intent);
        });

        return view;
    }

    private void userInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null) {
                    return;
                }
                User user = snapshot.getValue(User.class);
                try {
                    Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                username_profile.setText(user.getUsername());
                fullname_profile.setText(user.getFullName());
                account_type.setText(user.getAccount() + " Account");
                bio_profile.setText(user.getBio());
                profession_profile.setText(user.getProfession());
                city_profile.setText(user.getCity());
                gender_profile.setText(user.getGender());
                interest_profile.setText("Interested in " + user.getInterest());
                website_profile.setText(user.getWebsite());
                website_profile.setOnClickListener(v -> gotoUrl(user.getWebsite()));

                if (!user.getBio().isEmpty()) {
                    bio_profile.setVisibility(View.VISIBLE);
                }

                if (!user.getCity().isEmpty()) {
                    city_profile.setVisibility(View.VISIBLE);
                }

                if (!user.getWebsite().isEmpty()) {
                    website_profile.setVisibility(View.VISIBLE);
                }

                if (user.getIsVerified().equals("Yes")) {
                    verified.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFollowing() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileid).exists()) {
                    follow_btn.setText("Following");
                } else {
                    follow_btn.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFriends() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Friends")
                .child(profileid).child("close");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()) {
                    like_btn.setText("Friends");
                } else {
                    like_btn.setText("Add Friend");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers_profile.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowing() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following_profile.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFriends() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Friends").child(profileid).child("close");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friends_profile.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void followButton() {
        follow_btn.setOnClickListener(v -> {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow")
                    .child(firebaseUser.getUid()).child("following");

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(profileid).exists()) {
                        bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetTheme);

                        final View sheetView = LayoutInflater.from(getContext()).inflate(R.layout.layout_follow_bottom_sheet,
                                (ViewGroup) v.findViewById(R.id.bottom_sheet_follow));

                        image_profile_follow = sheetView.findViewById(R.id.image_profile_follow);
                        username_follow = sheetView.findViewById(R.id.username_follow);
                        close_friend_follow = sheetView.findViewById(R.id.close_friend_follow);
                        unfollow = sheetView.findViewById(R.id.unfollow);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                try {
                                    Glide.with(getContext()).load(user.getImageurl()).into(image_profile_follow);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                username_follow.setText(String.format(
                                        user.getUsername()
                                ));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        //Like and UnLike Accounts
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Like")
                                .child(profileid).child("likes");
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.child(firebaseUser.getUid()).exists()) {
                                    close_friend_follow.setText("Remove from Liked Accounts");

                                    sheetView.findViewById(R.id.close_friend_follow).setOnClickListener(v12 -> {
                                        FirebaseDatabase.getInstance().getReference().child("Like").child(profileid)
                                                .child("likes").child(firebaseUser.getUid()).removeValue();

                                        close_friend_follow.setText("Add to Liked Accounts");
                                        Toast.makeText(getContext(), "Removed from Liked Accounts List", Toast.LENGTH_SHORT).show();
                                        bottomSheetDialog.dismiss();
                                    });
                                } else {
                                    close_friend_follow.setText("Add to Liked Accounts");

                                    sheetView.findViewById(R.id.close_friend_follow).setOnClickListener(v13 -> {
                                        FirebaseDatabase.getInstance().getReference().child("Like").child(profileid)
                                                .child("likes").child(firebaseUser.getUid()).setValue(true);

                                        Toast.makeText(getContext(), "Added to Liked Accounts List", Toast.LENGTH_SHORT).show();
                                        bottomSheetDialog.dismiss();
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        unfollow.setText("Unfollow");
                        sheetView.findViewById(R.id.unfollow).setOnClickListener(v1 -> {
                            FirebaseDatabase.getInstance().getReference("Follow").child(firebaseUser.getUid())
                                    .child("following").child(profileid).removeValue();
                            FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                                    .child("followers").child(firebaseUser.getUid()).removeValue();
                            follow_btn.setText("Follow");

                            bottomSheetDialog.dismiss();
                        });

                        bottomSheetDialog.setContentView(sheetView);
                        bottomSheetDialog.show();
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                                .child("following").child(profileid).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                                .child("followers").child(firebaseUser.getUid()).setValue(true);

                        addFollowNotifications();
                        follow_btn.setText("Following");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });
    }

    private void likeButton() {
        like_btn.setOnClickListener(v -> {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Friends")
                    .child(profileid).child("close");

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(firebaseUser.getUid()).exists()) {
                        bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetTheme);

                        final View sheetView = LayoutInflater.from(getContext()).inflate(R.layout.layout_like_bottom_sheet,
                                (ViewGroup) v.findViewById(R.id.bottom_sheet_like));

                        image_profile_like = sheetView.findViewById(R.id.image_profile_like);
                        username_like = sheetView.findViewById(R.id.username_like);
                        close_friend_like = sheetView.findViewById(R.id.close_friend_like);
                        unlike = sheetView.findViewById(R.id.unlike);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                try {
                                    Glide.with(getContext()).load(user.getImageurl()).into(image_profile_like);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                username_like.setText(String.format(
                                        user.getUsername()
                                ));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        //Like and UnLike Accounts
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Like")
                                .child(profileid).child("likes");
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.child(firebaseUser.getUid()).exists()) {
                                    close_friend_like.setText("Remove from Liked Accounts");

                                    sheetView.findViewById(R.id.close_friend_like).setOnClickListener(v13 -> {
                                        FirebaseDatabase.getInstance().getReference().child("Like").child(profileid)
                                                .child("likes").child(firebaseUser.getUid()).removeValue();

                                        close_friend_like.setText("Add to Liked Accounts");
                                        Toast.makeText(getContext(), "Removed from Liked Accounts List", Toast.LENGTH_SHORT).show();
                                        bottomSheetDialog.dismiss();
                                    });
                                } else {
                                    close_friend_like.setText("Add to Liked Accounts");

                                    sheetView.findViewById(R.id.close_friend_like).setOnClickListener(v12 -> {
                                        FirebaseDatabase.getInstance().getReference().child("Like").child(profileid)
                                                .child("likes").child(firebaseUser.getUid()).setValue(true);

                                        Toast.makeText(getContext(), "Added to Liked Accounts List", Toast.LENGTH_SHORT).show();
                                        bottomSheetDialog.dismiss();
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        //Add Friends and Unfriend
                        unlike.setText("Unfriend");

                        sheetView.findViewById(R.id.unlike).setOnClickListener(v1 -> {
                            FirebaseDatabase.getInstance().getReference().child("Friends").child(profileid)
                                    .child("close").child(firebaseUser.getUid()).removeValue();
                            like_btn.setText("Add Friend");

                            bottomSheetDialog.dismiss();
                        });

                        bottomSheetDialog.setContentView(sheetView);
                        bottomSheetDialog.show();
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Friends").child(profileid)
                                .child("close").child(firebaseUser.getUid()).setValue(true);

                        like_btn.setText("Friends");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });
    }

    private void addFollowNotifications() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userId", firebaseUser.getUid());
        hashMap.put("text", "started following you");

        reference.push().setValue(hashMap);
    }

    private void readImagePosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)) {
                        if (!post.getType().equals("Video") && !post.getType().equals("Text")) {
                            postList.add(post);
                        }
                    }
                }
                Collections.reverse(postList);
                myPostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readTextPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)) {
                        if (!post.getType().equals("Video") && !post.getType().equals("Image")) {
                            postList.add(post);
                        }
                    }
                }
                Collections.reverse(postList);
                myPostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readVideoPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)) {
                        if (!post.getType().equals("Image") && !post.getType().equals("Text")) {
                            postList.add(post);
                        }
                    }
                }
                Collections.reverse(postList);
                myPostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }
}
