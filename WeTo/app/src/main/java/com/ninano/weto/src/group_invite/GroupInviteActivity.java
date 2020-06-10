package com.ninano.weto.src.group_invite;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.ApiErrorCode;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;
import com.ninano.weto.R;
import com.ninano.weto.src.ApplicationClass;
import com.ninano.weto.src.BaseActivity;
import com.ninano.weto.src.group_invite.interfaces.GroupInviteView;
import com.ninano.weto.src.main.todo_group.ToDoGroupService;

import static com.ninano.weto.src.ApplicationClass.X_ACCESS_TOKEN;
import static com.ninano.weto.src.ApplicationClass.fcmToken;
import static com.ninano.weto.src.ApplicationClass.sSharedPreferences;

public class GroupInviteActivity extends BaseActivity implements GroupInviteView {

    private Context mContext;
    private ImageView mImageViewProfile, mImageViewBack;
    private TextView mTextViewTitle, mTextViewContent;
    private Button mButtonPositive, mButtonNegative;

    private int mGroupId;
    private String mNickName, mProfileUrl;

    //카카오 로그인
    private ISessionCallback mKakaoCallback;
    long kakaoId;
    String nickName, profileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_invite);
        mContext = this;
        if(getIntent().getBooleanExtra("kakaoShare",false)){
            mGroupId = getIntent().getIntExtra("groupId", 0);
            mNickName = getIntent().getStringExtra("nickName");
            mProfileUrl = getIntent().getStringExtra("profileUrl");
        }
        init();
        if (!sSharedPreferences.getBoolean("kakaoLogin",false)){
            setLoginMode();
        } else {
            setInviteMode();
        }
        setConfigureKakao();
    }

    void init(){
        mImageViewBack = findViewById(R.id.group_invite_iv_x);
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mImageViewProfile = findViewById(R.id.group_invite_iv_profile);
        RequestOptions sharedOptions =
                new RequestOptions()
                        .placeholder(R.drawable.img_profile_default)
                        .error(R.drawable.img_profile_default)
                        .diskCacheStrategy(DiskCacheStrategy.DATA);
        ShapeDrawable oval = new ShapeDrawable(new OvalShape());
        oval.getPaint().setColor(Color.parseColor("#f5f6fa"));
        oval.getPaint().setAntiAlias(true);
        mImageViewProfile.setBackground(oval);
        Glide.with(mContext).load(mProfileUrl).apply(sharedOptions).into(mImageViewProfile);
        mTextViewTitle = findViewById(R.id.group_invite_tv_title);
        String title = mNickName+"님께서\n그룹에 초대하셨습니다.";
        SpannableStringBuilder ssb = new SpannableStringBuilder(title);
        ssb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorMarker)), 0, mNickName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTextViewTitle.setText(ssb);
        mTextViewContent = findViewById(R.id.group_invite_tv_content);
        mButtonPositive = findViewById(R.id.group_invite_btn_kakao);
        mButtonNegative = findViewById(R.id.group_invite_btn_deny);
    }

    void setLoginMode(){
        mTextViewContent.setText(getString(R.string.group_invite_content_login));
        mButtonPositive.setText(getString(R.string.kakao_login));
        mButtonPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, GroupInviteActivity.this);
            }
        });
    }

    void setInviteMode(){
        mTextViewContent.setText(getString(R.string.group_invite_content_invite));
        mButtonPositive.setText(getString(R.string.group_invite_button_invite));
        mButtonPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getGroupAccept(mGroupId);
            }
        });
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
                            showCustomToast(getString(R.string.network_error));
                        } else {
                            showCustomToast(errorResult.getErrorMessage());
                        }
                    }

                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        showCustomToast("세션이 종료되었습니다.\n다시 시도해주세요.");
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
                showCustomToast("세션이 열지못했습니다.\n다시 시도해주세요.");
                SharedPreferences.Editor editor = sSharedPreferences.edit();
                editor.putBoolean("kakaoLogin", false);
                editor.apply();
            }
        };
        Session.getCurrentSession().addCallback(mKakaoCallback);
        Session.getCurrentSession().checkAndImplicitOpen();
    }

    private void getGroupAccept(int groupNo){
        GroupInviteService groupInviteService = new GroupInviteService(mContext, this);
        groupInviteService.getGroupAccept(groupNo);
    }

    private void postIsExistUser(long kakaoId){
        GroupInviteService groupInviteService = new GroupInviteService(mContext, this);
        groupInviteService.postIsExistUser(kakaoId);
    }

    private void postSignUp(String fcmToken, long kakaoId, String profileUrl, String nickName){
        System.out.println(fcmToken + ", " + kakaoId + ", " + profileUrl + ", " + nickName);
        GroupInviteService groupInviteService = new GroupInviteService(mContext, this);
        groupInviteService.postSignUp(fcmToken, kakaoId, profileUrl, nickName);
    }

    @Override
    public void groupAcceptSuccess() {
        finish();
    }

    @Override
    public void existUser() {
        SharedPreferences.Editor editor = sSharedPreferences.edit();
        editor.putBoolean("kakaoLogin", true);
        editor.apply();
        setInviteMode();
    }

    @Override
    public void notExistUser() {
        postSignUp(ApplicationClass.fcmToken, kakaoId, profileUrl, nickName);
    }

    @Override
    public void signUpSuccess() {
        SharedPreferences.Editor editor = sSharedPreferences.edit();
        editor.putBoolean("kakaoLogin", true);
        editor.apply();
        setInviteMode();
    }

    @Override
    public void validateFailure(String message) {
        showCustomToast(message != null ? message : getString(R.string.network_error));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)){
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
