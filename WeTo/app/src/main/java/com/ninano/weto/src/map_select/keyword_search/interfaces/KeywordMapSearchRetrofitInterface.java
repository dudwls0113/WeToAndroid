package com.ninano.weto.src.map_select.keyword_search.interfaces;
import com.ninano.weto.src.map_select.keyword_search.models.LocationResponse;
import retrofit2.Call;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface KeywordMapSearchRetrofitInterface {
//    @GET("/test")
//    @GET("/v2/local/search/keyword")
//    Call<DefaultResponse> getKeywordLocation();

    @GET("/v2/local/search/keyword")
    Call<LocationResponse> getKeywordLocation(
            @Query("query") final String keyword,
            @Query("x") final double longitude,
            @Query("y") final double latitude,
            @Query("sort") final String sort
    );


}
