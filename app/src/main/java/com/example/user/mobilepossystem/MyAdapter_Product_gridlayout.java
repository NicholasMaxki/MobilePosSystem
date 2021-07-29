package com.example.user.mobilepossystem;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter_Product_gridlayout  extends RecyclerView.Adapter<MyAdapter_Product_gridlayout.MyViewHolder> {
    Context context;
    ArrayList<Product> products;

    public MyAdapter_Product_gridlayout(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.product_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.product_layout_tv.setText(products.get(position).product_name);
        holder.product_price_layout_tv.setText("RM "+products.get(position).price);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class MyViewHolder extends  RecyclerView.ViewHolder {
        TextView product_layout_tv, product_price_layout_tv;

        public MyViewHolder(View itemView) {
            super(itemView);
            product_layout_tv = (TextView) itemView.findViewById(R.id.product_layout_tv);
            product_price_layout_tv = (TextView) itemView.findViewById(R.id.product_price_layout_tv);
        }
    }
}
