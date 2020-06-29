package com.ninano.weto.src.common.util;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.ninano.weto.R;
import com.ninano.weto.db.ToDoWithData;
import com.ninano.weto.src.main.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.ninano.weto.src.ApplicationClass.AT_ARRIVE;
import static com.ninano.weto.src.ApplicationClass.AT_START;
import static com.ninano.weto.src.ApplicationClass.CHANNEL_ID;
import static com.ninano.weto.src.ApplicationClass.getApplicationClassContext;
import static com.ninano.weto.src.ApplicationClass.retrofit;

public class Util {

    public static double getDistance(double lat1, double lon1, double lat2, double lon2, String unit) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit.equals("kilometer")) {
            dist = dist * 1.609344;
        } else if (unit.equals("meter")) {
            dist = dist * 1609.344;
        }

        return (dist);
    }

    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public static boolean compareTimeSlot(int timeSlot) {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfNow = new SimpleDateFormat("HH");
        String hour = sdfNow.format(date);
        int currentHour = Integer.parseInt(hour);
        System.out.println("시간: " + currentHour);
        if (timeSlot == 100) {
            return true;
        } else if (timeSlot == 200) {
            return currentHour >= 6 && currentHour < 12;
        } else if (timeSlot == 300) {
            return currentHour >= 12 && currentHour < 21;
        } else if (timeSlot == 400) {
            return currentHour >= 21 && currentHour < 24 || currentHour >= 0 && currentHour < 6;
        } else {
            return false;
        }
    }

    public static String getLocationNotificationContent(ToDoWithData toDoWithData) {
        if (toDoWithData.getLocationMode() == AT_ARRIVE) {
            return (toDoWithData.getLocationName() + "에 도착하였습니다");
        } else if (toDoWithData.getLocationMode() == AT_START) {
            return (toDoWithData.getLocationName() + "에서 출발하였습니다");
        } else {
            return (toDoWithData.getLocationName() + "을(를) 지나치고 있습니다");
        }
    }

    public static String getWifiNotificationContent(ToDoWithData toDoWithData) {
        if (toDoWithData.getLocationMode() == AT_ARRIVE) {
            return (toDoWithData.getLocationName() + "에 연결되었습니다");
        } else if (toDoWithData.getLocationMode() == AT_START) {
            return (toDoWithData.getLocationName() + "에서 연결해제되었습니다.");
        } else {
            return (toDoWithData.getLocationName() + "을(를) 지나치고 있습니다");
        }
    }

    public static void sendNotification(String title, String content) {
        System.out.println("노티발생");
        Intent notificationIntent = new Intent(getApplicationClassContext(), MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationClassContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notificationManager = (NotificationManager) getApplicationClassContext().getSystemService(Context.NOTIFICATION_SERVICE);

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

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationClassContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.app_icon);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setContentIntent(pendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setAutoCancel(true);
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }
}
