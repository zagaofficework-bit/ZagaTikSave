package com.zagavideodown.app.activities;

import android.annotation.SuppressLint;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.zagavideodown.app.BaseActivity;
import com.zagavideodown.app.GlobalConstant;
import com.zagavideodown.app.R;
import com.zagavideodown.app.adapter.ViewFileAdapter;
import com.zagavideodown.app.listener.OnDeleteListener;

import java.io.File;
import java.util.ArrayList;

public class ViewFileActivity extends BaseActivity {
    private ArrayList<String> fileArrayList;
    private int mPosition = 0;
    private TextView textView;
    ViewFileAdapter storyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_file);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getData();
        initViews();
        if (fileArrayList != null) {
            textView.setText(mPosition + 1 + "/" + fileArrayList.size());
        }
        findViewById(R.id.ivBack).setOnClickListener(view -> finish());
    }

    private void getData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fileArrayList = getIntent().getStringArrayListExtra(GlobalConstant.STORY_FILE_DATA);
            mPosition = getIntent().getIntExtra(GlobalConstant.STORY_POSITION, 0);
        }
    }

    private void initViews() {
        textView = findViewById(R.id.tvShowStory);
        ViewPager2 viewPager = findViewById(R.id.viewPager2);
        viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        if (fileArrayList != null) {


            storyAdapter = new ViewFileAdapter(this, fileArrayList, new OnDeleteListener() {
                @Override
                public void onDelete(int position, String filePath) {
                    File fileDelete = new File(filePath);
                    if (fileDelete.exists()) {
                        fileDelete.delete();
                    }

                    MediaScannerConnection.scanFile(ViewFileActivity.this, new String[]{fileDelete.getAbsolutePath()}, null, null);

                    fileArrayList.remove(position);

                    if (fileArrayList.isEmpty()) {
                        finish();
                        return;
                    }

                    if (mPosition >= fileArrayList.size()) {
                        mPosition = fileArrayList.size() - 1;
                    }

                    storyAdapter.notifyItemRemoved(position);
                    storyAdapter.notifyItemRangeChanged(position, fileArrayList.size());
                    textView.setText((mPosition + 1) + "/" + fileArrayList.size());

                }
            });
            viewPager.setAdapter(storyAdapter);
            viewPager.setCurrentItem(mPosition, false);
            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    mPosition = position;
                    textView.setText(position + 1 + "/" + fileArrayList.size());
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    super.onPageScrollStateChanged(state);
                }
            });
        }
    }

}
