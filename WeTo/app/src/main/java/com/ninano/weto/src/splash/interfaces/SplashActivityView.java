package com.ninano.weto.src.splash.interfaces;

import com.ninano.weto.src.splash.models.ServerTodo;
import com.ninano.weto.src.splash.models.ServerTodoResponse;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public interface SplashActivityView {

    void successGetTodo(ServerTodoResponse.TodoArrayResponse response) throws InterruptedException, ExecutionException;

    void failGetTodo();
}
