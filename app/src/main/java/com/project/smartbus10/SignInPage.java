package com.project.smartbus10;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

public class SignInPage extends AppCompatActivity {
    public static int RC_SIGN_IN = 0;
    // Database Variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    //Input field variables
    private EditText userName;
    private EditText password;
    private Button signInButton;
    private Button forgot_your_password;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_page);

        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseReference=mFirebaseDatabase.getReference();


        userName=(EditText) findViewById(R.id.user_name);
        password=(EditText) findViewById(R.id.password);
        signInButton=(Button)findViewById(R.id.sign_in_button);
        forgot_your_password=(Button)findViewById(R.id.forgot_your_password_button);
        // sign In Button action
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
             if(isOnline()){
               if(checkTheInput()){
                   CheckTheLoginMethod(userName.getText().toString(),password.getText().toString());
               }
             }
            }});

        //Forgot your password action
        forgot_your_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent GoChangePasswordPage = new Intent(SignInPage.this, ChangePassword.class);
                startActivity(GoChangePasswordPage);

            }
        });


    }
    //Choose the input method using the userName or ID
    private void CheckTheLoginMethod(String userName,String password) {
        userName=userName.trim();//Delete the spaces from the userName
        password=password.trim();//Delete the spaces from the password
        if (userName.contains("A")){
            signIn("SchoolAdministration",userName,password);
        }
        else if (userName.contains("P")){
            signIn("Parent",userName,password);
        }
        else if (userName.contains("D")){
            signIn("Driver",userName,password);
        }
        else{
            signIn(userName,password);
        }
    }


    // sign In using your ID
    private void signIn( final String id, final String password) {
        final String[]path={"SchoolAdministration","Parent","Driver"};
        for(int i=0;i<path.length;i++){
            final int finalI = i;
            final int finalI1 = i;
            mDatabaseReference.child(path[i]).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                        if(id.equals((String) mDataSnapshot.getKey())){// ID verification
                            if(password.equals((String) mDataSnapshot.child("Password").getValue())){//Password verification
                                openHomeActivity(path[finalI],mDataSnapshot);
                                RC_SIGN_IN=1;
                                break;
                            }
                            else{ // error in password
                                Toast.makeText(SignInPage.this, R.string.error_incorrect_input, Toast.LENGTH_SHORT).show();
                            }
                        }


                    }
                    if (RC_SIGN_IN==0){//Do not sign in due to an error in the  ID
                        Toast.makeText(SignInPage.this, R.string.error_incorrect_input, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(SignInPage.this, R.string.error_database, Toast.LENGTH_SHORT).show();

                }
            });
        }

    }

    // sign In using your userName
    private void signIn(final String path , final String userName, final String password) {
        mDatabaseReference.child(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                    if(userName.equals((String) mDataSnapshot.child("UserName").getValue())){//UserName verification
                        if(password.equals((String) mDataSnapshot.child("Password").getValue())){// Password verification
                            openHomeActivity(path,mDataSnapshot);// open Home page
                            RC_SIGN_IN=1;
                            break;
                        }
                        else{  // error in password
                            Toast.makeText(SignInPage.this, R.string.error_incorrect_input, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                if (RC_SIGN_IN==0){//Do not log in due to an error in the username
                    Toast.makeText(SignInPage.this, R.string.error_incorrect_input, Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if(databaseError==null){
                Toast.makeText(SignInPage.this, R.string.error_database, Toast.LENGTH_SHORT).show();}

            }
        });
    }

    // Check the entry of all fields
    private boolean checkTheInput() {

        if (userName.getText().toString().isEmpty()) {
            userName.setError(getString(R.string.error_field_required));
            userName.requestFocus();
            return false;
        }

        if (password.getText().toString().isEmpty()) {
            password.setError(getString(R.string.error_field_required));
            password.requestFocus();
            return false;
        }

        return true;
    }

   // Reorientation to the appropriate home page
    public void openHomeActivity(String path ,DataSnapshot mDataSnapshot){
        Intent GoToHomePage;
        SharedPreferences sp = getSharedPreferences("SignIn",MODE_PRIVATE);
        sp.edit().putBoolean("login",true).apply();
       // User user;
        switch(path) {
            case "SchoolAdministration":
                SchoolAdministration admin=mDataSnapshot.getValue(SchoolAdministration.class);
                admin.setAdminID((String) mDataSnapshot.getKey());
                sp.edit().putString("UserName",admin.getUserName()).apply();
                sp.edit().putString("ID",admin.getAdminID()).apply();
                sp.edit().putString("Name",admin.getFirstName()+" "+admin.getLastName()).apply();
                sp.edit().putString("user_type","SchoolAdministration").apply();
                break;
            case  "Parent":
                Parent parent=mDataSnapshot.getValue(Parent.class);
                parent.setPatentID((String) mDataSnapshot.getKey());
                sp.edit().putString("UserName",parent.getUserName()).apply();
                sp.edit().putString("ID",parent.getPatentID()).apply();
                sp.edit().putString("Name",parent.getFirstName()+" "+parent.getLastName()).apply();
                sp.edit().putString("user_type","Parent").apply();
                break;
            case "Driver":
                Driver driver=mDataSnapshot.getValue(Driver.class);
                driver.setDriverID((String) mDataSnapshot.getKey());
                sp.edit().putString("UserName",driver.getUserName()).apply();
                sp.edit().putString("ID",driver.getDriverID()).apply();
                sp.edit().putString("Name",driver.getFirstName()+" "+driver.getLastName()).apply();
                sp.edit().putString("user_type","Driver").apply();
                break;
        }
        finish();
        GoToHomePage = new Intent(SignInPage.this, Home.class);
        startActivity(GoToHomePage);

    }
 // Check your internet connection
    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText( SignInPage.this, "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}






