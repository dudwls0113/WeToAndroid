package com.ninano.weto.src.common.group;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ninano.weto.R;
import com.ninano.weto.db.AppDatabase;
import com.ninano.weto.db.ToDo;
import com.ninano.weto.db.ToDoDao;
import com.ninano.weto.db.ToDoData;
import com.ninano.weto.src.common.FirebaseMessagingService;

import java.util.Objects;

import static com.ninano.weto.src.ApplicationClass.AT_ARRIVE;
import static com.ninano.weto.src.ApplicationClass.GPS_LADIUS;
import static com.ninano.weto.src.ApplicationClass.LOCATION;
import static com.ninano.weto.src.ApplicationClass.MEET;
import static com.ninano.weto.src.ApplicationClass.NO_DATA;
import static com.ninano.weto.src.ApplicationClass.ONE_DAY;
import static com.ninano.weto.src.ApplicationClass.TIME;
import static com.ninano.weto.src.ApplicationClass.getApplicationClassContext;
import static com.ninano.weto.src.common.alarm.AlarmMaker.getAlarmMaker;
import static com.ninano.weto.src.common.geofence.GeofenceMaker.getGeofenceMaker;
import static com.ninano.weto.src.common.util.Util.sendNotification;
import static com.ninano.weto.src.common.wifi.WifiMaker.getWifiMaker;

public class GroupTodoMaker {
    public GroupTodoMaker(Context mContext, String title, String content, String isImportant, String locationName, String isWiFi, String ssid,
                          String repeatDayOfWeek, String date, String time, int icon, int type, int serverTodoNo, int groupNo, int locationMode,
                          int timeSlot, int repeatDay, int year, int month, int day, int hour, int minute, int repeatType, int meetRemindTime,
                          double latitude, double longitude, GroupTodoMakerCallBack groupTodoMakerCallback) {
        this.mContext = mContext;
        this.title = title;
        this.content = content;
        this.isImportant = isImportant;
        this.locationName = locationName;
        this.isWiFi = isWiFi;
        this.ssid = ssid;
        this.repeatDayOfWeek = repeatDayOfWeek;
        this.date = date;
        this.time = time;
        this.icon = icon;
        this.type = type;
        this.serverTodoNo = serverTodoNo;
        this.groupNo = groupNo;
        this.locationMode = locationMode;
        this.timeSlot = timeSlot;
        this.repeatDay = repeatDay;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.repeatType = repeatType;
        this.meetRemindTime = meetRemindTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mDatabase = AppDatabase.getAppDatabase(getApplicationClassContext());
        this.mGroupTodoMakerCallback = groupTodoMakerCallback;
    }

    public interface GroupTodoMakerCallBack {
        void groupTodoMakeSuccess(int serverTodoNo);

        void groupTodoMakeFail();
    }

    GroupTodoMakerCallBack mGroupTodoMakerCallback;

    private Context mContext;
    String title, content, isImportant, locationName, isWiFi, ssid, repeatDayOfWeek, date, time;
    int icon, type, serverTodoNo, groupNo, locationMode, timeSlot, repeatDay;
    int year, month, day, hour, minute, repeatType, meetRemindTime;
    double latitude, longitude;
    int radius = GPS_LADIUS;
    AppDatabase mDatabase;

