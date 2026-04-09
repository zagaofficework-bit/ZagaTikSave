package com.zagavideodown.app.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CustomWebView extends WebView {

    private EncryptionCallback callback;
    private Handler handler;
    private Runnable timeoutRunnable;

    public CustomWebView(Context context) {
        super(context);
        init();
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        getSettings().setJavaScriptEnabled(true);
        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        getSettings().setDomStorageEnabled(true);
        getSettings().setDatabaseEnabled(true);
        getSettings().setLoadWithOverviewMode(true);
        getSettings().setUseWideViewPort(true);

        setWebViewClient(new WebViewClient());
        addJavascriptInterface(this, "Android");

        handler = new Handler(Looper.getMainLooper());
    }

    public void encryptVideoUrl(String videoUrl, String token, String apiUrl, String otoken, String rtoken, String userAgent, EncryptionCallback callback) {
        this.callback = callback;

        // Cancel any existing timeout runnable
        if (timeoutRunnable != null) {
            handler.removeCallbacks(timeoutRunnable);
        }

        // Set a timeout for 30 seconds
        timeoutRunnable = () -> {
            if (callback != null) {
                callback.onEncryptedData("{\"error\":\"Operation timed out after 30 seconds.\"}");
            }
        };
        handler.postDelayed(timeoutRunnable, 15000); // 5 seconds timeout
        String cryptoJSCode = readFromAssets();
        if (cryptoJSCode != null) {
            evaluateJavascript(cryptoJSCode, null);  // Load CryptoJS library
        }
        String jsCode = createJsCode(videoUrl, token, apiUrl, otoken, rtoken, userAgent);
        evaluateJavascript(jsCode, null);
    }
    private String readFromAssets() {
        StringBuilder script = new StringBuilder();
        try (InputStream inputStream = getContext().getAssets().open("crypto-js.min.js");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                script.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return script.toString();
    }
    private String createJsCode(String videoUrl, String token, String apiUrl, String otoken, String rtoken, String userAgent) {
        return "function randomString(length) {" +
                "    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';" +
                "    let result = '';" +
                "    for (let i = 0; i < length; i++) {" +
                "        result += characters.charAt(Math.floor(Math.random() * characters.length));" +
                "    }" +
                "    return result;" +
                "}" +
                "const DOWN_SECRET = atob('VTJGc2RHVmtYMTh3VmZvVHFUcEFRd0FudTlXQjlvc0lNU25sZEloWWc2ck12RkpraHBUNmVVTTlZcWdwVHJrNDFtazhjYWxoWXZLeWhHRjBuMjZJRFhObXRYcUk4TWpzWHRzcTBubkFRTFJPcnNCdUxudTRNenU2M21wSnNHeXc=');" +
                "const bytes = CryptoJS.AES.decrypt(DOWN_SECRET, atob('bWFuaGctYXBp'));" +
                "const decryptedText = bytes.toString(CryptoJS.enc.Utf8);" +
                "var key = decryptedText;" +
                "var url = '" + videoUrl + "';" +
                "function encryptData(data) {" +
                "    const key = CryptoJS.enc.Hex.parse(decryptedText);" +
                "    const iv = CryptoJS.lib.WordArray.random(16);" +
                "    const encrypted = CryptoJS.AES.encrypt(data, key, {" +
                "        iv: iv," +
                "        mode: CryptoJS.mode.CBC," +
                "        padding: CryptoJS.pad.Pkcs7" +
                "    });" +
                "    return {" +
                "        iv: iv.toString(CryptoJS.enc.Hex)," +
                "        k: randomString(11) + '8QXBNv5pHbzFt5QC'," +
                "        r: 'BRTsfMmf3CuN'," +
                "        encryptedData: encrypted.toString()" +
                "    };" +
                "};" +
                "var encryptedData_new = encryptData(JSON.stringify({url: url, unlock: true}));" +
                "var randomUserAgent = '" + userAgent + "';" +
                "const fetchData = async () => {" +
                "    const response = await fetch('" + apiUrl + "', {" +
                "        method: 'POST'," +
                "        headers: {" +
                "            'Content-Type': 'application/json'," +
                "            'token': '" + token + "'," +
                "            'crossorigin': 'anonymous'," +
                "            'User-Agent': randomUserAgent," +
                "            'Origin': '" + otoken + "'," +
                "            'Referer': '" + rtoken + "'" +
                "        }," +
                "        body: JSON.stringify({data: encryptedData_new})" +
                "    });" +
                "    const jsonData = await response.json();" +
                "    window.Android.onFetchComplete(JSON.stringify(jsonData));" +
                "};" +
                "fetchData().catch(err => window.Android.onFetchComplete(JSON.stringify({error: err.message})));";
    }

    @JavascriptInterface
    public void onFetchComplete(String response) {
        handler.removeCallbacks(timeoutRunnable);
        if (callback != null) {
            callback.onEncryptedData(response);
        }
    }
    public interface EncryptionCallback {
        void onEncryptedData(String encryptedData);
    }
}

