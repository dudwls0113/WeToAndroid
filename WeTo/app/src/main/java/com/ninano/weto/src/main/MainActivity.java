package com.ninano.weto.src.main;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ninano.weto.R;
import com.ninano.weto.src.BaseActivity;
import com.ninano.weto.src.BaseFragment;
import com.ninano.weto.src.WifiReceiver;
import com.ninano.weto.src.custom.NonSwipeViewPager;
import com.ninano.weto.src.main.adpater.MainViewPagerAdapter;
import com.ninano.weto.src.main.map.MapFragment;
import com.ninano.weto.src.main.todo_group.ToDoGroupFragment;
import com.ninano.weto.src.main.todo_personal.ToDoPersonalFragment;
import com.ninano.weto.src.todo_add.AddPersonalToDoActivity;
import com.ninano.weto.src.wifi_search.WifiSearchActivity;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import devlight.io.library.ntb.NavigationTabBar;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        AutoPermissions.Companion.loadAllPermissions(this, 100);
        init();
//        TextView textView = findViewById(R.id.test);
//        textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                JobScheduler jobScheduler = (JobScheduler)getSystemService(Context.JOB_SCHEDULER_SERVICE);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                    jobScheduler.schedule(new JobInfo.Builder(0, new ComponentName(MainActivity.this, WifiService.class))
//                            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
//                            .setOverrideDeadline(0)
//                            .build());
//                }
//                startActivity(new Intent(MainActivity.this, AddPersonalToDoActivity.class));
//            }
//        });

//        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//
//        TextView textView1 = findViewById(R.id.test2);
//        textView1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                IntentFilter intentFilter = new IntentFilter();
//                intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
//                getApplicationContext().registerReceiver(wifiScanReceiver, intentFilter);
//                boolean success = wifiManager.startScan();
//                if (!success){
//
//                }
//                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//                Log.d("서비스", wifiInfo + ", " + wifiInfo.getBSSID() + ", " + wifiInfo.getSSID());
//                Intent intent = new Intent(MainActivity.this, WifiSearchActivity.class);
//                startActivity(intent);
//            }
//        });

    }

    void init(){
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
