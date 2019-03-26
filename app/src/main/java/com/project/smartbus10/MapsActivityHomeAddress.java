package com.project.smartbus10;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivityHomeAddress extends FragmentActivity implements OnMapReadyCallback {


    private static final float DEFAULT_ZOOM = 15f;
    private GoogleMap mMap;
    LocationManager locationManager;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    Marker marker;
    LocationListener locationListener;
    Geocoder geocoder;
    List<Address> addresses;
    static double latitude;
    static double longitude;
    static String homeAddress;
    private boolean createMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_location);
        createMarker = true;
        Intent i = getIntent();
        if ((i.getSerializableExtra("latitude") != null && i.getSerializableExtra("longitude") != null)) {
            latitude = (double) i.getSerializableExtra("latitude");
            longitude = (double) i.getSerializableExtra("longitude");
        }
        if (i.getSerializableExtra("Address") != null) {
            homeAddress = (String) i.getSerializableExtra("Address");
            Log.d("geoLocate", "geoLocate: geolocating1" + homeAddress);

        }
        if (i.getSerializableExtra("createMarker") != null) {
            createMarker = (boolean) i.getSerializableExtra("createMarker");
            Log.d("geoLocate", "geoLocate: geolocating1" + homeAddress);

        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (latitude == 0 && longitude == 0 && homeAddress == null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
                //get the location name from latitude and longitude
                geocoder = new Geocoder(getApplicationContext());


                try {
                    if (homeAddress != null && latitude == 0 && longitude == 0) {
                        addresses = geocoder.getFromLocationName(homeAddress, 1);
                        if (addresses.size() >= 1) {
                            latitude = addresses.get(0).getLatitude();
                            longitude = addresses.get(0).getLongitude();
                            Log.d("geoLocate", "3" + latitude);
                            Log.d("geoLocate", "4" + longitude);
                        } else {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        }
                    } else {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        Log.d("geoLocate", "geoLocate: 5");
                    }

                    //String result = addresses.get(0).getLocality()+":";
                    // result += addresses.get(0).getCountryName();
                    LatLng latLng = new LatLng(latitude, longitude);
                    if (marker != null) {
                        marker.remove();
                        homeAddress = addresses.get(0).getFeatureName() + "," + addresses.get(0).getAddressLine(0);
                        marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Home"));
                        mMap.setMaxZoomPreference(20);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
                    } else {
                        homeAddress = addresses.get(0).getFeatureName() + "," + addresses.get(0).getAddressLine(0);
                        marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Home"));
                        mMap.setMaxZoomPreference(20);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18F));
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


    }

    private void geoLocate() {
        Log.d("geoLocate", "geoLocate: geolocating");


        Geocoder geocoder = new Geocoder(MapsActivityHomeAddress.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(homeAddress, 1);
        } catch (IOException e) {
        }

        if (list.size() > 0) {
            Address address = list.get(0);
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(), address.getLongitude()), 18F));

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
        //select location
        if (createMarker) {
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    latitude = latLng.latitude;
                    longitude = latLng.longitude;
                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    homeAddress = addresses.get(0).getFeatureName() + "," + addresses.get(0).getAddressLine(0);
                    Toast.makeText(MapsActivityHomeAddress.this, "" + latitude + longitude, Toast.LENGTH_SHORT).show();
                    //  LatLng addess = new LatLng(latitude ,  longitude);
                    mMap.clear();
                    marker = mMap.addMarker(new MarkerOptions().position(latLng).title("home"));

                }
            });
        }
        // Add a marker in Sydney and move the camera
        // LatLng uqu = new LatLng(21.387330 ,  39.858951);
        //  mMap.addMarker(new MarkerOptions().position(uqu).title("Marker in UQU"));
        //   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uqu,10F));
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onBackPressed() {
        if (createMarker) {
            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Do you want to save the location ?")
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            latitude = 0;
                            longitude = 0;
                            homeAddress = null;
                            MapsActivityHomeAddress.super.onBackPressed();
                        }
                    })
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            MapsActivityHomeAddress.super.onBackPressed();
                        }
                    }).create().show();
        } else {
            latitude = 0;
            longitude = 0;
            homeAddress = null;
            finish();
            MapsActivityHomeAddress.super.onBackPressed();
        }
    }
}



