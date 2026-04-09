package com.zagavideodown.app.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class VideoViewCustom extends VideoView {

    PlayPauseListener mListener;

    public interface PlayPauseListener {
        void onVideoPause();

        void onVideoPlay();
    }

    public void setOnPlayPauseListener(PlayPauseListener listener) {
        this.mListener = listener;
    }

    public VideoViewCustom(Context context) {
        super(context);
    }

    public VideoViewCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoViewCustom(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void start() {
        super.start();
        if (this.mListener != null) {
            this.mListener.onVideoPlay();
        }
    }

    @Override
    public void pause() {
        super.pause();
        if (this.mListener != null) {
            this.mListener.onVideoPause();
        }
    }
}

