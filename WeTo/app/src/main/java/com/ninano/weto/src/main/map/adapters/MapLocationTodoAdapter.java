package com.ninano.weto.src.main.map.adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.ninano.weto.db.ToDoWithData;
import com.ninano.weto.src.main.map.LocationTodoBannerFragment;

import java.util.ArrayList;
import java.util.List;

public class MapLocationTodoAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private ArrayList<ToDoWithData> toDoWithDataArrayList;
    private List<Fragment> mFragments = new ArrayList<>();

    private MapLocationTodoAdapterClickListener mapLocationTodoAdapterClickListener;

    public interface MapLocationTodoAdapterClickListener {
        void nextArrowClick();

        void itemClick(int position);
    }

    public MapLocationTodoAdapter(FragmentManager fm, ArrayList<ToDoWithData> arrayList, MapLocationTodoAdapterClickListener mapLocationTodoAdapterClickListener) {
        super(fm);
        this.toDoWithDataArrayList = arrayList;
        this.mapLocationTodoAdapterClickListener = mapLocationTodoAdapterClickListener;
    }

    @Override
    public int getCount() {
        return toDoWithDataArrayList.size();
    }

    @Override
    public Fragment getItem(int i) {
        if (toDoWithDataArrayList != null && toDoWithDataArrayList.size() > 0) {
            i = i % toDoWithDataArrayList.size();
            LocationTodoBannerFragment locationTodoBannerFragment = (LocationTodoBannerFragment) LocationTodoBannerFragment.newInstance(toDoWithDataArrayList.get(i));
            final int finalI = i;
            locationTodoBannerFragment.setClickInterface(new LocationTodoBannerFragment.LocationTodoBannerFragmentClickListener() {
                @Override
                public void nextArrowClick() {
                    mapLocationTodoAdapterClickListener.nextArrowClick();
                }

                @Override
                public void itemClick() {
                    mapLocationTodoAdapterClickListener.itemClick(finalI);
                }

            });
            return locationTodoBannerFragment;
        } else {
            return LocationTodoBannerFragment.newInstance(null);
        }
    }
}
