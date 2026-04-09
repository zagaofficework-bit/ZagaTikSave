/*
 * *
 *  * Created by Syed Usama Ahmad on 2/27/23, 1:22 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 2/26/23, 11:10 PM
 *
 */

package com.zagavideodown.app.other;

import static com.zagavideodown.app.other.DownloadVideosMain2.Mcontext;
import static com.zagavideodown.app.other.DownloadVideosMain2.fromService;
import static com.zagavideodown.app.other.DownloadVideosMain2.pd;

import android.content.Context;
import android.os.AsyncTask;

import androidx.appcompat.app.AlertDialog;

import com.zagavideodown.app.R;
import com.zagavideodown.app.utils.DownloadFileMain;
import com.zagavideodown.app.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class AparatDownloader {

    private final Context context;
    private final String VideoURL;

    public AparatDownloader(Context context, String vid) {
        this.context = context;
        VideoURL = vid;
    }

    public void DownloadVideo() {
        new CallAparatData().execute(VideoURL);
    }


    public static class CallAparatData extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            try {
                this.RoposoDoc = Jsoup.connect(strArr[0]).get();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {
            boolean isSecon = false;

            try {

                if (!fromService) {

                    pd.dismiss();
                }
                System.out.println("myresponseis111 exp166 " + document);


                String data = "";

                Elements elements = document.select("ul");
                for (Element element : elements) {
                    if (element.attr("class").equals("menu-list")) {


                        String replaceString = element.getElementsByTag("a").attr("href");
                        if (replaceString.contains("https")) {
                            if (isSecon) {

                                String quality_144p = replaceString;
                                String quality_240p = replaceString.replace("144p", "240p");
                                String quality_360p = replaceString.replace("144p", "240p");
                                String quality_480p = replaceString.replace("144p", "240p");
                                String quality_720p = replaceString.replace("144p", "240p");

                                System.out.println("myresponseis111 exp166 fff 144p = " + quality_144p);
                                System.out.println("myresponseis111 exp166 fff 720p = " + quality_720p);


                                ArrayList<String> arrayList1 = new ArrayList<>();
                                arrayList1.add(quality_144p);
                                arrayList1.add(quality_240p);
                                arrayList1.add(quality_360p);
                                arrayList1.add(quality_480p);
                                arrayList1.add(quality_720p);

                                CharSequence[] charSequenceArr = new CharSequence[arrayList1.size()];

                                charSequenceArr[0] = "144p";
                                charSequenceArr[1] = "240p";
                                charSequenceArr[2] = "3600p";
                                charSequenceArr[3] = "480p";
                                charSequenceArr[4] = "720p";

                                new AlertDialog.Builder(Mcontext).setTitle("Quality!").setItems(charSequenceArr, (dialogInterface, i) -> DownloadFileMain.startDownloading(Mcontext, arrayList1.get(i), "Aparat_" + System.currentTimeMillis(), ".mp4")).setPositiveButton("OK", (dialogInterface, i) -> {
                                    if (!fromService) {

                                        pd.dismiss();
                                    }
                                }).setCancelable(false).show();


                            } else {
                                isSecon = true;

                            }
                        }

                    }
                }


            } catch (Exception unused) {
                System.out.println("myresponseis111 exp " + unused.getMessage());


                if (!fromService) {

                    pd.dismiss();
                }
                Utils.ShowToast(Mcontext, Mcontext.getResources().getString(R.string.somthing));
            }
        }


    }


}

