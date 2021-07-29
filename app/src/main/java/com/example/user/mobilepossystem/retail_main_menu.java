package com.example.user.mobilepossystem;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
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

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class retail_main_menu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Button btn_check_checkout;
    private ImageButton btn_check_quantity,btn_check_barcode,btn_check_non_item,btn_check_category,btn_check_voice_search;
    private EditText search_retail_checkout;
    private  Spinner promt_spinner_category_retail;
    private String session_name,shop_name,un;
    private DatabaseReference db;
    private RecyclerView rcv;
    private ArrayList<Product> list;
    private MyAdapter_Product_gridlayout adapter_product;
    final int default_quantity = 1;
    private int selected_quantity = default_quantity;
    private int new_quantity =0;
    private boolean existed = false,c=false,enough=false;
   public static DecimalFormat df2 = new DecimalFormat("0.00");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retail_checkout);
        //get session name from previous activity
        Intent i = getIntent();
        session_name = i.getExtras().getString("Session ID");
        shop_name = i.getExtras().getString("Shop");
        un = i.getExtras().getString("UN");
        //product gridlayout here
        rcv = (RecyclerView) findViewById(R.id.recycleview_gridlayout_retail);
        rcv.setHasFixedSize(true);
        GridLayoutManager glm = new GridLayoutManager(this,3);
        rcv.setLayoutManager(glm);
        list = new ArrayList<Product>();
        //get reference from firebase
        db = FirebaseDatabase.getInstance().getReference("product").child(session_name).child("Retail");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                if(dataSnapshot.hasChildren()){
                    for (DataSnapshot d: dataSnapshot.getChildren()){
                        final Product pp = d.getValue(Product.class);
                        list.add(pp);
                    }
                    adapter_product = new MyAdapter_Product_gridlayout(retail_main_menu.this,list){
                        @Override
                        public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
                            holder.product_layout_tv.setText(products.get(position).product_name);
                            holder.product_price_layout_tv.setText("RM "+df2.format( Double.parseDouble(products.get(position).price)));
                            if(Integer.parseInt(products.get(position).quantity)==0){
                                holder.product_layout_tv.setBackgroundColor(Color.parseColor("#ff4c4c"));
                                holder.product_price_layout_tv.setBackgroundColor(Color.parseColor("#ff0000"));
                            }
                            final DatabaseReference db_order = FirebaseDatabase.getInstance().getReference("order").child(session_name).child("Retail");
                            holder.product_layout_tv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    new_quantity =1;
                                    final Product p = list.get(position);
                                    if(Integer.parseInt(p.getQuantity())!=0){
                                        if(Integer.parseInt(p.getQuantity())<selected_quantity){
                                            Toast.makeText(retail_main_menu.this,p.getProduct_name()+"'s stock limit is reach.",Toast.LENGTH_LONG).show();
                                        }else{
                                            enough=true;
                                            //use addListenerForSingleValueListener instead of  addValueListener to prevent infinity calling method OnDataChange()
                                            db_order.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                                                        Product pp = ds.getValue(Product.class);
                                                        if(pp.getProduct_name().equals(p.getProduct_name())){
                                                            existed = true;
                                                            if(Integer.parseInt(pp.getQuantity())+selected_quantity<=Integer.parseInt(p.getQuantity())){
                                                                new_quantity = Integer.parseInt(pp.getQuantity())+selected_quantity;
                                                                enough=true;
                                                            }else{
                                                                Toast.makeText(retail_main_menu.this,p.getProduct_name()+"'s stock limit is reach.",Toast.LENGTH_LONG).show();
                                                                enough=false;
                                                            }
                                                            break;
                                                        }
                                                    }
                                                    if(existed && enough){
                                                        Product p_o = new Product(p.getProduct_name(),p.getPrice(),String.valueOf(new_quantity));
                                                        db_order.child(p.getProduct_name()).setValue(p_o);
                                                        existed=false;
                                                        selected_quantity = default_quantity;
                                                        enough=false;
                                                    }else if(!existed &&enough){
                                                        Product p_o = new Product(p.getProduct_name(),p.getPrice(),String.valueOf(selected_quantity));
                                                        db_order.child(p.getProduct_name()).setValue(p_o);
                                                        selected_quantity = default_quantity;
                                                        enough=false;
                                                    }
                                                    showCurrentOrder();
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                }
                                            });
                                        }

                                    }else{
                                        Toast.makeText(retail_main_menu.this,p.getProduct_name()+" is out of stock.",Toast.LENGTH_LONG).show();

                                    }
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
        //update the current order
        showCurrentOrder();

        btn_check_checkout = (Button)findViewById(R.id.btn_check_checkout);
        btn_check_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoViewOrderList();
            }
        });

        btn_check_quantity = (ImageButton)findViewById(R.id.btn_check_quantity);
        btn_check_quantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //prompt popup coding start here
                LayoutInflater li = LayoutInflater.from(retail_main_menu.this);
                View promptsView = li.inflate(R.layout.promt_testing, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(retail_main_menu.this);
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);
                final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
                final TextView tv = promptsView.findViewById(R.id.tv);
                final Button done =promptsView.findViewById(R.id.btn_ok);
                tv.setText("Insert product quantity : ");
                // set dialog message
                alertDialogBuilder.setCancelable(true);
                // create alert dialog
                final AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!userInput.getText().toString().equals("")){
                            if(Integer.parseInt(userInput.getText().toString())<=0){
                                Toast.makeText(retail_main_menu.this,"Quantity should not less than 0",Toast.LENGTH_SHORT).show();
                                userInput.setText(null);
                            }else{
                                selected_quantity = Integer.parseInt(userInput.getText().toString());
                                alertDialog.cancel();
                            }
                        }else{
                            alertDialog.cancel();
                        }
                    }
                });
                //end
            }
        });
        btn_check_barcode =findViewById(R.id.btn_check_barcode);
        btn_check_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(retail_main_menu.this,retail_inventory_add_scanner.class).putExtra("Use Scanner","Retail Menu").putExtra("Session ID",session_name));
            }
        });

        search_retail_checkout = findViewById(R.id.search_retail_checkout);
        search_retail_checkout.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        search_retail_checkout.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
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

        btn_check_non_item = findViewById(R.id.btn_check_non_item);
        btn_check_non_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //prompt popup coding start here
                LayoutInflater li = LayoutInflater.from(retail_main_menu.this);
                View promptsView = li.inflate(R.layout.promt_non_item, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(retail_main_menu.this);
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);
                final EditText detail = (EditText) promptsView.findViewById(R.id.non_item_promt);
                final EditText price = (EditText) promptsView.findViewById(R.id.non_item_price_promt);
                final Button done = promptsView.findViewById(R.id.btn_ok);
                // set dialog message
                alertDialogBuilder.setCancelable(true);
                // create alert dialog
                final AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!detail.getText().toString().equals("") && !price.getText().toString().equals("") && Double.parseDouble(price.getText().toString())>0){
                            final DatabaseReference db_order = FirebaseDatabase.getInstance().getReference("order").child(session_name).child("Retail");
                            db_order.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Product p_o = new Product("(Non-item)"+detail.getText().toString(),price.getText().toString(),"1");
                                    db_order.child("(Non-item)"+detail.getText().toString()).setValue(p_o);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                            alertDialog.cancel();
                        }else if(TextUtils.isEmpty(detail.getText().toString())) {
                            Toast.makeText(retail_main_menu.this," Non item's detail should not be blanked  !",Toast.LENGTH_SHORT).show();
                            detail.setText(null);
                        }else if(TextUtils.isEmpty(price.getText().toString())) {
                            Toast.makeText(retail_main_menu.this," Price should not be blanked  !",Toast.LENGTH_SHORT).show();
                           price.setText(null);
                        }else if(Double.parseDouble(price.getText().toString())<=0){
                            Toast.makeText(retail_main_menu.this,"Price should not less than 0 !",Toast.LENGTH_SHORT).show();
                            price.setText(null);
                        }
                    }
                });
            }
        });
        btn_check_category= findViewById(R.id.btn_check_category);
        btn_check_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //prompt popup coding start here
                LayoutInflater li = LayoutInflater.from(retail_main_menu.this);
                View promptsView = li.inflate(R.layout.promt_category_spinner, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(retail_main_menu.this);
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);
                promt_spinner_category_retail = (Spinner) promptsView.findViewById(R.id.promt_spinner_category_retail);
                final Button done = promptsView.findViewById(R.id.btn_ok);
                addItemOnSpinner();
                // set dialog message
                alertDialogBuilder.setCancelable(true);
                // create alert dialog
                final AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        c= true;
                        groupbyCategory(promt_spinner_category_retail.getSelectedItem().toString());
                        alertDialog.cancel();
                    }
                });
                //end
            }
        });
        btn_check_voice_search= findViewById(R.id.btn_check_voice_search);
        btn_check_voice_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(retail_main_menu.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(retail_main_menu.this, new String[] {Manifest.permission.RECORD_AUDIO}, 1);
                }
                voiceToText();
            }
        });
        //nav bar function start
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
                Intent myIntent = new Intent(retail_main_menu.this, profile.class);
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

                    search_retail_checkout.setText(result.get(0));
                }
                break;
            }
        }
    }
    private void groupbyCategory(String s) {
            if(c=true) {
                if (promt_spinner_category_retail.getSelectedItemId() != 0) {
                    rcv.setAdapter(null);
                    Query q = db.orderByChild("category").startAt(s).endAt(s + "\uf8ff");
                    q.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                list.clear();
                                for (DataSnapshot d : dataSnapshot.getChildren()) {
                                    final Product pp = d.getValue(Product.class);
                                    list.add(pp);
                                }
                                adapter_product = new MyAdapter_Product_gridlayout(retail_main_menu.this, list) {
                                    @Override
                                    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
                                        holder.product_layout_tv.setText(products.get(position).product_name);
                                        holder.product_price_layout_tv.setText("RM " + products.get(position).price);
                                        if(Integer.parseInt(products.get(position).quantity)==0){
                                            holder.product_layout_tv.setBackgroundColor(Color.parseColor("#ff4c4c"));
                                            holder.product_price_layout_tv.setBackgroundColor(Color.parseColor("#ff0000"));
                                        }
                                        final DatabaseReference db_order = FirebaseDatabase.getInstance().getReference("order").child(session_name).child("Retail");
                                        holder.product_layout_tv.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                final Product p = list.get(position);
                                                //use addListenerForSingleValueListener instead of  addValueListener to prevent infinity calling method OnDataChange()
                                                db_order.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                            Product pp = ds.getValue(Product.class);
                                                            if (pp.getProduct_name().equals(p.getProduct_name())) {
                                                                new_quantity = Integer.parseInt(pp.getQuantity());
                                                                existed = true;
                                                                break;
                                                            }
                                                        }
                                                        if (existed) {
                                                            Product p_o = new Product(p.getProduct_name(), p.getPrice(), String.valueOf(new_quantity + selected_quantity));
                                                            db_order.child(p.getProduct_name()).setValue(p_o);
                                                            existed = false;
                                                        } else {
                                                            Product p_o = new Product(p.getProduct_name(), p.getPrice(), String.valueOf(selected_quantity));
                                                            db_order.child(p.getProduct_name()).setValue(p_o);
                                                            selected_quantity = default_quantity;
                                                        }
                                                        showCurrentOrder();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    }
                                                });
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
                    c = false;
                }
                else{
                  search_retail_checkout.setText("a");
                  search_retail_checkout.setText(null);
                    c=false;
                }
            }
    }
    private void search(String s) {
        rcv.setAdapter(null);
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
                    adapter_product = new MyAdapter_Product_gridlayout(retail_main_menu.this,list){
                        @Override
                        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
                            holder.product_layout_tv.setText(products.get(position).product_name);
                            holder.product_price_layout_tv.setText("RM "+products.get(position).price);
                            if(Integer.parseInt(products.get(position).quantity)==0){
                                holder.product_layout_tv.setBackgroundColor(Color.parseColor("#ff4c4c"));
                                holder.product_price_layout_tv.setBackgroundColor(Color.parseColor("#ff0000"));
                            }
                            final DatabaseReference db_order = FirebaseDatabase.getInstance().getReference("order").child(session_name).child("Retail");
                            holder.product_layout_tv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    final Product p = list.get(position);
                                    //use addListenerForSingleValueListener instead of  addValueListener to prevent infinity calling method OnDataChange()
                                    db_order.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                                Product pp = ds.getValue(Product.class);
                                                if(pp.getProduct_name().equals(p.getProduct_name())){
                                                    new_quantity = Integer.parseInt(pp.getQuantity());
                                                    existed = true;
                                                    break;
                                                }
                                            }
                                            if(existed){
                                                Product p_o = new Product(p.getProduct_name(),p.getPrice(),String.valueOf(new_quantity+selected_quantity));
                                                db_order.child(p.getProduct_name()).setValue(p_o);
                                                existed=false;
                                            }else{
                                                Product p_o = new Product(p.getProduct_name(),p.getPrice(),String.valueOf(selected_quantity));
                                                db_order.child(p.getProduct_name()).setValue(p_o);
                                                selected_quantity = default_quantity;
                                            }
                                            showCurrentOrder();
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
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
    }
    private void showCurrentOrder() {
        DatabaseReference ddb = FirebaseDatabase.getInstance().getReference("order").child(session_name).child("Retail");
        ddb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                double total_price=0;
                int total_quantity = 0;
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Product order = ds.getValue(Product.class);
                    total_price += (Double.parseDouble(order.getQuantity())*Double.parseDouble(order.getPrice())) ;
                    total_quantity += Integer.parseInt(order.getQuantity());
                }
                btn_check_checkout.setText(total_quantity+" Items = RM "+df2.format(total_price));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void gotoViewOrderList(){
        Intent myIntent = new Intent(retail_main_menu.this, retail_view_order_list.class);
        myIntent.putExtra("Session ID",session_name);
        myIntent.putExtra("Shop",shop_name);
        myIntent.putExtra("UN",un);
        startActivity(myIntent);
    }
    public void addItemOnSpinner(){
       DatabaseReference db_category = FirebaseDatabase.getInstance().getReference("category").child(session_name).child("Retail");
        db_category.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<String> category = new ArrayList<String>();
                category.add("All Item");
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    String c = dataSnapshot1.child("category").getValue(String.class);
                    category.add(c);
                }
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(retail_main_menu.this, android.R.layout.simple_spinner_item, category);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                promt_spinner_category_retail.setAdapter(areasAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(retail_main_menu.this, "Something wrong...", Toast.LENGTH_SHORT).show();
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

        } else if (id == R.id.nav_inventory) {
            Intent myIntent = new Intent(retail_main_menu.this, retail_inventory.class);
            myIntent.putExtra("Session ID",session_name);
            myIntent.putExtra("Shop",shop_name);
            myIntent.putExtra("UN",un);
            startActivity(myIntent);
        }  else if (id == R.id.nav_dashboard) {
            Intent myIntent = new Intent(retail_main_menu.this, retail_dashboard.class);
            myIntent.putExtra("Session ID",session_name);
            myIntent.putExtra("Shop",shop_name);
            myIntent.putExtra("UN",un);
            startActivity(myIntent);
        } else if (id == R.id.nav_restaurant) {
            Intent myIntent = new Intent(retail_main_menu.this, restaurant_role.class);
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
