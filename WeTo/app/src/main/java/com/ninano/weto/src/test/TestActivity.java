package com.ninano.weto.src.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.widget.TextView;

import com.ninano.weto.R;
import com.ninano.weto.db.AppDatabase;
import com.ninano.weto.db.ToDoWithData;

import java.util.List;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        AppDatabase mDatabase;
        mDatabase = AppDatabase.getAppDatabase(this);
        //UI 갱신 (라이브 데이터를 이용하여 자동으로)
        mDatabase.todoDao().getTodoList().observe(this, new Observer<List<ToDoWithData>>() {
            @Override
            public void onChanged(List<ToDoWithData> todoList) {
                StringBuilder text = new StringBuilder();
                for(int i=0; i<todoList.size(); i++){
                    text.append(todoList.get(i).toString());
                    text.append("\n");
                }
                ((TextView)findViewById(R.id.activity_test_tv)).setText(text);
            }
        });
    }
}
