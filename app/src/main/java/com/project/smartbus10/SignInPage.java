package com.project.smartbus10;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
    private EditText ID;
    private EditText password;
    private Button signInButton;
    private ProgressDialog progressBar;
    private Button forgot_your_password;
    //

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_page);

        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseReference=mFirebaseDatabase.getReference();

        ID=(EditText) findViewById(R.id.id);
        password=(EditText) findViewById(R.id.password);
        signInButton=(Button)findViewById(R.id.sign_in_button);
        forgot_your_password=(Button)findViewById(R.id.forgot_your_password_button);
        progressBar=new ProgressDialog(SignInPage.this);
        progressBar.setMessage("Logging you in");
        // sign In Button action
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                progressBar.show();
                if(isOnline()){
                    if(checkTheInput()){
                        CheckTheLoginMethod(ID.getText().toString(),password.getText().toString());
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
    private void CheckTheLoginMethod(String ID,String password) {
        ID=ID.trim();//Delete the spaces from the userName
        password=password.trim();//Delete the spaces from the password
        if (ID.contains("A")){
            signIn("SchoolAdministration",ID,password);
        }
        else if (ID.contains("P")){
            signIn("Parent",ID,password);
        }
        else if (ID.contains("D")){
            signIn("Driver",ID,password);
        }
        else{
            progressBar.dismiss();
            Toast.makeText(SignInPage.this, R.string.error_incorrect_input, Toast.LENGTH_SHORT).show();
        }
    }


    // sign In using your ID
    private void signIn(final String path , final String ID, final String password) {
        mDatabaseReference.child(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                    if(ID.equals((String) mDataSnapshot.getKey())){//UserName verification
                        if(password.equals((String) mDataSnapshot.child("password").getValue())){// Password verification
                            openHomeActivity(path,mDataSnapshot);// open Home page
                            RC_SIGN_IN=1;
                            break;
                        }
                        else{  // error in password
                            progressBar.dismiss();
                            Toast.makeText(SignInPage.this, R.string.error_incorrect_input, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                if (RC_SIGN_IN==0){//Do not log in due to an error in the ID
                    progressBar.dismiss();
                    Toast.makeText(SignInPage.this, R.string.error_incorrect_input, Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if(databaseError!=null){
                    progressBar.dismiss();
                    Toast.makeText(SignInPage.this, R.string.error_database, Toast.LENGTH_SHORT).show();}

            }
        });
    }

    // Check the entry of all fields
    private boolean checkTheInput() {

        if (ID.getText().toString().isEmpty()) {
            ID.setError(getString(R.string.error_field_required));
            ID.requestFocus();
            progressBar.dismiss();
            return false;
        }

        if (password.getText().toString().isEmpty()) {
            password.setError(getString(R.string.error_field_required));
            password.requestFocus();
            progressBar.dismiss();
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
                sp.edit().putString("ID",admin.getAdminID()).apply();
                sp.edit().putString("Name",admin.getFirstName()+" "+admin.getLastName()).apply();
                sp.edit().putString("user_type","SchoolAdministration").apply();
                break;
            case  "Parent":
                Parent parent=mDataSnapshot.getValue(Parent.class);
                parent.setPatentID((String) mDataSnapshot.getKey());
                sp.edit().putString("ID",parent.getPatentID()).apply();
                sp.edit().putString("Name",parent.getFirstName()+" "+parent.getLastName()).apply();
                sp.edit().putString("user_type","Parent").apply();
                break;
            case "Driver":
                Driver driver=mDataSnapshot.getValue(Driver.class);
                driver.setDriverID((String) mDataSnapshot.getKey());
                sp.edit().putString("ID",driver.getDriverID()).apply();
                sp.edit().putString("Name",driver.getFirstName()+" "+driver.getLastName()).apply();
                sp.edit().putString("user_type","Driver").apply();
                break;
        }
        progressBar.dismiss();
        finish();
        GoToHomePage = new Intent(SignInPage.this, Home.class);
        startActivity(GoToHomePage);

    }
    // Check your internet connection
    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            progressBar.dismiss();
            Toast.makeText( SignInPage.this, "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    @Override
    public void onBackPressed() {
            finishAffinity();
            super.onBackPressed();

        }
}







