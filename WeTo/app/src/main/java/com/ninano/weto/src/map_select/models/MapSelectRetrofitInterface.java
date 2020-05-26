package com.ninano.weto.src.map_select.models;

import com.ninano.weto.src.map_select.keyword_search.models.AddressResponse;
import com.ninano.weto.src.map_select.keyword_search.models.LocationResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MapSelectRetrofitInterface {
    @GET("/v2/local/geo/coord2address")
    Call<AddressResponse> getAddressFromXy(
            @Query("x") final double longitude,
            @Query("y") final double latitude
    );
}
