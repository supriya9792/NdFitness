package com.ndfitnessplus.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ndfitnessplus.Model.StaffAttendanceList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;

import java.util.ArrayList;
import java.util.Locale;

public class StaffAttendanceAdapter  extends RecyclerView.Adapter<StaffAttendanceAdapter.ViewHolder> {
    ArrayList<StaffAttendanceList> arrayList;
    private ArrayList<StaffAttendanceList> subList;
    Context context;
    public StaffAttendanceAdapter(Context context, ArrayList<StaffAttendanceList> admissionList) {
        this.subList = admissionList;
        this.arrayList = new ArrayList<StaffAttendanceList>();
        this.context = context;
        this.arrayList.addAll(admissionList);
    }

    @Override
    public StaffAttendanceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.staff_attendance_list, parent, false);
        return new StaffAttendanceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StaffAttendanceAdapter.ViewHolder holder, int position) {
        final StaffAttendanceList enq = arrayList.get(position);

        holder.attdateTV.setText(enq.getAttendanceDate());
        holder.IntimeTv.setText(enq.getInTime());
        holder.outTimeTv.setText(enq.getOutTime());
        holder.attendancemodeTv.setText(enq.getAttendanceMode());
        holder.staff_idTV.setText(enq.getStaffID());


        holder.nameTv.setText(enq.getName());
        holder.ContactTv.setText(enq.getContact());
        String domainurl= SharedPrefereneceUtil.getDomainUrl((Activity) context);
        String url= domainurl+ ServiceUrls.IMAGES_URL + enq.getImage();

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.nouser);
        requestOptions.error(R.drawable.nouser);


        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(url).into(holder.imageView);


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView attdateTV,nameTv,ContactTv,IntimeTv,outTimeTv,attendancemodeTv,staff_idTV;
        ImageView imageView,statusIV;

        public ViewHolder(View itemView) {
            super(itemView);

            attdateTV = (TextView) itemView.findViewById(R.id.attendacnedateTV);
            IntimeTv = (TextView) itemView.findViewById(R.id.intimeTV);
            outTimeTv = (TextView) itemView.findViewById(R.id.outtimeTv);
            nameTv = (TextView) itemView.findViewById(R.id.nameTV);
            ContactTv = (TextView) itemView.findViewById(R.id.contactTV);
            attendancemodeTv = (TextView) itemView.findViewById(R.id.attendancemodeTv);
            staff_idTV = (TextView) itemView.findViewById(R.id.staff_idTV);
            imageView=(ImageView)itemView.findViewById(R.id.input_image);
            statusIV=(ImageView)itemView.findViewById(R.id.status);
        }

    }
    //filter for search
    public int filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());

        arrayList.clear();
        if (charText.length() == 0) {

            arrayList.addAll(subList);
        } else {
            for (StaffAttendanceList wp : subList) {
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


