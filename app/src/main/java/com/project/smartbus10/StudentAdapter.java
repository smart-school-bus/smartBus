package com.project.smartbus10;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class StudentAdapter extends ArrayAdapter<Student> {

    public StudentAdapter(@NonNull Context context, int resource , List<Student> students) {
        super(context,resource, students);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup ViewParent) {

        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.bus_item, ViewParent, false);
        }
        RelativeLayout layout=convertView.findViewById(R.id.busitem);
        ImageView image=(ImageView)convertView.findViewById(R.id.profile_image);
        TextView nameText=(TextView)convertView.findViewById(R.id.name_text_view);
        TextView text=(TextView)convertView.findViewById(R.id.stateText);
        ImageView state =(ImageView)convertView.findViewById(R.id.state);

        Student student = getItem(position);
        image.setImageResource(R.drawable.student_);

        nameText.setText(student.getFirstName()+" "+student.getLastName());
        if(student.getStuAttendance()==false){
          layout.setBackgroundResource(R.color.colorHint);


        }else if(student.getState().equals("wrong")){
            layout.setBackgroundResource(R.color.wrong);
        }
        else {
            layout.setBackgroundResource(R.color.colorButtonText);
        }
         if(student.getState().equals("out")||(student.getState().equals("Wrong"))){
            state.setImageResource(R.drawable.ic_stop);
            text.setText("out Bus");
        }else if(student.getState().equals("on")){
            state.setImageResource(R.drawable.ic_action_playback_stop);
            text.setText("on Bus");

        }



        return convertView;
    }

}
