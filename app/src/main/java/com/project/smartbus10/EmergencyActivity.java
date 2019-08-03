package com.project.smartbus10;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.smartbus10.directionhelpers.Emergency;

public class EmergencyActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReference2;
    private SharedPreferences sp;
    private Button sendB;
    private ImageButton delete;
    private EditText mass;
    private TextView show_mass;
    private TextView driverId;
    private TextView namDriver;
    private TextView busID;
    private TextView plate;
    private LinearLayout showInf;
    private LinearLayout sendEmer;
    private ImageView phone;
    private ImageView location;
    private ProgressDialog progressBar;
    private Emergency emergency;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        sp = getSharedPreferences("SignIn", MODE_PRIVATE);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        delete=findViewById(R.id.delete);
        delete.setVisibility(View.GONE);
        progressBar = new ProgressDialog(EmergencyActivity.this);
        progressBar.setMessage(getString(R.string.p));
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Emergency");
        mDatabaseReference2 = mFirebaseDatabase.getReference();
        showInf = findViewById(R.id.show_emr);
        sendEmer = findViewById(R.id.emr);
        mass = findViewById(R.id.mass);
        sendB = findViewById(R.id.send);

        sendB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.show();
                sendEmergency();
                progressBar.dismiss();
            }
        });
        emergency = new Emergency();
        final Intent i = getIntent();
        Log.d("ADebugTag","VGNNBHBH "+i.getSerializableExtra("idItem"));
        if (i.getSerializableExtra("idItem") != null) {
            progressBar.show();
            showInf.setVisibility(View.VISIBLE);
            sendEmer.setVisibility(View.GONE);
            delete.setVisibility(View.VISIBLE);
            emergency.setEmergency_id((String) i.getSerializableExtra("idItem"));
            show_mass = findViewById(R.id.sho_mass);
            driverId = findViewById(R.id.id);
            namDriver = findViewById(R.id.name);
            busID = findViewById(R.id.bus_id);
            plate = findViewById(R.id.plate);
            phone = findViewById(R.id.phone);
            location = findViewById(R.id.b_map);
            getEmergency();
            phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Uri number = Uri.parse("tel:" + emergency.getDriver().getPhone());
                        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                        startActivity(callIntent);
                    } catch (Exception e) {
                    }
                }
            });
            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent goToMap = new Intent(EmergencyActivity.this, BusMapActivity.class);
                    goToMap.putExtra("busId", emergency.getDriver().getBus().getID());
                    startActivity(goToMap);
                }
            });
        } else {
            showInf.setVisibility(View.GONE);
            sendEmer.setVisibility(View.VISIBLE);
        }


        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sp.getString("user_type","").equals("Driver")){
                    Intent goToHomwPage = new Intent(EmergencyActivity.this, Home.class);
                    startActivity(goToHomwPage);
                }else {
                    Intent goToListPage = new Intent(EmergencyActivity.this, EmergencyList.class);
                    startActivity(goToListPage);
                }
                finish();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.show();
                mDatabaseReference.child((String) i.getSerializableExtra("idItem")).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            dataSnapshot.getRef().removeValue();
                            Intent goToListPage = new Intent(EmergencyActivity.this, EmergencyList.class);
                            startActivity(goToListPage);
                            progressBar.dismiss();
                        } else {  // error in password
                            progressBar.dismiss();
                            Toast.makeText(EmergencyActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
            }
        });

    }

    private void getEmergency() {
        mDatabaseReference.child(emergency.getEmergency_id()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    emergency = dataSnapshot.getValue(Emergency.class);
                    if (dataSnapshot.hasChildren()) {
                        for(DataSnapshot mDataSnapshot:dataSnapshot.getChildren()){
                            String adim=mDataSnapshot.getKey().toString();
                            for(DataSnapshot mDataSnapshot2:dataSnapshot.child(adim).getChildren()) {
                                emergency.setMessage(mDataSnapshot2.child("message").getValue().toString());
                                  getDriverInfo(mDataSnapshot2.getKey().toString());
                    }

                }
            }}}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getDriverInfo(String id) {
        mDatabaseReference2.child("Driver").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Driver driver = dataSnapshot.getValue(Driver.class);
                    driver.setDriverID(dataSnapshot.getKey());
                    getBusInfo(driver.getDriverID());
                    emergency.setDriver(driver);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getBusInfo(String id) {
        mDatabaseReference2.child("Bus").orderByChild("driverID").equalTo(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                        Bus bus = mDataSnapshot.getValue(Bus.class);
                        bus.setID(mDataSnapshot.getKey());
                        emergency.getDriver().setBus(bus);
                        showEmergency();
                    }
                    progressBar.dismiss();
                }else progressBar.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showEmergency() {
        show_mass.setText(emergency.getMessage());
        driverId.setText(emergency.getDriver().getDriverID());
        namDriver.setText(emergency.getDriver().getFirstName() +" "+emergency.getDriver().getLastName());
        busID.setText(emergency.getDriver().getBus().getID());
        plate.setText(emergency.getDriver().getBus().getPlate());
    }

    private void sendEmergency() {
        mDatabaseReference.push().child("A5681").child(sp.getString("ID", "")).child("message").setValue(mass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EmergencyActivity.this, R.string.su, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(EmergencyActivity.this, R.string.unsu, Toast.LENGTH_SHORT).show();
            }
        });

    }


}
