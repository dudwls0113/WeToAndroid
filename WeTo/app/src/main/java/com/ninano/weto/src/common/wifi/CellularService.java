package com.ninano.weto.src.common.wifi;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.ninano.weto.db.AppDatabase;
import com.ninano.weto.db.ToDoDao;
import com.ninano.weto.db.ToDoWithData;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.ninano.weto.src.ApplicationClass.getApplicationClassContext;
import static com.ninano.weto.src.common.util.Util.compareTimeSlot;
import static com.ninano.weto.src.common.util.Util.getWifiNotificationContent;
import static com.ninano.weto.src.common.util.Util.sendNotification;

public class CellularService extends JobService {

    AppDatabase mDatabase = AppDatabase.getAppDatabase(getApplicationClassContext());

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("job", "create");
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
//        Toast.makeText(getApplicationContext(), "와이파이 연결 끊어짐", Toast.LENGTH_LONG).show();
        System.out.println("cellular startjob");
        try {
            List<ToDoWithData> toDoWithDataList = new DBWifiAsyncTask(mDatabase.todoDao()).execute('Y', (char)11).get();
            SharedPreferences sf = getSharedPreferences("sFile", MODE_PRIVATE);
            String recentWifi = sf.getString("recentWifi", "");
            SharedPreferences.Editor editor = sf.edit();
            editor.putString("recentWifi", "");
            editor.apply();
            for(int i=0; i<toDoWithDataList.size(); i++){
                System.out.println("와이파이 정보 비교: " + recentWifi + ", " + toDoWithDataList.get(i).getSsid());
                if (recentWifi.equals(toDoWithDataList.get(i).getSsid())){
                    if (compareTimeSlot(toDoWithDataList.get(i).getTimeSlot())){
                        sendNotification(getWifiNotificationContent(toDoWithDataList.get(i)), toDoWithDataList.get(i).getContent());
                        jobFinished(jobParameters, false);
                    }
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("job", "startCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
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
            List<ToDoWithData> toDoWithData = mDatabase.todoDao().getTodoWithWifi(characters[0], (int)characters[1]);
            return toDoWithData;
        }
    }
}
