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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ndfitnessplus.Activity.LoginActivity;
import com.ndfitnessplus.Activity.MainActivity;
import com.ndfitnessplus.Activity.NotificationActivity;
import com.ndfitnessplus.Adapter.BalanceReceiptAdapter;
import com.ndfitnessplus.Adapter.CourseAdapter;
import com.ndfitnessplus.Adapter.MemberAdapter;
import com.ndfitnessplus.Model.CourseList;
import com.ndfitnessplus.Model.MemberDataList;
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

public class MembershipEndDateActivity extends AppCompatActivity {
    public static String TAG = MembershipEndDateActivity.class.getName();
    private ProgressDialog pd;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    ProgressBar progressBar;
    FrameLayout mainframe;
    View nodata;
    BalanceReceiptAdapter adapter;
    ArrayList<CourseList> subListArrayList = new ArrayList<CourseList>();
    CourseList subList;
    //Loading gif
    ViewDialog viewDialog;

    //search
    private EditText inputsearch;
    ImageView search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership_end_date);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.member_end_date));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){
        progressBar=findViewById(R.id.progressBar);
        viewDialog = new ViewDialog(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        mainframe=findViewById(R.id.main_frame);
        nodata=findViewById(R.id.nodata);
        inputsearch=(EditText)findViewById(R.id.inputsearchid);
        search=findViewById(R.id.search);
//        adapter = new EnquiryAdapter( new ArrayList<EnquiryList>(),EnquiryActivity.this);
//        recyclerView.setAdapter(adapter);


        if (isOnline(MembershipEndDateActivity.this)) {
            todaysEnddateclass();// check login details are valid or not from server
        }
        else {
            Toast.makeText(MembershipEndDateActivity.this, R.string.internet_unavailable, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MembershipEndDateActivity.this);
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
        inputsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                if (MembershipEndDateActivity.this.adapter == null){
                    // some print statement saying it is null
                    Toast toast = Toast.makeText(MembershipEndDateActivity.this,"no record found", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else
                {
                    //isLoading = false;
                    MembershipEndDateActivity.this.adapter.filter(String.valueOf(arg0));

                }
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
                if(inputsearch.getText().length()==0) {
                    //do your work here
                    // Toast.makeText(AddEnquiryActivity.this ,"Text vhanged count  is 10 then: " , Toast.LENGTH_LONG).show();
                    todaysEnddateclass();
                }

            }
        });


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
                Intent intent = new Intent(MembershipEndDateActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showProgressDialog() {
        Log.v(TAG, String.format("showProgressDialog"));
        pd = new ProgressDialog(MembershipEndDateActivity.this);
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
    private void todaysEnddateclass() {
        MembershipEndDateActivity.  MembershipEndDateTrackclass ru = new MembershipEndDateActivity.  MembershipEndDateTrackclass();
        ru.execute("5");
    }

    class   MembershipEndDateTrackclass extends AsyncTask<String, Void, String> {


        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
          //  showProgressDialog();
            viewDialog.showDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: show_balance_trasaction_details = %s", response));
            //dismissProgressDialog();
            viewDialog.hideDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
             MembershipEndDateDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String>  MembershipEndDateDetails = new HashMap<String, String>();
             MembershipEndDateDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(MembershipEndDateActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(MembershipEndDateActivity.this)));
             MembershipEndDateDetails.put("action","show_todays_enddate");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(MembershipEndDateActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL,   MembershipEndDateDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }

    private void   MembershipEndDateDetails(String jsonResponse) {

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
                        ArrayList<CourseList> item = new ArrayList<CourseList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {

                                subList = new CourseList();
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
                                    String Start_Date = jsonObj.getString("Start_Date");
                                    String End_Date = jsonObj.getString("End_Date");
                                    String Rate = jsonObj.getString("Rate");
                                    String Final_paid = jsonObj.getString("Final_paid");
                                    String Final_Balance = jsonObj.getString("Final_Balance");
                                    String Image = jsonObj.getString("Image");
                                    String Invoice_ID = jsonObj.getString("Invoice_ID");
                                    String Tax = jsonObj.getString("Tax");
                                    String Member_Email_ID = jsonObj.getString("Member_Email_ID");
                                    String Financial_Year = jsonObj.getString("Financial_Year");

                                    //  for (int j = 0; j < 5; j++) {

                                    subList.setName(name);
                                    String sdate= Utility.formatDate(Start_Date);
                                    String edate=Utility.formatDate(End_Date);
                                    String todate=sdate+" to "+edate;
                                    subList.setStartToEndDate(todate);
                                    subList.setContact(Contact);
//                                    String pack=Package_Name;
                                    subList.setPackageName(Package_Name);
                                    subList.setExecutiveName(ExecutiveName);
                                    subList.setTax(Tax);
                                    String dur_sess="Duration:"+Duration_Days+","+"Session:"+Session;
                                    subList.setPackageNameWithDS(dur_sess);
                                    String reg_date= Utility.formatDate(RegistrationDate);
                                    subList.setRegistrationDate(reg_date);
                                    subList.setID(Member_ID);
                                    subList.setInvoiceID(Invoice_ID);

                                    subList.setRate(Rate);
                                    // String fpaid="₹ "+Final_paid;
                                    subList.setPaid(Final_paid);
                                    if(Final_Balance.equals(".00")){
                                        Final_Balance="0.00";
                                    }
                                    //String fbalance="₹ "+Final_Balance;
                                    subList.setBalance(Final_Balance);
                                    Image.replace("\"", "");
                                    subList.setImage(Image);
                                    subList.setEmail(Member_Email_ID);
                                    subList.setFinancialYear(Financial_Year);
                                    subList.setFollowuptype("Renew");

                                    //Toast.makeText(CourseActivity.this, "followup date: "+next_foll_date, Toast.LENGTH_SHORT).show();



                                    item.add(subList);
                                    adapter = new BalanceReceiptAdapter( item,MembershipEndDateActivity.this);
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
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MembershipEndDateActivity.this);
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
        Intent intent=new Intent(MembershipEndDateActivity.this,MembershipEndDateActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(MembershipEndDateActivity.this, NotificationActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(MembershipEndDateActivity.this,NotificationActivity.class);
        startActivity(intent);
    }
}
