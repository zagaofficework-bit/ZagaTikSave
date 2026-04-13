package com.zagavideodown.app.ads;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zagavideodown.app.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;



public class NativeAdsAdmob {
    public static void loadNativeBig2(Activity mActivity, View view) {
        RelativeLayout rlLoadingAds;
        FrameLayout frameLayout;

        if (view != null) {
            frameLayout = view.findViewById(R.id.ads_container_native_2);
            rlLoadingAds = view.findViewById(R.id.rl_loading_ad_2);

        } else {
            frameLayout = mActivity.findViewById(R.id.ads_container_native_2);
            rlLoadingAds = mActivity.findViewById(R.id.rl_loading_ad_2);
        }
        if (mActivity != null) {
            AdLoader.Builder builder = new AdLoader.Builder(mActivity, mActivity.getResources().getString(R.string.AdmobNativeAds));
            builder.forNativeAd(nativeAd -> {
                if (mActivity.isDestroyed() || mActivity.isFinishing() || mActivity.isChangingConfigurations()) {
                    nativeAd.destroy();
                    return;
                }
                if (frameLayout == null || rlLoadingAds == null) {
                    nativeAd.destroy();
                    return;
                }
                @SuppressLint("InflateParams") NativeAdView adView =
                        (NativeAdView) mActivity.getLayoutInflater()
                                .inflate(R.layout.ad_admob_native_2, null);
                populateUnifiedNativeAdView2(nativeAd, adView);
                rlLoadingAds.setVisibility(View.GONE);
                frameLayout.removeAllViews();
                frameLayout.addView(adView);
            });
            AdLoader adLoader = builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NotNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);

                }
            }).build();
            adLoader.loadAd(new AdRequest.Builder().build());
        }

    }


    private static void populateUnifiedNativeAdView2(NativeAd nativeAd, NativeAdView adView) {

        adView.setMediaView(adView.findViewById(R.id.ad_media));
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        ((TextView) Objects.requireNonNull(adView.getHeadlineView())).setText(nativeAd.getHeadline());
        Objects.requireNonNull(adView.getMediaView()).setMediaContent(Objects.requireNonNull(nativeAd.getMediaContent()));
        if (nativeAd.getBody() == null) {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            Objects.requireNonNull(adView.getIconView()).setVisibility(View.GONE);
        } else {
            ((ImageView) Objects.requireNonNull(adView.getIconView())).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }
        adView.setNativeAd(nativeAd);

    }


    public static void loadNativeBig1(Activity mActivity, View view) {
        if (mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed()) return;

        RelativeLayout rlLoadingAds;
        FrameLayout frameLayout;

        if (view != null) {
            frameLayout = view.findViewById(R.id.ads_container_native_1);
            rlLoadingAds = view.findViewById(R.id.rl_loading_ad_1);
        } else {
            frameLayout = mActivity.findViewById(R.id.ads_container_native_1);
            rlLoadingAds = mActivity.findViewById(R.id.rl_loading_ad_1);
        }

        if (frameLayout == null || rlLoadingAds == null) return;

        AdLoader.Builder builder = new AdLoader.Builder(mActivity, mActivity.getResources().getString(R.string.AdmobNativeAds));
        builder.forNativeAd(nativeAd -> {
            if (mActivity.isDestroyed() || mActivity.isFinishing() || mActivity.isChangingConfigurations()) {
                nativeAd.destroy();
                return;
            }
            @SuppressLint("InflateParams") NativeAdView adView =
                    (NativeAdView) mActivity.getLayoutInflater()
                            .inflate(R.layout.ad_admob_native_1, null);
            populateUnifiedNativeAdView1(nativeAd, adView);
            rlLoadingAds.setVisibility(View.GONE);
            frameLayout.removeAllViews();
            frameLayout.addView(adView);
        });
        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }
        }).build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    public static void loadNativeBanner(Activity mActivity, View view) {
        if (mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed()) return;

        LinearLayout rlLoadingAds;
        FrameLayout frameLayout;

        if (view != null) {
            frameLayout = view.findViewById(R.id.ads_container_banner);
            rlLoadingAds = view.findViewById(R.id.ll_loading_ad_banner);
        } else {
            frameLayout = mActivity.findViewById(R.id.ads_container_banner);
            rlLoadingAds = mActivity.findViewById(R.id.ll_loading_ad_banner);
        }

        if (frameLayout == null || rlLoadingAds == null) return;

        AdLoader.Builder builder = new AdLoader.Builder(mActivity, mActivity.getResources().getString(R.string.AdmobNativeAds));
        builder.forNativeAd(nativeAd -> {
            if (mActivity.isDestroyed() || mActivity.isFinishing() || mActivity.isChangingConfigurations()) {
                nativeAd.destroy();
                return;
            }
            @SuppressLint("InflateParams") NativeAdView adView =
                    (NativeAdView) mActivity.getLayoutInflater()
                            .inflate(R.layout.ad_admob_banner, null);
            populateUnifiedNativeAdBanner(nativeAd, adView);
            rlLoadingAds.setVisibility(View.GONE);
            frameLayout.removeAllViews();
            frameLayout.addView(adView);
        });
        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }
        }).build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private static void populateUnifiedNativeAdBanner(NativeAd nativeAd, NativeAdView adView) {
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        ((TextView) Objects.requireNonNull(adView.getHeadlineView())).setText(nativeAd.getHeadline());
        if (nativeAd.getBody() == null) {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }
        if (nativeAd.getCallToAction() == null) {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }
        if (nativeAd.getIcon() == null) {
            Objects.requireNonNull(adView.getIconView()).setVisibility(View.GONE);
        } else {
            ((ImageView) Objects.requireNonNull(adView.getIconView())).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }
        adView.setNativeAd(nativeAd);
    }

    private static void populateUnifiedNativeAdView1(NativeAd nativeAd, NativeAdView adView) {

        adView.setMediaView(adView.findViewById(R.id.ad_media));
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        ((TextView) Objects.requireNonNull(adView.getHeadlineView())).setText(nativeAd.getHeadline());
        Objects.requireNonNull(adView.getMediaView()).setMediaContent(Objects.requireNonNull(nativeAd.getMediaContent()));
        if (nativeAd.getBody() == null) {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            Objects.requireNonNull(adView.getIconView()).setVisibility(View.GONE);
        } else {
            ((ImageView) Objects.requireNonNull(adView.getIconView())).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }
        adView.setNativeAd(nativeAd);

    }
}

