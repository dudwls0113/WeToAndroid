package com.ninano.weto.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

public class LocationToDo {
    private int todoNo;
    private String title;
    private String content;
    private int icon;
    private char type;
    private String status;
    private char isGroup;
    private int locationNo;
    private int meetNo;
    private String locationTitle;
    private double latitude;
    private double longitude;
    private int radius;
    private String ssid;
    private char isWiFi;
    private int timeSlot;

    @TypeConverters({Converters.class})
    private Date date;


    public void setTodoNo(int todoNo) {
        this.todoNo = todoNo;
    }

    public int getTodoNo() {
        return todoNo;
    }

    public void setNo(int no) {
        this.todoNo = no;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setType(char type) {
        this.type = type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setIsGroup(char isGroup) {
        this.isGroup = isGroup;
    }

    public int getNo() {
        return todoNo;
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

    public char getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public char getIsGroup() {
        return isGroup;
    }

    public int getLocationNo() {
        return locationNo;
    }

    public int getMeetNo() {
        return meetNo;
    }

    public String getLocationTitle() {
        return locationTitle;
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

    public void setLocationNo(int locationNo) {
        this.locationNo = locationNo;
    }

    public void setMeetNo(int meetNo) {
        this.meetNo = meetNo;
    }

    public void setLocationTitle(String locationTitle) {
        this.locationTitle = locationTitle;
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

    @NonNull
    @Override
    public String toString() {
        return this.title + " " + content + " " + icon + " " + type + " " + status + " " + isGroup + " " +
                locationNo + " " + locationTitle + " " + latitude + " " + longitude + " " + "\n";
    }
}
