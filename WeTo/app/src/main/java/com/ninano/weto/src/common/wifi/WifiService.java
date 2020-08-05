package com.ninano.weto.src.common.wifi;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.ninano.weto.db.AppDatabase;
import com.ninano.weto.db.ToDoDao;
import com.ninano.weto.db.ToDoWithData;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.ninano.weto.src.ApplicationClass.getApplicationClassContext;
import static com.ninano.weto.src.common.util.Util.compareTimeSlot;
import static com.ninano.weto.src.common.util.Util.getWifiNotificationContent;
import static com.ninano.weto.src.common.util.Util.sendNotification;

public class WifiService extends JobService {

    AppDatabase mDatabase = AppDatabase.getAppDatabase(getApplicationClassContext());

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("job", "create");
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        try {
//            Toast.makeText(getApplicationContext(), "와이파이 연결", Toast.LENGTH_LONG).show();
            List<ToDoWithData> toDoWithDataList = new DBWifiAsyncTask(mDatabase.todoDao()).execute('Y', (char) 22).get();
            SharedPreferences sf = getSharedPreferences("sFile", MODE_PRIVATE);
            String recentWifi = sf.getString("recentWifi", "");
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            final WifiInfo wifiInfo;
            if (wifiManager != null) {
                wifiInfo = wifiManager.getConnectionInfo();
                System.out.println("와이파이 정보 비교: " + recentWifi + ", " + wifiInfo.getBSSID());
                if (wifiInfo != null && wifiInfo.getBSSID() != null) {
                    if (!wifiInfo.getBSSID().equals(recentWifi)) { // 현재연결와이파이와 recentWifi가 다르면 노티 보내야함
                        SharedPreferences.Editor editor = sf.edit();
                        editor.putString("recentWifi", wifiInfo.getBSSID());
                        editor.apply();
                        for (int i = 0; i < toDoWithDataList.size(); i++) {
                            if (wifiInfo.getBSSID().equals(toDoWithDataList.get(i).getSsid())) {
                                //타임슬롯 확인
                                if (compareTimeSlot(toDoWithDataList.get(i).getTimeSlot())) {
                                    sendNotification(getWifiNotificationContent(toDoWithDataList.get(i)), toDoWithDataList.get(i).getContent());
                                    jobFinished(jobParameters, false);
                                }
                            }
                        }
                    }
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            Toast.makeText(getApplicationContext(), "연결실패", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
//        Toast.makeText(getApplicationContext(), "와이파이 연결", Toast.LENGTH_LONG).show();
//        System.out.println("wifi startjob");
//        SharedPreferences sf = getSharedPreferences("sFile", MODE_PRIVATE);
//        boolean isFirstWifiNoti = sf.getBoolean("firstWifiNoti", false);
//        if(!isFirstWifiNoti){
//            WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//            final WifiInfo wifiInfo;
//            if (wifiManager != null) {
//                wifiInfo = wifiManager.getConnectionInfo();
////            Log.d("job",wifiInfo.getBSSID());
//                if (wifiInfo!=null && wifiInfo.getBSSID()!=null){
//                    if (wifiInfo.getBSSID().equals("b4:a9:4f:50:d9:e2")){
//                        SharedPreferences.Editor editor = sf.edit();
//                        editor.putString("recentWifi", "b4:a9:4f:50:d9:e2");
//                        editor.putBoolean("firstWifiNoti", true);
//                        editor.apply();
////                    Toast.makeText(getApplicationContext(), "무료요금, 같은 와이파이", Toast.LENGTH_LONG).show();
//
//                        Intent notificationIntent = new Intent(this, MainActivity.class);
//                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
//                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
//
//                        final String CHANNEL_ID = "채널ID";
//                        NotificationManager notificationManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//
//                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//                            final String CHANNEL_NAME = "채널이름";
//                            final String CHANNEL_DESCRIPTION = "채널 Description";
//                            final int importance = NotificationManager.IMPORTANCE_HIGH;
//
//                            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
//                            mChannel.setDescription(CHANNEL_DESCRIPTION);
//                            mChannel.enableLights(true);
//                            mChannel.enableVibration(true);
//                            mChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
//                            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//                            if (notificationManager != null) {
//                                notificationManager.createNotificationChannel(mChannel);
//                            }
//                        }
//
//
//                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
//                        builder.setSmallIcon(R.mipmap.ic_launcher);
//                        builder.setWhen(System.currentTimeMillis());
//                        builder.setContentTitle("와이파이 감지");
//                        builder.setContentText("와이파이입니다.");
//                        builder.setContentIntent(pendingIntent);
//                        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
//                        builder.setAutoCancel(true);
//                        if (notificationManager != null) {
//                            notificationManager.notify(1, builder.build());
//                        }
//                        jobFinished(jobParameters, false);
//                    } else {
//                        Toast.makeText(getApplicationContext(), "무료요금 " + wifiInfo + ", " + wifiInfo.getBSSID() + ", " + wifiInfo.getSSID(), Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
//        }

        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("job", "startCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d("job", "stop");
//        Toast.makeText(getApplicationContext(), "유료요금", Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("job", "destroy");
    }

    private class DBWifiAsyncTask extends AsyncTask<Character, Void, List<ToDoWithData>> {
        private ToDoDao mTodoDao;

        DBWifiAsyncTask(ToDoDao mTodoDao) {
            this.mTodoDao = mTodoDao;
        }

        @Override
        protected List<ToDoWithData> doInBackground(Character... characters) {
            List<ToDoWithData> toDoWithData = mDatabase.todoDao().getTodoWithWifi(characters[0], (int) characters[1]);
            return toDoWithData;
        }
    }
}
