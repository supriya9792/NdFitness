package com.ndfitnessplus.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ndfitnessplus.Model.SellProductList;
import com.ndfitnessplus.R;

import java.util.ArrayList;

public class SellProductAdapter extends RecyclerView.Adapter<SellProductAdapter.ViewHolder> {
    ArrayList<SellProductList> arrayList;
    Context context;
    public SellProductAdapter(ArrayList<SellProductList> admissionList, Context context) {
        arrayList = admissionList;
        this.context = context;
    }

    @Override
    public SellProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sell_product_list, parent, false);
        return new SellProductAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SellProductAdapter.ViewHolder holder, int position) {
        holder.prodCodeTv.setText(arrayList.get(position).getProductCode());
//        holder.contactTV.setText(arrayList.get(position).getContact());
        holder.prodNameTv.setText(arrayList.get(position).getProductName());
        holder.quantityTv.setText(arrayList.get(position).getQuantity());
        holder.rateTV.setText(arrayList.get(position).getRate());
        holder.prodFinalRateTv.setText(arrayList.get(position).getProdFinalRate());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView prodCodeTv,prodNameTv,quantityTv,rateTV,prodFinalRateTv;

        public ViewHolder(View itemView) {
            super(itemView);

            prodCodeTv = (TextView) itemView.findViewById(R.id.prodcodeTV);
            prodNameTv = (TextView) itemView.findViewById(R.id.ProdnameTV);
            quantityTv = (TextView) itemView.findViewById(R.id.quantityTV);
            rateTV = (TextView) itemView.findViewById(R.id.rateTV);
            prodFinalRateTv = (TextView) itemView.findViewById(R.id.pfinalrateTV);
        }

    }
}

