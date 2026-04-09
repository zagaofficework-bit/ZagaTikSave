package com.zagavideodown.app.activities;

import android.os.Bundle;
import android.view.MenuItem;

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
import com.zagavideodown.app.databinding.ActivitySettingsBinding;
import com.zagavideodown.app.utils.DialogUtils;
import com.zagavideodown.app.utils.Utils;

import java.util.Objects;

public class SettingsActivity extends BaseActivity {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initToolbar();
        initData();
        initListener();
    }

    private void initToolbar() {

        setSupportActionBar(binding.toolbarSettings);
        if (getSupportActionBar() != null) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);

        }
    }

    private void initData() {
        binding.tvVersion.setText(BuildConfig.VERSION_NAME);
        binding.tvLanguageHint.setText(SharedPreferenceUtils.getInstance(this).getString(GlobalConstant.LANGUAGE_NAME, "English"));

    }

    private void initListener() {
        binding.clShareApp.setOnClickListener(v -> Utils.shareApp(SettingsActivity.this));
        binding.clRateApp.setOnClickListener(v -> Utils.rateApp(SettingsActivity.this));
        binding.clLanguageOptions.setOnClickListener(v -> DialogUtils.showLanguageDialog(SettingsActivity.this));
        binding.clFeedback.setOnClickListener(v -> Utils.feedbackApp(SettingsActivity.this));
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
