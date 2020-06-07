package com.ninano.weto.src.main.todo_personal.adpater;

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
import com.ninano.weto.src.main.todo_personal.models.ToDoPersonalData;

import java.util.ArrayList;
import java.util.Collections;

import static com.ninano.weto.src.ApplicationClass.ALL_DAY;
import static com.ninano.weto.src.ApplicationClass.ALWAYS;
import static com.ninano.weto.src.ApplicationClass.AT_ARRIVE;
import static com.ninano.weto.src.ApplicationClass.AT_NEAR;
import static com.ninano.weto.src.ApplicationClass.AT_START;
import static com.ninano.weto.src.ApplicationClass.EVENING;
import static com.ninano.weto.src.ApplicationClass.LOCATION;
import static com.ninano.weto.src.ApplicationClass.MONTH_DAY;
import static com.ninano.weto.src.ApplicationClass.MORNING;
import static com.ninano.weto.src.ApplicationClass.NIGHT;
import static com.ninano.weto.src.ApplicationClass.ONE_DAY;
import static com.ninano.weto.src.ApplicationClass.TIME;
import static com.ninano.weto.src.ApplicationClass.WEEK_DAY;

public class ToDoPersonalListAdapter extends RecyclerView.Adapter<ToDoPersonalListAdapter.CustomViewHolder> implements ToDoPersonalItemTouchHelperCallback.OnItemMoveListener{

    private Context mContext;
    private ArrayList<ToDoWithData> mData;
    private ItemClickListener mItemClickLIstener;

    public ToDoPersonalListAdapter(Context context, ArrayList<ToDoWithData> arrayList, ItemClickListener itemClickLIstener){
        mContext = context;
        mData = arrayList;
        mItemClickLIstener = itemClickLIstener;
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

    public interface ItemClickListener{
        void itemClick(int pos);

        void editClick(int pos);

        void onStartDrag(CustomViewHolder holder);

        void doneClick(int pos);
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_todo_personal, parent, false);
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
        }
        holder.mTextViewSubTitle.setText(condition);

        if (mData.get(position).isEditMode()){
            holder.mImageViewEdit.setImageResource(R.drawable.ic_edit_2);
        } else {
            holder.mImageViewEdit.setImageResource(R.drawable.ic_drag);
        }

        holder.mImageViewEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!mData.get(position).isEditMode()){
                    mItemClickLIstener.onStartDrag(holder);
                }
                return false;
            }
        });
        if (mData.get(position).getStatus().equals("ACTIVATE")){
            switch (mData.get(position).getIcon()){
                case 1:
                    holder.mImageViewIcon.setImageResource(R.drawable.personal_icon_1);
                    break;
                case 2:
                    holder.mImageViewIcon.setImageResource(R.drawable.personal_icon_2);
                    break;
                case 3:
                    holder.mImageViewIcon.setImageResource(R.drawable.personal_icon_3);
                    break;
                case 4:
                    holder.mImageViewIcon.setImageResource(R.drawable.personal_icon_4);
                    break;
                case 5:
                    holder.mImageViewIcon.setImageResource(R.drawable.personal_icon_5);
                    break;
                case 6:
                    holder.mImageViewIcon.setImageResource(R.drawable.personal_icon_6);
                    break;
                case 7:
                    holder.mImageViewIcon.setImageResource(R.drawable.personal_icon_7);
                    break;
                case 8:
                    holder.mImageViewIcon.setImageResource(R.drawable.personal_icon_8);
                    break;
                case 9:
                    holder.mImageViewIcon.setImageResource(R.drawable.personal_icon_9);
                    break;
                case 10:
                    holder.mImageViewIcon.setImageResource(R.drawable.personal_icon_10);
                    break;
                case 11:
                    holder.mImageViewIcon.setImageResource(R.drawable.personal_icon_11);
                    break;
                case 12:
                    holder.mImageViewIcon.setImageResource(R.drawable.personal_icon_12);
                    break;
                case 13:
                    holder.mImageViewIcon.setImageResource(R.drawable.personal_icon_13);
                    break;
            }
        } else if(mData.get(position).getStatus().equals("DONE")){
            switch (mData.get(position).getIcon()){
                case 1:
                    holder.mImageViewIcon.setImageResource(R.drawable.personal_icon_1_on);
                    break;
                case 2:
                    holder.mImageViewIcon.setImageResource(R.drawable.personal_icon_2_on);
                    break;
                case 3:
                    holder.mImageViewIcon.setImageResource(R.drawable.personal_icon_3_on);
                    break;
                case 4:
                    holder.mImageViewIcon.setImageResource(R.drawable.personal_icon_4_on);
                    break;
                case 5:
                    holder.mImageViewIcon.setImageResource(R.drawable.personal_icon_5_on);
                    break;
                case 6:
                    holder.mImageViewIcon.setImageResource(R.drawable.personal_icon_6_on);
                    break;
                case 7:
                    holder.mImageViewIcon.setImageResource(R.drawable.personal_icon_7_on);
                    break;
                case 8:
                    holder.mImageViewIcon.setImageResource(R.drawable.personal_icon_8_on);
                    break;
                case 9:
                    holder.mImageViewIcon.setImageResource(R.drawable.personal_icon_9_on);
                    break;
                case 10:
                    holder.mImageViewIcon.setImageResource(R.drawable.personal_icon_10_on);
                    break;
                case 11:
                    holder.mImageViewIcon.setImageResource(R.drawable.personal_icon_11_on);
                    break;
                case 12:
                    holder.mImageViewIcon.setImageResource(R.drawable.personal_icon_12_on);
                    break;
                case 13:
                    holder.mImageViewIcon.setImageResource(R.drawable.personal_icon_13_on);
                    break;
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
        return (mData!=null ? mData.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageViewIcon, mImageViewType, mImageViewEdit;
        TextView mTextViewTitle, mTextViewSubTitle;
        View mItem;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageViewIcon = itemView.findViewById(R.id.list_todo_personal_iv_icon);
            mImageViewIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickLIstener.doneClick(getAdapterPosition());
                }
            });
            mImageViewType = itemView.findViewById(R.id.list_todo_personal_iv_type);
            mImageViewEdit = itemView.findViewById(R.id.list_todo_personal_iv_edit);
            mTextViewTitle = itemView.findViewById(R.id.list_todo_personal_tv_title);
            mTextViewSubTitle = itemView.findViewById(R.id.list_todo_personal_tv_subtitle);
            mItem = itemView.findViewById(R.id.list_todo_personal_item);
            mItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickLIstener.itemClick(getAdapterPosition());
                }
            });
            mImageViewEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickLIstener.editClick(getAdapterPosition());
                }
            });
        }
    }
}
