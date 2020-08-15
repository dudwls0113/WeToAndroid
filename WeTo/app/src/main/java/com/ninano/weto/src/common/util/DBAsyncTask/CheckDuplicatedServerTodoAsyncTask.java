package com.ninano.weto.src.common.util.DBAsyncTask;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ninano.weto.db.ToDo;
import com.ninano.weto.db.ToDoDao;
import com.ninano.weto.db.ToDoData;
import com.ninano.weto.src.common.models.ServerTodoWithTodoNo;

import java.util.List;

import static com.ninano.weto.src.ApplicationClass.NO_DATA;
import static com.ninano.weto.src.ApplicationClass.getApplicationClassContext;
import static com.ninano.weto.src.common.alarm.AlarmMaker.getAlarmMaker;
import static com.ninano.weto.src.common.geofence.GeofenceMaker.getGeofenceMaker;
import static com.ninano.weto.src.common.wifi.WifiMaker.getWifiMaker;

public class CheckDuplicatedServerTodoAsyncTask extends AsyncTask<Integer, Void, List<ServerTodoWithTodoNo>> {
    private ToDoDao mTodoDao;
    private String title, content;

    public CheckDuplicatedServerTodoAsyncTask(ToDoDao mTodoDao, CheckDuplicatedServerTodoAsyncTaskCallBack mCheckDuplicatedServerTodoAsyncTaskCallBack) {
        this.mTodoDao = mTodoDao;
        this.mCheckDuplicatedServerTodoAsyncTaskCallBack = mCheckDuplicatedServerTodoAsyncTaskCallBack;
    }

    @Override
    protected List<ServerTodoWithTodoNo> doInBackground(Integer... integers) {
        return mTodoDao.checkServerTodoNo(integers[0]);
    }

    public interface CheckDuplicatedServerTodoAsyncTaskCallBack {
        void duplicated(ServerTodoWithTodoNo serverTodoWithTodoNo);

        void notDuplicated();
    }

    private CheckDuplicatedServerTodoAsyncTaskCallBack mCheckDuplicatedServerTodoAsyncTaskCallBack;


    @Override
    protected void onPostExecute(List<ServerTodoWithTodoNo> serverTodoWithTodoNos) {
        super.onPostExecute(serverTodoWithTodoNos);
        if (serverTodoWithTodoNos.size() > 0) {
            mCheckDuplicatedServerTodoAsyncTaskCallBack.duplicated(serverTodoWithTodoNos.get(0));
        } else {
            mCheckDuplicatedServerTodoAsyncTaskCallBack.notDuplicated();
        }
    }
}