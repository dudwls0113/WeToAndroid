package com.ninano.weto.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ToDoData")
public class ToDoData {
    @PrimaryKey(autoGenerate = true)
    private int todoDataNo;

    private int todoNo;

    private int order;

    private String locationName;

    private double latitude;

    private double longitude;

    private int locationMode;

    private int radius;

    private String ssid;

    private char isWiFi;

    private int timeSlot;

    private int repeatType;

    private String repeatDayOfWeek;

    private int repeatDay;

    private String date;

    private String time;

    private String isGroup;

    public String getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(String isGroup) {
        this.isGroup = isGroup;
    }

    public int getLocationMode() {
        return locationMode;
    }

    public void setLocationMode(int locationMode) {
        this.locationMode = locationMode;
    }

    public ToDoData(String locationName, double latitude, double longitude, int locationMode, int radius, String ssid, char isWiFi, int timeSlot, int repeatType, String repeatDayOfWeek, int repeatDay, String date, String time) {
        this.order = 0;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationMode = locationMode;
        this.radius = radius;
        this.ssid = ssid;
        this.isWiFi = isWiFi;
        this.timeSlot = timeSlot;
        this.repeatType = repeatType;
        this.repeatDayOfWeek = repeatDayOfWeek;
        this.repeatDay = repeatDay;
        this.date = date;
        this.time = time;
        this.isGroup = "N";
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTodoDataNo() {
        return todoDataNo;
    }

    public void setTodoDataNo(int todoDataNo) {
        this.todoDataNo = todoDataNo;
    }

    public int getTodoNo() {
        return todoNo;
    }

    public void setTodoNo(int todoNo) {
        this.todoNo = todoNo;
    }


    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
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

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public char getIsWiFi() {
        return isWiFi;
    }

    public void setIsWiFi(char isWiFi) {
        this.isWiFi = isWiFi;
    }

    public int getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(int timeSlot) {
        this.timeSlot = timeSlot;
    }

    public int getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(int repeatType) {
        this.repeatType = repeatType;
    }

    public String getRepeatDayOfWeek() {
        return repeatDayOfWeek;
    }

    public void setRepeatDayOfWeek(String repeatDayOfWeek) {
        this.repeatDayOfWeek = repeatDayOfWeek;
    }

    public int getRepeatDay() {
        return repeatDay;
    }

    public void setRepeatDay(int repeatDay) {
        this.repeatDay = repeatDay;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
    //    @NonNull
//    @Override
//    public String toString() {
//        return this.title;
//    }
}
