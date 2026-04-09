package com.zagavideodown.app.other;


import static com.zagavideodown.app.other.DownloadVideosMain2.Mcontext;
import static com.zagavideodown.app.other.DownloadVideosMain2.fromService;
import static com.zagavideodown.app.other.DownloadVideosMain2.pd;

import android.content.Context;
import android.os.AsyncTask;

import com.zagavideodown.app.R;
import com.zagavideodown.app.utils.DownloadFileMain;
import com.zagavideodown.app.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AllocineDownloader {

    private final Context context;
    private final String VideoURL;

    public AllocineDownloader(Context context, String vid) {
        this.context = context;
        VideoURL = vid;
    }

    public void DownloadVideo() {
        new CallAllocineData().execute(VideoURL);
    }


    public static class CallAllocineData extends AsyncTask<String, Void, Document> {
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

                Elements elements = document.select("link");
                for (Element element : elements) {
                    if (element.attr("as").equals("video")) {

                        String replaceString = element.getElementsByTag("link").attr("href");
                        System.out.println("myresponseis111 list_of_qualities" + replaceString);
                        DownloadFileMain.startDownloading(Mcontext, "https:" + replaceString, "Allocine_" + System.currentTimeMillis(), ".mp4");


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

