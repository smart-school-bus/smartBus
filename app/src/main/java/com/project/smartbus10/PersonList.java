package com.project.smartbus10;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class PersonList extends AppCompatActivity {
    //
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    //
    private String PersonType;
    private ListView list;
    private PersonAdapter adapter;
    private List<Person> Person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        //
        Intent i = getIntent();
        PersonType = (String) i.getSerializableExtra("Person");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToRegistrationPage = new Intent( PersonList.this,Registration.class);
                goToRegistrationPage.putExtra("Person",PersonType);
                startActivity(goToRegistrationPage);
            }
        });


        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseReference=mFirebaseDatabase.getReference().child(PersonType);

        Person = new ArrayList<>();
        list=(ListView)findViewById(R.id.itemlist) ;
        adapter = new PersonAdapter(this, R.layout.item_list, Person);
        list.setAdapter(adapter);

        //Take the information from the database and update it continuously
        mChildEventListener= new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Person person =dataSnapshot.getValue(Person.class);
                person.setID((String) dataSnapshot.getKey());
                person.setProfileImage((Uri) dataSnapshot.child("ProfileImage").getValue());
                adapter.add(person);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                adapter.remove(dataSnapshot.getValue(Person.class));

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                adapter.remove(dataSnapshot.getValue(Person.class));
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                adapter.remove(dataSnapshot.getValue(Person.class));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addChildEventListener(mChildEventListener);

    }

}


