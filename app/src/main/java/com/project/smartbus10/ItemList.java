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
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ItemList extends AppCompatActivity {
    //
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReference2;
    private ChildEventListener mChildEventListener;
    private SharedPreferences sp;

    //
    private String listType;
    private String parentId;
    private ListView list;
    private ItemAdapter adapter;
    private List<Item> itemList;

    //
    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        //
        Intent i = getIntent();
        listType = (String) i.getSerializableExtra("ListType");
        parentId=(String) i.getSerializableExtra("parentId");
        Log.d("ADebugTag", "Value: parent " +parentId);
        progressBar=new ProgressDialog(ItemList.this);
        progressBar.setMessage(getString(R.string.p));
        progressBar.show();
        FloatingActionButton fab = findViewById(R.id.fab);


        if (listType.equals("Complaints")){
            fab.setVisibility(View.GONE);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToRegistrationPage = new Intent( ItemList.this,Registration.class);
                goToRegistrationPage.putExtra("ListType", listType);
                startActivity(goToRegistrationPage);
            }
        });

        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseReference=mFirebaseDatabase.getReference().child(listType);
        mDatabaseReference2=mFirebaseDatabase.getReference();
        sp = getSharedPreferences("SignIn",MODE_PRIVATE);

        itemList = new ArrayList<>();
        list=(ListView)findViewById(R.id.itemlist) ;
        adapter = new ItemAdapter(this, R.layout.item_list, itemList);
        list.setAdapter(adapter);
        //Take the information from the database and update it continuously
        mChildEventListener= new ChildEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Item item =dataSnapshot.getValue(Item.class);
                checkListType(dataSnapshot,item);
                item.setID((String) dataSnapshot.getKey());
                item.setProfileImage(personImageProfile());
                adapter.add(item);
                isOnline();
                progressBar.dismiss();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try{
                String ID = dataSnapshot.getKey().toString();
                Item item =dataSnapshot.getValue(Item.class);
                 checkListType(dataSnapshot,item);
                item.setID((String) dataSnapshot.getKey());
                item.setProfileImage(personImageProfile());
                itemList.add(getIndext(ID), item);
                adapter.notifyDataSetChanged();}
                catch (Exception e){
                    Log.d("SmartBus", "Value: "+"error");
                }
                isOnline();
                progressBar.dismiss();
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String ID = dataSnapshot.getKey();
                try{
                for(Item p: itemList){
                    if(ID.equals(p.getID())){
                        itemList.remove(p);
                    }
                }}catch (Exception e){
                    Log.d("SmartBus", "Value: "+"error");
                }
                adapter.notifyDataSetChanged();
                progressBar.dismiss();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("databaseError", "postComments:onCancelled", databaseError.toException());
                Toast.makeText(ItemList.this, "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
                progressBar.dismiss();

            }
        };
        if(parentId==null){
            mDatabaseReference.addChildEventListener(mChildEventListener);

        }else {childrenList().addChildEventListener(mChildEventListener);
            fab.setVisibility(View.GONE);

        }

        //
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Item item = itemList.get(position);
                if(listType.equals("Complaints")){
                    Intent goToComplaints = new Intent(ItemList.this, ComplaintsActivity.class);
                    goToComplaints.putExtra("ComplaintsID", item.getID());
                    goToComplaints.putExtra("parentID", item.getFirstName());
                    startActivity(goToComplaints);
                } else if(listType.equals("Bus")){
                    Intent goToComplaints = new Intent(ItemList.this, BusDetailsActivity.class);
                    goToComplaints.putExtra("busId", item.getID());
                    startActivity(goToComplaints);
                }
                else{
                Intent goToProfile = new Intent(ItemList.this, DetailsActivity.class);
                goToProfile.putExtra("ListType", listType);
                goToProfile.putExtra("idItem", item.getID());
                startActivity(goToProfile);}

            }});
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(parentId!=null&&sp.getString("user_type","").equals("SchoolAdministration")){
                    Log.d("ADebugTag", parentId.isEmpty()+"");
                    Intent goToDetailsActivity = new Intent(ItemList.this, DetailsActivity.class);
                    goToDetailsActivity.putExtra("ListType","Parent");
                    goToDetailsActivity.putExtra("idItem",parentId);
                    startActivity(goToDetailsActivity);
                    ;}
                else
                {Intent goHome = new Intent(ItemList.this, Home.class);
                    startActivity(goHome);}
                finish();
            }
        });

    }
    public int personImageProfile(){
        int sourceImage = 0;
        if(listType.equals("Student")){
            sourceImage=R.drawable.student_;
        }
        else if(listType.equals("Driver")){
            sourceImage=R.drawable.driverp;
        }

        else if(listType.equals("Parent")){

            sourceImage=R.drawable.parent_;
        }
        else if (listType.equals("Bus")){
            sourceImage=R.drawable.bus_icon;
        }
        else if (listType.equals("Complaints")){
            sourceImage=R.drawable.complaints_icon;
        }

        return sourceImage;
    }

    public void onBackPressed() {
        if(parentId!=null&&sp.getString("user_type","").equals("SchoolAdministration")){
            Intent goToDetailsActivity = new Intent(ItemList.this, DetailsActivity.class);
            goToDetailsActivity.putExtra("ListType","Parent");
            goToDetailsActivity.putExtra("idItem",parentId);
            startActivity(goToDetailsActivity);
        }
        else
        {Intent goHome = new Intent(ItemList.this, Home.class);
        startActivity(goHome);}
        finish();
        super.onBackPressed();
    }

    public int getIndext(String ID){
        int i=0;
        for(Item p: itemList){
            if(ID.equals(p.getID())){
                i= itemList.indexOf(p);
                itemList.remove(p);
            }
        }
        return i;
    }

    // Check your internet connection
    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText( this, "No Internet connection!", Toast.LENGTH_LONG).show();
            progressBar.dismiss();
            return false;
        }
        return true;
    }
    public void checkListType(DataSnapshot dataSnapshot, Item item){
        if(listType.equals("Student")){
            if(dataSnapshot.child("parentID").getValue()!=null) {
                try {
                    parentPhone(item, dataSnapshot.child("parentID").getValue().toString());
                } catch (Exception e) {
                    Toast.makeText(this, "No Internet connection!", Toast.LENGTH_LONG).show();
                }

            }}
        else if(listType.equals("Complaints")) {
            try {
            String idp = dataSnapshot.child("parentID").getValue().toString();
             item.setFirstName(idp);}
            catch (Exception e){ Toast.makeText( this, "No Internet connection!", Toast.LENGTH_LONG).show();}

        }
    }

    public void parentPhone(final Item item, String idParent){
        mDatabaseReference2.child("Parent").child(idParent).child("phone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    item.setPhone(dataSnapshot.getValue().toString());}
                else{
                    item.setPhone("");}
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }


        });

    }
    private Query childrenList (){
        Query stuQuery = mDatabaseReference.orderByChild("parentID").equalTo(parentId);

        return stuQuery;
    }


}


