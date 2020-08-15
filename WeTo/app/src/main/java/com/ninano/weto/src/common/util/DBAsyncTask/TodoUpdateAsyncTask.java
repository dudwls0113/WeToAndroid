package com.ninano.weto.src.common.util.DBAsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ninano.weto.R;
import com.ninano.weto.db.ToDo;
import com.ninano.weto.db.ToDoDao;
import com.ninano.weto.db.ToDoData;
import com.ninano.weto.src.common.group.GroupTodoMaker;
import com.ninano.weto.src.common.models.ServerTodoWithTodoNo;
import com.ninano.weto.src.todo_add.AddPersonalToDoActivity;

import java.util.List;

import static com.ninano.weto.src.ApplicationClass.NO_DATA;
import static com.ninano.weto.src.ApplicationClass.getApplicationClassContext;
import static com.ninano.weto.src.common.alarm.AlarmMaker.getAlarmMaker;
import static com.ninano.weto.src.common.geofence.GeofenceMaker.getGeofenceMaker;
import static com.ninano.weto.src.common.wifi.WifiMaker.getWifiMaker;

public class TodoUpdateAsyncTask extends AsyncTask<Object, Void, ToDoData> {
    private ToDoDao mTodoDao;
    private String title, content;

    public TodoUpdateAsyncTask(ToDoDao mTodoDao, TodoUpdateAsyncTaskCallBack mTodoUpdateAsyncTaskCallBack) {
        this.mTodoDao = mTodoDao;
        this.mTodoUpdateAsyncTaskCallBack = mTodoUpdateAsyncTaskCallBack;
    }

    public interface TodoUpdateAsyncTaskCallBack {
        void updateSuccess();

        void updateFail(String message);
    }

    private TodoUpdateAsyncTaskCallBack mTodoUpdateAsyncTaskCallBack;

    @Override
    protected ToDoData doInBackground(Object... objects) {
        title = ((ToDo) objects[0]).getTitle();
        content = ((ToDo) objects[0]).getContent();
        mTodoDao.updateGroupTodo((ToDo) objects[0], (ToDoData) objects[1], ((ServerTodoWithTodoNo) objects[2]).getTodoNo(), ((ServerTodoWithTodoNo) objects[2]).getSeverTodoNo());
        return (ToDoData) objects[1];
    }

    @Override
    protected void onPostExecute(final ToDoData toDoData) {
        getAlarmMaker().removeAlarm(toDoData.getTodoNo());
        getGeofenceMaker().removeGeofence(String.valueOf(toDoData.getTodoNo()));
        //기존 지오펜스 삭제처리
        getGeofenceMaker().removeGeofence(String.valueOf(toDoData.getTodoNo()));
        // 기존 알람 삭제처리
        getAlarmMaker().removeAlarm(toDoData.getTodoNo());
        if (toDoData.getIsWiFi() == 'Y') {//와이파이
            getWifiMaker().registerAndUpdateWifi(getApplicationClassContext(), toDoData.getIsWiFi(), toDoData.getLocationMode(), false);
            mTodoUpdateAsyncTaskCallBack.updateSuccess();
        } else if (toDoData.getLongitude() == NO_DATA && toDoData.getRepeatType() != NO_DATA) {//시간일정
            getAlarmMaker().registerAlarm(toDoData.getTodoNo(), toDoData.getRepeatType(), toDoData.getYear(), toDoData.getMonth(), toDoData.getDay(), toDoData.getHour(), toDoData.getMinute(), title, content.toString(), toDoData.getRepeatDayOfWeek());
            mTodoUpdateAsyncTaskCallBack.updateSuccess();
        } else if (toDoData.getRepeatType() == NO_DATA && toDoData.getLongitude() != NO_DATA) {//위치일정
            getGeofenceMaker().addGeoFenceOne(toDoData.getTodoNo(), toDoData.getLatitude(), toDoData.getLongitude(), toDoData.getLocationMode(), toDoData.getRadius(),
                    new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
//                            showCustomToast("수정이 완료되었습니다.");
                            mTodoUpdateAsyncTaskCallBack.updateSuccess();
                        }
                    },
                    new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("지오펜스 등록 실패", e.toString());
                            TodoDeleteAsyncTask todoDeleteAsyncTask = new TodoDeleteAsyncTask(mTodoDao, new TodoDeleteAsyncTask.TodoDeleteAsyncTaskCallBack() {
                                @Override
                                public void deleteSuccess() {
                                    mTodoUpdateAsyncTaskCallBack.updateFail("위치 서비스 활성화가 필요합니다");
                                }

                                @Override
                                public void deleteFail() {

                                }
                            });
                            todoDeleteAsyncTask.execute(toDoData);
                        }
                    });
        }

        super.onPostExecute(toDoData);
    }
}