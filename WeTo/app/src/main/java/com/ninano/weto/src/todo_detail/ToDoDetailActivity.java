package com.ninano.weto.src.todo_detail;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ninano.weto.R;
import com.ninano.weto.db.AppDatabase;
import com.ninano.weto.db.ToDoDao;
import com.ninano.weto.db.ToDoWithData;
import com.ninano.weto.src.BaseActivity;
import com.ninano.weto.src.todo_add.AddPersonalToDoActivity;

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
import static com.ninano.weto.src.ApplicationClass.getApplicationClassContext;
import static com.ninano.weto.src.common.alarm.AlarmMaker.getAlarmMaker;
import static com.ninano.weto.src.common.geofence.GeofenceMaker.getGeofenceMaker;

public class ToDoDetailActivity extends BaseActivity {

    private Context mContext;
    private ImageView mImageViewIcon;
    private TextView mTextViewTitle, mTextViewCategory, mTextViewCondition, mTextViewContent;
    private Button mButtonModify, mButtonDelete, mButtonDone;
    private ToDoWithData mToDoWithData;
    private AppDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_detail);
        mContext = this;
        mDatabase = AppDatabase.getAppDatabase(getApplicationClassContext());
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
        mButtonModify = findViewById(R.id.add_personal_todo_btn_modify);
        mButtonDelete = findViewById(R.id.add_personal_todo_btn_delete);
        mButtonDone = findViewById(R.id.add_personal_todo_btn_done);
        changeIcon(mToDoWithData.getIcon());

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

    void changeIcon(int iconNum) {
        switch (iconNum) {
            case 1:
                mImageViewIcon.setImageResource(R.drawable.personal_icon_1);
                break;
            case 2:
                mImageViewIcon.setImageResource(R.drawable.personal_icon_2);
                break;
            case 3:
                mImageViewIcon.setImageResource(R.drawable.personal_icon_3);
                break;
            case 4:
                mImageViewIcon.setImageResource(R.drawable.personal_icon_4);
                break;
            case 5:
                mImageViewIcon.setImageResource(R.drawable.personal_icon_5);
                break;
            case 6:
                mImageViewIcon.setImageResource(R.drawable.personal_icon_6);
                break;
            case 7:
                mImageViewIcon.setImageResource(R.drawable.personal_icon_7);
                break;
            case 8:
                mImageViewIcon.setImageResource(R.drawable.personal_icon_8);
                break;
            case 9:
                mImageViewIcon.setImageResource(R.drawable.personal_icon_9);
                break;
            case 10:
                mImageViewIcon.setImageResource(R.drawable.personal_icon_10);
                break;
            case 11:
                mImageViewIcon.setImageResource(R.drawable.personal_icon_11);
                break;
            case 12:
                mImageViewIcon.setImageResource(R.drawable.personal_icon_12);
                break;
            case 13:
                mImageViewIcon.setImageResource(R.drawable.personal_icon_13);
                break;
        }
    }

    public void customOnClick(View view) {
        switch (view.getId()) {
            case R.id.activity_todo_detail_iv_close:
                finish();
                break;
            case R.id.add_personal_todo_btn_done:
                new UpdateDoneAsyncTask(mDatabase.todoDao()).execute(mToDoWithData);
                break;
            case R.id.add_personal_todo_btn_modify:
                Intent intent = new Intent(mContext, AddPersonalToDoActivity.class);
                intent.putExtra("isEditMode", true);
                intent.putExtra("todoData", mToDoWithData);
                startActivity(intent);
                finish();
                break;
            case R.id.add_personal_todo_btn_delete:
                new DeleteToDoAsyncTask(mDatabase.todoDao()).execute(mToDoWithData);
                break;
        }
    }

    private class UpdateDoneAsyncTask extends AsyncTask<ToDoWithData, Void, ToDoWithData> {

        private ToDoDao mTodoDao;

        UpdateDoneAsyncTask(ToDoDao mTodoDao) {
            this.mTodoDao = mTodoDao;
        }


        @Override
        protected ToDoWithData doInBackground(ToDoWithData... toDoWithData) {
            mDatabase.todoDao().updateStatusDone(toDoWithData[0].getTodoNo());
            return toDoWithData[0];
        }

        @Override
        protected void onPostExecute(ToDoWithData toDoWithData) {
            super.onPostExecute(toDoWithData);
            if (toDoWithData.getType()==LOCATION && toDoWithData.getIsWiFi() == 'N'){
                getGeofenceMaker().removeGeofence(String.valueOf(toDoWithData.getTodoNo()));
            } else if (toDoWithData.getType() == TIME) {
                getAlarmMaker().removeAlarm(toDoWithData.getTodoNo());
            }
            finish();
        }
    }

    private class DeleteToDoAsyncTask extends AsyncTask<ToDoWithData, Void, ToDoWithData> {

        private ToDoDao mTodoDao;

        DeleteToDoAsyncTask(ToDoDao mTodoDao) {
            this.mTodoDao = mTodoDao;
        }

        @Override
        protected ToDoWithData doInBackground(ToDoWithData... toDoWithData) {
            mDatabase.todoDao().deleteToDo(toDoWithData[0].getTodoNo());
            mDatabase.todoDao().deleteToDoData(toDoWithData[0].getTodoDataNo());
            return toDoWithData[0];
        }

        @Override
        protected void onPostExecute(ToDoWithData toDoWithData) {
            super.onPostExecute(toDoWithData);
            if (toDoWithData.getStatus().equals("ACTIVATE")) { // ACTIVATE 리스트
//                mTodoList.remove(mDeletePosition);
                if (toDoWithData.getType() == LOCATION && toDoWithData.getIsWiFi() == 'N') {
                    getGeofenceMaker().removeGeofence(String.valueOf(toDoWithData.getTodoNo()));
                }
                else if (toDoWithData.getType() == TIME) {
                    getAlarmMaker().removeAlarm(toDoWithData.getTodoNo());
                }
//                mToDoPersonalListAdapter.notifyItemRemoved(mDeletePosition);
            } else if (toDoWithData.getStatus().equals("DONE")) { // DONE 리스트
                if (toDoWithData.getType() == LOCATION && toDoWithData.getIsWiFi() == 'N') {
                    getGeofenceMaker().removeGeofence(String.valueOf(toDoWithData.getTodoNo()));
                }
                else if (toDoWithData.getType() == TIME) {
                    getAlarmMaker().removeAlarm(toDoWithData.getTodoNo());
                }
            }
            finish();
        }
    }


}
