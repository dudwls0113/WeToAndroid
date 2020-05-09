package com.ninano.weto.src.todo_add;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ValueAnimator;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
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
import com.ninano.weto.db.RoomDBActivity;
import com.ninano.weto.db.ToDo;
import com.ninano.weto.db.ToDoDao;
import com.ninano.weto.db.ToDoData;
import com.ninano.weto.db.ToDoWithData;
import com.ninano.weto.src.BaseActivity;
import com.ninano.weto.src.map_select.MapSelectActivity;
import com.ninano.weto.src.map_select.keyword_search.models.LocationResponse;
import com.ninano.weto.src.receiver.GeofenceBroadcastReceiver;
import com.ninano.weto.src.todo_add.adpater.LIkeLocationListAdapter;
import com.ninano.weto.src.todo_add.models.LikeLocationData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_DWELL;
import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER;
import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT;

public class AddPersonalToDoActivity extends BaseActivity {

    private Context mContext;
    private TextView mTextViewTimeNoRepeat, mTextViewTimeDayRepeat, mTextViewTimeWeekRepeat, mTextViewTimeMonthRepeat, mTextViewLocation;
    private boolean isSelectedNoRepeat, isSelectedDayRepeat, isSelectedWeekRepeat, isSelectedMonthRepeat;

    private Switch mSwitchTime, mSwitchGps;
    private boolean isSwitchTime, isSwitchGps;
    private LinearLayout mLinearHiddenTime, mLinearHiddenGps, mLinearMap;

    private RecyclerView mRecyclerViewLike;
    private LIkeLocationListAdapter mLIkeLocationListAdapter;
    private ArrayList<LikeLocationData> mDataArrayList = new ArrayList<>();
    private LocationResponse.Location mLocation;
    //
    private GeofencingClient geofencingClient;

    ArrayList<Geofence> geofenceList = new ArrayList<>();
    AppDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_personal_to_do);
        mContext = this;
        init();
        setTempLikeLocationData();
        initGeoFence();
//        addGeofencesToClient();
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

        mSwitchTime = findViewById(R.id.add_personal_todo_switch_time);
        mSwitchGps = findViewById(R.id.add_personal_todo_switch_gps);

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
    }

    //select해서 set해주는거니 여기말고 목록화면으로가면될듯함
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
        if (type == 1) {
            GEOFENCE_TRANSITION = GEOFENCE_TRANSITION_ENTER;  // 진입 감지시
        } else if (type == 2) {
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
                showCustomToast("add Success");

            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showCustomToast("add Fail");
                Log.d("에러", e.toString());

                // ...
            }
        });
    }

    private void insertToRommDB(String title, String content, int icon, int type) {
        new AddPersonalToDoActivity.InsertAsyncTask(mDatabase.todoDao())
                .execute(new ToDo(title, content, icon, type),
                        new ToDoData(0, "title", 10.3, 10.3, 100, "ssid", 'Y', 1,
                                1, "1 2", 30, "10:30", "10:30"));
    }

    //비동기처리                                   //넘겨줄객체, 중간에 처리할 데이터, 결과물(return)
    private class InsertAsyncTask extends AsyncTask<Object, Void, Void> {
        private ToDoDao mTodoDao;

        InsertAsyncTask(ToDoDao mTodoDao) {
            this.mTodoDao = mTodoDao;
        }

        @Override
        protected Void doInBackground(Object... toDos) {
            mTodoDao.insertTodo((ToDo) toDos[0], (ToDoData) toDos[1]);
            geofenceList.add(getGeofence(1, "사무실", new Pair<>(37.477198, 126.883828), (float) 300, 3000));
            addGeofencesToClient();
            return null;
        }
    }

    private void showTimeLayout() {
        ValueAnimator anim1 = ValueAnimator.ofInt(0, 500);
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
        ValueAnimator anim1 = ValueAnimator.ofInt(500, 0);
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
        ValueAnimator anim1 = ValueAnimator.ofInt(0, 600);
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
        ValueAnimator anim1 = ValueAnimator.ofInt(600, 0);
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
                    hideTimeLayout();
                    isSwitchTime = false;
                } else { // not expand 상태
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
                    hideGpsLayout();
                    isSwitchGps = false;
                } else {
                    showGpsLayout();
                    isSwitchGps = true;
                    if (isSwitchTime) {
                        hideTimeLayout();
                        isSwitchTime = false;
                        mSwitchTime.setChecked(false);
                    }
                }
                break;

            case R.id.add_personal_todo_layout_gps:
                // 지도 선 화면
                Intent intent = new Intent(mContext, MapSelectActivity.class);
                startActivityForResult(intent, 100);
                break;

        }
    }

    void setTempLikeLocationData() {
        mDataArrayList.add(new LikeLocationData("집", "청라1동", false, false));
        mDataArrayList.add(new LikeLocationData("회사", "청라1동", false, false));
        mDataArrayList.add(new LikeLocationData("사무실", "청라1동", false, false));
        //마지막 +
        mDataArrayList.add(new LikeLocationData("+", "청라1동", false, true));
        mLIkeLocationListAdapter.notifyDataSetChanged();
    }

    void setLocationInfo(){
        mTextViewLocation.setTextColor(getResources().getColor(R.color.colorBlack));
        mTextViewLocation.setText(mLocation.getPlaceName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (resultCode == 100) {
                //성공적으로 location  받음
                mLocation = (LocationResponse.Location) Objects.requireNonNull(data.getSerializableExtra("location"));
                setLocationInfo();
//                getLocationAndSetMap(mLocation);
            } else {

            }
        }
    }
}
