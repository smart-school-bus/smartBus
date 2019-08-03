package com.project.smartbus10;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BusDetailsActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReferenceDelete;
    private DatabaseReference mDatabaseReferenceDriver;
    private Query deleteQuery;
    private SharedPreferences sp;

    private String busId;
    private String driverId;
    private TextView busID;
    private TextView plate;
    private TextView numberPassengers;
    private TextView driverName;
    private ImageButton loc;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private ImageButton edit;

    private ProgressDialog progressBar;


    //
    private Bus bus;
    private Query fireQuery;
    private static boolean clickEdit;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_details);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(mToolbar);


        //
        edit = findViewById(R.id.edit);
        busID = findViewById(R.id.bus_id);
        plate = findViewById(R.id.plate);
        numberPassengers = findViewById(R.id.num_p);
        driverName = findViewById(R.id.driver_name);
        loc = findViewById(R.id.b_map);
        Intent i = getIntent();
        busId = (String) i.getSerializableExtra("busId");
        driverId = (String) i.getSerializableExtra("driverId");
        progressBar = new ProgressDialog(BusDetailsActivity.this);
        progressBar.setMessage(getString(R.string.p));

        sp = getSharedPreferences("SignIn", MODE_PRIVATE);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Bus");
        mDatabaseReferenceDelete = mFirebaseDatabase.getReference();
        mDatabaseReferenceDriver = mFirebaseDatabase.getReference("Driver");
        progressBar.show();
        isOnline();
        getBus();//
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        clickEdit = true;
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sp.getString("user_type", "").equals("Driver")) {
                    Intent goToHmePage = new Intent(BusDetailsActivity.this, Home.class);
                    startActivity(goToHmePage);
                } else {
                    Intent goToListPage = new Intent(BusDetailsActivity.this, ItemList.class);
                    goToListPage.putExtra("ListType", "Bus");
                    startActivity(goToListPage);
                }
            }

        });
        if ((sp.getString("user_type", "").equals("SchoolAdministration"))) {
            edit.setVisibility(View.VISIBLE);
        } else edit.setVisibility(View.GONE);
        edit.setBackgroundResource(R.drawable.edit_bus);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickEdit == true) {
                    mViewPager.setCurrentItem(1);
                    clickEdit = false;
                    edit.setBackgroundResource(R.drawable.ic_delete);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BusDetailsActivity.this);
                    builder.setMessage(R.string.delete_mas);
                    builder.setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @SuppressLint("NewApi")
                                public void onClick(DialogInterface dialog, int which) {
                                    updateStudentsInfo();
                                    Intent goToListPage = new Intent(BusDetailsActivity.this, ItemList.class);
                                    goToListPage.putExtra("ListType", "Bus");
                                    startActivity(goToListPage);
                                }


                            });
                    builder.setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    clickEdit = true;
                                    edit.setBackgroundResource(R.drawable.edit_bus);
                                    getClass();
                                }
                            });
                    builder.show();
                }

            }

        });

        //Tracking bus
        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToMap = new Intent(BusDetailsActivity.this, BusMapActivity.class);
                goToMap.putExtra("busId", busId);
                startActivity(goToMap);
            }
        });


    }
    //
    //

    public void getBus() {
        if (busId != null) {
            mDatabaseReference.child(busId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        bus = dataSnapshot.getValue(Bus.class);
                        Log.d("geoLocate", "postComments:onCancelled"+ bus.getStudentsNumber());
                        numberPassengers.setText(bus.getStudentsNumber() + "");
                        bus.setID(dataSnapshot.getKey().toString());
                        if (dataSnapshot.child("driverID").getValue() != null)
                            getDriver(dataSnapshot.child("driverID").getValue().toString());
                        progressBar.dismiss();
                    }else  progressBar.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progressBar.dismiss();
                }
            });
        }

    }


    public void getDriver(String id) {
        mDatabaseReferenceDriver.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    bus.setDriver(dataSnapshot.getValue(Driver.class));
                    bus.getDriver().setDriverID(dataSnapshot.getKey().toString());
                    showInfo();
                    progressBar.dismiss();
                }else  progressBar.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void showInfo() {
        busID.setText(bus.getID());
        busId = bus.getID();
        plate.setText(bus.getPlate());
        numberPassengers.setText(bus.getStudentsNumber() + "");
        if (bus.getDriver() != null)
            driverName.setText(bus.getDriver().getFirstName() + " " + bus.getDriver().getLastName());
        else driverName.setText("");
    }

    private void updateStudentsInfo() {
        deleteQuery = mDatabaseReferenceDelete.child("Student").orderByChild("busID").equalTo(busId);
        deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                        mDatabaseReferenceDelete.child("Student").child(mDataSnapshot.getKey()).child("busID").setValue("");
                        mDatabaseReferenceDelete.child("Student").child(mDataSnapshot.getKey()).child("busStopID").setValue("");
                    }
                    deleteBusStops();
                } else deleteBusStops();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void deleteBusStops() {
        deleteQuery = mDatabaseReferenceDelete.child("BusStop").orderByChild("busID").equalTo(busId);
        deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot mSnapshot : dataSnapshot.getChildren()) {
                        mSnapshot.getRef().removeValue();
                    }
                }
                delete();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void delete() {
        mDatabaseReferenceDelete.child("Bus").child(busId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot mSnapshot : dataSnapshot.getChildren()) {
                        mSnapshot.getRef().removeValue();
                    }
                } else
                    Toast.makeText(BusDetailsActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("s", "onCancelled", databaseError.toException());
            }
        });

    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            Toast.makeText(this, "No Internet connection!", Toast.LENGTH_LONG).show();
            progressBar.dismiss();
            return false;
        }
        return true;
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            // getItem is called to instantiate the fragment for the given page.
            // Return a StudentListFragment (defined as a static inner class below).
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return StudentListFragment.newInstance(busId, sp.getString("user_type", ""));
                case 1: // Fragment # 0 - This will show FirstFragment different title

                    return (Fragment) BusStopsListFragment.newInstance(busId, sp.getString("user_type", ""));
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }


    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class StudentListFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        private FirebaseDatabase mFirebaseDatabase;
        private DatabaseReference mDatabaseReference;
        private DatabaseReference mDatabaseReferenceNot;
        private ChildEventListener mChildEventListener;
        private Query stuQuery, fireQuery;

        private String busId;
        private String type;
        private Student student;
        private List<Student> studentList;
        private ListView list;
        private StudentAdapter adapter;

        public StudentListFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static StudentListFragment newInstance(String ID, String type) {
            StudentListFragment fragment = new StudentListFragment();
            Bundle args = new Bundle();
            args.putString("busId", ID);
            args.putString("type", type);
            fragment.setArguments(args);
            return fragment;
        }


        //
        private ProgressDialog progressBar;

        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mDatabaseReferenceNot = mFirebaseDatabase.getReference();
            mDatabaseReference = mFirebaseDatabase.getReference().child("Student");
            progressBar = new ProgressDialog(getActivity());
            progressBar.setMessage(getString(R.string.p));


        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.student_list_fragment, container, false);

            if (getArguments() != null) {
                busId = (String) getArguments().getString("busId");
                type = (String) getArguments().getString("type");
                stuQuery = mDatabaseReference.orderByChild("busID").equalTo(busId);
                studentList = new ArrayList<>();
                list = (ListView) view.findViewById(R.id.stud_list);
                adapter = new StudentAdapter(getActivity(), R.layout.student_list_fragment, studentList);
                list.setAdapter(adapter);
                int i = 0;
                getStudent();


            }
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Student student = studentList.get(i);
                    if (type.equals("Driver")) {
                        Intent goToMap = new Intent(getActivity(), BusStopsMapsActivity.class);
                        goToMap.putExtra("parentId", student.getParent().getPatentID());
                        goToMap.putExtra("busStopId", student.getBusStop().getBusStopID());
                        startActivity(goToMap);
                    } else if (type.equals("SchoolAdministration")) {
                        Intent goToStudentDetails = new Intent(getActivity(), DetailsActivity.class);
                        goToStudentDetails.putExtra("ListType", "Student");
                        goToStudentDetails.putExtra("idItem", student.getStuID());
                        goToStudentDetails.putExtra("busPage", busId);
                        startActivity(goToStudentDetails);
                    }
                }
            });
            return view;
        }


        public void getStudent() {
            //Take the information from the database and update it continuously
            mChildEventListener = new ChildEventListener() {
                @SuppressLint("NewApi")
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    student = dataSnapshot.getValue(Student.class);
                    student.setStuID(dataSnapshot.getKey());
                    Parent parent = new Parent();
                    if (dataSnapshot.child("parentID").getValue() != null) {
                        parent.setPatentID(dataSnapshot.child("parentID").getValue().toString());
                    }
                    student.setParent(parent);
                    BusStop busStop = new BusStop();
                    if (dataSnapshot.child("busStopID").getValue() != null) {
                        busStop.setBusStopID(dataSnapshot.child("busStopID").getValue().toString());
                        student.setBusStop(busStop);
                    }
                    adapter.add(student);


                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    student = dataSnapshot.getValue(Student.class);
                    student.setStuID(dataSnapshot.getKey());
                    Parent parent = new Parent();
                    if (dataSnapshot.child("parentID").getValue() != null) {
                        parent.setPatentID(dataSnapshot.child("parentID").getValue().toString());
                    }
                    student.setParent(parent);
                    BusStop busStop = new BusStop();
                    if (dataSnapshot.child("busStopID").getValue().toString() != null) {
                        busStop.setBusStopID(dataSnapshot.child("busStopID").getValue().toString());
                        student.setBusStop(busStop);
                    }
                    studentList.set(getIndex(student.getStuID()), student);
                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    String ID = dataSnapshot.getKey();
                    try {
                        for (Student p : studentList) {
                            if (ID.equals(p.getStuID())) {
                                studentList.remove(p);
                            }
                        }
                    } catch (Exception e) {
                    }
                    adapter.notifyDataSetChanged();
                    ;
                }

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
            };
            stuQuery.addChildEventListener(mChildEventListener);


        }


        public int getIndex(String ID) {
            int i = 0;
            for (Student p : studentList) {
                if (ID.equals(p.getStuID())) {
                    i = studentList.indexOf(p);
                    return i;
                }
            }
            return i;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class BusStopsListFragment extends Fragment {

        private FloatingActionButton fab;
        private String busId;
        private String type;

        //
        private FirebaseDatabase mFirebaseDatabase;
        private DatabaseReference mDatabaseReference;
        private DatabaseReference mDatabaseReferenceStudent;
        private ChildEventListener mChildEventListener;

        private Query BusQuery;


        private BusStop busStop;
        private List<BusStop> busStopList;
        private ListView list;
        private BusStopAdapter adapter;
        private List<String> busStopID;


        private ProgressDialog progressBar;

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public BusStopsListFragment() {

        }


        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static BusStopsListFragment newInstance(String ID, String type) {
            BusStopsListFragment fragment = new BusStopsListFragment();
            Bundle args = new Bundle();
            args.putString("busId", ID);
            args.putString("type", (type));
            fragment.setArguments(args);
            Log.d("geoLocate", "BusStopsListFragment2" + ID);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mDatabaseReferenceStudent = mFirebaseDatabase.getReference();
            mDatabaseReference = mFirebaseDatabase.getReference().child("BusStop");
            progressBar = new ProgressDialog(getActivity());
            progressBar.setMessage(getString(R.string.p));


        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.bus_stops_fragment, container, false);
            fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
            if (getArguments() != null) {
                progressBar.show();
                busId = (String) getArguments().getString("busId");
                type = (String) getArguments().getString("type");
                busStopList = new ArrayList<>();
                busStopID = new ArrayList<>();
                list = (ListView) rootView.findViewById(R.id.bus_stop_list);
                adapter = new BusStopAdapter(getActivity(), R.layout.bus_stops_fragment, busStopList);
                list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        BusStop busStop = busStopList.get(i);
                        if (clickEdit == false) {

                            Intent goToRegistrationBusStop = new Intent(getActivity(), RegistrationBusStop.class);
                            goToRegistrationBusStop.putExtra("busId", busId);
                            goToRegistrationBusStop.putExtra("BusStop", busStop);
                            goToRegistrationBusStop.putExtra("update", false);

                            startActivity(goToRegistrationBusStop);

                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setMessage("BusStopID :" + busStop.getBusStopID() + "\n" + "BusStop Address : " + busStop.getBusStopAddress() + "\n" +
                                    "Order :" + busStop.getBusStopOrder()
                            );
                            builder.setPositiveButton(R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        @SuppressLint("NewApi")
                                        public void onClick(DialogInterface dialog, int which) {
                                            getClass();
                                        }

                                    });
                            builder.show();
                        }

                    }
                });

            }
            //
            BusStopsMapsActivity.latitude = 0;
            BusStopsMapsActivity.longitude = 0;
            BusStopsMapsActivity.stopName = null;

            if (type.equals("SchoolAdministration")) {
                getBusStopInfo(null);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent goToRegistrationBusStop = new Intent(getActivity(), RegistrationBusStop.class);
                        goToRegistrationBusStop.putExtra("busId", busId);
                        goToRegistrationBusStop.putExtra("update", true);

                        startActivity(goToRegistrationBusStop);
                    }
                });
            } else if (type.equals("Driver")) {
                studentsAttendance();
                fab.setVisibility(View.GONE);
            }
            progressBar.dismiss();
            //

            return rootView;
        }

        private void getBusStopInfo(final String ID) {
            if (ID != null) {
                BusQuery = mDatabaseReference.orderByKey().equalTo(ID);
            } else
                BusQuery = mDatabaseReference.orderByChild("busID").equalTo(busId);
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    busStop = dataSnapshot.getValue(BusStop.class);
                    busStop.setBusStopID(dataSnapshot.getKey());
                    if (busStopList.size() > 0 && ID != null) {
                        if (getIndex(busStop.getBusStopID()) > 0) {
                            busStopList.set(getIndex(busStop.getBusStopID()), busStop);
                            adapter.notifyDataSetChanged();
                        } else adapter.add(busStop);
                    } else adapter.add(busStop);

                    adapter.sort(new Comparator<BusStop>() {
                        @Override
                        public int compare(BusStop busStop, BusStop t1) {
                            return busStop.getBusStopOrder() - t1.getBusStopOrder();
                        }
                    });

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    busStop = dataSnapshot.getValue(BusStop.class);
                    busStop.setBusStopID(dataSnapshot.getKey());
                    busStopList.set(getIndex(busStop.getBusStopID()), busStop);
                    adapter.sort(new Comparator<BusStop>() {
                        @Override
                        public int compare(BusStop busStop, BusStop t1) {
                            return busStop.getBusStopOrder() - t1.getBusStopOrder();
                        }
                    });
                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    String ID = dataSnapshot.getKey();
                    try {
                        for (BusStop p : busStopList) {
                            if (ID.equals(p.getBusStopID())) {
                                busStopList.remove(p);
                            }
                        }
                    } catch (Exception e) {
                        Log.d("ADebugTag", "Value: " + "error");
                    }
                    adapter.notifyDataSetChanged();
                    ;
                }


                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };


            BusQuery.addChildEventListener(mChildEventListener);
        }

        private void studentsAttendance() {

            Query stuQuery = mDatabaseReferenceStudent.child("Student").orderByChild("busID").equalTo(busId);

            stuQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                            if ((boolean) mDataSnapshot.child("stuAttendance").getValue() == true) {
                                if (mDataSnapshot.child("busStopID").getValue() != null)
                                    if (busStopID.size() > 0) {
                                        if (getIndexBus(mDataSnapshot.child("busStopID").getValue().toString()) == -1) {
                                            getBusStopInfo(mDataSnapshot.child("busStopID").getValue().toString());
                                        }
                                    } else {
                                        busStopID.add(mDataSnapshot.child("busStopID").getValue().toString());
                                        getBusStopInfo(mDataSnapshot.child("busStopID").getValue().toString());
                                    }
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), R.string.trip, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        public int getIndex(String ID) {
            int i = -1;
            for (BusStop p : busStopList) {
                if (ID.equals(p.getBusStopID())) {
                    i = busStopList.indexOf(p);
                    return i;
                }
            }
            return i;
        }

        public int getIndexBus(String ID) {
            int i = -1;
            for (String P : busStopID) {
                if (P.equals(ID)) {
                    i = busStopID.indexOf(P);
                    return i;
                }
            }
            busStopID.add(ID);
            return i;
        }
    }

    @Override
    public void onBackPressed() {
        if (sp.getString("user_type", "").equals("Driver")) {
            Intent goToHmePage = new Intent(BusDetailsActivity.this, Home.class);
            startActivity(goToHmePage);
        } else {
            Intent goToListPage = new Intent(BusDetailsActivity.this, ItemList.class);
            goToListPage.putExtra("ListType", "Bus");
            startActivity(goToListPage);
        }
        finish();
        super.onBackPressed();

    }

}
