package com.ninano.weto.src.main.map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.CircleOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.widget.ZoomControlView;
import com.ninano.weto.R;
import com.ninano.weto.src.BaseFragment;

import java.util.ArrayList;

public class MapFragment extends BaseFragment implements OnMapReadyCallback {

    Context mContext;
    ZoomControlView zoomControlView;
    private MapView mapView;
    NaverMap naverMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    ArrayList<PathOverlay> pathOverlays = new ArrayList<>();
    CircleOverlay mCircleOverlay = new CircleOverlay();
    float density;

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
//NaverMap 객체

        naverMap = naverMap2;
        Log.d("map", "onMapReady");
        naverMap.setNightModeEnabled(true);

        naverMap.addOnOptionChangeListener(new NaverMap.OnOptionChangeListener() {
            @Override
            public void onOptionChange() {
                Log.d("map", "onOptionChange");
            }
        });

        naverMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {
            @Override
            public void onLocationChange(@NonNull Location location) {
                mCircleOverlay.setMap(null);
                mCircleOverlay.setCenter(new LatLng(location.getLatitude(), location.getLongitude()));
                mCircleOverlay.setRadius(200);
                mCircleOverlay.setColor(getResources().getColor(R.color.colorMapGpsTransBlue));
                mCircleOverlay.setMap(naverMap);
            }
        });
        naverMap.setOnMapLongClickListener(new NaverMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
//                showCustomToast(pointF + "");
            }
        });

        naverMap.setLocationSource(locationSource);

        GpsTracker gpsTracker = new GpsTracker(mContext, new GpsTracker.GpsTrackerListener() {
            @Override
            public void onLocationChanged(Location location) {

            }
        });

        System.out.println("gps: " + gpsTracker.getLatitude() + ", " + gpsTracker.getLongitude());

        CameraPosition cameraPosition = new CameraPosition(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()), 14);
        naverMap.setCameraPosition(cameraPosition);


        zoomControlView.setMap(naverMap);

        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);
        uiSettings.setZoomControlEnabled(false);
        uiSettings.setScaleBarEnabled(false);
        uiSettings.setLogoGravity(Gravity.TOP);
        uiSettings.setLogoMargin((int)(12*density), (int)(12*density), 0, 0);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        naverMap.setLightness(0.3f);

    }
}
