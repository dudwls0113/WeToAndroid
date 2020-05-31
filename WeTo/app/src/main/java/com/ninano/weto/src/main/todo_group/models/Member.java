package com.ninano.weto.src.main.todo_group.models;

import com.google.gson.annotations.SerializedName;

public class Member {

    @SerializedName("profileUrl")
    String profileUrl;
    @SerializedName("nickName")
    String nickName;

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getNickName() {
        return nickName;
    }
}
