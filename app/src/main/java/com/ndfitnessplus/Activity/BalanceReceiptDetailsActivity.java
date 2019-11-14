package com.ndfitnessplus.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ndfitnessplus.Activity.Notification.TodaysEnrollmentActivity;
import com.ndfitnessplus.Adapter.BalanceReceiptAdapter;
import com.ndfitnessplus.Listeners.PaginationScrollListener;
import com.ndfitnessplus.Model.CourseList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.Utility;
import com.ndfitnessplus.Utility.ViewDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import static com.ndfitnessplus.Utility.HTTPRequestQueue.isOnline;

public class BalanceReceiptDetailsActivity extends AppCompatActivity  implements SwipeRefreshLayout.OnRefreshListener {
    BalanceReceiptAdapter adapter;

    CourseList subList;
    SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    ProgressBar progressBar;

    View nodata,frame;
    //No internet connectioin
    View noInternet;
    private ProgressBar progress_bar;
    private LinearLayout lyt_no_connection;

    public static String TAG = BalanceReceiptDetailsActivity.class.getName();
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
    TextView total_balance;
    //Loading gif
    ViewDialog viewDialog;
    int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_balance_receipt_details);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.balance_receipt));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){
        FloatingActionButton addbalance=findViewById(R.id.fab);
        progressBar=findViewById(R.id.progressBar);
        swipeRefresh=findViewById(R.id.swipeRefresh);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        viewDialog = new ViewDialog(this);

        total_balance=findViewById(R.id.ttl_balance);
        nodata=findViewById(R.id.nodata);
        frame=findViewById(R.id.main_frame);
        noInternet=findViewById(R.id.no_internet);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        lyt_no_connection = (LinearLayout) findViewById(R.id.lyt_no_connection);
        swipeRefresh.setOnRefreshListener(this);
        progress_bar.setVisibility(View.GONE);
        lyt_no_connection.setVisibility(View.VISIBLE);
//        adapter = new BalanceReceiptAdapter( new ArrayList<EnquiryList>(),CourseActivity.this);
//        recyclerView.setAdapter(adapter);


        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        if (args != null) {
            ArrayList<CourseList> filterArrayList = (ArrayList<CourseList>) args.getSerializable("filter_array_list");
            String bal=intent.getStringExtra("balance");
            progressBar.setVisibility(View.GONE);
            count=filterArrayList.size();
            total_balance.setText(String.valueOf(bal));
            adapter = new BalanceReceiptAdapter( filterArrayList,BalanceReceiptDetailsActivity.this);
            recyclerView.setAdapter(adapter);
        }else{
            if (isOnline(BalanceReceiptDetailsActivity.this)) {
                balanceReceiptclass();// check login details are valid or not from server
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

            }
        }



        addbalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(BalanceReceiptDetailsActivity.this,BalanceReceiptActivity.class);
                startActivity(intent);
            }
        });

        inputsearch=(EditText)findViewById(R.id.inputsearchid);
        search=findViewById(R.id.search);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputsearch.getText().length()>0){
                    balanceReceiptsearchclass();
                }else{
                    Toast.makeText(BalanceReceiptDetailsActivity.this,"Please enter text to search", Toast.LENGTH_LONG).show();
                }

            }
        });

        inputsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(final Editable arg0) {
                // TODO Auto-generated method stub
                if (BalanceReceiptDetailsActivity.this.adapter == null){
                    // some print statement saying it is null
//                   // Toast toast = Toast.makeText(BalanceReceiptDetailsActivity.this,"no record found", Toast.LENGTH_SHORT);
//                    toast.setGravity(Gravity.CENTER, 0, 0);
//                    toast.show();
                }
                else
                {
                    isLoading = false;
                     count= BalanceReceiptDetailsActivity.this.adapter.filter(String.valueOf(arg0));



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
                    balanceReceiptclass();
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

                Log.d(TAG, "prepare called current item: " + currentPage+"Total page"+totalPage);
                if(currentPage<=totalPage && count >100){
                    //currentPage = PAGE_START;
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

        if (isOnline(BalanceReceiptDetailsActivity.this)) {
            balanceReceiptoffsetclass();// check login details are valid or not from server
        }
        else {
            //Toast.makeText(BalanceReceiptDetailsActivity.this, R.string.internet_unavailable, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(BalanceReceiptDetailsActivity.this);
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
//        final ArrayList<CourseList> items = new ArrayList<>();
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//
//                for (int i = 0; i < 5; i++) {
//                    itemCount++;
//                    Log.d(TAG, "prepare called : " + itemCount);
//
//                    CourseList postItem = subListArrayList.get(i);
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
        pd = new ProgressDialog(BalanceReceiptDetailsActivity.this);
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
    private void balanceReceiptclass() {
        BalanceReceiptDetailsActivity.BalanceReceiptTrackclass ru = new BalanceReceiptDetailsActivity.BalanceReceiptTrackclass();
        ru.execute("5");
    }

    @Override
    public void onRefresh() {
        itemCount = 0;
        currentPage = PAGE_START;
        Log.d(TAG, "currentPage: " + currentPage);
        isLastPage = false;
        // adapter.clear();
        onRestart();
        //preparedListItem();


    }

    class BalanceReceiptTrackclass extends AsyncTask<String, Void, String> {

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
            BalanceReceiptDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> BalanceReceiptDetails = new HashMap<String, String>();
            BalanceReceiptDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptDetailsActivity.this));
            BalanceReceiptDetails.put("offset", String.valueOf(offset));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptDetailsActivity.this)));
            BalanceReceiptDetails.put("action","show_balance_receipt_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(BalanceReceiptDetailsActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, BalanceReceiptDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }

    }

    private void BalanceReceiptDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
