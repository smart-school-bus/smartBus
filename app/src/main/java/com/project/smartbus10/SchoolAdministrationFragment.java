package com.project.smartbus10;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SchoolAdministrationFragment extends Fragment {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.school_administration_fregment, container, false);

        Button buttonStudents=(Button)view.findViewById(R.id.students);
        Button buttonParents=(Button)view.findViewById(R.id.parent);
        Button buttonBuses=(Button)view.findViewById(R.id.bus);
        Button buttonDrivers=(Button)view.findViewById(R.id.drivers);
        Button buttonNotification=(Button)view.findViewById(R.id.notification);
        Button buttonComplains=(Button)view.findViewById(R.id.complains);

        //
        buttonStudents.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent GoToListPage = new Intent(getActivity(), PersonList.class);
                GoToListPage.putExtra("Person","Student");
                startActivity(GoToListPage);
                Log.d("ADebugTag", "Value: " +"hi hi hi");
                // do something
            }
        });
        //
        buttonParents.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent GoToListPage = new Intent(getActivity(), PersonList.class);
                GoToListPage.putExtra("Person","Parent");
                startActivity(GoToListPage);
                Log.d("ADebugTag", "Value: " +"hi hi hi");
                // do something
            }
        });

        //
        buttonBuses.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent GoToHomePage = new Intent(getActivity(), SignInPage.class);
                startActivity(GoToHomePage);
                Log.d("ADebugTag", "Value: " +"hi hi hi");
                // do something
            }
        });

        //
        buttonDrivers.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent GoToListPage = new Intent(getActivity(), PersonList.class);
                GoToListPage.putExtra("Person","Driver");
                startActivity(GoToListPage);
                Log.d("ADebugTag", "Value: " +"hi hi hi");
                // do something
            }
        });

        //
        buttonNotification.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent GoToHomePage = new Intent(getActivity(), SignInPage.class);
                startActivity(GoToHomePage);
                Log.d("ADebugTag", "Value: " +"hi hi hi");
                // do something
            }
        });
        //
        buttonComplains.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent GoToHomePage = new Intent(getActivity(), SignInPage.class);
                startActivity(GoToHomePage);
                Log.d("ADebugTag", "Value: " +"hi hi hi");
                // do something
            }
        });

       //
        return view;


    }

}
