/*
 * *
 *  * Created by Syed Usama Ahmad on 3/17/23, 11:37 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 3/17/23, 10:26 PM
 *
 */

package com.zagavideodown.app.other.facebook;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.zagavideodown.app.R;
import com.zagavideodown.app.databinding.ActivityTikTokDownloadWebviewBinding;
import com.zagavideodown.app.utils.DownloadFileMain;
import com.zagavideodown.app.utils.Utils;
import com.libs.zhkrb.cloudflare_scrape_webview.CfCallback;
import com.libs.zhkrb.cloudflare_scrape_webview.Cloudflare;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FacebookDownloadCloudBypassWebview_method_1 extends AppCompatActivity {


    public static Handler handler;
    static String myvidintenturlis = "";
    private static ValueCallback<Uri[]> mUploadMessageArr;
    public Handler handler1;
    String TAG = "whatsapptag";
    boolean doubleBackToExitPressedOnce = false;
    boolean isdownloadstarted = false;
    ProgressDialog progressDialog;
    ArrayList<String> listoflink_videos;
    private ActivityTikTokDownloadWebviewBinding binding;






    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = ActivityTikTokDownloadWebviewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        listoflink_videos = new ArrayList<String>();
        setSupportActionBar(binding.tool12);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);

        InitHandler();

        try {


            progressDialog = new ProgressDialog(FacebookDownloadCloudBypassWebview_method_1.this);
            progressDialog.setMessage(getString(R.string.nodeifittakeslonger));
            progressDialog.show();
        } catch (Exception e) {
            Log.d("dialog", "" + e.getLocalizedMessage());
        }
        if (getIntent().getStringExtra("myvidurl") != null && !getIntent().getStringExtra("myvidurl").isEmpty()) {
            myvidintenturlis = getIntent().getStringExtra("myvidurl");
        }


        binding.webViewscan.clearFormData();
        binding.webViewscan.getSettings().setSaveFormData(true);
        //  binding.webViewscan.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0");
        binding.webViewscan.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
        //   binding.webViewscan.setWebChromeClient(new webChromeClients());
        binding.webViewscan.setWebViewClient(new MyBrowser());

        binding.webViewscan.getSettings().setAllowFileAccess(true);
        binding.webViewscan.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        binding.webViewscan.getSettings().setJavaScriptEnabled(true);
        binding.webViewscan.getSettings().setDefaultTextEncodingName("UTF-8");
        binding.webViewscan.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        binding.webViewscan.getSettings().setDatabaseEnabled(true);
        binding.webViewscan.getSettings().setBuiltInZoomControls(false);
        binding.webViewscan.getSettings().setSupportZoom(false);
        binding.webViewscan.getSettings().setUseWideViewPort(true);
        binding.webViewscan.getSettings().setDomStorageEnabled(true);
        binding.webViewscan.getSettings().setAllowFileAccess(true);
        binding.webViewscan.getSettings().setLoadWithOverviewMode(true);
        binding.webViewscan.getSettings().setLoadsImagesAutomatically(true);
        binding.webViewscan.getSettings().setBlockNetworkImage(false);
        binding.webViewscan.getSettings().setBlockNetworkLoads(false);
        binding.webViewscan.getSettings().setLoadWithOverviewMode(true);
        binding.webViewscan.getSettings().setPluginState(WebSettings.PluginState.ON);

        binding.webViewscan.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                request.grant(request.getResources());
            }
        });

        binding.webViewscan.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {

            String nametitle = "Facebook_video_" +
                    System.currentTimeMillis();

            DownloadFileMain.startDownloading(FacebookDownloadCloudBypassWebview_method_1.this, url, nametitle, ".mp4");


        });

        binding.webViewscan.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && binding.progressBar.getVisibility() == View.GONE) {
                    binding.progressBar.setVisibility(View.VISIBLE);

                }

                binding.progressBar.setProgress(progress);
                if (progress == 100) {
                    binding.progressBar.setVisibility(View.GONE);

                }
            }
        });

        try {
            Cloudflare cf = new Cloudflare(FacebookDownloadCloudBypassWebview_method_1.this, "https://savefrom.net/");
            //   cf.setUser_agent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
            cf.setUser_agent(binding.webViewscan.getSettings().getUserAgentString());
            cf.setCfCallback(new CfCallback() {
                @Override
                public void onSuccess(List<HttpCookie> cookieList, boolean hasNewUrl, String newUrl) {

                    binding.webViewscan.loadUrl(newUrl);

                }

                @Override
                public void onFail(int code, String msg) {
                    //  Toast.makeText(TikTokDownloadCloudBypassWebview_method_3.this, "" + msg, Toast.LENGTH_SHORT).show();

                    binding.webViewscan.loadUrl("https://savefrom.net/");

                }
            });
            cf.getCookies();
        } catch (Exception e) {
            e.printStackTrace();
            binding.webViewscan.loadUrl("https://savefrom.net/");

        }

    }


    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 1001) {
            mUploadMessageArr.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(i2, intent));
            mUploadMessageArr = null;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean z = true;
        if (keyCode == 4) {
            try {
                if (binding.webViewscan.canGoBack()) {
                    binding.webViewscan.goBack();
                    return z;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        finish();
        z = super.onKeyDown(keyCode, event);
        return z;
    }

    @SuppressLint({"WrongConstant"})
    public void onBackPressed() {
        if (this.doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        runOnUiThread(() -> {
            Utils.ShowToastError(this, getString(R.string.pressagain)

            );
        });
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    public void onPause() {
        super.onPause();
        try {
            binding.webViewscan.clearCache(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        try {
            handler1.removeCallbacksAndMessages(null);

            binding.webViewscan.clearCache(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onStop() {
        try {
            binding.webViewscan.clearCache(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @SuppressLint({"HandlerLeak"})
    private void InitHandler() {
        handler = new btnInitHandlerListner();
        handler1 = new Handler();
    }

    @SuppressLint("HandlerLeak")
    private class btnInitHandlerListner extends Handler {
        @SuppressLint({"SetTextI18n"})
        public void handleMessage(Message msg) {
        }
    }

    private class webChromeClients extends WebChromeClient {
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Log.e("CustomClient", consoleMessage.message());
            return super.onConsoleMessage(consoleMessage);
        }
    }

    private class MyBrowser extends WebViewClient {
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            binding.progressBar.setVisibility(View.VISIBLE);
            Log.e(TAG, "binding.progressBar " + url);
            super.onPageStarted(view, url, favicon);
        }

        public boolean shouldOverrideUrlLoading(WebView view, String request) {
            view.loadUrl(request);
            return true;
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.e(TAG, "binding.progressBar GONE " + url);
            binding.progressBar.setVisibility(View.GONE);


            String jsscript = "javascript:(function() { "

                    + "document.getElementById(\"sf_url\").value ='" + myvidintenturlis + "';"
                    + "document.getElementById('sf_submit').click();"
                    //    + "await new Promise(resolve => setTimeout(resolve, 3000)); "
                    //  + "javascript:document.getElementsByClassName(\"pure-button pure-button-primary is-center u-bl dl-button download_link without_watermark_direct\").click(); "
                    + "})();";

            view.evaluateJavascript(jsscript, obj -> Log.e(TAG, "binding.progressBar reciveing data " + obj));
            try {


                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Log.e(TAG, "binding.progressBar reciveing data executed 1");


                        //    binding.webViewscan.loadUrl("javascript:window.HTMLOUT.showHTML('" + url + "',''+document.getElementsByTagName('audio')[0].getAttribute(\"src\"));");


                        view.evaluateJavascript(
                                "(function() { " +
                                        "var text='';" +
                                        "var aaa = document.getElementsByTagName('a');" +
                                        "for (var i = 0; i < aaa.length; i++) {" +  // "if(aaa[i].getAttribute('href').includes('https://scontent') || aaa[i].getAttribute('href').includes('https://instagram')){" +
                                        "  text += aaa[i].getAttribute('href')+'@_@';" +  //  "}" +
                                        "}" +
                                        "var withoutLast3 = text.slice(0, -3);" +
                                        "return withoutLast3+''; })();"
                                , obj -> {
                                    Log.e(
                                            "workkkk0",
                                            "binding!!.progressBar fbreciving data " + obj
                                    );
                                    String[] sss = obj.split("@_@");


                                    for (String i : sss) {
                                        if (i.contains("http") && i.startsWith("https://video") && i.contains("fbcdn.net")) {

                                            listoflink_videos.add(i);

                                        }
                                    }


                                    if (!isdownloadstarted && listoflink_videos != null && listoflink_videos.size() > 0) {
                                        isdownloadstarted = true;

                                        handler1.removeCallbacksAndMessages(null);

                                        isdownloadstarted = true;
                                        listoflink_videos = Utils.removeDuplicates(listoflink_videos);


                                        DownloadFileMain.startDownloading(FacebookDownloadCloudBypassWebview_method_1.this, listoflink_videos.get(0),
                                                "Facebook_" + System.currentTimeMillis() + "_" + Utils.getVideoFilenameFromURL(listoflink_videos.get(0)), ".mp4");

                                        if (progressDialog != null && progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
                                        //  startActivity(new Intent(TikTokDownloadWebview.this, MainActivity.class));
                                        finish();


                                    }


                                });

                        handler1.postDelayed(this, 2000);

                    }
                }, 2000);
            } catch (Exception e) {

                finish();
            }


        }
    }


}

