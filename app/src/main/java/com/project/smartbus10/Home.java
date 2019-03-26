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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private SharedPreferences sp;
    private TextView ID,fullName;

    private static final int PERMISSIONS_REQUEST = 1;
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
            Log.d("ADebugTag", "Value: " +sp.getString("user_type", "") +"");
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
        ID = (TextView)hView.findViewById(R.id.id);
        ID.setText(sp.getString("ID",""));
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
            finishAffinity();
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
            Intent goToProfilePage = new Intent(Home.this, DetailsActivity.class);
            goToProfilePage.putExtra("ListType",sp.getString("user_type",""));
            goToProfilePage.putExtra("idItem",sp.getString("ID",""));
            goToProfilePage.putExtra("Profile","Profile");
            startActivity(goToProfilePage);
        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_logout) {
            Intent serviceIntent = new Intent(Home.this, TrackingService.class);
            sp.edit().clear().commit();
            sp.edit().putBoolean("login",false).apply();
            sp.edit().putString("ID","").apply();
            sp.edit().putString("Name","").apply();
            sp.edit().putString("user_type","").apply();
            SignInPage.RC_SIGN_IN=0;
            stopService(serviceIntent);
            finish();
            Intent goToSignInPage = new Intent( Home.this,SignInPage.class);
            startActivity(goToSignInPage);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
