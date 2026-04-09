package com.zagavideodown.app.dialog;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

import com.zagavideodown.app.R;
import com.zagavideodown.app.listener.DeleteListener;

public class DeleteDialog extends Dialog {

    private final DeleteListener mListener;


    public DeleteDialog(@NonNull Context context, DeleteListener listener) {
        super(context);
        setContentView(R.layout.dialog_delete_file);
        this.mListener = listener;
        findViewById(R.id.tv_bt_negative).setOnClickListener(v -> dismiss());
        findViewById(R.id.tv_bt_positive).setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onDelete();
                dismiss();
            }
        });
    }
}

