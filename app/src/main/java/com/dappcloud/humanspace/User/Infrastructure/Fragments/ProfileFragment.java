package com.dappcloud.humanspace.User.Infrastructure.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.dappcloud.humanspace.AdapterClasses.MyPostAdapter;
import com.dappcloud.humanspace.AdapterClasses.TextPostAdapter;
import com.dappcloud.humanspace.Databases.Post;
import com.dappcloud.humanspace.User.Infrastructure.Activities.AddPostActivity;
import com.dappcloud.humanspace.User.Infrastructure.Activities.AddStoryActivity;
import com.dappcloud.humanspace.User.Infrastructure.Activities.EditProfileActivity;
import com.dappcloud.humanspace.User.Infrastructure.Activities.ShowFollowersActivity;
import com.dappcloud.humanspace.User.SigninSignup.SignIn;
import com.dappcloud.humanspace.Databases.User;
import com.dappcloud.humanspace.R;
import com.dappcloud.humanspace.User.Infrastructure.Activities.FeaturedActivity;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private RelativeLayout imageShowLayout;
    CircleImageView imageShowClose;
    private ImageView imageShowView;

    private TextView username_profile, fullname_profile, account_type, followers_profile, following_profile,
            friends_profile, bio_profile, profession_profile, city_profile, gender_profile, interest_profile, website_profile, username_signOut;
    private Button logout_bottom_sheet, edit_business_profile, promote_business, edit_profile_btn;
    private ImageView image_profile, notifications, profile_menu_bottom_sheet, add_story, verified;
    private CircleImageView image_profile_singOut;
    private LinearLayout show_followers, show_following, show_likes, body;
    private BottomSheetDialog bottomSheetDialog;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;

    private TextView images, videos, text;
    private View images_line, videos_line, text_line;
    private AppBarLayout app_bar_layout;

    private ShimmerFrameLayout shimmer;
    Handler handler = new Handler();

    private RecyclerView recycler_view, recycler_view_text, recycler_view_videos;
    MyPostAdapter myPostAdapter;
    private TextPostAdapter textPostAdapter;
    List<Post> postList;

    @SuppressLint({"ResourceAsColor", "UseCompatLoadingForDrawables"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();

        imageShowLayout = view.findViewById(R.id.imageShowLayout);
        imageShowView = view.findViewById(R.id.imageShowView);
        imageShowClose = view.findViewById(R.id.imageShowCloseBtn);
        Glide.with(this).load(view.getContext().getDrawable(R.drawable.ic_close)).transform(new CircleCrop()).into(imageShowClose);
        imageShowClose.setCircleBackgroundColor(android.R.color.white);
        imageShowClose.setOnClickListener(v->{
            imageShowLayout.clearFocus();
            imageShowLayout.setVisibility(View.GONE);
        });
        imageShowLayout.setVisibility(View.GONE);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        username_profile = view.findViewById(R.id.username_profile);
        verified = view.findViewById(R.id.verified);
        fullname_profile = view.findViewById(R.id.fullname_profile);
        account_type = view.findViewById(R.id.account_type);
        followers_profile = view.findViewById(R.id.followers_profile);
        following_profile = view.findViewById(R.id.following_profile);
        friends_profile = view.findViewById(R.id.friends_profile);
        edit_profile_btn = view.findViewById(R.id.edit_profile_btn);
        bio_profile = view.findViewById(R.id.bio_profile);
        profession_profile = view.findViewById(R.id.profession_profile);
        city_profile = view.findViewById(R.id.city_profile);
        gender_profile = view.findViewById(R.id.gender_profile);
        interest_profile = view.findViewById(R.id.interest_profile);
        website_profile = view.findViewById(R.id.website_profile);
        logout_bottom_sheet = view.findViewById(R.id.logout_bottom_sheet);
        edit_business_profile = view.findViewById(R.id.edit_business_profile);
        promote_business = view.findViewById(R.id.promote_business);
        add_story = view.findViewById(R.id.add_story);
        profile_menu_bottom_sheet = view.findViewById(R.id.profile_menu_bottom_sheet);
        image_profile = view.findViewById(R.id.image_profile);
        notifications = view.findViewById(R.id.notifications);
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
            body.setVisibility(View.VISIBLE);
            app_bar_layout.setVisibility(View.VISIBLE);
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
                if ( item.getType().equals("Image") ) {
                    initializeImageShow(item.getPostUrl());
                }
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

        userDetails();
        readImagePosts();
        getFollowers();
        getFollowing();
        getFriends();
        profileMenuBottomSheet();
        createNew();
        logout_bottom_sheet.setOnClickListener(v -> logoutBottomSheet(view));
        notifications.setOnClickListener(v -> ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new NotificationFragment()).addToBackStack(null).commit());

        show_followers.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ShowFollowersActivity.class);
            intent.putExtra("id", firebaseUser.getUid());
            intent.putExtra("title", "Followers");
            startActivity(intent);
        });

        show_following.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ShowFollowersActivity.class);
            intent.putExtra("id", firebaseUser.getUid());
            intent.putExtra("title", "Following");
            startActivity(intent);
        });

        show_likes.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ShowFollowersActivity.class);
            intent.putExtra("id", firebaseUser.getUid());
            intent.putExtra("title", "Friends");
            startActivity(intent);
        });

        edit_profile_btn.setOnClickListener(v -> startActivity(new Intent(getContext(), EditProfileActivity.class)));

        edit_business_profile.setOnClickListener(v -> Toast.makeText(getContext(), "Business Profile", Toast.LENGTH_SHORT).show());

        promote_business.setOnClickListener(v -> Toast.makeText(getContext(), "Promote Business", Toast.LENGTH_SHORT).show());

        return view;

    }

    private void initializeImageShow(String postUrl) {
        imageShowLayout.setVisibility(View.VISIBLE);
        imageShowLayout.requestFocus();
        Glide.with(imageShowLayout.getContext()).load(postUrl).into(imageShowView);
    }

    private void readImagePosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(firebaseUser.getUid())) {
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

    private void readVideoPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(firebaseUser.getUid())) {
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

    private void readTextPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(firebaseUser.getUid())) {
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

    private void createNew() {
        add_story.setOnClickListener(v -> {
            bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetTheme);

            View sheetView = LayoutInflater.from(getContext()).inflate(R.layout.layout_add_story_bottom_sheet,
                    (ViewGroup) v.findViewById(R.id.bottom_sheet_add_story));

            sheetView.findViewById(R.id.add_story_photo_bsheet).setOnClickListener(v1 -> {
                startActivity(new Intent(getActivity(), AddStoryActivity.class));
                bottomSheetDialog.dismiss();
            });

            sheetView.findViewById(R.id.new_post).setOnClickListener(v12 -> {
                startActivity(new Intent(getActivity(), AddPostActivity.class));
                bottomSheetDialog.dismiss();
            });

            bottomSheetDialog.setContentView(sheetView);
            bottomSheetDialog.show();
        });
    }

    private void profileMenuBottomSheet() {
        profile_menu_bottom_sheet.setOnClickListener(v -> {
            bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetTheme);

            View sheetView = LayoutInflater.from(getContext()).inflate(R.layout.layout_menu_bottom_sheet,
                    (ViewGroup) v.findViewById(R.id.bottom_sheet_menu));

            sheetView.findViewById(R.id.account_setting).setOnClickListener(v1 -> {
                ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AccountFragment()).commit();
                bottomSheetDialog.dismiss();
            });
            sheetView.findViewById(R.id.security_setting).setOnClickListener(v12 -> {
                ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SecurityFragment()).commit();
                bottomSheetDialog.dismiss();
            });
            sheetView.findViewById(R.id.discover_people).setOnClickListener(v15 -> {
                ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SearchFragment()).commit();
                bottomSheetDialog.dismiss();
            });
            sheetView.findViewById(R.id.about_setting).setOnClickListener(v16 -> {
                ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AboutFragment()).commit();
                bottomSheetDialog.dismiss();
            });
            bottomSheetDialog.setContentView(sheetView);
            bottomSheetDialog.show();
        });
    }

    private void logoutBottomSheet(View v) {
        bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetTheme);

        View sheetView = LayoutInflater.from(getContext()).inflate(R.layout.layout_sign_out_bottom_sheet,
                (ViewGroup) v.findViewById(R.id.bottom_sheet_signOut));

        image_profile_singOut = sheetView.findViewById(R.id.image_profile_singOut);
        username_signOut = sheetView.findViewById(R.id.username_signOut);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username_signOut.setText(String.format(
                        user.getUsername()
                ));
                try {
                    Glide.with(v.getContext()).load(user.getImageurl()).into(image_profile_singOut);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sheetView.findViewById(R.id.logout_btn).setOnClickListener(v1 -> {
            auth.signOut();
            startActivity(new Intent(getActivity(), SignIn.class));
            getActivity();

            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }

    private void userDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
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

                if(user.getAccount().equals("Personal")) {
                    add_story.setVisibility(View.VISIBLE);
                }

                username_profile.setText(user.getUsername());
                fullname_profile.setText(user.getFullName());
                account_type.setText(user.getAccount() + " Account");
                bio_profile.setText(user.getBio());
                profession_profile.setText(user.getProfession());
                city_profile.setText(user.getCity());
                gender_profile.setText("Gender: " + user.getGender());
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

    private void getFollowers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("followers");
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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following");
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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Friends").child(firebaseUser.getUid()).child("close");
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

    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }

}
