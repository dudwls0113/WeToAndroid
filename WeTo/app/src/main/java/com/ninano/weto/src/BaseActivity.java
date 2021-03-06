package com.ninano.weto.src;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;

import com.google.android.material.snackbar.Snackbar;
import com.ninano.weto.R;

import static com.ninano.weto.src.ApplicationClass.sSharedPreferences;

public class BaseActivity extends AppCompatActivity {

    public ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

    public void showSnackBar(View view, String message) {
        Snackbar snackbar;
        snackbar = Snackbar.make(view, Html.fromHtml("<font color=\"#ffffff\">"+message+"</font>"), Snackbar.LENGTH_SHORT);
        View snackView = snackbar.getView();
        snackView.setBackgroundColor(getResources().getColor(R.color.colorMarker));
        snackbar.show();
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

    private float firstPointX = 0;
    private float firstPointY = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            float distX = Math.abs(ev.getX() - firstPointX);
            float distY = Math.abs(ev.getY() - firstPointY);
            if (distX < 8 || distY < 8 || distX > 40 || distY > 40) {
                View view = getCurrentFocus();
                if (view instanceof EditText) {
                    view.clearFocus();
                    Rect outRect = new Rect();
                    view.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            } else {
                View view = getCurrentFocus();
                if (view instanceof EditText) {
                    view.clearFocus();
                }
            }
        } else if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            firstPointX = ev.getX();
            firstPointY = ev.getY();
        } if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            float distX = Math.abs(ev.getX() - firstPointX);
            float distY = Math.abs(ev.getY() - firstPointY);
            if (distX > 30 || distY > 30) {
                View view = getCurrentFocus();
                if (view instanceof EditText) {
                    view.clearFocus();
                    Rect outRect = new Rect();
                    view.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        if (inputMethodManager != null) {
                            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    @Override
    public void onStop() {
        super.onStop();
//        hideProgressDialog();
    }
}
