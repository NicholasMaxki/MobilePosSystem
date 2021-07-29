package com.example.user.mobilepossystem;
import android.content.Context;
import android.content.Intent;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
public class restaurant_take_order extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    private String session_name,shop_name,un,extra_note = "none";
    private Button btn_add_new_table;
    private ImageButton btn_take_away;
    private RecyclerView rcv;
    private ArrayList<Table> list;
    private ArrayList<Item> list2,list3;
    private MyAdapter_Table_gridlayout adapter_table;
    private MyAdapter_MenuList adapter_menuList;
    private DatabaseReference db;
    private Spinner spinner_menu;
    private RecyclerView rcv_menu_list,rcv_order_list_in_menu;
    private boolean existed=false,ready_delete = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_take_order);
        Intent i = getIntent();
        session_name = i.getExtras().getString("Session ID");
        shop_name = i.getExtras().getString("Shop");
        un = i.getExtras().getString("UN");

        final DatabaseReference checker = FirebaseDatabase.getInstance().getReference("ring").child(session_name);
        checker.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    Uri alarmSound = RingtoneManager. getDefaultUri (RingtoneManager. TYPE_NOTIFICATION );
                    MediaPlayer mp = MediaPlayer. create (getApplicationContext(), alarmSound);
                    mp.start();
                    final Vibrator vibe = (Vibrator) restaurant_take_order.this.getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(2000);
                    checker.removeValue();
                    Toast.makeText(restaurant_take_order.this,"Foods are ready to serve.",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_take_away = (ImageButton)findViewById(R.id.btn_take_away);
        btn_take_away.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenuSelect("Take Away");
        }
        });


        btn_add_new_table = (Button)findViewById(R.id.btn_add_new_table);
        btn_add_new_table.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(restaurant_take_order.this, restaurant_add_new_table.class);
                myIntent.putExtra("Session ID",session_name);
                myIntent.putExtra("Shop",shop_name);
                myIntent.putExtra("UN",un);
                startActivity(myIntent);
            }
        });
        //product gridlayout here
        rcv = (RecyclerView) findViewById(R.id.recycleview_gridlayout_restaurant);
        rcv.setHasFixedSize(true);
        GridLayoutManager glm = new GridLayoutManager(this,3);
        rcv.setLayoutManager(glm);
        list = new ArrayList<Table>();
        //get reference from firebase
        db = FirebaseDatabase.getInstance().getReference("table").child(session_name).child("Restaurant");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                if (dataSnapshot.hasChildren()) {
                    for(DataSnapshot d : dataSnapshot.getChildren()) {
                        final Table tt = d.getValue(Table.class);
                        list.add(tt);
                    }
                    adapter_table = new MyAdapter_Table_gridlayout(restaurant_take_order.this,list){
                        @Override
                        public void onBindViewHolder(@NonNull final MyAdapter_Table_gridlayout.MyViewHolder holder, final int position) {
                            holder.table_pax_tv.setText("PAX : "+tables.get(position).pax);
                            holder.table_layout_tv.setText(tables.get(position).table);
                          if(tables.get(position).status.equals("R")){
                        holder.table_layout_tv.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.new_r_s_2, null));
                              holder.table_layout_tv.setTextColor(Color.parseColor("#FFFFFF"));
                            }else if(tables.get(position).status.equals("N")){
                                holder.table_layout_tv.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.n_s_2, null));
                              holder.table_layout_tv.setTextColor(Color.parseColor("#FFFFFF"));
                            }
                            holder.table_layout_tv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    final Table t = list.get(position);
                                    if(t.getStatus().equals("A")){
                                        LayoutInflater li = LayoutInflater.from(restaurant_take_order.this);
                                        View promptsView = li.inflate(R.layout.promt_table_a, null);
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(restaurant_take_order.this);
                                        // set prompts.xml to alertdialog builder
                                        alertDialogBuilder.setView(promptsView);
                                        final Button open = (Button) promptsView.findViewById(R.id.promt_open);
                                        final Button reserve = (Button) promptsView.findViewById(R.id.promt_reserve);
                                        // set dialog message
                                        alertDialogBuilder.setCancelable(true);
                                        // create alert dialog
                                        final AlertDialog alertDialog = alertDialogBuilder.create();
                                        // show it
                                        alertDialog.show();
                                        open.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                setTable(t.getTable(),"N");
                                                alertDialog.cancel();
                                            }
                                        });
                                        reserve.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                setTable(t.getTable(),"R");
                                                alertDialog.cancel();
                                            }
                                        });
                                    }else  if(t.getStatus().equals("R")){
                                        LayoutInflater li = LayoutInflater.from(restaurant_take_order.this);
                                        View promptsView = li.inflate(R.layout.promt_table_r, null);
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(restaurant_take_order.this);
                                        // set prompts.xml to alertdialog builder
                                        alertDialogBuilder.setView(promptsView);
                                        final Button open = (Button) promptsView.findViewById(R.id.promt_open);
                                        final Button cancel = (Button) promptsView.findViewById(R.id.promt_cancel);
                                        // set dialog message
                                        alertDialogBuilder.setCancelable(true);
                                        // create alert dialog
                                        final AlertDialog alertDialog = alertDialogBuilder.create();
                                        // show it
                                        alertDialog.show();
                                        open.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                setTable(t.getTable(),"N");
                                                alertDialog.cancel();
                                            }
                                        });
                                        cancel.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                setTable(t.getTable(),"A");
                                                alertDialog.cancel();
                                            }
                                        });
                                    }else if(t.getStatus().equals("N")){
                                        LayoutInflater li = LayoutInflater.from(restaurant_take_order.this);
                                        View promptsView = li.inflate(R.layout.promt_table_n, null);
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(restaurant_take_order.this);

                                        alertDialogBuilder.setView(promptsView);
                                        final Button order = (Button) promptsView.findViewById(R.id.promt_order);
                                        final Button view_order = (Button) promptsView.findViewById(R.id.promt_view_order);
                                        final Button bill = (Button) promptsView.findViewById(R.id.promt_bill);
                                        final Button cancel = (Button) promptsView.findViewById(R.id.promt_cancel);

                                        alertDialogBuilder.setCancelable(true);

                                        final AlertDialog alertDialog = alertDialogBuilder.create();

                                        alertDialog.show();
                                        order.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                showMenuSelect(t.getTable());
                                                alertDialog.cancel();
                                            }
                                        });
                                        view_order.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                ///view order list
                                                showOrderByTable(t.getTable());
                                                alertDialog.cancel();
                                            }
                                        });
                                        bill.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                        ///make payment
                                                goBillByTable(t.getTable());
                                                alertDialog.cancel();
                                            }
                                        });
                                        cancel.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                setTable(t.getTable(),"A");
                                                alertDialog.cancel();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    };
                    rcv.setAdapter(adapter_table);
                    adapter_table.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
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
                Intent myIntent = new Intent(restaurant_take_order.this, profile.class);
                myIntent.putExtra("Session ID",session_name);
                myIntent.putExtra("Shop",shop_name);
                myIntent.putExtra("UN",un);
                startActivity(myIntent);
                finish();
            }
        });
    }
    private void showOrderByTable(final String table) {
        LayoutInflater li = LayoutInflater.from(restaurant_take_order.this);
        View promptsView = li.inflate(R.layout.table_order_list2, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(restaurant_take_order.this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(true);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        rcv_order_list_in_menu =(RecyclerView) promptsView.findViewById(R.id.rcv_order_list_in_menu);
        //recycleview
        rcv_order_list_in_menu.setLayoutManager(new LinearLayoutManager(this));
        list3 = new ArrayList<Item>();
        DatabaseReference db1 = FirebaseDatabase.getInstance().getReference("order").child(session_name).child("Restaurant").child(table);
        db1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list3.clear();
                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                        Item i = ds.getValue(Item.class);
                        list3.add(i);
                    }
                }
                adapter_menuList = new MyAdapter_MenuList(restaurant_take_order.this,list3){
                    @Override
                    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
                        holder.item_name.setText((position+1)+".)  "+items.get(position).product_name);
                        holder.item_price.setText("x "+items.get(position).quantity);
                        holder.item_name.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String i = items.get(position).product_name;
                                confirmDelete(table,i,alertDialog);
                            }
                        });
                    }
                };
                rcv_order_list_in_menu.setAdapter(adapter_menuList);
                adapter_menuList.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
    private void goBillByTable(String table) {
        Intent myIntent = new Intent(restaurant_take_order.this, restaurant_view_order_list.class);
        myIntent.putExtra("Session ID",session_name);
        myIntent.putExtra("Shop",shop_name);
        myIntent.putExtra("UN",un);
        myIntent.putExtra("Table",table);
        startActivity(myIntent);
    }
    private void showMenuSelect(final String table) {
        LayoutInflater li = LayoutInflater.from(restaurant_take_order.this);
        View promptsView = li.inflate(R.layout.promt_menu_order, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(restaurant_take_order.this);
        alertDialogBuilder.setView(promptsView);
        final EditText search_item_menu =(EditText) promptsView.findViewById(R.id.search_item_menu);
        search_item_menu.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        search_item_menu.addTextChangedListener(new TextWatcher() {
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
        spinner_menu=(Spinner) promptsView.findViewById(R.id.spinner_menu);
        addItemOnSpinner();
        rcv_menu_list = (RecyclerView) promptsView.findViewById(R.id.rcv_menu_list);
        //recycleview
        rcv_menu_list.setLayoutManager(new LinearLayoutManager(this));
        list2 = new ArrayList<Item>();
        spinner_menu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String s = spinner_menu.getSelectedItem().toString();
                DatabaseReference db1 = FirebaseDatabase.getInstance().getReference("product").child(session_name).child("Restaurant");
                Query q = db1.orderByChild("category").startAt(s).endAt(s+"\uf8ff");
                q.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        list2.clear();
                        if (dataSnapshot.hasChildren()){
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                            {
                                Item i = dataSnapshot1.getValue(Item.class);
                                list2.add(i);
                            }
                        }
                        adapter_menuList = new MyAdapter_MenuList(restaurant_take_order.this,list2){
                            @Override
                            public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
                                holder.item_name.setText(items.get(position).product_name);
                                holder.item_price.setText("RM "+items.get(position).price);
                                holder.item_name.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        final Item i = list2.get(position);
                                        int quantity = 1;
                                        placeOrderToWaiter(i.product_name,i.price,i.category,quantity,table);
                                    }
                                });
                            }
                        };
                        rcv_menu_list.setAdapter(adapter_menuList);
                        adapter_menuList.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(restaurant_take_order.this, "Something wrong...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        final Button button_done = (Button) promptsView.findViewById(R.id.button_done);
        alertDialogBuilder.setCancelable(true);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        button_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrderListbyTable(table);
                alertDialog.cancel();
            }
        });

    }
    private void placeOrderToWaiter(final String pn, final String pp, final String pc, final int pq, final String table) {

        final DatabaseReference dp = FirebaseDatabase.getInstance().getReference("waiter_view").child(session_name).child("Restaurant").child(table);
        dp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int oq = pq;
                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                        Item ic = ds.getValue(Item.class);
                        if(ic.product_name.equals(pn)){
                            oq = pq + Integer.parseInt(ic.quantity);
                            existed=true;
                            break;
                        }
                    }
                }
                if(existed){
                    dp.child(pn).child("quantity").setValue(String.valueOf(oq));
                    Toast.makeText(restaurant_take_order.this,pn+" is added ! \nCurrent quantity : "+oq,Toast.LENGTH_SHORT).show();
                    existed=false;
                }else{
                    final Item i2 = new Item(pn,pp,String.valueOf(oq),table);
                    dp.child(pn).setValue(i2);
                    Toast.makeText(restaurant_take_order.this,pn+" is added ! \nCurrent quantity :  "+oq,Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void showOrderListbyTable(final String table){
        LayoutInflater li = LayoutInflater.from(restaurant_take_order.this);
        View promptsView = li.inflate(R.layout.table_order_list, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(restaurant_take_order.this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(true);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        rcv_order_list_in_menu =(RecyclerView) promptsView.findViewById(R.id.rcv_order_list_in_menu);
        final Button send_kitchen = (Button)promptsView.findViewById(R.id.send_kitchen);
        final Button back_to_order = (Button)promptsView.findViewById(R.id.back_to_order);
        final Button btn_extra_note = (Button)promptsView.findViewById(R.id.btn_extra_note);
        //recycleview
        rcv_order_list_in_menu.setLayoutManager(new LinearLayoutManager(this));
        list3 = new ArrayList<Item>();
        DatabaseReference db1 = FirebaseDatabase.getInstance().getReference("waiter_view").child(session_name).child("Restaurant").child(table);
        db1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list3.clear();
                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                        Item i = ds.getValue(Item.class);
                        list3.add(i);
                    }
                }
                adapter_menuList = new MyAdapter_MenuList(restaurant_take_order.this,list3){
                    @Override
                    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
                        holder.item_name.setText((position+1)+".)  "+items.get(position).product_name);
                        holder.item_price.setText("x "+items.get(position).quantity);
                        holder.item_name.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String i = items.get(position).product_name;
                                confirmDelete(table,i,alertDialog);
                            }
                        });
                    }
                };
                rcv_order_list_in_menu.setAdapter(adapter_menuList);
                adapter_menuList.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        send_kitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference wodb = FirebaseDatabase.getInstance().getReference("waiter_view").child(session_name).child("Restaurant").child(table);
                wodb.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()){
                            placeToOrder(table);
                            placeOrderToKitchen(table);
                            alertDialog.dismiss();
                            if(table.equals("Take Away")){
                                goBillByTable(table);
                            }
                        }else{
                            Toast.makeText(getApplicationContext(), "Order list is empty.\n Please place some order(s).", Toast.LENGTH_LONG).show();
                            showMenuSelect(table);
                            alertDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });
        back_to_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                showMenuSelect(table);
            }
        });
        btn_extra_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(restaurant_take_order.this);
                View promptsView = li.inflate(R.layout.promt_extra_note, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(restaurant_take_order.this);
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);
                final EditText user_extra_note = (EditText) promptsView.findViewById(R.id.extra_note);
                final Button done =  promptsView.findViewById(R.id.btn_ok);
                user_extra_note.setText(extra_note);
                // set dialog message
                alertDialogBuilder.setCancelable(true);
                // create alert dialog
                final AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (user_extra_note.getText().toString().equals("")){
                            extra_note = "none";
                            alertDialog.cancel();
                        }else{
                            extra_note = user_extra_note.getText().toString();
                            alertDialog.cancel();
                        }
                    }
                });
            }
        });
    }
    private void placeToOrder(final String table) {
        final ArrayList<Item>  check_list = new ArrayList<Item>();
        final ArrayList<Item>  item_list = new ArrayList<Item>();
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference("order").child(session_name).child("Restaurant").child(table);
        final DatabaseReference wodb = FirebaseDatabase.getInstance().getReference("waiter_view").child(session_name).child("Restaurant").child(table);
        wodb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                item_list.clear();
                if (dataSnapshot.hasChildren()){
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                        Item i = ds.getValue(Item.class);
                        item_list.add(i);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                        Item i = ds.getValue(Item.class);
                        check_list.add(i);
                    }
                }else{
                    for(int x=0;x<item_list.size();x++){
                        Item i1 = new Item(item_list.get(x).product_name,item_list.get(x).price,item_list.get(x).quantity,table);
                        db.child(item_list.get(x).product_name).setValue(i1);
                    }
                }
                for(int c=0;c<item_list.size();c++){
                    String pn = item_list.get(c).product_name;
                    String pq = item_list.get(c).quantity;
                    String pp = item_list.get(c).price;
                    int q = Integer.parseInt(pq);
                    boolean check_existed =false;
                    for (int v=0;v<check_list.size();v++){
                        String cpn = check_list.get(v).product_name;
                        String cpq = check_list.get(v).quantity;
                        if(pn.equals(cpn)){
                            q = q+ Integer.parseInt(cpq);
                            check_existed =true;
                            break;
                        }
                    }
                    if(check_existed){
                        db.child(pn).child("quantity").setValue(String.valueOf(q));
                    }else{
                        Item i1 = new Item(pn,pp,pq,table);
                        db.child(pn).setValue(i1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

    }
    private void clearWaiterOrder(String table) {
        final DatabaseReference dp = FirebaseDatabase.getInstance().getReference("waiter_view").child(session_name).child("Restaurant").child(table);
        dp.removeValue();
        Toast.makeText(getApplicationContext(), "Order had send to kitchen !", Toast.LENGTH_LONG).show();
    }
    private void placeOrderToKitchen(final String table) {
        final String se  = new SimpleDateFormat("HH:mm:ss EEEE", Locale.getDefault()).format(new Date());
        final ArrayList<Item>  item_list = new ArrayList<Item>();
        final DatabaseReference dp = FirebaseDatabase.getInstance().getReference("kitchen_view").child(session_name).child("Restaurant");
        final DatabaseReference dp2 = FirebaseDatabase.getInstance().getReference("bridge_entity_2").child(session_name).child("Restaurant");
        final String uid =dp2.push().getKey();
        final Kitchen k = new Kitchen(table,uid,se,extra_note);
        final DatabaseReference wodb = FirebaseDatabase.getInstance().getReference("waiter_view").child(session_name).child("Restaurant").child(table);
        wodb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                item_list.clear();
                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                        Item i = ds.getValue(Item.class);
                        item_list.add(i);
                    }
                    clearWaiterOrder(table);
                }
                for (int i =0;i<item_list.size();i++){
                    String pn = item_list.get(i).product_name;
                    String pp  = item_list.get(i).price;
                    String pq  = item_list.get(i).quantity;
                    String table  = item_list.get(i).table;
                    Item i2 = new Item(pn,pp,pq,se,table);
                    dp.child(uid).child(pn).setValue(i2);
                }
                dp2.child(uid).setValue(k);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
    private void search(String s) {
        rcv_menu_list.setAdapter(null);
        s = s.toUpperCase();
        DatabaseReference db1 = FirebaseDatabase.getInstance().getReference("product").child(session_name).child("Restaurant");
        Query q = db1.orderByChild("product_name").startAt(s).endAt(s+"\uf8ff");
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    list2.clear();
                    for (DataSnapshot d: dataSnapshot.getChildren()){
                        final Item pp = d.getValue(Item.class);
                        list2.add(pp);
                    }
                    adapter_menuList = new MyAdapter_MenuList(restaurant_take_order.this,list2){
                        @Override
                        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
                            holder.item_name.setText(items.get(position).product_name);
                            holder.item_price.setText("RM "+items.get(position).price);
                        }
                    };
                    rcv_menu_list.setAdapter(adapter_menuList);
                    adapter_menuList.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
    public void addItemOnSpinner(){
        final ArrayList<String> s = new ArrayList<String>();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("category").child(session_name).child("Restaurant");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
                        String c = dataSnapshot1.child("category").getValue(String.class);
                        s.add(c);
                    }
                }
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(restaurant_take_order.this, android.R.layout.simple_spinner_item, s);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_menu.setAdapter(areasAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(restaurant_take_order.this, "Something wrong...", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setTable(String t,String status) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("table").child(session_name).child("Restaurant").child(t).child("status");
        db.setValue(status);
    }
    private void confirmDelete(final String table, final String item, final AlertDialog a) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.promt_update_delete_category, null);
        dialogBuilder.setView(dialogView);
        final Button buttonYes = (Button) dialogView.findViewById(R.id.buttonDeleteYes);
        final Button buttonNo = (Button) dialogView.findViewById(R.id.buttonDeleteNo);

        dialogBuilder.setTitle("Confirm to remove this item from the order?");
        dialogBuilder.setCancelable(true);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(table,item);
                list3.clear();
                a.dismiss();
                adapter_menuList.notifyDataSetChanged();
                overridePendingTransition(0, 0);
                b.dismiss();
                showOrderListbyTable(table);
            }
        });
        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.cancel();
            }
        });
    }
    private boolean deleteItem(String table,String item) {
        //getting the specified artist reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("waiter_view").child(session_name).child("Restaurant").child(table).child(item);
        //removing value
        dR.removeValue();
        Toast.makeText(getApplicationContext(), "Item removed from the order", Toast.LENGTH_LONG).show();
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

        } else if (id == R.id.nav_manage_menu) {
            Intent myIntent = new Intent(restaurant_take_order.this, restaurant_menu.class);
            myIntent.putExtra("Session ID",session_name);
            myIntent.putExtra("Shop",shop_name);
            myIntent.putExtra("UN",un);
            startActivity(myIntent);
        } else if (id == R.id.nav_dashboard_2) {
            Intent myIntent = new Intent(restaurant_take_order.this, restaurant_dashboard.class);
            myIntent.putExtra("Session ID",session_name);
            myIntent.putExtra("Shop",shop_name);
            myIntent.putExtra("UN",un);
            startActivity(myIntent);
        } else if (id == R.id.nav_retail ){
            Intent myIntent = new Intent(restaurant_take_order.this, retail_main_menu.class);
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
