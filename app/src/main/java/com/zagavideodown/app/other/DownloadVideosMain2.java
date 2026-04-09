package com.zagavideodown.app.other;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.WINDOW_SERVICE;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.zagavideodown.app.GlobalConstant;
import com.zagavideodown.app.R;
import com.zagavideodown.app.interfaces.DataCallBackNewJetApi;
import com.zagavideodown.app.models.dlapismodels.DLDataParser;
import com.zagavideodown.app.models.dlapismodels.Format;
import com.zagavideodown.app.models.dlapismodels.Video;
import com.zagavideodown.app.other.adapter.QualityBottomsheetAdapter;
import com.zagavideodown.app.other.snapchat.SnapChatBulkStoryDownloader;
import com.zagavideodown.app.utils.DownloadFileMain;
import com.zagavideodown.app.utils.Utils;
import com.zagavideodown.app.webservice.NewJetApi;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.libs.htetznaing.lowcostvideo.LowCostVideo;
import com.libs.htetznaing.lowcostvideo.Model.XModel;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.mapper.VideoFormat;
import com.yausername.youtubedl_android.mapper.VideoInfo;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class DownloadVideosMain2 {
    public static Activity Mcontext;
    public static ProgressDialog pd;
    public static Dialog dialog;
    public static SharedPreferences prefs;
    public static Boolean fromService;
    public static String VideoUrl;
    static LinearLayout mainLayout;
    static WindowManager windowManager2;
    static WindowManager.LayoutParams params;
    static View mChatHeadView;
    static ImageView img_dialog;
    static String myURLIS = "";
    static Dialog dialog_quality_allvids;
    static VideoInfo streamInfo;

    private static String newurl;

    public static void Start(final Activity context, String websiteUrl, Boolean service) {
        try {
            Mcontext = context;
            fromService = service;
            try {
                websiteUrl = Utils.extractUrls(websiteUrl).get(0);
            } catch (Exception ignored) {

            }

            myURLIS = websiteUrl;
            if (!fromService) {
                pd = new ProgressDialog(context);
                pd.setMessage(Mcontext.getResources().getString(R.string.genarating_download_link));
                pd.setCancelable(false);
                pd.show();
            }
            getDataFromNewJetApi(websiteUrl, result -> {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> {
                    if (!result) {
                        if (myURLIS.contains("snapchat.com")) {
                            if (!Utils.isSocialMediaOn("snapchat.com")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            try {
                                myURLIS = Utils.extractUrls(myURLIS).get(0);
                                URI uri = new URI(myURLIS);
                                String urlwi = new URI(
                                        uri.getScheme(),
                                        uri.getAuthority(),
                                        uri.getPath(),
                                        null,
                                        uri.getFragment()
                                ).toString();

                                System.out.println("snamppppp " + urlwi.trim());
                                dismissMyDialog();
                                if (urlwi.split("/").length > 5) {
                                    Mcontext.startActivity(new Intent(Mcontext, SnapChatBulkStoryDownloader.class).putExtra("urlsnap", urlwi.substring(0, urlwi.lastIndexOf("/")).trim()));
                                } else {
                                    Mcontext.startActivity(new Intent(Mcontext, SnapChatBulkStoryDownloader.class).putExtra("urlsnap", urlwi.trim()));

                                }

                            } catch (Exception e) {
                                dismissMyDialogErrortoast();
                            }

                        } else if (myURLIS.contains("popcornflix")) {
                            if (!Utils.isSocialMediaOn("popcornflix")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }

                            myURLIS = myURLIS.substring(myURLIS.lastIndexOf("/") + 1);

                            System.out.println("fjhjfhjsdfsdhf " + myURLIS);

                            AndroidNetworking.get("https://api.unreel.me/v2/sites/popcornflix/videos/" + myURLIS + "/play-url?__site=popcornflix&__source=web&embed=false&protocol=https&tv=false")
                                    .addHeaders("User-Agent", "Mozilla/5.0 (Linux; Android 10;TXY567) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/8399.0.9993.96 Mobile Safari/599.36")
                                    .setPriority(Priority.MEDIUM)
                                    .build()
                                    .getAsJSONObject(new JSONObjectRequestListener() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            System.out.println("fjhjfhjsdfsdhf " + response);
                                            dismissMyDialog();
                                            String matag;
                                            try {
                                                JSONObject jsonObject = new JSONObject(response.toString());
                                                matag = jsonObject.getString("url");
                                                System.out.println("wojfdjhfdjh " + matag);
                                                DownloadFileMain.startDownloading(context, matag, "Popcornflex_" + System.currentTimeMillis(), ".mp4");

                                            } catch (Exception e) {
                                                dismissMyDialog();
                                                Log.i("THANGERRROR", e.toString());
                                            }

                                        }

                                        @Override
                                        public void onError(ANError error) {
                                            dismissMyDialog();
                                        }
                                    });

                        } else if (myURLIS.contains("veoh")) {

                            if (!Utils.isSocialMediaOn("veoh")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                            }
                            myURLIS = myURLIS.substring(myURLIS.lastIndexOf("/") + 1);

                            System.out.println("fjhjfhjsdfsdhf " + myURLIS);

                            AndroidNetworking.get("http://www.veoh.com/watch/getVideo/" + myURLIS)
                                    .addHeaders("User-Agent", "Mozilla/5.0 (Linux; Android 10;TXY567) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/8399.0.9993.96 Mobile Safari/599.36")
                                    .setPriority(Priority.MEDIUM)
                                    .build()
                                    .getAsJSONObject(new JSONObjectRequestListener() {
                                        @Override
                                        public void onResponse(JSONObject response) {

                                            System.out.println("fjhjfhjsdfsdhf " + response);

                                            dismissMyDialog();
                                            String matag;
                                            try {

                                                JSONObject jsonObject = new JSONObject(response.toString());

                                                matag = jsonObject.getJSONObject("video").getJSONObject("src").getString("HQ");
                                                System.out.println("wojfdjhfdjh " + matag);
                                                DownloadFileMain.startDownloading(context, matag, "Veoh_" + System.currentTimeMillis(), ".mp4");

                                            } catch (Exception e) {
                                                dismissMyDialog();
                                                Log.i("THANGERRROR", e.toString());
                                            }

                                        }

                                        @Override /**/
                                        public void onError(ANError error) {
                                            dismissMyDialog();
                                        }
                                    });

                        } else if (myURLIS.contains("moj")) {

                            if (!Utils.isSocialMediaOn("moj")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            try {
                                myURLIS = myURLIS.substring(myURLIS.lastIndexOf("/") + 1);

                                if (myURLIS.contains("?referrer=share")) {
                                    myURLIS = myURLIS.substring(0, myURLIS.indexOf("?"));
                                    System.out.println("fjhjfhjsdfsdhf 000=" + myURLIS + " size " + myURLIS.indexOf("?"));
                                    System.out.println("fjhjfhjsdfsdhf 000=" + "https://moj-apis.sharechat.com/videoFeed?postId=" + myURLIS + "&firstFetch=true");
                                }

                                JSONObject jsonObject = new JSONObject("{\"appVersion\":83,\"bn\":\"broker1\",\"client\":\"android\",\"deviceId\":\"ebb088d29e7287b1\",\"message\":{\"adData\":{\"adsShown\":0,\"firstFeed\":false},\"deviceInfoKey\":\"OSyQoHJLJ4NsXPLyQePkAICh3Q0ih0bveFwm1KEV+vReMuldqo+mSyMjdhb4EeryKxk1ctAbYaDH\\\\nTI+PMRPZVYH5pBccAm7OT2uz69vmD/wPqGuSgWV2aVNMdM75DMb8NZn1JU2b1bo/oKs80baklsvx\\\\n1X7jrFPL6M5EDTdPDhs=\\\\n\",\"deviceInfoPayload\":\"M6g+6j6irhFT/H6MsQ/n/tEhCl7Z5QgtVfNKU8M90zTJHxqljm2263UkjRR9bRXAjmQFXXOTXJ25\\\\nOHRjV7L5Lw+tUCONoYfyUEzADihWfAiUgXJEcKePfZONbdXXuwGgOPeD0k4iSvI7JdzroRCScKXd\\\\n41CkmXFayPaRL9aqgAgs6kSoIncCWBU2gEXiX1lgPVvdmUzCZ+yi2hFA+uFOmv1MJ6dcFKKcpBM6\\\\nHSPIrGV+YtTyfd8nElx0kyUbE4xmjOuMrctkjnJkd2tMdxB8qOFKeYrcLzy4LZJNXyUmzs29XSE+\\\\nhsrMZib8fFPJhJZIyGCWqfWiURut4Bg5HxYhYhg3ejPxFjNyXxS3Ja+/pA+A0olt5Uia7ync/Gui\\\\n58tlDQ4SKPthCzGa1tCVN+2y/PW30+LM79t0ltJ/YrNZivQx4eEnszlM9nwmIuj5z5LPniQghA6x\\\\nrfQ8IqVUZfiitXj/Fr7UjKg1cs/Ajj8g4u/KooRvVkg9tMwWePtJFqrkk1+DU4cylnSEG3XHgfer\\\\nslrzj5NNZessMEi+4Nz0O2D+b8Y+RjqN6HqpwZPDHhZwjz0Iuj2nhZLgu1bgNJev5BwxAr8akDWv\\\\nvKsibrJS9auQOYVzbYZFdKMiBnh+WHq0qO2aW1akYWCha3ZsSOtsnyPnFC+1PnMbBv+FiuJmPMXg\\\\nSODFoRIXfxgA/qaiKBipS+kIyfaPxn6O1i6MOwejVuQiWdAPTO132Spx0cFtdyj2hX6wAMe21cSy\\\\n8rs3KQxiz+cq7Rfwzsx4wiaMryFunfwUwnauGwTFOW98D5j6oO8=\\\\n\",\"lang\":\"Hindi\",\"playEvents\":[{\"authorId\":\"18326559001\",\"networkBitrate\":1900000,\"initialBufferPercentage\":100,\"isRepost\":false,\"sg\":false,\"meta\":\"NotifPostId\",\"md\":\"Stream\",\"percentage\":24.68405,\"p\":\"91484006\",\"radio\":\"wifi\",\"r\":\"deeplink_VideoPlayer\",\"repeatCount\":0,\"timeSpent\":9633,\"duration\":15,\"videoStartTime\":3916,\"t\":1602255552820,\"clientType\":\"Android\",\"i\":79,\"appV\":83,\"sessionId\":\"72137847101_8863b3f5-ad2d-4d59-aa7c-cf1fb9ef32ea\"},{\"authorId\":\"73625124001\",\"networkBitrate\":1900000,\"initialBufferPercentage\":100,\"isRepost\":false,\"sg\":false,\"meta\":\"list2\",\"md\":\"Stream\",\"percentage\":17.766666,\"p\":\"21594412\",\"radio\":\"wifi\",\"r\":\"First Launch_VideoPlayer\",\"repeatCount\":0,\"tagId\":\"0\",\"tagName\":\"\",\"timeSpent\":31870,\"duration\":17,\"videoStartTime\":23509,\"t\":1602218215942,\"clientType\":\"Android\",\"i\":79,\"appV\":83,\"sessionId\":\"72137847101_db67c0c9-a267-4cec-a3c3-4c0fa4ea16e1\"}],\"r\":\"VideoFeed\"},\"passCode\":\"9e32d6145bfe53d14a0c\",\"resTopic\":\"response/user_72137847101_9e32d6145bfe53d14a0c\",\"userId\":\"72137847101\"}");

                                AndroidNetworking.post("https://moj-apis.sharechat.com/videoFeed?postId=" + myURLIS + "&firstFetch=true")
                                        .addHeaders("X-SHARECHAT-USERID", "72137847101")
                                        .addHeaders("X-SHARECHAT-SECRET", "9e32d6145bfe53d14a0c")
                                        .addHeaders("APP-VERSION", "83")
                                        .addHeaders("PACKAGE-NAME", "in.mohalla.video")
                                        .addHeaders("DEVICE-ID", "ebb088d29e7287b1")
                                        .addHeaders("CLIENT-TYPE", "Android:")
                                        .addHeaders("Content-Type", "application/json; charset=UTF-8")
                                        .addHeaders("Host", "moj-apis.sharechat.com")
                                        .addHeaders("Connection", "Keep-Alive:")
                                        .addHeaders("User-Agent", "okhttp/3.12.12app-version:83")
                                        .addHeaders("cache-control", "no-cache")
                                        .addHeaders("client-type", "Android")
                                        .addHeaders("connection", "Keep-Alive")
                                        .addHeaders("content-type", "application/json;charset=UTF-8")
                                        .addHeaders("device-id", "ebb088d29e7287b1")
                                        .addHeaders("host", "moj-apis.sharechat.com")
                                        .addHeaders("package-name", "in.mohalla.video")
                                        .addHeaders("postman-token", "37d59a7c-f247-3b70-ab3c-1dedf4079852")
                                        .addHeaders("user-agent", "okhttp/3.12.12")
                                        .addHeaders("x-sharechat-secret", "9e32d6145bfe53d14a0c")
                                        .addHeaders("x-sharechat-userid", "72137847101")
                                        .addJSONObjectBody(jsonObject)
                                        .setPriority(Priority.MEDIUM)
                                        .build()
                                        .getAsJSONObject(new JSONObjectRequestListener() {
                                            @Override
                                            public void onResponse(JSONObject response) {

                                                System.out.println("fjhjfhjsdfsdhf " + response);

                                                dismissMyDialog();
                                                String matag;
                                                try {

                                                    JSONObject jsonObject = new JSONObject(response.toString());

                                                    matag = jsonObject.getJSONObject("payload")
                                                            .getJSONArray("d")
                                                            .getJSONObject(0)
                                                            .getString("compressedVideoUrl");

                                                    System.out.println("wojfdjhfdjh " + matag);
                                                    DownloadFileMain.startDownloading(context, matag, "Moj_" + System.currentTimeMillis(), ".mp4");

                                                } catch (Exception e) {
                                                    dismissMyDialog();
                                                    Log.i("THANGERRROR", e.toString());
                                                }

                                            }

                                            @Override
                                            public void onError(ANError error) {
                                                dismissMyDialog();
                                            }
                                        });

                            } catch (Exception e) {
                                Log.i("THANGERRROR", e.toString());

                                dismissMyDialog();
                            }
                        } else if (myURLIS.contains("fairtok")) {
                            if (!Utils.isSocialMediaOn("fairtok")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            try {
                                myURLIS = myURLIS.substring(myURLIS.lastIndexOf("/") + 1);

                                JSONObject jsonObject = new JSONObject("{\"device_fingerprint_id\":\"838529202513017602\",\"identity_id\":\"838529202537882206\",\"hardware_id\":\"ebb088d29e7287b1\",\"is_hardware_id_real\":true,\"brand\":\"samsung\",\"model\":\"SM-J200G\",\"screen_dpi\":240,\"screen_height\":960,\"screen_width\":540,\"wifi\":true,\"ui_mode\":\"UI_MODE_TYPE_NORMAL\",\"os\":\"Android\",\"os_version\":22,\"cpu_type\":\"armv7l\",\"build\":\"LMY47X.J200GDCU2ARL1\",\"locale\":\"en_GB\",\"connection_type\":\"wifi\",\"os_version_android\":\"5.1.1\",\"country\":\"GB\",\"language\":\"en\",\"local_ip\":\"192.168.43.18\",\"app_version\":\"1.19\",\"facebook_app_link_checked\":false,\"is_referrable\":0,\"debug\":false,\"update\":1,\"latest_install_time\":1601158937336,\"latest_update_time\":1601158937336,\"first_install_time\":1601158937336,\"previous_update_time\":1601158937336,\"environment\":\"FULL_APP\",\"android_app_link_url\":\"https:\\/\\/fairtok.app.link\\/" + myURLIS + "\",\"external_intent_uri\":\"https:\\/\\/fairtok.app.link\\/Y7ov2al149\",\"cd\":{\"mv\":\"-1\",\"pn\":\"com.fairtok\"},\"metadata\":{},\"advertising_ids\":{\"aaid\":\"094dfa1f-77cf-4f84-b373-2c15bf74f9d1\"},\"lat_val\":0,\"google_advertising_id\":\"094dfa1f-77cf-4f84-b373-2c15bf74f9d1\",\"instrumentation\":{\"v1\\/open-qwt\":\"0\"},\"sdk\":\"android5.0.1\",\"branch_key\":\"key_live_hjLYp0Wi3i6R1qQ1Lr51TlpcBvkxEp53\",\"retryNumber\":0}");

                                AndroidNetworking.post("https://api2.branch.io/v1/open")
                                        .addHeaders("cache-control", "no-cache")

                                        .addJSONObjectBody(jsonObject)
                                        .setPriority(Priority.MEDIUM)
                                        .build()
                                        .getAsJSONObject(new JSONObjectRequestListener() {
                                            @Override
                                            public void onResponse(JSONObject response) {

                                                System.out.println("fjhjfhjsdfsdhf " + response);

                                                dismissMyDialog();
                                                String matag;
                                                try {

                                                    JSONObject jsonObject = new JSONObject(response.toString());

                                                    matag = "https://bucket-fairtok.s3.ap-south-1.amazonaws.com/" + jsonObject.getString("post_video");

                                                    System.out.println("wojfdjhfdjh " + matag);
                                                    DownloadFileMain.startDownloading(context, matag, "Fairtok_" + System.currentTimeMillis(), ".mp4");

                                                } catch (Exception e) {
                                                    dismissMyDialog();
                                                    Log.i("THANGERRROR", e.toString());
                                                }

                                            }

                                            @Override /**/
                                            public void onError(ANError error) {
                                                dismissMyDialog();
                                            }
                                        });
                            } catch (Exception e) {
                                Log.i("THANGERRROR", e.toString());
                                dismissMyDialog();
                            }
                        } else if (myURLIS.contains("vlipsy")) {
                            if (!Utils.isSocialMediaOn("vlipsy")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            try {
                                myURLIS = myURLIS.substring(myURLIS.lastIndexOf("/") + 1);
                                if (myURLIS.length() > 8) {
                                    String[] aa = myURLIS.split("-");
                                    myURLIS = aa[aa.length - 1];
                                }
                                AndroidNetworking.get("https://apiv2.vlipsy.com/v1/vlips/" + myURLIS + "?key=vl_R8daJGhs67i7Ej7y")
                                        .setPriority(Priority.MEDIUM)
                                        .build()
                                        .getAsJSONObject(new JSONObjectRequestListener() {
                                            @Override
                                            public void onResponse(JSONObject response) {

                                                dismissMyDialog();
                                                String matag;
                                                try {

                                                    JSONObject jsonObject = new JSONObject(response.toString());

                                                    matag = jsonObject.getJSONObject("data").getJSONObject("media").getJSONObject("mp4").getString("url");

                                                    System.out.println("wojfdjhfdjh " + matag);
                                                    DownloadFileMain.startDownloading(context, matag, "Vlipsy_" + System.currentTimeMillis(), ".mp4");

                                                } catch (Exception e) {
                                                    dismissMyDialog();
                                                    Log.i("THANGERRROR", e.toString());
                                                }
                                            }

                                            @Override /**/
                                            public void onError(ANError error) {
                                                dismissMyDialog();
                                            }
                                        });
                            } catch (Exception e) {
                                Log.i("THANGERRROR", e.toString());
                                dismissMyDialog();
                            }
                        } else if (myURLIS.contains("l_ik_e_e")) {
                            if (!Utils.isSocialMediaOn("likee")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            LikeeVideoDownloader tikTokNewTestDownloader = new LikeeVideoDownloader(Mcontext, myURLIS);
                            tikTokNewTestDownloader.DownloadVideo();

                        } else if (myURLIS.contains("likee")) {
                            if (!Utils.isSocialMediaOn("likee")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            final String[] finalUrl3 = {myURLIS};
                            new Thread(() -> {
                                Looper.prepare();
                                try {

                                    if (finalUrl3[0].contains("likee.video/v/")) {
                                        try {
                                            OkHttpClient client = new OkHttpClient().newBuilder()
                                                    .build();
                                            Request request = new Request.Builder()
                                                    .url(finalUrl3[0])
                                                    .method("GET", null)
                                                    .build();
                                            Response response = client.newCall(request).execute();
                                            String myres = Objects.requireNonNull(response.body()).string();
                                            System.out.println("mylikeeefjhjfhjsdfsdhf 2= " + myres);
                                            String line2 = myres.substring(myres.indexOf("window.data ="), myres.indexOf("}</script>"));
                                            line2 = line2.replaceFirst("window.data = ", "") + "}";

                                            finalUrl3[0] = line2;
                                            System.out.println("mylikeeefjhjfhjsdfsdhf postid = " + finalUrl3[0]);

                                        } catch (Exception e) {
                                            Log.i("THANGERRROR", e.toString());
                                        }

                                    }

                                    String v;
                                    if (finalUrl3[0].contains("likee.video/@")) {
                                        v = finalUrl3[0].substring(finalUrl3[0].indexOf("/video/") + 7);
                                    } else {
                                        Uri uri = Uri.parse(finalUrl3[0]);
                                        v = uri.getQueryParameter("postId");
                                    }
                                    System.out.println("mylikeeefjhjfhjsdfsdhf 3= " + v);


                                    OkHttpClient client = new OkHttpClient.Builder()
                                            .connectTimeout(10, TimeUnit.SECONDS)
                                            .writeTimeout(10, TimeUnit.SECONDS)
                                            .readTimeout(30, TimeUnit.SECONDS)
                                            .build();
                                    RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                            .addFormDataPart("postIds", v)
                                            .build();
                                    Request request = new Request.Builder()
                                            .url("https://likee.video/official_website/VideoApi/getVideoInfo")
                                            .method("POST", body)
                                            .addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.71 Safari/537.36")
                                            .build();
                                    Response response = client.newCall(request).execute();

                                    if (response.code() == 200) {
                                        dismissMyDialog();
                                        String urls = new JSONObject(Objects.requireNonNull(response.body()).string()).getJSONObject("data").getJSONArray("videoList").getJSONObject(0).getString("videoUrl");
                                        if (urls.contains("_4")) {
                                            urls = urls.replace("_4", "");
                                        }
                                        if (!urls.isEmpty()) {

                                            String nametitle = "Likee_" +
                                                    System.currentTimeMillis();

                                            DownloadFileMain.startDownloading(Mcontext, urls, nametitle, ".mp4");
                                        }

                                    } else {
                                        dismissMyDialogErrortoast();
                                    }

                                } catch (Exception e) {
                                    Log.i("THANGERRROR", e.toString());

                                    dismissMyDialogErrortoast();
                                }
                            }).start();

                        } else if (myURLIS.contains("kooapp")) {
                            if (!Utils.isSocialMediaOn("kooapp")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            KooDownloader kooDownloader = new KooDownloader(Mcontext, myURLIS);
                            kooDownloader.DownloadVideo();

                        } else if (myURLIS.contains("gfycat")) {
                            if (!Utils.isSocialMediaOn("gfycat")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, true);
                        } else if (myURLIS.contains("abc.com")) {
                            if (!Utils.isSocialMediaOn("abc.com")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, true);
                        } else if (myURLIS.contains("videoclip.org")) {
                            if (!Utils.isSocialMediaOn("videoclip.org")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, true);
                        } else if (myURLIS.contains("funimate")) {
                            if (!Utils.isSocialMediaOn("funimate")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }

                            CalldlApisDataData(myURLIS, true);
                        } else if (myURLIS.contains("1tv.ru")) {
                            if (!Utils.isSocialMediaOn("1tv.ru")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, true);
                        } else if (myURLIS.contains("naver")) {
                            if (!Utils.isSocialMediaOn("naver")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, true);
                        } else if (myURLIS.contains("gloria.tv")) {

                            if (!Utils.isSocialMediaOn("gloria.tv")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, true);
                        } else if (myURLIS.contains("vidcommons.org")) {
                            if (!Utils.isSocialMediaOn("vidcommons.org")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, true);
                        } else if (myURLIS.contains("media.ccc.de")) {

                            if (!Utils.isSocialMediaOn("media.ccc.de")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, true);
                        } else if (myURLIS.contains("vlive")) {
                            if (!Utils.isSocialMediaOn("vlive")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, true);
                        } else if (myURLIS.contains("blogspot.com")) {
                            if (!Utils.isSocialMediaOn("blogspot.com")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, true);

                        } else if (myURLIS.contains("vimeo.com")) {
                            if (!Utils.isSocialMediaOn("vimeo.com")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, true);
                        } else if (myURLIS.contains("flickr") || myURLIS.contains("flic.kr")) {
                            if (!Utils.isSocialMediaOn("flickr") || !Utils.isSocialMediaOn("flic.kr")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, true);
                        } else if (myURLIS.contains("streamable")) {
                            if (!Utils.isSocialMediaOn("streamable")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, true);
                        } else if (myURLIS.contains("vk.com")) {

                            if (!Utils.isSocialMediaOn("vk.com")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, false);
                        } else if (myURLIS.contains("soundcloud")) {
                            if (!Utils.isSocialMediaOn("soundcloud")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            if (GlobalConstant.showyoutube) {
                                myURLIS = myURLIS.replace("//m.", "//");
                                CalldlApisDataData(myURLIS, true);
                            } else {
                                dismissMyDialogErrortoast();
                            }
                        } else if (myURLIS.contains("bandcamp")) {
                            if (!Utils.isSocialMediaOn("bandcamp")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, true);
                        } else if (myURLIS.contains("cocoscope")) {

                            if (!Utils.isSocialMediaOn("cocoscope")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, true);
                        } else if (myURLIS.contains("vidlit")) {
                            if (!Utils.isSocialMediaOn("vidlit")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, true);
                        } else if (myURLIS.contains("izlesene")) {
                            if (!Utils.isSocialMediaOn("izlesene")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, false);

                        } else if (myURLIS.contains("linkedin")) {
                            if (!Utils.isSocialMediaOn("linkedin")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            Log.e(TAG, "Start: Working-Linkedin");
                            new CalllinkedinData(Mcontext).execute(myURLIS);
                        } else if (myURLIS.contains("kwai") || myURLIS.contains("kw.ai")) {
                            if (!Utils.isSocialMediaOn("kwai") || !Utils.isSocialMediaOn("kw.ai")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, false);
                        } else if (myURLIS.contains("bitchute")) {
                            if (!Utils.isSocialMediaOn("bitchute")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, false);
                        } else if (myURLIS.contains("ojoo")) {
                            if (!Utils.isSocialMediaOn("ojoo")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, false);
                        } else if (myURLIS.contains("blogger")) {
                            if (!Utils.isSocialMediaOn("blogger")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, false);
                        } else if (myURLIS.contains("break.com")) {
                            if (!Utils.isSocialMediaOn("break.com")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, false);
                        } else if (myURLIS.contains("espn")) {
                            if (!Utils.isSocialMediaOn("espn")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, true);
                        } else if (myURLIS.contains("coub")) {
                            if (!Utils.isSocialMediaOn("coub")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, true);
                        } else if (myURLIS.contains("ted.com")) {
                            if (!Utils.isSocialMediaOn("ted.com")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, true);
                        } else if (myURLIS.contains("fansubs.tv")) {
                            if (!Utils.isSocialMediaOn("fansubs.tv")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, true);
                        } else if (myURLIS.contains("twitch")) {
                            if (!Utils.isSocialMediaOn("twitch")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, true);

                        } else if (myURLIS.contains("imgur.com")) {
                            if (!Utils.isSocialMediaOn("imgur.com")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }

                            myURLIS = myURLIS.replace("//m.", "//");
                            CalldlApisDataData(myURLIS, false);

                        } else if (myURLIS.contains("rumble.com")) {
                            if (!Utils.isSocialMediaOn("rumble.com")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, false);
                        } else if (myURLIS.contains("youtube.com") || myURLIS.contains("youtu.be")) {
                            Log.i("LOGClipboard111111 testinggg ytd", " " + Utils.isSocialMediaOn("youtube.com"));

                            if (GlobalConstant.showyoutube) {
                                if (!Utils.isSocialMediaOn("youtube.com") || !Utils.isSocialMediaOn("youtu.be")) {
                                    dismissMyDialogErrorToastForBlockedWebsitePanel();
                                    return;
                                }
                                callDlApiNew(myURLIS);
                            } else {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                            }
                        } else if (myURLIS.contains("sharechat.com")) {
                            if (!Utils.isSocialMediaOn("sharechat.com")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            try {
                                myURLIS = Utils.extractUrls(myURLIS).get(0);
                                System.out.println("subssssssss11 " + myURLIS);
                                int index = myURLIS.lastIndexOf('/') + 1;
                                myURLIS = myURLIS.substring(index);
                                if (myURLIS.contains("?")) {
                                    myURLIS = myURLIS.split("[?]")[0];
                                }
                                System.out.println("subssssssss " + myURLIS);
                                JSONObject jsonObject = new JSONObject("{\"bn\":\"broker3\",\"userId\":644045091,\"passCode\":\"52859d76753457f8dcae\",\"client\":\"web\",\"message\":{\"key\":\"" + myURLIS + "\",\"ph\":\"" + myURLIS + "\"}}");
                                AndroidNetworking.post("https://apis.sharechat.com/requestType45")
                                        .addJSONObjectBody(jsonObject)
                                        .addHeaders("Content-Type", "application/json")
                                        .addHeaders("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36")
                                        .setPriority(Priority.MEDIUM)
                                        .build()
                                        .getAsJSONObject(new JSONObjectRequestListener() {
                                            @Override
                                            public void onResponse(JSONObject response) {

                                                dismissMyDialog();
                                                String matag;
                                                try {

                                                    JSONObject jsonObject = new JSONObject(response.toString());

                                                    boolean isvideo = true;
                                                    try {
                                                        matag = jsonObject.getJSONObject("payload").getJSONObject("d").getString("v");
                                                        isvideo = true;
                                                    } catch (Exception e) {
                                                        matag = jsonObject.getJSONObject("payload").getJSONObject("d").getString("g");
                                                        isvideo = false;
                                                    }

                                                    System.out.println("wojfdjhfdjh " + matag);
                                                    DownloadFileMain.startDownloading(context, matag, "Sharechat_" + System.currentTimeMillis(), ((isvideo) ? ".mp4" : ".jpg"));

                                                } catch (Exception e) {

                                                    Log.i("THANGERRROR", e.toString());
                                                    dismissMyDialog();
                                                }

                                            }

                                            @Override
                                            public void onError(ANError error) {
                                                dismissMyDialog();
                                            }
                                        });

                            } catch (Exception e) {
                                dismissMyDialog();
                            }
                        } else if (myURLIS.contains("roposo.com")) {
                            if (!Utils.isSocialMediaOn("roposo.com")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            Log.i("LOGClipboard111111 clip", "work 66666");
                            new callGetRoposoData().execute(myURLIS);
                            Log.i("LOGClipboard111111 clip", "work 1111111");
                        } else if (myURLIS.contains("snackvideo") || myURLIS.contains("sck.io")) {
                            if (!Utils.isSocialMediaOn("snackvideo") || !Utils.isSocialMediaOn("sck.io")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            System.out.println("myresponseis111 urlsss" + myURLIS);
                            new callGetsnackvideoData().execute(myURLIS);
                        } else if ((myURLIS.contains("facebook.com") || myURLIS.contains("fb.watch"))) {
                            if (!Utils.isNonPlayStoreApp) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            if (!Utils.isSocialMediaOn("facebook.com") || !Utils.isSocialMediaOn("fb.watch")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            if (!Mcontext.isFinishing()) {

                                try {

                                    System.out.println("myurliss = " + myURLIS);

                                    Executors.newSingleThreadExecutor().submit(() -> {
                                        try {
                                            Looper.prepare();

                                            ClearableCookieJar cookieJar =
                                                    new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(Mcontext));

                                            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                                            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

                                            OkHttpClient client = new OkHttpClient.Builder()
                                                    .cookieJar(cookieJar)
                                                    .addInterceptor(logging)
                                                    .connectTimeout(10, TimeUnit.SECONDS)
                                                    .writeTimeout(10, TimeUnit.SECONDS)
                                                    .readTimeout(30, TimeUnit.SECONDS)
                                                    .build();

                                            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                                    .addFormDataPart("q", myURLIS)
                                                    .addFormDataPart("p", "home")
                                                    .addFormDataPart("lang", "en")
                                                    .addFormDataPart("w", "")
                                                    .build();
                                            Request request = new Request.Builder()
                                                    .url("https://x2download.com/api/ajaxSearch")
                                                    .header("Cookie", "")
                                                    .method("POST", body)
                                                    .build();
                                            Response response = client.newCall(request).execute();

                                            String ressff = Objects.requireNonNull(response.body()).string();
                                            System.out.println("myurliss resss = " + ressff);

                                            JSONObject document = new JSONObject(ressff);
                                            dismissMyDialog();

                                            if (document.has("data") && !document.getString("data").isEmpty()) {
                                                String nametitle = "Facebook_" + System.currentTimeMillis();
                                                String inputString = document.getString("data");
                                                try {
                                                    String decodedHtml = StringEscapeUtils.unescapeHtml4(inputString);
                                                    System.out.println(decodedHtml);
                                                    List<String> allurls = Utils.extractUrlsWithVideo(decodedHtml);
                                                    System.out.println("myurliss lllllllinks = " + allurls.get(0));
                                                    DownloadFileMain.startDownloading(Mcontext, allurls.get(0), nametitle, ".mp4");
                                                } catch (Exception e) {
                                                    dismissMyDialogErrortoast();
                                                }
                                            } else {
                                                dismissMyDialogErrortoast();
                                            }
                                        } catch (Exception e) {
                                            System.out.println("myurliss err dialog = " + e.getLocalizedMessage());
                                            dismissMyDialogErrortoast();
                                        }
                                    });
                                } catch (Exception e) {
                                    System.out.println("myurliss err dialog 4= " + e.getLocalizedMessage());
                                    dismissMyDialogErrortoast();
                                }
//                                Dialog dialog = new Dialog(Mcontext);
//                                dialog.setContentView(R.layout.tiktok_optionselect_dialog);
//                                Button methode0 = dialog.findViewById(R.id.dig_btn_met0);
//                                Button methode1 = dialog.findViewById(R.id.dig_btn_met1);
//                                Button methode2 = dialog.findViewById(R.id.dig_btn_met2);
//                                Button methode3 = dialog.findViewById(R.id.dig_btn_met3);
//                                Button methode4 = dialog.findViewById(R.id.dig_btn_met4);
//                                Button methode5 = dialog.findViewById(R.id.dig_btn_met5);
//                                Button methode6 = dialog.findViewById(R.id.dig_btn_met6);
//
//                                methode5.setVisibility(View.VISIBLE);
//                                methode6.setVisibility(View.VISIBLE);
//
//                                Button dig_btn_cancel = dialog.findViewById(R.id.dig_btn_cancel);
//
//                                methode0.setOnClickListener(v -> {
//                                    dialog.dismiss();
//
//                                    System.out.println("wojfdjhfdjh myfgsdyfsfus=" + myURLIS);
//
//                                    dismissMyDialog();
//                                    Intent intent = new Intent(Mcontext, FacebookDownloadCloudBypassWebview_method_0_new.class);
//                                    intent.putExtra("myvidurl", myURLIS);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    Mcontext.startActivity(intent);
//
//                                });
//
//                                methode1.setOnClickListener(v -> {
//                                    dialog.dismiss();
//
//                                    System.out.println("wojfdjhfdjh myfgsdyfsfus=" + myURLIS);
//
//                                    dismissMyDialog();
//                                    Intent intent = new Intent(Mcontext, GetLinkThroughWebView2.class);
//                                    intent.putExtra("myurlis", myURLIS);
//                                    Mcontext.startActivityForResult(intent, 2);
//
//                                });
//
//                                //todo fix
//                                methode2.setOnClickListener(v -> {
//                                    dialog.dismiss();
//
//
//                                    System.out.println("myurliss = " + myURLIS);
//
//
//                                    AndroidNetworking.get("https://s4.downloadfacebook.net/ajax/getLinks.php?video=" + myURLIS + "&rand=PNSQNPOPRTTUOLP")
//                                            .setPriority(Priority.MEDIUM)
//                                            .addHeaders("Content-type", "application/x-www-form-urlencoded")
//                                            .addHeaders("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36")
//                                            .build()
//                                            .getAsJSONObject(new JSONObjectRequestListener() {
//                                                @Override
//                                                public void onResponse(JSONObject response) {
//
//                                                    System.out.println("ytff wojfdjhfdjhtik yyyy " + response);
//
//                                                    String matag;
//                                                    try {
//                                                        dismissMyDialog();
//                                                        JSONObject jsonObject = new JSONObject(response.toString());
//
//                                                        JSONArray jsonaaa = jsonObject.getJSONObject("data").getJSONArray("av");
//                                                        matag = jsonaaa.getJSONObject(0).getString("url");
//
//                                                        DownloadFileMain.startDownloading(context, matag, "Facebook_" + System.currentTimeMillis(), ".mp4");
//
//                                                    } catch (Exception e) {
//
//                                                        System.out.println("myresponseis111 exp2 " + e.getMessage());
//                                                        dismissMyDialogErrortoast();
//
//                                                    }
//
//                                                }
//
//                                                @Override
//                                                public void onError(ANError error) {
//                                                    System.out.println("myresponseis111 exp2 " + error.getMessage());
//                                                    dismissMyDialogErrortoast();
//
//                                                }
//                                            });
//
//
//                                });
//
//                                methode3.setOnClickListener(v -> {
//                                    dialog.dismiss();
//
//                                    System.out.println("wojfdjhfdjh myfgsdyfsfus=" + myURLIS);
//
//                                    dismissMyDialog();
//                                    Intent intent = new Intent(Mcontext, FacebookDownloadCloudBypassWebview_method_1.class);
//                                    intent.putExtra("myvidurl", myURLIS);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                                    Mcontext.startActivity(intent);
//
//
//                                });
//                                methode4.setOnClickListener(v -> {
//                                    dialog.dismiss();
//
//                                    System.out.println("wojfdjhfdjh myfgsdyfsfus=" + myURLIS);
//
//                                    dismissMyDialog();
//                                    Intent intent = new Intent(Mcontext, FacebookDownloadCloudBypassWebview_method_3.class);
//                                    intent.putExtra("myvidurl", myURLIS);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                                    Mcontext.startActivity(intent);
//                                });
//
//                                methode5.setOnClickListener(v -> {
//                                    dialog.dismiss();
//                                    callDlApiNew(myURLIS);
//                                });
//
//
//                                methode6.setOnClickListener(v -> {
//                                    dialog.dismiss();
//
//                                    try {
//
//                                        System.out.println("myurliss = " + myURLIS);
//
//                                        Executors.newSingleThreadExecutor().submit(() -> {
//                                            try {
//                                                Looper.prepare();
//
//                                                ClearableCookieJar cookieJar =
//                                                        new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(Mcontext));
//
//                                                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//                                                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//                                                OkHttpClient client = new OkHttpClient.Builder()
//                                                        .cookieJar(cookieJar)
//                                                        .addInterceptor(logging)
//                                                        .connectTimeout(10, TimeUnit.SECONDS)
//                                                        .writeTimeout(10, TimeUnit.SECONDS)
//                                                        .readTimeout(30, TimeUnit.SECONDS)
//                                                        .build();
//
//                                                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
//                                                        .addFormDataPart("q", myURLIS)
//                                                        .addFormDataPart("p", "home")
//                                                        .addFormDataPart("lang", "en")
//                                                        .addFormDataPart("w", "")
//                                                        .build();
//                                                Request request = new Request.Builder()
//                                                        .url("https://x2download.com/api/ajaxSearch")
//                                                        .header("Cookie", "")
//                                                        .method("POST", body)
//                                                        .build();
//                                                Response response = client.newCall(request).execute();
//
//                                                String ressff = Objects.requireNonNull(response.body()).string();
//                                                System.out.println("myurliss resss = " + ressff);
//
//                                                JSONObject document = new JSONObject(ressff);
//                                                dismissMyDialog();
//
//                                                if (document.has("data") && !document.getString("data").isEmpty()) {
//                                                    String nametitle = "Facebook_" + System.currentTimeMillis();
//                                                    String inputString = document.getString("data");
//                                                    try {
//                                                        String decodedHtml = StringEscapeUtils.unescapeHtml4(inputString);
//                                                        System.out.println(decodedHtml);
//                                                        List<String> allurls = Utils.extractUrlsWithVideo(decodedHtml);
//                                                        System.out.println("myurliss lllllllinks = " + allurls.get(0));
//                                                        DownloadFileMain.startDownloading(Mcontext, allurls.get(0), nametitle, ".mp4");
//                                                    } catch (Exception e) {
//                                                        dismissMyDialogErrortoast();
//                                                    }
//                                                } else {
//                                                    dismissMyDialogErrortoast();
//                                                }
//                                            } catch (Exception e) {
//                                                System.out.println("myurliss err dialog = " + e.getLocalizedMessage());
//                                                dismissMyDialogErrortoast();
//                                            }
//                                        });
//                                    } catch (Exception e) {
//                                        System.out.println("myurliss err dialog 4= " + e.getLocalizedMessage());
//                                        dismissMyDialogErrortoast();
//                                    }
//                                });
//                                dig_btn_cancel.setOnClickListener(v -> {
//                                    dialog.dismiss();
//                                    dismissMyDialog();
//                                });
//                                dialog.setCancelable(false);
//                                dialog.show();
                            }

                        } else if (myURLIS.contains("instagram.com")) {
                            if (!Utils.isNonPlayStoreApp) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            if (!Utils.isSocialMediaOn("instagram.com")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            callDlApiNew(myURLIS);
                        } else if (myURLIS.contains("bilibili.com")) {
                            if (!Utils.isSocialMediaOn("bilibili.com")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            new callGetbilibiliAppData().execute(myURLIS);
                        } else if (myURLIS.contains("mitron.tv")) {
                            if (!Utils.isSocialMediaOn("mitron.tv")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            new CallMitronData().execute(myURLIS);
                        } else if (myURLIS.contains("josh")) {
                            if (!Utils.isSocialMediaOn("josh")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            new CallJoshData().execute(myURLIS);
                        } else if (myURLIS.contains("kuaishou")) {
                            if (!Utils.isSocialMediaOn("kuaishou")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            if (myURLIS.contains("https://www.kuaishou.com/short-video/")) {
                                new CallkuaishouData().execute(myURLIS);
                            } else {
                                if (pd != null && pd.isShowing() && !fromService && !Mcontext.isFinishing()) {
                                    pd.dismiss();
                                    Mcontext.runOnUiThread(() -> {
                                        Utils.ShowToast(Mcontext, "Please Use this url type \"https://www.kuaishou.com/short-video/\"");
                                    });
                                }
                            }
                        } else if (myURLIS.contains("triller")) {
                            if (!Utils.isSocialMediaOn("triller")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            getTrillerData(myURLIS);
                        } else if (myURLIS.contains("rizzle")) {
                            if (!Utils.isSocialMediaOn("rizzle")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            new CallRizzleData().execute(myURLIS);
                        } else if (myURLIS.contains("solidfiles")) {
                            if (!Utils.isSocialMediaOn("solidfiles")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            SolidfilesDownloader solidfilesDownloader = new SolidfilesDownloader(Mcontext, myURLIS);
                            solidfilesDownloader.DownloadVideo();
                        } else if (myURLIS.contains("ifunny")) {
                            if (!Utils.isSocialMediaOn("ifunny")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            new CallIfunnyData().execute(myURLIS);
                        } else if (myURLIS.contains("trell.co")) {
                            if (!Utils.isSocialMediaOn("trell.co")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            if (myURLIS.contains("?")) {
                                myURLIS = myURLIS.split("\\?")[0];
                            }
                            myURLIS = myURLIS.substring(myURLIS.indexOf("watch"));
                            myURLIS = myURLIS.substring(myURLIS.indexOf("/") + 1);
                            System.out.println("mytrellis " + myURLIS);
                            AndroidNetworking.get("https://api.trell.co/api/v1/trail/" + myURLIS)
                                    .setPriority(Priority.MEDIUM)
                                    .build()
                                    .getAsJSONObject(new JSONObjectRequestListener() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                dismissMyDialog();
                                                String html = response.toString();
                                                System.out.println("myresponseis111 html " + html);
                                                if (!html.isEmpty()) {
                                                    VideoUrl = String.valueOf(new JSONObject(new JSONObject(html)
                                                            .getJSONObject("result")
                                                            .getJSONObject("trail")
                                                            .getJSONArray("posts").get(0).toString())
                                                            .get("video"));
                                                    System.out.println("myresponseis111 exp991 " + VideoUrl);
                                                    if (VideoUrl != null && !VideoUrl.isEmpty()) {
                                                        try {
                                                            String myurldocument = VideoUrl;
                                                            String nametitle = "trellvideo_" + System.currentTimeMillis();
                                                            DownloadFileMain.startDownloading(Mcontext, myurldocument, nametitle, ".mp4");
                                                            VideoUrl = "";
                                                        } catch (Exception document2) {
                                                            System.out.println("myresponseis111 exp1 " + document2.getMessage());
                                                            dismissMyDialog();
                                                            document2.printStackTrace();
                                                            Mcontext.runOnUiThread(() -> Utils.ShowToastError(Mcontext, Mcontext.getResources().getString(R.string.somthing)
                                                            ));
                                                        }

                                                        return;
                                                    }
                                                    return;
                                                }
                                                Mcontext.runOnUiThread(() -> {Utils.ShowToastError(Mcontext, Mcontext.getResources().getString(R.string.somthing));});
                                            } catch (Exception unused) {
                                                System.out.println("myresponseis111 exp " + unused.getMessage());
                                                dismissMyDialog();
                                                Mcontext.runOnUiThread(() -> Utils.ShowToastError(Mcontext, Mcontext.getResources().getString(R.string.somthing)));
                                            }
                                        }
                                        @Override
                                        public void onError(ANError error) {
                                            System.out.println("Apidata error " + error.getMessage());
                                        }
                                    });

                        } else if (myURLIS.contains("boloindya.com")) {
                            if (!Utils.isSocialMediaOn("boloindya.com")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            new CallBoloindyaData().execute(myURLIS);
                        } else if (myURLIS.contains("chingari")) {
                            if (!Utils.isSocialMediaOn("chingari")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CallchingariData(myURLIS);
                        } else if (myURLIS.contains("dubsmash")) {
                            if (!Utils.isSocialMediaOn("dubsmash")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            new CalldubsmashData().execute(myURLIS);
                        } else if (myURLIS.contains("bittube")) {
                            if (!Utils.isSocialMediaOn("bittube")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }

                            String myurlis1 = myURLIS;
                            if (myurlis1.contains(".tv")) {
                                String str = "/";
                                myurlis1 = myurlis1.split(str)[myurlis1.split(str).length - 1];
                                myurlis1 = "https://bittube.video/videos/watch/" +
                                        myurlis1;
                            }
                            new CallgdriveData().execute(myurlis1);

                        } else if (myURLIS.contains("drive.google.com") ||
                                myURLIS.contains("mp4upload") ||
                                myURLIS.contains("ok.ru") ||
                                myURLIS.contains("mediafire") ||
                                myURLIS.contains("gphoto") ||
                                myURLIS.contains("uptostream") ||
                                myURLIS.contains("fembed") ||
                                myURLIS.contains("cocoscope") ||
                                myURLIS.contains("sendvid") ||
                                myURLIS.contains("vivo") ||
                                myURLIS.contains("4shared")) {
                            if (!Utils.isSocialMediaOn("drive.google.com") ||
                                    !Utils.isSocialMediaOn("mp4upload") ||
                                    !Utils.isSocialMediaOn("ok.ru") ||
                                    !Utils.isSocialMediaOn("mediafire") ||
                                    !Utils.isSocialMediaOn("gphoto") ||
                                    !Utils.isSocialMediaOn("uptostream") ||
                                    !Utils.isSocialMediaOn("fembed") ||
                                    !Utils.isSocialMediaOn("cocoscope") ||
                                    !Utils.isSocialMediaOn("sendvid") ||
                                    !Utils.isSocialMediaOn("vivo") ||
                                    !Utils.isSocialMediaOn("fourShared")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            new CallgdriveData().execute(myURLIS);
                        } else if (myURLIS.contains("hind")) {
                            if (!Utils.isSocialMediaOn("hind")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            new CallhindData().execute(myURLIS);
                        } else if (myURLIS.contains("topbuzz.com")) {
                            if (!Utils.isSocialMediaOn("topbuzz.com")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            TopBuzzDownloader downloader = new TopBuzzDownloader(Mcontext, myURLIS);
                            downloader.DownloadVideo();
                        } else if (myURLIS.contains("audioboom")) {
                            if (!Utils.isSocialMediaOn("audioboom")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, true);
                        } else if (myURLIS.contains("zingmp3.vn")) {
                            if (!Utils.isSocialMediaOn("zingmp3.vn")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, true);
                        } else if (myURLIS.contains("twitter.com")) {
                            if (!Utils.isSocialMediaOn("twitter.com")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, true);
                        } else if (myURLIS.contains("gag.com")) {
                            if (!Utils.isSocialMediaOn("gag.com")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, false);
                        } else if (myURLIS.contains("buzzfeed.com")) {
                            if (!Utils.isSocialMediaOn("buzzfeed.com")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            dismissMyDialogErrortoast();
                        } else if (myURLIS.contains("redd.it") || myURLIS.contains("reddit")) {
                            if (!Utils.isSocialMediaOn("redd.it") || !Utils.isSocialMediaOn("reddit")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            Log.i("THANG1111", "ssssss");
                            CalldlApisDataData(myURLIS, true);
                        } else if (myURLIS.contains("mxtakatak")) {
                            if (!Utils.isSocialMediaOn("mxtakatak")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            String finalUrl3 = myURLIS;
                            if (finalUrl3.contains("share.mxtakatak.com")) {
                                new Thread(() -> {
                                    Looper.prepare();
                                    HttpURLConnection con;
                                    try {
                                        con = (HttpURLConnection) (new URL(finalUrl3).openConnection());
                                        con.setInstanceFollowRedirects(false);
                                        con.connect();
                                        int responseCode = con.getResponseCode();
                                        System.out.println(responseCode);
                                        String location = con.getHeaderField("Location");
                                        System.out.println(location);
                                        if (location != null && location.contains("https://www.mxtakatak.com/")) {
                                            String urls = location.split("/")[5];
                                            urls = urls.substring(0, urls.indexOf("?"));
                                            String newuuu = "https://mxshorts.akamaized.net/video/" + urls + "/download/1/h264_high_720.mp4";
                                            String nametitle = "Mxtaktak_" + System.currentTimeMillis();
                                            DownloadFileMain.startDownloading(Mcontext, newuuu, nametitle, ".mp4");
                                        }
                                        dismissMyDialog();
                                    } catch (Exception e) {
                                        dismissMyDialogErrortoast();
                                    }
                                }).start();
                            } else {
                                try {
                                    String urls = finalUrl3.split("/")[5];
                                    urls = urls.substring(0, urls.indexOf("?"));
                                    String newuuu = "https://mxshorts.akamaized.net/video/" + urls + "/download/1/h264_high_720.mp4";
                                    String nametitle = "Mxtaktak_" + System.currentTimeMillis();
                                    dismissMyDialog();
                                    DownloadFileMain.startDownloading(Mcontext, newuuu, nametitle, ".mp4");
                                } catch (Exception e) {
                                    dismissMyDialogErrortoast();
                                }
                            }
                        } else if (myURLIS.contains("test.com")) {
                            new CallgaanaData().execute(myURLIS);
                        } else if (myURLIS.contains("20min.ch")) {
                            if (!Utils.isSocialMediaOn("20min.ch")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            Twenty_min_ch_Downloader twenty_min_ch_downloader = new Twenty_min_ch_Downloader(Mcontext, myURLIS);
                            twenty_min_ch_downloader.DownloadVideo();
                        } else if (myURLIS.contains("gaana")) {
                            if (!Utils.isSocialMediaOn("gaana")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            String finalUrl = myURLIS;
                            new Thread(() -> {
                                HttpURLConnection con;
                                try {
                                    con = (HttpURLConnection) (new URL("https://tinyurl.com/f67p797b").openConnection());
                                    con.setInstanceFollowRedirects(false);
                                    con.connect();
                                    int responseCode = con.getResponseCode();
                                    System.out.println(responseCode);
                                    String location = con.getHeaderField("Location");
                                    System.out.println(location);
                                    AndroidNetworking.post(location)
                                            .addBodyParameter("url", finalUrl)
                                            .addBodyParameter("weburl", "https://video.infusiblecoder.com/")
                                            .setPriority(Priority.MEDIUM)
                                            .build()
                                            .getAsJSONObject(new JSONObjectRequestListener() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    dismissMyDialog();
                                                    String matag;
                                                    try {
                                                        JSONObject jsonObject = new JSONObject(response.toString());
                                                        matag = jsonObject.getJSONArray("songlinks").getJSONObject(0).getString("songurl");
                                                        System.out.println("wojfdjhfdjh " + matag);
                                                        DownloadFileMain.startDownloading(context, matag, "Gaana_" + System.currentTimeMillis(), ".mp3");
                                                    } catch (Exception e) {
                                                        Log.i("THANGERRROR", e.toString());
                                                        dismissMyDialog();
                                                    }
                                                }
                                                @Override
                                                public void onError(ANError error) {
                                                    System.out.println("wojfdjhfdjh error = " + error.getMessage());
                                                    dismissMyDialog();
                                                }
                                            });
                                } catch (IOException e) {
                                    Log.i("THANGERRROR", e.toString());
                                }
                            }).start();
                        } else if (myURLIS.contains("douyin")) {
                            if (!Utils.isSocialMediaOn("douyin")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            String finalUrl = myURLIS;
                            new Thread(() -> {
                                HttpURLConnection con;
                                try {
                                    String[] idis = new String[0];
                                    if (!finalUrl.contains("/video/")) {
                                        con = (HttpURLConnection) (new URL(finalUrl).openConnection());
                                        con.setInstanceFollowRedirects(false);
                                        con.connect();
                                        int responseCode = con.getResponseCode();
                                        System.out.println(responseCode);
                                        String location = con.getHeaderField("Location");
                                        System.out.println(location);
                                        if (location != null && location.contains("?")) {
                                            location = location.split("\\?")[0];
                                        }
                                        if (location != null && location.contains("/video/")) {
                                            idis = location.split("/");
                                        }
                                        System.out.println(idis[idis.length - 1]);
                                    } else {
                                        idis = finalUrl.split("/");
                                    }
                                    AndroidNetworking.get("https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + idis[idis.length - 1])
                                            .setPriority(Priority.MEDIUM)
                                            .build()
                                            .getAsJSONObject(new JSONObjectRequestListener() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    dismissMyDialog();
                                                    String matag;
                                                    try {
                                                        JSONObject jsonObject = new JSONObject(response.toString());
                                                        JSONArray itemlist = jsonObject.getJSONArray("item_list");
                                                        matag = itemlist.getJSONObject(0).getJSONObject("video").getJSONObject("play_addr").getJSONArray("url_list").getString(0);
                                                        matag = matag.replace("playwm", "play");
                                                        System.out.println("wojfdjhfdjh " + matag);
                                                        DownloadFileMain.startDownloading(context, matag, "Douyin_" + System.currentTimeMillis(), ".mp4");

                                                    } catch (Exception e) {

                                                        Log.i("THANGERRROR", e.toString());
                                                        dismissMyDialog();
                                                    }

                                                }

                                                @Override
                                                public void onError(ANError error) {
                                                    dismissMyDialogErrortoast();
                                                }
                                            });

                                } catch (IOException e) {
                                    Log.i("THANGERRROR", e.toString());
                                }
                            }).start();

                        } else if (myURLIS.contains("dailymotion") || myURLIS.contains("dai.ly")) {

                            if (!Utils.isSocialMediaOn("dailymotion") && !Utils.isSocialMediaOn("dai.ly")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }

                            CalldlApisDataData(myURLIS, false);

                        } else if (myURLIS.contains("mashable.com")) {
                            if (!Utils.isSocialMediaOn("mashable.com")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            MashableDownloader mashableDownloader = new MashableDownloader(Mcontext, myURLIS);
                            mashableDownloader.DownloadVideo();

                        } else if (myURLIS.contains("kickstarter")) {

                            if (!Utils.isSocialMediaOn("kickstarter")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }

                            KickstarterDownloader kickstarterDownloader = new KickstarterDownloader(Mcontext, myURLIS);
                            kickstarterDownloader.DownloadVideo();

                        } else if (myURLIS.contains("aparat")) {
                            if (!Utils.isSocialMediaOn("aparat")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            AparatDownloader aparatDownloader = new AparatDownloader(Mcontext, myURLIS);
                            aparatDownloader.DownloadVideo();

                        } else if (myURLIS.contains("allocine.fr")) {
                            if (!Utils.isSocialMediaOn("allocine.fr")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            AllocineDownloader allocineDownloader = new AllocineDownloader(Mcontext, myURLIS);
                            allocineDownloader.DownloadVideo();

                        } else if (myURLIS.contains("imdb.com")) {
                            if (!Utils.isSocialMediaOn("imdb.com")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            new CallIMDBData().execute(myURLIS);

                        } else if (myURLIS.contains("camdemy")) {
                            if (!Utils.isSocialMediaOn("camdemy")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, false);

                        } else if (myURLIS.contains("pinterest") || myURLIS.contains("pin.it")) {

                            if (!Utils.isSocialMediaOn("pinterest") && !Utils.isSocialMediaOn("pin.it")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }

                            System.out.println("sjdfjsdj 56 " + myURLIS);
                            CalldlApisDataData(myURLIS, true);

                        } else if (myURLIS.contains("tumblr.com")) {
                            if (!Utils.isSocialMediaOn("tumblr.com")) {
                                dismissMyDialogErrorToastForBlockedWebsitePanel();
                                return;
                            }
                            CalldlApisDataData(myURLIS, true);
                        } else {

                            CalldlApisDataData(myURLIS, true);
                        }

                        prefs = Mcontext.getSharedPreferences("AppConfig", MODE_PRIVATE);
                    } else {
                        dismissMyDialog();
                    }

                });
            });

        } catch (Exception e) {
            Log.i("THANGERRROR", e.toString());
        }
    }


    public static void getDataFromNewJetApi(String videoUrl, DataCallBackNewJetApi callBackNewJetApi) {

        if (!NewJetApi.isAllowedDomain(videoUrl)) {
            Log.d("isAllowedDomain: ", "unsupported domain " + videoUrl);
            callBackNewJetApi.onResult(false);
            return;
        }


        NewJetApi.sendNewJetApiDataRequest(Mcontext, videoUrl, encryptedData -> {
            try {
                if (encryptedData != null) {


                    // Process the response here
                    Log.d("LOGClipboard111111 clip", "Response: " + encryptedData);

                    JSONObject jsonObject = new JSONObject(encryptedData);

                    if (jsonObject.has("error") && jsonObject.getBoolean("error")) {
                        String errorMessage = jsonObject.getString("message");
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

                                // If it's a TikTok video and one has already been downloaded, skip further downloads
                                if (source.contains("tiktok") && mediaType.equals("video")) {
                                    if (isTikTokVideoDownloaded) {
                                        continue; // Skip this video
                                    }
                                    isTikTokVideoDownloaded = true; // Mark TikTok video as downloaded
                                }


                                switch (mediaType) {
                                    case "video":
                                        DownloadFileMain.startDownloading(Mcontext, mediaUrl, nametitle, ".mp4");
                                        break;

                                    case "image":
                                        DownloadFileMain.startDownloading(Mcontext, mediaUrl, nametitle, ".jpg");
                                        break;
                                    default:
                                        callBackNewJetApi.onResult(false);
                                        return;
                                }
                            }

                            callBackNewJetApi.onResult(true);
                        } catch (Exception e) {
                            Log.i("THANGERRROR", e.toString());
                            callBackNewJetApi.onResult(false);
                        }
                    };


                    mainHandler.post(runnable);
                } else {
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(() -> {
                        callBackNewJetApi.onResult(false);
                        System.err.println("Request failed with code: " + encryptedData);
                    });
                }
            } catch (Exception e) {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> {
                    callBackNewJetApi.onResult(false);
                    Log.i("THANGERRROR", e.toString());
                });
            }
        });
    }

    private static void getTrillerData(String url1) {

        new Thread(() -> {
            HttpURLConnection con = null;
            try {
                con = (HttpURLConnection) (new URL(url1).openConnection());

                con.setInstanceFollowRedirects(false);
                con.connect();
                int responseCode = con.getResponseCode();
                System.out.println(responseCode);
                String location = con.getHeaderField("Location");
                System.out.println(location);

                newurl = "https://social.triller.co/v1.5/api/videos/" + location.substring(location.indexOf("/video/") + 7);

                System.out.println("mydnewurlis=" + newurl);

                AndroidNetworking.get(newurl)
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                System.out.println("mydnewurlis res =" + response);

                                dismissMyDialog();
                                String matag;
                                try {

                                    matag = response.getJSONArray("videos").getJSONObject(0).getString("video_url");

                                    DownloadFileMain.startDownloading(Mcontext, matag, "Triller_" + System.currentTimeMillis(), ".mp4");

                                } catch (Exception e) {

                                    Log.i("THANGERRROR", e.toString());
                                    dismissMyDialog();
                                }

                            }

                            @Override /**/
                            public void onError(ANError error) {

                                System.out.println("wojfdjhfdjh error = " + error.getMessage());

                                dismissMyDialog();
                            }
                        });

            } catch (IOException e) {
                Log.i("THANGERRROR", e.toString());
            }
        }).start();

    }

    private static void CallchingariData(String url) {
        try {
            String[] urlstr = url.split("=");

            AndroidNetworking.get("https://api.chingari.io/post/post_details/" + urlstr[1])
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {

                            dismissMyDialog();
                            String matag = "";
                            try {
                                System.out.println("fjhjfhjsdfsdhf " + response);

                                JSONObject jsonObject = new JSONObject(response.toString());

                                JSONObject transcode = jsonObject.getJSONObject("data").getJSONObject("mediaLocation").getJSONObject("transcoded");

                                if (transcode.has("p1024")) {
                                    matag = transcode.getString("p1024");
                                } else if (transcode.has("p720")) {
                                    matag = transcode.getString("p720");

                                } else if (transcode.has("p480")) {
                                    matag = transcode.getString("p480");

                                }
                                matag = "https://media.chingari.io" + matag;
                                System.out.println("wojfdjhfdjh " + matag);
                                DownloadFileMain.startDownloading(Mcontext, matag, "Chingari_" + System.currentTimeMillis(), ".mp4");

                            } catch (Exception e) {
                                dismissMyDialog();
                                Log.i("THANGERRROR", e.toString());
                            }

                        }

                        @Override
                        public void onError(ANError error) {
                            dismissMyDialog();
                        }
                    });

        } catch (Exception e) {
            Log.i("THANGERRROR", e.toString());
        }
    }


    public static void dismissMyDialog() {

        if (pd != null && pd.isShowing() && !fromService && !Mcontext.isFinishing()) {
            pd.dismiss();
        }
    }

    public static void dismissMyDialogErrortoast() {

        if (pd != null && pd.isShowing() && !fromService && !Mcontext.isFinishing()) {
            pd.dismiss();
            Mcontext.runOnUiThread(() -> {
                Utils.ShowToastError(Mcontext,
                        Mcontext.getResources().getString(R.string.somthing)
                );
            });
        }
    }

    public static void dismissMyDialogErrorToastForBlockedWebsitePanel() {

        if (pd != null && pd.isShowing() && !fromService && !Mcontext.isFinishing()) {
            pd.dismiss();
            Mcontext.runOnUiThread(() -> {
                Utils.ShowToast(Mcontext,
                        Mcontext.getResources().getString(R.string.somthing_webiste_panele_block)
                );
            });
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
                //  readLine = bufferedReader.readLine();
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
            Log.i("THANGERRROR", e.toString());
        }

    }

    private static void addButtonToMainLayouttest_allvideo(final String videoTitle, String ytfile, String video_title) {
        try {

            // Display some buttons and let the user choose the format
            Button btn = new Button(Mcontext);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 8, 8, 8);
            btn.setLayoutParams(params);

            btn.setBackground(Mcontext.getResources().getDrawable(R.drawable.btn_bg_download_screen));
            btn.setTextColor(Color.WHITE);

            btn.setText(videoTitle);
            btn.setOnClickListener(v -> {

                if (windowManager2 != null) {
                    try {
                        windowManager2.removeView(mChatHeadView);
                    } catch (Exception e) {
                        Log.i("LOGClipboard111111", "error is " + e.getMessage());

                    }
                }


                if (videoTitle.equals("audio/mp4")) {
                    DownloadFileMain.startDownloading(Mcontext, ytfile, video_title + "_" + videoTitle, ".mp3");
                } else {
                    DownloadFileMain.startDownloading(Mcontext, ytfile, video_title + "_" + videoTitle, ".mp4");

                }
                dialog_quality_allvids.dismiss();
            });
            mainLayout.addView(btn);
        } catch (Exception e) {
            Log.i("THANGERRROR", e.toString());
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


    private static void splitDataToVideoAndAudio_video(List<Video> videoList, RecyclerView recyclerView_video, RecyclerView recyclerView_audio, QualityBottomsheetAdapter qualityBottomsheetAdapter, String extractor) {

        List<Video> videoList_sub = new ArrayList<>();
        List<Video> videoList_sub_video = new ArrayList<>();
        for (int i = 0; i < videoList.size(); i++) {

            if (videoList.get(i).getProtocol().contains("http") && !videoList.get(i).getProtocol().contains("http_dash_segments") && !videoList.get(i).getURL().contains(".m3u8") && !videoList.get(i).getProtocol().contains("m3u8_native")) {

                System.out.println("fhnsdkjhffjhsfhsdfjksdfsdhfkjs vifro= " + videoList.get(i).getURL());

                if (videoList.get(i).getEXT().equals("m4a") ||
                        videoList.get(i).getEXT().equals("mp3") ||
                        videoList.get(i).getEXT().equals("wav")) {
                    videoList_sub.add(videoList.get(i));
                } else if (videoList.get(i).getEXT().equals("mp4") || videoList.get(i).getEXT().equals("mpeg")) {

                    videoList_sub_video.add(videoList.get(i));

                }
            }
        }

        Collections.reverse(videoList_sub_video);

        qualityBottomsheetAdapter = new QualityBottomsheetAdapter(Mcontext, videoList_sub_video.get(0).getExtractor(), false, videoList_sub_video, true);
        recyclerView_video.setAdapter(qualityBottomsheetAdapter);

        qualityBottomsheetAdapter = new QualityBottomsheetAdapter(Mcontext, videoList_sub.get(0).getExtractor(), false, videoList_sub, true);
        recyclerView_audio.setAdapter(qualityBottomsheetAdapter);

    }

    private static void splitDataToVideoAndAudio_format(List<Format> formatList, RecyclerView recyclerView_video, RecyclerView recyclerView_audio, QualityBottomsheetAdapter qualityBottomsheetAdapter, String extractor) {

        List<Format> formatList_sub = new ArrayList<>();
        List<Format> formatList_sub_video = new ArrayList<>();
        for (int i = 0; i < formatList.size(); i++) {

            if (formatList.get(i).getProtocol().contains("http") && !formatList.get(i).getProtocol().contains("http_dash_segments") && !formatList.get(i).getURL().contains(".m3u8") && !formatList.get(i).getProtocol().contains("m3u8_native")) {

                if (formatList.get(i).getAcodec() != null && !formatList.get(i).getAcodec().equals("none")) {

                    if (formatList.get(i).getEXT().equals("m4a") ||
                            formatList.get(i).getEXT().equals("mp3") ||
                            formatList.get(i).getEXT().equals("wav")) {
                        formatList_sub.add(formatList.get(i));
                    } else if (formatList.get(i).getEXT().equals("mp4") || formatList.get(i).getEXT().equals("unknown_video") || formatList.get(i).getEXT().equals("mpeg")) {

                        formatList_sub_video.add(formatList.get(i));

                    }
                } else {
                    System.out.println("reccccc VVKK 8 " + formatList.get(i).getEXT());

                    if (formatList.get(i).getEXT().equals("m4a") ||
                            formatList.get(i).getEXT().equals("mp3") ||
                            formatList.get(i).getEXT().equals("wav")) {
                        formatList_sub.add(formatList.get(i));
                    } else if (formatList.get(i).getEXT().equals("mp4") || formatList.get(i).getEXT().equals("unknown_video") || formatList.get(i).getEXT().equals("mpeg")) {
                        System.out.println("reccccc VVKK 9 " + formatList.get(i).getURL());

                        formatList_sub_video.add(formatList.get(i));

                    }

                    //                   formatList.get(i).setFormat("(no audio) " + formatList.get(i).getFormat());
                    formatList.get(i).setFormat(formatList.get(i).getFormat());

                }

            }
        }

        Collections.reverse(formatList_sub_video);

        qualityBottomsheetAdapter = new QualityBottomsheetAdapter(Mcontext, formatList_sub_video, extractor, false);
        recyclerView_video.setAdapter(qualityBottomsheetAdapter);

        qualityBottomsheetAdapter = new QualityBottomsheetAdapter(Mcontext, formatList_sub, extractor, false);
        recyclerView_audio.setAdapter(qualityBottomsheetAdapter);

    }

    @Keep
    public static void CalldlApisDataData(String url, boolean hasQualityOption) {

        System.out.println("reccc " + url);
        callDlApiNew(url);

    }


    static void callBackup(String url) {
        AndroidNetworking.get("http://199.192.20.82:9191/api/info?url=" + url + "&flatten=True")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("reccccc VVKK1 " + response);
                        // Log.e("myresponse ",response.toString());
                        parseCalldlApisDataData(response, url, true);
                    }

                    @Override
                    public void onError(ANError error) {
                        System.out.println("reccccc VVKK1 error " + error.getErrorBody());

                        dismissMyDialogErrortoast();

                    }
                });
    }


    static void callDlApiNew(String url) {


        Executors.newSingleThreadExecutor().submit(() -> {

            try {

                streamInfo = YoutubeDL.getInstance().getInfo(url);

                Mcontext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        parseCalldlApisLocalDataData(streamInfo, url);

                    }
                });

            } catch (Throwable e) {
//                dismissMyDialogErrortoast();

                callBackup(url);
                System.err.println("YTDLP ERROR" + e.getLocalizedMessage());

            }
        });

    }


    @Keep
    public static void parseCalldlApisLocalDataData(VideoInfo response, String url) {
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

                System.out.println("reccccc VVKKtttt " + response.getTitle());

                String titletyledText = "Title: <font color='red'>" + String.format("%s", response.getTitle()) + "</font>";
                title_bottomsheet.setText(Html.fromHtml(titletyledText), TextView.BufferType.SPANNABLE);
            }

            System.out.println("reccccc VVKK thumb " + response.getThumbnail());


            if (!TextUtils.isEmpty(response.getThumbnail())) {

                Glide.with(Mcontext)
                        .load(response.getThumbnail())
                        .placeholder(R.drawable.ic_launcher).into(thumb_bottomsheet);

            }
            // source_bottomsheet.setText(String.format("Source: %s", gsonObj.getVideos().get(0).getExtractor()));
            btncancel_bottomsheet.setOnClickListener(v -> dialog.cancel());
            btnopen_bottomsheet.setOnClickListener(v -> {
                if (dialog.getBehavior().getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    dialog.getBehavior().setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    dialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                dialog.getBehavior().setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
                btnopen_bottomsheet.animate().rotationBy(180f).start();
            });

            dialog.setContentView(view);
            dialog.show();
            dismissMyDialog();
        } catch (Exception str2) {
            System.out.println("reccccc VVKK Error= " + str2);
            dismissMyDialogErrortoast();
        }

    }


    @Keep
    public static void parseCalldlApisDataData(JSONObject response, String url, boolean isFromBackup) {
        try {

            try {
                if (!response.getString("error").isEmpty()) {
                    if (isFromBackup) {
                        dismissMyDialogErrortoast();
                    } else {
                        callBackup(url);
                    }
                }

            } catch (Exception e) {

                Gson gson = new Gson();

                DLDataParser gsonObj = gson.fromJson(response.toString(), DLDataParser.class);

                System.out.println("reccccc VVKK " + gsonObj.getURL());

                View view = LayoutInflater.from(Mcontext).inflate(R.layout.bottomsheet_quality_layout, null);

                Button btncancel_bottomsheet = view.findViewById(R.id.btncancel_bottomsheet);
                Button btnopen_bottomsheet = view.findViewById(R.id.btnopen_bottomsheet);
                TextView source_bottomsheet = view.findViewById(R.id.source_bottomsheet);
                TextView title_bottomsheet = view.findViewById(R.id.bottomsheet_title);
                TextView duration_bottomsheet = view.findViewById(R.id.bottomsheet_duration);
                ImageView thumb_bottomsheet = view.findViewById(R.id.bottomsheet_thumbnail);

                RecyclerView recyclerView = view.findViewById(R.id.recqualitybottomsheet);

                recyclerView.setLayoutManager(new LinearLayoutManager(Mcontext));

                RecyclerView recyclerView_audio = view.findViewById(R.id.recqualitybottomsheet_aud);
                recyclerView_audio.setHasFixedSize(true);
                recyclerView_audio.setLayoutManager(new LinearLayoutManager(Mcontext));

                QualityBottomsheetAdapter qualityBottomsheetAdapter = null;

                System.out.println("reccc lengthe iss= " + response.getJSONArray("videos").length());

                if (response.getJSONArray("videos").length() > 1) {

                    System.out.println("reccccc VVKK 0 ");

                    System.out.println("reccccc VVKK 1 ");

                    if (response.getJSONArray("videos").getJSONObject(0).has("protocol")) {

                        System.out.println("reccccc VVKK 2");

                        System.out.println("reccccc VVKK 3 ");

                        splitDataToVideoAndAudio_video(gsonObj.getVideos(), recyclerView, recyclerView_audio, qualityBottomsheetAdapter, gsonObj.getVideos().get(0).getExtractor());

                    }

                    BottomSheetDialog dialog = new BottomSheetDialog(Mcontext);

                    if (response.getJSONArray("videos").getJSONObject(0).has("extractor")) {
                        String styledText = "";

                        switch (gsonObj.getVideos().get(0).getExtractor()) {
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
                                styledText = "Source: <font color='red'>" + gsonObj.getVideos().get(0).getExtractor() + "</font>";

                                break;
                            }

                        }
                        source_bottomsheet.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
                    }

                    if (response.getJSONArray("videos").getJSONObject(0).has("duration")) {

                        String mystring = gsonObj.getVideos().get(0).getDuration();
                        String[] correctstring = mystring.split("\\.");

                        long hours = Long.parseLong(correctstring[0]) / 3600;
                        long minutes = (Long.parseLong(correctstring[0]) % 3600) / 60;
                        long seconds = Long.parseLong(correctstring[0]) % 60;

                        String DurationstyledText = "Duration: <font color='red'>" + String.format("%02d:%02d:%02d", hours, minutes, seconds) + "</font>";
                        duration_bottomsheet.setText(Html.fromHtml(DurationstyledText), TextView.BufferType.SPANNABLE);
                    }

                    if (response.getJSONArray("videos").getJSONObject(0).has("title")) {

                        System.out.println("reccccc VVKKtttt " + gsonObj.getVideos().get(0).getTitle());

                        String titletyledText = "Title: <font color='red'>" + String.format("%s", gsonObj.getVideos().get(0).getTitle()) + "</font>";
                        title_bottomsheet.setText(Html.fromHtml(titletyledText), TextView.BufferType.SPANNABLE);
                    }

                    if (response.getJSONArray("videos").getJSONObject(0).has("thumbnail")) {

                        Glide.with(Mcontext)
                                .load(gsonObj.getVideos().get(0).getThumbnail())
                                .placeholder(R.drawable.ic_launcher).into(thumb_bottomsheet);

                    }
                    btncancel_bottomsheet.setOnClickListener(v -> dialog.cancel());

                    btnopen_bottomsheet.setOnClickListener(v -> {
                        dialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
                        dialog.getBehavior().setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
                    });

                    dialog.setContentView(view);
                    dialog.show();

                    dismissMyDialog();

                } else {

                    System.out.println("reccccc VVKK 6 ");

                    if (response.getJSONArray("videos").getJSONObject(0).has("formats")) {
                        System.out.println("reccccc VVKK 7 ");

                        splitDataToVideoAndAudio_format(gsonObj.getVideos().get(0).getFormats(), recyclerView, recyclerView_audio, qualityBottomsheetAdapter, gsonObj.getVideos().get(0).getExtractor());

                    } else {
                        if (response.getJSONArray("videos").getJSONObject(0).has("protocol")) {

                            String ishttp = response.getJSONArray("videos").getJSONObject(0).getString("protocol");
                            if (ishttp.contains("http")) {
                                qualityBottomsheetAdapter = new QualityBottomsheetAdapter(Mcontext, gsonObj.getVideos().get(0).getURL(), gsonObj.getVideos().get(0).getExtractor(), true);
                                recyclerView.setAdapter(qualityBottomsheetAdapter);

                            }
                        }
                    }

                    if (!Mcontext.isFinishing()) {

                        BottomSheetDialog dialog = new BottomSheetDialog(Mcontext);


                        if (response.getJSONArray("videos").getJSONObject(0).has("extractor")) {
                            String styledText = "";

                            switch (gsonObj.getVideos().get(0).getExtractor()) {
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
                                    styledText = "Source: <font color='red'>" + gsonObj.getVideos().get(0).getExtractor() + "</font>";

                                    break;
                                }

                            }
                            source_bottomsheet.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
                        }


                        if (response.getJSONArray("videos").getJSONObject(0).has("duration")) {

                            String mystring = gsonObj.getVideos().get(0).getDuration();
                            String[] correctstring = mystring.split("\\.");

                            long hours = Long.parseLong(correctstring[0]) / 3600;
                            long minutes = (Long.parseLong(correctstring[0]) % 3600) / 60;
                            long seconds = Long.parseLong(correctstring[0]) % 60;

                            String DurationstyledText = "Duration: <font color='red'>" + String.format("%02d:%02d:%02d", hours, minutes, seconds) + "</font>";
                            duration_bottomsheet.setText(Html.fromHtml(DurationstyledText), TextView.BufferType.SPANNABLE);
                        }

                        if (response.getJSONArray("videos").getJSONObject(0).has("title")) {

                            System.out.println("reccccc VVKKtttt " + gsonObj.getVideos().get(0).getTitle());

                            String titletyledText = "Title: <font color='red'>" + String.format("%s", gsonObj.getVideos().get(0).getTitle()) + "</font>";
                            title_bottomsheet.setText(Html.fromHtml(titletyledText), TextView.BufferType.SPANNABLE);
                        }

                        if (response.getJSONArray("videos").getJSONObject(0).has("thumbnail")) {

                            Glide.with(Mcontext)
                                    .load(gsonObj.getVideos().get(0).getThumbnail())
                                    .placeholder(R.drawable.ic_launcher).into(thumb_bottomsheet);

                        }
                        btncancel_bottomsheet.setOnClickListener(v -> dialog.cancel());

                        dialog.setContentView(view);
                        dialog.show();

                    }
                    dismissMyDialog();

                }

            }
        } catch (Exception str2) {
            System.out.println("reccccc VVKK Error= " + str2);

            dismissMyDialogErrortoast();
        }

    }


    private static class callGetRoposoData extends AsyncTask<String, Void, Document> {
        Document ShareChatDoc;

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Document doInBackground(String... strArr) {
            try {
                this.ShareChatDoc = Jsoup.connect(strArr[0]).get();
            } catch (Exception strArr2) {
                strArr2.printStackTrace();
                Log.d("ContentValues roposo_", "doInBackground: Error");
            }
            return this.ShareChatDoc;
        }

        protected void onPostExecute(Document document) {
            String charSequence = "";

            try {

                dismissMyDialog();

                String html = document.select("script[id=\"__NEXT_DATA__\"]").last().html();
                System.out.println("myresponseis111 html " + html);

                if (!html.isEmpty()) {
                    VideoUrl = new JSONObject(html)
                            .getJSONObject("props")
                            .getJSONObject("pageProps")
                            .getJSONObject("meta")
                            .getString("og:video");
                    if (!VideoUrl.isEmpty()) {
                        try {

                            String nametitle = "roposo_" +
                                    System.currentTimeMillis() +
                                    ".mp4";

                            DownloadFileMain.startDownloading(Mcontext, VideoUrl, nametitle, ".mp4");

                            VideoUrl = charSequence;

                        } catch (Exception document2) {
                            System.out.println("myresponseis111 exp1 " + document2.getMessage());
                            dismissMyDialog();
                            document2.printStackTrace();
                            Mcontext.runOnUiThread(() -> {
                                Utils.ShowToastError(Mcontext,
                                        Mcontext.getResources().getString(R.string.somthing)
                                );
                            });
                        }

                    }
                }

            } catch (Exception document22) {
                document22.printStackTrace();
                Mcontext.runOnUiThread(() -> {
                    Utils.ShowToastError(Mcontext,
                            Mcontext.getResources().getString(R.string.somthing)
                    );
                });
                dismissMyDialog();
            }
        }
    }

    public static class CallMitronData extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            String str;
            try {
                String str2 = strArr[0];
                if (str2.contains("api.mitron.tv")) {
                    String[] split = str2.split("=");
                    str = "https://web.mitron.tv/video/" + split[split.length - 1];
                } else {
                    str = strArr[0];
                }
                this.RoposoDoc = Jsoup.connect(str).get();
            } catch (IOException e) {
                Log.i("THANGERRROR", e.toString());
                dismissMyDialog();
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {


            try {

                dismissMyDialog();

                String html = document.select("script[id=\"__NEXT_DATA__\"]").last().html();
                if (!html.isEmpty()) {
                    this.VideoUrl = String.valueOf(new JSONObject(html)
                            .getJSONObject("props")
                            .getJSONObject("pageProps").getJSONObject("video").get("videoUrl"));
                    if (!this.VideoUrl.isEmpty()) {
                        try {

                            String myurldocument = VideoUrl;

                            String nametitle = "mitron_" +
                                    System.currentTimeMillis();

                            DownloadFileMain.startDownloading(Mcontext, myurldocument, nametitle, ".mp4");

                            VideoUrl = "";

                        } catch (Exception document2) {
                            System.out.println("myresponseis111 exp1 " + document2.getMessage());
                            dismissMyDialog();
                            document2.printStackTrace();
                            Mcontext.runOnUiThread(() -> {
                                Utils.ShowToastError(Mcontext,
                                        Mcontext.getResources().getString(R.string.somthing)
                                );
                            });
                        }

                        return;
                    }
                    return;
                }
                Mcontext.runOnUiThread(() -> {
                    Utils.ShowToastError(Mcontext,
                            Mcontext.getResources().getString(R.string.somthing)
                    );
                });
            } catch (Exception unused) {
                System.out.println("myresponseis111 exp " + unused.getMessage());

                dismissMyDialog();
                Mcontext.runOnUiThread(() -> {
                    Utils.ShowToastError(Mcontext,
                            Mcontext.getResources().getString(R.string.somthing)
                    );
                });
            }
        }

    }

    public static class CallIMDBData extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            try {
                String str2 = strArr[0];

                this.RoposoDoc = Jsoup.connect(str2).get();
            } catch (IOException e) {
                Log.i("THANGERRROR", e.toString());
                dismissMyDialog();
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {

            try {

                dismissMyDialog();

                String html = document.select("script[id=\"__NEXT_DATA__\"]").last().html();

                if (!html.isEmpty()) {

                    JSONArray listOfUrls = new JSONObject(html)
                            .getJSONObject("props")
                            .getJSONObject("pageProps")
                            .getJSONObject("videoPlaybackData")
                            .getJSONObject("video")
                            .getJSONArray("playbackURLs");

                    ArrayList<String> arrayList = new ArrayList<>();
                    ArrayList<String> qualityArrayList = new ArrayList<>();

                    for (int i = 1; i < listOfUrls.length(); i++) {
                        System.out.println("myressss urls " + listOfUrls.getJSONObject(i).getString("url"));

                        arrayList.add(listOfUrls.getJSONObject(i).getString("url"));
                        qualityArrayList.add(listOfUrls.getJSONObject(i).getJSONObject("displayName").getString("value"));

                    }
                    CharSequence[] charSequenceArr = qualityArrayList.toArray(new String[0]);
                    if (!Mcontext.isFinishing()) {
                        new AlertDialog.Builder(Mcontext).setTitle("Quality!")
                                .setItems(charSequenceArr, (dialogInterface, i) -> DownloadFileMain.startDownloading(Mcontext, arrayList.get(i), "IMDB_" + System.currentTimeMillis(), ".mp4"))
                                .setPositiveButton("OK", (dialogInterface, i) -> dismissMyDialog()).setCancelable(false).show();
                    }
                } else {
                    dismissMyDialogErrortoast();
                }
            } catch (Throwable f) {
                System.out.println("myresponseis111 exp " + f.getMessage());

                f.printStackTrace();
                dismissMyDialogErrortoast();

            }
        }

    }

    public static class CallJoshData extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";
        String link = "";

        String getVideoIdjosh(String link) {

            if (link.contains("?")) {
                link = link.split("\\?")[0];
            }
            link = (link.contains("video")) ? link.substring(link.indexOf("video")) : link.substring(link.indexOf("content"));
            link = link.substring(link.indexOf("/") + 1);
            return link;
        }

        public Document doInBackground(String... strArr) {
            try {

                link = getVideoIdjosh(strArr[0]);
            } catch (Exception e) {
                Log.i("THANGERRROR", e.toString());
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {
            try {
                JSONObject snackvideoDoc = new JSONObject("{\"method\": \"GET\",\"url\": \"https://api-internal.myjosh.in/share/content/" + link + "\",\"platform\": \"PWA\"}");

                AndroidNetworking.post("https://share.myjosh.in/webview/apiwbody")
                        .addJSONObjectBody(snackvideoDoc)
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                System.out.println("wojfdjhfdjh " + response);

                                try {
                                    dismissMyDialog();

                                    VideoUrl = response.getJSONObject("data").getString("mp4_url");

                                    String myurldocument = VideoUrl.replace("_r4_wmj_480.mp4", ".mp4");

                                    String nametitle = "joshvideo_" +
                                            System.currentTimeMillis();

                                    DownloadFileMain.startDownloading(Mcontext, myurldocument, nametitle, ".mp4");

                                    VideoUrl = "";

                                } catch (Exception document2) {
                                    System.out.println("myresponseis111 exp1 " + document2.getMessage());
                                    dismissMyDialog();
                                    document2.printStackTrace();
                                    Mcontext.runOnUiThread(() -> {
                                        Utils.ShowToastError(Mcontext,
                                                Mcontext.getResources().getString(R.string.somthing)
                                        );
                                    });
                                }

                            }

                            @Override
                            public void onError(ANError error) {

                                error.printStackTrace();
                                dismissMyDialogErrortoast();
                            }
                        });

            } catch (Exception eee) {
                System.out.println("myresponseis111 exp " + eee.getMessage());

                dismissMyDialog();
                Mcontext.runOnUiThread(() -> {
                    Utils.ShowToastError(Mcontext,
                            Mcontext.getResources().getString(R.string.somthing)
                    );
                });
            }
        }

    }

    public static class CallRizzleData extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            try {
                this.RoposoDoc = Jsoup.connect(strArr[0]).get();
            } catch (IOException e) {
                Log.i("THANGERRROR", e.toString());
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {

            try {

                dismissMyDialog();

                String html = document.select("script[id=\"__NEXT_DATA__\"]").last().html();
                if (!html.isEmpty()) {
                    this.VideoUrl = String.valueOf(new JSONObject(html).getJSONObject("props").getJSONObject("pageProps").getJSONObject("post").getJSONObject("video").get("originalUrl"));
                    if (!this.VideoUrl.isEmpty()) {
                        try {

                            String myurldocument = VideoUrl;

                            String nametitle = "rizzlevideo_" +
                                    System.currentTimeMillis();

                            DownloadFileMain.startDownloading(Mcontext, myurldocument, nametitle, ".mp4");
                            VideoUrl = "";
                        } catch (Exception document2) {
                            System.out.println("myresponseis111 exp1 " + document2.getMessage());
                            dismissMyDialog();
                            document2.printStackTrace();
                            Mcontext.runOnUiThread(() -> {
                                Utils.ShowToastError(Mcontext,
                                        Mcontext.getResources().getString(R.string.somthing)
                                );
                            });
                        }

                        return;
                    }
                    return;
                }
                Mcontext.runOnUiThread(() -> {
                    Utils.ShowToastError(Mcontext,
                            Mcontext.getResources().getString(R.string.somthing)
                    );
                });
            } catch (Exception unused) {
                System.out.println("myresponseis111 exp " + unused.getMessage());

                dismissMyDialog();
                Mcontext.runOnUiThread(() -> {
                    Utils.ShowToastError(Mcontext,
                            Mcontext.getResources().getString(R.string.somthing)
                    );
                });
            }
        }

    }

    public static class CallIfunnyData extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            try {
                this.RoposoDoc = Jsoup.connect(strArr[0]).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36").get();
            } catch (Exception e) {
                Log.i("THANGERRROR", e.toString());
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {

            try {

                dismissMyDialog();
                this.VideoUrl = document.select("meta[property=\"og:video:url\"]").last().attr("content");

                if (!this.VideoUrl.isEmpty()) {
                    try {

                        String myurldocument = VideoUrl;

                        String nametitle = "ifunnyvideo_" +
                                System.currentTimeMillis();

                        DownloadFileMain.startDownloading(Mcontext, myurldocument, nametitle, ".mp4");
                        VideoUrl = "";

                    } catch (Exception document2) {
                        System.out.println("myresponseis111 exp1 " + document2.getMessage());
                        dismissMyDialog();
                        document2.printStackTrace();
                        Mcontext.runOnUiThread(() -> {
                            Utils.ShowToastError(Mcontext,
                                    Mcontext.getResources().getString(R.string.somthing)
                            );
                        });
                    }

                    return;
                }

                Mcontext.runOnUiThread(() -> Utils.ShowToastError(Mcontext,
                        Mcontext.getResources().getString(R.string.somthing)
                ));
            } catch (Exception unused) {
                System.out.println("myresponseis111 exp " + unused.getMessage());

                dismissMyDialog();
                Mcontext.runOnUiThread(() -> {
                    Utils.ShowToastError(Mcontext,
                            Mcontext.getResources().getString(R.string.somthing)
                    );
                });
            }
        }

    }

    public static class CallBoloindyaData extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            try {
                this.RoposoDoc = Jsoup.connect(strArr[0]).get();
            } catch (Exception e) {
                Log.i("THANGERRROR", e.toString());
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {

            try {

                dismissMyDialog();

                for (Element element : document.getElementsByTag("script")) {
                    if (element.data().contains("videoFileCDN")) {
                        for (String str : element.data().split(StringUtils.LF)) {
                            if (str.contains("var videoFileCDN=\"https")) {
                                this.VideoUrl = str.split("=")[1]
                                        .replace("\"", "")
                                        .replace("\"", "")
                                        .replace(";", "");
                            }
                        }
                    }
                }
                if (this.VideoUrl.startsWith("//")) {
                    this.VideoUrl = "https:" + this.VideoUrl;
                }
                if (!this.VideoUrl.isEmpty()) {
                    try {

                        String myurldocument = VideoUrl;

                        String nametitle = "Boloindyavideo_" +
                                System.currentTimeMillis();

                        DownloadFileMain.startDownloading(Mcontext, myurldocument, nametitle, ".mp4");
                        VideoUrl = "";

                    } catch (Exception document2) {
                        System.out.println("myresponseis111 exp1 " + document2.getMessage());
                        dismissMyDialog();
                        document2.printStackTrace();
                        Mcontext.runOnUiThread(() -> {
                            Utils.ShowToastError(Mcontext,
                                    Mcontext.getResources().getString(R.string.somthing)
                            );
                        });
                    }

                    return;
                }

                Mcontext.runOnUiThread(() -> {
                    Utils.ShowToastError(Mcontext,
                            Mcontext.getResources().getString(R.string.somthing)
                    );
                });
            } catch (Exception unused) {
                System.out.println("myresponseis111 exp " + unused.getMessage());

                dismissMyDialog();
                Mcontext.runOnUiThread(() -> {
                    Utils.ShowToastError(Mcontext,
                            Mcontext.getResources().getString(R.string.somthing)
                    );
                });
            }
        }

    }

    public static class CallhindData extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            try {
                this.RoposoDoc = Jsoup.connect(strArr[0]).get();

            } catch (Exception e) {
                Log.i("THANGERRROR", e.toString());
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {

            try {
                dismissMyDialog();

                for (Element element : document.getElementsByTag("script")) {
                    if (element.data().contains("window.__STATE__")) {
                        String replace = element.html().replace("window.__STATE__", "").replace(";", "")
                                .replace("=", "");
                        this.VideoUrl = String.valueOf(new JSONObject(new JSONObject(new JSONArray("[" + replace + "]")
                                .get(0).toString()).getJSONObject("feed").getJSONArray("feed")
                                .get(0).toString()).get("download_media"));
                        if (!this.VideoUrl.isEmpty()) {
                            try {

                                String myurldocument = VideoUrl;

                                String nametitle = "hindvideo_" +
                                        System.currentTimeMillis();

                                DownloadFileMain.startDownloading(Mcontext, myurldocument, nametitle, ".mp4");

                                //  Utils.startDownload(document, str, shareChatActivity, stringBuilder);
                                VideoUrl = "";
                                //   binding.etText.setText(charSequence);

                            } catch (Exception document2) {
                                System.out.println("myresponseis111 exp1 " + document2.getMessage());
                                dismissMyDialog();
                                document2.printStackTrace();
                                Mcontext.runOnUiThread(() -> {
                                    Utils.ShowToastError(Mcontext,
                                            Mcontext.getResources().getString(R.string.somthing)
                                    );
                                });
                            }

                            return;
                        }
                    }
                }
                Mcontext.runOnUiThread(() -> {
                    Utils.ShowToastError(Mcontext,
                            Mcontext.getResources().getString(R.string.somthing)
                    );
                });
            } catch (Exception unused) {
                System.out.println("myresponseis111 exp " + unused.getMessage());

                dismissMyDialog();
                Mcontext.runOnUiThread(() -> {
                    Utils.ShowToastError(Mcontext,
                            Mcontext.getResources().getString(R.string.somthing)
                    );
                });
            }
        }

    }

    public static class CalldubsmashData extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            try {
                this.RoposoDoc = Jsoup.connect(strArr[0]).get();

            } catch (Exception e) {
                Log.i("THANGERRROR", e.toString());
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {

            try {

                dismissMyDialog();

                this.VideoUrl = document.select("video").last().attr("src");
                if (!this.VideoUrl.isEmpty()) {
                    try {

                        String myurldocument = VideoUrl;

                        String nametitle = "dubsmashvideo_" +
                                System.currentTimeMillis();

                        DownloadFileMain.startDownloading(Mcontext, myurldocument, nametitle, ".mp4");
                        VideoUrl = "";
                    } catch (Exception document2) {
                        System.out.println("myresponseis111 exp1 " + document2.getMessage());
                        dismissMyDialog();
                        document2.printStackTrace();
                        Mcontext.runOnUiThread(() -> {
                            Utils.ShowToastError(Mcontext,
                                    Mcontext.getResources().getString(R.string.somthing)
                            );
                        });
                    }

                    return;
                }

                Mcontext.runOnUiThread(() -> {
                    Utils.ShowToastError(Mcontext,
                            Mcontext.getResources().getString(R.string.somthing)
                    );
                });
            } catch (Exception unused) {
                System.out.println("myresponseis111 exp " + unused.getMessage());

                dismissMyDialog();
                Mcontext.runOnUiThread(() -> {
                    Utils.ShowToastError(Mcontext,
                            Mcontext.getResources().getString(R.string.somthing)
                    );
                });
            }
        }

    }

    public static class CalllinkedinData extends AsyncTask<String, Void, Document> {
        private final Context context;
        private Document roposoDoc;
        private String videoUrl = "";

        public CalllinkedinData(Context context) {
            this.context = context;
        }

        @Override
        protected Document doInBackground(String... strArr) {
            try {
                roposoDoc = Jsoup.connect(strArr[0])
                        .header("authority", "www.linkedin.com")
                        .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36")
                        .get();
            } catch (Exception e) {
                Log.e(TAG, "Error fetching document: ", e);
                dismissMyDialogErrortoast();
            }
            return roposoDoc;
        }

        @Override
        protected void onPostExecute(Document document) {
            Log.d(TAG, "Start: Working-Linkedin 0");

            try {

                Elements elements = document.select("script");
                int totalElements = elements.size();  // Get the total number of elements
                int currentIndex = 0;  // Initialize the current index

                boolean isElementFound = false;

                for (Element element : elements) {
                    currentIndex++;  // Increment the current index for each element

                    if ("application/ld+json".equals(element.attr("type"))) {
                        JSONObject obj = new JSONObject(element.html());
                        String typeOfElement = obj.getString("@type");
                        Log.d(TAG, "Start: Working-Linkedin 1 " + typeOfElement);

                        if (typeOfElement.contains("SocialMediaPosting")) {
                            new HandleImageDownloadTask().execute(obj);
                            isElementFound = true;
                        } else if (typeOfElement.contains("VideoObject")) {
                            handleVideoDownload(obj);
                            isElementFound = true;
                        }
                    } else {
                        Log.d(TAG, "Not-found Searching Linkedin 1.5 ");
                    }

                    // Check if it's the last iteration
                    if (currentIndex == totalElements) {
                        if (!isElementFound) {
                            Log.e(TAG, "Error: No suitable element type found.");
                            dismissMyDialogErrortoast();
                        } else {
                            dismissMyDialog();
                        }
                    }
                }


            } catch (Exception e) {
                Log.e(TAG, "Error processing document part 1 trying part 2 : ", e);
                try {

                    System.out.println("myresponseis111 exp166 linkedin" + document);

                    videoUrl = document.select("video").first().attr("data-sources");

                    JSONArray jsonArray = new JSONArray(videoUrl);
                    System.out.println("myresponseis111 exp1 " + jsonArray.getJSONObject(0).getString("src"));

                    ArrayList<String> arrayList = new ArrayList<>();
                    arrayList.add(jsonArray.getJSONObject(0).getString("src"));
                    arrayList.add(jsonArray.getJSONObject(1).getString("src"));

                    CharSequence[] charSequenceArr = new CharSequence[arrayList.size()];

                    charSequenceArr[0] = "Low quality";
                    charSequenceArr[1] = "High quality";

                    dismissMyDialog();

                    if (!Mcontext.isFinishing()) {
                        new AlertDialog.Builder(Mcontext).setTitle("Quality!").setItems(charSequenceArr, (dialogInterface, i) -> DownloadFileMain.startDownloading(Mcontext, arrayList.get(i), "Linkedin_" + System.currentTimeMillis(), ".mp4")).setPositiveButton("OK", (dialogInterface, i) -> dismissMyDialog()).setCancelable(false).show();
                    }

                } catch (Exception unused) {
                    System.out.println("myresponseis111 exp " + unused.getMessage());

                    dismissMyDialogErrortoast();
                }
            }
        }

        private String getImageExtension(String imageUrl) {
            try {
                Connection.Response response = Jsoup.connect(imageUrl)
                        .ignoreContentType(true)
                        .execute();

                String contentType = response.header("Content-Type");

                if (contentType != null) {
                    if (contentType.contains("image/png")) {
                        return ".png";
                    } else if (contentType.contains("image/jpeg") || contentType.contains("image/jpg")) {
                        return ".jpg";
                    } else if (contentType.contains("image/gif")) {
                        return ".gif";
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Error getting image extension: ", e);
            }
            return null;
        }

        private void handleVideoDownload(JSONObject obj) {
            try {
                videoUrl = obj.getString("contentUrl");
                DownloadFileMain.startDownloading(context, videoUrl, "LinkedIn_video_" + System.currentTimeMillis(), ".mp4");
            } catch (Exception e) {
                Log.e(TAG, "Error handling video download: ", e);
            }
        }

        private JSONObject getJsonData(String url) {
            try {
                String json = Jsoup.connect(url).ignoreContentType(true).execute().body();
                return new JSONObject(json);
            } catch (Exception e) {
                Log.e(TAG, "Error getting JSON data: ", e);
            }
            return new JSONObject();
        }

        private JSONArray getJsonArrayFrom(String url) {
            try {
                String json = Jsoup.connect(url).ignoreContentType(true).execute().body();
                return new JSONObject(json).getJSONArray("pages");
            } catch (Exception e) {
                Log.e(TAG, "Error getting JSON array: ", e);
            }
            return new JSONArray();
        }

        private class HandleImageDownloadTask extends AsyncTask<JSONObject, Void, Void> {
            @Override
            protected Void doInBackground(JSONObject... objs) {
                try {
                    JSONObject obj = objs[0];
                    if (obj.has("sharedContent") && obj.getJSONObject("sharedContent").has("url")) {
                        String playlistUrl = obj.getJSONObject("sharedContent").getString("url");
                        Log.d(TAG, "Start: Working-Linkedin playlist " + playlistUrl);

                        JSONObject playlistJson = getJsonData(playlistUrl);
                        JSONArray arrayJson = playlistJson.getJSONArray("perResolutions");
                        String imageManifestUrl = arrayJson.getJSONObject(arrayJson.length() - 1).getString("imageManifestUrl");

                        Log.d(TAG, "Start: Working-Linkedin playlist " + imageManifestUrl);

                        JSONArray jsonImages = getJsonArrayFrom(imageManifestUrl);
                        for (int i = 0; i < jsonImages.length(); i++) {
                            String imageUrl = jsonImages.getString(i);
                            String imageExtension = getImageExtension(imageUrl);
                            if (imageExtension == null) {
                                imageExtension = ".jpg";
                            }
                            DownloadFileMain.startDownloading(context, imageUrl, "LinkedIn_image_" + System.currentTimeMillis(), imageExtension);
                        }
                    } else if (obj.has("image") && obj.getJSONObject("image").has("url")) {
                        String imageUrl = obj.getJSONObject("image").getString("url");
                        Log.d(TAG, "Start: Working-Linkedin imageUrl " + imageUrl);

                        String imageExtension = getImageExtension(imageUrl);
                        if (imageExtension == null) {
                            imageExtension = ".jpg";
                        }
                        DownloadFileMain.startDownloading(context, imageUrl, "LinkedIn_image_" + System.currentTimeMillis(), imageExtension);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error handling image download: ", e);
                }
                return null;
            }
        }
    }

    public static class CallgaanaData extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            try {


            } catch (Exception e) {

                System.out.println("jskdfhksdhfkshdfkhsdj " + e.getMessage());
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {

            dismissMyDialog();

        }
    }

    public static class CallgdriveData extends AsyncTask<String, Void, String> {
        String VideoUrl = "";
        LowCostVideo xGetter;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            this.xGetter = new LowCostVideo(Mcontext);
            this.xGetter.onFinish(new LowCostVideo.OnTaskCompleted() {
                public void onTaskCompleted(ArrayList<XModel> arrayList, boolean z) {
                    if (!z) {

                        System.out.println("myresponseis111 exp122 " + arrayList.get(0));

                        CallgdriveData.this.done(arrayList.get(0));

                    } else if (arrayList != null) {
                        System.out.println("myresponseis111 exp133 " + arrayList.get(0));

                        CallgdriveData.this.multipleQualityDialog(arrayList);
                    } else {

                        dismissMyDialog();
                        Mcontext.runOnUiThread(() -> {
                            Utils.ShowToastError(Mcontext,
                                    Mcontext.getResources().getString(R.string.somthing)
                            );
                        });
                    }
                }

                public void onError() {

                    dismissMyDialog();
                    Mcontext.runOnUiThread(() -> {
                        Utils.ShowToastError(Mcontext,
                                Mcontext.getResources().getString(R.string.somthing)
                        );
                    });
                }
            });

        }

        public String doInBackground(String... strArr) {
            return strArr[0];
        }

        public void onPostExecute(String str) {
            System.out.println("myresponseis111 exp13344 " + str);

            if (xGetter != null) {
                this.xGetter.find(str);
            } else {
                this.xGetter = new LowCostVideo(Mcontext);
                this.xGetter.onFinish(new LowCostVideo.OnTaskCompleted() {
                    public void onTaskCompleted(ArrayList<XModel> arrayList, boolean z) {

                        System.out.println("myresponseis111 exp133 " + arrayList.get(0));

                        if (!z) {

                            System.out.println("myresponseis111 exp122 " + arrayList.get(0));

                            CallgdriveData.this.done(arrayList.get(0));

                        } else {
                            System.out.println("myresponseis111 exp133 " + arrayList.get(0));

                            CallgdriveData.this.multipleQualityDialog(arrayList);
                        }

                    }

                    public void onError() {

                        dismissMyDialog();
                        Mcontext.runOnUiThread(() -> {
                            Utils.ShowToastError(Mcontext,
                                    Mcontext.getResources().getString(R.string.somthing)
                            );
                        });
                    }
                });
                this.xGetter.find(str);
            }
        }

        public void multipleQualityDialog(final ArrayList<XModel> arrayList) {
            CharSequence[] charSequenceArr = new CharSequence[arrayList.size()];
            for (int i = 0; i < arrayList.size(); i++) {
                charSequenceArr[i] = arrayList.get(i).getQuality();
            }
            if (!Mcontext.isFinishing()) {
                new AlertDialog.Builder(Mcontext).setTitle("Quality!").setItems(charSequenceArr, (dialogInterface, i) -> CallgdriveData.this.done(arrayList.get(i))).setPositiveButton("OK", (dialogInterface, i) -> dismissMyDialog()).setCancelable(false).show();
            }
        }

        public void done(XModel xModel) {

            try {

                dismissMyDialog();
                this.VideoUrl = xModel.getUrl();
                if (this.VideoUrl != null && !this.VideoUrl.isEmpty()) {
                    try {

                        String myurldocument = VideoUrl;

                        String nametitle = "Allvideo_" +
                                System.currentTimeMillis();

                        DownloadFileMain.startDownloading(Mcontext, myurldocument, nametitle, ".mp4");
                        VideoUrl = "";
                    } catch (Exception document2) {
                        System.out.println("myresponseis111 exp1 " + document2.getMessage());
                        dismissMyDialog();
                        document2.printStackTrace();
                        Mcontext.runOnUiThread(() -> Utils.ShowToastError(Mcontext,
                                Mcontext.getResources().getString(R.string.somthing)
                        ));
                    }

                    return;
                }

                Mcontext.runOnUiThread(() -> Utils.ShowToastError(Mcontext,
                        Mcontext.getResources().getString(R.string.somthing)
                ));
            } catch (Exception unused) {
                System.out.println("myresponseis111 exp " + unused.getMessage());

                dismissMyDialog();
                Mcontext.runOnUiThread(() -> Utils.ShowToastError(Mcontext,
                        Mcontext.getResources().getString(R.string.somthing)
                ));
            }

        }

    }

    public static class callGetsnackvideoData extends AsyncTask<String, Void, JSONObject> {
        JSONObject snackvideoDoc;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            try {

                String photoId, authorId;
                String location = Utils.getRedirectUrl(urls[0]);

                System.out.println(location);
                if (location.contains("?")) {
                    location = location.split("\\?")[0];

                }
                if (location == null) {
                    location = urls[0];
                }
                authorId = location.split("/")[4];
                photoId = location.split("/")[5];

                snackvideoDoc = new JSONObject("{\"photoId\":\"" + photoId + "\",\"authorId\":\"" + authorId + "\"}");

            } catch (Exception e) {
                Log.i("THANGERRROR", e.toString());
            }
            return snackvideoDoc;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {

                AndroidNetworking.post("https://m.snackvideo.com/rest/o/w/photo/getUserHotPhoto?kpn=KWAI_BULLDOG")
                        .addJSONObjectBody(result)
                        .addHeaders("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36")
                        .addHeaders("x-csrf-token", "Acn4pAjr5lT5AT6OBjzyB9VP")
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                System.out.println("wojfdjhfdjh " + response);

                                try {
                                    VideoUrl = response.getJSONArray("datas").getJSONObject(0).getString("mp4Url");

                                    System.out.println("myresponseis111 list_of_qualities" + VideoUrl);

                                    // getSnackVideoData(jSONObject.getString("shortUrl"), Mcontext);
                                    String nametitle = "snackvideo_" +
                                            System.currentTimeMillis();

                                    DownloadFileMain.startDownloading(Mcontext, VideoUrl, nametitle, ".mp4");
                                    VideoUrl = "";
                                    dismissMyDialog();
                                } catch (Exception e) {
                                    Log.i("THANGERRROR", e.toString());
                                    dismissMyDialogErrortoast();
                                }

                            }

                            @Override
                            public void onError(ANError error) {

                                Log.i("THANGERRROR", error.toString());
                                dismissMyDialogErrortoast();
                            }
                        });

            } catch (Exception e) {
                Log.i("THANGERRROR", e.toString());
                dismissMyDialogErrortoast();
            }
        }

    }

    private static class callGetbilibiliAppData extends AsyncTask<String, Void, Document> {
        Document ShareChatDoc;
        private Iterator<Element> abk;

        callGetbilibiliAppData() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Document doInBackground(String... strArr) {
            try {
                this.ShareChatDoc = Jsoup.connect(strArr[0]).get();
            } catch (Exception strArr2) {
                strArr2.printStackTrace();
                Log.d("ContentValues roposo_", "doInBackground: Error");
            }
            return this.ShareChatDoc;
        }

        protected void onPostExecute(Document document) {

            try {

                ArrayList<String> mp4List = new ArrayList<>();
                ArrayList<String> qualitylist = new ArrayList<>();

                String data = "";

                Iterator<Element> documentitrator = document.select("script").iterator();

                do {
                    if (!documentitrator.hasNext()) {

                        break;
                    }
                    data = documentitrator.next().data();
                    Log.e("onP4342424te:datais ", data);

                } while (!data.contains("window.__playinfo__="));

                String stringbuil = data.substring(data.indexOf("{"), data.lastIndexOf("}"));

                StringBuilder stringBuilder = new StringBuilder();

                stringBuilder.append(stringbuil);
                stringBuilder.append("}");

                Log.e("onPostbjnkjhoso_11 ", stringBuilder.toString());
                if (!document.equals("")) {
                    try {
                        JSONObject jSONObject = new JSONObject(stringBuilder.toString());
                        JSONObject datajSONObject = jSONObject.getJSONObject("data");
                        JSONObject dashjSONObject1 = datajSONObject.getJSONObject("dash");
                        JSONArray videojSONObject1 = dashjSONObject1.getJSONArray("video");

                        System.out.println("respossss112212121URL)) " + videojSONObject1.getJSONObject(0).getString("base_url"));

                        for (int i = 0; i < videojSONObject1.length(); i++) {

                            JSONObject jsonObject12 = videojSONObject1.getJSONObject(i);
                            mp4List.add(jsonObject12.getString("base_url"));
                            qualitylist.add(jsonObject12.getString("width"));

                            System.out.println("respossss112212121URL " + jsonObject12.getString("base_url"));

                        }

                        try {
                            JSONArray audiojSONObject1 = dashjSONObject1.getJSONArray("audio");
                            for (int i = 0; i < audiojSONObject1.length(); i++) {

                                JSONObject jsonObject12 = audiojSONObject1.getJSONObject(i);
                                mp4List.add(jsonObject12.getString("base_url"));
                                qualitylist.add(jsonObject12.getString("mime_type"));

                                System.out.println("respossss112212121URL " + jsonObject12.getString("base_url"));

                            }

                        } catch (Exception e) {
                            dismissMyDialog();
                        }

                        if (videojSONObject1.length() > 0) {
                            if (!Mcontext.isFinishing()) {

                                dialog_quality_allvids = new Dialog(Mcontext);

                                dismissMyDialog();

                                windowManager2 = (WindowManager) Mcontext.getSystemService(WINDOW_SERVICE);
                                LayoutInflater layoutInflater = (LayoutInflater) Mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                                mChatHeadView = layoutInflater.inflate(R.layout.dialog_quality_ytd, null);

                                dialog_quality_allvids.setContentView(mChatHeadView);

                                mainLayout = mChatHeadView.findViewById(R.id.linlayout_dialog);

                                img_dialog = mChatHeadView.findViewById(R.id.img_dialog);

                                mainLayout = dialog_quality_allvids.findViewById(R.id.linlayout_dialog);
                                img_dialog = dialog_quality_allvids.findViewById(R.id.img_dialog);

                                int size;

                                try {
                                    DisplayMetrics displayMetrics = new DisplayMetrics();
                                    Mcontext.getWindowManager()
                                            .getDefaultDisplay()
                                            .getMetrics(displayMetrics);

                                    int height = displayMetrics.heightPixels;
                                    int width = displayMetrics.widthPixels;

                                    size = width / 2;

                                } catch (Exception e) {
                                    size = WindowManager.LayoutParams.WRAP_CONTENT;
                                }

                                params = new WindowManager.LayoutParams(
                                        size,
                                        WindowManager.LayoutParams.WRAP_CONTENT,
                                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                                                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                                                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                                                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                                        PixelFormat.TRANSLUCENT);

                                params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                                params.x = 0;
                                params.y = 100;
                                for (int i = 0; i < mp4List.size(); i++) {

                                    addButtonToMainLayouttest_allvideo(qualitylist.get(i), mp4List.get(i), "Bilibili_" + qualitylist.get(i) + "_" + System.currentTimeMillis());

                                }

                                img_dialog.setOnClickListener(v -> dialog_quality_allvids.dismiss());

                                dialog_quality_allvids.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                                dialog_quality_allvids.getWindow().setAttributes(params);
                                dialog_quality_allvids.show();
                                dialog_quality_allvids.show();
                            }
                        } else {
                            DownloadFileMain.startDownloading(Mcontext, mp4List.get(0), "Bilibili_" + System.currentTimeMillis(), ".mp4");
                            dismissMyDialog();
                        }

                    } catch (Exception document2) {
                        document2.printStackTrace();

                        System.out.println("respossss112212121qerrr " + document2.getMessage());

                        dismissMyDialog();
                        Mcontext.runOnUiThread(() -> {
                            Utils.ShowToastError(Mcontext,
                                    Mcontext.getResources().getString(R.string.somthing)
                            );
                        });
                    }
                }

            } catch (Exception document22) {
                dismissMyDialog();
                document22.printStackTrace();
                System.out.println("respossss112212121qerrr " + document22.getMessage());

                Mcontext.runOnUiThread(() -> {
                    Utils.ShowToastError(Mcontext,
                            Mcontext.getResources().getString(R.string.somthing)
                    );
                });
            }
        }
    }


    public static class CallkuaishouData extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        private String getCookiesFromWebsite() {
            try {
                URL url = new URL("https://www.kuaishou.com/");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                Map<String, List<String>> headerFields = connection.getHeaderFields();
                List<String> cookiesHeader = headerFields.get("Set-Cookie");
                if (cookiesHeader != null) {
                    StringBuilder cookies = new StringBuilder();
                    for (String cookie : cookiesHeader) {
                        cookies.append(cookie).append("; ");
                    }
                    return cookies.toString();
                }
            } catch (IOException e) {
                Log.i("THANGERRROR", e.toString());
            }
            return "";
        }


        public Document doInBackground(String... strArr) {
            try {
                System.out.println("myresponseis111 mybodyhh1111>>> " + strArr[0]);

                String tempCookies = getCookiesFromWebsite();

                ClearableCookieJar cookieJar =
                        new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(Mcontext));

                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                OkHttpClient client = new OkHttpClient.Builder()
                        .cookieJar(cookieJar)
                        .addInterceptor(logging)
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();


                Request request = new Request.Builder()
                        .url(strArr[0])
                        .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                        .addHeader("Accept-Language", "en-GB,en-US;q=0.9,en;q=0.8")
                        .addHeader("Connection", "keep-alive")
                        .addHeader("Cookie", tempCookies)
                        .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36")
                        .build();

                Response response = client.newCall(request).execute();

                return this.RoposoDoc = Jsoup.parse(Objects.requireNonNull(response.body()).string());
            } catch (IOException e) {
                Log.i("THANGERRROR", e.toString());
                return this.RoposoDoc;
            }
        }

        public void onPostExecute(Document document) {
            Log.d("", "myresponseis1115 exp12222 " + document.toString());

            try {
                dismissMyDialog();

                for (Element element : document.getElementsByTag("script")) {
                    if (element.data().contains("window.__APOLLO_STATE__=")) {
                        String replace = element.html().replace("window.__APOLLO_STATE__=", "");
                        replace = replace.substring(0, replace.indexOf("{}};") + 3);

                        System.out.println("myresponseis111 replace  " + replace);

                        String sr = StringUtils.substringBetween(replace, "\"videoResource\"", "\"__typename\"");
                        sr = sr.substring(1, sr.length() - 1);
                        System.out.println("myresponseis111 str  " + sr);

                        this.VideoUrl = new JSONObject(sr)
                                .getJSONObject("json")
                                .getJSONObject("h264")
                                .getJSONArray("adaptationSet")
                                .getJSONObject(0)
                                .getJSONArray("representation")
                                .getJSONObject(0)
                                .getString("url");

                        this.VideoUrl = URLDecoder.decode(this.VideoUrl, StandardCharsets.UTF_8);
                        System.out.println("myresponseis111 VideoUrl  " + this.VideoUrl);

                        if (this.VideoUrl != null && !this.VideoUrl.isEmpty()) {
                            try {
                                String myurldocument = VideoUrl;
                                String nametitle = "kuaishou_" +
                                        System.currentTimeMillis();
                                DownloadFileMain.startDownloading(Mcontext, myurldocument, nametitle, ".mp4");
                                VideoUrl = "";

                            } catch (Exception document2) {
                                System.out.println("myresponseis111 exp1 " + document2.getMessage());
                                dismissMyDialog();
                                document2.printStackTrace();
                                Mcontext.runOnUiThread(() -> Utils.ShowToastError(Mcontext,
                                        Mcontext.getResources().getString(R.string.somthing)
                                ));
                            }

                            return;
                        }
                    }
                }
                Mcontext.runOnUiThread(() -> Utils.ShowToastError(Mcontext,
                        Mcontext.getResources().getString(R.string.somthing)
                ));
            } catch (Exception unused) {
                System.out.println("myresponseis111 exp " + unused.getMessage());
                dismissMyDialog();
                Mcontext.runOnUiThread(() -> Utils.ShowToastError(Mcontext,
                        Mcontext.getResources().getString(R.string.somthing)
                ));
            }
        }

    }
}
