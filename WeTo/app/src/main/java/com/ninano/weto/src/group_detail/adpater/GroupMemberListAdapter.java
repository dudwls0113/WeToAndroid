package com.ninano.weto.src.group_detail.adpater;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ninano.weto.R;
import com.ninano.weto.src.group_detail.models.GroupMemberData;

import java.util.ArrayList;

public class GroupMemberListAdapter extends RecyclerView.Adapter<GroupMemberListAdapter.CustomViewHolder> {

    private Context mContext;
    private ArrayList<GroupMemberData> mData;
    private ItemClickListener mItemClickListener;
    private RequestManager mRequestManager;

    public GroupMemberListAdapter(Context context, ArrayList<GroupMemberData> arrayList, RequestManager requestManager, ItemClickListener itemClickListener){
        mContext = context;
        mData = arrayList;
        mRequestManager = requestManager;
        mItemClickListener = itemClickListener;
    }

    public interface ItemClickListener{
        void itemClick(int pos);
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_group_member, parent, false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        if(mData.get(position).isLast()){
            holder.mTextViewName.setText("추가하기");
            ShapeDrawable oval = new ShapeDrawable(new OvalShape());
            oval.getPaint().setColor(Color.parseColor("#f5f6fa"));
            oval.getPaint().setAntiAlias(true);
            oval.setPadding(90,90,90,90);
            holder.mImageViewProfile.setBackground(oval);
            holder.mImageViewProfile.setImageResource(R.drawable.ic_group_member_plus);
        }
        else {
            holder.mTextViewName.setText(mData.get(position).getName());

            RequestOptions sharedOptions =
                    new RequestOptions()
                            .placeholder(R.drawable.img_profile_default)
                            .error(R.drawable.img_profile_default)
                            .diskCacheStrategy(DiskCacheStrategy.DATA);
            ShapeDrawable oval = new ShapeDrawable(new OvalShape());
            oval.getPaint().setColor(Color.parseColor("#f5f6fa"));
            oval.getPaint().setAntiAlias(true);
            holder.mImageViewProfile.setBackground(oval);
            mRequestManager.load(mData.get(position).getImgUrl()).apply(sharedOptions).into(holder.mImageViewProfile);
        }
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageViewProfile;
        TextView mTextViewName;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageViewProfile = itemView.findViewById(R.id.list_group_member_iv_profile);
            mTextViewName = itemView.findViewById(R.id.list_group_member_tv_name);
        }
    }
}
