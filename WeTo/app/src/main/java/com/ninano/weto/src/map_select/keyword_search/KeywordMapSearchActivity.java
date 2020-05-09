package com.ninano.weto.src.map_select.keyword_search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ninano.weto.R;
import com.ninano.weto.src.BaseActivity;
import com.ninano.weto.src.main.todo_personal.adpater.ToDoPersonalListAdapter;
import com.ninano.weto.src.map_select.keyword_search.adapters.LocationListAdapter;
import com.ninano.weto.src.map_select.keyword_search.interfaces.KeywordMapSearchActivityView;
import com.ninano.weto.src.map_select.keyword_search.models.LocationResponse;

import java.io.Serializable;
import java.util.ArrayList;

public class KeywordMapSearchActivity extends BaseActivity implements KeywordMapSearchActivityView {

    private EditText mEditTextKeyword;
    private ImageButton mImageButtonBack;
    private RecyclerView mRecyclerViewResult;
    private LocationListAdapter mLocationListAdapter;
    private ArrayList<LocationResponse.Location> mLocationList = new ArrayList<>();
    double longitude, latitude;
    boolean isGpsOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keword_map_search);
        longitude = getIntent().getDoubleExtra("longitude", 37.565980);
        latitude = getIntent().getDoubleExtra("latitude", 126.978990);

        if (longitude == 37.565980) {
            isGpsOn = false;
        } else {
            isGpsOn = true;
        }

        mEditTextKeyword = findViewById(R.id.activity_keyword_search_et_keyword);
        mImageButtonBack = findViewById(R.id.activity_keyword_search_btn_back);

        mRecyclerViewResult = findViewById(R.id.activity_keyword_map_rv_result);
        mLocationListAdapter = new LocationListAdapter(this, mLocationList, new LocationListAdapter.ItemClickListener() {
            @Override
            public void itemClick(int pos) {
                Intent intent = new Intent();
                intent.putExtra("location", mLocationList.get(pos));
                setResult(100, intent);
                finish();
                overridePendingTransition(0,0);
            }
        });
        mRecyclerViewResult.setAdapter(mLocationListAdapter);
        mEditTextKeyword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    tryGetLocationSearch();

                }
                return false;
            }
        });
        mEditTextKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mEditTextKeyword.isFocusable() && mEditTextKeyword.getText().toString().length() > 1) {
                    tryGetLocationSearch();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void tryGetLocationSearch() {
        if (mEditTextKeyword.getText().length() < 1) {
            showCustomToast("검색어를 입력 해 주세요.");
        }
//        showProgressDialog();
        KeywordMapSearchService keywordMapSearchService = new KeywordMapSearchService(this);
        if (isGpsOn) {
            keywordMapSearchService.getKeywordLocation(mEditTextKeyword.getText().toString(), longitude, latitude, "distance");
        } else {
            keywordMapSearchService.getKeywordLocation(mEditTextKeyword.getText().toString(), longitude, latitude, "accuracy");
        }

    }

    public void customOnClick(View v) {
        switch (v.getId()) {
            case R.id.activity_keyword_map_btn_remove:
                mEditTextKeyword.setText("");
                mLocationListAdapter.notifyItemRangeRemoved(0, mLocationList.size());
                mLocationList.clear();
                break;

            case R.id.activity_keyword_search_btn_back:
                finish();
                break;
        }
    }


    @Override
    public void validateSuccess(ArrayList<LocationResponse.Location> arrayList) {
//        hideProgressDialog();
        mLocationListAdapter.notifyItemRangeRemoved(0, mLocationList.size());
        mLocationList.clear();

        for (int i = 0; i < arrayList.size(); i++) {
            Log.d("확인", arrayList.get(i).getAddressName());
        }

        mLocationList.addAll(arrayList);
        mLocationListAdapter.notifyItemRangeInserted(0, arrayList.size());
    }

    @Override
    public void validateFailure(String message) {
//        hideProgressDialog();
    }
}
