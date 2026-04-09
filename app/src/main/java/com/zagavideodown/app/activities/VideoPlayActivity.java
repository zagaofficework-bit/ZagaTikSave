/*
 * *
 *  * Created by Syed Usama Ahmad on 3/17/23, 11:37 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 3/17/23, 10:52 PM
 *
 */

package com.zagavideodown.app.activities;

import android.app.PictureInPictureParams;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Rational;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.zagavideodown.app.databinding.ActivityVideoPlayBinding;
import com.zagavideodown.app.utils.Utils;
import com.libs.universalvideoview.UniversalVideoView;


public class VideoPlayActivity extends AppCompatActivity {

    String urls;
    PictureInPictureParams.Builder pictureInPictureParamsBuilder;
    private ActivityVideoPlayBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityVideoPlayBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        setupPlayer();

        binding.imgPictureinpicture.setOnClickListener(v -> startPictureInPictureFeature());


        binding.back.setOnClickListener(view1 -> onBackPressed());


        binding.storyVideo.setOnClickListener(view12 -> {
            binding.storyVideo.setVisibility(View.GONE);
            binding.videoView.start();
        });


    }





    void setupPlayer() {
        try {
            urls = getIntent().getStringExtra("videourl");
            binding.videoView.setMediaController(binding.mediaController);
            binding.videoView.setVideoURI(Uri.parse(urls));
            binding.videoView.setAutoRotation(true);
            binding.storyVideo.setVisibility(View.GONE);
            binding.videoView.setVideoViewCallback(new UniversalVideoView.VideoViewCallback() {

                @Override
                public void onScaleChange(boolean isFullscreen) {

                }

                @Override
                public void onPause(MediaPlayer mediaPlayer) {
                    binding.storyVideo.setVisibility(View.VISIBLE);

                }

                @Override
                public void onStart(MediaPlayer mediaPlayer) {
                    binding.storyVideo.setVisibility(View.GONE);
                }

                @Override
                public void onBufferingStart(MediaPlayer mediaPlayer) {// steam start loading
                }

                @Override
                public void onBufferingEnd(MediaPlayer mediaPlayer) {// steam end loading
                }

            });


            binding.videoView.start();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onNewIntent(Intent intent) {

        urls = getIntent().getStringExtra("videourl");

        setupPlayer();

        super.onNewIntent(intent);
    }

    private void startPictureInPictureFeature() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                pictureInPictureParamsBuilder = new PictureInPictureParams.Builder();

                Rational aspectRatio = new Rational(16, 9);

                pictureInPictureParamsBuilder.setAspectRatio(aspectRatio).build();

                enterPictureInPictureMode(pictureInPictureParamsBuilder.build());

            } else {

                runOnUiThread(() -> {
                    Utils.ShowToastError(VideoPlayActivity.this,
                            "Picture in picture mode is not supported in this device"
                    );
                });

            }
        } catch (Exception e) {
            e.printStackTrace();

            runOnUiThread(() -> {
                Utils.ShowToastError(VideoPlayActivity.this,
                        "Picture in picture mode error"
                );
            });

        }
    }

    @Override
    public void onUserLeaveHint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!isInPictureInPictureMode()) {
                Rational aspectRatio = new Rational(binding.videoView.getWidth(), binding.videoView.getHeight());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    try {
                        pictureInPictureParamsBuilder.setAspectRatio(aspectRatio).build();
                        enterPictureInPictureMode(pictureInPictureParamsBuilder.build());
                    } catch (Exception e) {
                        //    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode,
                                              @NonNull Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        if (isInPictureInPictureMode) {

            Log.d("TAG", "onPictureInPictureModeChanged 0: " + isInPictureInPictureMode);
        } else {
            Log.d("TAG", "onPictureInPictureModeChanged 1: " + isInPictureInPictureMode);


        }
    }


    public void onDestroy() {
        super.onDestroy();
        try {
            binding.videoView.closePlayer();
            urls = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

