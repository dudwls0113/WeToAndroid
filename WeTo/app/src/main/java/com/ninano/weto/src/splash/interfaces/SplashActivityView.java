package com.ninano.weto.src.splash.interfaces;

import com.ninano.weto.src.splash.models.ServerTodo;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public interface SplashActivityView {

    void successGetTodo(ArrayList<ServerTodo> serverTodos) throws InterruptedException, ExecutionException;

    void failGetTodo();
}
