package com.project.smartbus10;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class RegistrationDriverFragment extends Fragment {
    //
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReferenceBus;
    private SharedPreferences sp;
    //
    private Driver driver;
    private SchoolAdministration admin;
    //
    private EditText fName;
    private EditText sName;
    private EditText lName;
    private EditText phNum;
    private EditText oldPass;
    private EditText newPass;
    private EditText vPass;
    private TextView oldPassText, newPassText,vPassText,busNumText;
    private Spinner busNum;
    private LinearLayout passLayout ,old_p,bus_id;
    private ProgressDialog progressBar;
    //
    private List<String> busIDList;
    private String busID;
    private String id;
    private String profile;
    private String itemType;
    private boolean d;
    private Driver existDriver;
    private boolean exist;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.registration_driver_fragment, container, false);
        sp = getActivity().getSharedPreferences("SignIn",MODE_PRIVATE);
        fName= view.findViewById(R.id.f_name);
        sName= view.findViewById(R.id.s_name);
        lName= view.findViewById(R.id.l_name);
        phNum= view.findViewById(R.id.ph_num);
        busNum=view.findViewById(R.id.bus_num);
        busNumText=view.findViewById(R.id.bus_num_text);
        oldPass=view.findViewById(R.id.old_pass);
        oldPassText=view.findViewById(R.id.old_pass_text);
        newPass=view.findViewById(R.id.new_pass);
        newPassText=view.findViewById(R.id.new_pass_text);
        vPass=view.findViewById(R.id.v_pass);
        vPassText=view.findViewById(R.id.v_pass_text);
        passLayout=view.findViewById(R.id.pass_l);
        old_p= view.findViewById(R.id.old_p);
        bus_id= view.findViewById(R.id.bus_id);
        Button createAcc=view.findViewById(R.id.create_acc);
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseReference=mFirebaseDatabase.getReference();
        mDatabaseReferenceBus=mFirebaseDatabase.getReference();
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
        //
        if(getArguments()!=null){
            createAcc.setText(R.string.ins);
            itemType=(String)getArguments().getString("ListType");
            id =(String)getArguments().getString("idItem");
            profile=(String)getArguments().getString("Profile");
            passLayout.setVisibility(View.VISIBLE);
            newPass.setVisibility(View.VISIBLE);
            newPassText.setVisibility(View.VISIBLE);
            vPass.setVisibility(View.VISIBLE);
            vPassText.setVisibility(View.VISIBLE);
            if(itemType.equals("SchoolAdministration")){
                busNum.setVisibility(View.GONE);
                busNumText.setVisibility(View.GONE);
                bus_id.setVisibility(View.GONE);

                getInfo();
            }else
               {driver=(Driver)getArguments().getSerializable("item");
                busID=(String)getArguments().getString("busId");
                if(profile!=null){
                    busNum.setVisibility(View.GONE);
                    busNumText.setVisibility(View.GONE);
                    bus_id.setVisibility(View.GONE);
                }
                   if(id !=null){ getBusID(); }
                   else{ getBusesID();
                         showDriverInfo();}
               }

            if(!(sp.getString("user_type","").equals("SchoolAdministration")&&itemType.equals("Driver"))){
                oldPass.setVisibility(View.VISIBLE);
                oldPassText.setVisibility(View.VISIBLE);
                old_p.setVisibility(View.VISIBLE); }



        }
       else{ getBusesID();}
        busNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                busID=adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                busID="";
            }
        });
        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.show();
                if(getArguments()==null){
                if(verifyInput()){
                    driver= new Driver();
                    mDatabaseReference=mFirebaseDatabase.getReference().child("Driver");
                    driver.setFirstName(""+fName.getText());
                    driver.setSecondName(""+sName.getText());
                    driver.setLastName(""+lName.getText());
                    driver.setPhone("+966"+phNum.getText());
                    generateIdPassword();
                    checkExist(driver);
                    if(!busID.equals(""))
                    { mDatabaseReferenceBus.child("Bus").child(busID).child("driverID").setValue(id);}}}
                    else{ if( itemType.equals("Driver")){
                           if(checkPass(driver.getPassword())){
                               if(!newPass.getText().toString().isEmpty())
                               {driver.setPassword(newPass.getText().toString().hashCode()+"");}
                               driver.setPhone("+966"+phNum.getText().toString());
                               if(profile==null)
                               { Log.d("ADebugTag",busID);
                                   if(busID!=null)
                                   {
                                       mDatabaseReferenceBus.child("Bus").child(busID).child("driverID").setValue(driver.getDriverID());}
                               }
                               updateDriver();}
                           }else{
                               if(checkPass(admin.getPassword())){
                                   if(!newPass.getText().toString().isEmpty())
                                   {admin.setPassword(newPass.getText().toString().hashCode()+"");}
                                   admin.setPhone("+966"+phNum.getText().toString());
                                   updateAdmin();}
                           }progressBar.dismiss();
                }

                }


        });
        return view;}


    public void generateIdPassword(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMdkms");
        id ="D"+sdf.format(date);
        sdf = new SimpleDateFormat("hhmmss");
        driver.setPassword(sdf.format(date));
    }
    public boolean verifyInput(){
        d=false;
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
        if(busID.equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.driv_mas);
            builder.setPositiveButton(R.string.ok,
                    new DialogInterface.OnClickListener() {
                        @SuppressLint("NewApi")
                        public void onClick(DialogInterface dialog,int which) {
                            driverWithoutbus();
                            d=true;
                        }
                    });
            builder.setNegativeButton(R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            d=false;
                        }
                    });
            builder.show();
            return d;
        }

        return true;
    }

    public void getBusesID(){
        busIDList=new ArrayList<>();
        if(busID!=null)
        {busIDList.add(busID);}
        else busIDList.add("");
        mDatabaseReferenceBus.child("Bus").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                    if(busID==null||!(busID.equals((String)mDataSnapshot.getKey()))){
                    busIDList.add((String)mDataSnapshot.getKey());}
                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_row,busIDList);
                    busNum.setAdapter(adapter); }
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }


        });


    }
    public void driverWithoutbus(){
        driver= new Driver();
        mDatabaseReference=mFirebaseDatabase.getReference().child("Driver");
        driver.setFirstName(""+fName.getText());
        driver.setSecondName(""+sName.getText());
        driver.setLastName(""+lName.getText());
        driver.setPhone("+966"+phNum.getText());
        generateIdPassword();
        checkExist(driver);

    }
    public  void insertDriver(){
        final String pass=driver.getPassword();
        driver.setPassword(pass.hashCode()+"");
        mDatabaseReference.child(id).setValue(driver).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete( Task<Void> task) {
                if (task.isSuccessful()){
                    fName.setText("");
                    sName.setText("");
                    lName.setText("");
                    phNum.setText("");
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.log_info);
                    builder.setMessage(getString(R.string.iD)+ id +'\n'+getString(R.string.passw)+pass);
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
        });;

    }

    public void checkExist(final Driver driver){
        exist= false;
        Query fireQuery = mDatabaseReferenceBus.child("Driver").orderByChild("phone").equalTo(driver.getPhone());
        fireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()){

                        existDriver =new Driver();
                        existDriver =mDataSnapshot.getValue(Driver.class);
                        existDriver.setDriverID(mDataSnapshot.getKey());
                        if(existDriver.getFirstName().equals(driver.getFirstName())){
                            if(existDriver.getSecondName().equals(driver.getSecondName())){
                                if(existDriver.getLastName().equals(driver.getLastName())){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    Driver driver1 =dataSnapshot.getValue(Driver.class);
                                    driver1.setDriverID(dataSnapshot.getKey());
                                    builder.setTitle(R.string.driv_exist);
                                    builder.setMessage('\n'+driver1.getDriverID()+'\n'+driver1.getFirstName()+" "+driver1.getLastName());
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
                    }
                }} if(exist==false){insertDriver();}
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        });

    }

    //edit methods
    // get Info from
    private void getInfo() {
        mDatabaseReference.child(itemType).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if(itemType.equals("Driver"))
                    {driver = dataSnapshot.getValue(Driver.class);
                        driver.setDriverID(id);
                          showDriverInfo();}
                          else if(itemType.equals("SchoolAdministration")){
                        admin = dataSnapshot.getValue(SchoolAdministration.class);
                        admin.setAdminID(id);
                        showAdminInfo();
                    }


                } else {  // error in password
                    progressBar.dismiss();
                    Toast.makeText(getActivity(), R.string.error_database, Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (databaseError != null) {
                    progressBar.dismiss();
                    Toast.makeText(getActivity(), R.string.error_database, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    //
    public void getBusID() {
        Query fireQuery = mDatabaseReferenceBus.child("Bus").orderByChild("driverID").equalTo(id);
        fireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                       for(DataSnapshot mDataSnapshot:dataSnapshot.getChildren())
                       {
                         busID= mDataSnapshot.getKey().toString();
                       }
                        getBusesID();
                        getInfo();


                } else {busID="";
                         getBusesID();
                                   getInfo();}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void showDriverInfo(){
        fName.setEnabled(false);
        fName.setText(driver.getFirstName());
        sName.setEnabled(false);
        sName.setText(driver.getSecondName());
        lName.setEnabled(false);
        lName.setText(driver.getLastName());
        phNum.setText(driver.getPhone().substring(4));

    }
    public void showAdminInfo(){
        fName.setEnabled(false);
        fName.setText(admin.getFirstName());
        sName.setEnabled(false);
        sName.setText(admin.getSecondName());
        lName.setEnabled(false);
        lName.setText(admin.getLastName());
        phNum.setText(admin.getPhone().substring(4));

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
        return true;

    }
    private void updateAdmin() {
        admin.setAdminID(null);
        mDatabaseReference.child(itemType).child(id).setValue(admin).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), R.string.su, Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(getActivity(), R.string.unsu, Toast.LENGTH_SHORT).show();
                }admin.setAdminID(id);

            }});progressBar.dismiss();
    }

    private void updateDriver() {
        id=driver.getDriverID();
        driver.setDriverID(null);
        mDatabaseReference.child(itemType).child(id).setValue(driver).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), R.string.su, Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(getActivity(), R.string.unsu, Toast.LENGTH_SHORT).show();
                }driver.setDriverID(id);

            }});progressBar.dismiss();
    }

}