//        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayoutPrabhagDetails);
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));

                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    String ttl_enq = object.getString("total_balance");
                    double ttlcol=Double.parseDouble(ttl_enq);
                    DecimalFormat df = new DecimalFormat("##,##,##,##,##,##,##0.00");
                    String rt= df.format(ttlcol);

                    total_balance.setText(rt);
                    progressBar.setVisibility(View.GONE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }
                        int count=0;
                        ArrayList<CourseList> item = new ArrayList<CourseList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                            if(jsonArrayResult.length()<100){
                                count=jsonArrayResult.length();
                            }else{
                                count=100;
                            }
                            for (int i = 0; i < count; i++) {


                                subList = new CourseList();
                                Log.d(TAG, "i: " + i);

                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String name = jsonObj.getString("Name");
                                    String RegistrationDate = jsonObj.getString("RegistrationDate");
                                    String Contact = jsonObj.getString("Contact");
                                    String Package_Name = jsonObj.getString("Package_Name");
                                    String ExecutiveName = jsonObj.getString("ExecutiveName");
                                    String Duration_Days = jsonObj.getString("Duration_Days");
                                    String Session = jsonObj.getString("Session");
                                    String Member_ID = jsonObj.getString("Member_ID");
                                    String Start_Date = jsonObj.getString("Start_Date");
                                    String End_Date = jsonObj.getString("End_Date");
                                    String Rate = jsonObj.getString("Rate");
                                    String Final_paid = jsonObj.getString("Final_paid");
                                    String Final_Balance = jsonObj.getString("Final_Balance");
                                    String Image = jsonObj.getString("Image");
                                    String Invoice_ID = jsonObj.getString("Invoice_ID");
                                    String Tax = jsonObj.getString("Tax");
                                    String Member_Email_ID = jsonObj.getString("Member_Email_ID");
                                    String Next_payment_date = jsonObj.getString("Next_payment_date");
                                    String Financial_Year = jsonObj.getString("Financial_Year");


                                    //  for (int j = 0; j < 5; j++) {
                                    itemCount++;
                                    Log.d(TAG, "run: " + itemCount);
                                    subList.setName(name);
                                    String sdate= Utility.formatDate(Start_Date);
                                    String edate=Utility.formatDate(End_Date);
                                    String todate=sdate+" to "+edate;
                                    subList.setStartToEndDate(todate);
                                    String cont=Utility.lastFour(Contact);
                                    subList.setContact(Contact);
                                    subList.setContactEncrypt(cont);
                                    String pack=Package_Name;
                                    subList.setPackageName(pack);
                                    String dur_sess="Duration:"+Duration_Days+","+"Session:"+Session;
                                    subList.setPackageNameWithDS(dur_sess);
                                    subList.setExecutiveName(ExecutiveName);
                                    subList.setTax(Tax);
                                    String reg_date= Utility.formatDate(RegistrationDate);
                                    subList.setRegistrationDate(reg_date);
                                    subList.setID(Member_ID);
                                    subList.setInvoiceID(Invoice_ID);
                                    subList.setRate(Rate);
                                    subList.setPaid(Final_paid);
                                    if(Final_Balance.equals(".00")){
                                        Final_Balance="0.00";
                                    }
                                    subList.setBalance(Final_Balance);
                                    Image.replace("\"", "");
                                    subList.setImage(Image);
                                    subList.setEmail(Member_Email_ID);
                                    String nextpaydate=Utility.formatDate(Next_payment_date);
                                    subList.setNextPaymentdate(nextpaydate);
                                    subList.setFinancialYear(Financial_Year);
                                    subList.setFollowuptype("Payment");
                                    //Toast.makeText(BalanceReceiptDetailsActivity.this, "followup date: "+next_foll_date, Toast.LENGTH_SHORT).show();

                                    //Toast.makeText(MainActivity.this, "j "+j, Toast.LENGTH_SHORT).show();
                                    item.add(subList);
                                    adapter = new BalanceReceiptAdapter( item,BalanceReceiptDetailsActivity.this);
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
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(BalanceReceiptDetailsActivity.this);
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
    private void balanceReceiptoffsetclass() {
        BalanceReceiptDetailsActivity.BalanceReceiptOffsetTrackclass ru = new BalanceReceiptDetailsActivity.BalanceReceiptOffsetTrackclass();
        ru.execute("5");
    }
    class BalanceReceiptOffsetTrackclass extends AsyncTask<String, Void, String> {

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
            BalanceReceiptOffsetDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> BalanceReceiptOffsetDetails = new HashMap<String, String>();
            BalanceReceiptOffsetDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptDetailsActivity.this));
            BalanceReceiptOffsetDetails.put("offset", String.valueOf(offset));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptDetailsActivity.this)));
            BalanceReceiptOffsetDetails.put("action","show_balance_receipt_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(BalanceReceiptDetailsActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, BalanceReceiptOffsetDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }

    private void BalanceReceiptOffsetDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
//        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayoutPrabhagDetails);
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    String ttl_enq = object.getString("total_balance");
                    double ttlcol=Double.parseDouble(ttl_enq);
                    DecimalFormat df = new DecimalFormat("##,##,##,##,##,##,##0.00");
                    String tb=total_balance.getText().toString();

                    double tbcol=Double.parseDouble(tb.replaceAll(",",""));
                    double ttl_bal=ttlcol+tbcol;
                    String rt= df.format(ttl_bal);
                    total_balance.setText(rt);
                    totalPage++;
                    currentPage++;
                    progressBar.setVisibility(View.GONE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }
                        ArrayList<CourseList> subListArrayList = new ArrayList<CourseList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new CourseList();
                                Log.d(TAG, "i: " + i);
                                // Log.d(TAG, "run: " + itemCount);
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String name = jsonObj.getString("Name");
                                    String RegistrationDate = jsonObj.getString("RegistrationDate");
                                    String Contact = jsonObj.getString("Contact");
                                    String Package_Name = jsonObj.getString("Package_Name");
                                    String ExecutiveName = jsonObj.getString("ExecutiveName");
                                    String Duration_Days = jsonObj.getString("Duration_Days");
                                    String Session = jsonObj.getString("Session");
                                    String Member_ID = jsonObj.getString("Member_ID");
                                    String Image = jsonObj.getString("Image");
                                    String Start_Date = jsonObj.getString("Start_Date");
                                    String End_Date = jsonObj.getString("End_Date");
                                    String Rate = jsonObj.getString("Rate");
                                    String Final_paid = jsonObj.getString("Final_paid");
                                    String Final_Balance = jsonObj.getString("Final_Balance");
                                    String Invoice_ID = jsonObj.getString("Invoice_ID");
                                    String Tax = jsonObj.getString("Tax");
                                    String Member_Email_ID = jsonObj.getString("Member_Email_ID");
                                    String Next_payment_date = jsonObj.getString("Next_payment_date");
                                    String Financial_Year = jsonObj.getString("Financial_Year");


                                    //  for (int j = 0; j < 5; j++) {
                                    itemCount++;
                                    Log.d(TAG, "run: " + itemCount);
                                    subList.setName(name);
                                    String sdate=Utility.formatDate(Start_Date);
                                    String edate=Utility.formatDate(End_Date);
                                    String todate=sdate+" to "+edate;
                                    subList.setStartToEndDate(todate);
                                    String cont=Utility.lastFour(Contact);
                                    subList.setContact(Contact);
                                    subList.setContactEncrypt(cont);
                                    String pack=Package_Name;
                                    String dur_sess="Duration:"+Duration_Days+","+"Session:"+Session;
                                    subList.setPackageNameWithDS(dur_sess);
                                    subList.setPackageName(pack);
                                    subList.setExecutiveName(ExecutiveName);
                                    subList.setTax(Tax);

                                    String reg_date= Utility.formatDate(RegistrationDate);
                                    subList.setRegistrationDate(reg_date);
                                    subList.setID(Member_ID);
                                    Image.replace("\"", "");
                                    subList.setImage(Image);
                                    subList.setRate(Rate);
                                    subList.setPaid(Final_paid);
                                    subList.setBalance(Final_Balance);
                                    Image.replace("\"", "");
                                    subList.setImage(Image);
                                    subList.setInvoiceID(Invoice_ID);
                                    subList.setEmail(Member_Email_ID);
                                    String nextpaydate=Utility.formatDate(Next_payment_date);
                                    subList.setNextPaymentdate(nextpaydate);
                                    subList.setFinancialYear(Financial_Year);
                                    subList.setFollowuptype("Payment");
                                    //Toast.makeText(BalanceReceiptDetailsActivity.this, "followup date: "+next_foll_date, Toast.LENGTH_SHORT).show();
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
                    currentPage = PAGE_START;
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
    private void balanceReceiptsearchclass() {
        BalanceReceiptDetailsActivity.BalanceReceiptSearchTrackclass ru = new BalanceReceiptDetailsActivity.BalanceReceiptSearchTrackclass();
        ru.execute("5");
    }
    class BalanceReceiptSearchTrackclass extends AsyncTask<String, Void, String> {

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
            //dismissProgressDialog();
            viewDialog.hideDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            BalanceReceiptSearchDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> BalanceReceiptSearchDetails = new HashMap<String, String>();
            BalanceReceiptSearchDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptDetailsActivity.this));
            BalanceReceiptSearchDetails.put("text",inputsearch.getText().toString());
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptDetailsActivity.this)));
            BalanceReceiptSearchDetails.put("action","show_search_balance_receipt");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(BalanceReceiptDetailsActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, BalanceReceiptSearchDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }

    private void BalanceReceiptSearchDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
