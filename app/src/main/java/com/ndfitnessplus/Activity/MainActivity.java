package com.ndfitnessplus.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ndfitnessplus.Activity.Notification.TodaysEnquiryActivity;
import com.ndfitnessplus.Adapter.AdSliderAdapter;
import com.ndfitnessplus.CustomData.AdSliderData;
import com.ndfitnessplus.Model.AdSliderList;
import com.ndfitnessplus.Model.EnquiryList;
import com.ndfitnessplus.Notification.NotificationUtils;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.Constants;
import com.ndfitnessplus.Utility.NetworkUtils;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefManager;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.Utility;
import com.ndfitnessplus.Utility.ViewDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager viewPager;
    AdSliderAdapter adapter;
    private static ArrayList<AdSliderList> data;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    int currentPage = 0;
    int NUM_PAGES = 0;
    int MAX_STEP = 5;
    Timer timer;
    public final String TAG = MainActivity.class.getName();
    private ProgressDialog pd;
    //dashnoard menu
    LinearLayout notification, enquiry, add_member, renew, balance_receipt, expenses, member_info, collections, attendance, measurement, pos, diet;
    TextView Username, Welcome;
    String UName, companyname, token;
    ImageView compLogo;
    //Loading gif
    ViewDialog viewDialog;
    public  String ImeiNo;
    TelephonyManager telephonyManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewDialog = new ViewDialog(this);
        data = new ArrayList<AdSliderList>();
        for (int i = 0; i < AdSliderData.nameArray.length; i++) {
            data.add(new AdSliderList(
                    AdSliderData.nameArray[i],
                    AdSliderData.nameArray[i],
                    AdSliderData.drawableArray[i],
                    AdSliderData.id_[i]
            ));
        }

        adapter = new AdSliderAdapter(MainActivity.this,data);
        viewPager.setAdapter(adapter);
        setupAutoPager();
        viewDialog = new ViewDialog(this);
//intitlization
        notification=findViewById(R.id.notification);
        enquiry=findViewById(R.id.enquiry);
        add_member=findViewById(R.id.addmember);
        renew=findViewById(R.id.renew);
        balance_receipt=findViewById(R.id.collection);
        expenses=findViewById(R.id.expenses);
        member_info=findViewById(R.id.mem_info);
        collections=findViewById(R.id.quicksms);
        attendance=findViewById(R.id.attendance);
        measurement=findViewById(R.id.measurement);
        pos=findViewById(R.id.pos);
        diet=findViewById(R.id.diet);

        Username=findViewById(R.id.user_name);
        Welcome=findViewById(R.id.welcome);
        UName=SharedPrefereneceUtil.getName(MainActivity.this);

        deviceId();
        String device_id = NetworkUtils.getIMEINo(this);
        Log.v(TAG, "IMEI No: "+device_id);
        token = SharedPrefManager.getInstance(this).getDeviceToken();
        //Toast.makeText(MainActivity.this, token, Toast.LENGTH_LONG).show();
        registerDeviceClass();
        String upperString = UName.substring(0,1).toUpperCase() + UName.substring(1);
        Username.setText(upperString);

        String wishes=Utility.getWishes();
        Welcome.setText(wishes);
        CheckCompanyClass();
        SmsLoginClass();

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });
        enquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, EnquiryActivity.class);
                startActivity(intent);
            }
        });
        add_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, AddMemberActivity.class);
                startActivity(intent);
            }
        });
        expenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, ExpensesActivity.class);
                startActivity(intent);
            }
        });
        renew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, CourseActivity.class);
                startActivity(intent);
            }
        });
        balance_receipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, BalanceReceiptDetailsActivity.class);
                startActivity(intent);
            }
        });
        member_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, MemberActivity.class);
                startActivity(intent);
            }
        });
        collections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, CollectionActivity.class);
                startActivity(intent);
            }
        });
        attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, AttendenceActivity.class);
                startActivity(intent);
            }
        });
        measurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, MeasurementActivity.class);
                startActivity(intent);
            }
        });
        pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, POSActivity.class);
                startActivity(intent);
            }
        });
        diet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, DietActivity.class);
                startActivity(intent);
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.wel_user);

        TextView compname=(TextView)headerView.findViewById(R.id.company_name);
         compLogo=(ImageView)headerView.findViewById(R.id.imageView);
        //String name=UName;
        String name = UName.substring(0,1).toUpperCase() + UName.substring(1);
         String  authority=SharedPrefereneceUtil.getAuthority(MainActivity.this);
        companyname=SharedPrefereneceUtil.getCompanyName(MainActivity.this);
        String brnach=SharedPrefereneceUtil.getSelectedBranch(MainActivity.this);
        String aname=name+"-"+authority;
        navUsername.setText(aname);
        String comp_branch=companyname+"-"+brnach;
        compname.setText(comp_branch);
        // get menu from navigationView
        Menu menu = navigationView.getMenu();

        // find MenuItem you want to change
