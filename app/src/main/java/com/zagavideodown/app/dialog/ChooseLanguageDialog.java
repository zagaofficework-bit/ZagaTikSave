package com.zagavideodown.app.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zagavideodown.app.GlobalConstant;
import com.zagavideodown.app.R;
import com.zagavideodown.app.SharedPreferenceUtils;
import com.zagavideodown.app.activities.SplashActivity;
import com.zagavideodown.app.adapter.LanguageDialogAdapter;
import com.zagavideodown.app.models.Language;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class ChooseLanguageDialog extends BottomSheetDialogFragment {
    RecyclerView recyclerView;
    LanguageDialogAdapter adapter;
    Activity mContext;
    int langChoice;

    public ChooseLanguageDialog(Activity mContext) {
        this.mContext = mContext;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_language, container, false);
        langChoice = SharedPreferenceUtils.getInstance(mContext).getInt(GlobalConstant.LANGUAGE_KEY_NUMBER, 0);
        recyclerView = view.findViewById(R.id.rcv_list);
        final ArrayList<Language> arrayList = GlobalConstant.createArrayLanguage();
        adapter = new LanguageDialogAdapter(getContext(), lang -> langChoice = lang);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        view.findViewById(R.id.tv_ok).setOnClickListener(view1 -> {
            SharedPreferenceUtils.getInstance(mContext).setString(GlobalConstant.LANGUAGE_NAME, arrayList.get(langChoice).getNameLanguage());
            SharedPreferenceUtils.getInstance(mContext).setString(GlobalConstant.LANGUAGE_KEY, arrayList.get(langChoice).getKeyLanguage());

            SharedPreferenceUtils.getInstance(mContext).setInt(GlobalConstant.LANGUAGE_KEY_NUMBER, langChoice);
            dismiss();

            mContext.startActivity(new Intent(mContext, SplashActivity.class));
            mContext.finish();
        });
        return view;
    }
}

