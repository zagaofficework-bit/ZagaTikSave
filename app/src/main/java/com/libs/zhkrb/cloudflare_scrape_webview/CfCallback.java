/*
 * *
 *  * Created by Syed Usama Ahmad on 2/27/23, 1:22 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 2/26/23, 11:10 PM
 *
 */

package com.libs.zhkrb.cloudflare_scrape_webview;

import java.net.HttpCookie;
import java.util.List;

public interface CfCallback {

    /**
     * get cookie success
     *
     * @param cookieList HttpCookie list
     * @param hasNewUrl  whether to redirect new url
     * @param newUrl     new url
     */
    void onSuccess(List<HttpCookie> cookieList, boolean hasNewUrl, String newUrl);

    /**
     * get cookie fail
     *
     * @param code fail code
     * @param msg  fail mag
     */
    void onFail(int code, String msg);

}
