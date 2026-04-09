package com.zagavideodown.app.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zagavideodown.app.R;
import com.zagavideodown.app.listener.DeleteListener;
import com.zagavideodown.app.utils.DialogUtils;
import com.zagavideodown.app.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;

public class FileActionDialog extends BottomSheetDialogFragment implements View.OnClickListener {
    private final Context mContext;
    private final File file;
    private final DeleteListener mListener;

    private boolean isVideo;


    public FileActionDialog(Context mContext, File file, DeleteListener mListener) {
        this.mContext = mContext;
        this.file = file;
        this.mListener = mListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_file_action, container, false);
        String extension = file.getName().substring(file.getName().lastIndexOf("."));
        isVideo = extension.equals(".mp4");
        view.findViewById(R.id.btnRepost).setOnClickListener(this);
        view.findViewById(R.id.btnShare).setOnClickListener(this);
        view.findViewById(R.id.btnDeleteFile).setOnClickListener(this);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        int idView = v.getId();
        if (idView == R.id.btnRepost) {
            if (isVideo) {
                Utils.repostVideo(mContext, file.getPath());
            } else {
                Utils.repostImage(mContext, file.getPath());
            }
            dismiss();
        } else if (idView == R.id.btnShare) {
            if (isVideo) {
                Utils.shareVideo(mContext, file.getPath());
            } else {
                Utils.shareImage(mContext, file.getPath());
            }
            dismiss();
        } else if (idView == R.id.btnDeleteFile) {
            DialogUtils.showDeleteDialog(mContext, () -> {
                if (mListener != null) {
                    mListener.onDelete();
                }
            });

            dismiss();
        }

    }
}

