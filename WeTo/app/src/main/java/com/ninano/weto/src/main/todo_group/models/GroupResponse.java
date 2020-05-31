package com.ninano.weto.src.main.todo_group.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GroupResponse {

    @SerializedName("result")
    ArrayList<GroupData> groupData;
    @SerializedName("code")
    int code;
    @SerializedName("message")
    String message;

    public ArrayList<GroupData> getGroupData() {
        return groupData;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
