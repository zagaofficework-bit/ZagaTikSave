package com.zagavideodown.app.listener;

public interface VideoDownloader {

    String getVideoId(String link);

    void DownloadVideo();
}

