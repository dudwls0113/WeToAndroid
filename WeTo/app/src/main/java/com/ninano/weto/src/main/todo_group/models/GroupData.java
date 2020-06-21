package com.ninano.weto.src.main.todo_group.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GroupData {

    @SerializedName("no")
    int no;
    @SerializedName("name")
    String name;
    @SerializedName("icon")
    int icon;
    @SerializedName("todoCount")
    int todoCount;
    @SerializedName("memberCount")
    int memberCount;
    @SerializedName("member")
    ArrayList<Member> members;

    public GroupData(int no, String name, int icon, int todoCount, int memberCount, ArrayList<Member> members) {
        this.no = no;
        this.name = name;
        this.icon = icon;
        this.todoCount = todoCount;
        this.memberCount = memberCount;
        this.members = members;
    }

    public int getIcon() {
        return icon;
    }

    public int getTodoCount() {
        return todoCount;
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
