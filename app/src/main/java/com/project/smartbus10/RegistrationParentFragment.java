package com.project.smartbus10;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class RegistrationParentFragment extends Fragment {
    //
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReferenceParent;
    private SharedPreferences sp;

    private Parent parent;
    private Parent existParent;
    private String parentId;
    private String profile;
    private boolean exist;

    //
    private EditText fName;
    private EditText sName;
    private EditText lName;
    private EditText phNum;
    private EditText address;
    private Button createAcc;
    private EditText oldPass;
    private EditText newPass;
    private EditText vPass;
    private TextView oldPassText, newPassText,vPassText;
    private LinearLayout passLayout ,old_p;
    private ImageButton addressMap ;

    //
    private ProgressDialog progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.registration_parent_fragment, container, false);
        fName= view.findViewById(R.id.f_name);
        sName= view.findViewById(R.id.s_name);
        lName= view.findViewById(R.id.l_name);
        phNum= view.findViewById(R.id.ph_num);
        address=view.findViewById(R.id.address);
        createAcc=view.findViewById(R.id.create_acc);
        oldPass=view.findViewById(R.id.old_pass);
        oldPassText=view.findViewById(R.id.old_pass_text);
        newPass=view.findViewById(R.id.new_pass);
        newPassText=view.findViewById(R.id.new_pass_text);
        vPass=view.findViewById(R.id.v_pass);
        vPassText=view.findViewById(R.id.v_pass_text);
        passLayout=view.findViewById(R.id.pass_l);
        addressMap=view.findViewById(R.id.b_map);
        old_p= view.findViewById(R.id.old_p);
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseReferenceParent =mFirebaseDatabase.getReference();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Parent");
        sp = getActivity().getSharedPreferences("SignIn",MODE_PRIVATE);
        progressBar= new ProgressDialog(getActivity());
        progressBar.setMessage(getString(R.string.p));
        oldPass.setVisibility(View.GONE);
        oldPassText.setVisibility(View.GONE);
        newPass.setVisibility(View.GONE);
        newPassText.setVisibility(View.GONE);
        vPass.setVisibility(View.GONE);
        vPassText.setVisibility(View.GONE);
        passLayout.setVisibility(View.GONE);
        old_p.setVisibility(View.GONE);

        if(getArguments()!=null){
            createAcc.setText(R.string.ins);
            parentId =(String)getArguments().getString("idItem");
            profile=(String)getArguments().getString("Profile");
            parent=(Parent) getArguments().getSerializable("item");

            passLayout.setVisibility(View.VISIBLE);
            newPass.setVisibility(View.VISIBLE);
            newPassText.setVisibility(View.VISIBLE);
            vPass.setVisibility(View.VISIBLE);
            vPassText.setVisibility(View.VISIBLE);
            if(!(sp.getString("user_type","").equals("SchoolAdministration"))){
                oldPass.setVisibility(View.VISIBLE);
                oldPassText.setVisibility(View.VISIBLE);
                old_p.setVisibility(View.VISIBLE); }
                if(parent==null){
                    getInfo();
                }else{
                    showParentInfo();
                }


        }
        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getArguments() == null) {
                    if (verifyInput()) {
                        parent = new Parent();
                        parent.setFirstName("" + fName.getText());
                        parent.setSecondName("" + sName.getText());
                        parent.setLastName("" + lName.getText());
                        parent.setPhone("+966" + phNum.getText());
                        if(MapsActivityHomeAddress.homeAddress!=null){
                            parent.setAddress("" + MapsActivityHomeAddress.homeAddress);
                        }
                        parent.setAddress("" + address.getText());
                        parent.setLongitude(MapsActivityHomeAddress.longitude);
                        parent.setLatitude(MapsActivityHomeAddress.latitude);
                        generateIdPassword();
                        checkExist(parent);
                    }
                }
                else {
                    if(checkPass(parent.getPassword())){
                        parent.setFirstName("" + fName.getText());
                        parent.setSecondName("" + sName.getText());
                        parent.setLastName("" + lName.getText());
                        parent.setPhone("+966" + phNum.getText());
                        if(MapsActivityHomeAddress.homeAddress!=null){
                            parent.setAddress("" + MapsActivityHomeAddress.homeAddress);
                        }
                        if(!newPass.getText().toString().isEmpty())
                        {parent.setPassword(newPass.getText().toString().hashCode()+"");}
                        parent.setPhone("+966"+phNum.getText().toString());
                        if(MapsActivityHomeAddress.longitude!=0&& MapsActivityHomeAddress.latitude!=0)
                        {parent.setLongitude(MapsActivityHomeAddress.longitude);
                        parent.setLatitude(MapsActivityHomeAddress.latitude);}
                        updateParent();
                    }
                }

            }});
        // map
        addressMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToMap=new Intent(getActivity(), MapsActivityHomeAddress.class);
                if(parent!=null){
                    goToMap.putExtra("latitude",parent.getLatitude());
                    goToMap.putExtra("longitude",parent.getLongitude());
                    goToMap.putExtra("Address",parent.getAddress());
                }else  if(!address.getText().toString().isEmpty())
                {goToMap.putExtra("Address",address.getText().toString());}
                startActivity(goToMap);
            }
        });
        return view;}

    public void checkExist(final Parent parent){
        exist=false;
        Query fireQuery = mDatabaseReferenceParent.child("Parent").orderByChild("phone").equalTo(parent.getPhone());
        fireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()){
                        existParent =new Parent();
                        existParent =mDataSnapshot.getValue(Parent.class);
                        existParent.setPatentID(mDataSnapshot.getKey());
                        if(existParent.getFirstName().equals(parent.getFirstName())){
                            if(existParent.getSecondName().equals(parent.getSecondName())){
                                if(existParent.getLastName().equals(parent.getLastName())){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle(R.string.p_exist);
                                    builder.setMessage('\n'+existParent.getPatentID()+'\n'+existParent.getFirstName()+" "+existParent.getLastName());
                                    builder.setPositiveButton(R.string.cancel,
                                            new DialogInterface.OnClickListener() {
                                                @SuppressLint("NewApi")
                                                public void onClick(DialogInterface dialog,int which) {
                                                    getClass();
                                                }
                                            });builder.show();

                                }
                        }

                    }
                }

            }if(exist==false){insertParent();}}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void generateIdPassword(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMdkms");
        parentId="P"+sdf.format(date);
        sdf = new SimpleDateFormat("hhmmss");
        parent.setPassword(sdf.format(date));
    }

    public  void insertParent(){
        final String pas=parent.getPassword();
        parent.setPassword(pas.hashCode()+"");
        mDatabaseReference.child(parentId).setValue(parent).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    fName.setText("");
                    sName.setText("");
                    lName.setText("");
                    address.setText("");
                    phNum.setText("");
                    MapsActivityHomeAddress.longitude=0;
                    MapsActivityHomeAddress.latitude=0;
                    MapsActivityHomeAddress.homeAddress=null;
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.log_info);
                    builder.setMessage(getString(R.string.iD)+parentId+'\n'+getString(R.string.passw)+pas);
                    builder.setPositiveButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @SuppressLint("NewApi")
                                public void onClick(DialogInterface dialog,int which) {
                                    getClass();
                                }
                            });
                    builder.show();
                    Toast.makeText(getActivity(), R.string.s_drive, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(), R.string.error_add_drive , Toast.LENGTH_SHORT).show();
                }
            }
        });;}


    public boolean verifyInput(){
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
        if(phNum.getText().toString().length()>9||phNum.getText().toString().length()<9||(phNum.getText().toString().startsWith("5")==false)){
            phNum.setError(getString(R.string.error_phone));
            phNum.requestFocus();
            return false;
        }
        if(address.getText().toString().isEmpty()){
            address.setError(getString(R.string.error_field_required));
            address.requestFocus();
            return false;
        }
        if (!address.getText().toString().contains(",")){
            address.setError(getString(R.string.patterns));
            address.requestFocus();
            return false;
        }
        return true;
    }

    //edit methods
    // get Info from
    private void getInfo() {
        mDatabaseReference.child(parentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    parent=dataSnapshot.getValue(Parent.class);
                    parent.setPatentID(parentId);
                    showParentInfo();
                    }
                    else {  // error in password
                    progressBar.dismiss();
                    Toast.makeText(getActivity(), R.string.error_database, Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (databaseError != null) {
                    Toast.makeText(getActivity(), R.string.error_database, Toast.LENGTH_SHORT).show();
                    progressBar.dismiss(); }

            }});
    }
    public boolean checkPass(String pass){
        if(profile!=null){
            if(oldPass.getText().toString().isEmpty()&&(!(newPass.getText().toString().isEmpty())||!(vPass.getText().toString().isEmpty()))){
                oldPass.setError(getString(R.string.error_field_required));
                oldPass.requestFocus();
                return false;
            }
        }
        if(!(oldPass.getText().toString().isEmpty())&&!((oldPass.getText().toString().hashCode()+"").equals(pass))){
            oldPass.setError(getString(R.string.c_p_error));
            oldPass.requestFocus();
            return false;
        }
        if(!(newPass.getText().toString().isEmpty())||!(vPass.getText().toString().isEmpty())){
            if(!(newPass.getText().toString().equals(vPass.getText().toString()))){
                vPass.setError(getString(R.string.error_match));
                vPass.requestFocus();
                return false;
            }
        }
        if(phNum.getText().toString().isEmpty()){
            phNum.setError(getString(R.string.error_field_required));
            phNum.requestFocus();
            return false;
        }
        if(phNum.getText().toString().length()>9||phNum.getText().toString().length()<9||(phNum.getText().toString().startsWith("5")==false)){
            phNum.setError(getString(R.string.error_phone));
            phNum.requestFocus();
            return false;
        }
        if(address.getText().toString().isEmpty()){
            address.setError(getString(R.string.error_field_required));
            address.requestFocus();
            return false;
        }
        if (!address.getText().toString().contains(",")){
            address.setError(getString(R.string.patterns));
            address.requestFocus();
            return false;
        }

        return true;

    }
    public void showParentInfo(){
        fName.setEnabled(false);
        fName.setText(parent.getFirstName());
        sName.setEnabled(false);
        sName.setText(parent.getSecondName());
        lName.setEnabled(false);
        lName.setText(parent.getLastName());
        phNum.setText(parent.getPhone().substring(4));
        address.setText(parent.getAddress());

    }
    private void updateParent() {
        parentId=parent.getPatentID();
        parent.setPatentID(null);

        mDatabaseReference.child(parentId).setValue(parent).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    MapsActivityHomeAddress.longitude=0;
                    MapsActivityHomeAddress.latitude=0;
                    MapsActivityHomeAddress.homeAddress=null;
                    Toast.makeText(getActivity(), R.string.su, Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(getActivity(), R.string.unsu, Toast.LENGTH_SHORT).show();
                }parent.setPatentID(parentId);

            }});progressBar.dismiss();
    }

    @Override
    public void onResume() {
        if(MapsActivityHomeAddress.homeAddress!=null)
        {address.setText(MapsActivityHomeAddress.homeAddress);}
        super.onResume();
    }
}
