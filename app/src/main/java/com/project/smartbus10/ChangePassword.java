package com.project.smartbus10;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class ChangePassword extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth fbAuth;
    //
    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    //Input field variables
    private EditText ID;
    private EditText code;
    private EditText newPass;
    private EditText vPass;
    private Button  checkId;
    private Button checkCode;
    private Button resendCode;
    private Button changePass;
    private TextView text1;
    private TextView text2;
    private TextView count;
    private String path;
    private String phone ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        FirebaseApp.initializeApp(this);
        ID = findViewById(R.id.id);
        code=findViewById(R.id.code);
        newPass=findViewById(R.id.newpass);
        vPass=findViewById(R.id.vpass);
        checkId=findViewById(R.id.b_check);
        checkCode=findViewById(R.id.sendCode);
        resendCode=findViewById(R.id.resendCode);
        changePass=findViewById(R.id.ch_pass);
        text1=(TextView)findViewById(R.id.textid);
        text2=(TextView)findViewById(R.id.v_text);
        count=(TextView)findViewById(R.id.secondscounter);
        code.setVisibility(View.GONE);
        newPass.setVisibility(View.GONE);
        vPass.setVisibility(View.GONE);
        checkCode.setVisibility(View.GONE);
        resendCode.setVisibility(View.GONE);
        changePass.setVisibility(View.GONE);
        text2.setVisibility(View.GONE);
        count.setVisibility(View.GONE);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        fbAuth = FirebaseAuth.getInstance();

    }

    public void getPhone(View view) {
        if (checkTheInput()) {
            if(CheckTheLoginMethod(ID.getText().toString())){
                mDatabaseReference.child(path).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                            if(ID.getText().toString().trim().equals((String) mDataSnapshot.getKey())){//UserName verification
                                phone=mDataSnapshot.child("phone").getValue().toString();
                                openMessage();
                                break;
                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        if(databaseError==null){
                            Toast.makeText(ChangePassword.this, R.string.error_database, Toast.LENGTH_SHORT).show();}

                    }

                });}
            else
                Toast.makeText(ChangePassword.this, R.string.error_input, Toast.LENGTH_SHORT).show();

        }
    }

    private boolean checkTheInput() {

        if (ID.getText().toString().isEmpty()) {
            ID.setError(getString(R.string.error_field_required));
            ID.requestFocus();
            return false;
        }

        return true;
    }

    private boolean CheckTheLoginMethod(String ID) {
        ID=ID.trim();//Delete the spaces from the userName
        //Delete the spaces from the password
        if (ID.contains("A")){
            path="SchoolAdministration";

        }
        else if (ID.contains("P")){
            path="Parent";

        }
        else if (ID.contains("D")){
            path="Driver";

        }
        else{
            Toast.makeText(ChangePassword.this, R.string.error_input, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void openMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePassword.this);
        builder.setMessage("Verification code sent to 05******"+phone.substring(10));
        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @SuppressLint("NewApi")
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        sendCode();
                        //finishAffinity();
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
    public void sendCode() {

        String phoneNumber=phone;

        setUpVerificatonCallbacks();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                120,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                verificationCallbacks);
    }

    private void setUpVerificatonCallbacks() {

        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {

                        signInWithPhoneAuthCredential(credential);
                        fbAuth.signOut();
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            // Log.d(TAG, "Invalid credential: "+ e.getLocalizedMessage());
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // SMS quota exceeded
                            // Log.d(TAG, "SMS Quota exceeded.");
                        }
                    }


                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {

                        phoneVerificationId = verificationId;
                        resendToken = token;
                        text1.setText(R.string.code);
                        ID.setVisibility(View.GONE);
                        checkId.setVisibility(View.GONE);
                        code.setVisibility(View.VISIBLE);
                        checkCode.setVisibility(View.VISIBLE);
                        resendCode.setVisibility(View.VISIBLE);
                        count.setVisibility(View.VISIBLE);
                        new CountDownTimer(120000, 1000) {


                            public void onTick(long millisUntilFinished) {
                                count.setText("Enter code before" +(millisUntilFinished / 1000+"s"));
                                checkCode.setEnabled(true);

                            }

                            @SuppressLint("ResourceAsColor")
                            public void onFinish() {
                                checkCode.setEnabled(false);
                            }
                        }.start();

                    }
                };
    }

    public void verifyCode(View view) {

        String codeinput = code.getText().toString().trim();
        if(!codeinput.isEmpty()){
            PhoneAuthCredential credential =
                    PhoneAuthProvider.getCredential(phoneVerificationId, codeinput);
            signInWithPhoneAuthCredential(credential);}
        else{
            code.setError(getString(R.string.error_code));
            code.requestFocus();
        }

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            text1.setText(R.string.n_pass);
                            text2.setVisibility(View.VISIBLE);
                            ID.setVisibility(View.GONE);
                            checkId.setVisibility(View.GONE);
                            code.setVisibility(View.GONE);
                            checkCode.setVisibility(View.GONE);
                            resendCode.setVisibility(View.GONE);
                            newPass.setVisibility(View.VISIBLE);
                            vPass.setVisibility(View.VISIBLE);
                            changePass.setVisibility(View.VISIBLE);
                            FirebaseUser user = task.getResult().getUser();
                            user.delete();
                            fbAuth.signOut();
                            //fbAuth.auth().currentUser.unlink(fbAuth.auth.PhoneAuthProvider.PROVIDER_ID);

                        } else {
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                code.setError(getString(R.string.error_math_code));
                                code.requestFocus();
                            }
                        }
                    }
                });

    }

    public void resendCode(View view) {

        String phoneNumber = phone;

        setUpVerificatonCallbacks();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                120,
                TimeUnit.SECONDS,
                this,
                verificationCallbacks,
                resendToken);
    }
    public  void changePass(View view){
        if(vPass.getText().toString().isEmpty()) {
            vPass.setError(getString(R.string.error_field_required));
            vPass.requestFocus();}
        if(newPass.getText().toString().isEmpty())
        {newPass.setError(getString(R.string.error_field_required));
            newPass.requestFocus();}
        else
        {if(vPass.getText().toString().trim().equals(newPass.getText().toString().trim())){
            mDatabaseReference.child(path).child(ID.getText().toString().trim()).child("password").setValue(newPass.getText().toString().trim().hashCode());
        }
        else{
            vPass.setError(getString(R.string.error_match));
            vPass.requestFocus();
        }}
    }




}