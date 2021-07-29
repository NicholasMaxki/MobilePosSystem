package com.example.user.mobilepossystem;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter_Transaction extends RecyclerView.Adapter<MyAdapter_Transaction.MyViewHolder> {
    Context context;
    ArrayList<Transaction> transactions;

    public MyAdapter_Transaction(Context context, ArrayList<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.cardview_transaction,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.dashboard_transaction_order_id.setText(transactions.get(position).order_id);
        holder.dashboard_transaction_amount.setText("RM "+transactions.get(position).total_amount);
        holder.dashboard_transaction_date.setText(transactions.get(position).date_time);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    class MyViewHolder extends  RecyclerView.ViewHolder {
        TextView dashboard_transaction_order_id,dashboard_transaction_amount,dashboard_transaction_date;

        public MyViewHolder(View itemView) {
            super(itemView);
            dashboard_transaction_order_id = (TextView) itemView.findViewById(R.id.dashboard_transaction_order_id);
            dashboard_transaction_amount = (TextView) itemView.findViewById(R.id.dashboard_transaction_amount);
            dashboard_transaction_date = (TextView) itemView.findViewById(R.id.dashboard_transaction_date);

        }
    }
}
