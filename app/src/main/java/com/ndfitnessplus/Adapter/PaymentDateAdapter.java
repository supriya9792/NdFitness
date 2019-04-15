package com.ndfitnessplus.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ndfitnessplus.Model.PaymentDateList;
import com.ndfitnessplus.R;

import java.util.ArrayList;

public class PaymentDateAdapter extends RecyclerView.Adapter<PaymentDateAdapter.ViewHolder> {
    ArrayList<PaymentDateList> arrayList;

    public PaymentDateAdapter(ArrayList<PaymentDateList> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_date_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.nameTV.setText(arrayList.get(position).getName());
        holder.payDateTV.setText(arrayList.get(position).getPayDate());
        holder.totalFeeTV.setText(arrayList.get(position).getTotalFeeDue());
        holder.totalPaidTV.setText(arrayList.get(position).getTotalPaid());
        holder.balanceTV.setText(arrayList.get(position).getBalance());
        holder.paymentModeTV.setText(arrayList.get(position).getPaymentMode());
        holder.paymentDetailsTV.setText(arrayList.get(position).getPayDetails());
        holder.statusTV.setText(arrayList.get(position).getStatus());
        holder.excecutive_nameTV.setText(arrayList.get(position).getExecutiveName());
        holder.followupDateTV.setText(arrayList.get(position).getFollowupDate());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTV,payDateTV,totalFeeTV,totalPaidTV,balanceTV,paymentModeTV,paymentDetailsTV,statusTV,excecutive_nameTV,followupDateTV;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTV = (TextView) itemView.findViewById(R.id.nameTV);
            payDateTV = (TextView) itemView.findViewById(R.id.payDateTV);
            totalFeeTV = (TextView) itemView.findViewById(R.id.totalFeeTV);
            totalPaidTV = (TextView) itemView.findViewById(R.id.totalPaidTV);
            balanceTV = (TextView) itemView.findViewById(R.id.balanceTV);
            paymentModeTV = (TextView) itemView.findViewById(R.id.paymentModeTV);
            paymentDetailsTV = (TextView) itemView.findViewById(R.id.paymentDetailsTV);
            statusTV = (TextView) itemView.findViewById(R.id.statusTV);
            excecutive_nameTV = (TextView) itemView.findViewById(R.id.excecutive_nameTV);
            followupDateTV = (TextView) itemView.findViewById(R.id.followupDateTV);
        }
    }
}
