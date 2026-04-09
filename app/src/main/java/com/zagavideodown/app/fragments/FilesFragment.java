package com.zagavideodown.app.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zagavideodown.app.R;
import com.zagavideodown.app.activities.MainActivity;
import com.zagavideodown.app.adapter.ViewPagerAdapter;
import com.zagavideodown.app.databinding.FragmentFilesBinding;

public class FilesFragment extends Fragment {

    private MainActivity mActivity;
    private FragmentFilesBinding binding;


    public FilesFragment() {
    }

    public FilesFragment(MainActivity mActivity) {
        this.mActivity = mActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFilesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFrag(new PhotoFragment(mActivity), mActivity.getString(R.string.str_photo));
        adapter.addFrag(new VideoFragment(mActivity), mActivity.getString(R.string.str_video));
        adapter.addFrag(new VideoHdFragment(), "Tiktok");

        binding.viewpagerDownload.setAdapter(adapter);
        binding.tabs.setupWithViewPager(binding.viewpagerDownload);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.mActivity = (MainActivity) context;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

