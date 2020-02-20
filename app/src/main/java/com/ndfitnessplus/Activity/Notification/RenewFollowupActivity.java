package com.ndfitnessplus.Activity.Notification;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ndfitnessplus.Activity.MainActivity;
import com.ndfitnessplus.Activity.NotificationActivity;
import com.ndfitnessplus.Adapter.EnquiryAdapter;
import com.ndfitnessplus.Adapter.FollowupAdapter;
import com.ndfitnessplus.Adapter.RenewFollowupAdapter;
import com.ndfitnessplus.Listeners.PaginationScrollListener;
import com.ndfitnessplus.Model.EnquiryList;
import com.ndfitnessplus.Model.FollowupList;
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

public class RenewFollowupActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    RenewFollowupAdapter adapter;

    FollowupList subList;
    SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    ProgressBar progressBar;

    View nodata;
    public static String TAG = RenewFollowupActivity.class.getName();
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
    //Loading gif
    ViewDialog viewDialog;
    TextView todate,fromdate;
    ImageButton toDatebtn,fromDateBtn;
    Button BtnSearch;
    private int mYear, mMonth, mDay;
    TextView ttl_followups;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_renew_followup);
        initToolbar();
        initComponent();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.renew_followup));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private  void initComponent(){
        progressBar=findViewById(R.id.progressBar);
        swipeRefresh=findViewById(R.id.swipeRefresh);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        viewDialog = new ViewDialog(this);
        nodata=findViewById(R.id.nodata);
        inputsearch=(EditText)findViewById(R.id.inputsearchid);
        swipeRefresh.setOnRefreshListener(this);

        nodata=findViewById(R.id.nodata);
        ttl_followups=findViewById(R.id.ttl_renew_followups);

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


                DatePickerDialog datePickerDialog = new DatePickerDialog(RenewFollowupActivity.this,
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


                DatePickerDialog datePickerDialog = new DatePickerDialog(RenewFollowupActivity.this,
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
        inputsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                if (RenewFollowupActivity.this.adapter == null){
                    // some print statement saying it is null
                    Toast toast = Toast.makeText(RenewFollowupActivity.this,"no record found", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else
                {
                    isLoading = false;
                  int cnt=  RenewFollowupActivity.this.adapter.filter(String.valueOf(arg0));
                  ttl_followups.setText(String.valueOf(cnt));

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


            }
        });

        if (isOnline(RenewFollowupActivity.this)) {
            followupclass();// check login details are valid or not from server
        }
        else {
            Toast.makeText(RenewFollowupActivity.this, R.string.internet_unavailable, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(RenewFollowupActivity.this);
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
                Intent intent = new Intent(RenewFollowupActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onRefresh() {
        itemCount = 0;
        currentPage = PAGE_START;
        isLastPage = false;

    }

    // Asycc class for loading data for database
    private void followupclass() {
        RenewFollowupActivity.FollowupTrackclass ru = new RenewFollowupActivity.FollowupTrackclass();
        ru.execute("5");
    }



class FollowupTrackclass extends AsyncTask<String, Void, String> {

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
        FollowupDetails(response);

    }

    @Override
    protected String doInBackground(String... params) {
        HashMap<String, String> FollowupDetails = new HashMap<String, String>();
        FollowupDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(RenewFollowupActivity.this));
        Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(RenewFollowupActivity.this)));
        FollowupDetails.put("action","show_renew_followup_list");
        String domainurl=SharedPrefereneceUtil.getDomainUrl(RenewFollowupActivity.this);
        String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, FollowupDetails);
        return loginResult;
    }


}

    private void FollowupDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    progressBar.setVisibility(View.GONE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
                        ttl_followups.setText(String.valueOf(jsonArrayResult.length()));
                        ArrayList<FollowupList> item = new ArrayList<FollowupList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new FollowupList();

                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String name = jsonObj.getString("Name");
                                    String Contact = jsonObj.getString("Contact");
                                    String Followup_Date = jsonObj.getString("FollowupDate");
                                    String ExecutiveName = jsonObj.getString("ExecutiveName");
                                    String Rating = jsonObj.getString("Rating");
                                    String CallResponse = jsonObj.getString("CallResponse");
                                    String Comment = jsonObj.getString("Comment");
                                    String FollowupType = jsonObj.getString("FollowupType");
                                    String NextFollowup_Date = jsonObj.getString("NextFollowupDate");
                                    String Auto_Id = jsonObj.getString("Member_ID");
                                    String Image = jsonObj.getString("Image");


                                    subList.setName(name);
                                    subList.setRating(Rating);
                                    String cont=Utility.lastFour(Contact);
                                    subList.setContact(Contact);
                                    subList.setContactEncrypt(cont);
                                    subList.setCallRespond(CallResponse);
                                    subList.setExecutiveName(ExecutiveName);
                                    subList.setComment(Comment);
                                    subList.setFollowupType(FollowupType);
                                    String next_foll_date= Utility.formatDate(NextFollowup_Date);
                                    subList.setNextFollowupDate(next_foll_date);
                                    String foll_date= Utility.formatDate(Followup_Date);

                                    subList.setFollowupDate(foll_date);
                                    subList.setID(Auto_Id);
                                    subList.setImage(Image);
                                    item.add(subList);
                                    adapter = new RenewFollowupAdapter( item,RenewFollowupActivity.this);
                                    recyclerView.setAdapter(adapter);

                                }
                            }
                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    ttl_followups.setText("0");
                    nodata.setVisibility(View.VISIBLE);
                    swipeRefresh.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(RenewFollowupActivity.this);
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
        RenewFollowupActivity. SearchActiveMemberTrackclass ru = new RenewFollowupActivity. SearchActiveMemberTrackclass();
        ru.execute("5");
    }

    class  SearchActiveMemberTrackclass extends AsyncTask<String, Void, String> {


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
            Log.v(TAG, String.format("onPostExecute :: search_active_member_filter = %s", response));
            viewDialog.hideDialog();
            SearchActiveMemberDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String>  SearchActiveMemberDetails = new HashMap<String, String>();
            SearchActiveMemberDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(RenewFollowupActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(RenewFollowupActivity.this)));
            SearchActiveMemberDetails.put("to_date",todate.getText().toString());
            Log.v(TAG, String.format("doInBackground :: to_date = %s",todate.getText().toString() ));
            SearchActiveMemberDetails.put("from_date",fromdate.getText().toString());
            Log.v(TAG, String.format("doInBackground :: from_date = %s", fromdate.getText().toString()));
            SearchActiveMemberDetails.put("action","search_renew_followup_filter");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(RenewFollowupActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL,  SearchActiveMemberDetails);
            return loginResult;
        }


    }

    private void  SearchActiveMemberDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: search_active_member_filter = %s", jsonResponse));
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    swipeRefresh.setVisibility(View.VISIBLE);
                    nodata.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
                        ttl_followups.setText(String.valueOf(jsonArrayResult.length()));
                        ArrayList<FollowupList> item = new ArrayList<FollowupList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new FollowupList();

                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String name = jsonObj.getString("Name");
                                    String Contact = jsonObj.getString("Contact");
                                    String Followup_Date = jsonObj.getString("FollowupDate");
                                    String ExecutiveName = jsonObj.getString("ExecutiveName");
                                    String Rating = jsonObj.getString("Rating");
                                    String CallResponse = jsonObj.getString("CallResponse");
                                    String Comment = jsonObj.getString("Comment");
                                    String FollowupType = jsonObj.getString("FollowupType");
                                    String NextFollowup_Date = jsonObj.getString("NextFollowupDate");
                                    String Auto_Id = jsonObj.getString("Member_ID");
                                    String Image = jsonObj.getString("Image");

                                    subList.setName(name);
                                    subList.setRating(Rating);
                                    String cont=Utility.lastFour(Contact);
                                    subList.setContact(Contact);
                                    subList.setContactEncrypt(cont);
                                    subList.setCallRespond(CallResponse);
                                    subList.setExecutiveName(ExecutiveName);
                                    subList.setComment(Comment);
                                    subList.setFollowupType(FollowupType);
                                    String next_foll_date= Utility.formatDate(NextFollowup_Date);
                                    subList.setNextFollowupDate(next_foll_date);
                                    String foll_date= Utility.formatDate(Followup_Date);

                                    subList.setFollowupDate(foll_date);
                                    subList.setID(Auto_Id);
                                    subList.setImage(Image);
                                    item.add(subList);
                                    adapter = new RenewFollowupAdapter( item,RenewFollowupActivity.this);
                                    recyclerView.setAdapter(adapter);
                                }
                            }
                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    ttl_followups.setText("0");
                    nodata.setVisibility(View.VISIBLE);
                    swipeRefresh.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(RenewFollowupActivity.this);
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
    protected void onRestart() {
        super.onRestart();
        Intent intent=new Intent(RenewFollowupActivity.this,RenewFollowupActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(RenewFollowupActivity.this, NotificationActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(RenewFollowupActivity.this,NotificationActivity.class);
        startActivity(intent);
    }
}
