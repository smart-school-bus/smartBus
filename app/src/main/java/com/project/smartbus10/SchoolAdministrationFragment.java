package com.project.smartbus10;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SchoolAdministrationFragment extends Fragment {
    private Intent goToListPage;
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
        goToListPage = new Intent(getActivity(), ItemList.class);

        //
        buttonStudents.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goToListPage.putExtra("ListType","Student");
                startActivity(goToListPage);
            }
        });
        //
        buttonParents.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goToListPage.putExtra("ListType","Parent");
                startActivity(goToListPage);

            }
        });

        //
        buttonBuses.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                goToListPage.putExtra("ListType","Bus");
                startActivity(goToListPage);

            }
        });

        //
        buttonDrivers.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            { goToListPage.putExtra("ListType","Driver");
                startActivity(goToListPage);

            }
        });

        //
        buttonNotification.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {


            }
        });
        //
        buttonComplains.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goToListPage.putExtra("ListType","Complaints");
                startActivity(goToListPage);
            }
        });

       //
        return view;


    }
}



