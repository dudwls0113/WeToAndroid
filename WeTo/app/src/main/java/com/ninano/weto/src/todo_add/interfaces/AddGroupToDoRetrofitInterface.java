package com.ninano.weto.src.todo_add.interfaces;

import com.ninano.weto.src.DefaultResponse2;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AddGroupToDoRetrofitInterface {

    @POST("/todo")
    Call<DefaultResponse2> postToDo(@Body RequestBody params);
}
