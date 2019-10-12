package com.ndfitnessplus.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ndfitnessplus.Model.ProductTrasanctionList;
import com.ndfitnessplus.R;

import java.util.ArrayList;

public class ProductTrasanctionAdapter extends RecyclerView.Adapter<ProductTrasanctionAdapter.ViewHolder> {
    ArrayList<ProductTrasanctionList> arrayList;

    Context context;

    public ProductTrasanctionAdapter(ArrayList<ProductTrasanctionList> paramArrayList, Context paramContext) {
        this.arrayList = paramArrayList;
        this.context = paramContext;
    }

    public int getItemCount() { return this.arrayList.size(); }

    public void onBindViewHolder(ViewHolder paramViewHolder, int paramInt) {
        paramViewHolder.prodCodeTV.setText(((ProductTrasanctionList)this.arrayList.get(paramInt)).getProdCode());
        paramViewHolder.prodNameTV.setText(((ProductTrasanctionList)this.arrayList.get(paramInt)).getProdName());
        paramViewHolder.rateTV.setText(((ProductTrasanctionList)this.arrayList.get(paramInt)).getRate());
        paramViewHolder.quantityTV.setText(((ProductTrasanctionList)this.arrayList.get(paramInt)).getQuantity());
        paramViewHolder.prodTotalTV.setText(((ProductTrasanctionList)this.arrayList.get(paramInt)).getProductFinalRate());
    }

    public ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt) {
        return new ViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.product_trasaction_list, paramViewGroup, false)); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView prodCodeTV;

        TextView prodNameTV;

        TextView prodTotalTV;

        TextView quantityTV;

        TextView rateTV;

        public ViewHolder(View param1View) {
            super(param1View);
            this.prodCodeTV = (TextView)param1View.findViewById(R.id.prod_code);
            this.prodNameTV = (TextView)param1View.findViewById(R.id.prod_name);
            this.rateTV = (TextView)param1View.findViewById(R.id.rate);
            this.quantityTV = (TextView)param1View.findViewById(R.id.quantityTV);
            this.prodTotalTV = (TextView)param1View.findViewById(R.id.prod_total);
        }
    }
}