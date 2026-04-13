

package com.zagavideodown.app.utils;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static com.zagavideodown.app.utils.Utils.sanitizeFilename;
import static com.zagavideodown.app.webservice.DownloadVideosMain.dismissMyDialog;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.zagavideodown.app.DataUpdateEvent;
import com.zagavideodown.app.GlobalConstant;
import com.zagavideodown.app.R;
import com.zagavideodown.app.interfaces.DownloadCallback;
import com.zagavideodown.app.receiver.DownloadBroadcastReceiver;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URLConnection;

public class DownloadFileMain {
    public static DownloadManager downloadManager;
    public static long downloadID;

    public static void startDownloading(final Context context, String url, String title, String ext) {

        try {


            DownloadBroadcastReceiver.isFirstTimeDownload = false;

            String cutTitle = sanitizeFilename(title, ext);
            Log.e("cutTitle", cutTitle);
            url = url.replace("\"", "");

            downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);


            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setTitle(title);
            request.setDescription(context.getString(R.string.downloading_des));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            String mime = URLConnection.guessContentTypeFromName(cutTitle);

            try {
                File file_v = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + GlobalConstant.directoryMediaVideos);
                if (!file_v.exists()) {
                    file_v.mkdirs();
                }
                File file_i = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + GlobalConstant.directoryMediaImages);
                if (!file_i.exists()) {
                    file_i.mkdirs();
                }

                File file_a = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + GlobalConstant.directoryMediaAudio);
                if (!file_a.exists()) {
                    file_a.mkdirs();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

            switch (ext) {
                case ".png":
                case ".jpg":
                case ".gif":
                case ".jpeg":
                    request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, GlobalConstant.directoryMediaImages + cutTitle);
                    break;
                case ".mp4":
                case ".webm":
                    request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, GlobalConstant.directoryMediaVideos + cutTitle);
                    break;
                case ".mp3":
                case ".m4a":
                case ".wav":
                    request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, GlobalConstant.directoryMediaImages + cutTitle);
                    break;
            }


            request.allowScanningByMediaScanner();
            downloadID = downloadManager.enqueue(request);
            Log.e("downloadFileName", cutTitle);
            expandNotificationBar(context);
            ((Activity) context).runOnUiThread(() -> Utils.ShowToast(context, context.getResources().getString(R.string.don_start)));


        } catch (Exception e) {
            Log.e("DownloadFileMain", "Error creating folders: " + e.getMessage());

            try {
                ((Activity) context).runOnUiThread(() -> Utils.ShowToastError(context, context.getResources().getString(R.string.error_occ)));
            } catch (Exception e1) {
                Log.e("DownloadFileMain", "Error creating folders: " + e1.getMessage());

            }
        }
    }

    public static void startDownloading(final Context context, String url, String title, String ext, DownloadCallback callback) {
        try {
            Log.i("DownloadFileMain", "Start downloading: " + url + " as: " + ext);
            DownloadBroadcastReceiver.isFirstTimeDownload = false;
            String cutTitle = sanitizeFilename(title, ext);
            Log.i("DownloadFileMain", "cutTitle: " + cutTitle);

            url = url.replace("\"", "");
            downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setTitle(title);
            request.setDescription(context.getString(R.string.downloading_des));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            try {
                Log.i("DownloadFileMain", "Creating directories if not exist");

                File file_v = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + GlobalConstant.directoryMediaVideos);
                if (!file_v.exists()) {
                    file_v.mkdirs();
                }
                File file_i = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + GlobalConstant.directoryMediaImages);
                if (!file_i.exists()) {
                    file_i.mkdirs();
                }

                File file_a = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + GlobalConstant.directoryMediaAudio);
                if (!file_a.exists()) {
                    file_a.mkdirs();
                }
            } catch (IllegalStateException e) {
                Log.e("DownloadFileMain", "Error creating folders: " + e.getMessage());

                e.printStackTrace();
            }

            switch (ext) {
                case ".png":
                case ".jpg":
                case ".gif":
                case ".jpeg":
                    Log.i("DownloadFileMain", "Set destination: image folder");

                    request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, GlobalConstant.directoryMediaImages + cutTitle);
                    break;
                case ".mp4":
                case ".webm":
                    Log.i("DownloadFileMain", "Set destination: video folder");

                    request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, GlobalConstant.directoryMediaVideos + cutTitle);

                    break;
                case ".mp3":
                case ".m4a":
                case ".wav":
                    Log.i("DownloadFileMain", "Set destination: audio folder");

                    request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, GlobalConstant.directoryMediaAudio + cutTitle);
                    break;
            }


            request.allowScanningByMediaScanner();
            EventBus.getDefault().post(new DataUpdateEvent.downloadFinished());

            downloadID = downloadManager.enqueue(request);
            Log.i("DownloadFileMain", "Download enqueued with ID: " + downloadID);
            Log.i("DownloadFileMain", "Download filename: " + cutTitle);
            expandNotificationBar(context);
            ((Activity) context).runOnUiThread(() -> {
                Log.i("DownloadFileMain", "Showing download started toast");
                Utils.ShowToast(context, context.getResources().getString(R.string.don_start));
            });
            dismissMyDialog();

        } catch (Exception e) {
            Log.e("DownloadFileMain", "Exception in startDownloading: " + e.getMessage());

            e.printStackTrace();
            try {

                ((Activity) context).runOnUiThread(() -> {
                    Log.e("DownloadFileMain", "Showing error toast");
                    Utils.ShowToastError(context, context.getResources().getString(R.string.error_occ));
                    callback.onComplete();
                });
                dismissMyDialog();
            } catch (Exception e1) {
                Log.e("DownloadFileMain", "Error while showing error toast: " + e1.getMessage());
                e1.printStackTrace();
                dismissMyDialog();
                callback.onComplete();
            }
        }
    }


    public static void expandNotificationBar(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.EXPAND_STATUS_BAR) != PackageManager.PERMISSION_GRANTED)

            return;
        try {
            Log.i("DownloadFileMain", "Trying to expand notification bar...");

            @SuppressLint("WrongConstant") Object service = context.getSystemService("statusbar");
            Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
            Method expand = statusbarManager.getMethod("expandNotificationsPanel");
            expand.invoke(service);
            Log.i("DownloadFileMain", "Notification bar expanded");

        } catch (Exception e) {
            Log.e("DownloadFileMain", "Failed to expand notification bar: " + e.toString());

            ((Activity) context).runOnUiThread(() ->
                    Utils.ShowToastError(context.getApplicationContext(), "Expansion Not Working")
            );
        }
    }


}
