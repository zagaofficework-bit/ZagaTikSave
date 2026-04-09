package com.zagavideodown.app.activities;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.zagavideodown.app.BaseActivity;
import com.zagavideodown.app.BuildConfig;
import com.zagavideodown.app.GlobalConstant;
import com.zagavideodown.app.R;
import com.zagavideodown.app.SharedPreferenceUtils;
import com.zagavideodown.app.animation.Techniques;
import com.zagavideodown.app.animation.YoYo;
import com.zagavideodown.app.databinding.ActivitySplashBinding;
import com.yausername.ffmpeg.FFmpeg;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SplashActivity extends BaseActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Completable.fromAction(() -> initLibraries(SplashActivity.this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        // it worked
                        Log.e("configureRxJavaSuccessHandler", "Done");

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (BuildConfig.DEBUG) {
                            Log.e("configureRxJavaErrorHandler", "failed to initialize youtubedl-android", e);
                            Toast.makeText(getApplicationContext(), "initialization failed: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        YoYo.with(Techniques.ZoomInLeft)
                .duration(1200)
                .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                .interpolate(new AccelerateDecelerateInterpolator())
                .withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animation) {

                        animationTvDes();
                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animation) {

                    }
                }).playOn(binding.tvTitle);
    }

    private void animationTvDes() {
        binding.tvDes.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.SlideInLeft)
                .duration(1200)
                .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                .interpolate(new AccelerateDecelerateInterpolator())
                .withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animation) {

                    }
                }).playOn(binding.tvDes);
    }

    private void executeCase() {
        if (!SharedPreferenceUtils.getInstance(SplashActivity.this).getBoolean(GlobalConstant.LANGUAGE_SET, false)) {

            startActivity(new Intent(SplashActivity.this, LanguageActivity.class));
            finish();
        } else {


            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    }

    private void initLibraries(Context context) throws YoutubeDLException {
        try {
            initDlLibs(context, true);

                executeCase();

        } catch (Throwable e) {
            Log.e("SplashScreenErrorYTDLP", "failed ", e);

            e.printStackTrace();
        }
    }

    static void initDlLibs(Context context, boolean isStart) throws YoutubeDLException {
        try {
            YoutubeDL.getInstance().init(context);
            FFmpeg.getInstance().init(context);
//            Aria2c.getInstance().init(context);

            if (isStart) {
                updateYoutubeDL(context);
            }
        } catch (Throwable e) {
            Log.e("SplashScreenErrorinitDlLibs", "failed ", e);

            e.printStackTrace();
        }
    }
    static void updateYoutubeDL(Context context) {


        final CompositeDisposable compositeDisposable = new CompositeDisposable();

        Disposable disposable = Observable.fromCallable(() -> YoutubeDL.getInstance().updateYoutubeDL(context, YoutubeDL.UpdateChannel._NIGHTLY))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(status -> {

                }, e -> {
                    if (BuildConfig.DEBUG) {
                        Log.e("SplashScreen", "failed to update", e);
//                        Toast.makeText(context, "update failed", Toast.LENGTH_LONG).show();
                    }
                });
        compositeDisposable.add(disposable);
    }
}
