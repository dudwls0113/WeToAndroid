package com.ninano.weto.src.map_select;

import android.util.Log;

import com.ninano.weto.config.KakaoTokenInterceptor;
import com.ninano.weto.src.map_select.keyword_search.interfaces.KeywordMapSearchActivityView;
import com.ninano.weto.src.map_select.keyword_search.interfaces.KeywordMapSearchRetrofitInterface;
import com.ninano.weto.src.map_select.keyword_search.models.AddressResponse;
import com.ninano.weto.src.map_select.keyword_search.models.LocationResponse;
import com.ninano.weto.src.map_select.models.MapSelectActivityView;
import com.ninano.weto.src.map_select.models.MapSelectRetrofitInterface;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class MapSelectService {
    private final MapSelectActivityView mapSelectActivityView;

    MapSelectService(final MapSelectActivityView mapSelectActivityView) {
        this.mapSelectActivityView = mapSelectActivityView;

    }

    private Retrofit getKakaoRenulltrofit() {
        Retrofit retrofit = null;
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .addNetworkInterceptor(new KakaoTokenInterceptor()) // 카카오
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://dapi.kakao.com")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

    //sort: distance 또는 accuracy, 기본 accuracy
    void getAddressFromXy(double longitude, double latitude) {
        final MapSelectRetrofitInterface mapSelectRetrofitInterface = getKakaoRenulltrofit().create(MapSelectRetrofitInterface.class);
        mapSelectRetrofitInterface.getAddressFromXy(latitude, longitude).enqueue(new Callback<AddressResponse>() {
            @Override
            public void onResponse(Call<AddressResponse> call, Response<AddressResponse> response) {
                final AddressResponse addressResponse = response.body();
                if (addressResponse == null) {
                    mapSelectActivityView.validateFailure(null);
                } else {
                    mapSelectActivityView.validateSuccess(addressResponse.getLocations().get(0));
                }
            }

            @Override
            public void onFailure(Call<AddressResponse> call, Throwable t) {
                Log.e("에러", t.toString());
                mapSelectActivityView.validateFailure(null);
            }
        });
    }
}
