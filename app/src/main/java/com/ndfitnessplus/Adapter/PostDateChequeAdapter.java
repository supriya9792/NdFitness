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

import com.ndfitnessplus.Model.PostDateChequeList;
import com.ndfitnessplus.R;

import java.util.ArrayList;

public class PostDateChequeAdapter extends RecyclerView.Adapter<PostDateChequeAdapter.ViewHolder> {
    ArrayList<PostDateChequeList> arrayList;
    Context context;

    public PostDateChequeAdapter(ArrayList<PostDateChequeList> arrayList,Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_date_cheque_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.nameTV.setText(arrayList.get(position).getName());
        holder.payDateTV.setText(arrayList.get(position).getPaymentDate());
        holder.chequeNoTV.setText(arrayList.get(position).getCheckNo());
        holder.paidTV.setText(arrayList.get(position).getPaid());
        holder.taxTypeTV.setText(arrayList.get(position).getTaxType());
        holder.paymentModeTV.setText(arrayList.get(position).getPaymentMode());
        holder.paidWithTaxTV.setText(arrayList.get(position).getPaidWithTax());
        holder.statusTV.setText(arrayList.get(position).getStatus());
        holder.chequeExpDateTV.setText(arrayList.get(position).getChequeExpiryDate());
//        holder.contactIV.setText(arrayList.get(position).getContact());
        holder.recieptNoTV.setText(arrayList.get(position).getRecieptNumber());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameTV,payDateTV,chequeNoTV,paidTV,taxTypeTV,paymentModeTV,paidWithTaxTV,statusTV,chequeExpDateTV,recieptNoTV;
        ImageView contactIV;
        public ViewHolder(View itemView) {
            super(itemView);
            nameTV = (TextView) itemView.findViewById(R.id.nameTV);
            payDateTV = (TextView) itemView.findViewById(R.id.payDateTV);
            chequeNoTV = (TextView) itemView.findViewById(R.id.chequeNoTV);
            paidTV = (TextView) itemView.findViewById(R.id.paidTV);
            taxTypeTV = (TextView) itemView.findViewById(R.id.taxTypeTV);
            paymentModeTV = (TextView) itemView.findViewById(R.id.paymentModeTV);
            paidWithTaxTV = (TextView) itemView.findViewById(R.id.paidWithTaxTV);
            statusTV = (TextView) itemView.findViewById(R.id.statusTV);
            chequeExpDateTV = (TextView) itemView.findViewById(R.id.chequeExpDateTV);
            contactIV = (ImageView) itemView.findViewById(R.id.contactIV);
            contactIV.setOnClickListener(this);
            recieptNoTV = (TextView) itemView.findViewById(R.id.recieptNoTV);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.contactIV){
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:"+arrayList.get(getAdapterPosition()).getContact()));
                context.startActivity(dialIntent);
            }
        }
    }
}

