package com.ninano.weto.src.todo_add;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ninano.weto.R;
import com.ninano.weto.db.AppDatabase;
import com.ninano.weto.db.ToDo;
import com.ninano.weto.db.ToDoDao;
import com.ninano.weto.db.ToDoData;
import com.ninano.weto.db.ToDoWithData;
import com.ninano.weto.src.BaseActivity;
import com.ninano.weto.src.DeviceBootReceiver;
import com.ninano.weto.src.map_select.MapSelectActivity;
import com.ninano.weto.src.map_select.keyword_search.models.LocationResponse;
import com.ninano.weto.src.common.Alarm.AlarmBroadcastReceiver;
import com.ninano.weto.src.todo_add.adpater.MyPlaceListAdapter;
import com.ninano.weto.src.todo_add.models.MyPlace;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.ninano.weto.src.ApplicationClass.ALL_DAY;
import static com.ninano.weto.src.ApplicationClass.ALWAYS;
import static com.ninano.weto.src.ApplicationClass.AT_START;
import static com.ninano.weto.src.ApplicationClass.GPS_LADIUS;
import static com.ninano.weto.src.ApplicationClass.MONTH_DAY;
import static com.ninano.weto.src.ApplicationClass.NONE;
import static com.ninano.weto.src.ApplicationClass.NO_DATA;
import static com.ninano.weto.src.ApplicationClass.ONE_DAY;
import static com.ninano.weto.src.ApplicationClass.TIME;
import static com.ninano.weto.src.ApplicationClass.LOCATION;
import static com.ninano.weto.src.ApplicationClass.AT_ARRIVE;
import static com.ninano.weto.src.ApplicationClass.AT_NEAR;
import static com.ninano.weto.src.ApplicationClass.MORNING;
import static com.ninano.weto.src.ApplicationClass.EVENING;
import static com.ninano.weto.src.ApplicationClass.NIGHT;
import static com.ninano.weto.src.ApplicationClass.WEEK_DAY;
import static com.ninano.weto.src.common.Alarm.AlarmMaker.getAlarmMaker;
import static com.ninano.weto.src.common.Geofence.GeofenceMaker.getGeofenceMaker;
import static com.ninano.weto.src.common.Wifi.WifiMaker.getWifiMaker;

public class AddPersonalToDoActivity extends BaseActivity {

    private Context mContext;

    //공통
    private EditText mEditTextTitle, mEditTextMemo;
    private ImageView mImageViewIcon;
    private int mIcon = -1;
    private Switch mSwitchTime, mSwitchGps;
    private boolean isSwitchTime, isSwitchGps;
    private char mImportantMode = 'N';
    private int mToDoNo = -1;
    private int mTodoCategory;
    private Button mDoneBtn;
    private Switch mImportantSwitch;

    //Time Mode
    private TextView mTextViewTimeNoRepeat, mTextViewTimeDayRepeat, mTextViewTimeWeekRepeat, mTextViewTimeMonthRepeat, mTextViewDate, mTextViewTime,
            mTextViewSun, mTextViewMon, mTextViewTue, mTextViewWed, mTextViewThu, mTextViewFri, mTextViewSat;
    private FrameLayout mFrameHiddenTimeDate;
    private LinearLayout mLinearHiddenTime, mLinearHiddenTimeDate, mLinearHiddenTimeTime, mLinearHiddenTimeWeekRepeat;
    private AlarmManager mAlarmManager;
    private int mRepeatType;
    private boolean mIsDatePick, mIsTimePick;
    private int mYear =0, mMonth=0, mDay=0, mHour=0, mMinute=0;
    private String mRepeatDayOfWeek = "일,";
    private int mRepeatDay; // 매월 의 반복일 (1~31)
    private int mINTRepeatDayOfWeek = 1;
    private boolean[] selectedDayList = new boolean[7];

    //Location Mode
    private TextView mTextViewLocation, mTextViewStart, mTextViewArrive, mTextViewNear;
    private TextView mTextViewAlways, mTextViewMorning, mTextViewEvening, mTextViewNight;
    private LinearLayout mLinearHiddenGps;
    private int mLocationMode, mLocationTime, mLadius;
    private boolean mIsLocationSelected;
    private char mWifiMode = 'N';
    private String mWifiBssid = "";
    private boolean mWifiConnected;
    private Double longitude = 0.0, latitude = 0.0;

    private RecyclerView mRecyclerViewMyPlace;
    private MyPlaceListAdapter mMyPlaceListAdapter;
    private ArrayList<MyPlace> mDataArrayList = new ArrayList<>();
    private LocationResponse.Location mLocation = null;

    AppDatabase mDatabase;
    public static float dpUnit;

