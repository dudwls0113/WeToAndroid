package com.ninano.weto.src.map_select.keyword_search.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class LocationResponse {

    public class MetaData{
        @SerializedName("is_end")
        private boolean is_end;

        @SerializedName("pageable_count")
        private int pageable_count;

        @SerializedName("total_count")
        private int total_count;

        public boolean isIs_end() {
            return is_end;
        }

        public int getPageable_count() {
            return pageable_count;
        }

        public int getTotal_count() {
            return total_count;
        }
    }

    public static class Location implements Serializable{
        @SerializedName("address_name")
        private String addressName;

        public Location(String addressName, String placeName, String longitude, String latitude) {
            this.addressName = addressName;
            this.placeName = placeName;
            this.longitude = longitude;
            this.latitude = latitude;
        }

        public Location() {

        }

        public void setAddressName(String addressName) {
            this.addressName = addressName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public void setCategoryGroupName(String categoryGroupName) {
            this.categoryGroupName = categoryGroupName;
        }

        public void setDistanceStr(String distanceStr) {
            this.distanceStr = distanceStr;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setPlaceName(String placeName) {
            this.placeName = placeName;
        }

        public void setPlaceUrl(String placeUrl) {
            this.placeUrl = placeUrl;
        }

        public void setRoadAddressName(String roadAddressName) {
            this.roadAddressName = roadAddressName;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        @SerializedName("category_name")
        private String categoryName;

        @SerializedName("category_group_name")
        private String categoryGroupName;

        @SerializedName("distance")
        private String distanceStr;

        @SerializedName("id")
        private String id;

        @SerializedName("phone")
        private String phone;

        @SerializedName("place_name")
        private String placeName;

        @SerializedName("place_url")
        private String placeUrl;

        @SerializedName("road_address_name")
        private String roadAddressName;

        @SerializedName("x")
        private String longitude;

        @SerializedName("y")
        private String latitude;

        public String getAddressName() {
            return addressName;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public String getDistanceStr() {
            return distanceStr;
        }

        public String getId() {
            return id;
        }

        public String getPhone() {
            return phone;
        }

        public String getPlaceName() {
            return placeName;
        }

        public String getPlaceUrl() {
            return placeUrl;
        }

        public String getRoadAddressName() {
            return roadAddressName;
        }

        public String getLongitude() {
            return longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public String getCategoryGroupName() {
            return categoryGroupName;
        }

        @NonNull
        @Override
        public String toString() {
            return placeName+" =" + distanceStr;
        }
    }

    @SerializedName("meta")
    private MetaData metaData;

    @SerializedName("documents")
    private ArrayList<Location> locations;

    public MetaData getMetaData() {
        return metaData;
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

}