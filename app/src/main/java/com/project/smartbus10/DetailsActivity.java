package com.project.smartbus10;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DetailsActivity extends AppCompatActivity {
    //
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReference2;
    private SharedPreferences sp;

    //
    private Intent i;
    private String listType;
    private String idItem;
    private String profile;
    private String busID;
    private Driver driver;
    private Student student;
    private SchoolAdministration admin;
    private Parent parent;

    //
    private Toolbar mToolbar;
    private ImageButton delete;
    private TextView name;
    private TextView id;
    private TextView fullName;
    private TextView tag, tagText;
    private TextView parentId, parentIdText;
    private TextView phone, phoneText;
    private TextView busId, busIdText;
    private TextView busStopId, busStopIdText;
    private TextView attendanceText;
    private TextView state, stateText;
    private TextView level, levelText;
    private TextView history, historyText;
    private Switch attendance;
    private Button goToListChildren;
    private ImageButton showMap;
    private ImageView profileImage;
    private Button edit;
    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        //
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(mToolbar);
        //
        progressBar = new ProgressDialog(DetailsActivity.this);
        progressBar.setMessage(getString(R.string.p));
        progressBar.show();
        //
        isOnline();
        //
        i = getIntent();
        listType = (String) i.getSerializableExtra("ListType");
        idItem = (String) i.getSerializableExtra("idItem");
        profile = (String) i.getSerializableExtra("Profile");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child(listType);
        mDatabaseReference2 = mFirebaseDatabase.getReference();
        sp = getSharedPreferences("SignIn", MODE_PRIVATE);
        //
        name = findViewById(R.id.name1);
        fullName = findViewById(R.id.name);
        id = findViewById(R.id.id);
        tag = findViewById(R.id.tagId);
        tagText = findViewById(R.id.tagIdText);
        parentId = findViewById(R.id.parentId);
        parentIdText = findViewById(R.id.parentIdText);
        phone = findViewById(R.id.phoneNm);
        phoneText = findViewById(R.id.phone_text);
        busId = findViewById(R.id.busId);
        busIdText = findViewById(R.id.busIdText);
        busStopId = findViewById(R.id.busStopId);
        busStopIdText = findViewById(R.id.busStopIdText);
        attendanceText = findViewById(R.id.attendanceText);
        state = findViewById(R.id.state);
        stateText = findViewById(R.id.stateText);
        level = findViewById(R.id.level);
        levelText = findViewById(R.id.levelText);
        history = findViewById(R.id.history);
        historyText = findViewById(R.id.historyText);
        attendance = findViewById(R.id.attendance);
        goToListChildren = findViewById(R.id.go_to_list_ch);
        showMap = findViewById(R.id.b_map);
        profileImage = findViewById(R.id.imageView2);
        delete = (ImageButton) findViewById(R.id.delete);
        edit= findViewById(R.id.edit_b);
        getInfo();

        //
        if (sp.getString("user_type", "").equals("SchoolAdministration") && profile == null) {
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);
                    builder.setMessage(R.string.delete_mas);
                    builder.setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @SuppressLint("NewApi")
                                public void onClick(DialogInterface dialog, int which) {
                                    if (listType.equals("Parent")) {
                                        Log.d("ADebugTag", "Value: " + listType);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);
                                        builder.setMessage(R.string.pdelete_mas);
                                        builder.setPositiveButton("yes",
                                                new DialogInterface.OnClickListener() {
                                                    @SuppressLint("NewApi")
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        deleteStudents();
                                                        Intent goToListPage = new Intent(DetailsActivity.this, ItemList.class);
                                                        goToListPage.putExtra("ListType", listType);
                                                        startActivity(goToListPage);

                                                    }
                                                });
                                        builder.setNegativeButton("no",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,
                                                                        int which) {
                                                        deleteParentIdToS();
                                                        Intent goToListPage = new Intent(DetailsActivity.this, ItemList.class);
                                                        goToListPage.putExtra("ListType", listType);
                                                        startActivity(goToListPage);
                                                    }
                                                });
                                        builder.show();
                                    } else {
                                        deleteItem();
                                        if(listType.equals("Driver")){deleteDriverFromBus();}
                                        Intent goToListPage = new Intent(DetailsActivity.this, ItemList.class);
                                        goToListPage.putExtra("ListType", listType);
                                        startActivity(goToListPage);
                                    }


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

                }
            });
        } else delete.setVisibility(View.GONE);

        ///
        goToListChildren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToListPage = new Intent(DetailsActivity.this, ItemList.class);
                goToListPage.putExtra("ListType", "Student");
                goToListPage.putExtra("parentId", parent.getPatentID());
                startActivity(goToListPage);


            }
        });
        ////
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sp.getString("user_type","").equals("Parent")&&listType.equals("Student")){
                    changeAttendance();
                }
                else
                {Intent goToEditPage = new Intent(DetailsActivity.this, Registration.class);
                    goToEditPage.putExtra("ListType", listType);
                    if(listType.equals("Parent")){
                        goToEditPage.putExtra("item", parent);
                        Log.d("ADebugTag","d "+listType);

                    }
                    else if(listType.equals("Student")){
                        goToEditPage.putExtra("item",  student);
                        goToEditPage.putExtra("parent", parent);


                    }
                    else if(listType.equals("Driver")){
                        goToEditPage.putExtra("item",  driver);
                        goToEditPage.putExtra("busId", busID);
                        Log.d("ADebugTag",""+busID);

                    }
                    else if(listType.equals("SchoolAdministration")){
                        goToEditPage.putExtra("idItem",  admin.getAdminID());


                    }

                    if(profile!=null)
                    { goToEditPage.putExtra("Profile","Profile");}
                startActivity(goToEditPage);}
            }
        });

        // map
        showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToMap=new Intent(DetailsActivity.this, MapsActivityHomeAddress.class);
                if(parent!=null){
                    Log.d("geoLocate", "2" );
                    goToMap.putExtra("latitude",parent.getLatitude());
                    goToMap.putExtra("longitude",parent.getLongitude());
                    goToMap.putExtra("Address",parent.getAddress());
                    goToMap.putExtra("createMarker",false);
                    startActivity(goToMap);
                }
            }
        });
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (profile == null) {
                    Intent goToListPage = new Intent(DetailsActivity.this, ItemList.class);
                    goToListPage.putExtra("ListType", listType);
                    startActivity(goToListPage);
                } else {
                    Intent goToHomePage = new Intent(DetailsActivity.this, Home.class);
                    startActivity(goToHomePage);
                }

            }
        });


    }

    // get Info from
    private void getInfo() {
        mDatabaseReference.child(idItem).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("ADebugTag", dataSnapshot.getValue().toString());
                    if (listType.equals("Parent")) {
                        parent = dataSnapshot.getValue(Parent.class);
                        parent.setPatentID(idItem);
                        parentDetails(parent);

                    } else if (listType.equals("Student")) {
                        student = dataSnapshot.getValue(Student.class);
                        student.setStuID(idItem);
                        student.setBus(new Bus());
                        student.getBus().setID(dataSnapshot.child("busID").getValue().toString());
                        student.setBusStop(new BusStop());
                        student.getBusStop().setBusStopID(dataSnapshot.child("busID").getValue().toString());
                        String parentId = dataSnapshot.child("parentID").getValue().toString();
                        getParentPhone(parentId);


                        Log.d("ADebugTag", "3");
                    } else if (listType.equals("Driver")) {
                        driver = dataSnapshot.getValue(Driver.class);
                        driver.setDriverID(idItem);
                        getBusID(driver);
                    } else if (listType.equals("SchoolAdministration")) {
                        admin = dataSnapshot.getValue(SchoolAdministration.class);
                        admin.setAdminID(idItem);
                        adminDetails(admin);
                    }

                } else {  // error in password
                    progressBar.dismiss();
                    Toast.makeText(DetailsActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (databaseError != null) {
                    progressBar.dismiss();
                    Toast.makeText(DetailsActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    //
    private void getStStatusInfo() {
        Query fireQuery = mDatabaseReference2.child("StudentTimeNote").orderByChild("studentID").equalTo(student.getStuID());
        fireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                        StudentTimeNote stuNot = (mDataSnapshot.getValue(StudentTimeNote.class));
                        studentDetails(stuNot);
                    }
                } else studentDetails(null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    //
    public void getBusID(final Driver driver) {
        Query fireQuery = mDatabaseReference2.child("Bus").orderByChild("driverID").equalTo(driver.getDriverID());
        fireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                        busID=mDataSnapshot.getKey().toString();
                        Log.d("ADebugTag",busID);
                        driverDetails(driver,busID );
                    }
                } else driverDetails(driver, " ");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //
    public void getParentPhone(final String parentID) {

        mDatabaseReference2.child("Parent").child(parentID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    parent = dataSnapshot.getValue(Parent.class);
                    parent.setPatentID(parentID);
                    getStStatusInfo();
                } else
                    getStStatusInfo();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //
    private void adminDetails(SchoolAdministration admin) {
        if (admin != null) {
            name.setText(admin.getFirstName() + " " + admin.getLastName());
            fullName.setText(admin.getFirstName() + " " + admin.getSecondName() + " " + admin.getLastName());

            id.setText(admin.getAdminID());
            tagText.setText(R.string.ph);
            tag.setText(admin.getPhone());
            parentIdText.setText(R.string.pas);
            parentId.setText(admin.getPassword().replaceAll("^[a-zA-Z0-9_.-]*$", "******"));
            Log.d("ADebugTag", "" + parentId.getMarqueeRepeatLimit());
            showMap.setVisibility(View.GONE);
            phoneText.setVisibility(View.GONE);
            phone.setVisibility(View.GONE);
            busIdText.setVisibility(View.GONE);
            busId.setVisibility(View.GONE);
            busStopId.setVisibility(View.GONE);
            busStopIdText.setVisibility(View.GONE);
            attendance.setVisibility(View.GONE);
            attendanceText.setVisibility(View.GONE);
            levelText.setVisibility(View.GONE);
            level.setVisibility(View.GONE);
            stateText.setVisibility(View.GONE);
            state.setVisibility(View.GONE);
            historyText.setVisibility(View.GONE);
            history.setVisibility(View.GONE);
            profileImage.setImageResource(R.drawable.parent_);
            goToListChildren.setVisibility(View.GONE);
            progressBar.dismiss();
        } else {
            progressBar.dismiss();
            Toast.makeText(DetailsActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }

    }
    //
    @SuppressLint("ResourceAsColor")
   public void studentDetails(StudentTimeNote stuNot) {
        if (student != null) {
            name.setText(student.getFirstName() + " " + student.getLastName());
            fullName.setTextSize(12);
            id.setText(student.getStuID());
            tag.setText(student.getTag());
            busId.setText(student.getBus().toString());
            busStopId.setText(student.getBusStop().toString());
            level.setText(student.getLevel());
            goToListChildren.setVisibility(View.GONE);
            showMap.setVisibility(View.GONE);
            profileImage.setImageResource(R.drawable.student_);
            if(sp.getString("user_type","").equals("Parent")){
            attendance.setEnabled(true);
                edit.setText("Save");
            }
            else { attendance.setEnabled(false);}

        if (student.getStuAttendance() == true) {
                attendance.setChecked(true);
            } else attendance.setChecked(false);

            if (parent != null) {
                fullName.setText(student.getFirstName() + " " + parent.getSecondName() + " " + student.getLastName());
                parentId.setText(parent.getPatentID());
                phone.setText(parent.getPhone());
            } else fullName.setText(student.getFirstName() + " " + student.getLastName());
            if (stuNot != null) {
                state.setText(stuNot.getState());
                history.setText(stuNot.toString());
            }
            progressBar.dismiss();

        } else {
            progressBar.dismiss();
            Toast.makeText(DetailsActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }


    }
    //
   public void parentDetails(Parent parent) {
        if (parent != null) {
            name.setText(parent.getFirstName() + " " + parent.getLastName());
            fullName.setText(parent.getFirstName() + " " + parent.getSecondName() + " " + parent.getLastName());
            id.setText(parent.getPatentID());
            if (profile != null) {
                tagText.setVisibility(View.GONE);
                goToListChildren.setVisibility(View.GONE);
            } else {
                tagText.setText(R.string.show);
                goToListChildren.setVisibility(View.VISIBLE);
            }
            tag.setVisibility(View.GONE);
            parentIdText.setText(R.string.address);
            parentId.setText(parent.getAddress());
            parentId.setTextSize(12);
            showMap.setVisibility(View.VISIBLE);
            showMap.setEnabled(true);
            phone.setText(parent.getPhone());
            busIdText.setText(R.string.pas);
            busId.setText(parent.getPassword().replaceAll("^[a-zA-Z0-9_.-]*$", "******"));
            busStopId.setVisibility(View.GONE);
            busStopIdText.setVisibility(View.GONE);
            attendance.setVisibility(View.GONE);
            attendanceText.setVisibility(View.GONE);
            levelText.setVisibility(View.GONE);
            level.setVisibility(View.GONE);
            stateText.setVisibility(View.GONE);
            state.setVisibility(View.GONE);
            historyText.setVisibility(View.GONE);
            history.setVisibility(View.GONE);

            profileImage.setImageResource(R.drawable.parent_);
            progressBar.dismiss();
        } else {
            progressBar.dismiss();
            Toast.makeText(DetailsActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }
    //
   public void driverDetails(Driver driver, String busID) {
        if (driver != null) {
            name.setText(driver.getFirstName() + " " + driver.getLastName());
            fullName.setText(driver.getFirstName() + " " + driver.getSecondName() + " " + driver.getLastName());
            id.setText(driver.getDriverID());
            tagText.setVisibility(View.GONE);
            tag.setVisibility(View.GONE);
            parentIdText.setVisibility(View.GONE);
            parentId.setVisibility(View.GONE);
            showMap.setVisibility(View.GONE);
            phoneText.setText(R.string.ph);
            phone.setText(driver.getPhone());
            busIdText.setText(R.string.bu_id);
            busId.setText(busID);
            busStopIdText.setText(R.string.pas);
            busStopId.setText(driver.getPassword().replaceAll("^[a-zA-Z0-9_.-]*$", "******"));
            attendance.setVisibility(View.GONE);
            attendanceText.setVisibility(View.GONE);
            levelText.setVisibility(View.GONE);
            level.setVisibility(View.GONE);
            stateText.setVisibility(View.GONE);
            state.setVisibility(View.GONE);
            historyText.setVisibility(View.GONE);
            history.setVisibility(View.GONE);
            goToListChildren.setVisibility(View.GONE);
            profileImage.setImageResource(R.drawable.driverp);
            progressBar.dismiss();
        } else {
            progressBar.dismiss();
            Toast.makeText(DetailsActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }

    }
    //
   public void deleteItem() {
        mDatabaseReference.child(idItem).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    dataSnapshot.getRef().removeValue();
                } else {  // error in password
                    progressBar.dismiss();
                    Toast.makeText(DetailsActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }
    //
   private void deleteStudents() {
        Query stuQuery = mDatabaseReference2.child("Student").orderByChild("parentID").equalTo(idItem);

        stuQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot mSnapshot : dataSnapshot.getChildren()) {
                        mSnapshot.getRef().removeValue();

                    }
                }
                deleteItem();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("s", "onCancelled", databaseError.toException());
            }
        });
    }
    //
   private void deleteParentIdToS() {
        Query stuQuery = mDatabaseReference2.child("Student").orderByChild("parentID").equalTo(idItem);

        stuQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot mSnapshot : dataSnapshot.getChildren()) {
                        mDatabaseReference2.child("Student").child(mSnapshot.getKey()).child("parentID").setValue("");
                    }
                }
                deleteItem();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("s", "onCancelled", databaseError.toException());
            }
        });


    }
    private void deleteDriverFromBus() {
        Query stuQuery = mDatabaseReference2.child("Bus").orderByChild("driverID").equalTo(idItem);

        stuQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot mSnapshot : dataSnapshot.getChildren()) {
                        mDatabaseReference2.child("Bus").child(mSnapshot.getKey()).child("driverID").setValue("");
                    }
                }
                deleteItem();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("s", "onCancelled", databaseError.toException());
            }
        });


    }
    //
   private void changeAttendance(){
        if(student!=null){
       mDatabaseReference2.child("Student").child(student.getStuID()).child("stuAttendance").setValue(attendance.isChecked()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete( Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(DetailsActivity.this, R.string.su, Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(DetailsActivity.this, R.string.unsu , Toast.LENGTH_SHORT).show();
                    }
                }
            });}else  Toast.makeText(DetailsActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
}
    // Check your internet connection
   public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            Toast.makeText(this, "No Internet connection!", Toast.LENGTH_LONG).show();
            progressBar.dismiss();
            return false;
        }
        return true;
    }
}