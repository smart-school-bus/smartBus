package com.project.smartbus10;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ItemAdapter extends ArrayAdapter<Item> {
    private SharedPreferences sp;
    Context context;
    public ItemAdapter(@NonNull Context context, int resource, List<Item> items) {
        super(context, resource, items);
        this.context=context;


    }

    @SuppressLint("ResourceType")
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
        final Item item = getItem(position);
        if (item.getFirstName() == null && (item.getDescription() == null)) {
            nameText.setText(item.getPlate());
            text.setText(item.getID());
        } else if (item.getDescription() != null) {
            nameText.setText(item.getFirstName());
            text.setText(item.getDescription());

        } else {
            nameText.setText(item.getFirstName() + " " + item.getLastName());
            text.setText(item.getID());
            edit.setImageResource(R.drawable.edit_icon);
            phone.setImageResource(R.drawable.ic_call);
            phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri number = Uri.parse("tel:" + item.getPhone());
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                    getContext().startActivity(callIntent);

                }
            });
        }


        image.setImageResource(item.getProfileImage());
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String listType="";
                Intent goToEditPage = new Intent( context, Registration.class);
                if( item.getID().contains("S")){
                    listType="Student";
                }else if(item.getID().contains("A")){
                    listType="SchoolAdministration";
                }else if(item.getID().contains("D"))
                {listType="Driver";}
                else if(item.getID().contains("P"))
                {listType="Parent";}
                goToEditPage.putExtra("ListType", listType);
                goToEditPage.putExtra("idItem", item.getID());
                context.startActivity(goToEditPage);

            }
        });
        return convertView;
    }


}