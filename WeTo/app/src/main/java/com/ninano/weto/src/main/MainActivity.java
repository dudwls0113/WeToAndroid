package com.ninano.weto.src.main;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ninano.weto.R;
import com.ninano.weto.db.AppDatabase;
import com.ninano.weto.db.RoomDBActivity;
import com.ninano.weto.db.ToDo;
import com.ninano.weto.db.ToDoDao;
import com.ninano.weto.db.ToDoData;
import com.ninano.weto.db.ToDoWithData;
import com.ninano.weto.src.BaseActivity;
import com.ninano.weto.src.BaseFragment;
import com.ninano.weto.src.custom.NonSwipeViewPager;
import com.ninano.weto.src.main.adpater.MainViewPagerAdapter;
import com.ninano.weto.src.main.map.MapFragment;
import com.ninano.weto.src.main.todo_group.ToDoGroupFragment;
import com.ninano.weto.src.main.todo_personal.ToDoPersonalFragment;
import com.ninano.weto.src.receiver.GeofenceBroadcastReceiver;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import devlight.io.library.ntb.NavigationTabBar;

import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_DWELL;
import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER;
import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT;

public class MainActivity extends BaseActivity implements AutoPermissionsListener {

    private Context mContext;

    private NonSwipeViewPager mViewPager;
    private MainViewPagerAdapter mMainViewPagerAdapter;

    final int MAP_FRAGMENT = 0;
    final int TODO_PERSONAL_FRAGMENT = 1;
    final int TODO_GROUP_FRAGMENT = 2;

    private BaseFragment mMapFragment;
    private BaseFragment mToDoPersonalFragment;
    private BaseFragment mToDoGroupFragment;

    private NavigationTabBar mNavigationTabBar;
    private ArrayList<NavigationTabBar.Model> mNavigationTabBarModels;

    private GeofencingClient geofencingClient;

    private int MY_PERMISSIONS_REQ_ACCESS_FINE_LOCATION = 100;
    private int MY_PERMISSIONS_REQ_ACCESS_BACKGROUND_LOCATION = 101;

    ArrayList<Geofence> geofenceList = new ArrayList<>();
    AppDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        AutoPermissions.Companion.loadAllPermissions(this, 100);
        checkPermission();
        init();
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceList.add(getGeofence(1, "사무실", new Pair<>(37.477198, 126.883828), (float) 300, 3000));
        addGeofences();
        setDatabase();

    }

    private void setDatabase() {
        mDatabase = AppDatabase.getAppDatabase(this);

        //UI 갱신 (라이브 데이터를 이용하여 자동으로)
        mDatabase.todoDao().getTodoList().observe(this, new Observer<List<ToDoWithData>>() {
            @Override
            public void onChanged(List<ToDoWithData> todoList) {
//                mTextView.setText(todoList.toString());
//                geofenceList.add(getGeofence(3, "가디역", new Pair<>(37.477198, 126.883828), (float) 100.0, 10000));
//                addGeofences();
            }
        });
    }

    //비동기처리                                   //넘겨줄객체, 중간에 처리할 데이터, 결과물(return)
    private static class InsertAsyncTask extends AsyncTask<Object, Void, Void> {
        private ToDoDao mTodoDao;

        InsertAsyncTask(ToDoDao mTodoDao) {
            this.mTodoDao = mTodoDao;
        }

        @Override
        protected Void doInBackground(Object... toDos) {
            mTodoDao.insertTodo((ToDo) toDos[0], (ToDoData) toDos[1]);
            return null;
        }
    }


    private void addGeofences() {
        geofencingClient.addGeofences(getGeofencingRequest(geofenceList), geofencePendingIntent()).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showCustomToast("add Success");

            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showCustomToast("add Fail");
                Log.d("에러", e.toString());

                // ...
            }
        });
