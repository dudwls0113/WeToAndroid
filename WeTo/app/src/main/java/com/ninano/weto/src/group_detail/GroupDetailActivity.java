package com.ninano.weto.src.group_detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.ninano.weto.R;
import com.ninano.weto.src.BaseActivity;
import com.ninano.weto.src.group_detail.adpater.GroupMemberListAdapter;
import com.ninano.weto.src.group_detail.models.GroupMemberData;

import java.util.ArrayList;

public class GroupDetailActivity extends BaseActivity {

    private Context mContext;

    private RequestManager mRequestManager;

    private RecyclerView mRecyclerViewMember;
    private ArrayList<GroupMemberData> mMemberData = new ArrayList<>();
    private GroupMemberListAdapter mGroupMemberListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        mContext = this;
        init();
        setGroupMemberTempData();
    }

    void init(){
        mRequestManager = Glide.with(mContext);

        mRecyclerViewMember = findViewById(R.id.group_detail_rv_member);
        mGroupMemberListAdapter = new GroupMemberListAdapter(mContext, mMemberData, mRequestManager, new GroupMemberListAdapter.ItemClickListener() {
            @Override
            public void itemClick(int pos) {

            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerViewMember.setLayoutManager(linearLayoutManager);
        mRecyclerViewMember.setAdapter(mGroupMemberListAdapter);
    }

    void setGroupMemberTempData(){
        mMemberData.clear();
        mMemberData.add(new GroupMemberData("", "나", false));
        mMemberData.add(new GroupMemberData("", "문영진", false));
        mMemberData.add(new GroupMemberData("", "모영민", false));
        mMemberData.add(new GroupMemberData("", "신민재", false));
        mMemberData.add(new GroupMemberData("", "이재학", false));
        mMemberData.add(new GroupMemberData("", "조현우", false));

        mMemberData.add(new GroupMemberData("","",true));

        mGroupMemberListAdapter.notifyDataSetChanged();
    }
}
