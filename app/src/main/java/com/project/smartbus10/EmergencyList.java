package com.project.smartbus10;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.smartbus10.directionhelpers.Emergency;

import java.util.ArrayList;
import java.util.List;

public class EmergencyList extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReference2;
    private ChildEventListener mChildEventListener;

    private ListView list;
    private EmergencyAdapter adapter;
    private List<Emergency> emergencyList;

    private ProgressDialog progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_list);
        progressBar=new ProgressDialog(EmergencyList.this);
        progressBar.setMessage(getString(R.string.p));

        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseReference=mFirebaseDatabase.getReference().child("Emergency");
        mDatabaseReference2=mFirebaseDatabase.getReference();


        emergencyList = new ArrayList<>();
        list=(ListView)findViewById(R.id.emergency_list) ;
        adapter = new EmergencyAdapter(this, R.layout.item_list, emergencyList);
        list.setAdapter(adapter);
        progressBar.show();
        getEmeList();

     list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
             Emergency emergency = emergencyList.get(i);
             Intent goToEmergency = new Intent(EmergencyList.this, EmergencyActivity.class);
             goToEmergency.putExtra("idItem", emergency.getEmergency_id());
             startActivity(goToEmergency);
         }
     });


    }

    private void getEmeList(){
        mChildEventListener= new ChildEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                final Emergency emergency =dataSnapshot.getValue(Emergency.class);
                emergency.setEmergency_id(dataSnapshot.getKey());
                for(DataSnapshot mDataSnapshot:dataSnapshot.getChildren()){
                    String adim=mDataSnapshot.getKey().toString();
                    for(DataSnapshot mDataSnapshot2:dataSnapshot.child(adim).getChildren()) {
                        emergency.setMessage(mDataSnapshot2.child("message").getValue().toString());
                        mDatabaseReference2.child("Driver").child(mDataSnapshot2.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Log.d("ADebugTag", dataSnapshot.getValue().toString());
                                if (dataSnapshot.exists()) {
                                    Driver driver = dataSnapshot.getValue(Driver.class);
                                    driver.setDriverID(dataSnapshot.getKey());
                                    emergency.setDriver(driver);
                                    adapter.add(emergency);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        progressBar.dismiss();

                    }}}

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try{
                final String ID = dataSnapshot.getKey().toString();
                final Emergency emergency = dataSnapshot.getValue(Emergency.class);
                emergency.setEmergency_id(dataSnapshot.getKey());
                    for(DataSnapshot mDataSnapshot:dataSnapshot.getChildren()) {
                        String adim = mDataSnapshot.getKey().toString();
                        for (DataSnapshot mDataSnapshot2 : dataSnapshot.child(adim).getChildren()) {
                            emergency.setMessage(mDataSnapshot2.child("message").getValue().toString());
                            mDatabaseReference2.child("Driver").child(mDataSnapshot2.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Driver driver = dataSnapshot.getValue(Driver.class);
                                    driver.setDriverID(dataSnapshot.getKey());
                                    emergency.setDriver(driver);
                                    emergencyList.add(getIndext(ID), emergency);
                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                    }}
                catch (Exception e){
                    Log.d("SmartBus", "Value: "+"error");
                }
                progressBar.dismiss();
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String ID = dataSnapshot.getKey();
                try{
                    for(Emergency p: emergencyList){
                        if(ID.equals(p.getEmergency_id())){
                            emergencyList.remove(p);
                        }
                    }}catch (Exception e){
                    Log.d("SmartBus", "Value: "+"error");
                }
                adapter.notifyDataSetChanged();
                progressBar.dismiss();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("databaseError", "postComments:onCancelled", databaseError.toException());
                Toast.makeText(EmergencyList.this, "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
                progressBar.dismiss();

            }

    }; mDatabaseReference.addChildEventListener(mChildEventListener);
    }

    public int getIndext(String ID){
        int i=0;
        for(Emergency p: emergencyList){
            if(ID.equals(p.getEmergency_id())){
                i= emergencyList.indexOf(p);
                emergencyList.remove(p);
            }
        }
        return i;
    }
}
