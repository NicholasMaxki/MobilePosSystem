package com.example.user.mobilepossystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class restaurant_kitchen extends AppCompatActivity{
    private String session_name,shop_name,un;
    private RecyclerView rcv_kitchen;
    private MyAdapter_Kitchen myAdapter_kitchen;
    private  MyAdapter_FoodList myAdapter_foodList;
    private  ArrayList<Kitchen> list=new ArrayList<Kitchen>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_kitchen);
        Intent i = getIntent();
        session_name = i.getExtras().getString("Session ID");
        shop_name = i.getExtras().getString("Shop");
        un = i.getExtras().getString("UN");

        rcv_kitchen = (RecyclerView) findViewById(R.id.rcv_kitchen);
        rcv_kitchen.setHasFixedSize(true);
        GridLayoutManager glm = new GridLayoutManager(this,2);
        rcv_kitchen.setLayoutManager(glm);

        list = new ArrayList<Kitchen>();
        //get reference from firebase
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("bridge_entity_2").child(session_name).child("Restaurant");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    list.clear();
                    for (DataSnapshot d: dataSnapshot.getChildren()){
                        final Kitchen k = d.getValue(Kitchen.class);
                        list.add(k);
                    }
                    myAdapter_kitchen = new MyAdapter_Kitchen(restaurant_kitchen.this,list){
                        @Override
                        public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
                            final ArrayList<Item> ilist = new ArrayList<Item>();
                            holder.food_table.setText("Table : "+kitchen.get(position).table);
                            holder.food_time.setText(kitchen.get(position).date);
                            holder.extra_note.setText(kitchen.get(position).extra_note);
                            holder.button_done.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String uid = kitchen.get(position).uid;
                                    dropBridge(uid);
                                    dropKitchen(uid);
                                    ringbell();
                                    list.clear();
                                    myAdapter_kitchen.notifyDataSetChanged();
                                    overridePendingTransition(0, 0);
                                }
                            });
                            holder.rcv_food.setLayoutManager(new LinearLayoutManager(restaurant_kitchen.this));
                            RecyclerView.OnItemTouchListener mScrollTouchListener = new RecyclerView.OnItemTouchListener() {
                                @Override
                                public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                                    int action = e.getAction();
                                    switch (action) {
                                        case MotionEvent.ACTION_MOVE:
                                            rv.getParent().requestDisallowInterceptTouchEvent(true);
                                            break;
                                    }
                                    return false;
                                }
                                @Override
                                public void onTouchEvent(RecyclerView rv, MotionEvent e) { }

                                @Override
                                public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                                }
                            };
                            holder.rcv_food.addOnItemTouchListener(mScrollTouchListener);
                            DatabaseReference db2 = FirebaseDatabase.getInstance().getReference("kitchen_view").child(session_name).child("Restaurant").child(kitchen.get(position).uid);
                            db2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChildren()){
                                        ilist.clear();
                                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                                            Item i = ds.getValue(Item.class);
                                            ilist.add(i);
                                        }
                                        myAdapter_foodList = new MyAdapter_FoodList(restaurant_kitchen.this,ilist){
                                            @Override
                                            public void onBindViewHolder(@NonNull final MyAdapter_FoodList.MyViewHolder holder, final int position) {
                                                holder.food_name.setText(item.get(position).product_name);
                                                holder.food_qty.setText("x "+item.get(position).quantity);
                                            }
                                        };
                                        holder.rcv_food.setAdapter(myAdapter_foodList);
                                        myAdapter_foodList.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {}
                            });
                        }
                    };
                    rcv_kitchen.setAdapter(myAdapter_kitchen);
                    myAdapter_kitchen.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ringbell() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("ring").child(session_name);
        String a=db.push().getKey();
        db.child(a).child("check").setValue(a);
    }

    private void dropKitchen(String uid) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("kitchen_view").child(session_name).child("Restaurant").child(uid);
        db.removeValue();
    }

    private void dropBridge(String uid) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("bridge_entity_2").child(session_name).child("Restaurant").child(uid);
        db.removeValue();
    }

}
