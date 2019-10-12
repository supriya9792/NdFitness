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
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ndfitnessplus.Adapter.WorkOutDetailsAdapter;
import com.ndfitnessplus.Model.WorkOutDetailsList;
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

public class WorkoutDetailsActivity extends AppCompatActivity {
    WorkOutDetailsAdapter adapter;
    ArrayList<WorkOutDetailsList> subListArrayList = new ArrayList<WorkOutDetailsList>();
    WorkOutDetailsList subList;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    ProgressBar progressBar;
    View nodata;
    ArrayList<WorkOutDetailsList> mList;
    public static String TAG = WorkoutDetailsActivity.class.getName();
    private ProgressDialog pd;
    //Loading gif
    ViewDialog viewDialog;
    //No internet connectioin
    View noInternet;
    private ProgressBar progress_bar;
    private LinearLayout lyt_no_connection;
    String Days,member_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_details);
        Intent intent = getIntent();
        if(intent !=null){
            Days =intent.getStringExtra("days");
            member_id =intent.getStringExtra("member_id");
        }
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Days);
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



        if (isOnline(WorkoutDetailsActivity.this)) {
            workout_detailsclass();// check login details are valid or not from server
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
            Intent intent = new Intent(WorkoutDetailsActivity.this, MainActivity.class);
            startActivity(intent);
        }else if(id== android.R.id.home){
            //Toast.makeText(this,"Navigation back pressed",Toast.LENGTH_SHORT).show();
            // NavUtils.navigateUpFromSameTask(this);
            finish();
        }

        return true;
    }
    private void workout_detailsclass() {
        WorkoutDetailsActivity.WorkoutDetailsTrackclass ru = new WorkoutDetailsActivity.WorkoutDetailsTrackclass();
        ru.execute("5");
    }
    class WorkoutDetailsTrackclass extends AsyncTask<String, Void, String> {

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
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            //dismissProgressDialog();
            viewDialog.hideDialog();
            progressBar.setVisibility(View.GONE);
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            WorkoutDetailsDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> WorkoutDetailsDetails = new HashMap<String, String>();
            WorkoutDetailsDetails.put("member_id", member_id);
            WorkoutDetailsDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(WorkoutDetailsActivity.this));
            WorkoutDetailsDetails.put("days", Days);
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getCompanyAutoId(WorkoutDetailsActivity.this)));
            WorkoutDetailsDetails.put("action","show_workout_details");
             String domainurl=SharedPrefereneceUtil.getDomainUrl(WorkoutDetailsActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, WorkoutDetailsDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }

    private void WorkoutDetailsDetails(String jsonResponse) {

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

                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                            for (int i = 0; i < jsonArrayResult.length(); i++) {
                                subList = new WorkOutDetailsList();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Musculargroup = jsonObj.getString("Musculargroup");
                                    String Workoutname = jsonObj.getString("Workoutname");
                                    String Sets = jsonObj.getString("Sets");
                                    String Repetation = jsonObj.getString("Repetation");
                                    String Time = jsonObj.getString("Time");
                                    String Weight = jsonObj.getString("Weight");
                                    String Description = jsonObj.getString("Description");
                                    String Image = jsonObj.getString("Image");
                                    String Vediolink = jsonObj.getString("Vediolink");

                                    //String musclegrp="Muscular Group: "+Musculargroup;
                                    subList.setBodyPart(Musculargroup);
                                    subList.setWorkoutName(Workoutname);
                                    String sets="Sets : "+Sets;
                                    subList.setSet(sets);
                                    String rep="Repetation : "+Repetation;
                                    subList.setRepitation(rep);
                                    // String timee=Time+" mins";
                                    subList.setTime(Time);
                                    subList.setWeight(Weight);
                                    subList.setDiscription(Description);
                                    subList.setWorkoutImage(Image);
                                    subList.setVideoLink(Vediolink);
                                    String setret=Sets+" x "+Repetation;
                                    subList.setSetAndRepitations(setret);

                                    //Toast.makeText(MainActivity.this, "j "+j, Toast.LENGTH_SHORT).show();
                                    subListArrayList.add(subList);
                                    adapter = new WorkOutDetailsAdapter(WorkoutDetailsActivity.this, subListArrayList);
                                    recyclerView.setAdapter(adapter);

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
