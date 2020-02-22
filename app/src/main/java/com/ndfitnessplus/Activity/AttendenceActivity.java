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
import com.ndfitnessplus.Adapter.MeasurementAdapter;
import com.ndfitnessplus.Listeners.PaginationScrollListener;
import com.ndfitnessplus.Model.AttendanceList;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.ndfitnessplus.Utility.HTTPRequestQueue.isOnline;

public class AttendenceActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    AttendanceAdapter adapter;

    AttendanceList subList;
    SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    ProgressBar progressBar;

    View nodata,frame;
    //No internet connectioin
    View noInternet;
    private ProgressBar progress_bar;
    private LinearLayout lyt_no_connection;

    public static String TAG = AttendenceActivity.class.getName();
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
    //Loading gif
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_attendence);
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


        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        if (args != null) {
            ArrayList<AttendanceList> filterArrayList = (ArrayList<AttendanceList>) args.getSerializable("filter_array_list");
            progressBar.setVisibility(View.GONE);
            int length=filterArrayList.size();
            total_present.setText(String.valueOf(length));
            adapter = new AttendanceAdapter( AttendenceActivity.this,filterArrayList);
            recyclerView.setAdapter(adapter);
        }else{
            if (isOnline(AttendenceActivity.this)) {
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


            }
        }


        inputsearch=(EditText)findViewById(R.id.inputsearchid);
        search=findViewById(R.id.search);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputsearch.getText().length()>0){
                    measurementsearchclass();
                }else{
                    Toast.makeText(AttendenceActivity.this,"Please enter text to search", Toast.LENGTH_LONG).show();
                }

            }
        });

        inputsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(final Editable arg0) {
                // TODO Auto-generated method stub
                if (AttendenceActivity.this.adapter == null){
                    // some print statement saying it is null
                }
                else
                {
                    isLoading = false;
                    int count=AttendenceActivity.this.adapter.filter(String.valueOf(arg0));

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

                    Log.d(TAG, "currentPage: " + currentPage);
                    isLastPage = false;

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
        AttendenceActivity.MeasurementTrackclass ru = new AttendenceActivity.MeasurementTrackclass();
        ru.execute("5");
    }
class MeasurementTrackclass extends AsyncTask<String, Void, String> {

    ServerClass ruc = new ServerClass();


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.v(TAG, "onPreExecute");

    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        Log.v(TAG, String.format("onPostExecute :: response = %s", response));

        MeasurementDetails(response);

    }

    @Override
    protected String doInBackground(String... params) {

        HashMap<String, String> MeasurementDetails = new HashMap<String, String>();
        MeasurementDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(AttendenceActivity.this));
        Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(AttendenceActivity.this)));
        MeasurementDetails.put("action","show_all_attendance");
        String domainurl=SharedPrefereneceUtil.getDomainUrl(AttendenceActivity.this);
        String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, MeasurementDetails);
        return loginResult;
    }

}

    private void MeasurementDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {


            try {
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));

                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    String ttl_enq = object.getString("total_attendance_count");
                    total_present.setText(ttl_enq);
                    progressBar.setVisibility(View.GONE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");

                        ArrayList<AttendanceList> item = new ArrayList<AttendanceList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new AttendanceList();

                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String MemberID = jsonObj.getString("MemberID");
                                    String Contact = jsonObj.getString("Contact");
                                    String Name = jsonObj.getString("Name");
                                    String PackageName = jsonObj.getString("PackageName");
                                    String InDateTime = jsonObj.getString("InDateTime");
                                    String AttendanceDate = jsonObj.getString("AttendanceDate");
                                    String Remaining_Session = jsonObj.getString("Remaining_Session");
                                    String Start_Date = jsonObj.getString("Start_Date");
                                    String End_Date = jsonObj.getString("End_Date");
                                    String Attendance_Mode = jsonObj.getString("Attendance_Mode");
                                    String Image = jsonObj.getString("Image");
                                    String Status = jsonObj.getString("Status");

                                    subList.setMemberID(MemberID);
                                    subList.setContact(Contact);
                                    String cont=Utility.lastFour(Contact);
                                    subList.setContactEncrypt(cont);
                                    subList.setName(Name);
                                    subList.setPackageName(PackageName);
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
                                    String edate=Utility.formatDate(End_Date);
                                    subList.setExpiryDate(edate);
                                    subList.setAttendanceMode(Attendance_Mode);
                                    subList.setImage(Image);
                                    subList.setStatus(Status);

                                    item.add(subList);


                                    adapter = new AttendanceAdapter(AttendenceActivity.this, item);
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
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AttendenceActivity.this);
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
        AttendenceActivity.MeasurementSearchTrackclass ru = new AttendenceActivity.MeasurementSearchTrackclass();
        ru.execute("5");
    }
