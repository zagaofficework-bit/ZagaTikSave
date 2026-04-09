package com.zagavideodown.app.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zagavideodown.app.R;
import com.zagavideodown.app.listener.OnDialogClick;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class LoginDialog extends BottomSheetDialogFragment {

    private final OnDialogClick mListener;

    public LoginDialog(OnDialogClick mListener) {
        this.mListener = mListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_login, container, false);
        view.findViewById(R.id.login_btn).setOnClickListener(v -> {
            dismiss();
            if (mListener != null) {
                mListener.onPositiveClick();
            }
        });
        return view;
    }
}

