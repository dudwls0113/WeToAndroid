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

    @Query("SELECT * FROM ToDo INNER JOIN ToDoData WHERE Todo.todoNo =  ToDoData.todoNo ORDER BY Todo.ordered")
    public abstract List<ToDoWithData> getTodoListNoLive();

    @Query("SELECT * FROM ToDo INNER JOIN ToDoData WHERE Todo.todoNo =  ToDoData.todoNo and Todo.todoNo = :todoNo")
    public abstract List<ToDoWithData> getTodoWithTodoNo(int todoNo);

    @Query("SELECT * FROM ToDo INNER JOIN ToDoData WHERE Todo.todoNo =  ToDoData.todoNo and ToDoData.isWiFi = :isWifi")
    public abstract List<ToDoWithData> getTodoWithWifi(char isWifi);

    @Query("SELECT count(*) FROM ToDo INNER JOIN ToDoData WHERE Todo.todoNo =  ToDoData.todoNo and ToDoData.isWiFi = :isWifi")
    public abstract int getTodoWithWifiCount(char isWifi);

    @Query("SELECT * FROM ToDo INNER JOIN ToDoData WHERE Todo.todoNo =  ToDoData.todoNo AND Todo.status = 'ACTIVATE' ORDER BY Todo.ordered")
    public abstract LiveData<List<ToDoWithData>> getActivatedTodoList();

    @Query("SELECT * FROM ToDo INNER JOIN ToDoData WHERE Todo.todoNo =  ToDoData.todoNo AND Todo.status = 'DONE' ORDER BY Todo.ordered")
    public abstract LiveData<List<ToDoWithData>> getDoneTodoList();

    @Transaction
    public int insertTodo(ToDo todo, ToDoData toDoData) {
        int todoNo = (int) insert(todo);
        toDoData.setTodoNo(todoNo);
        insertLocation(toDoData);
        return todoNo;
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