    //수정모드
    private boolean isEditMode = false;
    private ToDoWithData mToDoWithData;
    private int mToDoDataNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_personal_to_do);
        mContext = this;
        init();
        setTempLikeLocationData();
        setEditMode();
    }

    void setEditMode() {
        Intent intent = getIntent();
        if (intent.getBooleanExtra("isEditMode", false)) {
            isEditMode = true;
            mToDoWithData = (ToDoWithData) intent.getSerializableExtra("todoData");
            mToDoNo = mToDoWithData.getTodoNo();
            mToDoDataNo = mToDoWithData.getTodoDataNo();

            mEditTextTitle.setText(mToDoWithData.getTitle());
            mEditTextMemo.setText(mToDoWithData.getContent());

            mDoneBtn.setText("수정하기");
            switch (mToDoWithData.getType()) {
                case TIME:
                    mSwitchTime.setChecked(true);
                    mTodoCategory = TIME;
                    showTimeLayout();
                    isSwitchTime = true;
                    mIsLocationSelected = false;

                    switch (mToDoWithData.getRepeatType()) {
                        case ALL_DAY:
                            setRepeatTimeView(mTextViewTimeNoRepeat);
                            break;
                        case WEEK_DAY:
                            setRepeatTimeView(mTextViewTimeDayRepeat);
                            break;
                        case MONTH_DAY:
                            setRepeatTimeView(mTextViewTimeWeekRepeat);
                            break;
                        case ONE_DAY:
                            setRepeatTimeView(mTextViewTimeMonthRepeat);
                            break;
                    }
                    break;
                case LOCATION:
                    mSwitchGps.setChecked(true);
                    mTodoCategory = LOCATION;
                    showGpsLayout();
                    isSwitchGps = true;
                    mIsLocationSelected = true;
                    longitude = mToDoWithData.getLongitude();
                    latitude = mToDoWithData.getLatitude();
                    mLocationMode = mToDoWithData.getLocationMode();
                    mLocationTime = mToDoWithData.getTimeSlot();

                    switch (mToDoWithData.getLocationMode()) {
                        case AT_START:
                            setLocationModeView(mTextViewStart);
                            break;
                        case AT_ARRIVE:
                            setLocationModeView(mTextViewArrive);
                            break;
                        case AT_NEAR:
                            setLocationModeView(mTextViewNear);
                            break;
                    }
                    switch (mToDoWithData.getTimeSlot()) {
                        case ALWAYS:
                            setLocationTimeView(mTextViewAlways);
                            break;
                        case MORNING:
                            setLocationTimeView(mTextViewMorning);
                            break;
                        case EVENING:
                            setLocationTimeView(mTextViewEvening);
                            break;
                        case NIGHT:
                            setLocationTimeView(mTextViewNight);
                            break;
                    }
                    mLocation = new LocationResponse.Location("", mToDoWithData.getLocationName(), String.valueOf(mToDoWithData.getLongitude()), String.valueOf(mToDoWithData.getLatitude()));
                    longitude = Double.valueOf(mLocation.getLongitude());
                    latitude = Double.valueOf(mLocation.getLatitude());
                    setLocationInfo();

                    mTextViewLocation.setText(mToDoWithData.getLocationName());
                    mTextViewLocation.setTextColor(getResources().getColor(R.color.colorBlack));
                    if (mToDoWithData.getIsWiFi() == 'Y') {
                        mWifiMode = 'Y';
                        mWifiBssid = mToDoWithData.getSsid();
                        mTextViewNear.setVisibility(View.GONE);
                    } else {
                        mWifiMode = 'N';
                        mTextViewNear.setVisibility(View.VISIBLE);
                    }
                    break;

                //수정하면 UPDATE문으로 덮어쓰기, geo나 wifi, 알람매니저는 어떻게바꾸지?
                // 1. geo는 원래있던거 지우고 새로등록
                // 2. wifi는?
                // 2. 알람매니저는?
            }
            if (mToDoWithData.getIsImportant() == 'N') {
                mImportantSwitch.setChecked(false);
            } else {
                mImportantSwitch.setChecked(true);
            }
        }
    }

    void init() {
        mImageViewIcon = findViewById(R.id.add_personal_todo_iv_icon);

        mTextViewTimeNoRepeat = findViewById(R.id.add_personal_todo_tv_no_repeat);
        mTextViewTimeDayRepeat = findViewById(R.id.add_personal_todo_tv_day_repeat);
        mTextViewTimeWeekRepeat = findViewById(R.id.add_personal_todo_tv_week_repeat);
        mTextViewTimeMonthRepeat = findViewById(R.id.add_personal_todo_tv_month_repeat);

        mTextViewDate = findViewById(R.id.add_personal_todo_tv_date);
        mTextViewTime = findViewById(R.id.add_personal_todo_tv_time);

        mTextViewSun = findViewById(R.id.week_repeat_tv_sun);
        mTextViewMon = findViewById(R.id.week_repeat_tv_mon);
        mTextViewTue = findViewById(R.id.week_repeat_tv_tue);
        mTextViewWed = findViewById(R.id.week_repeat_tv_wed);
        mTextViewThu = findViewById(R.id.week_repeat_tv_thu);
        mTextViewFri = findViewById(R.id.week_repeat_tv_fri);
        mTextViewSat = findViewById(R.id.week_repeat_tv_sat);

        mFrameHiddenTimeDate = findViewById(R.id.add_personal_todo_frame_hidden_time_date);
        mLinearHiddenTimeDate = findViewById(R.id.add_personal_todo_layout_hidden_time_date);
        mLinearHiddenTimeTime = findViewById(R.id.add_personal_todo_layout_hidden_time_time);
        mLinearHiddenTimeWeekRepeat = findViewById(R.id.add_personal_todo_layout_hidden_time_date_week_repeat);

        mLinearHiddenTime = findViewById(R.id.add_personal_todo_layout_hidden_time);
        mLinearHiddenGps = findViewById(R.id.add_personal_todo_layout_hidden_gps);
        mTextViewLocation = findViewById(R.id.add_personal_todo_tv_location);

        mSwitchTime = findViewById(R.id.add_personal_todo_switch_time);
        mSwitchGps = findViewById(R.id.add_personal_todo_switch_gps);

        mEditTextTitle = findViewById(R.id.add_personal_todo_et_title);
        mEditTextMemo = findViewById(R.id.add_personal_todo_et_memo);

        mTextViewStart = findViewById(R.id.add_personal_todo_layout_hidden_gps_start);
        mTextViewArrive = findViewById(R.id.add_personal_todo_layout_hidden_gps_arrive);
        mTextViewNear = findViewById(R.id.add_personal_todo_layout_hidden_gps_near);

        mTextViewAlways = findViewById(R.id.add_personal_todo_btn_always);
        mTextViewMorning = findViewById(R.id.add_personal_todo_btn_morning);
        mTextViewEvening = findViewById(R.id.add_personal_todo_btn_evening);
        mTextViewNight = findViewById(R.id.add_personal_todo_btn_night);

        mRecyclerViewMyPlace = findViewById(R.id.add_personal_todo_rv_like);
        mDoneBtn = findViewById(R.id.add_personal_todo_btn_done);
        mImportantSwitch = findViewById(R.id.add_personal_todo_switch_important);
        mImportantSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mImportantMode = 'Y';
                } else {
                    mImportantMode = 'N';
                }
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerViewMyPlace.setLayoutManager(linearLayoutManager);
        mMyPlaceListAdapter = new MyPlaceListAdapter(mContext, mDataArrayList, new MyPlaceListAdapter.ItemClickListener() {
            @Override
            public void itemClick(int pos) {
                for (int i = 0; i < mDataArrayList.size(); i++) {
                    mDataArrayList.get(i).setSelected(false);
                }
                if (pos != mDataArrayList.size() - 1) {
                    mDataArrayList.get(pos).setSelected(true);
                } else {
                    // 즐겨찾기 장소 추가화면
                }
                mMyPlaceListAdapter.notifyDataSetChanged();
            }
        });
        mRecyclerViewMyPlace.setAdapter(mMyPlaceListAdapter);

        mTodoCategory = NONE;
        mRepeatType = ONE_DAY;
        mIsLocationSelected = false;
        mLocationMode = AT_ARRIVE;
        mLocationTime = ALWAYS;
