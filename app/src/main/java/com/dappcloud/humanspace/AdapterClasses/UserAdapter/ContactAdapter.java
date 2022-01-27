package com.dappcloud.humanspace.AdapterClasses.UserAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dappcloud.humanspace.Databases.Chat;
import com.dappcloud.humanspace.Databases.User;
import com.dappcloud.humanspace.R;
import com.dappcloud.humanspace.User.Infrastructure.Activities.ChattingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean isChat;

    String theLastMessage, theLastMessageTime;

    private FirebaseUser firebaseUser;

    public ContactAdapter(Context mContext, List<User> mUsers, boolean isChat) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_contacts_item, parent, false);
        return new ContactAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUsers.get(position);
        holder.username.setText(user.getUsername());
        try {
            Glide.with(mContext).load(user.getImageurl()).into(holder.image_profile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        unreadChats(user.getUserId(), holder.unread);

        if (user.getIsVerified().equals("Yes")) {
            holder.verified.setVisibility(View.VISIBLE);
        }

        if (isChat) {
            if (user.getStatus().equals("Online")) {
                holder.online_user.setVisibility(View.VISIBLE);
                lastMessage(user.getUserId(), holder.active_text);
                lastMessageTime(user.getUserId(), holder.time);
            } else {
                holder.online_user.setVisibility(View.GONE);
                lastMessage(user.getUserId(), holder.active_text);
                lastMessageTime(user.getUserId(), holder.time);
            }
        } else {
            holder.unread.setVisibility(View.GONE);
            holder.online_user.setVisibility(View.GONE);
            holder.active_text.setText(user.getFullName());
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ChattingActivity.class);
            intent.putExtra("profileid", user.getUserId());
            mContext.startActivity(intent);
        });
    }

    @Override

    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView image_profile;
        private ImageView online_user, verified;
        public TextView username;
        private TextView active_text, time;
        private TextView unread;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            online_user = itemView.findViewById(R.id.online_user);
            verified = itemView.findViewById(R.id.verified);
            username = itemView.findViewById(R.id.username);
            active_text = itemView.findViewById(R.id.active_text);
            time = itemView.findViewById(R.id.time);
            unread = itemView.findViewById(R.id.unread);
        }
    }

    private void lastMessage(final String userid, final TextView last_msg) {
        theLastMessage = "default";

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {

                        if (chat.getType().equals("text")) {
                            theLastMessage = chat.getMessage();
                        } else {
                            theLastMessage = "sent an image";
                        }
                    }
                }

                switch (theLastMessage) {
                    case "default":
                        last_msg.setText("No Message");
                        break;
                    default:
                        last_msg.setText(theLastMessage);
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void lastMessageTime(final String userid, final TextView last_msg_time) {
        theLastMessageTime = "default";

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    String showTime = "";
                    String timeCreated = chat.getCreatedAt().substring(4);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, HH:mm", Locale.getDefault());
                    String currentTime = simpleDateFormat.format(new Date());
                    long timeCurrent = System.currentTimeMillis();
                    long sentTimeMills = chat.getSentTimeMills();
                    long lapse = timeCurrent - sentTimeMills;
                    long minute = 60000;
                    long day = 86400000;
                    long twoDays = 172800000;
                    long week = 604800000;

                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {

                        if (currentTime.equals(chat.getCreatedAt())) {
                            showTime = "now";
                        } else if (lapse > minute/2 && lapse <= minute) {
                            showTime = "seconds ago";
                        } else if (lapse > minute && lapse <= day) {
                            showTime = "today " + timeCreated;
                        } else if (lapse >= day && lapse < twoDays) {
                            showTime = "yesterday " + timeCreated;
                        } else if (lapse >= twoDays && lapse < week) {
                            showTime = chat.getCreatedAt();
                        } else if (lapse >= week) {
                            showTime = chat.getCreatedOn();
                        }

                        if (chat.getType().equals("text")) {
                            theLastMessageTime = showTime;
                        } else {
                            theLastMessageTime = showTime;
                        }
                    }
                }

                switch (theLastMessageTime) {
                    case "default":
                        last_msg_time.setText("No Message");
                        break;
                    default:
                        last_msg_time.setText(theLastMessageTime);
                        break;
                }

                theLastMessageTime = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void unreadChats(final String userid, final TextView isSeen) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int unread = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)) {
                        if (chat.getIsSeen().equals("No")) {
                            unread++;
                        }
                    }
                }
                if (unread > 0) {
                    isSeen.setVisibility(View.VISIBLE);
                    isSeen.setText(Integer.toString(unread));
                } else {
                    isSeen.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
