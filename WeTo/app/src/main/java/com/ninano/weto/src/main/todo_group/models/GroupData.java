package com.ninano.weto.src.main.todo_group.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GroupData {

    @SerializedName("no")
    int no;
    @SerializedName("name")
    String name;
    @SerializedName("memberCount")
    int memberCount;
    @SerializedName("member")
    ArrayList<Member> members;

    public GroupData(int no, String name, int memberCount, ArrayList<Member> members) {
        this.no = no;
        this.name = name;
        this.memberCount = memberCount;
        this.members = members;
    }

    public int getNo() {
        return no;
    }

    public String getName() {
        return name;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public ArrayList<Member> getMembers() {
        return members;
    }
}
