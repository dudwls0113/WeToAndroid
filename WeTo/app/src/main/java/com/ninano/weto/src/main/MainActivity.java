package com.ninano.weto.src.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentPagerAdapter;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.kakao.auth.Session;
import com.ninano.weto.R;
import com.ninano.weto.db.ToDo;
import com.ninano.weto.db.ToDoDao;
import com.ninano.weto.db.ToDoData;
import com.ninano.weto.src.ApplicationClass;
import com.ninano.weto.src.BaseActivity;
import com.ninano.weto.src.BaseFragment;
import com.ninano.weto.src.custom.NonSwipeViewPager;
import com.ninano.weto.src.group_detail.GroupDetailActivity;
import com.ninano.weto.src.group_invite.GroupInviteActivity;
import com.ninano.weto.src.main.adpater.MainViewPagerAdapter;
import com.ninano.weto.src.main.interfaces.MainActivityView;
import com.ninano.weto.src.main.map.MapFragment;
import com.ninano.weto.src.main.todo_group.ToDoGroupFragment;
import com.ninano.weto.src.main.todo_group.ToDoGroupService;
import com.ninano.weto.src.main.todo_personal.ToDoPersonalFragment;
import com.ninano.weto.src.common.Geofence.GeofenceBroadcastReceiver;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import org.jetbrains.annotations.NotNull;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import devlight.io.library.ntb.NavigationTabBar;

import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_DWELL;
import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER;
import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT;
import static com.ninano.weto.src.ApplicationClass.fcmToken;
import static com.ninano.weto.src.ApplicationClass.sSharedPreferences;

public class MainActivity extends BaseActivity implements AutoPermissionsListener, MainActivityView {

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


    private int MY_PERMISSIONS_REQ_ACCESS_FINE_LOCATION = 100;
    private int MY_PERMISSIONS_REQ_ACCESS_BACKGROUND_LOCATION = 101;

    private long backPressedTime = 0;
    public static long FINISH_INTERVAL_TIME = 2000; // 두번 눌러 종료

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 카카오 로그인 확인
//        if(sSharedPreferences.getBoolean("kakaoLogin",false)){
//            Session.getCurrentSession().checkAndImplicitOpen();
//        }
//        showCustomToast(Session.getCurrentSession().getTokenInfo().getAccessToken());
        System.out.println(Session.getCurrentSession().getTokenInfo().getAccessToken());
        mContext = this;
        AutoPermissions.Companion.loadAllPermissions(this, 100);
//        checkPermission();

        init();

        //배포를 위해 아래부분 주석처리(그룹용없으면 필요없는부분)
        getAppKeyHash();
        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this,
                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        fcmToken = instanceIdResult.getToken();
                        Log.d("Firebase", "token: " + fcmToken);
                        if (sSharedPreferences.getBoolean("kakaoLogin", false)) { // 로그인 되어있으면 토큰갱신
                            Log.d("로그인", "token: " + fcmToken);
                            tryPostFcmToken(fcmToken);
                        }
                    }
                });
        if (getIntent().getBooleanExtra("kakaoShare",false)){
            Intent intent = new Intent(MainActivity.this, GroupInviteActivity.class);
            intent.putExtra("groupId", getIntent().getIntExtra("groupId", 0));
            intent.putExtra("nickName", getIntent().getStringExtra("nickName"));
            intent.putExtra("profileUrl", getIntent().getStringExtra("profileUrl"));
            intent.putExtra("kakaoShare", true);
            startActivity(intent);
        }
    }

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
//            Log.e("name not found", e.toString());
        }
    }

    public void tryPostFcmToken(String fcmToken) {
        MainService mainService = new MainService(mContext, this);
        mainService.postFcmToken(fcmToken);
    }

    @Override
    public void updateFcmTokenSuccess() {
        Log.d("MainActivity", "fcm업데이트 성공");
    }

    @Override
    public void updateFcmTokenFail() {
        Log.d("MainActivity", "fcm업데이트 실패");
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

    void init() {
        mMapFragment = new MapFragment();
        mToDoPersonalFragment = new ToDoPersonalFragment();
        mToDoGroupFragment = new ToDoGroupFragment();

        mMainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;
        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(this, "뒤로가기를 한번 더 누르면 앱을 종료합니다", Toast.LENGTH_SHORT).show();
        }
    }
}
