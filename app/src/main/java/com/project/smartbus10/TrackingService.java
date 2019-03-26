package com.project.smartbus10;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class TrackingService extends Service {
    private static final String TAG = TrackingService.class.getSimpleName();
    private SharedPreferences sp;
    private String busId;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences("SignIn",MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= 26) {
           String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "My Channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
              manager.createNotificationChannel(channel);
              Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();
            startForeground(1, notification);

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getBusID();
        return Service.START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ADebugTag", "close");
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("ADebugTag", "received stop broadcast");
            // Stop the service when the notification is tapped
            unregisterReceiver(stopReceiver);
            stopForeground(true);
            stopForeground(true);
            stopSelf();
        }
    };


    private void getBusID() {
           DatabaseReference mDatabaseReference =FirebaseDatabase.getInstance().getReference();
            Query fireQuery = mDatabaseReference.child("Bus").orderByChild("driverID").equalTo(sp.getString("ID", ""));
            fireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue()!=null){
                        for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()){
                            busId=mDataSnapshot.getKey().toString();
                            Log.d("ADebugTag", "Value: " +busId+"");
                        }

                        requestLocationUpdates();
                    }
                    else {  stopSelf();Log.d("ADebugTag", "BB: " +dataSnapshot.getValue()+"");}
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }});}


    private void requestLocationUpdates() {
        // Functionality coming next step
        Log.d(TAG, "location update " + 2);
        LocationRequest request = new LocationRequest();
        request.setInterval(100);
        request.setFastestInterval(50);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        final String path = "Bus" + "/" + busId;
        Log.d(TAG, "location update " + path);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        Log.d(TAG, "l " + PackageManager.PERMISSION_GRANTED);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            // Request location updates and when an update is
            // received, store the location in Firebase
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        Log.d(TAG, "location update " + location.getLatitude());
                        ref.child("busLatitude").setValue(location.getLatitude());
                        ref.child("busLongitude").setValue(location.getLongitude());
                    }
                }
            },  Looper.myLooper());
        }
    }

}