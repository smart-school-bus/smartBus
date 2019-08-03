package com.project.smartbus10;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class StudentListFragment extends Fragment {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReferenceNot;
    private ChildEventListener mChildEventListener;
    private  Query stuQuery, fireQuery;

    private String busId;
    private boolean exist;
    private Student student;
    private List<Student>studentList;
    private ListView list;
    private StudentAdapter adapter;

    //
    private ProgressDialog progressBar;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseReferenceNot=mFirebaseDatabase.getReference().child("StudentTimeNote");
        mDatabaseReference=mFirebaseDatabase.getReference().child("Student");
        progressBar=new ProgressDialog(getActivity());
        progressBar.setMessage(getString(R.string.p));

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.student_list_fragment, container, false);
        if(getArguments()!=null){
            busId=(String)getArguments().getString("busId");
            stuQuery = mDatabaseReference.orderByChild("busID").equalTo(busId);
            studentList = new ArrayList<>();
            list=(ListView)view.findViewById(R.id.stud_list) ;
            adapter = new StudentAdapter(getActivity(), R.layout.student_list_fragment, studentList);
            list.setAdapter(adapter);
            //Take the information from the database and update it continuously
            mChildEventListener= new ChildEventListener() {
                @SuppressLint("NewApi")
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    exist=false;
                    student=dataSnapshot.getValue(Student.class);
                    student.setStuID((String) dataSnapshot.getKey());
                    Log.d("ADebugTag", "4"+(String) dataSnapshot.getKey());
                        adapter.add(student);
                        
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                }
                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("databaseError", "postComments:onCancelled", databaseError.toException());
                    Toast.makeText(getActivity(), "Failed to load comments.",
                            Toast.LENGTH_SHORT).show();
                    progressBar.dismiss();

                }
            };stuQuery.addChildEventListener(mChildEventListener);
        }
        return view;}


}