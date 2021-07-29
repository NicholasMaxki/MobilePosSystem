package com.example.user.mobilepossystem;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter_Table_gridlayout  extends RecyclerView.Adapter<MyAdapter_Table_gridlayout.MyViewHolder> {
    Context context;
    ArrayList<Table> tables;

    public MyAdapter_Table_gridlayout(Context context, ArrayList<Table> tables) {
        this.context = context;
        this.tables = tables;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.table_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.table_layout_tv.setText(tables.get(position).table);
        holder.table_pax_tv.setText(tables.get(position).pax);
    }

    @Override
    public int getItemCount() {
        return tables.size();
    }

    class MyViewHolder extends  RecyclerView.ViewHolder {
        TextView table_layout_tv,table_pax_tv;

        public MyViewHolder(View itemView) {
            super(itemView);
            table_layout_tv = (TextView) itemView.findViewById(R.id.table_layout_tv);
            table_pax_tv = (TextView) itemView.findViewById(R.id.table_pax_tv);

        }
    }
}
