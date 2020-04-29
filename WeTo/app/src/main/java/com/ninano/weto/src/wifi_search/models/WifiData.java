package com.ninano.weto.src.wifi_search.models;

public class WifiData {

    String ssid;
    String bssid;
    int strength;
    boolean isConnected;

    public WifiData(String ssid, String bssid, int strength, boolean isConnected) {
        this.ssid = ssid;
        this.bssid = bssid;
        this.strength = strength;
        this.isConnected = isConnected;
    }

    public String getSsid() {
        return ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public int getStrength() {
        return strength;
    }

    public boolean isConnected() {
        return isConnected;
    }
}
