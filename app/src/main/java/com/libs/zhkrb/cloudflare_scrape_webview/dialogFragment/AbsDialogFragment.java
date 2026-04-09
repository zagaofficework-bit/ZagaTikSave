/*
 * *
 *  * Created by Syed Usama Ahmad on 2/27/23, 1:22 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 2/26/23, 11:10 PM
 *
 */

package com.libs.zhkrb.cloudflare_scrape_webview.dialogFragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public abstract class AbsDialogFragment extends DialogFragment {

    protected Activity mContext;
    protected View mRootView;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        Dialog dialog = new Dialog(mContext, getDialogStyle());
        mRootView = LayoutInflater.from(mContext).inflate(getLayoutId(), null);
        dialog.setContentView(mRootView);
        dialog.setCancelable(canCancel());
        dialog.setCanceledOnTouchOutside(canCancel());
        setWindowAttributes(dialog.getWindow());
        return dialog;
    }

    protected abstract void setWindowAttributes(Window window);

    protected abstract boolean canCancel();

    protected abstract int getDialogStyle();

    protected abstract int getLayoutId();

    @SuppressWarnings("deprecation")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        main();
    }

    protected abstract void main();
}
