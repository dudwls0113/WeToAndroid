package com.ninano.weto.src.main.interfaces;

import com.ninano.weto.src.DefaultResponse;
import com.ninano.weto.src.main.todo_group.models.GroupResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MainRetrofitInterface {
    @POST("/fcm")
    Call<DefaultResponse> postFcmToken(@Body RequestBody params);
}
