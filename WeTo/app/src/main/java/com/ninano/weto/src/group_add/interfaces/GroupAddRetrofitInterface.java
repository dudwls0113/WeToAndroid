package com.ninano.weto.src.group_add.interfaces;

import com.ninano.weto.src.DefaultResponse2;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GroupAddRetrofitInterface {

    @POST("/group")
    Call<DefaultResponse2> postGroup(@Body RequestBody params);
}
