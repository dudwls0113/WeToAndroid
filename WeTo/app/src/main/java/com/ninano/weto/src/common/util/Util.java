package com.ninano.weto.src.common.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

    public static double getDistance(double lat1, double lon1, double lat2, double lon2, String unit) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit.equals("kilometer")) {
            dist = dist * 1.609344;
        } else if (unit.equals("meter")) {
            dist = dist * 1609.344;
        }

        return (dist);
    }

    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public static boolean compareTimeSlot(int timeSlot){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfNow = new SimpleDateFormat("HH");
        String hour = sdfNow.format(date);
        int currentHour = Integer.parseInt(hour);
        System.out.println("시간: " + currentHour);
        if (timeSlot==100){
            return true;
        } else if(timeSlot==200){
            return currentHour >= 6 && currentHour < 12;
        } else if(timeSlot==300){
            return currentHour >= 12 && currentHour < 21;
        } else if(timeSlot==400){
            return currentHour >= 21 && currentHour < 24 || currentHour>=0 && currentHour<6;
        } else {
            return false;
        }
    }
}
