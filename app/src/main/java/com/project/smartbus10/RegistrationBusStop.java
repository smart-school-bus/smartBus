package com.project.smartbus10;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RegistrationBusStop extends AppCompatActivity {
    //
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    //
    private EditText stopName;
    private EditText order;
    private Button  location;
    private Button save;
    private ProgressDialog progressBar;

    //
    private BusStop busStop;
    private String busStopID;
    private String busID;
    //
    private Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_bus_stop);
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseReference=mFirebaseDatabase.getReference().child("BusStop");
        progressBar=new ProgressDialog(this);
        progressBar.setMessage(getString(R.string.p));
        stopName=findViewById(R.id.stop_n);
        order=findViewById(R.id.stop_or);
        location=findViewById(R.id.location);
        save=findViewById(R.id.insert);
        busStop=new BusStop();
        i=getIntent();
        busID = (String) i.getSerializableExtra("busID");
        //
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.show();
                generateId();
                if(verifyInput()){
                    checkExist();
                    progressBar.dismiss();
                }

            }
        });


    }

    public void generateId(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMdkms");
        busStopID="BS"+sdf.format(date);
    }

    public boolean verifyInput(){
        if(stopName.getText().toString().isEmpty()){
            stopName.setError(getString(R.string.error_field_required));
            stopName.requestFocus();
            progressBar.dismiss();
            return false;
        }
        if(order.getText().toString().isEmpty()){
            order.setError(getString(R.string.error_field_required));
            order.requestFocus();
            progressBar.dismiss();
            return false;
        }
        return true;
    }

    public void checkExist(){
    }

    public  void insertBusStop(){
        mDatabaseReference.child(busStopID).setValue(busStop).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete( Task<Void> task) {
                if (task.isSuccessful()){
                    order.setText("");
                    stopName.setText("");
                    Toast.makeText(RegistrationBusStop.this, R.string.su, Toast.LENGTH_SHORT).show();
                    mDatabaseReference.child("busID").setValue(busID);
                } else {
                    progressBar.dismiss();
                    Toast.makeText(RegistrationBusStop.this, R.string.unsu, Toast.LENGTH_SHORT).show();
                }
            }
        });;

    }
}
