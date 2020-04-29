package com.ninano.weto.src.main.todo_personal.models;

public class ToDoPersonalData {

    int iconNum;
    String toDoTitle;
    String subTitle;
    int toDoType;
    int highlight;
    boolean isEditMode;

    public ToDoPersonalData(int iconNum, String toDoTitle, String subTitle, int toDoType, int highlight) {
        this.iconNum = iconNum;
        this.toDoTitle = toDoTitle;
        this.subTitle = subTitle;
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
