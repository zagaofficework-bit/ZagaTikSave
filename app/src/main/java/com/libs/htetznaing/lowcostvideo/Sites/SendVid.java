package com.libs.htetznaing.lowcostvideo.Sites;

import static com.libs.htetznaing.lowcostvideo.Utils.Utils.putModel;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.libs.htetznaing.lowcostvideo.LowCostVideo;
import com.libs.htetznaing.lowcostvideo.Model.XModel;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
This is direct link getter for SendVid
    By
Khun Htetz Naing
 */

public class SendVid {
    public static void fetch(String url, final LowCostVideo.OnTaskCompleted onTaskCompleted) {
        AndroidNetworking.get(url)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        String src = getSrc(response);
                        if (src != null) {
                            ArrayList<XModel> xModels = new ArrayList<>();
                            putModel(src, "Normal", xModels);
                            onTaskCompleted.onTaskCompleted(xModels, false);
                        } else onTaskCompleted.onError();
                    }

                    @Override
                    public void onError(ANError anError) {
                        onTaskCompleted.onError();
                    }
                });
    }

    private static String getSrc(String response) {
        final String regex = "<source src=\"(.*?)\"";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(response);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
