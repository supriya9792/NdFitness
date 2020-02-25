package com.ndfitnessplus.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.ndfitnessplus.Adapter.MemberDietAdapter;
import com.ndfitnessplus.Model.MemberDietList;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.ndfitnessplus.Utility.HTTPRequestQueue.isOnline;

public class MemberDietActivity extends AppCompatActivity {
    MemberDietAdapter adapter;
    public static final String OPEN_NOTIFICATION_SETTINGS = "open_notification_settings";
    ArrayList<MemberDietList> subListArrayList = new ArrayList<MemberDietList>();
    MemberDietList subList;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    ProgressBar progressBar;
    View nodata;
    ArrayList<MemberDietList> mList;
    public static String TAG = DietActivity.class.getName();
    private ProgressDialog pd;
    //Loading gif
    ViewDialog viewDialog;
    //No internet connection
    View noInternet;
    private ProgressBar progress_bar;
    private LinearLayout lyt_no_connection;
//    Switch alarmONOFF;
    PendingIntent pi;
    int hour, min;
    ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();
    String member_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_member_diet);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.diet));
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
        if (isOnline(MemberDietActivity.this)) {
            dietclass();
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
            Intent intent = new Intent(MemberDietActivity.this, MainActivity.class);
            startActivity(intent);
        }else if(id== android.R.id.home){
            finish();
        }

        return true;
    }
    private void dietclass() {
        MemberDietActivity.DietTrackclass ru = new MemberDietActivity.DietTrackclass();
        ru.execute("5");
    }



    class DietTrackclass extends AsyncTask<String, Void, String> {

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
            DietDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> DietDetails = new HashMap<String, String>();
            DietDetails.put("member_id", member_id);
            DietDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(MemberDietActivity.this));
            DietDetails.put("action","show_diet_to_member");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(MemberDietActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, DietDetails);
            return loginResult;
        }


    }

    private void DietDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {

            try {
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");

                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                            for (int i = 0; i < jsonArrayResult.length(); i++) {
                                subList = new MemberDietList();
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Diet_Id = jsonObj.getString("Diet_Id");
                                    String Diet_Date = jsonObj.getString("Diet_Date");
                                    String End_Date = jsonObj.getString("End_Date");
                                    String Executive_DietitionName = jsonObj.getString("Executive_DietitionName");
                                    String Purpose = jsonObj.getString("Purpose");
                                    String Advoice = jsonObj.getString("Advoice");

                                    JSONArray jsonArrayDietInfo = jsonObj.getJSONArray("diet_info");
                                    if (jsonArrayDietInfo != null && jsonArrayDietInfo.length() > 0) {

                                        for (int j = 0; j < jsonArrayDietInfo.length(); j++) {
                                            subList = new MemberDietList();
                                            JSONObject jsonObjdiet = jsonArrayDietInfo.getJSONObject(j);
                                            if (jsonObj != null) {

                                                String MealType = jsonObjdiet.getString("Meal_Type");
                                                String MealDisc = jsonObjdiet.getString("MealDisc");
                                                String Time = jsonObjdiet.getString("Time");

                                                if(!(Time.equals("null")|| Time.equals(""))) {
                                                    Calendar calendar = Calendar.getInstance();
                                                    String curentdate = Utility.getCurrentDate();
                                                    SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                                                    Date time = fmt.parse(curentdate + " " + Time);
                                                    calendar.setTime(time);

                                                    String hourminsplit[]=Time.split(":");
                                                    hour=Integer.parseInt(hourminsplit[0]);
                                                    min=Integer.parseInt(hourminsplit[1]);
                                                }

                                                subList.setMealName(MealType);
                                                subList.setMealTime(Time);
                                                subList.setMealDisc(MealDisc);
                                                String curentdate = Utility.getCurrentDate();

                                                subListArrayList.add(subList);
                                                adapter = new MemberDietAdapter(MemberDietActivity.this, subListArrayList);
                                                recyclerView.setAdapter(adapter);

                                            }
                                        }
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
                e.printStackTrace();
            } catch (ParseException e) {
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
