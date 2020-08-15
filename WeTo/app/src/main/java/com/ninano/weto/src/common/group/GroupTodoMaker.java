package com.ninano.weto.src.common.group;

import android.content.Context;
import android.util.Log;

import com.ninano.weto.db.AppDatabase;
import com.ninano.weto.db.ToDo;
import com.ninano.weto.db.ToDoData;
import com.ninano.weto.src.common.models.ServerTodoWithTodoNo;
import com.ninano.weto.src.common.util.DBAsyncTask.CheckDuplicatedServerTodoAsyncTask;
import com.ninano.weto.src.common.util.DBAsyncTask.TodoInsertAsyncTask;
import com.ninano.weto.src.common.util.DBAsyncTask.TodoUpdateAsyncTask;

import java.util.Objects;

import static com.ninano.weto.src.ApplicationClass.GPS_LADIUS;
import static com.ninano.weto.src.ApplicationClass.GROUP_TODO_MAKE_FROM_FCM;
import static com.ninano.weto.src.ApplicationClass.GROUP_TODO_MAKE_FROM_LOCAL;
import static com.ninano.weto.src.ApplicationClass.LOCATION;
import static com.ninano.weto.src.ApplicationClass.MEET;
import static com.ninano.weto.src.ApplicationClass.NO_DATA;
import static com.ninano.weto.src.ApplicationClass.TIME;
import static com.ninano.weto.src.ApplicationClass.getApplicationClassContext;

public class GroupTodoMaker {
    public GroupTodoMaker(int makeMode, Context mContext, String title, String content, String isImportant, String locationName, String isWiFi, String ssid,
                          String repeatDayOfWeek, String date, String time, int icon, int type, int serverTodoNo, int groupNo, int locationMode,
                          int timeSlot, int repeatDay, int year, int month, int day, int hour, int minute, int repeatType, int meetRemindTime,
                          double latitude, double longitude, GroupTodoMakerCallBack groupTodoMakerCallback) {
        this.makeMode = makeMode;
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
        void groupTodoMakeSuccess(); //로컬에서 처음 그룹일정 등록 -> 이후작업: 없음

        void groupTodoMakeFail(String message);

        void groupTodoMakeSuccessFromFCM(int serverTodoNo); //fcm받아서 그룹일정 등록 -> 이후작업: 서버로 알리기

        void groupTodoUpdateSuccessFromFCM(int serverTodoNo); //fcm받아서 그룹일정 수정 -> 이후작업: 서버로 알리기
    }

    GroupTodoMakerCallBack mGroupTodoMakerCallback;

    private int makeMode;
    private Context mContext;
    String title, content, isImportant, locationName, isWiFi, ssid, repeatDayOfWeek, date, time;
    int icon, type, serverTodoNo, groupNo, locationMode, timeSlot, repeatDay;
    int year, month, day, hour, minute, repeatType, meetRemindTime;
    double latitude, longitude;
    int radius = GPS_LADIUS;
    AppDatabase mDatabase;

    public void makeGroupTodo() {
        ToDo todo = null;
        ToDoData toDoData = null;
        //1. DB에 추가하기
        if (type == 77) { // LOCATION
            todo = makeGroupTodoObject(title, content, icon, LOCATION, Objects.requireNonNull(isImportant).charAt(0), groupNo);
            toDoData = makeGroupTodoDataObject(type, locationName, latitude, longitude, locationMode, radius, ssid, Objects.requireNonNull(isWiFi).charAt(0),
                    timeSlot, repeatType, repeatDayOfWeek, repeatDay, date, time, -1, -1, -1, -1, -1, serverTodoNo);
        } else if (type == 88) { //MEET
            todo = makeGroupTodoObject(title, content, icon, MEET, Objects.requireNonNull(isImportant).charAt(0), groupNo);
            toDoData = makeGroupTodoDataObject(type, locationName, latitude, longitude, NO_DATA, radius, ssid, 'N',
                    NO_DATA, NO_DATA, "", NO_DATA, date, time, year, month, day, hour, minute, serverTodoNo);
            toDoData.setMeetRemindTime(meetRemindTime);
            toDoData.setIsMeet('Y');
        } else if (type == 66) {
            todo = makeGroupTodoObject(title, content, icon, TIME, Objects.requireNonNull(isImportant).charAt(0), groupNo);
            toDoData = makeGroupTodoDataObject(type, locationName, NO_DATA, NO_DATA, NO_DATA, NO_DATA, "", 'N',
                    NO_DATA, repeatType, repeatDayOfWeek, repeatDay, date, time, year, month, day, hour, minute, serverTodoNo);
        }
        assert toDoData != null;
        final ToDo finalTodo = todo;
        final ToDoData finalToDoData = toDoData;
        CheckDuplicatedServerTodoAsyncTask checkDuplicatedServerTodoAsyncTask = new CheckDuplicatedServerTodoAsyncTask(mDatabase.todoDao(), new CheckDuplicatedServerTodoAsyncTask.CheckDuplicatedServerTodoAsyncTaskCallBack() {
            @Override
            public void duplicated(final ServerTodoWithTodoNo serverTodoWithTodoNo) {
                //이미 있는 그룹 일정은 중복입력 하지 않도록 업데이트 필요
                TodoUpdateAsyncTask todoUpdateAsyncTask = new TodoUpdateAsyncTask(mDatabase.todoDao(), new TodoUpdateAsyncTask.TodoUpdateAsyncTaskCallBack() {
                    @Override
                    public void updateSuccess() {
                        Log.d("일정 업데이트 성공", serverTodoWithTodoNo.getSeverTodoNo() + "");
                        mGroupTodoMakerCallback.groupTodoUpdateSuccessFromFCM(serverTodoWithTodoNo.getSeverTodoNo());
                    }

                    @Override
                    public void updateFail(String message) {
                        mGroupTodoMakerCallback.groupTodoMakeFail(message);
                    }
                });
                todoUpdateAsyncTask.execute(finalTodo, finalToDoData, serverTodoWithTodoNo);
            }

            @Override
            public void notDuplicated() {
                TodoInsertAsyncTask todoInsertAsyncTask = new TodoInsertAsyncTask(mDatabase.todoDao(), new TodoInsertAsyncTask.TodoInsertAsyncTaskCallBack() {
                    @Override
                    public void insertSuccess(int serverTodoNo) {
                        if (makeMode == GROUP_TODO_MAKE_FROM_LOCAL) {
                            mGroupTodoMakerCallback.groupTodoMakeSuccess();

                        } else if (makeMode == GROUP_TODO_MAKE_FROM_FCM) {
                            mGroupTodoMakerCallback.groupTodoMakeSuccessFromFCM(finalToDoData.getSeverTodoNo());
                        }
                    }

                    @Override
                    public void insertFail(String message) {
                        mGroupTodoMakerCallback.groupTodoMakeFail(message);
                    }
                });
                todoInsertAsyncTask.execute(finalTodo, finalToDoData);
            }
        });
        checkDuplicatedServerTodoAsyncTask.execute(toDoData.getSeverTodoNo());
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
}
