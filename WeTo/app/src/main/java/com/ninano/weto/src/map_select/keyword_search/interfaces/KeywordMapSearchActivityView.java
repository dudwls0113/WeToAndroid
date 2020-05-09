package com.ninano.weto.src.map_select.keyword_search.interfaces;

import com.ninano.weto.src.map_select.keyword_search.models.LocationResponse;

import java.util.ArrayList;

public interface KeywordMapSearchActivityView {

    void validateSuccess(ArrayList<LocationResponse.Location> arrayList);

    void validateFailure(String message);

}
