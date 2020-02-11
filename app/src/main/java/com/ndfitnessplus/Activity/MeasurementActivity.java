package com.ndfitnessplus.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.ndfitnessplus.Adapter.CourseAdapter;
import com.ndfitnessplus.Adapter.EnquiryAdapter;
import com.ndfitnessplus.Adapter.MeasurementAdapter;
import com.ndfitnessplus.Listeners.PaginationScrollListener;
import com.ndfitnessplus.Model.CourseList;
import com.ndfitnessplus.Model.EnquiryList;
import com.ndfitnessplus.Model.MeasurementList;
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

public class MeasurementActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    MeasurementAdapter adapter;

    MeasurementList subList;
    SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    ProgressBar progressBar;

    View nodata,frame;
    //No internet connectioin
    View noInternet;
    private ProgressBar progress_bar;
    private LinearLayout lyt_no_connection;

    public static String TAG = CourseActivity.class.getName();
    private ProgressDialog pd;

    //paginnation parameters
    public static final int PAGE_START = 1;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 2;
    private boolean isLoading = false;
    int itemCount = 0;
    int offset = 0;

    //search
    private EditText inputsearch;
    ImageView search;
    TextView total_measurement;
    //Loading gif
    ViewDialog viewDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_measurement);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.exi_measurement));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){
        FloatingActionButton addenquiry=findViewById(R.id.fab);
        progressBar=findViewById(R.id.progressBar);
        swipeRefresh=findViewById(R.id.swipeRefresh);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        viewDialog = new ViewDialog(this);

        total_measurement=findViewById(R.id.ttl_measurement);
        nodata=findViewById(R.id.nodata);
        frame=findViewById(R.id.main_frame);
        noInternet=findViewById(R.id.no_internet);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        lyt_no_connection = (LinearLayout) findViewById(R.id.lyt_no_connection);
        swipeRefresh.setOnRefreshListener(this);
        progress_bar.setVisibility(View.GONE);
        lyt_no_connection.setVisibility(View.VISIBLE);
//        adapter = new CourseAdapter( new ArrayList<EnquiryList>(),CourseActivity.this);
//        recyclerView.setAdapter(adapter);


        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        if (args != null) {
            ArrayList<MeasurementList> filterArrayList = (ArrayList<MeasurementList>) args.getSerializable("filter_array_list");
            progressBar.setVisibility(View.GONE);
            int length=filterArrayList.size();
            total_measurement.setText(String.valueOf(length));
            adapter = new MeasurementAdapter( MeasurementActivity.this,filterArrayList);
            recyclerView.setAdapter(adapter);
        }else{
            if (isOnline(MeasurementActivity.this)) {
               measurementclass();// check login details are valid or not from server
            }
            else {
                frame.setVisibility(View.GONE);
                noInternet.setVisibility(View.VISIBLE);
                lyt_no_connection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        progress_bar.setVisibility(View.VISIBLE);
                        lyt_no_connection.setVisibility(View.GONE);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progress_bar.setVisibility(View.GONE);
                                lyt_no_connection.setVisibility(View.VISIBLE);
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                                overridePendingTransition(0, 0);
                                moveTaskToBack(false);

                            }
                        }, 1000);
                    }
                });
                //Toast.makeText(CourseActivity.this, R.string.internet_unavailable, Toast.LENGTH_LONG).show();
//                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CourseActivity.this);
//                builder.setMessage(R.string.internet_unavailable);
//                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.dismiss();
//                    }
//                });
//                android.app.AlertDialog dialog = builder.create();
//                dialog.setCancelable(false);
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                dialog.show();

            }
        }



        addenquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MeasurementActivity.this,AddMeasurementActivity.class);
                startActivity(intent);
            }
        });

        inputsearch=(EditText)findViewById(R.id.inputsearchid);
        search=findViewById(R.id.search);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputsearch.getText().length()>0){
                    measurementsearchclass();
                }else{
                    Toast.makeText(MeasurementActivity.this,"Please enter text to search", Toast.LENGTH_LONG).show();
                }

            }
        });

        inputsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(final Editable arg0) {
                // TODO Auto-generated method stub
                if (MeasurementActivity.this.adapter == null){
                    // some print statement saying it is null
//                   // Toast toast = Toast.makeText(MeasurementActivity.this,"no record found", Toast.LENGTH_SHORT);
//                    toast.setGravity(Gravity.CENTER, 0, 0);
//                    toast.show();
                }
                else
                {
                    isLoading = false;
                    int count=MeasurementActivity.this.adapter.filter(String.valueOf(arg0));

                    total_measurement.setText(String.valueOf(count));

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
                    measurementclass();
                }

            }
        });
        /**
         * add scroll listener while user reach in bottom load more will call
         */
        recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;

                Log.d(TAG, "prepare called current item: " + currentPage+"Total page"+totalPage);
                if(currentPage<=totalPage){
                    //currentPage = PAGE_START;
                    Log.d(TAG, "currentPage: " + currentPage);
                    isLastPage = false;
                   // preparedListItem();
                }


            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    @Override
    public void onRefresh() {
         onRestart();
    }
    // Asycc class for loading data for database
    private void measurementclass() {
        MeasurementActivity.MeasurementTrackclass ru = new MeasurementActivity.MeasurementTrackclass();
        ru.execute("5");
    }
    class MeasurementTrackclass extends AsyncTask<String, Void, String> {

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
            MeasurementDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> MeasurementDetails = new HashMap<String, String>();
            MeasurementDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(MeasurementActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(MeasurementActivity.this)));
            MeasurementDetails.put("action","show_measurement");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(MeasurementActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, MeasurementDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }

    }

    private void MeasurementDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
