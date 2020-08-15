package com.ninano.weto.src.splash;

import androidx.annotation.NonNull;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ninano.weto.R;
import com.ninano.weto.db.AppDatabase;
import com.ninano.weto.db.ToDoDao;
import com.ninano.weto.db.ToDoWithData;
import com.ninano.weto.src.BaseActivity;
import com.ninano.weto.src.DefaultResponse;
import com.ninano.weto.src.common.FirebaseRetrofitInterface;
import com.ninano.weto.src.common.group.GroupTodoMaker;
import com.ninano.weto.src.common.util.DBAsyncTask.AllGroupTodoAsyncTask;
import com.ninano.weto.src.main.MainActivity;
import com.ninano.weto.src.splash.interfaces.SplashActivityView;
import com.ninano.weto.src.splash.models.ServerTodo;
import com.ninano.weto.src.splash.models.ServerTodoResponse;
import com.ninano.weto.src.tutorial.TutorialActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Intent.ACTION_VIEW;
import static com.ninano.weto.src.ApplicationClass.GROUP_TODO_MAKE_FROM_FCM;
import static com.ninano.weto.src.ApplicationClass.LOCATION;
import static com.ninano.weto.src.ApplicationClass.MEET;
import static com.ninano.weto.src.ApplicationClass.TIME;
import static com.ninano.weto.src.ApplicationClass.getRetrofit;
import static com.ninano.weto.src.ApplicationClass.sSharedPreferences;
import static com.ninano.weto.src.common.alarm.AlarmMaker.getAlarmMaker;
import static com.ninano.weto.src.common.geofence.GeofenceMaker.getGeofenceMaker;
import static com.ninano.weto.src.common.util.Util.sendNotification;

public class SplashActivity extends BaseActivity implements SplashActivityView {

    private Context mContext;
    private boolean isKakaoShare;
    private int mGroupId;
    private String nickName, profileUrl;
    private AppDatabase mDatabase;
    protected int mNeedUpdateCount;
    private int updatedCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext = this;
        mDatabase = AppDatabase.getAppDatabase(getApplicationContext());
        boolean isFirst = sSharedPreferences.getBoolean("firstConnect", true);
        if (isFirst) {
            startActivity(new Intent(SplashActivity.this, TutorialActivity.class));
            finish();
            return;
        }

        if (getIntent() != null) {
            if (getIntent().getAction() != null) {
                if (getIntent().getAction().equals(ACTION_VIEW)) {
                    isKakaoShare = true;
                    String value = getIntent().getData().getQueryParameter("key1");
                    String groupId = value.substring(0, value.indexOf(","));
                    mGroupId = Integer.parseInt(groupId);
                    nickName = value.substring(value.indexOf(",") + 2, value.indexOf("/"));
                    profileUrl = value.substring(value.indexOf("/") + 2, value.length());
                }
            }
        }

