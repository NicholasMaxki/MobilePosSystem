package com.example.user.mobilepossystem;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognizerIntent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class retail_inventory extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    private String session_name,shop_name,un;
    private Button  btn_add;
    private ImageButton btn_inv_voice_search;
    private DatabaseReference db;
    private RecyclerView rcv;
    private ArrayList<Product> list;
    private MyAdapter_Product adapter_product;
    private Spinner spinner_category_promt;
    private EditText inventory_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retail_inventory2);
        //session ID
        Intent i = getIntent();
        session_name = i.getExtras().getString("Session ID");
        shop_name = i.getExtras().getString("Shop");
        un = i.getExtras().getString("UN");
        btn_add = (Button)findViewById(R.id.btn_add);
        inventory_search = (EditText)findViewById(R.id.retail_inventory_search);
        inventory_search.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        btn_inv_voice_search = (ImageButton)findViewById(R.id.btn_inv_voice_search);
        btn_inv_voice_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(retail_inventory.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(retail_inventory.this, new String[] {Manifest.permission.RECORD_AUDIO}, 1);
                }
                voiceToText();
            }
        });

        //search function
        inventory_search.addTextChangedListener(new TextWatcher() {
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
        rcv = (RecyclerView) findViewById(R.id.RecycleView1);
        rcv.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<Product>();
        //get reference from firebase
        db = FirebaseDatabase.getInstance().getReference("product").child(session_name).child("Retail");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    list.clear();
                    for (DataSnapshot d: dataSnapshot.getChildren()){
                        final Product pp = d.getValue(Product.class);
                        list.add(pp);
                    }
                    adapter_product = new MyAdapter_Product(retail_inventory.this,list){
                        @Override
                        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
                            holder.recycle_product_name.setText(products.get(position).product_name);
                            holder.recycle_price.setText("RM "+products.get(position).price);
                            holder.recycle_product_name.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Product p = list.get(position);
                                    showProductDetail(p.getBarcode(),p.getProduct_name(),p.getCategory(),p.getPrice(),p.getQuantity());
                                }
                            });
                        }
                    };
                    rcv.setAdapter(adapter_product);
                    adapter_product.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoAdd();
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
                Intent myIntent = new Intent(retail_inventory.this, profile.class);
                myIntent.putExtra("Session ID",session_name);
                myIntent.putExtra("Shop",shop_name);
                myIntent.putExtra("UN",un);
                myIntent.putExtra("Mode","Retail");
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
                    inventory_search.setText(result.get(0));
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
                            final Product pp = d.getValue(Product.class);
                            list.add(pp);
                        }
                        adapter_product = new MyAdapter_Product(retail_inventory.this,list){
                            @Override
                            public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
                                holder.recycle_product_name.setText(products.get(position).product_name);
                                holder.recycle_price.setText("RM "+products.get(position).price);
                                holder.recycle_product_name.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Product p = list.get(position);
                                        showProductDetail(p.getBarcode(),p.getProduct_name(),p.getCategory(),p.getPrice(),p.getQuantity());
                                    }
                                });
                            }
                        };
                        rcv.setAdapter(adapter_product);
                        adapter_product.notifyDataSetChanged();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
    }
    public void gotoAdd(){
        Intent myIntent = new Intent(retail_inventory.this, retail_inventory_add.class);
        myIntent.putExtra("Session ID",session_name);
        myIntent.putExtra("Shop",shop_name);
        myIntent.putExtra("UN",un);
        startActivity(myIntent);
        finish();
    }
    private void showProductDetail(final String bc, final String pn, String c, String pp, String pq) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.promt_product_detail, null);
        dialogBuilder.setView(dialogView);
        final TextView tv = dialogView.findViewById(R.id.tv);
        tv.setText(pn);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.btn_promt_update);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.btn_promt_delete);
        final EditText promt_p_bc = (EditText)dialogView.findViewById(R.id.promt_p_bc);
        final EditText promt_p_p = (EditText)dialogView.findViewById(R.id.promt_p_p);
        final EditText promt_p_q = (EditText)dialogView.findViewById(R.id.promt_p_q);
        spinner_category_promt = (Spinner)dialogView.findViewById(R.id.spinner_category_promt);
        addItemOnSpinner(c);
        promt_p_bc.setText(bc);
        promt_p_p.setText(pp);
        promt_p_q.setText(pq);
        dialogBuilder.setCancelable(true);
        final AlertDialog b = dialogBuilder.create();
        b.show();
        //while clicked on update button from prompt
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pbc = promt_p_bc.getText().toString();
                String ppn = pn;
                String ppp = promt_p_p.getText().toString();
                String ppq = promt_p_q.getText().toString();
                String pc = spinner_category_promt.getSelectedItem().toString();
                if (!TextUtils.isEmpty(pbc)  && !TextUtils.isEmpty(ppp) && !TextUtils.isEmpty(ppq)) {
                    if(Integer.parseInt(ppq)>=0){
                        if(Double.parseDouble(ppp)>=0.00){
                            updateProduct(pbc, ppn, ppp,ppq,pc);
                            b.dismiss();
                        }else{
                            Toast.makeText(retail_inventory.this,"Price should not be negative !",Toast.LENGTH_SHORT).show();
                            promt_p_p.setText(null);
                        }
                    }else{
                        Toast.makeText(retail_inventory.this,"Quantity should not be negative !",Toast.LENGTH_SHORT).show();
                        promt_p_q.setText(null);
                    }
                }else{
                    Toast.makeText(retail_inventory.this,"Product's field should not be blank !",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //while clicked on delete button from prompt
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
                adapter_product.notifyDataSetChanged();
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
        db_c = FirebaseDatabase.getInstance().getReference("category").child(session_name).child("Retail");
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
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(retail_inventory.this, android.R.layout.simple_spinner_item, category);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_category_promt.setAdapter(areasAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(retail_inventory.this, "Something wrong...", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean updateProduct(String bc, String pn, String pp, String pq, String pc) {
        //getting the specified artist reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("product").child(session_name).child("Retail").child(pn);
        //updating product
        Product p = new Product(bc,pn,pc,pp,pq);
        dR.setValue(p);
        Toast.makeText(getApplicationContext(), "Product Updated", Toast.LENGTH_LONG).show();
        return true;
    }
    private boolean deleteProduct(String product) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("product").child(session_name).child("Retail").child(product);
        //removing product
        dR.removeValue();
        Toast.makeText(getApplicationContext(), "Product Deleted", Toast.LENGTH_LONG).show();
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
            Intent myIntent = new Intent(retail_inventory.this, retail_main_menu.class);
            myIntent.putExtra("Session ID",session_name);
            myIntent.putExtra("Shop",shop_name);
            myIntent.putExtra("UN",un);
            startActivity(myIntent);
            finish();
        } else if (id == R.id.nav_inventory) {

        }  else if (id == R.id.nav_dashboard) {
            Intent myIntent = new Intent(retail_inventory.this, retail_dashboard.class);
            myIntent.putExtra("Session ID",session_name);
            myIntent.putExtra("Shop",shop_name);
            myIntent.putExtra("UN",un);
            startActivity(myIntent);
            finish();
        } else if (id == R.id.nav_restaurant) {
            Intent myIntent = new Intent(retail_inventory.this, restaurant_role.class);
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




