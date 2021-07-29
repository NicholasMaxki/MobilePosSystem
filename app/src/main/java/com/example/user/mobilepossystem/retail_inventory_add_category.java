package com.example.user.mobilepossystem;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class retail_inventory_add_category extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private String session_name,shop_name,un;
    private Button category_save;
    private EditText editText_category;
    private DatabaseReference db,db2;
    private RecyclerView rcv;
    private ArrayList<Category> list;
    private MyAdapter_Category adapter_category;
    private  boolean checked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retail_inventory_add_category);

        Intent i = getIntent();
        session_name = i.getExtras().getString("Session ID");
        shop_name = i.getExtras().getString("Shop");
        un = i.getExtras().getString("UN");
        category_save=(Button)findViewById(R.id.btn_add_category_save);
        editText_category=(EditText)findViewById(R.id.editText_category);
        category_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory();
            }
        });
        //recycleview
        rcv = (RecyclerView) findViewById(R.id.recycle_for_category_page);
        rcv.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<Category>();
        //get reference from firebase
        db = FirebaseDatabase.getInstance().getReference("category").child(session_name).child("Retail");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    Category c = dataSnapshot1.getValue(Category.class);
                    list.add(c);
                }
                adapter_category = new MyAdapter_Category(retail_inventory_add_category.this,list){
                    @Override
                    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
                        holder.recycle_product_category_category.setText(cry.get(position).Category);
                        holder.recycle_product_category_category.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                Category c = list.get(position);
                                showUpdateDeleteDialog(c.getCategory());
                                return true;
                            }
                        });
                    }
                };
                rcv.setAdapter(adapter_category);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(retail_inventory_add_category.this, "Something wrong...", Toast.LENGTH_SHORT).show();
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
                Intent myIntent = new Intent(retail_inventory_add_category.this, profile.class);
                myIntent.putExtra("Session ID",session_name);
                myIntent.putExtra("Shop",shop_name);
                myIntent.putExtra("UN",un);
                myIntent.putExtra("Mode","Retail");
                startActivity(myIntent);
                finish();
            }
        });
    }
    public  void addCategory(){
        if(TextUtils.isEmpty(editText_category.getText())){
            Toast.makeText(retail_inventory_add_category.this,"Please enter your category !",Toast.LENGTH_LONG).show();
        }else{
            checked = true;
            for (int i=0;i<list.size();i++){
                if(editText_category.getText().toString().equals(list.get(i).getCategory())){
                    checked = false;
                    break;
                }
            }
            db2 = FirebaseDatabase.getInstance().getReference("category");
            db2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(checked) {
                        Category c = new Category(editText_category.getText().toString());
                        String label = c.getCategory();
                        db2.child(session_name).child("Retail").child(label).setValue(c);
                        editText_category.setText(null);
                        checked = false;
                        list.clear();
                        adapter_category.notifyDataSetChanged();
                        overridePendingTransition(0, 0);
                        Toast.makeText(retail_inventory_add_category.this, "New category added !", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(retail_inventory_add_category.this, "Category is existed !", Toast.LENGTH_SHORT).show();
                        editText_category.setText(null);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(retail_inventory_add_category.this, "Something wrong...", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    //dialog
    private void showUpdateDeleteDialog(final String category) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.promt_update_delete_category, null);
        dialogBuilder.setView(dialogView);
        final Button buttonYes = (Button) dialogView.findViewById(R.id.buttonDeleteYes);
        final Button buttonNo = (Button) dialogView.findViewById(R.id.buttonDeleteNo);
        dialogBuilder.setCancelable(true);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCategory(category);
                list.clear();
                adapter_category.notifyDataSetChanged();
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
//delete
    private boolean deleteCategory(String category) {
    //getting the specified artist reference
    DatabaseReference dR = FirebaseDatabase.getInstance().getReference("category").child(session_name).child("Retail").child(category);
    //removing value
    dR.removeValue();
    Toast.makeText(getApplicationContext(), "Category Deleted", Toast.LENGTH_LONG).show();
    return true;
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
            Intent myIntent = new Intent(retail_inventory_add_category.this, retail_main_menu.class);
            myIntent.putExtra("Session ID",session_name);
            myIntent.putExtra("Shop",shop_name);
            myIntent.putExtra("UN",un);
            startActivity(myIntent);
            finish();
        } else if (id == R.id.nav_inventory) {

        }  else if (id == R.id.nav_dashboard) {
            Intent myIntent = new Intent(retail_inventory_add_category.this, retail_dashboard.class);
            myIntent.putExtra("Session ID",session_name);
            myIntent.putExtra("Shop",shop_name);
            myIntent.putExtra("UN",un);
            startActivity(myIntent);
            finish();
        } else if (id == R.id.nav_restaurant) {
            Intent myIntent = new Intent(retail_inventory_add_category.this, restaurant_role.class);
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
