package com.ninano.weto.src;

import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class AlertService extends Service {

    WifiManager wifiManager;
    ArrayList<String> ssidArrayList = new ArrayList<>();
    ConnectivityManager connectivityManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (connectivityManager != null && connectivityManager.getActiveNetwork() != null) {
                Network network = connectivityManager.getActiveNetwork();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "AlertService 시작", Toast.LENGTH_SHORT).show();
        ssidArrayList = intent.getStringArrayListExtra("ssid");

        return super.onStartCommand(intent, flags, startId);
    }
}
