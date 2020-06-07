package com.ninano.weto.src.main.map.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ninano.weto.db.ToDoWithData;
import com.ninano.weto.src.main.map.LocationTodoBannerFragment;

import java.util.ArrayList;
import java.util.List;

public class MapLocationTodoAdapter2 extends FragmentStateAdapter {
    private ArrayList<LocationTodoBannerFragment> mLocationTodoBannerFragments = new ArrayList<>();

    public MapLocationTodoAdapter2(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    public void addFragment(LocationTodoBannerFragment fragment) {
        mLocationTodoBannerFragments.add(fragment);
    }

    private void clearFragment() {
        mLocationTodoBannerFragments.clear();
    }

    public void setArrayList(List<LocationTodoBannerFragment> locationTodoBannerFragment){
        clearFragment();
        this.mLocationTodoBannerFragments.addAll(locationTodoBannerFragment);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mLocationTodoBannerFragments.get(position);
    }

    @Override
    public int getItemCount() {
        return mLocationTodoBannerFragments.size();
    }
}