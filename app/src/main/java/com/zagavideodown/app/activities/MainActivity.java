package com.zagavideodown.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.zagavideodown.app.BaseActivity;
import com.zagavideodown.app.R;
import com.zagavideodown.app.adapter.ViewPagerAdapter;
import com.zagavideodown.app.databinding.ActivityMainBinding;
import com.zagavideodown.app.fragments.FilesFragment;
import com.zagavideodown.app.fragments.HomeFragment;
import com.zagavideodown.app.utils.DialogUtils;
import com.zagavideodown.app.utils.Utils;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private String myString;

    private ActivityMainBinding binding;
    private HomeFragment fragment;
    private FilesFragment filesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.viewpager_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0); // chỉ set top
            return insets;
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottom_nav), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, 0, 0, systemBars.bottom); // chỉ padding bottom
            return insets;
        });
        initViews();
        initViewPager();
        initBottomNavigation();
        Utils.checkPostNotification(this);

        DialogUtils.showTutorialFirstTime(this);
//        handleSendText(getIntent());
    }

    private void initViews() {
        binding.navView.setNavigationItemSelectedListener(this);
    }

    private void initBottomNavigation() {
        binding.bottomNav.setOnNavigationItemSelectedListener(item -> {
            int idMenu = item.getItemId();
            if (idMenu == R.id.navigation_home) {
                binding.viewpagerMain.setCurrentItem(0);
            } else if (idMenu == R.id.navigation_files) {
                binding.viewpagerMain.setCurrentItem(1);
            }
            return true;
        });

    }

    private void initViewPager() {
        fragment = new HomeFragment(this);
        filesFragment = new FilesFragment(this);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFrag(fragment, "Home");
        viewPagerAdapter.addFrag(filesFragment, "Files");

        binding.viewpagerMain.setAdapter(viewPagerAdapter);
        binding.viewpagerMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        binding.bottomNav.getMenu().findItem(R.id.navigation_home).setChecked(true);
                        break;
                    case 1:
                        binding.bottomNav.getMenu().findItem(R.id.navigation_files).setChecked(true);
//                        EventBus.getDefault().post(new DataUpdateEvent.downloadFinished());

                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        binding.viewpagerMain.setCurrentItem(0);
        binding.viewpagerMain.setOffscreenPageLimit(3);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int idView = item.getItemId();
        if (idView == R.id.drawer_how_to_use) {
            DialogUtils.showTutorialDialog(this);
        } else if (idView == R.id.drawer_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (idView == R.id.drawer_langugage) {
            DialogUtils.showLanguageDialog(this);
        } else if (idView == R.id.drawer_rate_app) {
            Utils.rateApp(this);
        } else if (idView == R.id.drawer_feed_back) {
            Utils.feedbackApp(this);
        } else if (idView == R.id.drawer_share_app) {
            Utils.shareApp(this);
        } else if (idView == R.id.drawer_family_app) {
            Utils.moreApp(this);
        }
        binding.activityMainDrawer.closeDrawers();
        return false;
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        try {
            Log.e("myhdasbdhf newintent ", intent.getStringExtra(Intent.EXTRA_TEXT) + "_46237478234");

            if (fragment != null) {
                Log.e("myhdasbdhf downlaod ", intent.getStringExtra(Intent.EXTRA_TEXT) + "");

                HomeFragment my = fragment;

                if (my.getView() != null) {
                    EditText editText = my.getView().findViewById(R.id.edt_url);
                    if (editText != null) {
                        editText.setText(String.valueOf(intent.getStringExtra(Intent.EXTRA_TEXT)));
                    }
                }

                my.downloadVideo(String.valueOf(intent.getStringExtra(Intent.EXTRA_TEXT)));
            } else {
                handleSendText(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getMyData() {
        return myString;
    }

    public void setMyData(String mysa) {
        this.myString = mysa;
    }

    private void handleSendText(Intent intent) {
        try {
            this.setIntent(null);
            String url = intent.getStringExtra(Intent.EXTRA_TEXT);
            if ((url == null || url.isEmpty()) && Utils.checkURL(url)) {
                Utils.ShowToast(MainActivity.this, getResources().getString(R.string.enter_valid));
                return;
            }
            if (url != null && url.contains("instagram.com")) {
                Bundle bundle = new Bundle();
                bundle.putString("myinstaurl", url);
                HomeFragment fragobj = new HomeFragment();
                fragobj.setArguments(bundle);
                this.setMyData(url); // tên hàm đúng kiểu camelCase nếu dùng Java

            } else {
                try {
                    url = Utils.extractUrls(url).get(0);
                } catch (Exception ignored) {
                }
                String myurl = url;
                Bundle bundle = new Bundle();
                bundle.putString("myinstaurl", myurl);
                HomeFragment fragobj = new HomeFragment();
                fragobj.setArguments(bundle);

                this.setMyData(myurl);
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
