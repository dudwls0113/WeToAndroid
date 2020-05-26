package com.ninano.weto.src.wifi_search;

import androidx.recyclerview.widget.RecyclerView;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import com.ninano.weto.R;
import com.ninano.weto.src.BaseActivity;
import com.ninano.weto.src.CellularService;
import com.ninano.weto.src.WifiService;
import com.ninano.weto.src.wifi_search.adapter.WifiListAdapter;
import com.ninano.weto.src.wifi_search.models.WifiData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.ninano.weto.src.ApplicationClass.sSharedPreferences;

public class WifiSearchActivity extends BaseActivity {

    private BroadcastReceiver wifiScanReceiver;
    private WifiManager wifiManager;
    private Context mContext;

    private WifiListAdapter mWifiListAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<WifiData> mWifiData = new ArrayList<>();

    private String currentWifiBSSID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_search);
        mContext = this;
        showProgressDialog();
        init();
    }

    private void registerReceiver(){
        if(wifiScanReceiver!=null){
            return;
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        this.wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                    if (success){
                        scanSuccess();
                    } else{
                        scanFailure();
                    }
                }
            }
        };

        getApplicationContext().registerReceiver(wifiScanReceiver, intentFilter);
        boolean success = wifiManager.startScan();
        if (!success){
            scanFailure();
        }
    }

    private void unRegisterReceiver(){
        if (wifiScanReceiver!=null){
            getApplicationContext().unregisterReceiver(wifiScanReceiver);
            wifiScanReceiver = null;
        }
    }

    void init(){
        mRecyclerView = findViewById(R.id.wifi_search_rv_list);
        mWifiListAdapter = new WifiListAdapter(mContext, mWifiData, new WifiListAdapter.WifiClickListener() {
            @Override
            public void wifiClick(int pos) {
                Intent intent = new Intent();
                intent.putExtra("bssid", mWifiData.get(pos).getBssid());
                intent.putExtra("ssid", mWifiData.get(pos).getSsid());
                intent.putExtra("connected", mWifiData.get(pos).isConnected());
                setResult(111, intent);
                finish();
//                boolean isUseWifi = sSharedPreferences.getBoolean("useWifi",false); // jobScheduler는 한번만 돌리고 디비 값을 가져와서 비교함
//                if(!isUseWifi){
//                    SharedPreferences.Editor editor = sSharedPreferences.edit();
//                    editor.putBoolean("useWifi", true);
//                    editor.apply();
//                    JobScheduler jobScheduler = (JobScheduler)mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
//                    if (jobScheduler != null) {
//                        jobScheduler.cancelAll();
//                    }
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                        if (jobScheduler != null) {
//                            if (mWifiData.get(pos).isConnected()){
//                                System.out.println("연결되어 있는 와이파이");
//                                SharedPreferences.Editor editor1 = sSharedPreferences.edit();
//                                editor1.putBoolean("firstWifiNoti", true);
//                                editor1.apply();
//                            }
//                            System.out.println(currentWifiBSSID);
//                            jobScheduler.schedule(new JobInfo.Builder(0, new ComponentName(mContext, WifiService.class))
//                                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
//                                    .setPeriodic(TimeUnit.MINUTES.toMillis(15))
//                                    .build());
//                            jobScheduler.schedule(new JobInfo.Builder(1,new ComponentName(mContext, CellularService.class))
//                                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_CELLULAR)
//                                    .setPeriodic(TimeUnit.MINUTES.toMillis(15))
//                                    .build());
//                        }
//                    }
//                } else {
//                    // db에 와이파이 정보만 추가
//                    SharedPreferences.Editor editor = sSharedPreferences.edit();
//                    editor.putBoolean("useWifi", false);
//                    editor.apply();
//                }

            }
        });
        mRecyclerView.setAdapter(mWifiListAdapter);

        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        registerReceiver();
    }

    private void scanSuccess(){
        List<ScanResult> scanResults = wifiManager.getScanResults();
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        for(int i=0; i<scanResults.size(); i++){
            if(!scanResults.get(i).SSID.equals("")){
                if (scanResults.get(i).BSSID.equals(wifiInfo.getBSSID())){
                    currentWifiBSSID = wifiInfo.getBSSID();
                    mWifiData.add(new WifiData(scanResults.get(i).SSID, scanResults.get(i).BSSID, WifiManager.calculateSignalLevel(scanResults.get(i).level, 101), true));
                } else {
                    mWifiData.add(new WifiData(scanResults.get(i).SSID, scanResults.get(i).BSSID, WifiManager.calculateSignalLevel(scanResults.get(i).level, 101), false));
                }
            }
        }
        mWifiListAdapter.notifyDataSetChanged();
        unRegisterReceiver();
        hideProgressDialog();
    }

    private void scanFailure(){
        showCustomToast("와이파이 검색에 실패하였습니다.");
        unRegisterReceiver();
        hideProgressDialog();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegisterReceiver();
    }
}
