package com.zagavideodown.app;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zagavideodown.app.GlobalConstant;
import com.zagavideodown.app.LocaleHelper;
import com.zagavideodown.app.SharedPreferenceUtils;


public class BaseActivity extends AppCompatActivity {
    public BaseActivity context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        LocaleHelper.setLocale(context, SharedPreferenceUtils.getInstance(this).getString(GlobalConstant.LANGUAGE_KEY, "en"));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

}

