package com.example.user.mobilepossystem;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter_MenuList  extends RecyclerView.Adapter<MyAdapter_MenuList.MyViewHolder> {
    Context context;
    ArrayList<Item> items;

    public MyAdapter_MenuList(Context context, ArrayList<Item> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.cardview_menu_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.item_name.setText(items.get(position).product_name);
        holder.item_price.setText("RM "+items.get(position).price);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MyViewHolder extends  RecyclerView.ViewHolder {
        TextView item_name, item_price;

        public MyViewHolder(View itemView) {
            super(itemView);
            item_name = (TextView) itemView.findViewById(R.id.item_name);
            item_price = (TextView) itemView.findViewById(R.id.item_price);
        }
    }
}
