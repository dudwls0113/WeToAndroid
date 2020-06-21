package com.ninano.weto.src.group_detail.models;

public class GroupMemberData {

    int userNo;
    String imgUrl;
    String name;
    boolean isLast;

    public GroupMemberData(int userNo, String imgUrl, String name, boolean isLast) {
        this.userNo = userNo;
        this.imgUrl = imgUrl;
        this.name = name;
        this.isLast = isLast;
    }

    public int getUserNo() {
        return userNo;
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
