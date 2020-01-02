package com.ndfitnessplus.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ndfitnessplus.Adapter.AttendanceDetailsAdapter;
import com.ndfitnessplus.Adapter.BalanceTrasactionAdapter;
import com.ndfitnessplus.Model.AttendanceDetailList;
import com.ndfitnessplus.Model.BalanceTrasactionList;
import com.ndfitnessplus.Model.CourseList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.Utility;
import com.ndfitnessplus.Utility.ViewDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CourseWiseAttendanceDetailsActivity extends AppCompatActivity {

    ViewDialog viewDialog;
    String invoice_id;
    String member_id;
    String FinancialYear;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    ProgressBar progressBar;
    View nodata;
    AttendanceDetailsAdapter adapter;
    ArrayList<AttendanceDetailList> subListArrayList = new ArrayList<AttendanceDetailList>();
    AttendanceDetailList subList;
    public final String TAG = CourseWiseAttendanceDetailsActivity.class.getName();
    ImageButton backmonth, nextmonth;

    private ProgressDialog pd;
    TextView nameTV,regdateTV,packagenameTV,start_to_end_dateTV,rateTV,paidTV,balanceTV,contactTV,executiveNameTV,durationTv,invoice_idTV;
    ImageView contactIV;
    CircularImageView imageView;
    CourseList filterArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_course_wise_attendance_details);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.attendance_details));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){

        contactTV = (TextView) findViewById(R.id.contactTV);
        rateTV = (TextView) findViewById(R.id.rateTV);
        nameTV = (TextView) findViewById(R.id.nameTV);
        contactIV = (ImageView) findViewById(R.id.contactIV);
        imageView=(CircularImageView) findViewById(R.id.input_image);
        nodata=findViewById(R.id.nodata);
        viewDialog = new ViewDialog(this);

        regdateTV = (TextView) findViewById(R.id.reg_dateTV);
        packagenameTV = (TextView) findViewById(R.id.package_nameTV);
        start_to_end_dateTV = (TextView) findViewById(R.id.start_to_end_date_TV);
        paidTV = (TextView) findViewById(R.id.paidTV);
        executiveNameTV=(TextView)findViewById(R.id.excecutive_nameTV);
        balanceTV = (TextView) findViewById(R.id.balanceTV);
        durationTv = (TextView) findViewById(R.id.duration);
        invoice_idTV = (TextView) findViewById(R.id.invoice_idTV);

        progressBar=findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        Intent intent = getIntent();
        if(intent!=null){
            invoice_id = intent.getStringExtra("invoice_id");
            FinancialYear =intent.getStringExtra("financial_yr");
            member_id = intent.getStringExtra("member_id");
            coursedetailsclass();
            attendanceclass();
        }


    }
    private void coursedetailsclass() {
        CourseWiseAttendanceDetailsActivity. CourseDetailsTrackclass ru = new CourseWiseAttendanceDetailsActivity. CourseDetailsTrackclass();
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
            CourseDetailsDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(CourseWiseAttendanceDetailsActivity.this));
            CourseDetailsDetails.put("member_id",member_id );
            CourseDetailsDetails.put("invoice_id",invoice_id );
            CourseDetailsDetails.put("financial_yr",FinancialYear );
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(CourseWiseAttendanceDetailsActivity.this)));
            Log.v(TAG, String.format("doInBackground :: member_id id = %s", member_id));
            String domainurl=SharedPrefereneceUtil.getDomainUrl(CourseWiseAttendanceDetailsActivity.this);
            CourseDetailsDetails.put("action","show_course_details_by_member_id");
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL,  CourseDetailsDetails);
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
                    progressBar.setVisibility(View.GONE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }
                        ArrayList<BalanceTrasactionList> item = new ArrayList<BalanceTrasactionList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new AttendanceDetailList();
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

                                    String reg_date= Utility.formatDate(RegistrationDate);
                                    String dur_sess="Duration:"+Duration_Days+","+"Session:"+Session;
                                    contactTV.setText(Contact);
                                    nameTV.setText(name);
                                    String fpaid="₹ "+Final_paid;
                                    String ttl="₹ "+Rate;
                                    rateTV.setText(ttl);
                                    regdateTV.setText(reg_date);
                                    packagenameTV.setText(Package_Name);
                                    durationTv.setText(dur_sess);

                                    paidTV.setText(fpaid);
                                    executiveNameTV.setText(ExecutiveName);
                                    balanceTV.setText(Final_Balance);
                                    invoice_id=Invoice_ID;
                                    invoice_idTV.setText(invoice_id);
                                    member_id=Member_ID;
                                    String domainurl= SharedPrefereneceUtil.getDomainUrl(CourseWiseAttendanceDetailsActivity.this);
                                    String url= domainurl+ServiceUrls.IMAGES_URL + Image;

                                    // Glide.with(this).load(url).placeholder(R.drawable.nouser).into(imageView);
                                    RequestOptions requestOptions = new RequestOptions();
                                    requestOptions.placeholder(R.drawable.nouser);
                                    requestOptions.error(R.drawable.nouser);


                                    Glide.with(this)
                                            .setDefaultRequestOptions(requestOptions)
                                            .load(url).into(imageView);

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
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CourseWiseAttendanceDetailsActivity.this);
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

    private void attendanceclass() {
        CourseWiseAttendanceDetailsActivity.AttendanceTrackclass ru = new CourseWiseAttendanceDetailsActivity.AttendanceTrackclass();
        ru.execute("5");
    }

    class AttendanceTrackclass extends AsyncTask<String, Void, String> {


        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
            // showProgressDialog();
            viewDialog.showDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: show_balance_trasaction_details = %s", response));
            //  dismissProgressDialog();
            viewDialog.hideDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            AttendanceDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> AttendanceDetails = new HashMap<String, String>();
            AttendanceDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(CourseWiseAttendanceDetailsActivity.this));
            AttendanceDetails.put("member_id",member_id );
            AttendanceDetails.put("invoice_id",invoice_id );
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(CourseWiseAttendanceDetailsActivity.this)));
            Log.v(TAG, String.format("doInBackground :: member_id = %s", member_id));
            Log.v(TAG, String.format("doInBackground :: invoice_id = %s", invoice_id));
            AttendanceDetails.put("action","show_attendance");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(CourseWiseAttendanceDetailsActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, AttendanceDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }

    private void AttendanceDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
