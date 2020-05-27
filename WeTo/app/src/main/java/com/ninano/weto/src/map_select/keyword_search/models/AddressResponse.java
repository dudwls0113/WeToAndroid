package com.ninano.weto.src.map_select.keyword_search.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class AddressResponse {

    public class MetaData{
        @SerializedName("total_count")
        private int total_count;

        public int getTotal_count() {
            return total_count;
        }
    }

    public class Address implements Serializable{
        @SerializedName("road_address")
        private RoadAddress roadAddress;

        @SerializedName("address")
        private NormalAddress normalAddress;

        public RoadAddress getRoadAddress() {
            return roadAddress;
        }

        public NormalAddress getNormalAddress() {
            return normalAddress;
        }

        public class RoadAddress{
            public String getAddressName() {
                return addressName;
            }

            public String getRegion_1depth_name() {
                return region_1depth_name;
            }

            public String getRegion_2depth_name() {
                return region_2depth_name;
            }

            public String getRegion_3depth_name() {
                return region_3depth_name;
            }

            public String getRoad_name() {
                return road_name;
            }

            public String getUnderground_yn() {
                return underground_yn;
            }

            public String getMain_building_no() {
                return main_building_no;
            }

            public String getSub_building_no() {
                return sub_building_no;
            }

            public String getBuilding_name() {
                return building_name;
            }

            public String getZone_no() {
                return zone_no;
            }

            @SerializedName("address_name")
            private String addressName;

            @SerializedName("region_1depth_name")
            private String region_1depth_name;

            @SerializedName("region_2depth_name")
            private String region_2depth_name;

            @SerializedName("region_3depth_name")
            private String region_3depth_name;

            @SerializedName("road_name")
            private String road_name;

            @SerializedName("underground_yn")
            private String underground_yn;

            @SerializedName("main_building_no")
            private String main_building_no;

            @SerializedName("sub_building_no")
            private String sub_building_no;

            @SerializedName("building_name")
            private String building_name;

            @SerializedName("zone_no")
            private String zone_no;
        }

        public class NormalAddress{
            public String getAddressName() {
                return addressName;
            }

            public String getRegion_1depth_name() {
                return region_1depth_name;
            }

            public String getRegion_2depth_name() {
                return region_2depth_name;
            }

            public String getRegion_3depth_name() {
                return region_3depth_name;
            }

            public String getMountain_yn() {
                return mountain_yn;
            }

            public String getMain_address_no() {
                return main_address_no;
            }

            public String getSub_address_no() {
                return sub_address_no;
            }

            @SerializedName("address_name")
            private String addressName;

            @SerializedName("region_1depth_name")
            private String region_1depth_name;

            @SerializedName("region_2depth_name")
            private String region_2depth_name;

            @SerializedName("region_3depth_name")
            private String region_3depth_name;

            @SerializedName("mountain_yn")
            private String mountain_yn;

            @SerializedName("main_address_no")
            private String main_address_no;

            @SerializedName("sub_address_no")
            private String sub_address_no;
        }
    }

    @SerializedName("meta")
    private MetaData metaData;

    @SerializedName("documents")
    private ArrayList<Address> locations;

    public MetaData getMetaData() {
        return metaData;
    }

    public ArrayList<Address> getLocations() {
        return locations;
    }
}