package com.ninano.weto.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

//Dao = Data Access Object  = db접근 객체
@Dao
public abstract class ToDoDao {
    @Query("SELECT * FROM ToDo")
    abstract LiveData<List<ToDo>> getAll();

    @Query("SELECT * FROM ToDo INNER JOIN ToDoData WHERE Todo.todoNo =  ToDoData.todoNo ORDER BY Todo.ordered")
    public abstract LiveData<List<ToDoWithData>> getTodoList();

    @Transaction
    public void insertTodo(ToDo todo, ToDoData toDoData) {
        int todoNo = (int) insert(todo);
        toDoData.setTodoNo(todoNo);
        insertLocation(toDoData);
    }

    @Insert
    abstract long insert(ToDo todo);

    @Insert
    abstract void insertLocation(ToDoData toDoData);

    @Update
    abstract void update(ToDo todo);

    @Delete
    abstract void delete(ToDo todo);


}
