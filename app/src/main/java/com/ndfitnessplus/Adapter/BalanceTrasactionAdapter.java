package com.ndfitnessplus.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ndfitnessplus.Model.BalanceTrasactionList;
import com.ndfitnessplus.R;

import java.util.ArrayList;

public class BalanceTrasactionAdapter extends RecyclerView.Adapter<BalanceTrasactionAdapter.ViewHolder> {
    ArrayList<BalanceTrasactionList> arrayList;
    Context context;
    public BalanceTrasactionAdapter(ArrayList<BalanceTrasactionList> admissionList, Context context) {
        arrayList = admissionList;
        this.context = context;
    }

    @Override
    public BalanceTrasactionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.balance_trasaction_list, parent, false);
        return new BalanceTrasactionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BalanceTrasactionAdapter.ViewHolder holder, int position) {
        holder.paymentdateTv.setText(arrayList.get(position).getPaymentDate());
        holder.paidTv.setText(arrayList.get(position).getPaid());
        holder.paymenttypeTv.setText(arrayList.get(position).getPaymentType());
        holder.excecutive_nameTV.setText(arrayList.get(position).getExecutiveName());
        holder.balanceTv.setText(arrayList.get(position).getBalance());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView paymentdateTv,paidTv,paymenttypeTv,excecutive_nameTV,balanceTv;

        public ViewHolder(View itemView) {
            super(itemView);

            paymentdateTv = (TextView) itemView.findViewById(R.id.payment_date);
            paidTv = (TextView) itemView.findViewById(R.id.paid);
            paymenttypeTv = (TextView) itemView.findViewById(R.id.paymentTypeTV);
            excecutive_nameTV = (TextView) itemView.findViewById(R.id.excecutive_nameTV);
            balanceTv = (TextView) itemView.findViewById(R.id.balance);
        }

    }
}
