package com.dappcloud.humanspace.User.Infrastructure.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.dappcloud.humanspace.AdapterClasses.ChatAdapter;
import com.dappcloud.humanspace.Databases.ChatNotifications.APIService;
import com.dappcloud.humanspace.Databases.ChatNotifications.Client;
import com.dappcloud.humanspace.Databases.ChatNotifications.Data;
import com.dappcloud.humanspace.Databases.ChatNotifications.MyResponse;
import com.dappcloud.humanspace.Databases.ChatNotifications.Sender;
import com.dappcloud.humanspace.Databases.ChatNotifications.Token;
import com.dappcloud.humanspace.Databases.Chat;
import com.dappcloud.humanspace.Databases.User;
import com.dappcloud.humanspace.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChattingActivity extends AppCompatActivity {

    private RelativeLayout chatImageShowLayout;
    CircleImageView chatImageShowClose;
    private ImageView chatImageShowView;

    ImageView back_press, online_user, more_options, verified;
    CircleImageView image_profile;
    TextView username, active_text;
    EmojiconEditText text_send;
    ImageButton btn_send, emoji_icon, add_image;
    EmojIconActions emojIconActions;
    View view;

    ChatAdapter chatAdapter;
    List<Chat> mChats;
    RecyclerView recyclerView;

    Uri imageUri;
    String myUrl = "";
    StorageTask storageTask;
    StorageReference storageReference;
    FirebaseUser firebaseUser;
    String profileid;

    private static final int GALLERY_PICK = 1;

    ValueEventListener seenListener;
    APIService apiService;
    boolean notify = false;

    @SuppressLint({"ResourceAsColor", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        back_press = findViewById(R.id.back_press);
        back_press.setOnClickListener(v -> startActivity(new Intent(ChattingActivity.this, MessageActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("chatImages");
        Intent intent = getIntent();
        profileid = intent.getStringExtra("profileid");

        chatImageShowLayout = findViewById(R.id.chatImageShowLayout);
        chatImageShowView = findViewById(R.id.chatImageShowView);
        chatImageShowClose = findViewById(R.id.chatImageShowCloseBtn);
        Glide.with(this).load(chatImageShowLayout.getContext().getDrawable(R.drawable.ic_close)).transform(new CircleCrop()).into(chatImageShowClose);
        chatImageShowClose.setCircleBackgroundColor(android.R.color.white);
        chatImageShowClose.setOnClickListener(v->{
            chatImageShowLayout.clearFocus();
            chatImageShowLayout.setVisibility(View.GONE);
        });
        chatImageShowLayout.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        image_profile = findViewById(R.id.image_profile);
        online_user = findViewById(R.id.online_user);
        more_options = findViewById(R.id.more_options);
        username = findViewById(R.id.username);
        verified = findViewById(R.id.verified);
        active_text = findViewById(R.id.active_text);
        text_send = findViewById(R.id.text_send);
        btn_send = findViewById(R.id.btn_send);
        emoji_icon = findViewById(R.id.emoji_icon);
        add_image = findViewById(R.id.add_image);
        view = findViewById(R.id.root_view);

        userInfo();
        more_options.setOnClickListener(v -> Toast.makeText(getApplicationContext(), "More Options", Toast.LENGTH_SHORT).show());

        emojIconActions = new EmojIconActions(this, view, text_send, emoji_icon);
        emojIconActions.ShowEmojIcon();

        add_image.setOnClickListener(v -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
        });

        btn_send.setOnClickListener(v -> {
            notify =true;
            String msg = text_send.getText().toString().trim();
            if (!msg.equals("") && !msg.isEmpty()) {
                sendMessage(firebaseUser.getUid(), profileid, msg);
            }
            text_send.setText("");
        });
    }

    private void userInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                try {
                    Glide.with(getApplicationContext()).load(user.getImageurl()).into(image_profile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                username.setText(user.getUsername());
                active_text.setText(user.getStatus());
                if (user.getStatus().equals("Online")) {
                    online_user.setVisibility(View.VISIBLE);
                } else {
                    online_user.setVisibility(View.GONE);
                }
                if (user.getIsVerified().equals("Yes")) {
                    verified.setVisibility(View.VISIBLE);
                }
                readMessages(firebaseUser.getUid(), profileid, user.getImageurl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        seenMessage(profileid);
    }

    private void sendMessage(String sender, final String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = simpleDateFormat.format(new Date());

        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("EEE, HH:mm", Locale.getDefault());
        String currentTime = simpleDateFormat1.format(new Date());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("type", "text");
        hashMap.put("createdOn", currentDate);
        hashMap.put("createdAt", currentTime);
        hashMap.put("sentTimeMills", ServerValue.TIMESTAMP);
        hashMap.put("isSeen", "No");
        reference.child("Chats").push().setValue(hashMap);

        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid()).child(profileid);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    chatRef.child("id").setValue(profileid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final String msg = message;

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (notify) {
                    sendNotification(receiver, user.getUsername(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Token token = dataSnapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, username + ": " + message, "New Message",
                            profileid); //TODO: Change ic_launcher with app logo

                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(ChattingActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessages(final String myid, final String userid, final String imageurl) {
        mChats = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChats.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                        mChats.add(chat);
                    }

                    chatAdapter = new ChatAdapter(ChattingActivity.this, mChats, imageurl, new ChatAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Chat item) {
                            if (item.getType().equals("image")){
                                chatImageShowLayout.setVisibility(View.VISIBLE);
                                chatImageShowLayout.requestFocus();
                                Glide.with(chatImageShowLayout.getContext()).load(item.getMessage()).into(chatImageShowView);
                            }
                        }
                    });
                    recyclerView.setAdapter(chatAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void seenMessage(final String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", "Yes");
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void currentUser(String userid) {
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
        editor.putString("currentUser", userid);
        editor.apply();
    }

    private void status(String status) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            imageUri = data.getData();

            final String sender = firebaseUser.getUid();
            final String receiver = profileid;

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String currentDate = simpleDateFormat.format(new Date());

            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("EEE, HH:mm", Locale.getDefault());
            String currentTime = simpleDateFormat1.format(new Date());

            if (imageUri != null) {
                final StorageReference imageReference = storageReference.child(System.currentTimeMillis()
                        +"."+ getFileExtension(imageUri));

                storageTask = imageReference.putFile(imageUri);
                storageTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return imageReference.getDownloadUrl();
                }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("sender", sender);
                        hashMap.put("receiver", receiver);
                        hashMap.put("message", myUrl);
                        hashMap.put("type", "image");
                        hashMap.put("createdOn", currentDate);
                        hashMap.put("createdAt", currentTime);
                        hashMap.put("sentTimeMills", ServerValue.TIMESTAMP);
                        hashMap.put("isSeen", "No");
                        reference.child("Chats").push().setValue(hashMap);

                        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
                                .child(firebaseUser.getUid()).child(profileid);
                        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.exists()) {
                                    chatRef.child("id").setValue(profileid);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        final String msg = myUrl;

                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        reference1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                if (notify) {
                                    sendNotification(receiver, user.getUsername(), msg);
                                }
                                notify = false;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    } else {
                        Toast.makeText(ChattingActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(ChattingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(ChattingActivity.this, "No image selected!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("Online");
        currentUser(profileid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.removeEventListener(seenListener);
        status("Offline");
        currentUser("none");
    }
}
