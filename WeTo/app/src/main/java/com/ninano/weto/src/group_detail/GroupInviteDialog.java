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

import com.ninano.weto.R;

public class GroupInviteDialog extends Dialog {

    private Context mContext;
    private ImageView mImageViewBack;
    private Button mButtonInvite;
    private InviteClickLIstener mInviteClickLIstener;

    public GroupInviteDialog(@NonNull Context context, InviteClickLIstener inviteClickLIstener) {
        super(context);
        mContext = context;
        mInviteClickLIstener = inviteClickLIstener;
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
                mInviteClickLIstener.inviteClick();
                dismiss();
            }
        });
    }

    public interface InviteClickLIstener{
        void inviteClick();
    }
}
