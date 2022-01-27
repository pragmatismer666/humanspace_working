package com.dappcloud.humanspace.AdapterClasses;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dappcloud.humanspace.Databases.Story;
import com.dappcloud.humanspace.Databases.User;
import com.dappcloud.humanspace.R;
import com.dappcloud.humanspace.User.Infrastructure.Activities.AddStoryActivity;
import com.dappcloud.humanspace.User.Infrastructure.Activities.DisplayStoryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class StoryAllAdapter extends RecyclerView.Adapter<StoryAllAdapter.ViewHolder> {

    private Context mContext;
    private List<Story> mStory;

    private FirebaseUser firebaseUser;

    public StoryAllAdapter(Context mContext, List<Story> mStory) {
        this.mContext = mContext;
        this.mStory = mStory;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_add_story_item, parent, false);
            return new StoryAllAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_story_item, parent, false);
            return new StoryAllAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Story story = mStory.get(position);

        userInfo(holder, story.getUserId(), position);

        if (holder.getAdapterPosition() != 0 ) {
            seenStory(holder, story.getUserId());
        }

        if (holder.getAdapterPosition() == 0) {
            myStory(holder.add_story_text, holder.add_story_photo, false);
        }

        holder.itemView.setOnClickListener(v -> {
            if (holder.getAdapterPosition() == 0) {
                myStory(holder.add_story_text, holder.add_story_photo, true);
            } else {
                Intent intent = new Intent(mContext, DisplayStoryActivity.class);
                intent.putExtra("userId", story.getUserId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView story_photo, story_photo_seen;
        ImageView add_story_photo;
        TextView story_username, add_story_text;
        LinearLayout story;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            story_photo = itemView.findViewById(R.id.story_photo);
            story_photo_seen = itemView.findViewById(R.id.story_photo_seen);
            add_story_photo = itemView.findViewById(R.id.add_story_photo);
            story_username = itemView.findViewById(R.id.story_username);
            add_story_text = itemView.findViewById(R.id.add_story_text);
            story = itemView.findViewById(R.id.story);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        }
        return 1;
    }

    private void userInfo(final ViewHolder holder, final String userId, final int position) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                try {
                    Glide.with(mContext).load(user.getImageurl()).into(holder.story_photo);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (position != 0) {
                    try {
                        Glide.with(mContext).load(user.getImageurl()).into(holder.story_photo_seen);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    holder.story_username.setText(user.getUsername());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void myStory(final TextView textView, final ImageView imageView, final boolean click) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(firebaseUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                long timecurrent = System.currentTimeMillis();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Story story = dataSnapshot.getValue(Story.class);
                    if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
                        count++;
                    }
                }

                if (click) {
                    if (count > 0) {
                        Intent intent = new Intent(mContext, DisplayStoryActivity.class);
                        intent.putExtra("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        mContext.startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, AddStoryActivity.class);
                        mContext.startActivity(intent);
                    }

                } else {
                    if (count > 0) {
                        textView.setText("My Status");
                        imageView.setVisibility(View.GONE);
                    } else {
                        textView.setText("Add Status");
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void seenStory(final ViewHolder holder, String userId) {
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
