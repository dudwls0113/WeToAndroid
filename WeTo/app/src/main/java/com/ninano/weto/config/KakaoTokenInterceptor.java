package com.ninano.weto.config;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.ninano.weto.src.ApplicationClass.X_ACCESS_TOKEN;
import static com.ninano.weto.src.ApplicationClass.sSharedPreferences;

public class KakaoTokenInterceptor implements Interceptor {

    @Override
    @NonNull
    public Response intercept(@NonNull final Chain chain) throws IOException {
        final Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("Authorization", "KakaoAK cf8eee92b4f04837a0b43ff89e91f039");
        return chain.proceed(builder.build());
    }
}

