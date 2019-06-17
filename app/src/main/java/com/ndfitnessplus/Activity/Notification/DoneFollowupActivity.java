package com.ndfitnessplus.Activity.Notification;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ndfitnessplus.Activity.EnquiryActivity;
import com.ndfitnessplus.Activity.LoginActivity;
import com.ndfitnessplus.Activity.MainActivity;
import com.ndfitnessplus.Activity.NotificationActivity;
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

public class DoneFollowupActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    FollowupAdapter adapter;

    FollowupList subList;
    SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    ProgressBar progressBar;

    View nodata;
    public static String TAG = DoneFollowupActivity.class.getName();
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done_followup);
        initToolbar();
        initComponent();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.done_followup));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private  void initComponent(){
        progressBar=findViewById(R.id.progressBar);
        swipeRefresh=findViewById(R.id.swipeRefresh);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        swipeRefresh.setOnRefreshListener(this);
        nodata=findViewById(R.id.nodata);
        inputsearch=(EditText)findViewById(R.id.inputsearchid);
        search=findViewById(R.id.search);
        viewDialog = new ViewDialog(this);
        inputsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                if (DoneFollowupActivity.this.adapter == null){
                    // some print statement saying it is null
                    Toast toast = Toast.makeText(DoneFollowupActivity.this,"no record found", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else
                {
                    isLoading = false;
                    DoneFollowupActivity.this.adapter.filter(String.valueOf(arg0));

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
                    followupclass();
                }

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputsearch.getText().length()>0){
                    followupsearchclass();
                }else{
                    Toast.makeText(DoneFollowupActivity.this,"Please enter text to search", Toast.LENGTH_LONG).show();
                }

            }
        });
