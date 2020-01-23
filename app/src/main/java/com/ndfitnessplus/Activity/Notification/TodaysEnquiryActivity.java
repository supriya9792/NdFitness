package com.ndfitnessplus.Activity.Notification;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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

import com.ndfitnessplus.Activity.AddEnquiryActivity;
import com.ndfitnessplus.Activity.EnquiryActivity;
import com.ndfitnessplus.Activity.LoginActivity;
import com.ndfitnessplus.Activity.MainActivity;
import com.ndfitnessplus.Activity.NotificationActivity;
import com.ndfitnessplus.Adapter.EnquiryAdapter;
import com.ndfitnessplus.Adapter.StaffBirthdayAdapter;
import com.ndfitnessplus.Listeners.PaginationScrollListener;
import com.ndfitnessplus.Model.EnquiryList;
import com.ndfitnessplus.Model.StaffBirthdayList;
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

public class TodaysEnquiryActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    EnquiryAdapter adapter;
    ArrayList<EnquiryList> subListArrayList = new ArrayList<EnquiryList>();
    EnquiryList subList;
    SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    ProgressBar progressBar;
    FrameLayout mainframe;
    View nodata;
    ArrayList<EnquiryList> branchList;
    public static String TAG = TodaysEnquiryActivity.class.getName();
    private ProgressDialog pd;
    //search
    private EditText inputsearch;
    ImageView search;
    //paginnation parameters
    public static final int PAGE_START = 1;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;
    int itemCount = 0;
    int offset = 0;
    //Loading gif
    ViewDialog viewDialog;
    //Search ...
    TextView todate,fromdate;
    ImageButton toDatebtn,fromDateBtn;
    Button BtnSearch;
    private int mYear, mMonth, mDay;
    TextView ttl_enquiry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_todays_enquiry);
        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.menu_today_enquiry));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void initComponent(){
        progressBar=findViewById(R.id.progressBar);
        swipeRefresh=findViewById(R.id.swipeRefresh);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        mainframe=findViewById(R.id.main_frame);
        ttl_enquiry=findViewById(R.id.ttl_enquiry);

        nodata=findViewById(R.id.nodata);

        todate=findViewById(R.id.to_date);
        fromdate=findViewById(R.id.from_date);
        fromDateBtn=findViewById(R.id.btn_from_date);
        toDatebtn=findViewById(R.id.btn_to_date);
        BtnSearch=findViewById(R.id.btn_search);

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


                DatePickerDialog datePickerDialog = new DatePickerDialog(TodaysEnquiryActivity.this,
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


                DatePickerDialog datePickerDialog = new DatePickerDialog(TodaysEnquiryActivity.this,
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
        inputsearch=(EditText)findViewById(R.id.inputsearchid);
        search=findViewById(R.id.search);

        swipeRefresh.setOnRefreshListener(this);
        viewDialog = new ViewDialog(this);
//        adapter = new EnquiryAdapter( new ArrayList<EnquiryList>(),EnquiryActivity.this);
//        recyclerView.setAdapter(adapter);


            if (isOnline(TodaysEnquiryActivity.this)) {
                enquiryclass();// check login details are valid or not from server
            }
            else {
                Toast.makeText(TodaysEnquiryActivity.this, R.string.internet_unavailable, Toast.LENGTH_LONG).show();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(TodaysEnquiryActivity.this);
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
                if (TodaysEnquiryActivity.this.adapter == null){
                    // some print statement saying it is null
                    Toast toast = Toast.makeText(TodaysEnquiryActivity.this,"no record found", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else
                {
                    //isLoading = false;
                    ArrayList<EnquiryList> filterlist=TodaysEnquiryActivity.this.adapter.filter(String.valueOf(arg0));
                    ttl_enquiry.setText(String.valueOf(filterlist.size()));

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
                   enquiryclass();
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
                Log.d(TAG, "prepare called current item: " + currentPage+"Total page"+totalPage);
                if(currentPage<=totalPage){
                    currentPage = PAGE_START;
                    Log.d(TAG, "currentPage: " + currentPage);
                    isLastPage = false;
                    //preparedListItem();
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
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputsearch.getText().length()>0){
                    enquirysearchclass();
                }else{
                    Toast.makeText(TodaysEnquiryActivity.this,"Please enter text to search", Toast.LENGTH_LONG).show();
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
                Toast.makeText(this, "From date should not be greater than to date: " , Toast.LENGTH_LONG).show();
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
                Intent intent = new Intent(TodaysEnquiryActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void preparedListItem() {
        offset=offset+100;

        if (isOnline(TodaysEnquiryActivity.this)) {
            enquiryoffsetclass();// check login details are valid or not from server
        }
        else {
            Toast.makeText(TodaysEnquiryActivity.this, R.string.internet_unavailable, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(TodaysEnquiryActivity.this);
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

    }
    //Showing progress dialog
    private void showProgressDialog() {
        Log.v(TAG, String.format("showProgressDialog"));
        pd = new ProgressDialog(TodaysEnquiryActivity.this);
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
    private void enquiryclass() {
        TodaysEnquiryActivity.EnquiryTrackclass ru = new TodaysEnquiryActivity.EnquiryTrackclass();
        ru.execute("5");
    }

    @Override
    public void onRefresh() {
        itemCount = 0;
        currentPage = PAGE_START;
        isLastPage = false;
        swipeRefresh.setRefreshing(false);
    }

    class EnquiryTrackclass extends AsyncTask<String, Void, String> {

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
           // dismissProgressDialog();
            viewDialog.hideDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            EnquiryDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> EnquiryDetails = new HashMap<String, String>();
            EnquiryDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(TodaysEnquiryActivity.this));
           // EnquiryDetails.put("offset", String.valueOf(offset));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getCompanyAutoId(TodaysEnquiryActivity.this)));
            EnquiryDetails.put("action","show_todays_enquiry");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(TodaysEnquiryActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, EnquiryDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }

    private void EnquiryDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
//        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayoutPrabhagDetails);
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    progressBar.setVisibility(View.GONE);
                    mainframe.setVisibility(View.VISIBLE);
                    nodata.setVisibility(View.GONE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }
                        int count=0;
                        ttl_enquiry.setText(String.valueOf(jsonArrayResult.length()));
                        ArrayList<EnquiryList> item = new ArrayList<EnquiryList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
//                            if(jsonArrayResult.length()<100){
//                                count=jsonArrayResult.length();
//                            }else{
//                                count=100;
//                            }
                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new EnquiryList();
                                Log.d(TAG, "i: " + i);

                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String name = jsonObj.getString("Name");
                                    String gender = jsonObj.getString("Gender");
                                    String Contact = jsonObj.getString("Contact");
                                    String address = jsonObj.getString("Address");
                                    String ExecutiveName = jsonObj.getString("ExecutiveName");
                                    String Comment = jsonObj.getString("Comment");
                                    String NextFollowup_Date = jsonObj.getString("NextFollowupDate");
                                    String Enquiry_ID = jsonObj.getString("Enquiry_ID");
                                    String Image = jsonObj.getString("Image");
                                    String CallResponse = jsonObj.getString("CallResponse");
                                    String Rating = jsonObj.getString("Rating");
                                    String Followup_Date = jsonObj.getString("FollowupDate");
                                    //  for (int j = 0; j < 5; j++) {
                                    itemCount++;
                                    Log.d(TAG, "run: " + itemCount);
                                    subList.setName(name);
                                    subList.setGender(gender);
                                    String cont=Utility.lastFour(Contact);
                                    subList.setContactEncrypt(cont);
                                    subList.setContact(Contact);
                                    subList.setAddress(address);
                                    subList.setExecutiveName(ExecutiveName);
                                    subList.setComment(Comment);
                                    String next_foll_date= Utility.formatDate(NextFollowup_Date);
                                    subList.setNextFollowUpDate(next_foll_date);
                                    subList.setID(Enquiry_ID);
                                    Image.replace("\"", "");
                                    subList.setImage(Image);
                                    subList.setCallResponse(CallResponse);
                                    subList.setRating(Rating);
                                    String foll_date= Utility.formatDate(Followup_Date);
                                    subList.setFollowupdate(foll_date);
                                    //Toast.makeText(TodaysEnquiryActivity.this, "followup date: "+next_foll_date, Toast.LENGTH_SHORT).show();

                                    //Toast.makeText(MainActivity.this, "j "+j, Toast.LENGTH_SHORT).show();
                                    item.add(subList);
                                    adapter = new EnquiryAdapter( item,TodaysEnquiryActivity.this);
                                    recyclerView.setAdapter(adapter);

                                }
                            }
                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    nodata.setVisibility(View.VISIBLE);
                    mainframe.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(TodaysEnquiryActivity.this);
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
    private void enquiryoffsetclass() {
        TodaysEnquiryActivity.EnquiryOffsetTrackclass ru = new TodaysEnquiryActivity.EnquiryOffsetTrackclass();
        ru.execute("5");
    }
    class EnquiryOffsetTrackclass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
            //showProgressDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            // dismissProgressDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            EnquiryOffsetDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> EnquiryOffsetDetails = new HashMap<String, String>();
            EnquiryOffsetDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(TodaysEnquiryActivity.this));
            EnquiryOffsetDetails.put("offset", String.valueOf(offset));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(TodaysEnquiryActivity.this)));
            EnquiryOffsetDetails.put("action","show_todays_enquiry");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(TodaysEnquiryActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, EnquiryOffsetDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }

    private void EnquiryOffsetDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
//        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayoutPrabhagDetails);
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    totalPage++;
                    progressBar.setVisibility(View.GONE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }
                        ArrayList<EnquiryList> item = new ArrayList<EnquiryList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new EnquiryList();
                                Log.d(TAG, "i: " + i);
                                // Log.d(TAG, "run: " + itemCount);
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String name = jsonObj.getString("Name");
                                    String gender = jsonObj.getString("Gender");
                                    String Contact = jsonObj.getString("Contact");
                                    String address = jsonObj.getString("Address");
                                    String ExecutiveName = jsonObj.getString("ExecutiveName");
                                    String Comment = jsonObj.getString("Comment");
                                    String NextFollowup_Date = jsonObj.getString("NextFollowupDate");
                                    String Enquiry_ID = jsonObj.getString("Enquiry_ID");
                                    String Image = jsonObj.getString("Image");
                                    String CallResponse = jsonObj.getString("CallResponse");
                                    String Rating = jsonObj.getString("Rating");
                                    String Followup_Date = jsonObj.getString("FollowupDate");
                                    //  for (int j = 0; j < 5; j++) {
                                    itemCount++;
                                    Log.d(TAG, "run offset: " + itemCount);
                                    subList.setName(name);
                                    subList.setGender(gender);
                                    String cont=Utility.lastFour(Contact);
                                    subList.setContact(Contact);
                                    subList.setContactEncrypt(cont);
                                    subList.setAddress(address);
                                    subList.setExecutiveName(ExecutiveName);
                                    subList.setComment(Comment);
                                    String next_foll_date= Utility.formatDate(NextFollowup_Date);
                                    subList.setNextFollowUpDate(next_foll_date);
                                    subList.setID(Enquiry_ID);

                                    Image.replace("\"", "");
                                    subList.setImage(Image);
                                    subList.setCallResponse(CallResponse);
                                    subList.setRating(Rating);
                                    String foll_date= Utility.formatDate(Followup_Date);
                                    subList.setFollowupdate(foll_date);
                                    //Toast.makeText(EnquiryActivity.this, "followup date: "+next_foll_date, Toast.LENGTH_SHORT).show();
                                    subListArrayList.add(subList);


                                }
                            }
                            if (currentPage != PAGE_START) adapter.removeLoading();
                            adapter.addAll(subListArrayList);
                            swipeRefresh.setRefreshing(false);
                            if (currentPage < totalPage) adapter.addLoading();
                            else isLastPage = true;
                            isLoading = false;

                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    // nodata.setVisibility(View.VISIBLE);

                    progressBar.setVisibility(View.GONE);
                    if (currentPage != PAGE_START)
                    adapter.removeblank();
                    //adapter.addAll(subListArrayList);
                    swipeRefresh.setRefreshing(false);
                    isLoading = false;
                    //recyclerView.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
                mainframe.setVisibility(View.GONE);
                nodata.setVisibility(View.VISIBLE);
            }
        }
    }
    private void searchactivememberclass() {
        TodaysEnquiryActivity. SearchActiveMemberTrackclass ru = new TodaysEnquiryActivity. SearchActiveMemberTrackclass();
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
            SearchActiveMemberDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(TodaysEnquiryActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(TodaysEnquiryActivity.this)));
            SearchActiveMemberDetails.put("to_date",todate.getText().toString());
            Log.v(TAG, String.format("doInBackground :: to_date = %s",todate.getText().toString() ));
            SearchActiveMemberDetails.put("from_date",fromdate.getText().toString());
            Log.v(TAG, String.format("doInBackground :: from_date = %s", fromdate.getText().toString()));
            SearchActiveMemberDetails.put("action","search_todays_enquiry_filter");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(TodaysEnquiryActivity.this);
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
                    mainframe.setVisibility(View.VISIBLE);
                    nodata.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }
                        ttl_enquiry.setText(String.valueOf(jsonArrayResult.length()));
                        ArrayList<EnquiryList> item = new ArrayList<EnquiryList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new EnquiryList();
                                Log.d(TAG, "i: " + i);
                                // Log.d(TAG, "run: " + itemCount);
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String name = jsonObj.getString("Name");
                                    String gender = jsonObj.getString("Gender");
                                    String Contact = jsonObj.getString("Contact");
                                    String address = jsonObj.getString("Address");
                                    String ExecutiveName = jsonObj.getString("ExecutiveName");
                                    String Comment = jsonObj.getString("Comment");
                                    String NextFollowup_Date = jsonObj.getString("NextFollowupDate");
                                    String Enquiry_ID = jsonObj.getString("Enquiry_ID");
                                    String Image = jsonObj.getString("Image");
                                    String CallResponse = jsonObj.getString("CallResponse");
                                    String Rating = jsonObj.getString("Rating");
                                    String Followup_Date = jsonObj.getString("FollowupDate");
                                    //  for (int j = 0; j < 5; j++) {
                                    itemCount++;
                                    Log.d(TAG, "run offset: " + itemCount);
                                    subList.setName(name);
                                    subList.setGender(gender);
                                    String cont=Utility.lastFour(Contact);
                                    subList.setContact(Contact);
                                    subList.setContactEncrypt(cont);
                                    subList.setAddress(address);
                                    subList.setExecutiveName(ExecutiveName);
                                    subList.setComment(Comment);
                                    String next_foll_date= Utility.formatDate(NextFollowup_Date);
                                    subList.setNextFollowUpDate(next_foll_date);
                                    subList.setID(Enquiry_ID);

                                    Image.replace("\"", "");
                                    subList.setImage(Image);
                                    subList.setCallResponse(CallResponse);
                                    subList.setRating(Rating);
                                    String foll_date= Utility.formatDate(Followup_Date);
                                    subList.setFollowupdate(foll_date);
                                    //Toast.makeText(EnquiryActivity.this, "followup date: "+next_foll_date, Toast.LENGTH_SHORT).show();
                                    item.add(subList);
                                    adapter = new EnquiryAdapter( item,TodaysEnquiryActivity.this);
                                    recyclerView.setAdapter(adapter);
                                }
                            }
                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    ttl_enquiry.setText("0");
                    nodata.setVisibility(View.VISIBLE);
                    mainframe.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(TodaysEnquiryActivity.this);
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
    private void enquirysearchclass() {
        TodaysEnquiryActivity.EnquirySearchTrackclass ru = new TodaysEnquiryActivity.EnquirySearchTrackclass();
        ru.execute("5");
    }
    class EnquirySearchTrackclass extends AsyncTask<String, Void, String> {

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
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            // dismissProgressDialog();
            viewDialog.hideDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            EnquirySearchDetails(response);
        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> EnquirySearchDetails = new HashMap<String, String>();
            EnquirySearchDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(TodaysEnquiryActivity.this));
            EnquirySearchDetails.put("text", inputsearch.getText().toString());
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(TodaysEnquiryActivity.this)));
            EnquirySearchDetails.put("action","show_search_enquiry");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(TodaysEnquiryActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, EnquirySearchDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }

    private void EnquirySearchDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
