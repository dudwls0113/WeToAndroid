package com.ninano.weto.db;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "ToDoData")
public class ToDoData {
    @PrimaryKey(autoGenerate = true)
    private int todoDataNo;

    private int todoNo;

    private String locationName;

    private double latitude;

    private double longitude;

    private int locationMode;

    private int radius;

    private String ssid;

    private char isWiFi;

    private int timeSlot;

    private int repeatType; //매일, 매주, 매월

    private String repeatDayOfWeek; //월,화  월,수,금

    private int repeatDay;// 23

    private String date;

    private String time;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int severTodoNo;
    private char isMeet;
    private int meetRemindTime;

    public char getIsMeet() {
        return isMeet;
    }

    public void setIsMeet(char isMeet) {
        this.isMeet = isMeet;
    }

    public int getMeetRemindTime() {
        return meetRemindTime;
    }

    public void setMeetRemindTime(int meetRemindTime) {
        this.meetRemindTime = meetRemindTime;
    }

    public int getSeverTodoNo() {
        return severTodoNo;
    }

    public void setSeverTodoNo(int severTodoNo) {
        this.severTodoNo = severTodoNo;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getLocationMode() {
        return locationMode;
    }

    public void setLocationMode(int locationMode) {
        this.locationMode = locationMode;
    }

    public ToDoData(String locationName, double latitude, double longitude, int locationMode, int radius, String ssid, char isWiFi, int timeSlot, int repeatType, String repeatDayOfWeek, int repeatDay, String date, String time
                    , int year, int month, int day, int hour, int minute) {
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
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    @Ignore
    public ToDoData(String locationName, double latitude, double longitude, int locationMode, int radius, String ssid, char isWiFi, int timeSlot, int repeatType, String repeatDayOfWeek, int repeatDay, String date, String time
            , int year, int month, int day, int hour, int minute, int severTodoNo) {
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
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.severTodoNo = severTodoNo;
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

}
