package com.example.user.mobilepossystem;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class fragment_retail_dashboard_transaction extends Fragment {
    private String session_name,shop_name;
    private View v;
    private Spinner s;
    private RecyclerView rcv;
    private ArrayList<Transaction> list;
    private MyAdapter_Transaction adapter_transaction;
    private EditText search_bar;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_retail_dashboard_transaction,container,false);
        Bundle bundle = this.getArguments();
        session_name= bundle.getString("Session ID");
        shop_name= bundle.getString("Shop");
        s = v.findViewById(R.id.spinner_transaction);
        search_bar = v.findViewById(R.id.retail_transaction_search);
        //search function
        search_bar.addTextChangedListener(new TextWatcher() {
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
        addItemOnSpinner();
        rcv =  v.findViewById(R.id.rcv_transaction);
        rcv.setLayoutManager(new LinearLayoutManager(v.getContext()));
        list = new ArrayList<Transaction>();
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(s.getSelectedItem().toString().equals("Show All")){
                    list.clear();
                    runShowAll();
                }else{
                    list.clear();
                    rcv.setAdapter(null);
                    runGroupByMonth(s.getSelectedItem().toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
        return v;
    }
    private void search(String s) {
        rcv.setAdapter(null);
        Query q = FirebaseDatabase.getInstance().getReference("transaction").child(session_name).child("Retail").orderByChild("order_id").startAt(s).endAt(s+"\uf8ff");
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                if(dataSnapshot.hasChildren()){
                    for (DataSnapshot d: dataSnapshot.getChildren()){
                        final Transaction t = d.getValue(Transaction.class);
                        list.add(t);
                    }
                    adapter_transaction = new MyAdapter_Transaction(v.getContext(),list){
                        @Override
                        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
                            holder.dashboard_transaction_order_id.setText("Order ID: "+transactions.get(position).order_id);
                             DecimalFormat df2 = new DecimalFormat("0.00");
                            holder.dashboard_transaction_amount.setText("RM "+df2.format( Double.parseDouble(transactions.get(position).total_amount)));
                            holder.dashboard_transaction_date.setText(transactions.get(position).date_time);
                            holder.dashboard_transaction_order_id.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String type = transactions.get(position).payment_type;
                                    String order_id = transactions.get(position).order_id;
                                    String  dt =  transactions.get(position).date_time;
                                    int d = Integer.parseInt(transactions.get(position).discount);
                                    double ch = Double.parseDouble( transactions.get(position).change);
                                    double ca=Double.parseDouble( transactions.get(position).cash);
                                    double dr=Double.parseDouble( transactions.get(position).discount_rate);
                                    double st=Double.parseDouble( transactions.get(position).sub_total);
                                    double total=Double.parseDouble( transactions.get(position).total_amount);
                                    String pid = transactions.get(position).pid;
                                    viewTransaction(type,order_id,ch,ca,d,st,dr,total,dt,pid);
                                }
                            });
                        }
                    };
                    rcv.setAdapter(adapter_transaction);
                    adapter_transaction.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(v.getContext(),"Database invalid.",Toast.LENGTH_LONG).show();
            }
        });
    }
    private void runGroupByMonth(String s) {
        Query q = FirebaseDatabase.getInstance().getReference("transaction").child(session_name).child("Retail").orderByChild("month").startAt(s).endAt(s+"\uf8ff");
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                if(dataSnapshot.hasChildren()){
                    for (DataSnapshot d: dataSnapshot.getChildren()){
                        final Transaction t = d.getValue(Transaction.class);
                        list.add(t);
                    }
                    Collections.reverse(list);
                    adapter_transaction = new MyAdapter_Transaction(v.getContext(),list){
                        @Override
                        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
                            holder.dashboard_transaction_order_id.setText("Order ID: "+transactions.get(position).order_id);
                            holder.dashboard_transaction_amount.setText("RM "+transactions.get(position).total_amount);
                            holder.dashboard_transaction_date.setText(transactions.get(position).date_time);
                            holder.dashboard_transaction_order_id.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String type = transactions.get(position).payment_type;
                                    String order_id = transactions.get(position).order_id;
                                    String  dt =  transactions.get(position).date_time;
                                    int d = Integer.parseInt(transactions.get(position).discount);
                                    double ch = Double.parseDouble( transactions.get(position).change);
                                    double ca=Double.parseDouble( transactions.get(position).cash);
                                    double dr=Double.parseDouble( transactions.get(position).discount_rate);
                                    double st=Double.parseDouble( transactions.get(position).sub_total);
                                    double total=Double.parseDouble( transactions.get(position).total_amount);
                                    String pid = transactions.get(position).pid;
                                    viewTransaction(type,order_id,ch,ca,d,st,dr,total,dt,pid);
                                }
                            });
                        }
                    };
                    rcv.setAdapter(adapter_transaction);
                    adapter_transaction.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(v.getContext(),"Database invalid.",Toast.LENGTH_LONG).show();
            }
        });
    }
    private void runShowAll() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("transaction").child(session_name).child("Retail");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                if(dataSnapshot.hasChildren()){
                    for (DataSnapshot d: dataSnapshot.getChildren()){
                        final Transaction t = d.getValue(Transaction.class);
                        list.add(t);
                    }
                    Collections.reverse(list);
                    adapter_transaction = new MyAdapter_Transaction(v.getContext(),list){
                        @Override
                        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
                            holder.dashboard_transaction_order_id.setText("Order ID: "+transactions.get(position).order_id);
                            holder.dashboard_transaction_amount.setText("RM "+transactions.get(position).total_amount);
                            holder.dashboard_transaction_date.setText(transactions.get(position).date_time);
                            holder.dashboard_transaction_order_id.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String type = transactions.get(position).payment_type;
                                    String order_id = transactions.get(position).order_id;
                                    String  dt =  transactions.get(position).date_time;
                                    int d = Integer.parseInt(transactions.get(position).discount);
                                    double ch = Double.parseDouble( transactions.get(position).change);
                                    double ca=Double.parseDouble( transactions.get(position).cash);
                                    double dr=Double.parseDouble( transactions.get(position).discount_rate);
                                    double st=Double.parseDouble( transactions.get(position).sub_total);
                                    double total=Double.parseDouble( transactions.get(position).total_amount);
                                    String pid = transactions.get(position).pid;
                                    viewTransaction(type,order_id,ch,ca,d,st,dr,total,dt,pid);
                                }
                            });
                        }
                    };
                    rcv.setAdapter(adapter_transaction);
                    adapter_transaction.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(v.getContext(),"Database invalid.",Toast.LENGTH_LONG).show();
            }
        });
    }
    private void viewTransaction(String type, String oi, double t_ch, double t_ca, int d, double st, double dr, double total, String dt,String pid) {
        Intent myIntent = new Intent(v.getContext(), retail_payment_receipt.class);
        myIntent.putExtra("Session ID",session_name);
        myIntent.putExtra("Shop",shop_name);
        myIntent.putExtra("Payment Type",type);
        myIntent.putExtra("Order ID",oi);
        myIntent.putExtra("Change",t_ch);
        myIntent.putExtra("Cash",t_ca);
        myIntent.putExtra("Discount",d);
        myIntent.putExtra("Sub Total",st);
        myIntent.putExtra("Discount rate",dr);
        myIntent.putExtra("Grand Total",total);
        myIntent.putExtra("Date Time",dt);
        myIntent.putExtra("PID",pid);
        myIntent.putExtra("ft",true);
        startActivity(myIntent);
    }
    public void addItemOnSpinner(){
        List<String> list = new ArrayList<String>();
        list.add("Show All");
        list.add("January");
        list.add("February");
        list.add("March");
        list.add("April");
        list.add("May");
        list.add("June");
        list.add("July");
        list.add("August");
        list.add("September");
        list.add("October");
        list.add("November");
        list.add("December");
        ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_spinner_item, list);
        areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(areasAdapter);
    }
}
