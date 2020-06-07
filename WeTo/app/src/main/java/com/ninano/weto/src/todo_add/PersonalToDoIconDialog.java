package com.ninano.weto.src.todo_add;

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

public class PersonalToDoIconDialog extends Dialog {

    private Context mContext;
    private ImageView mImageViewBack;
    private Button mButtonSelect;
    private SelectClickListener mSelectClickListener = null;
    private int iconNum = 0;

    private ImageView mImageViewIcon1, mImageViewIcon2, mImageViewIcon3, mImageViewIcon4, mImageViewIcon5, mImageViewIcon6,
            mImageViewIcon7, mImageViewIcon8, mImageViewIcon9, mImageViewIcon10, mImageViewIcon11, mImageViewIcon12, mImageViewIcon13;

    public PersonalToDoIconDialog(@NonNull Context context, SelectClickListener selectClickListener) {
        super(context);
        mContext = context;
        mSelectClickListener = selectClickListener;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_personal_todo_icon);
        mImageViewBack = findViewById(R.id.dialog_personal_todo_icon_iv_back);
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mImageViewIcon1 = findViewById(R.id.dialog_personal_todo_icon_iv_1);
        mImageViewIcon2 = findViewById(R.id.dialog_personal_todo_icon_iv_2);
        mImageViewIcon3 = findViewById(R.id.dialog_personal_todo_icon_iv_3);
        mImageViewIcon4 = findViewById(R.id.dialog_personal_todo_icon_iv_4);
        mImageViewIcon5 = findViewById(R.id.dialog_personal_todo_icon_iv_5);
        mImageViewIcon6 = findViewById(R.id.dialog_personal_todo_icon_iv_6);
        mImageViewIcon7 = findViewById(R.id.dialog_personal_todo_icon_iv_7);
        mImageViewIcon8 = findViewById(R.id.dialog_personal_todo_icon_iv_8);
        mImageViewIcon9 = findViewById(R.id.dialog_personal_todo_icon_iv_9);
        mImageViewIcon10 = findViewById(R.id.dialog_personal_todo_icon_iv_10);
        mImageViewIcon11 = findViewById(R.id.dialog_personal_todo_icon_iv_11);
        mImageViewIcon12 = findViewById(R.id.dialog_personal_todo_icon_iv_12);
        mImageViewIcon13 = findViewById(R.id.dialog_personal_todo_icon_iv_13);
    }

    public interface SelectClickListener{
        void selectClick();
    }
}
