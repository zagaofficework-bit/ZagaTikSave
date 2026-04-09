package com.zagavideodown.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zagavideodown.app.GlobalConstant;
import com.zagavideodown.app.R;
import com.zagavideodown.app.SharedPreferenceUtils;
import com.zagavideodown.app.listener.ItemClickListener;
import com.zagavideodown.app.models.Language;

import java.util.ArrayList;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolder> {
    private final ArrayList<Language> languages;
    private int selectedItem;
    private final Context mContext;
    private final ItemClickListener listener;

    public LanguageAdapter(Context mContext, ItemClickListener mListener) {
        this.mContext = mContext;
        this.languages = GlobalConstant.createArrayLanguage();
        this.selectedItem = SharedPreferenceUtils.getInstance(mContext).getInt(GlobalConstant.LANGUAGE_KEY_NUMBER, 0);
        this.listener = mListener;
    }

    public void setSelectedItem(int position) {
        selectedItem = position;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (languages == null) {
            return 0;
        } else {
            return languages.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvLang;
        ImageView imgChoice;
        ImageView imgFlag;
        LinearLayout rootView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFlag = itemView.findViewById(R.id.img_flag);
            tvLang = itemView.findViewById(R.id.tv_name);
            imgChoice = itemView.findViewById(R.id.iv_selected_state);
            rootView = itemView.findViewById(R.id.rootView);
            itemView.setOnClickListener(this);
        }

        public void bind(int position) {
            Language language = languages.get(position);
            tvLang.setText(language.getNameLanguage());
            Glide.with(mContext).load(language.getImgResource()).into(imgFlag);
            if (selectedItem == position) {
                Glide.with(mContext).load(R.drawable.ic_choice).into(imgChoice);
                rootView.setBackgroundResource(R.drawable.bg_language_selected);
            } else {
                Glide.with(mContext).load(R.drawable.ic_not_choice).into(imgChoice);
                rootView.setBackgroundResource(R.drawable.bg_transparent);

            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                setSelectedItem(position);
                if (listener != null) {
                    listener.onItemSelected(position);
                }
            }
        }
    }
}

