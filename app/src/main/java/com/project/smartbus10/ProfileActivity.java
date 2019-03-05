package com.project.smartbus10;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent i = getIntent();
        Parent parent = (Parent)i.getSerializableExtra("Person");

        ImageView imageProfile=(ImageView)findViewById(R.id.image_profile);
        TextView nameText=(TextView) findViewById(R.id.name);
        TextView userName=(TextView) findViewById(R.id.user_name);
        TextView phone=(TextView) findViewById(R.id.phone);
        TextView address=(TextView) findViewById(R.id.home_address);
        nameText.setText(parent.getFirstName());
        userName.setText(parent.getUserName());
        phone.setText(""+parent.getPhone());

    }
}
