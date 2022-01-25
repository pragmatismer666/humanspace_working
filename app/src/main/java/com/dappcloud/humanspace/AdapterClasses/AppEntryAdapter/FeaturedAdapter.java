package com.dappcloud.humanspace.AdapterClasses.AppEntryAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dappcloud.humanspace.Databases.User;
import com.dappcloud.humanspace.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;

    private FirebaseUser firebaseUser;

    public FeaturedAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_global_featured_card, parent, false);
        return new FeaturedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUsers.get(position);

        holder.username.setText(user.getUsername());
        holder.gender.setText(user.getGender());
        holder.profession.setText(user.getProfession());
        Glide.with(mContext).load(user.getImageurl()).into(holder.image_profile);
        if (user.getIsVerified().equals("Yes")) {
            holder.verified.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> Toast.makeText(mContext, "Open User Profile", Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile, verified;
        public TextView username, gender, profession;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            verified = itemView.findViewById(R.id.verified);
            username = itemView.findViewById(R.id.username);
            gender = itemView.findViewById(R.id.gender);
            profession = itemView.findViewById(R.id.profession);
        }
    }

}

