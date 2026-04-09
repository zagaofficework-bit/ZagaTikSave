package com.zagavideodown.app.webservice;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.zagavideodown.app.GlobalConstant;
import com.zagavideodown.app.interfaces.EncryptionCallbackNew;
import com.zagavideodown.app.utils.Utils;
import com.zagavideodown.app.views.CustomWebView;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewJetApi {
    private static final String TAG = "EncryptionAndNetworkRequest";

    // Base64 encoded tokens and URL
    private static final String MAIN_URL = "aHR0cHM6Ly9hcGkuem0uaW8udm4vdjEvc29jaWFsL2F1dG9saW5r";
    private static final String MAIN_TOKEN = "ZXlKMGVYQWlPaUpxZDNRaUxDSmhiR2NpT2lKSVV6STFOaUo5LmV5Snh4eA==";
    private static final String O_TOKEN = "aHR0cHM6Ly9qMmRvd25sb2FkLmNvbQ==";
    private static final String R_TOKEN = "aHR0cHM6Ly9qMmRvd25sb2FkLmNvbS8=";
    // Regular expression to match various URL patterns
    private static final Pattern URL_PATTERN = Pattern.compile(
            "(?:(?:https?|ftp|file)://|www\\.)[-A-Z0-9+&@#/%=~_|$?!:,.]*[-A-Z0-9+&@#/%=~_|$]",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL
    );
    // Regular expression to check if the URL matches known domains
    private static final Pattern DOMAIN_PATTERN = Pattern.compile(
            "^(?:https?://)?(?:[\\w-]+\\.)?(tiktok|douyin|iesdouyin|capcut|instagram|espn|kuaishou|pinterest|pin|imdb|imgur|ifunny|reddit|twitter|x|t|vimeo|snapchat|bilibili|sharechat|tumblr|hipi|getstickerpack|xiaohongshu|weibo|miaopai|meipai|xiaoying|nationalvideo|yingke|soundcloud|spotify|zingmp3|bitchute|febspot|izlesene|9gag|rumble|streamable|ted|sohu|ixigua)\\.[a-z]{2,}(/.*)?$",
            Pattern.CASE_INSENSITIVE
    );

    public static void sendNewJetApiDataRequest(Context context, String url, EncryptionCallbackNew callback) {
        try {
            String apiUrl = decodeBase64(MAIN_URL);
            String token = decodeBase64(MAIN_TOKEN);
            String otoken = decodeBase64(O_TOKEN);
            String rtoken = decodeBase64(R_TOKEN);

            String userAgent = getRandomUserAgent();

            new CustomWebView(context).encryptVideoUrl(url, token, apiUrl, otoken, rtoken, userAgent, encr -> {
                Log.d(TAG, "Encrypted Data: " + encr);
                // Pass the encrypted data through the custom interface
                callback.onDataEncrypted(encr);
            });
        } catch (Exception e) {
            Log.e(TAG, "Error sending API data request", e);
            // You might want to handle errors differently
            e.printStackTrace();
        }
    }

    private static String decodeBase64(String encoded) {
        return new String(Base64.decode(encoded, Base64.DEFAULT), StandardCharsets.UTF_8).trim();
    }

    private static String getRandomUserAgent() {
        try {
            int randomNumber = Utils.getRandomNumber(GlobalConstant.UserAgentsList2.length);
            return GlobalConstant.UserAgentsList2[randomNumber];
        } catch (Exception e) {
            return "Mozilla/5.0 (Linux; Android 10;TXY567) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/8399.0.9993.96 Mobile Safari/599.36";
        }
    }


    public static boolean isAllowedDomain(String inputString) {
        Matcher urlMatcher = URL_PATTERN.matcher(inputString);
        String extractedURL = urlMatcher.find() ? urlMatcher.group() : inputString;

        Matcher domainMatcher = DOMAIN_PATTERN.matcher(extractedURL);
        return domainMatcher.matches();
    }
}

