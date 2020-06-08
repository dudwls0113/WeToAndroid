package com.ninano.weto.src.common.Alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.ninano.weto.src.DeviceBootReceiver;
import com.ninano.weto.src.todo_add.AddPersonalToDoActivity;

import java.util.Calendar;

import static com.ninano.weto.src.ApplicationClass.ALL_DAY;
import static com.ninano.weto.src.ApplicationClass.MONTH_DAY;
import static com.ninano.weto.src.ApplicationClass.ONE_DAY;
import static com.ninano.weto.src.ApplicationClass.WEEK_DAY;
import static com.ninano.weto.src.ApplicationClass.getApplicationClassContext;

public class AlarmMaker {

    private static AlarmMaker AlarmMakerInstance = null;

    public static AlarmMaker getAlarmMaker(){
        if(AlarmMakerInstance==null){
            AlarmMakerInstance = new AlarmMaker();
        }

        return AlarmMakerInstance;
    }

    private AlarmManager mAlarmManager;

    public void registerAlarm(int mToDoNo, int mRepeatType, int mYear, int mMonth, int mDay, int mHour, int mMinute, String title, String content, String mRepeatDayOfWeek){
        AlarmManager mAlarmManager = (AlarmManager) getApplicationClassContext().getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (mRepeatType == ONE_DAY) {
            calendar.set(Calendar.YEAR, mYear);
            switch (mMonth) {
                case 1:
                    calendar.set(Calendar.MONTH, Calendar.JANUARY);
                    break;
                case 2:
                    calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
                    break;
                case 3:
                    calendar.set(Calendar.MONTH, Calendar.MARCH);
                    break;
                case 4:
                    calendar.set(Calendar.MONTH, Calendar.APRIL);
                    break;
                case 5:
                    calendar.set(Calendar.MONTH, Calendar.MAY);
                    break;
                case 6:
                    calendar.set(Calendar.MONTH, Calendar.JUNE);
                    break;
                case 7:
                    calendar.set(Calendar.MONTH, Calendar.JULY);
                    break;
                case 8:
                    calendar.set(Calendar.MONTH, Calendar.AUGUST);
                    break;
                case 9:
                    calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
                    break;
                case 10:
                    calendar.set(Calendar.MONTH, Calendar.OCTOBER);
                    break;
                case 11:
                    calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
                    break;
                case 12:
                    calendar.set(Calendar.MONTH, Calendar.DECEMBER);
                    break;

            }
            calendar.set(Calendar.DATE, mDay);
            calendar.set(Calendar.HOUR_OF_DAY, mHour);
            calendar.set(Calendar.MINUTE, mMinute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            // 특정날은 이미 지나간 시간이면 선택 안되게
//            if (calendar.before(Calendar.getInstance())) {
//                calendar.add(Calendar.DATE, 1);
//            }

            System.out.println("특정 알람 시간: " + calendar.getTime().toString() + ", " + calendar.getTimeInMillis());
            PackageManager pm = getApplicationClassContext().getPackageManager();
            ComponentName receiver = new ComponentName(getApplicationClassContext(), DeviceBootReceiver.class);

            Intent intent = new Intent(getApplicationClassContext(), AlarmBroadcastReceiver.class);
            intent.putExtra("repeatType", 4);
            intent.putExtra("alarmIndex", mToDoNo);
            intent.putExtra("title", title);
            intent.putExtra("memo", content);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationClassContext(), mToDoNo, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (mAlarmManager != null) {
                mAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }

            //부팅후 재실행
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

        } else if (mRepeatType == ALL_DAY) {
            calendar.set(Calendar.HOUR_OF_DAY, mHour);
            calendar.set(Calendar.MINUTE, mMinute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1);
            }

            System.out.println("알람 시간: " + calendar.getTime().toString() + ", " + calendar.getTimeInMillis());
            PackageManager pm = getApplicationClassContext().getPackageManager();
            ComponentName receiver = new ComponentName(getApplicationClassContext(), DeviceBootReceiver.class);

            Intent intent = new Intent(getApplicationClassContext(), AlarmBroadcastReceiver.class);
            intent.putExtra("repeatType", 1);
            intent.putExtra("alarmIndex", mToDoNo);
            intent.putExtra("title", title);
            intent.putExtra("memo", content);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationClassContext(), mToDoNo, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (mAlarmManager != null) {
                mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
//                mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
            }

            //부팅후 재실행
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

        } else if (mRepeatType == WEEK_DAY) {
            calendar.set(Calendar.HOUR_OF_DAY, mHour);
            calendar.set(Calendar.MINUTE, mMinute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1);
            }

            System.out.println(mRepeatDayOfWeek);

            System.out.println("알람 시간: " + calendar.getTime().toString() + ", " + calendar.getTimeInMillis());
            PackageManager pm = getApplicationClassContext().getPackageManager();
            ComponentName receiver = new ComponentName(getApplicationClassContext(), DeviceBootReceiver.class);

            Intent intent = new Intent(getApplicationClassContext(), AlarmBroadcastReceiver.class);
            intent.putExtra("repeatType", 2);
            intent.putExtra("repeatDayOfWeek", mRepeatDayOfWeek);
            intent.putExtra("alarmIndex", mToDoNo);
            intent.putExtra("title", title);
            intent.putExtra("memo", content);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationClassContext(), mToDoNo, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (mAlarmManager != null) {
                mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
//                mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
            }

            //부팅후 재실행
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

        } else if (mRepeatType == MONTH_DAY) {

        }
    }

    public void removeAlarm( int todoNo){
        AlarmManager mAlarmManager = (AlarmManager) getApplicationClassContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationClassContext(), AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationClassContext(), todoNo, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if(pendingIntent!=null){
            if (mAlarmManager != null) {
                mAlarmManager.cancel(pendingIntent);
                System.out.println("cancel, " + todoNo);
            }
            pendingIntent.cancel();
        }
    }
}
