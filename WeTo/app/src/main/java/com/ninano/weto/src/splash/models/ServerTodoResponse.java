package com.ninano.weto.src.splash.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ServerTodoResponse {

    @SerializedName("code")
    int code;
    @SerializedName("message")
    String message;
    @SerializedName("result")
    ArrayList<ServerTodo> result;

    public ArrayList<ServerTodo> getResult() {
        return result;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
