package com.ninano.weto.src.map_select.models;

import com.ninano.weto.src.map_select.keyword_search.models.AddressResponse;
import com.ninano.weto.src.map_select.keyword_search.models.LocationResponse;

import java.util.ArrayList;

public interface MapSelectActivityView {

    void validateSuccess(AddressResponse.Address address);

    void validateFailure(String message);

}
