package com.ninano.weto.src.main.todo_group.interfaces;

import com.ninano.weto.src.main.todo_group.models.GroupData;

import java.util.ArrayList;

public interface ToDoGroupView {

    void existUser();

    void notExistUser();

    void signUpSuccess();

    void getGroupSuccess(ArrayList<GroupData> arrayList);

    void validateFailure(String message);
}