//        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayoutPrabhagDetails);
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    nodata.setVisibility(View.GONE);
                    mainframe.setVisibility(View.VISIBLE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }\
                        ttl_enquiry.setText(String.valueOf(jsonArrayResult.length()));
                        String ttl_enq = String.valueOf(jsonArrayResult.length());
//                        total_enquiry.setText(ttl_enq);
                        final   ArrayList<EnquiryList> subListArrayList = new ArrayList<EnquiryList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new EnquiryList();
                                Log.d(TAG, "i: " + i);
                                // Log.d(TAG, "run: " + itemCount);
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String name = jsonObj.getString("Name");
                                    String gender = jsonObj.getString("Gender");
                                    String Contact = jsonObj.getString("Contact");
                                    String address = jsonObj.getString("Address");
                                    String ExecutiveName = jsonObj.getString("ExecutiveName");
                                    String Comment = jsonObj.getString("Comment");
                                    String NextFollowup_Date = jsonObj.getString("NextFollowupDate");
                                    String Enquiry_ID = jsonObj.getString("Enquiry_ID");
                                    String Image = jsonObj.getString("Image");
                                    String CallResponse = jsonObj.getString("CallResponse");
                                    String Rating = jsonObj.getString("Rating");
                                    String Followup_Date = jsonObj.getString("FollowupDate");
                                    String Budget = jsonObj.getString("Budget");
                                    //  for (int j = 0; j < 5; j++) {
                                    itemCount++;
                                    Log.d(TAG, "run offset: " + itemCount);
                                    subList.setName(name);
                                    subList.setGender(gender);
                                    String cont=Utility.lastFour(Contact);
                                    subList.setContact(Contact);
                                    subList.setContactEncrypt(cont);
                                    subList.setAddress(address);
                                    subList.setExecutiveName(ExecutiveName);
                                    subList.setComment(Comment);
                                    String next_foll_date= Utility.formatDate(NextFollowup_Date);
                                    subList.setNextFollowUpDate(next_foll_date);
                                    subList.setID(Enquiry_ID);
                                    Image.replace("\"", "");
                                    subList.setImage(Image);
                                    subList.setRating(Rating);
                                    subList.setCallResponse(CallResponse);
                                    String foll_date= Utility.formatDate(Followup_Date);
                                    subList.setFollowupdate(foll_date);
                                    if(Budget.equals(".00")){
                                        Budget="0.00";
                                    }
                                    subList.setBudget(Budget);

                                    subListArrayList.add(subList);

                                    adapter = new EnquiryAdapter( subListArrayList,TodaysEnquiryActivity.this);
                                    recyclerView.setAdapter(adapter);

                                }
                            }

                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    // nodata.setVisibility(View.VISIBLE);
//                    nodata.setVisibility(View.VISIBLE);
//                    swipeRefresh.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(TodaysEnquiryActivity.this, "NO Record Found", Toast.LENGTH_SHORT).show();
                    //frame.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
                //recyclerView.setVisibility(View.GONE);
//                frame.setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent=new Intent(TodaysEnquiryActivity.this,TodaysEnquiryActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(TodaysEnquiryActivity.this, NotificationActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(TodaysEnquiryActivity.this,NotificationActivity.class);
        startActivity(intent);
    }
}
