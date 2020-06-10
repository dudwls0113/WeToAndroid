package com.ninano.weto.src.splash;

import androidx.annotation.NonNull;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ninano.weto.R;
import com.ninano.weto.db.AppDatabase;
import com.ninano.weto.db.ToDoDao;
import com.ninano.weto.db.ToDoWithData;
import com.ninano.weto.src.BaseActivity;
import com.ninano.weto.src.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

import static com.ninano.weto.src.ApplicationClass.LOCATION;
import static com.ninano.weto.src.ApplicationClass.TIME;
import static com.ninano.weto.src.ApplicationClass.getApplicationClassContext;
import static com.ninano.weto.src.common.Alarm.AlarmMaker.getAlarmMaker;
import static com.ninano.weto.src.common.Geofence.GeofenceMaker.getGeofenceMaker;

public class SplashActivity extends BaseActivity {

    private ToDoDao mTodoDao;
    private AppDatabase mDatabase = AppDatabase.getAppDatabase(getApplicationClassContext());
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext = this;

        getGeofenceMaker().removeAllGeofence();

        SplashAsyncTask splashAsyncTask = new SplashAsyncTask(mDatabase.todoDao());
        splashAsyncTask.execute();
    }

    //비동기처리                                   //넘겨줄객체, 중간에 처리할 데이터, 결과물(return)
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
                if(toDoWithData.getType() == TIME){ // 시간 정보
                    getAlarmMaker().removeAlarm(toDoWithData.getTodoNo());
                    getAlarmMaker().registerAlarm(toDoWithData.getTodoNo(), toDoWithData.getRepeatType(), toDoWithData.getYear(), toDoWithData.getMonth(), toDoWithData.getDay(), toDoWithData.getHour(), toDoWithData.getMinute(), toDoWithData.getTitle(), toDoWithData.getContent(), toDoWithData.getRepeatDayOfWeek());
                }
            }

            if (geofenceList.size() == 0) {
                Intent intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
            getGeofenceMaker().addGeoFenceList(geofenceList, new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("에러: " + e.toString());
                    showCustomToast(getString(R.string.cant_geofence_when_splash));
                    Intent intent = new Intent(mContext, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }


    }
}
