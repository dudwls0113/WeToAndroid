package com.ninano.weto.src.group_detail;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.ninano.weto.R;

public class GroupToDoAddDialog extends Dialog {

    private Context mContext;
    private ImageView mImageViewBack;
    private Button mButtonTodo, mButtonMeet;
    private GroupToDoAddListener mGroupToDoAddListener;

    public GroupToDoAddDialog(@NonNull Context context, GroupToDoAddListener groupToDoAddListener) {
        super(context);
        mContext = context;
        mGroupToDoAddListener = groupToDoAddListener;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_group_todo_add);
        mImageViewBack = findViewById(R.id.dialog_group_todo_add_iv_back);
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mButtonTodo = findViewById(R.id.dialog_group_todo_add_btn_todo);
        mButtonTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGroupToDoAddListener.todoClick();
            }
        });

        mButtonMeet = findViewById(R.id.dialog_group_todo_add_btn_promise);
        mButtonMeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGroupToDoAddListener.meetClick();
            }
        });
    }

    public interface GroupToDoAddListener{
        void todoClick();
        void meetClick();
    }
}