//        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayoutPrabhagDetails);
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));

                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
//                    nodata.setVisibility(View.GONE);
//                    swipeRefresh.setVisibility(View.VISIBLE);
                    String ttl_enq = object.getString("total_measurement_count");
                    total_measurement.setText(ttl_enq);
                    progressBar.setVisibility(View.GONE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }

                        ArrayList<MeasurementList> item = new ArrayList<MeasurementList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new MeasurementList();
                                Log.d(TAG, "i: " + i);

                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Measurement_Date = jsonObj.getString("Measurement_Date");
                                    String Member_ID = jsonObj.getString("Member_ID");
                                    String MemberContact = jsonObj.getString("MemberContact");
                                    String MemberName = jsonObj.getString("MemberName");
                                    String Weight = jsonObj.getString("Weight");
                                    String Height = jsonObj.getString("Height");
                                    String Age = jsonObj.getString("Age");
                                    String BMI = jsonObj.getString("BMI");
                                    String Fat = jsonObj.getString("Fat");
                                    String Neck = jsonObj.getString("Neck");
                                    String Shoulder = jsonObj.getString("Shoulder");
                                    String Chest = jsonObj.getString("Chest");
                                    String Arms_R = jsonObj.getString("Arms_R");
                                    String Arms_L = jsonObj.getString("Arms_L");
                                    String ForArms = jsonObj.getString("ForArms");
                                    String Waist = jsonObj.getString("Waist");
                                    String Hips = jsonObj.getString("Hips");
                                    String Thigh_R = jsonObj.getString("Thigh_R");
                                    String Thigh_L = jsonObj.getString("Thigh_L");
                                    String Calf_R = jsonObj.getString("Calf_R");
                                    String Calf_L = jsonObj.getString("Calf_L");
                                    String NextFollowupDate = jsonObj.getString("NextFollowupDate");
                                    String Executive_Name = jsonObj.getString("Executive_Name");


                                    //  for (int j = 0; j < 5; j++) {

                                    String mdate=Utility.formatDate(Measurement_Date);
                                    subList.setMeasurement_Date(mdate);
                                    subList.setMemberId(Member_ID);
                                    String cont=Utility.lastFour(MemberContact);
                                    subList.setContact(MemberContact);
                                    subList.setContactEncrypt(cont);
//                                    String name = MemberName.substring(0,1).toUpperCase() + MemberName.substring(1);
                                    subList.setName(MemberName);
                                    subList.setWeight(Weight);
                                    subList.setHeight(Height);
                                    subList.setAge(Age);
                                    subList.setBMI(BMI);
                                    subList.setFat(Fat);
                                    subList.setNeck(Neck);
                                    subList.setShoulder(Shoulder);
                                    subList.setChest(Chest);
                                    subList.setArms_R(Arms_R);
                                    subList.setArms_L(Arms_L);
                                    subList.setForArms(ForArms);
                                    subList.setWaist(Waist);
                                    subList.setHips(Hips);
                                    subList.setThigh_R(Thigh_R);
                                    subList.setThigh_L(Thigh_L);
                                    subList.setCalf_R(Calf_R);
                                    subList.setCalf_L(Calf_L);
//                                    subList.setNextFollowupDate(NextFollowupDate);
//                                    subList.setExecutive_Name(Executive_Name);
                                    if(!NextFollowupDate.equals("null")){
                                        Log.v(TAG, "Next Followup date is not null");
                                        String nextdate= Utility.formatDate(NextFollowupDate);
                                        String on="Your Next Measurement Date is "+nextdate;
                                        subList.setNextFollowupDate(on);
                                    }
                                    String takenby="Taken By:"+Executive_Name;
                                    subList.setExecutive_Name(takenby);
                                    //Toast.makeText(MeasurementActivity.this, "followup date: "+next_foll_date, Toast.LENGTH_SHORT).show();

                                    //Toast.makeText(MainActivity.this, "j "+j, Toast.LENGTH_SHORT).show();
                                    item.add(subList);
                                    adapter = new MeasurementAdapter(MeasurementActivity.this, item);
                                    recyclerView.setAdapter(adapter);

                                }
                            }
                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    nodata.setVisibility(View.VISIBLE);
                    swipeRefresh.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MeasurementActivity.this);
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
    private void measurementsearchclass() {
        MeasurementActivity.MeasurementSearchTrackclass ru = new MeasurementActivity.MeasurementSearchTrackclass();
        ru.execute("5");
    }
    class MeasurementSearchTrackclass extends AsyncTask<String, Void, String> {

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
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            // dismissProgressDialog();
            viewDialog.hideDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            MeasurementSearchDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> MeasurementSearchDetails = new HashMap<String, String>();
            MeasurementSearchDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(MeasurementActivity.this));
            MeasurementSearchDetails.put("text", inputsearch.getText().toString());
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(MeasurementActivity.this)));
            MeasurementSearchDetails.put("action","show_search_measurement");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(MeasurementActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, MeasurementSearchDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }

    private void MeasurementSearchDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
