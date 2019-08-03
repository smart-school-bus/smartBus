package com.project.smartbus10;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

public class BusStopsMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    static String stopName;
    static double latitude;
    static double longitude;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private Query studentsHomeQuery;
    private GoogleMap mMap;
    private GoogleMap mMapStop;
    private String busId;
    private String parentId;
    private String busStopId;
    private boolean reStud;
    private boolean createMarker;
    private List<Address> addresses;
    private Geocoder geocoder;
    private Marker markerOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_stops_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        createMarker = true;
        reStud = false;

        Intent i = getIntent();
        if ((i.getSerializableExtra("latitude") != null && i.getSerializableExtra("longitude") != null)) {
            latitude = (double) i.getSerializableExtra("latitude");
            longitude = (double) i.getSerializableExtra("longitude");
        }
        if (i.getSerializableExtra("stopName") != null) {
            stopName = (String) i.getSerializableExtra("Address");

        }
        if (i.getSerializableExtra("busId") != null) {
            busId = (String) i.getSerializableExtra("busId");

        }

        if (i.getSerializableExtra("createMarker") != null) {
            createMarker = (boolean) i.getSerializableExtra("createMarker");
        }
        if (i.getSerializableExtra("reStud") != null) {
            reStud = (boolean) i.getSerializableExtra("reStud");
        }
        if (i.getSerializableExtra("parentId") != null) {
            parentId = (String) i.getSerializableExtra("parentId");
            createMarker = false;

        }
        if (i.getSerializableExtra("busStopId") != null) {
            busStopId = (String) i.getSerializableExtra("busStopId");

        }


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        if (busId != null && reStud == false) {
            if (parentId != null) {
                getStudentHomeLoc(parentId);
            } else {
                getStudent();
            }

            busStopsLocation();
        } else if (reStud == true) {
            if (parentId != null) {
                getStudentHomeLoc(parentId);
            }
            if (busId != null) {
                busStopsLocation();
            } else if(busStopId!= null){
                getbusStop();
            }
            else
                getAllBusStop();
        }else if(busStopId != null)
                  { Log.d("ADebugTag", "busS"+busStopId );
                      getbusStop();
                      if (parentId != null) {
                      getStudentHomeLoc(parentId); }
                      }


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
        mMapStop = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(21.423176, 39.825633), 12.0f));
        final BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.bus_stops_icon);
        geocoder = new Geocoder(getApplicationContext());
        // Add a marker in Sydney and move the camera
        mMapStop.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (createMarker == true) {
                    latitude = latLng.latitude;
                    longitude = latLng.longitude;
                    if (markerOptions != null) { //if marker exists (not null or whatever)
                        markerOptions.setPosition(latLng);

                    } else {
                        markerOptions = mMapStop.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title("BusStop")
                                .icon(icon)
                                .draggable(true));
                    }
                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addresses.size() > 0)
                        stopName = addresses.get(0).getFeatureName() + "," + addresses.get(0).getAddressLine(0);


                }
            }
        });

        if (createMarker == false && busId == null&&parentId==null&&busStopId==null) {
            LatLng latLng;

            if(latitude!=0){
                latLng = new LatLng(latitude, longitude);
                mMapStop.addMarker(new MarkerOptions().position(latLng).title("BusStop").icon(icon));
            }else
               latLng = new LatLng(21.441019, 39.810650);
            mMapStop.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
            mMapStop.setMaxZoomPreference(20);
        }

    }


    public void getStudent() {
        if(busId!=null){
        studentsHomeQuery = mDatabaseReference.child("Student").orderByChild("busID").equalTo(busId);}
        else{studentsHomeQuery = mDatabaseReference.child("Student").orderByChild("busStopID").equalTo(busStopId);}
        studentsHomeQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {
                        for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {

                            if (mDataSnapshot.child("parentID").getValue().toString() != null) {
                                getStudentHomeLoc(mDataSnapshot.child("parentID").getValue().toString());
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getStudentHomeLoc(String Id) {
        final BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.home_icon);
        geocoder = new Geocoder(getApplicationContext());
        mDatabaseReference.child("Parent").child(Id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((double) dataSnapshot.child("latitude").getValue() != 0) {
                    LatLng latLng = new LatLng((double) dataSnapshot.child("latitude").getValue(), (double) dataSnapshot.child("longitude").getValue());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Home").icon(icon));
                    mMap.setMaxZoomPreference(20);
                } else {
                    try {
                        addresses = geocoder.getFromLocationName(dataSnapshot.child("address").getValue().toString(), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addresses.size() >= 1) {
                        LatLng latLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                       // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
                        mMap.addMarker(new MarkerOptions().position(latLng).title("Home").icon(icon));
                        mMap.setMaxZoomPreference(20);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void busStopsLocation() {
        final BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.bus_stops_icon);

        studentsHomeQuery = mDatabaseReference.child("BusStop").orderByChild("busID").equalTo(busId);
        studentsHomeQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("ADebugTag", "dataSnapshot"+dataSnapshot.getValue() );
                    for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                        try {
                            if ((double) mDataSnapshot.child("busStopLatitude").getValue() != 0) {
                                LatLng latLng = new LatLng((double) mDataSnapshot.child("busStopLatitude").getValue(), (double) mDataSnapshot.child("busStopLongitude").getValue());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
                                mMap.addMarker(new MarkerOptions().position(latLng).title(mDataSnapshot.child("busStopOrder").getValue() + "").icon(icon));
                                mMap.setMaxZoomPreference(20);

                            } else {

                                try {
                                    addresses = geocoder.getFromLocationName(dataSnapshot.child("busStopName").getValue().toString(), 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (addresses.size() >= 1) {

                                    LatLng latLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());

                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
                                    if ((int) mDataSnapshot.child("busStopOrder").getValue() != 0) {
                                        mMap.addMarker(new MarkerOptions().position(latLng).title(mDataSnapshot.child("busStopOrder").getValue() + "").icon(icon));
                                    } else {
                                        mMap.addMarker(new MarkerOptions().position(latLng).title("BusStop").icon(icon));
                                    }

                                    mMap.setMaxZoomPreference(20);
                                }

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void getbusStop(){
        final BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.bus_stops_icon);
      mDatabaseReference.child("BusStop").child(busStopId).addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              if(dataSnapshot.exists())
              {Log.d("ADebugTag", "dataSnapshot"+dataSnapshot.getValue() );
              BusStop busStop=dataSnapshot.getValue(BusStop.class);
              if(busStop.getBusStopLatitude()!=0){
                  LatLng latLng = new LatLng(busStop.getBusStopLatitude(), busStop.getBusStopLongitude());
                  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
                  mMap.addMarker(new MarkerOptions().position(latLng).title(busStop.getBusStopOrder() + "").icon(icon));
                  mMap.setMaxZoomPreference(20);
              }
          }}

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });
    }

    public void getAllBusStop() {
        final BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.bus_stops_icon);
        mDatabaseReference.child("BusStop").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                        BusStop busStop = mDataSnapshot.getValue(BusStop.class);
                        busStop.setBusStopID(mDataSnapshot.getKey());

                        try {
                            if ((busStop.getBusStopLatitude() != 0)) {
                                LatLng latLng = new LatLng(busStop.getBusStopLatitude(), busStop.getBusStopLongitude());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
                                if (busStop.getBusStopID() != null) {
                                    mMap.addMarker(new MarkerOptions().position(latLng).title(busStop.getBusStopID() + "").icon(icon));
                                } else {
                                    mMap.addMarker(new MarkerOptions().position(latLng).title("BusStop").icon(icon));
                                }

                                mMap.setMaxZoomPreference(20);

                            } else {
                                try {
                                    addresses = geocoder.getFromLocationName(busStop.getBusStopAddress(), 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (addresses.size() >= 1) {
                                    LatLng latLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
                                    if (busStop.getBusStopID() != null) {
                                        mMap.addMarker(new MarkerOptions().position(latLng).title(busStop.getBusStopID() + "").icon(icon));
                                    } else {
                                        mMap.addMarker(new MarkerOptions().position(latLng).title("BusStop").icon(icon));
                                    }

                                    mMap.setMaxZoomPreference(20);
                                }

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (createMarker) {
            Toast.makeText(BusStopsMapsActivity.this, "" + latitude + longitude, Toast.LENGTH_SHORT).show();
            //  LatLng addess = new LatLng(latitude ,  longitude);
            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Do you want to save the location ?")
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            latitude = 0;
                            longitude = 0;
                            stopName = null;
                            BusStopsMapsActivity.super.onBackPressed();
                        }
                    })
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {

                            BusStopsMapsActivity.super.onBackPressed();
                        }
                    }).create().show();
        } else {
            latitude = 0;
            longitude = 0;
            stopName = null;
            finish();
            BusStopsMapsActivity.super.onBackPressed();
        }
    }

}
