package com.ninano.weto.src.main.map;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.CircleOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.widget.ZoomControlView;
import com.ninano.weto.R;
import com.ninano.weto.db.AppDatabase;
import com.ninano.weto.db.ToDoWithData;
import com.ninano.weto.src.BaseFragment;
import com.ninano.weto.src.main.map.adapters.MapLocationTodoAdapter;
import com.ninano.weto.src.todo_detail.ToDoDetailActivity;

import java.util.ArrayList;
import java.util.List;

import static com.ninano.weto.src.ApplicationClass.getApplicationClassContext;

public class MapFragment extends BaseFragment implements OnMapReadyCallback {

    Context mContext;
    ZoomControlView zoomControlView;
    private MapView mapView;
    NaverMap naverMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    ArrayList<Marker> markers = new ArrayList<>();
    CircleOverlay mCircleOverlay = new CircleOverlay();
    float density;

    private ViewPager mViewPager;
    private ArrayList<ToDoWithData> toDoWithDataArrayList = new ArrayList<>();
    private MapLocationTodoAdapter mMapLocationTodoAdapter;
    private AppDatabase mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_map, container, false);
        mContext = getContext();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        density = displayMetrics.density;
        setComponentView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        mViewPager = view.findViewById(R.id.fragment_map_vp);
        mViewPager.setClipToPadding(false);
        mMapLocationTodoAdapter = new MapLocationTodoAdapter(getChildFragmentManager(), toDoWithDataArrayList, new MapLocationTodoAdapter.MapLocationTodoAdapterClickListener() {
            @Override
            public void nextArrowClick() {
                int currentPosition = mViewPager.getCurrentItem();
                if (currentPosition < toDoWithDataArrayList.size() - 1) {
                    mViewPager.setCurrentItem(currentPosition + 1);
                }
            }

            @Override
            public void itemClick(int position) {
                Intent intent = new Intent(mContext, ToDoDetailActivity.class);
                intent.putExtra("todoData", toDoWithDataArrayList.get(position));
                startActivity(intent);
            }
        });
        mViewPager.setAdapter(mMapLocationTodoAdapter);
        //       뷰페이저 미리보기 설정//
        mViewPager.setClipToPadding(false);
        int dpValue = 30;
        float d = getResources().getDisplayMetrics().density;
        int margin = (int) (dpValue * d);
        mViewPager.setPadding(margin, 0, margin, 0);
        mViewPager.setPageMargin(margin / 2);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                CameraUpdate cameraUpdate = CameraUpdate.zoomTo(12);
                naverMap.moveCamera(cameraUpdate);
                CameraUpdate cameraUpdate2 = CameraUpdate.scrollTo(new LatLng(toDoWithDataArrayList.get(position).getLatitude(), toDoWithDataArrayList.get(position).getLongitude()))
                        .animate(CameraAnimation.Fly);
                naverMap.moveCamera(cameraUpdate2);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setDatabase() {
        mDatabase = AppDatabase.getAppDatabase(getApplicationClassContext());
        mDatabase.todoDao().getActivatedLocationTodoList().observe(this, new Observer<List<ToDoWithData>>() {
            @Override
            public void onChanged(List<ToDoWithData> todoList) {
                toDoWithDataArrayList.clear();
                toDoWithDataArrayList.addAll(todoList);
                mMapLocationTodoAdapter.notifyDataSetChanged();
                addTodoMarker(todoList);
            }
        });
    }

    public void addTodoMarker(List<ToDoWithData> todoList) {
//        Marker marker
        for (int i = 0; i < markers.size(); i++) {
            markers.get(i).setMap(null);
        }
        markers.clear();
        for (int i = 0; i < todoList.size(); i++) {
            Marker marker = new Marker();
            marker.setPosition(new LatLng(todoList.get(i).getLatitude(), todoList.get(i).getLongitude()));
            switch (todoList.get(i).getIcon()) {
                case 1:
                    marker.setIcon(OverlayImage.fromResource(R.drawable.pin_1));
                    break;
                case 2:
                    marker.setIcon(OverlayImage.fromResource(R.drawable.pin_2));
                    break;
                case 3:
                    marker.setIcon(OverlayImage.fromResource(R.drawable.pin_3));
                    break;
                case 4:
                    marker.setIcon(OverlayImage.fromResource(R.drawable.pin_4));
                    break;
                case 5:
                    marker.setIcon(OverlayImage.fromResource(R.drawable.pin_5));
                    break;
                case 6:
                    marker.setIcon(OverlayImage.fromResource(R.drawable.pin_6));
                    break;
                case 7:
                    marker.setIcon(OverlayImage.fromResource(R.drawable.pin_7));
                    break;
                case 8:
                    marker.setIcon(OverlayImage.fromResource(R.drawable.pin_8));
                    break;
                case 9:
                    marker.setIcon(OverlayImage.fromResource(R.drawable.pin_9));
                    break;
                case 10:
                    marker.setIcon(OverlayImage.fromResource(R.drawable.pin_10));
                    break;
                case 11:
                    marker.setIcon(OverlayImage.fromResource(R.drawable.pin_11));
                    break;
                case 12:
                    marker.setIcon(OverlayImage.fromResource(R.drawable.pin_12));
                    break;
                case 13:
                    marker.setIcon(OverlayImage.fromResource(R.drawable.pin_13));
                    break;
                default:
                    marker.setIcon(OverlayImage.fromResource(R.drawable.pin_1));
                    break;
            }
            marker.setWidth(140);
            marker.setHeight(140);
            marker.setMap(naverMap);
            markers.add(marker);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        } else {
//            showCustomToast("알 수 없는 오류가 발생했습니다. 재접속을 부탁드립니다;");
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void setComponentView(View v) {
        zoomControlView = v.findViewById(R.id.zoom);
        locationSource =
                new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            return;
        } else {
            CameraPosition cameraPosition = new CameraPosition(new LatLng(37.5535582, 126.9670034), 8);
            naverMap.setCameraPosition(cameraPosition);
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap2) {
        naverMap = naverMap2;
        naverMap.setNightModeEnabled(true);
        naverMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {
            @Override
            public void onLocationChange(@NonNull Location location) {
                mCircleOverlay.setMap(null);
                mCircleOverlay.setCenter(new LatLng(location.getLatitude(), location.getLongitude()));
                mCircleOverlay.setRadius(60);
                mCircleOverlay.setColor(getResources().getColor(R.color.colorMapGpsTransBlue));
                mCircleOverlay.setMap(naverMap);
            }
        });
        naverMap.setLocationSource(locationSource);
        zoomControlView.setMap(naverMap);
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);
        uiSettings.setZoomControlEnabled(false);
        uiSettings.setScaleBarEnabled(false);
        uiSettings.setLogoGravity(Gravity.TOP);
        uiSettings.setLogoMargin((int) (12 * density), (int) (12 * density), 0, 0);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
        setDatabase();

//        naverMap.setLightness(0.3f);

    }
}
