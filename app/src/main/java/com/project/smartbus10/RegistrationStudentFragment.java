package com.project.smartbus10;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RegistrationStudentFragment extends Fragment {
    //
    Query fireQuery;
    //
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReference2;
    //
    private Parent parent;
    private Student student;
    private Student existStudent;
    private Bus bus;
    private BusStop busStop;
    private String stlevel;
    private String studentID;
    private boolean exist;
    //
    private EditText fName;
    private EditText lName;
    private EditText tag;
    private TextView parentName;
    private LinearLayout parent_NAME;
    private Spinner level;
    private Spinner parentSpinner;
    private Spinner busSpinner;
    private Spinner busStopSpinner;
    private ImageButton busStopB;
    private Button insert;
    //
    private List<Bus> busList;
    private List<Parent> parentList;
    private List<BusStop> busStopList;
    private List<String> levelList;
    private ProgressDialog progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.registration_student_fargment, container, false);
        progressBar = new ProgressDialog(getActivity());
        progressBar.setMessage(getString(R.string.p));
        fName = view.findViewById(R.id.f_name);
        lName = view.findViewById(R.id.l_name);
        tag = view.findViewById(R.id.tag);
        level = view.findViewById(R.id.st_level);
        parentName = view.findViewById(R.id.parentName);
        parent_NAME = view.findViewById(R.id.parentname);
        parentSpinner = view.findViewById(R.id.parentId);
        busSpinner = view.findViewById(R.id.bus_num);
        busStopSpinner = view.findViewById(R.id.bus_stop);
        busStopB = view.findViewById(R.id.b_map);
        insert = view.findViewById(R.id.insert);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference2 = mFirebaseDatabase.getReference();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Student");
        if (getArguments() != null) {
            studentID = (String) getArguments().getString("idItem");
            student = (Student) getArguments().getSerializable("item");
            parent = (Parent) getArguments().getSerializable("parent");
            if (studentID != null) {
                bus = new Bus();
                busStop = new BusStop();
                parent = new Parent();
                getInfo();
            } else {
                bus = student.getBus();
                busStop = student.getBusStop();
                student.setBus(null);
                student.setBusStop(null);
                showInfo();
            }
        } else
            parent = new Parent();
        getParentList();
        getBusList();
        levelList();


        parentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int item = adapterView.getSelectedItemPosition();
                parent = (Parent) parentList.get(item);
                if (item != 0) {
                    parent_NAME.setVisibility(View.VISIBLE);
                    parentName.setText(parent.getFirstName() + " " + parent.getSecondName() + " " + parent.getLastName());
                    parentName.setEnabled(false);
                } else {
                    parent_NAME.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        busSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int item = adapterView.getSelectedItemPosition();
                bus = (Bus) busList.get(item);
                getBusStopList(i);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                bus = (Bus) adapterView.getItemAtPosition(0);
            }
        });

        busStopSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int item = adapterView.getSelectedItemPosition();
                busStop = (BusStop) busStopList.get(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                bus = (Bus) adapterView.getItemAtPosition(0);
            }
        });
        level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int item = adapterView.getSelectedItemPosition();
                stlevel = levelList.get(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getArguments() == null) {
                    if (verifyInput()) {
                        progressBar.show();
                        student = new Student();
                        student.setFirstName("" + fName.getText());
                        student.setLastName("" + lName.getText());
                        student.setTag("" + tag.getText());
                        student.setLevel(stlevel.toString());
                        student.setStuAttendance(true);
                        generateId();
                        checkExistTag();
                    }
                } else {

                    progressBar.show();
                    if (verifyInput()) {
                        studentID=student.getStuID();
                        student.setStuID(null);
                        student.setTag("" + tag.getText());
                        student.setLevel(stlevel.toString());
                        updateStudentInfo();
                    }
                    progressBar.dismiss();


                }
            }
        });
        return view;
    }

    public void generateId() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMdkms");
        studentID = "S" + sdf.format(date);
    }

    public boolean verifyInput() {
        if (fName.getText().toString().isEmpty()) {
            fName.setError(getString(R.string.error_field_required));
            fName.requestFocus();
            return false;
        }
        if (lName.getText().toString().isEmpty()) {
            lName.setError(getString(R.string.error_field_required));
            lName.requestFocus();
            return false;
        }
        if (tag.getText().toString().isEmpty()) {
            tag.setError(getString(R.string.error_field_required));
            tag.requestFocus();
            return false;
        }
        if (parent.getPatentID() == null) {
            ((TextView) parentSpinner.getSelectedView()).setError(getString(R.string.error_field_required));
            ((TextView) parentSpinner.getSelectedView()).requestFocus();
            return false;
        }

        if (bus.getID() == null) {
            ((TextView) busSpinner.getSelectedView()).setError(getString(R.string.error_field_required));
            ((TextView) busSpinner.getSelectedView()).requestFocus();
            return false;
        }
        if (busStop.getBusStopID() == null) {
            ((TextView) busStopSpinner.getSelectedView()).setError(getString(R.string.error_field_required));
            ((TextView) busStopSpinner.getSelectedView()).requestFocus();
            return false;
        }

        return true;
    }

    public void checkExist() {
        exist = false;
        Query fireQuery = mDatabaseReference2.child("Student").orderByChild("parentID").equalTo(parent.getPatentID());
        fireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                        existStudent = new Student();
                        existStudent = mDataSnapshot.getValue(Student.class);
                        existStudent.setStuID(mDataSnapshot.getKey());
                        if (existStudent.getFirstName().equals(student.getFirstName())) {
                            Log.d("ADebugTag", "Value: student1");
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle(R.string.st_exist);
                            builder.setMessage('\n' + existStudent.getStuID() + '\n' + existStudent.getFirstName() + " " + existStudent.getLastName());
                            builder.setPositiveButton(R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        @SuppressLint("NewApi")
                                        public void onClick(DialogInterface dialog, int which) {
                                            fName.setText("");
                                            lName.setText("");
                                            tag.setText("");
                                            progressBar.dismiss();
                                            getClass();
                                        }
                                    });
                            builder.show();
                            exist = true;
                            break;

                        }
                    }
                }
                if (exist == false) {
                    insertStudent();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void checkExistTag() {
        Query fireQuery = mDatabaseReference2.child("Student").orderByChild("tag").equalTo(tag.getText().toString());
        fireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.st_exist);
                    builder.setMessage("Tag is existing");
                    builder.setPositiveButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @SuppressLint("NewApi")
                                public void onClick(DialogInterface dialog, int which) {
                                    fName.setText("");
                                    lName.setText("");
                                    tag.setText("");
                                    progressBar.dismiss();
                                    getClass();
                                }
                            });
                    builder.show();
                } else {
                    Log.d("ADebugTag", "Value: student4");
                    checkExist();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void insertStudent() {
        try {

            mDatabaseReference.child(studentID).setValue(student).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        fName.setText("");
                        lName.setText("");
                        tag.setText("");
                        Toast.makeText(getActivity(), R.string.su, Toast.LENGTH_SHORT).show();
                        mDatabaseReference.child(studentID).child("busID").setValue(bus.getID());
                        mDatabaseReference.child(studentID).child("busStopID").setValue(busStop.getBusStopID());
                        mDatabaseReference.child(studentID).child("parentID").setValue(parent.getPatentID());
                        student.setStuID(studentID);
                    } else {

                        Toast.makeText(getActivity(), R.string.unsu, Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } catch (Exception e) {

            // generic exception handling
            e.printStackTrace();
        }
        progressBar.dismiss();
    }

    public void getParentList() {
        parentList = new ArrayList<>();
        if (parent != null) {
            parentList.add(parent);
        } else
            parentList.add(new Parent());
        fireQuery = mDatabaseReference2.child("Parent");

        fireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                        Parent p = mDataSnapshot.getValue(Parent.class);
                        p.setPatentID(mDataSnapshot.getKey().toString());
                        if ((parent == null || parent.getPatentID() == null) || !(parent.getPatentID().equals(p.getPatentID()))) {
                            parentList.add(p);
                            final ArrayAdapter<Parent> adapter = new ArrayAdapter<Parent>(getActivity(), R.layout.spinner_row, parentList);
                            parentSpinner.setAdapter(adapter);
                        } else parent = mDataSnapshot.getValue(Parent.class);
                    }
                } else {
                    parentList.clear();
                    parentList.add(new Parent());
                    final ArrayAdapter<Parent> adapter = new ArrayAdapter<Parent>(getActivity(), R.layout.spinner_row, parentList);
                    parentSpinner.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });
    }

    public void getBusList() {
        busList = new ArrayList<>();
        if (student == null) {
            busList.add(new Bus());
        }
        mDatabaseReference2.child("Bus").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                        Bus b = new Bus();
                        b.setID(mDataSnapshot.getKey().toString());
                        if (student == null || bus == null || !(bus.getID().equals(b.getID()))) {
                            busList.add(b);
                            final ArrayAdapter<Bus> adapter = new ArrayAdapter<Bus>(getActivity(), R.layout.spinner_row, busList);
                            busSpinner.setAdapter(adapter);
                        } else busList.add(0, b);

                    }
                } else {
                    busList.clear();
                    busList.add(new Bus());
                    final ArrayAdapter<Bus> adapter = new ArrayAdapter<Bus>(getActivity(), R.layout.spinner_row, busList);
                    busSpinner.setAdapter(adapter);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }


        });
    }

    public void getBusStopList(int i) {
        busStopList = new ArrayList<>();
        if (student == null) {
            busStopList.add(new BusStop());
        }
        if (i != 0) {
            fireQuery = mDatabaseReference2.child("BusStop").orderByChild("busID").equalTo(bus.getID());
        } else {
            fireQuery = mDatabaseReference2.child("BusStop");
        }
        fireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                        BusStop busStop = mDataSnapshot.getValue(BusStop.class);
                        busStop.setBusStopID(mDataSnapshot.getKey().toString());
                        busStopList.add(busStop);
                        final ArrayAdapter<BusStop> adapter = new ArrayAdapter<BusStop>(getActivity(), R.layout.spinner_row, busStopList);
                        busStopSpinner.setAdapter(adapter);
                    }
                } else {
                    busStopList.clear();
                    busStopList.add(new BusStop());
                    final ArrayAdapter<BusStop> adapter = new ArrayAdapter<BusStop>(getActivity(), R.layout.spinner_row, busStopList);
                    busStopSpinner.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });
    }

    public void levelList() {
        levelList = new ArrayList<>();
        if (student != null) {
            levelList.add(0, student.getLevel());
        }
        for (int i = 1; i <= 12; i++) {
            levelList.add(getString(R.string.level) + i);
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_row, levelList);
        level.setAdapter(adapter);
    }

    //Edit method
    // get Info from
    private void getInfo() {
        mDatabaseReference.child(studentID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    student = dataSnapshot.getValue(Student.class);
                    student.setStuID(dataSnapshot.getKey().toString());
                    bus.setID(dataSnapshot.child("busID").getValue().toString());
                    busStop.setBusStopID(dataSnapshot.child("busID").getValue().toString());
                    parent.setPatentID(dataSnapshot.child("parentID").getValue().toString());
                    getBusList();
                    showInfo();
                } else {
                    progressBar.dismiss();
                    Toast.makeText(getActivity(), R.string.error_database, Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (databaseError != null) {
                    Toast.makeText(getActivity(), R.string.error_database, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void showInfo() {
        fName.setEnabled(false);
        fName.setText(student.getFirstName());
        lName.setEnabled(false);
        lName.setText(student.getLastName());
        tag.setText(student.getTag());

    }

    public void updateStudentInfo() {
        mDatabaseReference.child(studentID).setValue(student).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mDatabaseReference.child(studentID).child("busID").setValue(bus.getID());
                    mDatabaseReference.child(studentID).child("busStopID").setValue(busStop.getBusStopID());
                    mDatabaseReference.child(studentID).child("parentID").setValue(parent.getPatentID());
                    student.setStuID(studentID);
                    Toast.makeText(getActivity(), R.string.su, Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(getActivity(), R.string.unsu, Toast.LENGTH_SHORT).show();
                }

            }
        });
        progressBar.dismiss();

    }
}

