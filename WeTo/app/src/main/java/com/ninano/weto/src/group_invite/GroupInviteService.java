package com.ninano.weto.src.group_invite;

import android.content.Context;

import com.ninano.weto.src.DefaultResponse;
import com.ninano.weto.src.DefaultResponse2;
import com.ninano.weto.src.group_invite.interfaces.GroupInviteRetrofitInterface;
import com.ninano.weto.src.group_invite.interfaces.GroupInviteView;
import com.ninano.weto.src.main.todo_group.interfaces.ToDoGroupRetrofitInterface;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ninano.weto.src.ApplicationClass.MEDIA_TYPE_JSON;
import static com.ninano.weto.src.ApplicationClass.getRetrofit;

public class GroupInviteService {

    private Context mContext;
    private GroupInviteView mGroupInviteView;

    GroupInviteService(Context context, GroupInviteView groupInviteView){
        mContext = context;
        mGroupInviteView = groupInviteView;
    }

    void getGroupAccept(int groupNo){
        final GroupInviteRetrofitInterface groupInviteRetrofitInterface = getRetrofit().create(GroupInviteRetrofitInterface.class);
        groupInviteRetrofitInterface.getGroupAccept(groupNo).enqueue(new Callback<DefaultResponse2>() {
            @Override
            public void onResponse(Call<DefaultResponse2> call, Response<DefaultResponse2> response) {
                final DefaultResponse2 defaultResponse2 = response.body();
                if(defaultResponse2==null){
                    mGroupInviteView.validateFailure(null);
                } else if(defaultResponse2.getCode()==100){
                    mGroupInviteView.groupAcceptSuccess();
                } else{
                    mGroupInviteView.validateFailure(defaultResponse2.getMessage());
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse2> call, Throwable t) {
                System.out.println("에러: " + t.toString());
                mGroupInviteView.validateFailure(null);
            }
        });
    }

    void postSignUp(String fcmToken, long kakaoId, String profileUrl, String nickName){
        JSONObject params = new JSONObject();
        try {
            params.put("fcmToken",fcmToken);
            params.put("kakaoId",kakaoId);
            params.put("profileUrl", profileUrl);
            params.put("nickName", nickName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final GroupInviteRetrofitInterface groupInviteRetrofitInterface = getRetrofit().create(GroupInviteRetrofitInterface.class);
        groupInviteRetrofitInterface.postSignUp(RequestBody.create(params.toString(),MEDIA_TYPE_JSON)).enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                final DefaultResponse defaultResponse = response.body();
                if(defaultResponse==null){
                    mGroupInviteView.validateFailure(null);
                } else if(defaultResponse.getCode()==100){
                    mGroupInviteView.signUpSuccess();
                } else {
                    mGroupInviteView.validateFailure(defaultResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                System.out.println(t.toString());
                mGroupInviteView.validateFailure(null);
            }
        });
    }

    void postIsExistUser(long kakaoId){
        JSONObject params = new JSONObject();
        try {
            params.put("kakaoId", kakaoId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final GroupInviteRetrofitInterface groupInviteRetrofitInterface = getRetrofit().create(GroupInviteRetrofitInterface.class);
        groupInviteRetrofitInterface.postIsExistUser(RequestBody.create(params.toString(), MEDIA_TYPE_JSON)).enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                final DefaultResponse defaultResponse = response.body();
                if (defaultResponse==null){
                    mGroupInviteView.validateFailure(null);
                } else if(defaultResponse.getCode()==100){
                    mGroupInviteView.existUser();
                } else if(defaultResponse.getCode()==101){
                    mGroupInviteView.notExistUser();
                } else {
                    mGroupInviteView.validateFailure(defaultResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                mGroupInviteView.validateFailure(null);
            }
        });
    }
}
