package com.zagavideodown.app.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zagavideodown.app.BaseActivity;
import com.zagavideodown.app.GlobalConstant;
import com.zagavideodown.app.R;
import com.zagavideodown.app.SharedPreferenceUtils;
import com.zagavideodown.app.adapter.LanguageAdapter;
import com.zagavideodown.app.models.Language;

import java.util.ArrayList;

public class LanguageActivity extends BaseActivity {
    RecyclerView recyclerView;
    LanguageAdapter adapter;
    int langChoice = 0;
    ArrayList<Language> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_language);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView = findViewById(R.id.rvLanguages);
        arrayList = GlobalConstant.createArrayLanguage();
        adapter = new LanguageAdapter(this, lang -> langChoice = lang);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        findViewById(R.id.llDone).setOnClickListener(v -> {
            SharedPreferenceUtils.getInstance(LanguageActivity.this).setBoolean(GlobalConstant.LANGUAGE_SET, true);
            SharedPreferenceUtils.getInstance(LanguageActivity.this).setString(GlobalConstant.LANGUAGE_NAME, arrayList.get(langChoice).getNameLanguage());
            SharedPreferenceUtils.getInstance(LanguageActivity.this).setString(GlobalConstant.LANGUAGE_KEY, arrayList.get(langChoice).getKeyLanguage());
            SharedPreferenceUtils.getInstance(LanguageActivity.this).setInt(GlobalConstant.LANGUAGE_KEY_NUMBER, langChoice);
            Intent refresh = new Intent(LanguageActivity.this, OnBoardActivity.class);
            refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(refresh);
            finish();
        });
    }
}
