package com.zagavideodown.app.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.zagavideodown.app.BaseActivity;
import com.zagavideodown.app.GlobalConstant;
import com.zagavideodown.app.R;
import com.zagavideodown.app.SharedPreferenceUtils;
import com.zagavideodown.app.adapter.OnBoardAdapter;

import java.util.ArrayList;

public class OnBoardActivity extends BaseActivity {
    public AnimationSet animationSetTitle;
    public AnimationSet animationSetDes;
    public ConstraintLayout btnNext;
    public AppCompatTextView tvNext;
    ViewPager2 viewPager2;
    public final ArrayList<View> listView = new ArrayList<>();
    public View viewGuide1;
    public View viewGuide2;
    public View viewGuide3;
    public View indicator1;
    public View indicator2;
    public View indicator3;
    public int positionOld;

    public AppCompatTextView tvTitle1;
    public AppCompatTextView tvDes1;
    public AppCompatTextView tvTitle2;
    public AppCompatTextView tvDes2;
    public AppCompatTextView tvTitle3;
    public AppCompatTextView tvDes3;

    public static final class ViewPagerChange extends ViewPager2.OnPageChangeCallback {
        public final OnBoardActivity activity;

        public ViewPagerChange(OnBoardActivity activity) {
            this.activity = activity;
        }

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            activity.animationIndicator(position);
            if (position > activity.positionOld) {
                activity.animationTv(position);
            }
            activity.positionOld = position;
            if (position == 2) {
                AppCompatTextView appCompatTextView = activity.tvNext;
                if (appCompatTextView != null) {
                    appCompatTextView.setText(R.string.str_action_start);
                }
            } else {
                AppCompatTextView appCompatTextView2 = activity.tvNext;
                if (appCompatTextView2 != null) {
                    appCompatTextView2.setText(R.string.str_next);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_on_board);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
        initViewPager();
    }

    private void initViewPager() {
        if (viewPager2 != null) {
            OnBoardAdapter aVar = new OnBoardAdapter();
            aVar.list = this.listView;
            viewPager2.setAdapter(aVar);
            viewPager2.setCurrentItem(0);
            viewPager2.registerOnPageChangeCallback(new ViewPagerChange(this));
        }
    }

