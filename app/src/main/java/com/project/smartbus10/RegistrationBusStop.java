package com.project.smartbus10;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RegistrationBusStop extends AppCompatActivity {
    //
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReferenceS;
    private FirebaseStorage storage ;
    private StorageReference audioRef;

    //
    private EditText stopName;
    private EditText order;
    private EditText voice;
    private ImageView location;
    private ImageView voice_path;
    private ImageView delete;
    private Button save;
    private ProgressDialog progressBar;
    //
    // Used when user select audio file.
    private static final int REQUEST_CODE_SELECT_AUDIO_FILE = 1;

    // Used when user require android READ_EXTERNAL_PERMISSION.
    private static final int REQUEST_CODE_READ_EXTERNAL_PERMISSION = 2;
    // Save user selected or inputted audio file unique resource identifier.
    private Uri audioFileUri = null;


    //
    private BusStop busStop;
    private String busStopID;
    private String busID;
    private boolean update;
    //
    private Intent i;
    private boolean exist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_bus_stop);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(mToolbar);
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        i=getIntent();
        update=true;
        busID = (String) i.getSerializableExtra("busId");
        busStop=(BusStop) i.getSerializableExtra("BusStop");
        update=(boolean)i.getSerializableExtra("update");
        audioRef = storage.getReference().child("audio").child(busID);
        mDatabaseReference=mFirebaseDatabase.getReference();
        mDatabaseReferenceS=mFirebaseDatabase.getReference();

        progressBar=new ProgressDialog(this);
        progressBar.setMessage(getString(R.string.p));
        stopName=findViewById(R.id.stop_n);
        order=findViewById(R.id.stop_or);
        voice=findViewById(R.id.voice);
        location=findViewById(R.id.b_map);
        voice_path=findViewById(R.id.voice_path);
        save=findViewById(R.id.insert);
        delete=findViewById(R.id.delete);

        if(update==false){
          showInfo();
          delete.setVisibility(View.VISIBLE);
        }
        else
        { busStop=new BusStop();
          delete.setVisibility(View.GONE);}
        busStop.setBusStopAddress(stopName.getText().toString());



        //
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToMap=new Intent(RegistrationBusStop.this, BusStopsMapsActivity.class);
                    goToMap.putExtra("busId",busID);
                    if(busStop!=null){
                        goToMap.putExtra("latitude", busStop.getBusStopLatitude());
                        goToMap.putExtra("longitude", busStop.getBusStopLongitude());
                        goToMap.putExtra("stopName", busStop.getBusStopAddress());
                    }
                startActivity(goToMap);

            }
        });
        //
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationBusStop.this);
                builder.setMessage(R.string.delete_mas);
                builder.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @SuppressLint("NewApi")
                            public void onClick(DialogInterface dialog, int which) {
                                updateStudentsInfo();
                                Intent goToListPage = new Intent(RegistrationBusStop.this, BusDetailsActivity.class);
                                goToListPage.putExtra("busId", busID);
                                startActivity(goToListPage);
                            }


                        });builder.setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                getClass();
                            }
                        });
                builder.show();}
            }
        );
        //
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.show();
                generateId();
                if(BusStopsMapsActivity.latitude!=0){
                    busStop.setBusStopLatitude(BusStopsMapsActivity.latitude);
                    busStop.setBusStopLongitude(BusStopsMapsActivity.longitude);}
                    if(BusStopsMapsActivity.stopName!=null){
                        busStop.setBusStopAddress(BusStopsMapsActivity.stopName);
                        stopName.setText(busStop.getBusStopAddress());
                    }

                if(verifyInput()){
                    busStop.setBusStopOrder(Integer.parseInt(order.getText().toString()));
                    if(update==false){

                        updateBusStopOrder();
                       }
                    else
                    {
                        checkExist();}
                    }
                    progressBar.dismiss();
                }


        });

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToListPage = new Intent(RegistrationBusStop.this, BusDetailsActivity.class);
                goToListPage.putExtra("busId",busID);
                startActivity(goToListPage);
                      finish();}

        });


                /* Click this button to popup select audio file component. */

        voice_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Require read external storage permission from user.
                int readExternalStoragePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                if(readExternalStoragePermission != PackageManager.PERMISSION_GRANTED)
                {
                    String requirePermission[] = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    ActivityCompat.requestPermissions(RegistrationBusStop.this, requirePermission, REQUEST_CODE_READ_EXTERNAL_PERMISSION);
                }else {
                    selectAudioFile();
                }
            }
        });



    }

    private void showInfo() {
        stopName.setText(busStop.getBusStopAddress());
        order.setText(busStop.getBusStopOrder()+"");
        voice.setText(busStop.getLinkAudio());
    }

    public void generateId(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dkms");
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
        }if(busStop.getBusStopLatitude()==0||busStop.getBusStopLongitude()==0){
            stopName.setError(getString(R.string.error_field_required));
            stopName.requestFocus();
            progressBar.dismiss();
            return false;
        }
        return true;
    }

    public void checkExist(){
        exist=false;
        Query fireQuery=mDatabaseReference.child("BusStop").orderByChild("busID").equalTo(busID);
        fireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                    if(busStop.getBusStopLatitude()==((double) mDataSnapshot.child("busStopLatitude").getValue())){
                        if(busStop.getBusStopLongitude()==(double) mDataSnapshot.child("busStopLongitude").getValue()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationBusStop.this);
                            Driver driver1 =dataSnapshot.getValue(Driver.class);
                            driver1.setDriverID(dataSnapshot.getKey());
                            builder.setTitle(R.string.bus_st_err);
                            builder.setMessage('\n'+mDataSnapshot.getKey().toString()+'\n'+mDataSnapshot.child("busStopName").getValue());
                            builder.setPositiveButton(R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        @SuppressLint("NewApi")
                                        public void onClick(DialogInterface dialog,int which) {
                                            getClass();
                                            exist=true;
                                        }
                                    });builder.show(); break;
                        }
                    }
                } if(exist==false){
                    insertBusStop();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if(databaseError!=null){
                    progressBar.dismiss();
                    Toast.makeText(RegistrationBusStop.this, R.string.error_database, Toast.LENGTH_SHORT).show();}

            }
        });

    }

    public  void insertBusStop(){
        mDatabaseReference.child("BusStop").child(busStopID).setValue(busStop).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete( Task<Void> task) {
                if (task.isSuccessful()){
                    uploadAudio(busStopID);
                    order.setText("");
                    stopName.setText("");
                    voice.setText("");
                    BusStopsMapsActivity.latitude = 0;
                    BusStopsMapsActivity.longitude = 0;
                    BusStopsMapsActivity.stopName = null;
                    Toast.makeText(RegistrationBusStop.this, R.string.su, Toast.LENGTH_SHORT).show();
                    mDatabaseReference.child("BusStop").child(busStopID).child("busID").setValue(busID);

                } else {
                    progressBar.dismiss();
                    Toast.makeText(RegistrationBusStop.this, R.string.unsu, Toast.LENGTH_SHORT).show();
                }
            }
        });;

    }
    private void update(){
        busStopID=busStop.getBusStopID();
        busStop.setBusStopID(null);
        if(busStopID!=null)
        { mDatabaseReference.child("BusStop").child(busStopID).setValue(busStop).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mDatabaseReference.child("BusStop").child(busStopID).child("busID").setValue(busID);
                    uploadAudio(busStopID);
                    Toast.makeText(RegistrationBusStop.this, R.string.su, Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(RegistrationBusStop.this, R.string.unsu, Toast.LENGTH_SHORT).show();
                } busStop.setBusStopID(busStopID);

            }});}progressBar.dismiss();

    }

    private void uploadAudio(final String busStopID) {
        if(audioFileUri!=null) {
            audioRef= storage.getReference().child("audio").child(busID).child(busStopID);
            audioRef.putFile(audioFileUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return audioRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downUri = task.getResult();
                        busStop.setLinkAudio(downUri.toString());
                        mDatabaseReference.child("BusStop").child(busStopID).child("linkAudio").setValue(busStop.getLinkAudio());

                    }
                }
            });
            //  Log.d("ADebugTag", "audioFileName"+audioRef.getDownloadUrl().getResult());

        }
    }

    /* This method start get content activity to let user select audio file from local directory.*/
    private void selectAudioFile()
    {
        // Create an intent with ACTION_GET_CONTENT.
        Intent selectAudioIntent = new Intent(Intent.ACTION_GET_CONTENT);

        // Show audio in the content browser.
        // Set selectAudioIntent.setType("*/*") to select all data
        // Intent for this action must set content type, otherwise android.content.ActivityNotFoundException: No Activity found to handle Intent { act=android.intent.action.GET_CONTENT } will be thrown
        selectAudioIntent.setType("audio/*");

        // Start the activity.
        startActivityForResult(selectAudioIntent, REQUEST_CODE_SELECT_AUDIO_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_SELECT_AUDIO_FILE)
        {
            if(resultCode==RESULT_OK)
            {
                // To make example simple and clear, we only choose audio file from
                // local file, this is easy to get audio file real local path.
                // If you want to get audio file real local path from a audio content provider
                // Please read another article.
                audioFileUri = data.getData();
                voice.setText(audioFileUri+"");
                String audioFileName = audioFileUri.getLastPathSegment();



            }
        }
    }
    public void updateStudentsInfo(){
        Query deleteQuery=mDatabaseReferenceS.child("Student").orderByChild("busStopID").equalTo(busStop.getBusStopID());
        deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot mDataSnapshot:dataSnapshot.getChildren()){
                        mDatabaseReferenceS.child("Student").child(mDataSnapshot.getKey()).child("busStopID").setValue("");
                    }
                   deleteAudio();
                }else    deleteAudio();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void deleteAudio() {
        if(busStop.getLinkAudio()!=null){
        StorageReference audioRef = storage.getReferenceFromUrl(busStop.getLinkAudio());

            audioRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                deleteBusStop();
                // File deleted successfully
                Toast.makeText(RegistrationBusStop.this, "Succeed", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Toast.makeText(RegistrationBusStop.this, "Unsucceed", Toast.LENGTH_SHORT).show();
            }
        });}else  deleteBusStop();
    }

    private void deleteBusStop() {
        mDatabaseReference.child("BusStop").child(busStop.getBusStopID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot mSnapshot : dataSnapshot.getChildren()) {
                        mSnapshot.getRef().removeValue();
                        updateDeleteOrder();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }});
    }

    private void updateDeleteOrder(){
        Query updateOrder=mDatabaseReference.child("BusStop").orderByChild("busID").equalTo(busID);
        updateOrder.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot mDataSnapshot:dataSnapshot.getChildren()){
                            if(Integer.parseInt(mDataSnapshot.child("busStopOrder").getValue().toString())>=busStop.getBusStopOrder()){
                                mDatabaseReference.child("BusStop").child(mDataSnapshot.getKey()).child("busStopOrder").setValue(Integer.parseInt(mDataSnapshot.child("busStopOrder").getValue().toString())-1);
                    }
                }

            }}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void updateBusStopOrder(){
        mDatabaseReference.child("BusStop").child(busStop.getBusStopID()).child("busStopOrder").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if(busStop.getBusStopOrder()!=Integer.parseInt(dataSnapshot.getValue().toString())){
                        Query updateOrder=mDatabaseReference.child("BusStop").orderByChild("busID").equalTo(busID);
                        updateOrder.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    for(DataSnapshot mDataSnapshot:dataSnapshot.getChildren()){
                                        if(Integer.parseInt(mDataSnapshot.child("busStopOrder").getValue().toString())>=busStop.getBusStopOrder()){
                                            mDatabaseReference.child("BusStop").child(mDataSnapshot.getKey()).child("busStopOrder").setValue(Integer.parseInt(mDataSnapshot.child("busStopOrder").getValue().toString())+1);
                                        }
                                    }

                                } update();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    } else update();

                } else update();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onResume() {
        if(BusStopsMapsActivity.stopName!=null)
        {stopName.setText((BusStopsMapsActivity.stopName));}
        super.onResume();
    }
    @Override
    public void onBackPressed() {
        finish();
            super.onBackPressed();

        }

}
