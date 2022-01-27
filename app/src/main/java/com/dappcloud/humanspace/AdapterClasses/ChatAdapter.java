package com.dappcloud.humanspace.AdapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dappcloud.humanspace.Databases.Chat;
import com.dappcloud.humanspace.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChats;
    private String imageurl;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Chat item);
    }

    private FirebaseUser firebaseUser;

    public ChatAdapter(Context mContext, List<Chat> mChats, String imageurl, OnItemClickListener listener) {
        this.mContext = mContext;
        this.mChats = mChats;
        this.imageurl = imageurl;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_chat_item_right, parent, false);
            return new ChatAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_chat_item_left, parent, false);
            return new ChatAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {

        Chat chat = mChats.get(position);
        try {
            Glide.with(mContext).load(imageurl).into(holder.image_profile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (chat.getType().equals("text")) {
            holder.show_image.setVisibility(View.GONE);
            holder.show_message.setText(chat.getMessage());
        } else {
            holder.show_message.setVisibility(View.GONE);
            try {
                Glide.with(mContext).load(chat.getMessage()).fitCenter().into(holder.show_image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, HH:mm", Locale.getDefault());
        String currentTime = simpleDateFormat.format(new Date());
        long timeCurrent = System.currentTimeMillis();
        long sentTimeMills = chat.getSentTimeMills();
        long lapse = timeCurrent - sentTimeMills;
        long minute = 60000;
        long day = 86400000;
        long twoDays = 172800000;
        long week = 604800000;

        String timeCreated = chat.getCreatedAt().substring(4);
        if (currentTime.equals(chat.getCreatedAt())) {
            holder.text_time.setText("now");
        } else if (lapse > minute / 2 && lapse <= minute) {
            holder.text_time.setText("seconds ago");
        } else if (lapse > minute && lapse <= day) {
            holder.text_time.setText(timeCreated);
        } else if (lapse >= day && lapse < twoDays) {
            holder.text_time.setText("yesterday " + timeCreated);
        } else if (lapse >= twoDays && lapse < week) {
            holder.text_time.setText(chat.getCreatedAt());
        } else if (lapse >= week) {
            holder.text_time.setText(chat.getCreatedAt() + " - " + chat.getCreatedOn());
        }

        String seen = chat.getIsSeen();
        if (seen.equals("No")) {
            holder.text_seen.setText("Sent");
        } else {
            holder.text_seen.setText("Seen");
        }

//        holder.show_image.setOnClickListener(v -> {
//            Toast.makeText(mContext, "Show Image", Toast.LENGTH_SHORT).show();
//        });

        holder.bind(chat, listener);
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView image_profile;
        EmojiconTextView show_message;
        ImageView show_image;
        TextView text_seen;
        TextView text_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            show_message = itemView.findViewById(R.id.show_message);
            show_image = itemView.findViewById(R.id.show_image);
            text_seen = itemView.findViewById(R.id.text_seen);
            text_time = itemView.findViewById(R.id.text_time);
        }

        public void bind(Chat chat, OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(chat);
                }
            });
        }

    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChats.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
