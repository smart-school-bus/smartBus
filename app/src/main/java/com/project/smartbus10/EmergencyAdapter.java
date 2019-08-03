package com.project.smartbus10;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.smartbus10.directionhelpers.Emergency;

import java.util.List;

public class EmergencyAdapter extends ArrayAdapter<Emergency> {
   private Context context;

    public EmergencyAdapter(@NonNull Context context, int resource, List<Emergency> emergencyList) {
        super(context, resource, emergencyList);
        this.context = context;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup ViewParent) {

        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_list, ViewParent, false);
        }
        ImageView image = (ImageView) convertView.findViewById(R.id.profile_image);
        TextView nameText = (TextView) convertView.findViewById(R.id.name_text_view);
        TextView text = (TextView) convertView.findViewById(R.id.text_view);
        ImageView phone = (ImageView) convertView.findViewById(R.id.phone);
        final ImageView edit = (ImageView) convertView.findViewById(R.id.edit);
        phone.setVisibility(View.GONE);
        edit.setVisibility(View.GONE);
        final Emergency emergency = getItem(position);
        image.setImageResource(R.drawable.ic_error);
        if(emergency!=null){
            if(emergency.getDriver()!=null){
                nameText.setText(emergency.getDriver().getFirstName()+" "+emergency.getDriver().getLastName());
            }
            text.setText(emergency.getMessage());
        }

        return convertView;
    }
}
