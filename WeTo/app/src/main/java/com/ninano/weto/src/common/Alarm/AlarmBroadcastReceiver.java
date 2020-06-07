package com.ninano.weto.src.common.Alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.ninano.weto.R;
import com.ninano.weto.src.main.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int alarmIdx = intent.getIntExtra("alarmIndex", -1);
        if (alarmIdx == -1){
            return;
        }
        String title = intent.getStringExtra("title");
        String memo = intent.getStringExtra("memo");

        int repeatType = intent.getIntExtra("repeatType", -1); // 1: 매일, 2: 매주, 3: 매월, 4: 특정일
        if (repeatType==-1){
            return;
        }
        if (repeatType==1){
            sendNotification(context, title, memo);
        }
        else if(repeatType==2){
            String dayOfWeek = "";
            dayOfWeek = intent.getStringExtra("repeatDayOfWeek");
            if (dayOfWeek.equals("")){
                return;
            }
            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat weekdayFormat = new SimpleDateFormat("EE", Locale.getDefault());
            String weekDay = weekdayFormat.format(currentTime);
            Toast.makeText(context, weekDay, Toast.LENGTH_LONG).show();
            if (dayOfWeek.contains(weekDay)){
                sendNotification(context, title, memo);
            }
        }
        else if(repeatType==3){

        }
        else if (repeatType == 4) {
            sendNotification(context, title, memo);
            // 특정일의 경우 반복이 없으므로 여기서 완료로 수정???
        }
    }

    void sendNotification(Context context, String title, String memo){
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

        final String CHANNEL_ID = "채널ID";
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
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
        builder.setContentTitle(title);
        builder.setContentText(memo);
        builder.setContentIntent(pendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setAutoCancel(true);
        if (notificationManager != null) {
            notificationManager.notify(3, builder.build());
        }
    }
}
