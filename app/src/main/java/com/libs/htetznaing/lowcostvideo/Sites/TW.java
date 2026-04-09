package com.libs.htetznaing.lowcostvideo.Sites;

import static com.libs.htetznaing.lowcostvideo.Utils.Utils.sortMe;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.libs.htetznaing.lowcostvideo.Core.Twitter;
import com.libs.htetznaing.lowcostvideo.LowCostVideo;

/*
This is direct link getter for Twitter
    By
Khun Htetz Naing
 */

public class TW {
    public static void fetch(String url, final LowCostVideo.OnTaskCompleted onComplete) {
        AndroidNetworking.post("https://twdown.net/download.php")
                .addBodyParameter("URL", url)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        onComplete.onTaskCompleted(sortMe(Twitter.fetch(response)), true);
                    }

                    @Override
                    public void onError(ANError anError) {
                        onComplete.onError();
                    }
                });
    }
}
