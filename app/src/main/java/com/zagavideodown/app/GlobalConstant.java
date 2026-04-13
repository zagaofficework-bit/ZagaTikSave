package com.zagavideodown.app;

import android.os.Environment;

import com.zagavideodown.app.models.Language;
import com.zagavideodown.app.models.TutorialModel;

import java.io.File;
import java.util.ArrayList;

public class GlobalConstant {

    public static String STORY_FILE_DATA = "StoryDataFile";
    public static String STORY_POSITION = "Position";
    public static String myInstagramTempCookies = "";
    public static String GUIDE_SET = "guide_set";
    public static String RootDirectoryHD = "/MediaManager/VideosHD/";
    public static final String TEST_DEVICE_HASHED_ID = "903957CFACA92DD2EAD1287E887E33AA";

    public static boolean showyoutube = false;

    public static String MY_ANDROID_10_IDENTIFIER_OF_FILE = "All_Video_Downloader_";

    public static File RootDirectoryVideoHDSaved = new File(Environment.getExternalStorageDirectory() + "/Download/MediaManager/VideosHD");
    //    public static File RootDirectoryVideoSaved = new File(Environment.getExternalStorageDirectory() + "/Download/TikSave/Video");
    public static File RootDirectoryAudioSaved = new File(Environment.getExternalStorageDirectory() + "/Download/MediaManager/Audio");

    public static String TIKTOK_PACKAGE = "com.ss.android.ugc.trill";

    public static boolean isNonPlayStoreApp = false;

    public static String[] UserAgentsList0 = {"Mozilla/5.0 (Linux; Android 10; SM-A205U) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 10; SM-G981B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.162 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 10; SM-A102U) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Mobile Safari/537.36",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1"
    };


    public static String[] UserAgentsList2 = {"Mozilla/5.0 (Linux; Android 10; SM-A205U) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 11; SAMSUNG SM-A515F) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/15.0 Chrome/90.0.4430.210 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 10; SM-A102U) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Mobile Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 12_2_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36"};

    public static String[] UserAgentsListLogin = {"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36 Edg/106.0.1370.52",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36",
    };


    public static boolean iSAdminAttached = false;
    public static String MY_ANDROID_IDENTIFIER_OF_FILE_DL = "_All_Video_Downloader.";
    public static File RootDirectoryVideoSaved = new File(Environment.getExternalStorageDirectory() + "/Download/VideoDownload/videos/");
    public static File RootDirectoryPhotoSaved = new File(Environment.getExternalStorageDirectory() + "/Download/VideoDownload/images/");

    public static String directoryMediaVideos = "/VideoDownload/videos/";
    public static String directoryMediaImages = "/VideoDownload/images/";
    public static String directoryMediaAudio = "/VideoDownload/audios/";

    public static String myNotificationChannel = "";

    public static String FAMILY_APP = "https://play.google.com/store/apps/developer?id=Movie+Maker+Team";

    public static String EMAIL_FEEDBACK = "asdasd@gmail.com";

    public static String INSTAGRAM_PACKAGE = "com.instagram.android";
    public static String IS_FIRST_TIME_LAUNCH = "first_time_launch";


    public static String LANGUAGE_SET = "language_set";
    public static String LANGUAGE_NAME = "language_name";
    public static String LANGUAGE_KEY = "language_key";
    public static String LANGUAGE_KEY_NUMBER = "language_key_number";

    public static ArrayList<Language> createArrayLanguage() {
        ArrayList<Language> arrayList = new ArrayList<>();
        arrayList.add(new Language("en", "English", R.drawable.flag_en));
        arrayList.add(new Language("vi", "Tiếng Việt", R.drawable.flag_vi));
        arrayList.add(new Language("de", "Deutsch", R.drawable.flag_de));
        arrayList.add(new Language("in", "Indonesia", R.drawable.flag_in));
        arrayList.add(new Language("it", "Italiano", R.drawable.flag_it));
        arrayList.add(new Language("ja", "日本語", R.drawable.flag_ja));
        arrayList.add(new Language("ko", "한국어", R.drawable.flag_ko));
        arrayList.add(new Language("pt", "Português", R.drawable.flag_pt));
        arrayList.add(new Language("ru", "Русский", R.drawable.flag_ru));
        arrayList.add(new Language("th", "แบบไทย", R.drawable.flag_th));
        arrayList.add(new Language("es", "Español", R.drawable.flag_es));
        arrayList.add(new Language("fr", "Francés", R.drawable.flag_fr));
        arrayList.add(new Language("hi", "हिंदी", R.drawable.flag_hi));
        arrayList.add(new Language("ar", "عربي", R.drawable.flag_ar));
        arrayList.add(new Language("cs", "čeština", R.drawable.flag_cs));
        arrayList.add(new Language("pl", "Język polski", R.drawable.flag_pl));
        arrayList.add(new Language("ro", "Română", R.drawable.flag_ro));
        arrayList.add(new Language("sv", "Svenska", R.drawable.flag_sv));
        return arrayList;
    }

    public static ArrayList<TutorialModel> getDataTutorial() {
        ArrayList<TutorialModel> list = new ArrayList<>();
        list.add(new TutorialModel(R.drawable.tutorial_copy_step_1, R.string.tutorial_copy, R.string.tutorial_copy_step_1));
        list.add(new TutorialModel(R.drawable.tutorial_copy_step_2, R.string.tutorial_copy, R.string.tutorial_copy_step_2));
        list.add(new TutorialModel(R.drawable.tutorial_copy_step_3, R.string.tutorial_copy, R.string.tutorial_copy_step_3));
        list.add(new TutorialModel(R.drawable.tutorial_share_step_1, R.string.tutorial_share, R.string.tutorial_share_step_1));
        list.add(new TutorialModel(R.drawable.tutorial_share_step_2, R.string.tutorial_share, R.string.tutorial_share_step_2));
        return list;
    }

}

