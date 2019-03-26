package com.project.smartbus10;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;


public class Registration extends AppCompatActivity {

    private SharedPreferences sp;
    //
    private String ListType;
    private String itemId;
    private String profile;
    private Student student;
    private Parent parent;
    private SchoolAdministration admin;
    private Driver driver;
    private String busID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        sp = getSharedPreferences("SignIn",MODE_PRIVATE);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        Intent i = getIntent();
        ListType = (String) i.getSerializableExtra("ListType");
        Log.d("ADebugTag","re "+ListType);
        itemId=(String) i.getSerializableExtra("idItem");
        if (sp.getString("user_type","").equals("Parent")&&ListType.equals("Student")){
            Intent goToEditPage = new Intent( Registration.this, DetailsActivity.class);
            goToEditPage.putExtra("ListType", ListType);
            goToEditPage.putExtra("idItem",itemId );
            startActivity(goToEditPage);}

        if (ListType.equals("SchoolAdministration")) {
            profile=(String)i.getSerializableExtra("Profile");

                Bundle args = new Bundle();
                args.putSerializable("idItem", itemId);
                args.putSerializable("ListType", "SchoolAdministration");
                args.putSerializable("Profile", profile);
                Fragment adminEdit=new RegistrationDriverFragment();
                adminEdit.setArguments(args);
                getSupportFragmentManager().beginTransaction().add(R.id.Registration,adminEdit, "RegistrationDriverFragment").addToBackStack("Driver")
                        .commit();


        }

        if (ListType.equals("Driver")) {
            driver=(Driver)i.getSerializableExtra("item");
            itemId=(String) i.getSerializableExtra("idItem");
            busID=(String) i.getSerializableExtra("busId");
            profile=(String)i.getSerializableExtra("Profile");
            if(driver!=null||itemId!=null){
                Bundle args = new Bundle();
                args.putSerializable("item", driver);
                args.putSerializable("idItem", itemId);
                args.putSerializable("busId", busID);
                args.putSerializable("Profile", profile);
                args.putSerializable("ListType", "Driver");
                Fragment driverEdit=new RegistrationDriverFragment();
                driverEdit.setArguments(args);
                getSupportFragmentManager().beginTransaction().add(R.id.Registration,driverEdit, "RegistrationDriverFragment").addToBackStack("Driver")
                        .commit();

            }else  getSupportFragmentManager().beginTransaction().add(R.id.Registration, new RegistrationDriverFragment(), "RegistrationDriverFragment").addToBackStack("Driver")
                    .commit();
        }

        if (ListType.equals("Parent")) {
            parent=(Parent)i.getSerializableExtra("item");
            itemId=(String) i.getSerializableExtra("idItem");
            profile=(String)i.getSerializableExtra("Profile");
            MapsActivityHomeAddress.longitude=0;
            MapsActivityHomeAddress.latitude=0;

            if(parent!=null||itemId!=null){
                Bundle args = new Bundle();
                args.putSerializable("item", parent);
                args.putSerializable("idItem", itemId);
                args.putSerializable("Profile", profile);

                Fragment parentEdit=new RegistrationParentFragment();
                parentEdit.setArguments(args);
                getSupportFragmentManager().beginTransaction().add(R.id.Registration,parentEdit, "RegistrationParentFragment").addToBackStack("Parent")
                        .commit();
            }
            else
            getSupportFragmentManager().beginTransaction().add(R.id.Registration, new RegistrationParentFragment(), "RegistrationParentFragment").addToBackStack("Parent")
                    .commit();
        }

        if (ListType.equals("Student")) {
            parent=(Parent)i.getSerializableExtra("parent");
            student=(Student)i.getSerializableExtra("item");
            itemId=(String) i.getSerializableExtra("idItem");
            if(student!=null||itemId!=null){
                Bundle args = new Bundle();
                args.putSerializable("item", student);
                args.putSerializable("idItem", itemId);
                args.putSerializable("parent", parent);
                Fragment studentEdit=new RegistrationStudentFragment();
                studentEdit.setArguments(args);
                getSupportFragmentManager().beginTransaction().add(R.id.Registration,studentEdit, "RegistrationStudentFragment").addToBackStack("Student")
                        .commit();
            }
            getSupportFragmentManager().beginTransaction().add(R.id.Registration, new RegistrationStudentFragment(), "RegistrationStudentFragment").addToBackStack("Student")
                    .commit();
        }

