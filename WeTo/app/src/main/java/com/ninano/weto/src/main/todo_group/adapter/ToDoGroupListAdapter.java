package com.ninano.weto.src.main.todo_group.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ninano.weto.R;
import com.ninano.weto.db.ToDoWithData;
import com.ninano.weto.src.main.todo_group.models.ToDoGroupData;
import com.ninano.weto.src.main.todo_personal.adpater.ToDoPersonalItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.Collections;

import static com.ninano.weto.src.ApplicationClass.ALL_DAY;
import static com.ninano.weto.src.ApplicationClass.ALWAYS;
import static com.ninano.weto.src.ApplicationClass.AT_ARRIVE;
import static com.ninano.weto.src.ApplicationClass.AT_NEAR;
import static com.ninano.weto.src.ApplicationClass.AT_START;
import static com.ninano.weto.src.ApplicationClass.EVENING;
import static com.ninano.weto.src.ApplicationClass.LOCATION;
import static com.ninano.weto.src.ApplicationClass.MEET;
import static com.ninano.weto.src.ApplicationClass.MONTH_DAY;
import static com.ninano.weto.src.ApplicationClass.MORNING;
import static com.ninano.weto.src.ApplicationClass.NIGHT;
import static com.ninano.weto.src.ApplicationClass.ONE_DAY;
import static com.ninano.weto.src.ApplicationClass.TIME;
import static com.ninano.weto.src.ApplicationClass.WEEK_DAY;

public class ToDoGroupListAdapter extends RecyclerView.Adapter<ToDoGroupListAdapter.CustomViewHolder> implements ToDoPersonalItemTouchHelperCallback.OnItemMoveListener {

    private Context mContext;
    private ArrayList<ToDoWithData> mData;
    private ItemClickListener mItemClickListener;

