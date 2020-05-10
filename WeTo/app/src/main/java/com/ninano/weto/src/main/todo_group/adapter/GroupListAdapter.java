package com.ninano.weto.src.main.todo_group.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ninano.weto.R;
import com.ninano.weto.src.main.todo_group.models.GroupData;

import java.util.ArrayList;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.CustomViewHolder> {

    private Context mContext;
    private ArrayList<GroupData> mData;
    private ItemClickListener mItemClickListener;
    private float mDensity;

    public GroupListAdapter(Context context, ArrayList<GroupData> arrayList, float density, ItemClickListener itemClickListener){
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_group, parent, false);
        return new CustomViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        if (position==0){
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.mFrameBack.getLayoutParams();
            params.leftMargin = (int)(24*mDensity);
            holder.mFrameBack.setLayoutParams(params);
        } else{
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.mFrameBack.getLayoutParams();
            params.leftMargin = 0;
            holder.mFrameBack.setLayoutParams(params);
        }

        if (position==mData.size()-1){
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.mFrameBack.getLayoutParams();
            params.rightMargin = (int)(24*mDensity);
            holder.mFrameBack.setLayoutParams(params);
        } else{
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.mFrameBack.getLayoutParams();
            params.rightMargin = (int)(12*mDensity);
            holder.mFrameBack.setLayoutParams(params);
        }

        if (mData.get(position).isLast()){
            holder.mLinearFront.setVisibility(View.GONE);
        } else {
            holder.mLinearFront.setVisibility(View.VISIBLE);
            holder.mTextViewTitle.setText(mData.get(position).getGruopTitle());
            String member = "";
            if(mData.get(position).getGroupMember().size()>2){
                member = mData.get(position).getGroupMember().get(0) + ", " + mData.get(position).getGroupMember().get(1) + "외 " + (mData.get(position).getGroupMember().size()-2) + "명";
            } else {
                if (mData.get(position).getGroupMember().size()==1){
                    member = mData.get(position).getGroupMember().get(0);
                } else if (mData.get(position).getGroupMember().size()==2){
                    member = mData.get(position).getGroupMember().get(0) + ", " + mData.get(position).getGroupMember().get(1);
                }
            }
            holder.mTextViewMember.setText(member);
            String count= "";
            if (mData.get(position).getToDoCount()!= 0 && mData.get(position).getMeetCount()!=0){
                count = "일정 " + mData.get(position).getToDoCount()+"개, 약속 " + mData.get(position).getMeetCount() + "개";
            } else if (mData.get(position).getToDoCount()== 0){
                count = "약속 " + mData.get(position).getMeetCount() + "개";
            } else if (mData.get(position).getMeetCount()==0){
                count = "일정 " + mData.get(position).getToDoCount()+"개";
            }
            holder.mTextViewCount.setText(count);

            switch (mData.get(position).getIconType()){
                case 1:
                    holder.mTextViewTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup1));
                    holder.mTextViewMember.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup1));
                    holder.mTextViewCount.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup1));
                    holder.mLinearFront.setBackgroundResource(R.drawable.bg_round_group_1);
                    break;
                case 2:
                    holder.mTextViewTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup2));
                    holder.mTextViewMember.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup2));
                    holder.mTextViewCount.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup2));
                    holder.mLinearFront.setBackgroundResource(R.drawable.bg_round_group_2);
                    break;
                case 3:
                    holder.mTextViewTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup3));
                    holder.mTextViewMember.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup3));
                    holder.mTextViewCount.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup3));
                    holder.mLinearFront.setBackgroundResource(R.drawable.bg_round_group_3);
                    break;
                case 4:
                    holder.mTextViewTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup4));
                    holder.mTextViewMember.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup4));
                    holder.mTextViewCount.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup4));
                    holder.mLinearFront.setBackgroundResource(R.drawable.bg_round_group_4);
                    break;
                case 5:
                    break;
                case 6:
                    break;
                case 7:
                    break;
                case 8:
                    break;
                case 9:
                    break;
                case 10:
                    break;
                case 11:
                    break;
                case 12:
                    break;
                case 13:
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageViewType;
        TextView mTextViewTitle, mTextViewMember, mTextViewCount;
        LinearLayout mLinearFront;
        FrameLayout mFrameBack;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageViewType = itemView.findViewById(R.id.list_group_iv_type);
            mTextViewTitle = itemView.findViewById(R.id.list_group_tv_title);
            mTextViewMember = itemView.findViewById(R.id.list_group_tv_member);
            mTextViewCount = itemView.findViewById(R.id.list_group_tv_count);
            mLinearFront = itemView.findViewById(R.id.list_group_layout_front);
            mLinearFront.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.itemClick(getAdapterPosition());
                }
            });
            mFrameBack = itemView.findViewById(R.id.list_group_layout_item);
        }
    }
}
