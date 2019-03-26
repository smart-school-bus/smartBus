package com.project.smartbus10;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class BusDetailsActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReferenceDriver;

    private String busId;
    private String driverId;
    private TextView busID;
    private TextView Location;
    private TextView numberPassengers;
    private TextView driverName;
    private Button studentRegistButton;
    private Button busStops;
    private ImageButton loc;
    private ProgressDialog progressBar;
    //
    private Bus bus;
    private  Query fireQuery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_details);

        busID=findViewById(R.id.bus_id);
        Location=findViewById(R.id.location);
        numberPassengers=findViewById(R.id.num_p);
        driverName=findViewById(R.id.driver_name);
        studentRegistButton=findViewById(R.id.students);
        busStops=findViewById(R.id.bus_stops);
        loc=findViewById(R.id.b_map);
        Intent i = getIntent();
        busId = (String) i.getSerializableExtra("busId");
        driverId=(String) i.getSerializableExtra("driverId");
        progressBar=new ProgressDialog(BusDetailsActivity.this);
        progressBar.setMessage(getString(R.string.p));
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseReference=mFirebaseDatabase.getReference().child("Bus");
        mDatabaseReferenceDriver=mFirebaseDatabase.getReference("Driver");
        progressBar.show();
        getBus();

        // open fragment  StudentList
        studentRegistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putSerializable("busId", busId);
                Fragment studentListFragment =new StudentListFragment();
                studentListFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.bus_details, studentListFragment, "StudentListFragment").addToBackStack("StudentList")
                        .commit();
            }
        });
        // open fragment  BusStops
        busStops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putSerializable("busId", busId);
                Fragment busStopsFragment =new BusStopsFragment();
                busStopsFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.bus_details, new BusStopsFragment(), "BusStopsFragment").addToBackStack("BusStops")
                        .commit();
            }
        });
        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToMap=new Intent(BusDetailsActivity.this, BusLocationMapActivity.class);
                    goToMap.putExtra("busId",busId);
                    startActivity(goToMap);
            }
        });

    }

    public void getBus() {
        if(busId!=null){
            fireQuery= mDatabaseReference.child(busId)  ;
        }else
            fireQuery= mDatabaseReference.orderByChild("driverID").equalTo(driverId);
        fireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    bus=dataSnapshot.getValue(Bus.class);
                    bus.setID(dataSnapshot.getKey().toString());
                    Log.d("ADebugTag", "1");
                     getDriver(dataSnapshot.child("driverID").getValue().toString());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getDriver(String id) {
        mDatabaseReferenceDriver.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    bus.setDriver(dataSnapshot.getValue(Driver.class));
                    bus.getDriver().setDriverID(dataSnapshot.getKey().toString());
                    Log.d("ADebugTag", "1");
                    showInfo();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void showInfo() {
        busID.setText(bus.getID());
        Location.setText(bus.getBusLatitude()+","+bus.getBusLongitude());
        numberPassengers.setText(bus.getStudentNumber()+"");
        driverName.setText(bus.getDriver().getFirstName()+" "+bus.getDriver().getLastName());
        progressBar.dismiss();
    }
}