//        mWifiMode = 'Y';
        mLadius = GPS_LADIUS;

        mTextViewSun.setSelected(true);
        selectedDayList[0] = true;

        /* Set Constant */
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        dpUnit = displayMetrics.density;

        setDatabase();
    }

    private void setDatabase() {
        mDatabase = AppDatabase.getAppDatabase(this);
    }

    private void insertToRoomDB() {
        ToDo todo = makeTodoObject();
        ToDoData toDoData = makeTodoDataObject();
        switch (mLocationMode) {
            case NONE:
                break;
            case TIME:
                break;
            case LOCATION:
                break;
        }
        new AddPersonalToDoActivity.InsertAsyncTask(mDatabase.todoDao()).execute(todo, toDoData);
    }

    //비동기처리                                   //넘겨줄객체, 중간에 처리할 데이터, 결과물(return)
    private class InsertAsyncTask extends AsyncTask<Object, Void, ToDoData> {
        private ToDoDao mTodoDao;

        InsertAsyncTask(ToDoDao mTodoDao) {
            this.mTodoDao = mTodoDao;
        }

        @Override
        protected ToDoData doInBackground(Object... toDos) {
            mToDoNo = mTodoDao.insertTodo((ToDo) toDos[0], (ToDoData) toDos[1]);
            Log.d("추가된 todoNo", " = " + mToDoNo);
            return (ToDoData) toDos[1];
//            if (((ToDo) toDos[0]).getType() == LOCATION) { //위치기반 일정
//                if (((ToDoData) toDos[1]).getIsWiFi() == 'Y') {
//                    return (ToDoData) toDos[1];
//                } else {
//                    ToDoData toDoData = (ToDoData) toDos[1];
//                }
//            } else if (((ToDo) toDos[0]).getType() == TIME) {
//                return (ToDoData) toDos[1];
//            }
//
//            return null;
        }

        @Override
        protected void onPostExecute(ToDoData toDoData) {
            super.onPostExecute(toDoData);
            if (toDoData == null) {
                return;
            }
            if (toDoData.getIsWiFi() == 'Y') {//와이파이
                getWifiMaker().registerAndUpdateWifi(mContext, mWifiMode, mLocationMode, mWifiConnected);
                finish();
//                registerWifi();
            } else if (toDoData.getLongitude() == NO_DATA && toDoData.getRepeatType() != NO_DATA) {//시간일정
                changeRepeatDayOfWeek();
                getAlarmMaker().registerAlarm(toDoData.getTodoNo(), mRepeatType, mYear, mMonth, mDay, mHour, mMinute, mEditTextTitle.getText().toString(), mEditTextMemo.getText().toString(), mRepeatDayOfWeek);
                finish();
            } else if (toDoData.getRepeatType() == NO_DATA && toDoData.getLongitude() != NO_DATA) {//위치일정
                getGeofenceMaker().addGeoFenceOne(mToDoNo, toDoData.getLatitude(), toDoData.getLongitude(), toDoData.getLocationMode(), toDoData.getRadius(),
                        new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                showCustomToast("할 일이 추가되었습니다");
                                finish();
                            }
                        },
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showCustomToast("추가할 수 없습니다. 다시 시도해주세요");
                                Log.e("지오펜스 등록 실패", e.toString());
                                //지오펜스 실패하면 db에사도 지워줘야함
                            }
                        });
            }
        }
    }

    private void updateToRoomDB() {
        ToDo todo = makeTodoObject();
        ToDoData toDoData = makeTodoDataObject();
        todo.setTodoNo(mToDoNo);
        toDoData.setTodoNo(mToDoNo);
        toDoData.setTodoDataNo(mToDoDataNo);
        System.out.println("넘버: " + mToDoNo + ", " + mToDoDataNo + ", " + todo.getTitle());
        new updateAsyncTask(mDatabase.todoDao()).execute(todo, toDoData);
    }

    //비동기처리                                   //넘겨줄객체, 중간에 처리할 데이터, 결과물(return)
    private class updateAsyncTask extends AsyncTask<Object, Void, ToDoData> {
        private ToDoDao mTodoDao;

        updateAsyncTask(ToDoDao mTodoDao) {
            this.mTodoDao = mTodoDao;
        }

        @Override
        protected ToDoData doInBackground(Object... objects) {
            mTodoDao.updateTodo((ToDo) objects[0], (ToDoData) objects[1]);
            System.out.println(objects[0].toString());
            return (ToDoData) objects[1];
        }

        @Override
        protected void onPostExecute(ToDoData toDoData) {
            //수정로직 돌리기
            super.onPostExecute(toDoData);
            if (toDoData == null) {
                return;
            }
            //기존 지오펜스 삭제처리
            getGeofenceMaker().removeGeofence(String.valueOf(toDoData.getTodoNo()));
            // 기존 알람 삭제처리
            getAlarmMaker().removeAlarm(toDoData.getTodoNo());

            if (toDoData.getIsWiFi() == 'Y') {//와이파이
                getWifiMaker().registerAndUpdateWifi(mContext, mWifiMode, mLocationMode, mWifiConnected);
                finish();
//                registerWifi();
            } else if (toDoData.getLongitude() == NO_DATA && toDoData.getRepeatType() != NO_DATA) {//시간일정
                changeRepeatDayOfWeek();
                getAlarmMaker().registerAlarm(toDoData.getTodoNo(), mRepeatType, mYear, mMonth, mDay, mHour, mMinute, mEditTextTitle.getText().toString(), mEditTextMemo.getText().toString(), mRepeatDayOfWeek);
                finish();
            } else if (toDoData.getRepeatType() == NO_DATA && toDoData.getLongitude() != NO_DATA) {//위치일정
                getGeofenceMaker().addGeoFenceOne(mToDoNo, toDoData.getLatitude(), toDoData.getLongitude(), toDoData.getLocationMode(), toDoData.getRadius(),
                        new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                showCustomToast("수정이 완료되었습니다.");
                                finish();
                            }
                        },
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showCustomToast("수정 할 수 없습니다. 다시 시도해주세요");
                                Log.e("지오펜스 등록 실패", e.toString());
                                //지오펜스 실패하면 db에사도 지워줘야함
                            }
                        });
            }
        }
    }


    private void showTimeLayout() {
        ValueAnimator anim1 = ValueAnimator.ofInt(0, 170 * (int) dpUnit);
        anim1.setDuration(500);
        anim1.setRepeatMode(ValueAnimator.REVERSE);
        anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Integer value = (Integer) valueAnimator.getAnimatedValue();
                mLinearHiddenTime.getLayoutParams().height = value.intValue();
                mLinearHiddenTime.requestLayout();
            }
        });
        anim1.start();
    }

    private void hideTimeLayout() {
        ValueAnimator anim1 = ValueAnimator.ofInt(170 * (int) dpUnit, 0);
        anim1.setDuration(500); // duration 5 seconds
        anim1.setRepeatMode(ValueAnimator.REVERSE);
        anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                mLinearHiddenTime.getLayoutParams().height = value.intValue();
                mLinearHiddenTime.requestLayout();
            }
        });
        anim1.start();
    }

    private void showGpsLayout() {
        ValueAnimator anim1 = ValueAnimator.ofInt(0, 260 * (int) dpUnit);
        anim1.setDuration(500);
        anim1.setRepeatMode(ValueAnimator.REVERSE);
        anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Integer value = (Integer) valueAnimator.getAnimatedValue();
                mLinearHiddenGps.getLayoutParams().height = value.intValue();
                mLinearHiddenGps.requestLayout();
            }
        });
        anim1.start();
        System.out.println(mDataArrayList.size());
        mMyPlaceListAdapter.notifyDataSetChanged();
    }

    private void hideGpsLayout() {
        ValueAnimator anim1 = ValueAnimator.ofInt(250 * (int) dpUnit, 0);
        anim1.setDuration(500); // duration 5 seconds
        anim1.setRepeatMode(ValueAnimator.REVERSE);
        anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                mLinearHiddenGps.getLayoutParams().height = value.intValue();
                mLinearHiddenGps.requestLayout();
            }
        });
        anim1.start();
    }

    public void customOnClick(View v) {
        switch (v.getId()) {
            case R.id.add_personal_todo_tv_no_repeat:
                setRepeatTimeView(mTextViewTimeNoRepeat);
                mFrameHiddenTimeDate.setVisibility(View.VISIBLE);
                mLinearHiddenTimeDate.setVisibility(View.VISIBLE);
                mLinearHiddenTimeWeekRepeat.setVisibility(View.GONE);
                mRepeatType = ONE_DAY;
                break;
            case R.id.add_personal_todo_tv_day_repeat:
                setRepeatTimeView(mTextViewTimeDayRepeat);
                mFrameHiddenTimeDate.setVisibility(View.GONE);
                mLinearHiddenTimeDate.setVisibility(View.VISIBLE);
                mLinearHiddenTimeWeekRepeat.setVisibility(View.GONE);
                mRepeatType = ALL_DAY;
                break;
            case R.id.add_personal_todo_tv_week_repeat:
                setRepeatTimeView(mTextViewTimeWeekRepeat);
                mFrameHiddenTimeDate.setVisibility(View.VISIBLE);
                mLinearHiddenTimeDate.setVisibility(View.GONE);
                mLinearHiddenTimeWeekRepeat.setVisibility(View.VISIBLE);
                mRepeatType = WEEK_DAY;
                break;
            case R.id.add_personal_todo_tv_month_repeat:
                setRepeatTimeView(mTextViewTimeMonthRepeat);
                mFrameHiddenTimeDate.setVisibility(View.VISIBLE);
                mLinearHiddenTimeDate.setVisibility(View.VISIBLE);
                mLinearHiddenTimeWeekRepeat.setVisibility(View.GONE);
                mRepeatType = MONTH_DAY;
                break;
            case R.id.week_repeat_tv_sun:
                setRepeatWeekView(mTextViewSun);
                break;
            case R.id.week_repeat_tv_mon:
                setRepeatWeekView(mTextViewMon);
                break;
            case R.id.week_repeat_tv_tue:
                setRepeatWeekView(mTextViewTue);
                break;
            case R.id.week_repeat_tv_wed:
                setRepeatWeekView(mTextViewWed);
                break;
            case R.id.week_repeat_tv_thu:
                setRepeatWeekView(mTextViewThu);
                break;
            case R.id.week_repeat_tv_fri:
                setRepeatWeekView(mTextViewFri);
                break;
            case R.id.week_repeat_tv_sat:
                setRepeatWeekView(mTextViewSat);
                break;
            case R.id.add_personal_todo_switch_time:
                if (isSwitchTime) { // expand 상태
                    mTodoCategory = NONE;
                    hideTimeLayout();
                    isSwitchTime = false;
                } else { // not expand 상태
                    mTodoCategory = TIME;
                    showTimeLayout();
                    isSwitchTime = true;
                    if (isSwitchGps) {
                        hideGpsLayout();
                        isSwitchGps = false;
                        mSwitchGps.setChecked(false);
                    }
                }
                break;
            case R.id.add_personal_todo_switch_gps:
                if (isSwitchGps) {
                    mTodoCategory = NONE;
                    hideGpsLayout();
                    isSwitchGps = false;
                } else {
                    mTodoCategory = LOCATION;
                    showGpsLayout();
                    isSwitchGps = true;
                    if (isSwitchTime) {
                        hideTimeLayout();
                        isSwitchTime = false;
                        mSwitchTime.setChecked(false);
                    }
                }
                break;

            case R.id.add_personal_todo_layout_hidden_gps_start:
                mLocationMode = AT_START;
                setLocationModeView(mTextViewStart);
                break;
            case R.id.add_personal_todo_layout_hidden_gps_arrive:
                mLocationMode = AT_ARRIVE;
                setLocationModeView(mTextViewArrive);
                break;
            case R.id.add_personal_todo_layout_hidden_gps_near:
                mLocationMode = AT_NEAR;
                setLocationModeView(mTextViewNear);
                break;

            case R.id.add_personal_todo_btn_always:
                mLocationTime = ALWAYS;
                setLocationTimeView(mTextViewAlways);
                break;
            case R.id.add_personal_todo_btn_morning:
                mLocationTime = MORNING;
                setLocationTimeView(mTextViewMorning);
                break;
            case R.id.add_personal_todo_btn_evening:
                mLocationTime = EVENING;
                setLocationTimeView(mTextViewEvening);
                break;
            case R.id.add_personal_todo_btn_night:
                mLocationTime = NIGHT;
                setLocationTimeView(mTextViewNight);
                break;

            case R.id.add_personal_todo_layout_gps:
                // 지도 선 화면
                Intent intent = new Intent(mContext, MapSelectActivity.class);
                if(mLocation != null){
                    intent.putExtra("location", mLocation);
                    intent.putExtra("isLocationSelected", true);
                }
                startActivityForResult(intent, 100);
                break;
            case R.id.add_personal_todo_layout_hidden_time_date:
                // 날짜선택
                setDate();
                break;
            case R.id.add_personal_todo_layout_hidden_time_time:
                //시간선택
                setTime();
                break;
            case R.id.add_personal_todo_btn_done:
                //추가버튼
                if (validateBeforeAdd()) {
                    if (isEditMode) {
                        updateToRoomDB();
                    } else {
                        insertToRoomDB();
                    }
                }
                break;
            case R.id.add_personal_todo_iv_icon: // 아이콘 선택버튼
                PersonalToDoIconDialog personalToDoIconDialog = new PersonalToDoIconDialog(mContext, new PersonalToDoIconDialog.SelectClickListener() {
                    @Override
                    public void selectClick(int iconNum) {
                        mIcon = iconNum;
                        changeIcon(mIcon);
                    }
                });
                personalToDoIconDialog.show();
                break;
        }
    }

    void changeIcon(int iconNum){
        switch (iconNum){
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

    void setDate() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                mYear = i;
                mMonth = i1 + 1;
                mDay = i2;
                mTextViewDate.setText(i + "년 " + (i1 + 1) + "월 " + i2 + "일");
                mTextViewDate.setTextColor(getResources().getColor(R.color.colorBlack));
                mIsDatePick = true;
            }
        };
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
        DatePickerDialog dialog = new DatePickerDialog(this, R.style.DatePickerTheme, dateSetListener, Integer.parseInt(yearFormat.format(currentTime)), Integer.parseInt(monthFormat.format(currentTime)) - 1, Integer.parseInt(dayFormat.format(currentTime)));
        dialog.show();
    }

    void setTime() {
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                mHour = i;
                mMinute = i1;
                mTextViewTime.setText(i + "시 " + i1 + "분");
                mTextViewTime.setTextColor(getResources().getColor(R.color.colorBlack));
                mIsTimePick = true;
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.DatePickerTheme, timeSetListener, 06, 00, true);
        timePickerDialog.show();
    }

    void setLocationModeView(TextView selectedView) {
        if (selectedView.equals(mTextViewStart)) {
            setVineOnMode(mTextViewStart);
            setVineOffMode(mTextViewArrive);
            setVineOffMode(mTextViewNear);
        } else if (selectedView.equals(mTextViewArrive)) {
            setVineOffMode(mTextViewStart);
            setVineOnMode(mTextViewArrive);
            setVineOffMode(mTextViewNear);
        } else {
            setVineOffMode(mTextViewStart);
            setVineOffMode(mTextViewArrive);
            setVineOnMode(mTextViewNear);
        }
    }

    void setLocationTimeView(TextView selectedView) {
        if (selectedView.equals(mTextViewAlways)) {
            setVineOnMode(mTextViewAlways);
            setVineOffMode(mTextViewMorning);
            setVineOffMode(mTextViewEvening);
            setVineOffMode(mTextViewNight);
        } else if (selectedView.equals(mTextViewMorning)) {
            setVineOffMode(mTextViewAlways);
            setVineOnMode(mTextViewMorning);
            setVineOffMode(mTextViewEvening);
            setVineOffMode(mTextViewNight);
        } else if (selectedView.equals(mTextViewEvening)) {
            setVineOffMode(mTextViewAlways);
            setVineOffMode(mTextViewMorning);
            setVineOnMode(mTextViewEvening);
            setVineOffMode(mTextViewNight);
        } else {
            setVineOffMode(mTextViewAlways);
            setVineOffMode(mTextViewMorning);
            setVineOffMode(mTextViewEvening);
            setVineOnMode(mTextViewNight);
        }
    }

    void setRepeatWeekView(TextView selectedView) {
        if(selectedView.isSelected()){
            selectedView.setSelected(false);
            setVineOffMode(selectedView);
            if (selectedView.equals(mTextViewSun)) {
                selectedDayList[0] = false;
            } else if (selectedView.equals(mTextViewMon)) {
                selectedDayList[1] = false;
            } else if (selectedView.equals(mTextViewTue)) {
                selectedDayList[2] = false;
            } else if (selectedView.equals(mTextViewWed)) {
                selectedDayList[3] = false;
            } else if (selectedView.equals(mTextViewThu)) {
                selectedDayList[4] = false;
            } else if (selectedView.equals(mTextViewFri)) {
                selectedDayList[5] = false;
            } else if (selectedView.equals(mTextViewSat)) {
                selectedDayList[6] = false;
            }
        } else {
            selectedView.setSelected(true);
            setVineOnMode(selectedView);
            if (selectedView.equals(mTextViewSun)) {
                selectedDayList[0] = true;
            } else if (selectedView.equals(mTextViewMon)) {
                selectedDayList[1] = true;
            } else if (selectedView.equals(mTextViewTue)) {
                selectedDayList[2] = true;
            } else if (selectedView.equals(mTextViewWed)) {
                selectedDayList[3] = true;
            } else if (selectedView.equals(mTextViewThu)) {
                selectedDayList[4] = true;
            } else if (selectedView.equals(mTextViewFri)) {
                selectedDayList[5] = true;
            } else if (selectedView.equals(mTextViewSat)) {
                selectedDayList[6] = true;
            }
        }
    }

    void setRepeatTimeView(TextView selectedView) {
        if (selectedView.equals(mTextViewTimeNoRepeat)) {
            setVineOnMode(mTextViewTimeNoRepeat);
            setVineOffMode(mTextViewTimeDayRepeat);
            setVineOffMode(mTextViewTimeWeekRepeat);
            setVineOffMode(mTextViewTimeMonthRepeat);
        } else if (selectedView.equals(mTextViewTimeDayRepeat)) {
            setVineOffMode(mTextViewTimeNoRepeat);
            setVineOnMode(mTextViewTimeDayRepeat);
            setVineOffMode(mTextViewTimeWeekRepeat);
            setVineOffMode(mTextViewTimeMonthRepeat);
        } else if (selectedView.equals(mTextViewTimeWeekRepeat)) {
            setVineOffMode(mTextViewTimeNoRepeat);
            setVineOffMode(mTextViewTimeDayRepeat);
            setVineOnMode(mTextViewTimeWeekRepeat);
            setVineOffMode(mTextViewTimeMonthRepeat);
        } else {
            setVineOffMode(mTextViewTimeNoRepeat);
            setVineOffMode(mTextViewTimeDayRepeat);
            setVineOffMode(mTextViewTimeWeekRepeat);
            setVineOnMode(mTextViewTimeMonthRepeat);
        }
    }

    void setVineOnMode(TextView textView) {
        textView.setBackgroundResource(R.drawable.bg_round_button_on);
        textView.setTextColor(Color.parseColor("#ffffff"));
    }

    void setVineOffMode(TextView textView) {
        textView.setBackgroundResource(R.drawable.bg_round_button_off);
        textView.setTextColor(Color.parseColor("#657884"));
    }

    boolean validateBeforeAdd() {
        if (mIcon==-1){
            showSnackBar(mEditTextTitle, "아이콘을 선택해주세요.");
        }
        if (mEditTextTitle.getText().length() < 1) {
            showSnackBar(mEditTextTitle, "내용을 입력해 주세요");
            return false;
        }
        if (mTodoCategory == LOCATION) {
            resetTimeInfo();
            if (!mIsLocationSelected) {
                showSnackBar(mEditTextTitle, "장소를 선택해 주세요");
                return false;
            }
        } else if (mTodoCategory == TIME) {
            resetLocationInfo();
            if (mRepeatType == ONE_DAY) {
                if (!mIsDatePick || !mIsTimePick) {
                    showSnackBar(mEditTextTitle, "시간을 선택해 주세요");
                    return false;
                }
            } else if (mRepeatType == ALL_DAY) {
                if (!mIsTimePick) {
                    showSnackBar(mEditTextTitle, "시간을 선택해 주세요");
                    return false;
                }

            } else if (mRepeatType == WEEK_DAY) {
                if (!mIsTimePick) {
                    showSnackBar(mEditTextTitle, "시간을 선택해 주세요");
                    return false;
                }
            } else if (mRepeatType == MONTH_DAY) {

            }
        } else {
            showSnackBar(mEditTextTitle, "일정 정보를 입력해 주세요");
            return false;
        }
        return true;
    }

    void setTempLikeLocationData() {
        mDataArrayList.add(new MyPlace("집", "청라1동", false, false));
        mDataArrayList.add(new MyPlace("회사", "청라1동", false, false));
        mDataArrayList.add(new MyPlace("사무실", "청라1동", false, false));
        //마지막 +
        mDataArrayList.add(new MyPlace("+", "청라1동", false, true));
        mMyPlaceListAdapter.notifyDataSetChanged();
    }

    void resetLocationInfo() {
        mIsLocationSelected = false;
        mWifiMode = 'N';
    }

    void resetTimeInfo() {
        mRepeatType = ONE_DAY;
    }

    void setLocationInfo() {
        mIsLocationSelected = true;
        mTextViewLocation.setTextColor(getResources().getColor(R.color.colorBlack));
        mTextViewLocation.setText(mLocation.getPlaceName());
        mTextViewNear.setVisibility(View.VISIBLE);
    }

    void setWifiInfo(String ssid) {
        mIsLocationSelected = true;
        mWifiMode = 'Y';
        mTextViewLocation.setTextColor(getResources().getColor(R.color.colorBlack));
        mTextViewLocation.setText(ssid);
        setLocationModeView(mTextViewArrive);
        mLocationMode = AT_ARRIVE;
        mTextViewNear.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (resultCode == 100) {
                //성공적으로 location  받음
                mLocation = (LocationResponse.Location) Objects.requireNonNull(data.getSerializableExtra("location"));
                longitude = Double.parseDouble(mLocation.getLongitude());
                latitude = Double.parseDouble(mLocation.getLatitude());
                setLocationInfo();
            } else if (resultCode == 111) {
                mWifiBssid = data.getStringExtra("bssid");
                longitude = data.getDoubleExtra("longitude", 0);
                latitude = data.getDoubleExtra("latitude", 0);
                mWifiConnected = data.getBooleanExtra("connected", false);
                setWifiInfo(data.getStringExtra("ssid"));
            }
        }
    }

