package com.ndfitnessplus.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ndfitnessplus.Model.ExpensesList;
import com.ndfitnessplus.Model.FollowupList;
import com.ndfitnessplus.R;

import java.util.ArrayList;
import java.util.Locale;

import static com.ndfitnessplus.Activity.EnquiryActivity.TAG;

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.ViewHolder> {
    ArrayList<ExpensesList> arrayList;
    private ArrayList<ExpensesList> subList;
    Context context;
    public ExpensesAdapter(ArrayList<ExpensesList> expenselist, Context context) {
        arrayList = expenselist;
        this.context = context;
        this.subList = expenselist;
        this.arrayList = new ArrayList<ExpensesList>();
        this.arrayList.addAll(expenselist);
    }

    @Override
    public ExpensesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expenses_list_item, parent, false);
        return new ExpensesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExpensesAdapter.ViewHolder holder, int position) {
        holder.ttlofexpenses.setText(arrayList.get(position).getTtl_of_expenses());
        holder.expenseDate.setText(arrayList.get(position).getExpenses_date());
        holder.expenseGroup.setText(arrayList.get(position).getExpenses_group());
        String amt="₹ "+arrayList.get(position).getAmount();
        holder.amount.setText(amt);
        holder.excecutive_nameTV.setText(arrayList.get(position).getExecutive_name());
        holder.paymentType.setText(arrayList.get(position).getPayment_type());
        holder.disc.setText(arrayList.get(position).getDisc());
        holder.paymentDtl.setText(arrayList.get(position).getPayment_dtl());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView ttlofexpenses,expenseDate,expenseGroup,paymentDtl,amount,excecutive_nameTV,paymentType,disc;
        public ViewHolder(View itemView) {
            super(itemView);

            ttlofexpenses = (TextView) itemView.findViewById(R.id.ttlOfExpenses);
            expenseDate = (TextView) itemView.findViewById(R.id.expense_dateTV);
            expenseGroup = (TextView) itemView.findViewById(R.id.expenseGroup);
            paymentDtl = (TextView) itemView.findViewById(R.id.paymentDetailsTV);
            amount = (TextView) itemView.findViewById(R.id.amountTV);
            excecutive_nameTV = (TextView) itemView.findViewById(R.id.excecutive_nameTV);
            paymentType = (TextView) itemView.findViewById(R.id.paymentTypeTV);
            disc = (TextView) itemView.findViewById(R.id.pay_disc);
            disc.setVisibility(View.GONE);
            expenseGroup.setVisibility(View.GONE);

        }


    }
    //filter for search
    public ArrayList<ExpensesList> filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        arrayList.clear();
        if (charText.length() == 0) {

            arrayList.addAll(subList);
        } else {
            for (ExpensesList wp : subList) {
                if (wp.getTtl_of_expenses().toLowerCase(Locale.getDefault())
                        .contains(charText) ||
                        wp.getExecutive_name().toLowerCase(Locale.getDefault()).contains(charText)||wp.getPayment_type().toLowerCase(Locale.getDefault()).contains(charText)
                        ||wp.getAmount().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrayList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
        return arrayList;
    }
}
