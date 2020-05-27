package com.ninano.weto.src.main.todo_personal;

import android.animation.ValueAnimator;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ninano.weto.R;
import com.ninano.weto.src.BaseFragment;
import com.ninano.weto.src.WifiService;
import com.ninano.weto.src.main.todo_personal.adpater.ToDoPersonalItemTouchHelperCallback;
import com.ninano.weto.src.main.todo_personal.adpater.ToDoPersonalListAdapter;
import com.ninano.weto.src.main.todo_personal.models.ToDoPersonalData;
import com.ninano.weto.src.test.TestActivity;
import com.ninano.weto.src.todo_add.AddPersonalToDoActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class ToDoPersonalFragment extends BaseFragment {

    private Context mContext;

    private FrameLayout mFrameLayout;
    private ImageView mImageViewSearch, mImageViewXCircle;
    private EditText mEditTextSearch;
    private boolean isSearchMode;

    private TextView mTextViewDate;

    private RecyclerView mRecyclerView;
    private ToDoPersonalListAdapter mToDoPersonalListAdapter;
    private ArrayList<ToDoPersonalData> mData = new ArrayList<>();
    private ItemTouchHelper mItemTouchHelper;

    private ImageView mImageViewDrag, mImageViewAddAndDragConfirm, mImageViewSetting;
    private boolean isEditMode = true;

    private LinearLayout mLInearHiddenDone;
    private RecyclerView mRecyclerViewDone;
    private ToDoPersonalListAdapter mToDoPersonalDoneListAdapter;
    private ArrayList<ToDoPersonalData> mDoneData = new ArrayList<>();
    private ItemTouchHelper mDoneItemTouchHelper;

    private LinearLayout mLInearExpand;
    private boolean isExpandable;
    private TextView mTextViewExpand;
    private ImageView mImageViewExpand;
    private float density;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_to_do_personal, container, false);
        mContext = getContext();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        density = displayMetrics.density;
        setComponentView(v);
        setToDoTempData();
        setToDoDoneTempData();
        return v;
    }

    @Override
    public void setComponentView(View v) {
        mTextViewDate = v.findViewById(R.id.todo_personal_fragment_tv_date);
//        mTextViewDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                JobScheduler jobScheduler = (JobScheduler)mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
//                if (jobScheduler != null) {
//                    jobScheduler.cancelAll();
//                }
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                    if (jobScheduler != null) {
//                        jobScheduler.schedule(new JobInfo.Builder(0, new ComponentName(mContext, WifiService.class))
//                                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
//                                .setPeriodic(TimeUnit.MINUTES.toMillis(15))
//                                .build());
//                    }
////                    jobScheduler.schedule(new JobInfo.Builder(1,new ComponentName(mContext, WifiService.class))
////                            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_CELLULAR)
////                            .setOverrideDeadline(0)
////                            .build());
//                }
//            }
//        });
        getCurrentTime();

        mFrameLayout = v.findViewById(R.id.todo_personal_fragment_layout_frame);
        mImageViewSearch = v.findViewById(R.id.todo_personal_fragment_iv_search);
        mImageViewXCircle = v.findViewById(R.id.todo_personal_fragment_iv_x_circle);
        mEditTextSearch = v.findViewById(R.id.todo_personal_fragment_edt);
        mImageViewSetting = v.findViewById(R.id.todo_personal_fragment_iv_setting);

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

        mImageViewSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(mContext, TestActivity.class);
                startActivity(intent);
            }
        });

        // editText 검색시 isSearchMode 원래대로
        mRecyclerView = v.findViewById(R.id.todo_personal_fragment_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext){
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mToDoPersonalListAdapter = new ToDoPersonalListAdapter(mContext, mData, new ToDoPersonalListAdapter.ItemClickListener() {
            @Override
            public void itemClick(int pos) {

            }

            @Override
            public void onStartDrag(ToDoPersonalListAdapter.CustomViewHolder holder) {
                mItemTouchHelper.startDrag(holder);
            }
        });

        ToDoPersonalItemTouchHelperCallback mCallBack = new ToDoPersonalItemTouchHelperCallback(mToDoPersonalListAdapter, mContext);
        mItemTouchHelper = new ItemTouchHelper(mCallBack);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.setAdapter(mToDoPersonalListAdapter);

        mImageViewDrag = v.findViewById(R.id.todo_personal_fragment_iv_drag);
        mImageViewAddAndDragConfirm = v.findViewById(R.id.todo_personal_fragment_iv_add_and_drag_confirm);

        mImageViewDrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEditMode){
                    for(int i=0; i<mData.size(); i++){
                        mData.get(i).setEditMode(false);
                    }
                    isEditMode = false;
                    mImageViewDrag.setVisibility(View.GONE);
                    mImageViewAddAndDragConfirm.setBackgroundResource(R.drawable.bg_round_float_button_gray);
                    mImageViewAddAndDragConfirm.setImageResource(R.drawable.ic_float_check);
                    mToDoPersonalListAdapter.notifyDataSetChanged();
                }
            }
        });
        mImageViewAddAndDragConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEditMode){
                    for(int i=0; i<mData.size(); i++){
                        mData.get(i).setEditMode(true);
                    }
                    isEditMode = true;
                    mImageViewDrag.setVisibility(View.VISIBLE);
                    mImageViewAddAndDragConfirm.setBackgroundResource(R.drawable.bg_round_float_button_blue);
                    mImageViewAddAndDragConfirm.setImageResource(R.drawable.ic_float_plus);
                    mToDoPersonalListAdapter.notifyDataSetChanged();
                } else {
                    // 할일 추가 화면
                    Intent intent =new Intent(mContext, AddPersonalToDoActivity.class);
                    startActivity(intent);
                }
            }
        });

        mLInearHiddenDone = v.findViewById(R.id.todo_personal_fragment_layout_hidden_done);
        mRecyclerViewDone = v.findViewById(R.id.todo_personal_fragment_rv_done);

        ToDoPersonalItemTouchHelperCallback mDoneCallBack = new ToDoPersonalItemTouchHelperCallback(mToDoPersonalDoneListAdapter, mContext);
        mDoneItemTouchHelper = new ItemTouchHelper(mDoneCallBack);
        mDoneItemTouchHelper.attachToRecyclerView(mRecyclerViewDone);

        mRecyclerViewDone.setLayoutManager(new LinearLayoutManager(mContext){
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        mToDoPersonalDoneListAdapter = new ToDoPersonalListAdapter(mContext, mDoneData, new ToDoPersonalListAdapter.ItemClickListener() {
            @Override
            public void itemClick(int pos) {

            }

            @Override
            public void onStartDrag(ToDoPersonalListAdapter.CustomViewHolder holder) {
                mDoneItemTouchHelper.startDrag(holder);
            }
        });

        mRecyclerViewDone.setAdapter(mToDoPersonalDoneListAdapter);

        mTextViewExpand = v.findViewById(R.id.todo_personal_tv_expand);
        mImageViewExpand = v.findViewById(R.id.todo_personal_iv_expand);

        mLInearExpand = v.findViewById(R.id.todo_personal_fragment_layout_expandable);
        mLInearExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isExpandable){
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

    private void getCurrentTime(){
        Date currentTime = Calendar.getInstance().getTime();
        String cur_date = new SimpleDateFormat("MM월 dd일 (EE)", Locale.getDefault()).format(currentTime);
        mTextViewDate.setText(cur_date);
    }

    void setToDoTempData(){
        mData.add(new ToDoPersonalData(1, "비타민 챙겨먹기", "집, 매일, 아침 8시", 1, 0));
        mData.add(new ToDoPersonalData(1, "형광펜 사기", "집, 매일, 아침 8시", 1, 0));
        mData.add(new ToDoPersonalData(1, "우유 사기", "집, 매일, 아침 8시", 1, 0));
        mData.add(new ToDoPersonalData(1, "우산 챙기기", "집, 매일, 아침 8시", 1, 0));

        mToDoPersonalListAdapter.notifyDataSetChanged();
    }

    void setToDoDoneTempData(){
        mDoneData.add(new ToDoPersonalData(1, "밥 챙겨먹기", "집, 매일, 아침 8시", 1, 0));
        mDoneData.add(new ToDoPersonalData(1, "볼펜 사기", "집, 매일, 아침 8시", 1, 0));
        mDoneData.add(new ToDoPersonalData(1, "콜라 사기", "집, 매일, 아침 8시", 1, 0));
        mDoneData.add(new ToDoPersonalData(1, "마스크 챙기기", "집, 매일, 아침 8시", 1, 0));

        mToDoPersonalDoneListAdapter.notifyDataSetChanged();

    }

    private void showDoneLayout() {
        ValueAnimator anim1 = ValueAnimator.ofInt(0, (int)(66*density*mDoneData.size() + 15*density));
        anim1.setDuration(500);
        anim1.setRepeatMode(ValueAnimator.REVERSE);
        anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Integer value = (Integer) valueAnimator.getAnimatedValue();
                mLInearHiddenDone.getLayoutParams().height = value.intValue();
                mLInearHiddenDone.requestLayout();
            }
        });
        anim1.start();
    }

    private void hideDoneLayout() {
        ValueAnimator anim1 = ValueAnimator.ofInt((int)(66*density*mDoneData.size()+ 15*density), 0);
        anim1.setDuration(500); // duration 5 seconds
        anim1.setRepeatMode(ValueAnimator.REVERSE);
        anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                mLInearHiddenDone.getLayoutParams().height = value.intValue();
                mLInearHiddenDone.requestLayout();
            }
        });
        anim1.start();
    }
}