//    void registerAlarm() {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        if (mRepeatType == ONE_DAY) {
//            calendar.set(Calendar.YEAR, mYear);
//            switch (mMonth) {
//                case 1:
//                    calendar.set(Calendar.MONTH, Calendar.JANUARY);
//                    break;
//                case 2:
//                    calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
//                    break;
//                case 3:
//                    calendar.set(Calendar.MONTH, Calendar.MARCH);
//                    break;
//                case 4:
//                    calendar.set(Calendar.MONTH, Calendar.APRIL);
//                    break;
//                case 5:
//                    calendar.set(Calendar.MONTH, Calendar.MAY);
//                    break;
//                case 6:
//                    calendar.set(Calendar.MONTH, Calendar.JUNE);
//                    break;
//                case 7:
//                    calendar.set(Calendar.MONTH, Calendar.JULY);
//                    break;
//                case 8:
//                    calendar.set(Calendar.MONTH, Calendar.AUGUST);
//                    break;
//                case 9:
//                    calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
//                    break;
//                case 10:
//                    calendar.set(Calendar.MONTH, Calendar.OCTOBER);
//                    break;
//                case 11:
//                    calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
//                    break;
//                case 12:
//                    calendar.set(Calendar.MONTH, Calendar.DECEMBER);
//                    break;
//
//            }
//            calendar.set(Calendar.DATE, mDay);
//            calendar.set(Calendar.HOUR_OF_DAY, mHour);
//            calendar.set(Calendar.MINUTE, mMinute);
//            calendar.set(Calendar.SECOND, 0);
//            calendar.set(Calendar.MILLISECOND, 0);
//
//            if (calendar.before(Calendar.getInstance())) {
//                calendar.add(Calendar.DATE, 1);
//            }
//
//            mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//            System.out.println("특정 알람 시간: " + calendar.getTime().toString() + ", " + calendar.getTimeInMillis());
//            PackageManager pm = this.getPackageManager();
//            ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);
//
//            Intent intent = new Intent(AddPersonalToDoActivity.this, AlarmBroadcastReceiver.class);
//            intent.putExtra("repeatType", 4);
//            intent.putExtra("alarmIndex", mToDoNo);
//            intent.putExtra("title", mEditTextTitle.getText().toString());
//            intent.putExtra("memo", mEditTextMemo.getText().toString());
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, mToDoNo, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//            if (mAlarmManager != null) {
//                mAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//            }
//
//            //부팅후 재실행
//            pm.setComponentEnabledSetting(receiver,
//                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                    PackageManager.DONT_KILL_APP);
//
//        } else if (mRepeatType == ALL_DAY) {
//            calendar.set(Calendar.HOUR_OF_DAY, mHour);
//            calendar.set(Calendar.MINUTE, mMinute);
//            calendar.set(Calendar.SECOND, 0);
//            calendar.set(Calendar.MILLISECOND, 0);
//
//            if (calendar.before(Calendar.getInstance())) {
//                calendar.add(Calendar.DATE, 1);
//            }
//
//            mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//            System.out.println("알람 시간: " + calendar.getTime().toString() + ", " + calendar.getTimeInMillis());
//            PackageManager pm = this.getPackageManager();
//            ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);
//
//            Intent intent = new Intent(AddPersonalToDoActivity.this, AlarmBroadcastReceiver.class);
//            intent.putExtra("repeatType", 1);
//            intent.putExtra("alarmIndex", mToDoNo);
//            intent.putExtra("title", mEditTextTitle.getText().toString());
//            intent.putExtra("memo", mEditTextMemo.getText().toString());
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, mToDoNo, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//            if (mAlarmManager != null) {
////                mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
//                mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
//            }
//
//            //부팅후 재실행
//            pm.setComponentEnabledSetting(receiver,
//                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                    PackageManager.DONT_KILL_APP);
//
//        } else if (mRepeatType == WEEK_DAY) {
//            calendar.set(Calendar.HOUR_OF_DAY, mHour);
//            calendar.set(Calendar.MINUTE, mMinute);
//            calendar.set(Calendar.SECOND, 0);
//            calendar.set(Calendar.MILLISECOND, 0);
//
//            if (calendar.before(Calendar.getInstance())) {
//                calendar.add(Calendar.DATE, 1);
//            }
//            System.out.println(mRepeatDayOfWeek);
//
//            mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//            System.out.println("알람 시간: " + calendar.getTime().toString() + ", " + calendar.getTimeInMillis());
//            PackageManager pm = this.getPackageManager();
//            ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);
//
//            Intent intent = new Intent(AddPersonalToDoActivity.this, AlarmBroadcastReceiver.class);
//            intent.putExtra("repeatType", 2);
//            intent.putExtra("repeatDayOfWeek", mRepeatDayOfWeek);
//            intent.putExtra("alarmIndex", mToDoNo);
//            intent.putExtra("title", mEditTextTitle.getText().toString());
//            intent.putExtra("memo", mEditTextMemo.getText().toString());
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, mToDoNo, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//            if (mAlarmManager != null) {
//                mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
////                mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
//            }
//
//            //부팅후 재실행
//            pm.setComponentEnabledSetting(receiver,
//                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                    PackageManager.DONT_KILL_APP);
//
//        } else if (mRepeatType == MONTH_DAY) {
//
//        }
//    }

    private void changeRepeatDayOfWeek(){
        for(int i=0; i<7; i++){
            if(selectedDayList[i]){
                switch (i){
                    case 0:
                        mRepeatDayOfWeek += "일,";
                        break;
                    case 1:
                        mRepeatDayOfWeek += "월,";
                        break;
                    case 2:
                        mRepeatDayOfWeek += "화,";
                        break;
                    case 3:
                        mRepeatDayOfWeek += "수,";
                        break;
                    case 4:
                        mRepeatDayOfWeek += "목,";
                        break;
                    case 5:
                        mRepeatDayOfWeek += "금,";
                        break;
                    case 6:
                        mRepeatDayOfWeek += "토,";
                        break;
                }
            }
        }

        mRepeatDayOfWeek = mRepeatDayOfWeek.substring(0, mRepeatDayOfWeek.length()-1);
    }


    private ToDo makeTodoObject() {
        ToDo todo = new ToDo(mEditTextTitle.getText().toString(), mEditTextMemo.getText().toString(), mIcon, mTodoCategory, mImportantMode, 'N', 0);
//        todo.setTodoNo(mToDoNo);
        return todo;
    }

    private ToDoData makeTodoDataObject() {

        if (mTodoCategory == LOCATION) {
            return new ToDoData(mTextViewLocation.getText().toString(),
                    latitude, longitude, mLocationMode, mLadius,
                    mWifiBssid, mWifiMode, mLocationTime, NO_DATA, "", NO_DATA, "", "", NO_DATA, NO_DATA, NO_DATA, NO_DATA,NO_DATA);
        } else if (mTodoCategory == TIME) {
            return new ToDoData(mTextViewLocation.getText().toString(),
                    NO_DATA, NO_DATA, NO_DATA, NO_DATA,
                    "", 'N', NO_DATA, mRepeatType, mRepeatDayOfWeek, mRepeatDay, mTextViewDate.getText().toString(), mTextViewTime.getText().toString(), mYear, mMonth, mDay, mHour, mMinute);
        } else {
            return new ToDoData(mTextViewLocation.getText().toString(),
                    NO_DATA, NO_DATA, NO_DATA, NO_DATA,
                    "", 'N', NO_DATA, NO_DATA, "", NO_DATA, "", "",  NO_DATA, NO_DATA, NO_DATA, NO_DATA,NO_DATA);
        }
    }
}
