package com.ndfitnessplus.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ndfitnessplus.Model.AttendanceList;
import com.ndfitnessplus.Model.MeasurementList;
import com.ndfitnessplus.R;

import java.util.ArrayList;
import java.util.Locale;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {
    ArrayList<AttendanceList> arrayList;
    private ArrayList<AttendanceList> subList;
    Context context;
    public AttendanceAdapter(Context context, ArrayList<AttendanceList> admissionList) {
        this.subList = admissionList;
        this.arrayList = new ArrayList<AttendanceList>();
        this.context = context;
        this.arrayList.addAll(admissionList);
    }

    @Override
    public AttendanceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_list, parent, false);
        return new AttendanceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AttendanceAdapter.ViewHolder holder, int position) {
        final AttendanceList enq = arrayList.get(position);

        holder.attdateTV.setText(enq.getAttendanceDate());
        holder.timeTv.setText(enq.getTime());
        //holder.packageNameTv.setText(enq.getPackageName());
        //holder.ExpiryDateTv.setText(enq.getExpiryDate());
        holder.attendancemodeTv.setText(enq.getAttendanceMode());
        holder.member_idTV.setText(enq.getMemberID());

        //holder.excecutive_nameTV.setText(enq.getExecutiveName());


        holder.nameTv.setText(enq.getName());
        holder.ContactTv.setText(enq.getContactEncrypt());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView excecutive_nameTV,attdateTV,nameTv,ContactTv,timeTv,packageNameTv,ExpiryDateTv,attendancemodeTv,member_idTV;

        public ViewHolder(View itemView) {
            super(itemView);

            attdateTV = (TextView) itemView.findViewById(R.id.attendacnedateTV);
            timeTv = (TextView) itemView.findViewById(R.id.timeTV);
           // packageNameTv = (TextView) itemView.findViewById(R.id.packagenameTv);
           // ExpiryDateTv = (TextView) itemView.findViewById(R.id.membershipEnddateTV);
          //  excecutive_nameTV = (TextView) itemView.findViewById(R.id.exe_name);
            nameTv = (TextView) itemView.findViewById(R.id.nameTV);
            ContactTv = (TextView) itemView.findViewById(R.id.contactTV);
            attendancemodeTv = (TextView) itemView.findViewById(R.id.attendancemodeTv);
            member_idTV = (TextView) itemView.findViewById(R.id.member_idTV);
        }

    }
    //filter for search
    public int filter(String charText) {
        // subList=arrayList;

        charText = charText.toLowerCase(Locale.getDefault());
        // Log.d(TAG, "sublist size whentext  filter: "+String.valueOf(subList.size()) );
        arrayList.clear();
        if (charText.length() == 0) {

            arrayList.addAll(subList);
        } else {
            for (AttendanceList wp : subList) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText) || wp.getContact().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrayList.add(wp);
                    // return arrayList.size();
                }
            }
        }
        notifyDataSetChanged();
        return arrayList.size();
        //Log.d(TAG, "sublist size filter: "+String.valueOf(subList.size()) );

    }

}

