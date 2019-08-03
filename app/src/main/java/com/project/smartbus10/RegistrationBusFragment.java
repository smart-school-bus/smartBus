package com.project.smartbus10;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class RegistrationBusFragment extends Fragment {
    //
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReference2;
    private SharedPreferences sp;

    //
    private Bus bus;
    private String busId;
    private String driverID;
    //
    private EditText plate;
    private Spinner driverSpinner;
    private Button save;
    private List<String>driverList;
    private ProgressDialog progressBar;

    //
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.registration_bus_fragment, container, false);
        progressBar=new ProgressDialog(getActivity());
        progressBar.setMessage(getString(R.string.p));
        plate=view.findViewById(R.id.plate);
        driverSpinner=view.findViewById(R.id.driverID);
        save=view.findViewById(R.id.save);
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseReference=mFirebaseDatabase.getReference().child("Bus");
        mDatabaseReference2=mFirebaseDatabase.getReference().child("Driver");
        sp = getActivity().getSharedPreferences("SignIn",MODE_PRIVATE);

        getBusesID();
        bus=new Bus();
        driverID="";
        driverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                driverID=adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                driverID="";
            }
        });
        //
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.show();
                generateId();
                bus.setPlate(plate.getText().toString());
                bus.setBusLatitude(0);
                bus.setBusLatitude(0);
                bus.setStudentsNumber(0);
                if(verifyInput()){ checkExist();}
            }
        });

        return view;}

    public void getBusesID(){
        driverList=new ArrayList<>();
        driverList.add("");
        mDatabaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                    driverList.add((String)mDataSnapshot.getKey());
                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_row,driverList);
                    driverSpinner.setAdapter(adapter); }
            }
            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        });


    }

    public void generateId(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMdkms");
        busId="Bus"+sdf.format(date);
    }
    public boolean verifyInput(){
        if(plate.getText().toString().isEmpty()||plate.getText().toString().equals("")){
            plate.setError(getString(R.string.error_field_required));
            plate.requestFocus();
            progressBar.dismiss();
            return false;
        }
        if(driverID.equals("")||driverID==null){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.bus_ma);
            builder.setPositiveButton(R.string.ok,
                    new DialogInterface.OnClickListener() {
                        @SuppressLint("NewApi")
                        public void onClick(DialogInterface dialog,int which) {
                            checkExist();
                        }
                    });
            builder.setNegativeButton(R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            getClass();

                        }

                    });
            builder.show();
            progressBar.dismiss();
            return false;
        }

        return true;
    }
    public void checkExist(){
        Query fireQuery = mDatabaseReference.orderByChild("plate").equalTo(plate.getText().toString().trim());
        fireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    Log.d("ADebugTag", "Value: " +dataSnapshot.getValue());
                    progressBar.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(getString(R.string.bus_exist));
                    builder.setPositiveButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @SuppressLint("NewApi")
                                public void onClick(DialogInterface dialog,int which) {
                                    plate.setText("");
                                    progressBar.dismiss();
                                    getClass();
                                }
                            });builder.show();

                } else {
                    saveBus();
                    progressBar.dismiss();
                }
            }
            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        });

    }

    private void saveBus() {
        bus.setBusLatitude(21.441129);
        bus.setBusLongitude(39.810661);
        mDatabaseReference.child(busId).setValue(bus).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete( Task<Void> task) {
                if (task.isSuccessful()){
                    mDatabaseReference.child(busId).child("driverID").setValue(driverID);
                    mDatabaseReference.child(busId).child("adminID").setValue(sp.getString("ID",""));
                    plate.setText("");
                    Toast.makeText(getActivity(), R.string.su, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.unsu, Toast.LENGTH_SHORT).show();
                }
            }
        });;

    }

}
