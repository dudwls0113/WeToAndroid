package com.ninano.weto.src.todo_add;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.ninano.weto.R;
import com.ninano.weto.db.AppDatabase;
import com.ninano.weto.db.FavoriteLocation;
import com.ninano.weto.db.ToDo;
import com.ninano.weto.db.ToDoData;
import com.ninano.weto.src.BaseActivity;
import com.ninano.weto.src.main.todo_group.models.Member;
import com.ninano.weto.src.map_select.MapSelectActivity;
import com.ninano.weto.src.map_select.keyword_search.models.LocationResponse;
import com.ninano.weto.src.todo_add.adpater.AddGroupToDoMemberAdapter;
import com.ninano.weto.src.todo_add.adpater.FavoritePlaceAdapter;
import com.ninano.weto.src.todo_add.interfaces.AddGroupToDoView;
import com.ninano.weto.src.todo_add.models.AddGroupToDoMemberData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.ninano.weto.src.ApplicationClass.ALL_DAY;
import static com.ninano.weto.src.ApplicationClass.COMPANY_SELECT_MODE;
import static com.ninano.weto.src.ApplicationClass.DAY_FORMAT;
import static com.ninano.weto.src.ApplicationClass.HOME_SELECT_MODE;
import static com.ninano.weto.src.ApplicationClass.LOCATION;
import static com.ninano.weto.src.ApplicationClass.MEET;
import static com.ninano.weto.src.ApplicationClass.MINUTE_FORMAT;
import static com.ninano.weto.src.ApplicationClass.MONTH_DAY;
import static com.ninano.weto.src.ApplicationClass.MONTH_FORMAT;
import static com.ninano.weto.src.ApplicationClass.NO_DATA;
import static com.ninano.weto.src.ApplicationClass.ONE_DAY;
import static com.ninano.weto.src.ApplicationClass.SCHOOL_SELECT_MODE;
import static com.ninano.weto.src.ApplicationClass.TIME;
import static com.ninano.weto.src.ApplicationClass.TIME_FORMAT;
import static com.ninano.weto.src.ApplicationClass.WEEK_DAY;
import static com.ninano.weto.src.ApplicationClass.YEAR_FORMAT;
import static com.ninano.weto.src.ApplicationClass.sSharedPreferences;
import static com.ninano.weto.src.main.MainActivity.FINISH_INTERVAL_TIME;

public class AddGroupMeetActivity extends BaseActivity implements AddGroupToDoView {

    private Context mContext;
    AppDatabase mDatabase;
    //공통
    private EditText mEditTextTitle, mEditTextMemo;
    private int mIcon = -1; // 아이콘 값은 그룹 생성시의 아이콘으로 가져온다.
    private char mImportantMode = 'N';
    private int mToDoNo = -1;
    private int mTodoCategory;
    private Switch mImportantSwitch;

    private TextView mTextViewDate, mTextViewTime, mTextViewLocation;

    private boolean mIsLocationSelected;
    private char mWifiMode = 'N';
    private String mWifiBssid = "";
    private boolean mWifiConnected;
    private Double longitude = 0.0, latitude = 0.0;

    private boolean mIsDatePick, mIsTimePick;
    private int mYear = 0, mMonth = 0, mDay = 0, mHour = 0, mMinute = 0;

    private int mFavoriteSelectedIndex = -1;

    private RecyclerView mRecyclerViewMyPlace;
    private FavoritePlaceAdapter mFavoritePlaceAdapter;
    private ArrayList<FavoriteLocation> mFavoritePlaceList = new ArrayList<>();
    private LocationResponse.Location mLocation = null;

    private RecyclerView mRecyclerViewMember;
    private AddGroupToDoMemberAdapter mAddGroupToDoMemberAdapter;
    private ArrayList<AddGroupToDoMemberData> mData = new ArrayList<>();

    //친구 선택 밸리데이션
    ArrayList<AddGroupToDoMemberData> friendList = new ArrayList<>();

    //intent로 받아온 멤버
    private int mGroupNo = 0;
    private int mGroupIcon = -1;
    private ArrayList<Member> members = new ArrayList<>();

