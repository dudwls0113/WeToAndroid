package com.ninano.weto.src.group_detail;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;
import com.ninano.weto.R;

import java.util.HashMap;
import java.util.Map;

import static com.ninano.weto.src.ApplicationClass.sSharedPreferences;

public class GroupInviteDialog extends Dialog {

    private Context mContext;
    private ImageView mImageViewBack;
    private Button mButtonInvite;
    private int mGroupNo=0;


    public GroupInviteDialog(@NonNull Context context, int groupNo) {
        super(context);
        mContext = context;
        mGroupNo = groupNo;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_group_invite);
        mImageViewBack = findViewById(R.id.dialog_group_invite_iv_back);
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mButtonInvite = findViewById(R.id.dialog_group_invite_btn_kakao);
        mButtonInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    setKakaoLink();
                } catch (Exception e){
                    ((GroupDetailActivity) mContext).showCustomToast("카카오톡 앱이 설치 되어있지 않습니다.");
                }
                dismiss();
            }
        });
    }

    void setKakaoLink(){
        String nickName = sSharedPreferences.getString("kakaoNickName", "");
        String profileUrl = sSharedPreferences.getString("profileUrl", "");
        FeedTemplate params = FeedTemplate.newBuilder(ContentObject.newBuilder(nickName+"님이 초대하였습니다.",
                "https://i.imgur.com/VhZc3i7.png",
                LinkObject.newBuilder().setWebUrl("")
                        .setMobileWebUrl("").build())
                .setDescrption(nickName+"님이 초대하였습니다.").build())
                .addButton(new ButtonObject("초대 수락하기", LinkObject.newBuilder()
                        .setWebUrl("")
                        .setMobileWebUrl("")
                        .setAndroidExecutionParams("key1="+mGroupNo+", "+nickName+"/ "+profileUrl)
                        .build()))
                .build();

        Map<String, String> serverCallbackArgs = new HashMap<String, String>();
        serverCallbackArgs.put("user_id", "${current_user_id}");
        serverCallbackArgs.put("product_id", "${shared_product_id}");

        KakaoLinkService.getInstance().sendDefault(mContext, params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {

            }
        });

    }
}
