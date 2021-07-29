package com.example.user.mobilepossystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class restaurant_menu extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{
    private Button btn_add_new_menu;
    private ImageButton btn_inv_voice_search2;
    private DatabaseReference db;
    private RecyclerView rcv;
    private ArrayList<Item> list;
    private MyAdapter_MenuItem adapter_menuItem;
    private Spinner spinner_category_promt2;
    private EditText restaurant_menu_search;
    private String session_name,shop_name,un;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_menu);
        Intent i = getIntent();
        session_name = i.getExtras().getString("Session ID");
        shop_name = i.getExtras().getString("Shop");
        un = i.getExtras().getString("UN");

        btn_add_new_menu = (Button)findViewById(R.id.btn_add_new_menu);
        restaurant_menu_search = (EditText)findViewById(R.id.restaurant_menu_search);
        restaurant_menu_search.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        btn_inv_voice_search2 = (ImageButton)findViewById(R.id.btn_inv_voice_search2);
        btn_inv_voice_search2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(restaurant_menu.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(restaurant_menu.this, new String[] {Manifest.permission.RECORD_AUDIO}, 1);
                }
                voiceToText();
            }
        });
        //search function
        restaurant_menu_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().isEmpty()){
                    search(editable.toString());
                }
                else{
                    search("");
                }
            }
        });
        //recycleview
        rcv = (RecyclerView) findViewById(R.id.rcv_menu_restaurant);
        rcv.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<Item>();
        db = FirebaseDatabase.getInstance().getReference("product").child(session_name).child("Restaurant");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    list.clear();
                    for (DataSnapshot d: dataSnapshot.getChildren()){
                        final Item i = d.getValue(Item.class);
                        list.add(i);
                    }
                    adapter_menuItem = new MyAdapter_MenuItem(restaurant_menu.this,list){
                        @Override
                        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
                            holder.recycle_product_name.setText(items.get(position).product_name);
                            holder.recycle_price.setText("RM "+items.get(position).price);
                            holder.recycle_product_name.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Item i = list.get(position);
                                    showProductDetail(i.product_name,i.price,i.category);
                                }
                            });
                        }
                    };
                    rcv.setAdapter(adapter_menuItem);
                    adapter_menuItem.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        btn_add_new_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoAdd();
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
                Intent myIntent = new Intent(restaurant_menu.this, profile.class);
                myIntent.putExtra("Session ID",session_name);
                myIntent.putExtra("Shop",shop_name);
                myIntent.putExtra("UN",un);
                startActivity(myIntent);
                finish();
            }
        });
    }
    private void voiceToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Something");
        try{
            startActivityForResult(intent,1000);
        }catch(Exception e){
            Toast.makeText(this," "+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 1000:{
                if(resultCode == RESULT_OK && null !=data){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    restaurant_menu_search.setText(result.get(0));
                }
                break;
            }
        }
    }

    private void search(String s) {
        rcv.setAdapter(null);
        s = s.toUpperCase();
        Query q = db.orderByChild("product_name").startAt(s).endAt(s+"\uf8ff");
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    list.clear();
                    for (DataSnapshot d: dataSnapshot.getChildren()){
                        final Item ii = d.getValue(Item.class);
                        list.add(ii);
                    }
                    adapter_menuItem = new MyAdapter_MenuItem(restaurant_menu.this,list){
                        @Override
                        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
                            holder.recycle_product_name.setText(items.get(position).product_name);
                            holder.recycle_price.setText("RM "+items.get(position).price);
                            holder.recycle_product_name.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Item i = list.get(position);
                                    showProductDetail(i.getProduct_name(),i.getCategory(),i.getPrice());
                                }
                            });
                        }
                    };
                    rcv.setAdapter(adapter_menuItem);
                    adapter_menuItem.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public void gotoAdd(){
        Intent myIntent = new Intent(restaurant_menu.this, restaurant_add_new_menu.class);
        myIntent.putExtra("Session ID",session_name);
        myIntent.putExtra("Shop",shop_name);
        myIntent.putExtra("UN",un);
        startActivity(myIntent);
        finish();
    }

    private void showProductDetail(final String pn, String pp, String c) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.promt_item_detail, null);
        dialogBuilder.setView(dialogView);
        final TextView item_name = (TextView) dialogView.findViewById(R.id.item_name);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.btn_promt_update);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.btn_promt_delete);
        final EditText promt_p_p = (EditText)dialogView.findViewById(R.id.promt_p_p);
        spinner_category_promt2 = (Spinner)dialogView.findViewById(R.id.spinner_category_promt);
        addItemOnSpinner(c);
        promt_p_p.setText(pp);
        item_name.setText(pn);
        dialogBuilder.setCancelable(true);
        final AlertDialog b = dialogBuilder.create();
        b.show();
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ppn = pn;
                String ppp = promt_p_p.getText().toString();
                String pc = spinner_category_promt2.getSelectedItem().toString();

                if (!TextUtils.isEmpty(ppn) && !TextUtils.isEmpty(ppp) ) {
                        if(Double.parseDouble(ppp)>=0.00){
                            updateProduct(ppn, ppp,pc);
                            b.dismiss();
                        }else{
                            Toast.makeText(restaurant_menu.this,"Price should not be negative !",Toast.LENGTH_SHORT).show();
                            promt_p_p.setText(null);
                        }
                }else{
                    Toast.makeText(restaurant_menu.this,"Item's field should not be blank !",Toast.LENGTH_SHORT).show();
                }
            }
        });
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog(pn);
                b.dismiss();
            }
        });
    }
    private void showDeleteDialog(final String pn) {
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
                deleteProduct(pn);
                list.clear();
                adapter_menuItem.notifyDataSetChanged();
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
    public void addItemOnSpinner(final String c){
        DatabaseReference db_c;
        db_c = FirebaseDatabase.getInstance().getReference("category").child(session_name).child("Restaurant");
        db_c.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<String> category = new ArrayList<String>();
                category.add(c);
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    String c = dataSnapshot1.child("category").getValue(String.class);
                    category.add(c);
                }
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(restaurant_menu.this, android.R.layout.simple_spinner_item, category);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_category_promt2.setAdapter(areasAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(restaurant_menu.this, "Something wrong...", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean updateProduct(String pn, String pp,String pc) {
        //getting the specified artist reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("product").child(session_name).child("Restaurant").child(pn);
        //updating product
        Item item = new Item(pn,pp,pc);
        dR.setValue(item);
        Toast.makeText(getApplicationContext(), "Item Updated", Toast.LENGTH_LONG).show();
        return true;
    }
    private boolean deleteProduct(String product) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("product").child(session_name).child("Restaurant").child(product);
        //removing product
        dR.removeValue();
        Toast.makeText(getApplicationContext(), "Item Deleted", Toast.LENGTH_LONG).show();
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

        if (id == R.id.nav_take_order) {
            Intent myIntent = new Intent(restaurant_menu.this, restaurant_take_order.class);
            myIntent.putExtra("Session ID",session_name);
            myIntent.putExtra("Shop",shop_name);
            myIntent.putExtra("UN",un);
            startActivity(myIntent);
            finish();
        } else if (id == R.id.nav_manage_menu) {

        } else if (id == R.id.nav_dashboard_2) {
            Intent myIntent = new Intent(restaurant_menu.this, restaurant_dashboard.class);
            myIntent.putExtra("Session ID",session_name);
            myIntent.putExtra("Shop",shop_name);
            myIntent.putExtra("UN",un);
            startActivity(myIntent);
            finish();
        } else if (id == R.id.nav_retail ){
            Intent myIntent = new Intent(restaurant_menu.this, retail_main_menu.class);
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
