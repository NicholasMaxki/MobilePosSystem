package com.example.user.mobilepossystem;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter_FoodList  extends RecyclerView.Adapter<MyAdapter_FoodList.MyViewHolder> {
    Context context;
    ArrayList<Item> item;

    public MyAdapter_FoodList(Context context, ArrayList<Item> item) {
        this.context = context;
        this.item = item;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.cardview_kitchen_food,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    class MyViewHolder extends  RecyclerView.ViewHolder {
        TextView food_name,food_qty;

        public MyViewHolder(View itemView) {
            super(itemView);
            food_name = (TextView) itemView.findViewById(R.id.food_name);
            food_qty = (TextView) itemView.findViewById(R.id.food_qty);


        }
    }
}
