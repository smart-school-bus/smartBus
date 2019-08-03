package com.project.smartbus10;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ComplaintsActivity extends AppCompatActivity {
    //
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReference2;
    private SharedPreferences sp;

    //
    private Complaints complaints;
    private String driverID;
    private String parentID;
    private EditText busID;
    private EditText parentName;
    private EditText description;
    private TextView text1;
    private TextView text2;
    private Button addcomplaintsB;
    private ImageButton delete;
    private ProgressDialog progressBar;
    private Intent i;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mDatabaseReference2 = mFirebaseDatabase.getReference();
        sp = getSharedPreferences("SignIn", MODE_PRIVATE);
        progressBar = new ProgressDialog(this);
        progressBar.setMessage(getString(R.string.p));
        complaints = new Complaints();
        busID = findViewById(R.id.busID);
        parentName = findViewById(R.id.parentNameE);
        text1 = findViewById(R.id.busIDT);
        text2 = findViewById(R.id.parentNameText);
        description = findViewById(R.id.com);
        delete=findViewById(R.id.delete);
        addcomplaintsB = findViewById(R.id.addcomplains);
        progressBar.show();
        if (sp.getString("user_type", "").equals("SchoolAdministration")) {
            addcomplaintsB.setVisibility(View.GONE);
            parentName.setVisibility(View.VISIBLE);
            text2.setVisibility(View.VISIBLE);
            text1.setText(R.string.driverN);
            description.setEnabled(false);
            busID.setEnabled(false);
            text2.setEnabled(false);
            parentName.setEnabled(false);
            delete.setVisibility(View.VISIBLE);
            i = getIntent();
            String complaintID = (String) i.getSerializableExtra("ComplaintsID");
            try {
                getComplaint(complaintID);
               ;
            }catch (Exception e){
                progressBar.dismiss();
                Toast.makeText(ComplaintsActivity.this, R.string.comp_mass, Toast.LENGTH_SHORT).show();
            }


        } else if (sp.getString("user_type", "").equals("Parent")) {
            delete.setVisibility(View.GONE);
            addcomplaintsB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (verifyInput(busID, description)) {
                        try {
                            complaints.setDescription(description.getText().toString());
                            getDriverID();
                        }catch (Exception e){
                            progressBar.dismiss();
                            Toast.makeText(ComplaintsActivity.this, R.string.comp_erro, Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            });
        }
         mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(sp.getString("user_type","").equals("SchoolAdministration")){
                     Intent goToItemListPage=new Intent(ComplaintsActivity.this, ItemList.class);
                     goToItemListPage.putExtra("ListType","Complaints");
                     startActivity(goToItemListPage);
                 }
                 else if(sp.getString("user_type","").equals("Parent")){
                     Intent goToHomePage=new Intent(ComplaintsActivity.this,Home.class);
                     startActivity(goToHomePage);
                 }
             }
         });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.show();
                mDatabaseReference.child("Complaints").child((String) i.getSerializableExtra("ComplaintsID")).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            dataSnapshot.getRef().removeValue();
                            Intent goToItemListPage=new Intent(ComplaintsActivity.this, ItemList.class);
                            goToItemListPage.putExtra("ListType","Complaints");
                            startActivity(goToItemListPage);
                            progressBar.dismiss();
                        } else {  // error in password
                            progressBar.dismiss();
                            Toast.makeText(ComplaintsActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }});


    }}); progressBar.dismiss();}

    private void showComplaints() {
        text1.setText(R.string.driverN);
        text2.setText(R.string.parentN);
        description.setText(complaints.getDescription());
        if(complaints.getParent()!=null)
        {parentName.setText(complaints.getParent().getFirstName()+ " "+complaints.getParent().getLastName());}
        else
            parentName.setText("");
        if(complaints.getDriver()!=null)
        { busID.setText(complaints.getDriver().getFirstName() + " " + complaints.getDriver().getLastName());}
        else
            busID.setText( " " );
        progressBar.dismiss();
    }


    private void addComplaints() {
        final String id=mDatabaseReference.child("Complaints").push().getKey().toString();
        mDatabaseReference.child("Complaints").child(id).setValue(complaints).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mDatabaseReference.child("Complaints").child(id).child("driverID").setValue(driverID);
                    mDatabaseReference.child("Complaints").child(id).child("parentID").setValue(sp.getString("ID", ""));
                    busID.setText("");
                    description.setText("");
                    Toast.makeText(ComplaintsActivity.this, R.string.su, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ComplaintsActivity.this, R.string.unsu, Toast.LENGTH_SHORT).show();
                }
            }
        });
        progressBar.dismiss();
    }




    private void getDriverID() {
        mDatabaseReference2.child("Bus").child(busID.getText().toString().trim()).child("driverID").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    driverID =dataSnapshot.getValue().toString();
                    addComplaints();
                } else {
                    complaints.setDriver(null);
                    busID.setError(getString(R.string.errorbus));
                    busID.requestFocus();
                    progressBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getDriverInfo() {
        mDatabaseReference2.child("Driver").child(driverID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    complaints.setDriver(dataSnapshot.getValue(Driver.class));
                    showComplaints();
                } else {
                    Toast.makeText(ComplaintsActivity.this, "Driver account deleted", Toast.LENGTH_SHORT).show();
                    showComplaints();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }
    public void getParentInfo() {
        mDatabaseReference2.child("Parent").child(parentID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                complaints.setParent(dataSnapshot.getValue(Parent.class));
                   getDriverInfo();}
                   else{
                    Toast.makeText(ComplaintsActivity.this,"Parent  account deleted", Toast.LENGTH_SHORT).show();
                    getDriverInfo();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }

    public void getComplaint(String complaintID) {

        mDatabaseReference.child("Complaints").child(complaintID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                   if(dataSnapshot.exists()){
                        complaints.setDescription(dataSnapshot.child("description").getValue().toString());
                        driverID =dataSnapshot.child("driverID").getValue().toString();
                        parentID =dataSnapshot.child("parentID").getValue().toString();
                      getParentInfo();}
                   else
                   { Toast.makeText(ComplaintsActivity.this, R.string.comp_mass, Toast.LENGTH_SHORT).show();
                    progressBar.dismiss();}

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public boolean verifyInput(EditText busID, EditText description) {
        if (busID.getText().toString().isEmpty()) {
            busID.setError(getString(R.string.error_field_required));
            busID.requestFocus();
            progressBar.dismiss();
            return false;
        }
        if (description.getText().toString().isEmpty()) {
            description.setError(getString(R.string.error_field_required));
            description.requestFocus();
            progressBar.dismiss();
            return false;
        }
        return true;
    }

}
