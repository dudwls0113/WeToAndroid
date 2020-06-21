package com.ninano.weto.src.main.todo_group.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ninano.weto.R;
import com.ninano.weto.src.main.todo_group.models.GroupData;
import com.ninano.weto.src.main.todo_group.models.Member;

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

        void backItemClick(int pos);
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

        if (mData.size()-1==position){
            holder.mLinearFront.setVisibility(View.GONE);
        } else {
            holder.mLinearFront.setVisibility(View.VISIBLE);
            holder.mTextViewTitle.setText(mData.get(position).getName());
            String member = "";
            if(mData.get(position).getMemberCount()>2){
                member = mData.get(position).getMembers().get(0).getNickName() + ", " + mData.get(position).getMembers().get(1).getNickName() + "외 " + (mData.get(position).getMemberCount()-2) + "명";
            } else {
                if (mData.get(position).getMemberCount()==1){
                    member = mData.get(position).getMembers().get(0).getNickName();
                } else if (mData.get(position).getMemberCount()==2){
                    member = mData.get(position).getMembers().get(0).getNickName() + ", " + mData.get(position).getMembers().get(1).getNickName();
                }
            }
            holder.mTextViewMember.setText(member);
//            String count= "";
//            if (mData.get(position).getToDoCount()!= 0 && mData.get(position).getMeetCount()!=0){
//                count = "일정 " + mData.get(position).getToDoCount()+"개, 약속 " + mData.get(position).getMeetCount() + "개";
//            } else if (mData.get(position).getToDoCount()== 0){
//                count = "약속 " + mData.get(position).getMeetCount() + "개";
//            } else if (mData.get(position).getMeetCount()==0){
//                count = "일정 " + mData.get(position).getToDoCount()+"개";
//            }
            holder.mTextViewCount.setText("일정 " + mData.get(position).getTodoCount()+"개");

            RequestOptions sharedOptions =
                    new RequestOptions()
                            .placeholder(R.drawable.img_profile_default)
                            .error(R.drawable.img_profile_default)
                            .diskCacheStrategy(DiskCacheStrategy.DATA);

            if (mData.get(position).getMemberCount()==1){
                Glide.with(mContext).load(mData.get(position).getMembers().get(0).getProfileUrl()).apply(sharedOptions).into(holder.mImageViewImage1);
                ShapeDrawable oval = new ShapeDrawable(new OvalShape());
                oval.getPaint().setColor(Color.parseColor("#f5f6fa"));
                oval.getPaint().setAntiAlias(true);
                holder.mImageViewImage1.setBackground(oval);
                holder.mImageViewImage1.setClipToOutline(true);
            } else if(mData.get(position).getMemberCount()==2){
                Glide.with(mContext).load(mData.get(position).getMembers().get(0).getProfileUrl()).apply(sharedOptions).into(holder.mImageViewImage1);
                Glide.with(mContext).load(mData.get(position).getMembers().get(1).getProfileUrl()).apply(sharedOptions).into(holder.mImageViewImage2);
                ShapeDrawable oval = new ShapeDrawable(new OvalShape());
                oval.getPaint().setColor(Color.parseColor("#f5f6fa"));
                oval.getPaint().setAntiAlias(true);
                holder.mImageViewImage1.setBackground(oval);
                holder.mImageViewImage1.setClipToOutline(true);
                holder.mImageViewImage2.setBackground(oval);
                holder.mImageViewImage2.setClipToOutline(true);
            } else if(mData.get(position).getMemberCount()==3){
                Glide.with(mContext).load(mData.get(position).getMembers().get(0).getProfileUrl()).apply(sharedOptions).into(holder.mImageViewImage1);
                Glide.with(mContext).load(mData.get(position).getMembers().get(1).getProfileUrl()).apply(sharedOptions).into(holder.mImageViewImage2);
                Glide.with(mContext).load(mData.get(position).getMembers().get(2).getProfileUrl()).apply(sharedOptions).into(holder.mImageViewImage3);
                ShapeDrawable oval = new ShapeDrawable(new OvalShape());
                oval.getPaint().setColor(Color.parseColor("#f5f6fa"));
                oval.getPaint().setAntiAlias(true);
                holder.mImageViewImage1.setBackground(oval);
                holder.mImageViewImage1.setClipToOutline(true);
                holder.mImageViewImage2.setBackground(oval);
                holder.mImageViewImage2.setClipToOutline(true);
                holder.mImageViewImage3.setBackground(oval);
                holder.mImageViewImage3.setClipToOutline(true);
            } else {
                Glide.with(mContext).load(mData.get(position).getMembers().get(0).getProfileUrl()).apply(sharedOptions).into(holder.mImageViewImage1);
                Glide.with(mContext).load(mData.get(position).getMembers().get(1).getProfileUrl()).apply(sharedOptions).into(holder.mImageViewImage2);
                Glide.with(mContext).load(mData.get(position).getMembers().get(2).getProfileUrl()).apply(sharedOptions).into(holder.mImageViewImage3);
                Glide.with(mContext).load(mData.get(position).getMembers().get(2).getProfileUrl()).apply(sharedOptions).into(holder.mImageViewImage4);
                ShapeDrawable oval = new ShapeDrawable(new OvalShape());
                oval.getPaint().setColor(Color.parseColor("#f5f6fa"));
                oval.getPaint().setAntiAlias(true);
                holder.mImageViewImage1.setBackground(oval);
                holder.mImageViewImage1.setClipToOutline(true);
                holder.mImageViewImage2.setBackground(oval);
                holder.mImageViewImage2.setClipToOutline(true);
                holder.mImageViewImage3.setBackground(oval);
                holder.mImageViewImage3.setClipToOutline(true);
                holder.mImageViewImage4.setBackground(oval);
                holder.mImageViewImage4.setClipToOutline(true);
            }

            switch (mData.get(position).getIcon()){
                case 1:
                    holder.mImageViewType.setImageResource(R.drawable.ic_paperclip1);
                    holder.mTextViewTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup1));
                    holder.mTextViewMember.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup1));
                    holder.mTextViewCount.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup1));
                    holder.mLinearFront.setBackgroundResource(R.drawable.bg_round_group_1);
                    break;
                case 2:
                    holder.mImageViewType.setImageResource(R.drawable.ic_paperclip2);
                    holder.mTextViewTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup2));
                    holder.mTextViewMember.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup2));
                    holder.mTextViewCount.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup2));
                    holder.mLinearFront.setBackgroundResource(R.drawable.bg_round_group_2);
                    break;
                case 3:
                    holder.mImageViewType.setImageResource(R.drawable.ic_paperclip3);
                    holder.mTextViewTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup3));
                    holder.mTextViewMember.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup3));
                    holder.mTextViewCount.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup3));
                    holder.mLinearFront.setBackgroundResource(R.drawable.bg_round_group_3);
                    break;
                case 4:
                    holder.mImageViewType.setImageResource(R.drawable.ic_paperclip4);
                    holder.mTextViewTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup4));
                    holder.mTextViewMember.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup4));
                    holder.mTextViewCount.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup4));
                    holder.mLinearFront.setBackgroundResource(R.drawable.bg_round_group_4);
                    break;
                case 5:
                    holder.mImageViewType.setImageResource(R.drawable.ic_paperclip5);
                    holder.mTextViewTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup5));
                    holder.mTextViewMember.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup5));
                    holder.mTextViewCount.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup5));
                    holder.mLinearFront.setBackgroundResource(R.drawable.bg_round_group_5);
                    break;
                case 6:
                    holder.mImageViewType.setImageResource(R.drawable.ic_paperclip6);
                    holder.mTextViewTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup6));
                    holder.mTextViewMember.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup6));
                    holder.mTextViewCount.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup6));
                    holder.mLinearFront.setBackgroundResource(R.drawable.bg_round_group_6);
                    break;
                case 7:
                    holder.mImageViewType.setImageResource(R.drawable.ic_paperclip7);
                    holder.mTextViewTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup7));
                    holder.mTextViewMember.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup7));
                    holder.mTextViewCount.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup7));
                    holder.mLinearFront.setBackgroundResource(R.drawable.bg_round_group_7);
                    break;
                case 8:
                    holder.mImageViewType.setImageResource(R.drawable.ic_paperclip8);
                    holder.mTextViewTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup8));
                    holder.mTextViewMember.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup8));
                    holder.mTextViewCount.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup8));
                    holder.mLinearFront.setBackgroundResource(R.drawable.bg_round_group_8);
                    break;
                case 9:
                    holder.mImageViewType.setImageResource(R.drawable.ic_paperclip9);
                    holder.mTextViewTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup9));
                    holder.mTextViewMember.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup9));
                    holder.mTextViewCount.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup9));
                    holder.mLinearFront.setBackgroundResource(R.drawable.bg_round_group_9);
                    break;
                case 10:
                    holder.mImageViewType.setImageResource(R.drawable.ic_paperclip10);
                    holder.mTextViewTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup10));
                    holder.mTextViewMember.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup10));
                    holder.mTextViewCount.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup10));
                    holder.mLinearFront.setBackgroundResource(R.drawable.bg_round_group_10);
                    break;
                case 11:
                    holder.mImageViewType.setImageResource(R.drawable.ic_paperclip11);
                    holder.mTextViewTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup11));
                    holder.mTextViewMember.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup11));
                    holder.mTextViewCount.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup11));
                    holder.mLinearFront.setBackgroundResource(R.drawable.bg_round_group_11);
                    break;
                case 12:
                    holder.mImageViewType.setImageResource(R.drawable.ic_paperclip12);
                    holder.mTextViewTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup12));
                    holder.mTextViewMember.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup12));
                    holder.mTextViewCount.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup12));
                    holder.mLinearFront.setBackgroundResource(R.drawable.bg_round_group_12);
                    break;
                case 13:
                    holder.mImageViewType.setImageResource(R.drawable.ic_paperclip13);
                    holder.mTextViewTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup13));
                    holder.mTextViewMember.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup13));
                    holder.mTextViewCount.setTextColor(ContextCompat.getColor(mContext, R.color.colorGroup13));
                    holder.mLinearFront.setBackgroundResource(R.drawable.bg_round_group_13);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageViewType, mImageViewImage1, mImageViewImage2, mImageViewImage3, mImageViewImage4;
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
            mFrameBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getAdapterPosition() == mData.size()-1){
                        mItemClickListener.backItemClick(getAdapterPosition());
                    }
                }
            });

            mImageViewImage1 = itemView.findViewById(R.id.list_group_iv_image1);
            mImageViewImage2 = itemView.findViewById(R.id.list_group_iv_image2);
            mImageViewImage3 = itemView.findViewById(R.id.list_group_iv_image3);
            mImageViewImage4 = itemView.findViewById(R.id.list_group_iv_image4);
        }
    }
}
