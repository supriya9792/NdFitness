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
//        alarmONOFF=findViewById(R.id.alarm_on_off);
        noInternet=findViewById(R.id.no_internet);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        lyt_no_connection = (LinearLayout) findViewById(R.id.lyt_no_connection);
        Intent intent = getIntent();
        if(intent!=null){
            member_id=intent.getStringExtra("member_id");
        }
        if (isOnline(MemberDietActivity.this)) {
            dietclass();// check login details are valid or not from server
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
//        alarmONOFF.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                Log.v("Switch State=", ""+b);
//                if (!compoundButton.isSelected()) {
//                    Log.i("Yeah" , "Is Not Selected");
//                    // stopAlarmManager();
//
//                }
//
//            }
//        });


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
            //Toast.makeText(this,"Navigation back pressed",Toast.LENGTH_SHORT).show();
            // NavUtils.navigateUpFromSameTask(this);
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
            DietDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> DietDetails = new HashMap<String, String>();
            DietDetails.put("member_id", member_id);
            DietDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(MemberDietActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getCompanyAutoId(MemberDietActivity.this)));
            DietDetails.put("action","show_diet_to_member");
             String domainurl=SharedPrefereneceUtil.getDomainUrl(MemberDietActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, DietDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }

    private void DietDetails(String jsonResponse) {

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
                                subList = new MemberDietList();
                                Log.v(TAG, "JsonResponseOpeartion ::");
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
                                            Log.v(TAG, "JsonResponseOpeartion ::");
                                            JSONObject jsonObjdiet = jsonArrayDietInfo.getJSONObject(j);
                                            if (jsonObj != null) {

                                                String MealType = jsonObjdiet.getString("Meal_Type");
                                                String MealDisc = jsonObjdiet.getString("MealDisc");
                                                String Time = jsonObjdiet.getString("Time");

//                                              if(Time.contains(" ")){
//
//
//                                                String sp[] = Time.split(" ");
//                                                String sptime[] = sp[0].split(" ");
//
//                                                if(!(sp[1].equals("") || sp[1].equals("null") )){
//                                                    Calendar calendar = Calendar.getInstance();
//                                                    SimpleDateFormat fmt = new SimpleDateFormat("hh:mm");
//                                                    Date time = fmt.parse(sp[0]);
//                                                    calendar.setTime(time);
//                                                    setAlarm(calendar.getTimeInMillis());
//                                                }
//                                              }else{
                                                if(!(Time.equals("null")|| Time.equals(""))) {
                                                    Calendar calendar = Calendar.getInstance();
                                                    String curentdate = Utility.getCurrentDate();
                                                    SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                                                    Date time = fmt.parse(curentdate + " " + Time);
                                                    calendar.setTime(time);
//                                                    long seconds = TimeUnit.MILLISECONDS.toSeconds(calendar.getTimeInMillis());
//                                                     calendar.add(Calendar.SECOND, (int) seconds);

//                                              }
                                                    String hourminsplit[]=Time.split(":");
                                                    hour=Integer.parseInt(hourminsplit[0]);
                                                    min=Integer.parseInt(hourminsplit[1]);
                                                }

                                                //  NotificationScheduler.setReminder(this, AlarmReceiver.class, hour,min);

//                                                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
//
//                                                    Intent intent = new Intent(this, AlarmReceiver.class);
//                                                    // Loop counter `i` is used as a `requestCode`
//                                                     pi = PendingIntent.getBroadcast(this, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                                                    // Single alarms in 1, 2, ..., 10 minutes (in `i` minutes)
//                                                  am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
//
//                                                    intentArray.add(pi);

                                                subList.setMealName(MealType);
                                                subList.setMealTime(Time);
                                                subList.setMealDisc(MealDisc);
                                                String curentdate = Utility.getCurrentDate();

                                                // setAlarm(calendar.getTimeInMillis());
                                                //AlarmReceiver.alarmNotification(MealType,getApplicationContext());
                                                // AlarmNotificationService.sendNotification(MealType);
                                                //Toast.makeText(MainActivity.this, "j "+j, Toast.LENGTH_SHORT).show();
                                                subListArrayList.add(subList);
                                                adapter = new MemberDietAdapter(MemberDietActivity.this, subListArrayList);
                                                recyclerView.setAdapter(adapter);

                                            }
                                        }
                                    }
//                                    subList.setMealName(MealType);
//                                    subList.setMealTime(MealTime);
//                                    subList.setMealDisc(MealDisc);
//
//
//                                    //Toast.makeText(MainActivity.this, "j "+j, Toast.LENGTH_SHORT).show();
//                                    subListArrayList.add(subList);
//                                    adapter = new MemberDietAdapter(MemberDietActivity.this, subListArrayList);
//                                    recyclerView.setAdapter(adapter);

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
