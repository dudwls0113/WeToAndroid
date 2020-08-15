package com.ninano.weto.src.common.util.DBAsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ninano.weto.R;
import com.ninano.weto.db.ToDo;
import com.ninano.weto.db.ToDoDao;
import com.ninano.weto.db.ToDoData;
import com.ninano.weto.src.common.group.GroupTodoMaker;

import static com.ninano.weto.src.ApplicationClass.AT_ARRIVE;
import static com.ninano.weto.src.ApplicationClass.NO_DATA;
import static com.ninano.weto.src.ApplicationClass.ONE_DAY;
import static com.ninano.weto.src.ApplicationClass.getApplicationClassContext;
import static com.ninano.weto.src.common.alarm.AlarmMaker.getAlarmMaker;
import static com.ninano.weto.src.common.geofence.GeofenceMaker.getGeofenceMaker;
import static com.ninano.weto.src.common.wifi.WifiMaker.getWifiMaker;

public class TodoInsertAsyncTask extends AsyncTask<Object, Void, ToDoData> {
    private ToDoDao mTodoDao;
    private String title, content;

    public TodoInsertAsyncTask(ToDoDao mTodoDao, TodoInsertAsyncTaskCallBack mTodoUpdateAsyncTaskCallBack) {
        this.mTodoDao = mTodoDao;
        this.mTodoInsertAsyncTaskCallBack = mTodoUpdateAsyncTaskCallBack;
    }

    public interface TodoInsertAsyncTaskCallBack {
        void insertSuccess(int serverTodoNo);

        void insertFail(String message);
    }

    private TodoInsertAsyncTaskCallBack mTodoInsertAsyncTaskCallBack;

    @Override
    protected ToDoData doInBackground(Object... toDos) {
        int todoNo = mTodoDao.insertTodo((ToDo) toDos[0], (ToDoData) toDos[1]);
        ((ToDoData) toDos[1]).setTodoNo(todoNo);
        Log.d("추가된 todoNo", " = " + todoNo);
        return (ToDoData) toDos[1];
    }

    @Override
    protected void onPostExecute(final ToDoData toDoData) {
        super.onPostExecute(toDoData);
        if (toDoData == null) {
            return;
        }
        if (toDoData.getIsWiFi() == 'Y') {//와이파이
            getWifiMaker().registerAndUpdateWifi(getApplicationClassContext(), toDoData.getIsWiFi(), toDoData.getLocationMode(), true);
        } else if (toDoData.getLongitude() == NO_DATA && toDoData.getRepeatType() != NO_DATA) {//시간일정
            getAlarmMaker().registerAlarm(toDoData.getTodoNo(), toDoData.getRepeatType(), toDoData.getYear(), toDoData.getMonth(), toDoData.getDay(), toDoData.getHour(), toDoData.getMinute(), title, content, toDoData.getRepeatDayOfWeek());
        } else if (toDoData.getIsMeet() == 'Y') { //약속
            //1. 약속시간 전 알람 셋팅
            getAlarmMaker().registerAlarm(toDoData.getTodoNo(), ONE_DAY, toDoData.getYear(), toDoData.getMonth(), toDoData.getDay(), toDoData.getHour() - toDoData.getMeetRemindTime(), toDoData.getMinute(),
                    getApplicationClassContext().getString(R.string.notification_title_meet_1) + toDoData.getMeetRemindTime() + getApplicationClassContext().getString(R.string.notification_title_meet_2), content, "");
            //2. 약속작송 지오펜스 셋팅(특정시간에만 지오펜스 잡도록)
            getGeofenceMaker().addGeoFenceOneForGroupTodo("meet" + toDoData.getTodoNo(), toDoData.getLatitude(), toDoData.getLongitude(), AT_ARRIVE, toDoData.getRadius(),
                    new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            Log.d("약속이 추가되었습니다", " = " + "meet" + toDoData.getTodoNo());
                            mTodoInsertAsyncTaskCallBack.insertSuccess(toDoData.getSeverTodoNo());
                        }
                    },
                    new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //지오펜스 실패하면 db에사도 지워줘야함
                            Log.d("지오펜스 등록 실패", e.toString());
                            TodoDeleteAsyncTask todoDeleteAsyncTask = new TodoDeleteAsyncTask(mTodoDao, new TodoDeleteAsyncTask.TodoDeleteAsyncTaskCallBack() {
                                @Override
                                public void deleteSuccess() {
                                    mTodoInsertAsyncTaskCallBack.insertFail("위치 서비스를 활성화가 필요합니다");
                                }

                                @Override
                                public void deleteFail() {

                                }
                            });
                            todoDeleteAsyncTask.execute(toDoData);
                        }
                    });
        } else if (toDoData.getRepeatType() == NO_DATA && toDoData.getLongitude() != NO_DATA) {//위치일정
            getGeofenceMaker().addGeoFenceOneForGroupTodo("group" + toDoData.getTodoNo(), toDoData.getLatitude(), toDoData.getLongitude(), toDoData.getLocationMode(), toDoData.getRadius(),
                    new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            Log.d("할 일이 추가되었습니다", " = " + "group" + toDoData.getTodoNo());
                            mTodoInsertAsyncTaskCallBack.insertSuccess(toDoData.getSeverTodoNo());
                        }
                    },
                    new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //지오펜스 실패하면 db에사도 지워줘야함
                            Log.e("지오펜스 등록 실패", e.toString());
                            TodoDeleteAsyncTask todoDeleteAsyncTask = new TodoDeleteAsyncTask(mTodoDao, new TodoDeleteAsyncTask.TodoDeleteAsyncTaskCallBack() {
                                @Override
                                public void deleteSuccess() {
                                    mTodoInsertAsyncTaskCallBack.insertFail("위치 서비스를 활성화가 필요합니다");
                                }

                                @Override
                                public void deleteFail() {

                                }
                            });
                            todoDeleteAsyncTask.execute(toDoData);
                        }
                    });
        }
    }
}