package com.ninano.weto.src.common;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.RemoteMessage;
import com.ninano.weto.R;
import com.ninano.weto.db.AppDatabase;
import com.ninano.weto.db.ToDo;
import com.ninano.weto.db.ToDoDao;
import com.ninano.weto.db.ToDoData;
import com.ninano.weto.src.todo_add.AddPersonalToDoActivity;

import java.util.Map;
import java.util.Objects;

import static com.ninano.weto.src.ApplicationClass.LOCATION;
import static com.ninano.weto.src.ApplicationClass.NO_DATA;
import static com.ninano.weto.src.ApplicationClass.TIME;
import static com.ninano.weto.src.ApplicationClass.getApplicationClassContext;
import static com.ninano.weto.src.common.Alarm.AlarmMaker.getAlarmMaker;
import static com.ninano.weto.src.common.Geofence.GeofenceMaker.getGeofenceMaker;
import static com.ninano.weto.src.common.Wifi.WifiMaker.getWifiMaker;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    AppDatabase mDatabase;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // TODO(developer): Handle FCM messages here.
        Log.d("FCM", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        Map<String, String> data = remoteMessage.getData();
        if (data.size() > 0) {
            Log.d("FCM", "Message data payload: " + data);

            // Check if message contains a notification payload.
            RemoteMessage.Notification notification = remoteMessage.getNotification();
//            if (notification != null) {
//                Log.d("FCM", "Message Notification Body: " + notification.getBody());
////                pushNotification(this, notification.getBody(), data);
//            }
            if (data.size() > 0) {
                receiveFcmData(data);
            }

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                //scheduleJob();
            } else {
                // Handle message within 10 seconds
                //handleNow();
            }
        }
    }

    private void receiveFcmData(Map<String, String> data) {
        String title = data.get("title");
        String content = data.get("content");
        int icon = Integer.parseInt(data.get("icon"));
        int type = Integer.parseInt(data.get("type"));
        String isImportant = data.get("isImportant");
        int ordered = Integer.parseInt(data.get("ordered"));
        String status = data.get("status");
        int serverTodoNo = Integer.parseInt(data.get("serverTodoNo"));

        int locationMode = 0;
        String locationName = "";
        int timeSlot = 0;
        double latitude = 0;
        double longitude = 0;
        int radius = 0;
        String isWiFi = "";
        String ssid = "";
        String repeatDayOfWeek = "";
        int repeatDay = 0;
        String date = "";
        String time = "";
        int repeatType = 0;
        try {
            mDatabase = AppDatabase.getAppDatabase(getApplicationClassContext());
            if (type == 77) {
                locationMode = Integer.parseInt(data.get("locationMode"));
                locationName = data.get("locationName");
                timeSlot = Integer.parseInt(data.get("timeSlot"));
                latitude = Double.parseDouble(data.get("latitude"));
                longitude = Double.parseDouble(data.get("longitude"));
                radius = Integer.parseInt(data.get("radius"));
                isWiFi = data.get("isWiFi");
                   ssid = data.get("ssid");
            } else if (type == 66) {
                repeatDayOfWeek = data.get("repeatDayOfWeek");
                repeatDay = Integer.parseInt(data.get("repeatDay"));
                date = data.get("date");
                time = data.get("time");
                repeatType = Integer.parseInt(data.get("repeatType"));
            }

            if (type == 77) { // LOCATION
                ToDo todo = makeGroupTodoObject(title, content, icon, type, Objects.requireNonNull(isImportant).charAt(0), serverTodoNo);
                ToDoData toDoData = makeGroupTodoDataObject(type, locationName, latitude, longitude, locationMode, radius, ssid, Objects.requireNonNull(isWiFi).charAt(0),
                        timeSlot, repeatType, repeatDayOfWeek, repeatDay, date, time, 2020, 12, 1, 1, 11);
                new InsertAsyncTask(mDatabase.todoDao()).execute(todo, toDoData);
            }

        } catch (NumberFormatException numberFormatException) {
            if (type == 77) { // LOCATION
                ToDo todo = makeGroupTodoObject(title, content, icon, type, Objects.requireNonNull(isImportant).charAt(0), serverTodoNo);
                ToDoData toDoData = makeGroupTodoDataObject(type, locationName, latitude, longitude, locationMode, radius, ssid, Objects.requireNonNull(isWiFi).charAt(0),
                        timeSlot, repeatType, repeatDayOfWeek, repeatDay, date, time, 2020, 12, 1, 1, 11);
                new InsertAsyncTask(mDatabase.todoDao()).execute(todo, toDoData);
            }
        }
    }

    //비동기처리                                   //넘겨줄객체, 중간에 처리할 데이터, 결과물(return)
    private class InsertAsyncTask extends AsyncTask<Object, Void, ToDoData> {
        private ToDoDao mTodoDao;

        InsertAsyncTask(ToDoDao mTodoDao) {
            this.mTodoDao = mTodoDao;
        }

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
//                getWifiMaker().registerAndUpdateWifi(mContext, mWifiMode, mLocationMode, mWifiConnected);
//                Log.d("와이파이 일정 등록", mWifiMode + " " + mLocationMode + " " + mWifiConnected);
            } else if (toDoData.getLongitude() == NO_DATA && toDoData.getRepeatType() != NO_DATA) {//시간일정
//                changeRepeatDayOfWeek();
//                getAlarmMaker().registerAlarm(toDoData.getTodoNo(), mRepeatType, mYear, mMonth, mDay, mHour, mMinute, mEditTextTitle.getText().toString(), mEditTextMemo.getText().toString(), mRepeatDayOfWeek);
            } else if (toDoData.getRepeatType() == NO_DATA && toDoData.getLongitude() != NO_DATA) {//위치일정
                getGeofenceMaker().addGeoFenceOneForGroupTodo("group" + toDoData.getTodoNo(), toDoData.getLatitude(), toDoData.getLongitude(), toDoData.getLocationMode(), toDoData.getRadius(),
                        new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Log.d("할 일이 추가되었습니다", " = " + "group" + toDoData.getTodoNo());
                                //서버에 성공했다고 api통신
                            }
                        },
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
//                                showCustomToast(getString(R.string.cant_geofence));
//                                new AddPersonalToDoActivity.DeleteToDoAsyncTask().execute(toDoData.getTodoNo(), toDoData.getTodoDataNo());
                                Log.e("지오펜스 등록 실패", e.toString());
                                //지오펜스 실패하면 db에사도 지워줘야함
                            }
                        });
            }
        }
    }

    private ToDo makeGroupTodoObject(String title, String content, int icon, int type, char importantMode, int serverTodoNo) {
        ToDo todo = new ToDo(title, content, icon, type, importantMode, 'Y', serverTodoNo);
        return todo;
    }

    private ToDoData makeGroupTodoDataObject(int type, String locationName, double latitude, double longitude, int locationMode, int radius, String ssid, char wifiMode,
                                             int timeSlot, int repeatType, String repeatDayOfWeek, int repeatDay, String date, String time, int year, int month, int day, int hour, int minute) {
        if (type == LOCATION) {
            return new ToDoData(locationName,
                    latitude, longitude, locationMode, radius,
                    ssid, wifiMode, timeSlot, NO_DATA, "", NO_DATA, "", "", NO_DATA, NO_DATA, NO_DATA, NO_DATA, NO_DATA);
        } else if (type == TIME) {
            return new ToDoData(locationName,
                    NO_DATA, NO_DATA, NO_DATA, NO_DATA,
                    "", 'N', NO_DATA, repeatType, repeatDayOfWeek, repeatDay, date, time, year, month, day, hour, minute);
        } else {
            return new ToDoData(locationName,
                    NO_DATA, NO_DATA, NO_DATA, NO_DATA,
                    "", 'N', NO_DATA, NO_DATA, "", NO_DATA, "", "", NO_DATA, NO_DATA, NO_DATA, NO_DATA, NO_DATA);
        }
    }
}
