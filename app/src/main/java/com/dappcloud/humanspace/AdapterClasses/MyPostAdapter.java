package com.dappcloud.humanspace.AdapterClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dappcloud.humanspace.Databases.Post;
import com.dappcloud.humanspace.R;

import java.util.List;

public class MyPostAdapter extends RecyclerView.Adapter<MyPostAdapter.ViewHolder>{

    public interface OnItemClickListener {
        void onItemClick(Post item);
    }

    private Context mContext;
    private List<Post> mPost;
    private final OnItemClickListener listener;

    public MyPostAdapter(Context mContext, List<Post> mPost, OnItemClickListener listener) {
        this.mContext = mContext;
        this.mPost = mPost;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_photo_item, parent, false);
        return new MyPostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Post post = mPost.get(position);
        try {
            Glide.with(mContext).load(post.getPostUrl()).into(holder.post_image);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.bind(mPost.get(position), listener);
        // holder.itemView.setOnClickListener(v -> {});
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

        public void bind(Post post, OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(post);
                }
            });
        }
    }
}
