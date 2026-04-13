/*
 * *
 *  * Created by Syed Usama Ahmad on 3/17/23, 11:37 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 3/17/23, 10:13 PM
 *
 */

package com.zagavideodown.app.webservice;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zagavideodown.app.R;
import com.zagavideodown.app.adapter.QualityBottomsheetAdapter;
import com.zagavideodown.app.interfaces.DataCallBackNewJetApi;
import com.zagavideodown.app.interfaces.DownloadCallback;
import com.zagavideodown.app.utils.DownloadFileMain;
import com.zagavideodown.app.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.mapper.VideoFormat;
import com.yausername.youtubedl_android.mapper.VideoInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;


public class DownloadVideosMain {


    public static Activity Mcontext;
    public static ProgressDialog pd;
    public static SharedPreferences prefs;
    public static Boolean fromService;


    static String myURLIS = "";
    static VideoInfo streamInfo;


    public static void Start(final Activity context, String websiteUrl, Boolean service, DownloadCallback callback) {
        try {
            Log.i("Start", "Start() called");

            Mcontext = context;
            fromService = service;
            try {
                websiteUrl = Utils.extractUrls(websiteUrl).get(0);
                Log.i("Start", "URL extracted: " + websiteUrl);

            } catch (Exception ignored) {
                Log.i("Start", "URL extraction failed");

            }

            myURLIS = websiteUrl;
            if (!fromService) {
                pd = new ProgressDialog(context);
                pd.setMessage(Mcontext.getResources().getString(R.string.genarating_download_link));
                pd.setCancelable(false);
                pd.show();
                Log.i("Start", "ProgressDialog shown");

            }
            getDataFromNewJetApi(websiteUrl, result -> {
                Log.i("Start", "getDataFromNewJetApi result: " + result);
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> {
                    if (!result) {
                        if (myURLIS.contains("instagram.com")) {
                            if (!Utils.isNonPlayStoreApp || !Utils.isSocialMediaOn("instagram.com")) {
                                Log.i("Start", "Instagram blocked or disabled");
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            callDlApiNew(myURLIS, callback);

                        } else {
                            Toast.makeText(context, context.getString(R.string.invalid_instagram_link), Toast.LENGTH_SHORT).show();
                            dismissMyDialog();
                        }
                        prefs = Mcontext.getSharedPreferences("AppConfig", MODE_PRIVATE);
                    } else {
                        dismissMyDialog();
                    }

                });
            }, callback);

        } catch (Exception e) {
            Log.e("Start", "Exception in Start(): " + e.getMessage());
            callback.onComplete();
        }
    }


