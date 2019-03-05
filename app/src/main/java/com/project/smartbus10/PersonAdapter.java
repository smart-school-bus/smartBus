package com.project.smartbus10;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class PersonAdapter extends ArrayAdapter<Person> {

    public PersonAdapter(@NonNull Context context, int resource, List<Person> persons) {
        super(context, resource, persons);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup ViewParent) {

        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_list, ViewParent, false);
        }
        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        TextView nameText = (TextView) convertView.findViewById(R.id.name_text_view);
        TextView text = (TextView) convertView.findViewById(R.id.text_view);
        ImageView phone = (ImageView) convertView.findViewById(R.id.phone);
        ImageView edit = (ImageView) convertView.findViewById(R.id.edit);

        Person person = getItem(position);
        nameText.setText("Name :" + person.getFirstName()+" "+person.getLastName());
        text.setText("ID : " + person.getID());
        image.setImageURI(person.getProfileImage());
        return convertView;
    }
}