package com.zagavideodown.app;

import android.app.Application;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import com.zagavideodown.app.ads.FullAds;
import com.google.android.gms.ads.MobileAds;

import io.reactivex.rxjava3.exceptions.UndeliverableException;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class AppController extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        new Thread(() -> {
            try {
                initializeApplication();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this, initializationStatus -> {
            });

        }).start();
        FullAds.loadAds(this);

    }

    private void initializeApplication() {
        try {
            configureRxJavaErrorHandler();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    private void configureRxJavaErrorHandler() {
        RxJavaPlugins.setErrorHandler(e -> {
            Throwable error = e;
            if (error instanceof UndeliverableException) {
                if (error.getCause() != null) {
                    error = error.getCause();
                }
            }
            if (!(error instanceof InterruptedException)) {
                Log.e("configureRxJavaErrorHandler", "Undeliverable exception received", error);
            }
        });
    }
}

