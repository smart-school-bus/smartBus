package com.project.smartbus10;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import static android.content.Context.MODE_PRIVATE;

public class ParentFragment extends Fragment {
    private Button addComplaints;
    private Button  childButton;
    private Button  viewBusLocButton;

    private SharedPreferences sp;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getActivity().getSharedPreferences("SignIn",MODE_PRIVATE);

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.parent_fregment, container, false);
        addComplaints = view.findViewById(R.id.add_complains);
        childButton=view.findViewById(R.id.students);
        viewBusLocButton=view.findViewById(R.id.bus_location);
        childButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToChildList = new Intent(getActivity(), ItemList.class);
                goToChildList.putExtra("ListType","Student");
                goToChildList.putExtra("parentId",sp.getString("ID",""));
                startActivity(goToChildList);
            }
        });
        viewBusLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToChildList = new Intent(getActivity(), StudentLocation.class);

                startActivity(goToChildList);
            }
        });
        addComplaints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToComplaintsActivity = new Intent(getActivity(), ComplaintsActivity.class);
                startActivity(goToComplaintsActivity);
            }
        });
        return view;
    }
}
