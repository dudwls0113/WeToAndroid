package com.ninano.weto.src.main.todo_group;

import android.content.Context;
import android.content.Intent;
import android.content.ReceiverCallNotAllowedException;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.util.DisplayMetrics;
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
import com.ninano.weto.src.ApplicationClass;
import com.ninano.weto.src.BaseFragment;
import com.ninano.weto.src.custom.StartSnapHelper;
import com.ninano.weto.src.group_detail.GroupDetailActivity;
import com.ninano.weto.src.main.todo_group.adapter.GroupListAdapter;
import com.ninano.weto.src.main.todo_group.adapter.ToDoGroupListAdapter;
import com.ninano.weto.src.main.todo_group.interfaces.ToDoGroupView;
import com.ninano.weto.src.main.todo_group.models.GroupData;
import com.ninano.weto.src.main.todo_group.models.ToDoGroupData;
import com.ninano.weto.src.main.todo_personal.adpater.ToDoPersonalItemTouchHelperCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
    private ArrayList<ToDoGroupData> mGroupListData = new ArrayList<>();
    private ItemTouchHelper mItemTouchHelper;

    private ImageView mImageViewDrag, mImageViewAddAndDragConfirm;
    private boolean isEditMode = true;

    private FrameLayout mLayoutView;
    private RelativeLayout mLayoutLogin;
    private LinearLayout mLayoutButton;
    private Button mButtonLogin;

    private float density;

    //카카오 로그인
    private ISessionCallback mKakaoCallback;
    long kakaoId;
    String nickName, profileUrl;

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
        if (isLogin){ // 로그인 되어있으면
            mLayoutButton.setVisibility(View.VISIBLE);
            mLayoutLogin.setVisibility(View.GONE);
        } else {
            mLayoutButton.setVisibility(View.GONE);
            mLayoutLogin.setVisibility(View.VISIBLE);
        }
        setGroupTempData();
        setToDoTempData();
        setConfigureKakao();
        return v;
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
                startActivity(intent);
            }
        });

//        PagerSnapHelper snapHelper = new PagerSnapHelper();
//        snapHelper.attachToRecyclerView(mRecyclerViewGroup);
//        SnapHelper snapHelper = new StartSnapHelper();
//        snapHelper.attachToRecyclerView(mRecyclerViewGroup);

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
                    }

                    @Override
                    public void onSuccess(MeV2Response result) {
                        kakaoId = result.getId();
                        nickName = result.getKakaoAccount().getProfile().getNickname();
                        profileUrl = result.getKakaoAccount().getProfile().getProfileImageUrl();
                        postIsExistUser(kakaoId);
                        System.out.println("성공: " + nickName + ", " + profileUrl);
                    }
                });
            }

            @Override
            public void onSessionOpenFailed(KakaoException exception) {
                showCustomToast(mContext, "세션이 열지못했습니다.\n다시 시도해주세요.");
            }
        };
        Session.getCurrentSession().addCallback(mKakaoCallback);
    }

    void setGroupTempData(){
        mData.clear();
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

    void setToDoTempData(){
        mGroupListData.clear();
        mGroupListData.add(new ToDoGroupData(1, "비타민 챙겨먹기", "집, 매일, 아침 8시", "나",1, 0));
        mGroupListData.add(new ToDoGroupData(1, "형광펜 사기", "집, 매일, 아침 8시","문영진", 1, 0));
        mGroupListData.add(new ToDoGroupData(1, "우유 사기", "집, 매일, 아침 8시", "모영민",1, 0));
        mGroupListData.add(new ToDoGroupData(1, "우산 챙기기", "집, 매일, 아침 8시", "나",1, 0));

        mToDoGroupListAdapter.notifyDataSetChanged();
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

    @Override
    public void existUser() {
        mLayoutButton.setVisibility(View.VISIBLE);
        mLayoutLogin.setVisibility(View.GONE);
        SharedPreferences.Editor editor = sSharedPreferences.edit();
        editor.putBoolean("kakaoLogin", true);
        editor.apply();
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
    }

    @Override
    public void validateFailure(String message) {
        showCustomToast(mContext, message!=null?message : getString(R.string.network_error));
    }
}
