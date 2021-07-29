package com.example.user.mobilepossystem;
import android.content.Intent;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
public class restaurant_add_new_table extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private String session_name,shop_name,un;
    private EditText editText_table_name,editText_table_pax;
    private Button btn_add_table_save;
    private RecyclerView rcv;
    private ArrayList<Table> list;
    private DatabaseReference db;
    private MyAdapter_Table adapter_table;
    private boolean checked=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_add_new_table);

        Intent i = getIntent();
        session_name = i.getExtras().getString("Session ID");
        shop_name = i.getExtras().getString("Shop");
        un = i.getExtras().getString("UN");
        btn_add_table_save=(Button)findViewById(R.id.btn_add_table_save);
        editText_table_name=(EditText)findViewById(R.id.editText_table_name);
        editText_table_name.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        editText_table_pax=(EditText)findViewById(R.id.editText_table_pax);
        btn_add_table_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTable();
            }
        });
        //recycleview
        rcv = (RecyclerView) findViewById(R.id.recycleview_table);
        rcv.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<Table>();
        //get reference from firebase
        db = FirebaseDatabase.getInstance().getReference("table").child(session_name).child("Restaurant");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                if (dataSnapshot.hasChildren()){
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
                        Table t = dataSnapshot1.getValue(Table.class);
                        list.add(t);
                    }
                }
                adapter_table = new MyAdapter_Table(restaurant_add_new_table.this,list){
                    @Override
                    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
                        holder.recycle_product_category_category.setText(cry.get(position).table);
                        holder.recycle_product_category_category.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                Table t = list.get(position);
                                showUpdateDeleteDialog(t.getTable());
                                return true;
                            }
                        });
                    }
                };
                rcv.setAdapter(adapter_table);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(restaurant_add_new_table.this, "Something wrong...", Toast.LENGTH_SHORT).show();
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
                Intent myIntent = new Intent(restaurant_add_new_table.this, profile.class);
                myIntent.putExtra("Session ID",session_name);
                myIntent.putExtra("Shop",shop_name);
                myIntent.putExtra("UN",un);
                startActivity(myIntent);
                finish();
            }
        });
    }
    private void showUpdateDeleteDialog(final String table) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.promt_update_delete_category, null);
        dialogBuilder.setView(dialogView);
        final Button buttonYes = (Button) dialogView.findViewById(R.id.buttonDeleteYes);
        final Button buttonNo = (Button) dialogView.findViewById(R.id.buttonDeleteNo);

        dialogBuilder.setTitle("Confirm to delete this category?");
        dialogBuilder.setCancelable(true);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteTable(table);
                list.clear();
                adapter_table.notifyDataSetChanged();
                overridePendingTransition(0, 0);;
                b.dismiss();
            }
        });
        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.cancel();
            }
        });
    }
    private boolean deleteTable(String table) {
        //getting the specified artist reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("table").child(session_name).child("Restaurant").child(table);
        //removing value
        dR.removeValue();
        Toast.makeText(getApplicationContext(), "Table Deleted", Toast.LENGTH_LONG).show();
        return true;
    }
    private void addTable() {
        if(TextUtils.isEmpty(editText_table_name.getText())){
            Toast.makeText(restaurant_add_new_table.this,"Please enter your table name !",Toast.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(editText_table_pax.getText())){
            Toast.makeText(restaurant_add_new_table.this,"Please enter your table pax !",Toast.LENGTH_LONG).show();
        }else if(Integer.parseInt(editText_table_pax.getText().toString())<1){
            Toast.makeText(restaurant_add_new_table.this,"Table's pax should not less than 1 !",Toast.LENGTH_LONG).show();
            editText_table_pax.setText(null);
        } else{
            checked = true;
            for (int i=0;i<list.size();i++){
                if(editText_table_name.getText().toString().equals(list.get(i).table)){
                    checked = false;
                    break;
                }
            }
           final DatabaseReference db2 = FirebaseDatabase.getInstance().getReference("table");
            db2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(checked) {
                        Table t = new Table(editText_table_name.getText().toString(),"A",editText_table_pax.getText().toString());
                        String label = t.getTable();
                        db2.child(session_name).child("Restaurant").child(label).setValue(t);
                        editText_table_name.setText(null);
                        editText_table_pax.setText(null);
                        checked = false;
                        list.clear();
                        adapter_table.notifyDataSetChanged();
                        overridePendingTransition(0, 0);
                        Toast.makeText(restaurant_add_new_table.this, "New table added !", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(restaurant_add_new_table.this, "Table name is existed !!", Toast.LENGTH_SHORT).show();
                        editText_table_name.setText(null);
                        editText_table_pax.setText(null);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(restaurant_add_new_table.this, "Something wrong...", Toast.LENGTH_SHORT).show();
                }
            });
        }
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
            Intent myIntent = new Intent(restaurant_add_new_table.this, restaurant_take_order.class);
            myIntent.putExtra("Session ID",session_name);
            myIntent.putExtra("Shop",shop_name);
            myIntent.putExtra("UN",un);
            startActivity(myIntent);
            finish();
        } else if (id == R.id.nav_manage_menu) {
            Intent myIntent = new Intent(restaurant_add_new_table.this, restaurant_menu.class);
            myIntent.putExtra("Session ID",session_name);
            myIntent.putExtra("Shop",shop_name);
            myIntent.putExtra("UN",un);
            startActivity(myIntent);
            finish();
        } else if (id == R.id.nav_dashboard_2) {
            Intent myIntent = new Intent(restaurant_add_new_table.this, restaurant_dashboard.class);
            myIntent.putExtra("Session ID",session_name);
            myIntent.putExtra("Shop",shop_name);
            myIntent.putExtra("UN",un);
            startActivity(myIntent);
            finish();
        } else if (id == R.id.nav_retail ){
            Intent myIntent = new Intent(restaurant_add_new_table.this, retail_main_menu.class);
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
