package com.ninano.weto.src.common.models;

import java.io.Serializable;

public class ServerTodoWithTodoNo implements Serializable {
    private int severTodoNo;
    private int todoNo;

    public int getSeverTodoNo() {
        return severTodoNo;
    }

    public int getTodoNo() {
        return todoNo;
    }

    public void setSeverTodoNo(int severTodoNo) {
        this.severTodoNo = severTodoNo;
    }

    public void setTodoNo(int todoNo) {
        this.todoNo = todoNo;
    }
}
