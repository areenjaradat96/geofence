package com.example.geofence;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent != null) {
            if (geofencingEvent.hasError()) {
                String errorMessage = GeofenceStatusCodes
                        .getStatusCodeString(geofencingEvent.getErrorCode());
                Log.d("mainactitvity", errorMessage);
                return;
            }

            // Get the transition type.
            int geofenceTransition = geofencingEvent.getGeofenceTransition();

            // Test that the reported transition was of interest.
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                    geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

                // Get the geofences that were triggered. A single event can trigger
                // multiple geofences.
                List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

                // Get the transition details as a String.
                String geofenceTransitionDetails = getGeofenceTransitionDetails(
                        geofenceTransition,
                        triggeringGeofences
                );
                Log.d("mainactitvity", geofenceTransitionDetails);
                sendNotification( context,geofenceTransitionDetails);
                if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
                    // Logic to add a new geofence
                    double newLatitude =31.9774688 /* new latitude */;
                    double newLongitude = 35.8342264/* new longitude */;
                    float newRadius = 150/* new radius */;
//                    long newExpirationDuration = /* new expiration duration */;

                    GeofenceHelper geofenceHelper = new GeofenceHelper(context);
                    geofenceHelper.addGeofence(newLatitude, newLongitude, newRadius);
                }



            } else {
                // Log the error.
                Log.d("mainactitvity", "geofence_transition_invalid_type   :" +
                        geofenceTransition);
            }
        }
    }

    private String getGeofenceTransitionDetails(int geofenceTransition, List<Geofence> triggeringGeofences) {
        String transitionString = getTransitionString(geofenceTransition);
        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);
        return transitionString + ": " + triggeringGeofencesIdsString;
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "Entered Geofence";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "Exited Geofence";
            default:
                return "Unknown Geofence Transition";
        }
    }

    private void sendNotification(Context context, String notificationDetails) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "GeofenceServiceChannel",
                    "Geofence Channel",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "GeofenceServiceChannel")
                .setSmallIcon(R.drawable.ic_launcher_background)  // Ensure you have this drawable
                .setContentTitle("Geofence Alert")
                .setContentText(notificationDetails)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(0, builder.build());
    }
}