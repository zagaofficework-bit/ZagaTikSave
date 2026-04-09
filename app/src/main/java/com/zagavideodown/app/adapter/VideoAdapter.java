package com.zagavideodown.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zagavideodown.app.R;
import com.zagavideodown.app.utils.Utils;

import java.io.File;
import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private final Context mContext;
    private final ArrayList<File> fileArrayList;


    public VideoAdapter(Context mContext, ArrayList<File> fileArrayList) {
        this.mContext = mContext;
        this.fileArrayList = fileArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        File file = fileArrayList.get(position);
        Glide.with(mContext).load(file.getPath()).into(holder.ivThumbnail);
        holder.tvFileSize.setText(Utils.getFileSize(file));
        holder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.shareVideo(mContext, file.getPath());
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                file.delete();
                MediaScannerConnection.scanFile(mContext, new String[]{file.getPath()}, null, null);
                fileArrayList.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(), fileArrayList.size());
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(String.valueOf(file)), "video/mp4");
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileArrayList == null ? 0 : fileArrayList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivThumbnail;
        private final TextView tvFileSize;
        private final ImageView btnShare;
        private final ImageView btnDelete;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            tvFileSize = itemView.findViewById(R.id.tvSizeFile);
            btnShare = itemView.findViewById(R.id.btnShare);
        }
    }
}

