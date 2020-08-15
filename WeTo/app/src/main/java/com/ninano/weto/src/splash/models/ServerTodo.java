package com.ninano.weto.src.splash.models;

import com.google.gson.annotations.SerializedName;

public class ServerTodo {
    @SerializedName("todoNo")
    int todoNo;
    @SerializedName("groupNo")
    int groupNo;
    @SerializedName("title")
    String title;
    @SerializedName("content")
    String content;
    @SerializedName("icon")
    int icon;
    @SerializedName("type")
    int type;
    @SerializedName("status")
    String status;
    @SerializedName("masterUserNo")
    int masterUserNo;
    @SerializedName("isImportant")
    String isImportant;
    @SerializedName("locationName")
    String locationName;
    @SerializedName("latitude")
    double latitude;
    @SerializedName("longitude")
    double longitude;
    @SerializedName("locationMode")
    int locationMode;
    @SerializedName("radius")
    int radius;
    @SerializedName("ssid")
    String ssid;
    @SerializedName("isWiFi")
    String isWiFi;
    @SerializedName("timeSlot")
    int timeSlot;
    @SerializedName("repeatType")
    int repeatType;
    @SerializedName("repeatDayOfWeek")
    String repeatDayOfWeek;
    @SerializedName("repeatDay")
    int repeatDay;
    @SerializedName("date")
    String date;
    @SerializedName("time")
    String time;
    @SerializedName("year")
    int year;
    @SerializedName("month")
    int month;
    @SerializedName("day")
    int day;
    @SerializedName("hour")
    int hour;
    @SerializedName("minute")
    int minute;
    @SerializedName("meetRemindTime")
    int meetRemindTime;

    public int getTodoNo() {
        return todoNo;
    }

    public int getGroupNo() {
        return groupNo;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getIcon() {
        return icon;
    }

    public int getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public int getMasterUserNo() {
        return masterUserNo;
    }

    public String getIsImportant() {
        return isImportant;
    }

    public String getLocationName() {
        return locationName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getLocationMode() {
        return locationMode;
    }

    public int getRadius() {
        return radius;
    }

    public String getSsid() {
        return ssid;
    }

    public String getIsWiFi() {
        return isWiFi;
    }

    public int getTimeSlot() {
        return timeSlot;
    }

    public int getRepeatType() {
        return repeatType;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
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

    public int getMeetRemindTime() {
        return meetRemindTime;
    }

    public String getRepeatDayOfWeek() {
        return repeatDayOfWeek;
    }

    public int getRepeatDay() {
        return repeatDay;
    }
}

