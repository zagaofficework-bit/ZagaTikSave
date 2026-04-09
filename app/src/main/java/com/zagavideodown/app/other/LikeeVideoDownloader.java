/*
 * *
 *  * Created by Syed Usama Ahmad on 2/23/23, 12:24 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 2/22/23, 10:43 PM
 *
 */

package com.zagavideodown.app.other;


import static com.zagavideodown.app.GlobalConstant.MY_ANDROID_10_IDENTIFIER_OF_FILE;
import static com.zagavideodown.app.other.DownloadVideosMain2.fromService;
import static com.zagavideodown.app.other.DownloadVideosMain2.pd;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.zagavideodown.app.GlobalConstant;
import com.zagavideodown.app.R;
import com.zagavideodown.app.listener.VideoDownloader;
import com.zagavideodown.app.utils.Utils;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LikeeVideoDownloader implements VideoDownloader {
    private static final Pattern a = Pattern.compile("window\\._INIT_PROPS_\\s*=\\s*(\\{.+\\})");
    String VideoUrl;
    Pattern pattern = Pattern.compile("window\\.data \\s*=\\s*(\\{.+?\\});");
    ProgressDialog progressDialog;
    AsyncTask downloadAsyncTask;
    private Activity context;
    private String VideoURL;
    private String VideoTitle;

    public LikeeVideoDownloader(Activity context, String videoURL) {
        this.context = context;
        VideoURL = videoURL;
    }

    private static int ordinalIndexOf(String str, String substr, int n) {
        int pos = -1;
        do {
            pos = str.indexOf(substr, pos + 1);
        } while (n-- > 0 && pos != -1);
        return pos;
    }

    public static void down(String str) {
        String obj = "obj";
        String str2 = "";

        try {
            Matcher str1 = a.matcher(str);
            while (str1.find()) {
                JSONObject jSONObject = new JSONObject(str.replaceAll("window\\._INIT_PROPS_\\s*=\\s*", str2).replace("/v/:id", obj)).getJSONObject(obj).getJSONObject("videoData");
                JSONObject jSONObject2 = jSONObject.getJSONObject("itemInfos");
                jSONObject.getJSONObject("authorInfos");
                String obj2 = jSONObject2.getJSONObject("video").getJSONArray("urls").get(0).toString();
                String obj3 = jSONObject2.getJSONArray("covers").get(0).toString();

            }
        } catch (Exception e2) {
            e2.printStackTrace();

        }

    }

    public static String a() {
        return a(new int[]{103896, 115884, 115884, 111888, 114885, 57942, 46953, 46953, 118881, 118881, 118881, 45954, 106893, 96903, 115884, 96903, 113886, 108891, 96903, 107892, 45954, 104895, 109890, 46953, 117882, 48951, 46953, 111888, 113886, 110889, 98901, 100899, 114885, 114885, 84915, 113886, 107892});
    }

    public static String a(int[] iArr) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i : iArr) {
            stringBuilder.append((char) (i / 999));
        }
        return stringBuilder.toString();
    }

    public static String b() {
        return a(new int[]{103896, 115884, 115884, 111888, 114885, 57942, 46953, 46953, 96903, 111888, 104895, 45954, 99900, 110889, 118881, 109890, 107892, 110889, 96903, 99900, 115884, 104895, 106893, 115884, 110889, 106893, 117882, 104895, 99900, 100899, 110889, 114885, 45954, 98901, 110889, 108891, 46953, 96903, 111888, 104895, 94905, 111888, 110889, 114885, 115884, 46953, 104895, 109890, 99900, 100899, 119880, 45954, 111888, 103896, 111888});
    }


    @Override
    public String getVideoId(String link) {
        if (!link.contains("https")) {
            link = link.replace("http", "https");
        }
        return link;
    }

    @Override
    public void DownloadVideo() {
//        new callGetLikeeData().execute(getVideoId(VideoURL));
        new callGetLikeeData().execute(VideoURL);
    }


    class callGetLikeeData extends AsyncTask<String, Void, String> {
        String likeeDoc;

        callGetLikeeData() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... strArr) {
            try {
//                this.likeeDoc = Jsoup.connect(strArr[0])
//                        .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36")
//                        .get();


                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                Request request = new Request.Builder()
                        .url(strArr[0])
                        .get()
                        .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                        .addHeader("Accept-Encoding", "gzip, deflate, br")
                        .addHeader("Accept-Language", "en-US,en;q=0.9")
                        .addHeader("Connection", "keep-alive")
                        .addHeader("Host", "likee.video")
                        .addHeader("Upgrade-Insecure-Requests", "1")
                        .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 9; ASUS_Z01QD Build/PI) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.70 Mobile Safari/537.36")
                        .build();
                Response response = client.newCall(request).execute();
                this.likeeDoc = response.body().string();

            } catch (Exception strArr2) {
                strArr2.printStackTrace();
                Log.d("ContentValues", "doInBackground: Error");
            }
            return this.likeeDoc;
        }

        protected void onPostExecute(String document) {

            try {

                String JSONData = "";
                Pattern pattern2 = Pattern.compile("window\\.data\\s*=\\s*(\\{.+?\\});\\s*$");

                Matcher matcher = pattern2.matcher(document.toString());
                while (matcher.find()) {
                    JSONData = matcher.group().replaceFirst("window.data = ", "").replace(";", "");
                }
                Log.e("onPostExecutenew14251: JSONData ", JSONData);


//                JSONObject jsonObject = new JSONObject(JSONData);
//                VideoUrl = jsonObject.getString("video_url").replace("_4", "");
//                //    VideoUrl = VideoUrl.substring(0, VideoUrl.indexOf("{"));
//
//                Log.e("onPostExecutenew14251: ", VideoUrl);
//
//                if (!VideoUrl.equals("")) {
//                    try {
//                        pd.setMessage(context.getResources().getString(R.string.downloading_des));
//                        downloadAsyncTask = new DownloadFileFromURL().execute(VideoUrl);
//
//                        VideoUrl = "";
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        if (!fromService) {
//                            pd.dismiss();
//                        }
//                    }
//                } else {
//                    if (!fromService) {
//                        pd.dismiss();
//                    }
//                    Log.e("onPostExecuteEonWrong", "Wrong url");
//                }
            } catch (Exception document22) {
                if (!fromService) {
                    pd.dismiss();
                }
                document22.printStackTrace();
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                int lenghtOfFile = conection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                OutputStream output;

                Log.e("onPostExecuteOUTPUT0", "" + VideoUrl);

                String outputFileName = MY_ANDROID_10_IDENTIFIER_OF_FILE + Utils.getFilenameFromURL() + ".mp4";


                if (outputFileName.length() > 100)
                    outputFileName = outputFileName.substring(0, 100);


                output = new BufferedOutputStream(new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + GlobalConstant.directoryInstaShoryDirectorydownload_videos + outputFileName));

                byte[] data = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();

            } catch (Exception e) {
                if (!fromService) {
                    pd.dismiss();
                }
                Log.e("Error111: ", e.getMessage());
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {
            pd.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String file_url) {

            if (!fromService) {
                pd.dismiss();
            }
            Utils.ShowToast(context, context.getResources().getString(R.string.yourdoncomple));
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (!fromService) {
                pd.dismiss();
            }

        }
    }

}

