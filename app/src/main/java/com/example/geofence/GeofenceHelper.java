package com.example.geofence;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class GeofenceHelper extends ContextWrapper {
    private static final String TAG = "GeofenceHelper";
    private final GeofencingClient geofencingClient;


    private PendingIntent pendingIntent;

    public void addGeofence(double latitude, double longitude, float radius) {
        Geofence geofence = new Geofence.Builder()
                .setRequestId("GEOFENCE_ID_1" )
                .setCircularRegion(latitude, longitude, radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE) // Set the expiration
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        List<Geofence> geofences1 =new ArrayList<>() ;
        geofences1.add(geofence);
//        geofences1.add(geofence2);
        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences(geofences1)
                .build();

        PendingIntent geofencePendingIntent = getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("GeofenceHelper", "Geofence added successfully");
            } else {
                Log.d("GeofenceHelper", "Failed to add geofence: " + task.getException());
            }
        });
    }

    public GeofenceHelper(Context base) {
        super(base);
        geofencingClient = LocationServices.getGeofencingClient(this);
    }

    public Geofence getGeofence(String id, double lat, double lng, float radius) {
        return new Geofence.Builder()
                .setRequestId(id)
                .setCircularRegion(lat, lng, radius)
                .setLoiteringDelay(200)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
    }

    public GeofencingRequest getGeofencingRequest(Geofence geofence) {
        return new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();
    }

    public PendingIntent getPendingIntent() {
        if (pendingIntent != null) {
            return pendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
            );
        } else {
            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return pendingIntent;
    }
}
