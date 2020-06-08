package com.ninano.weto.src.main.map.adapters;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
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

    public void setArrayList(List<ToDoWithData> toDoWithDataArrayList){
        this.toDoWithDataArrayList.clear();
        this.toDoWithDataArrayList.addAll(toDoWithDataArrayList);
        this.notifyDataSetChanged();
    }

//    public void setPagerItems(ArrayList<ToDoWithData> toDoWithDataArrayList) {
//        if (toDoWithDataArrayList != null)
//            for (int i = 0; i < toDoWithDataArrayList.size(); i++) {
//                mFragmentManager.beginTransaction().remove(toDoWithDataArrayList.get(i).getFragment()).commit();
//            }
//        this.toDoWithDataArrayList = toDoWithDataArrayList;
//    }

    @Override
    public int getCount() {
        return toDoWithDataArrayList.size();
    }

    @Override
    public Fragment getItem(int i) {
        Log.d("프래그먼트", i+"번째 " + toDoWithDataArrayList.get(i).toString());
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

    @Override
    public int getItemPosition(@NonNull Object object) {
//        if (mIsUpdating) {
            return POSITION_NONE;
//        }
//        else {
//            return super.getItemPosition(object);
//        }
    }
}
