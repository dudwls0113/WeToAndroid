package com.ninano.weto.src.todo_add;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ValueAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ninano.weto.R;
import com.ninano.weto.db.AppDatabase;
import com.ninano.weto.db.ToDo;
import com.ninano.weto.db.ToDoDao;
import com.ninano.weto.db.ToDoData;
import com.ninano.weto.db.ToDoWithData;
import com.ninano.weto.src.BaseActivity;
import com.ninano.weto.src.CellularService;
import com.ninano.weto.src.WifiService;
import com.ninano.weto.src.map_select.MapSelectActivity;
import com.ninano.weto.src.map_select.keyword_search.models.LocationResponse;
import com.ninano.weto.src.receiver.AlarmBroadcastReceiver;
import com.ninano.weto.src.receiver.GeofenceBroadcastReceiver;
import com.ninano.weto.src.todo_add.adpater.LIkeLocationListAdapter;
import com.ninano.weto.src.todo_add.models.LikeLocationData;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_DWELL;
import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER;
import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT;
import static com.ninano.weto.src.ApplicationClass.ALWAYS;
import static com.ninano.weto.src.ApplicationClass.AT_START;
import static com.ninano.weto.src.ApplicationClass.NONE;
import static com.ninano.weto.src.ApplicationClass.TIME;
import static com.ninano.weto.src.ApplicationClass.LOCATION;
import static com.ninano.weto.src.ApplicationClass.AT_ARRIVE;
import static com.ninano.weto.src.ApplicationClass.AT_NEAR;
import static com.ninano.weto.src.ApplicationClass.MORNING;
import static com.ninano.weto.src.ApplicationClass.EVENING;
import static com.ninano.weto.src.ApplicationClass.NIGHT;
import static com.ninano.weto.src.ApplicationClass.sSharedPreferences;

public class AddPersonalToDoActivity extends BaseActivity {

    private Context mContext;
    private TextView mTextViewTimeNoRepeat, mTextViewTimeDayRepeat, mTextViewTimeWeekRepeat, mTextViewTimeMonthRepeat, mTextViewLocation;
    private EditText mEditTextTitle, mEditTextMemo;
    private boolean isSelectedNoRepeat, isSelectedDayRepeat, isSelectedWeekRepeat, isSelectedMonthRepeat;

    //위치 - Location Mode
    private TextView mTextViewStart, mTextViewArrive, mTextViewNear;
    //위치 - Location Time
    private TextView mTextViewAlways, mTextViewMorning, mTextViewEvening, mTextViewNight;

    private Switch mSwitchTime, mSwitchGps;
    private boolean isSwitchTime, isSwitchGps;
    private LinearLayout mLinearHiddenTime, mLinearHiddenGps, mLinearMap;

    private RecyclerView mRecyclerViewLike;
    private LIkeLocationListAdapter mLIkeLocationListAdapter;
    private ArrayList<LikeLocationData> mDataArrayList = new ArrayList<>();
    private LocationResponse.Location mLocation;
    //
    private GeofencingClient geofencingClient;

    private boolean mIsLocationSelected;
    private int mTodoCategory, mLocationMode, mLocationTime, mLadius;
    private char mWifiMode = 'N';
    private String mWifiBssid = "";

    public static float dpUnit;             // dp단위 값
    ArrayList<Geofence> geofenceList = new ArrayList<>();
    AppDatabase mDatabase;

    private AlarmManager mAlarmManager;

    private Double longitude, latitude;
    private boolean mWifiConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_personal_to_do);
        mContext = this;
        init();
        setTempLikeLocationData();
        initGeoFence();
