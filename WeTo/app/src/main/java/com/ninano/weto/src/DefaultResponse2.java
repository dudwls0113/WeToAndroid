package com.ninano.weto.src;

import com.google.gson.annotations.SerializedName;

public class DefaultResponse2 {
    @SerializedName("code")
    int code;
    @SerializedName("message")
    String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
