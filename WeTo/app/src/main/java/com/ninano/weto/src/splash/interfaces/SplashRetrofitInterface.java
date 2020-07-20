package com.ninano.weto.src.splash.interfaces;

import com.ninano.weto.src.DefaultResponse;
import com.ninano.weto.src.splash.models.ServerTodoResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface SplashRetrofitInterface {
    @GET("/todo")
    Call<ServerTodoResponse> getAllTodo();
}
