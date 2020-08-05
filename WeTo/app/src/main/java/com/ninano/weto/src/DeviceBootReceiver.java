package com.ninano.weto.src;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.AsyncTask;

import com.ninano.weto.db.AppDatabase;
import com.ninano.weto.db.ToDoDao;
import com.ninano.weto.db.ToDoWithData;

import java.util.List;
import java.util.Objects;

import static com.ninano.weto.src.ApplicationClass.TIME;
import static com.ninano.weto.src.ApplicationClass.getApplicationClassContext;
import static com.ninano.weto.src.common.alarm.AlarmMaker.getAlarmMaker;

public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {

            AppDatabase mDatabase = AppDatabase.getAppDatabase(getApplicationClassContext());
            DeviceBootAsyncTask deviceBootAsyncTask = new DeviceBootAsyncTask(mDatabase.todoDao());
            deviceBootAsyncTask.execute();


//            TODO 지오펜스 재동록 - 휴대폰 재부팅
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (!Objects.requireNonNull(locationManager).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                context.registerReceiver(this, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
            } else {
                //We are good, continue with adding geofences!
            }

//            TODO 지오펜스 재동록 - GPS 껏다
            if (intent.getAction().equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    context.unregisterReceiver(this);
                    //We got our GPS stuff up, add our geofences!
                }
            }
        }
    }

    //비동기처리                                   //넘겨줄객체, 중간에 처리할 데이터, 결과물(return)
    private class DeviceBootAsyncTask extends AsyncTask<Void, Void, List<ToDoWithData>> {
        private ToDoDao mTodoDao;

        DeviceBootAsyncTask(ToDoDao mTodoDao) {
            this.mTodoDao = mTodoDao;
        }

        @Override
        protected List<ToDoWithData> doInBackground(Void... voids) {
            return mTodoDao.getActivatedTodoListNoLive();
        }

        @Override
        protected void onPostExecute(List<ToDoWithData> toDoWithDataList) {
            super.onPostExecute(toDoWithDataList);
            for(int i=0; i<toDoWithDataList.size(); i++){
                if(toDoWithDataList.get(i).getType() == TIME){ // 시간 정보
                    ToDoWithData tempData = toDoWithDataList.get(i);
                    getAlarmMaker().removeAlarm(tempData.getTodoNo());
                    getAlarmMaker().registerAlarm(tempData.getTodoNo(), tempData.getRepeatType(), tempData.getYear(), tempData.getMonth(), tempData.getDay(), tempData.getHour(), tempData.getMinute(), tempData.getTitle(), tempData.getContent(), tempData.getRepeatDayOfWeek());
                }
            }
        }


    }
}
