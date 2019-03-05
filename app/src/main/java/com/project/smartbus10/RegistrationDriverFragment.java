package com.project.smartbus10;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RegistrationDriverFragment extends Fragment {
    //
   private FirebaseDatabase mFirebaseDatabase;
   private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReferenceBus;

   private Driver driver;
   //
    EditText fName;
    EditText sName;
    EditText lName;
    EditText phNum;
    Spinner busNum;
    //
    List<String> busIDList;
    String busID;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_registration_driver_fragment, container, false);
        fName= view.findViewById(R.id.f_name);
        sName= view.findViewById(R.id.s_name);
        lName= view.findViewById(R.id.l_name);
        phNum= view.findViewById(R.id.ph_num);
        busNum=view.findViewById(R.id.bus_num);
        Button image=view.findViewById(R.id.profile_image);
        Button createAcc=view.findViewById(R.id.create_acc);
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseReferenceBus=mFirebaseDatabase.getReference();
        GetBusesID();
        busNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                busID=adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                busID= (String) adapterView.getItemAtPosition(1);
            }
        });
        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(VerifyInput()){ driver= new Driver();
                mDatabaseReference=mFirebaseDatabase.getReference().child("Driver");
                driver.setFirstName(""+fName.getText());fName.setText("");
                driver.setSecondName(""+sName.getText());sName.setText("");
                driver.setLastName(""+lName.getText());lName.setText("");
                driver.setPhone(""+phNum.getText());phNum.setText("");
                final String id = mDatabaseReference.push().getKey();
                    mDatabaseReference.child(id).setValue(driver);
                    mDatabaseReferenceBus.child("Bus").child(busID).child("driverID").setValue(id);
                }
            }
        });
        return view;}

   public void GenerateUsernamePassword(){
       Date date = new Date();
       SimpleDateFormat sdf = new SimpleDateFormat("yyMdkm");
       driver.setUserName("D"+sdf.format(date));
       sdf = new SimpleDateFormat("hh:mm:ss");
       driver.setPassword(sdf.format(date));
   }
   public boolean VerifyInput(){
        if(fName.getText().toString().isEmpty()){
            fName.setError(getString(R.string.error_field_required));
            fName.requestFocus();
            return false;
        }
       if(sName.getText().toString().isEmpty()){
           sName.setError(getString(R.string.error_field_required));
           sName.requestFocus();
           return false;
       }
       if(lName.getText().toString().isEmpty()){
           lName.setError(getString(R.string.error_field_required));
           lName.requestFocus();
           return false;
       }
       if(phNum.getText().toString().isEmpty()){
           phNum.setError(getString(R.string.error_field_required));
           phNum.requestFocus();
           return false;
       }

       return true;
   }

   public void GetBusesID(){
       busIDList=new ArrayList<>();
       mDatabaseReferenceBus.child("Bus").addListenerForSingleValueEvent(new ValueEventListener() {
           @Override

           public void onDataChange(DataSnapshot dataSnapshot) {
               for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                   busIDList.add((String)mDataSnapshot.getKey());
                   final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_row,busIDList);
                   busNum.setAdapter(adapter);}
               }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }


       });


       }



}