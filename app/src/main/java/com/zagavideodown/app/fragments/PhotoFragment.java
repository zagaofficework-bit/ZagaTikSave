package com.zagavideodown.app.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zagavideodown.app.DataUpdateEvent;
import com.zagavideodown.app.GlobalConstant;
import com.zagavideodown.app.activities.MainActivity;
import com.zagavideodown.app.activities.ViewFileActivity;
import com.zagavideodown.app.adapter.FileSaveAdapter;
import com.zagavideodown.app.databinding.FragmentRecyclerBinding;
import com.zagavideodown.app.interfaces.FileClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class PhotoFragment extends Fragment implements FileClickListener {
    private MainActivity mActivity;
    private FragmentRecyclerBinding binding;
    private ArrayList<File> fileArrayList;
    private FileSaveAdapter fileListAdapter;
    int mPosition;

    public PhotoFragment() {
    }

    public PhotoFragment(MainActivity mActivity) {
        this.mActivity = mActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRecyclerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        getAllFiles();
    }

    private void initViews() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3, RecyclerView.VERTICAL, false);
        binding.recyclerSave.setLayoutManager(gridLayoutManager);
        binding.recyclerSave.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerSave.setEmptyView(binding.listEmpty);
        fileArrayList = new ArrayList<>();
        fileListAdapter = new FileSaveAdapter(mActivity, fileArrayList, this);
        binding.recyclerSave.setAdapter(fileListAdapter);
//        binding.swipeRefresh.setOnRefreshListener(() -> {
//            getAllFiles();
//            binding.swipeRefresh.setRefreshing(false);
//        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllFiles();
    }

    private void getAllFiles() {
        fileArrayList.clear();
        File[] files = GlobalConstant.RootDirectoryPhotoSaved.listFiles();
        if (files != null && files.length > 0) {
            Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
            Collections.addAll(fileArrayList, files);
            fileListAdapter.notifyDataSetChanged();

        }
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


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void getPosition(int position, File file) {
        mPosition = position;
        ArrayList<String> filePathArray = new ArrayList<>();
        for (int i = 0; i < fileArrayList.size(); i++) {
            filePathArray.add(fileArrayList.get(i).getPath());
        }
        Intent intent = new Intent(mActivity, ViewFileActivity.class);
        intent.putExtra(GlobalConstant.STORY_FILE_DATA, filePathArray);
        intent.putExtra(GlobalConstant.STORY_POSITION, mPosition);
        startActivity(intent);
//        mActivity.startActivityForResult(intent, GlobalConstant.REQUEST_VIEW_POST);
    }

    @Subscribe
    public void updateFav(DataUpdateEvent.downloadFinished updateFav) {
        getAllFiles();
        Log.e("THANGDOWNLOADSUCESS", "phoot");
    }
}

