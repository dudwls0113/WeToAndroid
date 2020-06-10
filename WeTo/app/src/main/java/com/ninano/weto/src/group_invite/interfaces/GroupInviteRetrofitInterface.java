package com.ninano.weto.src.group_invite.interfaces;

import com.ninano.weto.src.DefaultResponse;
import com.ninano.weto.src.DefaultResponse2;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GroupInviteRetrofitInterface {

    @GET("/group/accept/{groupNo}")
    Call<DefaultResponse2> getGroupAccept(@Path("groupNo") final int groupNo);

    @POST("/userSignUp")
    Call<DefaultResponse> postSignUp(@Body RequestBody params);

    @POST("/isExistUser")
    Call<DefaultResponse> postIsExistUser(@Body RequestBody params);

}
