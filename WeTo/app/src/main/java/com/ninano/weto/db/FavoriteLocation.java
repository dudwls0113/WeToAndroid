package com.ninano.weto.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class FavoriteLocation {

    @PrimaryKey(autoGenerate = true)
    private int favoriteNo;

    private String name;

    private double latitude;

    private double longitude;

    private boolean isConfirmed;

    private boolean isWiFi;

    private String ssid;

    private String wifiName;

    @Ignore
    private boolean isSelected;
    @Ignore
    private boolean isLast;

    public FavoriteLocation(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isSelected = false;
        this.isLast = false;
        this.isConfirmed = true;
        this.isWiFi = false;
    }

    public FavoriteLocation(String name, double latitude, double longitude, String wifiName, String ssid) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isSelected = false;
        this.isLast = false;
        this.isConfirmed = true;
        this.wifiName = wifiName;
        this.ssid = ssid;
        this.isWiFi = true;
    }


    public FavoriteLocation(String name) {
        //맨처음 집,학교,회사
        this.name = name;
        this.latitude = 0;
        this.longitude = 0;
        this.isSelected = false;
        this.isLast = false;
        this.isConfirmed = false;
        this.isWiFi = false;
    }

    public FavoriteLocation() {
        //기타일정
        this.name = "+";
        this.latitude = 0;
        this.longitude = 0;
        this.isSelected = false;
        this.isLast = true;
        this.isConfirmed = true;
        this.isWiFi = false;
    }

    public void setPlaceConfirm(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.isConfirmed = true;
    }

    public int getFavoriteNo() {
        return favoriteNo;
    }

    public void setFavoriteNo(int favoriteNo) {
        this.favoriteNo = favoriteNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    public boolean isWiFi(){
        return isWiFi;
    }

    public void setWiFi(boolean wiFi) {
        isWiFi = wiFi;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getWifiName() {
        return wifiName;
    }

    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }

    @NonNull
    @Override
    public String toString() {
        return favoriteNo + ", " + name + ", " + latitude + ", " + longitude;
    }
}
