package com.ninano.weto.src.tutorial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.ninano.weto.R;
import com.ninano.weto.src.BaseActivity;
import com.ninano.weto.src.main.MainActivity;
import com.ninano.weto.src.tutorial.models.TutorialData;

import java.util.ArrayList;

import static com.ninano.weto.src.ApplicationClass.sSharedPreferences;

public class TutorialActivity extends BaseActivity {

    private Context mContext;

    private ImageView mImageViewBack;

    private ViewPager mViewPager;
    private ArrayList<TutorialData> mTutorialList = new ArrayList<>();
    private TutorialViewPagerAdapter mTutorialViewPagerAdapter;
    private TabLayout mTabIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        mContext = this;
        init();
    }

    void init(){
        mImageViewBack = findViewById(R.id.tutorial_activity_iv_x);
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sSharedPreferences.edit();
                editor.putBoolean("firstConnect", false);
                editor.apply();
                Intent intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        setTutorialList();
        mViewPager = findViewById(R.id.tutorial_activity_vp);
        mTutorialViewPagerAdapter = new TutorialViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, mTutorialList);
        mViewPager.setAdapter(mTutorialViewPagerAdapter);
        mTabIndicator = findViewById(R.id.tutorial_tab_indicator);
        mTabIndicator.setupWithViewPager(mViewPager);
    }

    void setTutorialList(){
        mTutorialList.add(new TutorialData(R.drawable.img_tutorial1));
        mTutorialList.add(new TutorialData( R.drawable.img_tutorial2));
        mTutorialList.add(new TutorialData(R.drawable.img_tutorial3));
        mTutorialList.add(new TutorialData(R.drawable.img_tutorial4));
    }
}