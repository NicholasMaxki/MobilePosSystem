package com.example.user.mobilepossystem;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter_Table extends RecyclerView.Adapter<MyAdapter_Table.MyViewHolder> {
    Context context;
    ArrayList<Table> cry;

    public MyAdapter_Table(Context context, ArrayList<Table> cry) {
        this.context = context;
        this.cry = cry;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.cardview_category,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.recycle_product_category_category.setText(cry.get(position).table);
    }

    @Override
    public int getItemCount() {
        return cry.size();
    }

    class MyViewHolder extends  RecyclerView.ViewHolder {
        TextView recycle_product_category_category;

        public MyViewHolder(View itemView) {
            super(itemView);
            recycle_product_category_category = (TextView) itemView.findViewById(R.id.recycle_product_category_category);
        }
    }
    public void clear() {
        int size = cry.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                cry.remove(0);
            }
            notifyItemRangeRemoved(0, size);
        }
    }
}
