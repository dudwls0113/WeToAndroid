package com.ninano.weto.db;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ninano.weto.R;

import java.util.Date;
import java.util.List;


public class RoomDBActivity extends AppCompatActivity {

    EditText mEditTextTitle, mEditTextContent, mEditTextIcon, mEditTextType, mEditTextIsGroup, mEditTextLocationTitle, mEditTextLatitude, mEditTextLongitude;
    TextView mTextView;
    Button mBtn;
    Context mContext;

    //디비생성
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_d_b);

        mEditTextTitle = findViewById(R.id.activity_room_et_title);
        mEditTextContent = findViewById(R.id.activity_room_et_content);
        mEditTextIcon = findViewById(R.id.activity_room_et_icon);
        mEditTextType = findViewById(R.id.activity_room_et_type);
//        mEditTextIsGroup = findViewById(R.id.activity_room_et_is_group);
        mEditTextLocationTitle = findViewById(R.id.activity_room_et_location_title);
        mEditTextLatitude = findViewById(R.id.activity_room_et_latitude);
        mEditTextLongitude = findViewById(R.id.activity_room_et_longitude);

        mTextView = findViewById(R.id.activity_room_tv);
        mBtn = findViewById(R.id.activity_room_btn);

        db = AppDatabase.getAppDatabase(this);

        //UI 갱신 (라이브 데이터를 이용하여 자동으로)
        db.todoDao().getTodoList().observe(this, new Observer<List<ToDoWithData>>() {
            @Override
            public void onChanged(List<ToDoWithData> todoList) {
                mTextView.setText(todoList.toString());
            }
        });

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = mEditTextTitle.getText().toString();
                String content = mEditTextContent.getText().toString();
                int icon = Integer.parseInt(mEditTextIcon.getText().toString());
                char type = mEditTextType.getText().toString().charAt(0);
//                char isGroup = mEditTextIsGroup.getText().toString().charAt(0);

                new InsertAsyncTask(db.todoDao())
                        .execute(new ToDo(title, content, icon, type),
                                new ToDoData(0, "title", 10.3, 10.3, 100, "ssid", 'Y', 1,
                                        1, "1 2", 30, "10:30"));
            }
        });
    }

    //비동기처리                                   //넘겨줄객체, 중간에 처리할 데이터, 결과물(return)
    private static class InsertAsyncTask extends AsyncTask<Object, Void, Void> {
        private ToDoDao mTodoDao;

        InsertAsyncTask(ToDoDao mTodoDao) {
            this.mTodoDao = mTodoDao;
        }

        @Override
        protected Void doInBackground(Object... toDos) {
//            mTodoDao.insert((ToDo) toDos[0]);
            mTodoDao.insertTodo((ToDo) toDos[0], (ToDoData) toDos[1]);
            return null;
        }
    }
}
