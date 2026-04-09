package com.libs.htetznaing.lowcostvideo.Sites;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.libs.htetznaing.lowcostvideo.LowCostVideo;
import com.libs.htetznaing.lowcostvideo.Model.XModel;
import com.libs.htetznaing.lowcostvideo.Utils.GPhotosUtils;

import java.util.ArrayList;

/*
This is direct link getter for Google Photos
    By
Khun Htetz Naing
 */

public class GPhotos {
    public static void fetch(String url, final LowCostVideo.OnTaskCompleted onTaskCompleted) {
        AndroidNetworking.get(url)
                .setUserAgent(LowCostVideo.agent)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        ArrayList<XModel> xModels = GPhotosUtils.getGPhotoLink(response);
                        onTaskCompleted.onTaskCompleted(xModels, true);
                    }

                    @Override
                    public void onError(ANError anError) {
                        onTaskCompleted.onError();
                    }
                });
    }
}
