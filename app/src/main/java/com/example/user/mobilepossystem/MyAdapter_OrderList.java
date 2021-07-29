package com.example.user.mobilepossystem;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter_OrderList extends RecyclerView.Adapter<MyAdapter_OrderList.MyViewHolder> {
    Context context;
    ArrayList<Order> orders;

    public MyAdapter_OrderList(Context context, ArrayList<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.cardview_order_list_retail,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.order_product_name_retail.setText(orders.get(position).product_name);
        holder.order_price_retail.setText("RM "+orders.get(position).price);
        holder.order_quantity_retail.setText(orders.get(position).quantity);
        holder.order_ori_price_retail.setText(orders.get(position).price);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class MyViewHolder extends  RecyclerView.ViewHolder {
        TextView order_quantity_retail,order_product_name_retail,order_price_retail,order_ori_price_retail;

        public MyViewHolder(View itemView) {
            super(itemView);
            order_quantity_retail = (TextView) itemView.findViewById(R.id.order_quantity_retail);
            order_product_name_retail = (TextView) itemView.findViewById(R.id.order_product_name_retail);
            order_price_retail = (TextView) itemView.findViewById(R.id.order_price_retail);
            order_ori_price_retail = (TextView) itemView.findViewById(R.id.order_ori_price_retail);
        }
    }
}