//        addGeofencesToClient();
        registerAlarm();
    }

    void init() {
        mTextViewTimeNoRepeat = findViewById(R.id.add_personal_todo_tv_no_repeat);
        isSelectedNoRepeat = true;
        mTextViewTimeDayRepeat = findViewById(R.id.add_personal_todo_tv_day_repeat);
        mTextViewTimeWeekRepeat = findViewById(R.id.add_personal_todo_tv_week_repeat);
        mTextViewTimeMonthRepeat = findViewById(R.id.add_personal_todo_tv_month_repeat);

        mLinearHiddenTime = findViewById(R.id.add_personal_todo_layout_hidden_time);
        mLinearHiddenGps = findViewById(R.id.add_personal_todo_layout_hidden_gps);
        mLinearMap = findViewById(R.id.add_personal_todo_layout_gps);
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

        mRecyclerViewLike = findViewById(R.id.add_personal_todo_rv_like);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerViewLike.setLayoutManager(linearLayoutManager);
        mLIkeLocationListAdapter = new LIkeLocationListAdapter(mContext, mDataArrayList, new LIkeLocationListAdapter.ItemClickListener() {
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
                mLIkeLocationListAdapter.notifyDataSetChanged();
            }
        });
        mRecyclerViewLike.setAdapter(mLIkeLocationListAdapter);

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

    private void setDatabase() {
        mDatabase = AppDatabase.getAppDatabase(this);
        //UI 갱신 (라이브 데이터를 이용하여 자동으로)
        mDatabase.todoDao().getTodoList().observe(this, new Observer<List<ToDoWithData>>() {
            @Override
            public void onChanged(List<ToDoWithData> todoList) {
                //목록가져와서 자동으로 처리
//                mTextView.setText(todoList.toString());
//                geofenceList.add(getGeofence(3, "가디역", new Pair<>(37.477198, 126.883828), (float) 100.0, 10000));
//                addGeofences();
            }
        });
    }

    void initGeoFence() {
        geofencingClient = LocationServices.getGeofencingClient(this);
    }

    private Geofence getGeofence(int type, String reqId, Pair<Double, Double> geo, Float radiusMeter, int LoiteringDelay) {
        int GEOFENCE_TRANSITION;
        if (type == AT_ARRIVE) {
            Log.d("지오 추가 중", "진입 감지");
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
        geofencingClient.addGeofences(getGeofencingRequest(geofenceList), geofencePendingIntent()).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showSnackBar(mEditTextMemo, "일정 등록에 성공하였습니다.");
                finish();
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showCustomToast("add Fail");
                Log.d("에러", e.toString());
            }
        });
        //        removeGeofences -> List<String> 을 매개변수로 넘겨서 id(string)값으로 지오펜싱 제거
