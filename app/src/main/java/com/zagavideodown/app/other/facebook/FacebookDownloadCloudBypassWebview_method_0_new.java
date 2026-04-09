package com.zagavideodown.app.other.facebook;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.MimeTypeMap;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FacebookDownloadCloudBypassWebview_method_0_new extends AppCompatActivity {

    private static String myvidintenturlis = "";
    private static ValueCallback<Uri[]> mUploadMessageArr;
    private static final String TAG = "whatsapptag";
    private static final int MAX_ATTEMPTS = 10; // Maximum attempts to find download links
    private boolean isdownloadstarted = false;
    private ProgressDialog progressDialog;
    private ActivityTikTokDownloadWebviewBinding binding;
    private Handler handler1;
    private int attemptCount = 0; // Counter for download link attempts
    private boolean firstLoad = true;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = ActivityTikTokDownloadWebviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.tool12);

        handler1 = new Handler();

        // Initialize ProgressDialog
        try {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.nodeifittakeslonger));
            progressDialog.setCancelable(true);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Get video URL from intent
        if (getIntent().getStringExtra("myvidurl") != null && !getIntent().getStringExtra("myvidurl").isEmpty()) {
            myvidintenturlis = getIntent().getStringExtra("myvidurl");
        }

        // TikTok button click listener (optional, might be a leftover)
        binding.opentiktok.setOnClickListener(view -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com/"));
                intent.setPackage("com.zhiliaoapp.musically");
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Utils.ShowToast(this, "TikTok not Installed");
            }
        });

        // WebView configuration
        configureWebView();

        // Cloudflare bypass
        Cloudflare cf = new Cloudflare(this, "https://getmyfb.com/");
        cf.setUser_agent(binding.webViewscan.getSettings().getUserAgentString());
        cf.setCfCallback(new CfCallback() {
            @Override
            public void onSuccess(List<HttpCookie> cookieList, boolean hasNewUrl, String newUrl) {
                binding.webViewscan.loadUrl(newUrl);
            }

            @Override
            public void onFail(int code, String msg) {
                binding.webViewscan.loadUrl("https://getmyfb.com/");
            }
        });
        cf.getCookies();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configureWebView() {
        binding.webViewscan.clearFormData();
        binding.webViewscan.getSettings().setSaveFormData(true);
        binding.webViewscan.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
        binding.webViewscan.setWebViewClient(new MyBrowser());

        WebSettings settings = binding.webViewscan.getSettings();
        settings.setAllowFileAccess(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setDatabaseEnabled(true);
        settings.setBuiltInZoomControls(false);
        settings.setSupportZoom(false);
        settings.setUseWideViewPort(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setBlockNetworkImage(false);
        settings.setBlockNetworkLoads(false);

        // Combined WebChromeClient for permission requests and progress
        binding.webViewscan.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                request.grant(request.getResources());
            }

            @Override
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

        binding.webViewscan.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            String nametitle = "Instagram_video_" + System.currentTimeMillis();
            DownloadFileMain.startDownloading(this, url, nametitle, ".mp4");
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 1001 && mUploadMessageArr != null) {
            mUploadMessageArr.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
            mUploadMessageArr = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (binding.webViewscan.canGoBack()) {
            binding.webViewscan.goBack();
        } else {
            finishAndDismiss();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            binding.webViewscan.clearCache(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            binding.webViewscan.clearCache(true);
            handler1.removeCallbacksAndMessages(null);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            binding.webViewscan.clearCache(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finishAndDismiss();
        return true;
    }

    private void finishAndDismiss() {
        try {
            if (progressDialog != null && progressDialog.isShowing() && !isFinishing()) {
                progressDialog.dismiss();
            }
            handler1.removeCallbacksAndMessages(null);
            finish();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            binding.progressBar.setVisibility(View.VISIBLE);
            Log.e(TAG, "Page started loading");
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String request) {
            view.loadUrl(request);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.e(TAG, "Page finished loading");
            binding.progressBar.setVisibility(View.GONE);

            view.evaluateJavascript("javascript:(function() { "
                            + "document.getElementById('main-link').value = '" + myvidintenturlis.replace("'", "\\'") + "';"
                            + "document.querySelector('button[type=\"submit\"]').click();"
                            + "})();",
                    value -> {
                        Log.e(TAG, "JavaScript result: " + value);


                        handler1.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (attemptCount >= MAX_ATTEMPTS) {
                                    Utils.ShowToast(FacebookDownloadCloudBypassWebview_method_0_new.this, "Failed to find download links");
                                    finishAndDismiss();
                                    return;
                                }
                                attemptCount++;

                                view.evaluateJavascript("(function() { " +
                                        "var text='';" + "var aaa = document.getElementsByTagName('a');" +
                                        "for (var i = 0; i < aaa.length; i++) {" +
                                        "   var href = String(aaa[i].getAttribute('href')); " +
                                        "   if (href.startsWith('http')) {" +
                                        "       text += href + '@_@';" +
                                        "   }" +
                                        "}" +
                                        "var withoutLast3 = text.slice(0, -3);" +
                                        "return withoutLast3+''; })();", value -> {
                                    Log.e(TAG, "Download links: " + value);
                                    if (value == null || value.equals("\"\"")) {
                                        handler1.postDelayed(this, 3000);
                                        return;
                                    }

                                    ArrayList<String> listoflinke = new ArrayList<>();
                                    ArrayList<String> links = new ArrayList<>(
                                            Arrays.stream(value.replace("\"", "").split("@_@"))
                                                    .filter(s -> !s.isEmpty())
                                                    .filter(s -> Patterns.WEB_URL.matcher(s).matches())
                                                    .distinct()
                                                    .collect(Collectors.toList())
                                    );
                                    for (String s : links) {
                                        if (s.isEmpty()) continue;

                                        String nametitle = "Instagram_" + System.currentTimeMillis();
                                        String mimeType = null;

                                        try {
                                            String ext = MimeTypeMap.getFileExtensionFromUrl(s);
                                            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
                                        } catch (Exception ignored) {
                                        }

                                        Log.e(TAG, "Download mimeType: " + mimeType);

                                        if (!s.contains("https://play.google.com")) {
                                            if (mimeType != null) {
                                                if (mimeType.startsWith("image/")) {
                                                    DownloadFileMain.startDownloading(FacebookDownloadCloudBypassWebview_method_0_new.this, s, nametitle, ".jpg");
                                                    listoflinke.add(s);


                                                    isdownloadstarted = true;
                                                    finishAndDismiss();
                                                    return;
                                                } else if (mimeType.startsWith("video/") || s.contains("ssscdn.io/getmyfb") || s.contains(".mp4") || s.contains("fbcdn.net")) {
                                                    DownloadFileMain.startDownloading(FacebookDownloadCloudBypassWebview_method_0_new.this, s, nametitle, ".mp4");
                                                    listoflinke.add(s);


                                                    isdownloadstarted = true;
                                                    finishAndDismiss();
                                                    return;
                                                }
                                            } else if (s.contains("ssscdn.io/getmyfb") || s.contains(".mp4") || s.contains("fbcdn.net")) {
                                                DownloadFileMain.startDownloading(FacebookDownloadCloudBypassWebview_method_0_new.this, s, nametitle, ".mp4");
                                                listoflinke.add(s);


                                                isdownloadstarted = true;
                                                finishAndDismiss();
                                                return;
                                            }
                                        }
                                    }

                                    if (!isdownloadstarted && listoflinke.size() > 0) {
                                        isdownloadstarted = true;
                                        finishAndDismiss();
                                    } else {
                                        handler1.postDelayed(this, 3000);
                                    }
                                });
                            }
                        }, 3000);


                    });


        }


    }

    @SuppressLint("HandlerLeak")
    private class btnInitHandlerListner extends Handler {
        // Empty handler class, possibly a placeholder
    }

    private class webChromeClients extends WebChromeClient {
        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Log.e("CustomClient", consoleMessage.message());
            return super.onConsoleMessage(consoleMessage);
        }
    }
}
