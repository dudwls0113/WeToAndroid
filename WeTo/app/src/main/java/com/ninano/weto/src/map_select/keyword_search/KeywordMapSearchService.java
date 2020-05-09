package com.ninano.weto.src.map_select.keyword_search;

import com.ninano.weto.config.XAccessTokenInterceptor;
import com.ninano.weto.src.map_select.keyword_search.interfaces.KeywordMapSearchActivityView;
import com.ninano.weto.src.map_select.keyword_search.interfaces.KeywordMapSearchRetrofitInterface;
import com.ninano.weto.src.map_select.keyword_search.models.LocationResponse;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ninano.weto.src.ApplicationClass.getRetrofit;
import static com.ninano.weto.src.ApplicationClass.retrofit;

class KeywordMapSearchService {
    private final KeywordMapSearchActivityView keywordMapSearchActivityView;

    KeywordMapSearchService(final KeywordMapSearchActivityView keywordMapSearchActivityView) {
        this.keywordMapSearchActivityView = keywordMapSearchActivityView;

    }

    private Retrofit getKakaoRenulltrofit() {
        Retrofit retrofit = null;
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .addNetworkInterceptor(new XAccessTokenInterceptor()) // JWT 자동 헤더 전송
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://dapi.kakao.com")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

    //sort: distance 또는 accuracy, 기본 accuracy
    void getKeywordLocation(String keyword, double longitude, double latitude, String sort) {
        final KeywordMapSearchRetrofitInterface mainRetrofitInterface = getKakaoRenulltrofit().create(KeywordMapSearchRetrofitInterface.class);
        mainRetrofitInterface.getKeywordLocation(keyword, longitude, latitude, sort).enqueue(new Callback<LocationResponse>() {
            @Override
            public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
                final LocationResponse locationResponse = response.body();
                if (locationResponse == null) {
                    keywordMapSearchActivityView.validateFailure(null);
                    return;
                }
            }

            @Override
            public void onFailure(Call<LocationResponse> call, Throwable t) {
                keywordMapSearchActivityView.validateFailure(null);
            }
        });
    }


}
