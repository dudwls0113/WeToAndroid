package com.ninano.weto.src.splash.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ServerTodoResponse {

    public class TodoArrayResponse {
        @SerializedName("add")
        ArrayList<ServerTodo> addList;

        public ArrayList<ServerTodo> getAddList() {
            return addList;
        }
    }

    @SerializedName("code")
    int code;
    @SerializedName("message")
    String message;
    @SerializedName("result")
    TodoArrayResponse result;

    public TodoArrayResponse getResult() {
        return result;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
