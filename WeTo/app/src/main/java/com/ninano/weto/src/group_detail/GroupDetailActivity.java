package com.ninano.weto.src.group_detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.ninano.weto.R;
import com.ninano.weto.db.AppDatabase;
import com.ninano.weto.db.ToDoWithData;
import com.ninano.weto.src.BaseActivity;
import com.ninano.weto.src.custom.StartSnapHelper;
import com.ninano.weto.src.group_detail.adpater.GroupMemberListAdapter;
import com.ninano.weto.src.group_detail.models.GroupMemberData;
import com.ninano.weto.src.main.todo_group.adapter.ToDoGroupListAdapter;
import com.ninano.weto.src.main.todo_group.models.Member;
import com.ninano.weto.src.main.todo_group.models.ToDoGroupData;
import com.ninano.weto.src.main.todo_personal.adpater.ToDoPersonalItemTouchHelperCallback;
import com.ninano.weto.src.todo_add.AddGroupMeetActivity;
import com.ninano.weto.src.todo_add.AddGroupToDoActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.ninano.weto.src.ApplicationClass.getApplicationClassContext;

public class GroupDetailActivity extends BaseActivity {

    private Context mContext;

    private RequestManager mRequestManager;

    private RecyclerView mRecyclerViewMember;
    private ArrayList<GroupMemberData> mMemberData = new ArrayList<>();
    private GroupMemberListAdapter mGroupMemberListAdapter;

    private RecyclerView mRecyclerViewList;
    private ToDoGroupListAdapter mToDoGroupListAdapter;
    private ArrayList<ToDoWithData> mGroupListData = new ArrayList<>();
    private ItemTouchHelper mItemTouchHelper;

    private LinearLayout mLinearHiddenDone;
    private RecyclerView mRecyclerViewDone;
    private ToDoGroupListAdapter mToDoGroupDoneListAdapter;
    private ArrayList<ToDoWithData> mGroupListDoneData = new ArrayList<>();
    private ItemTouchHelper mDoneItemTouchHelper;

    private ImageView mImageViewDrag, mImageViewAddAndDragConfirm;
    private boolean isEditMode = true;

    private float density;

    private LinearLayout mLinearExpand;
    private boolean isExpandable;
    private TextView mTextViewExpand;
    private ImageView mImageViewExpand;

    private int mGroupNo = 0;
    private int mGroupIcon = -1;
    private ArrayList<Member> members = new ArrayList<>();

    AppDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        mContext = this;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        density = displayMetrics.density;
        mGroupNo = getIntent().getIntExtra("groupId", 0);
        mGroupIcon = getIntent().getIntExtra("groupIcon", -1);
        members = (ArrayList<Member>)getIntent().getSerializableExtra("members");
        init();
        setGroupMemberData(members);
        setDatabase();
    }

    private void setDatabase() {
        mDatabase = AppDatabase.getAppDatabase(getApplicationClassContext());
        mDatabase.todoDao().getActivatedGroupTodoList(mGroupNo).observe(this, new Observer<List<ToDoWithData>>() {
            @Override
            public void onChanged(List<ToDoWithData> todoList) {
                mGroupListData.clear();
                mGroupListData.addAll(todoList);
                mToDoGroupListAdapter.notifyDataSetChanged();
            }
        });
        mDatabase.todoDao().getDoneGroupTodoList(mGroupNo).observe(this, new Observer<List<ToDoWithData>>() {
            @Override
            public void onChanged(List<ToDoWithData> todoList) {
                mGroupListDoneData.clear();
                mGroupListDoneData.addAll(todoList);
                mToDoGroupDoneListAdapter.notifyDataSetChanged();
                if (isExpandable) {
                    showDoneLayout();
                }
            }
        });
    }

    void init(){
        mRequestManager = Glide.with(mContext);

        mRecyclerViewMember = findViewById(R.id.group_detail_rv_member);
        mGroupMemberListAdapter = new GroupMemberListAdapter(mContext, mMemberData, mRequestManager,density, new GroupMemberListAdapter.ItemClickListener() {
            @Override
            public void itemClick(int pos) {
                if (pos==mMemberData.size()-1){
                    GroupInviteDialog groupInviteDialog = new GroupInviteDialog(mContext, mGroupNo);
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
                            Intent intent = new Intent(GroupDetailActivity.this, AddGroupToDoActivity.class);
                            intent.putExtra("groupId", mGroupNo);
                            intent.putExtra("groupIcon", mGroupIcon);
                            intent.putExtra("members", members);
                            startActivity(intent);
                        }

                        @Override
                        public void meetClick() {
                            Intent intent = new Intent(GroupDetailActivity.this, AddGroupMeetActivity.class);
                            intent.putExtra("groupId", mGroupNo);
                            intent.putExtra("groupIcon", mGroupIcon);
                            intent.putExtra("members", members);
                            startActivity(intent);
                        }
                    });
                    groupToDoAddDialog.show();
                }
            }
        });

        mLinearHiddenDone = findViewById(R.id.group_detail_layout_hidden_done);
        mRecyclerViewDone = findViewById(R.id.group_detail_rv_done);
        mRecyclerViewDone.setLayoutManager(new LinearLayoutManager(mContext) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        mToDoGroupDoneListAdapter = new ToDoGroupListAdapter(mContext, mGroupListDoneData, new ToDoGroupListAdapter.ItemClickListener() {
            @Override
            public void itemClick(int pos) {

            }

            @Override
            public void onStartDrag(ToDoGroupListAdapter.CustomViewHolder holder) {
                mDoneItemTouchHelper.startDrag(holder);
            }
        });

        ToDoPersonalItemTouchHelperCallback mDoneCallBack = new ToDoPersonalItemTouchHelperCallback(mToDoGroupDoneListAdapter, mContext);
        mDoneItemTouchHelper = new ItemTouchHelper(mDoneCallBack);
        mDoneItemTouchHelper.attachToRecyclerView(mRecyclerViewDone);

        mRecyclerViewDone.setAdapter(mToDoGroupDoneListAdapter);

        mTextViewExpand = findViewById(R.id.group_detail_tv_expand);
        mImageViewExpand = findViewById(R.id.group_detail_iv_expand);

        mLinearExpand = findViewById(R.id.group_detail_layout_expandable);
        mLinearExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isExpandable) {
                    hideDoneLayout();
                    mTextViewExpand.setText("완료된 항목");
                    mImageViewExpand.setImageResource(R.drawable.ic_chevron_down_blue);
                    isExpandable = false;
                } else {
                    showDoneLayout();
                    mTextViewExpand.setText("접기");
                    mImageViewExpand.setImageResource(R.drawable.ic_chevron_up);
                    isExpandable = true;
                }
            }
        });
    }

    private void showDoneLayout() {
        ValueAnimator anim1 = ValueAnimator.ofInt(0, (int) (66 * density * mGroupListDoneData.size() + 15 * density));
        anim1.setDuration(500);
        anim1.setRepeatMode(ValueAnimator.REVERSE);
        anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Integer value = (Integer) valueAnimator.getAnimatedValue();
                mLinearHiddenDone.getLayoutParams().height = value.intValue();
                mLinearHiddenDone.requestLayout();
            }
        });
        anim1.start();
    }

    private void hideDoneLayout() {
        ValueAnimator anim1 = ValueAnimator.ofInt((int) (66 * density * mGroupListDoneData.size() + 15 * density), 0);
        anim1.setDuration(500); // duration 5 seconds
        anim1.setRepeatMode(ValueAnimator.REVERSE);
        anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                mLinearHiddenDone.getLayoutParams().height = value.intValue();
                mLinearHiddenDone.requestLayout();
            }
        });
        anim1.start();
    }

    void setGroupMemberData(ArrayList<Member> members){
        mMemberData.clear();

        for(int i=0; i<members.size(); i++){
            mMemberData.add(new GroupMemberData(members.get(i).getUserNo(), members.get(i).getProfileUrl(), members.get(i).getNickName(), false));
        }
        mMemberData.add(new GroupMemberData(-1,"","",true));

        mGroupMemberListAdapter.notifyDataSetChanged();
    }
}