//        removeGeofences -> List<String> 을 매개변수로 넘겨서 id(string)값으로 지오펜싱 제거
//        geofencingClient.removeGeofences(new List<String>())
    }

    private PendingIntent geofencePendingIntent() {
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        PendingIntent geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    private GeofencingRequest getGeofencingRequest(List<Geofence> list) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        // Geofence 이벤트는 진입시 부터 처리할 때
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(list);  // Geofence 리스트 추가
        return builder.build();
    }

    private Geofence getGeofence(int type, String reqId, Pair<Double, Double> geo, Float radiusMeter, int LoiteringDelay) {
        int GEOFENCE_TRANSITION;
        if (type == 1) {
            GEOFENCE_TRANSITION = GEOFENCE_TRANSITION_ENTER;  // 진입 감지시
        } else if (type == 2) {
            GEOFENCE_TRANSITION = GEOFENCE_TRANSITION_EXIT;  // 이탈 감지시
        } else {
            GEOFENCE_TRANSITION = GEOFENCE_TRANSITION_DWELL; // 머물기 감지시
        }
        return new Geofence.Builder()
                .setRequestId(reqId)    // 이벤트 발생시 BroadcastReceiver에서 구분할 id
                .setCircularRegion(geo.first, geo.second, radiusMeter)    // 위치및 반경(m)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)        // Geofence 만료 시간
                .setLoiteringDelay(LoiteringDelay)                            // 머물기 체크 시간
                .setNotificationResponsiveness(120000)      //위치감지하는 텀 180000 = 180초
                .setTransitionTypes(GEOFENCE_TRANSITION)
                .build();
    }

    void init() {
        mMapFragment = new MapFragment();
        mToDoPersonalFragment = new ToDoPersonalFragment();
        mToDoGroupFragment = new ToDoGroupFragment();

        mMainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        mMainViewPagerAdapter.addFragment(mMapFragment, "1");
        mMainViewPagerAdapter.addFragment(mToDoPersonalFragment, "2");
        mMainViewPagerAdapter.addFragment(mToDoGroupFragment, "3");

        mViewPager = findViewById(R.id.main_vp);
        mViewPager.setAdapter(mMainViewPagerAdapter);

        mNavigationTabBar = findViewById(R.id.main_ntb);
        mNavigationTabBarModels = new ArrayList<>();
        mNavigationTabBarModels.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_main_map),
                        getResources().getColor(R.color.colorWhite)
                )
                        .title("")
                        .build()
        );
        mNavigationTabBarModels.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_main_personal),
                        getResources().getColor(R.color.colorWhite)
                )
                        .title("")
                        .build()
        );
        mNavigationTabBarModels.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_main_group),
                        getResources().getColor(R.color.colorWhite)
                )
                        .title("")
                        .build()
        );

        mNavigationTabBar.setModels(mNavigationTabBarModels);
        mNavigationTabBar.setIsBadged(true);
        mNavigationTabBar.setViewPager(mViewPager, 1);
        mNavigationTabBar.setBadgeTitleColor(Color.WHITE);
        mNavigationTabBar.setIsSwiped(true);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new InsertAsyncTask(mDatabase.todoDao())
                        .execute(new ToDo("일정 이름", "일정 내용", 10, 1),
                                new ToDoData(0, "위치이름(가디역)", 37.480414, 126.883104, 200, "ssid_no", 'N', 1,
                                        1, "1 2", 30, "10:30", "10:30"));
            }
        });

    }

    private void checkPermission() {
        boolean permissionAccessFineLocationApproved = ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;

        if (permissionAccessFineLocationApproved) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                boolean backgroundLocationPermissionApproved = ActivityCompat
                        .checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED;

                if (!backgroundLocationPermissionApproved) {
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                            MY_PERMISSIONS_REQ_ACCESS_BACKGROUND_LOCATION
                    );
                }
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{(Manifest.permission.ACCESS_FINE_LOCATION)},
                    MY_PERMISSIONS_REQ_ACCESS_FINE_LOCATION
            );
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }

    @Override
    public void onDenied(int i, @NotNull String[] strings) {
//        showCustomToast("권한 거부");
    }

    @Override
    public void onGranted(int i, @NotNull String[] strings) {
//        showCustomToast("권한 허용");
    }
}
