package com.zagavideodown.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.zagavideodown.app.R;
import com.zagavideodown.app.adapter.TutorialAdapter;

import me.relex.circleindicator.CircleIndicator3;

public class TutorialDialog extends Dialog {
    public TutorialDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_tutorial);
        ImageView btnCloseDialog = findViewById(R.id.close_dialog);
        btnCloseDialog.setOnClickListener(v -> dismiss());
        ViewPager2 viewPager = findViewById(R.id.view_page);
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        TutorialAdapter adapter = new TutorialAdapter(context, this);
        viewPager.setAdapter(adapter);
        CircleIndicator3 indicator3 = findViewById(R.id.dots);
        indicator3.setViewPager(viewPager);
    }
}