//        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayoutPrabhagDetails);
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    progressBar.setVisibility(View.GONE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }
                        ArrayList<AttendanceDetailList> item = new ArrayList<AttendanceDetailList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new AttendanceDetailList();
                                Log.d(TAG, "i: " + i);

                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Name = jsonObj.getString("Name");
                                    String Contact = jsonObj.getString("Contact");
                                    String PackageName = jsonObj.getString("PackageName");
                                    String InDateTime = jsonObj.getString("InDateTime");
                                    String AttendanceDate = jsonObj.getString("AttendanceDate");
                                    String MemberID = jsonObj.getString("MemberID");
                                    String Remaining_Session = jsonObj.getString("Remaining_Session");
                                    String Start_Date = jsonObj.getString("Start_Date");
                                    String End_Date = jsonObj.getString("End_Date");
                                    String Attendance_Mode = jsonObj.getString("Attendance_Mode");
                                    String[] timearr=InDateTime.split(" ");


                                    SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm:ss");
                                    SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
                                    Date _24HourDt = null;
                                    try {
                                        if(timearr[1]!=null)
                                            _24HourDt = _24HourSDF.parse(timearr[1]);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    System.out.println(_24HourDt);
                                    System.out.println(_12HourSDF.format(_24HourDt));
                                    String format12=_12HourSDF.format(_24HourDt);
                                    subList.setTime(format12);
                                    String adate=Utility.formatDate(AttendanceDate);
                                    subList.setAttendanceDate(adate);
                                    subList.setAttendanceMode(Attendance_Mode);

                                    item.add(subList);
                                    adapter = new AttendanceDetailsAdapter( item,CourseWiseAttendanceDetailsActivity.this);
                                    recyclerView.setAdapter(adapter);

                                }
                            }
                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    nodata.setVisibility(View.VISIBLE);
                    // recyclerView.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CourseWiseAttendanceDetailsActivity.this);
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
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        //dismissProgressDialog();
        finish();
    }
}
