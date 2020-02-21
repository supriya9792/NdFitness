package com.ndfitnessplus.Activity.Notification;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ndfitnessplus.Activity.EnrollmentActivity;
import com.ndfitnessplus.Activity.LoginActivity;
import com.ndfitnessplus.Activity.MainActivity;
import com.ndfitnessplus.Activity.NotificationActivity;
import com.ndfitnessplus.Adapter.EnquiryAdapter;
import com.ndfitnessplus.Adapter.MemberAdapter;
import com.ndfitnessplus.Listeners.PaginationScrollListener;
import com.ndfitnessplus.Model.EnquiryList;
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

public class TodaysEnrollmentActivity extends AppCompatActivity {
    MemberAdapter adapter;

    MemberDataList subList;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    ProgressBar progressBar;

    View nodata,frame;
    //No internet connectioin
    View noInternet;
    private LinearLayout lyt_no_connection;
    public static String TAG = TodaysEnrollmentActivity.class.getName();
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
    //Loading gif
    ViewDialog viewDialog;
    //Search ...
    TextView todate,fromdate;
    ImageButton toDatebtn,fromDateBtn;
    Button BtnSearch;
    private int mYear, mMonth, mDay;
    TextView ttl_enrollment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_todays_enrollment);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.menu_today_adminssion));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        viewDialog = new ViewDialog(this);
        nodata=findViewById(R.id.nodata);
        frame=findViewById(R.id.main_frame);
        inputsearch=(EditText)findViewById(R.id.inputsearchid);
        search=findViewById(R.id.search);

        nodata=findViewById(R.id.nodata);

        todate=findViewById(R.id.to_date);
        fromdate=findViewById(R.id.from_date);
        fromDateBtn=findViewById(R.id.btn_from_date);
        toDatebtn=findViewById(R.id.btn_to_date);
        BtnSearch=findViewById(R.id.btn_search);
        ttl_enrollment=findViewById(R.id.ttl_enrollment);

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


                DatePickerDialog datePickerDialog = new DatePickerDialog(TodaysEnrollmentActivity.this,
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


                DatePickerDialog datePickerDialog = new DatePickerDialog(TodaysEnrollmentActivity.this,
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
                if(adapter !=null)
                    adapter.clear();
                searchactivememberclass();
            }
        });
        noInternet=findViewById(R.id.no_internet);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        lyt_no_connection = (LinearLayout) findViewById(R.id.lyt_no_connection);
        progressBar.setVisibility(View.VISIBLE);
        lyt_no_connection.setVisibility(View.VISIBLE);


        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        if (args != null) {
            ArrayList<MemberDataList> filterArrayList = (ArrayList<MemberDataList>) args.getSerializable("filter_array_list");
            progressBar.setVisibility(View.GONE);
            int length=filterArrayList.size();
            adapter = new MemberAdapter( filterArrayList,TodaysEnrollmentActivity.this);
            recyclerView.setAdapter(adapter);
        }else {
            if (isOnline(TodaysEnrollmentActivity.this)) {
                enrollmentclass();// check login details are valid or not from server
            } else {
                frame.setVisibility(View.GONE);
                noInternet.setVisibility(View.VISIBLE);
                lyt_no_connection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        lyt_no_connection.setVisibility(View.GONE);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
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
            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(inputsearch.getText().length()>0){
                        membersearchclass();
                    }else{
                        Toast.makeText(TodaysEnrollmentActivity.this,"Please enter text to search", Toast.LENGTH_LONG).show();
                    }

                }
            });
            inputsearch.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                    if (TodaysEnrollmentActivity.this.adapter == null){
                        // some print statement saying it is null
                        Toast toast = Toast.makeText(TodaysEnrollmentActivity.this,"no record found", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                    else
                    {
                      int ttlenroll=  TodaysEnrollmentActivity.this.adapter.filter(String.valueOf(arg0));
                      ttl_enrollment.setText(String.valueOf(ttlenroll));

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
                        enrollmentclass();
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
                    currentPage++;
                    Log.d(TAG, "prepare called current item: " + currentPage + "Total page" + totalPage);
                    if (currentPage <= totalPage) {
                        currentPage = PAGE_START;
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
    }
    public void CampareTwoDates(){
        //******************campare two dates****************
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy");
        Date convertedDate = new Date();
        Date convertedDate2 = new Date();
        try {
            convertedDate = dateFormat.parse(todate.getText().toString());
            convertedDate2 = dateFormat.parse(fromdate.getText().toString());
            if (convertedDate2.after(convertedDate) || convertedDate2.equals(convertedDate)) {
            } else {
                String firstday= Utility.getFirstDayofMonth();
                todate.setText(firstday);
                Toast.makeText(this, "From date should not be greater than to date: " , Toast.LENGTH_LONG).show();
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void CampareFronTwoDates(){
        //******************campare two dates****************
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy");
        Date convertedDate = new Date();
        Date convertedDate2 = new Date();
        try {
            convertedDate = dateFormat.parse(fromdate.getText().toString());
            convertedDate2 = dateFormat.parse(todate.getText().toString());
            if (convertedDate2.before(convertedDate) || convertedDate2.equals(convertedDate)) {
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
    private void showProgressDialog() {
        Log.v(TAG, String.format("showProgressDialog"));
        pd = new ProgressDialog(TodaysEnrollmentActivity.this);
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
    // Asycc class for loading data for database
    private void enrollmentclass() {
        TodaysEnrollmentActivity.EnrollmentTrackclass ru = new TodaysEnrollmentActivity.EnrollmentTrackclass();
        ru.execute("5");
    }


    class EnrollmentTrackclass extends AsyncTask<String, Void, String> {

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
            EnrollmentDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> EnrollmentDetails = new HashMap<String, String>();
            EnrollmentDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(TodaysEnrollmentActivity.this));
           // EnrollmentDetails.put("offset", String.valueOf(offset));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(TodaysEnrollmentActivity.this)));
            EnrollmentDetails.put("action","show_todays_enrollment_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(TodaysEnrollmentActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, EnrollmentDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }

    }

    private void EnrollmentDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
//        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayoutPrabhagDetails);
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));

                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    //String ttl_enq = object.getString("total_member_count");
                    recyclerView.setVisibility(View.VISIBLE);
                    nodata.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }
                        ttl_enrollment.setText(String.valueOf(jsonArrayResult.length()));
                        int count=0;
                        ArrayList<MemberDataList> item = new ArrayList<MemberDataList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                            if(jsonArrayResult.length()<100){
                                count=jsonArrayResult.length();
                            }else{
                                count=100;
                            }
                            for (int i = 0; i < count; i++) {


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
                                    itemCount++;
                                    Log.d(TAG, "run: " + itemCount);
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

                                    //Toast.makeText(EnrollmentActivity.this, "followup date: "+next_foll_date, Toast.LENGTH_SHORT).show();

                                    //Toast.makeText(MainActivity.this, "j "+j, Toast.LENGTH_SHORT).show();
                                    item.add(subList);
                                    adapter = new MemberAdapter( item,TodaysEnrollmentActivity.this);
                                    recyclerView.setAdapter(adapter);

                                }
                            }
                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    ttl_enrollment.setText("0");
                    nodata.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(TodaysEnrollmentActivity.this);
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
        TodaysEnrollmentActivity. SearchActiveMemberTrackclass ru = new TodaysEnrollmentActivity. SearchActiveMemberTrackclass();
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
            HashMap<String, String>  SearchActiveMemberDetails = new HashMap<String, String>();
            SearchActiveMemberDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(TodaysEnrollmentActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(TodaysEnrollmentActivity.this)));
            SearchActiveMemberDetails.put("to_date",todate.getText().toString());
            Log.v(TAG, String.format("doInBackground :: to_date = %s",todate.getText().toString() ));
            SearchActiveMemberDetails.put("from_date",fromdate.getText().toString());
            Log.v(TAG, String.format("doInBackground :: from_date = %s", fromdate.getText().toString()));
            SearchActiveMemberDetails.put("action","search_enrollment_filter");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(TodaysEnrollmentActivity.this);
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
                    recyclerView.setVisibility(View.VISIBLE);
                    nodata.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }
                        ttl_enrollment.setText(String.valueOf(jsonArrayResult.length()));
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
                                    itemCount++;
                                    Log.d(TAG, "run: " + itemCount);
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

                                    //Toast.makeText(EnrollmentActivity.this, "followup date: "+next_foll_date, Toast.LENGTH_SHORT).show();

                                    //Toast.makeText(MainActivity.this, "j "+j, Toast.LENGTH_SHORT).show();
                                    item.add(subList);
                                    adapter = new MemberAdapter( item,TodaysEnrollmentActivity.this);
                                    recyclerView.setAdapter(adapter);
                                }
                            }
                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    ttl_enrollment.setText("0");
                    nodata.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(TodaysEnrollmentActivity.this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_action_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                Intent intent = new Intent(TodaysEnrollmentActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();

    }
    private void membersearchclass() {
        TodaysEnrollmentActivity.EnquirySearchTrackclass ru = new TodaysEnrollmentActivity.EnquirySearchTrackclass();
        ru.execute("5");
    }
    class EnquirySearchTrackclass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
            showProgressDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            dismissProgressDialog();
            EnquirySearchDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> EnquirySearchDetails = new HashMap<String, String>();
            EnquirySearchDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(TodaysEnrollmentActivity.this));
            EnquirySearchDetails.put("text", inputsearch.getText().toString());
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(TodaysEnrollmentActivity.this)));
            EnquirySearchDetails.put("action","show_search_member");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(TodaysEnrollmentActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, EnquirySearchDetails);
            return loginResult;
        }


    }

    private void EnquirySearchDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    nodata.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");

                        ttl_enrollment.setText(String.valueOf(jsonArrayResult.length()));
                        final   ArrayList<MemberDataList> subListArrayList = new ArrayList<MemberDataList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new MemberDataList();

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


                                    subList.setName(name);
                                    subList.setGender(gender);
                                    String cont=Utility.lastFour(Contact);
                                    subList.setContactEncrypt(cont);
                                    subList.setContact(Contact);
                                    String dob= Utility.formatDate(DOB);
                                    subList.setBirthDate(dob);
                                    subList.setExecutiveName(ExecutiveName);
                                    subList.setBlodGroup(BloodGroup);
                                    subList.setOccupation(occupation);
                                    subList.setID(MemberID);
                                    String replace = Image.replace("\"", "");
                                    subList.setImage(replace);
                                    subList.setStatus(status);
                                    subList.setEmail(Email);
                                    String enddate= Utility.formatDateDB(End_Date);
                                    subList.setEndDate(enddate);
                                    subList.setFinalBalance(FinalBalance);

                                    subListArrayList.add(subList);
                                    adapter = new MemberAdapter( subListArrayList,TodaysEnrollmentActivity.this);
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
                    Toast.makeText(TodaysEnrollmentActivity.this, "NO Record Found", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent=new Intent(TodaysEnrollmentActivity.this,TodaysEnrollmentActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(TodaysEnrollmentActivity.this, NotificationActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(TodaysEnrollmentActivity.this,NotificationActivity.class);
        startActivity(intent);
    }


}
