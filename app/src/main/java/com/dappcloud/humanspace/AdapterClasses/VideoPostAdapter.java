package com.dappcloud.humanspace.AdapterClasses;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.dappcloud.humanspace.Databases.Post;
import com.dappcloud.humanspace.R;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class VideoPostAdapter extends RecyclerView.Adapter<VideoPostAdapter.VideoViewHolder> {

    public Context mContext;
    public List<Post> mPost;

    private FirebaseUser firebaseUser;

    public VideoPostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_videos_cotainer_item, parent, false);
        return new VideoPostAdapter.VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.setVideoData(mPost.get(position));
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        VideoView post;
        TextView caption, publisher;
        LottieAnimationView progressBar;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);

            post = itemView.findViewById(R.id.post);
            caption = itemView.findViewById(R.id.caption);
            publisher = itemView.findViewById(R.id.publisher);
            progressBar = itemView.findViewById(R.id.progressBar);
        }

        void setVideoData(Post postItem) {
            publisher.setText(postItem.getPublisher());
            caption.setText(postItem.getCaption());
            post.setOnPreparedListener(mp -> {
                progressBar.setVisibility(View.GONE);
                mp.start();

                float videoRatio = mp.getVideoWidth() / (float) mp.getVideoHeight();
                float screenRatio = post.getWidth() / (float) post.getHeight();
                float scale = videoRatio / screenRatio;
                if (scale >= 1f) {
                    post.setScaleX(scale);
                }
                else {
                    post.setScaleY(1f / scale);
                }
            });

            post.setOnCompletionListener(mp -> mp.start());

        }
    }

}
