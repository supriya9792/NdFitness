package com.ndfitnessplus.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ndfitnessplus.Adapter.WorkOutAdapter;
import com.ndfitnessplus.Adapter.WorkoutLevelAdapter;
import com.ndfitnessplus.Model.MemberDietList;
import com.ndfitnessplus.Model.WorkOutDayList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.ViewDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.ndfitnessplus.Utility.HTTPRequestQueue.isOnline;

public class MemberWorkoutActivity extends AppCompatActivity {
    WorkoutLevelAdapter adapter;
    ArrayList<WorkOutDayList> subListArrayList = new ArrayList<WorkOutDayList>();
    WorkOutDayList subList;
    WorkOutDayList subList1;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    ProgressBar progressBar;
    View nodata;
    ArrayList<WorkOutDayList> mList;
    public static String TAG = MemberWorkoutActivity.class.getName();
    private ProgressDialog pd;
    //Loading gif
    ViewDialog viewDialog;
    //No internet connectioin
    View noInternet;
    String member_id;
    private ProgressBar progress_bar;
    private LinearLayout lyt_no_connection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_member_workout);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.action_workout));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        viewDialog = new ViewDialog(this);
        progressBar=findViewById(R.id.progressBar);
        nodata=findViewById(R.id.nodata);
        noInternet=findViewById(R.id.no_internet);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        lyt_no_connection = (LinearLayout) findViewById(R.id.lyt_no_connection);
        Intent intent = getIntent();
        if(intent!=null){
            member_id=intent.getStringExtra("member_id");
        }

        if (isOnline(MemberWorkoutActivity.this)) {
            workout_daysclass();
        }
        else {
            recyclerView.setVisibility(View.GONE);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_action_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_home) {
            Intent intent = new Intent(MemberWorkoutActivity.this, MainActivity.class);
            startActivity(intent);
        }else if(id== android.R.id.home){
            finish();
        }

        return true;
    }
    private void workout_daysclass() {
        MemberWorkoutActivity.WorkoutDaysTrackclass ru = new MemberWorkoutActivity.WorkoutDaysTrackclass();
        ru.execute("5");
    }
    class WorkoutDaysTrackclass extends AsyncTask<String, Void, String> {

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
            progressBar.setVisibility(View.GONE);
            WorkoutDaysDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> WorkoutDaysDetails = new HashMap<String, String>();
            WorkoutDaysDetails.put("member_id", member_id);
            WorkoutDaysDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(MemberWorkoutActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getCompanyAutoId(MemberWorkoutActivity.this)));
            WorkoutDaysDetails.put("action","show_workout_level_days");
             String domainurl=SharedPrefereneceUtil.getDomainUrl(MemberWorkoutActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, WorkoutDaysDetails);
            return loginResult;
        }


    }

    private void WorkoutDaysDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");

                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                            for (int i = 0; i < jsonArrayResult.length(); i++) {
                                subList1 = new WorkOutDayList();

                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String LevelName = jsonObj.getString("LevelName");

                                    subList1.setLevel(LevelName);
                                    subList1.setSection(true);
                                    subListArrayList.add(subList1);
                                    JSONArray jsonArrayday_info = jsonObj.getJSONArray("day_info");
                                    if (jsonArrayday_info != null && jsonArrayday_info.length() > 0) {

                                        for (int j = 0; j < jsonArrayday_info.length(); j++) {
                                            subList = new WorkOutDayList();

                                            JSONObject jsonObjdiet = jsonArrayday_info.getJSONObject(j);
                                            if (jsonObj != null) {

                                                String Days = jsonObjdiet.getString("Days");

                                                subList.setDay(Days);
                                                subList.setMemberId(member_id);
                                                subList.setSection(false);

                                                subListArrayList.add(subList);


                                            }
                                        }

                                        adapter = new WorkoutLevelAdapter(MemberWorkoutActivity.this, subListArrayList);
                                        recyclerView.setAdapter(adapter);
                                    }
                                }
                            }
                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    nodata.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
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
      finish();
    }
}
