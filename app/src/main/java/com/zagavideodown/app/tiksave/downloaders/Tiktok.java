package com.zagavideodown.app.tiksave.downloaders;

import android.content.Context;
import android.widget.Toast;

import com.zagavideodown.app.tiksave.listener.AsyncResponse;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class Tiktok extends Base {

    static String VideoUrl = "";

    public Tiktok(Context c, AsyncResponse asyncResponse) {
        super(c, asyncResponse);
    }


    public static String play;
    public static String playHD;
    public static String music;
    public static String cover;
    public static String title;
    public static String msg;


    @Override
    protected Document doInBackground(String... strings) {

        try {
            Tiktok.VideoUrl = strings[0];
            String url = "https://www.tikwm.com/api/";
            this.roposoDoc = Jsoup.connect(url).ignoreContentType(true)
                    .data("url", Tiktok.VideoUrl)
                    .data("hd", "1")
                    .get();
            System.out.println(this.roposoDoc.body());
        } catch (Exception ignored) {
        }
        return this.roposoDoc;

    }

    @Override
    public void onPostExecute(Document document) {
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(document.body().text());

            msg = jsonObject.get("msg").toString();
            if (msg.equals("success")) {
                play = jsonObject.getJSONObject("data").get("play").toString();
                playHD = jsonObject.getJSONObject("data").get("hdplay").toString();
                music = jsonObject.getJSONObject("data").get("music").toString();
                cover = jsonObject.getJSONObject("data").get("cover").toString();
                title = jsonObject.getJSONObject("data").get("title").toString();
            } else {
                Toast.makeText(mainContext, msg, Toast.LENGTH_SHORT).show();
            }

            progressDialog.dismiss();

            delegate.processFinish(1);

        } catch (Exception e) {
            DownloadFailed();
            e.printStackTrace();
        }


    }


}

