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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ndfitnessplus.Activity.MainActivity;
import com.ndfitnessplus.Activity.NotificationActivity;
import com.ndfitnessplus.Adapter.FollowupAdapter;
import com.ndfitnessplus.Adapter.RenewFollowupAdapter;
import com.ndfitnessplus.Listeners.PaginationScrollListener;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    RenewFollowupActivity.this.adapter.filter(String.valueOf(arg0));

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
//        adapter = new EnquiryAdapter( new ArrayList<EnquiryList>(),RenewFollowupActivity.this);
//        recyclerView.setAdapter(adapter);

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
        //adapter.clear();
        //preparedListItem();

    }
    private void showProgressDialog() {
        Log.v(TAG, String.format("showProgressDialog"));
        pd = new ProgressDialog(RenewFollowupActivity.this);
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
        RenewFollowupActivity.FollowupTrackclass ru = new RenewFollowupActivity.FollowupTrackclass();
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
        FollowupDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(RenewFollowupActivity.this));
        Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(RenewFollowupActivity.this)));
        FollowupDetails.put("action","show_renew_followup_list");
        String domainurl=SharedPrefereneceUtil.getDomainUrl(RenewFollowupActivity.this);
        String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, FollowupDetails);
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
