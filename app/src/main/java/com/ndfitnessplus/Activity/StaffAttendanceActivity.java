package com.ndfitnessplus.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
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

import com.ndfitnessplus.Adapter.AttendanceAdapter;
import com.ndfitnessplus.Adapter.StaffAttendanceAdapter;
import com.ndfitnessplus.Listeners.PaginationScrollListener;
import com.ndfitnessplus.Model.StaffAttendanceList;
import com.ndfitnessplus.Model.StaffAttendanceList;
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

import static com.ndfitnessplus.Utility.HTTPRequestQueue.isOnline;

public class StaffAttendanceActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    StaffAttendanceAdapter adapter;

    StaffAttendanceList subList;
    SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    ProgressBar progressBar;

    View nodata,frame;
    //No internet connectioin
    View noInternet;
    private ProgressBar progress_bar;
    private LinearLayout lyt_no_connection;

    public static String TAG = StaffAttendanceActivity.class.getName();
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
    TextView total_present;
    FloatingActionButton addStaffAtt;
    //Loading gif
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_staff_attendance);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.attendance));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){

        progressBar=findViewById(R.id.progressBar);
        swipeRefresh=findViewById(R.id.swipeRefresh);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        viewDialog = new ViewDialog(this);

        total_present=findViewById(R.id.ttl_present);
        nodata=findViewById(R.id.nodata);
        frame=findViewById(R.id.main_frame);
        noInternet=findViewById(R.id.no_internet);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        lyt_no_connection = (LinearLayout) findViewById(R.id.lyt_no_connection);
        swipeRefresh.setOnRefreshListener(this);
        progress_bar.setVisibility(View.GONE);
        lyt_no_connection.setVisibility(View.VISIBLE);
        addStaffAtt=findViewById(R.id.fab);
//        adapter = new CourseAdapter( new ArrayList<EnquiryList>(),CourseActivity.this);
//        recyclerView.setAdapter(adapter);


        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        if (args != null) {
            ArrayList<StaffAttendanceList> filterArrayList = (ArrayList<StaffAttendanceList>) args.getSerializable("filter_array_list");
            progressBar.setVisibility(View.GONE);
            int length=filterArrayList.size();
            total_present.setText(String.valueOf(length));
            adapter = new StaffAttendanceAdapter( StaffAttendanceActivity.this,filterArrayList);
            recyclerView.setAdapter(adapter);
        }else{
            if (isOnline(StaffAttendanceActivity.this)) {
                staffAttendanceclass();// check login details are valid or not from server
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
            }
        }


        inputsearch=(EditText)findViewById(R.id.inputsearchid);
        search=findViewById(R.id.search);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputsearch.getText().length()>0){
                    staffAttendancesearchclass();
                }else{
                    Toast.makeText(StaffAttendanceActivity.this,"Please enter text to search", Toast.LENGTH_LONG).show();
                }

            }
        });

        inputsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(final Editable arg0) {
                // TODO Auto-generated method stub
                if (StaffAttendanceActivity.this.adapter == null){
                    // some print statement saying it is null
//                   // Toast toast = Toast.makeText(StaffAttendanceActivity.this,"no record found", Toast.LENGTH_SHORT);
//                    toast.setGravity(Gravity.CENTER, 0, 0);
//                    toast.show();
                }
                else
                {
                    isLoading = false;
                    int count=StaffAttendanceActivity.this.adapter.filter(String.valueOf(arg0));

                    total_present.setText(String.valueOf(count));

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
                    staffAttendanceclass();
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

        addStaffAtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(StaffAttendanceActivity.this,AddStaffAttendanceActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh() {
        onRestart();
    }
    // Asycc class for loading data for database
    private void staffAttendanceclass() {
        StaffAttendanceActivity.StaffAttendanceTrackclass ru = new StaffAttendanceActivity.StaffAttendanceTrackclass();
        ru.execute("5");
    }
    class StaffAttendanceTrackclass extends AsyncTask<String, Void, String> {

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
            StaffAttendanceDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> StaffAttendanceDetails = new HashMap<String, String>();
            StaffAttendanceDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(StaffAttendanceActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(StaffAttendanceActivity.this)));
            StaffAttendanceDetails.put("action","show_staff_attendance_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(StaffAttendanceActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, StaffAttendanceDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }

    }

    private void StaffAttendanceDetails(String jsonResponse) {

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
                    String ttl_enq = object.getString("total_attendance_count");
                    total_present.setText(ttl_enq);
                    progressBar.setVisibility(View.GONE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }

                        ArrayList<StaffAttendanceList> item = new ArrayList<StaffAttendanceList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new StaffAttendanceList();
                                //  Log.d(TAG, "i: " + i);

                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Staff_ID = jsonObj.getString("Staff_ID");
                                    String Contact = jsonObj.getString("Contact");
                                    String Name = jsonObj.getString("Name");
                                    String InDateTime = jsonObj.getString("InTime");
                                    String OutTime = jsonObj.getString("OutTime");
                                    String Attendance_Date = jsonObj.getString("Attendance_Date");
                                    String AttendanceMode = jsonObj.getString("AttendanceMode");
                                    String Image = jsonObj.getString("Image");

                                    subList.setStaffID(Staff_ID);
                                    subList.setContact(Contact);
                                    String cont= Utility.lastFour(Contact);
                                    //subList.setContactEncrypt(cont);
                                    subList.setName(Name);
                                    String[] timearr=InDateTime.split(" ");
                                    String outtimearr[] = new String[0];



                                    SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm:ss");
                                    SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
                                    Date _24HourDt = null;
                                    Date _24HourDtOut = null;

                                    try {

                                        if(timearr[1]!=null)
                                            _24HourDt = _24HourSDF.parse(timearr[1]);
                                        if(!OutTime.equals("null")){
                                            outtimearr=OutTime.split(" ");
                                            if(outtimearr[1]!=null) {
                                                _24HourDtOut = _24HourSDF.parse(outtimearr[1]);
                                                String format12Out = _12HourSDF.format(_24HourDtOut);
                                                subList.setOutTime(format12Out);
                                            }
                                        }

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    System.out.println(_24HourDt);
                                    System.out.println(_12HourSDF.format(_24HourDt));
                                    String format12=_12HourSDF.format(_24HourDt);

                                    subList.setInTime(format12);

                                    String adate=Utility.formatDate(Attendance_Date);
                                    subList.setAttendanceDate(adate);
                                    subList.setAttendanceMode(AttendanceMode);
                                    subList.setImage(Image);

                                    item.add(subList);

                                    adapter = new StaffAttendanceAdapter(StaffAttendanceActivity.this, item);
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
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(StaffAttendanceActivity.this);
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
    private void staffAttendancesearchclass() {
        StaffAttendanceActivity.StaffAttendanceSearchTrackclass ru = new StaffAttendanceActivity.StaffAttendanceSearchTrackclass();
        ru.execute("5");
    }
    class StaffAttendanceSearchTrackclass extends AsyncTask<String, Void, String> {

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
            StaffAttendanceSearchDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> StaffAttendanceSearchDetails = new HashMap<String, String>();
            StaffAttendanceSearchDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(StaffAttendanceActivity.this));
            StaffAttendanceSearchDetails.put("text", inputsearch.getText().toString());
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(StaffAttendanceActivity.this)));
            StaffAttendanceSearchDetails.put("action","show_search_staff_attendance");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(StaffAttendanceActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, StaffAttendanceSearchDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }
    }

    private void StaffAttendanceSearchDetails(String jsonResponse) {

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
                        total_present.setText(ttl_enq);
                        final   ArrayList<StaffAttendanceList> subListArrayList = new ArrayList<StaffAttendanceList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new StaffAttendanceList();
                                Log.d(TAG, "i: " + i);
                                // Log.d(TAG, "run: " + itemCount);
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Staff_ID = jsonObj.getString("Staff_ID");
                                    String Contact = jsonObj.getString("Contact");
                                    String Name = jsonObj.getString("Name");
                                    String InDateTime = jsonObj.getString("InTime");
                                    String OutTime = jsonObj.getString("OutTime");
                                    String Attendance_Date = jsonObj.getString("Attendance_Date");
                                    String AttendanceMode = jsonObj.getString("AttendanceMode");
                                    String Image = jsonObj.getString("Image");

                                    subList.setStaffID(Staff_ID);
                                    subList.setContact(Contact);
                                    String cont= Utility.lastFour(Contact);
                                    //subList.setContactEncrypt(cont);
                                    subList.setName(Name);
                                    String[] timearr=InDateTime.split(" ");
                                    String outtimearr[] = new String[0];



                                    SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm:ss");
                                    SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
                                    Date _24HourDt = null;
                                    Date _24HourDtOut = null;

                                    try {

                                        if(timearr[1]!=null)
                                            _24HourDt = _24HourSDF.parse(timearr[1]);
                                        if(!OutTime.equals("null")){
                                            outtimearr=OutTime.split(" ");
                                            if(outtimearr[1]!=null) {
                                                _24HourDtOut = _24HourSDF.parse(outtimearr[1]);
                                                String format12Out = _12HourSDF.format(_24HourDtOut);
                                                subList.setOutTime(format12Out);
                                            }
                                        }

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    System.out.println(_24HourDt);
                                    System.out.println(_12HourSDF.format(_24HourDt));
                                    String format12=_12HourSDF.format(_24HourDt);

                                    subList.setInTime(format12);
                                    String adate=Utility.formatDate(Attendance_Date);
                                    subList.setAttendanceDate(adate);
                                    subList.setAttendanceMode(AttendanceMode);
                                    subList.setImage(Image);
                                    //  for (int j = 0; j < 5; j++) {

                                    subListArrayList.add(subList);


                                    adapter = new StaffAttendanceAdapter( StaffAttendanceActivity.this,subListArrayList);
                                    recyclerView.setAdapter(adapter);

                                }
                            }

                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    // nodata.setVisibility(View.VISIBLE);
                     nodata.setVisibility(View.VISIBLE);
                    swipeRefresh.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(StaffAttendanceActivity.this, "NO Record Found", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(StaffAttendanceActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_filter) {
            Intent intent = new Intent(StaffAttendanceActivity.this, StaffAttendanceFilterActivity.class);
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
        Intent intent=new Intent(StaffAttendanceActivity.this,StaffAttendanceActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(StaffAttendanceActivity.this, MainActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(StaffAttendanceActivity.this,MainActivity.class);
        startActivity(intent);
    }
}



