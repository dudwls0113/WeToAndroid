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


    private int MY_PERMISSIONS_REQ_ACCESS_FINE_LOCATION = 100;
    private int MY_PERMISSIONS_REQ_ACCESS_BACKGROUND_LOCATION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        AutoPermissions.Companion.loadAllPermissions(this, 100);
        checkPermission();
        init();
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
