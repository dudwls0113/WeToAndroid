package com.ninano.weto.src.wifi_search;

import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.ninano.weto.R;
import com.ninano.weto.src.BaseActivity;
import com.ninano.weto.src.wifi_search.adapter.WifiListAdapter;
import com.ninano.weto.src.wifi_search.models.WifiData;

import java.util.ArrayList;
import java.util.List;

public class WifiSearchActivity extends BaseActivity {

    private BroadcastReceiver wifiScanReceiver;
    private WifiManager wifiManager;
    private Context mContext;

    private WifiListAdapter mWifiListAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<WifiData> mWifiData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_search);
        mContext = this;
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

            }
        });
        mRecyclerView.setAdapter(mWifiListAdapter);

        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        registerReceiver();
    }

    private void scanSuccess(){
        List<ScanResult> scanResults = wifiManager.getScanResults();
        System.out.println(scanResults.size());
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        for(int i=0; i<scanResults.size(); i++){
            System.out.println("," + scanResults.get(i).SSID + ",");
            System.out.println(scanResults.get(i).SSID.equals(""));
            if(!scanResults.get(i).SSID.equals("")){
                if (scanResults.get(i).BSSID.equals(wifiInfo.getBSSID())){
                    mWifiData.add(new WifiData(scanResults.get(i).SSID, scanResults.get(i).BSSID, WifiManager.calculateSignalLevel(scanResults.get(i).level, 101), true));
                } else {
                    mWifiData.add(new WifiData(scanResults.get(i).SSID, scanResults.get(i).BSSID, WifiManager.calculateSignalLevel(scanResults.get(i).level, 101), false));
                }
            }
        }
        mWifiListAdapter.notifyDataSetChanged();
    }

    private void scanFailure(){
        showCustomToast("와이파이 검색에 실패하였습니다.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegisterReceiver();
    }
}
