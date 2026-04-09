/*
 * *
 *  * Created by Syed Usama Ahmad on 2/27/23, 1:22 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 2/26/23, 11:10 PM
 *
 */

package com.libs.zhkrb.cloudflare_scrape_webview.util;

import android.util.Log;

public class LogUtil {

    public static void e(String tag, String content) {
        Log.e(tag, content);
    }

    public static void e(String content) {
        Log.e("cloudflare", content);
    }

}
