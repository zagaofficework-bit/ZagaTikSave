package com.zagavideodown.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zagavideodown.app.R;

import java.util.List;

public class OnBoardAdapter extends RecyclerView.Adapter<OnBoardAdapter.ViewHolder> {
    public List<? extends View> list;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_empty, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewGroup viewGroup = holder.viewGroup;
        viewGroup.removeAllViews();
        viewGroup.addView(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewGroup viewGroup;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewGroup = itemView.findViewById(R.id.empty);
        }
    }
}

