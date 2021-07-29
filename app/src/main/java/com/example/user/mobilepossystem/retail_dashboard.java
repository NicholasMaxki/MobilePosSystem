package com.example.user.mobilepossystem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class retail_dashboard extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {
private  String session_name,shop_name,un;
private Bundle bundle = new Bundle();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retail_dashboard);
        Intent i = getIntent();
        session_name = i.getExtras().getString("Session ID");
        shop_name = i.getExtras().getString("Shop");
        un = i.getExtras().getString("UN");
        bundle.putString("Session ID", session_name);
        bundle.putString("Shop", shop_name);

        BottomNavigationView bn_retail_dashboard = findViewById(R.id.bn_retail_dashboard);
        Fragment initial = new fragment_retail_dashboard_analytic();
        initial.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,initial).commit();

        bn_retail_dashboard.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment f =null;

                switch (menuItem.getItemId()){
                    case R.id.a: f = new fragment_retail_dashboard_analytic();
                    f.setArguments(bundle);
                    break;
                    case R.id.b: f = new fragment_retail_dashboard_graph();
                    f.setArguments(bundle);
                    break;
                    case R.id.c: f = new fragment_retail_dashboard_transaction();
                    f.setArguments(bundle);
                    break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,f).commit();
                return  true;
            }
        });

        //nav
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        TextView drawer_shop_name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.drawer_shop_name);
        TextView drawer_user_name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.drawer_user_name);
        TextView drawer_user_phone = (TextView) navigationView.getHeaderView(0).findViewById(R.id.drawer_user_phone);
        LinearLayout nav_head = (LinearLayout) navigationView.getHeaderView(0).findViewById(R.id.nav_head);
        drawer_shop_name.setText(shop_name);
        drawer_user_name.setText(un);
        drawer_user_phone.setText(session_name);
        nav_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(retail_dashboard.this, profile.class);
                myIntent.putExtra("Session ID",session_name);
                myIntent.putExtra("Shop",shop_name);
                myIntent.putExtra("UN",un);
                myIntent.putExtra("Mode","Retail");
                startActivity(myIntent);
                finish();
            }
        });
    }
    //nav bar function start
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_checkout) {
            Intent myIntent = new Intent(retail_dashboard.this, retail_main_menu.class);
            myIntent.putExtra("Session ID",session_name);
            myIntent.putExtra("Shop",shop_name);
            myIntent.putExtra("UN",un);
            startActivity(myIntent);
            finish();
        } else if (id == R.id.nav_inventory) {
            Intent myIntent = new Intent(retail_dashboard.this, retail_inventory.class);
            myIntent.putExtra("Session ID",session_name);
            myIntent.putExtra("Shop",shop_name);
            myIntent.putExtra("UN",un);
            startActivity(myIntent);
            finish();
        } else if (id == R.id.nav_dashboard) {

        } else if (id == R.id.nav_restaurant) {
            Intent myIntent = new Intent(retail_dashboard.this, restaurant_role.class);
            myIntent.putExtra("Session ID",session_name);
            myIntent.putExtra("Shop",shop_name);
            myIntent.putExtra("UN",un);
            startActivity(myIntent);
            finish();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
