package com.dappcloud.humanspace.AdapterClasses.PostsAdapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dappcloud.humanspace.Databases.Post;
import com.dappcloud.humanspace.R;
import com.dappcloud.humanspace.User.Infrastructure.Fragments.DisplayUserPostsFromProfileFragment;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MyPostAdapter extends RecyclerView.Adapter<MyPostAdapter.ViewHolder>{

    private Context mContext;
    private List<Post> mPost;

    private FirebaseUser firebaseUser;

    public MyPostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_photo_item, parent, false);
        return new MyPostAdapter.ViewHolder(view);
    }

    public void deletePost(int position) {
        final Post post = mPost.get(position);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Post").child(post.getPostId());
        reference.removeValue();
        Toast.makeText(mContext, "Post Deleted!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Post post = mPost.get(position);
        try {
            Glide.with(mContext).load(post.getPostUrl()).into(holder.post_image);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(v -> {
            SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
            editor.putString("profileid", post.getPublisher());
            editor.putString("postid", post.getPostId());
            editor.apply();

            ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new DisplayUserPostsFromProfileFragment()).addToBackStack(null).commit();
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