//                geofencingClient.removeGeofences(new List<String>())
    }

    private void insertToRoomDB() {
        ToDo todo = new ToDo(mEditTextTitle.getText().toString(), mEditTextMemo.getText().toString(), 1, mTodoCategory);
        ToDoData toDoData = new ToDoData(mTextViewLocation.getText().toString(),
                longitude, latitude, mLocationMode, mLadius,
                mWifiBssid, mWifiMode, mLocationTime, 0, "", 0, "", "");
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
    private class InsertAsyncTask extends AsyncTask<Object, Void, Integer> {
        private ToDoDao mTodoDao;

        InsertAsyncTask(ToDoDao mTodoDao) {
            this.mTodoDao = mTodoDao;
        }

        @Override
        protected Integer doInBackground(Object... toDos) {
            int todoNo = mTodoDao.insertTodo((ToDo) toDos[0], (ToDoData) toDos[1]);
            Log.d("추가된 todoNo", " = "+todoNo);

            if (((ToDo) toDos[0]).getType() == LOCATION) { //위치기반 일정
                if (((ToDoData) toDos[1]).getIsWiFi()=='Y'){
                    return 1;
                } else {
                    ToDoData toDoData = (ToDoData) toDos[1];
                    geofenceList.add(getGeofence(toDoData.getLocationMode(), String.valueOf(todoNo), new Pair<>(toDoData.getLatitude(), toDoData.getLongitude()), (float) toDoData.getRadius(), 1000));
                    addGeofencesToClient();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer==1){
                System.out.println("실행???!!!!");
                if (mWifiMode=='Y'){
                    try {
                        Integer count = new CountWifiAsyncTask(mDatabase.todoDao()).execute('Y').get();
                        System.out.println("카운트: " + count);
                        if(count == 1 ){
                            JobScheduler jobScheduler = (JobScheduler)mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                            if (jobScheduler != null) {
                                jobScheduler.cancelAll();
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                if (jobScheduler != null) {
                                    if (mWifiConnected){
                                        System.out.println("현재 연결 와이파이");
                                        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                                        final WifiInfo wifiInfo;
                                        if (wifiManager != null) {
                                            wifiInfo = wifiManager.getConnectionInfo();
                                            SharedPreferences sf = getSharedPreferences("sFile", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sf.edit();
                                            editor.putString("recentWifi", wifiInfo.getBSSID());
                                            editor.putBoolean("firstWifiNoti", true);
                                            editor.apply();
                                        }
                                    }
                                    jobScheduler.schedule(new JobInfo.Builder(0, new ComponentName(mContext, WifiService.class))
                                            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                                            .setPeriodic(TimeUnit.MINUTES.toMillis(15))
                                            .build());
                                    jobScheduler.schedule(new JobInfo.Builder(1, new ComponentName(mContext, CellularService.class))
                                            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_CELLULAR)
                                            .setPeriodic(TimeUnit.MINUTES.toMillis(15))
                                            .build());
                                }
                            }
                        }
                        else {
                            System.out.println("카운트 아님");
                            if (mWifiConnected){
                                System.out.println("현재 연결 와이파이2");
                                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                                final WifiInfo wifiInfo;
                                if (wifiManager != null) {
                                    wifiInfo = wifiManager.getConnectionInfo();
                                    SharedPreferences sf = getSharedPreferences("sFile", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sf.edit();
                                    editor.putString("recentWifi", wifiInfo.getBSSID());
                                    editor.putBoolean("firstWifiNoti", true);
                                    editor.apply();
                                }
                            }
                        }
                    } catch (ExecutionException | InterruptedException e) {
                        showCustomToast(getString(R.string.insert_todo_error));
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void showTimeLayout() {
        ValueAnimator anim1 = ValueAnimator.ofInt(0, 180 * (int) dpUnit);
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
        ValueAnimator anim1 = ValueAnimator.ofInt(180 * (int) dpUnit, 0);
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
        ValueAnimator anim1 = ValueAnimator.ofInt(0, 300 * (int) dpUnit);
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
        mLIkeLocationListAdapter.notifyDataSetChanged();
    }

    private void hideGpsLayout() {
        ValueAnimator anim1 = ValueAnimator.ofInt(300 * (int) dpUnit, 0);
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
                isSelectedNoRepeat = true;
                mTextViewTimeNoRepeat.setBackgroundResource(R.drawable.bg_round_button_on);
                mTextViewTimeNoRepeat.setTextColor(Color.parseColor("#ffffff"));
                isSelectedDayRepeat = false;
                mTextViewTimeDayRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeDayRepeat.setTextColor(Color.parseColor("#657884"));
                isSelectedWeekRepeat = false;
                mTextViewTimeWeekRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeWeekRepeat.setTextColor(Color.parseColor("#657884"));
                isSelectedMonthRepeat = false;
                mTextViewTimeMonthRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeMonthRepeat.setTextColor(Color.parseColor("#657884"));
                break;
            case R.id.add_personal_todo_tv_day_repeat:
                isSelectedNoRepeat = false;
                mTextViewTimeNoRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeNoRepeat.setTextColor(Color.parseColor("#657884"));
                isSelectedDayRepeat = true;
                mTextViewTimeDayRepeat.setBackgroundResource(R.drawable.bg_round_button_on);
                mTextViewTimeDayRepeat.setTextColor(Color.parseColor("#ffffff"));
                isSelectedWeekRepeat = false;
                mTextViewTimeWeekRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeWeekRepeat.setTextColor(Color.parseColor("#657884"));
                isSelectedMonthRepeat = false;
                mTextViewTimeMonthRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeMonthRepeat.setTextColor(Color.parseColor("#657884"));
                break;
            case R.id.add_personal_todo_tv_week_repeat:
                isSelectedNoRepeat = false;
                mTextViewTimeNoRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeNoRepeat.setTextColor(Color.parseColor("#657884"));
                isSelectedDayRepeat = false;
                mTextViewTimeDayRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeDayRepeat.setTextColor(Color.parseColor("#657884"));
                isSelectedWeekRepeat = true;
                mTextViewTimeWeekRepeat.setBackgroundResource(R.drawable.bg_round_button_on);
                mTextViewTimeWeekRepeat.setTextColor(Color.parseColor("#ffffff"));
                isSelectedMonthRepeat = false;
                mTextViewTimeMonthRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeMonthRepeat.setTextColor(Color.parseColor("#657884"));
                break;
            case R.id.add_personal_todo_tv_month_repeat:
                isSelectedNoRepeat = false;
                mTextViewTimeNoRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeNoRepeat.setTextColor(Color.parseColor("#657884"));
                isSelectedDayRepeat = false;
                mTextViewTimeDayRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeDayRepeat.setTextColor(Color.parseColor("#657884"));
                isSelectedWeekRepeat = false;
                mTextViewTimeWeekRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeWeekRepeat.setTextColor(Color.parseColor("#657884"));
                isSelectedMonthRepeat = true;
                mTextViewTimeMonthRepeat.setBackgroundResource(R.drawable.bg_round_button_on);
                mTextViewTimeMonthRepeat.setTextColor(Color.parseColor("#ffffff"));
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
                startActivityForResult(intent, 100);
                break;

            case R.id.add_personal_todo_btn_done:
                //추가버튼
                if (validateBeforeAdd()) {
                    insertToRoomDB();
                    showCustomToast("일정이 등록되었습니다.");
                    finish();
                }
                break;
        }
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

    void setTempLikeLocationData() {
        mDataArrayList.add(new LikeLocationData("집", "청라1동", false, false));
        mDataArrayList.add(new LikeLocationData("회사", "청라1동", false, false));
        mDataArrayList.add(new LikeLocationData("사무실", "청라1동", false, false));
        //마지막 +
        mDataArrayList.add(new LikeLocationData("+", "청라1동", false, true));
        mLIkeLocationListAdapter.notifyDataSetChanged();
    }

    void setLocationInfo() {
        mIsLocationSelected = true;
        mTextViewLocation.setTextColor(getResources().getColor(R.color.colorBlack));
        mTextViewLocation.setText(mLocation.getPlaceName());
    }

    void setWifiInfo(String ssid){
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
            } else if (resultCode==111){
                mWifiBssid = data.getStringExtra("bssid");
                longitude = data.getDoubleExtra("longitude", 0);
                latitude = data.getDoubleExtra("latitude", 0);
                mWifiConnected = data.getBooleanExtra("connected", false);
                setWifiInfo(data.getStringExtra("ssid"));
            }
        }
    }

    void registerAlarm(){
        Intent intent = new Intent(AddPersonalToDoActivity.this, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 3);
        calendar.set(Calendar.MINUTE, 53);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        mAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        if (mAlarmManager != null) {
            mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    private class CountWifiAsyncTask extends AsyncTask<Character, Void, Integer> {
        private ToDoDao mTodoDao;

        CountWifiAsyncTask(ToDoDao mTodoDao) {
            this.mTodoDao = mTodoDao;
        }

        @Override
        protected Integer doInBackground(Character... characters) {
            Integer count = mDatabase.todoDao().getTodoWithWifiCount(characters[0]);
            return count;
        }
    }
}
