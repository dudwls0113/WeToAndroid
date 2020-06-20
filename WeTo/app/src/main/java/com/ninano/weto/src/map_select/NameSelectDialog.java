package com.ninano.weto.src.map_select;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ninano.weto.R;

import java.util.Objects;

public class NameSelectDialog extends Dialog {

    private NameSelectDialogClickListener mNameSelectDialogClickListener;
    private EditText mEditTextName;
    private ImageView mImageViewBack;
    private Context mContext;

    public interface NameSelectDialogClickListener {
        void okClicked(String name);
    }

    public NameSelectDialog(final Context context, NameSelectDialogClickListener nameSelectDialogClickListener) {
        super(context);
        mContext = context;
        mNameSelectDialogClickListener = nameSelectDialogClickListener;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_favorite_place_input_name);

        View mView = findViewById(R.id.dialog_favorite_place_input_name);
        GradientDrawable drawable = (GradientDrawable) mContext.getDrawable(R.drawable.bg_round_dialog);
        mView.setBackground(drawable);
        mView.setClipToOutline(true);

        mImageViewBack = findViewById(R.id.dialog_favorite_place_iv_back);
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        TextView mTextViewYes = findViewById(R.id.dialog_favorite_place_input_name_btn_ok);
        mEditTextName = findViewById(R.id.dialog_favorite_place_input_name_et_name);
        mEditTextName.setEnabled(true);

        mTextViewYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mEditTextName.getText().toString().length()<1){
                    ((MapSelectActivity)context).showCustomToast("이름을 입력 해 주세요");
                    return;
                }
                mNameSelectDialogClickListener.okClicked(mEditTextName.getText().toString());
                dismiss();   //다이얼로그를 닫는 메소드입니다.
            }
        });
//        mEditTextName.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                if (mEditTextName.getText().toString().length() > 0) {
//                    mEditTextName.setEnabled(true);
//                }
//            }
//        });
    }

}
