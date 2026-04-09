package com.zagavideodown.app.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.view.Window;

import androidx.fragment.app.FragmentActivity;

import com.zagavideodown.app.GlobalConstant;
import com.zagavideodown.app.SharedPreferenceUtils;
import com.zagavideodown.app.dialog.ChooseLanguageDialog;
import com.zagavideodown.app.dialog.DeleteDialog;
import com.zagavideodown.app.dialog.FileActionDialog;
import com.zagavideodown.app.dialog.LoginDialog;
import com.zagavideodown.app.dialog.TutorialDialog;
import com.zagavideodown.app.listener.DeleteListener;
import com.zagavideodown.app.listener.OnDialogClick;

import java.io.File;

public class DialogUtils {

    public static void showDeleteDialog(Context mContext, DeleteListener listener) {
        if (mContext == null) {
            return;
        }
        DeleteDialog dialog = new DeleteDialog(mContext, listener);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        dialog.show();

    }

    public static void showLoginDialog(Activity mContext, OnDialogClick listener) {
        if (mContext == null || mContext.isFinishing()) {
            return; // Không hiển thị dialog nếu mContext là null hoặc activity đã kết thúc
        }

        LoginDialog dialog = new LoginDialog(listener);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    dialog.show(((FragmentActivity) mContext).getSupportFragmentManager(), dialog.getTag());
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void showFileActionDialog(Activity mContext, File mFile, DeleteListener listener) {
        if (mContext == null || mContext.isFinishing()) {
            return; // Không hiển thị dialog nếu mContext là null hoặc activity đã kết thúc
        }

        FileActionDialog dialog = new FileActionDialog(mContext, mFile, listener);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    dialog.show(((FragmentActivity) mContext).getSupportFragmentManager(), dialog.getTag());
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void showLanguageDialog(Activity mContext) {
        if (mContext == null || mContext.isFinishing()) {
            return; // Không hiển thị dialog nếu mContext là null hoặc activity đã kết thúc
        }

        ChooseLanguageDialog dialog = new ChooseLanguageDialog(mContext);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    dialog.show(((FragmentActivity) mContext).getSupportFragmentManager(), dialog.getTag());
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void showTutorialDialog(Context mActivity) {
        TutorialDialog dialog = new TutorialDialog(mActivity);
        Window window = dialog.getWindow();
        assert window != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

    }

    public static void showTutorialFirstTime(Activity mContext) {
        if (SharedPreferenceUtils.getInstance(mContext).getBoolean(GlobalConstant.IS_FIRST_TIME_LAUNCH, true)) {
            TutorialDialog dialog = new TutorialDialog(mContext);
            Window window = dialog.getWindow();
            assert window != null;
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();
            SharedPreferenceUtils.getInstance(mContext).setBoolean(GlobalConstant.IS_FIRST_TIME_LAUNCH, false);
        }
    }
}

