package com.ninano.weto.db;

import androidx.annotation.NonNull;
import androidx.room.Ignore;

import java.io.Serializable;

public class ToDoWithData implements Serializable {
    private int todoNo;
    private int todoDataNo;
    private String title;
    private String content;
    private int icon;
    private int type;
    private String status;
    private String locationName;
    private int locationMode;
    private double latitude;
    private double longitude;
    private int radius;
    private String ssid;
    private char isWiFi;
    private int timeSlot;
    private int repeatType;
    private String repeatDayOfWeek;
    private int repeatDay;
    private String date;
    private char isImportant;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String time;
    private char isGroup;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

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

    @Ignore
    boolean isEditMode = true;

    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    public int getTodoNo() {
        return todoNo;
    }

    public void setTodoNo(int todoNo) {
        this.todoNo = todoNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getType() {
        return type;
    }

    public int getLocationMode() {
        return locationMode;
    }

    public void setLocationMode(int locationMode) {
        this.locationMode = locationMode;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public char getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(char isGroup) {
        this.isGroup = isGroup;
    }

    public char getIsImportant() {
        return isImportant;
    }

    public void setIsImportant(char isImportant) {
        this.isImportant = isImportant;
    }


    @NonNull
    @Override
    public String toString() {
        return "todoNo: " + todoNo + "todoDataNo: " + todoDataNo + " title: " + title + " icon: " + icon + " type: " + type + " status: " + status +
                " locationName: " + locationName + " latitude: " + latitude + " longitude: " + longitude +
                " radius: " + radius + " ssid: " + ssid + " isWiFi: " + isWiFi +
                " timeSlot: " + timeSlot + " repeatType: " + repeatType + " repeatDayOfWeek: " + repeatDayOfWeek + " repeatDay: " + repeatDay +
                "date: " + date +  " time:" + time + "year: " + year + "month: " + month + "day: " + day + "hour: " + hour + "day: " + day +
                " isGroup: " + String.valueOf(isGroup) + "\n";
    }

    public int getTodoDataNo() {
        return todoDataNo;
    }

    public void setTodoDataNo(int todoDataNo) {
        this.todoDataNo = todoDataNo;
    }
}
