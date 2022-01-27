package com.dappcloud.humanspace.User.Infrastructure.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.dappcloud.humanspace.AdapterClasses.GridImageAdapter;
import com.dappcloud.humanspace.AdapterClasses.GridVideoAdapter;
import com.dappcloud.humanspace.AdapterClasses.PostAdapter;
import com.dappcloud.humanspace.AdapterClasses.TextPostAdapter;
import com.dappcloud.humanspace.Databases.Post;
import com.dappcloud.humanspace.Databases.User;
import com.dappcloud.humanspace.R;
import com.dappcloud.humanspace.User.Infrastructure.Activities.AddPostActivity;
import com.dappcloud.humanspace.User.Infrastructure.Activities.AddVideoPostActivity;
import com.dappcloud.humanspace.User.Infrastructure.Activities.MessageActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

/**
 * All Posts, Timeline interactions
 */

public class PostsFragment extends Fragment {

    private View view;

    private RelativeLayout videoLayout;
    CircleImageView closeVideo;
    private VideoView videoView;

    private ImageView add_post, chats, notifications;
    private CircleImageView image_profile;
    private TextView username;
    private ImageButton emoji_icon, btn_send;
    private EmojiconEditText text_send;
    private EmojIconActions emojIconActions;
    private View root_view;
    private BottomSheetDialog bottomSheetDialog;
    private TextView all_post, images, videos, text, audios;
    private View all_post_line, images_line, videos_line, text_line, audios_line;

    private LinearLayout new_user;
    private ImageView first_timer;
    private TextView first_post;

    private RecyclerView recycler_view_images, recycler_view_text, recycler_view_all_post, recycler_view_videos, recycler_view_audios;
    private List<Post> postList;

    private PostAdapter allPostAdapter;
    private GridImageAdapter imagePostAdapter;
    private GridVideoAdapter videoPostAdapter;
    private TextPostAdapter textPostAdapter;

    private List<String> followingList;

    private FirebaseUser firebaseUser;
    String profileid;

    private RelativeLayout post_text;
    private LinearLayout header;

    private ShimmerFrameLayout shimmer;
    Handler handler = new Handler();

