package com.ninano.weto.src.main.map;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ninano.weto.R;
import com.ninano.weto.db.ToDoWithData;
import com.ninano.weto.src.BaseFragment;

import static com.ninano.weto.src.ApplicationClass.ALL_DAY;
import static com.ninano.weto.src.ApplicationClass.ALWAYS;
import static com.ninano.weto.src.ApplicationClass.AT_ARRIVE;
import static com.ninano.weto.src.ApplicationClass.AT_NEAR;
import static com.ninano.weto.src.ApplicationClass.AT_START;
import static com.ninano.weto.src.ApplicationClass.EVENING;
import static com.ninano.weto.src.ApplicationClass.LOCATION;
import static com.ninano.weto.src.ApplicationClass.MONTH_DAY;
import static com.ninano.weto.src.ApplicationClass.MORNING;
import static com.ninano.weto.src.ApplicationClass.NIGHT;
import static com.ninano.weto.src.ApplicationClass.ONE_DAY;
import static com.ninano.weto.src.ApplicationClass.TIME;
import static com.ninano.weto.src.ApplicationClass.WEEK_DAY;

public class LocationTodoBannerFragment extends BaseFragment {

    private LinearLayout mItem;
    private ImageView mImageViewIcon, mImageViewNextArrow;
    private TextView mTextViewTitle, mTextViewContent;
    //    private Context mContext;
//    private int todoNo, icon;
//    private String title, content;
    private ToDoWithData toDoWithData;

    private LocationTodoBannerFragmentClickListener mLocationTodoBannerFragmentClickListener;

    public interface LocationTodoBannerFragmentClickListener {
        void nextArrowClick();

        void itemClick();
    }

    public static Fragment newInstance(ToDoWithData toDoWithData) {
        LocationTodoBannerFragment locationTodoBannerFragment = new LocationTodoBannerFragment();
        Bundle args = new Bundle();
//        args.putInt("todoNo", toDoWithData.getTodoNo());
//        args.putInt("icon", toDoWithData.getIcon());
//        args.putString("title", toDoWithData.getTitle());
//        args.putString("content", toDoWithData.getContent());
        args.putSerializable("toDoWithData", toDoWithData);
        locationTodoBannerFragment.setArguments(args);
        return locationTodoBannerFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            toDoWithData = (ToDoWithData) getArguments().getSerializable("toDoWithData");
        }
    }

    public void setClickInterface(LocationTodoBannerFragmentClickListener mLocationTodoBannerFragmentClickListener) {
        this.mLocationTodoBannerFragmentClickListener = mLocationTodoBannerFragmentClickListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_todo_banner, container, false);
        setComponentView(view);
        return view;
    }

    @Override
    public void setComponentView(View v) {
        mImageViewIcon = v.findViewById(R.id.list_todo_personal_iv_icon);
        mImageViewNextArrow = v.findViewById(R.id.list_todo_personal_iv_arrow);
        mTextViewTitle = v.findViewById(R.id.list_todo_personal_tv_title);
        mTextViewContent = v.findViewById(R.id.list_todo_personal_tv_subtitle);
        mItem = v.findViewById(R.id.list_todo_item);

        mImageViewNextArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLocationTodoBannerFragmentClickListener.nextArrowClick();
            }
        });
        mItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLocationTodoBannerFragmentClickListener.itemClick();
            }
        });
        mTextViewTitle.setText(toDoWithData.getTitle());
        StringBuilder condition = new StringBuilder();
        switch (toDoWithData.getType()) {
            case TIME:
                switch (toDoWithData.getRepeatType()) {
                    case ALL_DAY:
                        condition.append("매일, ").append(toDoWithData.getTime());
                        break;
                    case WEEK_DAY:
                        condition.append("매 주 ");
                        String[] splitArr = toDoWithData.getRepeatDayOfWeek().split(",");
                        for (String s : splitArr) {
                            condition.append(s).append("요일 ");
                        }
                        condition.append(", " + toDoWithData.getTime());
                        break;
                    case MONTH_DAY:
                        condition.append("매 월 ").append(toDoWithData.getRepeatDay()).append("일, ").append(toDoWithData.getTime());
                        break;
                    case ONE_DAY:
                        condition.append(toDoWithData.getDate()).append(", ").append(toDoWithData.getTime());
                        break;
                }

                break;
            case LOCATION:
                condition.append(toDoWithData.getLocationName());
                switch (toDoWithData.getLocationMode()) {
                    case AT_START:
                        condition.append("에서 출발 할 때");
                        break;
                    case AT_ARRIVE:
                        condition.append("에 도착 할 때");
                        break;
                    case AT_NEAR:
                        condition.append(" 지나갈 때");
                        break;
                }
                switch (toDoWithData.getTimeSlot()) {
                    case ALWAYS:
                        condition.append(", 언제든");
                        break;
                    case MORNING:
                        condition.append(", 아침(06시 ~ 12시)");
                        break;
                    case EVENING:
                        condition.append(", 오후(12시 ~ 21시)");
                        break;
                    case NIGHT:
                        condition.append(", 밤(21시 ~ 06시)");
                        break;
                }
                break;
        }
        mTextViewContent.setText(condition);
    }
}
