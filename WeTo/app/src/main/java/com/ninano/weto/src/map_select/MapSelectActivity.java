package com.ninano.weto.src.map_select;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.CircleOverlay;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.widget.ZoomControlView;
import com.ninano.weto.R;
import com.ninano.weto.src.BaseActivity;
import com.ninano.weto.src.main.map.GpsTracker;
import com.ninano.weto.src.map_select.keyword_search.KeywordMapSearchActivity;
import com.ninano.weto.src.todo_add.AddPersonalToDoActivity;

import java.util.ArrayList;

public class MapSelectActivity extends BaseActivity implements OnMapReadyCallback {

    Context mContext;
    ZoomControlView zoomControlView;
    private MapView mapView;
    NaverMap naverMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    ArrayList<PathOverlay> pathOverlays = new ArrayList<>();
    CircleOverlay mCircleOverlay = new CircleOverlay();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_select);
        mContext = this;
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
    }
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public void customOnClick(View v) {
        switch (v.getId()) {
            case R.id.activity_map_select_tv_title:
                // 검색 화면
                Intent intent =new Intent(mContext, KeywordMapSearchActivity.class);
                startActivity(intent);
                break;
        }
    }


    public void onMapReady(@NonNull NaverMap naverMap2) {
        //NaverMap 객체
        naverMap = naverMap2;
        naverMap.setNightModeEnabled(true);
        naverMap.addOnOptionChangeListener(new NaverMap.OnOptionChangeListener() {
            @Override
            public void onOptionChange() {
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
                showCustomToast(pointF + "");
            }
        });

        naverMap.setLocationSource(locationSource);

        GpsTracker gpsTracker = new GpsTracker(mContext, new GpsTracker.GpsTrackerListener() {
            @Override
            public void onLocationChanged(Location location) {

            }
        });

        CameraPosition cameraPosition = new CameraPosition(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()), 14);
        naverMap.setCameraPosition(cameraPosition);


        zoomControlView.setMap(naverMap);

        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);
        uiSettings.setZoomControlEnabled(false);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        naverMap.setLightness(0.3f);

    }

}
