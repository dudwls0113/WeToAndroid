package com.ninano.weto.src.group_detail.models;

public class GroupMemberData {

    String imgUrl;
    String name;
    boolean isLast;

    public GroupMemberData(String imgUrl, String name, boolean isLast) {
        this.imgUrl = imgUrl;
        this.name = name;
        this.isLast = isLast;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getName() {
        return name;
    }

    public boolean isLast() {
        return isLast;
    }
}
