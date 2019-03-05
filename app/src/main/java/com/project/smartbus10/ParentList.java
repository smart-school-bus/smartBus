package com.project.smartbus10;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ParentList extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    //

    private ListView list;
    private Adapter adapter;
    private  List<Parent> parents;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_list);

        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseReference=mFirebaseDatabase.getReference().child("Parent");

        // Initialize parents ListView and its adapter
        parents = new ArrayList<>();
        list=(ListView)findViewById(R.id.list) ;
        adapter = new Adapter(this, R.layout.item_list, parents);
        list.setAdapter(adapter);

        //Display item in detail
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
             Parent parent= parents.get(position);
                Intent GoToDetailsPage = new Intent(ParentList.this,ProfileActivity.class);
                GoToDetailsPage.putExtra("Person",parent);
                startActivity(GoToDetailsPage);

            }});
        //Take the information from the database and update it continuously
        mChildEventListener= new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Parent parent =dataSnapshot.getValue(Parent.class);
                parent.setPatentID((String) dataSnapshot.getKey());
                adapter.add(parent);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addChildEventListener(mChildEventListener);

    }

}
