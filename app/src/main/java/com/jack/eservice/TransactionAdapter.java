package com.jack.eservice;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jason on 2017/3/17.
 */

public class TransactionAdapter extends  RecyclerView.Adapter<TransactionAdapter.ViewHolder>{
    private List<Transaction> trans;
    public TransactionAdapter(List<Transaction> trans){
        this.trans = trans;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.serial3,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("trans",position+"");
        Transaction tran = trans.get(position);
        Log.d("trans",tran.getDate());
        Log.d("trans",tran.getAccount());
        holder.dateTextView.setText(tran.getDate());
        holder.amountTextView.setText(tran.getAmount()+"");
        holder.typeTextView.setText(tran.getType()+"");

    }

    @Override
    public int getItemCount() {
        return trans.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView dateTextView;
        private final TextView amountTextView;
        private final TextView typeTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            dateTextView = (TextView)itemView.findViewById(R.id.col_date);
            amountTextView = (TextView)itemView.findViewById(R.id.col_amount);
            typeTextView = (TextView)itemView.findViewById(R.id.col_type);
        }
    }
}
