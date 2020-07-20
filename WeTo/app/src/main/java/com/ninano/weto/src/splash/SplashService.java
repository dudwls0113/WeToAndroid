package com.ninano.weto.src.splash;

import android.content.Context;

import com.ninano.weto.src.DefaultResponse;
import com.ninano.weto.src.main.interfaces.MainActivityView;
import com.ninano.weto.src.main.interfaces.MainRetrofitInterface;
import com.ninano.weto.src.splash.interfaces.SplashActivityView;
import com.ninano.weto.src.splash.interfaces.SplashRetrofitInterface;
import com.ninano.weto.src.splash.models.ServerTodoResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ninano.weto.src.ApplicationClass.MEDIA_TYPE_JSON;
import static com.ninano.weto.src.ApplicationClass.getRetrofit;

public class SplashService {

    private Context mContext;
    private SplashActivityView mSplashActivityView;

    SplashService(Context context, SplashActivityView mSplashActivityView) {
        mContext = context;
        this.mSplashActivityView = mSplashActivityView;
    }

    void getAllTodo() {
        SplashRetrofitInterface splashRetrofitInterface = getRetrofit().create(SplashRetrofitInterface.class);
        splashRetrofitInterface.getAllTodo().enqueue(new Callback<ServerTodoResponse>() {
            @Override
            public void onResponse(Call<ServerTodoResponse> call, Response<ServerTodoResponse> response) {
                final ServerTodoResponse serverTodoResponse = response.body();
                if (serverTodoResponse == null) {
                    mSplashActivityView.failGetTodo();
                } else if (serverTodoResponse.getCode() == 100) {
                    try {
                        mSplashActivityView.successGetTodo(serverTodoResponse.getResult());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                } else {
                    mSplashActivityView.failGetTodo();
                }
            }

            @Override
            public void onFailure(Call<ServerTodoResponse> call, Throwable t) {
                System.out.println(t.toString());
                mSplashActivityView.failGetTodo();
            }
        });
    }
}
