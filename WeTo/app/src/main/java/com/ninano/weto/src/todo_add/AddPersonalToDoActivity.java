package com.ninano.weto.src.todo_add;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

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
import com.ninano.weto.src.DeviceBootReceiver;
import com.ninano.weto.src.WifiService;
import com.ninano.weto.src.map_select.MapSelectActivity;
import com.ninano.weto.src.map_select.keyword_search.models.LocationResponse;
import com.ninano.weto.src.receiver.AlarmBroadcastReceiver;
import com.ninano.weto.src.receiver.GeofenceBroadcastReceiver;
import com.ninano.weto.src.todo_add.adpater.MyPlaceListAdapter;
import com.ninano.weto.src.todo_add.models.MyPlace;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_DWELL;
import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER;
import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT;
import static com.ninano.weto.src.ApplicationClass.ALL_DAY;
import static com.ninano.weto.src.ApplicationClass.ALWAYS;
import static com.ninano.weto.src.ApplicationClass.AT_START;
import static com.ninano.weto.src.ApplicationClass.MONTH_DAY;
import static com.ninano.weto.src.ApplicationClass.NONE;
import static com.ninano.weto.src.ApplicationClass.ONE_DAY;
import static com.ninano.weto.src.ApplicationClass.TIME;
import static com.ninano.weto.src.ApplicationClass.LOCATION;
import static com.ninano.weto.src.ApplicationClass.AT_ARRIVE;
import static com.ninano.weto.src.ApplicationClass.AT_NEAR;
import static com.ninano.weto.src.ApplicationClass.MORNING;
import static com.ninano.weto.src.ApplicationClass.EVENING;
import static com.ninano.weto.src.ApplicationClass.NIGHT;
import static com.ninano.weto.src.ApplicationClass.WEEK_DAY;

public class AddPersonalToDoActivity extends BaseActivity {

    private Context mContext;

    //공통
    private EditText mEditTextTitle, mEditTextMemo;
    private int mIcon;
    private Switch mSwitchTime, mSwitchGps;
    private boolean isSwitchTime, isSwitchGps;
    private char mImportantMode = 'N';

    //Time Mode
    private TextView mTextViewTimeNoRepeat, mTextViewTimeDayRepeat, mTextViewTimeWeekRepeat, mTextViewTimeMonthRepeat, mTextViewDate, mTextViewTime;
    private LinearLayout mLinearHiddenTime;
    private AlarmManager mAlarmManager;
    private int mRepeatType;

    //Location Mode
    private TextView mTextViewLocation, mTextViewStart, mTextViewArrive, mTextViewNear;
    private TextView mTextViewAlways, mTextViewMorning, mTextViewEvening, mTextViewNight;
    private LinearLayout mLinearHiddenGps;
    private int mTodoCategory, mLocationMode, mLocationTime, mLadius;
    private boolean mIsLocationSelected;
    private char mWifiMode = 'N';
    private String mWifiBssid = "";
    private boolean mWifiConnected;
    ArrayList<Geofence> geofenceList = new ArrayList<>();
    private Double longitude, latitude;

    private RecyclerView mRecyclerViewMyPlace;
    private MyPlaceListAdapter mMyPlaceListAdapter;
    private ArrayList<MyPlace> mDataArrayList = new ArrayList<>();
    private LocationResponse.Location mLocation;

