package com.ninano.weto.src.main.todo_group;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ninano.weto.R;
import com.ninano.weto.src.main.MainActivity;
import com.ninano.weto.src.map_select.MapSelectActivity;

import java.util.Objects;

public class GroupMakeDialog extends Dialog {

    private GroupMakeDialogClickListener mGroupMakeDialogClickListener;
    private EditText mEditTextName;
    private ImageView mImageViewBack;
    private ImageView mImageViewGroup1, mImageViewGroup2, mImageViewGroup3, mImageViewGroup4, mImageViewGroup5, mImageViewGroup6, mImageViewGroup7,
            mImageViewGroup8, mImageViewGroup9, mImageViewGroup10, mImageViewGroup11, mImageViewGroup12, mImageViewGroup13;

    private int mGroupIcon = -1;
    private Context mContext;

    public interface GroupMakeDialogClickListener {
        void okClicked(String name, int groupIcon);
    }

    public GroupMakeDialog(@NonNull Context context, GroupMakeDialogClickListener groupMakeDialogClickListener) {
        super(context);
        mContext = context;
        mGroupMakeDialogClickListener = groupMakeDialogClickListener;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_make_group);

        mImageViewBack = findViewById(R.id.dialog_make_group_iv_back);
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        TextView mTextViewYes = findViewById(R.id.dialog_make_group_input_name_btn_ok);
        mEditTextName = findViewById(R.id.dialog_make_group_input_name_et_name);
        mEditTextName.setEnabled(true);

        mTextViewYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mEditTextName.getText().toString().length()<1){
                    ((MainActivity)mContext).showCustomToast("그룹명을 입력 해 주세요");
                    return;
                }
                if (mGroupIcon == -1){
                    ((MainActivity)mContext).showCustomToast("아이콘을 선택해주세요");
                    return;
                }
                mGroupMakeDialogClickListener.okClicked(mEditTextName.getText().toString(), mGroupIcon);
                dismiss();   //다이얼로그를 닫는 메소드입니다.
            }
        });

        mImageViewGroup1 = findViewById(R.id.dialog_personal_todo_icon_iv_1);
        mImageViewGroup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGroupIcon = 1;
                iconClick(mGroupIcon);
            }
        });
        mImageViewGroup2 = findViewById(R.id.dialog_personal_todo_icon_iv_2);
        mImageViewGroup2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGroupIcon = 2;
                iconClick(mGroupIcon);
            }
        });
        mImageViewGroup3 = findViewById(R.id.dialog_personal_todo_icon_iv_3);
        mImageViewGroup3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGroupIcon = 3;
                iconClick(mGroupIcon);
            }
        });
        mImageViewGroup4 = findViewById(R.id.dialog_personal_todo_icon_iv_4);
        mImageViewGroup4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGroupIcon = 4;
                iconClick(mGroupIcon);
            }
        });
        mImageViewGroup5 = findViewById(R.id.dialog_personal_todo_icon_iv_5);
        mImageViewGroup5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGroupIcon = 5;
                iconClick(mGroupIcon);
            }
        });
        mImageViewGroup6 = findViewById(R.id.dialog_personal_todo_icon_iv_6);
        mImageViewGroup6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGroupIcon = 6;
                iconClick(mGroupIcon);
            }
        });
        mImageViewGroup7 = findViewById(R.id.dialog_personal_todo_icon_iv_7);
        mImageViewGroup7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGroupIcon = 7;
                iconClick(mGroupIcon);
            }
        });
        mImageViewGroup8 = findViewById(R.id.dialog_personal_todo_icon_iv_8);
        mImageViewGroup8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGroupIcon = 8;
                iconClick(mGroupIcon);
            }
        });
        mImageViewGroup9 = findViewById(R.id.dialog_personal_todo_icon_iv_9);
        mImageViewGroup9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGroupIcon = 9;
                iconClick(mGroupIcon);
            }
        });
        mImageViewGroup10 = findViewById(R.id.dialog_personal_todo_icon_iv_10);
        mImageViewGroup10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGroupIcon = 10;
                iconClick(mGroupIcon);
            }
        });
        mImageViewGroup11 = findViewById(R.id.dialog_personal_todo_icon_iv_11);
        mImageViewGroup11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGroupIcon = 11;
                iconClick(mGroupIcon);
            }
        });
        mImageViewGroup12 = findViewById(R.id.dialog_personal_todo_icon_iv_12);
        mImageViewGroup12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGroupIcon = 12;
                iconClick(mGroupIcon);
            }
        });
        mImageViewGroup13 = findViewById(R.id.dialog_personal_todo_icon_iv_13);
        mImageViewGroup13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGroupIcon = 13;
                iconClick(mGroupIcon);
            }
        });
    }

    private void iconClick(int num){
        switch (num){
            case 1:
                mImageViewGroup1.setImageResource(R.drawable.group1_on);
                mImageViewGroup2.setImageResource(R.drawable.group2_off);
                mImageViewGroup3.setImageResource(R.drawable.group3_off);
                mImageViewGroup4.setImageResource(R.drawable.group4_off);
                mImageViewGroup5.setImageResource(R.drawable.group5_off);
                mImageViewGroup6.setImageResource(R.drawable.group6_off);
                mImageViewGroup7.setImageResource(R.drawable.group7_off);
                mImageViewGroup8.setImageResource(R.drawable.group8_off);
                mImageViewGroup9.setImageResource(R.drawable.group9_off);
                mImageViewGroup10.setImageResource(R.drawable.group10_off);
                mImageViewGroup11.setImageResource(R.drawable.group11_off);
                mImageViewGroup12.setImageResource(R.drawable.group12_off);
                mImageViewGroup13.setImageResource(R.drawable.group13_off);
                break;
            case 2:
                mImageViewGroup1.setImageResource(R.drawable.group1_off);
                mImageViewGroup2.setImageResource(R.drawable.group2_on);
                mImageViewGroup3.setImageResource(R.drawable.group3_off);
                mImageViewGroup4.setImageResource(R.drawable.group4_off);
                mImageViewGroup5.setImageResource(R.drawable.group5_off);
                mImageViewGroup6.setImageResource(R.drawable.group6_off);
                mImageViewGroup7.setImageResource(R.drawable.group7_off);
                mImageViewGroup8.setImageResource(R.drawable.group8_off);
                mImageViewGroup9.setImageResource(R.drawable.group9_off);
                mImageViewGroup10.setImageResource(R.drawable.group10_off);
                mImageViewGroup11.setImageResource(R.drawable.group11_off);
                mImageViewGroup12.setImageResource(R.drawable.group12_off);
                mImageViewGroup13.setImageResource(R.drawable.group13_off);
                break;
            case 3:
                mImageViewGroup1.setImageResource(R.drawable.group1_off);
                mImageViewGroup2.setImageResource(R.drawable.group2_off);
                mImageViewGroup3.setImageResource(R.drawable.group3_on);
                mImageViewGroup4.setImageResource(R.drawable.group4_off);
                mImageViewGroup5.setImageResource(R.drawable.group5_off);
                mImageViewGroup6.setImageResource(R.drawable.group6_off);
                mImageViewGroup7.setImageResource(R.drawable.group7_off);
                mImageViewGroup8.setImageResource(R.drawable.group8_off);
                mImageViewGroup9.setImageResource(R.drawable.group9_off);
                mImageViewGroup10.setImageResource(R.drawable.group10_off);
                mImageViewGroup11.setImageResource(R.drawable.group11_off);
                mImageViewGroup12.setImageResource(R.drawable.group12_off);
                mImageViewGroup13.setImageResource(R.drawable.group13_off);
                break;
            case 4:
                mImageViewGroup1.setImageResource(R.drawable.group1_off);
                mImageViewGroup2.setImageResource(R.drawable.group2_off);
                mImageViewGroup3.setImageResource(R.drawable.group3_off);
                mImageViewGroup4.setImageResource(R.drawable.group4_on);
                mImageViewGroup5.setImageResource(R.drawable.group5_off);
                mImageViewGroup6.setImageResource(R.drawable.group6_off);
                mImageViewGroup7.setImageResource(R.drawable.group7_off);
                mImageViewGroup8.setImageResource(R.drawable.group8_off);
                mImageViewGroup9.setImageResource(R.drawable.group9_off);
                mImageViewGroup10.setImageResource(R.drawable.group10_off);
                mImageViewGroup11.setImageResource(R.drawable.group11_off);
                mImageViewGroup12.setImageResource(R.drawable.group12_off);
                mImageViewGroup13.setImageResource(R.drawable.group13_off);
                break;
            case 5:
                mImageViewGroup1.setImageResource(R.drawable.group1_off);
                mImageViewGroup2.setImageResource(R.drawable.group2_off);
                mImageViewGroup3.setImageResource(R.drawable.group3_off);
                mImageViewGroup4.setImageResource(R.drawable.group4_off);
                mImageViewGroup5.setImageResource(R.drawable.group5_on);
                mImageViewGroup6.setImageResource(R.drawable.group6_off);
                mImageViewGroup7.setImageResource(R.drawable.group7_off);
                mImageViewGroup8.setImageResource(R.drawable.group8_off);
                mImageViewGroup9.setImageResource(R.drawable.group9_off);
                mImageViewGroup10.setImageResource(R.drawable.group10_off);
                mImageViewGroup11.setImageResource(R.drawable.group11_off);
                mImageViewGroup12.setImageResource(R.drawable.group12_off);
                mImageViewGroup13.setImageResource(R.drawable.group13_off);
                break;
            case 6:
                mImageViewGroup1.setImageResource(R.drawable.group1_off);
                mImageViewGroup2.setImageResource(R.drawable.group2_off);
                mImageViewGroup3.setImageResource(R.drawable.group3_off);
                mImageViewGroup4.setImageResource(R.drawable.group4_off);
                mImageViewGroup5.setImageResource(R.drawable.group5_off);
                mImageViewGroup6.setImageResource(R.drawable.group6_on);
                mImageViewGroup7.setImageResource(R.drawable.group7_off);
                mImageViewGroup8.setImageResource(R.drawable.group8_off);
                mImageViewGroup9.setImageResource(R.drawable.group9_off);
                mImageViewGroup10.setImageResource(R.drawable.group10_off);
                mImageViewGroup11.setImageResource(R.drawable.group11_off);
                mImageViewGroup12.setImageResource(R.drawable.group12_off);
                mImageViewGroup13.setImageResource(R.drawable.group13_off);
                break;
            case 7:
                mImageViewGroup1.setImageResource(R.drawable.group1_off);
                mImageViewGroup2.setImageResource(R.drawable.group2_off);
                mImageViewGroup3.setImageResource(R.drawable.group3_off);
                mImageViewGroup4.setImageResource(R.drawable.group4_off);
                mImageViewGroup5.setImageResource(R.drawable.group5_off);
                mImageViewGroup6.setImageResource(R.drawable.group6_off);
                mImageViewGroup7.setImageResource(R.drawable.group7_on);
                mImageViewGroup8.setImageResource(R.drawable.group8_off);
                mImageViewGroup9.setImageResource(R.drawable.group9_off);
                mImageViewGroup10.setImageResource(R.drawable.group10_off);
                mImageViewGroup11.setImageResource(R.drawable.group11_off);
                mImageViewGroup12.setImageResource(R.drawable.group12_off);
                mImageViewGroup13.setImageResource(R.drawable.group13_off);
                break;
            case 8:
                mImageViewGroup1.setImageResource(R.drawable.group1_off);
                mImageViewGroup2.setImageResource(R.drawable.group2_off);
                mImageViewGroup3.setImageResource(R.drawable.group3_off);
                mImageViewGroup4.setImageResource(R.drawable.group4_off);
                mImageViewGroup5.setImageResource(R.drawable.group5_off);
                mImageViewGroup6.setImageResource(R.drawable.group6_off);
                mImageViewGroup7.setImageResource(R.drawable.group7_off);
                mImageViewGroup8.setImageResource(R.drawable.group8_on);
                mImageViewGroup9.setImageResource(R.drawable.group9_off);
                mImageViewGroup10.setImageResource(R.drawable.group10_off);
                mImageViewGroup11.setImageResource(R.drawable.group11_off);
                mImageViewGroup12.setImageResource(R.drawable.group12_off);
                mImageViewGroup13.setImageResource(R.drawable.group13_off);
                break;
            case 9:
                mImageViewGroup1.setImageResource(R.drawable.group1_off);
                mImageViewGroup2.setImageResource(R.drawable.group2_off);
                mImageViewGroup3.setImageResource(R.drawable.group3_off);
                mImageViewGroup4.setImageResource(R.drawable.group4_off);
                mImageViewGroup5.setImageResource(R.drawable.group5_off);
                mImageViewGroup6.setImageResource(R.drawable.group6_off);
                mImageViewGroup7.setImageResource(R.drawable.group7_off);
                mImageViewGroup8.setImageResource(R.drawable.group8_off);
                mImageViewGroup9.setImageResource(R.drawable.group9_on);
                mImageViewGroup10.setImageResource(R.drawable.group10_off);
                mImageViewGroup11.setImageResource(R.drawable.group11_off);
                mImageViewGroup12.setImageResource(R.drawable.group12_off);
                mImageViewGroup13.setImageResource(R.drawable.group13_off);
                break;
            case 10:
                mImageViewGroup1.setImageResource(R.drawable.group1_off);
                mImageViewGroup2.setImageResource(R.drawable.group2_off);
                mImageViewGroup3.setImageResource(R.drawable.group3_off);
                mImageViewGroup4.setImageResource(R.drawable.group4_off);
                mImageViewGroup5.setImageResource(R.drawable.group5_off);
                mImageViewGroup6.setImageResource(R.drawable.group6_off);
                mImageViewGroup7.setImageResource(R.drawable.group7_off);
                mImageViewGroup8.setImageResource(R.drawable.group8_off);
                mImageViewGroup9.setImageResource(R.drawable.group9_off);
                mImageViewGroup10.setImageResource(R.drawable.group10_on);
                mImageViewGroup11.setImageResource(R.drawable.group11_off);
                mImageViewGroup12.setImageResource(R.drawable.group12_off);
                mImageViewGroup13.setImageResource(R.drawable.group13_off);
                break;
            case 11:
                mImageViewGroup1.setImageResource(R.drawable.group1_off);
                mImageViewGroup2.setImageResource(R.drawable.group2_off);
                mImageViewGroup3.setImageResource(R.drawable.group3_off);
                mImageViewGroup4.setImageResource(R.drawable.group4_off);
                mImageViewGroup5.setImageResource(R.drawable.group5_off);
                mImageViewGroup6.setImageResource(R.drawable.group6_off);
                mImageViewGroup7.setImageResource(R.drawable.group7_off);
                mImageViewGroup8.setImageResource(R.drawable.group8_off);
                mImageViewGroup9.setImageResource(R.drawable.group9_off);
                mImageViewGroup10.setImageResource(R.drawable.group10_off);
                mImageViewGroup11.setImageResource(R.drawable.group11_on);
                mImageViewGroup12.setImageResource(R.drawable.group12_off);
                mImageViewGroup13.setImageResource(R.drawable.group13_off);
                break;
            case 12:
                mImageViewGroup1.setImageResource(R.drawable.group1_off);
                mImageViewGroup2.setImageResource(R.drawable.group2_off);
                mImageViewGroup3.setImageResource(R.drawable.group3_off);
                mImageViewGroup4.setImageResource(R.drawable.group4_off);
                mImageViewGroup5.setImageResource(R.drawable.group5_off);
                mImageViewGroup6.setImageResource(R.drawable.group6_off);
                mImageViewGroup7.setImageResource(R.drawable.group7_off);
                mImageViewGroup8.setImageResource(R.drawable.group8_off);
                mImageViewGroup9.setImageResource(R.drawable.group9_off);
                mImageViewGroup10.setImageResource(R.drawable.group10_off);
                mImageViewGroup11.setImageResource(R.drawable.group11_off);
                mImageViewGroup12.setImageResource(R.drawable.group12_on);
                mImageViewGroup13.setImageResource(R.drawable.group13_off);
                break;
            case 13:
                mImageViewGroup1.setImageResource(R.drawable.group1_off);
                mImageViewGroup2.setImageResource(R.drawable.group2_off);
                mImageViewGroup3.setImageResource(R.drawable.group3_off);
                mImageViewGroup4.setImageResource(R.drawable.group4_off);
                mImageViewGroup5.setImageResource(R.drawable.group5_off);
                mImageViewGroup6.setImageResource(R.drawable.group6_off);
                mImageViewGroup7.setImageResource(R.drawable.group7_off);
                mImageViewGroup8.setImageResource(R.drawable.group8_off);
                mImageViewGroup9.setImageResource(R.drawable.group9_off);
                mImageViewGroup10.setImageResource(R.drawable.group10_off);
                mImageViewGroup11.setImageResource(R.drawable.group11_off);
                mImageViewGroup12.setImageResource(R.drawable.group12_off);
                mImageViewGroup13.setImageResource(R.drawable.group13_on);
                break;
        }
    }

}
