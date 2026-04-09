/*
 * *
 *  * Created by Syed Usama Ahmad on 3/4/23, 2:35 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 3/4/23, 2:28 AM
 *
 */

package com.zagavideodown.app.other.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.zagavideodown.app.R;
import com.zagavideodown.app.models.dlapismodels.Format;
import com.zagavideodown.app.models.dlapismodels.Video;
import com.zagavideodown.app.receiver.DownloadWorker;
import com.zagavideodown.app.utils.DownloadFileMain;
import com.zagavideodown.app.utils.Utils;
import com.yausername.youtubedl_android.mapper.VideoFormat;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class QualityBottomsheetAdapter extends RecyclerView.Adapter<QualityBottomsheetAdapter.ViewHolder> {

    private final String vidSource;
    boolean isDLAPI = false;
    boolean issingle;
    boolean hasMultiAlbumb = false;
    private String url, title, id;
    private Activity context;
    private Activity activity;
    private List<Format> filesList;
    private List<Video> filesVideoList;
    private List<VideoFormat> videoFormatList;
    private String vidurl;

    public QualityBottomsheetAdapter(Activity context, List<Format> filesList, String source, boolean issingle) {
        this.context = context;
        this.filesList = addtoarrayno_m3u8(filesList);
        this.issingle = issingle;
        this.vidSource = source;
    }

    public QualityBottomsheetAdapter(Activity context, String source, boolean issingle, List<Video> filesList, boolean hasMultiAlbumb) {
        this.context = context;
        this.filesVideoList = filesList;
        this.issingle = issingle;
        this.hasMultiAlbumb = hasMultiAlbumb;
        this.vidSource = source;
    }

    public QualityBottomsheetAdapter(Activity context, String url, String source, boolean issingle) {
        this.context = context;
        this.vidurl = url;
        this.issingle = issingle;
        this.vidSource = source;
    }

    public QualityBottomsheetAdapter(Activity activity, String source, boolean issingle, List<VideoFormat> videoList_sub_video, boolean isDLAPI, String title, String id, String url) {
        this.activity = activity;
        this.videoFormatList = videoList_sub_video;
        this.issingle = issingle;
        this.vidSource = source;
        this.isDLAPI = isDLAPI;
        this.url = url;
        this.title = title;
        this.id = id;
    }

    public static String extractQuality(String input) {
        try {
            // This pattern matches strings like 1080p, 720p, etc.
            Pattern pattern = Pattern.compile("(\\d+p)");
            Matcher matcher = pattern.matcher(input);

            if (matcher.find()) {
                // Returns the first matched group which is the quality
                return matcher.group(1);
            }

            return "HD";
        } catch (Exception e) {
            e.printStackTrace();
            return "HD";
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quality_bottomfragment, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NotNull QualityBottomsheetAdapter.ViewHolder holder, final int position) {
        String resolution = "none";
        if (isDLAPI) {

            final VideoFormat videoFormatFiles = videoFormatList.get(position);
            System.out.println("reccccc VVKK working1111 06 " + videoFormatFiles.getFormat() + " >>>>> " + videoFormatFiles.getFormatId());


            if (videoFormatFiles.getFormat().length() > 20 && videoFormatFiles.getFormatNote() != null) {
                if (videoFormatFiles.getFormat().contains("-")) {
                    String[] ccc = videoFormatFiles.getFormat().split("-");
                    resolution = (ccc.length > 2) ? ccc[2] : ccc[1];
                } else {
                    resolution = videoFormatFiles.getFormatNote();
                }

            } else {
                resolution = videoFormatFiles.getFormat();
            }

//            String finalResolution = extractQuality(resolution);
            String finalResolution = resolution;


            holder.resolution.setText(String.format("%s", resolution));
            holder.resolution.setSelected(true);
            holder.resolution.setSingleLine(true);

            String formatedsizew = "", newformatedsizew = "";
            newformatedsizew = Utils.getStringSizeLengthFile(videoFormatFiles.getFileSize());
            if (newformatedsizew.contains("0.00")) {
                formatedsizew = Utils.getStringSizeLengthFile(videoFormatFiles.getFileSizeApproximate());
            } else {
                formatedsizew = newformatedsizew;
            }
            formatedsizew = formatedsizew.replace(",", ".");
            holder.fileSize.setText(!formatedsizew.equals("") ? formatedsizew + "" : "undefined");
            holder.downloadbtnd.setOnClickListener(v -> {

                Data workData = new Data.Builder()
                        .putString(DownloadWorker.urlKey, url)
                        .putString(DownloadWorker.nameKey, title + finalResolution)
                        .putString(DownloadWorker.formatIdKey, videoFormatFiles.getFormatId())
                        .putString(DownloadWorker.acodecKey, videoFormatFiles.getAcodec())
                        .putString(DownloadWorker.vcodecKey, videoFormatFiles.getVcodec())
                        .putString(DownloadWorker.taskIdKey, id + "_" + finalResolution)
                        .putString(DownloadWorker.ext, videoFormatFiles.getExt())
                        .build();

                WorkRequest workRequest = new OneTimeWorkRequest.Builder(DownloadWorker.class)
                        .addTag(id)
                        .setInputData(workData)
                        .build();

                WorkManager.getInstance(activity).enqueueUniqueWork(
                        id,
                        ExistingWorkPolicy.KEEP,
                        (OneTimeWorkRequest) workRequest
                );

                activity.runOnUiThread(() -> Utils.ShowToast(activity, activity.getResources().getString(R.string.don_start)));


            });


        } else {
            if (issingle) {
                System.out.println("reccccc VVKK working0000 ");

                holder.resolution.setText("HD");

                holder.fileSize.setText("undefined");
                holder.downloadbtnd.setOnClickListener(v -> DownloadFileMain.startDownloading(context, vidurl, vidSource + "_" + System.currentTimeMillis(), ".mp4"));

            } else {

                if (hasMultiAlbumb) {

                    final Video files = filesVideoList.get(position);


                    if (!files.getURL().contains(".m3u8") && files.getProtocol().contains("http")) {


                        String finalResolution = extractQuality(String.format("%s", files.getFormat().length() > 20 && files.getFormatID() != null ? files.getFormatID() : files.getFormat()));
                        holder.resolution.setText(finalResolution);


                        String formatedsizew = "NaN";


                        holder.fileSize.setText(formatedsizew);

                        holder.downloadbtnd
                                .setOnClickListener(
                                        v -> {
                                            if (files.getEXT().equals("com") || files.getEXT().equals("")) {
                                                DownloadFileMain.startDownloading(context, files.getURL(), vidSource + "_" + files.getTitle(), ".mp4");
                                            } else if (files.getEXT().equals("gif")) {
                                                DownloadFileMain.startDownloading(context, files.getURL(), vidSource + "_" + files.getTitle(), "." + files.getEXT());
                                            } else {
                                                DownloadFileMain.startDownloading(context, files.getURL(), vidSource + "_" + files.getTitle(), "." + files.getEXT());
                                            }
                                        });
                    }

                } else {
                    final Format files = filesList.get(position);
                    System.out.println("reccccc VVKK working1111 06 " + files.getFormat() + files.getProtocol());


                    if (!files.getURL().contains(".m3u8") && files.getProtocol().contains("http")) {


                        String finalResolution = extractQuality(String.format("%s", files.getFormat().length() > 20 && files.getFormatNote() != null ? files.getFormatNote() : files.getFormat()));
                        holder.resolution.setText(finalResolution);


                        String formatedsizew = Utils.getStringSizeLengthFile(files.getFilesize());
                        formatedsizew = formatedsizew.replace(",", ".");
                        holder.fileSize.setText(!formatedsizew.equals("") ? formatedsizew + "" : "undefined");
                        holder.downloadbtnd.setOnClickListener(v -> {
                            if (files.getEXT().equals("com") || files.getEXT().equals("")) {
                                DownloadFileMain.startDownloading(context, files.getURL(), vidSource + "_" + System.currentTimeMillis(), ".mp4");
                            } else if (files.getEXT().equals("gif")) {
                                DownloadFileMain.startDownloading(context, files.getURL(), vidSource + "_" + System.currentTimeMillis(), "." + files.getEXT());
                            } else {
                                DownloadFileMain.startDownloading(context, files.getURL(), vidSource + "_" + System.currentTimeMillis(), "." + files.getEXT());
                            }

                        });
                    }

                }

            }
        }

    }

    String formet_size(long bytes) {

        System.out.println("fhsdjfsdjfsfsdfk addd long=" + bytes);

        try {

            if (bytes < 1024) {
                return bytes + "B";
            } else if (bytes < 1048576L) {
                return Math.round(bytes / 1024) + "KB";
            } else if (bytes < 1073741824) {
                return Math.round(bytes / 1048576) + "MB";
            } else if (bytes > 1073741824) {
                System.out.println("fhsdjfsdjfsfsdfk addd long=" + bytes);

                return Math.round(bytes / 1073741824) + "GB";

            }

        } catch (Exception e) {
            System.out.println("fhsdjfsdjfsfsdfk addd long eee=" + e.getMessage());

            return "NaN";

        }

        return "NaN";
    }

    void removeRepeatedVideos() {
        List<String> uniqueResolutions = new ArrayList<>();
        List<Video> filteredVideoList = new ArrayList<>();

        for (Video video : filesVideoList) {
            String resolution = String.format("%s", video.getFormat().length() > 20 && video.getFormatID() != null
                    ? video.getFormatID()
                    : video.getFormat());
            resolution = extractQuality(resolution);

            if (!uniqueResolutions.contains(resolution)) {
                uniqueResolutions.add(resolution);
                filteredVideoList.add(video);
            }
        }

        filesVideoList.clear();
        filesVideoList.addAll(filteredVideoList);
    }

    void removeRepeatedFormates() {
        List<String> uniqueResolutions = new ArrayList<>();
        List<Format> filteredVideoList = new ArrayList<>();

        for (Format video : filesList) {
            String resolutionmm = String.format("%s", video.getFormat().length() > 20 && video.getFormatNote() != null ? video.getFormatNote() : video.getFormat());
            resolutionmm = extractQuality(resolutionmm);

            if (!uniqueResolutions.contains(resolutionmm)) {
                uniqueResolutions.add(resolutionmm);
                filteredVideoList.add(video);
            }
        }

        filesList.clear();
        filesList.addAll(filteredVideoList);
    }

    @Override
    public int getItemCount() {
        if (isDLAPI) {
            return videoFormatList.size();
        }
        if (hasMultiAlbumb) {

            return filesVideoList.size();
        }
        return issingle ? 1 : filesList.size();
    }

    List<Format> addtoarrayno_m3u8(List<Format> fromList) {
        if (fromList != null) {
            List<Format> toList = new ArrayList<>();
            for (int i = 0; i < fromList.size(); i++) {
                System.out.println("fhsdjfsdjfk " + fromList.get(i).getURL());

                if (!fromList.get(i).getURL().contains(".m3u8")) {
                    System.out.println("fhsdjfsdjfk addd" + fromList.get(i).getURL());

                    toList.add(fromList.get(i));
                }
            }
            return toList;
        }
        return new ArrayList<>();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView resolution;
        TextView fileSize;
        Button downloadbtnd;


        public ViewHolder(View itemView) {
            super(itemView);
            resolution = itemView.findViewById(R.id.resolution);
            fileSize = itemView.findViewById(R.id.fileSize);
            downloadbtnd = itemView.findViewById(R.id.downloadqua);
        }
    }
}

