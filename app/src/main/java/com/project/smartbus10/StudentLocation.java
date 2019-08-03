package com.project.smartbus10;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.List;

public class StudentLocation extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReferenceNot;
    private SharedPreferences sp;
    private ChildEventListener mChildEventListener;
    private Query stuQuery, fireQuery;

    private String busId;
    private Student student;
    private List<Student> studentList;
    private ListView list;
    private StudentAdapter adapter;
    private ProgressDialog progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_location);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReferenceNot = mFirebaseDatabase.getReference();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Student");
        sp = getSharedPreferences("SignIn",MODE_PRIVATE);
        progressBar = new ProgressDialog(StudentLocation.this);
        progressBar.setMessage(getString(R.string.p));
        studentList = new ArrayList<>();
        list = (ListView) findViewById(R.id.stud_list);
        adapter = new StudentAdapter(this, R.layout.activity_student_location, studentList);
        list.setAdapter(adapter);

        getStudent();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Student student=studentList.get(i);
                if(student.getState().equals("on")){
                    Intent goToMap=new Intent(StudentLocation.this,BusMapActivity.class);
                    goToMap.putExtra("busId",student.getBus().getID());
                    startActivity(goToMap);
                }else{
                    Toast.makeText(StudentLocation.this, R.string.st_out, Toast.LENGTH_SHORT).show();
                }

                }});}
    public void getStudent(){
        stuQuery=mDatabaseReference.orderByChild("parentID").equalTo(sp.getString("ID",""));

        mChildEventListener= new ChildEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                student=dataSnapshot.getValue(Student.class);
                student.setStuID(dataSnapshot.getKey());
                Parent parent=new Parent();
                if(dataSnapshot.child("parentID").getValue()!=null)
                { parent.setPatentID(dataSnapshot.child("parentID").getValue().toString());}
                student.setParent(parent);
                BusStop busStop=new BusStop();
                if(dataSnapshot.child("busStopID").getValue()!=null)
                {busStop.setBusStopID(dataSnapshot.child("busStopID").getValue().toString());
                    student.setBusStop(busStop); }
                Bus bus=new Bus();
                if(dataSnapshot.child("busID").getValue()!=null)
                {bus.setID(dataSnapshot.child("busID").getValue().toString());
                    student.setBus(bus); }
                adapter.add(student);}




            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                student=dataSnapshot.getValue(Student.class);
                student.setStuID(dataSnapshot.getKey());
                Parent parent=new Parent();
                if(dataSnapshot.child("parentID").getValue()!=null)
                {parent.setPatentID(dataSnapshot.child("parentID").getValue().toString());}
                student.setParent(parent);
                BusStop busStop=new BusStop();
                if(dataSnapshot.child("parentID").getValue().toString()!=null)
                {busStop.setBusStopID(dataSnapshot.child("parentID").getValue().toString());
                    student.setBusStop(busStop); }
                studentList.set(getIndex(student.getStuID()),student);
                adapter.notifyDataSetChanged();}


            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String ID = dataSnapshot.getKey();
                try{
                    for(Student p: studentList){
                        if(ID.equals(p.getStuID())){
                            studentList.remove(p);
                        }
                    }}catch (Exception e){
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("databaseError", "postComments:onCancelled", databaseError.toException());
                Toast.makeText(StudentLocation.this, "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
                progressBar.dismiss();

            }
        };stuQuery.addChildEventListener(mChildEventListener);




    }




    public int getIndex(String ID){
        int i=0;
        for(Student p: studentList){
            if(ID.equals(p.getStuID())){
                i= studentList.indexOf(p);
                return i;
            }
        }
        return i;
    }
}