    @SuppressLint({"UseCompatLoadingForDrawables", "ResourceAsColor"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_posts, container, false);

        videoLayout = view.findViewById(R.id.videoLayout);
        videoLayout.setVisibility(View.GONE);
        videoView = view.findViewById(R.id.videoView);
        closeVideo = view.findViewById(R.id.videoCloseBtn);
        Glide.with(this).load(view.getContext().getDrawable(R.drawable.ic_close)).transform(new CircleCrop()).into(closeVideo);
        closeVideo.setCircleBackgroundColor(android.R.color.white);
        closeVideo.setOnClickListener(v->{
            videoView.setVideoURI(null);
            videoView.stopPlayback();
            videoView.clearFocus();
            videoLayout.setVisibility(View.GONE);
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        image_profile = view.findViewById(R.id.image_profile);
        text_send = view.findViewById(R.id.text_send);
        emoji_icon = view.findViewById(R.id.emoji_icon);
        btn_send = view.findViewById(R.id.btn_send);
        add_post = view.findViewById(R.id.add_post);
        chats = view.findViewById(R.id.chats);
        notifications = view.findViewById(R.id.notifications);
        root_view = view.findViewById(R.id.root_view);
        add_post = view.findViewById(R.id.add_post);
        all_post = view.findViewById(R.id.all_post);
        images = view.findViewById(R.id.images);
        videos = view.findViewById(R.id.videos);
        text = view.findViewById(R.id.text);
        audios = view.findViewById(R.id.audios);
        all_post_line = view.findViewById(R.id.all_post_line);
        images_line = view.findViewById(R.id.images_line);
        videos_line = view.findViewById(R.id.videos_line);
        text_line = view.findViewById(R.id.text_line);
        audios_line = view.findViewById(R.id.audios_line);
        new_user = view.findViewById(R.id.new_user);
        first_timer = view.findViewById(R.id.first_timer);
        first_post = view.findViewById(R.id.first_post);
        post_text = view.findViewById(R.id.post_text);
        header = view.findViewById(R.id.header);
        shimmer = view.findViewById(R.id.shimmer);
        recycler_view_all_post = view.findViewById(R.id.recycler_view_all_post);
        recycler_view_images = view.findViewById(R.id.recycler_view_images);
        recycler_view_videos = view.findViewById(R.id.recycler_view_videos);
        recycler_view_text = view.findViewById(R.id.recycler_view_text);

        handler.postDelayed(() -> {
            shimmer.stopShimmer();
            shimmer.hideShimmer();
            shimmer.setVisibility(View.GONE);

            post_text.setVisibility(View.VISIBLE);
            header.setVisibility(View.VISIBLE);
            recycler_view_all_post.setVisibility(View.VISIBLE);
        }, 2500);

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");

        emojIconActions = new EmojIconActions(getContext(), root_view, text_send, emoji_icon);
        emojIconActions.ShowEmojIcon();

        postList = new ArrayList<>();
        showAllPosts();
        showOnlyImages();
        showOnlyVideos();
        showOnlyText();
        all_post.setOnClickListener(v -> getAllPosts());
        text.setOnClickListener(v -> getOnlyText());
        images.setOnClickListener(v -> getOnlyImages());
        videos.setOnClickListener(v -> getOnlyVideos());
        audios.setOnClickListener(v -> getOnlyAudios());
        btn_send.setOnClickListener(v -> shareText());
        notifications.setOnClickListener(v -> openNotifications());

        add_post.setOnClickListener(v -> postOptions(view));
        first_timer.setOnClickListener(v -> postOptions(view));
        first_post.setOnClickListener(v -> postOptions(view));

        openChats();
        userDetails();
        checkFollowing();
        return view;
    }

    private void openNotifications() {
        ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new NotificationFragment()).addToBackStack(null).commit();
    }

    private void openChats() {
        chats.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MessageActivity.class);
            startActivity(intent);
        });
    }

    private void shareText() {
        //Share new text post
        String msg = text_send.getText().toString().trim();
        if (!msg.equals("") && !msg.isEmpty()) {
            sendPost(firebaseUser.getUid(), msg);
        }
        text_send.setText("");
        Toast.makeText(getContext(), "Posted to your space!", Toast.LENGTH_SHORT).show();
    }

    private void sendPost(String publisher, String text) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());
        String currentDate = simpleDateFormat.format(new Date());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");
        String postId = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("postUrl", "");
        hashMap.put("type", "Text");
        hashMap.put("createdOn", currentDate);
        hashMap.put("createdAt", ServerValue.TIMESTAMP);
        hashMap.put("caption", text);
        hashMap.put("postId", postId);
        hashMap.put("publisher", publisher);
        reference.child(postId).setValue(hashMap);
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
                readAllPost();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readAllPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(firebaseUser.getUid())) {
                        if (!post.getType().equals("Video") && !post.getType().equals("Text")) {
                            postList.add(post);
                            new_user.setVisibility(View.GONE);
                        }
                    }
                    for (String id : followingList) {
                        if (id.equals(post.getPublisher())) {
                            if (!post.getType().equals("Video") && !post.getType().equals("Text")) {
                                postList.add(post);
                                new_user.setVisibility(View.GONE);
                            }
                        }
                    }
                }
                allPostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readImagePosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (!post.getPublisher().equals(firebaseUser.getUid())) {
                        if (!post.getType().equals("Video") && !post.getType().equals("Text")) {
                            postList.add(post);
                        }
                    }
                }
                Collections.shuffle(postList);
                imagePostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readVideoPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (!post.getPublisher().equals(firebaseUser.getUid())) {
                        if (!post.getType().equals("Image") && !post.getType().equals("Text")) {
                            postList.add(post);
                        }
                    }
                }
                Collections.shuffle(postList);
                videoPostAdapter.notifyDataSetChanged();
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
                    if (!post.getType().equals("Video") && !post.getType().equals("Image")) {
                        postList.add(post);
                    }
                }
                textPostAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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
                    add_post.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void postOptions(View v) {
        bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetTheme);

        View sheetView = LayoutInflater.from(getContext()).inflate(R.layout.layout_publish_post_bottom_sheet,
                (ViewGroup) v.findViewById(R.id.bottom_sheet_post_options));

        image_profile = sheetView.findViewById(R.id.image_profile);
        username = sheetView.findViewById(R.id.username);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                try {
                    Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                username.setText(String.format(
                        user.getUsername()
                ));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sheetView.findViewById(R.id.post_image).setOnClickListener(v1 -> {
            startActivity(new Intent(getContext(), AddPostActivity.class));
            bottomSheetDialog.dismiss();
        });

        sheetView.findViewById(R.id.post_video).setOnClickListener(v2 -> {
            startActivity(new Intent(getContext(), AddVideoPostActivity.class));
            bottomSheetDialog.dismiss();
        });

        //Add Voice
        sheetView.findViewById(R.id.post_voice).setOnClickListener(v12 -> {
            //Add Voice intent or function or method here
            Toast.makeText(getContext(), "Add Voice", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }

    private void getOnlyAudios() {
        recycler_view_all_post.setVisibility(View.GONE);
        recycler_view_images.setVisibility(View.GONE);
        recycler_view_text.setVisibility(View.GONE);
        recycler_view_videos.setVisibility(View.GONE);
        all_post.setTextColor(getResources().getColor(R.color.ae));
        text.setTextColor(getResources().getColor(R.color.ae));
        images.setTextColor(getResources().getColor(R.color.ae));
        videos.setTextColor(getResources().getColor(R.color.ae));
        all_post_line.setVisibility(View.GONE);
        text_line.setVisibility(View.GONE);
        images_line.setVisibility(View.GONE);
        videos_line.setVisibility(View.GONE);
        audios.setTextColor(getResources().getColor(R.color.blue));
        audios_line.setVisibility(View.VISIBLE);
        //Show Audio Recycler
        //Read Voice Recordings
    }

    private void getOnlyVideos() {
        recycler_view_all_post.setVisibility(View.GONE);
        recycler_view_images.setVisibility(View.GONE);
        recycler_view_text.setVisibility(View.GONE);
        all_post.setTextColor(getResources().getColor(R.color.ae));
        text.setTextColor(getResources().getColor(R.color.ae));
        images.setTextColor(getResources().getColor(R.color.ae));
        audios.setTextColor(getResources().getColor(R.color.ae));
        audios_line.setVisibility(View.GONE);
        all_post_line.setVisibility(View.GONE);
        text_line.setVisibility(View.GONE);
        images_line.setVisibility(View.GONE);
        videos.setTextColor(getResources().getColor(R.color.blue));
        videos_line.setVisibility(View.VISIBLE);
        recycler_view_videos.setVisibility(View.VISIBLE);
        readVideoPosts();
    }

    private void getOnlyImages() {
        recycler_view_all_post.setVisibility(View.GONE);
        recycler_view_text.setVisibility(View.GONE);
        recycler_view_videos.setVisibility(View.GONE);
        all_post.setTextColor(getResources().getColor(R.color.ae));
        videos.setTextColor(getResources().getColor(R.color.ae));
        text.setTextColor(getResources().getColor(R.color.ae));
        audios.setTextColor(getResources().getColor(R.color.ae));
        audios_line.setVisibility(View.GONE);
        all_post_line.setVisibility(View.GONE);
        videos_line.setVisibility(View.GONE);
        text_line.setVisibility(View.GONE);
        images.setTextColor(getResources().getColor(R.color.blue));
        images_line.setVisibility(View.VISIBLE);
        recycler_view_images.setVisibility(View.VISIBLE);
        readImagePosts();
    }

    private void getOnlyText() {
        recycler_view_all_post.setVisibility(View.GONE);
        recycler_view_images.setVisibility(View.GONE);
        recycler_view_videos.setVisibility(View.GONE);
        all_post.setTextColor(getResources().getColor(R.color.ae));
        videos.setTextColor(getResources().getColor(R.color.ae));
        images.setTextColor(getResources().getColor(R.color.ae));
        audios.setTextColor(getResources().getColor(R.color.ae));
        audios_line.setVisibility(View.GONE);
        all_post_line.setVisibility(View.GONE);
        videos_line.setVisibility(View.GONE);
        images_line.setVisibility(View.GONE);
        text.setTextColor(getResources().getColor(R.color.blue));
        text_line.setVisibility(View.VISIBLE);
        recycler_view_text.setVisibility(View.VISIBLE);
        readTextPost();
    }

    private void getAllPosts() {
        recycler_view_images.setVisibility(View.GONE);
        recycler_view_text.setVisibility(View.GONE);
        recycler_view_videos.setVisibility(View.GONE);
        images.setTextColor(getResources().getColor(R.color.ae));
        videos.setTextColor(getResources().getColor(R.color.ae));
        text.setTextColor(getResources().getColor(R.color.ae));
        audios.setTextColor(getResources().getColor(R.color.ae));
        audios_line.setVisibility(View.GONE);
        images_line.setVisibility(View.GONE);
        videos_line.setVisibility(View.GONE);
        text_line.setVisibility(View.GONE);
        all_post.setTextColor(getResources().getColor(R.color.blue));
        all_post_line.setVisibility(View.VISIBLE);
        recycler_view_all_post.setVisibility(View.VISIBLE);
        checkFollowing();
    }

    private void initializePlayer(String videoUrl) {
        videoLayout.setVisibility(View.VISIBLE);
        videoLayout.requestFocus();
        videoView.setVideoURI(Uri.parse(videoUrl));
        ProgressDialog bar = new ProgressDialog(videoLayout.getContext());
        bar.setTitle("Loading video");
        bar.setMessage("Please Wait... ");
        bar.setCancelable(false);
        bar.show();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                bar.dismiss();
                view.setClickable(false);
                videoView.start();
                MediaController controller = new MediaController(view.getContext());
                controller.setMediaPlayer(videoView);
                videoView.setMediaController(controller);
            }
        });
        videoView.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        videoView.seekTo(0);
                    }
                });
    }

    private void showOnlyText() {
        recycler_view_text.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext());
        linearLayoutManager2.setReverseLayout(true);
        linearLayoutManager2.setStackFromEnd(true);
        recycler_view_text.setLayoutManager(linearLayoutManager2);
        textPostAdapter = new TextPostAdapter(getContext(), postList);
        recycler_view_text.setAdapter(textPostAdapter);
    }

    private void showOnlyVideos() {
        recycler_view_images.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager3 = new GridLayoutManager(getContext(), 3);
        recycler_view_videos.setLayoutManager(linearLayoutManager3);
        videoPostAdapter = new GridVideoAdapter(getContext(), postList, new GridVideoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Post item) {
                initializePlayer(item.getPostUrl());
            }
        });
        recycler_view_videos.setAdapter(videoPostAdapter);
    }

    private void showOnlyImages() {
        recycler_view_images.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new GridLayoutManager(getContext(), 3);
        recycler_view_images.setLayoutManager(linearLayoutManager1);
        imagePostAdapter = new GridImageAdapter(getContext(), postList);
        recycler_view_images.setAdapter(imagePostAdapter);
    }

    private void showAllPosts() {
        recycler_view_all_post.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recycler_view_all_post.setLayoutManager(linearLayoutManager);
        allPostAdapter = new PostAdapter(getContext(), postList);
        recycler_view_all_post.setAdapter(allPostAdapter);
    }

}
