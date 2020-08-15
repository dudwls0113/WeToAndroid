package com.ninano.weto.src.common.util.DBAsyncTask;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ninano.weto.db.ToDoDao;
import com.ninano.weto.db.ToDoData;

import static com.ninano.weto.src.ApplicationClass.NO_DATA;
import static com.ninano.weto.src.ApplicationClass.getApplicationClassContext;
import static com.ninano.weto.src.common.alarm.AlarmMaker.getAlarmMaker;
import static com.ninano.weto.src.common.geofence.GeofenceMaker.getGeofenceMaker;
import static com.ninano.weto.src.common.wifi.WifiMaker.getWifiMaker;

public class TodoDeleteAsyncTask extends AsyncTask<ToDoData, Void, ToDoData> {
    private ToDoDao mTodoDao;

    public TodoDeleteAsyncTask(ToDoDao mTodoDao, TodoDeleteAsyncTaskCallBack mTodoUpdateAsyncTaskCallBack) {
        this.mTodoDao = mTodoDao;
        this.mTodoUpdateAsyncTaskCallBack = mTodoUpdateAsyncTaskCallBack;
    }

    public interface TodoDeleteAsyncTaskCallBack {
        void deleteSuccess();

        void deleteFail();
    }

    private TodoDeleteAsyncTaskCallBack mTodoUpdateAsyncTaskCallBack;

    @Override
    protected ToDoData doInBackground(ToDoData... toDoData) {
        mTodoDao.deleteToDo(toDoData[0].getTodoNo());
        mTodoDao.deleteToDoData(toDoData[0].getTodoDataNo());
        return toDoData[0];
    }

    @Override
    protected void onPostExecute(final ToDoData toDoData) {
        mTodoUpdateAsyncTaskCallBack.deleteSuccess();
    }
}