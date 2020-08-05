package com.ninano.weto.src.splash;

import androidx.annotation.NonNull;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ninano.weto.R;
import com.ninano.weto.db.AppDatabase;
import com.ninano.weto.db.ToDoDao;
import com.ninano.weto.db.ToDoWithData;
import com.ninano.weto.src.BaseActivity;
import com.ninano.weto.src.common.util.DBAsyncTask.AllGroupTodoAsyncTask;
import com.ninano.weto.src.main.MainActivity;
import com.ninano.weto.src.splash.interfaces.SplashActivityView;
import com.ninano.weto.src.splash.models.ServerTodo;
import com.ninano.weto.src.tutorial.TutorialActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.Intent.ACTION_VIEW;
import static com.ninano.weto.src.ApplicationClass.LOCATION;
import static com.ninano.weto.src.ApplicationClass.MEET;
import static com.ninano.weto.src.ApplicationClass.TIME;
import static com.ninano.weto.src.ApplicationClass.getApplicationClassContext;
import static com.ninano.weto.src.ApplicationClass.sSharedPreferences;
import static com.ninano.weto.src.common.Alarm.AlarmMaker.getAlarmMaker;
import static com.ninano.weto.src.common.Geofence.GeofenceMaker.getGeofenceMaker;
import static com.ninano.weto.src.common.Wifi.WifiMaker.getWifiMaker;

public class SplashActivity extends BaseActivity implements SplashActivityView {

    private Context mContext;
    private boolean isKakaoShare;
    private int mGroupId;
    private String nickName, profileUrl;
    private AppDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext = this;
        mDatabase = AppDatabase.getAppDatabase(getApplicationContext());
        boolean isFirst = sSharedPreferences.getBoolean("firstConnect", true);
        if (isFirst) {
            startActivity(new Intent(SplashActivity.this, TutorialActivity.class));
            finish();
            return;
        }

        if (getIntent() != null) {
            if (getIntent().getAction() != null) {
                if (getIntent().getAction().equals(ACTION_VIEW)) {
                    isKakaoShare = true;
                    String value = getIntent().getData().getQueryParameter("key1");
                    String groupId = value.substring(0, value.indexOf(","));
                    mGroupId = Integer.parseInt(groupId);
                    nickName = value.substring(value.indexOf(",") + 2, value.indexOf("/"));
                    profileUrl = value.substring(value.indexOf("/") + 2, value.length());
                }
            }
        }
        SplashService splashService = new SplashService(this, this);
        splashService.getAllTodo();

        getGeofenceMaker().removeAllGeofence();
        SplashAsyncTask splashAsyncTask = new SplashAsyncTask(mDatabase.todoDao());
        splashAsyncTask.execute();
    }

    @Override
    public void successGetTodo(ArrayList<ServerTodo> serverTodos) throws InterruptedException, ExecutionException {
        AllGroupTodoAsyncTask allGroupTodoAsyncTask = new AllGroupTodoAsyncTask(mDatabase.todoDao());
        List<Integer> inDBserverTodoNoList = allGroupTodoAsyncTask.execute().get();
        Log.d("serverTodoNoList", inDBserverTodoNoList.toString());
//        Log.d("serverTodos", serverTodos.toString());
        for (ServerTodo serverTodo : serverTodos) {
            //1. 내 일정에 없는게 있으면 추가
            //2. 없어진 일정인데 내일정에 있으면 해당일정 삭제 -> 서버에서 가져온 리스트에는 없는데 db에는 들어잇는 경우 (어캐잡지?)
            //3. 일정 정보 업데이트(order 제외하고 전부 update시키기)

            if(inDBserverTodoNoList.contains(serverTodo.getTodoNo())){
                //3. 일정 정보 업데이트(order 제외하고 전부 update시키기)

            }
            else{
                //1. 내 일정에 없는게 있으면 추가
            }
        }
        for (Integer serverTodoNo : inDBserverTodoNoList) {
            //2. 없어진 일정인데 내일정에 있으면 해당일정 삭제 -> 서버에서 가져온 리스트에는 없는데 db에는 들어잇는 경우
            // -> api response를 int만해서 따로줌
//            if(!serverTodos.contains(??)){


//            }
//            else{
//                1. 내 일정에 없는게 있으면 추가
//            }
        }
    }

    @Override
    public void failGetTodo() {
        showCustomToast("일정 동기화에 실패하였습니다");
    }

    //비동기처리                                   //넘겨줄객체, 중간에 처리할 데이터, 결과물(return)
    @SuppressLint("StaticFieldLeak")
    private class SplashAsyncTask extends AsyncTask<Void, Void, List<ToDoWithData>> {
        private ToDoDao mTodoDao;

        SplashAsyncTask(ToDoDao mTodoDao) {
            this.mTodoDao = mTodoDao;
        }

        @Override
        protected List<ToDoWithData> doInBackground(Void... voids) {
            return mTodoDao.getActivatedTodoListNoLive();
        }

        @Override
        protected void onPostExecute(List<ToDoWithData> toDoWithDataList) {
            super.onPostExecute(toDoWithDataList);
            List<Geofence> geofenceList = new ArrayList<>();
            for (ToDoWithData toDoWithData : toDoWithDataList) {
                if (toDoWithData.getType() == LOCATION && toDoWithData.getIsWiFi() == 'N') {
                    geofenceList.add(getGeofenceMaker().getGeofence(toDoWithData.getLocationMode(), String.valueOf(toDoWithData.getTodoNo()),
                            new Pair<>(toDoWithData.getLatitude(), toDoWithData.getLongitude()), (float) toDoWithData.getRadius()));
                }

                if (toDoWithData.getIsGroup() == 'Y') {
                    if (toDoWithData.getType() == MEET) {
                        geofenceList.add(getGeofenceMaker().getGeofence(toDoWithData.getLocationMode(), "meet" + (toDoWithData.getTodoNo()),
                                new Pair<>(toDoWithData.getLatitude(), toDoWithData.getLongitude()), (float) toDoWithData.getRadius()));
                    }
                }
                if (toDoWithData.getType() == TIME) { // 시간 정보
                    getAlarmMaker().removeAlarm(toDoWithData.getTodoNo());
                    getAlarmMaker().registerAlarm(toDoWithData.getTodoNo(), toDoWithData.getRepeatType(), toDoWithData.getYear(), toDoWithData.getMonth(), toDoWithData.getDay(), toDoWithData.getHour(), toDoWithData.getMinute(), toDoWithData.getTitle(), toDoWithData.getContent(), toDoWithData.getRepeatDayOfWeek());
                }
            }

            if (geofenceList.size() == 0) {
                if (isKakaoShare) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.putExtra("groupId", mGroupId);
                    intent.putExtra("nickName", nickName);
                    intent.putExtra("profileUrl", profileUrl);
                    intent.putExtra("kakaoShare", true);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                return;
            }
            getGeofenceMaker().addGeoFenceList(geofenceList, new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    if (isKakaoShare) {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        intent.putExtra("groupId", mGroupId);
                        intent.putExtra("nickName", nickName);
                        intent.putExtra("profileUrl", profileUrl);
                        intent.putExtra("kakaoShare", true);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(mContext, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (isKakaoShare) {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        intent.putExtra("groupId", mGroupId);
                        intent.putExtra("nickName", nickName);
                        intent.putExtra("profileUrl", profileUrl);
                        intent.putExtra("kakaoShare", true);
                        startActivity(intent);
                        finish();
                    } else {
                        System.out.println("에러: " + e.toString());
                        showCustomToast(getString(R.string.cant_geofence_when_splash));
                        Intent intent = new Intent(mContext, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }
}