//        MenuItem selcteorg = menu.findItem(R.id.selected_org);
//
//        // set new title to the MenuItem
//        selcteorg.setTitle(SharedPrefereneceUtil.getSelectedBranch(MainActivity.this));
        navigationView.setNavigationItemSelectedListener(this);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Constants.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC_GLOBAL);


                } else if (intent.getAction().equals(Constants.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                    //txtMessage.setText(message);
                }
            }
        };
    }
    private void deviceId() {
        telephonyManager = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
            return;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
                        return;
                    }
                     ImeiNo = telephonyManager.getDeviceId();
                    Log.v(TAG, "IMEI No: "+ImeiNo);
                    Toast.makeText(MainActivity.this,ImeiNo,Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this,"Without permission we check",Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    private void setupAutoPager() {
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
                if(currentPage == MAX_STEP){
                    currentPage=0;
                }
            }
        };
        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(Update);
            }
        }, 500, 2500);
    }
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        } else {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Exit Application?");
            alertDialogBuilder
                    .setMessage("Click yes to exit!")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //finish();
                                    finish();
                                    moveTaskToBack(true);
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                    System.exit(0);
                                }
                            })

                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            //  super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_coll) {
            Intent intent=new Intent(MainActivity.this, CollectionActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_todays_enq) {
            Intent intent=new Intent(MainActivity.this, TodaysEnquiryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_todays_admi) {
            Intent intent=new Intent(MainActivity.this,EnrollmentActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_push_noti) {
            Intent intent=new Intent(MainActivity.this,PushNotificationActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_switch_branch) {
            Intent intent=new Intent(MainActivity.this,BranchSelectionActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_generate_qr) {
            Intent intent=new Intent(MainActivity.this,GenerateQRActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_terms) {
            Intent intent=new Intent(MainActivity.this,TermsAndConditionActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_logout) {
            SharedPrefereneceUtil.LogOut(MainActivity.this);
            Intent intent=new Intent(MainActivity.this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void showProgressDialog() {
        Log.v(TAG, String.format("showProgressDialog"));
        pd = new ProgressDialog(MainActivity.this);
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
     // ************** Check Company is active or not **************
    public void  CheckCompanyClass() {
        MainActivity.CheckCompanyTrackClass ru = new MainActivity.CheckCompanyTrackClass();
        ru.execute("5");
    }

    class CheckCompanyTrackClass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
           // showProgressDialog();
            //viewDialog.showDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: check_company_is_inactive_or_not response = %s", response));
           // dismissProgressDialog();
            //viewDialog.hideDialog();
            //Toast.makeText(CandiateListView.this, response, Toast.LENGTH_LONG).show();
            //  Toast.makeText(NewCustomerActivity.this, response, Toast.LENGTH_LONG).show();
            CheckCompanyDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
           // Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> CheckCompanyDetails = new HashMap<String, String>();
            CheckCompanyDetails.put("comp_id",SharedPrefereneceUtil.getSelectedBranchId(MainActivity.this) );
            CheckCompanyDetails.put("action", "check_company_is_inactive_or_not");
            //EnquiryForloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(EnquiryForloyee.this));
            String domainurl=SharedPrefereneceUtil.getDomainUrl(MainActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, CheckCompanyDetails);
            Log.v(TAG, String.format("doInBackground :: check_company_is_inactive_or_not= %s", loginResult));
            return loginResult;
        }
    }
    private void CheckCompanyDetails(String jsonResponse) {

        Log.v(TAG, String.format("check_company_is_inactive_or_not :: response = %s", jsonResponse));

        JSONObject jsonObjLoginResponse = null;
        try {
            jsonObjLoginResponse = new JSONObject(jsonResponse);
            String success = jsonObjLoginResponse.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.zero))) {

                // showCustomDialog();
                Intent intent=new Intent(MainActivity.this,BranchSelectionActivity.class);
                startActivity(intent);
                //inputEmail, inputPhone,inputAdd,inputReq,inputFollowupdate;
            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.two)))
            {
                String logo = jsonObjLoginResponse.getString("Logo");
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.nouser);
                requestOptions.error(R.drawable.nouser);
                String domainurl= SharedPrefereneceUtil.getDomainUrl(MainActivity.this);
                String url= domainurl+ServiceUrls.IMAGES_URL + logo;

                Glide.with(MainActivity.this)
                        .setDefaultRequestOptions(requestOptions)
                        .load(url).into(compLogo);
                checkDeviceClass();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void  SmsLoginClass() {
        MainActivity.SmsLoginTrackClass ru = new MainActivity.SmsLoginTrackClass();
        ru.execute("5");
    }

    class SmsLoginTrackClass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
           // showProgressDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
          //  dismissProgressDialog();
            //Toast.makeText(CandiateListView.this, response, Toast.LENGTH_LONG).show();
            //  Toast.makeText(NewCustomerActivity.this, response, Toast.LENGTH_LONG).show();
            SmsLoginDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
           // Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> SmsLoginDetails = new HashMap<String, String>();
            SmsLoginDetails.put("comp_id",SharedPrefereneceUtil.getSelectedBranchId(MainActivity.this) );
            SmsLoginDetails.put("action", "sms_login_creadetials");
            //EnquiryForloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(EnquiryForloyee.this));
            String domainurl=SharedPrefereneceUtil.getDomainUrl(MainActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, SmsLoginDetails);
            Log.v(TAG, String.format("doInBackground :: sms_login_creadetials= %s", loginResult));
            return loginResult;
        }
    }
    private void SmsLoginDetails(String jsonResponse) {

        Log.v(TAG, String.format("sms_login_creadetials :: response = %s", jsonResponse));

        JSONObject jsonObjLoginResponse = null;
        try {
            jsonObjLoginResponse = new JSONObject(jsonResponse);
            String success = jsonObjLoginResponse.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.zero))) {

                // showCustomDialog();

                //inputEmail, inputPhone,inputAdd,inputReq,inputFollowupdate;
            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.two)))
            {
                if (jsonObjLoginResponse != null) {
                    JSONArray jsonArrayResult = jsonObjLoginResponse.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }
                    int count=0;
                    ArrayList<EnquiryList> item = new ArrayList<EnquiryList>();
                    if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                        if(jsonArrayResult.length()<100){
                            count=jsonArrayResult.length();
                        }else{
                            count=100;
                        }
                        for (int i = 0; i < count; i++) {


                            Log.v(TAG, "JsonResponseOpeartion ::");
                            JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                            if (jsonObj != null) {

                                String UserName = jsonObj.getString("UserName");
                                String Password = jsonObj.getString("Password");
                                String Sender_Id = jsonObj.getString("Sender_Id");
                                String Route = jsonObj.getString("Route");


                                SharedPrefereneceUtil.setSmsUsername(MainActivity.this,UserName);
                                SharedPrefereneceUtil.setSmsPassword(MainActivity.this,Password);
                                SharedPrefereneceUtil.setSmsSenderid(MainActivity.this,Sender_Id);
                                SharedPrefereneceUtil.setSmsRoute(MainActivity.this,Route);
                                //  for (int j = 0; j < 5; j++) {


                            }
                        }
                    } else if (jsonArrayResult.length() == 0) {
                        System.out.println("No records found");
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void  registerDeviceClass() {
        MainActivity.registerDeviceTrackClass ru = new MainActivity.registerDeviceTrackClass();
        ru.execute("5");
    }


    class registerDeviceTrackClass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
            // showProgressDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            //dismissProgressDialog();
            //Toast.makeText(CandiateListView.this, response, Toast.LENGTH_LONG).show();
            // Toast.makeText(Drawer.this, response, Toast.LENGTH_LONG).show();
            //registerDeviceDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            String device_id = NetworkUtils.getIMEINo(MainActivity.this);
          //  Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> registerDeviceDetails = new HashMap<String, String>();
            registerDeviceDetails.put("token",token);
            registerDeviceDetails.put("imei_no",device_id);
            Log.v(TAG, String.format("doInBackground :: Token= %s", token));
            Log.v(TAG, String.format("doInBackground :: ImeiNo= %s", device_id));
            registerDeviceDetails.put("user_id",SharedPrefereneceUtil.getUserId(MainActivity.this));
            registerDeviceDetails.put("user","User");
            Log.v(TAG, String.format("doInBackground :: user_id= %s", SharedPrefereneceUtil.getUserId(MainActivity.this)));
            registerDeviceDetails.put("action", "register_device_token");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(MainActivity.this);
            String loginResult2 = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, registerDeviceDetails);
            Log.v(TAG, String.format("doInBackground :: Register Device= %s", loginResult2));
            return loginResult2;
        }


    }
    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
    public void  checkDeviceClass() {
        MainActivity.checkDeviceTrackClass ru = new MainActivity.checkDeviceTrackClass();
        ru.execute("5");
    }


    class checkDeviceTrackClass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
            // showProgressDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            //dismissProgressDialog();
            //Toast.makeText(CandiateListView.this, response, Toast.LENGTH_LONG).show();
            // Toast.makeText(Drawer.this, response, Toast.LENGTH_LONG).show();
            checkDeviceDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            String device_id = NetworkUtils.getIMEINo(MainActivity.this);
           // Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> checkDeviceDetails = new HashMap<String, String>();
            checkDeviceDetails.put("username",SharedPrefereneceUtil.getUserNm(MainActivity.this));
            checkDeviceDetails.put("imei_no",device_id);
            checkDeviceDetails.put("mode","AdminApp");
            Log.v(TAG, String.format("doInBackground :: ImeiNo= %s", device_id));
            checkDeviceDetails.put("comp_id",SharedPrefereneceUtil.getSelectedBranchId(MainActivity.this));
            Log.v(TAG, String.format("doInBackground :: user_id= %s", SharedPrefereneceUtil.getUserNm(MainActivity.this)));
            checkDeviceDetails.put("action", "check_device_login");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(MainActivity.this);
            String loginResult2 = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, checkDeviceDetails);
            Log.v(TAG, String.format("doInBackground :: domainurl= %s", domainurl+ ServiceUrls.LOGIN_URL));
            Log.v(TAG, String.format("doInBackground :: check_device_login= %s", loginResult2));
            return loginResult2;
        }


    }
    private void checkDeviceDetails(String jsonResponse) {

        Log.v(TAG, String.format(" :: check_device_login = %s", jsonResponse));

        JSONObject jsonObjLoginResponse = null;
        try {
            jsonObjLoginResponse = new JSONObject(jsonResponse);
            String success = jsonObjLoginResponse.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.one))) {

                // showCustomDialog();

                //inputEmail, inputPhone,inputAdd,inputReq,inputFollowupdate;
            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.two)))
            {
                SharedPrefereneceUtil.LogOut(MainActivity.this);
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
