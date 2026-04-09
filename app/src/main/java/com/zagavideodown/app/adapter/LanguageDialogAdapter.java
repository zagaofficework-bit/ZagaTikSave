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
import com.zagavideodown.app.SharedPreferenceUtils;
import com.zagavideodown.app.listener.ItemClickListener;
import com.zagavideodown.app.models.Language;

import java.util.ArrayList;

public class LanguageDialogAdapter extends RecyclerView.Adapter<LanguageDialogAdapter.ViewHolder> {
    private final ArrayList<Language> languages;
    private final ItemClickListener mListener;
    int lastPost;

    public LanguageDialogAdapter(Context mContext, ItemClickListener mListener) {
        this.languages = GlobalConstant.createArrayLanguage();
        this.mListener = mListener;
        this.lastPost = SharedPreferenceUtils.getInstance(mContext).getInt(GlobalConstant.LANGUAGE_KEY_NUMBER, 0);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language_dialog, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Language language = languages.get(position);
        holder.imgFlag.setImageResource(language.getImgResource());
        holder.tvLang.setText(language.getNameLanguage());
        if (lastPost == position) {
            holder.imgChoice.setImageResource(R.drawable.ic_choice);
        } else {
            holder.imgChoice.setImageResource(R.drawable.ic_not_choice);
        }
        holder.itemView.setOnClickListener(view -> {
            notifyItemChanged(lastPost);
            notifyItemChanged(holder.getAdapterPosition());
            lastPost = holder.getAdapterPosition();
            if (mListener != null) {
                mListener.onItemSelected(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (languages == null) {
            return 0;
        } else {
            return languages.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLang;
        ImageView imgChoice;
        ImageView imgFlag;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFlag = itemView.findViewById(R.id.img_flag);
            tvLang = itemView.findViewById(R.id.tv_name);
            imgChoice = itemView.findViewById(R.id.iv_selected_state);
        }
    }
}

