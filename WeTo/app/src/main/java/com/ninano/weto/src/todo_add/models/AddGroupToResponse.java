package com.ninano.weto.src.todo_add.models;

import com.google.gson.annotations.SerializedName;
import com.ninano.weto.src.main.todo_group.models.GroupData;

import java.util.ArrayList;

public class AddGroupToResponse {

    @SerializedName("code")
    int code;
    @SerializedName("message")
    String message;
    @SerializedName("groupNo")
    int groupNo;
    @SerializedName("severTodoNo")
    int severTodoNo;

    public int getGroupNo() {
        return groupNo;
    }

    public int getSeverTodoNo() {
        return severTodoNo;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
