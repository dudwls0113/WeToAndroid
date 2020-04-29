package com.ninano.weto.src.todo_add.adpater;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ninano.weto.R;
import com.ninano.weto.src.todo_add.models.LikeLocationData;

import java.util.ArrayList;

public class LIkeLocationListAdapter extends RecyclerView.Adapter<LIkeLocationListAdapter.CustomViewHolder> {

    private Context mContext;
    private ArrayList<LikeLocationData> mDataArrayList;
    private ItemClickListener mItemClickListener = null;

    public LIkeLocationListAdapter(Context context, ArrayList<LikeLocationData> arrayList, ItemClickListener itemClickListener){
        mContext = context;
        mDataArrayList = arrayList;
        mItemClickListener = itemClickListener;
    }

    public interface ItemClickListener{
        void itemClick(int pos);
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_like_location, parent, false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        if (mDataArrayList.get(position).isSelected()){
            holder.mTextViewName.setBackgroundResource(R.drawable.bg_round_button_on);
            holder.mTextViewName.setTextColor(Color.parseColor("#ffffff"));
        } else {
            holder.mTextViewName.setBackgroundResource(R.drawable.bg_round_button_off);
            holder.mTextViewName.setTextColor(Color.parseColor("#657884"));
        }

        holder.mTextViewName.setText(mDataArrayList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return (mDataArrayList!=null ? mDataArrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView mTextViewName;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewName = itemView.findViewById(R.id.list_like_location_tv_name);
            mTextViewName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.itemClick(getAdapterPosition());
                }
            });
        }
    }
}
