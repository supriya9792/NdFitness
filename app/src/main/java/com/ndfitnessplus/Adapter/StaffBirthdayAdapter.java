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

import com.ndfitnessplus.Model.StaffBirthdayList;
import com.ndfitnessplus.Model.StaffBirthdayList;
import com.ndfitnessplus.R;

import java.util.ArrayList;

public class StaffBirthdayAdapter extends RecyclerView.Adapter<StaffBirthdayAdapter.ViewHolder> {

    ArrayList<StaffBirthdayList> arrayList;
    Context context;

    public StaffBirthdayAdapter(ArrayList<StaffBirthdayList> memberList, Context context) {
        arrayList = memberList;
        this.context = context;
    }

    @Override
    public StaffBirthdayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.staff_bday_list_item, parent, false);
        return new StaffBirthdayAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StaffBirthdayAdapter.ViewHolder holder, int position) {
        holder.nameTV.setText(arrayList.get(position).getName());
//        holder.contactTV.setText(arrayList.get(position).getContact());
        holder.birth_dateTV.setText(arrayList.get(position).getBirthDate());
        holder.statusTV.setText(arrayList.get(position).getStatus());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameTV,birth_dateTV,contactTV,statusTV,excecutive_nameTV;
        ImageView contactIV;

        public ViewHolder(View itemView) {
            super(itemView);

            nameTV = (TextView) itemView.findViewById(R.id.nameTV);
            birth_dateTV = (TextView) itemView.findViewById(R.id.birth_dateTV);
            contactIV = (ImageView) itemView.findViewById(R.id.contactIV);
            contactIV.setOnClickListener(this);
            statusTV = (TextView) itemView.findViewById(R.id.statusTV);

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