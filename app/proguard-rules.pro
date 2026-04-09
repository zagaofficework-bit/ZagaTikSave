# Zaga Video Downloader - Shrinking Rules

# Ignore warnings to find the real issues
-ignorewarnings

# Basic keep rules for stability
-keepattributes *Annotation*,SourceFile,LineNumberTable,Signature,EnclosingMethod
-dontpreverify

# Gson
-keep class com.google.gson.** { *; }
-keep class com.zagavideodown.app.models.** { *; }
-keep class com.example.tiksave.models.** { *; } # Legacy safety

# OkHttp/Okio
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**

# Jsoup
-keep class org.jsoup.** { *; }

# EventBus
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# WorkManager
-keep class androidx.work.** { *; }

# Android Core Components
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends androidx.preference.Preference
-keep public class * extends androidx.core.app.ComponentActivity

# Native Methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# View Constructors for XML inflation
-keepclassmembers class * extends android.view.View {
   public <init>(android.content.Context);
   public <init>(android.content.Context, android.util.AttributeSet);
   public <init>(android.content.Context, android.util.AttributeSet, int);
}

# DataBinding and ViewBinding
-keep class com.zagavideodown.app.databinding.** { *; }
-keep class androidx.databinding.** { *; }