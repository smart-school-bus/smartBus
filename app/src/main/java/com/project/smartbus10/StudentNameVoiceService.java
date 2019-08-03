package com.project.smartbus10;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class StudentNameVoiceService extends Service  implements
        TextToSpeech.OnInitListener {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private TextToSpeech tts;
    private BusStop busStop;
    private String studentName;
    public StudentNameVoiceService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        tts = new TextToSpeech(this, this);
        Log.d("databaseError", "open");
        tts.setSpeechRate(0.5f);
        studentName="";
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        busStop= (BusStop) intent.getSerializableExtra("busStop");
        Log.d("databaseError", "open");
        if(busStop!=null)
        {  Log.d("databaseError", "if");
            studentList(busStop);}
            else Log.d("databaseError", "else");
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.UK);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("databaseError", "للأسف لم يتم اعتماده");
            }

        } else { Log.e("databaseError", "Initilization فشل!");}


    }
    private void speakOut( ) {
        Log.d("databaseError", "speak");
        String text =studentName;
        Log.d("databaseError", "speak"+studentName);
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
    private void studentList(final BusStop busStop){
        final String stud="Student";
        Log.d("databaseError", "stu");
        mDatabaseReference.child("Student").orderByChild("busStopID").equalTo(busStop.getBusStopID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(int j=0;j<2;j++){
                if(dataSnapshot.exists()){
                   int i= (int) dataSnapshot.getChildrenCount();
                    Log.d("databaseError", "exite");
                    for(DataSnapshot mDataSnapshot:dataSnapshot.getChildren()){
                        i--;
                        Log.d("databaseError", "for");
                        Student student=mDataSnapshot.getValue(Student.class);
                        student.setStuID(mDataSnapshot.getKey());
                        if(student.getStuAttendance()==true&&student.getState().equals("on")){
                            Log.d("databaseError", "stet"+student.getFirstName());
                            studentName+= stud+" " +student.getFirstName()+" "+student.getLastName();
                        }
                        if(i==0){
                            speakOut();}
                    }

                }}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
