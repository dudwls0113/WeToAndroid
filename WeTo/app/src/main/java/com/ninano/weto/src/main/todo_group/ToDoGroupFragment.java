package com.ninano.weto.src.main.todo_group;

import android.content.Context;
import android.content.ReceiverCallNotAllowedException;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ninano.weto.R;
import com.ninano.weto.src.BaseFragment;
import com.ninano.weto.src.main.todo_group.adapter.GroupListAdapter;
import com.ninano.weto.src.main.todo_group.models.GroupData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ToDoGroupFragment extends BaseFragment {

    private Context mContext;

    private FrameLayout mFrameLayout;
    private ImageView mImageViewSearch, mImageViewXCircle;
    private EditText mEditTextSearch;
    private boolean isSearchMode;

    private TextView mTextViewDate;

    private RecyclerView mRecyclerViewGroup;
    private GroupListAdapter mGroupListAdapter;
    private ArrayList<GroupData> mData = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_to_do_group, container, false);
        mContext = getContext();
        setComponentView(v);
        setGroupTempData();
        return v;
    }

    @Override
    public void setComponentView(View v) {
        mTextViewDate = v.findViewById(R.id.todo_group_fragment_tv_date);
        getCurrentTime();

        mFrameLayout = v.findViewById(R.id.todo_group_fragment_layout_frame);
        mImageViewSearch = v.findViewById(R.id.todo_group_fragment_iv_search);
        mImageViewXCircle = v.findViewById(R.id.todo_group_fragment_iv_x_circle);
        mEditTextSearch = v.findViewById(R.id.todo_group_fragment_edt);

        mFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isSearchMode){
                    mFrameLayout.setBackgroundResource(R.drawable.bg_round_edit);
                    mImageViewSearch.setVisibility(View.GONE);
                    mEditTextSearch.setVisibility(View.VISIBLE);
                    mImageViewXCircle.setVisibility(View.VISIBLE);
                    mEditTextSearch.requestFocus();
                    isSearchMode = true;
                }
            }
        });
        // editText 검색시 isSearchMode 원래대로
        mRecyclerViewGroup = v.findViewById(R.id.todo_group_fragment_rv_group);
        mGroupListAdapter = new GroupListAdapter(mContext, mData, new GroupListAdapter.ItemClickListener() {
            @Override
            public void itemClick(int pos) {

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerViewGroup.setLayoutManager(linearLayoutManager);
        mRecyclerViewGroup.setAdapter(mGroupListAdapter);
    }

    private void getCurrentTime(){
        Date currentTime = Calendar.getInstance().getTime();
        String cur_date = new SimpleDateFormat("MM월 dd일 (EE)", Locale.getDefault()).format(currentTime);
        mTextViewDate.setText(cur_date);
    }

    void setGroupTempData(){
        ArrayList<String> temp1 = new ArrayList<>();
        temp1.add("나");
        temp1.add("김하나");
        temp1.add("문영진");
        mData.add(new GroupData(1,"가족", temp1, 2,1,false));
        ArrayList<String> temp2 = new ArrayList<>();
        temp2.add("나");
        temp2.add("문영진");
        temp2.add("모영민");
        mData.add(new GroupData(4,"친구", temp2, 3,0,false));
        ArrayList<String> temp3 = new ArrayList<>();
        temp3.add("나");
        temp3.add("야나");
        temp3.add("섬머");
        mData.add(new GroupData(2,"메이커스", temp3, 1,4,false));
        ArrayList<String> temp4 = new ArrayList<>();
        mData.add(new GroupData(1,"", temp4, 1,1,true));

        mGroupListAdapter.notifyDataSetChanged();
    }
}
