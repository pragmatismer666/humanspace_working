package com.dappcloud.humanspace.AdapterClasses;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dappcloud.humanspace.Databases.Story;
import com.dappcloud.humanspace.Databases.User;
import com.dappcloud.humanspace.R;
import com.dappcloud.humanspace.User.Infrastructure.Activities.DisplayStoryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryCloseFreindsAdapter extends RecyclerView.Adapter<StoryCloseFreindsAdapter.ViewHolder> {

    private Context mContext;
    private List<Story> mStory;

    private FirebaseUser firebaseUser;

    public StoryCloseFreindsAdapter(Context mContext, List<Story> mStory) {
        this.mContext = mContext;
        this.mStory = mStory;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_story_close_friends_item, parent, false);
        return new StoryCloseFreindsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Story story = mStory.get(position);
        try {
            Glide.with(mContext).load(story.getImageurl()).into(holder.story);
        } catch (Exception e) {
            e.printStackTrace();
        }

        userInfo(holder, story.getUserId());
        seenStory(holder, story.getUserId());

        holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, DisplayStoryActivity.class);
                intent.putExtra("userId", story.getUserId());
                mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mStory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView storyCard;
        ImageView story;
        CircleImageView story_photo, story_photo_seen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            story = itemView.findViewById(R.id.story);
            story_photo = itemView.findViewById(R.id.story_photo);
            story_photo_seen = itemView.findViewById(R.id.story_photo_seen);
            storyCard = itemView.findViewById(R.id.storyCard);
        }
    }

    private void userInfo(final StoryCloseFreindsAdapter.ViewHolder holder, final String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                try {
                    Glide.with(mContext).load(user.getImageurl()).into(holder.story_photo);
                    Glide.with(mContext).load(user.getImageurl()).into(holder.story_photo_seen);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void seenStory(final StoryCloseFreindsAdapter.ViewHolder holder, String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (!dataSnapshot.child("views")
                            .child(firebaseUser.getUid())
                            .exists() && System.currentTimeMillis() < dataSnapshot.getValue(Story.class).getTimeend()) {
                        i++;
                    }
                }

                if (i > 0) {
                    holder.story_photo.setVisibility(View.VISIBLE);
                    holder.story_photo_seen.setVisibility(View.GONE);
                } else {
                    holder.story_photo.setVisibility(View.GONE);
                    holder.story_photo_seen.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
