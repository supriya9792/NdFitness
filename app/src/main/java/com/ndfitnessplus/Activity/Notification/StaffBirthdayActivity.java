package com.ndfitnessplus.Activity.Notification;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ndfitnessplus.Activity.LoginActivity;
import com.ndfitnessplus.Activity.MainActivity;
import com.ndfitnessplus.Activity.NotificationActivity;
import com.ndfitnessplus.Adapter.MemberAdapter;
import com.ndfitnessplus.Adapter.StaffBirthdayAdapter;
import com.ndfitnessplus.Model.MemberDataList;
import com.ndfitnessplus.Model.StaffBirthdayList;
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

public class StaffBirthdayActivity extends AppCompatActivity {
    public static String TAG = StaffBirthdayActivity.class.getName();
    private ProgressDialog pd;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    ProgressBar progressBar;
    FrameLayout mainframe;
    View nodata;
    StaffBirthdayAdapter adapter;
    ArrayList<StaffBirthdayList> subListArrayList = new ArrayList<StaffBirthdayList>();
    StaffBirthdayList subList;
    //Loading gif
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_birthday);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.staff_bday));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){
        progressBar=findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        mainframe=findViewById(R.id.main_frame);
        nodata=findViewById(R.id.nodata);
        viewDialog = new ViewDialog(this);
//        adapter = new EnquiryAdapter( new ArrayList<EnquiryList>(),EnquiryActivity.this);
//        recyclerView.setAdapter(adapter);


        if (isOnline(StaffBirthdayActivity.this)) {
            staffBirthdayclass();// check login details are valid or not from server
        }
        else {
            Toast.makeText(StaffBirthdayActivity.this, R.string.internet_unavailable, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(StaffBirthdayActivity.this);
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
        switch (item.getItemId()) {
            case R.id.action_home:
                Intent intent = new Intent(StaffBirthdayActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showProgressDialog() {
        Log.v(TAG, String.format("showProgressDialog"));
        pd = new ProgressDialog(StaffBirthdayActivity.this);
        pd.setMessage("loading");
        pd.setCancelable(false);
        pd.show();
    }

    /**
     * Dismiss Progress Dialog.
     */
    private void dismissProgressDialog() {
        Log.v(TAG, String.format("dismissProgressDialog"));

        pd.cancel();
    }
    //*********** Asycc class for loading data for database **************
    private void staffBirthdayclass() {
        StaffBirthdayActivity.  StaffBirthdayTrackclass ru = new StaffBirthdayActivity.  StaffBirthdayTrackclass();
        ru.execute("5");
    }

    class   StaffBirthdayTrackclass extends AsyncTask<String, Void, String> {


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
            Log.v(TAG, String.format("onPostExecute :: show_balance_trasaction_details = %s", response));
          //  dismissProgressDialog();
            viewDialog.hideDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
             StaffBirthdayDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String>  StaffBirthdayDetails = new HashMap<String, String>();
             StaffBirthdayDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(StaffBirthdayActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(StaffBirthdayActivity.this)));
             StaffBirthdayDetails.put("action","show_staffs_birthdays");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(StaffBirthdayActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL,   StaffBirthdayDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }

    private void   StaffBirthdayDetails(String jsonResponse) {

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
                        ArrayList<StaffBirthdayList> item = new ArrayList<StaffBirthdayList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new StaffBirthdayList();
                                Log.d(TAG, "i: " + i);

                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String name = jsonObj.getString("Name");
                                    String gender = jsonObj.getString("Gender");
                                    String Contact = jsonObj.getString("Contact");
                                    String DOB = jsonObj.getString("DOB");
                                    String Address = jsonObj.getString("Address");
                                    String Staff_Id = jsonObj.getString("Staff_Id");
                                    String Designation = jsonObj.getString("Designation");
                                    String Date = jsonObj.getString("Date");


                                    //  for (int j = 0; j < 5; j++) {

                                    subList.setName(name);
                                    subList.setGender(gender);
                                    String cont[]=Contact.split(",");
                                    subList.setContact(cont[0]);
                                    String dob= Utility.formatDate(DOB);
                                    subList.setBirthDate(dob);
                                    subList.setAddress(Address);
                                    subList.setID(Staff_Id);
                                    subList.setDesignation(Designation);
                                    String date=Utility.formatDate(Date);
                                    subList.setJoiningDate(date);





                                    item.add(subList);
                                    adapter = new StaffBirthdayAdapter( item,StaffBirthdayActivity.this);
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
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(StaffBirthdayActivity.this);
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
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //swipeRefresh.setRefreshing(false);
        Intent intent=new Intent(StaffBirthdayActivity.this,StaffBirthdayActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(StaffBirthdayActivity.this, NotificationActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(StaffBirthdayActivity.this,NotificationActivity.class);
        startActivity(intent);
    }
}
