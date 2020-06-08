package com.ninano.weto.src.main.todo_personal.adpater;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.view.MotionEvent;
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
@SuppressLint("ClickableViewAccessibility")
public class ToDoPersonalItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final OnItemMoveListener mItemMoveListener;
    private boolean swipeBack = false;
    private ButtonState buttonShowedState = ButtonState.GONE;
    private static final float buttonWidth =250;
    private RectF buttonInstance = null;
    private RecyclerView.ViewHolder currentItemViewHolder = null;
    private Context mContext;

    public ToDoPersonalItemTouchHelperCallback(OnItemMoveListener onItemMoveListener, Context context){
        mItemMoveListener = onItemMoveListener;
        mContext = context;
    }

    public interface OnItemMoveListener{
        void onItemMove(int fromPosition, int toPosition);
        void onItemSwipe(int position);
        void onLeftClick(int position, RecyclerView.ViewHolder viewHolder);
        void onRightClick(int position, RecyclerView.ViewHolder viewHolder);
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START;
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
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
//            if (buttonShowedState != ButtonState.GONE) {
//                if (buttonShowedState == ButtonState.LEFT_VISIBLE) dX = Math.max(dX, buttonWidth);
//                if (buttonShowedState == ButtonState.RIGHT_VISIBLE) dX = Math.min(dX, -buttonWidth);
//                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//            } else {
//                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//            }
//            if (buttonShowedState == ButtonState.GONE) {
//                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//            } else {
//                currentItemViewHolder = viewHolder;
//                drawButtons(c, currentItemViewHolder);
//            }
            View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 3;
            Paint p = new Paint();
            if (dX > 0) {
                p.setColor(Color.parseColor("#ff6f00"));
                RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                c.drawRect(background, p);

                TextPaint textPaint;
                String text = "삭제";
                int textSize = mContext.getResources().getDimensionPixelSize(R.dimen.sp14);
                textPaint = new TextPaint();
                textPaint.setAntiAlias(true);
                textPaint.setColor(Color.WHITE);
                textPaint.setTextSize(textSize);
                float textWidth = textPaint.measureText(text);
//                c.drawText(text, background.centerX() - (textWidth / 2), background.centerY() + (textSize / 2), p);
                float textX = 100;
                float textY = itemView.getTop() + itemView.getPaddingTop() + height / 2 - (textSize/2)
                        + ((Math.abs(textPaint.ascent() - Math.abs(textPaint.descent()))) / 2);
                c.drawText(text, textX, background.centerY()+(textSize/2), textPaint);
            } else {
                p.setColor(Color.parseColor("#ff6f00"));
                RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                c.drawRect(background, p);

                TextPaint textPaint;
                String text = "삭제";
                int textSize = mContext.getResources().getDimensionPixelSize(R.dimen.sp14);
                textPaint = new TextPaint();
                textPaint.setAntiAlias(true);
                textPaint.setColor(Color.WHITE);
                textPaint.setTextSize(textSize);
                float textWidth = textPaint.measureText(text);
//                c.drawText(text, background.centerX() - (textWidth / 2), background.centerY() + (textSize / 2), p);
                float textX = itemView.getWidth() - itemView.getPaddingRight() - textWidth - 100;
                float textY = itemView.getTop() + itemView.getPaddingTop() + height / 2
                        + ((Math.abs(textPaint.ascent() - Math.abs(textPaint.descent()))) / 2);
                c.drawText(text, textX, background.centerY()+(textSize/2), textPaint);
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//        else {
//            if (buttonShowedState == ButtonState.LEFT_VISIBLE) dX = Math.max(dX, buttonWidth);
//            if (buttonShowedState == ButtonState.RIGHT_VISIBLE) dX = Math.min(dX, -buttonWidth);
//            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//        }
    }

    private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder){
        View itemView = viewHolder.itemView;
        Paint p = new Paint();

        buttonInstance = null;

        if(buttonShowedState == ButtonState.LEFT_VISIBLE){
            RectF leftButton = new RectF(itemView.getLeft(), itemView.getTop(), itemView.getLeft()+buttonWidth, itemView.getBottom());
            p.setColor(Color.parseColor("#ff6f00"));
            c.drawRect(leftButton, p);
            drawText("삭제", c, leftButton, p);
            buttonInstance = leftButton;
        }
        else if(buttonShowedState == ButtonState.RIGHT_VISIBLE){
            RectF rightButton = new RectF(itemView.getRight()-buttonWidth, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            p.setColor(Color.parseColor("#ff6f00"));
            c.drawRect(rightButton, p);
            drawText("삭제", c, rightButton, p);
            buttonInstance = rightButton;
        }

    }

    private void drawText(String text, Canvas c, RectF button, Paint p){
        int textSize = mContext.getResources().getDimensionPixelSize(R.dimen.sp14);
        p.setColor(Color.WHITE);
        p.setAntiAlias(true);
        p.setTextSize(textSize);

        float textWidth = p.measureText(text);
        c.drawText(text, button.centerX()-(textWidth/2), button.centerY()+(textSize/2),p);
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if (swipeBack){
            swipeBack=false;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    private void setTouchListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive){
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                swipeBack = event.getAction()==MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                if(swipeBack){
                    if(dX < -buttonWidth){
                        buttonShowedState = ButtonState.RIGHT_VISIBLE;
                    }
                    else if(dX>buttonWidth){
                        buttonShowedState = ButtonState.LEFT_VISIBLE;
                    }

                    if(buttonShowedState!=ButtonState.GONE){
                        setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        setItemsClickable(recyclerView, false);
                    }
                }
                return false;
            }
        });
    }

    private void setTouchDownListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive){
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN){
                       setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
                return false;
            }
        });
    }

    private void setTouchUpListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive){
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                ToDoPersonalItemTouchHelperCallback.super.onChildDraw(c,recyclerView,viewHolder,0F,dY,actionState,isCurrentlyActive);
                recyclerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return false;
                    }
                });
                setItemsClickable(recyclerView,true);
                swipeBack = false;
                if(mItemMoveListener!=null && buttonInstance!=null && buttonInstance.contains(event.getX(), event.getY())){
                    if(buttonShowedState == ButtonState.LEFT_VISIBLE){
                        mItemMoveListener.onLeftClick(viewHolder.getAdapterPosition(), viewHolder);
                    } else if(buttonShowedState == ButtonState.RIGHT_VISIBLE){
                        mItemMoveListener.onRightClick(viewHolder.getAdapterPosition(), viewHolder);
                    }
                }
                buttonShowedState = ButtonState.GONE;
                currentItemViewHolder = null;
                return false;
            }
        });
    }

    private void setItemsClickable(RecyclerView recyclerView, boolean isClickable){
        for(int i=0; i<recyclerView.getChildCount(); i++){
            recyclerView.getChildAt(i).setClickable(isClickable);
        }
    }
}
