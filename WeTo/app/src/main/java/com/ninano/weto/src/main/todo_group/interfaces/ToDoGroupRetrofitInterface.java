package com.ninano.weto.src.main.todo_group.interfaces;

import com.ninano.weto.src.DefaultResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ToDoGroupRetrofitInterface {
    @POST("/userSignUp")
    Call<DefaultResponse> postSignUp(@Body RequestBody params);

    @POST("/isExistUser")
    Call<DefaultResponse> postIsExistUser(@Body RequestBody params);
}
