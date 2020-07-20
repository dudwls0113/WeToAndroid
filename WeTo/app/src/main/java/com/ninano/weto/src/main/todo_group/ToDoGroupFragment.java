package com.ninano.weto.src.main.todo_group;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.ReceiverCallNotAllowedException;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.ApiErrorCode;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.User;
import com.kakao.util.exception.KakaoException;
import com.ninano.weto.R;
import com.ninano.weto.db.AppDatabase;
import com.ninano.weto.db.ToDoWithData;
import com.ninano.weto.src.ApplicationClass;
import com.ninano.weto.src.BaseFragment;
import com.ninano.weto.src.custom.StartSnapHelper;
import com.ninano.weto.src.group_add.GroupAddActivity;
import com.ninano.weto.src.group_add.GroupAddService;
import com.ninano.weto.src.group_detail.GroupDetailActivity;
import com.ninano.weto.src.main.MainActivity;
import com.ninano.weto.src.main.todo_group.adapter.GroupListAdapter;
import com.ninano.weto.src.main.todo_group.adapter.ToDoGroupListAdapter;
import com.ninano.weto.src.main.todo_group.interfaces.ToDoGroupView;
import com.ninano.weto.src.main.todo_group.models.GroupData;
import com.ninano.weto.src.main.todo_group.models.Member;
import com.ninano.weto.src.main.todo_group.models.ToDoGroupData;
import com.ninano.weto.src.main.todo_personal.adpater.ToDoPersonalItemTouchHelperCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.ninano.weto.src.ApplicationClass.X_ACCESS_TOKEN;
import static com.ninano.weto.src.ApplicationClass.fcmToken;
import static com.ninano.weto.src.ApplicationClass.getApplicationClassContext;
import static com.ninano.weto.src.ApplicationClass.sSharedPreferences;

public class ToDoGroupFragment extends BaseFragment implements ToDoGroupView {

    private Context mContext;

    private FrameLayout mFrameLayout;
    private ImageView mImageViewSearch, mImageViewXCircle;
    private EditText mEditTextSearch;
    private boolean isSearchMode;

    private TextView mTextViewDate;

    private RecyclerView mRecyclerViewGroup;
    private GroupListAdapter mGroupListAdapter;
    private ArrayList<GroupData> mData = new ArrayList<>();

    private RecyclerView mRecyclerViewList;
    private ToDoGroupListAdapter mToDoGroupListAdapter;
    private ArrayList<ToDoWithData> mGroupListData = new ArrayList<>();
    private ItemTouchHelper mItemTouchHelper;

    private LinearLayout mLinearHiddenDone;
    private RecyclerView mRecyclerViewDone;
    private ToDoGroupListAdapter mToDoGroupDoneListAdapter;
    private ArrayList<ToDoWithData> mGroupListDoneData = new ArrayList<>();
    private ItemTouchHelper mDoneItemTouchHelper;

    private LinearLayout mLinearExpand;
    private boolean isExpandable;
    private TextView mTextViewExpand;
    private ImageView mImageViewExpand;

    private ImageView mImageViewDrag, mImageViewAddAndDragConfirm;
    private boolean isEditMode = true;

    private FrameLayout mLayoutView;
    private RelativeLayout mLayoutLogin;
    private LinearLayout mLayoutButton;
    private Button mButtonLogin;

    private float density;

    private GroupMakeDialog groupMakeDialog;

    //카카오 로그인
    private ISessionCallback mKakaoCallback;
    long kakaoId;
    String nickName, profileUrl;

