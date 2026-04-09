package com.zagavideodown.app.adapter;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zagavideodown.app.R;
import com.zagavideodown.app.activities.ViewFileActivity;
import com.zagavideodown.app.listener.OnDeleteListener;
import com.zagavideodown.app.utils.DialogUtils;
import com.zagavideodown.app.utils.Utils;
import com.zagavideodown.app.views.VideoViewCustom;

import java.util.ArrayList;

public class ViewFileAdapter extends RecyclerView.Adapter<ViewFileAdapter.ViewHolder> {
    private final ViewFileActivity mContext;
    private final ArrayList<String> storyList;
    private final OnDeleteListener mListener;
    private boolean isComplete;
    private final Handler mHandler = new Handler();

    public ViewFileAdapter(ViewFileActivity mContext, ArrayList<String> storyList, OnDeleteListener mListener) {
        this.mContext = mContext;
        this.storyList = storyList;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_file, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String extension = storyList.get(position).substring(storyList.get(position).lastIndexOf("."));
        String filePath = storyList.get(position);
        if (extension.equals(".mp4")) {
            holder.btnPlay.setVisibility(View.VISIBLE);
            holder.imgPhoto.setVisibility(View.GONE);
            holder.playerView.setVisibility(View.VISIBLE);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (!isComplete) {
                        mHandler.postDelayed(this, 100);
                    }
                }
            };
            holder.playerView.setVideoPath(filePath);
            holder.playerView.setOnClickListener(view -> {
                if (holder.playerView.isPlaying()) {
                    holder.playerView.pause();
                    return;
                }
                holder.playerView.start();
            });
            holder.btnPlay.setOnClickListener(view -> {
                if (holder.playerView.isPlaying()) {
                    holder.playerView.pause();
                    return;
                }
                holder.playerView.start();
            });
            holder.playerView.setOnPreparedListener(mediaPlayer -> {
                mediaPlayer.start();
                holder.btnPlay.setVisibility(View.INVISIBLE);
            });
            holder.playerView.setOnCompletionListener(mediaPlayer -> {
                isComplete = true;
                mHandler.removeCallbacks(runnable);
            });
            holder.playerView.setOnPlayPauseListener(new VideoViewCustom.PlayPauseListener() {
                @Override
                public void onVideoPause() {
                    mHandler.removeCallbacks(runnable);
                    Animation animation = new AlphaAnimation(0.0f, 1.0f);
                    animation.setDuration(500);
                    animation.setFillAfter(true);
                    holder.btnPlay.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            holder.btnPlay.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }

                @Override
                public void onVideoPlay() {
                    Animation animation = new AlphaAnimation(1.0f, 0.0f);
                    animation.setDuration(500);
                    animation.setFillAfter(true);
                    holder.btnPlay.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            holder.btnPlay.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            holder.btnPlay.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }
            });
        } else {
            holder.btnPlay.setVisibility(View.GONE);
            holder.playerView.setVisibility(View.GONE);
            holder.imgPhoto.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(filePath).into(holder.imgPhoto);

        }
        holder.imgShareImage.setOnClickListener(view -> {
            if (extension.equals(".mp4")) {
                Utils.shareVideo(mContext, filePath);
            } else {
                Utils.shareImage(mContext, filePath);
            }
        });
        holder.imgRepost.setOnClickListener(v -> {
            if (extension.equals(".mp4")) {
                Utils.repostVideo(mContext, filePath);
            } else {
                Utils.repostImage(mContext, filePath);
            }
        });
        holder.imgDelete.setOnClickListener(view -> DialogUtils.showDeleteDialog(mContext, () -> {
            if (mListener != null) {
                mListener.onDelete(holder.getAdapterPosition(), filePath);
            }

        }));
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPhoto;
        ImageView imgShareImage;
        ImageView imgDelete;
        ImageView imgRepost;
        ImageView btnPlay;
        VideoViewCustom playerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.photoView);
            playerView = itemView.findViewById(R.id.playerView);
            imgShareImage = itemView.findViewById(R.id.ivShare);
            imgRepost = itemView.findViewById(R.id.ivRepost);
            imgDelete = itemView.findViewById(R.id.ivDelete);
            btnPlay = itemView.findViewById(R.id.btnPlay);
        }
    }
}

