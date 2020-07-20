package com.ninano.weto.src.common.util.DBAsyncTask;

import android.os.AsyncTask;

import com.ninano.weto.db.ToDoDao;
import com.ninano.weto.db.ToDoWithData;

import java.util.List;

public class AllGroupTodoAsyncTask extends AsyncTask<Void, Void, List<Integer>> {
    private ToDoDao mTodoDao;

    public AllGroupTodoAsyncTask(ToDoDao mTodoDao) {
        this.mTodoDao = mTodoDao;
    }

    @Override
    protected List<Integer> doInBackground(Void... voids) {
        return mTodoDao.getAllGroupTodo();
    }
}