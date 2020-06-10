package com.ninano.weto.src.main;

import android.content.Context;

import com.ninano.weto.src.DefaultResponse;
import com.ninano.weto.src.main.interfaces.MainActivityView;
import com.ninano.weto.src.main.interfaces.MainRetrofitInterface;
import com.ninano.weto.src.main.todo_group.interfaces.ToDoGroupRetrofitInterface;
import com.ninano.weto.src.main.todo_group.models.GroupResponse;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ninano.weto.src.ApplicationClass.MEDIA_TYPE_JSON;
import static com.ninano.weto.src.ApplicationClass.getRetrofit;

public class MainService {

    private Context mContext;
    private MainActivityView mMainActivityView;

    MainService(Context context, MainActivityView mainActivityView) {
        mContext = context;
        this.mMainActivityView = mainActivityView;
    }

    void postFcmToken(String fcmToken) {
        JSONObject params = new JSONObject();
        try {
            params.put("fcmToken", fcmToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MainRetrofitInterface mainRetrofitInterface = getRetrofit().create(MainRetrofitInterface.class);
        mainRetrofitInterface.postFcmToken(RequestBody.create(params.toString(), MEDIA_TYPE_JSON)).enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                final DefaultResponse defaultResponse = response.body();
                if (defaultResponse == null) {
                    mMainActivityView.updateFcmTokenFail();
                } else if (defaultResponse.getCode() == 100) {
                    mMainActivityView.updateFcmTokenSuccess();
                } else {
                    mMainActivityView.updateFcmTokenFail();
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                System.out.println(t.toString());
                mMainActivityView.updateFcmTokenFail();
            }
        });
    }
}
