package com.ninano.weto.src.main.todo_group;

import android.content.Context;

import com.ninano.weto.src.DefaultResponse;
import com.ninano.weto.src.main.todo_group.interfaces.ToDoGroupRetrofitInterface;
import com.ninano.weto.src.main.todo_group.interfaces.ToDoGroupView;
import com.ninano.weto.src.main.todo_group.models.GroupResponse;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ninano.weto.src.ApplicationClass.MEDIA_TYPE_JSON;
import static com.ninano.weto.src.ApplicationClass.getRetrofit;

public class ToDoGroupService {

    private Context mContext;
    private ToDoGroupView mToDoGroupView;

    ToDoGroupService(Context context, ToDoGroupView toDoGroupView){
        mContext = context;
        mToDoGroupView = toDoGroupView;
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

        ToDoGroupRetrofitInterface toDoGroupRetrofitInterface = getRetrofit().create(ToDoGroupRetrofitInterface.class);
        toDoGroupRetrofitInterface.postSignUp(RequestBody.create(params.toString(),MEDIA_TYPE_JSON)).enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                final DefaultResponse defaultResponse = response.body();
                if(defaultResponse==null){
                    mToDoGroupView.validateFailure(null);
                } else if(defaultResponse.getCode()==100){
                    mToDoGroupView.signUpSuccess();
                } else {
                    mToDoGroupView.validateFailure(defaultResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                System.out.println(t.toString());
                mToDoGroupView.validateFailure(null);
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

        ToDoGroupRetrofitInterface toDoGroupRetrofitInterface = getRetrofit().create(ToDoGroupRetrofitInterface.class);
        toDoGroupRetrofitInterface.postIsExistUser(RequestBody.create(params.toString(), MEDIA_TYPE_JSON)).enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                final DefaultResponse defaultResponse = response.body();
                if (defaultResponse==null){
                    mToDoGroupView.validateFailure(null);
                } else if(defaultResponse.getCode()==100){
                    mToDoGroupView.existUser();
                } else if(defaultResponse.getCode()==101){
                    mToDoGroupView.notExistUser();
                } else {
                    mToDoGroupView.validateFailure(defaultResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                mToDoGroupView.validateFailure(null);
            }
        });
    }

    void getGroup(){
        ToDoGroupRetrofitInterface toDoGroupRetrofitInterface = getRetrofit().create(ToDoGroupRetrofitInterface.class);
        toDoGroupRetrofitInterface.getGroup().enqueue(new Callback<GroupResponse>() {
            @Override
            public void onResponse(Call<GroupResponse> call, Response<GroupResponse> response) {
                final GroupResponse groupResponse = response.body();
                if(groupResponse==null){
                    mToDoGroupView.validateFailure(null);
                } else if(groupResponse.getCode()==100){
                    mToDoGroupView.getGroupSuccess(groupResponse.getGroupData());
                } else {
                    mToDoGroupView.validateFailure(groupResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<GroupResponse> call, Throwable t) {
                mToDoGroupView.validateFailure(null);
            }
        });
    }
}
