package com.dappcloud.humanspace.AdapterClasses.UserAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dappcloud.humanspace.Databases.User;
import com.dappcloud.humanspace.R;
import com.dappcloud.humanspace.User.Infrastructure.Activities.ChattingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OnlineUsersAdapter extends RecyclerView.Adapter<OnlineUsersAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean isChat;

    private FirebaseUser firebaseUser;

    public OnlineUsersAdapter(Context mContext, List<User> mUsers, boolean isChat) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public OnlineUsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_online_users, parent, false);
        return new OnlineUsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnlineUsersAdapter.ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUsers.get(position);
        holder.username.setText(user.getUsername());
        try {
            Glide.with(mContext).load(user.getImageurl()).into(holder.image_profile);
        } catch (Exception e) {
            e.printStackTrace();
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
        public TextView username, active_text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
        }
    }
}
