package com.ninano.weto.src.map_select;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.CircleOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.widget.ZoomControlView;
import com.ninano.weto.R;
import com.ninano.weto.src.BaseActivity;
import com.ninano.weto.src.main.map.GpsTracker;
import com.ninano.weto.src.map_select.keyword_search.KeywordMapSearchActivity;
import com.ninano.weto.src.map_select.keyword_search.models.LocationResponse;
import com.ninano.weto.src.todo_add.AddPersonalToDoActivity;
import com.ninano.weto.src.wifi_search.WifiSearchActivity;

import java.util.ArrayList;
import java.util.Objects;

public class MapSelectActivity extends BaseActivity implements OnMapReadyCallback {

    private Context mContext;
    private ZoomControlView zoomControlView;
    private MapView mapView;
    private NaverMap naverMap;

    private TextView mTextViewTitle, mTextViewLocationTitle, mTextViewLocationAddress;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private ArrayList<PathOverlay> pathOverlays = new ArrayList<>();
    private CircleOverlay mCircleOverlay = new CircleOverlay();
    private Double longitude, latitude;
    private LinearLayout mLayoutWifi, mLayoutLocation;
    private LocationResponse.Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_select);
        mContext = this;
        mapView = findViewById(R.id.map_view);
        zoomControlView = findViewById(R.id.zoom);
        mLayoutWifi = findViewById(R.id.activity_map_select_layout_wifi);
        mLayoutWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapSelectActivity.this, WifiSearchActivity.class);
                startActivity(intent);
            }
        });
        mapView.onCreate(savedInstanceState);
        mTextViewTitle = findViewById(R.id.activity_map_select_tv_title);
        mTextViewLocationTitle = findViewById(R.id.activity_map_select_layout_location_tv_title);
        mTextViewLocationAddress = findViewById(R.id.activity_map_select_layout_location_tv_address);
        mLayoutLocation = findViewById(R.id.activity_map_select_layout_location);

        mapView.getMapAsync(this);
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
                Intent intent = new Intent(mContext, KeywordMapSearchActivity.class);
                intent.putExtra("longitude", longitude);
                intent.putExtra("latitude", latitude);
                startActivityForResult(intent, 100);
                overridePendingTransition(0, 0);
                break;
            case R.id.activity_map_select_btn_back:
                finish();
                break;
            case R.id.activity_map_select_layout_location_btn:
                Intent intent2 = new Intent();
                intent2.putExtra("location", mLocation);
                setResult(100, intent2);
                finish();
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (resultCode == 100) {
                //성공적으로 location  받음
                mLocation = (LocationResponse.Location) Objects.requireNonNull(data.getSerializableExtra("location"));
                getLocationAndSetMap(mLocation);
            } else {

            }
        }
    }

    private void getLocationAndSetMap(LocationResponse.Location location) {
        Log.d("좌표", location.getLatitude() + " " + location.getLongitude());
        CameraPosition cameraPosition = new CameraPosition(new LatLng(Double.parseDouble(location.getLatitude()), Double.parseDouble(location.getLongitude())), 14);
        naverMap.setCameraPosition(cameraPosition);
        mLayoutLocation.setVisibility(View.VISIBLE);
        mLayoutWifi.setVisibility(View.GONE);
        mTextViewTitle.setText(location.getPlaceName());
        mTextViewTitle.setTextColor(getResources().getColor(R.color.colorBlack));
        mTextViewLocationTitle.setText(location.getPlaceName());
        mTextViewLocationAddress.setText(location.getAddressName());

        Marker marker = new Marker();
        marker.setPosition(new LatLng(Double.parseDouble(location.getLatitude()), Double.parseDouble(location.getLongitude())));
        marker.setIcon(OverlayImage.fromResource(R.drawable.img_gps));
        marker.setIconTintColor(getResources().getColor(R.color.colorMarker));
        marker.setWidth(100);
        marker.setHeight(130);
        marker.setMap(naverMap);
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
                longitude = location.getLongitude();
                latitude = location.getLatitude();
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


//        zoomControlView.setMap(naverMap);

        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);
        uiSettings.setZoomControlEnabled(false);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        naverMap.setLightness(0.3f);

    }

}
