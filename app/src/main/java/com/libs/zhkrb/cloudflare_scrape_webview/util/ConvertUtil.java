/*
 * *
 *  * Created by Syed Usama Ahmad on 2/27/23, 1:22 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 2/26/23, 11:10 PM
 *
 */

package com.libs.zhkrb.cloudflare_scrape_webview.util;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

public class ConvertUtil {

    /**
     * 转换list为 ; 符号链接的字符串
     *
     * @param list 列表
     * @return string
     */
    public static String listToString(List<HttpCookie> list) {
        char separator = ";".charAt(0);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).getName()).append("=").append(list.get(i).getValue()).append(separator);
        }
        return sb.substring(0, sb.toString().length() - 1);
    }

    /**
     * 转换String为List
     *
     * @param cookie httpCookie
     * @return List<HttpCookie>
     */
    public static List<HttpCookie> String2List(String cookie) {
        List<HttpCookie> list = new ArrayList<>();
        String[] listStr = cookie.split(";");
        for (String str : listStr) {
            String[] cookieStr = str.split("=");
            list.add(new HttpCookie(cookieStr[0], cookieStr[1]));
        }
        return list;
    }

}
