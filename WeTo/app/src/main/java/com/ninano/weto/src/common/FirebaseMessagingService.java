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
import com.ninano.weto.src.DefaultResponse;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ninano.weto.src.ApplicationClass.AT_ARRIVE;
import static com.ninano.weto.src.ApplicationClass.GPS_LADIUS;
import static com.ninano.weto.src.ApplicationClass.LOCATION;
import static com.ninano.weto.src.ApplicationClass.MEET;
import static com.ninano.weto.src.ApplicationClass.NO_DATA;
import static com.ninano.weto.src.ApplicationClass.ONE_DAY;
import static com.ninano.weto.src.ApplicationClass.TIME;
import static com.ninano.weto.src.ApplicationClass.getApplicationClassContext;
import static com.ninano.weto.src.ApplicationClass.getRetrofit;
import static com.ninano.weto.src.common.Alarm.AlarmMaker.getAlarmMaker;
import static com.ninano.weto.src.common.Geofence.GeofenceMaker.getGeofenceMaker;
import static com.ninano.weto.src.common.Wifi.WifiMaker.getWifiMaker;
import static com.ninano.weto.src.common.util.Util.sendNotification;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    AppDatabase mDatabase;
    String title;
    String content;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getData();
        if (data.size() > 0) {
            receiveFcmData(data);
        }
    }

    private void receiveFcmData(Map<String, String> data) {
        if (Objects.equals(data.get("type"), "arrive")) {
            sendNotification(data.get("content"), data.get("title"));
            return;
        }
        title = data.get("title");
        content = data.get("content");
        int icon = Integer.parseInt(data.get("icon"));
        int type = Integer.parseInt(data.get("type"));
        String isImportant = data.get("isImportant");
        int ordered = Integer.parseInt(data.get("ordered"));
        String status = data.get("status");
        int serverTodoNo = Integer.parseInt(data.get("serverTodoNo"));
        int groupNo = Integer.parseInt(data.get("groupNo"));

        int locationMode = 0;
        String locationName = "";
        int timeSlot = 0;
        double latitude = 0;
        double longitude = 0;
        int radius = GPS_LADIUS;
        String isWiFi = "";
        String ssid = "";
        String repeatDayOfWeek = "";
        int repeatDay = 0;
        String date = "";
        String time = "";

        int year = 0;
        int month = 0;
        int day = 0;
        int hour = 0;
        int minute = 0;

        int repeatType = 0;
        int meetRemindTime = 0;
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
                year = Integer.parseInt(data.get("year"));
                month = Integer.parseInt(data.get("month"));
                day = Integer.parseInt(data.get("day"));
                hour = Integer.parseInt(data.get("hour"));
                minute = Integer.parseInt(data.get("minute"));
            } else if (type == 88) { // 약속
                locationName = data.get("locationName");
                latitude = Double.parseDouble(data.get("latitude"));
                longitude = Double.parseDouble(data.get("longitude"));
                meetRemindTime = Integer.parseInt(data.get("meetRemindTime"));
                year = Integer.parseInt(data.get("year"));
                month = Integer.parseInt(data.get("month"));
                day = Integer.parseInt(data.get("day"));
                hour = Integer.parseInt(data.get("hour"));
                minute = Integer.parseInt(data.get("minute"));
            }

            if (type == 77) { // LOCATION
                ToDo todo = makeGroupTodoObject(title, content, icon, LOCATION, Objects.requireNonNull(isImportant).charAt(0), groupNo);
                ToDoData toDoData = makeGroupTodoDataObject(type, locationName, latitude, longitude, locationMode, radius, ssid, Objects.requireNonNull(isWiFi).charAt(0),
                        timeSlot, repeatType, repeatDayOfWeek, repeatDay, date, time, -1, -1, -1, -1, -1, serverTodoNo);
                new InsertAsyncTask(mDatabase.todoDao()).execute(todo, toDoData);
            } else if (type == 88) { //MEET
                ToDo todo = makeGroupTodoObject(title, content, icon, MEET, Objects.requireNonNull(isImportant).charAt(0), groupNo);
                ToDoData toDoData = makeGroupTodoDataObject(type, locationName, latitude, longitude, NO_DATA, radius, ssid, 'N',
                        NO_DATA, NO_DATA, "", NO_DATA, date, time, year, month, day, hour, minute, serverTodoNo);
                toDoData.setMeetRemindTime(meetRemindTime);
                toDoData.setIsMeet('Y');
                new InsertAsyncTask(mDatabase.todoDao()).execute(todo, toDoData);
            } else if (type == 66) {
                ToDo todo = makeGroupTodoObject(title, content, icon, TIME, Objects.requireNonNull(isImportant).charAt(0), groupNo);
                ToDoData toDoData = makeGroupTodoDataObject(type, locationName, NO_DATA, NO_DATA, NO_DATA, NO_DATA, "", 'N',
                        NO_DATA, repeatType, repeatDayOfWeek, repeatDay, date, time, year, month, day, hour, minute, serverTodoNo);
                new InsertAsyncTask(mDatabase.todoDao()).execute(todo, toDoData);
            }
            ////와이파이랑 시간쪽 로직 추가 필요

        } catch (NumberFormatException numberFormatException) {
            if (type == 77) { // LOCATION
                ToDo todo = makeGroupTodoObject(title, content, icon, LOCATION, Objects.requireNonNull(isImportant).charAt(0), groupNo);
                ToDoData toDoData = makeGroupTodoDataObject(type, locationName, latitude, longitude, locationMode, radius, ssid, Objects.requireNonNull(isWiFi).charAt(0),
                        timeSlot, repeatType, repeatDayOfWeek, repeatDay, date, time, -1, -1, -1, -1, -1, serverTodoNo);
                new InsertAsyncTask(mDatabase.todoDao()).execute(todo, toDoData);
            } else if (type == 88) { //MEET
                ToDo todo = makeGroupTodoObject(title, content, icon, MEET, Objects.requireNonNull(isImportant).charAt(0), groupNo);
                ToDoData toDoData = makeGroupTodoDataObject(type, locationName, latitude, longitude, locationMode, radius, ssid, Objects.requireNonNull(isWiFi).charAt(0),
                        timeSlot, repeatType, repeatDayOfWeek, repeatDay, date, time, year, month, day, hour, minute, serverTodoNo);
                toDoData.setMeetRemindTime(meetRemindTime);
                toDoData.setIsMeet('Y');
                new InsertAsyncTask(mDatabase.todoDao()).execute(todo, toDoData);
            } else if (type == 66) {
                ToDo todo = makeGroupTodoObject(title, content, icon, TIME, Objects.requireNonNull(isImportant).charAt(0), groupNo);
                ToDoData toDoData = makeGroupTodoDataObject(type, locationName, NO_DATA, NO_DATA, NO_DATA, NO_DATA, "", 'N',
                        NO_DATA, repeatType, repeatDayOfWeek, repeatDay, date, time, year, month, day, hour, minute, serverTodoNo);
                new InsertAsyncTask(mDatabase.todoDao()).execute(todo, toDoData);
            }
            ////와이파이랑 시간쪽 로직 추가 필요 (위랑똑같이쓰면댐)
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
                getWifiMaker().registerAndUpdateWifi(getApplicationContext(), toDoData.getIsWiFi(), toDoData.getLocationMode(), true);
                Log.d("와이파이 일정 등록", "");
            } else if (toDoData.getLongitude() == NO_DATA && toDoData.getRepeatType() != NO_DATA) {//시간일정
                getAlarmMaker().registerAlarm(toDoData.getTodoNo(), toDoData.getRepeatType(), toDoData.getYear(), toDoData.getMonth(), toDoData.getDay(), toDoData.getHour(), toDoData.getMinute(), title, content, toDoData.getRepeatDayOfWeek());
            } else if (toDoData.getIsMeet() == 'Y') { //약속
                //1. 약속시간 전 알람 셋팅
                getAlarmMaker().registerAlarm(toDoData.getTodoNo(), ONE_DAY, toDoData.getYear(), toDoData.getMonth(), toDoData.getDay(), toDoData.getHour() - toDoData.getMeetRemindTime(), toDoData.getMinute(),
                        getString(R.string.notification_title_meet_1) + toDoData.getMeetRemindTime() + getString(R.string.notification_title_meet_2), content, "");
                //2. 약속작송 지오펜스 셋팅(특정시간에만 지오펜스 잡도록)
                getGeofenceMaker().addGeoFenceOneForGroupTodo("meet" + toDoData.getTodoNo(), toDoData.getLatitude(), toDoData.getLongitude(), AT_ARRIVE, toDoData.getRadius(),
                        new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Log.d("약속이 추가되었습니다", " = " + "meet" + toDoData.getTodoNo());
                                //서버에 성공했다고 api통신
                                todoRegisterSuccess(toDoData.getSeverTodoNo());
                            }
                        },
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //지오펜스 실패하면 db에사도 지워줘야함
                                new FirebaseMessagingService.DeleteToDoAsyncTask().execute(toDoData);
                                Log.e("지오펜스 등록 실패", e.toString());
                            }
                        });
            } else if (toDoData.getRepeatType() == NO_DATA && toDoData.getLongitude() != NO_DATA) {//위치일정
                getGeofenceMaker().addGeoFenceOneForGroupTodo("group" + toDoData.getTodoNo(), toDoData.getLatitude(), toDoData.getLongitude(), toDoData.getLocationMode(), toDoData.getRadius(),
                        new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Log.d("할 일이 추가되었습니다", " = " + "group" + toDoData.getTodoNo());
                                //서버에 성공했다고 api통신
                                todoRegisterSuccess(toDoData.getSeverTodoNo());
                            }
                        },
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //지오펜스 실패하면 db에사도 지워줘야함
                                new FirebaseMessagingService.DeleteToDoAsyncTask().execute(toDoData);
                                Log.e("지오펜스 등록 실패", e.toString());
                            }
                        });
            }
        }
    }

    private class DeleteToDoAsyncTask extends AsyncTask<ToDoData, Void, ToDoData> {

        @Override
        protected ToDoData doInBackground(ToDoData... toDoData) {
            mDatabase.todoDao().deleteToDo(toDoData[0].getTodoNo());
            mDatabase.todoDao().deleteToDoData(toDoData[0].getTodoDataNo());
            return toDoData[0];
        }
    }

    private ToDo makeGroupTodoObject(String title, String content, int icon, int type, char importantMode, int groupNo) {
        ToDo todo = new ToDo(title, content, icon, type, importantMode, 'Y');
        todo.setGroupNo(groupNo);
        return todo;
    }

    private ToDoData makeGroupTodoDataObject(int type, String locationName, double latitude, double longitude, int locationMode, int radius, String ssid, char wifiMode,
                                             int timeSlot, int repeatType, String repeatDayOfWeek, int repeatDay, String date, String time, int year, int month, int day, int hour, int minute, int serverTodoNo) {
        if (type == LOCATION) {
            return new ToDoData(locationName,
                    latitude, longitude, locationMode, radius,
                    ssid, wifiMode, timeSlot, NO_DATA, "", NO_DATA, "", "", NO_DATA, NO_DATA, NO_DATA, NO_DATA, NO_DATA, serverTodoNo);
        } else if (type == TIME) {
            return new ToDoData(locationName,
                    NO_DATA, NO_DATA, NO_DATA, NO_DATA,
                    "", 'N', NO_DATA, repeatType, repeatDayOfWeek, repeatDay, date, time, year, month, day, hour, minute, serverTodoNo);
        } else if (type == MEET) {
            return new ToDoData(locationName,
                    latitude, longitude, NO_DATA, radius,
                    "", 'N', NO_DATA, NO_DATA, "", NO_DATA, date, time, year, month, day, hour, minute, serverTodoNo);
        } else {
            return new ToDoData(locationName,
                    NO_DATA, NO_DATA, NO_DATA, NO_DATA,
                    "", 'N', NO_DATA, NO_DATA, "", NO_DATA, date, time, year, month, day, hour, minute, serverTodoNo);
        }
    }

    void todoRegisterSuccess(int todoNo) {
        final FirebaseRetrofitInterface firebaseRetrofitInterface = getRetrofit().create(FirebaseRetrofitInterface.class);
        firebaseRetrofitInterface.todoRegisterSuccess(todoNo).enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                final DefaultResponse defaultResponse = response.body();
                if (defaultResponse == null) {
                } else {
                    sendNotification("일정에 초대되었습니다.", title);
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
            }
        });
    }
}