//        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayoutPrabhagDetails);
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    String ttl_enq = object.getString("balance");
                    double ttlcol=Double.parseDouble(ttl_enq);
                    DecimalFormat df = new DecimalFormat("##,##,##,##,##,##,##0.00");
                    String rt= df.format(ttlcol);

                    total_balance.setText(rt);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }
                        final   ArrayList<CourseList> subListArrayList = new ArrayList<CourseList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new CourseList();
                                Log.d(TAG, "i: " + i);
                                // Log.d(TAG, "run: " + itemCount);
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String name = jsonObj.getString("Name");
                                    String RegistrationDate = jsonObj.getString("RegistrationDate");
                                    String Contact = jsonObj.getString("Contact");
                                    String Package_Name = jsonObj.getString("Package_Name");
                                    String ExecutiveName = jsonObj.getString("ExecutiveName");
                                    String Duration_Days = jsonObj.getString("Duration_Days");
                                    String Session = jsonObj.getString("Session");
                                    String Member_ID = jsonObj.getString("Member_ID");
                                    String Image = jsonObj.getString("Image");
                                    String Start_Date = jsonObj.getString("Start_Date");
                                    String End_Date = jsonObj.getString("End_Date");
                                    String Rate = jsonObj.getString("Rate");
                                    String Final_paid = jsonObj.getString("Final_paid");
                                    String Final_Balance = jsonObj.getString("Final_Balance");
                                    String Invoice_ID = jsonObj.getString("Invoice_ID");
                                    String Tax = jsonObj.getString("Tax");
                                    String Member_Email_ID = jsonObj.getString("Member_Email_ID");
                                    String Next_payment_date = jsonObj.getString("Next_payment_date");
                                    String Financial_Year = jsonObj.getString("Financial_Year");


                                    //  for (int j = 0; j < 5; j++) {
                                    itemCount++;
                                    Log.d(TAG, "run: " + itemCount);
                                    subList.setName(name);
                                    String sdate=Utility.formatDate(Start_Date);
                                    String edate=Utility.formatDate(End_Date);
                                    String todate=sdate+" to "+edate;
                                    subList.setStartToEndDate(todate);
                                    String cont=Utility.lastFour(Contact);
                                    subList.setContact(Contact);
                                    subList.setContactEncrypt(cont);
                                    String pack=Package_Name;
                                    String dur_sess="Duration:"+Duration_Days+","+"Session:"+Session;
                                    subList.setPackageNameWithDS(dur_sess);
                                    subList.setPackageName(pack);
                                    subList.setExecutiveName(ExecutiveName);
                                    subList.setTax(Tax);

                                    String reg_date= Utility.formatDate(RegistrationDate);
                                    subList.setRegistrationDate(reg_date);
                                    subList.setID(Member_ID);
                                    Image.replace("\"", "");
                                    subList.setImage(Image);
                                    subList.setRate(Rate);
                                    subList.setPaid(Final_paid);
                                    subList.setBalance(Final_Balance);
                                    Image.replace("\"", "");
                                    subList.setImage(Image);
                                    subList.setInvoiceID(Invoice_ID);
                                    subList.setEmail(Member_Email_ID);
                                    String nextpaydate=Utility.formatDate(Next_payment_date);
                                    subList.setNextPaymentdate(nextpaydate);
                                    subList.setFinancialYear(Financial_Year);
                                    subList.setFollowuptype("Payment");
                                    subListArrayList.add(subList);

                                    adapter = new BalanceReceiptAdapter( subListArrayList,BalanceReceiptDetailsActivity.this);
                                    recyclerView.setAdapter(adapter);


                                }
                            }

                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    // nodata.setVisibility(View.VISIBLE);
                    Toast.makeText(BalanceReceiptDetailsActivity.this, "NO Record Found", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(BalanceReceiptDetailsActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_filter) {
            Intent intent = new Intent(BalanceReceiptDetailsActivity.this, BalanceReceiptFilterActivity.class);
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
        swipeRefresh.setRefreshing(false);
        Intent intent=new Intent(BalanceReceiptDetailsActivity.this,BalanceReceiptDetailsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(BalanceReceiptDetailsActivity.this, MainActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(BalanceReceiptDetailsActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
