package com.project.smartbus10;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    SharedPreferences sp;
    TextView userName,fullName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sp = getSharedPreferences("SignIn",MODE_PRIVATE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //
        if(sp.getString("user_type","").equals("SchoolAdministration")){
            getSupportFragmentManager().beginTransaction().add(R.id.home,new SchoolAdministrationFragment(),"SchoolAdministrationFragment").commit();

        }
        else if(sp.getString("user_type","").equals("Parent")){
            getSupportFragmentManager().beginTransaction().add(R.id.home,new ParentFragment(),"ParentFragment").commit();
        }
        else if(sp.getString("user_type","").equals("Driver")){
            getSupportFragmentManager().beginTransaction().add(R.id.home,new DriverFragment(),"DriverFragment").commit();
        }
        else
            Toast.makeText(Home.this, sp.getString("user_type",""), Toast.LENGTH_SHORT).show();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        userName = (TextView)hView.findViewById(R.id.user_name2);
        userName.setText(sp.getString("UserName",""));
        fullName=(TextView)hView.findViewById(R.id.full_name2);
        fullName.setText(sp.getString("Name",""));
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        }
        else if (id == R.id.nav_profile) {
        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_logout) {
            sp.edit().clear();
            sp.edit().putBoolean("login",false).apply();
            sp.edit().putString("UserName","").apply();
            sp.edit().putString("ID","").apply();
            sp.edit().putString("Name","").apply();
            sp.edit().putString("user_type","").apply();
            SignInPage.RC_SIGN_IN=0;
            finish();
            Intent goToSignInPage = new Intent( Home.this,SignInPage.class);
            startActivity(goToSignInPage);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public void insertIntoDb(View v) {
    }
}
