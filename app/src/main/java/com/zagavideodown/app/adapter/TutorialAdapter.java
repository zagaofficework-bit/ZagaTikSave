package com.zagavideodown.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zagavideodown.app.GlobalConstant;
import com.zagavideodown.app.R;
import com.zagavideodown.app.dialog.TutorialDialog;
import com.zagavideodown.app.models.TutorialModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class TutorialAdapter extends RecyclerView.Adapter<TutorialAdapter.TutorialViewHolder> {
    private final Context mContext;
    private final ArrayList<TutorialModel> arrayList;
    private final TutorialDialog mTutorialDialog;

    public TutorialAdapter(Context mContext, TutorialDialog tutorialDialog) {
        this.mContext = mContext;
        this.arrayList = GlobalConstant.getDataTutorial();
        this.mTutorialDialog = tutorialDialog;
    }


    @NonNull
    @NotNull
    @Override
    public TutorialViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tutorial, parent, false);
        return new TutorialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TutorialViewHolder holder, int position) {
        TutorialModel tutorialModel = arrayList.get(position);

        holder.img.setImageResource(tutorialModel.getResourceId());
        holder.tvTitle.setText(mContext.getString(tutorialModel.getTvTitle()));
        holder.tvDes.setText(mContext.getString(tutorialModel.getTvDes()));


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class TutorialViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvTitle, tvDes;

        public TutorialViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.tutorial_img);
            tvTitle = itemView.findViewById(R.id.tutorial_tv_1);
            tvDes = itemView.findViewById(R.id.tutorial_tv_4_2);
        }
    }
}