        getGeofenceMaker().removeAllGeofence();
        SplashAsyncTask splashAsyncTask = new SplashAsyncTask(mDatabase.todoDao());
        splashAsyncTask.execute();
    }

    @Override
    public void successGetTodo(ServerTodoResponse.TodoArrayResponse response) throws InterruptedException, ExecutionException {
        mNeedUpdateCount = response.getAddList().size();
        updatedCount = 0;

        //1. 내 일정에 없는게 있으면 추가
        //3. 일정 정보 업데이트(order 제외하고 전부 update시키기)
        for (ServerTodo serverTodo : response.getAddList()) {
            GroupTodoMaker groupTodoMaker = new GroupTodoMaker(GROUP_TODO_MAKE_FROM_FCM, getApplicationContext(), serverTodo.getTitle(), serverTodo.getContent(), serverTodo.getIsImportant(), serverTodo.getLocationName(), serverTodo.getIsWiFi(), serverTodo.getSsid(),
                    serverTodo.getRepeatDayOfWeek(), serverTodo.getDate(), serverTodo.getTime(), serverTodo.getIcon(), serverTodo.getType(), serverTodo.getTodoNo(), serverTodo.getGroupNo(), serverTodo.getLocationMode(),
                    serverTodo.getTimeSlot(), serverTodo.getRepeatDay(), serverTodo.getYear(), serverTodo.getMonth(), serverTodo.getDay(), serverTodo.getHour(), serverTodo.getMinute(), serverTodo.getRepeatType(), serverTodo.getMeetRemindTime(),
                    serverTodo.getLatitude(), serverTodo.getLongitude(), new GroupTodoMaker.GroupTodoMakerCallBack() {
                @Override
                public void groupTodoMakeSuccess() {
                    //x
                }

                @Override
                public void groupTodoMakeFail(String message) {
                    updatedCount++;
                    updateCheckAndGoMain();
                }

                @Override
                public void groupTodoMakeSuccessFromFCM(int serverTodoNo) {
                    updatedCount++;
                    todoUpdateSuccess(serverTodoNo);
                    updateCheckAndGoMain();
                }

                @Override
                public void groupTodoUpdateSuccessFromFCM(int serverTodoNo) {
                    updatedCount++;
                    todoUpdateSuccess(serverTodoNo);
                    updateCheckAndGoMain();
                }
            });
            groupTodoMaker.makeGroupTodo();
        }
        //2. 없어진 일정인데 내일정에 있으면 해당일정 삭제

        updateCheckAndGoMain();
    }

    @Override
    public void failGetTodo() {
        showCustomToast("일정 동기화에 실패하였습니다");
    }

    //비동기처리                                   //넘겨줄객체, 중간에 처리할 데이터, 결과물(return)
    @SuppressLint("StaticFieldLeak")
    private class SplashAsyncTask extends AsyncTask<Void, Void, List<ToDoWithData>> {
        private ToDoDao mTodoDao;

        SplashAsyncTask(ToDoDao mTodoDao) {
            this.mTodoDao = mTodoDao;
        }

        @Override
        protected List<ToDoWithData> doInBackground(Void... voids) {
            return mTodoDao.getActivatedTodoListNoLive();
        }

        @Override
        protected void onPostExecute(List<ToDoWithData> toDoWithDataList) {
            super.onPostExecute(toDoWithDataList);
            List<Geofence> geofenceList = new ArrayList<>();
            for (ToDoWithData toDoWithData : toDoWithDataList) {
                if (toDoWithData.getType() == LOCATION && toDoWithData.getIsWiFi() == 'N') {
                    geofenceList.add(getGeofenceMaker().getGeofence(toDoWithData.getLocationMode(), String.valueOf(toDoWithData.getTodoNo()),
                            new Pair<>(toDoWithData.getLatitude(), toDoWithData.getLongitude()), (float) toDoWithData.getRadius()));
                }

                if (toDoWithData.getIsGroup() == 'Y') {
                    if (toDoWithData.getType() == MEET) {
                        geofenceList.add(getGeofenceMaker().getGeofence(toDoWithData.getLocationMode(), "meet" + (toDoWithData.getTodoNo()),
                                new Pair<>(toDoWithData.getLatitude(), toDoWithData.getLongitude()), (float) toDoWithData.getRadius()));
                    }
                }
                if (toDoWithData.getType() == TIME) { // 시간 정보
                    getAlarmMaker().removeAlarm(toDoWithData.getTodoNo());
                    getAlarmMaker().registerAlarm(toDoWithData.getTodoNo(), toDoWithData.getRepeatType(), toDoWithData.getYear(), toDoWithData.getMonth(), toDoWithData.getDay(), toDoWithData.getHour(), toDoWithData.getMinute(), toDoWithData.getTitle(), toDoWithData.getContent(), toDoWithData.getRepeatDayOfWeek());
                }
            }

            if (geofenceList.size() == 0) {
                updateAllTodo();
                return;
            }
            getGeofenceMaker().addGeoFenceList(geofenceList, new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    updateAllTodo();
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showCustomToast(getString(R.string.cant_geofence_when_splash));
                    updateAllTodo();
                }
            });
        }
    }

    void todoUpdateSuccess(int todoNo) {
        final FirebaseRetrofitInterface firebaseRetrofitInterface = getRetrofit().create(FirebaseRetrofitInterface.class);
        firebaseRetrofitInterface.todoRegisterSuccess(todoNo).enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                final DefaultResponse defaultResponse = response.body();
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
            }
        });
    }

    void kakaoLinkCheckAndGoMain() {
        if (isKakaoShare) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.putExtra("groupId", mGroupId);
            intent.putExtra("nickName", nickName);
            intent.putExtra("profileUrl", profileUrl);
            intent.putExtra("kakaoShare", true);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(mContext, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    void updateAllTodo() {
        SplashService splashService = new SplashService(this, this);
        splashService.getAllTodo();
    }

    void updateCheckAndGoMain() {
        if (updatedCount == mNeedUpdateCount) {
            kakaoLinkCheckAndGoMain();
        }
    }
}
