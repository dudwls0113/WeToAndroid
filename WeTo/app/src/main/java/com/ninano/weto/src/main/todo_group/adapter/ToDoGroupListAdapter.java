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
import com.ninano.weto.src.main.todo_group.models.ToDoGroupData;
import com.ninano.weto.src.main.todo_personal.adpater.ToDoPersonalItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.Collections;

public class ToDoGroupListAdapter extends RecyclerView.Adapter<ToDoGroupListAdapter.CustomViewHolder> implements ToDoPersonalItemTouchHelperCallback.OnItemMoveListener {

    private Context mContext;
    private ArrayList<ToDoGroupData> mData;
    private ItemClickListener mItemClickListener;

    public ToDoGroupListAdapter(Context context, ArrayList<ToDoGroupData> arrayList, ItemClickListener itemClickListener){
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
        holder.mTextViewTitle.setText(mData.get(position).getToDoTitle());
        holder.mTextViewSubTitle.setText(mData.get(position).getSubTitle());
        holder.mTextViewName.setText(mData.get(position).getName());

        if (mData.get(position).isEditMode()){
            holder.mImageViewEdit.setBackgroundResource(R.drawable.ic_edit_2);
        } else {
            holder.mImageViewEdit.setBackgroundResource(R.drawable.ic_drag);
        }

        holder.mImageViewEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!mData.get(position).isEditMode()){
                    mItemClickListener.onStartDrag(holder);
                }
                return false;
            }
        });
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
