package com.ninano.weto.src.todo_add;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.ValueAnimator;
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
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ninano.weto.R;
import com.ninano.weto.db.AppDatabase;
import com.ninano.weto.db.FavoriteLocation;
import com.ninano.weto.db.ToDo;
import com.ninano.weto.db.ToDoDao;
import com.ninano.weto.db.ToDoData;
import com.ninano.weto.src.BaseActivity;
import com.ninano.weto.src.DeviceBootReceiver;
import com.ninano.weto.src.map_select.MapSelectActivity;
import com.ninano.weto.src.map_select.keyword_search.models.LocationResponse;
import com.ninano.weto.src.common.Alarm.AlarmBroadcastReceiver;
import com.ninano.weto.src.common.Geofence.GeofenceBroadcastReceiver;
import com.ninano.weto.src.todo_add.adpater.AddGroupToDoMemberAdapter;
//import com.ninano.weto.src.todo_add.adpater.LIkeLocationListAdapter;
import com.ninano.weto.src.todo_add.adpater.FavoritePlaceAdapter;
import com.ninano.weto.src.todo_add.models.AddGroupToDoMemberData;
//import com.ninano.weto.src.todo_add.models.LikeLocationData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_DWELL;
import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER;
import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT;
import static com.ninano.weto.src.ApplicationClass.ALWAYS;
import static com.ninano.weto.src.ApplicationClass.AT_ARRIVE;
import static com.ninano.weto.src.ApplicationClass.AT_NEAR;
import static com.ninano.weto.src.ApplicationClass.AT_START;
import static com.ninano.weto.src.ApplicationClass.COMPANY_SELECT_MODE;
import static com.ninano.weto.src.ApplicationClass.DAYREPEAT;
import static com.ninano.weto.src.ApplicationClass.DAY_FORMAT;
import static com.ninano.weto.src.ApplicationClass.EVENING;
import static com.ninano.weto.src.ApplicationClass.HOME_SELECT_MODE;
import static com.ninano.weto.src.ApplicationClass.LOCATION;
import static com.ninano.weto.src.ApplicationClass.MINUTE_FORMAT;
import static com.ninano.weto.src.ApplicationClass.MONTHREPEAT;
import static com.ninano.weto.src.ApplicationClass.MONTH_FORMAT;
import static com.ninano.weto.src.ApplicationClass.MORNING;
import static com.ninano.weto.src.ApplicationClass.NIGHT;
import static com.ninano.weto.src.ApplicationClass.NONE;
import static com.ninano.weto.src.ApplicationClass.NOREPEAT;
import static com.ninano.weto.src.ApplicationClass.NO_DATA;
import static com.ninano.weto.src.ApplicationClass.SCHOOL_SELECT_MODE;
import static com.ninano.weto.src.ApplicationClass.TIME;
import static com.ninano.weto.src.ApplicationClass.TIME_FORMAT;
import static com.ninano.weto.src.ApplicationClass.WEEKREPEAT;
import static com.ninano.weto.src.ApplicationClass.YEAR_FORMAT;
import static com.ninano.weto.src.common.Geofence.GeofenceMaker.getGeofenceMaker;

public class AddGroupToDoActivity extends BaseActivity {

    private Context mContext;

    //공통
    private TextView mTextViewTitle;
    private EditText mEditTextTitle, mEditTextMemo;
    private ImageView mImageViewIcon;
    private int mIcon = -1; // 아이콘 값은 그룹 생성시의 아이콘으로 가져온다.
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
    private int mYear = 0, mMonth = 0, mDay = 0, mHour = 0, mMinute = 0;
    private String mRepeatDayOfWeek = "";
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
    private int mFavoriteSelectedIndex = -1;

    private float density;

    private RecyclerView mRecyclerViewMyPlace;
    private FavoritePlaceAdapter mFavoritePlaceAdapter;
    private ArrayList<FavoriteLocation> mFavoritePlaceList = new ArrayList<>();
    private LocationResponse.Location mLocation = null;

