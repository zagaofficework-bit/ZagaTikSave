package com.zagavideodown.app.fragments;

import static com.zagavideodown.app.utils.Utils.ShowToast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.zagavideodown.app.GlobalConstant;
import com.zagavideodown.app.R;
import com.zagavideodown.app.SharedPrefsForInstagram;
import com.zagavideodown.app.activities.GetLinkThroughWebView;
import com.zagavideodown.app.activities.InstagramDownloadCloudBypassWebView;
import com.zagavideodown.app.activities.InstagramLoginActivity;
import com.zagavideodown.app.activities.MainActivity;
import com.zagavideodown.app.activities.SettingsActivity;
import com.zagavideodown.app.ads.NativeAdsAdmob;
import com.zagavideodown.app.databinding.FragmentHomeBinding;
import com.zagavideodown.app.interfaces.DownloadCallback;
import com.zagavideodown.app.models.DisplayResource;
import com.zagavideodown.app.models.EdgeSidecarToChildrenEdge;
import com.zagavideodown.app.models.InstagramPostDataNew;
import com.zagavideodown.app.models.StickyNode;
import com.zagavideodown.app.models.instawithlogin.ModelInstagramPref;
import com.zagavideodown.app.models.storymodels.ModelEdNode;
import com.zagavideodown.app.models.storymodels.ModelGetEdgetoNode;
import com.zagavideodown.app.models.storymodels.ModelInstagramResponse;
import com.zagavideodown.app.other.DownloadVideosMain2;
import com.zagavideodown.app.other.GetLinkThroughWebView2;
import com.zagavideodown.app.tiksave.downloaders.Tiktok;
import com.zagavideodown.app.tiksave.listener.AsyncResponse;
import com.zagavideodown.app.utils.DialogUtils;
import com.zagavideodown.app.utils.DownloadFileMain;
import com.zagavideodown.app.utils.Utils;
import com.zagavideodown.app.webservice.DownloadVideosMain;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class HomeFragment extends Fragment {
    private MainActivity mActivity;
    private DrawerLayout drawerLayout;
    private FragmentHomeBinding binding;

    private ProgressDialog progressDralogGenaratinglink;
    private String myVideoUrlIs = null;
    private String myInstaUsername = "";
    private String myPhotoUrlIs = null;
    public static long DownloadId1;

    public HomeFragment() {
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (DownloadId1 == id) {
                Toast.makeText(context, mActivity.getResources().getString(R.string.DownloadComplete), Toast.LENGTH_SHORT).show();

            }
        }
    };

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void registerDownload() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mActivity.registerReceiver(receiver, intentFilter, Context.RECEIVER_EXPORTED);
        } else {
            mActivity.registerReceiver(receiver, intentFilter);
        }
    }

    public HomeFragment(MainActivity mActivity) {
        this.mActivity = mActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Utils.createNotificationChannel(mActivity);
        initListener();
        setUpProgressDialog();
        getDataFromActivity();
        registerDownload();
        NativeAdsAdmob.loadNativeBig1(mActivity, view);

    }

    public void dismissMyDialogFrag() {
        try {
            if (getActivity() != null && isAdded()) {
                mActivity.runOnUiThread(() -> {
                    if (!mActivity.isFinishing()
                            && progressDralogGenaratinglink != null
                            && progressDralogGenaratinglink.isShowing()) {
                        progressDralogGenaratinglink.dismiss();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDataFromActivity() {
        if (mActivity != null) {
            String strText = mActivity.getMyData();
            if (strText != null && !strText.isEmpty()) {
                mActivity.setMyData("");
                binding.edtUrl.setText(strText);
                String url = binding.edtUrl.getText().toString();
                downloadVideo(url);
            }
        }
    }

    private void setUpProgressDialog() {
        progressDralogGenaratinglink = new ProgressDialog(mActivity);
        progressDralogGenaratinglink.setMessage(mActivity.getResources().getString(R.string.genarating_download_link));
        progressDralogGenaratinglink.setCancelable(false);
    }

    private void initListener() {
        binding.edtUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    binding.ivClear.setVisibility(View.VISIBLE);
                } else if (s.length() == 0) {
                    binding.ivClear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        drawerLayout = mActivity.findViewById(R.id.activity_main_drawer);
        binding.btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        binding.btnSetting.setOnClickListener(v -> startActivity(new Intent(mActivity, SettingsActivity.class)));
        binding.btnInstagram.setOnClickListener(v -> Utils.openAppInstagram(mActivity));
        binding.btnPaste.setOnClickListener(v -> pasteLink());
        binding.btnDownload.setOnClickListener(v -> downloadVideo(binding.edtUrl.getText().toString()));
        binding.ivClear.setOnClickListener(v -> clearEditText());
        binding.btnTiktok.setOnClickListener(view -> Utils.openTikTok(requireContext()));
    }

    private void clearEditText() {
        binding.edtUrl.setText("");
        Utils.hideKeyboard(mActivity);
    }


    private void pasteLink() {
        ClipboardManager clipBoardManager = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData primaryClipData = clipBoardManager.getPrimaryClip();
        CharSequence clip = "";

        if (primaryClipData != null && primaryClipData.getItemCount() > 0) {
            clip = primaryClipData.getItemAt(0).getText();
        }
        binding.edtUrl.setText(Editable.Factory.getInstance().newEditable(clip));
//        downloadVideo(clip.toString());
    }


    public interface RedirectCallback {
        void onResult(@Nullable String redirectedUrl);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.mActivity = (MainActivity) context;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            MainActivity activity = (MainActivity) getActivity();
            String url = activity.getMyData();
            if (url != null && !url.isEmpty()) {
                activity.setMyData("");
                binding.edtUrl.setText(url);
                downloadVideo(url);
            }
        }
    }

    public void downloadVideo(String url1) {
        Utils.hideKeyboard(mActivity);


        progressDralogGenaratinglink.setMessage(mActivity.getString(R.string.genarating_download_link));
        if (TextUtils.isEmpty(url1.trim()) && url1.trim().isEmpty()) {
            ShowToast(mActivity, mActivity.getResources().getString(R.string.enter_valid));
        } else {
            int rand_int1 = Utils.getRandomNumber(2);
            System.out.println("randonvalueis = " + rand_int1);
            String url = url1;
            try {
                url = Utils.extractUrls(url1).get(0);
            } catch (Exception ignored) {
            }
            if (!Utils.checkURL(url.trim())) {
                Utils.ShowToast(mActivity, mActivity.getResources().getString(R.string.enter_valid));
                return;
            }


            if (url.contains("instagram.com")) {
                if (!mActivity.isFinishing()) {
                    if (!GlobalConstant.isNonPlayStoreApp) {
                        Utils.ShowToast(mActivity, mActivity.getResources().getString(R.string.something_webiste_panele_block));
                        return;
                    }
                    if (!Utils.isSocialMediaOn("instagram.com")) {
                        Utils.ShowToast(mActivity, mActivity.getResources().getString(R.string.something_webiste_panele_block));
                        return;
                    }
                    progressDralogGenaratinglink.show();
                    startInstagramDownload(url);
                }
            } else if (url.contains("tik")) {
                if (binding.edtUrl.getText().toString().contains("tik")) {
                    new Tiktok(mActivity, new AsyncResponse() {
                        @Override
                        public void processFinish(long output) {
                            String a = Tiktok.playHD;
                            String fileName = "tiktok" + System.currentTimeMillis() + "_VideoHD" + ".mp4";
                            try {
                                DownloadId1 = Utils.startDownload(a, GlobalConstant.RootDirectoryHD, mActivity, fileName);
                                try {
                                    Toast.makeText(mActivity, mActivity.getResources().getString(R.string.downloadstarted), Toast.LENGTH_SHORT).show();
                                } catch (Exception ignored) {
                                }
                            } catch (Exception ignored) {
                            }
                        }
                    }).execute(binding.edtUrl.getText().toString());

                } else {
                    Toast.makeText(mActivity, R.string.linktiktok, Toast.LENGTH_SHORT).show();
                }
            } else if (url.contains("threads.net")) {
                String myurl = url;
                try {
                    myurl = Utils.extractUrls(myurl).get(0);
                } catch (Exception ignored) {
                }
                dismissMyDialogFrag();
                Intent intent = new Intent(requireActivity(), GetLinkThroughWebView2.class);
                intent.putExtra("myurlis", myurl);
                startActivityForResult(intent, 2);
            } else if (url.contains("myjosh.in")) {
                String myurl = url;
                try {
                    myurl = Utils.extractUrls(myurl).get(0);
                } catch (Exception ignored) {
                }
                DownloadVideosMain2.Start(requireActivity(), myurl.trim(), false);
                Log.e("downloadFileName12", myurl.trim());

            } else if (url.contains("audiomack")) {
                if (!Utils.isSocialMediaOn("audiomack")) {
                    Utils.ShowToast(mActivity, mActivity.getResources().getString(R.string.somthing_webiste_panele_block));
                }
                dismissMyDialogFrag();
                Intent intent = new Intent(requireActivity(), GetLinkThroughWebView2.class);
                intent.putExtra("myurlis", url);
                startActivityForResult(intent, 2);

            } else if (url.contains("ok.ru")) {
                if (!Utils.isSocialMediaOn("ok.ru")) {
                    Utils.ShowToast(mActivity, mActivity.getResources().getString(R.string.somthing_webiste_panele_block));

                }
                dismissMyDialogFrag();
                Intent intent = new Intent(requireActivity(), GetLinkThroughWebView2.class);
                intent.putExtra("myurlis", url);
                startActivityForResult(intent, 2);
            } else if (url.contains("tiki")) {
                if (!Utils.isSocialMediaOn("tiki")) {
                    Utils.ShowToast(mActivity, mActivity.getResources().getString(R.string.somthing_webiste_panele_block));
                }
                dismissMyDialogFrag();
                Intent intent = new Intent(requireActivity(), GetLinkThroughWebView2.class);
                intent.putExtra("myurlis", url);
                startActivityForResult(intent, 2);
            } else if (url.contains("vidlit")) {
                if (!Utils.isSocialMediaOn("vidlit")) {
                    Utils.ShowToast(mActivity, mActivity.getResources().getString(R.string.somthing_webiste_panele_block));
                }
                dismissMyDialogFrag();
                Intent intent = new Intent(requireActivity(), GetLinkThroughWebView2.class);
                intent.putExtra("myurlis", url);
                startActivityForResult(intent, 2);
            } else if (url.contains("byte.co")) {
                if (!Utils.isSocialMediaOn("byte.co")) {
                    Utils.ShowToast(mActivity, mActivity.getResources().getString(R.string.somthing_webiste_panele_block));
                }
                dismissMyDialogFrag();
                Intent intent = new Intent(requireActivity(), GetLinkThroughWebView2.class);
                intent.putExtra("myurlis", url);
                startActivityForResult(intent, 2);
            } else if (url.contains("fthis.gr")) {
                if (!Utils.isSocialMediaOn("fthis.gr")) {
                    Utils.ShowToast(mActivity, mActivity.getResources().getString(R.string.somthing_webiste_panele_block));
                }
                dismissMyDialogFrag();
                Intent intent = new Intent(requireActivity(), GetLinkThroughWebView2.class);
                intent.putExtra("myurlis", url);
                startActivityForResult(intent, 2);
            } else if (url.contains("fw.tv") || url.contains("firework.tv")) {
                if (!Utils.isSocialMediaOn("fw.tv") || !Utils.isSocialMediaOn("firework.tv")) {
                    Utils.ShowToast(mActivity, mActivity.getResources().getString(R.string.somthing_webiste_panele_block));
                }
                dismissMyDialogFrag();
                Intent intent = new Intent(requireActivity(), GetLinkThroughWebView2.class);
                intent.putExtra("myurlis", url);
                startActivityForResult(intent, 2);
            } else if (url.contains("traileraddict")) {
                if (!Utils.isSocialMediaOn("traileraddict")) {
                    Utils.ShowToast(mActivity, mActivity.getResources().getString(R.string.somthing_webiste_panele_block));
                }
                dismissMyDialogFrag();
                Intent intent = new Intent(requireActivity(), GetLinkThroughWebView2.class);
                intent.putExtra("myurlis", url);
                startActivityForResult(intent, 2);
            } else if (url.contains("bemate")) {
                if (!Utils.isSocialMediaOn("bemate")) {
                    Utils.ShowToast(mActivity, mActivity.getResources().getString(R.string.somthing_webiste_panele_block));
                }
                dismissMyDialogFrag();
                String myurl = url;
                try {
                    myurl = Utils.extractUrls(myurl).get(0);
                } catch (Exception ignored) {
                }
                Intent intent = new Intent(requireActivity(), GetLinkThroughWebView2.class);
                intent.putExtra("myurlis", myurl);
                startActivityForResult(intent, 2);
            } else if (url.contains("chingari")) {
                String myurl = url;
                try {
                    myurl = Utils.extractUrls(myurl).get(0);
                } catch (Exception ignored) {
                }
                DownloadVideosMain2.Start(requireActivity(), myurl.trim(), false);


            } else if (url.contains("sck.io") || url.contains("snackvideo")) {
                String myurl = url;
                try {
                    myurl = Utils.extractUrls(myurl).get(0);
                } catch (Exception ignored) {
                }
                DownloadVideosMain2.Start(requireActivity(), myurl.trim(), false);

            } else {
                String myurl = url;
                try {
                    myurl = Utils.extractUrls(myurl).get(0);
                } catch (Exception ignored) {
                }
                Log.i("THANG123456","o day aaaa222");

                DownloadVideosMain2.Start(requireActivity(), myurl.trim(), false);


//                DownloadVideosMain.Start(mActivity, myurl.trim(), false, () -> {

//                });
            }
        }
    }

    private void startInstagramDownload(String url) {
        String Urlwi;
        try {
            URI uri = new URI(url);
            Urlwi = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), null, uri.getFragment()).toString();
        } catch (Exception exception) {
            dismissMyDialogFrag();
            Utils.ShowToast(mActivity, mActivity.getString(R.string.invalid_url));
            return;
        }
        String urlwithoutlettersqp = Urlwi;
        if (urlwithoutlettersqp.contains("/share/")) {
            getRedirectUrl(urlwithoutlettersqp, redirectedUrl -> {
                try {
                    if (redirectedUrl != null) {
                        URI uri = new URI(redirectedUrl);
                        redirectedUrl = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), null, uri.getFragment()).toString();
                    } else {
                        redirectedUrl = urlwithoutlettersqp;
                    }
                    continueInstagramDownload2(redirectedUrl);
                } catch (Exception e) {
                }
            });
        } else {
            continueInstagramDownload2(urlwithoutlettersqp);
        }

    }

    public void getRedirectUrl(String url, RedirectCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            HttpURLConnection connection = null;
            String result = null;
            try {
                URL initialUrl = new URL(url);
                connection = (HttpURLConnection) initialUrl.openConnection();
                connection.setInstanceFollowRedirects(false); // xử lý redirect thủ công
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode >= 300 && responseCode <= 399) {
                    result = connection.getHeaderField("Location");
                } else {
                    result = connection.getURL().toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                String finalResult = result;
                new Handler(Looper.getMainLooper()).post(() -> callback.onResult(finalResult));
            }
        });
    }


    private void continueInstagramDownload2(String myUrl) {
        String urlwithoutlettersqp = myUrl;
        if (urlwithoutlettersqp.contains("/reel/")) {
            urlwithoutlettersqp = urlwithoutlettersqp.replace("/reel/", "/p/");
        }
        if (urlwithoutlettersqp.contains("/tv/")) {
            urlwithoutlettersqp = urlwithoutlettersqp.replace("/tv/", "/p/");
        }

        final String urlNoA = urlwithoutlettersqp;
        final String urlWithQP = urlwithoutlettersqp + "?__a=1&__d=dis";

        try {
            if (urlWithQP.split("/")[4].length() > 15) {
                SharedPrefsForInstagram sharedPrefsFor = new SharedPrefsForInstagram(mActivity);
                if (sharedPrefsFor.getPreference().getPREFERENCE_SESSIONID().isEmpty()) {
                    sharedPrefsFor.clearSharePrefs();
                }
                ModelInstagramPref map = sharedPrefsFor.getPreference();
                if (map != null && "false".equals(map.getPREFERENCE_ISINSTAGRAMLOGEDIN())) {
                    dismissMyDialogFrag();
                    if (!mActivity.isFinishing()) {
                        DialogUtils.showLoginDialog(mActivity, () -> {
                            Intent intent = new Intent(mActivity, InstagramLoginActivity.class);
                            startActivityForResult(intent, 200);
                        });
                    }
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tryDownloadSequentially(urlNoA, urlWithQP);
    }

    private void tryDownloadSequentially(String urlNoA, String urlWithQP) {
        SharedPrefsForInstagram sharedPrefsFor = new SharedPrefsForInstagram(mActivity);
        ModelInstagramPref map = sharedPrefsFor.getPreference();

        String session = (map != null && map.getPREFERENCE_USERID() != null
                && !"oopsDintWork".equals(map.getPREFERENCE_USERID())
                && !map.getPREFERENCE_USERID().isEmpty())
                ? "ds_user_id=" + map.getPREFERENCE_USERID() + "; sessionid=" + map.getPREFERENCE_SESSIONID()
                : null;
        boolean isLoggedIn = map != null
                && "true".equals(map.getPREFERENCE_ISINSTAGRAMLOGEDIN())
                && session != null;


        if (isLoggedIn) {
            // ✅ Nếu đã đăng nhập → chỉ chạy method 0 → 3 → 1
            tryMethod0(urlWithQP, session, new DownloadCallback() {
                @Override
                public void onComplete() {
                    tryMethod6(urlNoA, new DownloadCallback() {
                        @Override
                        public void onComplete() {
                            Log.i("THANGDOWNLOAD", "Phh5");

                            tryMethod5(urlWithQP, new DownloadCallback() {
                                @Override
                                public void onComplete() {
                                    Log.i("THANGDOWNLOAD", "Phh3");
                                    tryMethod3(urlNoA, urlWithQP, session, new DownloadCallback() {
                                        @Override
                                        public void onComplete() {
                                            Log.i("THANGDOWNLOAD", "Phh1");
                                            tryMethod1(urlWithQP, session);
                                        }
                                    });
                                }
                            });
                        }
                    });

                }
            });

        } else {
            tryMethod6(urlNoA, new DownloadCallback() {
                @Override
                public void onComplete() {
                    tryMethod5(urlNoA, new DownloadCallback() {
                        @Override
                        public void onComplete() {

                        }
                    });
                }
            });
        }
    }

    private void tryMethod6(String url, DownloadCallback callback) {

        dismissMyDialogFrag();
        try {
            String finalUrl = url;
            try {
                finalUrl = Utils.extractUrls(url).get(0);
            } catch (Exception ignored) {
            }
            DownloadVideosMain.Start(mActivity, finalUrl.trim(), false, callback);

        } catch (Exception e) {
            e.printStackTrace();
            callback.onComplete();
            Log.i("THANGDOWNLOAD", "KHONG Thanh cong o phuong phap 6");
        }
    }

    private void tryMethod3(String urlNoA, String urlWithQP, String session, DownloadCallback callback) {
        try {
            if (session != null) {
                fetchInstagramDataWithCallback(urlWithQP, session, callback);
                Log.i("THANGDOWNLOAD", "✅ Phương pháp 3 thành công (session)");
            } else {
                dismissMyDialogFrag();
                Intent intent = new Intent(mActivity, GetLinkThroughWebView.class);
                intent.putExtra("myurlis", urlNoA + "?_fb_noscript=1");
                startActivityForResult(intent, 2);
            }
        } catch (Exception e) {

            e.printStackTrace();
            Log.i("THANGDOWNLOAD", "FAIL Phương pháp 3 thành công (session)");

            callback.onComplete();
        }
    }

    private void tryMethod1(String urlWithQP, String session) {
        try {
            fetchInstagramData(urlWithQP, session);
            Log.i("THANGDOWNLOAD", "✅ Phương pháp 1 thành công");

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("THANGDOWNLOAD", "❌ Phương pháp 1 thất bại");

        }
    }

    private void tryMethod2(String urlNoA, String urlWithQP, String session, DownloadCallback callback) {
        try {
            if (session != null) {
                fetchInstagramDataWithCallback(urlWithQP, session, callback);
            } else {
                downloadInstagramImageOrVideoResponseOkhttp(urlNoA, callback);
                Log.i("THANGDOWNLOAD", "✅ Phương pháp 2 thành công (không session)");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("THANGDOWNLOAD", "❌ Phương pháp 2 thất bại");
            callback.onComplete();
        }
    }

    public void fetchInstagramDataWithCallback(String instagramUrl, String cookies, DownloadCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String postData = scrapePost(instagramUrl, cookies);
                InstagramPostDataNew postDataModel = InstagramPostDataNew.Companion.fromJson(postData);
                String ownerUsername = postDataModel.getOwner() != null ? postDataModel.getOwner().getUsername() : "unknown_user";
                myInstaUsername = ownerUsername;

                List<EdgeSidecarToChildrenEdge> edges = postDataModel.getEdgeSidecarToChildren() != null
                        ? postDataModel.getEdgeSidecarToChildren().getEdges()
                        : null;

                boolean success = false;

                if (edges != null && !edges.isEmpty()) {
                    for (EdgeSidecarToChildrenEdge edge : edges) {
                        StickyNode node = edge.getNode();
                        if (node != null) {
                            if (Boolean.TRUE.equals(node.isVideo())) {
                                String videoUrl = node.getVideoURL();
                                if (videoUrl != null) {
                                    downloadMedia(videoUrl, ownerUsername + Utils.getVideoFilenameFromURL(videoUrl), ".mp4");
                                    success = true;
                                }
                            } else {
                                List<DisplayResource> displayResources = node.getDisplayResources();
                                if (displayResources != null && !displayResources.isEmpty()) {
                                    String imageUrl = displayResources.get(displayResources.size() - 1).getSrc();
                                    if (imageUrl != null) {
                                        downloadMedia(imageUrl, ownerUsername + Utils.getImageFilenameFromURL(imageUrl), ".png");
                                        success = true;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (Boolean.TRUE.equals(postDataModel.isVideo())) {
                        String videoUrl = postDataModel.getVideoURL();
                        if (videoUrl != null) {
                            downloadMedia(videoUrl, ownerUsername + "_" + Utils.getVideoFilenameFromURL(videoUrl), ".mp4");
                            success = true;
                        }
                    } else {
                        List<DisplayResource> displayResources = postDataModel.getDisplayResources();
                        if (displayResources != null && !displayResources.isEmpty()) {
                            String imageUrl = displayResources.get(displayResources.size() - 1).getSrc();
                            if (imageUrl != null) {
                                downloadMedia(imageUrl, ownerUsername + "_" + Utils.getImageFilenameFromURL(imageUrl), ".png");
                                success = true;
                            }
                        }
                    }
                }
                Log.i("THANGDOWNLOAD", "T1hanh cong o phuong phap 0 (session)");

                if (!success) {
                    callback.onComplete();
                    throw new Exception("Không có media hợp lệ để tải.");
                }
            } catch (Exception e) {
                e.printStackTrace();
//                showToast(mActivity.getResources().getString(R.string.something));
                callback.onComplete();
            } finally {
                dismissMyDialogFrag();
            }
        });
    }

    // Phương pháp 0
    private void tryMethod0(String urlWithQP, String session, DownloadCallback callback) {
        try {
            if (session != null && !session.trim().isEmpty()) {
                fetchInstagramDataWithCallback(urlWithQP, session, callback);

//                Toast.makeText(mActivity, "✅ Phương pháp 0 thành công (session)", Toast.LENGTH_SHORT).show();
            } else {
                dismissMyDialogFrag();
                Intent intent = new Intent(mActivity, InstagramDownloadCloudBypassWebView.class);
                intent.putExtra("myvidurl", urlWithQP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
                Log.i("THANGDOWNLOAD", "Thanh cong o phuong phap 0");
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.onComplete();
        }
    }

    private void tryMethod5(String url, DownloadCallback callback) {
        try {
            downloadgraminstagramapi(url, callback);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onComplete();
        }
    }


    public void downloadgraminstagramapi(String stringurl, DownloadCallback callback) {
        OkHttpClient client = new OkHttpClient();
        FormBody formBody = new FormBody.Builder().add("url", stringurl).build();
        Request request = new Request.Builder()
                .url("https://api.downloadgram.org/media")
                .post(formBody)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("origin", "https://downloadgram.org")
                .addHeader("referer", "https://downloadgram.org/")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (mActivity != null) {
                    mActivity.runOnUiThread(() -> {
                        dismissMyDialogFrag();
                        Log.i("THANG_LOG", "loi 1");
                    });
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = "";
                try {
                    if (response.body() != null) {
                        responseBody = response.body().string();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final String finalResponseBody = responseBody;

                if (mActivity != null) {
                    mActivity.runOnUiThread(() -> {
                        Set<String> downloadedUrls = new HashSet<>();

                        try {
                            if (response.isSuccessful()) {
                                List<String> listOfUrls = Utils.extractUrls(finalResponseBody);

                                for (String url : listOfUrls) {
                                    if (!TextUtils.isEmpty(url)) {
                                        Uri uri = Uri.parse(url);
                                        String token = uri.getQueryParameter("token");
                                        if (token != null) {
                                            String[] payloadJson = Utils.extractUrlAndFilenameFromJwt(token);
                                            if (payloadJson != null && payloadJson.length == 2) {
                                                String filename = payloadJson[1];
                                                int lastDotIndex = filename.lastIndexOf('.');
                                                String name = (lastDotIndex > 0) ? filename.substring(0, lastDotIndex) : filename;
                                                String extension = (lastDotIndex > 0) ? filename.substring(lastDotIndex) : ".jpg";
                                                String urlpayload = payloadJson[0];
                                                if (!downloadedUrls.contains(urlpayload)) {
                                                    downloadedUrls.add(urlpayload);
                                                    DownloadFileMain.startDownloading(mActivity, urlpayload, name, extension, new DownloadCallback() {
                                                        @Override
                                                        public void onComplete() {

                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }
                                }
                                Log.i("THANGDOWNLOAD", "Thanh cong o phuong phap 5");
                                dismissMyDialogFrag();
                            } else {
                                dismissMyDialogFrag();
                                callback.onComplete();
                                Log.i("THANG_LOG", "loi 2");
//                                ShowToast(mActivity, mActivity.getString(R.string.something));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            callback.onComplete();
                            dismissMyDialogFrag();
                            Log.i("THANG_LOG", e.getMessage().toString());
//                            ShowToast(mActivity, mActivity.getString(R.string.something));
                        }
                    });
                }
            }
        });
    }

    private String scrapePost(String urlOrShortcode, String mycookies) throws Exception {
        final String INSTAGRAM_DOCUMENT_ID = "8845758582119845";
        String shortcode;
        if (urlOrShortcode.contains("http")) {
            String[] parts = urlOrShortcode.split("/p/");
            if (parts.length > 1) {
                shortcode = parts[1].split("/")[0];
            } else {
                throw new Exception("Invalid Instagram URL");
            }
        } else {
            shortcode = urlOrShortcode;
        }
        // Create variables JSON
        JSONObject variables = new JSONObject();
        variables.put("shortcode", shortcode);
        variables.put("fetch_tagged_user_count", JSONObject.NULL);
        variables.put("hoisted_comment_id", JSONObject.NULL);
        variables.put("hoisted_reply_id", JSONObject.NULL);
        String encodedVariables = URLEncoder.encode(variables.toString(), StandardCharsets.UTF_8.toString());
        String body = "variables=" + encodedVariables + "&doc_id=" + INSTAGRAM_DOCUMENT_ID;
        RequestBody requestBody = RequestBody.create(body, MediaType.parse("application/x-www-form-urlencoded"));
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder().url("https://www.instagram.com/graphql/query").post(requestBody).addHeader("Content-Type", "application/x-www-form-urlencoded");
        if (mycookies != null && !mycookies.isEmpty()) {
            builder.addHeader("Cookie", mycookies);
            builder.addHeader("User-Agent", "Instagram 9.5.2 (iPhone7,2; iPhone OS 9_3_3; en_US; en-US; scale=2.00; 750x1334) AppleWebKit/420+");
        }
        Request request = builder.build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("Request failed with status: " + response.code());
            }
            String responseBody = response.body() != null ? response.body().string() : "";
            JSONObject jsonResponse = new JSONObject(responseBody);
            return jsonResponse.getJSONObject("data").getJSONObject("xdt_shortcode_media").toString();
        }
    }

    public void fetchInstagramData(String instagramUrl, String cookies) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Gọi hàm lấy dữ liệu post từ Instagram
                String postData = scrapePost(instagramUrl, cookies);
                InstagramPostDataNew postDataModel = InstagramPostDataNew.Companion.fromJson(postData);
                // Lấy username
                String ownerUsername = postDataModel.getOwner() != null ? postDataModel.getOwner().getUsername() : "unknown_user";
                myInstaUsername = ownerUsername;
                // Lấy danh sách media nếu là carousel
                List<EdgeSidecarToChildrenEdge> edges = postDataModel.getEdgeSidecarToChildren() != null
                        ? postDataModel.getEdgeSidecarToChildren().getEdges()
                        : null;

                if (edges != null && !edges.isEmpty()) {
                    for (EdgeSidecarToChildrenEdge edge : edges) {
                        StickyNode node = edge.getNode();
                        if (node != null) {
                            if (Boolean.TRUE.equals(node.isVideo())) {
                                String videoUrl = node.getVideoURL();
                                if (videoUrl != null) {
                                    downloadMedia(videoUrl, ownerUsername + Utils.getVideoFilenameFromURL(videoUrl), ".mp4");
                                }
                            } else {
                                List<DisplayResource> displayResources = node.getDisplayResources();
                                if (displayResources != null && !displayResources.isEmpty()) {
                                    String imageUrl = displayResources.get(displayResources.size() - 1).getSrc();
                                    if (imageUrl != null) {
                                        downloadMedia(imageUrl, ownerUsername + Utils.getImageFilenameFromURL(imageUrl), ".png");
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Xử lý nếu chỉ có 1 ảnh/video
                    if (Boolean.TRUE.equals(postDataModel.isVideo())) {
                        String videoUrl = postDataModel.getVideoURL();
                        if (videoUrl != null) {
                            downloadMedia(videoUrl, ownerUsername + "_" + Utils.getVideoFilenameFromURL(videoUrl), ".mp4");
                        }
                    } else {
                        List<DisplayResource> displayResources = postDataModel.getDisplayResources();
                        if (displayResources != null && !displayResources.isEmpty()) {
                            String imageUrl = displayResources.get(displayResources.size() - 1).getSrc();
                            if (imageUrl != null) {
                                downloadMedia(imageUrl, ownerUsername + "_" + Utils.getImageFilenameFromURL(imageUrl), ".png");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
//                showToast(mActivity.getResources().getString(R.string.something));
            } finally {
                dismissMyDialogFrag();
            }
        });
    }

    private void downloadMedia(String url, String fileName, String extension) {
        if (mActivity != null) {
            DownloadFileMain.startDownloading(mActivity, url, fileName, extension, new DownloadCallback() {
                @Override
                public void onComplete() {

                }
            });
        }
    }

    private void showToast(String message) {
        if (mActivity != null) {
            mActivity.runOnUiThread(() ->
                    Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void downloadInstagramImageOrVideo_tikinfApi(String url) {
        dismissMyDialogFrag();
        String myurl = url;
        try {
            myurl = Utils.extractUrls(myurl).get(0);
        } catch (Exception ignored) {
        }
        DownloadVideosMain.Start(mActivity, myurl.trim(), false, new DownloadCallback() {
            @Override
            public void onComplete() {

            }
        });
    }

    public void downloadInstagramImageOrVideoResponseOkhttp(String URL, DownloadCallback callback) {
        new Thread(() -> {
            try {
                // CookieJar setup
                ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(mActivity));
                // Logging
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).addInterceptor(logging).build();
                Request request = new Request.Builder().url(URL + "?__a=1&__d=dis").method("GET", null)
                        .header("Cookie", GlobalConstant.myInstagramTempCookies)
                        .header("user-agent", Utils.generateInstagramUserAgent()).build();
                Response response = client.newCall(request).execute();
                String ressd = response.body() != null ? response.body().string() : "";
                int code = response.code();
                if (!ressd.contains("shortcode_media")) {
                    code = 400;
                }
                if (code == 200) {
                    try {
                        Type listType = new TypeToken<ModelInstagramResponse>() {
                        }.getType();
                        ModelInstagramResponse modelInstagramResponse = new GsonBuilder().create().fromJson(ressd, listType);
                        if (modelInstagramResponse != null) {
                            if (modelInstagramResponse.getModelGraphshortcode().getShortcode_media().getEdge_sidecar_to_children() != null) {
                                ModelGetEdgetoNode modelGetEdgetoNode = modelInstagramResponse.getModelGraphshortcode().getShortcode_media().getEdge_sidecar_to_children();
                                List<ModelEdNode> modelEdNodeArrayList = modelGetEdgetoNode.getModelEdNodes();
                                for (ModelEdNode node : modelEdNodeArrayList) {
                                    if (node.getModelNode().isIs_video()) {
                                        String videoUrl = node.getModelNode().getVideo_url();
                                        myVideoUrlIs = videoUrl;
                                        DownloadFileMain.startDownloading(mActivity, videoUrl, myInstaUsername + Utils.getVideoFilenameFromURL(videoUrl), ".mp4", callback);
                                        dismissMyDialogFrag();
                                        myVideoUrlIs = "";
                                    } else {
                                        String imageUrl = node.getModelNode().getDisplay_resources().get(node.getModelNode().getDisplay_resources().size() - 1).getSrc();
                                        myPhotoUrlIs = imageUrl;
                                        DownloadFileMain.startDownloading(mActivity, imageUrl, myInstaUsername + Utils.getImageFilenameFromURL(imageUrl), ".png", callback);
                                        dismissMyDialogFrag();
                                        myPhotoUrlIs = "";
                                    }
                                }
                            } else {
                                boolean isVideo = modelInstagramResponse.getModelGraphshortcode().getShortcode_media().isIs_video();
                                if (isVideo) {
                                    String videoUrl = modelInstagramResponse.getModelGraphshortcode().getShortcode_media().getVideo_url();
                                    myVideoUrlIs = videoUrl;
                                    DownloadFileMain.startDownloading(mActivity, videoUrl, myInstaUsername + Utils.getVideoFilenameFromURL(videoUrl), ".mp4", callback);
                                    dismissMyDialogFrag();
                                    myVideoUrlIs = "";
                                } else {
                                    String imageUrl = modelInstagramResponse.getModelGraphshortcode().getShortcode_media().getDisplay_resources().get(modelInstagramResponse.getModelGraphshortcode().getShortcode_media().getDisplay_resources().size() - 1).getSrc();
                                    myPhotoUrlIs = imageUrl;
                                    DownloadFileMain.startDownloading(mActivity, imageUrl, myInstaUsername + Utils.getImageFilenameFromURL(imageUrl), ".png", callback);
                                    dismissMyDialogFrag();
                                    myPhotoUrlIs = "";
                                }
                            }
                        } else {
                            callback.onComplete();
//                            mActivity.runOnUiThread(() -> Utils.ShowToastError(mActivity, mActivity.getResources().getString(R.string.something)));
                            dismissMyDialogFrag();
                        }

                    } catch (Exception e) {

                        mActivity.runOnUiThread(() -> progressDralogGenaratinglink.setMessage("Method 1 failed trying method 2"));
                        downloadInstagramImageOrVideoResOkhttpM2(URL, callback);
                    }

                } else {

                    mActivity.runOnUiThread(() -> progressDralogGenaratinglink.setMessage("Method 1 failed trying method 2"));
                    downloadInstagramImageOrVideoResOkhttpM2(URL, callback);
                }

            } catch (Throwable e) {
                e.printStackTrace();

                mActivity.runOnUiThread(() -> progressDralogGenaratinglink.setMessage("Method 1 failed trying method 2"));
                downloadInstagramImageOrVideoResOkhttpM2(URL, callback);
            }
        }).start();

    }

    public void downloadInstagramImageOrVideoResOkhttpM2(String URL, DownloadCallback callback) {
        try {
            // Setup CookieJar
            ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(mActivity));
            // Logging
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).addInterceptor(logging).build();
            String cleanUrl = URL.split("\\?")[0] + "embed/captioned/?_fb_noscript=1";
            Request request = new Request.Builder().url(cleanUrl).method("GET", null).build();
            Response response = client.newCall(request).execute();
            String ss = response.body() != null ? response.body().string() : "";
            if (response.code() == 200) {
                try {
                    String doc = ss.substring(ss.indexOf("contextJSON"), ss.indexOf("[]}}}\"")) + "[]}}}";
                    doc = doc.replace("contextJSON\":\"", "");
                    doc = doc.substring(doc.indexOf("video_url"), doc.indexOf("video_view_count"));
                    doc = doc.substring(14, doc.length() - 5);
                    String decodedHtml = StringEscapeUtils.unescapeHtml4(doc);
                    myVideoUrlIs = decodedHtml.replaceAll("\\\\/", "/");
                    myVideoUrlIs = StringEscapeUtils.unescapeJava(myVideoUrlIs);
                    if (myVideoUrlIs != null && !myVideoUrlIs.isEmpty()) {
                        DownloadFileMain.startDownloading(mActivity, myVideoUrlIs, myInstaUsername + Utils.getVideoFilenameFromURL(myVideoUrlIs), ".mp4", callback);
                        dismissMyDialogFrag();
                        myVideoUrlIs = "";
                    } else {
                        callback.onComplete();
//                        mActivity.runOnUiThread(() -> Utils.ShowToastError(mActivity, mActivity.getResources().getString(R.string.something)));
                        dismissMyDialogFrag();
                    }
                } catch (Exception exception) {
                    callback.onComplete();
//                    mActivity.runOnUiThread(() -> progressDralogGenaratinglink.setMessage("Method 2 failed trying method 3"));
                    downloadInstagramImageOrVideo_tikinfApi(URL);
                }
            } else {
                callback.onComplete();
//                mActivity.runOnUiThread(() -> progressDralogGenaratinglink.setMessage("Method 2 failed trying method 3"));
                downloadInstagramImageOrVideo_tikinfApi(URL);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            callback.onComplete();
            System.out.println("The request has failed " + e.getMessage());
            mActivity.runOnUiThread(() -> progressDralogGenaratinglink.setMessage("Method 2 failed trying method 3"));
            downloadInstagramImageOrVideo_tikinfApi(URL);
        }
    }
}

