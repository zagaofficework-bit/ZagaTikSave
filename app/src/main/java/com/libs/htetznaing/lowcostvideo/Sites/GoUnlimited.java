package com.libs.htetznaing.lowcostvideo.Sites;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.libs.htetznaing.lowcostvideo.LowCostVideo;
import com.libs.htetznaing.lowcostvideo.Model.XModel;
import com.libs.htetznaing.lowcostvideo.Utils.JSUnpacker;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
This is direct link getter for GoUnlimited
    By
Khun Htetz Naing
 */

public class GoUnlimited {
    public static void fetch(String url, final LowCostVideo.OnTaskCompleted onComplete) {
        AndroidNetworking.get(url)
                .setUserAgent(LowCostVideo.agent)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        ArrayList<XModel> xModels = parse(response);
                        if (xModels != null) {
                            onComplete.onTaskCompleted(xModels, false);
                        } else onComplete.onError();
                    }

                    @Override
                    public void onError(ANError anError) {
                        onComplete.onError();
                    }
                });
    }

    private static ArrayList<XModel> parse(String response) {
        JSUnpacker jsUnpacker = new JSUnpacker(getEvalCode(response));
        if (jsUnpacker.detect()) {
            String src = getSrc(jsUnpacker.unpack());
            if (src != null && src.length() > 0) {
                XModel xModel = new XModel();
                xModel.setUrl(src);
                xModel.setQuality("Normal");

                ArrayList<XModel> xModels = new ArrayList<>();
                xModels.add(xModel);
                return xModels;
            }
        }
        return null;
    }


    private static String getSrc(String code) {
        final String regex = "src: ?\"(.*?)\"";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static String getEvalCode(String html) {
        final String regex = ">eval(.*)";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }
}
