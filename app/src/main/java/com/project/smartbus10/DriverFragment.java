package com.project.smartbus10;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class DriverFragment extends Fragment {
    private static final int PERMISSIONS_REQUEST = 1;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS =9003 ;
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private SharedPreferences sp;
    private String busID;


    //


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getActivity().getSharedPreferences("SignIn",MODE_PRIVATE);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Bus");
        getBusID();

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        trackingBus();
        View view=inflater.inflate(R.layout.driver_fregment, container, false);
        Button emergency=(Button)view.findViewById(R.id.alert);
        Button busInfo=(Button)view.findViewById(R.id.busInfo);
        emergency.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent GoToEmergencyPage = new Intent(getActivity(), EmergencyActivity.class);
                startActivity(GoToEmergencyPage);

            }
        });
        busInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(busID!=null)
                {Intent goToBusDetailsPage = new Intent(getActivity(), BusDetailsActivity.class);
                    goToBusDetailsPage.putExtra("driverId",sp.getString("ID",""));
                    goToBusDetailsPage.putExtra("busId",busID);
                    startActivity(goToBusDetailsPage);}
            }
        });

        return  view;




    }
    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
      /*  if(mBlueAdapter!=null){
        if (!mBlueAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }}*/
    }
    public void trackingBus(){
        // Check GPS is enabled
        LocationManager lm = (LocationManager)getActivity().getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
           buildAlertMessageNoGps();

        }

        // Check location permission is granted - if it is, start
        // the service, otherwise request the permission
        int permission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            Log.d("ADebugTag", "open" +PackageManager.PERMISSION_GRANTED);
            startTrackerService();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);

        }
    }

    public void startTrackerService() {
        Intent serviceIntent = new Intent(getActivity(), TrackingService.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            getActivity().startForegroundService(serviceIntent);
        }else{
            getActivity().startService(serviceIntent);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Start the service when the permission is granted
            startTrackerService();

        }
    }
    public void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.gps)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                        startTrackerService();
                    }
                });
        builder.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
            }
        });
        try {
            final AlertDialog alert = builder.create();
            alert.show();
        }catch (Exception e){}

    }
    public void getBusID(){
        Query fireQuery = mDatabaseReference.orderByChild("driverID").equalTo(sp.getString("ID",""));
        fireQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange (DataSnapshot dataSnapshot){
                if (dataSnapshot.getValue() != null) {
                    for(DataSnapshot mDataSnapshot: dataSnapshot.getChildren())
                        busID=mDataSnapshot.getKey().toString();
                }}

            @Override
            public void onCancelled (DatabaseError databaseError){

            }
        });
    }
}