    public void makeGroupTodo() {

        //1. DB에 추가하기
        if (type == 77) { // LOCATION
            ToDo todo = makeGroupTodoObject(title, content, icon, LOCATION, Objects.requireNonNull(isImportant).charAt(0), groupNo);
            ToDoData toDoData = makeGroupTodoDataObject(type, locationName, latitude, longitude, locationMode, radius, ssid, Objects.requireNonNull(isWiFi).charAt(0),
                    timeSlot, repeatType, repeatDayOfWeek, repeatDay, date, time, -1, -1, -1, -1, -1, serverTodoNo);
            new InsertAsyncTask().execute(todo, toDoData);
        } else if (type == 88) { //MEET
            ToDo todo = makeGroupTodoObject(title, content, icon, MEET, Objects.requireNonNull(isImportant).charAt(0), groupNo);
            ToDoData toDoData = makeGroupTodoDataObject(type, locationName, latitude, longitude, NO_DATA, radius, ssid, 'N',
                    NO_DATA, NO_DATA, "", NO_DATA, date, time, year, month, day, hour, minute, serverTodoNo);
            toDoData.setMeetRemindTime(meetRemindTime);
            toDoData.setIsMeet('Y');
            new InsertAsyncTask().execute(todo, toDoData);
        } else if (type == 66) {
            ToDo todo = makeGroupTodoObject(title, content, icon, TIME, Objects.requireNonNull(isImportant).charAt(0), groupNo);
            ToDoData toDoData = makeGroupTodoDataObject(type, locationName, NO_DATA, NO_DATA, NO_DATA, NO_DATA, "", 'N',
                    NO_DATA, repeatType, repeatDayOfWeek, repeatDay, date, time, year, month, day, hour, minute, serverTodoNo);
            new InsertAsyncTask().execute(todo, toDoData);
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

    //비동기처리
    @SuppressLint("StaticFieldLeak")
    private class InsertAsyncTask extends AsyncTask<Object, Void, ToDoData> {

        @Override
        protected ToDoData doInBackground(Object... toDos) {
            int todoNo = mDatabase.todoDao().insertTodo((ToDo) toDos[0], (ToDoData) toDos[1]);
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
                getWifiMaker().registerAndUpdateWifi(mContext, toDoData.getIsWiFi(), toDoData.getLocationMode(), true);
                Log.d("와이파이 일정 등록", "");
            } else if (toDoData.getLongitude() == NO_DATA && toDoData.getRepeatType() != NO_DATA) {//시간일정
                getAlarmMaker().registerAlarm(toDoData.getTodoNo(), toDoData.getRepeatType(), toDoData.getYear(), toDoData.getMonth(), toDoData.getDay(), toDoData.getHour(), toDoData.getMinute(), title, content, toDoData.getRepeatDayOfWeek());
            } else if (toDoData.getIsMeet() == 'Y') { //약속
                //1. 약속시간 전 알람 셋팅
                getAlarmMaker().registerAlarm(toDoData.getTodoNo(), ONE_DAY, toDoData.getYear(), toDoData.getMonth(), toDoData.getDay(), toDoData.getHour() - toDoData.getMeetRemindTime(), toDoData.getMinute(),
                        mContext.getString(R.string.notification_title_meet_1) + toDoData.getMeetRemindTime() + mContext.getString(R.string.notification_title_meet_2), content, "");
                //2. 약속작송 지오펜스 셋팅(특정시간에만 지오펜스 잡도록)
                getGeofenceMaker().addGeoFenceOneForGroupTodo("meet" + toDoData.getTodoNo(), toDoData.getLatitude(), toDoData.getLongitude(), AT_ARRIVE, toDoData.getRadius(),
                        new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Log.d("약속이 추가되었습니다", " = " + "meet" + toDoData.getTodoNo());
                                //서버에 성공했다고 api통신
                                mGroupTodoMakerCallback.groupTodoMakeSuccess(toDoData.getSeverTodoNo());
                            }
                        },
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //지오펜스 실패하면 db에사도 지워줘야함
                                new DeleteToDoAsyncTask().execute(toDoData);
                                Log.e("지오펜스 등록 실패", e.toString());
                                mGroupTodoMakerCallback.groupTodoMakeFail();
                            }
                        });
            } else if (toDoData.getRepeatType() == NO_DATA && toDoData.getLongitude() != NO_DATA) {//위치일정
                getGeofenceMaker().addGeoFenceOneForGroupTodo("group" + toDoData.getTodoNo(), toDoData.getLatitude(), toDoData.getLongitude(), toDoData.getLocationMode(), toDoData.getRadius(),
                        new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Log.d("할 일이 추가되었습니다", " = " + "group" + toDoData.getTodoNo());
                                //서버에 성공했다고 api통신
                                mGroupTodoMakerCallback.groupTodoMakeSuccess(toDoData.getSeverTodoNo());
                            }
                        },
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //지오펜스 실패하면 db에사도 지워줘야함
                                new DeleteToDoAsyncTask().execute(toDoData);
                                Log.e("지오펜스 등록 실패", e.toString());
                                mGroupTodoMakerCallback.groupTodoMakeFail();
                            }
                        });
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DeleteToDoAsyncTask extends AsyncTask<ToDoData, Void, ToDoData> {

        @Override
        protected ToDoData doInBackground(ToDoData... toDoData) {
            mDatabase.todoDao().deleteToDo(toDoData[0].getTodoNo());
            mDatabase.todoDao().deleteToDoData(toDoData[0].getTodoDataNo());
            return toDoData[0];
        }
    }
}
