package com.project.smartbus10;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Registration extends AppCompatActivity {
    String PersonType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Intent i = getIntent();
        PersonType = (String) i.getSerializableExtra("Person");
        if(PersonType.equals("Driver")){
            getSupportFragmentManager().beginTransaction().add(R.id.Registration,new RegistrationDriverFragment(),"RegistrationDriverFragment").commit();
        }
    }
}
