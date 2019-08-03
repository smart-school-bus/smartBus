package com.project.smartbus10;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.smartbus10.directionhelpers.TaskLoadedCallback;



public class BusMapActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {
    Button getDirection;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReferencebusStop;
    //map
    private GoogleMap mMap;
    private Marker busLocation;
    private  LatLng latLng;
    private Polyline currentPolyline;
    //
    private String busId;
    private double busLatitude;
    private double busLongitude;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        //
        Intent i = getIntent();
        busId = (String) i.getSerializableExtra("busId");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Bus").child(busId);
        mDatabaseReferencebusStop = mFirebaseDatabase.getReference();


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapNearBy);
        mapFragment.getMapAsync(this);
        getBusLocation();
        busStopsLocation();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap; }
    private void getBusLocation() {
        final BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.school_bus_icon);
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.getKey().equals("busLatitude")) {
                    busLatitude = (double) dataSnapshot.getValue();
                }
                if (dataSnapshot.getKey().equals("busLongitude")) {
                    busLongitude = (double) dataSnapshot.getValue();
                }
                if (busLatitude != 0 && busLongitude != 0) {
                    latLng = new LatLng(busLatitude, busLongitude);
                    if(busLocation!=null){
                        busLocation.setPosition(latLng);
                    }else
                    busLocation = mMap.addMarker(new MarkerOptions().position(latLng).title("Bus").icon(icon));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.setMaxZoomPreference(20);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f)); } }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getKey().equals("busLatitude")) {
                    busLatitude = (double) dataSnapshot.getValue(); }
                if (dataSnapshot.getKey().equals("busLongitude")) {
                    busLongitude = (double) dataSnapshot.getValue(); }
                if (dataSnapshot.getKey().equals("busLatitude") || dataSnapshot.getKey().equals("busLongitude")) {
                     latLng = new LatLng(busLatitude, busLongitude);
                    busLocation.setPosition(latLng);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("databaseError", "postComments:onCancelled", databaseError.toException());
                Toast.makeText(BusMapActivity.this, "Failed to load comments.",
                        Toast.LENGTH_SHORT).show(); }
        });
    }

    public void busStopsLocation() {
        final BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.bus_stops_icon);
        Query busStopsQuery = mDatabaseReferencebusStop.child("BusStop").orderByChild("busID").equalTo(busId);
        busStopsQuery.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                        BusStop busStop = mDataSnapshot.getValue(BusStop.class);
                            if ((double) mDataSnapshot.child("busStopLatitude").getValue() != 0) {
                                LatLng latLng = new LatLng((double) mDataSnapshot.child("busStopLatitude").getValue(), (double) mDataSnapshot.child("busStopLongitude").getValue());
                                mMap.addMarker(new MarkerOptions().position(latLng).title(busStop.getBusStopOrder()+"").icon(icon));
                                mMap.setMaxZoomPreference(20);
                            } else {
                                Toast.makeText(BusMapActivity.this, R.string.bu_stop, Toast.LENGTH_SHORT).show();
                            }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }





    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}
