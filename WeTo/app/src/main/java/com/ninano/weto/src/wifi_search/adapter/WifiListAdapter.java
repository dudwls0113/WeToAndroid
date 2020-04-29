package com.ninano.weto.src.wifi_search.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ninano.weto.R;
import com.ninano.weto.src.wifi_search.models.WifiData;

import java.util.ArrayList;

public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.CustomViewHolder> {

    private ArrayList<WifiData> mArrayList;
    private Context mContext;
    private WifiClickListener mWifiClickListener = null;

    public WifiListAdapter(Context context, ArrayList<WifiData> arrayList, WifiClickListener wifiClickListener){
        mContext = context;
        mArrayList = arrayList;
        mWifiClickListener = wifiClickListener;
    }

    public interface WifiClickListener{
        void wifiClick(int pos);
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_wifi, parent, false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        if (mArrayList.get(position).getStrength()>=100){
            Glide.with(mContext).load(R.drawable.img_wifi_4).into(holder.mImageViewWifi);
        }
        else if(mArrayList.get(position).getStrength()>=75){
            Glide.with(mContext).load(R.drawable.img_wifi_3).into(holder.mImageViewWifi);
        } else if (mArrayList.get(position).getStrength()>=50){
            Glide.with(mContext).load(R.drawable.img_wifi_2).into(holder.mImageViewWifi);
        } else if (mArrayList.get(position).getStrength()>=25){
            Glide.with(mContext).load(R.drawable.img_wifi_1).into(holder.mImageViewWifi);
        } else {
            Glide.with(mContext).load(R.drawable.img_wifi_0).into(holder.mImageViewWifi);
        }

        if (mArrayList.get(position).isConnected()){
            holder.mTextViewIsConnected.setVisibility(View.VISIBLE);
        } else {
            holder.mTextViewIsConnected.setVisibility(View.GONE);
        }

        holder.mTextViewWifiName.setText(mArrayList.get(position).getSsid());
    }

    @Override
    public int getItemCount() {
        return (mArrayList!=null ? mArrayList.size(): 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageViewWifi;
        TextView mTextViewWifiName;
        TextView mTextViewIsConnected;
        View mItem;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageViewWifi = itemView.findViewById(R.id.list_wifi_iv_strength);
            mTextViewWifiName = itemView.findViewById(R.id.list_wifi_tv_name);
            mTextViewIsConnected = itemView.findViewById(R.id.list_wifi_tv_connect);
            mItem = itemView.findViewById(R.id.list_wifi_layout_item);
            mItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mWifiClickListener.wifiClick(getAdapterPosition());
                }
            });
        }
    }
}
