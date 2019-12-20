package com.ndfitnessplus.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ndfitnessplus.Model.AttendanceDetailList;
import com.ndfitnessplus.R;

import java.util.ArrayList;

public class AttendanceDetailsAdapter extends RecyclerView.Adapter<AttendanceDetailsAdapter.ViewHolder> {
    ArrayList<AttendanceDetailList> arrayList;
    Context context;
    public AttendanceDetailsAdapter(ArrayList<AttendanceDetailList> admissionList, Context context) {
        arrayList = admissionList;
        this.context = context;
    }

    @Override
    public AttendanceDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_date_list, parent, false);
        return new AttendanceDetailsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AttendanceDetailsAdapter.ViewHolder holder, int position) {
        holder.attendancedateTv.setText(arrayList.get(position).getAttendanceDate());
//        holder.contactTV.setText(arrayList.get(position).getContact());
        holder.timeTV.setText(arrayList.get(position).getTime());
        holder.ModeTv.setText(arrayList.get(position).getAttendanceMode());


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView attendancedateTv,timeTV,ModeTv;

        public ViewHolder(View itemView) {
            super(itemView);

            attendancedateTv = (TextView) itemView.findViewById(R.id.attendance_date);
            timeTV = (TextView) itemView.findViewById(R.id.time);
            ModeTv = (TextView) itemView.findViewById(R.id.mode);

        }

    }
}