        if(ListType.equals("Bus")){
            getSupportFragmentManager().beginTransaction().add(R.id.Registration, new RegistrationBusFragment(), "RegistrationBusFragment").addToBackStack("Bus")
                    .commit();
        }


        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(student!=null){
                    Intent goToDetailsPage = new Intent(Registration.this, DetailsActivity.class);
                    goToDetailsPage.putExtra("ListType",ListType);
                    goToDetailsPage.putExtra("idItem",student.getStuID());
                    startActivity(goToDetailsPage);
                }else if(parent!=null){
                    Intent goToDetailsPage = new Intent(Registration.this, DetailsActivity.class);
                    goToDetailsPage.putExtra("ListType",ListType);
                    goToDetailsPage.putExtra("idItem",parent.getPatentID());
                    goToDetailsPage.putExtra("Profile",profile);
                    startActivity(goToDetailsPage);
                    MapsActivityHomeAddress.longitude=0;
                    MapsActivityHomeAddress.latitude=0;
                    MapsActivityHomeAddress.homeAddress=null;
                }
                else if(driver!=null){
                    Intent goToDetailsPage = new Intent(Registration.this, DetailsActivity.class);
                    goToDetailsPage.putExtra("ListType",ListType);
                    goToDetailsPage.putExtra("idItem",driver.getDriverID());
                    goToDetailsPage.putExtra("Profile",profile);
                    startActivity(goToDetailsPage);
                } else if(ListType.equals("SchoolAdministration")){
                    Intent goToDetailsPage = new Intent(Registration.this, DetailsActivity.class);
                    goToDetailsPage.putExtra("ListType",ListType);
                    goToDetailsPage.putExtra("idItem",itemId);
                    goToDetailsPage.putExtra("Profile",profile);
                    startActivity(goToDetailsPage);
                }
                else
                {Intent goToListPage = new Intent(Registration.this, ItemList.class);
                goToListPage.putExtra("ListType",ListType);
                startActivity(goToListPage);}
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        if(student!=null){
            Intent goToDetailsPage = new Intent(Registration.this, DetailsActivity.class);
            goToDetailsPage.putExtra("ListType",ListType);
            goToDetailsPage.putExtra("idItem",student.getStuID());
            startActivity(goToDetailsPage);
        }else if(parent!=null){
            Intent goToDetailsPage = new Intent(Registration.this, DetailsActivity.class);
            goToDetailsPage.putExtra("ListType",ListType);
            goToDetailsPage.putExtra("idItem",parent.getPatentID());
            goToDetailsPage.putExtra("Profile",profile);
            startActivity(goToDetailsPage);
            MapsActivityHomeAddress.longitude=0;
            MapsActivityHomeAddress.latitude=0;
            MapsActivityHomeAddress.homeAddress=null;
        }
        else if(driver!=null){
            Intent goToDetailsPage = new Intent(Registration.this, DetailsActivity.class);
            goToDetailsPage.putExtra("ListType",ListType);
            goToDetailsPage.putExtra("idItem",driver.getDriverID());
            goToDetailsPage.putExtra("Profile",profile);
            startActivity(goToDetailsPage);
        } else if(ListType.equals("SchoolAdministration")){
            Intent goToDetailsPage = new Intent(Registration.this, DetailsActivity.class);
            goToDetailsPage.putExtra("ListType",ListType);
            goToDetailsPage.putExtra("idItem",itemId);
            goToDetailsPage.putExtra("Profile",profile);
            startActivity(goToDetailsPage);
        }
        else
        {Intent goToListPage = new Intent(Registration.this, ItemList.class);
            goToListPage.putExtra("ListType",ListType);
            startActivity(goToListPage);}

             finish();
            super.onBackPressed();}



}
