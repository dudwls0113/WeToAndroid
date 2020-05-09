package com.ninano.weto.src.main.todo_group.models;

public class ToDoGroupData {

    int iconNum;
    String toDoTitle;
    String subTitle;
    String name;
    int toDoType;
    int highlight;
    boolean isEditMode;

    public ToDoGroupData(int iconNum, String toDoTitle, String subTitle, String name, int toDoType, int highlight) {
        this.iconNum = iconNum;
        this.toDoTitle = toDoTitle;
        this.subTitle = subTitle;
        this.name = name;
        this.toDoType = toDoType;
        this.highlight = highlight;
        this.isEditMode = true;
    }

    public int getIconNum() {
        return iconNum;
    }

    public String getToDoTitle() {
        return toDoTitle;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getName() {
        return name;
    }

    public int getToDoType() {
        return toDoType;
    }

    public int getHighlight() {
        return highlight;
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
    }
}