    private RecyclerView mRecyclerViewMember;
    private AddGroupToDoMemberAdapter mAddGroupToDoMemberAdapter;
    private ArrayList<AddGroupToDoMemberData> mData = new ArrayList<>();
    //
    private GeofencingClient geofencingClient;

    public static float dpUnit;             // dp단위 값
    ArrayList<Geofence> geofenceList = new ArrayList<>();
    AppDatabase mDatabase;

    private int mRepeatMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_to_do);
        mContext = this;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        density = displayMetrics.density;
        init();
        initGeoFence();
    }

    void init() {
        mImageViewIcon = findViewById(R.id.add_group_todo_iv_icon);

        mTextViewTimeNoRepeat = findViewById(R.id.add_group_todo_tv_no_repeat);
//        isSelectedNoRepeat = true;
        mTextViewTimeDayRepeat = findViewById(R.id.add_group_todo_tv_day_repeat);
        mTextViewTimeWeekRepeat = findViewById(R.id.add_group_todo_tv_week_repeat);
        mTextViewTimeMonthRepeat = findViewById(R.id.add_group_todo_tv_month_repeat);

        mTextViewDate = findViewById(R.id.add_group_todo_tv_date);
        mTextViewTime = findViewById(R.id.add_group_todo_tv_time);

        mLinearHiddenTime = findViewById(R.id.add_group_todo_layout_hidden_time);
        mLinearHiddenGps = findViewById(R.id.add_group_todo_layout_hidden_gps);
        mTextViewLocation = findViewById(R.id.add_group_todo_tv_location);

        mSwitchTime = findViewById(R.id.add_group_todo_switch_time);
        mSwitchGps = findViewById(R.id.add_group_todo_switch_gps);

        mEditTextTitle = findViewById(R.id.add_group_todo_et_title);
        mEditTextMemo = findViewById(R.id.add_group_todo_et_memo);

        mTextViewStart = findViewById(R.id.add_group_todo_layout_hidden_gps_start);
        mTextViewArrive = findViewById(R.id.add_group_todo_layout_hidden_gps_arrive);
        mTextViewNear = findViewById(R.id.add_group_todo_layout_hidden_gps_near);

        mTextViewAlways = findViewById(R.id.add_group_todo_btn_always);
        mTextViewMorning = findViewById(R.id.add_group_todo_btn_morning);
        mTextViewEvening = findViewById(R.id.add_group_todo_btn_evening);
        mTextViewNight = findViewById(R.id.add_group_todo_btn_night);

        mRecyclerViewMember = findViewById(R.id.add_group_todo_rv_member);
        mAddGroupToDoMemberAdapter = new AddGroupToDoMemberAdapter(mContext, mData, density, new AddGroupToDoMemberAdapter.ItemClickListener() {
            @Override
            public void itemClick(int pos) {

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerViewMember.setLayoutManager(linearLayoutManager);
        mRecyclerViewMember.setAdapter(mAddGroupToDoMemberAdapter);

        mRecyclerViewMyPlace = findViewById(R.id.add_group_todo_rv_like);
        LinearLayoutManager myPlaceLinearLayoutManager = new LinearLayoutManager(this);
        myPlaceLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerViewMyPlace.setLayoutManager(myPlaceLinearLayoutManager);
        mFavoritePlaceAdapter = new FavoritePlaceAdapter(mContext, mFavoritePlaceList, new FavoritePlaceAdapter.ItemClickListener() {
            @Override
            public void itemClick(int pos) {
                if (pos != mFavoritePlaceList.size() - 1) {
                    if (mFavoritePlaceList.get(pos).isConfirmed()) {
                        //집,회사,학교 / 기타 가 이미 입력되있음
                        setFavoritePlaceItem(pos);
                    } else {
                        //집,회사,학교 입력안받음
                        Intent intent = new Intent(mContext, MapSelectActivity.class);
                        intent.putExtra("isFavoritePlaceMode", true);
                        if (mFavoritePlaceList.get(pos).getName().equals("집")) {
                            intent.putExtra("homeMode", true);
                            startActivityForResult(intent, HOME_SELECT_MODE);
                        } else if (mFavoritePlaceList.get(pos).getName().equals("회사")) {
                            intent.putExtra("companyMode", true);
                            startActivityForResult(intent, COMPANY_SELECT_MODE);
                        } else if (mFavoritePlaceList.get(pos).getName().equals("학교")) {
                            intent.putExtra("schoolMode", true);
                            startActivityForResult(intent, SCHOOL_SELECT_MODE);
                        }
                    }
                } else {
                    // 즐겨찾기 장소 추가화면
                    Intent intent = new Intent(mContext, MapSelectActivity.class);
                    intent.putExtra("isFavoritePlaceMode", true);
                    startActivityForResult(intent, 200);
                }
                mFavoritePlaceAdapter.notifyDataSetChanged();
            }
        });


        mRecyclerViewMyPlace.setAdapter(mFavoritePlaceAdapter);

        mTodoCategory = NONE;
        mIsLocationSelected = false;
        mLocationMode = AT_ARRIVE;
        mLocationTime = AT_START;
//        mWifiMode = 'Y';
        mLadius = 300;

        /* Set Constant */
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        dpUnit = displayMetrics.density;

        setDatabase();
    }

    private void setFavoritePlaceItem(int index) {
        if (index == NO_DATA) {
            return;
        }
        if (!mFavoritePlaceList.get(index).isConfirmed()) {
            return;
        }
        for (int i = 0; i < mFavoritePlaceList.size(); i++) {
            mFavoritePlaceList.get(i).setSelected(false);
        }
        mFavoritePlaceList.get(index).setSelected(true);
        mLocation = new LocationResponse.Location("", mFavoritePlaceList.get(index).getName(), String.valueOf(mFavoritePlaceList.get(index).getLongitude()), String.valueOf(mFavoritePlaceList.get(index).getLatitude()));
        longitude = Double.valueOf(mLocation.getLongitude());
        latitude = Double.valueOf(mLocation.getLatitude());
        setLocationInfo();

        //즐겨찾는 장소가 와이파이일 경우 처리해야함
        if (mFavoritePlaceList.get(index).isWiFi()) {
            mWifiMode = 'Y';
            mWifiBssid = mFavoritePlaceList.get(index).getSsid();
            setWifiInfo(mFavoritePlaceList.get(index).getWifiName());
        } else {
            mWifiMode = 'N';
            mTextViewNear.setVisibility(View.VISIBLE);
        }
    }

    private void setDatabase() {
        mDatabase = AppDatabase.getAppDatabase(this);

        mDatabase.todoDao().getFavoriteLocation().observe(this, new Observer<List<FavoriteLocation>>() {
            @Override
            public void onChanged(List<FavoriteLocation> favoriteLocations) {
                mFavoritePlaceList.clear();
                mFavoritePlaceList.addAll(favoriteLocations);
                if (mFavoritePlaceList.size() == 0) {
                    new FavoritePlaceInsertAsyncTask().execute(new FavoriteLocation("집"));
                    new FavoritePlaceInsertAsyncTask().execute(new FavoriteLocation("학교"));
                    new FavoritePlaceInsertAsyncTask().execute(new FavoriteLocation("회사"));
                }
                if (mFavoriteSelectedIndex != -1) {
                    setFavoritePlaceItem(mFavoriteSelectedIndex);
                }
                mFavoritePlaceList.add(new FavoriteLocation());
                mFavoritePlaceAdapter.notifyDataSetChanged();
            }
        });
    }

    void initGeoFence() {
        geofencingClient = LocationServices.getGeofencingClient(this);
    }

    private Geofence getGeofence(int type, String reqId, Pair<Double, Double> geo, Float radiusMeter, int LoiteringDelay) {
        int GEOFENCE_TRANSITION;
        if (type == AT_ARRIVE) {
            GEOFENCE_TRANSITION = GEOFENCE_TRANSITION_ENTER;  // 진입 감지시
        } else if (type == AT_START) {
            GEOFENCE_TRANSITION = GEOFENCE_TRANSITION_EXIT;  // 이탈 감지시
        } else {
            GEOFENCE_TRANSITION = GEOFENCE_TRANSITION_DWELL; // 머물기 감지시
        }
        return new Geofence.Builder()
                .setRequestId(reqId)    // 이벤트 발생시 BroadcastReceiver에서 구분할 id
                .setCircularRegion(geo.first, geo.second, radiusMeter)    // 위치및 반경(m)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)        // Geofence 만료 시간
                .setLoiteringDelay(LoiteringDelay)                            // 머물기 체크 시간
                .setNotificationResponsiveness(120000)      //위치감지하는 텀 180000 = 180초
                .setTransitionTypes(GEOFENCE_TRANSITION)
                .build();
    }

    private PendingIntent geofencePendingIntent() {
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        PendingIntent geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    private GeofencingRequest getGeofencingRequest(List<Geofence> list) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        // Geofence 이벤트는 진입시 부터 처리할 때
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(list);  // Geofence 리스트 추가
        return builder.build();
    }

    private void addGeofencesToClient() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geofencingClient.addGeofences(getGeofencingRequest(geofenceList), geofencePendingIntent()).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showCustomToast("add Fail");
                Log.d("에러", e.toString());
            }
        });
    }

    private void insertToRoomDB() {
        ToDo todo = new ToDo(mEditTextTitle.getText().toString(), mEditTextMemo.getText().toString(), 1, mTodoCategory, mImportantMode);
        switch (mLocationMode) {
            case NONE:
                break;
            case TIME:
                break;
            case LOCATION:
                ToDoData toDoData = new ToDoData(mTextViewLocation.getText().toString(),
                        longitude, latitude, mLocationMode, mLadius,
                        mWifiBssid, mWifiMode, mLocationTime, 0, "", 0, "", "", NO_DATA, NO_DATA, NO_DATA, NO_DATA, NO_DATA);
                new InsertAsyncTask(mDatabase.todoDao()).execute(todo, toDoData);
                break;
        }
    }

    //비동기처리                                   //넘겨줄객체, 중간에 처리할 데이터, 결과물(return)
    private class InsertAsyncTask extends AsyncTask<Object, Void, Integer> {
        private ToDoDao mTodoDao;

        InsertAsyncTask(ToDoDao mTodoDao) {
            this.mTodoDao = mTodoDao;
        }

        @Override
        protected Integer doInBackground(Object... toDos) {
            int todoNo = mTodoDao.insertTodo((ToDo) toDos[0], (ToDoData) toDos[1]);
            Log.d("추가된 todoNo", " = " + todoNo);

            if (((ToDo) toDos[0]).getType() == LOCATION) { //위치기반 일정
                if (((ToDoData) toDos[1]).getIsWiFi() == 'Y') {
                    return 1;
                } else {
                    ToDoData toDoData = (ToDoData) toDos[1];
//                    geofenceList.add(getGeofence(toDoData.getLocationMode(), String.valueOf(todoNo), new Pair<>(toDoData.getLatitude(), toDoData.getLongitude()), (float) toDoData.getRadius(), 1000));
//                    addGeofencesToClient();
                    getGeofenceMaker().addGeoFenceOne(todoNo, toDoData.getLatitude(), toDoData.getLongitude(), toDoData.getLocationMode(), toDoData.getRadius(),
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
                                }
                            });
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer == 1) {
                System.out.println("실행???!!!!");
            }
        }
    }

    private void showTimeLayout() {
        ValueAnimator anim1 = ValueAnimator.ofInt(0, 160 * (int) dpUnit);
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
        ValueAnimator anim1 = ValueAnimator.ofInt(160 * (int) dpUnit, 0);
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
        ValueAnimator anim1 = ValueAnimator.ofInt(0, 210 * (int) dpUnit);
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
//        mLIkeLocationListAdapter.notifyDataSetChanged();
    }

    private void hideGpsLayout() {
        ValueAnimator anim1 = ValueAnimator.ofInt(210 * (int) dpUnit, 0);
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
            case R.id.add_personal_todo_iv_back:
                finish();
                break;
            case R.id.add_group_todo_tv_no_repeat:
                mRepeatMode = NOREPEAT;
                mTextViewTimeNoRepeat.setBackgroundResource(R.drawable.bg_round_button_on);
                mTextViewTimeNoRepeat.setTextColor(Color.parseColor("#ffffff"));
                mTextViewTimeDayRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeDayRepeat.setTextColor(Color.parseColor("#657884"));
                mTextViewTimeWeekRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeWeekRepeat.setTextColor(Color.parseColor("#657884"));
                mTextViewTimeMonthRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeMonthRepeat.setTextColor(Color.parseColor("#657884"));
                break;
            case R.id.add_group_todo_tv_day_repeat:
                mRepeatMode = DAYREPEAT;
                mTextViewTimeNoRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeNoRepeat.setTextColor(Color.parseColor("#657884"));
                mTextViewTimeDayRepeat.setBackgroundResource(R.drawable.bg_round_button_on);
                mTextViewTimeDayRepeat.setTextColor(Color.parseColor("#ffffff"));
                mTextViewTimeWeekRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeWeekRepeat.setTextColor(Color.parseColor("#657884"));
                mTextViewTimeMonthRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeMonthRepeat.setTextColor(Color.parseColor("#657884"));
                break;
            case R.id.add_group_todo_tv_week_repeat:
                mRepeatMode = WEEKREPEAT;
                mTextViewTimeNoRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeNoRepeat.setTextColor(Color.parseColor("#657884"));
                mTextViewTimeDayRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeDayRepeat.setTextColor(Color.parseColor("#657884"));
                mTextViewTimeWeekRepeat.setBackgroundResource(R.drawable.bg_round_button_on);
                mTextViewTimeWeekRepeat.setTextColor(Color.parseColor("#ffffff"));
                mTextViewTimeMonthRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeMonthRepeat.setTextColor(Color.parseColor("#657884"));
                break;
            case R.id.add_group_todo_tv_month_repeat:
                mRepeatMode = MONTHREPEAT;
                mTextViewTimeNoRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeNoRepeat.setTextColor(Color.parseColor("#657884"));
                mTextViewTimeDayRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeDayRepeat.setTextColor(Color.parseColor("#657884"));
                mTextViewTimeWeekRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeWeekRepeat.setTextColor(Color.parseColor("#657884"));
                mTextViewTimeMonthRepeat.setBackgroundResource(R.drawable.bg_round_button_on);
                mTextViewTimeMonthRepeat.setTextColor(Color.parseColor("#ffffff"));
                break;
            case R.id.add_group_todo_switch_time:
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
            case R.id.add_group_todo_switch_gps:
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

            case R.id.add_group_todo_layout_hidden_gps_start:
                mLocationMode = AT_START;
                setLocationModeView(mTextViewStart);
                break;
            case R.id.add_group_todo_layout_hidden_gps_arrive:
                mLocationMode = AT_ARRIVE;
                setLocationModeView(mTextViewArrive);
                break;
            case R.id.add_group_todo_layout_hidden_gps_near:
                mLocationMode = AT_NEAR;
                setLocationModeView(mTextViewNear);
                break;

            case R.id.add_group_todo_btn_always:
                mLocationTime = ALWAYS;
                setLocationTimeView(mTextViewAlways);
                break;
            case R.id.add_group_todo_btn_morning:
                mLocationTime = MORNING;
                setLocationTimeView(mTextViewMorning);
                break;
            case R.id.add_group_todo_btn_evening:
                mLocationTime = EVENING;
                setLocationTimeView(mTextViewEvening);
                break;
            case R.id.add_group_todo_btn_night:
                mLocationTime = NIGHT;
                setLocationTimeView(mTextViewNight);
                break;

            case R.id.add_group_todo_layout_gps:
                // 지도 선 화면
                Intent intent = new Intent(mContext, MapSelectActivity.class);
                startActivityForResult(intent, 100);
                break;
            case R.id.add_group_todo_layout_hidden_time_date:
                // 날짜선택
                setDate();
                break;
            case R.id.add_group_todo_layout_hidden_time_time:
                //시간선택
                setTime();
                break;
            case R.id.add_group_todo_btn_done:
                //추가버튼
                if (validateBeforeAdd()) {
                    insertToRoomDB();
//                    showCustomToast("일정이 등록되었습록니다.");
//                    finish();
                }
                break;
            case R.id.add_group_todo_iv_icon: // 아이콘 선택버튼
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

    void setDate() {
        Calendar today = Calendar.getInstance();

        TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                mYear = Integer.parseInt(YEAR_FORMAT.format(date));
                mMonth = Integer.parseInt(MONTH_FORMAT.format(date));
                mDay = Integer.parseInt(DAY_FORMAT.format(date));
                calendar.set(mYear, mMonth-1, mDay);
                if(calendar.before(Calendar.getInstance())){
                    showCustomToast("과거의 날짜는 선택할 수 없습니다.");
                } else {
                    mTextViewDate.setText(mYear + "년 " + mMonth + "월 " + mDay + "일");
                    mTextViewDate.setTextColor(getResources().getColor(R.color.colorBlack));
                    mIsDatePick = true;
                }
            }
        })
                .setType(new boolean[]{true, true, true, false, false, false})// 默认全部显示
                .setCancelText("취소")//取消按钮文字
                .setSubmitText("확인")//确认按钮文字
                .setTitleSize(15)//标题文字大小
                .setContentTextSize(20)
                .setSubCalSize(15)
                .setTitleText("")//标题文字
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setTitleColor(getResources().getColor(R.color.colorBlack))//标题文字颜色
                .setSubmitColor(getResources().getColor(R.color.colorBlack))//确定按钮文字颜色
                .setCancelColor(getResources().getColor(R.color.colorBlack))//取消按钮文字颜色
                .setTitleBgColor(getResources().getColor(R.color.colorWhite))//标题背景颜色 Night mode
                .setBgColor(getResources().getColor(R.color.colorWhite))//滚轮背景颜色 Night mode
                .setTextColorOut(getResources().getColor(R.color.colorBlackGray))
                .setTextColorCenter(getResources().getColor(R.color.colorBlack))
                .setLabel("년", "월", "일", "시", "분", "秒")//默认设置为年月日时分秒
                .setDate(today)
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)//是否显示为对话框样式
                .build();
        pvTime.show();
    }

    void setTime() {
        TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                mHour = Integer.parseInt(TIME_FORMAT.format(date));
                mMinute = Integer.parseInt(MINUTE_FORMAT.format(date));
                mTextViewTime.setText(mHour + "시 " + mMinute + "분");
                mTextViewTime.setTextColor(getResources().getColor(R.color.colorBlack));
                mIsTimePick = true;
            }
        })
                .setType(new boolean[]{false, false, false, true, true, false})// 默认全部显示
                .setCancelText("취소")//取消按钮文字
                .setSubmitText("확인")//确认按钮文字
                .setTitleSize(15)//标题文字大小
                .setContentTextSize(20)
                .setSubCalSize(15)
                .setTitleText("")//标题文字
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setTitleColor(getResources().getColor(R.color.colorBlack))//标题文字颜色
                .setSubmitColor(getResources().getColor(R.color.colorBlack))//确定按钮文字颜色
                .setCancelColor(getResources().getColor(R.color.colorBlack))//取消按钮文字颜色
                .setTitleBgColor(getResources().getColor(R.color.colorWhite))//标题背景颜色 Night mode
                .setBgColor(getResources().getColor(R.color.colorWhite))//滚轮背景颜色 Night mode
                .setTextColorOut(getResources().getColor(R.color.colorBlackGray))
                .setTextColorCenter(getResources().getColor(R.color.colorBlack))
                .setLabel("년", "월", "일", "시", "분", "秒")//默认设置为年月日时分秒
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)//是否显示为对话框样式
                .build();
        pvTime.show();
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

    void setVineOnMode(TextView textView) {
        textView.setBackgroundResource(R.drawable.bg_round_button_on);
        textView.setTextColor(Color.parseColor("#ffffff"));
    }

    void setVineOffMode(TextView textView) {
        textView.setBackgroundResource(R.drawable.bg_round_button_off);
        textView.setTextColor(Color.parseColor("#657884"));
    }

    boolean validateBeforeAdd() {
        if (mEditTextTitle.getText().length() < 1) {
            showSnackBar(mEditTextTitle, "내용을 입력 해 주세요");
            return false;
        }
        if (mTodoCategory == LOCATION) {
            if (!mIsLocationSelected) {
                showSnackBar(mEditTextTitle, "장소를 선택 해 주세요");
                return false;
            }
        } else if (mTodoCategory == TIME) {
            return true;
        } else {
            return true;
        }
        return true;
    }

    void setLocationInfo() {
        mIsLocationSelected = true;
        mTextViewLocation.setTextColor(getResources().getColor(R.color.colorBlack));
        mTextViewLocation.setText(mLocation.getPlaceName());
    }

    void setWifiInfo(String ssid) {
        mIsLocationSelected = true;
        mWifiMode = 'Y';
        mTextViewLocation.setTextColor(getResources().getColor(R.color.colorBlack));
        mTextViewLocation.setText(ssid);
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
//                getLocationAndSetMap(mLocation);
            } else if (resultCode == 111) {
                mWifiBssid = data.getStringExtra("bssid");
                longitude = data.getDoubleExtra("longitude", 0);
                latitude = data.getDoubleExtra("latitude", 0);
                mWifiConnected = data.getBooleanExtra("connected", false);
                setWifiInfo(data.getStringExtra("ssid"));
            }
        } else if (requestCode == 200) {//즐겨찾기 추가(기타)
            if (resultCode == 100) {
                mLocation = (LocationResponse.Location) Objects.requireNonNull(data.getSerializableExtra("location"));
                new FavoritePlaceInsertAsyncTask().execute(new FavoriteLocation(mLocation.getAddressName(), Double.parseDouble(mLocation.getLatitude()), Double.parseDouble(mLocation.getLongitude())));
            } else if (resultCode == 333) {
                mWifiBssid = data.getStringExtra("bssid");
                longitude = data.getDoubleExtra("longitude", 0);
                latitude = data.getDoubleExtra("latitude", 0);
                String wifiName = data.getStringExtra("ssid");
                String favoriteName = data.getStringExtra("favoriteName");
                new FavoritePlaceInsertAsyncTask().execute(new FavoriteLocation(favoriteName, longitude, latitude), wifiName, mWifiBssid);
            }
        } else if (requestCode == HOME_SELECT_MODE) {
            if (resultCode == 100) {
                mLocation = (LocationResponse.Location) Objects.requireNonNull(data.getSerializableExtra("location"));
                new FavoritePlaceUpdateAsyncTask().execute(mFavoritePlaceList.get(0), Double.parseDouble(mLocation.getLatitude()), Double.parseDouble(mLocation.getLongitude()), HOME_SELECT_MODE);
            } else if (resultCode == 333) {
                mWifiBssid = data.getStringExtra("bssid");
                longitude = data.getDoubleExtra("longitude", 0);
                latitude = data.getDoubleExtra("latitude", 0);
                String wifiName = data.getStringExtra("ssid");
                new FavoritePlaceUpdateAsyncTask().execute(mFavoritePlaceList.get(0), latitude, longitude, HOME_SELECT_MODE, mWifiBssid, wifiName);
            }
        } else if (requestCode == SCHOOL_SELECT_MODE) {
            if (resultCode == 100) {
                mLocation = (LocationResponse.Location) Objects.requireNonNull(data.getSerializableExtra("location"));
                new FavoritePlaceUpdateAsyncTask().execute(mFavoritePlaceList.get(1), Double.parseDouble(mLocation.getLatitude()), Double.parseDouble(mLocation.getLongitude()), SCHOOL_SELECT_MODE);
            } else if (resultCode == 333) {
                mWifiBssid = data.getStringExtra("bssid");
                longitude = data.getDoubleExtra("longitude", 0);
                latitude = data.getDoubleExtra("latitude", 0);
                String wifiName = data.getStringExtra("ssid");
                new FavoritePlaceUpdateAsyncTask().execute(mFavoritePlaceList.get(1), latitude, longitude, SCHOOL_SELECT_MODE, mWifiBssid, wifiName);
            }
        } else if (requestCode == COMPANY_SELECT_MODE) {
            if (resultCode == 100) {
                mLocation = (LocationResponse.Location) Objects.requireNonNull(data.getSerializableExtra("location"));
                new FavoritePlaceUpdateAsyncTask().execute(mFavoritePlaceList.get(2), Double.parseDouble(mLocation.getLatitude()), Double.parseDouble(mLocation.getLongitude()), COMPANY_SELECT_MODE);
            } else if (resultCode == 333) {
                mWifiBssid = data.getStringExtra("bssid");
                longitude = data.getDoubleExtra("longitude", 0);
                latitude = data.getDoubleExtra("latitude", 0);
                String wifiName = data.getStringExtra("ssid");
                new FavoritePlaceUpdateAsyncTask().execute(mFavoritePlaceList.get(2), latitude, longitude, COMPANY_SELECT_MODE, mWifiBssid, wifiName);
            }
        }
    }

    private class FavoritePlaceInsertAsyncTask extends AsyncTask<Object, Void, Void> {

        FavoritePlaceInsertAsyncTask() {
        }

        @Override
        protected Void doInBackground(Object... objects) {
            FavoriteLocation favoriteLocation = ((FavoriteLocation) objects[0]);
            mDatabase.todoDao().insert(favoriteLocation);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mFavoriteSelectedIndex = mFavoritePlaceList.size() - 1;
        }
    }

    private class FavoritePlaceUpdateAsyncTask extends AsyncTask<Object, Void, Integer> {

        FavoritePlaceUpdateAsyncTask() {

        }

        @Override
        protected Integer doInBackground(Object... objects) {
            FavoriteLocation favoriteLocation = ((FavoriteLocation) objects[0]);
            double latitude = (double) objects[1];
            double longitude = (double) objects[2];
            int type = (int) objects[3];
            if (objects.length > 4) { //와이파이 일 경우
                favoriteLocation.setPlaceConfirm(latitude, longitude);
                favoriteLocation.setWiFi(true);
                favoriteLocation.setSsid((String) objects[4]);
                favoriteLocation.setWifiName((String) objects[5]);
                mDatabase.todoDao().update(favoriteLocation);
            } else {//위치일경우
                favoriteLocation.setPlaceConfirm(latitude, longitude);
                mDatabase.todoDao().update(favoriteLocation);
            }
            return type;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer == HOME_SELECT_MODE) {
                mFavoriteSelectedIndex = 0;
            } else if (integer == SCHOOL_SELECT_MODE) {
                mFavoriteSelectedIndex = 1;
            } else if (integer == COMPANY_SELECT_MODE) {
                mFavoriteSelectedIndex = 2;
            }
        }
    }


}
