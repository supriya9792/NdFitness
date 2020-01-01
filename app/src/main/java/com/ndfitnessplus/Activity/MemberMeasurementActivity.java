package com.ndfitnessplus.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
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

import com.ndfitnessplus.Adapter.MeasurementAdapter;
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

public class MemberMeasurementActivity extends AppCompatActivity {
    MeasurementAdapter adapter;
    ArrayList<MeasurementList> subListArrayList = new ArrayList<MeasurementList>();
    MeasurementList subList;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    ProgressBar progressBar;
    FloatingActionButton AddMeasurement;
    View nodata;
    ArrayList<MeasurementList> mList;
    public static String TAG = MemberMeasurementActivity.class.getName();
    private ProgressDialog pd;
    //Loading gif
    ViewDialog viewDialog;
    //No internet connection
    View noInternet;
    private ProgressBar progress_bar;
    private LinearLayout lyt_no_connection;
    String member_id,contact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_member_measurement);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.measurement));
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
        AddMeasurement = (FloatingActionButton) findViewById(R.id.fab);

        Intent intent = getIntent();
        if(intent!=null){
            member_id=intent.getStringExtra("member_id");
            contact=intent.getStringExtra("contact");
        }
        if (isOnline(MemberMeasurementActivity.this)) {
            measurementclass();// check login details are valid or not from server
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
        AddMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MemberMeasurementActivity.this,AddMeasurementActivity.class);
                intent.putExtra("contact",contact);
                startActivity(intent);
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
        int id = item.getItemId();

        if (id == R.id.action_home) {
            Intent intent = new Intent(MemberMeasurementActivity.this, MainActivity.class);
            startActivity(intent);
        }else if(id== android.R.id.home){
            //Toast.makeText(this,"Navigation back pressed",Toast.LENGTH_SHORT).show();
            // NavUtils.navigateUpFromSameTask(this);
            finish();
        }

        return true;
    }
    private void measurementclass() {
        MemberMeasurementActivity.MeasurementTrackclass ru = new MemberMeasurementActivity.MeasurementTrackclass();
        ru.execute("5");
    }
    class MeasurementTrackclass extends AsyncTask<String, Void, String> {

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
//            progressBar.setVisibility(View.GONE);
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            MeasurementDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> MeasurementDetails = new HashMap<String, String>();
            MeasurementDetails.put("member_id", member_id);
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getCompanyAutoId(MemberMeasurementActivity.this)));
            MeasurementDetails.put("action","show_measurement_by_member");
             String domainurl=SharedPrefereneceUtil.getDomainUrl(MemberMeasurementActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, MeasurementDetails);
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
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");

                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                            for (int i = 0; i < jsonArrayResult.length(); i++) {
                                subList = new MeasurementList();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Measurement_Date = jsonObj.getString("Measurement_Date");
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

                                    String mdate= Utility.formatDate(Measurement_Date);
                                    subList.setMeasurement_Date(mdate);
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
                                    String nextdate= Utility.formatDate(NextFollowupDate);
                                    String on="Your Next Measurement Date is "+nextdate;
                                    subList.setNextFollowupDate(on);
                                    String takenby="Taken By:"+Executive_Name;
                                    subList.setExecutive_Name(takenby);


                                    //Toast.makeText(MainActivity.this, "j "+j, Toast.LENGTH_SHORT).show();
                                    subListArrayList.add(subList);
                                    adapter = new MeasurementAdapter(MemberMeasurementActivity.this, subListArrayList);
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
