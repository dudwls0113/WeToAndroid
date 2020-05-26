package com.ninano.weto.src.receiver;

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
        Toast.makeText(context, "알람~!!", Toast.LENGTH_SHORT).show();

        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
        Date currentTime = Calendar.getInstance().getTime();
        String day = dayFormat.format(currentTime);

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
        builder.setContentTitle("알람 감지");
        builder.setContentText(day + "일 입니다.");
        builder.setContentIntent(pendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setAutoCancel(true);
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }
}
