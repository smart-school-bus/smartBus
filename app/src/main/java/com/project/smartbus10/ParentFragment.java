package com.project.smartbus10;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ParentFragment extends Fragment {
    private Button addcomplaints;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.parent_fregment, container, false);
        addcomplaints= view.findViewById(R.id.add_complains);
        addcomplaints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToComplaintsActivity = new Intent(getActivity(), ComplaintsActivity.class);
                startActivity(goToComplaintsActivity);
            }
        });
        return view;
    }
}
