package com.ninano.weto.src.tutorial;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.ninano.weto.src.tutorial.models.TutorialData;

import java.util.ArrayList;

public class TutorialViewPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<TutorialData> mTutorialList = new ArrayList<>();

    public TutorialViewPagerAdapter(@NonNull FragmentManager fm, int behavior, ArrayList<TutorialData> tutorialData) {
        super(fm, behavior);
        this.mTutorialList = tutorialData;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (mTutorialList!=null && mTutorialList.size()>0){
            return TutorialFragment.newInstance(mTutorialList.get(position));
        }
        return TutorialFragment.newInstance(null);
    }

    @Override
    public int getCount() {
        return mTutorialList.size();
    }
}