    AppDatabase mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_to_do_group, container, false);
        isEditMode = true;
        mContext = getContext();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        density = displayMetrics.density;
        setComponentView(v);
        boolean isLogin = sSharedPreferences.getBoolean("kakaoLogin", false);
        System.out.println("isLogin,  " + isLogin);
        if (isLogin){ // 로그인 되어있으면
            mLayoutButton.setVisibility(View.VISIBLE);
            mLayoutLogin.setVisibility(View.GONE);
        } else {
            mLayoutButton.setVisibility(View.GONE);
            mLayoutLogin.setVisibility(View.VISIBLE);
        }
        setDatabase();
        setConfigureKakao();
        return v;
    }

    private void setDatabase() {
        mDatabase = AppDatabase.getAppDatabase(getApplicationClassContext());
        mDatabase.todoDao().getActivatedGroupTodoList().observe(this, new Observer<List<ToDoWithData>>() {
            @Override
            public void onChanged(List<ToDoWithData> todoList) {
                mGroupListData.clear();
                mGroupListData.addAll(todoList);
                mToDoGroupListAdapter.notifyDataSetChanged();
            }
        });
        mDatabase.todoDao().getDoneGroupTodoList().observe(this, new Observer<List<ToDoWithData>>() {
            @Override
            public void onChanged(List<ToDoWithData> todoList) {
                mGroupListDoneData.clear();
                mGroupListDoneData.addAll(todoList);
                mToDoGroupDoneListAdapter.notifyDataSetChanged();
                if (isExpandable) {
                    showDoneLayout();
                }
                if(todoList.size() == 0){
                    mLinearExpand.setVisibility(View.GONE);
                }
                else{
                    mLinearExpand.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void setComponentView(View v) {
        mLayoutView = v.findViewById(R.id.todo_group_fragment_layout);

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
        mGroupListAdapter = new GroupListAdapter(mContext, mData, density, new GroupListAdapter.ItemClickListener() {
            @Override
            public void itemClick(int pos) {
                Intent intent = new Intent(mContext, GroupDetailActivity.class);
                intent.putExtra("groupId", mData.get(pos).getNo());
                intent.putExtra("groupIcon", mData.get(pos).getIcon());
                intent.putExtra("members", mData.get(pos).getMembers());
                startActivity(intent);
            }

            @Override
            public void backItemClick(int pos) {
                groupMakeDialog = new GroupMakeDialog(mContext, new GroupMakeDialog.GroupMakeDialogClickListener() {
                    @Override
                    public void okClicked(String name, int groupIcon) {
                        postGroup(groupIcon, name);
                    }
                });
                groupMakeDialog.show();
            }
        });


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerViewGroup.setLayoutManager(linearLayoutManager);
        mRecyclerViewGroup.setAdapter(mGroupListAdapter);

        mRecyclerViewList = v.findViewById(R.id.todo_group_fragment_rv_list);
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

        mImageViewDrag = v.findViewById(R.id.todo_group_fragment_iv_drag);
        mImageViewAddAndDragConfirm = v.findViewById(R.id.todo_group_fragment_iv_add_and_drag_confirm);

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
        mImageViewAddAndDragConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        mLinearHiddenDone = v.findViewById(R.id.todo_group_fragment_layout_hidden_done);
        mRecyclerViewDone = v.findViewById(R.id.todo_group_fragment_rv_done);
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

        mTextViewExpand = v.findViewById(R.id.todo_group_tv_expand);
        mImageViewExpand = v.findViewById(R.id.todo_group_iv_expand);

        mLinearExpand = v.findViewById(R.id.todo_group_fragment_layout_expandable);
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

        mLayoutLogin = v.findViewById(R.id.todo_group_fragment_layout_login);
        mLayoutButton = v.findViewById(R.id.todo_group_fragment_layout_button);
        mButtonLogin = v.findViewById(R.id.dialog_group_login_btn_login);

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, getActivity());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(sSharedPreferences.getBoolean("kakaoLogin", false)){
            getGroup();
        }
    }

    private void getCurrentTime(){
        Date currentTime = Calendar.getInstance().getTime();
        String cur_date = new SimpleDateFormat("MM월 dd일 (EE)", Locale.getDefault()).format(currentTime);
        mTextViewDate.setText(cur_date);
    }

    private void setConfigureKakao(){
        mKakaoCallback = new ISessionCallback() {
            @Override
            public void onSessionOpened() {
                UserManagement.getInstance().me(new MeV2ResponseCallback() {
                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        int result = errorResult.getErrorCode();
                        if (result== ApiErrorCode.CLIENT_ERROR_CODE){
                            showCustomToast(mContext, getString(R.string.network_error));
                        } else {
                            showCustomToast(mContext, errorResult.getErrorMessage());
                        }
                    }

                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        showCustomToast(mContext, "세션이 종료되었습니다.\n다시 시도해주세요.");
                        SharedPreferences.Editor editor = sSharedPreferences.edit();
                        editor.putBoolean("kakaoLogin", false);
                        editor.apply();
                    }

                    @Override
                    public void onSuccess(MeV2Response result) {
                        kakaoId = result.getId();
                        nickName = result.getKakaoAccount().getProfile().getNickname();
                        profileUrl = result.getKakaoAccount().getProfile().getProfileImageUrl();
                        SharedPreferences.Editor editor = sSharedPreferences.edit();
                        editor.putString(X_ACCESS_TOKEN, String.valueOf(kakaoId));
                        editor.putString("kakaoNickName", nickName);
                        editor.putString("profileUrl", profileUrl);
                        editor.apply();
                        postIsExistUser(kakaoId);
                        System.out.println("성공: " + nickName + ", " + profileUrl + ", " + fcmToken);
                    }
                });
            }

            @Override
            public void onSessionOpenFailed(KakaoException exception) {
                showCustomToast(mContext, "세션이 열지못했습니다.\n다시 시도해주세요.");
                SharedPreferences.Editor editor = sSharedPreferences.edit();
                editor.putBoolean("kakaoLogin", false);
                editor.apply();
            }
        };
        Session.getCurrentSession().addCallback(mKakaoCallback);
        Session.getCurrentSession().checkAndImplicitOpen();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(mKakaoCallback);
    }

    private void postIsExistUser(long kakaoId){
        ToDoGroupService toDoGroupService = new ToDoGroupService(mContext, this);
        toDoGroupService.postIsExistUser(kakaoId);
    }

    private void postSignUp(String fcmToken, long kakaoId, String profileUrl, String nickName){
        System.out.println(fcmToken + ", " + kakaoId + ", " + profileUrl + ", " + nickName);
        ToDoGroupService toDoGroupService = new ToDoGroupService(mContext, this);
        toDoGroupService.postSignUp(fcmToken, kakaoId, profileUrl, nickName);
    }

    private void getGroup(){
        if (sSharedPreferences.getBoolean("kakaoLogin", false)){ // 로그인 되어있으면
            ToDoGroupService toDoGroupService = new ToDoGroupService(mContext, this);
            toDoGroupService.getGroup();
        }
    }

    private void postGroup(int icon, String name){
        ToDoGroupService toDoGroupService = new ToDoGroupService(mContext, this);
        toDoGroupService.postGroup(icon, name);
    }

    @Override
    public void existUser() {
        mLayoutButton.setVisibility(View.VISIBLE);
        mLayoutLogin.setVisibility(View.GONE);
        SharedPreferences.Editor editor = sSharedPreferences.edit();
        editor.putBoolean("kakaoLogin", true);
        editor.apply();
        getGroup();
        if(getActivity()==null){
            return;
        }
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(Objects.requireNonNull(getActivity()),
                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        fcmToken = instanceIdResult.getToken();
                        Log.d("Firebase", "token: " + fcmToken);
                        if (sSharedPreferences.getBoolean("kakaoLogin", false)) { // 로그인 되어있으면 토큰갱신
                            Log.d("로그인", "token: " + fcmToken);
                            ((MainActivity)getActivity()).tryPostFcmToken(fcmToken);
                        }
                    }
                });
    }

    @Override
    public void notExistUser() {
        postSignUp(ApplicationClass.fcmToken, kakaoId, profileUrl, nickName);
    }

    @Override
    public void signUpSuccess() {
        mLayoutButton.setVisibility(View.VISIBLE);
        mLayoutLogin.setVisibility(View.GONE);
        SharedPreferences.Editor editor = sSharedPreferences.edit();
        editor.putBoolean("kakaoLogin", true);
        editor.apply();
        getGroup();
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(getActivity(),
                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        fcmToken = instanceIdResult.getToken();
                        Log.d("Firebase", "token: " + fcmToken);
                        if (sSharedPreferences.getBoolean("kakaoLogin", false)) { // 로그인 되어있으면 토큰갱신
                            Log.d("로그인", "token: " + fcmToken);
                            ((MainActivity)getActivity()).tryPostFcmToken(fcmToken);
                        }
                    }
                });
    }

    @Override
    public void getGroupSuccess(ArrayList<GroupData> arrayList) {
        mData.clear();
        mData.addAll(arrayList);
        mData.add(new GroupData(-1,null,-1,-1, -1, null));
        mGroupListAdapter.notifyDataSetChanged();
    }

    @Override
    public void groupAddSuccess() {
        groupMakeDialog.dismiss();
        getGroup();
    }

    @Override
    public void validateFailure(String message) {
        showCustomToast(mContext, message!=null?message : getString(R.string.network_error));
    }
}
