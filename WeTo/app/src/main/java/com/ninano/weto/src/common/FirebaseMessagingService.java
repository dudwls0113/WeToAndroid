package com.ninano.weto.src.common;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.RemoteMessage;
import com.ninano.weto.db.AppDatabase;
import com.ninano.weto.src.DefaultResponse;
import com.ninano.weto.src.common.group.GroupTodoMaker;

import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ninano.weto.src.ApplicationClass.GPS_LADIUS;

import static com.ninano.weto.src.ApplicationClass.GROUP_TODO_MAKE_FROM_FCM;
import static com.ninano.weto.src.ApplicationClass.getApplicationClassContext;
import static com.ninano.weto.src.ApplicationClass.getRetrofit;
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
//        int ordered = Integer.parseInt(data.get("ordered"));
//        String status = data.get("status");
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

            GroupTodoMaker groupTodoMaker = new GroupTodoMaker(GROUP_TODO_MAKE_FROM_FCM, getApplicationContext(), title, content, isImportant, locationName, isWiFi, ssid,
                    repeatDayOfWeek, date, time, icon, type, serverTodoNo, groupNo, locationMode,
                    timeSlot, repeatDay, year, month, day, hour, minute, repeatType, meetRemindTime,
                    latitude, longitude, new GroupTodoMaker.GroupTodoMakerCallBack() {
                @Override
                public void groupTodoMakeSuccess() {

                }

                @Override
                public void groupTodoMakeFail(String message) {

                }

                @Override
                public void groupTodoMakeSuccessFromFCM(int serverTodoNo) {
                    todoRegisterSuccess(serverTodoNo);
                }

                @Override
                public void groupTodoUpdateSuccessFromFCM(int serverTodoNo) {
                    todoRegisterSuccess(serverTodoNo);
                }

            });
            groupTodoMaker.makeGroupTodo();

        } catch (NumberFormatException numberFormatException) {
            GroupTodoMaker groupTodoMaker = new GroupTodoMaker(GROUP_TODO_MAKE_FROM_FCM, getApplicationContext(), title, content, isImportant, locationName, isWiFi, ssid,
                    repeatDayOfWeek, date, time, icon, type, serverTodoNo, groupNo, locationMode,
                    timeSlot, repeatDay, year, month, day, hour, minute, repeatType, meetRemindTime,
                    latitude, longitude, new GroupTodoMaker.GroupTodoMakerCallBack() {
                @Override
                public void groupTodoMakeSuccess() {

                }

                @Override
                public void groupTodoMakeFail(String message) {

                }

                @Override
                public void groupTodoMakeSuccessFromFCM(int serverTodoNo) {
                    todoRegisterSuccess(serverTodoNo);
                }

                @Override
                public void groupTodoUpdateSuccessFromFCM(int serverTodoNo) {
                    todoRegisterSuccess(serverTodoNo);
                }
            });
            groupTodoMaker.makeGroupTodo();
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
