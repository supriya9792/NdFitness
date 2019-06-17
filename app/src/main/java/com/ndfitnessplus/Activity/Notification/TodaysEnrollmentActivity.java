package com.ndfitnessplus.Activity.Notification;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ndfitnessplus.Activity.EnrollmentActivity;
import com.ndfitnessplus.Activity.LoginActivity;
import com.ndfitnessplus.Activity.MainActivity;
import com.ndfitnessplus.Activity.NotificationActivity;
import com.ndfitnessplus.Adapter.MemberAdapter;
import com.ndfitnessplus.Listeners.PaginationScrollListener;
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

import java.util.ArrayList;
import java.util.HashMap;

import static com.ndfitnessplus.Utility.HTTPRequestQueue.isOnline;

public class TodaysEnrollmentActivity extends AppCompatActivity {
    MemberAdapter adapter;

    MemberDataList subList;
    SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    ProgressBar progressBar;

    View nodata,frame;
    //No internet connectioin
    View noInternet;
    private LinearLayout lyt_no_connection;
    public static String TAG = EnrollmentActivity.class.getName();
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


        noInternet=findViewById(R.id.no_internet);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        lyt_no_connection = (LinearLayout) findViewById(R.id.lyt_no_connection);
       // swipeRefresh.setOnRefreshListener(this);
        progressBar.setVisibility(View.VISIBLE);
        lyt_no_connection.setVisibility(View.VISIBLE);
//        adapter = new EnquiryAdapter( new ArrayList<EnquiryList>(),EnquiryActivity.this);
//        recyclerView.setAdapter(adapter);


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
                            //isLoading = false;
                            TodaysEnrollmentActivity.this.adapter.filter(String.valueOf(arg0));

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
            }
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
            //showProgressDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            //dismissProgressDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            EnrollmentDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> EnrollmentDetails = new HashMap<String, String>();
            EnrollmentDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(TodaysEnrollmentActivity.this));
            EnrollmentDetails.put("offset", String.valueOf(offset));
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
                   // total_enrollment.setText(ttl_enq);
                    progressBar.setVisibility(View.GONE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }
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


                                    //  for (int j = 0; j < 5; j++) {
                                    itemCount++;
                                    Log.d(TAG, "run: " + itemCount);
                                    subList.setName(name);
                                    subList.setGender(gender);
                                    subList.setContact(Contact);
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
                    nodata.setVisibility(View.VISIBLE);
                   // swipeRefresh.setVisibility(View.GONE);
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

    @Override
    protected void onRestart() {
        super.onRestart();
        //swipeRefresh.setRefreshing(false);
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