//        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayoutPrabhagDetails);
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    nodata.setVisibility(View.GONE);
                    swipeRefresh.setVisibility(View.VISIBLE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }
                        String ttl_enq = String.valueOf(jsonArrayResult.length());
                        total_measurement.setText(ttl_enq);
                        final   ArrayList<MeasurementList> subListArrayList = new ArrayList<MeasurementList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new MeasurementList();
                                Log.d(TAG, "i: " + i);
                                // Log.d(TAG, "run: " + itemCount);
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Measurement_Date = jsonObj.getString("Measurement_Date");
                                    String Member_ID = jsonObj.getString("Member_ID");
                                    String MemberContact = jsonObj.getString("MemberContact");
                                    String MemberName = jsonObj.getString("MemberName");
                                    String Weight = jsonObj.getString("Weight");
                                    String Height = jsonObj.getString("Height");
                                    String Age = jsonObj.getString("Age");
                                    String BMI = jsonObj.getString("BMI");
                                    String Fat = jsonObj.getString("Fat");
                                    String Neck = jsonObj.getString("Neck");
                                    String Shoulder = jsonObj.getString("Shoulder");
                                    String Chest = jsonObj.getString("Chest");
                                    String Arms_R = jsonObj.getString("Arms_R");
                                    String Arms_L = jsonObj.getString("Arms_L");
                                    String ForArms = jsonObj.getString("ForArms");
                                    String Waist = jsonObj.getString("Waist");
                                    String Hips = jsonObj.getString("Hips");
                                    String Thigh_R = jsonObj.getString("Thigh_R");
                                    String Thigh_L = jsonObj.getString("Thigh_L");
                                    String Calf_R = jsonObj.getString("Calf_R");
                                    String Calf_L = jsonObj.getString("Calf_L");
                                    String NextFollowupDate = jsonObj.getString("NextFollowupDate");
                                    String Executive_Name = jsonObj.getString("Executive_Name");


                                    //  for (int j = 0; j < 5; j++) {

                                    String mdate=Utility.formatDate(Measurement_Date);
                                    subList.setMeasurement_Date(mdate);
                                    subList.setMemberId(Member_ID);
                                    String cont=Utility.lastFour(MemberContact);
                                    subList.setContact(MemberContact);
                                    subList.setContactEncrypt(cont);
                                    String name = MemberName.substring(0,1).toUpperCase() + MemberName.substring(1);
                                    subList.setName(name);
                                    subList.setWeight(Weight);
                                    subList.setHeight(Height);
                                    subList.setAge(Age);
                                    subList.setBMI(BMI);
                                    subList.setFat(Fat);
                                    subList.setNeck(Neck);
                                    subList.setShoulder(Shoulder);
                                    subList.setChest(Chest);
                                    subList.setArms_R(Arms_R);
                                    subList.setArms_L(Arms_L);
                                    subList.setForArms(ForArms);
                                    subList.setWaist(Waist);
                                    subList.setHips(Hips);
                                    subList.setThigh_R(Thigh_R);
                                    subList.setThigh_L(Thigh_L);
                                    subList.setCalf_R(Calf_R);
                                    subList.setCalf_L(Calf_L);
//                                    subList.setNextFollowupDate(NextFollowupDate);
//                                    subList.setExecutive_Name(Executive_Name);
                                    if(NextFollowupDate ==null){

                                    }else{
                                        String nextdate= Utility.formatDate(NextFollowupDate);
                                        String on="Your Next Measurement Date is "+nextdate;
                                        subList.setNextFollowupDate(on);
                                    }


                                    String takenby="Taken By:"+Executive_Name;
                                    subList.setExecutive_Name(takenby);


                                    subListArrayList.add(subList);

                                    adapter = new MeasurementAdapter( MeasurementActivity.this,subListArrayList);
                                    recyclerView.setAdapter(adapter);

                                }
                            }

                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    // nodata.setVisibility(View.VISIBLE);
//                    nodata.setVisibility(View.VISIBLE);
//                    swipeRefresh.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MeasurementActivity.this, "NO Record Found", Toast.LENGTH_SHORT).show();
                    //frame.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
                recyclerView.setVisibility(View.GONE);
                frame.setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter_action_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_home) {
            Intent intent = new Intent(MeasurementActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_filter) {
            Intent intent = new Intent(MeasurementActivity.this, MeasurementFilterActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        swipeRefresh.setRefreshing(false);
        Intent intent=new Intent(MeasurementActivity.this,MeasurementActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(MeasurementActivity.this, MainActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(MeasurementActivity.this,MainActivity.class);
        startActivity(intent);
    }
}