    private void initView() {
        viewPager2 = findViewById(R.id.guide_vp);
        this.btnNext = findViewById(R.id.next_layout);
        this.tvNext = findViewById(R.id.next_start_tv);
        LayoutInflater from = LayoutInflater.from(this);

        this.viewGuide1 = from.inflate(R.layout.layout_on_board_1, null);
        this.viewGuide2 = from.inflate(R.layout.layout_on_board_2, null);
        this.viewGuide3 = from.inflate(R.layout.layout_on_board_3, null);
        this.indicator1 = findViewById(R.id.v_indicator1);
        this.indicator2 = findViewById(R.id.v_indicator2);
        this.indicator3 = findViewById(R.id.v_indicator3);
        View view = this.viewGuide1;
        if (view != null) {
            this.listView.add(view);
            this.tvTitle1 = view.findViewById(R.id.title_tv);
            this.tvDes1 = view.findViewById(R.id.subtitle_tv);
        }
        View view3 = this.viewGuide2;
        if (view3 != null) {
            listView.add(view3);
            this.tvTitle2 = view3.findViewById(R.id.title_tv2);
            this.tvDes2 = view3.findViewById(R.id.subtitle_tv2);
        }
        View view4 = this.viewGuide3;
        if (view4 != null) {
            listView.add(view4);
            this.tvTitle3 = view4.findViewById(R.id.title_tv3);
            this.tvDes3 = view4.findViewById(R.id.subtitle_tv3);
        }
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (positionOld == 0) {
                    viewPager2.setCurrentItem(1);
                } else if (positionOld == 1) {
                    viewPager2.setCurrentItem(2);
                } else {
                    SharedPreferenceUtils.getInstance(OnBoardActivity.this).setBoolean(GlobalConstant.GUIDE_SET, true);
                    startActivity(new Intent(OnBoardActivity.this, MainActivity.class));
                    finish();

                }
            }
        });
    }

    public final void animationTv(int position) {
        if (position == 0) {
            tvTitleAnimation(this.tvTitle1);
            tvDesAnimation(this.tvDes1);
        } else if (position == 1) {
            tvTitleAnimation(this.tvTitle2);
            tvDesAnimation(this.tvDes2);
        } else if (position == 2) {
            tvTitleAnimation(this.tvTitle3);
            tvDesAnimation(this.tvDes3);
        }
    }

    public final void animationIndicator(int position) {
        if (position == 0) {
            changeIndicator(this.indicator1, true);
            changeIndicator(this.indicator2, false);
            changeIndicator(this.indicator3, false);
        } else if (position == 1) {
            changeIndicator(this.indicator1, false);
            changeIndicator(this.indicator2, true);
            changeIndicator(this.indicator3, false);
        } else if (position == 2) {
            changeIndicator(this.indicator1, false);
            changeIndicator(this.indicator2, false);
            changeIndicator(this.indicator3, true);
        }
    }

    public final void tvDesAnimation(AppCompatTextView tvDes) {
        if (this.animationSetDes == null) {
            float dimensionPixelSize = (float) getResources().getDimensionPixelSize(R.dimen.dp_75);
//            if (this.H) {
//                dimensionPixelSize = -dimensionPixelSize;
//            }
            AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 1.0f);
            TranslateAnimation translateAnimation = new TranslateAnimation(dimensionPixelSize, 0.0f, 0.0f, 0.0f);
            AnimationSet animationSet = new AnimationSet(true);
            this.animationSetDes = animationSet;
            animationSet.setDuration(350);
            AnimationSet animationSet2 = this.animationSetDes;
            if (animationSet2 != null) {
                animationSet2.addAnimation(alphaAnimation);
            }
            AnimationSet animationSet3 = this.animationSetDes;
            if (animationSet3 != null) {
                animationSet3.addAnimation(translateAnimation);
            }
            AnimationSet animationSet4 = this.animationSetDes;
            if (animationSet4 != null) {
                animationSet4.setStartOffset(50);
            }
        }
        if (tvDes != null) {
            tvDes.startAnimation(this.animationSetDes);
        }
    }

    public final void tvTitleAnimation(AppCompatTextView tvTitle) {
        if (animationSetTitle == null) {
            float dimensionPixelSize = (float) getResources().getDimensionPixelSize(R.dimen.dp_60);
            AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 1.0f);
            TranslateAnimation translateAnimation = new TranslateAnimation(dimensionPixelSize, 0.0f, 0.0f, 0.0f);
            AnimationSet animationSet = new AnimationSet(true);
            animationSetTitle = animationSet;
            animationSet.setDuration(350);
            AnimationSet animationSet2 = this.animationSetTitle;
            if (animationSet2 != null) {
                animationSet2.addAnimation(alphaAnimation);
            }
            AnimationSet animationSet3 = this.animationSetTitle;
            if (animationSet3 != null) {
                animationSet3.addAnimation(translateAnimation);
            }
        }
        if (tvTitle != null) {
            tvTitle.startAnimation(this.animationSetTitle);
        }
    }

    public final void changeIndicator(View view, boolean selected) {
        int idResource;
        int idDimen;
        if (view != null) {
            Resources resources = getResources();
            if (selected) {
                idResource = R.drawable.shape_red_radius_46;
            } else {
                idResource = R.drawable.shape_gray_radius_46;
            }
            view.setBackground(ResourcesCompat.getDrawable(resources, idResource, null));
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams != null) {
                Resources resources2 = getResources();
                if (selected) {
                    idDimen = R.dimen.dp_44;
                } else {
                    idDimen = R.dimen.dp_16;
                }
                layoutParams.width = resources2.getDimensionPixelSize(idDimen);
            }
            view.setLayoutParams(layoutParams);
        }
    }
}
