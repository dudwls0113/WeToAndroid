package com.ninano.weto.src.common.wifi;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import com.ninano.weto.db.AppDatabase;
import com.ninano.weto.db.ToDoDao;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;
import static com.ninano.weto.src.ApplicationClass.AT_ARRIVE;
import static com.ninano.weto.src.ApplicationClass.AT_START;

public class WifiMaker {

    private static WifiMaker WifiMakerInstance = null;

    public static WifiMaker getWifiMaker() {
        if (WifiMakerInstance == null) {
            WifiMakerInstance = new WifiMaker();
        }

        return WifiMakerInstance;
    }

    //    private Context mContext;
//    private char mWifiMode;
//    private int mLocationMode;
//    private boolean mWifiConnected;
    private AppDatabase mDatabase;

    public void startJobScheduler(Context mContext) {
//        JobScheduler cellularJobScheduler = (JobScheduler) getApplicationClassContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
//        if(cellularJobScheduler!=null){
//            cellularJobScheduler.cancel(1);
//        }
        JobScheduler wifiJobScheduler = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
//        if (wifiJobScheduler!=null){
//            wifiJobScheduler.cancel(0);
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            if (cellularJobScheduler != null) {
//                cellularJobScheduler.schedule(new JobInfo.Builder(1, new ComponentName(getApplicationClassContext(), CellularService.class))
//                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_CELLULAR)
//                        .setPeriodic(TimeUnit.MINUTES.toMillis(15))
//                        .setPersisted(true)
//                        .build());
//            }
            if (wifiJobScheduler != null) {
                wifiJobScheduler.schedule(new JobInfo.Builder(0, new ComponentName(mContext, WifiService.class))
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                        .setPeriodic(TimeUnit.MINUTES.toMillis(15))
                        .setPersisted(true)
                        .build());
            }
        } else {
            Toast.makeText(mContext, "WiFi 기반 서비스를 사용할 수 없는 안드로이드 버전입니다.", Toast.LENGTH_LONG).show();
        }
    }

    public void registerAndUpdateWifi(Context mContext, char mWifiMode, int mLocationMode, boolean mWifiConnected) {
        setDatabase(mContext);
        Integer startCount = 0;
        Integer arriveCount = 0;
        try {
            startCount = new CountWifiAsyncTask(mDatabase.todoDao()).execute('Y', (char) AT_START).get(); // 연결 해제시
            if (startCount == 0) {
                JobScheduler jobScheduler = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                if (jobScheduler != null) {
                    jobScheduler.cancel(1);
                }
            }
            arriveCount = new CountWifiAsyncTask(mDatabase.todoDao()).execute('Y', (char) AT_ARRIVE).get(); // 연결 시
//            if (arriveCount==0){
//                JobScheduler jobScheduler = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
//                if (jobScheduler != null) {
//                    jobScheduler.cancel(0);
//                }
//            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        if (mWifiMode == 'Y') {
            if (mLocationMode == AT_START) {
                System.out.println("출발카운트: " + startCount);
                if (startCount == 1) {
                    JobScheduler jobScheduler = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                    if (jobScheduler != null) {
                        jobScheduler.cancel(1);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        if (jobScheduler != null) {
                            if (mWifiConnected) {
                                WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                                final WifiInfo wifiInfo;
                                if (wifiManager != null) {
                                    wifiInfo = wifiManager.getConnectionInfo();
                                    SharedPreferences sf = mContext.getSharedPreferences("sFile", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sf.edit();
                                    editor.putString("recentWifi", wifiInfo.getBSSID());
//                                        editor.putBoolean("firstWifiNoti", true);
                                    editor.apply();
                                    System.out.println("현재 연결 와이파이: " + wifiInfo.getBSSID());
                                }
                            }
                            jobScheduler.schedule(new JobInfo.Builder(1, new ComponentName(mContext, CellularService.class))
                                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_CELLULAR)
                                    .setPeriodic(TimeUnit.MINUTES.toMillis(15))
                                    .setPersisted(true)
                                    .build());
                        }
                    }
                } else {
                    System.out.println("카운트 아님");
                    if (mWifiConnected) {
                        System.out.println("현재 연결 와이파이2");
                        WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        final WifiInfo wifiInfo;
                        if (wifiManager != null) {
                            wifiInfo = wifiManager.getConnectionInfo();
                            SharedPreferences sf = mContext.getSharedPreferences("sFile", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sf.edit();
                            editor.putString("recentWifi", wifiInfo.getBSSID());
//                                editor.putBoolean("firstWifiNoti", true);
                            editor.apply();
                            System.out.println("현재 연결 와이파이: " + wifiInfo.getBSSID());
                        }
                    }
                }
            } else if (mLocationMode == AT_ARRIVE) {
//                    System.out.println("도착카운트: " + arriveCount);
//                    if (arriveCount == 1) {
//                        JobScheduler jobScheduler = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
//                        if (jobScheduler != null) {
//                            jobScheduler.cancel(0);
//                        }
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                            if (jobScheduler != null) {
//                                if (mWifiConnected) {
//                                    System.out.println("현재 연결 와이파이");
//                                    WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//                                    final WifiInfo wifiInfo;
//                                    if (wifiManager != null) {
//                                        wifiInfo = wifiManager.getConnectionInfo();
//                                        SharedPreferences sf = mContext.getSharedPreferences("sFile", MODE_PRIVATE);
//                                        SharedPreferences.Editor editor = sf.edit();
//                                        editor.putString("recentWifi", wifiInfo.getBSSID());
////                                        editor.putBoolean("firstWifiNoti", true);
//                                        editor.apply();
//                                    }
//                                }
//                                jobScheduler.schedule(new JobInfo.Builder(0, new ComponentName(mContext, WifiService.class))
//                                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
//                                        .setPeriodic(TimeUnit.MINUTES.toMillis(15))
//                                        .setPersisted(true)
//                                        .build());
//                            }
//                        }
//                    } else {
                System.out.println("카운트 아님");
                if (mWifiConnected) {
                    System.out.println("현재 도착 연결 와이파이2");
                    WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    final WifiInfo wifiInfo;
                    if (wifiManager != null) {
                        wifiInfo = wifiManager.getConnectionInfo();
                        SharedPreferences sf = mContext.getSharedPreferences("sFile", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sf.edit();
                        editor.putString("recentWifi", wifiInfo.getBSSID());
//                                editor.putBoolean("firstWifiNoti", true);
                        editor.apply();
                        System.out.println("현재 연결 와이파이: " + wifiInfo.getBSSID());
                    }
//                        }
                }
            }
        }
    }

    public void removeWifi(int todoNo) {

    }

    private void setDatabase(Context context) {
        mDatabase = AppDatabase.getAppDatabase(context);
    }

    private class CountWifiAsyncTask extends AsyncTask<Character, Void, Integer> {
        private ToDoDao mTodoDao;

        CountWifiAsyncTask(ToDoDao mTodoDao) {
            this.mTodoDao = mTodoDao;
        }

        @Override
        protected Integer doInBackground(Character... characters) {
            Integer count = mDatabase.todoDao().getTodoWithWifiCount(characters[0], (int) characters[1]);
            return count;
        }
    }
}
