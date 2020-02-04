package com.ndfitnessplus.Activity.Notification;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ndfitnessplus.Activity.CourseDetailsActivity;
import com.ndfitnessplus.Activity.EnquiryActivity;
import com.ndfitnessplus.Activity.LoginActivity;
import com.ndfitnessplus.Activity.MainActivity;
import com.ndfitnessplus.Activity.MemberActivity;
import com.ndfitnessplus.Activity.MemberFilterActivity;
import com.ndfitnessplus.Activity.NotificationActivity;
import com.ndfitnessplus.Adapter.MemberAdapter;
import com.ndfitnessplus.Adapter.MemberBirthdayAdapter;
import com.ndfitnessplus.Model.MemberDataList;
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

public class ActiveMemberActivity extends AppCompatActivity {
    public static String TAG = ActiveMemberActivity.class.getName();
    private ProgressDialog pd;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    ProgressBar progressBar;
    FrameLayout mainframe;
    View nodata;
    MemberAdapter adapter;
    ArrayList<MemberDataList> subListArrayList = new ArrayList<MemberDataList>();
    MemberDataList subList;
    //search
    private EditText inputsearch;
    ImageView search;
    //Loading gif
    ViewDialog viewDialog;
    //Search ...
    TextView todate,fromdate;
    ImageButton toDatebtn,fromDateBtn;
    Button BtnSearch;
    private int mYear, mMonth, mDay;
    TextView ttl_members;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_active_member);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.active_members));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){
        progressBar=findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        mainframe=findViewById(R.id.main_frame);
        inputsearch=(EditText)findViewById(R.id.inputsearchid);
        search=findViewById(R.id.search);
        nodata=findViewById(R.id.nodata);
        todate=findViewById(R.id.to_date);
        fromdate=findViewById(R.id.from_date);
        fromDateBtn=findViewById(R.id.btn_from_date);
        toDatebtn=findViewById(R.id.btn_to_date);
        BtnSearch=findViewById(R.id.btn_search);
        ttl_members=findViewById(R.id.ttl_members);

        String firstday= Utility.getFirstDayofMonth();
        todate.setText(firstday);
        String curr_date=Utility.getCurrentDate();
        fromdate.setText(curr_date);


        //date pickers for to date and from date
        toDatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(ActiveMemberActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String date=(year + "-"
                                        + (monthOfYear + 1) + "-" + dayOfMonth).toString();
                                String cdate=Utility.formatDateDB(date);
                                todate.setText(cdate);
                                CampareTwoDates();

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        fromDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(ActiveMemberActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String date=(year + "-"
                                        + (monthOfYear + 1) + "-" + dayOfMonth).toString();
                                String cdate=Utility.formatDateDB(date);
                                fromdate.setText(cdate);
                                CampareFronTwoDates();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        BtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.clear();
                searchactivememberclass();
            }
        });
        nodata=findViewById(R.id.nodata);
        viewDialog = new ViewDialog(this);