    public ToDoGroupListAdapter(Context context, ArrayList<ToDoWithData> arrayList, ItemClickListener itemClickListener){
        mContext = context;
        mData = arrayList;
        mItemClickListener = itemClickListener;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mData, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemSwipe(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onLeftClick(int position, RecyclerView.ViewHolder viewHolder) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onRightClick(int position, RecyclerView.ViewHolder viewHolder) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    public interface ItemClickListener{
        void itemClick(int pos);

        void onStartDrag(CustomViewHolder holder);
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_todo_group, parent, false);
        return new CustomViewHolder(v);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, final int position) {
        holder.mTextViewTitle.setText(mData.get(position).getTitle());
        StringBuilder condition = new StringBuilder();
        switch (mData.get(position).getType()) {
            case TIME:
                switch (mData.get(position).getRepeatType()) {
                    case ALL_DAY:
                        condition.append("매일, ").append(mData.get(position).getTime());
                        break;
                    case WEEK_DAY:
                        condition.append("매 주 ");
                        String[] splitArr = mData.get(position).getRepeatDayOfWeek().split(",");
                        for (String s : splitArr) {
                            condition.append(s).append("요일 ");
                        }
                        condition.append(", " + mData.get(position).getTime());
                        break;
                    case MONTH_DAY:
                        condition.append("매 월 ").append(mData.get(position).getRepeatDay()).append("일, ").append(mData.get(position).getTime());
                        break;
                    case ONE_DAY:
                        condition.append(mData.get(position).getDate()).append(", ").append(mData.get(position).getTime());
                        break;
                }

                break;
            case LOCATION:
                condition.append(mData.get(position).getLocationName());
                switch (mData.get(position).getLocationMode()) {
                    case AT_START:
                        condition.append("에서 출발 할 때");
                        break;
                    case AT_ARRIVE:
                        condition.append("에 도착 할 때");
                        break;
                    case AT_NEAR:
                        condition.append(" 지나갈 때");
                        break;
                }
                switch (mData.get(position).getTimeSlot()) {
                    case ALWAYS:
                        condition.append(", 언제든");
                        break;
                    case MORNING:
                        condition.append(", 아침(06시 ~ 12시)");
                        break;
                    case EVENING:
                        condition.append(", 오후(12시 ~ 21시)");
                        break;
                    case NIGHT:
                        condition.append(", 밤(21시 ~ 06시)");
                        break;
                }
                break;
            case MEET:
                condition.append(mData.get(position).getLocationName()).append("에서 ");
        }
        holder.mTextViewSubTitle.setText(condition);

        if (mData.get(position).isEditMode()){
            holder.mImageViewEdit.setImageResource(R.drawable.ic_edit_2);
        } else {
            holder.mImageViewEdit.setImageResource(R.drawable.ic_drag);
        }

        holder.mTextViewName.setText(mData.get(position).getGroupTodoCreator());

        holder.mImageViewEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!mData.get(position).isEditMode()){
                    mItemClickListener.onStartDrag(holder);
                }
                return false;
            }
        });
        if(mData.get(position).getType()!=MEET){
            if (mData.get(position).getStatus().equals("ACTIVATE")){
                switch (mData.get(position).getIcon()){
                    case 1:
                        holder.mImageViewIcon.setImageResource(R.drawable.group1_off);
                        break;
                    case 2:
                        holder.mImageViewIcon.setImageResource(R.drawable.group2_off);
                        break;
                    case 3:
                        holder.mImageViewIcon.setImageResource(R.drawable.group3_off);
                        break;
                    case 4:
                        holder.mImageViewIcon.setImageResource(R.drawable.group4_off);
                        break;
                    case 5:
                        holder.mImageViewIcon.setImageResource(R.drawable.group5_off);
                        break;
                    case 6:
                        holder.mImageViewIcon.setImageResource(R.drawable.group6_off);
                        break;
                    case 7:
                        holder.mImageViewIcon.setImageResource(R.drawable.group7_off);
                        break;
                    case 8:
                        holder.mImageViewIcon.setImageResource(R.drawable.group8_off);
                        break;
                    case 9:
                        holder.mImageViewIcon.setImageResource(R.drawable.group9_off);
                        break;
                    case 10:
                        holder.mImageViewIcon.setImageResource(R.drawable.group10_off);
                        break;
                    case 11:
                        holder.mImageViewIcon.setImageResource(R.drawable.group11_off);
                        break;
                    case 12:
                        holder.mImageViewIcon.setImageResource(R.drawable.group12_off);
                        break;
                    case 13:
                        holder.mImageViewIcon.setImageResource(R.drawable.group13_off);
                        break;
                }
            } else if(mData.get(position).getStatus().equals("DONE")){
                switch (mData.get(position).getIcon()){
                    case 1:
                        holder.mImageViewIcon.setImageResource(R.drawable.group1_on);
                        break;
                    case 2:
                        holder.mImageViewIcon.setImageResource(R.drawable.group2_on);
                        break;
                    case 3:
                        holder.mImageViewIcon.setImageResource(R.drawable.group3_on);
                        break;
                    case 4:
                        holder.mImageViewIcon.setImageResource(R.drawable.group4_on);
                        break;
                    case 5:
                        holder.mImageViewIcon.setImageResource(R.drawable.group5_on);
                        break;
                    case 6:
                        holder.mImageViewIcon.setImageResource(R.drawable.group6_on);
                        break;
                    case 7:
                        holder.mImageViewIcon.setImageResource(R.drawable.group7_on);
                        break;
                    case 8:
                        holder.mImageViewIcon.setImageResource(R.drawable.group8_on);
                        break;
                    case 9:
                        holder.mImageViewIcon.setImageResource(R.drawable.group9_on);
                        break;
                    case 10:
                        holder.mImageViewIcon.setImageResource(R.drawable.group10_on);
                        break;
                    case 11:
                        holder.mImageViewIcon.setImageResource(R.drawable.group11_on);
                        break;
                    case 12:
                        holder.mImageViewIcon.setImageResource(R.drawable.group12_on);
                        break;
                    case 13:
                        holder.mImageViewIcon.setImageResource(R.drawable.group13_on);
                        break;
                }
            }
        } else if(mData.get(position).getType()==MEET){
            if (mData.get(position).getStatus().equals("ACTIVATE")){
                switch (mData.get(position).getIcon()){
                    case 1:
                        holder.mImageViewIcon.setImageResource(R.drawable.appointment1_off);
                        break;
                    case 2:
                        holder.mImageViewIcon.setImageResource(R.drawable.appointment2_off);
                        break;
                    case 3:
                        holder.mImageViewIcon.setImageResource(R.drawable.appointment3_off);
                        break;
                    case 4:
                        holder.mImageViewIcon.setImageResource(R.drawable.appointment4_off);
                        break;
                    case 5:
                        holder.mImageViewIcon.setImageResource(R.drawable.appointment5_off);
                        break;
                    case 6:
                        holder.mImageViewIcon.setImageResource(R.drawable.appointment6_off);
                        break;
                    case 7:
                        holder.mImageViewIcon.setImageResource(R.drawable.appointment7_off);
                        break;
                    case 8:
                        holder.mImageViewIcon.setImageResource(R.drawable.appointment8_off);
                        break;
                    case 9:
                        holder.mImageViewIcon.setImageResource(R.drawable.appointment9_off);
                        break;
                    case 10:
                        holder.mImageViewIcon.setImageResource(R.drawable.appointment10_off);
                        break;
                    case 11:
                        holder.mImageViewIcon.setImageResource(R.drawable.appointment11_off);
                        break;
                    case 12:
                        holder.mImageViewIcon.setImageResource(R.drawable.appointment12_off);
                        break;
                    case 13:
                        holder.mImageViewIcon.setImageResource(R.drawable.appointment13_off);
                        break;
                }
            } else if(mData.get(position).getStatus().equals("DONE")){
                switch (mData.get(position).getIcon()){
                    case 1:
                        holder.mImageViewIcon.setImageResource(R.drawable.appointment1_on);
                        break;
                    case 2:
                        holder.mImageViewIcon.setImageResource(R.drawable.appointment2_on);
                        break;
                    case 3:
                        holder.mImageViewIcon.setImageResource(R.drawable.appointment3_on);
                        break;
                    case 4:
                        holder.mImageViewIcon.setImageResource(R.drawable.appointment4_on);
                        break;
                    case 5:
                        holder.mImageViewIcon.setImageResource(R.drawable.appointment5_on);
                        break;
                    case 6:
                        holder.mImageViewIcon.setImageResource(R.drawable.appointment6_on);
                        break;
                    case 7:
                        holder.mImageViewIcon.setImageResource(R.drawable.appointment7_on);
                        break;
                    case 8:
                        holder.mImageViewIcon.setImageResource(R.drawable.appointment8_on);
                        break;
                    case 9:
                        holder.mImageViewIcon.setImageResource(R.drawable.appointment9_on);
                        break;
                    case 10:
                        holder.mImageViewIcon.setImageResource(R.drawable.appointment10_on);
                        break;
                    case 11:
                        holder.mImageViewIcon.setImageResource(R.drawable.appointment11_on);
                        break;
                    case 12:
                        holder.mImageViewIcon.setImageResource(R.drawable.appointment12_on);
                        break;
                    case 13:
                        holder.mImageViewIcon.setImageResource(R.drawable.appointment13_on);
                        break;
                }
            }
        }
        if (mData.get(position).getIsImportant() == 'Y'){
            switch (mData.get(position).getIcon()){
                case 1:
                    holder.mTextViewTitle.setTextColor(mContext.getResources().getColor(R.color.colorGroup1));
                    break;
                case 2:
                    holder.mTextViewTitle.setTextColor(mContext.getResources().getColor(R.color.colorGroup2));
                    break;
                case 3:
                    holder.mTextViewTitle.setTextColor(mContext.getResources().getColor(R.color.colorGroup3));
                    break;
                case 4:
                    holder.mTextViewTitle.setTextColor(mContext.getResources().getColor(R.color.colorGroup4));
                    break;
                case 5:
                    holder.mTextViewTitle.setTextColor(mContext.getResources().getColor(R.color.colorGroup5));
                    break;
                case 6:
                    holder.mTextViewTitle.setTextColor(mContext.getResources().getColor(R.color.colorGroup6));
                    break;
                case 7:
                    holder.mTextViewTitle.setTextColor(mContext.getResources().getColor(R.color.colorGroup7));
                    break;
                case 8:
                    holder.mTextViewTitle.setTextColor(mContext.getResources().getColor(R.color.colorGroup8));
                    break;
                case 9:
                    holder.mTextViewTitle.setTextColor(mContext.getResources().getColor(R.color.colorGroup9));
                    break;
                case 10:
                    holder.mTextViewTitle.setTextColor(mContext.getResources().getColor(R.color.colorGroup10));
                    break;
                case 11:
                    holder.mTextViewTitle.setTextColor(mContext.getResources().getColor(R.color.colorGroup11));
                    break;
                case 12:
                    holder.mTextViewTitle.setTextColor(mContext.getResources().getColor(R.color.colorGroup12));
                    break;
                case 13:
                    holder.mTextViewTitle.setTextColor(mContext.getResources().getColor(R.color.colorGroup13));
                    break;
            }
        } else {
            holder.mTextViewTitle.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
        }
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageViewIcon, mImageViewType, mImageViewEdit;
        TextView mTextViewTitle, mTextViewSubTitle, mTextViewName;
        View mItem;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageViewIcon = itemView.findViewById(R.id.list_todo_group_iv_icon);
            mImageViewType = itemView.findViewById(R.id.list_todo_group_iv_type);
            mImageViewEdit = itemView.findViewById(R.id.list_todo_group_iv_edit);
            mTextViewTitle = itemView.findViewById(R.id.list_todo_group_tv_title);
            mTextViewSubTitle = itemView.findViewById(R.id.list_todo_group_tv_subtitle);
            mTextViewName = itemView.findViewById(R.id.list_todo_group_tv_name);
            mItem = itemView.findViewById(R.id.list_todo_group_item);
            mItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.itemClick(getAdapterPosition());
                }
            });
        }
    }
}
