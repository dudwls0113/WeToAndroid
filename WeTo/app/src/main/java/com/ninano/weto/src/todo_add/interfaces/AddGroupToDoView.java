package com.ninano.weto.src.todo_add.interfaces;

public interface AddGroupToDoView {

    void postToDoSuccess(int groupNo, int serverTodoNo);

    void validateFailure(String message);
}
