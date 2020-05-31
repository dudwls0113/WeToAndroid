package com.ninano.weto.src.todo_detail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ninano.weto.R;
import com.ninano.weto.src.BaseActivity;

public class ToDoDetailActivity extends BaseActivity {

    private Context mContext;
    private ImageView mImageViewBack, mImageViewIcon;
    private TextView mTextViewTitle, mTextViewNotiType, mTextViewNotiTypeContent, mTextViewMemo, mTextViewMemoContent;
    private Button mButtonModify;
    private int iconType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_detail);
        mContext = this;
        init();
    }

    void init(){
        mImageViewBack = findViewById(R.id.todo_detail_iv_x);
        mImageViewIcon = findViewById(R.id.todo_detail_iv_icon);
        mTextViewTitle = findViewById(R.id.todo_detail_tv_title);
        mTextViewNotiType = findViewById(R.id.todo_detail_tv_noti_type);
        mTextViewNotiTypeContent = findViewById(R.id.todo_detail_tv_noti_type_content);
        mTextViewMemo = findViewById(R.id.todo_detail_tv_memo);
        mTextViewMemoContent = findViewById(R.id.todo_detail_tv_memo_content);
        mButtonModify = findViewById(R.id.todo_detail_btn_modify);
    }
}
