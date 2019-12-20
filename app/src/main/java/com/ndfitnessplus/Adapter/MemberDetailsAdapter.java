package com.ndfitnessplus.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ndfitnessplus.Activity.BalanceReceiptActivity;
import com.ndfitnessplus.Activity.CourseDetailsActivity;
import com.ndfitnessplus.Activity.MemberDetailsActivity;
import com.ndfitnessplus.Model.CourseList;
import com.ndfitnessplus.Model.MemberDataList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.ViewDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ndfitnessplus.Activity.EnquiryActivity.TAG;

public class MemberDetailsAdapter extends RecyclerView.Adapter<MemberDetailsAdapter.BaseViewHolder> {
    ArrayList<CourseList> arrayList;
    private ArrayList<CourseList> subList;
    Context context;

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    String member_id,name,contact,status,End_Date,FinalBalance,InvoiceId,PackName,FinicialYr;
    ViewDialog viewDialog;

    public MemberDetailsAdapter(ArrayList<CourseList> enquiryList, Context context) {
        //this.arrayList = enquiryList;
        this.subList = enquiryList;
        this.arrayList = new ArrayList<CourseList>();
        this.context = context;
        this.arrayList.addAll(enquiryList);
    }

    @Override
    public MemberDetailsAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "view type: "+viewType );
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new MemberDetailsAdapter.ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.member_details_list, parent, false));
            case VIEW_TYPE_LOADING:
                return new MemberDetailsAdapter.FooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(MemberDetailsAdapter.BaseViewHolder holder, int position) {
        holder.onBind(position);

        Log.d(TAG, "call onbind method of viewholder: " );
    }
    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == arrayList.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return arrayList == null ? 0 : arrayList.size();
    }
    public void add(CourseList response) {
        arrayList.add(response);
        //subList.add(response);
        Log.d(TAG, "sublist size after add : "+String.valueOf(subList.size()) );
        notifyItemInserted(arrayList.size() - 1);
    }

    public void addAll(ArrayList<CourseList> postItems) {
        for (CourseList response : postItems) {
            add(response);
            subList.add(response);
        }

        Log.d(TAG, "arraylist size after adding new data: "+String.valueOf(arrayList.size()) );

    }


    private void remove(CourseList postItems) {
        int position = arrayList.indexOf(postItems);
        if (position > -1) {
            arrayList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addLoading() {
        isLoaderVisible = false;
        add(new CourseList());

    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = arrayList.size() - 1;
        CourseList item = getItem(position);
        if (item != null) {
            arrayList.remove(position);
            notifyItemRemoved(position);
            // notifyDataSetChanged();
        }
    }
    public void removeblank(){
        isLoaderVisible = false;
        int position = arrayList.size() - 1;
        CourseList item = getItem(position);
        if (item != null) {
            arrayList.remove(position);
            notifyItemRemoved(position);
            //notifyDataSetChanged();
        }
    }
    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }
    CourseList getItem(int position) {
        return arrayList.get(position);
    }
    //filter for search
    public void filter(String charText) {
        // subList=arrayList;

        charText = charText.toLowerCase(Locale.getDefault());
        Log.d(TAG, "sublist size whentext  filter: "+String.valueOf(subList.size()) );
        arrayList.clear();
        if (charText.length() == 0) {

            arrayList.addAll(subList);
        } else {
            for (CourseList wp : subList) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText) || wp.getPackageNameWithDS().toLowerCase(Locale.getDefault()).contains(charText)||
                        wp.getBalance().toLowerCase(Locale.getDefault()).contains(charText)
                        ||wp.getPaid().toLowerCase(Locale.getDefault()).contains(charText)
                        ||wp.getContact().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrayList.add(wp);
                }
            }
        }
        Log.d(TAG, "sublist size filter: "+String.valueOf(subList.size()) );
        notifyDataSetChanged();
    }
    //filter for search
    public int search( String charTex,final ArrayList<CourseList> subList) {
        // subList=arrayList;

        final String charText = charTex.toLowerCase(Locale.getDefault());
        Log.d(TAG, "sublist size whentext  filter: "+String.valueOf(subList.size()) );
        arrayList.clear();
        if (charText.length() == 0) {

            arrayList.addAll(subList);
            return 0;
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    // UI code goes here
                    for (final CourseList wp : subList) {
                        if (wp.getName().toLowerCase(Locale.getDefault())
                                .contains(charText) || wp.getPackageNameWithDS().toLowerCase(Locale.getDefault()).contains(charText)||
                                wp.getBalance().toLowerCase(Locale.getDefault()).contains(charText)
                                ||wp.getPaid().toLowerCase(Locale.getDefault()).contains(charText)
                                ||wp.getContact().toLowerCase(Locale.getDefault()).contains(charText)) {
                            arrayList.add(wp);

                        }
                    }
                }
            });



        }
        Log.d(TAG, "sublist size search: "+String.valueOf(subList.size()) );
        notifyDataSetChanged();
        return arrayList.size();
    }
    //View for showing enquiry
    public class ViewHolder extends MemberDetailsAdapter.BaseViewHolder implements View.OnClickListener {
        TextView nameTV,regdateTV,packagenameTV,start_to_end_dateTV,rateTV,paidTV,balanceTV,contactTV,executiveNameTV;
        ImageView contactIV;
        ImageView statusIv,attendanceIv;
        CircularImageView imageView;
        View layoutparent;
        public ViewHolder(View itemView) {
            super(itemView);

            contactTV = (TextView) itemView.findViewById(R.id.contactTV);
            rateTV = (TextView) itemView.findViewById(R.id.rateTV);
            statusIv = (ImageView) itemView.findViewById(R.id.status);
           // contactIV = (ImageView) itemView.findViewById(R.id.contactIV);
           // imageView=(CircularImageView) itemView.findViewById(R.id.input_image);
            //contactIV.setOnClickListener(this);
            regdateTV = (TextView) itemView.findViewById(R.id.reg_dateTV);
            packagenameTV = (TextView) itemView.findViewById(R.id.package_nameTV);
            start_to_end_dateTV = (TextView) itemView.findViewById(R.id.start_to_end_date_TV);
            paidTV = (TextView) itemView.findViewById(R.id.paidTV);
            executiveNameTV=(TextView)itemView.findViewById(R.id.excecutive_nameTV);
            balanceTV = (TextView) itemView.findViewById(R.id.balanceTV);
            layoutparent=(View)itemView.findViewById(R.id.lyt_parent);
            attendanceIv = (ImageView) itemView.findViewById(R.id.attendanceIV);
            viewDialog = new ViewDialog((Activity) context);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.contactIV){
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:"+arrayList.get(getAdapterPosition()).getContact()));
                context.startActivity(dialIntent);
            }
        }
        @Override
        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
            final CourseList enq = arrayList.get(position);
            //Log.d(TAG, "enquiry name: " + enq.getName());
            // idTV.setText(enq.getID());

            regdateTV.setText(enq.getRegistrationDate());
            packagenameTV.setText(enq.getPackageNameWithDS());
            executiveNameTV.setText(enq.getExecutiveName());
            start_to_end_dateTV.setText(enq.getStartToEndDate());
            paidTV.setText(enq.getPaid());
            rateTV.setText(enq.getRate());
            balanceTV.setText(enq.getBalance());
            if(enq.getStatus()!=null) {
                if (enq.getStatus().equals("Active")) {

                    statusIv.setColorFilter(ContextCompat.getColor(context, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    statusIv.setColorFilter(ContextCompat.getColor(context, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
                }
            }
           // if(!enq.getBalance().equals("0.00")) {
             layoutparent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String member_id = enq.getID();
                        Intent intent = new Intent(context, CourseDetailsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("filter_array_list", enq);
                        intent.putExtra("BUNDLE",bundle);
                        context.startActivity(intent);
                  }
              });
            attendanceIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(enq.getStatus().equals("Active")) {
                        if(enq.getCourseStatus().equals("Start")) {
                            member_id = enq.getID();
                            name = enq.getName();
                            status = enq.getStatus();
                            contact = enq.getContact();
                            End_Date = enq.getEndDate();
                            FinalBalance = enq.getBalance();
                            InvoiceId = enq.getInvoiceID();
                            PackName = enq.getPackageName();
                            FinicialYr = enq.getFinancialYear();
                            makeattendanceclass();
                        }else {
                            Toast.makeText(context,"Your Package not stared yet ",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(context,"Your An Inactive Member",Toast.LENGTH_SHORT).show();
                    }
                }
            });
          //  }

        }
    }
    //view for loading on swipe of recyclerview
    public class FooterHolder extends MemberDetailsAdapter.BaseViewHolder {

        @BindView(R.id.progressBar)
        ProgressBar mProgressBar;


        FooterHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {

        }

    }
    //base class for both the views
    public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

        private int mCurrentPosition;

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        protected abstract void clear();

        public void onBind(int position) {
            mCurrentPosition = position;
            clear();
        }

        public int getCurrentPosition() {
            return mCurrentPosition;
        }

    }
    private void makeattendanceclass() {
        MakeAttendanceTrackclass ru = new MakeAttendanceTrackclass();
        ru.execute("5");
    }
    class MakeAttendanceTrackclass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
            //showProgressDialog();
            viewDialog.showDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            // dismissProgressDialog();
            viewDialog.hideDialog();
            //Toast.makeText(CandiateListView.this, response, Toast.LENGTH_LONG).show();
            //  Toast.makeText(NewCustomerActivity.this, response, Toast.LENGTH_LONG).show();
            MakeAttendanceDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> MakeAttendanceDetails = new HashMap<String, String>();
            MakeAttendanceDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId((Activity)context));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId((Activity)context)));
            MakeAttendanceDetails.put("member_id",member_id);
            Log.v(TAG, String.format("doInBackground :: member_id = %s", member_id));
            MakeAttendanceDetails.put("invoice_id",InvoiceId);
            Log.v(TAG, String.format("doInBackground :: invoice_id = %s", InvoiceId));
            MakeAttendanceDetails.put("pack_name",PackName);
            Log.v(TAG, String.format("doInBackground :: pack_name = %s", PackName));
            MakeAttendanceDetails.put("name",name);
            Log.v(TAG, String.format("doInBackground :: name = %s", name));
            MakeAttendanceDetails.put("contact",contact);
            Log.v(TAG, String.format("doInBackground :: contact = %s", contact));
            MakeAttendanceDetails.put("balance",FinalBalance);
            MakeAttendanceDetails.put("expiry_date",End_Date);
            MakeAttendanceDetails.put("status",status);
            MakeAttendanceDetails.put("mode", "AdminApp");
            MakeAttendanceDetails.put("financial_yr", FinicialYr);
            Log.v(TAG, String.format("doInBackground :: expiry_date = %s", End_Date));
            MakeAttendanceDetails.put("action", "make_attendence");
            String domainurl=SharedPrefereneceUtil.getDomainUrl((Activity)context);
            String loginResult2 = ruc.sendPostRequest( domainurl+ServiceUrls.LOGIN_URL, MakeAttendanceDetails);

            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult2));
            return loginResult2;
        }
    }
    private void MakeAttendanceDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject jsonObjLoginResponse = null;
        try {
            jsonObjLoginResponse = new JSONObject(jsonResponse);
            String success = jsonObjLoginResponse.getString(context.getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(context.getResources().getString(R.string.two))) {
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                Toast.makeText(context,"Your Attendance Marked Successfully",Toast.LENGTH_SHORT).show();
                // finish();

                // showCustomDialog();

                //inputEmail, inputPhone,inputAdd,inputReq,inputFollowupdate;
            }

            else if (success.equalsIgnoreCase(context.getResources().getString(R.string.one)))
            {
                Toast.makeText(context,"Please try after 1 hour ,Your attendance is already marked",Toast.LENGTH_SHORT).show();
                // inputContact.getText().clear();
                //Toast.makeText(AttendanceActivity.this,"Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

