package com.zagavideodown.app.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.zagavideodown.app.utils.DownloadFileMain;

import java.io.File;

public class DownloadCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) return;

        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        if (id != DownloadFileMain.downloadID) return;

        scanAfterDelay(context, id);
    }

    private void scanAfterDelay(Context context, long downloadId) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            scanFile(context, downloadId);
        }, 2500); // 🔥 2–3s là ngưỡng ổn định
    }

    private void scanFile(Context context, long downloadId) {
        DownloadManager dm =
                (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterById(downloadId);

        Cursor c = dm.query(q);
        if (c == null) return;

        if (c.moveToFirst()) {
            int index = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
            String path = c.getString(index);

            if (path != null) {
                forceMediaScan(context, path);
            }
        }
        c.close();
    }

    private void forceMediaScan(Context context, String path) {

        // Lần 1 – scan trực tiếp
        MediaScannerConnection.scanFile(
                context,
                new String[]{path},
                null,
                (p, uri) -> Log.e("MediaScanner", "Scanned 1: " + p)
        );

        // Lần 2 – fallback (Samsung hay cần)
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(path)));
        context.sendBroadcast(intent);
    }
}