class MeasurementSearchTrackclass extends AsyncTask<String, Void, String> {

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
        MeasurementSearchDetails(response);

    }

    @Override
    protected String doInBackground(String... params) {

        HashMap<String, String> MeasurementSearchDetails = new HashMap<String, String>();
        MeasurementSearchDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(AttendenceActivity.this));
        MeasurementSearchDetails.put("text", inputsearch.getText().toString());
        Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(AttendenceActivity.this)));
        MeasurementSearchDetails.put("action","show_search_attendance");
        String domainurl=SharedPrefereneceUtil.getDomainUrl(AttendenceActivity.this);
        String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, MeasurementSearchDetails);
        return loginResult;
    }


}

    private void MeasurementSearchDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {


            try {
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    nodata.setVisibility(View.GONE);
                    swipeRefresh.setVisibility(View.VISIBLE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");

                        String ttl_enq = String.valueOf(jsonArrayResult.length());
                        total_present.setText(ttl_enq);
                        final   ArrayList<AttendanceList> subListArrayList = new ArrayList<AttendanceList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new AttendanceList();

                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String MemberID = jsonObj.getString("MemberID");
                                    String Contact = jsonObj.getString("Contact");
                                    String Name = jsonObj.getString("Name");
                                    String PackageName = jsonObj.getString("PackageName");
                                    String InDateTime = jsonObj.getString("InDateTime");
                                    String AttendanceDate = jsonObj.getString("AttendanceDate");
                                    String Remaining_Session = jsonObj.getString("Remaining_Session");
                                    String Start_Date = jsonObj.getString("Start_Date");
                                    String End_Date = jsonObj.getString("End_Date");
                                    String Attendance_Mode = jsonObj.getString("Attendance_Mode");
                                    String Image = jsonObj.getString("Image");
                                    String Status = jsonObj.getString("Status");

                                    subList.setMemberID(MemberID);
                                    subList.setContact(Contact);
                                    String cont=Utility.lastFour(Contact);
                                    subList.setContactEncrypt(cont);
                                    subList.setName(Name);
                                    subList.setPackageName(PackageName);
                                    String[] timearr=InDateTime.split(" ");
                                    SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm:ss");
                                    SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
                                    Date _24HourDt = null;
                                    try {
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
                                    String edate=Utility.formatDate(End_Date);
                                    subList.setExpiryDate(edate);
                                    subList.setAttendanceMode(Attendance_Mode);
                                    subList.setImage(Image);
                                    subList.setStatus(Status);

                                    subListArrayList.add(subList);


                                    adapter = new AttendanceAdapter( AttendenceActivity.this,subListArrayList);
                                    recyclerView.setAdapter(adapter);

                                }
                            }

                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){

                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AttendenceActivity.this, "NO Record Found", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(AttendenceActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_filter) {
            Intent intent = new Intent(AttendenceActivity.this, AttendenceFilterActivity.class);
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
        Intent intent=new Intent(AttendenceActivity.this,AttendenceActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(AttendenceActivity.this, MainActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(AttendenceActivity.this,MainActivity.class);
        startActivity(intent);
    }
}


