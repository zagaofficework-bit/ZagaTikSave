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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.zagavideodown.app.GlobalConstant;
import com.zagavideodown.app.R;
import com.zagavideodown.app.adapter.VideoAdapter;
import com.zagavideodown.app.views.EmptyRecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class VideoHdFragment extends Fragment {

    private Activity mActivity;
    private ArrayList<File> mFiles;
    private EmptyRecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private VideoAdapter adapter;

    public VideoHdFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_recycler_download, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewSave);
        swipeRefreshLayout = view.findViewById(R.id.swiperefreshstory);

        // Setup RecyclerView
        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(getActivity(), 2, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setEmptyView(view.findViewById(R.id.list_empty));

        // Init list + adapter
        mFiles = new ArrayList<>();
        adapter = new VideoAdapter(mActivity, mFiles);
        recyclerView.setAdapter(adapter);

        // Load data lần đầu
        getAllFiles();

        // Swipe refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            getAllFiles();
            swipeRefreshLayout.setRefreshing(false);
        });

        return view;
    }

    /**
     * Load toàn bộ video trong thư mục
     * Sắp xếp: video mới download → hiển thị trước
     */
    private void getAllFiles() {
        mFiles.clear();

        File rootDir = GlobalConstant.RootDirectoryVideoHDSaved;
        if (rootDir == null || !rootDir.exists()) {
            adapter.notifyDataSetChanged();
            return;
        }

        File[] files = rootDir.listFiles();
        if (files == null || files.length == 0) {
            adapter.notifyDataSetChanged();
            return;
        }

        Collections.addAll(mFiles, files);

        // SORT: file mới nhất lên đầu
        Collections.sort(mFiles, (file1, file2) ->
                Long.compare(file2.lastModified(), file1.lastModified())
        );

        adapter.notifyDataSetChanged();
    }
}
