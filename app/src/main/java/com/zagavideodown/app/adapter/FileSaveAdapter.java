package com.zagavideodown.app.adapter;

import android.app.Activity;
import android.media.MediaScannerConnection;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zagavideodown.app.R;
import com.zagavideodown.app.interfaces.FileClickListener;
import com.zagavideodown.app.utils.DialogUtils;

import java.io.File;
import java.util.ArrayList;

public class FileSaveAdapter extends RecyclerView.Adapter<FileSaveAdapter.ViewHolder> {
    private final Activity mContext;
    private final ArrayList<File> fileArrayList;
    private final FileClickListener mListener;

    public FileSaveAdapter(Activity mContext, ArrayList<File> fileArrayList, FileClickListener mListener) {
        this.mContext = mContext;
        this.fileArrayList = fileArrayList;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_save, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        File file = fileArrayList.get(position);
        try {
            String extension = file.getName().substring(file.getName().lastIndexOf("."));
            if (extension.equals(".mp4")) {
                holder.ivPlay.setVisibility(View.VISIBLE);
            } else {
                holder.ivPlay.setVisibility(View.GONE);
            }
            Glide.with(mContext).load(file.getPath()).into(holder.ivThumbnail);

        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.rlMain.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.getPosition(holder.getAdapterPosition(), file);
            }
        });
        holder.rlMain.setOnLongClickListener(view -> {
            DialogUtils.showFileActionDialog(mContext, file, () -> {
                File fileDelete = new File(file.getAbsolutePath());
                fileDelete.delete();
                MediaScannerConnection.scanFile(mContext, new String[]{fileDelete.getAbsolutePath()}, null, null);
                fileArrayList.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(), fileArrayList.size());
            });
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return fileArrayList == null ? 0 : fileArrayList.size();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout rlMain;
        private final ImageView ivPlay;
        private final ImageView ivThumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rlMain = itemView.findViewById(R.id.rl_main);
            ivPlay = itemView.findViewById(R.id.iv_play);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
        }
    }
}

