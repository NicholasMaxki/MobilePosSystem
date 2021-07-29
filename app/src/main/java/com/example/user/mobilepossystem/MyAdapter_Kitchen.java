package com.example.user.mobilepossystem;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter_Kitchen  extends RecyclerView.Adapter<MyAdapter_Kitchen.MyViewHolder> {
    Context context;
    ArrayList<Kitchen> kitchen;

    public MyAdapter_Kitchen(Context context, ArrayList<Kitchen> kitchen) {
        this.context = context;
        this.kitchen = kitchen;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.cardview_kitchen,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return kitchen.size();
    }

    class MyViewHolder extends  RecyclerView.ViewHolder {
        TextView food_table,food_time,extra_note;
        Button button_done;
        RecyclerView rcv_food;

        public MyViewHolder(View itemView) {
            super(itemView);
            food_table = (TextView) itemView.findViewById(R.id.food_table);
            food_time = (TextView) itemView.findViewById(R.id.food_time);
            extra_note = (TextView) itemView.findViewById(R.id.extra_note);
            rcv_food = (RecyclerView) itemView.findViewById(R.id.rcv_food);
            button_done = (Button) itemView.findViewById(R.id.button_done);

        }
    }
}
