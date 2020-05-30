package com.ninano.weto.src;

import com.google.gson.annotations.SerializedName;

public class DefaultResponse {
    @SerializedName("code")
    int code;
    @SerializedName("message")
    String message;
    @SerializedName("isSuccess")
    boolean isSuccess;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
