package com.zagavideodown.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.zagavideodown.app.BaseActivity;
import com.zagavideodown.app.GlobalConstant;
import com.zagavideodown.app.R;
import com.zagavideodown.app.SharedPrefsForInstagram;
import com.zagavideodown.app.databinding.ActivityInstagramLoginBinding;
import com.zagavideodown.app.models.instawithlogin.ModelInstagramPref;
import com.zagavideodown.app.utils.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InstagramLoginActivity extends BaseActivity {
    Map<String, String> extraHeaders = new HashMap<>();
    int randomnumber = 0;
    private ActivityInstagramLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityInstagramLoginBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        randomnumber = Utils.getRandomNumber(GlobalConstant.UserAgentsListLogin.length);
        extraHeaders.put("x-requested-with", "XMLHttpRequest");
        extraHeaders.put("HTTP_X-Requested-With", "com.android.chrome");


        extraHeaders.put("authority", "www.instagram.com");
        extraHeaders.put("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        extraHeaders.put("accept-language", "en-GB,en-US;q=0.9,en;q=0.8");
        extraHeaders.put("cache-control", "max-age=0");
        extraHeaders.put("dnt", "1");
        extraHeaders.put("dpr", "1.5");
        extraHeaders.put("referer", "https://www.instagram.com/");
        extraHeaders.put("sec-ch-prefers-color-scheme", "light");
        extraHeaders.put("sec-ch-ua", "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"");
        extraHeaders.put("sec-ch-ua-full-version-list", "\"Google Chrome\";v=\"131.0.6778.205\", \"Chromium\";v=\"131.0.6778.205\", \"Not_A Brand\";v=\"24.0.0.0\"");
        extraHeaders.put("sec-ch-ua-mobile", "?1");
        extraHeaders.put("sec-ch-ua-model", "\"SM-G981B\"");
        extraHeaders.put("sec-ch-ua-platform", "\"Android\"");
        extraHeaders.put("sec-ch-ua-platform-version", "\"13\"");
        extraHeaders.put("sec-fetch-dest", "document");
        extraHeaders.put("sec-fetch-mode", "navigate");
        extraHeaders.put("sec-fetch-site", "same-origin");
        extraHeaders.put("upgrade-insecure-requests", "1");

        String webViewVersion = getWebViewVersion();
        String dynamicUserAgent = String.format(
                "Mozilla/5.0 (Linux; Android 13; SM-G981B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/%s Mobile Safari/537.36",
                webViewVersion
        );
        Log.d("webViewVersion", "onCreate: "+dynamicUserAgent);

        extraHeaders.put("user-agent", dynamicUserAgent);
        extraHeaders.put("viewport-width", "383");


        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true);

        if (isFirstLaunch) {
            clearBrowserData();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirstLaunch", false);
            editor.apply();
        }

        LoadPage();
        binding.swipeRefreshLayout.setOnRefreshListener(this::LoadPage);
    }
    private String getWebViewVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo("com.google.android.webview", 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

            return "131.0.6778.135";
        }
    }
    public void LoadPage() {

        SharedPrefsForInstagram sharedPrefsForInstagram = new SharedPrefsForInstagram(InstagramLoginActivity.this);

        binding.webView.getSettings().setJavaScriptEnabled(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(binding.webView, true);
        binding.webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        binding.webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        binding.webView.getSettings().setDatabaseEnabled(true);
        binding.webView.getSettings().setBuiltInZoomControls(false);
        binding.webView.getSettings().setSupportZoom(true);
        binding.webView.getSettings().setUseWideViewPort(true);
        binding.webView.getSettings().setDomStorageEnabled(true);
        binding.webView.getSettings().setAllowFileAccess(true);
        binding.webView.getSettings().setLoadsImagesAutomatically(true);
        binding.webView.getSettings().setBlockNetworkImage(false);
        binding.webView.getSettings().setBlockNetworkLoads(false);
        binding.webView.getSettings().setLoadWithOverviewMode(true);
        binding.webView.clearCache(true);
        binding.webView.getSettings().setGeolocationEnabled(true);
        binding.webView.getSettings().setDisplayZoomControls(false);
        binding.webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 13; SM-G981B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Mobile Safari/537.36");

// TODO working on fixing cookies
        if (sharedPrefsForInstagram.getPreference() != null &&
                !Objects.equals(sharedPrefsForInstagram.getPreference().getPREFERENCE_ISINSTAGRAMLOGEDIN(), "true")) {
            CookieSyncManager.createInstance(InstagramLoginActivity.this);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
        }



        binding.webView.setWebViewClient(new MyWebViewClient());
        binding.webView.setWebChromeClient(new MyWebViewChromeClient());

        if (sharedPrefsForInstagram.getPreference() != null && !Objects.equals(sharedPrefsForInstagram.getPreference().getPREFERENCE_ISINSTAGRAMLOGEDIN(), "true")) {
            binding.webView.loadUrl("http://www.instagram.com/accounts/login", extraHeaders);
        } else {
            binding.webView.loadUrl("https://www.instagram.com/", extraHeaders);
        }


    }

    private void clearBrowserData() {
        // Clear WebView cache
        binding.webView.clearCache(true);

        // Clear cookies
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookies(null);
        cookieManager.flush();

        // Optionally clear any other data
        CookieSyncManager.createInstance(this);
        CookieManager.getInstance().removeAllCookie();
    }

    private class MyWebViewChromeClient extends WebChromeClient {


        @Override
        public void onPermissionRequest(PermissionRequest request) {
            boolean hasUnrecognizedFeatures = false;
            for (String permission : request.getResources()) {
                if (permission.startsWith("ambient-light-sensor") ||
                        permission.startsWith("bluetooth") ||
                        permission.startsWith("hid") ||
                        permission.startsWith("payment") ||
                        permission.startsWith("serial") ||
                        permission.startsWith("usb")) {
                    hasUnrecognizedFeatures = true;
                    break;
                }
            }

            if (!hasUnrecognizedFeatures) {
                // Check for specific permissions
                if (Arrays.asList(request.getResources()).contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE) ||
                        Arrays.asList(request.getResources()).contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE)) {
                    // Handle the specific permission request here
                    request.grant(new String[]{PermissionRequest.RESOURCE_VIDEO_CAPTURE, PermissionRequest.RESOURCE_AUDIO_CAPTURE});
                } else {
                    request.grant(request.getResources());
                }
            }
        }


        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            System.out.println("MyWebViewChromeClient= " + consoleMessage.message());
            return super.onConsoleMessage(consoleMessage);

        }


        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            if (newProgress >= 100) {

                String str = view.getUrl();
                String cookies = CookieManager.getInstance().getCookie(str);

                try {
                    String session_id = getCookie(str, "sessionid");
                    String csrftoken = getCookie(str, "csrftoken");
                    String userid = getCookie(str, "ds_user_id");
                    if (session_id != null && csrftoken != null && userid != null) {
                        ModelInstagramPref instagramPref = new ModelInstagramPref(session_id, userid, cookies, csrftoken, "true");
                        SharedPrefsForInstagram sharedPrefsForInstagram = new SharedPrefsForInstagram(InstagramLoginActivity.this);
                        sharedPrefsForInstagram.setPreference(instagramPref);
                        view.destroy();
                        Intent intent = new Intent();
                        intent.putExtra("result", "result");
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public String getCookie(String siteName, String CookieName) {
        String CookieValue = null;

        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(siteName);
        if (cookies != null && !cookies.isEmpty()) {
            String[] temp = cookies.split(";");
            for (String ar1 : temp) {
                if (ar1.contains(CookieName)) {
                    String[] temp1 = ar1.split("=");
                    CookieValue = temp1[1];
                    break;
                }
            }
        }
        return CookieValue;
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url, extraHeaders);
            return true;
        }

        @Override
        public void onLoadResource(WebView webView, String str) {
            super.onLoadResource(webView, str);
        }


        @Override
        public void onPageFinished(WebView webView, String str) {
            super.onPageFinished(webView, str);
        }

        @Override
        public void onReceivedError(WebView webView, int i, String str, String str2) {
            super.onReceivedError(webView, i, str, str2);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest webResourceRequest) {
            return super.shouldInterceptRequest(webView, webResourceRequest);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            view.loadUrl(request.getUrl().toString(), extraHeaders);
            return true;
        }
    }
}
