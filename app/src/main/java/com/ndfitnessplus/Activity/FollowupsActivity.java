package com.ndfitnessplus.Activity;

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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ndfitnessplus.Activity.Notification.TodaysEnrollmentActivity;
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

import java.util.ArrayList;
import java.util.HashMap;

import static com.ndfitnessplus.Utility.HTTPRequestQueue.isOnline;

public class FollowupsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    FollowupAdapter adapter;
    ArrayList<FollowupList> subListArrayList = new ArrayList<FollowupList>();
    FollowupList subList;
    SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    ProgressBar progressBar;

    View nodata;
    public static String TAG = FollowupsActivity.class.getName();
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followups);
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
        viewDialog = new ViewDialog(this);

        nodata=findViewById(R.id.nodata);
//        adapter = new EnquiryAdapter( new ArrayList<EnquiryList>(),FollowupsActivity.this);
//        recyclerView.setAdapter(adapter);

            if (isOnline(FollowupsActivity.this)) {
                followupclass();// check login details are valid or not from server
            }
            else {
                Toast.makeText(FollowupsActivity.this, R.string.internet_unavailable, Toast.LENGTH_LONG).show();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(FollowupsActivity.this);
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

        inputsearch=(EditText)findViewById(R.id.inputsearchid);

        inputsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                if (FollowupsActivity.this.adapter == null){
                    // some print statement saying it is null
                    Toast toast = Toast.makeText(FollowupsActivity.this,"no record found", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else
                {
                    FollowupsActivity.this.adapter.filter(String.valueOf(arg0));

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
        offset=offset+10;

        if (isOnline(FollowupsActivity.this)) {
            followupoffsetclass();// check login details are valid or not from server
        }
        else {
            Toast.makeText(FollowupsActivity.this, R.string.internet_unavailable, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(FollowupsActivity.this);
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
        pd = new ProgressDialog(FollowupsActivity.this);
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
    private void followupclass() {
        FollowupsActivity.FollowupTrackclass ru = new FollowupsActivity.FollowupTrackclass();
        ru.execute("5");
    }

    @Override
    public void onRefresh() {
        itemCount = 0;
        currentPage = PAGE_START;
        isLastPage = false;
        adapter.clear();
        preparedListItem();

    }

    class FollowupTrackclass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
//            showProgressDialog();
            viewDialog.showDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
//            dismissProgressDialog();
            viewDialog.hideDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            FollowupDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> FollowupDetails = new HashMap<String, String>();
            FollowupDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(FollowupsActivity.this));
            FollowupDetails.put("offset", String.valueOf(offset));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getCompanyAutoId(FollowupsActivity.this)));
            FollowupDetails.put("action","show_followup_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(FollowupsActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, FollowupDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }

    private void FollowupDetails(String jsonResponse) {

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
                        int count=0;
                        ArrayList<FollowupList> item = new ArrayList<FollowupList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                            if(jsonArrayResult.length()<10){
                                count=jsonArrayResult.length();
                            }else{
                                count=10;
                            }
                            for (int i = 0; i < count; i++) {


                                subList = new FollowupList();
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
                                    String Followup_Date = jsonObj.getString("NextFollowup_Date");
                                    //  for (int j = 0; j < 5; j++) {
                                    itemCount++;
                                    Log.d(TAG, "run: " + itemCount);

                                    //  for (int j = 0; j < 5; j++) {
                                    itemCount++;
                                    Log.d(TAG, "run offset: " + itemCount);
                                    subList.setName(name);
                                    subList.setRating(gender);
                                    subList.setContact(Contact);
                                    subList.setCallRespond(address);
                                    subList.setExecutiveName(ExecutiveName);
                                    subList.setComment(Comment);
                                    subList.setFollowupType(Comment);
                                    String next_foll_date= Utility.formatDate(NextFollowup_Date);
                                    subList.setNextFollowupDate(next_foll_date);
                                    String foll_date= Utility.formatDate(Followup_Date);
                                    subList.setFollowupDate(foll_date);
                                    subList.setID(Enquiry_ID);

                                    adapter = new FollowupAdapter( item,FollowupsActivity.this);
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
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(FollowupsActivity.this);
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
    private void followupoffsetclass() {
        FollowupsActivity.FollowupOffsetTrackclass ru = new FollowupsActivity.FollowupOffsetTrackclass();
        ru.execute("5");
    }
    class FollowupOffsetTrackclass extends AsyncTask<String, Void, String> {

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
            FollowupOffsetDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> FollowupOffsetDetails = new HashMap<String, String>();
            FollowupOffsetDetails.put("comp_id", SharedPrefereneceUtil.getCompanyAutoId(FollowupsActivity.this));
            FollowupOffsetDetails.put("offset", String.valueOf(offset));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getCompanyAutoId(FollowupsActivity.this)));
            FollowupOffsetDetails.put("action","show_enquiry");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(FollowupsActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, FollowupOffsetDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }

    private void FollowupOffsetDetails(String jsonResponse) {

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
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new FollowupList();
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
                                    String Followup_Date = jsonObj.getString("NextFollowup_Date");
                                    String Enquiry_ID = jsonObj.getString("Enquiry_ID");
                                    //  for (int j = 0; j < 5; j++) {
                                    itemCount++;
                                    Log.d(TAG, "run offset: " + itemCount);
                                    subList.setName(name);
                                    subList.setRating(gender);
                                    subList.setContact(Contact);
                                    subList.setCallRespond(address);
                                    subList.setExecutiveName(ExecutiveName);
                                    subList.setComment(Comment);
                                    subList.setFollowupType(Comment);
                                    String next_foll_date= Utility.formatDate(NextFollowup_Date);
                                    subList.setNextFollowupDate(next_foll_date);
                                    String foll_date= Utility.formatDate(Followup_Date);
                                    subList.setFollowupDate(foll_date);
                                    subList.setID(Enquiry_ID);

                                    //Toast.makeText(FollowupsActivity.this, "followup date: "+next_foll_date, Toast.LENGTH_SHORT).show();
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
                    nodata.setVisibility(View.VISIBLE);

                    progressBar.setVisibility(View.GONE);
                    //if (currentPage != PAGE_START)
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
                nodata.setVisibility(View.VISIBLE);
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
            Intent intent = new Intent(FollowupsActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_filter) {
            Intent intent = new Intent(FollowupsActivity.this, EnquiryFilterActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(FollowupsActivity.this, MainActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(FollowupsActivity.this,MainActivity.class);
        startActivity(intent);
    }

}
