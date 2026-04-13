# Zaga Video Downloader - ProGuard/R8 Rules

# ─── Attributes (must keep for crash stack traces & reflection) ───────────────
-keepattributes *Annotation*,SourceFile,LineNumberTable,Signature,EnclosingMethod,
                InnerClasses,Exceptions

# ─── Kotlin (required — Kotlin uses reflection internally) ───────────────────
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-keep class kotlinx.** { *; }
-dontwarn kotlin.**
-dontwarn kotlinx.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# ─── YoutubeDL-Android & FFmpeg (JNI + reflection — CRITICAL for release) ────
-keep class com.yausername.** { *; }
-keep class com.yausername.youtubedl_android.** { *; }
-keep class com.yausername.ffmpeg.** { *; }
-keep class com.yausername.aria2c.** { *; }
-dontwarn com.yausername.**

# ─── LowCostVideo (htetznaing) ───────────────────────────────────────────────
-keep class com.libs.htetznaing.** { *; }
-dontwarn com.libs.htetznaing.**

# ─── Jackson (used with Kotlin data classes via reflection) ──────────────────
-keep class com.fasterxml.jackson.** { *; }
-keep class com.fasterxml.jackson.databind.** { *; }
-keepclassmembers class * {
    @com.fasterxml.jackson.annotation.* *;
}
-dontwarn com.fasterxml.jackson.**

# ─── Gson ────────────────────────────────────────────────────────────────────
-keep class com.google.gson.** { *; }
-keep class com.zagavideodown.app.models.** { *; }
-keep class com.example.zagasave.models.** { *; }
# Gson uses generic type information stored in a class file when working with
# fields. Proguard removes such information by default.
-keepattributes Signature
-keepattributes EnclosingMethod

# ─── App Model Classes (used with Gson/Jackson deserialization) ───────────────
-keep class com.zagavideodown.app.models.** { *; }
-keepclassmembers class com.zagavideodown.app.models.** {
    <init>(...);
    <fields>;
    <methods>;
}

# ─── OkHttp / Okio ───────────────────────────────────────────────────────────
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-dontwarn okhttp3.internal.platform.**

# ─── Retrofit / Converter-Gson ───────────────────────────────────────────────
-keep class retrofit2.** { *; }
-keepclassmembernames interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**

# ─── Android Networking (Fast Android Networking) ────────────────────────────
-keep class com.androidnetworking.** { *; }
-dontwarn com.androidnetworking.**

# ─── Persistent CookieJar ────────────────────────────────────────────────────
-keep class com.franmontiel.persistentcookiejar.** { *; }
-dontwarn com.franmontiel.**

# ─── RxJava / RxAndroid ──────────────────────────────────────────────────────
-keep class io.reactivex.rxjava3.** { *; }
-dontwarn io.reactivex.**

# ─── Jsoup ───────────────────────────────────────────────────────────────────
-keep class org.jsoup.** { *; }
-dontwarn com.google.re2j.**

# ─── Apache Commons ──────────────────────────────────────────────────────────
-keep class org.apache.commons.** { *; }
-dontwarn com.github.luben.zstd.**
-dontwarn org.tukaani.xz.**
-dontwarn org.apache.commons.**

# ─── EventBus ────────────────────────────────────────────────────────────────
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

# ─── Glide ───────────────────────────────────────────────────────────────────
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn com.bumptech.glide.**

# ─── Google Ads (AdMob) ──────────────────────────────────────────────────────
-keep class com.google.android.gms.ads.** { *; }
-dontwarn com.google.android.gms.**

# ─── WorkManager ─────────────────────────────────────────────────────────────
-keep class androidx.work.** { *; }
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context,androidx.work.WorkerParameters);
}

# ─── Android Core Components ─────────────────────────────────────────────────
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends androidx.preference.Preference
-keep public class * extends androidx.core.app.ComponentActivity

# ─── Native Methods (JNI) ────────────────────────────────────────────────────
-keepclasseswithmembernames class * {
    native <methods>;
}

# ─── View Constructors (required for XML inflation) ──────────────────────────
-keepclassmembers class * extends android.view.View {
   public <init>(android.content.Context);
   public <init>(android.content.Context, android.util.AttributeSet);
   public <init>(android.content.Context, android.util.AttributeSet, int);
}

# ─── DataBinding / ViewBinding ───────────────────────────────────────────────
-keep class com.zagavideodown.app.databinding.** { *; }
-keep class androidx.databinding.** { *; }

# ─── Enums (often broken by R8 when accessed via name()/valueOf()) ────────────
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ─── Parcelable ──────────────────────────────────────────────────────────────
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ─── Serializable ────────────────────────────────────────────────────────────
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ─── @Keep annotation (explicit opt-in for reflection-heavy code) ─────────────
-keep @androidx.annotation.Keep class * { *; }
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}

# ─── Animation Library (Local implementation using reflection) ────────────────
-keep class com.zagavideodown.app.animation.** { *; }
-keep class com.zagavideodown.app.animation.effect.** { *; }