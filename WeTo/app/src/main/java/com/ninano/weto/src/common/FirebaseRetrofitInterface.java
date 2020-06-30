package com.ninano.weto.src.common;

import com.ninano.weto.src.DefaultResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FirebaseRetrofitInterface {
    @POST("/todo/success/{todoNo}")
    Call<DefaultResponse> todoRegisterSuccess(@Path("todoNo") final int todoNo);

    @GET("/arrive/{todoNo}")
    Call<DefaultResponse> arrivePush(@Path("todoNo") final int todoNo);
}
