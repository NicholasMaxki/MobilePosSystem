package com.example.user.mobilepossystem;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
public class retail_make_payment extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private String session_name,shop_name,un;
    private int nd;
    private double sub_total,grand_total,discount,discount_rate;
    private static DecimalFormat df2 = new DecimalFormat("0.00");
    private TextView grand_total_retail,sub_total_retail_2,discount_2;
    private EditText receive_money_retail;
    private ImageButton btn_cash_payment_retail,btn_card_payment_retail;
    private  Button btn_paypal_payment_retail;
    private  String p_type,paypal_grand_total;
    public static final int PAYPAL_REQUEST_CODE = 123;
    // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
    // or live (ENVIRONMENT_PRODUCTION)
    private static PayPalConfiguration config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX).clientId(PayPalConfig.PAYPAL_CLIENT_ID);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retail_make_payment);

        Intent i = getIntent();
        session_name = i.getExtras().getString("Session ID");
        shop_name = i.getExtras().getString("Shop");
        un = i.getExtras().getString("UN");
        discount =  i.getExtras().getDouble("Discount");
        sub_total = i.getExtras().getDouble("Sub Total");
        grand_total = i.getExtras().getDouble("Grand Total");
        ///start paypal service
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        sub_total_retail_2  = (TextView) findViewById(R.id.sub_total_retail_2);
        sub_total_retail_2.setText("RM " + df2.format(sub_total));
        nd = (int) discount;
        discount_rate = sub_total - grand_total;
        discount_2  = (TextView) findViewById(R.id.discount_2);
        discount_2.setText(nd+"% (-RM "+df2.format(discount_rate)+")");
        grand_total_retail = (TextView) findViewById(R.id.grand_total_retail);
        grand_total_retail.setText("RM " + df2.format(grand_total));

        btn_cash_payment_retail = (ImageButton)findViewById(R.id.btn_cash_payment_retail);
        btn_cash_payment_retail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               p_type = "Cash Paid";
                call_prompt();
            }
        });
        btn_card_payment_retail = (ImageButton)findViewById(R.id.btn_card_payment_retail);
        btn_card_payment_retail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                p_type = "Card Paid";
                final String today_Month = new SimpleDateFormat("MMMM", Locale.getDefault()).format(new Date());
                final String today_Year = new SimpleDateFormat("YYYY", Locale.getDefault()).format(new Date());
                final String unique_id = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss EEEE", Locale.getDefault()).format(new Date());
                final String today_Date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                final DatabaseReference dr_1 = FirebaseDatabase.getInstance().getReference("transaction");
                final String order_id = dr_1.push().getKey();
                dr_1.addListenerForSingleValueEvent(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Transaction t = new Transaction(today_Month,today_Year,order_id,unique_id,p_type,String.valueOf(df2.format(sub_total)),String.valueOf(df2.format(grand_total)),String.valueOf((int)discount),String.valueOf(discount_rate),String.valueOf(0),String.valueOf(0),today_Date,"");
                        String label = unique_id;
                        dr_1.child(session_name).child("Retail").child(label).setValue(t);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
                setSalesByYearMonth(today_Year,today_Month,grand_total);
                reduceStock();
                setBestSell();
                setOrderListtoTransaction(unique_id);
                clearOrderList();
                Intent myIntent = new Intent(retail_make_payment.this, retail_payment_receipt.class);
                myIntent.putExtra("Session ID",session_name);
                myIntent.putExtra("Shop",shop_name);
                myIntent.putExtra("UN",un);
                myIntent.putExtra("Payment Type",p_type);
                myIntent.putExtra("Order ID",order_id);
                myIntent.putExtra("Change",0);
                myIntent.putExtra("Cash",0);
                myIntent.putExtra("Discount",nd);
                myIntent.putExtra("Sub Total",sub_total);
                myIntent.putExtra("Discount rate",discount_rate);
                myIntent.putExtra("Grand Total",grand_total);
                myIntent.putExtra("Date Time",unique_id);
                startActivity(myIntent);
                finish();
            }
        });
        btn_paypal_payment_retail = (Button)findViewById(R.id.btn_paypal_payment_retail);
        btn_paypal_payment_retail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                p_type = "PayPal";
                getPayment();
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
                Intent myIntent = new Intent(retail_make_payment.this, profile.class);
                myIntent.putExtra("Session ID",session_name);
                myIntent.putExtra("Shop",shop_name);
                myIntent.putExtra("UN",un);
                myIntent.putExtra("Mode","Retail");
                startActivity(myIntent);
                finish();
            }
        });
    }
    private void call_prompt() {
        LayoutInflater li = LayoutInflater.from(retail_make_payment.this);
        View promptsView = li.inflate(R.layout.promt_testing, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(retail_make_payment.this);
        alertDialogBuilder.setView(promptsView);
        final TextView tv =promptsView.findViewById(R.id.tv);
        tv.setText("Please insert the cash received :");
        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
        final Button done = promptsView.findViewById(R.id.btn_ok);
        userInput.setText(String.valueOf(df2.format(grand_total)));
        alertDialogBuilder.setCancelable(true);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final double cash = Double.parseDouble(userInput.getText().toString());
                if(cash >= grand_total){
                    final double change = cash - grand_total;
                    final String today_Month = new SimpleDateFormat("MMMM", Locale.getDefault()).format(new Date());
                    final String today_Year = new SimpleDateFormat("YYYY", Locale.getDefault()).format(new Date());
                    final String today_Date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    final String unique_id = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss EEEE", Locale.getDefault()).format(new Date());
                    final DatabaseReference dr_1 = FirebaseDatabase.getInstance().getReference("transaction");
                    final String order_id = dr_1.push().getKey();
                    dr_1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Transaction t = new Transaction(today_Month,today_Year,order_id,unique_id,p_type,String.valueOf(df2.format(sub_total)),String.valueOf(df2.format(grand_total)),String.valueOf((int)discount),String.valueOf(discount_rate),String.valueOf(change),String.valueOf(cash),today_Date,"");
                            String label = unique_id;
                            dr_1.child(session_name).child("Retail").child(label).setValue(t);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
                    setSalesByYearMonth(today_Year,today_Month,grand_total);
                    reduceStock();
                    setBestSell();
                    setOrderListtoTransaction(unique_id);
                    clearOrderList();
                    Intent myIntent = new Intent(retail_make_payment.this, retail_payment_receipt.class);
                    myIntent.putExtra("Session ID",session_name);
                    myIntent.putExtra("Shop",shop_name);
                    myIntent.putExtra("UN",un);
                    myIntent.putExtra("Payment Type",p_type);
                    myIntent.putExtra("Order ID",order_id);
                    myIntent.putExtra("Change",change);
                    myIntent.putExtra("Cash",cash);
                    myIntent.putExtra("Discount",nd);
                    myIntent.putExtra("Sub Total",sub_total);
                    myIntent.putExtra("Discount rate",discount_rate);
                    myIntent.putExtra("Grand Total",grand_total);
                    myIntent.putExtra("Date Time",unique_id);
                    startActivity(myIntent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Cash receive must be greater or equal to grand total !", Toast.LENGTH_LONG).show();
                    alertDialog.dismiss();
                    call_prompt();
                }
            }
        });

    }
    private void setSalesByYearMonth(final String today_year, final String today_month, final double grand_total) {
        final DatabaseReference db= FirebaseDatabase.getInstance().getReference("sales").child(session_name).child("Retail").child("Monthly").child(today_year);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot d: dataSnapshot.getChildren()){
                        Sales ss = d.getValue(Sales.class);
                        if(ss.getMonth().equals(today_month)){
                            double new_total=0;
                            double g_total = Double.parseDouble(ss.getTotal());
                            new_total = g_total+grand_total;
                            db.child(today_month).child("total").setValue(String.valueOf(df2.format(new_total)));
                        }else{
                            Sales s = new Sales(today_year,today_month,String.valueOf(df2.format(grand_total)));
                            db.child(today_month).setValue(s);
                        }
                    }
                }else{
                    Sales s = new Sales(today_year,today_month,String.valueOf(df2.format(grand_total)));
                    db.child(today_month).setValue(s);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        final DatabaseReference db2= FirebaseDatabase.getInstance().getReference("sales").child(session_name).child("Retail").child("Yearly");
        db2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot d: dataSnapshot.getChildren()){
                        Sales ss = d.getValue(Sales.class);
                        if(ss.getYear().equals(today_year)){
                            double new_total=0;
                            double g_total = Double.parseDouble(ss.getTotal());
                            new_total = g_total+grand_total;
                            db2.child(today_year).child("total").setValue(String.valueOf(df2.format(new_total)));
                        }else{
                            Sales s = new Sales(today_year,String.valueOf(df2.format(grand_total)));
                            db2.child(today_year).setValue(s);
                        }
                    }
                }else{
                    Sales s = new Sales(today_year,String.valueOf(df2.format(grand_total)));
                    db2.child(today_year).setValue(s);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }
    private void setBestSell() {
        DatabaseReference dr_3 = FirebaseDatabase.getInstance().getReference("order").child(session_name).child("Retail");
        final ArrayList<Order> list1 = new ArrayList<>();
        dr_3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot d :dataSnapshot.getChildren()){
                        Order order = d.getValue(Order.class);
                        list1.add(order);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        DatabaseReference dp = FirebaseDatabase.getInstance().getReference("best_sell").child(session_name).child("Retail");
        dp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot d :dataSnapshot.getChildren()){
                       BestSell b =d.getValue(BestSell.class);
                        for(int i=0;i<list1.size();i++){
                            if(b.getProduct_name().equals(list1.get(i).getProduct_name())){
                                int new_qty = Integer.parseInt(b.getQuantity())+Integer.parseInt(list1.get(i).getQuantity());
                                updateBestSellQTY(b.getProduct_name(),new_qty);
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
    private boolean updateBestSellQTY(String product_name, int qty) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("best_sell").child(session_name).child("Retail").child(product_name).child("quantity");
        //updating qty
        dR.setValue(String.valueOf(qty));
        return true;
    }
    private void reduceStock() {
        DatabaseReference dr_3 = FirebaseDatabase.getInstance().getReference("order").child(session_name).child("Retail");
        final ArrayList<Order> list1 = new ArrayList<>();
        dr_3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot d :dataSnapshot.getChildren()){
                        Order order = d.getValue(Order.class);
                        list1.add(order);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        DatabaseReference dp = FirebaseDatabase.getInstance().getReference("product").child(session_name).child("Retail");
        dp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot d :dataSnapshot.getChildren()){
                      Product product =d.getValue(Product.class);
                        for(int i=0;i<list1.size();i++){
                          if(product.getProduct_name().equals(list1.get(i).getProduct_name())){
                              int new_qty = Integer.parseInt(product.getQuantity())-Integer.parseInt(list1.get(i).getQuantity());
                              updateQTY(product.getProduct_name(),new_qty);
                          }
                      }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
    private boolean updateQTY(String product_name, int qty) {
            DatabaseReference dR = FirebaseDatabase.getInstance().getReference("product").child(session_name).child("Retail").child(product_name).child("quantity");
            //updating qty
            dR.setValue(String.valueOf(qty));
            return true;
    }
    private void getPayment() {
        paypal_grand_total = String.valueOf(grand_total);
        //Creating a paypalpayment
        PayPalPayment payment = new PayPalPayment(new BigDecimal(paypal_grand_total), "USD", "Grand Total", PayPalPayment.PAYMENT_INTENT_SALE);
        //Creating Paypal Payment activity intent
        Intent intent = new Intent(this, PaymentActivity.class);
        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        //Puting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        //Starting the intent activity for result
        //the request code will be used on the method onActivityResult
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the result is from paypal
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                //if confirmation is not null
                if (confirm != null) {
                    try {
                        final String today_Month = new SimpleDateFormat("MMMM", Locale.getDefault()).format(new Date());
                        final String today_Year = new SimpleDateFormat("YYYY", Locale.getDefault()).format(new Date());
                        final String today_Date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                        final String unique_id = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss EEEE", Locale.getDefault()).format(new Date());
                        final DatabaseReference dr_1 = FirebaseDatabase.getInstance().getReference("transaction");
                        final String order_id = dr_1.push().getKey();
                        dr_1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Transaction t = new Transaction(today_Month,today_Year,order_id,unique_id,p_type,String.valueOf(df2.format(sub_total)),String.valueOf(df2.format(grand_total)),String.valueOf((int)discount),String.valueOf(discount_rate),String.valueOf(0),String.valueOf(0),today_Date,"");
                                String label = unique_id;
                                dr_1.child(session_name).child("Retail").child(label).setValue(t);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                        setSalesByYearMonth(today_Year,today_Month,grand_total);
                        reduceStock();
                        setBestSell();
                        setOrderListtoTransaction(unique_id);
                        clearOrderList();
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.i("paymentExample", paymentDetails);
                        //Starting a new activity for the payment details and also putting the payment details with intent
                        startActivity(new Intent(this, ConfirmationPaypal.class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", paypal_grand_total)
                                .putExtra("Payment Type", "PayPal")
                                .putExtra("Order ID", order_id)
                                .putExtra("Session ID", session_name)
                                .putExtra("Shop", shop_name)
                                .putExtra("UN",un)
                                .putExtra("Change", 0)
                                .putExtra("Cash", 0)
                                .putExtra("Discount", nd)
                                .putExtra("Sub Total", sub_total)
                                .putExtra("Discount rate", discount_rate)
                                .putExtra("Grand Total", grand_total)
                                .putExtra("Date Time", unique_id)
                        );
                        finish();
                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }
    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }
    private boolean clearOrderList() {
        final DatabaseReference dr_3 = FirebaseDatabase.getInstance().getReference("order").child(session_name).child("Retail");
        dr_3.removeValue();
        return true;
    }
    private void setOrderListtoTransaction(String unique_id) {
        final DatabaseReference dr_2 = FirebaseDatabase.getInstance().getReference("bridge_entity").child(session_name).child("Retail").child(unique_id);
        final DatabaseReference dr_3 = FirebaseDatabase.getInstance().getReference("order").child(session_name).child("Retail");
        final DatabaseReference dr_4 = FirebaseDatabase.getInstance().getReference("best_sell_item").child(session_name).child("Retail");
        dr_3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    for (DataSnapshot d: dataSnapshot.getChildren()){
                        final Order o = d.getValue(Order.class);
                        Order oo = new Order(o.getProduct_name(),o.getPrice(),o.getQuantity());
                        Order ooo= new Order(o.getProduct_name(),o.getQuantity());
                        dr_2.child(o.getProduct_name()).setValue(oo);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
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
            Intent myIntent = new Intent(retail_make_payment.this, retail_inventory.class);
            myIntent.putExtra("Session ID",session_name);
            myIntent.putExtra("Shop",shop_name);
            myIntent.putExtra("UN",un);
            startActivity(myIntent);
            finish();
        }  else if (id == R.id.nav_dashboard) {
            Intent myIntent = new Intent(retail_make_payment.this, retail_dashboard.class);
            myIntent.putExtra("Session ID",session_name);
            myIntent.putExtra("Shop",shop_name);
            myIntent.putExtra("UN",un);
            startActivity(myIntent);
            finish();
        } else if (id == R.id.nav_restaurant) {
            Intent myIntent = new Intent(retail_make_payment.this, restaurant_role.class);
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
