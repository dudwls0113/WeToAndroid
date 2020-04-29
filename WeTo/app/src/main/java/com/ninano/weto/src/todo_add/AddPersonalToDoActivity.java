package com.ninano.weto.src.todo_add;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.ninano.weto.R;
import com.ninano.weto.src.BaseActivity;
import com.ninano.weto.src.todo_add.adpater.LIkeLocationListAdapter;
import com.ninano.weto.src.todo_add.models.LikeLocationData;

import java.util.ArrayList;

public class AddPersonalToDoActivity extends BaseActivity {

    private Context mContext;
    private TextView mTextViewTimeNoRepeat, mTextViewTimeDayRepeat, mTextViewTimeWeekRepeat, mTextViewTimeMonthRepeat;
    private boolean isSelectedNoRepeat, isSelectedDayRepeat, isSelectedWeekRepeat, isSelectedMonthRepeat;

    private Switch mSwitchTime, mSwitchGps;
    private boolean isSwitchTime, isSwitchGps;
    private LinearLayout mLinearHiddenTime, mLinearHiddenGps;

    private RecyclerView mRecyclerViewLike;
    private LIkeLocationListAdapter mLIkeLocationListAdapter;
    private ArrayList<LikeLocationData> mDataArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_personal_to_do);
        mContext = this;
        init();
        setTempLikeLocationData();
    }

    void init(){
        mTextViewTimeNoRepeat = findViewById(R.id.add_personal_todo_tv_no_repeat);
        isSelectedNoRepeat = true;
        mTextViewTimeDayRepeat = findViewById(R.id.add_personal_todo_tv_day_repeat);
        mTextViewTimeWeekRepeat = findViewById(R.id.add_personal_todo_tv_week_repeat);
        mTextViewTimeMonthRepeat = findViewById(R.id.add_personal_todo_tv_month_repeat);

        mLinearHiddenTime = findViewById(R.id.add_personal_todo_layout_hidden_time);
        mLinearHiddenGps = findViewById(R.id.add_personal_todo_layout_hidden_gps);

        mSwitchTime = findViewById(R.id.add_personal_todo_switch_time);
        mSwitchGps = findViewById(R.id.add_personal_todo_switch_gps);

        mRecyclerViewLike = findViewById(R.id.add_personal_todo_rv_like);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerViewLike.setLayoutManager(linearLayoutManager);
        mLIkeLocationListAdapter = new LIkeLocationListAdapter(mContext, mDataArrayList, new LIkeLocationListAdapter.ItemClickListener() {
            @Override
            public void itemClick(int pos) {
                for(int i=0; i<mDataArrayList.size(); i++){
                    mDataArrayList.get(i).setSelected(false);
                }
                if (pos != mDataArrayList.size()-1){
                    mDataArrayList.get(pos).setSelected(true);
                } else {
                    // 즐겨찾기 장소 추가화면
                }
                mLIkeLocationListAdapter.notifyDataSetChanged();
            }
        });
        mRecyclerViewLike.setAdapter(mLIkeLocationListAdapter);
    }

    private void showTimeLayout(){
        ValueAnimator anim1 = ValueAnimator.ofInt(0, 500);
        anim1.setDuration(500);
        anim1.setRepeatMode(ValueAnimator.REVERSE);
        anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Integer value = (Integer) valueAnimator.getAnimatedValue();
                mLinearHiddenTime.getLayoutParams().height = value.intValue();
                mLinearHiddenTime.requestLayout();
            }
        });
        anim1.start();
    }

    private void hideTimeLayout(){
        ValueAnimator anim1 = ValueAnimator.ofInt(500, 0);
        anim1.setDuration(500); // duration 5 seconds
        anim1.setRepeatMode(ValueAnimator.REVERSE);
        anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                mLinearHiddenTime.getLayoutParams().height = value.intValue();
                mLinearHiddenTime.requestLayout();
            }
        });
        anim1.start();
    }

    private void showGpsLayout(){
        ValueAnimator anim1 = ValueAnimator.ofInt(0, 600);
        anim1.setDuration(500);
        anim1.setRepeatMode(ValueAnimator.REVERSE);
        anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Integer value = (Integer) valueAnimator.getAnimatedValue();
                mLinearHiddenGps.getLayoutParams().height = value.intValue();
                mLinearHiddenGps.requestLayout();
            }
        });
        anim1.start();
        System.out.println(mDataArrayList.size());
        mLIkeLocationListAdapter.notifyDataSetChanged();
    }

    private void hideGpsLayout(){
        ValueAnimator anim1 = ValueAnimator.ofInt(600, 0);
        anim1.setDuration(500); // duration 5 seconds
        anim1.setRepeatMode(ValueAnimator.REVERSE);
        anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                mLinearHiddenGps.getLayoutParams().height = value.intValue();
                mLinearHiddenGps.requestLayout();
            }
        });
        anim1.start();
    }

    public void customOnClick(View v){
        switch (v.getId()){
            case R.id.add_personal_todo_tv_no_repeat:
                isSelectedNoRepeat = true;
                mTextViewTimeNoRepeat.setBackgroundResource(R.drawable.bg_round_button_on);
                mTextViewTimeNoRepeat.setTextColor(Color.parseColor("#ffffff"));
                isSelectedDayRepeat = false;
                mTextViewTimeDayRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeDayRepeat.setTextColor(Color.parseColor("#657884"));
                isSelectedWeekRepeat = false;
                mTextViewTimeWeekRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeWeekRepeat.setTextColor(Color.parseColor("#657884"));
                isSelectedMonthRepeat = false;
                mTextViewTimeMonthRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeMonthRepeat.setTextColor(Color.parseColor("#657884"));
                break;
            case R.id.add_personal_todo_tv_day_repeat:
                isSelectedNoRepeat = false;
                mTextViewTimeNoRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeNoRepeat.setTextColor(Color.parseColor("#657884"));
                isSelectedDayRepeat = true;
                mTextViewTimeDayRepeat.setBackgroundResource(R.drawable.bg_round_button_on);
                mTextViewTimeDayRepeat.setTextColor(Color.parseColor("#ffffff"));
                isSelectedWeekRepeat = false;
                mTextViewTimeWeekRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeWeekRepeat.setTextColor(Color.parseColor("#657884"));
                isSelectedMonthRepeat = false;
                mTextViewTimeMonthRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeMonthRepeat.setTextColor(Color.parseColor("#657884"));
                break;
            case R.id.add_personal_todo_tv_week_repeat:
                isSelectedNoRepeat = false;
                mTextViewTimeNoRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeNoRepeat.setTextColor(Color.parseColor("#657884"));
                isSelectedDayRepeat = false;
                mTextViewTimeDayRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeDayRepeat.setTextColor(Color.parseColor("#657884"));
                isSelectedWeekRepeat = true;
                mTextViewTimeWeekRepeat.setBackgroundResource(R.drawable.bg_round_button_on);
                mTextViewTimeWeekRepeat.setTextColor(Color.parseColor("#ffffff"));
                isSelectedMonthRepeat = false;
                mTextViewTimeMonthRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeMonthRepeat.setTextColor(Color.parseColor("#657884"));
                break;
            case R.id.add_personal_todo_tv_month_repeat:
                isSelectedNoRepeat = false;
                mTextViewTimeNoRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeNoRepeat.setTextColor(Color.parseColor("#657884"));
                isSelectedDayRepeat = false;
                mTextViewTimeDayRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeDayRepeat.setTextColor(Color.parseColor("#657884"));
                isSelectedWeekRepeat = false;
                mTextViewTimeWeekRepeat.setBackgroundResource(R.drawable.bg_round_button_off);
                mTextViewTimeWeekRepeat.setTextColor(Color.parseColor("#657884"));
                isSelectedMonthRepeat = true;
                mTextViewTimeMonthRepeat.setBackgroundResource(R.drawable.bg_round_button_on);
                mTextViewTimeMonthRepeat.setTextColor(Color.parseColor("#ffffff"));
                break;
            case R.id.add_personal_todo_switch_time:
                if (isSwitchTime){ // expand 상태
                    hideTimeLayout();
                    isSwitchTime = false;
                } else { // not expand 상태
                    showTimeLayout();
                    isSwitchTime = true;
                    if (isSwitchGps){
                        hideGpsLayout();
                        isSwitchGps = false;
                        mSwitchGps.setChecked(false);
                    }
                }
                break;
            case R.id.add_personal_todo_switch_gps:
                if (isSwitchGps){
                    hideGpsLayout();
                    isSwitchGps = false;
                } else {
                    showGpsLayout();
                    isSwitchGps = true;
                    if (isSwitchTime){
                        hideTimeLayout();
                        isSwitchTime = false;
                        mSwitchTime.setChecked(false);
                    }
                }
                break;
        }
    }

    void setTempLikeLocationData(){
        mDataArrayList.add(new LikeLocationData("집", "청라1동", false, false));
        mDataArrayList.add(new LikeLocationData("회사", "청라1동", false, false));
        mDataArrayList.add(new LikeLocationData("사무실", "청라1동", false, false));
        //마지막 +
        mDataArrayList.add(new LikeLocationData("+", "청라1동", false, true));
        mLIkeLocationListAdapter.notifyDataSetChanged();
    }
}
