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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ndfitnessplus.Activity.EnquiryActivity;
import com.ndfitnessplus.Activity.EnquiryFilterActivity;
import com.ndfitnessplus.Activity.MainActivity;
import com.ndfitnessplus.Activity.NotificationActivity;
import com.ndfitnessplus.Activity.OtherFollowupFilterActivity;
import com.ndfitnessplus.Adapter.EnquiryAdapter;
import com.ndfitnessplus.Adapter.FollowupAdapter;
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

public class OtherFollowupActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    FollowupAdapter adapter;

    FollowupList subList;
    SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    ProgressBar progressBar;
    View nodata,frame;
    //No internet connectioin
    View noInternet;
    private ProgressBar progress_bar;
    private LinearLayout lyt_no_connection;
    public static String TAG = OtherFollowupActivity.class.getName();
    private ProgressDialog pd;

    private boolean isLoading = false;

    private EditText inputsearch;
    //Loading gif
    ViewDialog viewDialog;
    //Search ...
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
        setContentView(R.layout.activity_other_followup);
        initToolbar();
        initComponent();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.other_followup));
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
        ttl_followups=findViewById(R.id.ttl_foll);
        frame=findViewById(R.id.main_frame);
        noInternet=findViewById(R.id.no_internet);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        inputsearch=(EditText)findViewById(R.id.inputsearchid);
        swipeRefresh.setOnRefreshListener(this);

        inputsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                if (OtherFollowupActivity.this.adapter == null){
                    // some print statement saying it is null
                    Toast toast = Toast.makeText(OtherFollowupActivity.this,"no record found", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else
                {
                    isLoading = false;
                   int cnt= OtherFollowupActivity.this.adapter.filter(String.valueOf(arg0));
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

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        if (args != null) {
            ArrayList<FollowupList> filterArrayList = (ArrayList<FollowupList>) args.getSerializable("filter_array_list");
            progressBar.setVisibility(View.GONE);
            int length=filterArrayList.size();
            ttl_followups.setText(String.valueOf(length));
            adapter = new FollowupAdapter( filterArrayList,OtherFollowupActivity.this);
            recyclerView.setAdapter(adapter);
        }else {
            if (isOnline(OtherFollowupActivity.this)) {
                followupclass();// check login details are valid or not from server
            } else {
                frame.setVisibility(View.GONE);
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter_action_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_home) {
                Intent intent = new Intent(OtherFollowupActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
        } else if (id == R.id.action_filter) {
            Intent intent = new Intent(OtherFollowupActivity.this, OtherFollowupFilterActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(OtherFollowupActivity.this, NotificationActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(OtherFollowupActivity.this,NotificationActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {

    }
    // Asycc class for loading data for database
    private void followupclass() {
        OtherFollowupActivity.FollowupTrackclass ru = new OtherFollowupActivity.FollowupTrackclass();
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
            FollowupDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(OtherFollowupActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(OtherFollowupActivity.this)));
            FollowupDetails.put("action","show_other_followup_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(OtherFollowupActivity.this);
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
                        int count=0;
                        ArrayList<FollowupList> item = new ArrayList<FollowupList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new FollowupList();

                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String name = jsonObj.getString("Name");
                                    String Rating = jsonObj.getString("Rating");
                                    String Contact = jsonObj.getString("Contact");
                                    String CallResponse = jsonObj.getString("CallResponse");
                                    String ExecutiveName = jsonObj.getString("ExecutiveName");
                                    String Comment = jsonObj.getString("Comment");
                                    String FollowupType = jsonObj.getString("FollowupType");
                                    String NextFollowup_Date = jsonObj.getString("NextFollowupDate");
                                    String Member_ID = jsonObj.getString("Member_ID");
                                    String Followup_Date = jsonObj.getString("FollowupDate");


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
                                    subList.setID(Member_ID);
                                    subList.setImage("");
                                    item.add(subList);
                                    adapter = new FollowupAdapter( item,OtherFollowupActivity.this);
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
                    recyclerView.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(OtherFollowupActivity.this);
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
        Intent intent=new Intent(OtherFollowupActivity.this,OtherFollowupActivity.class);
        startActivity(intent);
    }
}
