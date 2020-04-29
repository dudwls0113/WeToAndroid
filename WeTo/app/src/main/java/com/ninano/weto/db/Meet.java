package com.ninano.weto.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Meet")
public class Meet {
    @PrimaryKey(autoGenerate = true)
    private int meetNo;

    private String title;

    private String content;

    private int remindTime;

//    @NonNull
//    @Override
//    public String toString() {
//        return this.title;
//    }
}
