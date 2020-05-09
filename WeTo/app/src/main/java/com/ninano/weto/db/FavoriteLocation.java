package com.ninano.weto.db;

import androidx.room.PrimaryKey;

//@Entity(foreignKeys = @ForeignKey(entity = ToDo.class, parentColumns = "Todo", childColumns = "todoNo"))
public class FavoriteLocation {
    @PrimaryKey(autoGenerate = true)
    private int favoriteNo;

    private String name;

    private double latitude;

    private double longitude;

//    @NonNull
//    @Override
//    public String toString() {
//        return this.title;
//    }
}
