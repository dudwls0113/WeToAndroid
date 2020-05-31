package com.ninano.weto.src.todo_detail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ninano.weto.R;
import com.ninano.weto.db.ToDoWithData;
import com.ninano.weto.src.ApplicationClass;
import com.ninano.weto.src.BaseActivity;

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

public class ToDoDetailActivity extends BaseActivity {
    private ImageView mImageViewIcon;
    private TextView mTextViewTitle, mTextViewCategory, mTextViewCondition, mTextViewContent;
    private ToDoWithData mToDoWithData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_detail);
        Intent intent = getIntent();
        mToDoWithData = (ToDoWithData) intent.getSerializableExtra("todoData");
        initView();
    }

    void initView() {
        mTextViewTitle = findViewById(R.id.activity_todo_detail_tv_title);
        mTextViewCategory = findViewById(R.id.activity_todo_detail_tv_category);
        mTextViewCondition = findViewById(R.id.activity_todo_detail_tv_condition);
        mTextViewContent = findViewById(R.id.activity_todo_detail_tv_content);
        mImageViewIcon = findViewById(R.id.activity_todo_detail_iv_icon);

        mTextViewTitle.setText(mToDoWithData.getTitle());
        switch (mToDoWithData.getType()) {
            case TIME:
                mTextViewCategory.setText(getResources().getText(R.string.todo_detail_category_time));
                break;
            case LOCATION:
                mTextViewCategory.setText(getResources().getText(R.string.todo_detail_category_gps));
                break;
        }

        StringBuilder condition = new StringBuilder();
        switch (mToDoWithData.getType()) {
            case TIME:
                switch (mToDoWithData.getRepeatType()) {
                    case ALL_DAY:
                        condition.append("매일, ").append(mToDoWithData.getTime());
                        break;
                    case WEEK_DAY:
                        condition.append("매 주 ");
                        String[] splitArr = mToDoWithData.getRepeatDayOfWeek().split(",");
                        for (String s : splitArr) {
                            condition.append(s).append("요일 ");
                        }
                        condition.append(mToDoWithData.getTime());
                        break;
                    case MONTH_DAY:
                        condition.append("매 월 ").append(mToDoWithData.getRepeatDay()).append("일, ").append(mToDoWithData.getTime());
                        break;
                    case ONE_DAY:
                        condition.append(mToDoWithData.getDate()).append(", ").append(mToDoWithData.getTime());
                        break;
                }

                break;
            case LOCATION:
                mTextViewCategory.setText(getResources().getText(R.string.todo_detail_category_gps));
                condition.append(mToDoWithData.getLocationName());
                switch (mToDoWithData.getLocationMode()) {
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
                switch (mToDoWithData.getTimeSlot()) {
                    case ALWAYS:
                        condition.append("\n언제든");
                        break;
                    case MORNING:
                        condition.append("\n아침(06시 ~ 12시)");
                        break;
                    case EVENING:
                        condition.append("\n오후(12시 ~ 21시)");
                        break;
                    case NIGHT:
                        condition.append("\n밤(21시 ~ 06시)");
                        break;
                }
                break;
        }
        mTextViewCondition.setText(condition);
        mTextViewContent.setText(mToDoWithData.getContent());

    }

    public void customOnClick(View view) {
        switch (view.getId()) {
            case R.id.activity_todo_detail_iv_close:
                finish();
                break;
            case R.id.add_personal_todo_btn_done:

                break;
        }
    }

}
