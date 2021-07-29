package com.example.user.mobilepossystem;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.util.Patterns;
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
import com.itextpdf.text.Chunk;
import com.itextpdf.text.pdf.PdfWriter;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class retail_payment_receipt extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{
    private String session_name,shop_name,un,date_time,order_id,p_type;
    private double discount_rate,change,sub_total,grand_total,cash;
    private int discount;
    private TextView date_time_invoice_retail,oid,sub_total_invoice_retail,discount_invoice_retail,grand_total_invoice_retail,cash_invoice_retail,change_invoice_retail,cash_payment_method,change_pid,payment_receipt_shop_name;
    private Button btn_email_retail,btn_new_sale_retail;
    private static DecimalFormat df2 = new DecimalFormat("0.00");
    private RecyclerView rcv_invoice_retail;
    private DatabaseReference db;
    private ArrayList<Order> list;
    private ArrayList<User> list3;
    private MyAdapter_OrderList adapter_orderList;
    private String recipient_email,PID;
    private boolean from_transaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retail_payment_receipt);
        Intent i = getIntent();
        session_name = i.getExtras().getString("Session ID");
        shop_name = i.getExtras().getString("Shop");
        un = i.getExtras().getString("UN");
        date_time = i.getExtras().getString("Date Time");
        order_id = i.getExtras().getString("Order ID");
        change = i.getExtras().getDouble("Change");
        sub_total = i.getExtras().getDouble("Sub Total");
        grand_total = i.getExtras().getDouble("Grand Total");
        discount = i.getExtras().getInt("Discount");
        discount_rate = i.getExtras().getDouble("Discount rate");
        cash = i.getExtras().getDouble("Cash");
        p_type = i.getExtras().getString("Payment Type");
        PID = i.getExtras().getString("PID");
        from_transaction=i.getExtras().getBoolean("ft",false);

        date_time_invoice_retail = (TextView)findViewById(R.id.date_time_invoice_retail);
        date_time_invoice_retail.setText(date_time);

        oid = (TextView)findViewById(R.id.order_id);
        oid.setText("Order ID : "+order_id);

        payment_receipt_shop_name = (TextView)findViewById(R.id.payment_receipt_shop_name);
        payment_receipt_shop_name.setText(shop_name);

        sub_total_invoice_retail = (TextView)findViewById(R.id.sub_total_invoice_retail);
        sub_total_invoice_retail.setText("RM "+df2.format(sub_total));

        grand_total_invoice_retail = (TextView)findViewById(R.id.grand_total_invoice_retail);
        grand_total_invoice_retail.setText("RM "+df2.format(grand_total));

        discount_invoice_retail = (TextView)findViewById(R.id.discount_invoice_retail);
        discount_invoice_retail.setText(discount+"% (-RM "+df2.format(discount_rate)+")");


        cash_invoice_retail = (TextView)findViewById(R.id.cash_invoice_retail);
        change_invoice_retail = (TextView)findViewById(R.id.change_invoice_retail);
        cash_payment_method = (TextView)findViewById(R.id.cash_payment_method);
        change_pid=(TextView)findViewById(R.id.change_pid);

        if(p_type.equals("Cash Paid")){
            cash_invoice_retail.setText("RM "+df2.format(cash));
            change_invoice_retail.setText("RM "+df2.format(change));
        }else if(p_type.equals("Card Paid")){
            cash_payment_method.setText("Payment Method :");
            cash_invoice_retail.setText("Card");
            change_invoice_retail.setVisibility(View.INVISIBLE);
            change_pid.setVisibility(View.INVISIBLE);
        }else{
            cash_payment_method.setText("Payment Method :");
            cash_invoice_retail.setText("PayPal");
            change_invoice_retail.setText(PID);
            change_pid.setText("Payment ID :");

            DatabaseReference db = FirebaseDatabase.getInstance().getReference("transaction").child(session_name).child("Retail").child(date_time).child("pid");
            db.setValue(PID);

        }

        btn_new_sale_retail = (Button)findViewById(R.id.btn_new_sale_retail);
        if(from_transaction){
            btn_new_sale_retail.setVisibility(View.INVISIBLE);
        }else{
            btn_new_sale_retail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(retail_payment_receipt.this, retail_main_menu.class);
                    myIntent.putExtra("Session ID",session_name);
                    myIntent.putExtra("Shop",shop_name);
                    myIntent.putExtra("UN",un);
                    startActivity(myIntent);
                    finish();
                }
            });
        }
        //recycle view
        rcv_invoice_retail = (RecyclerView) findViewById(R.id.rcv_invoice_retail);
        rcv_invoice_retail.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<Order>();
        db = FirebaseDatabase.getInstance().getReference("bridge_entity").child(session_name).child("Retail").child(date_time);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    list.clear();
                    for (DataSnapshot d: dataSnapshot.getChildren()){
                        final Order o = d.getValue(Order.class);
                        list.add(o);
                    }
                    adapter_orderList = new MyAdapter_OrderList(retail_payment_receipt.this,list){
                        @Override
                        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
                            holder.order_product_name_retail.setText(orders.get(position).product_name);
                            holder.order_price_retail.setText("RM "+(df2.format(Double.parseDouble(orders.get(position).quantity)*Double.parseDouble(orders.get(position).price))));
                            holder.order_quantity_retail.setText("X "+orders.get(position).quantity);
                            holder.order_ori_price_retail.setText("(RM "+orders.get(position).price+")");
                        }
                    };
                    rcv_invoice_retail.setAdapter(adapter_orderList);
                    adapter_orderList.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        btn_email_retail = (Button)findViewById(R.id.btn_email_retail);
        btn_email_retail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check and request permission
                if (ContextCompat.checkSelfPermission(retail_payment_receipt.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(retail_payment_receipt.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }
                if (ContextCompat.checkSelfPermission(retail_payment_receipt.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(retail_payment_receipt.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }

                if (ContextCompat.checkSelfPermission(retail_payment_receipt.this, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS) == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(retail_payment_receipt.this, new String[] {Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, 1);
                }
                getRecipient();
            }
        });

        //nav code
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
                Intent myIntent = new Intent(retail_payment_receipt.this, profile.class);
                myIntent.putExtra("Session ID",session_name);
                myIntent.putExtra("Shop",shop_name);
                myIntent.putExtra("UN",un);
                myIntent.putExtra("Mode","Retail");
                startActivity(myIntent);
                finish();
            }
        });
    }
    private void getRecipient() {
        LayoutInflater li = LayoutInflater.from(retail_payment_receipt.this);
        View promptsView = li.inflate(R.layout.promt_email, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(retail_payment_receipt.this);
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.email_input);
        final Button done = promptsView.findViewById(R.id.btn_ok);
        alertDialogBuilder.setCancelable(true);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!userInput.getText().toString().equals("") && Patterns.EMAIL_ADDRESS.matcher(userInput.getText().toString()).matches()){
                    recipient_email =  userInput.getText().toString();
                    makePDF();
                    alertDialog.dismiss();
                }else{
                    Toast.makeText(getBaseContext(), "Please insert valid email address", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    getRecipient();
                }
            }
        });
    }
    private void makePDF() {
        final StringBuilder sb = new StringBuilder();final StringBuilder sb2 = new StringBuilder();


        sb.append(shop_name+"\n");
        sb.append(date_time+"\n");
        String s = String.format("%-5s %-95.55s %-25s %-20s\n","No.","Item", "Qty", "Price");
        sb2.append("Order ID : "+order_id);
        sb2.append("\n----------------------------------------------------------------------------------------------------------------------------------\n");
        sb2.append(s);
        sb2.append("----------------------------------------------------------------------------------------------------------------------------------\n");
        for(int i=0;i<list.size();i++){
            String pn = list.get(i).getProduct_name();
            String s2 = String.format("%-5s %-55.15s \n%110s %25s\n", (i + 1) +".",pn, list.get(i).getQuantity(), "RM "+list.get(i).getPrice());
            sb2.append(s2);
        }
        sb2.append("----------------------------------------------------------------------------------------------------------------------------------\n");
        Document doc = new Document();
        try {
            Chunk cc = new Chunk(sb.toString());
            Paragraph para1 = new Paragraph(cc);
            Chunk c2 = new Chunk(sb2.toString());
            Paragraph para2 = new Paragraph(c2);

            para1.setAlignment(Paragraph.ALIGN_CENTER);
            para1.setSpacingAfter(10);
            para2.setAlignment(Paragraph.ALIGN_LEFT);
            para2.setSpacingAfter(0);
            String mpath = "/sdcard/Download/retail_receipt.pdf";
            PdfWriter.getInstance(doc, new FileOutputStream(mpath));
            doc.open();
            doc.addAuthor("Company Name");
            doc.add(para1);
            doc.add(para2);


            if(discount_rate !=0){
                String s3 = "Sub Total :";
                c2 = new Chunk(s3);
                Paragraph para3 = new Paragraph(c2);
                para3.setAlignment(Paragraph.ALIGN_LEFT);
                doc.add(para3);
                s3 = "RM "+df2.format(sub_total)+"\n";
                c2 = new Chunk(s3);
                para3 = new Paragraph(c2);
                para3.setAlignment(Paragraph.ALIGN_RIGHT);
                doc.add(para3);

                s3 = "Discount  :";
                c2 = new Chunk(s3);
                para3 = new Paragraph(c2);
                para3.setAlignment(Paragraph.ALIGN_LEFT);
                doc.add(para3);
                s3 = discount+"% (RM "+df2.format(discount_rate)+")\n";
                c2 = new Chunk(s3);
                para3 = new Paragraph(c2);
                para3.setAlignment(Paragraph.ALIGN_RIGHT);
                doc.add(para3);
            }
            String s4 = "Grand Total  :";
            c2 = new Chunk(s4);
            Paragraph para4 = new Paragraph(c2);
            para4.setAlignment(Paragraph.ALIGN_LEFT);
            doc.add(para4);
            s4 = "RM "+df2.format(grand_total)+"\n";
            c2 = new Chunk(s4);
            para4 = new Paragraph(c2);
            para4.setAlignment(Paragraph.ALIGN_RIGHT);
            doc.add(para4);

            String sss = "Payment Type :";
            c2 = new Chunk(sss);
            Paragraph para3 = new Paragraph(c2);
            para3.setAlignment(Paragraph.ALIGN_LEFT);
            doc.add(para3);
            c2 = new Chunk(p_type);
            para3 = new Paragraph(c2);
            para3.setAlignment(Paragraph.ALIGN_RIGHT);
            doc.add(para3);

            if(p_type.equals("PayPal")){
                c2 = new Chunk("Payment ID :");
                para3 = new Paragraph(c2);
                para3.setAlignment(Paragraph.ALIGN_LEFT);
                doc.add(para3);
                c2 = new Chunk(PID);
                para3 = new Paragraph(c2);
                para3.setAlignment(Paragraph.ALIGN_RIGHT);
                doc.add(para3);
            }

            String s5 = "\n\nTHANKS YOU FOR YOUR BUSINESS ! \n PLEASE VISIT AGAIN !";
            para4 = new Paragraph(s5);
            para4.setAlignment(Paragraph.ALIGN_CENTER);
            para4.setSpacingAfter(10);
            doc.add(para4);
            doc.close();
            sendEmail();
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
    protected void sendEmail(){
        Log.i("Send email", "");
        String[] TO = new String[]{recipient_email} ;
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL,TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Receipt from "+shop_name);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Your order (order id :"+order_id+") from "+date_time+" at the retail shop. Here is the receipt. Please visit next time. Thanks you.");
        String filename="retail_receipt.pdf";
        File filelocation = new File("/sdcard/Download/", filename);
        Uri p = FileProvider.getUriForFile(retail_payment_receipt.this, retail_payment_receipt.this.getApplicationContext().getPackageName() + ".provider", filelocation);
        emailIntent .putExtra(Intent.EXTRA_STREAM,  p);
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            Log.i("Finish sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(retail_payment_receipt.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
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

        if (id == R.id.nav_checkout) {
            Intent myIntent = new Intent(retail_payment_receipt.this, retail_main_menu.class);
            myIntent.putExtra("Session ID",session_name);
            myIntent.putExtra("Shop",shop_name);
            myIntent.putExtra("UN",un);
            startActivity(myIntent);
            finish();
        } else if (id == R.id.nav_inventory) {
            Intent myIntent = new Intent(retail_payment_receipt.this, retail_inventory.class);
            myIntent.putExtra("Session ID",session_name);
            myIntent.putExtra("Shop",shop_name);
            myIntent.putExtra("UN",un);
            startActivity(myIntent);
            finish();
        }  else if (id == R.id.nav_dashboard) {
            Intent myIntent = new Intent(retail_payment_receipt.this, retail_dashboard.class);
            myIntent.putExtra("Session ID",session_name);
            myIntent.putExtra("Shop",shop_name);
            myIntent.putExtra("UN",un);
            startActivity(myIntent);
            finish();
        } else if (id == R.id.nav_restaurant) {
            Intent myIntent = new Intent(retail_payment_receipt.this, restaurant_role.class);
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
