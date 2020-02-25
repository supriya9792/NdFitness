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

import com.ndfitnessplus.Adapter.CourseAdapter;
import com.ndfitnessplus.Adapter.EnquiryAdapter;
import com.ndfitnessplus.Adapter.ShowWorkoutAdapter;
import com.ndfitnessplus.Listeners.PaginationScrollListener;
import com.ndfitnessplus.Model.CourseList;
import com.ndfitnessplus.Model.WorkOutDayList;
import com.ndfitnessplus.Model.WorkOutDayList;
import com.ndfitnessplus.Model.WorkOutDayList;
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

public class WorkoutActivity extends AppCompatActivity  implements SwipeRefreshLayout.OnRefreshListener {
    ShowWorkoutAdapter adapter;

    WorkOutDayList subList;
    SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    ProgressBar progressBar;

    View nodata,frame;
    //No internet connectioin
    View noInternet;
    private ProgressBar progress_bar;
    private LinearLayout lyt_no_connection;
    public static String TAG = WorkoutActivity.class.getName();
    private ProgressDialog pd;
    //search
    private EditText inputsearch;
    ImageView search;
    TextView total_workout;
    int count=0;
    private boolean isLoading = false;
    //Loading gif
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_workout);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.exi_workout));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){
        FloatingActionButton addenquiry=findViewById(R.id.fab);
        progressBar=findViewById(R.id.progressBar);
        swipeRefresh=findViewById(R.id.swipeRefresh);
        recyclerView =  findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        viewDialog = new ViewDialog(this);

        total_workout=findViewById(R.id.ttl_workout);
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
            String tt_budget=intent.getStringExtra("ttl_budget");
            ArrayList<WorkOutDayList> filterArrayList = (ArrayList<WorkOutDayList>) args.getSerializable("filter_array_list");
            progressBar.setVisibility(View.GONE);
            count=filterArrayList.size();
            total_workout.setText(String.valueOf(count));
            adapter = new ShowWorkoutAdapter( WorkoutActivity.this,filterArrayList);
            recyclerView.setAdapter(adapter);
        }else{
            if (isOnline(WorkoutActivity.this)) {
                workoutclass();
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

        addenquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WorkoutActivity.this,AddWorkoutActivity.class);
                startActivity(intent);
            }
        });

        inputsearch=(EditText)findViewById(R.id.inputsearchid);
        search=findViewById(R.id.search);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputsearch.getText().length()>0){
                    workoutsearchclass();
                }else{
                    Toast.makeText(WorkoutActivity.this,"Please enter text to search", Toast.LENGTH_LONG).show();
                }

            }
        });

        inputsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(final Editable arg0) {
                // TODO Auto-generated method stub
                if (WorkoutActivity.this.adapter == null){

                }
                else
                {
                    isLoading = false;
                    ArrayList<WorkOutDayList> filterlist=WorkoutActivity.this.adapter.filter(String.valueOf(arg0));
                    double totalBudget=0;

                    total_workout.setText(String.valueOf(filterlist.size()));

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
                    workoutclass();
                }
            }
        });

    }
    private void workoutclass() {
        WorkoutActivity.WorkoutTrackclass ru = new WorkoutActivity.WorkoutTrackclass();
        ru.execute("5");
    }

    @Override
    public void onRefresh() {

        swipeRefresh.setRefreshing(false);
        onRestart();
    }

    class WorkoutTrackclass extends AsyncTask<String, Void, String> {

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

            WorkoutDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {

            HashMap<String, String> WorkoutDetails = new HashMap<String, String>();
            WorkoutDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(WorkoutActivity.this));

            WorkoutDetails.put("action","show_workout_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(WorkoutActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, WorkoutDetails);

            return loginResult;
        }

    }

    private void WorkoutDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {


            try {

                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));

                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    nodata.setVisibility(View.GONE);
                    swipeRefresh.setVisibility(View.VISIBLE);
                    String ttl_enq = object.getString("total_workout_count");

                    total_workout.setText(ttl_enq);

                    progressBar.setVisibility(View.GONE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");

                        ArrayList<WorkOutDayList> item = new ArrayList<WorkOutDayList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new WorkOutDayList();

                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String name = jsonObj.getString("Name");
                                    String Contact = jsonObj.getString("Contact");
                                    String Date = jsonObj.getString("Date");
                                    String Exercise_Id = jsonObj.getString("Exercise_Id");
                                    String Email_Id = jsonObj.getString("Email_Id");
                                    String LevelName = jsonObj.getString("LevelName");
                                    String Member_Id = jsonObj.getString("Member_Id");
                                    String Image = jsonObj.getString("Image");
                                    String Instructarname = jsonObj.getString("Instructarname");


                                    subList.setMemberName(name);
                                    String cont= Utility.lastFour(Contact);
                                    subList.setMemberContact(Contact);
                                    subList.setEncryptContact(cont);
                                    subList.setExerciseId(Exercise_Id);
                                    subList.setEmailId(Email_Id);
                                    String next_foll_date= Utility.formatDate(Date);
                                    subList.setAssignDate(next_foll_date);
                                    subList.setMemberId(Member_Id);
                                    Image.replace("\"", "");
                                    subList.setMemberImage(Image);
                                    subList.setLevel(LevelName);
                                    subList.setInstructorName(Instructarname);

                                    item.add(subList);
                                    adapter = new ShowWorkoutAdapter( WorkoutActivity.this,item);
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

                e.printStackTrace();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WorkoutActivity.this);
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
    private void workoutsearchclass() {
        WorkoutActivity.EnquirySearchTrackclass ru = new WorkoutActivity.EnquirySearchTrackclass();
        ru.execute("5");
    }
    class EnquirySearchTrackclass extends AsyncTask<String, Void, String> {

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
            EnquirySearchDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {

            HashMap<String, String> EnquirySearchDetails = new HashMap<String, String>();
            EnquirySearchDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(WorkoutActivity.this));
            EnquirySearchDetails.put("text",inputsearch.getText().toString());
            EnquirySearchDetails.put("action","show_search_workout");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(WorkoutActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, EnquirySearchDetails);
            return loginResult;
        }


    }

    private void EnquirySearchDetails(String jsonResponse) {

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
                        String ttl_enq = object.getString("total_workout_count");
                        total_workout.setText(ttl_enq);
                        ArrayList<WorkOutDayList> item = new ArrayList<WorkOutDayList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new WorkOutDayList();

                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String name = jsonObj.getString("Name");
                                    String Contact = jsonObj.getString("Contact");
                                    String Date = jsonObj.getString("Date");
                                    String Exercise_Id = jsonObj.getString("Exercise_Id");
                                    String Email_Id = jsonObj.getString("Email_Id");
                                    String LevelName = jsonObj.getString("LevelName");
                                    String Member_Id = jsonObj.getString("Member_Id");
                                    String Image = jsonObj.getString("Image");
                                    String Instructarname = jsonObj.getString("Instructarname");


                                    subList.setMemberName(name);
                                    String cont= Utility.lastFour(Contact);
                                    subList.setMemberContact(Contact);
                                    subList.setEncryptContact(cont);

                                    subList.setExerciseId(Exercise_Id);
                                    subList.setEmailId(Email_Id);
                                    String next_foll_date= Utility.formatDate(Date);
                                    subList.setAssignDate(next_foll_date);
                                    subList.setMemberId(Member_Id);
                                    Image.replace("\"", "");
                                    subList.setMemberImage(Image);
                                    subList.setLevel(LevelName);
                                    subList.setInstructorName(Instructarname);

                                    item.add(subList);
                                    adapter = new ShowWorkoutAdapter( WorkoutActivity.this,item);
                                    recyclerView.setAdapter(adapter);


                                }
                            }

                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    Toast.makeText(WorkoutActivity.this, "NO Record Found", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    nodata.setVisibility(View.VISIBLE);
                    swipeRefresh.setVisibility(View.GONE);
                }
            } catch (JSONException e) {

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
            Intent intent = new Intent(WorkoutActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_filter) {
            Intent intent = new Intent(WorkoutActivity.this, WorkoutFilterActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent = new Intent(WorkoutActivity.this, MainActivity.class);
        startActivity(intent);
        return true;
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(WorkoutActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
