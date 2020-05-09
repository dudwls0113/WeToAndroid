package com.ninano.weto.src.main.todo_group;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.ninano.weto.R;

public class GroupLoginDialog extends Dialog {

    private Context mContext;
    private Button mButtonKakaoLogin;
    private LoginClickLIstener mLoginClickLIstener;

    public GroupLoginDialog(@NonNull Context context, final LoginClickLIstener loginClickLIstener) {
        super(context);
        mContext = context;
        mLoginClickLIstener = loginClickLIstener;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_group_login);
        mButtonKakaoLogin = findViewById(R.id.dialog_group_login_btn_login);
        mButtonKakaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginClickLIstener.loginClick();
                dismiss();
            }
        });
    }

    public interface LoginClickLIstener{
        void loginClick();
    }
}
