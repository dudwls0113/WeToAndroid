package com.ninano.weto.src.main.todo_group.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Member implements Serializable {

    @SerializedName("userNo")
    int userNo;
    @SerializedName("profileUrl")
    String profileUrl;
    @SerializedName("nickName")
    String nickName;

    public int getUserNo() {
        return userNo;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getNickName() {
        return nickName;
    }
}
