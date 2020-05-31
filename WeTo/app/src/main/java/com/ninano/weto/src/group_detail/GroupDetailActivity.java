package com.ninano.weto.src.group_detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.ninano.weto.R;
import com.ninano.weto.src.BaseActivity;
import com.ninano.weto.src.custom.StartSnapHelper;
import com.ninano.weto.src.group_detail.adpater.GroupMemberListAdapter;
import com.ninano.weto.src.group_detail.models.GroupMemberData;
import com.ninano.weto.src.main.todo_group.adapter.ToDoGroupListAdapter;
import com.ninano.weto.src.main.todo_group.models.ToDoGroupData;
import com.ninano.weto.src.main.todo_personal.adpater.ToDoPersonalItemTouchHelperCallback;

import java.util.ArrayList;

public class GroupDetailActivity extends BaseActivity {

    private Context mContext;

    private RequestManager mRequestManager;

    private RecyclerView mRecyclerViewMember;
    private ArrayList<GroupMemberData> mMemberData = new ArrayList<>();
    private GroupMemberListAdapter mGroupMemberListAdapter;

    private RecyclerView mRecyclerViewList;
    private ToDoGroupListAdapter mToDoGroupListAdapter;
    private ArrayList<ToDoGroupData> mGroupListData = new ArrayList<>();
    private ItemTouchHelper mItemTouchHelper;

    private ImageView mImageViewDrag, mImageViewAddAndDragConfirm;
    private boolean isEditMode = true;

    private float density;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        mContext = this;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        density = displayMetrics.density;
        init();
        setGroupMemberTempData();
    }

    void init(){
        mRequestManager = Glide.with(mContext);

        mRecyclerViewMember = findViewById(R.id.group_detail_rv_member);
        mGroupMemberListAdapter = new GroupMemberListAdapter(mContext, mMemberData, mRequestManager,density, new GroupMemberListAdapter.ItemClickListener() {
            @Override
            public void itemClick(int pos) {
                if (pos==mMemberData.size()-1){
                    GroupInviteDialog groupInviteDialog = new GroupInviteDialog(mContext, new GroupInviteDialog.InviteClickLIstener() {
                        @Override
                        public void inviteClick() {

                        }
                    });
                    groupInviteDialog.show();
                }
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerViewMember.setLayoutManager(linearLayoutManager);
        mRecyclerViewMember.setAdapter(mGroupMemberListAdapter);

        mRecyclerViewList = findViewById(R.id.group_detail_rv_list);
        mRecyclerViewList.setLayoutManager(new LinearLayoutManager(mContext){
            @Override
            public boolean canScrollVertically() {
                return false;
            }

            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        });
        mToDoGroupListAdapter = new ToDoGroupListAdapter(mContext, mGroupListData, new ToDoGroupListAdapter.ItemClickListener() {
            @Override
            public void itemClick(int pos) {

            }

            @Override
            public void onStartDrag(ToDoGroupListAdapter.CustomViewHolder holder) {
                mItemTouchHelper.startDrag(holder);
            }
        });
        ToDoPersonalItemTouchHelperCallback mCallBack = new ToDoPersonalItemTouchHelperCallback(mToDoGroupListAdapter, mContext);
        mItemTouchHelper = new ItemTouchHelper(mCallBack);
        mItemTouchHelper.attachToRecyclerView(mRecyclerViewList);

        mRecyclerViewList.setAdapter(mToDoGroupListAdapter);

        mImageViewDrag = findViewById(R.id.group_detail_iv_drag);
        mImageViewDrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEditMode){
                    for(int i=0; i<mGroupListData.size(); i++){
                        mGroupListData.get(i).setEditMode(false);
                    }
                    isEditMode = false;
                    mImageViewDrag.setVisibility(View.GONE);
                    mImageViewAddAndDragConfirm.setVisibility(View.VISIBLE);
                    mImageViewAddAndDragConfirm.setBackgroundResource(R.drawable.bg_round_float_button_gray);
                    mImageViewAddAndDragConfirm.setImageResource(R.drawable.ic_float_check);
                    mToDoGroupListAdapter.notifyDataSetChanged();
                }
            }
        });

        mImageViewAddAndDragConfirm = findViewById(R.id.group_detail_iv_add_and_drag_confirm);
        mImageViewAddAndDragConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // drag 여부 분기 나눠야함
                if (!isEditMode){
                    for(int i=0; i<mGroupListData.size(); i++){
                        mGroupListData.get(i).setEditMode(true);
                    }
                    isEditMode = true;
                    mImageViewDrag.setVisibility(View.VISIBLE);
                    mImageViewAddAndDragConfirm.setVisibility(View.GONE);
                    mImageViewAddAndDragConfirm.setBackgroundResource(R.drawable.bg_round_float_button_blue);
                    mImageViewAddAndDragConfirm.setImageResource(R.drawable.ic_float_plus);
                    mToDoGroupListAdapter.notifyDataSetChanged();
                }
                else {
                    GroupToDoAddDialog groupToDoAddDialog = new GroupToDoAddDialog(mContext, new GroupToDoAddDialog.GroupToDoAddListener() {
                        @Override
                        public void todoClick() {

                        }

                        @Override
                        public void meetClick() {

                        }
                    });
                    groupToDoAddDialog.show();
                }
            }
        });
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
