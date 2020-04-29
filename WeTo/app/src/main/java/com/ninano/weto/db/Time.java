package com.ninano.weto.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "Time")
public class Time {
    @PrimaryKey(autoGenerate = true)
    private int timeNo;

    private String repeatType;

    private String repeatDayOfWeek;

    private int repeatDay;

    private int time;

//    private int todoNo;


//    @NonNull
//    @Override
//    public String toString() {
//        return this.title;
//    }
}
