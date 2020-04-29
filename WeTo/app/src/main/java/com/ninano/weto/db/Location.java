package com.ninano.weto.db;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity(tableName = "Location")
public class Location {
    @PrimaryKey(autoGenerate = true)
    private int locationNo;

    private int todoNo;

    private int meetNo;

    private String title;

    private double latitude;

    private double longitude;


    private int radius;

    private String ssid;

    private char isWiFi;

    @TypeConverters({Converters.class})
    private Date date;

    private int timeSlot;



    public Location(int todoNo, int meetNo, String title, double latitude, double longitude, int radius, String ssid, char isWiFi, Date date, int timeSlot) {
        this.todoNo = todoNo;
        this.meetNo = meetNo;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.ssid = ssid;
        this.isWiFi = isWiFi;
        this.date = date;
        this.timeSlot = timeSlot;
    }

    public void setLocationNo(int locationNo) {
        this.locationNo = locationNo;
    }

    public void setTodoNo(int todoNo) {
        this.todoNo = todoNo;
    }

    public void setMeetNo(int meetNo) {
        this.meetNo = meetNo;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public void setIsWiFi(char isWiFi) {
        this.isWiFi = isWiFi;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTimeSlot(int timeSlot) {
        this.timeSlot = timeSlot;
    }

    public int getLocationNo() {
        return locationNo;
    }

    public int getTodoNo() {
        return todoNo;
    }

    public int getMeetNo() {
        return meetNo;
    }

    public String getTitle() {
        return title;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getRadius() {
        return radius;
    }

    public String getSsid() {
        return ssid;
    }

    public char getIsWiFi() {
        return isWiFi;
    }

    public Date getDate() {
        return date;
    }

    public int getTimeSlot() {
        return timeSlot;
    }

    //    @NonNull
//    @Override
//    public String toString() {
//        return this.title;
//    }
}
