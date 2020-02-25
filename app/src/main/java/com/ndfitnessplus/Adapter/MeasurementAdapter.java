package com.ndfitnessplus.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.ndfitnessplus.Model.EnquiryList;
import com.ndfitnessplus.Model.MeasurementList;
import com.ndfitnessplus.R;

import java.util.ArrayList;
import java.util.Locale;

import static com.ndfitnessplus.Activity.EnquiryActivity.TAG;

public class MeasurementAdapter extends RecyclerView.Adapter<MeasurementAdapter.ViewHolder> {
   public ArrayList<MeasurementList> arrayList;
    private ArrayList<MeasurementList> subList;
   public Context context;
    public MeasurementAdapter(Context context, ArrayList<MeasurementList> admissionList) {
        this.subList = admissionList;
        this.arrayList = new ArrayList<MeasurementList>();
        this.context = context;
        this.arrayList.addAll(admissionList);
    }

    @Override
    public MeasurementAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.measurement_list, parent, false);
        return new MeasurementAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MeasurementAdapter.ViewHolder holder, int position) {
        final MeasurementList enq = arrayList.get(position);

        holder.mDateTv.setText(enq.getMeasurement_Date());
        holder.weightTv.setText(enq.getWeight());
        holder.heightTv.setText(enq.getHeight());
        holder.ageTV.setText(enq.getAge());
        holder.bmiTv.setText(enq.getBMI());
        holder.fatTv.setText(enq.getFat());
        holder.neckTv.setText(enq.getNeck());
        holder.shoulderTv.setText(enq.getShoulder());
        holder.chestTv.setText(enq.getChest());
        holder.arms_rTv.setText(enq.getArms_R());
        holder.arms_lTv.setText(enq.getArms_L());
        holder.forarmsTv.setText(enq.getForArms());
        holder.waistTv.setText(enq.getWaist());
        holder.hipsTv.setText(enq.getHips());
        holder.thigh_rTv.setText(enq.getThigh_R());
        holder.thigh_lTv.setText(enq.getThigh_L());
        holder.calf_rTv.setText(enq.getCalf_R());
        holder.calf_lTv.setText(enq.getCalf_L());
        holder.nextfolldateTv.setVisibility(View.GONE);
        holder.excecutive_nameTV.setText(enq.getExecutive_Name());
        holder.mdateTV.setText(enq.getNextFollowupDate());

        holder.nameTv.setText(enq.getName());
        holder.ContactTv.setText(enq.getContactEncrypt());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView mDateTv,weightTv,heightTv,ageTV,bmiTv,fatTv,neckTv,shoulderTv,chestTv,arms_rTv,arms_lTv,forarmsTv,waistTv,hipsTv,thigh_rTv,thigh_lTv,calf_rTv,
                calf_lTv,nextfolldateTv,excecutive_nameTV,mdateTV,nameTv,ContactTv;

        public ViewHolder(View itemView) {
            super(itemView);

            mDateTv =  itemView.findViewById(R.id.measurement_date);
            weightTv =  itemView.findViewById(R.id.weight);
            heightTv =  itemView.findViewById(R.id.height);
            ageTV =  itemView.findViewById(R.id.age);
            bmiTv =  itemView.findViewById(R.id.bmi);
            fatTv =  itemView.findViewById(R.id.fat);
            neckTv =  itemView.findViewById(R.id.neck);
            shoulderTv =  itemView.findViewById(R.id.shoulder);
            chestTv =  itemView.findViewById(R.id.chest);
            arms_rTv =  itemView.findViewById(R.id.arms_r);
            arms_lTv =  itemView.findViewById(R.id.arms_l);
            forarmsTv =  itemView.findViewById(R.id.forarms);
            waistTv =  itemView.findViewById(R.id.waist);
            hipsTv =  itemView.findViewById(R.id.hips);
            thigh_rTv =  itemView.findViewById(R.id.thigh_r);
            thigh_lTv =  itemView.findViewById(R.id.thigh_l);
            calf_rTv =  itemView.findViewById(R.id.calf_r);
            calf_lTv =  itemView.findViewById(R.id.calf_l);
            nextfolldateTv =  itemView.findViewById(R.id.nextFollDate);
            excecutive_nameTV =  itemView.findViewById(R.id.exe_name);
            mdateTV =  itemView.findViewById(R.id.mdate);
            nameTv =  itemView.findViewById(R.id.nameTV);
            ContactTv =  itemView.findViewById(R.id.contactTV);
        }

    }
    //filter for search
    public int filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        arrayList.clear();
        if (charText.length() == 0) {

            arrayList.addAll(subList);
        } else {
            for (MeasurementList wp : subList) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText) || wp.getContact().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrayList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
        return arrayList.size();
    }
}
