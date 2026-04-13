package com.zagavideodown.app.utils;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.MediaScannerConnection;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.zagavideodown.app.GlobalConstant;
import com.zagavideodown.app.R;
import com.zagavideodown.app.activities.MainActivity;
import com.zagavideodown.app.notification.OreoNotification;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static boolean isNonPlayStoreApp = false;
    private static final DecimalFormat format = new DecimalFormat("#.##");
    private static final long MiB = 1024 * 1024;
    private static final long KiB = 1024;
    public static List<String> socialMediaList;

    public static boolean isWaku = false;
    public static int DefaultSoundWaku = R.raw.wakuwaku;
    public static int DefaultSound = R.raw.notification;

    public static String getFileSize(File file) {
        try {
            double length = file.length();
            return getFileSize(length);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    public static String getRedirectUrl(String url) {
        URL urlTmp = null;
        String redUrl = null;
        HttpURLConnection connection = null;

        try {
            urlTmp = new URL(url);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }

        try {
            connection = (HttpURLConnection) urlTmp.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            connection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }

        redUrl = connection.getURL().toString();
        connection.disconnect();

        return redUrl;
    }

    public static ArrayList<String> removeDuplicates(ArrayList<String> arrayList) {
        TreeSet treeSet = new TreeSet((Comparator<String>) (videoModel, videoModel2) -> videoModel.equalsIgnoreCase(videoModel2) ? 0 : 1);
        treeSet.addAll(arrayList);
        return new ArrayList<>(treeSet);
    }
    public static String getFilenameFromURL() {

        return "likee_" + System.currentTimeMillis();

    }

    public static List<String> extractUrlsWithVideo(String text) {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find()) {
            String cc = text.substring(urlMatcher.start(0), urlMatcher.end(0));
            if (cc.contains(".mp4")) {
                containedUrls.add(cc);
            }
        }

        return containedUrls;
    }
    public static String getFileSize(double length) {
        if (length > MiB) {
            return format.format(length / MiB) + " MB";
        }
        if (length > KiB) {
            return format.format(length / KiB) + " KB";
        }
        return format.format(length) + " B";
    }
    public static void createFileFolder() {
        if (!GlobalConstant.RootDirectoryVideoHDSaved.exists()) {
            boolean dir = GlobalConstant.RootDirectoryVideoHDSaved.mkdirs();
        }
        if (!GlobalConstant.RootDirectoryVideoSaved.exists()) {
            boolean dir = GlobalConstant.RootDirectoryVideoSaved.mkdirs();
        }
        if (!GlobalConstant.RootDirectoryAudioSaved.exists()) {
            boolean dir = GlobalConstant.RootDirectoryAudioSaved.mkdirs();
        }
    }
    public static String sanitizeFilename(String title, String ext) {
        Log.i("DownloadFileMain", "Sanitizing filename: " + title + " with ext: " + ext);

        String cutTitle = "";

        cutTitle = GlobalConstant.MY_ANDROID_10_IDENTIFIER_OF_FILE + title;

        String characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";

        // Remove newline characters and carriage returns
        cutTitle = cutTitle.replaceAll("[\\r\\n]", "");
        // Replace invalid characters with an empty string
        cutTitle = cutTitle.replaceAll("[<>:\"/|?*\\\\]|\\s", "");
        // Replace invalid characters with an empty string
        cutTitle = cutTitle.replaceAll("[^\\p{L}\\p{N}]", "-");
        // Remove leading and trailing hyphens
        cutTitle = cutTitle.replaceAll("^-+|-+$", "");
        // Replace spaces with hyphens (if not already handled)
        cutTitle = cutTitle.replace(" ", "-");


        cutTitle = cutTitle.replaceAll(characterFilter, "");
        cutTitle = cutTitle.replaceAll("['+.^:,#\"]", "");
        cutTitle = cutTitle.replaceAll("[<>:\"/|?*]", "");
        cutTitle = cutTitle.replaceAll("[:\\\\/*\"?|<>']", "");
        cutTitle = cutTitle.replace(" ", "-").replace("!", "").replace(":", "") + ext;
        if (cutTitle.length() > 100) {
            cutTitle = cutTitle.substring(0, 100) + ext;
        }
        Log.i("DownloadFileMain", "Sanitized filename: " + cutTitle);

        return cutTitle;

    }

    public static int getRandomNumber(int bound) {
        int gen = new Random().nextInt(bound);
        Log.d("getRandomNumberTAG", "bound = " + bound + " gennumberis = " + gen);
        return gen;
    }

    public static boolean isPermissionGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(
                context,
                permission
        ) == PackageManager.PERMISSION_GRANTED;
    }

    public static List<String> extractUrls(String text) {
        List<String> containedUrls = new ArrayList<>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find()) {
            containedUrls.add(text.substring(urlMatcher.start(0), urlMatcher.end(0)));
        }

        return containedUrls;
    }

    public static void checkPostNotification(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 111111);
            }
        }
    }

    public static boolean isSocialMediaOn(String source) {
        if (source == null) return false;
        source = source.toLowerCase();
        if (source.contains("youtube.com") || source.contains("youtu.be") || 
            source.contains("instagram.com") || source.contains("tiktok.com") || 
            source.contains("facebook.com") || source.contains("fb.watch")) {
            return false;
        }
        return true;
    }

    public static void ShowToast(Context context, String str) {
        try {
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            try {
                Toast.makeText(context, str, Toast.LENGTH_LONG).show();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void ShowToastError(Context context, String str) {
        try {
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            try {
                Toast.makeText(context, str, Toast.LENGTH_LONG).show();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void sendOreoNotification(Context context, String notificationTitle, String notificationBody) {
        try {
            int j = getRandomNumber(5);

            Intent intent;

            intent = new Intent(context, MainActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, j, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
            Uri defaultSound = Uri.parse("android.resource://" + context.getPackageName() + "/" + ((!isWaku) ? DefaultSoundWaku : DefaultSound));
            //Define sound URI
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            OreoNotification oreoNotification = new OreoNotification(context);
            Notification.Builder builder = oreoNotification.getOreoNotification(notificationTitle, notificationBody, pendingIntent,
                    defaultSound, R.drawable.ic_download_yellow);

            int num = getRandomNumber(5);

            oreoNotification.getManager().notify(num, builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String createFilenameWithJapneseAndOthers(String title) {
        try {

            String cleanFileName = title.replaceAll("[\\\\><\"|*?'%:#/]", " ");
            String fileName = cleanFileName.trim().replaceAll(" +", " ");
            fileName = fileName.replaceAll("[\\\\/:*?\"<>|]", "");
            fileName = fileName.replace("\n", " ");
            if (fileName.length() > 127) {
                fileName = fileName.substring(0, 127);
            }
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return "_";

        }
    }

    public static boolean isValidJson(String jsonString) {
        try {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(jsonString);
            return true;
        } catch (JsonSyntaxException | IllegalStateException e) {
            return false;
        }
    }

    public static void moreApp(Context context) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse(GlobalConstant.FAMILY_APP));
        context.startActivity(intent);
    }

    public static void feedbackApp(Activity context) {
        try {
            Intent intent3 = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + GlobalConstant.EMAIL_FEEDBACK));
            intent3.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.str_feed_back_app));
            intent3.putExtra(Intent.EXTRA_TEXT, "");
            context.startActivity(intent3);
        } catch (ActivityNotFoundException ignored) {
        }
    }

    public static void rateApp(Context mContext) {
        try {
            Uri marketUri = Uri.parse("market://details?id=" + mContext.getPackageName());
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, marketUri));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void shareApp(Activity context) {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            Resources res = context.getResources();

            i.putExtra(Intent.EXTRA_SUBJECT, res.getString(R.string.share_app_tittle));
            String appUrl = "https://play.google.com/store/apps/details?id=" + context.getPackageName();
            String shareMessage = res.getString(R.string.share_message) + "\n" + appUrl;
            i.putExtra(Intent.EXTRA_TEXT, shareMessage);
            context.startActivity(Intent.createChooser(i, res.getString(R.string.share_action)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void openTikTok(Context mContext) {
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(GlobalConstant.TIKTOK_PACKAGE);
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } else {
            // TikTok is not installed, redirect to Play Store
            redirectToPlayStore(mContext);
        }
    }
    private static void redirectToPlayStore(Context mContext) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + GlobalConstant.TIKTOK_PACKAGE));
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + GlobalConstant.TIKTOK_PACKAGE));
            mContext.startActivity(intent);
        }
    }
    public static void openAppInstagram(Context mContext) {
        try {
            Utils.openLinkPost(mContext, "https://www.instagram.com/");
        } catch (Exception ignored) {
        }
    }

    public static void openLinkPost(Context mContext, String mediaLink) {
        if (isPackageInstalled(mContext, GlobalConstant.INSTAGRAM_PACKAGE)) {
            try {
                Uri uri = Uri.parse(mediaLink);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage(GlobalConstant.INSTAGRAM_PACKAGE);
                mContext.startActivity(intent);
            } catch (Exception ignored) {

            }
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(mediaLink));
            mContext.startActivity(intent);
        }

    }

    public static boolean isPackageInstalled(Context c, String targetPackage) {
        PackageManager pm = c.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    public static String getStringSizeLengthFile(long j) {
        try {

            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            float f = (float) j;
            if (f < 1048576.0f) {
                return decimalFormat.format(f / 1024.0f) + " Kb";
            } else if (f < 1.07374182E9f) {
                return decimalFormat.format(f / 1048576.0f) + " Mb";
            } else if (f >= 1.09951163E12f) {
                return "";
            } else {
                return decimalFormat.format(f / 1.07374182E9f) + " Gb";
            }
        } catch (Exception e) {
            return "NaN";
        }
    }

    public static boolean checkURL(CharSequence input) {
        if (TextUtils.isEmpty(input)) {
            return false;
        }
        Pattern URL_PATTERN = Patterns.WEB_URL;
        boolean isURL = URL_PATTERN.matcher(input).matches();
        if (!isURL) {
            String urlString = input + "";
            if (URLUtil.isNetworkUrl(urlString)) {
                try {
                    new URL(urlString);
                    isURL = true;
                } catch (Exception ignored) {
                }
            }
        }
        return isURL;
    }

    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager inputManager = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);

            View currentFocusedView = activity.getCurrentFocus();
            if (currentFocusedView != null) {
                inputManager.hideSoftInputFromWindow(
                        currentFocusedView.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS
                );
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    public static long startDownload(String str, String str2, Context context2, String str3) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(str));
        request.setAllowedNetworkTypes(3);
        request.setNotificationVisibility(1);
        StringBuilder sb = new StringBuilder();
        sb.append(str3);
        sb.append("");
        request.setTitle(sb.toString());
        String str4 = Environment.DIRECTORY_DOWNLOADS;
        StringBuilder sb2 = new StringBuilder();
        sb2.append(str2);
        sb2.append(str3);
        request.setDestinationInExternalPublicDir(str4, sb2.toString());
        return ((DownloadManager) context2.getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request);
    }
    public static void createNotificationChannel(Activity context) {
        new OreoNotification(context);
        Log.e("loged112211", "Notification Channel Created!");
    }

    public static String generateInstagramUserAgent() {


        String format_new = String.format("%s Android (%s/%s; 800x1280; %s; %s; %s; %s; %s_%s ;%s)", "Instagram 311.0.0.32.118", Build.VERSION.RELEASE, Build.VERSION.SDK, Build.MODEL, Build.MANUFACTURER, Build.BRAND, Build.DEVICE, Locale.getDefault().getLanguage(), Locale.getDefault().getCountry(), 545986883);

        Log.i("useragent", format_new);
        return format_new;
    }

    public static String getImageFilenameFromURL(String url) {
        try {
            return new File(new URL(url).getPath()).getName();
        } catch (Exception e) {
            e.printStackTrace();
            return System.currentTimeMillis() + ".png";
        }
    }

    public static String getVideoFilenameFromURL(String url) {
        try {
            return new File(new URL(url).getPath()).getName();
        } catch (Exception e) {
            e.printStackTrace();
            return System.currentTimeMillis() + ".mp4";
        }
    }

    public static String[] extractUrlAndFilenameFromJwt(String jwtToken) {
        String[] jwtParts = jwtToken.split("\\."); // Split the token into its three parts
        if (jwtParts.length != 3) {
            return null; // Invalid token format
        }

        String payloadBase64 = jwtParts[1];
        try {
            byte[] decodedBytes = Base64.decode(payloadBase64, Base64.URL_SAFE);
            String decodedPayload = new String(decodedBytes);

            // Parse the JSON payload
            JSONObject payloadJson = new JSONObject(decodedPayload);
            String url = payloadJson.getString("url");
            String filename = payloadJson.getString("filename");

            return new String[]{url, filename};
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Error decoding payload or parsing JSON
        }
    }

    public static void repostImage(Context mContext, String filePath) {
        if (isPackageInstalled(mContext, GlobalConstant.INSTAGRAM_PACKAGE)) {
            try {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, mContext.getResources().getString(R.string.utils_share_txt));
                String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), filePath, "", null);
                Uri screenshotUri = Uri.parse(path);
                intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                intent.setType("image/*");
                intent.setPackage(GlobalConstant.INSTAGRAM_PACKAGE);
                mContext.startActivity(intent);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.app_not_install), Toast.LENGTH_SHORT).show();
        }

    }

    public static void repostVideo(Context mContext, String filePath) {
        if (isPackageInstalled(mContext, GlobalConstant.INSTAGRAM_PACKAGE)) {
            MediaScannerConnection.scanFile(mContext, new String[]{filePath},
                    null, (path, uri) -> {
                        Intent shareIntent = new Intent(
                                Intent.ACTION_SEND);
                        shareIntent.setType("video/*");
                        shareIntent.putExtra(
                                Intent.EXTRA_SUBJECT, mContext.getResources().getString(R.string.app_name));
                        shareIntent.putExtra(
                                Intent.EXTRA_TITLE, mContext.getResources().getString(R.string.app_name));
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        shareIntent.setPackage(GlobalConstant.INSTAGRAM_PACKAGE);
                        mContext.startActivity(shareIntent);

                    });
        } else {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.app_not_install), Toast.LENGTH_SHORT).show();
        }
    }

    public static void shareVideo(Context mContext, String filePath) {
        try {
            MediaScannerConnection.scanFile(mContext, new String[]{filePath},

                    null, (path, uri) -> {
                        Intent shareIntent = new Intent(
                                Intent.ACTION_SEND);
                        shareIntent.setType("video/*");
                        shareIntent.putExtra(
                                Intent.EXTRA_SUBJECT, mContext.getResources().getString(R.string.app_name));
                        shareIntent.putExtra(
                                Intent.EXTRA_TITLE, mContext.getResources().getString(R.string.app_name));
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mContext.startActivity(Intent.createChooser(shareIntent, mContext.getResources().getString(R.string.share_video_using)));

                    });
        } catch (Exception ignored) {

        }

    }

    public static void shareImage(Context context, String filePath) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.utils_share_txt));
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), filePath, "", null);
            Uri screenshotUri = Uri.parse(path);
            intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
            intent.setType("image/*");
            context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.utils_share_image_via)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