    private int mServerTodoNo = 0;

    private float density;

    //중복클릭 방지
    private long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_add_group_meet);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        density = displayMetrics.density;
        mGroupNo = getIntent().getIntExtra("groupId", 0);
        mGroupIcon = getIntent().getIntExtra("groupIcon", -1);
        System.out.println("그룹 정보: " + mGroupNo + ", " + mGroupIcon);
        members = (ArrayList<Member>)getIntent().getSerializableExtra("members");
        init();
        setDatabase();
        setGroupMemberData(members);
    }

    private void setDatabase() {
        mDatabase = AppDatabase.getAppDatabase(this);

        mDatabase.todoDao().getFavoriteLocation().observe(this, new Observer<List<FavoriteLocation>>() {
            @Override
            public void onChanged(List<FavoriteLocation> favoriteLocations) {
                mFavoritePlaceList.clear();
                mFavoritePlaceList.addAll(favoriteLocations);
                if (mFavoritePlaceList.size() == 0) {
                    new FavoritePlaceInsertAsyncTask().execute(new FavoriteLocation("집"));
                    new FavoritePlaceInsertAsyncTask().execute(new FavoriteLocation("학교"));
                    new FavoritePlaceInsertAsyncTask().execute(new FavoriteLocation("회사"));
                }
                if (mFavoriteSelectedIndex != -1) {
                    setFavoritePlaceItem(mFavoriteSelectedIndex);
                }
                mFavoritePlaceList.add(new FavoriteLocation());
                mFavoritePlaceAdapter.notifyDataSetChanged();
            }
        });
    }

    private void postMeet(int groupNo, String title, String content, int icon, int type, ArrayList<AddGroupToDoMemberData> friendList, char isImportant,
                     int meetRemindTime, String locationName, double latitude, double longitude, String date, String time, int year, int month, int day, int hour, int minute){
        AddGroupToDoService addGroupToDoService = new AddGroupToDoService(this);
        addGroupToDoService.postMeet(groupNo, title, content, icon, type, friendList, isImportant, meetRemindTime, locationName, latitude, longitude, date, time, year, month, day, hour, minute);
    }

    @Override
    public void postToDoSuccess(int groupNo, int serverTodoNo) {
        mGroupNo = groupNo;
        mServerTodoNo = serverTodoNo;
    }

    private ToDo makeGroupTodoObject(String title, String content, int icon, int type, char importantMode, int groupNo) {
        ToDo todo = new ToDo(title, content, icon, type, importantMode, 'Y');
        todo.setGroupNo(groupNo);
        return todo;
    }


    @Override
    public void validateFailure(String message) {
        showCustomToast(message!=null ? message : getString(R.string.network_error));
    }

    private class FavoritePlaceInsertAsyncTask extends AsyncTask<Object, Void, Void> {

        FavoritePlaceInsertAsyncTask() {
        }

        @Override
        protected Void doInBackground(Object... objects) {
            FavoriteLocation favoriteLocation = ((FavoriteLocation) objects[0]);
            mDatabase.todoDao().insert(favoriteLocation);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mFavoriteSelectedIndex = mFavoritePlaceList.size() - 1;
        }
    }

    private class FavoritePlaceUpdateAsyncTask extends AsyncTask<Object, Void, Integer> {

        FavoritePlaceUpdateAsyncTask() {

        }

        @Override
        protected Integer doInBackground(Object... objects) {
            FavoriteLocation favoriteLocation = ((FavoriteLocation) objects[0]);
            double latitude = (double) objects[1];
            double longitude = (double) objects[2];
            int type = (int) objects[3];
            if (objects.length > 4) { //와이파이 일 경우
                favoriteLocation.setPlaceConfirm(latitude, longitude);
                favoriteLocation.setWiFi(true);
                favoriteLocation.setSsid((String) objects[4]);
                favoriteLocation.setWifiName((String) objects[5]);
                mDatabase.todoDao().update(favoriteLocation);
            } else {//위치일경우
                favoriteLocation.setPlaceConfirm(latitude, longitude);
                mDatabase.todoDao().update(favoriteLocation);
            }
            return type;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer == HOME_SELECT_MODE) {
                mFavoriteSelectedIndex = 0;
            } else if (integer == SCHOOL_SELECT_MODE) {
                mFavoriteSelectedIndex = 1;
            } else if (integer == COMPANY_SELECT_MODE) {
                mFavoriteSelectedIndex = 2;
            }
        }
    }

    boolean validateBeforeAdd() {
        if (mEditTextTitle.getText().length() < 1) {
            showSnackBar(mEditTextTitle, "내용을 입력해 주세요");
            return false;
        }
        if (!mIsDatePick || !mIsTimePick) {
            showSnackBar(mEditTextTitle, "시간을 선택해 주세요");
            return false;
        }
        if (!mIsLocationSelected) {
            showSnackBar(mEditTextTitle, "장소를 선택해 주세요");
            return false;
        }
        if (mEditTextMemo.getText().length()<1) {
            showSnackBar(mEditTextTitle, "일정 정보를 입력해 주세요");
            return false;
        }
        friendList = setFriendList(mData);
        if (friendList.size()==0){
            showSnackBar(mEditTextTitle, "해당 멤버를 입력해 주세요");
            return false;
        }
        return true;
    }

    private ArrayList<AddGroupToDoMemberData> setFriendList(ArrayList<AddGroupToDoMemberData> data){
        ArrayList<AddGroupToDoMemberData> temp = new ArrayList<>();
        for(int i=0; i<data.size(); i++){
            if(data.get(i).isSelected()){
                temp.add(data.get(i));
            }
        }

        return temp;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (resultCode == 100) {
                //성공적으로 location  받음
                mLocation = (LocationResponse.Location) Objects.requireNonNull(data.getSerializableExtra("location"));
                longitude = Double.parseDouble(mLocation.getLongitude());
                latitude = Double.parseDouble(mLocation.getLatitude());
                setFavoritePlaceItem(NO_DATA);
                setLocationInfo();
            } else if (resultCode == 111) {
                mWifiBssid = data.getStringExtra("bssid");
                longitude = data.getDoubleExtra("longitude", 0);
                latitude = data.getDoubleExtra("latitude", 0);
                mWifiConnected = data.getBooleanExtra("connected", false);
                setWifiInfo(data.getStringExtra("ssid"));
            }
        } else if (requestCode == 200) {//즐겨찾기 추가(기타)
            if (resultCode == 100) {
                mLocation = (LocationResponse.Location) Objects.requireNonNull(data.getSerializableExtra("location"));
                new FavoritePlaceInsertAsyncTask().execute(new FavoriteLocation(mLocation.getAddressName(), Double.parseDouble(mLocation.getLatitude()), Double.parseDouble(mLocation.getLongitude())));
            } else if (resultCode == 333) {
                mWifiBssid = data.getStringExtra("bssid");
                longitude = data.getDoubleExtra("longitude", 0);
                latitude = data.getDoubleExtra("latitude", 0);
                String wifiName = data.getStringExtra("ssid");
                String favoriteName = data.getStringExtra("favoriteName");
                new FavoritePlaceInsertAsyncTask().execute(new FavoriteLocation(favoriteName, longitude, latitude), wifiName, mWifiBssid);
            }
        } else if (requestCode == HOME_SELECT_MODE) {
            if (resultCode == 100) {
                mLocation = (LocationResponse.Location) Objects.requireNonNull(data.getSerializableExtra("location"));
                new FavoritePlaceUpdateAsyncTask().execute(mFavoritePlaceList.get(0), Double.parseDouble(mLocation.getLatitude()), Double.parseDouble(mLocation.getLongitude()), HOME_SELECT_MODE);
            } else if (resultCode == 333) {
                mWifiBssid = data.getStringExtra("bssid");
                longitude = data.getDoubleExtra("longitude", 0);
                latitude = data.getDoubleExtra("latitude", 0);
                String wifiName = data.getStringExtra("ssid");
                new FavoritePlaceUpdateAsyncTask().execute(mFavoritePlaceList.get(0), latitude, longitude, HOME_SELECT_MODE, mWifiBssid, wifiName);
            }
        } else if (requestCode == SCHOOL_SELECT_MODE) {
            if (resultCode == 100) {
                mLocation = (LocationResponse.Location) Objects.requireNonNull(data.getSerializableExtra("location"));
                new FavoritePlaceUpdateAsyncTask().execute(mFavoritePlaceList.get(1), Double.parseDouble(mLocation.getLatitude()), Double.parseDouble(mLocation.getLongitude()), SCHOOL_SELECT_MODE);
            } else if (resultCode == 333) {
                mWifiBssid = data.getStringExtra("bssid");
                longitude = data.getDoubleExtra("longitude", 0);
                latitude = data.getDoubleExtra("latitude", 0);
                String wifiName = data.getStringExtra("ssid");
                new FavoritePlaceUpdateAsyncTask().execute(mFavoritePlaceList.get(1), latitude, longitude, SCHOOL_SELECT_MODE, mWifiBssid, wifiName);
            }
        } else if (requestCode == COMPANY_SELECT_MODE) {
            if (resultCode == 100) {
                mLocation = (LocationResponse.Location) Objects.requireNonNull(data.getSerializableExtra("location"));
                new FavoritePlaceUpdateAsyncTask().execute(mFavoritePlaceList.get(2), Double.parseDouble(mLocation.getLatitude()), Double.parseDouble(mLocation.getLongitude()), COMPANY_SELECT_MODE);
            } else if (resultCode == 333) {
                mWifiBssid = data.getStringExtra("bssid");
                longitude = data.getDoubleExtra("longitude", 0);
                latitude = data.getDoubleExtra("latitude", 0);
                String wifiName = data.getStringExtra("ssid");
                new FavoritePlaceUpdateAsyncTask().execute(mFavoritePlaceList.get(2), latitude, longitude, COMPANY_SELECT_MODE, mWifiBssid, wifiName);
            }
        }
    }

    void setGroupMemberData(ArrayList<Member> members){
        mData.clear();
        for(int i=0; i<members.size(); i++){
            if (!members.get(i).getNickName().equals(sSharedPreferences.getString("kakaoNickName", ""))){
                mData.add(new AddGroupToDoMemberData(members.get(i).getUserNo(), members.get(i).getNickName(), false));
            }
        }
        mAddGroupToDoMemberAdapter.notifyDataSetChanged();
    }

    private void init(){
        mImportantSwitch = findViewById(R.id.add_group_todo_switch_improtant);
        mImportantSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mImportantMode = 'Y';
                } else {
                    mImportantMode = 'N';
                }
            }
        });

        mTextViewDate = findViewById(R.id.add_group_meet_tv_date);
        mTextViewTime = findViewById(R.id.add_group_meet_tv_time);

        mTextViewLocation = findViewById(R.id.add_group_meet_tv_location);

        mEditTextTitle = findViewById(R.id.add_group_meet_et_title);
        mEditTextMemo = findViewById(R.id.add_group_meet_et_memo);

        mRecyclerViewMember = findViewById(R.id.add_group_meet_rv_member);
        mAddGroupToDoMemberAdapter = new AddGroupToDoMemberAdapter(mContext, mData, density, new AddGroupToDoMemberAdapter.ItemClickListener() {
            @Override
            public void itemClick(int pos) {
                if(mData.get(pos).isSelected()){
                    mData.get(pos).setSelected(false);
                } else {
                    mData.get(pos).setSelected(true);
                }

                mAddGroupToDoMemberAdapter.notifyDataSetChanged();
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerViewMember.setLayoutManager(linearLayoutManager);
        mRecyclerViewMember.setAdapter(mAddGroupToDoMemberAdapter);

        mRecyclerViewMyPlace = findViewById(R.id.add_group_meet_rv_like);
        LinearLayoutManager myPlaceLinearLayoutManager = new LinearLayoutManager(this);
        myPlaceLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerViewMyPlace.setLayoutManager(myPlaceLinearLayoutManager);
        mFavoritePlaceAdapter = new FavoritePlaceAdapter(mContext, mFavoritePlaceList, new FavoritePlaceAdapter.ItemClickListener() {
            @Override
            public void itemClick(int pos) {
                if (pos != mFavoritePlaceList.size() - 1) {
                    if (mFavoritePlaceList.get(pos).isConfirmed()) {
                        //집,회사,학교 / 기타 가 이미 입력되있음
                        setFavoritePlaceItem(pos);
                    } else {
                        //집,회사,학교 입력안받음
                        Intent intent = new Intent(mContext, MapSelectActivity.class);
                        intent.putExtra("isFavoritePlaceMode", true);
                        if (mFavoritePlaceList.get(pos).getName().equals("집")) {
                            intent.putExtra("homeMode", true);
                            startActivityForResult(intent, HOME_SELECT_MODE);
                        } else if (mFavoritePlaceList.get(pos).getName().equals("회사")) {
                            intent.putExtra("companyMode", true);
                            startActivityForResult(intent, COMPANY_SELECT_MODE);
                        } else if (mFavoritePlaceList.get(pos).getName().equals("학교")) {
                            intent.putExtra("schoolMode", true);
                            startActivityForResult(intent, SCHOOL_SELECT_MODE);
                        }
                    }
                } else {
                    // 즐겨찾기 장소 추가화면
                    Intent intent = new Intent(mContext, MapSelectActivity.class);
                    intent.putExtra("isFavoritePlaceMode", true);
                    startActivityForResult(intent, 200);
                }
                mFavoritePlaceAdapter.notifyDataSetChanged();
            }
        });


        mRecyclerViewMyPlace.setAdapter(mFavoritePlaceAdapter);
    }

    private void setFavoritePlaceItem(int index) {
        if (index == NO_DATA) {
            return;
        }
        if (!mFavoritePlaceList.get(index).isConfirmed()) {
            return;
        }
        for (int i = 0; i < mFavoritePlaceList.size(); i++) {
            mFavoritePlaceList.get(i).setSelected(false);
        }
        mFavoritePlaceList.get(index).setSelected(true);
        mLocation = new LocationResponse.Location("", mFavoritePlaceList.get(index).getName(), String.valueOf(mFavoritePlaceList.get(index).getLongitude()), String.valueOf(mFavoritePlaceList.get(index).getLatitude()));
        longitude = Double.valueOf(mLocation.getLongitude());
        latitude = Double.valueOf(mLocation.getLatitude());
        setLocationInfo();

        //즐겨찾는 장소가 와이파이일 경우 처리해야함
        if (mFavoritePlaceList.get(index).isWiFi()) {
            mWifiMode = 'Y';
            mWifiBssid = mFavoritePlaceList.get(index).getSsid();
            setWifiInfo(mFavoritePlaceList.get(index).getWifiName());
        } else {
            mWifiMode = 'N';
        }
    }

    void setLocationInfo() {
        mIsLocationSelected = true;
        mTextViewLocation.setTextColor(getResources().getColor(R.color.colorBlack));
        mTextViewLocation.setText(mLocation.getPlaceName());
    }

    void setWifiInfo(String ssid) {
        mIsLocationSelected = true;
        mWifiMode = 'Y';
        mTextViewLocation.setTextColor(getResources().getColor(R.color.colorBlack));
        mTextViewLocation.setText(ssid);
    }

    void setDate() {
        Calendar today = Calendar.getInstance();

        TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                mYear = Integer.parseInt(YEAR_FORMAT.format(date));
                mMonth = Integer.parseInt(MONTH_FORMAT.format(date));
                mDay = Integer.parseInt(DAY_FORMAT.format(date));
                calendar.set(mYear, mMonth-1, mDay);
//                if(calendar.before(Calendar.getInstance())){
//                    showCustomToast("과거의 날짜는 선택할 수 없습니다.");
//                } else {
//                    mTextViewDate.setText(mYear + "년 " + mMonth + "월 " + mDay + "일");
//                    mTextViewDate.setTextColor(getResources().getColor(R.color.colorBlack));
//                    mIsDatePick = true;
//                }
                mTextViewDate.setText(mYear + "년 " + mMonth + "월 " + mDay + "일");
                mTextViewDate.setTextColor(getResources().getColor(R.color.colorBlack));
                mIsDatePick = true;
            }
        })
                .setType(new boolean[]{true, true, true, false, false, false})// 默认全部显示
                .setCancelText("취소")//取消按钮文字
                .setSubmitText("확인")//确认按钮文字
                .setTitleSize(15)//标题文字大小
                .setContentTextSize(20)
                .setSubCalSize(15)
                .setTitleText("")//标题文字
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setTitleColor(getResources().getColor(R.color.colorBlack))//标题文字颜色
                .setSubmitColor(getResources().getColor(R.color.colorBlack))//确定按钮文字颜色
                .setCancelColor(getResources().getColor(R.color.colorBlack))//取消按钮文字颜色
                .setTitleBgColor(getResources().getColor(R.color.colorWhite))//标题背景颜色 Night mode
                .setBgColor(getResources().getColor(R.color.colorWhite))//滚轮背景颜色 Night mode
                .setTextColorOut(getResources().getColor(R.color.colorBlackGray))
                .setTextColorCenter(getResources().getColor(R.color.colorBlack))
                .setLabel("년", "월", "일", "시", "분", "秒")//默认设置为年月日时分秒
                .setDate(today)
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)//是否显示为对话框样式
                .build();
        pvTime.show();
    }

    void setTime() {
        TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                mHour = Integer.parseInt(TIME_FORMAT.format(date));
                mMinute = Integer.parseInt(MINUTE_FORMAT.format(date));
                mTextViewTime.setText(mHour + "시 " + mMinute + "분");
                mTextViewTime.setTextColor(getResources().getColor(R.color.colorBlack));
                mIsTimePick = true;
            }
        })
                .setType(new boolean[]{false, false, false, true, true, false})// 默认全部显示
                .setCancelText("취소")//取消按钮文字
                .setSubmitText("확인")//确认按钮文字
                .setTitleSize(15)//标题文字大小
                .setContentTextSize(20)
                .setSubCalSize(15)
                .setTitleText("")//标题文字
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setTitleColor(getResources().getColor(R.color.colorBlack))//标题文字颜色
                .setSubmitColor(getResources().getColor(R.color.colorBlack))//确定按钮文字颜色
                .setCancelColor(getResources().getColor(R.color.colorBlack))//取消按钮文字颜色
                .setTitleBgColor(getResources().getColor(R.color.colorWhite))//标题背景颜色 Night mode
                .setBgColor(getResources().getColor(R.color.colorWhite))//滚轮背景颜色 Night mode
                .setTextColorOut(getResources().getColor(R.color.colorBlackGray))
                .setTextColorCenter(getResources().getColor(R.color.colorBlack))
                .setLabel("년", "월", "일", "시", "분", "秒")//默认设置为年月日时分秒
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)//是否显示为对话框样式
                .build();
        pvTime.show();
    }

    public void customOnClick(View v){
        switch (v.getId()){
            case R.id.add_group_meet_iv_back:
                finish();
                break;
            case R.id.add_group_meet_layout_gps:
                // 지도 선 화면
                Intent intent = new Intent(mContext, MapSelectActivity.class);
                startActivityForResult(intent, 100);
                break;
            case R.id.add_group_meet_layout_hidden_time_date:
                // 날짜선택
                setDate();
                break;
            case R.id.add_group_meet_layout_hidden_time_time:
                //시간선택
                setTime();
                break;
            case R.id.add_group_meet_btn_done:
                //추가버튼
                long tempTime = System.currentTimeMillis();
                long intervalTime = tempTime - backPressedTime;
                backPressedTime = tempTime;
                if(intervalTime>FINISH_INTERVAL_TIME){
                    if (validateBeforeAdd()) {
                       postMeet(mGroupNo, mEditTextTitle.getText().toString(), mEditTextMemo.getText().toString(), mGroupIcon, MEET, friendList, mImportantMode,
                               0, mTextViewLocation.getText().toString(), latitude, longitude, mTextViewDate.getText().toString(), mTextViewTime.getText().toString(), mYear, mMonth, mDay, mHour, mMinute);
                    }
                }
                break;
        }
    }
}