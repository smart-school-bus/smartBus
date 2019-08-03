package com.project.smartbus10;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CheckStudentsService extends Service {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private String busStopID;
    private List<Student> studentList;
    public CheckStudentsService() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        studentList=new ArrayList<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        busStopID=intent.getStringExtra("busStopID");
        getStudentListTag(busStopID);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public void getStudentListTag(String busStopID){
        mDatabaseReference.child("Student").orderByChild("busStop").equalTo(busStopID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                { for(DataSnapshot mDataSnapshot :dataSnapshot.getChildren())
                {Student student=mDataSnapshot.getValue(Student.class);
                student.setStuID(mDataSnapshot.getKey());
                if(student.getStuAttendance()==true){
                    studentList.add(student);
                } }} }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void machTag(){


    }


}
