package com.dappcloud.humanspace.AdapterClasses.PostsAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dappcloud.humanspace.Databases.Post;
import com.dappcloud.humanspace.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MyVideoPostAdapter extends RecyclerView.Adapter<MyVideoPostAdapter.ViewHolder>{

    private Context mContext;
    private List<Post> mPost;

    private FirebaseUser firebaseUser;

    public MyVideoPostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public MyVideoPostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_photo_item, parent, false);
        return new MyVideoPostAdapter.ViewHolder(view);
    }

    public void deletePost(int position) {
        final Post post = mPost.get(position);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Post").child(post.getPostId());
        reference.removeValue();
        Toast.makeText(mContext, "Post Deleted!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBindViewHolder(@NonNull MyVideoPostAdapter.ViewHolder holder, int position) {
        Post post = mPost.get(position);
        try {
            Glide.with(mContext).load(post.getPostUrl()).into(holder.post_image);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView post_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            post_image = itemView.findViewById(R.id.post_image);
        }
    }
}
