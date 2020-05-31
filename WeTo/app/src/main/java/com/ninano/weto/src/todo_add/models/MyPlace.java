package com.ninano.weto.src.todo_add.models;

public class MyPlace {

    String name;
    String detailLocation;
    boolean isSelected;
    boolean isLast;

    public MyPlace(String name, String detailLocation, boolean isSelected, boolean isLast) {
        this.name = name;
        this.detailLocation = detailLocation;
        this.isSelected = isSelected;
        this.isLast = isLast;
    }

    public String getName() {
        return name;
    }

    public String getDetailLocation() {
        return detailLocation;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDetailLocation(String detailLocation) {
        this.detailLocation = detailLocation;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setLast(boolean last) {
        isLast = last;
    }
}