    AppDatabase mDatabase;
    private GeofencingClient geofencingClient;
    public static float dpUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_personal_to_do);
        mContext = this;
        init();
        setTempLikeLocationData();
        initGeoFence();
    }

    void init() {
        mTextViewTimeNoRepeat = findViewById(R.id.add_personal_todo_tv_no_repeat);
        mTextViewTimeDayRepeat = findViewById(R.id.add_personal_todo_tv_day_repeat);
        mTextViewTimeWeekRepeat = findViewById(R.id.add_personal_todo_tv_week_repeat);
        mTextViewTimeMonthRepeat = findViewById(R.id.add_personal_todo_tv_month_repeat);

        mTextViewDate = findViewById(R.id.add_personal_todo_tv_date);
        mTextViewTime = findViewById(R.id.add_personal_todo_tv_time);

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
        mIsLocationSelected = false;
        mLocationMode = AT_ARRIVE;
        mLocationTime = ALWAYS;
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
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
        ToDo todo = new ToDo(mEditTextTitle.getText().toString(), mEditTextMemo.getText().toString(), mIcon, mTodoCategory, mImportantMode, 'N', 0);
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
            Log.d("추가된 todoNo", " = " + todoNo);

            if (((ToDo) toDos[0]).getType() == LOCATION) { //위치기반 일정
                if (((ToDoData) toDos[1]).getIsWiFi() == 'Y') {
                    return 1;
                } else {
                    ToDoData toDoData = (ToDoData) toDos[1];
                    geofenceList.add(getGeofence(toDoData.getLocationMode(), String.valueOf(todoNo), new Pair<>(toDoData.getLongitude(), toDoData.getLatitude()), (float) toDoData.getRadius(), 1000));
                    addGeofencesToClient();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer != null && integer == 1) {
                if (mWifiMode == 'Y') {
                    try {
                        Integer count = new CountWifiAsyncTask(mDatabase.todoDao()).execute('Y').get();
                        System.out.println("카운트: " + count);
                        if (count == 1) {
                            JobScheduler jobScheduler = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                            if (jobScheduler != null) {
                                jobScheduler.cancelAll();
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                if (jobScheduler != null) {
                                    if (mWifiConnected) {
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
                        } else {
                            System.out.println("카운트 아님");
                            if (mWifiConnected) {
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
        mMyPlaceListAdapter.notifyDataSetChanged();
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
            case R.id.activity_todo_edit_todo_tv_no_repeat:
                setRepeatTimeView(mTextViewTimeNoRepeat);
                mRepeatType = ONE_DAY;
                break;
            case R.id.activity_todo_edit_todo_tv_day_repeat:
                setRepeatTimeView(mTextViewTimeDayRepeat);
                mRepeatType = ALL_DAY;
                break;
            case R.id.activity_todo_edit_todo_tv_week_repeat:
                setRepeatTimeView(mTextViewTimeWeekRepeat);
                mRepeatType = WEEK_DAY;
                break;
            case R.id.activity_todo_edit_todo_tv_month_repeat:
                setRepeatTimeView(mTextViewTimeMonthRepeat);
                mRepeatType = MONTH_DAY;
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
                    insertToRoomDB();
                    showCustomToast("일정이 등록되었습니다.");
                    finish();
                }
                break;
        }
    }

    void setDate() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                mTextViewDate.setText(i + "년 " + (i1 + 1) + "월 " + i2 + "일");
                mTextViewDate.setTextColor(getResources().getColor(R.color.colorBlack));
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
                mTextViewTime.setText(i + "시 " + i1 + "분");
                mTextViewTime.setTextColor(getResources().getColor(R.color.colorBlack));
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
        mDataArrayList.add(new MyPlace("집", "청라1동", false, false));
        mDataArrayList.add(new MyPlace("회사", "청라1동", false, false));
        mDataArrayList.add(new MyPlace("사무실", "청라1동", false, false));
        //마지막 +
        mDataArrayList.add(new MyPlace("+", "청라1동", false, true));
        mMyPlaceListAdapter.notifyDataSetChanged();
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
            } else if (resultCode == 111) {
                mWifiBssid = data.getStringExtra("bssid");
                longitude = data.getDoubleExtra("longitude", 0);
                latitude = data.getDoubleExtra("latitude", 0);
                mWifiConnected = data.getBooleanExtra("connected", false);
                setWifiInfo(data.getStringExtra("ssid"));
            }
        }
    }

    void registerAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 5);
        calendar.set(Calendar.MINUTE, 06);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        System.out.println("알람 시간: " + calendar.getTime().toString() + ", " + calendar.getTimeInMillis());
        PackageManager pm = this.getPackageManager();
        ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);

        Intent intent = new Intent(AddPersonalToDoActivity.this, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (mAlarmManager != null) {
            mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }

        //부팅후 재실행
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
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
