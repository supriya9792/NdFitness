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

import com.ndfitnessplus.Model.AdmissionList;
import com.ndfitnessplus.R;

import java.util.ArrayList;

public class AdmissionAdapter extends RecyclerView.Adapter<AdmissionAdapter.ViewHolder> {
        ArrayList<AdmissionList> arrayList;
        Context context;
public AdmissionAdapter(ArrayList<AdmissionList> admissionList, Context context) {
        arrayList = admissionList;
        this.context = context;
        }

@Override
public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admission_list_item, parent, false);
        return new ViewHolder(view);
        }

@Override
public void onBindViewHolder(ViewHolder holder, int position) {
        holder.nameTV.setText(arrayList.get(position).getName());
//        holder.contactTV.setText(arrayList.get(position).getContact());
        holder.reg_dateTV.setText(arrayList.get(position).getRegDate());
        holder.statusTV.setText(arrayList.get(position).getStatus());
        holder.excecutive_nameTV.setText(arrayList.get(position).getExecutiveName());
        holder.start_dateTV.setText(arrayList.get(position).getStartDate());
        holder.endDateTV.setText(arrayList.get(position).getEndDate());
        holder.packageTV.setText(arrayList.get(position).getPackageInfo());
        holder.balanceTV.setText(arrayList.get(position).getBalance());
        holder.totalPaidTV.setText(arrayList.get(position).getTotalPaid());
        holder.sessionTV.setText(arrayList.get(position).getSession());
        holder.totalFeeTV.setText(arrayList.get(position).getTotalFeeDue());
        }

@Override
public int getItemCount() {
        return arrayList.size();
        }

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView nameTV,reg_dateTV,contactTV,statusTV,excecutive_nameTV,start_dateTV,endDateTV,packageTV,balanceTV,totalPaidTV,sessionTV,totalFeeTV;
    ImageView contactIV;
    public ViewHolder(View itemView) {
        super(itemView);

        nameTV = (TextView) itemView.findViewById(R.id.nameTV);
        reg_dateTV = (TextView) itemView.findViewById(R.id.reg_dateTV);
        contactIV = (ImageView) itemView.findViewById(R.id.contactIV);
        contactIV.setOnClickListener(this);
        statusTV = (TextView) itemView.findViewById(R.id.statusTV);
        excecutive_nameTV = (TextView) itemView.findViewById(R.id.excecutive_nameTV);
        start_dateTV = (TextView) itemView.findViewById(R.id.start_dateTV);
        endDateTV = (TextView) itemView.findViewById(R.id.endDateTV);
        packageTV = (TextView) itemView.findViewById(R.id.packageTV);
        balanceTV = (TextView) itemView.findViewById(R.id.balanceTV);
        totalPaidTV = (TextView) itemView.findViewById(R.id.totalPaidTV);
        sessionTV = (TextView) itemView.findViewById(R.id.sessionTV);
        totalFeeTV = (TextView) itemView.findViewById(R.id.totalFeeTV);
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
