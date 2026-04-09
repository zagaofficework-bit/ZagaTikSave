package com.zagavideodown.app.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import com.zagavideodown.app.BaseActivity;
import com.zagavideodown.app.GlobalConstant;
import com.zagavideodown.app.R;
import com.zagavideodown.app.databinding.ActivityGetLinkThroughWebViewBinding;
import com.zagavideodown.app.interfaces.DownloadCallback;
import com.zagavideodown.app.models.dlapismodels.VideoModel;
import com.zagavideodown.app.utils.DownloadFileMain;
import com.zagavideodown.app.utils.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class GetLinkThroughWebView extends BaseActivity {
    String url = "";
    ProgressDialog progressDialog;
    boolean isOnetime = false;
    Handler handler;
    Runnable runnable;
    private ArrayList<VideoModel> videoModelArrayList;
    private ActivityGetLinkThroughWebViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityGetLinkThroughWebViewBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        progressDialog = new ProgressDialog(GetLinkThroughWebView.this);
        progressDialog.setMessage(getString(R.string.str_note_when_long));

        url = "https://audiomack.com/lightskinkeisha/song/fdh";
        videoModelArrayList = new ArrayList<>();
        handler = new Handler();


        if (getIntent().hasExtra("myurlis")) {
            url = getIntent().getStringExtra("myurlis");


            if (url.contains("ok.ru")) {
                try {
                    url = "https://dirpy.com/studio?url=" + URLEncoder.encode(url, String.valueOf(StandardCharsets.UTF_8));
                } catch (UnsupportedEncodingException e) {
                }
            }


            if (url.contains("audiomack")) {

                String[] urlarray = url.split("/");
                System.out.println("length ksdjjfsdfsd 6" + urlarray[5]);
                System.out.println("length ksdjjfsdfsd 5" + urlarray[3]);
                url = "https://audiomack.com/embed/song/" + urlarray[3] + "/" + urlarray[5] + "?background=1";

                System.out.println("length ksdjjfsdfsd 77 =" + url);
            }


        } else {
            url = "https://audiomack.com/embed/song/lightskinkeisha/fdh?background=1";
        }


        binding.browser.clearCache(true);


        CookieManager.getInstance().setAcceptThirdPartyCookies(binding.browser, true);
        binding.browser.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        CookieSyncManager.createInstance(GetLinkThroughWebView.this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();

        binding.browser.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
        WebView.setWebContentsDebuggingEnabled(true);


        binding.browser.getSettings().setJavaScriptEnabled(true);
        binding.browser.getSettings().getAllowFileAccess();
        binding.browser.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        binding.browser.getSettings().setDomStorageEnabled(true);
        binding.browser.getSettings().setAllowUniversalAccessFromFileURLs(true);
        binding.browser.getSettings().setMediaPlaybackRequiresUserGesture(false);


        int randomnumber = Utils.getRandomNumber(GlobalConstant.UserAgentsList0.length);

        try {
            binding.browser.getSettings().setUserAgentString(GlobalConstant.UserAgentsList0[randomnumber]);

        } catch (Exception e) {
            binding.browser.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 10;TXY567) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/8399.0.9993.96 Mobile Safari/599.36");
        }


        if (url.contains("facebook") || url.contains("fb.watch")) {
            url = url.replace("https://www.facebook.com", "https://m.facebook.com");
            binding.browser.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36");
        }

        binding.browser.getSettings().setSupportMultipleWindows(true);


        binding.browser.setWebViewClient(new WebViewClient() {

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);


                final String viewUrl = view.getUrl();

                if ((url.contains("facebook.com") || url.contains("fb.watch")))
                    Log.d("TAG :", "onLoadResourceFB url: " + url);

                if (url.contains("instagram") || url.contains("threads.net")) {

                    Log.d("TAG :", "onLoadResource url: " + url);


                    if (url.contains(".mp4")) {
                        Log.d("TAG", "onLoadResource: view url" + url);
                        String title = view.getTitle();
                        Log.d("TAG", "onLoadResource: title" + title);

                        VideoModel videoModelImage = new VideoModel();
                        videoModelImage.setUrl(url);
                        videoModelImage.setTitle(title);
                        videoModelImage.setType(".mp4");

                        videoModelArrayList.add(videoModelImage);
                    }

                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);
                return false;
            }
        });

        binding.browser.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                getWindow().setTitle(title); //Set Activity tile to page title.
            }

            @Override
            public Bitmap getDefaultVideoPoster() {
                return Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
            }


            @Override
            public void onProgressChanged(WebView view1, int newProgress) {


                if (newProgress >= 100) {


                    runnable = () -> {

                        try {
                            url = view1.getUrl();

                            if (url.contains("audiomack")) {

                                binding.browser.loadUrl("javascript:window.HTMLOUT.showHTML('" + url + "',''+document.getElementsByTagName('audio')[0].getAttribute(\"src\"));");
                            } else if (url.contains("instagram")) {

                                view1.evaluateJavascript("javascript:(function() { document.getElementsByTagName('video')[0].play(); })()", value -> {
                                    Log.d("TAG :", "videoModelArrayList Video Played: " + value);


                                    if (videoModelArrayList != null && videoModelArrayList.size() > 0) {

                                        if (handler != null && runnable != null) {
                                            handler.removeCallbacks(runnable);
                                        }

                                        for (int i = 0; i < videoModelArrayList.size(); i++) {
                                            Log.d("TAG :", "videoModelArrayList urls: " + videoModelArrayList.get(i).getUrl());

                                            DownloadFileMain.startDownloading(GetLinkThroughWebView.this, videoModelArrayList.get(i).getUrl(), "Instagram_Web_" + System.currentTimeMillis(), videoModelArrayList.get(i).getType(), new DownloadCallback() {
                                                @Override
                                                public void onComplete() {

                                                }
                                            });
                                        }

                                        setIntentResultData(true);
                                    }
                                });


                            } else {
                                setIntentResultData(false);
                            }

                            handler.postDelayed(runnable, 3000);
                        } catch (Exception e) {
                            e.printStackTrace();
                            setIntentResultData(false);

                        }
                    };

                    handler.postDelayed(runnable, 2000);


                }

            }
        });

        binding.browser.loadUrl(url);

        webViewLightDark();
    }
    private void webViewLightDark() {

        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_YES:
                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                    WebSettingsCompat.setForceDark(binding.browser.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
                }
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                    WebSettingsCompat.setForceDark(binding.browser.getSettings(), WebSettingsCompat.FORCE_DARK_OFF);
                }
                break;

            default:

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }

    }


    public void setIntentResultData(boolean isDone) {


        if (!isFinishing() && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }

        if (!isDone) {
//            Utils.ShowToast(GetLinkThroughWebView.this, getResources().getString(R.string.something));
        }
        Intent intent = new Intent();
        setResult(2, intent);
        finish();

    }


    class MyJavaScriptInterface {
        @JavascriptInterface
        public void showHTML(final String myurl, final String html) {
            try {
                progressDialog.dismiss();

                System.out.println("myhtml res =" + html);
                if (!isOnetime) {
                    isOnetime = true;

                    handler.removeCallbacks(runnable);

//                    if (myurl.contains("audiomack")) {
//
//                        DownloadFileMain.startDownloading(GetLinkThroughWebView.this, html, "Audiomack_" + System.currentTimeMillis(), ".mp3");
//
//                    }
//                    else
                    if (myurl.contains("instagram")) {

                        DownloadFileMain.startDownloading(GetLinkThroughWebView.this, html, "Instagram_web_" + System.currentTimeMillis(), ".mp4", new DownloadCallback() {
                            @Override
                            public void onComplete() {

                            }
                        });

                    } else {
                        setIntentResultData(false);
                    }

                    System.out.println("htmlissss vid_url=" + html + " url=" + myurl);

                    setIntentResultData(true);

                } else {
                    if (handler != null && runnable != null) {
                        handler.removeCallbacks(runnable);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                setIntentResultData(false);

            }
        }
    }

}
