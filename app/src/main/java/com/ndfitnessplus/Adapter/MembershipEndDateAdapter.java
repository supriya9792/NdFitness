package com.ndfitnessplus.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ndfitnessplus.Model.MembershipEndDateList;
import com.ndfitnessplus.R;

import java.util.ArrayList;

public class MembershipEndDateAdapter extends RecyclerView.Adapter<MembershipEndDateAdapter.ViewHolder> {
        ArrayList<MembershipEndDateList> arrayList;

public MembershipEndDateAdapter(ArrayList<MembershipEndDateList> arrayList) {
        this.arrayList = arrayList;
        }

@Override
public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_end_date_list_item, parent, false);
        return new ViewHolder(view);
        }

@Override
public void onBindViewHolder(ViewHolder holder, int position) {
        holder.nameTV.setText(arrayList.get(position).getName());
        holder.packageTV.setText(arrayList.get(position).getPackage());
        holder.start_dateTV.setText(arrayList.get(position).getStartDate());
        holder.endDateTV.setText(arrayList.get(position).getEndDate());
        holder.amountTV.setText(arrayList.get(position).getAmount());
        holder.member_typeTV.setText(arrayList.get(position).getCourseMemberType());
        holder.statusTV.setText(arrayList.get(position).getStatus());
        }

@Override
public int getItemCount() {
        return arrayList.size();
        }

public class ViewHolder extends RecyclerView.ViewHolder {
    TextView nameTV,packageTV,start_dateTV,endDateTV,amountTV,member_typeTV,statusTV;
    public ViewHolder(View itemView) {
        super(itemView);
        nameTV = (TextView) itemView.findViewById(R.id.nameTV);
        packageTV = (TextView) itemView.findViewById(R.id.packageTV);
        start_dateTV = (TextView) itemView.findViewById(R.id.start_dateTV);
        endDateTV = (TextView) itemView.findViewById(R.id.endDateTV);
        amountTV = (TextView) itemView.findViewById(R.id.amountTV);
        member_typeTV = (TextView) itemView.findViewById(R.id.member_typeTV);
        statusTV = (TextView) itemView.findViewById(R.id.statusTV);
    }
  }
}
