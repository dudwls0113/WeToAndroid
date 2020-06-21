package com.ninano.weto.src.todo_add.adpater;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ninano.weto.R;
import com.ninano.weto.src.todo_add.models.AddGroupToDoMemberData;

import java.util.ArrayList;

public class AddGroupToDoMemberAdapter extends RecyclerView.Adapter<AddGroupToDoMemberAdapter.CustomViewHolder> {

    private Context mContext;
    private ArrayList<AddGroupToDoMemberData> mData;
    private ItemClickListener mItemClickListener;
    private float mDensity;

    public AddGroupToDoMemberAdapter(Context context, ArrayList<AddGroupToDoMemberData> arrayList, float density, ItemClickListener itemClickListener){
        mContext = context;
        mData = arrayList;
        mDensity = density;
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
//        if (position==0){
//            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.mItem.getLayoutParams();
//            params.leftMargin = (int)(24*mDensity);
//            holder.mItem.setLayoutParams(params);
//        } else{
//            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.mItem.getLayoutParams();
//            params.leftMargin = 0;
//            holder.mItem.setLayoutParams(params);
//        }
//
//        if (position==mData.size()-1){
//            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.mItem.getLayoutParams();
//            params.rightMargin = (int)(24*mDensity);
//            holder.mItem.setLayoutParams(params);
//        } else{
//            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.mItem.getLayoutParams();
//            params.rightMargin = (int)(12*mDensity);
//            holder.mItem.setLayoutParams(params);
//        }

        if (mData.get(position).isSelected()){
            holder.mTextViewName.setBackgroundResource(R.drawable.bg_round_button_on);
            holder.mTextViewName.setTextColor(Color.parseColor("#ffffff"));
        } else {
            holder.mTextViewName.setBackgroundResource(R.drawable.bg_round_button_off);
            holder.mTextViewName.setTextColor(Color.parseColor("#657884"));
        }

        holder.mTextViewName.setText(mData.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return (mData!=null ? mData.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mItem;
        TextView mTextViewName;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            mItem = itemView.findViewById(R.id.list_like_location_item);
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