//        adapter = new EnquiryAdapter( new ArrayList<EnquiryList>(),DoneFollowupActivity.this);
//        recyclerView.setAdapter(adapter);

        if (isOnline(DoneFollowupActivity.this)) {
            followupclass();// check login details are valid or not from server
        }
        else {
            Toast.makeText(DoneFollowupActivity.this, R.string.internet_unavailable, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DoneFollowupActivity.this);
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

        if (isOnline(DoneFollowupActivity.this)) {
            followupoffsetclass();// check login details are valid or not from server
        }
        else {
            Toast.makeText(DoneFollowupActivity.this, R.string.internet_unavailable, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DoneFollowupActivity.this);
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

    @Override
    public void onRefresh() {
        itemCount = 0;
        currentPage = PAGE_START;
        isLastPage = false;
        //adapter.clear();
        preparedListItem();
    }
    private void showProgressDialog() {
        Log.v(TAG, String.format("showProgressDialog"));
        pd = new ProgressDialog(DoneFollowupActivity.this);
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
        DoneFollowupActivity.FollowupTrackclass ru = new DoneFollowupActivity.FollowupTrackclass();
        ru.execute("5");
    }



    class FollowupTrackclass extends AsyncTask<String, Void, String> {

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
            FollowupDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> FollowupDetails = new HashMap<String, String>();
            FollowupDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(DoneFollowupActivity.this));
            FollowupDetails.put("offset", String.valueOf(offset));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(DoneFollowupActivity.this)));
            Log.v(TAG, String.format("doInBackground :: offset  = %s",offset ));
            FollowupDetails.put("action","show_done_followup");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(DoneFollowupActivity.this);
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
                            if(jsonArrayResult.length()<100){
                                count=jsonArrayResult.length();
                            }else{
                                count=100;
                            }
                            for (int i = 0; i < count; i++) {


                                subList = new FollowupList();
                                Log.d(TAG, "i: " + i);

                                Log.v(TAG, "JsonResponseOpeartion ::");
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
                                    String Auto_Id = jsonObj.getString("Member_ID");
                                    String Followup_Date = jsonObj.getString("FollowupDate");
                                    //  for (int j = 0; j < 5; j++) {
                                    Log.d(TAG, "next followup date: " + NextFollowup_Date);
                                    Log.d(TAG, "Followup date: " + Followup_Date);

                                    //  for (int j = 0; j < 5; j++) {

                                    subList.setName(name);
                                    subList.setRating(Rating);
                                    subList.setContact(Contact);
                                    subList.setCallRespond(CallResponse);
                                    subList.setExecutiveName(ExecutiveName);
                                    subList.setComment(Comment);
                                    subList.setFollowupType(FollowupType);
                                    Log.d(TAG, "FollowupType: " + FollowupType);
                                    String next_foll_date= Utility.formatDate(NextFollowup_Date);
                                    Log.d(TAG, "converted next followup date: " + next_foll_date);
                                    subList.setNextFollowupDate(next_foll_date);
                                    String foll_date= Utility.formatDate(Followup_Date);

                                    Log.d(TAG, "converted Followup date: " + foll_date);
                                    subList.setFollowupDate(foll_date);
                                    subList.setID(Auto_Id);
                                    item.add(subList);
                                    adapter = new FollowupAdapter( item,DoneFollowupActivity.this);
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
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DoneFollowupActivity.this);
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
        DoneFollowupActivity.FollowupOffsetTrackclass ru = new DoneFollowupActivity.FollowupOffsetTrackclass();
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
            FollowupOffsetDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(DoneFollowupActivity.this));
            FollowupOffsetDetails.put("offset", String.valueOf(offset));
            Log.v(TAG, String.format("doInBackground :: offset company id = %s", SharedPrefereneceUtil.getSelectedBranchId(DoneFollowupActivity.this)));
            FollowupOffsetDetails.put("action","show_done_followup");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(DoneFollowupActivity.this);
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
                        ArrayList<FollowupList> subListArrayList = new ArrayList<FollowupList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new FollowupList();
                                Log.d(TAG, "i: " + i);
                                // Log.d(TAG, "run: " + itemCount);
                                Log.v(TAG, "JsonResponseOpeartion ::");
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
                                    String Auto_Id = jsonObj.getString("Member_ID");
                                    String Followup_Date = jsonObj.getString("FollowupDate");
                                    //  for (int j = 0; j < 5; j++) {
                                    Log.d(TAG, "next followup date: " + NextFollowup_Date);
                                    Log.d(TAG, "Followup date: " + Followup_Date);

                                    //  for (int j = 0; j < 5; j++) {

                                    subList.setName(name);
                                    subList.setRating(Rating);
                                    subList.setContact(Contact);
                                    subList.setCallRespond(CallResponse);
                                    subList.setExecutiveName(ExecutiveName);
                                    subList.setComment(Comment);
                                    subList.setFollowupType(FollowupType);
                                    String next_foll_date= Utility.formatDate(NextFollowup_Date);
                                    Log.d(TAG, "converted next followup date: " + next_foll_date);
                                    subList.setNextFollowupDate(next_foll_date);
                                    String foll_date= Utility.formatDate(Followup_Date);

                                    Log.d(TAG, "converted Followup date: " + foll_date);
                                    subList.setFollowupDate(foll_date);
                                    subList.setID(Auto_Id);
                                    //Toast.makeText(DoneFollowupActivity.this, "followup date: "+next_foll_date, Toast.LENGTH_SHORT).show();
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
//                recyclerView.setVisibility(View.GONE);
//                nodata.setVisibility(View.VISIBLE);
            }
        }
    }
    private void followupsearchclass() {
        DoneFollowupActivity.FollowupSearchTrackclass ru = new DoneFollowupActivity.FollowupSearchTrackclass();
        ru.execute("5");
    }
    class FollowupSearchTrackclass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
          //  showProgressDialog();
            viewDialog.showDialog();

        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
          //  dismissProgressDialog();
            viewDialog.hideDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            FollowupSearchDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> FollowupSearchDetails = new HashMap<String, String>();
            FollowupSearchDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(DoneFollowupActivity.this));
            FollowupSearchDetails.put("text", inputsearch.getText().toString());
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(DoneFollowupActivity.this)));
            Log.v(TAG, String.format("doInBackground :: text = %s", inputsearch.getText().toString()));
            FollowupSearchDetails.put("action","show_search_followup");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(DoneFollowupActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, FollowupSearchDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }

    private void FollowupSearchDetails(String jsonResponse) {

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
                        ArrayList<FollowupList> subListArrayList = new ArrayList<FollowupList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new FollowupList();
                                Log.d(TAG, "i: " + i);
                                // Log.d(TAG, "run: " + itemCount);
                                Log.v(TAG, "JsonResponseOpeartion ::");
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
                                    String Auto_Id = jsonObj.getString("Member_ID");
                                    String Followup_Date = jsonObj.getString("FollowupDate");
                                    //  for (int j = 0; j < 5; j++) {
                                    Log.d(TAG, "next followup date: " + NextFollowup_Date);
                                    Log.d(TAG, "Followup date: " + Followup_Date);

                                    //  for (int j = 0; j < 5; j++) {

                                    subList.setName(name);
                                    subList.setRating(Rating);
                                    subList.setContact(Contact);
                                    subList.setCallRespond(CallResponse);
                                    subList.setExecutiveName(ExecutiveName);
                                    subList.setComment(Comment);
                                    subList.setFollowupType(FollowupType);
                                    String next_foll_date= Utility.formatDate(NextFollowup_Date);
                                    Log.d(TAG, "converted next followup date: " + next_foll_date);
                                    subList.setNextFollowupDate(next_foll_date);
                                    String foll_date= Utility.formatDate(Followup_Date);

                                    Log.d(TAG, "converted Followup date: " + foll_date);
                                    subList.setFollowupDate(foll_date);
                                    subList.setID(Auto_Id);

                                    subListArrayList.add(subList);
                                    adapter = new FollowupAdapter( subListArrayList,DoneFollowupActivity.this);
                                    recyclerView.setAdapter(adapter);
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
                //frame.setVisibility(View.VISIBLE);
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
                Intent intent = new Intent(DoneFollowupActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent=new Intent(DoneFollowupActivity.this,DoneFollowupActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(DoneFollowupActivity.this, NotificationActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(DoneFollowupActivity.this,NotificationActivity.class);
        startActivity(intent);
    }
}
