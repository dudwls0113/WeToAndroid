package com.ninano.weto.src.todo_add.models;

public class AddGroupToDoMemberData {

    int userId;
    String name;
    boolean isSelected;

    public AddGroupToDoMemberData(int userId, String name, boolean isSelected) {
        this.userId = userId;
        this.name = name;
        this.isSelected = isSelected;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
