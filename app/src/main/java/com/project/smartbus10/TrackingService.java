package com.project.smartbus10;


import android.Manifest;
import android.annotation.SuppressLint;
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

import java.util.ArrayList;
import java.util.List;

public class TrackingService extends Service {
    private static final String TAG = TrackingService.class.getSimpleName();
    private SharedPreferences sp;
    private String busId;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private List<BusStop> busStopList;
    private List<String> busStopID;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences("SignIn", MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "My Channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();
            startForeground(1, notification);

        }
        sp = getSharedPreferences("SignIn",MODE_PRIVATE);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        busStopList=new ArrayList<>();
        busStopID=new ArrayList<>();
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
                        }
                        studentsAttendance( busId );
                       // LocationListener();
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
        request.setInterval(5000);
        request.setFastestInterval(1000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        final String path = "Bus" + "/" + busId;
        Log.d(TAG, "location vcbnm, " + path);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        Log.d(TAG, "l " + PackageManager.PERMISSION_GRANTED);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            // Request location updates and when an update is
            // received, store the location in Firebase
            Log.d(TAG, "location update Speed onLocationResult"+client );
            client.requestLocationUpdates(request, new LocationCallback() {

                @SuppressLint("NewApi")
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
                    Location location = locationResult.getLastLocation();
                    Log.d(TAG, "location update Speed onLocationResult" );
                    if (location != null) {
                        Log.d(TAG, "location update Speed" + location.getSpeedAccuracyMetersPerSecond());
                        Log.d(TAG, "location update1 " + location.getLatitude());
                        Log.d(TAG, "location update1 " + location.getLongitude());
                        if(location.getSpeed()>1)
                        {Log.d(TAG, "location update " + location.getLatitude());
                        ref.child("busLatitude").setValue(location.getLatitude());
                        ref.child("busLongitude").setValue(location.getLongitude());
                        Log.d(TAG, "location update Speed" + location.getSpeed());
                        }
                    else{ Log.d(TAG, " not Speed " + location.getLatitude());
                            Log.d(TAG, "not Speed " + location.getLongitude());
                              matchLocation(location);}
                        Log.d(TAG, "location update Speed" + location.getSpeed());
                    }
                }
            },Looper.myLooper());
        }
    }

    public void getBusStopInfo(final String busStID) {
        mDatabaseReference.child("BusStop").child(busStID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               // Log.d("geoLocate", "" + dataSnapshot.getValue().toString());
                if(dataSnapshot.exists()){
                    BusStop busStop=dataSnapshot.getValue(BusStop.class);
                    busStop.setBusStopID(dataSnapshot.getKey());
                    busStopList.add(busStop);
                    Log.d("geoLocate", "busStopList NOT" );
                }else Log.d("geoLocate", "busStopList nll" );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void studentsAttendance(String busId ) {

        Log.d("geoLocate", "BusStopsListFragmentA" + busId);
        Query stuQuery = mDatabaseReference.child("Student").orderByChild("busID").equalTo(busId);

        stuQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                        if ((boolean) mDataSnapshot.child("stuAttendance").getValue() == true) {
                            if (mDataSnapshot.child("busStopID").getValue() != null)
                                if (busStopID.size() > 0) {
                                    if (getIndexBus(mDataSnapshot.child("busStopID").getValue().toString()) == -1) {
                                        getBusStopInfo(mDataSnapshot.child("busStopID").getValue().toString());

                                    }
                                    else
                                        {Log.d("ADebugTag", "busStopID out" + -1);}
                                } else {
                                    Log.d("ADebugTag", "mDataSnapshot" + true);
                                    busStopID.add(mDataSnapshot.child("busStopID").getValue().toString());
                                    getBusStopInfo(mDataSnapshot.child("busStopID").getValue().toString());
                                }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void matchLocation(Location location){
        Intent blouttoth  = new Intent(this, BluetoothService.class);
        blouttoth.putExtra("busID",busId);
        if(busStopList.size()>0){
            for(BusStop b:busStopList){
                if(getDistance(location.getLatitude(),b.getBusStopLatitude(),location.getLongitude(),b.getBusStopLongitude())<0.09){
                    blouttoth.putExtra("busStopId",b.getBusStopID());
                    Log.d(TAG, "match"+b.getBusStopID() );
                    if(b.getLinkAudio()!=null&&!(b.getLinkAudio().equals(""))){
                        Log.d(TAG, "match"+b.getLinkAudio() );
                        Intent serviceIntent = new Intent(this, PlayAudioService.class);
                        serviceIntent.putExtra("audio",b.getLinkAudio());
                        startService(serviceIntent);
                        busStopList.remove(b);
                        Log.d("geoLocate", "size2"+busStopList.size() );
                        break;

                    }else {Intent serviceIntent = new Intent(this, StudentNameVoiceService.class);
                        Log.d(TAG, "match name" );
                        serviceIntent.putExtra("busStop",b);
                        startService(serviceIntent);
                        busStopList.remove(b);
                        Log.d(TAG, "match name size2"+busStopList.size() );
                        break;}

                }else Log.d(TAG, "not match" );
            }
        }
        startService(blouttoth);

    }
    public double getDistance(double lat1, double lat2, double lon1, double lon2)
    {
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));
        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        Log.d("geoLocate", "log" +(c * r));
        return(c * r);
    }

    public int getIndexBus(String ID) {
        int i = -1;
        for (String P : busStopID) {
            if (P.equals(ID)) {
                i = busStopID.indexOf(P);
                return i;
            }
        }
        busStopID.add(ID);
        return i;
    }

}