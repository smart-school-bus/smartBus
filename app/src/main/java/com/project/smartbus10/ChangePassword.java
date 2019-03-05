package com.project.smartbus10;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangePassword extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    //Input field variables
    private EditText userName;
    private Button checkUser;
    //
    private String phoneNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        userName=findViewById(R.id.user_name);
        checkUser=findViewById(R.id.b_check);
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseReference=mFirebaseDatabase.getReference();
        checkUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkTheInput()){
                    CheckTheLoginMethod(userName.getText().toString());


                }
            }
        });



    }
    //Choose the input method using the userName or ID
    private void CheckTheLoginMethod(String userName) {
        userName=userName.trim();//Delete the spaces from the userName
        //Delete the spaces from the password
        if (userName.contains("A")){
            getPhoneUseUserName("SchoolAdministration",userName);
        }
        else if (userName.contains("P")){
            getPhoneUseUserName("Parent",userName);
        }
        else if (userName.contains("D")){
            getPhoneUseUserName("Driver",userName);
        }
        else{
            getPhoneUseID(userName);
        }
    }


    //  using your ID
    private void getPhoneUseID(final String id) {
        final String[]path={"SchoolAdministration","Parent","Driver"};
        for(int i=0;i<path.length;i++){
            final int finalI = i;
            final int finalI1 = i;
            mDatabaseReference.child(path[i]).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                        if(id.equals((String) mDataSnapshot.getKey())){// ID verification
                            phoneNum=mDataSnapshot.child("Phone").getValue().toString();
                            Toast.makeText(ChangePassword.this, userName+" GF"+phoneNum, Toast.LENGTH_SHORT).show();
                            open();
                            break;
                        }


                    }
                    if (phoneNum.isEmpty()){
                        Toast.makeText(ChangePassword.this, R.string.error_input, Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ChangePassword.this, R.string.error_database, Toast.LENGTH_SHORT).show();

                }
            });
        }

    }

    // sign In using your userName
    private void getPhoneUseUserName(final String path , final String userName) {
        mDatabaseReference.child(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                    if(userName.equals((String) mDataSnapshot.child("UserName").getValue())){//UserName verification
                        phoneNum=mDataSnapshot.child("Phone").getValue().toString();
                        open();
                        Toast.makeText(ChangePassword.this, userName+" GF"+phoneNum, Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                if (phoneNum.isEmpty()){
                    Toast.makeText(ChangePassword.this, R.string.error_input, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if(databaseError==null){
                    Toast.makeText(ChangePassword.this, R.string.error_database, Toast.LENGTH_SHORT).show();}

            }
        });
    }
    private boolean checkTheInput() {

        if (userName.getText().toString().isEmpty()) {
            userName.setError(getString(R.string.error_field_required));
            userName.requestFocus();
            return false;
        }
        return true;
    }
    private void open(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePassword.this);
        builder.setMessage("You will recieve the new password As SMS message to your phone number 05******"+phoneNum.substring(10));
        builder.setPositiveButton(R.string.send_m,
                new DialogInterface.OnClickListener() {
                    @SuppressLint("NewApi")
                    public void onClick(DialogInterface dialog,
                                        int which) {

                        finishAffinity();

                    }
                });
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {

                    }
                });
        builder.show();
    }


}
