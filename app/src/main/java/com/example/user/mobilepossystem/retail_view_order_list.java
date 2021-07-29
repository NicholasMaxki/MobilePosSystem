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
import java.text.DecimalFormat;
import java.util.ArrayList;
public class retail_view_order_list extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    private String session_name,shop_name,un;
    private Button btn_order_check;
    private RecyclerView recycleview_order_list_retail;
    private TextView total_price_order_list,add_discount_retail,sub_total_price_order_list,st_textview_order_retail;
    private DatabaseReference db;
    private ArrayList<Order> list;
    private MyAdapter_OrderList adapter_orderList;
    private static DecimalFormat df2 = new DecimalFormat("0.00");
    private double discount_rate = 100.00, intent_discount = 0.00;
    private double total_price_global_use=0,keep_use_total=0,intent_sub_total;
    private boolean check_discount = false,orderlist_isEMPTY = false,enough=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retail_view_order_list);
        Intent i = getIntent();
        session_name = i.getExtras().getString("Session ID");
        shop_name = i.getExtras().getString("Shop");
        un = i.getExtras().getString("UN");
        total_price_order_list = (TextView)findViewById(R.id.total_price_order_list);
        st_textview_order_retail = (TextView)findViewById(R.id.st_textview_order_retail);
        sub_total_price_order_list= (TextView)findViewById(R.id.sub_total_price_order_list);
        //recycleview
        recycleview_order_list_retail = (RecyclerView) findViewById(R.id.recyclevoew_order_list_retail);
        recycleview_order_list_retail.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<Order>();
        db = FirebaseDatabase.getInstance().getReference("order").child(session_name).child("Retail");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double total_price=0;
                if(dataSnapshot.hasChildren()){
                    orderlist_isEMPTY = false;
                    list.clear();
                    for (DataSnapshot d: dataSnapshot.getChildren()){
                        final Order o = d.getValue(Order.class);
                        list.add(o);
                        total_price += (Double.parseDouble(o.getPrice())*Integer.parseInt(o.getQuantity()));
                    }
                       total_price_global_use = total_price;
                        keep_use_total = total_price;
                    intent_sub_total = total_price_global_use;
                    total_price_order_list.setText("RM "+df2.format(total_price));
                    adapter_orderList = new MyAdapter_OrderList(retail_view_order_list.this,list){
                        @Override
                        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
                            holder.order_product_name_retail.setText(orders.get(position).product_name);
                            holder.order_price_retail.setText("RM "+(df2.format(Double.parseDouble(orders.get(position).quantity)*Double.parseDouble(orders.get(position).price))));
                            holder.order_quantity_retail.setText("X "+orders.get(position).quantity);
                            holder.order_ori_price_retail.setText("(RM "+orders.get(position).price+")");
                           holder.order_product_name_retail.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Order order = list.get(position);
                                    showSelection(order.getProduct_name(),order.getQuantity());
                                }
                            });
                        }
                    };
                    recycleview_order_list_retail.setAdapter(adapter_orderList);
                    adapter_orderList.notifyDataSetChanged();
                }else{
                        orderlist_isEMPTY = true;
                    total_price_order_list.setText("RM 0.00");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        btn_order_check = (Button)findViewById(R.id.btn_order_checkout);
        btn_order_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orderlist_isEMPTY){
                    Toast.makeText(getApplicationContext(), "Order List is empty", Toast.LENGTH_LONG).show();
                }else{
                    Intent myIntent = new Intent(retail_view_order_list.this, retail_make_payment.class);
                    myIntent.putExtra("Session ID",session_name);
                    myIntent.putExtra("Shop",shop_name);
                    myIntent.putExtra("UN",un);
                    myIntent.putExtra("Discount",intent_discount);
                    myIntent.putExtra("Sub Total",intent_sub_total);
                    myIntent.putExtra("Grand Total",total_price_global_use);
                    startActivity(myIntent);
                    finish();
                }
            }
        });
        add_discount_retail = (TextView)findViewById(R.id.add_discount_retail);
        add_discount_retail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(retail_view_order_list.this);
                View promptsView = li.inflate(R.layout.promt_testing, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(retail_view_order_list.this);
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);
                final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
                final Button done = promptsView.findViewById(R.id.btn_ok);
                final TextView tv = promptsView.findViewById(R.id.tv);
                tv.setText("Insert discount rate ( %) : ");
                alertDialogBuilder.setCancelable(true);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String user_input;
                        if(userInput.getText().toString().equals("")){
                            check_discount = false;
                            alertDialog.dismiss();
                        }else if(Double.parseDouble(userInput.getText().toString())>=100){
                            check_discount = false;
                            alertDialog.dismiss();
                        } else{
                            total_price_global_use = keep_use_total;
                            user_input = userInput.getText().toString();
                            check_discount = true;
                            double dr = Double.parseDouble(user_input);
                            discountTotalPrice(dr);
                            intent_discount = dr;
                            alertDialog.dismiss();
                        }
                    }
                });
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
                Intent myIntent = new Intent(retail_view_order_list.this, profile.class);
                myIntent.putExtra("Session ID",session_name);
                myIntent.putExtra("Shop",shop_name);
                myIntent.putExtra("UN",un);
                myIntent.putExtra("Mode","Retail");
                startActivity(myIntent);
                finish();
            }
        });
    }
    private void discountTotalPrice(double dr) {
        if(check_discount){
            discount_rate = (100-dr)/100;
        }
        if(discount_rate==1) {
            add_discount_retail.setText("Add Discount");
            st_textview_order_retail.setVisibility(View.INVISIBLE);
            sub_total_price_order_list.setVisibility(View.INVISIBLE);
            intent_sub_total = total_price_global_use;
            total_price_order_list.setText("RM " + df2.format(total_price_global_use));
        }else{
            double sub_total = total_price_global_use;
            total_price_global_use=total_price_global_use * discount_rate;
            int d = (int)dr;
            add_discount_retail.setText("Discount rate : "+String.valueOf(d)+" %");
            st_textview_order_retail.setVisibility(View.VISIBLE);
            sub_total_price_order_list.setVisibility(View.VISIBLE);
            sub_total_price_order_list.setText("RM " + df2.format(sub_total));
            intent_sub_total = sub_total;
            total_price_order_list.setText("RM " + df2.format(total_price_global_use));
        }
        check_discount = false;
    }
    private void showSelection(final String product_name, String quantity) {
        //prompt popup coding start here
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.promt_update_delete_order_list, null);
        dialogBuilder.setView(dialogView);
        final TextView tv =dialogView.findViewById(R.id.tv1);
        tv.setText(product_name);
        final Button btn_update_quantity_retail = (Button) dialogView.findViewById(R.id.btn_update_quantity_retail);
        final Button btn_remove_order_retail = (Button) dialogView.findViewById(R.id.btn_remove_order_retail);
        dialogBuilder.setCancelable(true);
        final AlertDialog b = dialogBuilder.create();
        b.show();
        btn_update_quantity_retail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();
                LayoutInflater li = LayoutInflater.from(retail_view_order_list.this);
                View promptsView = li.inflate(R.layout.promt_testing, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(retail_view_order_list.this);
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);
                final TextView tv = promptsView.findViewById(R.id.tv);
                tv.setText("Insert new quantity :");
                final Button done =  promptsView.findViewById(R.id.btn_ok);
                final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
                // set dialog message
                alertDialogBuilder.setCancelable(true);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(TextUtils.isEmpty(userInput.getText())){
                            alertDialog.cancel();
                        }else{
                            if(Integer.parseInt(userInput.getText().toString())<=0){
                                Toast.makeText(retail_view_order_list.this,"Quantity should not less than 0, else remove the item from order list !",Toast.LENGTH_SHORT).show();
                                userInput.setText(null);
                            }else{
                                final int qty = Integer.parseInt(userInput.getText().toString());
                                DatabaseReference dr = FirebaseDatabase.getInstance().getReference("product").child(session_name).child("Retail");
                                dr.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChildren()) {
                                            for (DataSnapshot d:dataSnapshot.getChildren()){
                                                Product here = d.getValue(Product.class);
                                                if(here.getProduct_name().equals(product_name)){
                                                    if(qty<=Integer.parseInt(here.getQuantity())){
                                                        enough = true;
                                                        break;
                                                    }
                                                }
                                            }
                                        }else{
                                            Toast.makeText(retail_view_order_list.this,"No Child",Toast.LENGTH_SHORT).show();
                                        }
                                        if(enough){
                                            updateQTY(product_name,qty);
                                            enough=false;
                                        }else{
                                            Toast.makeText(retail_view_order_list.this,product_name+"'s stock limit is reached..",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                                });
                                alertDialog.cancel();
                            }
                        }
                    }
                });
            }
        });
        btn_remove_order_retail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(retail_view_order_list.this);
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.promt_update_delete_category, null);
                dialogBuilder.setView(dialogView);
                final TextView tv = dialogView.findViewById(R.id.textview_id);
                tv.setText("Confirm to remove this product from the order list?");
                final Button buttonYes = (Button) dialogView.findViewById(R.id.buttonDeleteYes);
                final Button buttonNo = (Button) dialogView.findViewById(R.id.buttonDeleteNo);
                dialogBuilder.setCancelable(true);
                final AlertDialog bb = dialogBuilder.create();
                bb.show();
                buttonYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteOrder(product_name);
                        list.clear();
                        adapter_orderList.notifyDataSetChanged();
                        overridePendingTransition(0, 0);;
                        bb.dismiss();
                    }
                });
                buttonNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bb.cancel();
                    }
                });
            }
        });
    }
    private boolean updateQTY(String product_name, int qty) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("order").child(session_name).child("Retail").child(product_name).child("quantity");
        //updating qty
        dR.setValue(String.valueOf(qty));
        Toast.makeText(getApplicationContext(), "Quantity updated", Toast.LENGTH_LONG).show();
        add_discount_retail.setText("Add Discount");
        st_textview_order_retail.setVisibility(View.INVISIBLE);
        sub_total_price_order_list.setVisibility(View.INVISIBLE);
        adapter_orderList.notifyDataSetChanged();
        overridePendingTransition(0, 0);
        return true;
    }
    private boolean deleteOrder(String product_name) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("order").child(session_name).child("Retail").child(product_name);
        //removing order from list
        dR.removeValue();
        Toast.makeText(getApplicationContext(), product_name+" has been removed from the order list", Toast.LENGTH_LONG).show();
        add_discount_retail.setText("Add Discount");
        st_textview_order_retail.setVisibility(View.INVISIBLE);
        sub_total_price_order_list.setVisibility(View.INVISIBLE);
        adapter_orderList.notifyDataSetChanged();
        overridePendingTransition(0, 0);
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

        } else if (id == R.id.nav_inventory) {
            Intent myIntent = new Intent(retail_view_order_list.this, retail_inventory.class);
            myIntent.putExtra("Session ID",session_name);
            myIntent.putExtra("Shop",shop_name);
            myIntent.putExtra("UN",un);
            startActivity(myIntent);
            finish();
        }  else if (id == R.id.nav_dashboard) {
            Intent myIntent = new Intent(retail_view_order_list.this, retail_dashboard.class);
            myIntent.putExtra("Session ID",session_name);
            myIntent.putExtra("Shop",shop_name);
            myIntent.putExtra("UN",un);
            startActivity(myIntent);
            finish();
        } else if (id == R.id.nav_restaurant) {
            Intent myIntent = new Intent(retail_view_order_list.this, restaurant_role.class);
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
