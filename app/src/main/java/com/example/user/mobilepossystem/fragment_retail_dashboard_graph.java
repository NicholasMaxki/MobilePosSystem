package com.example.user.mobilepossystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class fragment_retail_dashboard_graph extends Fragment {
    private String session_name,shop_name;
    private  Spinner s;
    private View v;
    private  ArrayList<BarEntry> ms;
    private  ArrayList<BarEntry> bi ;
    private  ArrayList<BarEntry> ys ;
    private ArrayList sales = new ArrayList();
    private  ArrayList<BestSell> list = new ArrayList<BestSell>();
    private ArrayList<Sales> yearlist = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         v=inflater.inflate(R.layout.fragment_retail_dashboard_graph,container,false);
        Bundle bundle = this.getArguments();
        session_name= bundle.getString("Session ID");
        shop_name= bundle.getString("Shop");

        final BarChart chart = v.findViewById(R.id.barchart);
        s =v.findViewById(R.id.spinner2);
        addItemOnSpinner();
        s.setSelection(2);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("best_sell").child(session_name).child("Retail");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i=0;
                if (dataSnapshot.hasChildren()){
                    for (DataSnapshot d:dataSnapshot.getChildren()){
                        BestSell bs = d.getValue(BestSell.class);
                        list.add(bs);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        DatabaseReference db2 = FirebaseDatabase.getInstance().getReference("sales").child(session_name).child("Retail").child("Yearly");
        db2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    for (DataSnapshot d:dataSnapshot.getChildren()){
                        Sales ss = d.getValue(Sales.class);
                        yearlist.add(ss);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });


        for(int i=0;i<12;i++){
            String s = null;
            if(i==0){
                s="January"; }else  if(i==1){ s="February"; }else if(i==2){ s="March"; }else if(i==3){ s="April"; }else if(i==4){ s="May"; }else if(i==5){ s="June"; }else if(i==6){ s="July"; }else if(i==7){ s="August"; }else if(i==8){ s="September"; }else if(i==9){ s="October"; }else if(i==10){ s="November"; }else if(i==11){ s="December"; }

            String today_Year = new SimpleDateFormat("YYYY", Locale.getDefault()).format(new Date());
            DatabaseReference db1 = FirebaseDatabase.getInstance().getReference("sales").child(session_name).child("Retail").child("Monthly").child(today_Year);
            final String finalS = s;
            db1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    float total=0;
                    if (dataSnapshot.hasChildren()){
                        for (DataSnapshot d:dataSnapshot.getChildren()){
                            Sales ss = d.getValue(Sales.class);
                            if (finalS.equals(ss.month)){
                                total =Float.parseFloat(ss.getTotal());
                            }
                        }
                        sales.add(total);
                    }else{
                        sales.add(total);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
        }

        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(s.getSelectedItem().toString().equals("Monthly Sales")){

                    ms = new ArrayList<BarEntry>();

                    for(int i=0;i<sales.size();i++){
                        ms.add(new BarEntry(Float.parseFloat(sales.get(i).toString()),i));
                    }
                    ArrayList month = new ArrayList();
                    month.add("Jan");month.add("Feb");month.add("Mar");month.add("Apr");month.add("May");month.add("Jun");
                    month.add("July");month.add("Aug");month.add("Sep");month.add("Oct");month.add("Nov");month.add("Dec");

                    BarDataSet bardataset = new BarDataSet(ms, "Sales per Month");
                    chart.animateY(2500);
                    BarData data = new BarData(month, bardataset);
                    bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
                    bardataset.setValueTextSize(15);
                    chart.setData(data);
                    chart.setDescription("in RM");
                    chart.getXAxis().setLabelsToSkip(0);
                    chart.getXAxis().setTextSize(15);
                }
                else if(s.getSelectedItem().toString().equals("Best Item Sold")){

                       bi= new ArrayList<BarEntry>();

                    for (int i=0;i<list.size();i++){
                        bi.add(new BarEntry(Float.parseFloat(list.get(i).getQuantity()),i));
                    }
                    
                    ArrayList itemName = new ArrayList();
                    for (int i=0;i<list.size();i++){
                        itemName.add(list.get(i).getProduct_name());
                    }
                    int count = list.size();
                    BarDataSet bardataset = new BarDataSet(bi, "Sales per Item");
                    chart.animateY(2500);
                    BarData data = new BarData(itemName, bardataset);
                    bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
                    bardataset.setValueTextSize(20);
                    chart.setData(data);
                    chart.setDescription("by Quantity");
                    chart.getXAxis().setLabelsToSkip(0);
                    chart.getXAxis().setTextSize(7);
                }else if(s.getSelectedItem().toString().equals("Yearly Sales")){
                    ys= new ArrayList<BarEntry>();

                    for (int i=0;i<yearlist.size();i++){
                        ys.add(new BarEntry(Float.parseFloat(yearlist.get(i).getTotal()),i));
                    }

                    ArrayList itemName = new ArrayList();
                    for (int i=0;i<yearlist.size();i++){
                        itemName.add(yearlist.get(i).getYear());
                    }
                    BarDataSet bardataset = new BarDataSet(ys, "Sales per Year");
                    chart.animateY(2500);
                    BarData data = new BarData(itemName, bardataset);
                    bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
                    bardataset.setValueTextSize(20);
                    chart.setData(data);
                    chart.setDescription("in RM");
                    chart.getXAxis().setLabelsToSkip(0);
                    chart.getXAxis().setTextSize(10);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
        return v;
    }
    
    public void addItemOnSpinner(){
        List<String> list = new ArrayList<String>();
        list.add("Monthly Sales");
        list.add("Yearly Sales");
        list.add("Best Item Sold");
        ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_spinner_item, list);
        areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(areasAdapter);
    }

}