package com.project.smartbus10;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences("SignIn",MODE_PRIVATE);
        if((sp.getBoolean("login",false))){
            Intent GoToSignIn = new Intent(MainActivity.this,Home.class);
            startActivity(GoToSignIn);
            finish();
        }
        else
        { final Button button =findViewById(R.id.start_button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        Intent GoToSignIn = new Intent(MainActivity.this,SignInPage.class);
                        startActivity(GoToSignIn);
                        finish();
                }

            });}

    }

}
