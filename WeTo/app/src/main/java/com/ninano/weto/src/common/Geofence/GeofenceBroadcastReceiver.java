package com.ninano.weto.src.common.Geofence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.ninano.weto.R;
import com.ninano.weto.db.AppDatabase;
import com.ninano.weto.db.ToDoDao;
import com.ninano.weto.db.ToDoWithData;
import com.ninano.weto.src.DefaultResponse;
import com.ninano.weto.src.common.FirebaseRetrofitInterface;
import com.ninano.weto.src.common.util.Util;
import com.ninano.weto.src.main.MainActivity;

import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_DWELL;
import static com.ninano.weto.src.ApplicationClass.AT_ARRIVE;
import static com.ninano.weto.src.ApplicationClass.AT_START;
import static com.ninano.weto.src.ApplicationClass.getRetrofit;
import static com.ninano.weto.src.common.util.Util.compareTimeSlot;
import static com.ninano.weto.src.common.util.Util.getLocationNotificationContent;
import static com.ninano.weto.src.common.util.Util.isNumericString;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    AppDatabase mDatabase;

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        String errorMessage;
        if (geofencingEvent.hasError()) {
            errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition(); // 발생 이벤트 타입

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT || geofenceTransition == GEOFENCE_TRANSITION_DWELL) {
            mDatabase = AppDatabase.getAppDatabase(context);
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            System.out.println("지오펜스1");
            try {
                for (Geofence geofence : triggeringGeofences) {
                    if (isNumericString(geofence.getRequestId())) {
                        ToDoWithData toDoWithData = new DbAsyncTask().execute(Integer.valueOf(geofence.getRequestId())).get();
                        if (toDoWithData == null && !(toDoWithData.getStatus().equals("ACTIVATE"))) {
                            return;
                        } else {
                            if (compareTimeSlot(toDoWithData.getTimeSlot())) {//타임슬롯 체크
                                Util.sendNotification(toDoWithData.getTitle(), getLocationNotificationContent(toDoWithData));
                            }
                        }
                    } else {
                        //그룹일정 (Caused by: java.lang.NumberFormatException: For input string: "group2")
                        Log.d("리퀘스트", triggeringGeofences.get(0).getRequestId());
                        if (triggeringGeofences.get(0).getRequestId().contains("group")) {
                            ToDoWithData toDoWithData = new DbAsyncTask().execute(Integer.valueOf(triggeringGeofences.get(0).getRequestId().substring(5))).get();
                            if (toDoWithData != null || toDoWithData.getStatus().equals("ACTIVATE")) {
                                if (compareTimeSlot(toDoWithData.getTimeSlot())) {//타임슬롯 체크
                                    Util.sendNotification(toDoWithData.getTitle(), getLocationNotificationContent(toDoWithData));
                                }
                            } else { //Todo: 서버 거쳐서 유효한 일정인지 확인 필요
                                //없는 일정이므로 알림 X
                            }
                        } else {//그룹 약속
                            //(Caused by: java.lang.NumberFormatException: For input string: "meet2")
                            Log.d("리퀘스트", Integer.valueOf(triggeringGeofences.get(0).getRequestId().substring(4)) + "meet번호");
                            ToDoWithData toDoWithData = new DbAsyncTask().execute(Integer.valueOf(triggeringGeofences.get(0).getRequestId().substring(5))).get();
                            toDoWithData = new DbAsyncTask().execute(Integer.valueOf(triggeringGeofences.get(0).getRequestId().substring(4))).get();

                            if (toDoWithData != null || toDoWithData.getStatus().equals("ACTIVATE")) {
                                Log.d("리퀘스트", toDoWithData.getSeverTodoNo() + " serverTodo");
                                arrivePush(toDoWithData.getSeverTodoNo());
                            } else { //Todo: 서버 거쳐서 유효한 일정인지 확인 필요
                                //없는 일정이므로 알림 X
                            }
                        }
                    }
                }
            } catch (ExecutionException | InterruptedException | NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    //비동기처리                                   //넘겨줄객체, 중간에 처리할 데이터, 결과물(return)
    private class DbAsyncTask extends AsyncTask<Integer, Void, ToDoWithData> {

        DbAsyncTask() {

        }

        @Override
        protected ToDoWithData doInBackground(Integer... integers) {
            List<ToDoWithData> toDoWithData = mDatabase.todoDao().getTodoWithTodoNo(integers[0]);
            for (int i = 0; i < toDoWithData.size(); i++) {
                Log.d("조회된  일정 ", toDoWithData.get(i).getLocationName() + toDoWithData.get(i).getTodoNo());
            }
            if (toDoWithData.size() > 0)
                return toDoWithData.get(0);
            else
                return null;
        }
    }

    void arrivePush(int todoNo) {
        final FirebaseRetrofitInterface firebaseRetrofitInterface = getRetrofit().create(FirebaseRetrofitInterface.class);
        firebaseRetrofitInterface.arrivePush(todoNo).enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                final DefaultResponse defaultResponse = response.body();
                if (defaultResponse == null) {
                } else {
                    Log.d("", defaultResponse.toString());
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
            }
        });
    }
}