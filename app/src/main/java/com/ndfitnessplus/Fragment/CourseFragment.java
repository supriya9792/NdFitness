package com.ndfitnessplus.Fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ndfitnessplus.Activity.CourseActivity;
import com.ndfitnessplus.Activity.CourseDetailsActivity;
import com.ndfitnessplus.Activity.RenewActivity;
import com.ndfitnessplus.Model.BalanceTrasactionList;
import com.ndfitnessplus.Model.CourseList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class CourseFragment extends DialogFragment {
    private View root_view;
    String member_id,invoice_id,FinancialYear;
   TextView Name,Packagename,StartDate,EndDate,PaymentMode,Paid,InvoiceId,Date,SessionTv,DurationTv,MobileNo,BalanceTV,Total;
    CircularImageView imageView;
    public static String TAG = CourseDetailsActivity.class.getName();
    public CourseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root_view= inflater.inflate(R.layout.fragment_course, container, false);

        member_id= this.getArguments().getString("member_id");
        invoice_id= this.getArguments().getString("invoice_id");
        FinancialYear= this.getArguments().getString("financial_yr");
        Name=root_view.findViewById(R.id.name);
        Packagename=root_view.findViewById(R.id.package_name);
        StartDate=root_view.findViewById(R.id.start_date);
        EndDate=root_view.findViewById(R.id.end_date);
       // PaymentMode=root_view.findViewById(R.id.payment_type);
        Paid=root_view.findViewById(R.id.paid);
        InvoiceId=root_view.findViewById(R.id.invoice_idTV);
        Date=root_view.findViewById(R.id.reg_dateTV);
        SessionTv=root_view.findViewById(R.id.session);
        DurationTv=root_view.findViewById(R.id.duration);
        MobileNo=root_view.findViewById(R.id.contact);
        BalanceTV=root_view.findViewById(R.id.balance);
        //Total=root_view.findViewById(R.id.total);
        imageView=root_view.findViewById(R.id.user_image);
        coursedetailsclass();
        ((FloatingActionButton) root_view.findViewById(R.id.fab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent intent = new Intent(getContext(), CourseActivity.class);
                startActivity(intent);
            }
        });

        return root_view;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
       // getDialog().getWindow().setLayout(width, height);

        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    private void coursedetailsclass() {
         CourseDetailsTrackclass ru = new  CourseDetailsTrackclass();
        ru.execute("5");
    }

    class  CourseDetailsTrackclass extends AsyncTask<String, Void, String> {


        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
            //showProgressDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            //dismissProgressDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            CourseDetailsDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> CourseDetailsDetails = new HashMap<String, String>();
            CourseDetailsDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId((Activity)getContext()));
            CourseDetailsDetails.put("member_id",member_id );
            CourseDetailsDetails.put("invoice_id",invoice_id );
            CourseDetailsDetails.put("financial_yr",FinancialYear );
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId((Activity)getContext())));
            Log.v(TAG, String.format("doInBackground :: member_id  = %s", member_id));
            String domainurl=SharedPrefereneceUtil.getDomainUrl((Activity)getContext());
            CourseDetailsDetails.put("action","show_course_details_by_member_id");
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL,  CourseDetailsDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }

    private void  CourseDetailsDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
//        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayoutPrabhagDetails);
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {

                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }
                        ArrayList<BalanceTrasactionList> item = new ArrayList<BalanceTrasactionList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                               // filterArrayList = new CourseList();
                                Log.d(TAG, "i: " + i);

                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String name = jsonObj.getString("Name");
                                    String RegistrationDate = jsonObj.getString("RegistrationDate");
                                    String Contact = jsonObj.getString("Contact");
                                    String Package_Name = jsonObj.getString("Package_Name");
                                    String ExecutiveName = jsonObj.getString("ExecutiveName");
                                    String Duration_Days = jsonObj.getString("Duration_Days");
                                    String Session = jsonObj.getString("Session");
                                    String Member_ID = jsonObj.getString("Member_ID");
                                    String Image = jsonObj.getString("Image");
                                    String Start_Date = jsonObj.getString("Start_Date");
                                    String End_Date = jsonObj.getString("End_Date");
                                    String Rate = jsonObj.getString("Rate");
                                    String Final_paid = jsonObj.getString("Final_paid");
                                    String Final_Balance = jsonObj.getString("Final_Balance");
                                    String Invoice_ID = jsonObj.getString("Invoice_ID");
                                    String Tax = jsonObj.getString("Tax");
                                    String Member_Email_ID = jsonObj.getString("Member_Email_ID");
                                    String Financial_Year = jsonObj.getString("Financial_Year");
                                    String PaymentType = jsonObj.getString("PaymentType");

                                    //  for (int j = 0; j < 5; j++) {

                                    String sdate=Utility.formatDate(Start_Date);
                                    String edate=Utility.formatDate(End_Date);
                                    String todate=sdate+" to "+edate;

                                    String dur_sess="Duration: "+Duration_Days;
                                    String session="Session: "+Session;

                                    String reg_date= Utility.formatDate(RegistrationDate);



                                    if(Final_Balance.equals(".00")){
                                        Final_Balance="0.00";
                                    }
                                    String fbalance="Remaining : ₹ "+Final_Balance;

                                    Image.replace("\"", "");

                                    String fpaid="₹ "+Final_paid;
                                    String ttl="₹ "+Rate;


//                                    String sdate=Utility.formatDate(Start_Date);
//                                    String edate=Utility.dformatDate(End_Date);
////                                    String todate=sdate+" to "+eate;
//                                    start_to_end_dateTV.setText(todate);

                                    invoice_id=Invoice_ID;

                                    member_id=Member_ID;

                                    Name.setText(name);
                                    MobileNo.setText(Contact);
                                    Packagename.setText(Package_Name);

                                    StartDate.setText(sdate);
                                    EndDate.setText(edate);
                                   // PaymentMode.setText(PaymentType);
                                    Paid.setText(fpaid);
                                    InvoiceId.setText(Invoice_ID);
                                    Date.setText(reg_date);
                                    SessionTv.setText(session);
                                    DurationTv.setText(dur_sess);
                                   // Total.setText(ttl);
                                    BalanceTV.setText(fbalance);

                                    String domainurl= SharedPrefereneceUtil.getDomainUrl((Activity)getActivity());
                                    String url= domainurl+ServiceUrls.IMAGES_URL + Image;

                                    // Glide.with(this).load(url).placeholder(R.drawable.nouser).into(imageView);
                                    RequestOptions requestOptions = new RequestOptions();
                                    requestOptions.placeholder(R.drawable.nouser);
                                    requestOptions.error(R.drawable.nouser);

                                    if(!(Image.equals("null")||Image.equals(""))){
                                        Glide.with(this)
                                                .setDefaultRequestOptions(requestOptions)
                                                .load(url).into(imageView);
                                    }

                                    Tax=Tax;



                                }
                            }
                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    //nodata.setVisibility(View.VISIBLE);
                    // recyclerView.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                builder.setMessage(R.string.server_exception);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                android.app.AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();
            }
        }
    }
}
