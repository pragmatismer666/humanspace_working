package com.dappcloud.humanspace.AdapterClasses;

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
import com.dappcloud.humanspace.Databases.Post;
import com.dappcloud.humanspace.Databases.User;
import com.dappcloud.humanspace.R;
import com.dappcloud.humanspace.User.Infrastructure.Activities.CommentsActivity;
import com.dappcloud.humanspace.User.Infrastructure.Activities.UserDashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TextPostAdapter extends RecyclerView.Adapter<TextPostAdapter.ViewHolder> {

    public Context mContext;
    public List<Post> mPost;

    private FirebaseUser firebaseUser;

    public TextPostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_text_item, parent, false);
        return new TextPostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = mPost.get(position);

        long timeCurrent = System.currentTimeMillis();
        long createdAt = post.getCreatedAt();
        long lapse = timeCurrent - createdAt;

        long minute = 60000;
        long hour = 3600000;
        long day = 86400000;
        long twoDays = 172800000;
        long week = 604800000;
        long month = (long) 2.6280036e+12;
        long year = (long) 3.1536036e+12;

        if (lapse < minute) {
            holder.time.setText(" - a few seconds ago"); // less than 1min
        } else if (lapse >= minute && lapse < hour) {
            holder.time.setText(" - a few minutes ago"); // less than 1hour
        } else if (lapse >= hour && lapse < day) {
            holder.time.setText(" - a few hours ago"); // less than 1day
        } else if (lapse >= day && lapse < twoDays) {
            holder.time.setText(" - a day ago"); // less than 48hours
        } else if (lapse >= twoDays && lapse < week) {
            holder.time.setText(" - a few days ago"); // less than 1week
        } else if (lapse >= week && lapse < month) {
            holder.time.setText(" - a few weeks ago"); // less than 1month
        } else if (lapse == month) {
            holder.time.setText(" - a month ago"); // 1month
        } else if (lapse > month && lapse < year) {
            holder.time.setText(" - a few months ago"); //more than 1month
        } else {
            holder.time.setText(" - more than a year"); //more than 1year
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(post.getPublisher());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                holder.name.setText(user.getFullName());
                if (user.getIsVerified().equals("Yes")) {
                    holder.verified.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.post_text.setText(post.getCaption());
        publisherInfo(holder.image_profile, holder.username, post.getPublisher());
        isLiked(post.getPostId(), holder.like);
        likesCount(holder.likes_count, post.getPostId());
        isDisliked(post.getPostId(), holder.dislike);
        dislikesCount(holder.dislikes_count, post.getPostId());
        getComments(post.getPostId(), holder.comments_count);

        holder.like.setOnClickListener(v -> {
            if (holder.like.getTag().equals("like")) {
                FirebaseDatabase.getInstance().getReference().child("PostLikes").child(post.getPostId())
                        .child(firebaseUser.getUid()).setValue(true);
                FirebaseDatabase.getInstance().getReference().child("PostDislikes").child(post.getPostId())
                        .child(firebaseUser.getUid()).removeValue();

                //addNotifications(firebaseUser.getUid());
            } else {
                FirebaseDatabase.getInstance().getReference().child("PostLikes").child(post.getPostId())
                        .child(firebaseUser.getUid()).removeValue();
            }
        });
        holder.dislike.setOnClickListener(v -> {
            if (holder.dislike.getTag().equals("dislike")) {
                FirebaseDatabase.getInstance().getReference().child("PostDislikes").child(post.getPostId())
                        .child(firebaseUser.getUid()).setValue(true);
                FirebaseDatabase.getInstance().getReference().child("PostLikes").child(post.getPostId())
                        .child(firebaseUser.getUid()).removeValue();
            } else {
                FirebaseDatabase.getInstance().getReference().child("PostDislikes").child(post.getPostId())
                        .child(firebaseUser.getUid()).removeValue();
            }
        });
        holder.comment.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, CommentsActivity.class);
            intent.putExtra("postId", post.getPostId());
            intent.putExtra("publisher", post.getPublisher());
            mContext.startActivity(intent);
        });
        holder.comments_count.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, CommentsActivity.class);
            intent.putExtra("postId", post.getPostId());
            intent.putExtra("publisher", post.getPublisher());
            mContext.startActivity(intent);
        });

        holder.image_profile.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, UserDashboardActivity.class);
            intent.putExtra("publisher", post.getPublisher());
            mContext.startActivity(intent);
        });
        holder.username.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, UserDashboardActivity.class);
            intent.putExtra("publisher", post.getPublisher());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name, username, likes_count, dislikes_count, post_text, comments_count, time;
        public ImageView verified, like, dislike, comment;
        public CircleImageView image_profile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            like = itemView.findViewById(R.id.like);
            dislike = itemView.findViewById(R.id.dislike);
            comment = itemView.findViewById(R.id.comment);
            verified = itemView.findViewById(R.id.verified);
            name = itemView.findViewById(R.id.name); //Publisher full name
            username = itemView.findViewById(R.id.username); //publisher
            likes_count = itemView.findViewById(R.id.likes_count);
            dislikes_count = itemView.findViewById(R.id.dislikes_count);
            post_text = itemView.findViewById(R.id.post_text); //caption
            comments_count = itemView.findViewById(R.id.comments_count);
            time = itemView.findViewById(R.id.time);
        }
    }

    private void isLiked(String postid, ImageView imageView) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("PostLikes")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_favorite);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_favorite_border);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void likesCount(TextView likes, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("PostLikes")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount() + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isDisliked(String postid, ImageView imageView) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("PostDislikes")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_thumb_down_cliked);
                    imageView.setTag("disliked");
                } else {
                    imageView.setImageResource(R.drawable.ic_thumb_down);
                    imageView.setTag("dislike");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void dislikesCount(TextView dislikes, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("PostDislikes")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dislikes.setText(snapshot.getChildrenCount() + " dislikes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getComments(String postid, TextView comments) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PostComments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    comments.setText("View All " + snapshot.getChildrenCount() + " comments");
                } else {
                    comments.setText("Comment");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void publisherInfo(CircleImageView image_profile, TextView username, String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                try {
                    Glide.with(mContext).load(user.getImageurl()).into(image_profile);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addNotifications(String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userId", firebaseUser.getUid());
        hashMap.put("text", "liked your space post");

        reference.push().setValue(hashMap);
    }
}
