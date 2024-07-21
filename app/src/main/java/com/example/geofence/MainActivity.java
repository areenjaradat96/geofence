package com.example.geofence;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        geofenceHelper = new GeofenceHelper(this);

        // Check permissions and start geofencing
        checkPermissionsAndStartGeofencing();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;


    private void checkPermissionsAndStartGeofencing() {
        // Check if the location permission is already granted
        List<String> permissions = new ArrayList<>();

        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) && !isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION))
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !isPermissionGranted(Manifest.permission.POST_NOTIFICATIONS)))
            permissions.add(Manifest.permission.POST_NOTIFICATIONS);

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && !isPermissionGranted(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) && isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) && isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION))
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);

        // If you need to convert the list back to an array
        String[] permissionsArray = permissions.toArray(new String[0]);
        if (!permissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsArray,
                    LOCATION_PERMISSION_REQUEST_CODE);



        } else {
            addGeofence();
        }

    }

    private boolean isPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && !isPermissionGranted(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) && isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) && isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION))
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION} ,
                            LOCATION_PERMISSION_REQUEST_CODE);
                else addGeofence();
            } else {
                Log.d("mainactitvity", "Permission denied");
            }

        }
    }

    private void addGeofence() {
        Intent serviceIntent = new Intent(this, GeofenceService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(serviceIntent);
        else
            startService(serviceIntent);

        geofenceHelper.addGeofence(31.9826022
                , 35.8656497
                , 150
                );
//        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
//        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//
//        geofencingClient.addGeofences(geofencingRequest, pendingIntent).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.i("mainactitvity", "Geofence added");
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.e("mainactitvity", "Failed to add geofence: " + e.getMessage());
//            }
//        });
    }
//    private void checkPermissionsAndStartGeofencing() {
//        // Check if the location permission is already granted
//        List<String> permissions = new ArrayList<>();
////        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && !isPermissionGranted(Manifest.permission.FOREGROUND_SERVICE_LOCATION)))
////            permissions.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION);
//
////        if (!isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION) )
////            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
//
//        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) && !isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION))
//            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
//
////        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !isPermissionGranted(Manifest.permission.FOREGROUND_SERVICE)))
////            permissions.add(Manifest.permission.FOREGROUND_SERVICE);
////
//        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !isPermissionGranted(Manifest.permission.POST_NOTIFICATIONS)))
//            permissions.add(Manifest.permission.POST_NOTIFICATIONS);
////
//        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && !isPermissionGranted(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) && isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) && isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION))
//            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
//
//        // If you need to convert the list back to an array
//        String[] permissionsArray = permissions.toArray(new String[0]);
//        if (!permissions.isEmpty()) {
//            ActivityCompat.requestPermissions(this, permissionsArray,
//                    LOCATION_PERMISSION_REQUEST_CODE);
//
//        } else {
//            addGeofence();
//        }
//
//    }
}