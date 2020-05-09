package com.ninano.weto.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Todo")
public class ToDo {
    @PrimaryKey(autoGenerate = true)
    private int todoNo;

    private String title;
    private String content;
    private int icon;
    private int type;
    private String status;
    private int ordered;

    public ToDo(String title, String content, int icon, int type) {
        this.title = title;
        this.content = content;
        this.icon = icon;
        this.type = type;
        this.status = "ACTIVATE";
        this.ordered = 0;
    }

    public void setTodoNo(int todoNo) {
        this.todoNo = todoNo;
    }


    public int getTodoNo() {
        return todoNo;
    }

    public void setNo(int no) {
        this.todoNo = no;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getOrder() {
        return ordered;
    }

    public void setOrder(int order) {
        this.ordered = order;
    }

    public int getNo() {
        return todoNo;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getIcon() {
        return icon;
    }

    public int getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }


    public int getOrdered() {
        return ordered;
    }

    public void setOrdered(int ordered) {
        this.ordered = ordered;
    }

    @NonNull
    @Override
    public String toString() {
        return this.title + " " + content + " " + icon + " " + type + " " + status + " " + "\n";
    }
}
