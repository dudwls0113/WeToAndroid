package com.ninano.weto.src.group_add;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ninano.weto.R;
import com.ninano.weto.src.BaseActivity;
import com.ninano.weto.src.group_add.interfaces.GroupAddView;

public class GroupAddActivity extends BaseActivity implements GroupAddView {

    private Context mContext;
    private EditText mEditTextName;
    private Button mButtonDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_add);
        mContext = this;
        init();
    }

    void init(){
        mEditTextName = findViewById(R.id.group_add_edt_name);
        mButtonDone = findViewById(R.id.group_add_btn_done);
        mButtonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postGroup(1, mEditTextName.getText().toString());
            }
        });
    }

    void postGroup(int icon, String name){
        GroupAddService groupAddService = new GroupAddService(mContext, this);
        groupAddService.postGroup(icon, name);
    }

    @Override
    public void groupAddSuccess() {
        finish();
    }

    @Override
    public void validateFailure(String message) {
        showCustomToast(message!=null ? message : getString(R.string.network_error));
    }
}
