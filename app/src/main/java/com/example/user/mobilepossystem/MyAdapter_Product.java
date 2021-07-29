package com.example.user.mobilepossystem;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter_Product  extends RecyclerView.Adapter<MyAdapter_Product.MyViewHolder> {
    Context context;
    ArrayList<Product> products;

    public MyAdapter_Product(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.cardview,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.recycle_product_name.setText(products.get(position).product_name);
        holder.recycle_price.setText("RM "+products.get(position).price);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class MyViewHolder extends  RecyclerView.ViewHolder {
        TextView recycle_product_name, recycle_price;

        public MyViewHolder(View itemView) {
            super(itemView);
            recycle_product_name = (TextView) itemView.findViewById(R.id.recycle_product_name);
            recycle_price = (TextView) itemView.findViewById(R.id.recycle_price);
        }
    }
}
