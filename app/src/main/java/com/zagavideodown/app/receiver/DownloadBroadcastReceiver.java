package com.zagavideodown.app.receiver;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.zagavideodown.app.DataUpdateEvent;
import com.zagavideodown.app.R;
import com.zagavideodown.app.utils.Utils;

import org.greenrobot.eventbus.EventBus;

public class DownloadBroadcastReceiver extends BroadcastReceiver {
    public static boolean isFirstTimeDownload = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadId == -1)
                return;
            Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(downloadId));
            if (cursor.moveToFirst()) {
                @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status == DownloadManager.STATUS_SUCCESSFUL) {

                    if (!isFirstTimeDownload) {
                        DownloadBroadcastReceiver.isFirstTimeDownload = true;
                        Utils.sendOreoNotification(context, context.getResources().getString(R.string.app_name), context.getResources().getString(R.string.yourdoncomple));
                    }
                    EventBus.getDefault().post(new DataUpdateEvent.downloadFinished());
                } else {
                    Log.d("error", "error");
                }
            } else {
                Log.d("error", "error");
            }
            cursor.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}

