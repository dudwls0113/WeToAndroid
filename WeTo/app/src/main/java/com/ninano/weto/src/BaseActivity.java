package com.ninano.weto.src;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;

import com.ninano.weto.R;

import static com.ninano.weto.src.ApplicationClass.sSharedPreferences;

public class BaseActivity extends AppCompatActivity {

    public ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getWindow().getDecorView();
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M){
            if (view != null) {
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
        if (sSharedPreferences == null) {
            sSharedPreferences = getApplicationContext().getSharedPreferences(ApplicationClass.TAG, Context.MODE_PRIVATE);
        }
    }


    public void showCustomToast(final String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

//    public void showProgressDialog(Activity activity) {
//
//        if (activity == null || activity.isFinishing()) {
//            return;
//        }
//
//
//        if (progressDialog != null && progressDialog.isShowing()) {
////            progressSET(message);
//        } else {
//
//            progressDialog = new AppCompatDialog(activity);
//            progressDialog.setCancelable(false);
//            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//            progressDialog.setContentView(R.layout.custom_dialog_loading);
//            progressDialog.show();
//
//        }
//
//
//        final ImageView img_loading_frame = progressDialog.findViewById(R.id.iv_frame_loading);
//        Glide.with(this).asGif().load(R.raw.here_there_loading).into(img_loading_frame);
//
//    }
//
//    public void hideProgressDialog() {
//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }
//    }

    @Override
    public void onStop() {
        super.onStop();
//        hideProgressDialog();
    }
}
