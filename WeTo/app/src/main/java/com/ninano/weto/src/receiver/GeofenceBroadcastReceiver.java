package com.ninano.weto.src.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_DWELL;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    // ...
    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e("지오", errorMessage);
            return;
        }
        Log.e("지오", "수신" + geofencingEvent.getTriggeringLocation());

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition(); // 발생 이벤트 타입

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT || geofenceTransition == GEOFENCE_TRANSITION_DWELL) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // triggeringGeofences <- 이 리스트에서 어떤 지오펜싱이 감지된건지 판단
            String geofenceTransitionDetails;
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                geofenceTransitionDetails = "ENTER";
            } else {
                geofenceTransitionDetails = "EXIT";
            }

            geofenceTransitionDetails += triggeringGeofences.get(0).getRequestId();

            Toast.makeText(context, geofenceTransitionDetails, Toast.LENGTH_LONG).show();


            // Send notification and log the transition details.
//                sendNotification(geofenceTransitionDetails);
            Log.i("지오", geofenceTransitionDetails);
        } else {
            // Log the error.
            Log.e("지오", "");
        }
    }
}