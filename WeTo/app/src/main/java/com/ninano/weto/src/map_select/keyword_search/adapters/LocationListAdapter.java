package com.ninano.weto.src.map_select.keyword_search.adapters;

import android.annotation.SuppressLint;
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
import com.ninano.weto.src.map_select.keyword_search.models.LocationResponse;

import java.util.ArrayList;

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.CustomViewHolder> {

    private Context mContext;
    private ArrayList<LocationResponse.Location> mDataArrayList;
    private ItemClickListener mItemClickListener = null;

    public LocationListAdapter(Context context, ArrayList<LocationResponse.Location> arrayList, ItemClickListener itemClickListener) {
        mContext = context;
        mDataArrayList = arrayList;
        mItemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void itemClick(int pos);
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_location, parent, false);
        return new CustomViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        holder.mTextViewPlaceName.setText(mDataArrayList.get(position).getPlaceName());
        holder.mTextViewAddressName.setText(mDataArrayList.get(position).getAddressName());
        holder.mTextViewCategory.setText(mDataArrayList.get(position).getCategoryGroupName());
        try {
            int distance = Integer.parseInt(mDataArrayList.get(position).getDistanceStr());
            if(distance>1000){
                int disKm = distance / 1000;
                holder.mTextViewDistance.setText(disKm + "Km");
            }
            else{
                int disM = distance;
                holder.mTextViewDistance.setText(disM + "m");
            }

        } catch (NumberFormatException e) {

        }
    }

    @Override
    public int getItemCount() {
        return (mDataArrayList != null ? mDataArrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView mTextViewPlaceName, mTextViewAddressName, mTextViewCategory, mTextViewDistance;
        LinearLayout mLinearItem;


        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            mLinearItem = itemView.findViewById(R.id.list_location_item);
            mTextViewPlaceName = itemView.findViewById(R.id.list_location_tv_place);
            mTextViewAddressName = itemView.findViewById(R.id.list_location_tv_address);
            mTextViewCategory = itemView.findViewById(R.id.list_location_tv_category);
            mTextViewDistance = itemView.findViewById(R.id.list_location_tv_distance);

            mLinearItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.itemClick(getAdapterPosition());
                }
            });
        }
    }
}
