package com.ninano.weto.src.common.geofence;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.util.Pair;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_DWELL;
import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER;
import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT;
import static com.ninano.weto.src.ApplicationClass.AT_ARRIVE;
import static com.ninano.weto.src.ApplicationClass.AT_START;
import static com.ninano.weto.src.ApplicationClass.LOITERING_DELAY;
import static com.ninano.weto.src.ApplicationClass.getApplicationClassContext;

public class GeofenceMaker {

    private static GeofenceMaker GeofenceMakerInstance = null;

    public static GeofenceMaker getGeofenceMaker() {
        if (GeofenceMakerInstance == null) {
            GeofenceMakerInstance = new GeofenceMaker();
        }
        return GeofenceMakerInstance;
    }

    private GeofencingClient geofencingClient;

    private GeofenceMaker() {
        this.geofencingClient = LocationServices.getGeofencingClient(getApplicationClassContext());
    }

    private GeofencingRequest getGeofencingRequest(Geofence geofence, int locationMode) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        Log.d("locationMode", locationMode+"");

        if (locationMode == AT_START) {
            builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        } else if (locationMode == AT_ARRIVE) {
            builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT);
        } else { //들를 때
            builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        }
//        builder.addGeofences(geofence);  // Geofence 리스트 추가
        builder.addGeofence(geofence);
        return builder.build();
    }

    private GeofencingRequest getGeofencingListRequest(List<Geofence> geofences) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
//        if (locationMode == AT_START) {
//            builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
//        } else if (locationMode == AT_ARRIVE) {
//            builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT);
//        }
//        builder.addGeofences(geofence);  // Geofence 리스트 추가
        builder.addGeofences(geofences);
        return builder.build();
    }

    public Geofence getGeofence(int type, String reqId, Pair<Double, Double> geo, Float radiusMeter) {
        int GEOFENCE_TRANSITION;
        if (type == AT_ARRIVE) {
            GEOFENCE_TRANSITION = GEOFENCE_TRANSITION_ENTER;  // 진입 감지시
            return new Geofence.Builder()
                    .setRequestId(reqId)    // 이벤트 발생시 BroadcastReceiver에서 구분할 id
                    .setCircularRegion(geo.first, geo.second, radiusMeter)    // 위치및 반경(m)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)        // Geofence 만료 시간
                    .setLoiteringDelay(LOITERING_DELAY)                            // 머물기 체크 시간 -> 10초
                    .setNotificationResponsiveness(120000)      //위치감지하는 텀 120000 = 120초
                    .setTransitionTypes(GEOFENCE_TRANSITION)
                    .build();
        }
        else if(type == AT_START){
            GEOFENCE_TRANSITION = GEOFENCE_TRANSITION_EXIT;  // 진입 감지시
            return new Geofence.Builder()
                    .setRequestId(reqId)    // 이벤트 발생시 BroadcastReceiver에서 구분할 id
                    .setCircularRegion(geo.first, geo.second, radiusMeter)    // 위치및 반경(m)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)        // Geofence 만료 시간
                    .setLoiteringDelay(LOITERING_DELAY)                            // 머물기 체크 시간 -> 10초
                    .setNotificationResponsiveness(120000)      //위치감지하는 텀 120000 = 120초
                    .setTransitionTypes(GEOFENCE_TRANSITION)
                    .build();
        }
        else {
            GEOFENCE_TRANSITION = GEOFENCE_TRANSITION_DWELL; // 머물기 감지시, LOITERING_DELAY를 좀더 낮게설정(인식이 잘되도록)
            return new Geofence.Builder()
                    .setRequestId(reqId)    // 이벤트 발생시 BroadcastReceiver에서 구분할 id
                    .setCircularRegion(geo.first, geo.second, radiusMeter)    // 위치및 반경(m)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)        // Geofence 만료 시간
                    .setLoiteringDelay(3000)                            // 머물기 체크 시간 -> 3초
                    .setNotificationResponsiveness(120000)      //위치감지하는 텀 120000 = 120초
                    .setTransitionTypes(GEOFENCE_TRANSITION)
                    .build();
        }
    }

    private PendingIntent geofencePendingIntent() {
        Intent intent = new Intent(getApplicationClassContext(), GeofenceBroadcastReceiver.class);
        return PendingIntent.getBroadcast(getApplicationClassContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void addGeoFenceOne(int todoNo, double latitude, double longitude,
                                    int locationMode, int radius, OnSuccessListener onSuccessListener, OnFailureListener
                                            onFailureListener) {
        if (ActivityCompat.checkSelfPermission(getApplicationClassContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(getGeofencingRequest(getGeofence(locationMode, String.valueOf(todoNo), new Pair<>(latitude, longitude), (float) radius), locationMode), geofencePendingIntent()).addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    public void addGeoFenceOneForGroupTodo(String todoNo, double latitude, double longitude,
                               int locationMode, int radius, OnSuccessListener onSuccessListener, OnFailureListener
                                       onFailureListener) {
        if (ActivityCompat.checkSelfPermission(getApplicationClassContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geofencingClient.addGeofences(getGeofencingRequest(getGeofence(locationMode, todoNo, new Pair<>(latitude, longitude), (float) radius), locationMode), geofencePendingIntent()).addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }


    public void removeAllGeofence() {
        geofencingClient.removeGeofences(geofencePendingIntent());
    }

    public void removeGeofence(String id) {
        List<String> idList = new ArrayList<>();
        idList.add(id);
        geofencingClient.removeGeofences(idList);
    }

    public void addGeoFenceList(List<Geofence> geofenceList, OnSuccessListener
            onSuccessListener, OnFailureListener onFailureListener) {
        if (ActivityCompat.checkSelfPermission(getApplicationClassContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(getGeofencingListRequest(geofenceList), geofencePendingIntent()).addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }
}
