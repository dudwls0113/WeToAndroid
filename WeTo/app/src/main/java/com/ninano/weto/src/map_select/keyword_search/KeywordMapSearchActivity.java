package com.ninano.weto.src.map_select.keyword_search;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.ninano.weto.R;
import com.ninano.weto.src.BaseActivity;

public class KeywordMapSearchActivity extends BaseActivity {

    EditText mEditTextKeyword;
    ImageButton mImageButtonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keword_map_search);
        mEditTextKeyword = findViewById(R.id.activity_keyword_search_et_keyword);
        mImageButtonBack = findViewById(R.id.activity_keyword_search_btn_back);

    }

    public void customOnClick(View v) {
        switch (v.getId()) {
            case R.id.activity_keyword_map_btn_remove:

                break;
        }
    }

}