    public static void getDataFromNewJetApi(String videoUrl, DataCallBackNewJetApi callBackNewJetApi, DownloadCallback callback) {
        Log.i("JetApi", "Checking allowed domain for: " + videoUrl);
        if (!NewJetApi.isAllowedDomain(videoUrl)) {
            Log.i("JetApi", "Unsupported domain");
            callBackNewJetApi.onResult(false);
            return;
        }
        Log.i("JetApi", "Domain allowed, sending request...");
        NewJetApi.sendNewJetApiDataRequest(Mcontext, videoUrl, encryptedData -> {
            try {
                if (encryptedData != null) {
                    // Process the response here
                    Log.i("JetApi", "Response received");
                    JSONObject jsonObject = new JSONObject(encryptedData);
                    if (jsonObject.has("error") && jsonObject.getBoolean("error")) {
                        String errorMessage = jsonObject.getString("message");
                        Log.e("JetApi", "API Error: " + errorMessage);

                        throw new IllegalArgumentException(errorMessage);

                    }
                    JSONArray mymedias = jsonObject.getJSONArray("medias");
                    String source = jsonObject.getString("source");
                    String nametitle = source + "_" + jsonObject.getString("author") + "_" +
                            System.currentTimeMillis();
                    // Use Handler to post results to the main thread
                    Handler mainHandler = new Handler(Looper.getMainLooper());

                    Runnable runnable = () -> {
                        boolean isTikTokVideoDownloaded = false;

                        try {
                            for (int i = 0; i < mymedias.length(); i++) {
                                var mediaItem = mymedias.getJSONObject(i);
                                var mediaType = mediaItem.getString("type");
                                var mediaUrl = mediaItem.getString("url");
                                Log.i("JetApi", "Media type: " + mediaType + ", URL: " + mediaUrl);

                                // If it's a TikTok video and one has already been downloaded, skip further downloads
                                if (source.contains("tiktok") && mediaType.equals("video")) {
                                    if (isTikTokVideoDownloaded) {
                                        continue; // Skip this video
                                    }
                                    isTikTokVideoDownloaded = true; // Mark TikTok video as downloaded
                                }


                                switch (mediaType) {
                                    case "video":
                                        DownloadFileMain.startDownloading(Mcontext, mediaUrl, nametitle, ".mp4", callback);
                                        Log.i("JetApi", "Video download started");

                                        break;
//                                    case "audio":
//                                        DownloadFileMain.startDownloading(Mcontext, selectedobj.getString("url"), nametitle, ".mp3");
//                                        break;
                                    case "image":
                                        DownloadFileMain.startDownloading(Mcontext, mediaUrl, nametitle, ".jpg", callback);
                                        Log.i("JetApi", "Image download started");

                                        break;
                                    default:
                                        Log.e("JetApi", "Unknown media type: " + mediaType);

                                        callBackNewJetApi.onResult(false);
                                        return;
                                }
                            }
                            callBackNewJetApi.onResult(true);
                        } catch (Exception e) {
                            Log.e("JetApi", "Exception in media loop: " + e.getMessage());
                            callback.onComplete();
                            callBackNewJetApi.onResult(false);
                        }
                    };

                    mainHandler.post(runnable);
                } else {
                    Log.e("JetApi", "Encrypted data null or failed");

                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(() -> {
                        callBackNewJetApi.onResult(false);
                        Log.i("LOGClipboard111111 clip", "work 17");
                        callback.onComplete();
                        System.err.println("Request failed with code: " + encryptedData);
                    });
                }
            } catch (Exception e) {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> {
                    callback.onComplete();
                    callBackNewJetApi.onResult(false);

                });
            }
        });
    }

    static void callDlApiNew(String url, DownloadCallback callback) {
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Log.i("LOGClipboard111111 clip", "work 7");

                streamInfo = YoutubeDL.getInstance().getInfo(url);
                Mcontext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("LOGClipboard111111 clip", "work 8");

                        parseCalldlApisLocalDataData(streamInfo, url,callback);
                    }
                });

            } catch (Throwable e) {
                Log.i("LOGClipboard111111 clip", "work 9");
                dismissMyDialog();
                callback.onComplete();
            }
        });

    }


    @Keep
    public static void parseCalldlApisLocalDataData(VideoInfo response, String url,DownloadCallback callback) {
        try {
            dismissMyDialog();
            System.out.println("reccccc VVKK " + response.getId());
            View view = LayoutInflater.from(Mcontext).inflate(R.layout.bottomsheet_quality_layout, null);
            String[] dataSize;
            Button btncancel_bottomsheet = view.findViewById(R.id.btncancel_bottomsheet);
            Button btnopen_bottomsheet = view.findViewById(R.id.btnopen_bottomsheet);
            TextView source_bottomsheet = view.findViewById(R.id.source_bottomsheet);
            TextView title_bottomsheet = view.findViewById(R.id.bottomsheet_title);
            TextView duration_bottomsheet = view.findViewById(R.id.bottomsheet_duration);
            ImageView thumb_bottomsheet = view.findViewById(R.id.bottomsheet_thumbnail);
            TextView bottomsheet_description = view.findViewById(R.id.bottomsheet_description);
            LinearLayout linAudios = view.findViewById(R.id.linbbb2);
            LinearLayout linVideos = view.findViewById(R.id.linbbb3);
            RecyclerView recyclerView = view.findViewById(R.id.recqualitybottomsheet);
            recyclerView.setLayoutManager(new LinearLayoutManager(Mcontext));
            RecyclerView recyclerView_audio = view.findViewById(R.id.recqualitybottomsheet_aud);
            recyclerView_audio.setHasFixedSize(true);
            recyclerView_audio.setLayoutManager(new LinearLayoutManager(Mcontext));
            QualityBottomsheetAdapter qualityBottomsheetAdapter = null;
            System.out.println("reccc lengthe iss= " + response.getFormats().size());
            System.out.println("reccccc VVKK 0 ");
            if (!response.getFormats().isEmpty()) {
                callback.onComplete();
                System.out.println("reccccc VVKK 2");
                System.out.println("reccccc VVKK 3 ");
                dataSize = splitDataToVideoAndAudio_dlapiformat(response.getFormats(), recyclerView, recyclerView_audio, qualityBottomsheetAdapter, response.getExtractor(), response.getTitle(), response.getId(), url);

                if (!dataSize[0].equals("y")) {
                    linVideos.setVisibility(View.GONE);
                }

                if (!dataSize[1].equals("y")) {
                    linAudios.setVisibility(View.GONE);
                }
            }

            BottomSheetDialog dialog = new BottomSheetDialog(Mcontext);

            if (!TextUtils.isEmpty(response.getDescription())) {

                String descriptiontext = "Description: " + String.format("%s", response.getDescription());
                bottomsheet_description.setText(Html.fromHtml(descriptiontext), TextView.BufferType.SPANNABLE);
            }
            if (!TextUtils.isEmpty(response.getExtractor())) {
                String styledText = "";

                switch (response.getExtractor()) {
                    case "youtube": {
                        styledText = "Source: <font color='red'>" + "YTD" + "</font>";

                        break;
                    }
                    case "soundcloud": {
                        styledText = "Source: <font color='red'>" + "SCloud" + "</font>";

                        break;
                    }
                    case "facebook": {
                        styledText = "Source: <font color='red'>" + "SocialF" + "</font>";

                        break;
                    }

                    default: {
                        styledText = "Source: <font color='red'>" + response.getExtractor() + "</font>";

                        break;
                    }

                }
                source_bottomsheet.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
            }

            if (response.getDuration() > 0) {

                String mystring = response.getDuration() + "";
                String[] correctstring = mystring.split("\\.");

                long hours = Long.parseLong(correctstring[0]) / 3600;
                long minutes = (Long.parseLong(correctstring[0]) % 3600) / 60;
                long seconds = Long.parseLong(correctstring[0]) % 60;

                String DurationstyledText = "Duration: <font color='red'>" + String.format("%02d:%02d:%02d", hours, minutes, seconds) + "</font>";
                duration_bottomsheet.setText(Html.fromHtml(DurationstyledText), TextView.BufferType.SPANNABLE);
            }

            if (!TextUtils.isEmpty(response.getTitle())) {
                String titletyledText = "Title: <font color='red'>" + String.format("%s", response.getTitle()) + "</font>";
                title_bottomsheet.setText(Html.fromHtml(titletyledText), TextView.BufferType.SPANNABLE);
            }
            if (!TextUtils.isEmpty(response.getThumbnail())) {
                Glide.with(Mcontext).load(response.getThumbnail()).placeholder(R.drawable.ic_appicon).into(thumb_bottomsheet);
            }
            btncancel_bottomsheet.setOnClickListener(v -> dialog.cancel());
            btnopen_bottomsheet.setOnClickListener(v -> {
                if (dialog.getBehavior().getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    dialog.getBehavior().setState(BottomSheetBehavior.STATE_COLLAPSED);
                    dialog.getBehavior().setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
                    btnopen_bottomsheet.animate().rotationBy(180f).start();
                } else {
                    dialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
                    dialog.getBehavior().setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
                    btnopen_bottomsheet.animate().rotationBy(180f).start();
                }
            });
            dialog.setContentView(view);
            dialog.show();
            dismissMyDialog();
        } catch (Exception str2) {
            callback.onComplete();
            dismissMyDialogErrortoast();
        }

    }

    private static String[] splitDataToVideoAndAudio_dlapiformat(ArrayList<VideoFormat> videoList, RecyclerView recyclerView_video, RecyclerView recyclerView_audio, QualityBottomsheetAdapter qualityBottomsheetAdapter, String extractor, String title, String id, String url) {

        List<VideoFormat> videoList_sub = new ArrayList<>();
        List<VideoFormat> videoList_sub_video = new ArrayList<>();
        for (int i = 0; i < videoList.size(); i++) {


            System.out.println("fhnsdkjhffjhsfhsdfjksdfsdhfkjs vifro= " + videoList.get(i).getExt());

            String myext = videoList.get(i).getExt();
            if (myext.equals("m4a") ||
                    myext.equals("mp3") ||
                    myext.equals("wav") || (myext.equals("webm") && !videoList.get(i).getAcodec().equals("none"))) {
                videoList_sub.add(videoList.get(i));
            } else if (myext.equals("mp4") || myext.equals("mpeg") || (myext.equals("webm") && videoList.get(i).getAcodec().equals("none"))) {

                videoList_sub_video.add(videoList.get(i));

            }

        }

        Collections.reverse(videoList_sub_video);

        System.out.println("fhnsdkjhffjhsfhsdfjksdfsdhfkjs cideos list = " + videoList_sub_video.size());
        System.out.println("fhnsdkjhffjhsfhsdfjksdfsdhfkjs audios list = " + videoList_sub.size());

        qualityBottomsheetAdapter = new QualityBottomsheetAdapter(Mcontext, extractor, false, videoList_sub_video, true, title, id, url);
        recyclerView_video.setAdapter(qualityBottomsheetAdapter);

        qualityBottomsheetAdapter = new QualityBottomsheetAdapter(Mcontext, extractor, false, videoList_sub, true, title, id, url);
        recyclerView_audio.setAdapter(qualityBottomsheetAdapter);
        String[] dataSize = {"n", "n"};
        if (!videoList_sub_video.isEmpty()) {
            dataSize[0] = "y";
        }

        if (!videoList_sub.isEmpty()) {
            dataSize[1] = "y";
        }
        return dataSize;
    }

    public static void dismissMyDialog() {
        try {
            if (pd != null && pd.isShowing() && !fromService
                    && Mcontext != null && !Mcontext.isFinishing() && !Mcontext.isDestroyed()) {
                pd.dismiss();
            }
        } catch (Exception e) {
            Log.e("DownloadVideosMain", "dismissMyDialog error: " + e.getMessage());
        }
    }

    public static void dismissMyDialogErrortoast() {
        try {
            if (pd != null && pd.isShowing() && !fromService
                    && Mcontext != null && !Mcontext.isFinishing() && !Mcontext.isDestroyed()) {
                pd.dismiss();
            }
        } catch (Exception e) {
            Log.e("DownloadVideosMain", "dismissMyDialogErrortoast error: " + e.getMessage());
        }
    }

    public static void dismissMyDialogErrorToastForBlockedWebsitePanel() {
        try {
            if (pd != null && pd.isShowing() && !fromService
                    && Mcontext != null && !Mcontext.isFinishing() && !Mcontext.isDestroyed()) {
                pd.dismiss();
                Mcontext.runOnUiThread(() -> {
                    if (!Mcontext.isFinishing() && !Mcontext.isDestroyed()) {
                        Utils.ShowToast(Mcontext,
                                Mcontext.getResources().getString(R.string.something_webiste_panele_block)
                        );
                    }
                });
            }
        } catch (Exception e) {
            Log.e("DownloadVideosMain", "dismissBlocked error: " + e.getMessage());
        }
    }

    public static void download(String url12) {
        String readLine;
        URL url = null;
        try {
            url = new URL(url12);

            Log.d("ThumbnailURL11111_1 ", url12);


            URL url1 = new URL(url12);
            URLConnection connection = url1.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));

            while ((readLine = bufferedReader.readLine()) != null) {
                Log.d("ThumbnailURL11111_2  ", readLine);

                readLine = readLine.substring(readLine.indexOf("VideoObject"));
                String substring = readLine.substring(readLine.indexOf("thumbnailUrl") + 16);
                substring = substring.substring(0, substring.indexOf("\""));

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("ThumbnailURL: ");
                stringBuilder.append(substring);

                Log.d("ThumbnailURL", substring);
                readLine = readLine.substring(readLine.indexOf("contentUrl") + 13);
                readLine = readLine.substring(0, readLine.indexOf("?"));
                stringBuilder = new StringBuilder();
                stringBuilder.append("ContentURL: ");
                stringBuilder.append(readLine);

                Log.d("ContentURL1111 thumb  ", substring);
                Log.d("ContentURL1111", stringBuilder.toString());

                if (readLine == null) {
                    break;
                }
            }
            bufferedReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
