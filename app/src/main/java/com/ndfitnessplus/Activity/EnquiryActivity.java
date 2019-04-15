package com.ndfitnessplus.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ndfitnessplus.Activity.Notification.StaffBirthdayActivity;
import com.ndfitnessplus.Adapter.BranchSelectionAdapter;
import com.ndfitnessplus.Adapter.EnquiryAdapter;
import com.ndfitnessplus.Listeners.PaginationScrollListener;
import com.ndfitnessplus.Model.BranchList;
import com.ndfitnessplus.Model.EnquiryList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.ndfitnessplus.Utility.HTTPRequestQueue.isOnline;

public class EnquiryActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    EnquiryAdapter adapter;

    EnquiryList subList;
    SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    ProgressBar progressBar;

    View nodata,frame;
    //No internet connectioin
    View noInternet;
    private ProgressBar progress_bar;
    private LinearLayout lyt_no_connection;
    ArrayList<EnquiryList> branchList;
    public static String TAG = EnquiryActivity.class.getName();
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
    TextView total_enquiry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiry);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.enquiry));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){
        FloatingActionButton addenquiry=findViewById(R.id.fab);
        progressBar=findViewById(R.id.progressBar);
        swipeRefresh=findViewById(R.id.swipeRefresh);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        total_enquiry=findViewById(R.id.ttl_enq);
        nodata=findViewById(R.id.nodata);
        frame=findViewById(R.id.main_frame);
        noInternet=findViewById(R.id.no_internet);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        lyt_no_connection = (LinearLayout) findViewById(R.id.lyt_no_connection);

        progress_bar.setVisibility(View.GONE);
        lyt_no_connection.setVisibility(View.VISIBLE);
//        adapter = new EnquiryAdapter( new ArrayList<EnquiryList>(),EnquiryActivity.this);
//        recyclerView.setAdapter(adapter);


        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        if (args != null) {
            ArrayList<EnquiryList> filterArrayList = (ArrayList<EnquiryList>) args.getSerializable("filter_array_list");
            progressBar.setVisibility(View.GONE);
            int length=filterArrayList.size();
            total_enquiry.setText(String.valueOf(length));
            adapter = new EnquiryAdapter( filterArrayList,EnquiryActivity.this);
            recyclerView.setAdapter(adapter);
        }else{
            if (isOnline(EnquiryActivity.this)) {
                enquiryclass();// check login details are valid or not from server
            }
            else {
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
                //Toast.makeText(EnquiryActivity.this, R.string.internet_unavailable, Toast.LENGTH_LONG).show();
//                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EnquiryActivity.this);
//                builder.setMessage(R.string.internet_unavailable);
//                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.dismiss();
//                    }
//                });
//                android.app.AlertDialog dialog = builder.create();
//                dialog.setCancelable(false);
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                dialog.show();

            }
        }



        addenquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EnquiryActivity.this,AddEnquiryActivity.class);
                startActivity(intent);
            }
        });

        inputsearch=(EditText)findViewById(R.id.inputsearchid);
        search=findViewById(R.id.search);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enquirysearchclass();
            }
        });

        inputsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(final Editable arg0) {
                // TODO Auto-generated method stub
                if (EnquiryActivity.this.adapter == null){
                    // some print statement saying it is null
                    Toast toast = Toast.makeText(EnquiryActivity.this,"no record found", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else
                {
                    isLoading = false;
                    EnquiryActivity.this.adapter.filter(String.valueOf(arg0));



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
                    preparedListItem();
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


    private void preparedListItem() {
        offset=offset+100;

        if (isOnline(EnquiryActivity.this)) {
            enquiryoffsetclass();// check login details are valid or not from server
        }
        else {
            //Toast.makeText(EnquiryActivity.this, R.string.internet_unavailable, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EnquiryActivity.this);
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
//        final ArrayList<EnquiryList> items = new ArrayList<>();
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//
//                for (int i = 0; i < 5; i++) {
//                    itemCount++;
//                    Log.d(TAG, "prepare called : " + itemCount);
//
//                    EnquiryList postItem = subListArrayList.get(i);
//                    subList.setExecutiveName(postItem.getExecutiveName());
//                    subList.setName(postItem.getName());
//                    subList.setGender(postItem.getGender());
//                    subList.setContact(postItem.getContact());
//                    subList.setAddress(postItem.getAddress());
//                    subList.setComment(postItem.getComment());
//                    subList.setNextFollowUpDate(postItem.getNextFollowUpDate());
//                    items.add(subList);
//                }
//                if (currentPage != PAGE_START) adapter.removeLoading();
//                adapter.addAll(items);
//                swipeRefresh.setRefreshing(false);
//                if (currentPage < totalPage) adapter.addLoading();
//                else isLastPage = true;
//                isLoading = false;
//
//            }
//        }, 2000);
    }
//Showing progress dialog
    private void showProgressDialog() {
        Log.v(TAG, String.format("showProgressDialog"));
        pd = new ProgressDialog(EnquiryActivity.this);
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
        EnquiryActivity.EnquiryTrackclass ru = new EnquiryActivity.EnquiryTrackclass();
        ru.execute("5");
    }

    @Override
    public void onRefresh() {
        itemCount = 0;
        currentPage = PAGE_START;
        Log.d(TAG, "currentPage: " + currentPage);
        isLastPage = false;
        adapter.clear();
        preparedListItem();

    }

    class EnquiryTrackclass extends AsyncTask<String, Void, String> {

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
            //dismissProgressDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            EnquiryDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> EnquiryDetails = new HashMap<String, String>();
            EnquiryDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(EnquiryActivity.this));
            EnquiryDetails.put("offset", String.valueOf(offset));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(EnquiryActivity.this)));
            EnquiryDetails.put("action","show_enquiry_list");
            String loginResult = ruc.sendPostRequest(ServiceUrls.LOGIN_URL, EnquiryDetails);
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
                    String ttl_enq = object.getString("total_enquiry_count");
                    total_enquiry.setText(ttl_enq);
                    progressBar.setVisibility(View.GONE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
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
                                    String NextFollowup_Date = jsonObj.getString("NextFollowup_Date");
                                    String Enquiry_ID = jsonObj.getString("Enquiry_ID");
                                    String Image = jsonObj.getString("Image");

                                  //  for (int j = 0; j < 5; j++) {
                                        itemCount++;
                                        Log.d(TAG, "run: " + itemCount);
                                    subList.setName(name);
                                    subList.setGender(gender);
                                    subList.setContact(Contact);
                                    subList.setAddress(address);
                                    subList.setExecutiveName(ExecutiveName);
                                    subList.setComment(Comment);
                                    String next_foll_date= Utility.formatDate(NextFollowup_Date);
                                    subList.setNextFollowUpDate(next_foll_date);
                                    subList.setID(Enquiry_ID);
                                    Image.replace("\"", "");
                                    subList.setImage(Image);
                                    //Toast.makeText(EnquiryActivity.this, "followup date: "+next_foll_date, Toast.LENGTH_SHORT).show();

                                    //Toast.makeText(MainActivity.this, "j "+j, Toast.LENGTH_SHORT).show();
                                    item.add(subList);
                                    adapter = new EnquiryAdapter( item,EnquiryActivity.this);
                                    recyclerView.setAdapter(adapter);

                                }
                            }
                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    nodata.setVisibility(View.VISIBLE);
                    frame.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EnquiryActivity.this);
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
        EnquiryActivity.EnquiryOffsetTrackclass ru = new EnquiryActivity.EnquiryOffsetTrackclass();
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
            EnquiryOffsetDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(EnquiryActivity.this));
            EnquiryOffsetDetails.put("offset", String.valueOf(offset));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(EnquiryActivity.this)));
            EnquiryOffsetDetails.put("action","show_enquiry_list");
            String loginResult = ruc.sendPostRequest(ServiceUrls.LOGIN_URL, EnquiryOffsetDetails);
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
                        ArrayList<EnquiryList> subListArrayList = new ArrayList<EnquiryList>();
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
                                    String NextFollowup_Date = jsonObj.getString("NextFollowup_Date");
                                    String Enquiry_ID = jsonObj.getString("Enquiry_ID");
                                    //  for (int j = 0; j < 5; j++) {
                                    itemCount++;
                                    Log.d(TAG, "run offset: " + itemCount);
                                    subList.setName(name);
                                    subList.setGender(gender);
                                    subList.setContact(Contact);
                                    subList.setAddress(address);
                                    subList.setExecutiveName(ExecutiveName);
                                    subList.setComment(Comment);
                                    String next_foll_date= Utility.formatDate(NextFollowup_Date);
                                    subList.setNextFollowUpDate(next_foll_date);
                                    subList.setID(Enquiry_ID);

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
                recyclerView.setVisibility(View.GONE);
                frame.setVisibility(View.VISIBLE);
            }
        }
    }
    private void enquirysearchclass() {
        EnquiryActivity.EnquirySearchTrackclass ru = new EnquiryActivity.EnquirySearchTrackclass();
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
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            EnquirySearchDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> EnquirySearchDetails = new HashMap<String, String>();
            EnquirySearchDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(EnquiryActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(EnquiryActivity.this)));
            EnquirySearchDetails.put("action","show_search_enquiry");
            String loginResult = ruc.sendPostRequest(ServiceUrls.LOGIN_URL, EnquirySearchDetails);
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
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }
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
                                    String NextFollowup_Date = jsonObj.getString("NextFollowup_Date");
                                    String Enquiry_ID = jsonObj.getString("Enquiry_ID");
                                    //  for (int j = 0; j < 5; j++) {
                                    itemCount++;
                                    Log.d(TAG, "run offset: " + itemCount);
                                    subList.setName(name);
                                    subList.setGender(gender);
                                    subList.setContact(Contact);
                                    subList.setAddress(address);
                                    subList.setExecutiveName(ExecutiveName);
                                    subList.setComment(Comment);
                                    String next_foll_date= Utility.formatDate(NextFollowup_Date);
                                    subList.setNextFollowUpDate(next_foll_date);
                                    subList.setID(Enquiry_ID);

                                    subListArrayList.add(subList);
                                    int count =EnquiryActivity.this.adapter.search(inputsearch.getText().toString(),subListArrayList);
                                    //Toast.makeText(EnquiryActivity.this,count, Toast.LENGTH_SHORT).show();
                                    System.out.println("count:"+count);
                                    if(count == 0){
                                       Toast.makeText(EnquiryActivity.this,"no record found", Toast.LENGTH_SHORT).show();
                                    }
                                    total_enquiry.setText(String.valueOf(count));


                                }
                            }

                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    // nodata.setVisibility(View.VISIBLE);

                    progressBar.setVisibility(View.GONE);

                    //recyclerView.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
                recyclerView.setVisibility(View.GONE);
                frame.setVisibility(View.VISIBLE);
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
            Intent intent = new Intent(EnquiryActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_filter) {
            Intent intent = new Intent(EnquiryActivity.this, EnquiryFilterActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(EnquiryActivity.this, MainActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(EnquiryActivity.this,MainActivity.class);
        startActivity(intent);
    }
}