//        adapter = new EnquiryAdapter( new ArrayList<EnquiryList>(),EnquiryActivity.this);
//        recyclerView.setAdapter(adapter);


        if (isOnline(ActiveMemberActivity.this)) {
            activememberclass();// check login details are valid or not from server
        }
        else {
            Toast.makeText(ActiveMemberActivity.this, R.string.internet_unavailable, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ActiveMemberActivity.this);
            builder.setMessage(R.string.internet_unavailable);
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
        inputsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                if (ActiveMemberActivity.this.adapter == null){
                    // some print statement saying it is null
                    Toast toast = Toast.makeText(ActiveMemberActivity.this,"no record found", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else
                {
                    //isLoading = false;
                  int count=  ActiveMemberActivity.this.adapter.filter(String.valueOf(arg0));
                  ttl_members.setText(String.valueOf(count));

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
                    activememberclass();
                }

            }
        });

    }
    public void CampareTwoDates(){
        //******************campare two dates****************
//        String date = "03/26/2012 11:00:00";
//        String dateafter = "03/26/2012 11:59:00";
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy");
        Date convertedDate = new Date();
        Date convertedDate2 = new Date();
        try {
            convertedDate = dateFormat.parse(todate.getText().toString());
            convertedDate2 = dateFormat.parse(fromdate.getText().toString());
            if (convertedDate2.after(convertedDate) || convertedDate2.equals(convertedDate)) {
                //.setText("true");
            } else {
                String firstday= Utility.getFirstDayofMonth();
                todate.setText(firstday);
                Toast.makeText(this, "From date should be greater than to date: " , Toast.LENGTH_LONG).show();
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void CampareFronTwoDates(){
        //******************campare two dates****************
//        String date = "03/26/2012 11:00:00";
//        String dateafter = "03/26/2012 11:59:00";
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy");
        Date convertedDate = new Date();
        Date convertedDate2 = new Date();
        try {
            convertedDate = dateFormat.parse(fromdate.getText().toString());
            convertedDate2 = dateFormat.parse(todate.getText().toString());
            if (convertedDate2.before(convertedDate) || convertedDate2.equals(convertedDate)) {
                //.setText("true");
            } else {
                String firstday= Utility.getCurrentDate();
                fromdate.setText(firstday);
                Toast.makeText(this, "From date should not be less than to date: " , Toast.LENGTH_LONG).show();
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_action_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                Intent intent = new Intent(ActiveMemberActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showProgressDialog() {
        Log.v(TAG, String.format("showProgressDialog"));
        pd = new ProgressDialog(ActiveMemberActivity.this);
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
    //*********** Asycc class for loading data for database **************
    private void activememberclass() {
        ActiveMemberActivity. ActiveMemberTrackclass ru = new ActiveMemberActivity. ActiveMemberTrackclass();
        ru.execute("5");
    }

    class  ActiveMemberTrackclass extends AsyncTask<String, Void, String> {


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
            Log.v(TAG, String.format("onPostExecute :: show_balance_trasaction_details = %s", response));
         //   dismissProgressDialog();
            viewDialog.hideDialog();

            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
             ActiveMemberDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String>  ActiveMemberDetails = new HashMap<String, String>();
             ActiveMemberDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(ActiveMemberActivity.this));
             ActiveMemberDetails.put("status", "Active");
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(ActiveMemberActivity.this)));
             ActiveMemberDetails.put("action","show_active_deactive_members_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(ActiveMemberActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL,  ActiveMemberDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }

    private void  ActiveMemberDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
//        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayoutPrabhagDetails);
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    progressBar.setVisibility(View.GONE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }
                        ttl_members.setText(String.valueOf(jsonArrayResult.length()));
                        ArrayList<MemberDataList> item = new ArrayList<MemberDataList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new MemberDataList();
                                Log.d(TAG, "i: " + i);

                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String name = jsonObj.getString("Name");
                                    String gender = jsonObj.getString("Gender");
                                    String Contact = jsonObj.getString("Contact");
                                    String DOB = jsonObj.getString("DOB");
                                    String ExecutiveName = jsonObj.getString("ExecutiveName");
                                    String BloodGroup = jsonObj.getString("BloodGroup");
                                    String occupation = jsonObj.getString("Occupation");
                                    String MemberID = jsonObj.getString("MemberID");
                                    String Image = jsonObj.getString("Image");
                                    String status=jsonObj.getString("MemberStatus");
                                    String Email=jsonObj.getString("Email");
                                    String End_Date=jsonObj.getString("End_Date");
                                    String FinalBalance=jsonObj.getString("FinalBalance");
                                    //  for (int j = 0; j < 5; j++) {

                                    subList.setName(name);
                                    subList.setGender(gender);
                                    String cont=Utility.lastFour(Contact);
                                    subList.setContact(Contact);
                                    subList.setContactEncrypt(cont);
                                    String dob= Utility.formatDate(DOB);
                                    subList.setBirthDate(dob);
                                    subList.setExecutiveName(ExecutiveName);
                                    subList.setBlodGroup(BloodGroup);
                                    subList.setOccupation(occupation);
                                    subList.setID(MemberID);
                                    Image.replace("\"", "");
                                    subList.setImage(Image);
                                    subList.setStatus(status);
                                    subList.setEmail(Email);
                                    String enddate= Utility.formatDateDB(End_Date);
                                    subList.setEndDate(enddate);
                                    subList.setFinalBalance(FinalBalance);
                                    item.add(subList);
                                    adapter = new MemberAdapter( item,ActiveMemberActivity.this);
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
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ActiveMemberActivity.this);
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
    private void searchactivememberclass() {
        ActiveMemberActivity. SearchActiveMemberTrackclass ru = new ActiveMemberActivity. SearchActiveMemberTrackclass();
        ru.execute("5");
    }

    class  SearchActiveMemberTrackclass extends AsyncTask<String, Void, String> {


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
            Log.v(TAG, String.format("onPostExecute :: search_active_member_filter = %s", response));
            //   dismissProgressDialog();
            viewDialog.hideDialog();

            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            SearchActiveMemberDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String>  SearchActiveMemberDetails = new HashMap<String, String>();
            SearchActiveMemberDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(ActiveMemberActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(ActiveMemberActivity.this)));
            SearchActiveMemberDetails.put("to_date",todate.getText().toString());
            Log.v(TAG, String.format("doInBackground :: to_date = %s",todate.getText().toString() ));
            SearchActiveMemberDetails.put("from_date",fromdate.getText().toString());
            Log.v(TAG, String.format("doInBackground :: from_date = %s", fromdate.getText().toString()));
            SearchActiveMemberDetails.put("action","search_active_member_filter");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(ActiveMemberActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL,  SearchActiveMemberDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }

    private void  SearchActiveMemberDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: search_active_member_filter = %s", jsonResponse));
//        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayoutPrabhagDetails);
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    progressBar.setVisibility(View.GONE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }
                        ttl_members.setText(String.valueOf(jsonArrayResult.length()));
                        ArrayList<MemberDataList> item = new ArrayList<MemberDataList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new MemberDataList();
                                Log.d(TAG, "i: " + i);

                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String name = jsonObj.getString("Name");
                                    String gender = jsonObj.getString("Gender");
                                    String Contact = jsonObj.getString("Contact");
                                    String DOB = jsonObj.getString("DOB");
                                    String ExecutiveName = jsonObj.getString("ExecutiveName");
                                    String BloodGroup = jsonObj.getString("BloodGroup");
                                    String occupation = jsonObj.getString("Occupation");
                                    String MemberID = jsonObj.getString("MemberID");
                                    String Image = jsonObj.getString("Image");
                                    String status=jsonObj.getString("MemberStatus");
                                    String Email=jsonObj.getString("Email");
                                    String End_Date=jsonObj.getString("End_Date");
                                    String FinalBalance=jsonObj.getString("FinalBalance");
                                    //  for (int j = 0; j < 5; j++) {

                                    subList.setName(name);
                                    subList.setGender(gender);
                                    String cont=Utility.lastFour(Contact);
                                    subList.setContact(Contact);
                                    subList.setContactEncrypt(cont);
                                    String dob= Utility.formatDate(DOB);
                                    subList.setBirthDate(dob);
                                    subList.setExecutiveName(ExecutiveName);
                                    subList.setBlodGroup(BloodGroup);
                                    subList.setOccupation(occupation);
                                    subList.setID(MemberID);
                                    Image.replace("\"", "");
                                    subList.setImage(Image);
                                    subList.setStatus(status);
                                    subList.setEmail(Email);
                                    String enddate= Utility.formatDateDB(End_Date);
                                    subList.setEndDate(enddate);
                                    subList.setFinalBalance(FinalBalance);
                                    item.add(subList);
                                    adapter = new MemberAdapter( item,ActiveMemberActivity.this);
                                    recyclerView.setAdapter(adapter);

                                }
                            }
                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    ttl_members.setText("0");
                    nodata.setVisibility(View.VISIBLE);
                     recyclerView.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ActiveMemberActivity.this);
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

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //swipeRefresh.setRefreshing(false);
        Intent intent=new Intent(ActiveMemberActivity.this,ActiveMemberActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(ActiveMemberActivity.this, NotificationActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(ActiveMemberActivity.this,NotificationActivity.class);
        startActivity(intent);
    }
}
