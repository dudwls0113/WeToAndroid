package com.ninano.weto.src.main.todo_personal.adpater;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.ninano.weto.R;

enum ButtonState{
    GONE,
    LEFT_VISIBLE,
    RIGHT_VISIBLE
}

public class ToDoPersonalItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final OnItemMoveListener mItemMoveListener;
    private boolean swipeBack = false;
    private ButtonState buttonShowedState = ButtonState.GONE;
    private Context mContext;

    public ToDoPersonalItemTouchHelperCallback(OnItemMoveListener onItemMoveListener, Context context){
        mItemMoveListener = onItemMoveListener;
        mContext = context;
    }

    public interface OnItemMoveListener{
        void onItemMove(int fromPosition, int toPosition);
        void onItemSwipe(int position);
        void onRemoveClick(int position, RecyclerView.ViewHolder viewHolder);
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        mItemMoveListener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        mItemMoveListener.onItemSwipe(viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height/3;
            Paint p = new Paint();
            if (dX>0){
                p.setColor(Color.parseColor("#ff6f00"));
                RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                c.drawRect(background, p);

                TextPaint textPaint;
                String text = "삭제";
                int textSize = mContext.getResources().getDimensionPixelSize(R.dimen.sp14);
                textPaint = new TextPaint();
                textPaint.setAntiAlias(true);
                textPaint.setColor(Color.WHITE);
                textPaint.setTextSize(textSize);
                float textWidth =  textPaint.measureText(text);
                float textX = 100;
                float textY = itemView.getTop() + itemView.getPaddingTop() + height / 2
                        + ((Math.abs(textPaint.ascent() - Math.abs(textPaint.descent()))) / 2);
                c.drawText(text, textX, textY, textPaint);
            } else {
                p.setColor(Color.parseColor("#ff6f00"));
                RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                c.drawRect(background, p);

                TextPaint textPaint;
                String text = "삭제";
                int textSize = mContext.getResources().getDimensionPixelSize(R.dimen.sp14);
                textPaint = new TextPaint();
                textPaint.setAntiAlias(true);
                textPaint.setColor(Color.WHITE);
                textPaint.setTextSize(textSize);
                float textWidth =  textPaint.measureText(text);
                float textX = itemView.getWidth() - itemView.getPaddingRight() - textWidth-100;
                float textY = itemView.getTop() + itemView.getPaddingTop() + height / 2
                        + ((Math.abs(textPaint.ascent() - Math.abs(textPaint.descent()))) / 2);
                c.drawText(text, textX, textY, textPaint);
            }
        }
        super.onChildDraw(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive);
    }
}
