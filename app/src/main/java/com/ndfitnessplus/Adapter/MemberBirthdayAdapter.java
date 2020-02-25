package com.ndfitnessplus.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ndfitnessplus.Activity.MemberDetailsActivity;
import com.ndfitnessplus.Model.MemberDataList;
import com.ndfitnessplus.R;

import java.util.ArrayList;

public class MemberBirthdayAdapter extends RecyclerView.Adapter<MemberBirthdayAdapter.ViewHolder> {

    ArrayList<MemberDataList> arrayList;
    Context context;

    public MemberBirthdayAdapter(ArrayList<MemberDataList> memberList, Context context) {
        arrayList = memberList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_birthday_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MemberDataList enq = arrayList.get(position);
        holder.nameTV.setText(arrayList.get(position).getName());
        holder.contactTV.setText(arrayList.get(position).getContact());
        holder.birth_dateTV.setText(arrayList.get(position).getBirthDate());
        holder.regDateTV.setText(arrayList.get(position).getRegistrationDate());
        if(enq.getStatus().equals("Active")){

            holder.statusTV.setColorFilter(ContextCompat.getColor(context, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
        }else{
           holder.statusTV.setColorFilter(ContextCompat.getColor(context, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        holder.excecutive_nameTV.setText(arrayList.get(position).getExecutiveName());
        holder.layoutp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, MemberDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("filter_array_list", enq);
                intent.putExtra("BUNDLE",bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameTV,birth_dateTV,contactTV,excecutive_nameTV,regDateTV;
        ImageView statusTV;
        View layoutp;
        public ViewHolder(View itemView) {
            super(itemView);

            nameTV = (TextView) itemView.findViewById(R.id.nameTV);
            birth_dateTV = (TextView) itemView.findViewById(R.id.birth_dateTV);
            contactTV = (TextView) itemView.findViewById(R.id.contactTV);

            statusTV = (ImageView) itemView.findViewById(R.id.status);
            excecutive_nameTV = (TextView) itemView.findViewById(R.id.excecutive_nameTV);
            regDateTV = (TextView) itemView.findViewById(R.id.reg_dateTV);

            layoutp=(View)itemView.findViewById(R.id.layout);
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

