package com.example.user.mobilepossystem;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.example.user.mobilepossystem.retail_main_menu.df2;

public class fragment_restaurant_dashboard_analytic extends Fragment {
    private View v;
    private String session_name,shop_name;
    private TextView retail_overall_product,retail_overall_pp,retail_overall_card,retail_overall_cash,retail_overall_ic,retail_overall_oc,retail_overall_sales,retail_overall_today_sales,retail_overall_stock;
    private ArrayList<Item> plist= new ArrayList<Item>();
    private MyAdapter_MenuItem adapter_menuItem;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_restaurant_dashboard_analytic,container,false);
        Bundle bundle = this.getArguments();
        session_name= bundle.getString("Session ID");
        shop_name= bundle.getString("Shop");

        retail_overall_today_sales = v.findViewById(R.id.retail_overall_today_sales);
        getTodaySales();

        retail_overall_sales = v.findViewById(R.id.retal_overall_sales);
        getTotalSales();
        retail_overall_oc = v.findViewById(R.id.retail_overall_oc);
        getTotalOrder();

        retail_overall_ic = v.findViewById(R.id.retail_overall_ic);
        getItem();
        retail_overall_cash = v.findViewById(R.id.retail_overall_cash);
        getPaymentAmount("Cash Paid");
        retail_overall_card = v.findViewById(R.id.retail_overall_card);
        getPaymentAmount("Card Paid");
        retail_overall_pp = v.findViewById(R.id.retail_overall_pp);
        getPaymentAmount("PayPal");
        retail_overall_product = v.findViewById(R.id.retail_overall_product);
        getBestSell();
        return v;
    }

    private void getPaymentAmount(final String s) {
        DatabaseReference dt = FirebaseDatabase.getInstance().getReference("transaction").child(session_name).child("Restaurant");
        Query q =dt.orderByChild("payment_type").startAt(s).endAt(s+"\uf8ff");
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double total=0;
                if (dataSnapshot.hasChildren()){
                    for (DataSnapshot d:dataSnapshot.getChildren()){
                        Transaction t = d.getValue(Transaction.class);
                        total +=Double.parseDouble(t.getTotal_amount());
                    }
                    if(s.equals("Cash Paid")){
                        retail_overall_cash.setText("Cash : RM "+df2.format(total));
                    }else  if(s.equals("Card Paid")){
                        retail_overall_card.setText("Card : RM "+df2.format(total));
                    }else if(s.equals("PayPal")){
                        retail_overall_pp.setText("PayPal : RM "+df2.format(total));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });
    }

    private void getItem() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("best_sell").child(session_name).child("Restaurant");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int qty=0;
                if (dataSnapshot.hasChildren()){
                    for (DataSnapshot d:dataSnapshot.getChildren()){
                        BestSell b = d.getValue(BestSell.class);
                        qty += Integer.parseInt(b.getQuantity());
                    }
                    retail_overall_ic.setText("Item Count : "+qty);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
    private void getTotalOrder() {
        DatabaseReference dto = FirebaseDatabase.getInstance().getReference("transaction").child(session_name).child("Restaurant");
        dto.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    retail_overall_oc.setText("Orders Count : "+dataSnapshot.getChildrenCount());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
    private void getTodaySales() {
        final String today_Date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        DatabaseReference dt = FirebaseDatabase.getInstance().getReference("transaction").child(session_name).child("Restaurant");
        Query q =dt.orderByChild("date").startAt(today_Date).endAt(today_Date+"\uf8ff");
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double total=0;
                if (dataSnapshot.hasChildren()){
                    for (DataSnapshot d:dataSnapshot.getChildren()){
                        Transaction t = d.getValue(Transaction.class);
                        total +=Double.parseDouble(t.getTotal_amount());
                    }
                    retail_overall_today_sales.setText("Daily Sales : RM "+df2.format(total));
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
    private void getTotalSales() {
        DatabaseReference dt = FirebaseDatabase.getInstance().getReference("transaction").child(session_name).child("Restaurant");
        dt.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double total=0;
                if (dataSnapshot.hasChildren()){
                    for (DataSnapshot d:dataSnapshot.getChildren()){
                        Transaction t = d.getValue(Transaction.class);
                        total +=Double.parseDouble(t.getTotal_amount());
                    }
                    retail_overall_sales.setText("Total Sales : RM "+df2.format(total));
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
    private void getBestSell() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("best_sell").child(session_name).child("Restaurant");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int qty=0;
                String pn = null;
                if (dataSnapshot.hasChildren()){
                    for (DataSnapshot d:dataSnapshot.getChildren()){
                        BestSell b = d.getValue(BestSell.class);
                        if(Integer.parseInt(b.getQuantity())>qty){
                            qty =Integer.parseInt(b.getQuantity());
                            pn=b.getProduct_name();
                        }
                    }
                    retail_overall_product.setText(pn+" ("+qty+")");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
}
