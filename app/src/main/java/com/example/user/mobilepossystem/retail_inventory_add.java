package com.example.user.mobilepossystem;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class retail_inventory_add extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
 private Button  btn_add,btn_reset,btn_category;
 private ImageButton btn_scan;
 private EditText pn,qty,price;
 public static EditText bc;
 private Spinner spinner_category;
 private DatabaseReference db_product,db_category;
 private String session_name,shop_name,un;
 private boolean checked = false,duplicate;
 private final static  String default_spinner = "Select Category";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retail_inventory_add);
        db_product = FirebaseDatabase.getInstance().getReference("product");
        Intent i = getIntent();
        session_name = i.getExtras().getString("Session ID");
        shop_name = i.getExtras().getString("Shop");
        un = i.getExtras().getString("UN");
        btn_scan = (ImageButton)findViewById(R.id.btn_scan);
        btn_add = (Button)findViewById(R.id.btn_add);
        btn_reset = (Button)findViewById(R.id.btn_reset);
        btn_category=(Button)findViewById(R.id.btn_category);
        bc = (EditText)findViewById(R.id.bc);
        price = (EditText)findViewById(R.id.price);
        pn = (EditText)findViewById(R.id.pn);
        pn.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        qty = (EditText)findViewById(R.id.qty);

        spinner_category = (Spinner)findViewById(R.id.spinner_category);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan();
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_product();
            }
        });
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
        btn_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(retail_inventory_add.this, retail_inventory_add_category.class);
                myIntent.putExtra("Session ID",session_name);
                myIntent.putExtra("Shop",shop_name);
                myIntent.putExtra("UN",un);
                startActivity(myIntent);
            }
        });
        addItemOnSpinner();

        //nav
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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
                Intent myIntent = new Intent(retail_inventory_add.this, profile.class);
                myIntent.putExtra("Session ID",session_name);
                myIntent.putExtra("Shop",shop_name);
                myIntent.putExtra("UN",un);
                myIntent.putExtra("Mode","Retail");
                startActivity(myIntent);
                finish();
            }
        });
    }
   public void scan(){
       startActivity(new Intent(retail_inventory_add.this,retail_inventory_add_scanner.class).putExtra("Use Scanner","Inventory Retail"));
   }
    public void add_product(){
        DatabaseReference d1 = FirebaseDatabase.getInstance().getReference("product").child(session_name).child("Retail").child(pn.getText().toString());
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

        if(TextUtils.isEmpty(bc.getText())){
            Toast.makeText(retail_inventory_add.this,"Please enter your barcode !",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(pn.getText())){
            Toast.makeText(retail_inventory_add.this,"Please enter your product name !",Toast.LENGTH_LONG).show();;
        }else if(duplicate){
            Toast.makeText(retail_inventory_add.this,"Product name already existed !",Toast.LENGTH_LONG).show();
            pn.setText(null);
        }else if(String.valueOf(spinner_category.getSelectedItem()).equals(default_spinner)){
            Toast.makeText(retail_inventory_add.this,"Please select your product category !",Toast.LENGTH_LONG).show();;
        }
        else if(TextUtils.isEmpty(qty.getText())){
            Toast.makeText(retail_inventory_add.this,"Please enter the quantity !",Toast.LENGTH_LONG).show();;
        }else if(Integer.parseInt(qty.getText().toString())<0){
            Toast.makeText(retail_inventory_add.this,"Quantity can't be negative number !",Toast.LENGTH_LONG).show();
            qty.setText(null);
        } else if(TextUtils.isEmpty(price.getText())){
            Toast.makeText(retail_inventory_add.this,"Please enter the price !",Toast.LENGTH_LONG).show();
            price.setText(null);
        }else if(Double.parseDouble(price.getText().toString())<0.00){
            Toast.makeText(retail_inventory_add.this,"Price can't be negative number !",Toast.LENGTH_LONG).show();
        }
        else{
            checked = true;
            db_product.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   if(checked && !duplicate){
                       Product product = new Product(bc.getText().toString(),pn.getText().toString(),String.valueOf(spinner_category.getSelectedItem()),price.getText().toString(),qty.getText().toString());
                       BestSell b = new BestSell(pn.getText().toString(),"0");
                       String label = product.getProduct_name();
                       db_product.child(session_name).child("Retail").child(label).setValue(product);
                       DatabaseReference ddd = FirebaseDatabase.getInstance().getReference("best_sell").child(session_name).child("Retail");
                       ddd.child(label).setValue(b);
                       bc.setText(null);
                       pn.setText(null);
                       spinner_category.setSelection(0);
                       price.setText(null);
                       qty.setText(null);
                       Toast.makeText(retail_inventory_add.this,"Product added !",Toast.LENGTH_LONG).show();
                       checked = false;
                       Intent myIntent = new Intent(retail_inventory_add.this, retail_inventory.class);
                       myIntent.putExtra("Session ID",session_name);
                       startActivity(myIntent);
                       finish();
                   }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(retail_inventory_add.this, "Something wrong...", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public void reset(){
        bc.setText(null);
        pn.setText(null);
        price.setText(null);
        spinner_category.setSelection(0);
        qty.setText(null);
    }
    public void addItemOnSpinner(){
        db_category = FirebaseDatabase.getInstance().getReference("category").child(session_name).child("Retail");
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
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(retail_inventory_add.this, android.R.layout.simple_spinner_item, category);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_category.setAdapter(areasAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(retail_inventory_add.this, "Something wrong...", Toast.LENGTH_SHORT).show();
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
            Intent myIntent = new Intent(retail_inventory_add.this, retail_main_menu.class);
            myIntent.putExtra("Session ID",session_name);
            myIntent.putExtra("Shop",shop_name);
            myIntent.putExtra("UN",un);
            startActivity(myIntent);
            finish();
        } else if (id == R.id.nav_inventory) {

        }  else if (id == R.id.nav_dashboard) {
            Intent myIntent = new Intent(retail_inventory_add.this, retail_dashboard.class);
            myIntent.putExtra("Session ID",session_name);
            myIntent.putExtra("Shop",shop_name);
            myIntent.putExtra("UN",un);
            startActivity(myIntent);
            finish();
        } else if (id == R.id.nav_restaurant) {
            Intent myIntent = new Intent(retail_inventory_add.this, restaurant_role.class);
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
