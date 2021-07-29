package com.example.user.mobilepossystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
public class restaurant_add_new_menu extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{
    private Button btn_restaurant_add,btn_restaurant_reset,btn_restaurant_c;
    private EditText rapn,rapp;
    private Spinner spinner_category2;
    private DatabaseReference db_product,db_category;
    private String session_name,shop_name,un;
    private boolean checked = false,duplicate;
    private final static  String default_spinner = "Select Category";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_add_new_menu);
        Intent i = getIntent();
        session_name = i.getExtras().getString("Session ID");
        shop_name = i.getExtras().getString("Shop");
        un = i.getExtras().getString("UN");

        db_product = FirebaseDatabase.getInstance().getReference("product");

        btn_restaurant_add = (Button)findViewById(R.id.btn_restaurant_add);
        btn_restaurant_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_product();
            }
        });

        btn_restaurant_reset = (Button)findViewById(R.id.btn_restaurant_reset);
        btn_restaurant_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

        rapn=(EditText)findViewById(R.id.rapn);
        rapn.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        rapp=(EditText)findViewById(R.id.rapp);

        spinner_category2 = (Spinner)findViewById(R.id.spinner_category2);
        addItemOnSpinner();

        btn_restaurant_c=(Button)findViewById(R.id.btn_restaurant_c);
        btn_restaurant_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(restaurant_add_new_menu.this, restaurant_add_category.class);
                myIntent.putExtra("Session ID",session_name);
                myIntent.putExtra("Shop",shop_name);
                myIntent.putExtra("UN",un);
                startActivity(myIntent);
            }
        });

        //nav code
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
                Intent myIntent = new Intent(restaurant_add_new_menu.this, profile.class);
                myIntent.putExtra("Session ID",session_name);
                myIntent.putExtra("Shop",shop_name);
                myIntent.putExtra("UN",un);
                startActivity(myIntent);
                finish();
            }
        });
    }
    public void add_product(){
        DatabaseReference d1 = FirebaseDatabase.getInstance().getReference("product").child(session_name).child("Restaurant").child(rapn.getText().toString());
        d1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    duplicate=true;
                }else{
                    duplicate=false;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        if(TextUtils.isEmpty(rapn.getText())){
            Toast.makeText(restaurant_add_new_menu.this,"Please enter your item name !",Toast.LENGTH_LONG).show();;
        }else if(duplicate){
            Toast.makeText(restaurant_add_new_menu.this,"Item name already existed !",Toast.LENGTH_LONG).show();
            rapn.setText(null);
        }else if(String.valueOf(spinner_category2.getSelectedItem()).equals(default_spinner)){
            Toast.makeText(restaurant_add_new_menu.this,"Please select your item category !",Toast.LENGTH_LONG).show();;
        }else if(TextUtils.isEmpty(rapp.getText())){
            Toast.makeText(restaurant_add_new_menu.this,"Please enter the price !",Toast.LENGTH_LONG).show();
            rapp.setText(null);
        }else if(Double.parseDouble(rapp.getText().toString())<0.00){
            Toast.makeText(restaurant_add_new_menu.this,"Price can't be negative number !",Toast.LENGTH_LONG).show();
        } else{
            checked = true;
            db_product.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(checked && !duplicate){
                        Item item = new Item(rapn.getText().toString(),rapp.getText().toString(),String.valueOf(spinner_category2.getSelectedItem()));
                        BestSell b = new BestSell(rapn.getText().toString(),"0");
                        String label = item.getProduct_name();
                        db_product.child(session_name).child("Restaurant").child(label).setValue(item);
                        DatabaseReference ddd = FirebaseDatabase.getInstance().getReference("best_sell").child(session_name).child("Restaurant");
                        ddd.child(label).setValue(b);
                        rapn.setText(null);
                        spinner_category2.setSelection(0);
                        rapp.setText(null);
                        Toast.makeText(restaurant_add_new_menu.this,"Item added !",Toast.LENGTH_LONG).show();
                        checked = false;
                        Intent myIntent = new Intent(restaurant_add_new_menu.this, restaurant_menu.class);
                        myIntent.putExtra("Session ID",session_name);
                        myIntent.putExtra("Shop",shop_name);
                        myIntent.putExtra("UN",un);
                        startActivity(myIntent);
                        finish();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(restaurant_add_new_menu.this, "Something wrong...", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public void reset(){
        rapn.setText(null);
        rapp.setText(null);
        spinner_category2.setSelection(0);
    }
    public void addItemOnSpinner(){
        db_category = FirebaseDatabase.getInstance().getReference("category").child(session_name).child("Restaurant");
        db_category.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<String> category = new ArrayList<String>();
                category.add(default_spinner);
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    String c = dataSnapshot1.child("category").getValue(String.class);
                    category.add(c);
                }
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(restaurant_add_new_menu.this, android.R.layout.simple_spinner_item, category);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_category2.setAdapter(areasAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(restaurant_add_new_menu.this, "Something wrong...", Toast.LENGTH_SHORT).show();
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

        if (id == R.id.nav_take_order) {
            Intent myIntent = new Intent(restaurant_add_new_menu.this, restaurant_take_order.class);
            myIntent.putExtra("Session ID",session_name);
            myIntent.putExtra("Shop",shop_name);
            myIntent.putExtra("UN",un);
            startActivity(myIntent);
            finish();
        } else if (id == R.id.nav_manage_menu) {

        } else if (id == R.id.nav_dashboard_2) {
            Intent myIntent = new Intent(restaurant_add_new_menu.this, restaurant_dashboard.class);
            myIntent.putExtra("Session ID",session_name);
            myIntent.putExtra("Shop",shop_name);
            myIntent.putExtra("UN",un);
            startActivity(myIntent);
            finish();
        } else if (id == R.id.nav_retail ){
            Intent myIntent = new Intent(restaurant_add_new_menu.this, retail_main_menu.class);
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
