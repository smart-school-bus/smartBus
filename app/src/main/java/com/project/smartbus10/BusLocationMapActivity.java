package com.project.smartbus10;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BusLocationMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private GoogleMap mMap;
    private String busId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_location_map);
        Intent i=getIntent();
        busId=(String) i.getSerializableExtra("busId");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Bus").child(busId);
        Log.d("geoLocate", "geoLocate: EnterMap" );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mChildEventListener= new ChildEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Bus bus=dataSnapshot.getValue(Bus.class);
                LatLng latLng = new LatLng(bus.getBusLatitude(),bus.getBusLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Sydney"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mMap.clear();
                Bus bus=dataSnapshot.getValue(Bus.class);
                LatLng latLng = new LatLng(bus.getBusLatitude(),bus.getBusLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Sydney"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("databaseError", "postComments:onCancelled", databaseError.toException());
                Toast.makeText(BusLocationMapActivity.this, "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();

            }
        };
        mDatabaseReference.addChildEventListener(mChildEventListener);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
