package com.project.smartbus10;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BluetoothService extends Service {
    private static final String DEVICE_NAME = "HC-05";
    private final UUID PORT_UUID = UUID.fromString("69831847-d759-4128-bac7-ff55e25a1180");
    int studentNUM;
    boolean stopThread;
    private SharedPreferences sp;
    private String busStopId;
    private String busID;
    private List<String> tagStudent;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReferenceBus;
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> bondedDevices;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private BluetoothDevice device;
    private String tag;
    private boolean found;
    private Thread thread;
    private byte buffer[];
    private int bufferPosition;

    public BluetoothService() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Student");
        mDatabaseReferenceBus = mFirebaseDatabase.getReference().child("Bus");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        tagStudent = new ArrayList<>();
    }

    @Override
    public void onCreate() {
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Device doesnt Support Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            if (!bluetoothAdapter.isEnabled()) {

                Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableAdapter);

            } else {
                bondedDevices = bluetoothAdapter.getBondedDevices();
            }
        }

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getSerializableExtra("busStopId") != null) {
            busStopId = (String) intent.getSerializableExtra("busStopId");
        }
        if (intent.getSerializableExtra("busID") != null) {
            busID = (String) intent.getSerializableExtra("busID");
        }
        ContactWithRFIDreader();
        getStudentTagFromData();
        return Service.START_NOT_STICKY;
    }

    private void ContactWithRFIDreader() {
        try {


            if (bondedDevices.isEmpty()) {

                Toast.makeText(getApplicationContext(), "Please Pair the Device first", Toast.LENGTH_SHORT).show();

            } else {

                for (BluetoothDevice iterator : bondedDevices) {

                    if (iterator.getName().equals(DEVICE_NAME)) {
                        device = iterator;
                        try {
                            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
                            socket.connect();
                            outputStream = socket.getOutputStream();
                            inputStream = socket.getInputStream();
                            beginListenForData();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        found = true;

                        break;

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void beginListenForData() {
        final Handler handler = new Handler();
        stopThread = false;
        buffer = new byte[1024];
        Thread thread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopThread) {
                    try {
                        int byteCount = inputStream.available();
                        if (byteCount > 0) {
                            byte[] rawBytes = new byte[byteCount];
                            inputStream.read(rawBytes);
                            final String string = new String(rawBytes, "UTF-8");
                            handler.post(new Runnable() {
                                public void run() {
                                    tag = string;
                                    if (tag != null) {
                                        if (matchTag()) {
                                            updateInfo();
                                        } else {

                                            studentWrong();

                                        }
                                    }
                                }
                            });

                        }
                    } catch (IOException ex) {
                        stopThread = true;
                    }
                }
            }
        });

        thread.start();
    }

    private void updateInfo() {
        getbusstudentsNumber();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("d/M/Y");
        final String date1 = sdf.format(date);
        SimpleDateFormat sdf1 = new SimpleDateFormat("h:m a");
        final String hour = sdf1.format(date);
        mDatabaseReference.orderByChild("tag").equalTo(tag).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Student student = dataSnapshot.getValue(Student.class);
                    student.setStuID(dataSnapshot.getKey());
                    if (student.getState().equals("on")) {
                        mDatabaseReference.child(student.getStuID()).child("state").setValue("out");
                        mDatabaseReference.child(student.getStuID()).child("leaveTime").setValue(hour);
                        mDatabaseReference.child(student.getStuID()).child("date").setValue(date1);

                        mDatabaseReferenceBus.child(busID).child("studentsNumber").setValue(studentNUM - 1);
                    } else if (student.getState().equals("out")) {
                        mDatabaseReference.child(student.getStuID()).child("state").setValue("on");
                        mDatabaseReference.child(student.getStuID()).child("leaveTime").setValue("");
                        mDatabaseReference.child(student.getStuID()).child("enterTime:").setValue(hour);
                        mDatabaseReference.child(student.getStuID()).child("date").setValue(date1);
                        mDatabaseReferenceBus.child(busID).child("studentsNumber").setValue(studentNUM + 1);
                    } else if (student.getState().equals("wrong")) {
                        mDatabaseReference.child(student.getStuID()).child("state").setValue("on");
                        mDatabaseReference.child(student.getStuID()).child("leaveTime").setValue("");
                        mDatabaseReference.child(student.getStuID()).child("enterTime:").setValue(hour);
                        mDatabaseReference.child(student.getStuID()).child("date").setValue(date1);
                        mDatabaseReferenceBus.child(busID).child("studentsNumber").setValue(studentNUM - 1);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getbusstudentsNumber() {
        mDatabaseReferenceBus.child(busID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                        if (mDataSnapshot.getKey().equals("studentsNumber:")) {
                            studentNUM = Integer.parseInt(mDataSnapshot.getValue().toString());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void studentWrong() {
        Intent serviceIntent = new Intent(this, PlayAudioService.class);
        serviceIntent.putExtra("audio", "https://firebasestorage.googleapis.com/v0/b/smart-bus10.appspot.com/o/audio%2Fnotfiction%2Fwrong.mp3?alt=media&token=bbbd6109-bad2-4a27-8fc3-3d5ba1fb504d");
        startService(serviceIntent);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("d/M/Y");
        final String date1 = sdf.format(date);
        SimpleDateFormat sdf1 = new SimpleDateFormat("h:m a");
        final String hour = sdf1.format(date);
        mDatabaseReference.orderByChild("tag").equalTo(tag).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Student student = dataSnapshot.getValue(Student.class);
                    student.setStuID(dataSnapshot.getKey());
                    mDatabaseReference.child(student.getStuID()).child("state").setValue("wrong");
                    mDatabaseReference.child(student.getStuID()).child("leaveTime").setValue(hour);
                    mDatabaseReference.child(student.getStuID()).child("date").setValue(date1);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getStudentTagFromData() {
        Query fireQuery;
        if (busStopId != null) {
            fireQuery = mDatabaseReference.orderByChild("busStopId").equalTo(busStopId);
        } else {
            fireQuery = mDatabaseReference.orderByChild("busID").equalTo(busID);
        }
        fireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                        Student student = mDataSnapshot.getValue(Student.class);
                        tagStudent.add(student.getTag());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public boolean matchTag() {
        if (tagStudent != null) {
            for (String tagS : tagStudent) {
                if (tagS.equals(tag)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
