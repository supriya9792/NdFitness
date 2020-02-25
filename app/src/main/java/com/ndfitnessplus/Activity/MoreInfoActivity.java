package com.ndfitnessplus.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ndfitnessplus.Activity.Notification.TodaysEnrollmentActivity;
import com.ndfitnessplus.Adapter.FollowupDetailsAdapter;
import com.ndfitnessplus.Model.FollowupList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.Utility;
import com.ndfitnessplus.Utility.ViewDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.ndfitnessplus.Utility.HTTPRequestQueue.isOnline;

public class MoreInfoActivity extends AppCompatActivity {
    TextView username,mobilenumber,Birthday,Email,BloodGroup,Gender,Occupation,Budget,EnquiryFor,EnquirySource,EnquiryType,Address;
    CircularImageView imageView;
    View nodata;
    public static String TAG = MoreInfoActivity.class.getName();
    private ProgressDialog pd;

    String enquiry_id;
    //Loading gif
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_more_info);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.enquiry_followup_more_dtl));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){
        imageView=findViewById(R.id.image);

        username=findViewById(R.id.username);
        mobilenumber=findViewById(R.id.mobilenumber);
        Birthday=findViewById(R.id.birthday);
        Email=findViewById(R.id.email);
        BloodGroup=findViewById(R.id.bloodgroup);
        Gender=findViewById(R.id.gender);
        Occupation=findViewById(R.id.occupation);
        Budget=findViewById(R.id.budget);
        EnquiryFor=findViewById(R.id.enq_for);
        EnquirySource=findViewById(R.id.enq_src);
        EnquiryType=findViewById(R.id.enquiry_type);
        Address=findViewById(R.id.address);
        nodata=findViewById(R.id.nodata);
        viewDialog = new ViewDialog(this);

        Intent intent = getIntent();
        if (intent != null) {
            enquiry_id=intent.getStringExtra("enquiry_id");

        }

        if (isOnline(MoreInfoActivity.this)) {
            followupclass();// check login details are valid or not from server
        }
        else {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MoreInfoActivity.this);
            builder.setMessage(R.string.internet_unavailable);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_action_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_home) {
            Intent intent = new Intent(MoreInfoActivity.this, MainActivity.class);
            startActivity(intent);
        }else if(id== android.R.id.home){
            finish();
        }

        return true;
    }

    //*********** Asycc class for loading data for database **************
    private void followupclass() {
        MoreInfoActivity.FollowupTrackclass ru = new MoreInfoActivity.FollowupTrackclass();
        ru.execute("5");
    }

    class FollowupTrackclass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
            viewDialog.showDialog();


        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            viewDialog.hideDialog();
            FollowupDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> FollowupDetails = new HashMap<String, String>();
            FollowupDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(MoreInfoActivity.this));
            FollowupDetails.put("enquiry_id",enquiry_id );
            FollowupDetails.put("action","show_enquiry_followup_more_details");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(MoreInfoActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, FollowupDetails);
            return loginResult;
        }


    }

    private void FollowupDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {

                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");

                        int count=0;
                        ArrayList<FollowupList> item = new ArrayList<FollowupList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {

                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String name = jsonObj.getString("Name");
                                    String Rating = jsonObj.getString("Rating");
                                     String Contact = jsonObj.getString("Contact");
                                    String CallResponse = jsonObj.getString("CallResponse");
                                    String ExecutiveName = jsonObj.getString("ExecutiveName");
                                    String email = jsonObj.getString("Email");
                                    String FollowupType = jsonObj.getString("FollowupType");
                                    String gender = jsonObj.getString("Gender");
                                    String Enquiry_ID = jsonObj.getString("Enquiry_ID");
                                    String address = jsonObj.getString("Address");
                                    String Image = jsonObj.getString("Image");
                                    String dOB = jsonObj.getString("DOB");
                                    String blood_Group = jsonObj.getString("Blood_Group");
                                    String occupation = jsonObj.getString("Occupation");
                                    String Sourcesof_Enq = jsonObj.getString("Sourcesof_Enq");
                                    String enquiry_For = jsonObj.getString("Enquiry_For");
                                    String budget = jsonObj.getString("Budget");
                                    String enquiry_Type = jsonObj.getString("Enquiry_Type");


                                    String foll_date= Utility.formatDate(dOB);

                                    username.setText(name);
                                    mobilenumber.setText(Contact);
                                    Image.replace("\"", "");
                                    String domainurl= SharedPrefereneceUtil.getDomainUrl(MoreInfoActivity.this);
                                    String url= domainurl+ServiceUrls.IMAGES_URL + Image;
                                    RequestOptions requestOptions = new RequestOptions();
                                    requestOptions.placeholder(R.drawable.nouser);
                                    requestOptions.error(R.drawable.nouser);


                                    Glide.with(this)
                                            .setDefaultRequestOptions(requestOptions)
                                            .load(url).into(imageView);

                                    Email.setText(email);
                                    Gender.setText(gender);
                                    Address.setText(address);
                                    Birthday.setText(foll_date);
                                    BloodGroup.setText(blood_Group);
                                    Occupation.setText(occupation);
                                    EnquirySource.setText(Sourcesof_Enq);
                                    EnquiryFor.setText(enquiry_For);
                                    EnquiryType.setText(enquiry_Type);
                                    if(budget.equals(".00")){
                                        budget="0.00";
                                    }
                                    Budget.setText(budget);
                                }
                            }
                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    nodata.setVisibility(View.VISIBLE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MoreInfoActivity.this);
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
