package com.project.smartbus10;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import static android.content.Context.LOCATION_SERVICE;

public class DriverFragment extends Fragment {
    private static final int PERMISSIONS_REQUEST = 1;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS =9003 ;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        trackingBus();
        View view=inflater.inflate(R.layout.driver_fregment, container, false);
        Button emergency=(Button)view.findViewById(R.id.alert);
        emergency.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent GoToEmergencyPage = new Intent(getActivity(), Emergency.class);
                startActivity(GoToEmergencyPage);
                Log.d("ADebugTag", "Value: " +"hi hi hi");
                // do something
            }
        });

        return  view;




    }
    public void trackingBus(){
        // Check GPS is enabled
        Log.d("ADebugTag", "Value: " +1 +"");
        LocationManager lm = (LocationManager)getActivity().getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

            Log.d("ADebugTag", "Value: " +2);
        }


        // Check location permission is granted - if it is, start
        // the service, otherwise request the permission
        int permission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
            Log.d("ADebugTag", "Value: " +3+"");
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
            Log.d("ADebugTag", "Value: " +4+"");
        }
    }

    private void startTrackerService() {
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
            Log.d("ADebugTag", "Value: " +6 +"");
        } else {
            Log.d("ADebugTag", "Value: " +8);
        }
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.gps)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        builder.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
        }
    });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
