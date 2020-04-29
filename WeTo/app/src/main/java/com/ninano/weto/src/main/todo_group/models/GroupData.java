package com.ninano.weto.src.main.todo_group.models;

import java.util.ArrayList;

public class GroupData {

    int iconType;
    String gruopTitle;
    ArrayList <String> groupMember;
    int toDoCount;
    int meetCount;
    boolean isLast;

    public GroupData(int iconType, String gruopTitle, ArrayList<String> groupMember, int toDoCount, int meetCount, boolean isLast) {
        this.iconType = iconType;
        this.gruopTitle = gruopTitle;
        this.groupMember = groupMember;
        this.toDoCount = toDoCount;
        this.meetCount = meetCount;
        this.isLast = isLast;
    }

    public int getIconType() {
        return iconType;
    }

    public String getGruopTitle() {
        return gruopTitle;
    }

    public ArrayList<String> getGroupMember() {
        return groupMember;
    }

    public int getToDoCount() {
        return toDoCount;
    }

    public int getMeetCount() {
        return meetCount;
    }

    public boolean isLast() {
        return isLast;
    }
}
