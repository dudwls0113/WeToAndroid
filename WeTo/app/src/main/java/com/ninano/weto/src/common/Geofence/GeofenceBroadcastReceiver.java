package com.ninano.weto.src.common.Geofence;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.ninano.weto.R;
import com.ninano.weto.db.AppDatabase;
import com.ninano.weto.db.ToDoDao;
import com.ninano.weto.db.ToDoWithData;
import com.ninano.weto.src.main.MainActivity;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_DWELL;
import static com.ninano.weto.src.ApplicationClass.AT_ARRIVE;
import static com.ninano.weto.src.ApplicationClass.AT_START;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    AppDatabase mDatabase;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("지오", "수신 첫번째" );
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e("지오", errorMessage);
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition(); // 발생 이벤트 타입

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT || geofenceTransition == GEOFENCE_TRANSITION_DWELL) {

            mDatabase = AppDatabase.getAppDatabase(context);

            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            String geofenceTransitionDetails;

            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                geofenceTransitionDetails = "ENTER";
            } else {
                geofenceTransitionDetails = "EXIT";
            }

            geofenceTransitionDetails += triggeringGeofences.get(0).getRequestId();
            Toast.makeText(context, geofenceTransitionDetails, Toast.LENGTH_LONG).show();
            Log.d("지오", "수신" + geofenceTransitionDetails);
            try {
                List<ToDoWithData> toDoWithDataList = new DbAsyncTask(mDatabase.todoDao()).execute(Integer.valueOf(triggeringGeofences.get(0).getRequestId())).get();
                if (toDoWithDataList.size() > 0) {
                    Log.d("지오", toDoWithDataList.get(0).toString());
                    if (toDoWithDataList.get(0).getStatus().equals("ACTIVATE")) { //활성중인 일정이면 알림보내기다
                        //시간조건 추가 필요
                        sendNotification(context, toDoWithDataList.get(0));
                    }
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        } else {
            // Log the error.
            Log.e("지오", "");
        }
    }

    //비동기처리                                   //넘겨줄객체, 중간에 처리할 데이터, 결과물(return)
    private class DbAsyncTask extends AsyncTask<Integer, Void, List<ToDoWithData>> {
        private ToDoDao mTodoDao;

        DbAsyncTask(ToDoDao mTodoDao) {
            this.mTodoDao = mTodoDao;
        }

        @Override
        protected List<ToDoWithData> doInBackground(Integer... integers) {
            Log.e("지오", "doInBackground");
            List<ToDoWithData> toDoWithData = mDatabase.todoDao().getTodoWithTodoNo(integers[0]);
            for (int i = 0; i < toDoWithData.size(); i++) {
                Log.d("조회된  일정 ", toDoWithData.get(i).getLocationName() + toDoWithData.get(i).getTodoNo());
            }

            //조회된 일정으로 어떤 알림 보낼 지 파악
            if (toDoWithData.size() > 0) {
            }

            return toDoWithData;
        }
    }

    private void sendNotification(Context context, ToDoWithData toDoWithData) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final String CHANNEL_ID = "채널ID";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final String CHANNEL_NAME = "채널이름";
            final String CHANNEL_DESCRIPTION = "채널 Description";
            final int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            mChannel.setDescription(CHANNEL_DESCRIPTION);
            mChannel.enableLights(true);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(mChannel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle(toDoWithData.getTitle());
        if (toDoWithData.getLocationMode() == AT_ARRIVE) {
            builder.setContentText(toDoWithData.getLocationName() + "에 도착하였습니다");
        } else if (toDoWithData.getLocationMode() == AT_START) {
            builder.setContentText(toDoWithData.getLocationName() + "에서 출발하였습니다");
        } else {
            builder.setContentText(toDoWithData.getLocationName() + "을(를) 지나치고 있습니");
        }
        builder.setContentIntent(pendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setAutoCancel(true);
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }
}