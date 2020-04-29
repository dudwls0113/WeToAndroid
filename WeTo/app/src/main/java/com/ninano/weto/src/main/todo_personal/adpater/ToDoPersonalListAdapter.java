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
import com.ninano.weto.src.main.todo_personal.models.ToDoPersonalData;

import java.util.ArrayList;
import java.util.Collections;

public class ToDoPersonalListAdapter extends RecyclerView.Adapter<ToDoPersonalListAdapter.CustomViewHolder> implements ToDoPersonalItemTouchHelperCallback.OnItemMoveListener{

    private Context mContext;
    private ArrayList<ToDoPersonalData> mData;
    private ItemClickListener mItemClickLIstener;

    public ToDoPersonalListAdapter(Context context, ArrayList<ToDoPersonalData> arrayList, ItemClickListener itemClickLIstener){
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

        void onStartDrag(CustomViewHolder holder);
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
        holder.mTextViewTitle.setText(mData.get(position).getToDoTitle());
        holder.mTextViewSubTitle.setText(mData.get(position).getSubTitle());

        if (mData.get(position).isEditMode()){
            holder.mImageViewEdit.setBackgroundResource(R.drawable.ic_edit_2);
        } else {
            holder.mImageViewEdit.setBackgroundResource(R.drawable.ic_drag);
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
        }
    }
}
