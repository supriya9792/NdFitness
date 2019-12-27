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
import com.ndfitnessplus.Adapter.CollectionAdapter;
import com.ndfitnessplus.Listeners.PaginationScrollListener;
import com.ndfitnessplus.Model.CollectionList;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.ndfitnessplus.Utility.HTTPRequestQueue.isOnline;

public class CollectionActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener  {
    CollectionAdapter adapter;

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

    public static String TAG = CollectionActivity.class.getName();
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
    //TextView total_balance;
    TextView collection;
    //Loading gif
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
//                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_collection);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.menu_collections));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){

        progressBar=findViewById(R.id.progressBar);
        swipeRefresh=findViewById(R.id.swipeRefresh);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        viewDialog = new ViewDialog(this);
        //total_balance=findViewById(R.id.ttl_balance);
        collection=findViewById(R.id.ttl_collection);
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
            String coll=intent.getStringExtra("collection");
            progressBar.setVisibility(View.GONE);
            int length=filterArrayList.size();
            collection.setText(coll);
            adapter = new CollectionAdapter( filterArrayList,CollectionActivity.this);
            recyclerView.setAdapter(adapter);
        }else{
            if (isOnline(CollectionActivity.this)) {
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


        inputsearch=(EditText)findViewById(R.id.inputsearchid);
        search=findViewById(R.id.search);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputsearch.getText().length()>0){
                    if(!SharedPrefereneceUtil.getAuthority(CollectionActivity.this).equals("User")){
                        balanceReceiptsearchclass();
                    }

                }else{
                    Toast.makeText(CollectionActivity.this,"Please enter text to search", Toast.LENGTH_LONG).show();
                }

            }
        });

        inputsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(final Editable arg0) {
                // TODO Auto-generated method stub
                if (CollectionActivity.this.adapter == null){
                    // some print statement saying it is null
//                   // Toast toast = Toast.makeText(CollectionActivity.this,"no record found", Toast.LENGTH_SHORT);
//                    toast.setGravity(Gravity.CENTER, 0, 0);
//                    toast.show();
                }
                else
                {
                    isLoading = false;
                    CollectionActivity.this.adapter.filter(String.valueOf(arg0));



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

        if (isOnline(CollectionActivity.this)) {
           // balanceReceiptoffsetclass();// check login details are valid or not from server
        }
        else {
            //Toast.makeText(CollectionActivity.this, R.string.internet_unavailable, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CollectionActivity.this);
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
//
    }
    //Showing progress dialog
    private void showProgressDialog() {
        Log.v(TAG, String.format("showProgressDialog"));
        pd = new ProgressDialog(CollectionActivity.this);
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
        CollectionActivity.BalanceReceiptTrackclass ru = new CollectionActivity.BalanceReceiptTrackclass();
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
            BalanceReceiptDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(CollectionActivity.this));
            BalanceReceiptDetails.put("offset", String.valueOf(offset));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(CollectionActivity.this)));
            BalanceReceiptDetails.put("action","show_collection_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(CollectionActivity.this);
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
                    String ttl_enq = object.getString("total_balance_count");
                    String col = object.getString("collection");
                   // total_balance.setText(ttl_enq);
                    double ttlcol=Double.parseDouble(col);
                    DecimalFormat df = new DecimalFormat("##,##,##,##,##,##,##0.00");
                    String rt= df.format(ttlcol);

                    collection.setText(rt);
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
                                    String ReceiptDate = jsonObj.getString("ReceiptDate");
                                    String Contact = jsonObj.getString("Contact");
                                    String PaymentType = jsonObj.getString("PaymentType");
                                    String ExecutiveName = jsonObj.getString("ExecutiveName");
                                    String Member_ID = jsonObj.getString("Member_ID");
                                    String Final_paid = jsonObj.getString("Paid");
                                    String Final_Balance = jsonObj.getString("RemainingBalance");
                                    String Image = jsonObj.getString("Image");
                                    String Invoice_ID = jsonObj.getString("Invoice_ID");
                                    String Receipt_Id = jsonObj.getString("Receipt_Id");
                                    String Tax = jsonObj.getString("Tax");
                                    String Member_Email_ID = jsonObj.getString("Member_Email_ID");
                                    String Next_payment_date = jsonObj.getString("NextPaymentDate");
                                    String ReceiptType = jsonObj.getString("ReceiptType");
                                    String PaymentDetails = jsonObj.getString("PaymentDetails");

                                    String Start_Date = jsonObj.getString("Start_Date");
                                    String End_Date = jsonObj.getString("End_Date");
                                    String Rate = jsonObj.getString("Rate");
                                    String RegistrationDate = jsonObj.getString("RegistrationDate");
                                    String Package_Name = jsonObj.getString("Package_Name");
                                    String Duration_Days = jsonObj.getString("Duration_Days");
                                    String Session = jsonObj.getString("Session");
                                    String Financial_Year = jsonObj.getString("Financial_Year");
                                    //  for (int j = 0; j < 5; j++) {
                                    itemCount++;
                                    Log.d(TAG, "run: " + itemCount);
                                    subList.setName(name);

                                    String cont=Utility.lastFour(Contact);
                                    subList.setContact(Contact);
                                    subList.setContactEncrypt(cont);
                                   // String pack=Package_Name+"(Duration:"+Duration_Days+","+"Session:"+Session+")";
                                    subList.setPaymentType(PaymentType);
                                    subList.setExecutiveName(ExecutiveName);
                                    subList.setTax(Tax);
                                    String rec_date= Utility.formatDate(ReceiptDate);
                                    subList.setReceiptDate(rec_date);
                                    subList.setID(Member_ID);
                                    subList.setInvoiceID(Invoice_ID);
                                    subList.setReceiptId(Receipt_Id);
                                    if(Final_paid.equals(".00")){
                                        Final_paid="0.00";
                                    }
                                    String paid="₹ "+Final_paid;
                                    subList.setPaid(paid);
                                    if(Final_Balance.equals(".00")){
                                        Final_Balance="0.00";
                                    }
                                    String nextpaydate=Utility.formatDate(Next_payment_date);
                                    String balance="₹ "+Final_Balance;
                                    subList.setBalance(Final_Balance);
                                    subList.setBalanceRuppe(balance);
                                    Image.replace("\"", "");
                                    subList.setImage(Image);
                                    subList.setEmail(Member_Email_ID);

                                    subList.setNextPaymentdate(nextpaydate);
                                    subList.setReceiptType(ReceiptType);
                                    subList.setPaymentDetails(PaymentDetails);

                                    String sdate=Utility.formatDate(Start_Date);
                                    String edate=Utility.formatDate(End_Date);
                                    String todate=sdate+" to "+edate;
                                    subList.setStartToEndDate(todate);

                                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                                            "dd-MM-yyyy");
                                    Date endDate = new Date();
                                    Date currentdate = new Date();
                                    String endc=Utility.formatDateDB(End_Date);
                                    try {
                                        endDate = dateFormat.parse(endc);
                                        currentdate = dateFormat.parse(Utility.getCurrentDate());
                                        Log.v(TAG, String.format(" ::endDate = %s", endDate));
                                        Log.v(TAG, String.format(" :: currentdate = %s",currentdate));
                                        if (currentdate.before(endDate)|| currentdate.equals(endDate) ) {
                                            subList.setStatus("Active");
                                        } else {
                                            subList.setStatus("Inactive");
                                        }
                                    } catch (ParseException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }

                                    subList.setPackageName(Package_Name);
                                    String dur_sess="Duration:"+Duration_Days+","+"Session:"+Session;
                                    subList.setPackageNameWithDS(dur_sess);
                                    String reg_date= Utility.formatDate(RegistrationDate);
                                    subList.setRegistrationDate(reg_date);
                                    subList.setRate(Rate);
                                    subList.setFinancialYear(Financial_Year);
                                    //Toast.makeText(CollectionActivity.this, "followup date: "+next_foll_date, Toast.LENGTH_SHORT).show();

                                    //Toast.makeText(MainActivity.this, "j "+j, Toast.LENGTH_SHORT).show();
                                    item.add(subList);
                                    adapter = new CollectionAdapter( item,CollectionActivity.this);
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
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CollectionActivity.this);
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

    private void balanceReceiptsearchclass() {
        CollectionActivity.BalanceReceiptSearchTrackclass ru = new CollectionActivity.BalanceReceiptSearchTrackclass();
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
           // dismissProgressDialog();
            viewDialog.hideDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            BalanceReceiptSearchDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> BalanceReceiptSearchDetails = new HashMap<String, String>();
            BalanceReceiptSearchDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(CollectionActivity.this));
            BalanceReceiptSearchDetails.put("text", inputsearch.getText().toString());
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(CollectionActivity.this)));
            BalanceReceiptSearchDetails.put("action","show_search_collection");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(CollectionActivity.this);
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
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }
                        String cnt= String.valueOf(jsonArrayResult.length());
                       // total_member.setText(cnt);
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
                                    String RegistrationDate = jsonObj.getString("ReceiptDate");
                                    String Contact = jsonObj.getString("Contact");
                                    String PaymentType = jsonObj.getString("PaymentType");
                                    String ExecutiveName = jsonObj.getString("ExecutiveName");
                                    String Member_ID = jsonObj.getString("Member_ID");
                                    String Final_paid = jsonObj.getString("Paid");
                                    String Final_Balance = jsonObj.getString("RemainingBalance");
                                    String Image = jsonObj.getString("Image");
                                    String Invoice_ID = jsonObj.getString("Invoice_ID");
                                    String Receipt_Id = jsonObj.getString("Receipt_Id");
                                    String Tax = jsonObj.getString("Tax");
                                    String Member_Email_ID = jsonObj.getString("Member_Email_ID");
                                    String Next_payment_date = jsonObj.getString("NextPaymentDate");
                                    String ReceiptType = jsonObj.getString("ReceiptType");
                                    String PaymentDetails = jsonObj.getString("PaymentDetails");


                                    //  for (int j = 0; j < 5; j++) {
                                    itemCount++;
                                    Log.d(TAG, "run: " + itemCount);
                                    subList.setName(name);

                                    String cont=Utility.lastFour(Contact);
                                    subList.setContact(Contact);
                                    subList.setContactEncrypt(cont);
                                    // String pack=Package_Name+"(Duration:"+Duration_Days+","+"Session:"+Session+")";
                                    subList.setPaymentType(PaymentType);
                                    subList.setExecutiveName(ExecutiveName);
                                    subList.setTax(Tax);
                                    String reg_date= Utility.formatDate(RegistrationDate);
                                    subList.setReceiptDate(reg_date);
                                    subList.setID(Member_ID);
                                    subList.setInvoiceID(Invoice_ID);
                                    subList.setReceiptId(Receipt_Id);
                                    if(Final_paid.equals(".00")){
                                        Final_paid="0.00";
                                    }
                                    String paid="₹ "+Final_paid;
                                    subList.setPaid(paid);
                                    if(Final_Balance.equals(".00")){
                                        Final_Balance="0.00";
                                    }
                                    String nextpaydate=Utility.formatDate(Next_payment_date);
                                    String balance="₹ "+Final_Balance;
                                    subList.setBalance(balance);
                                    Image.replace("\"", "");
                                    subList.setImage(Image);
                                    subList.setEmail(Member_Email_ID);

                                    subList.setNextPaymentdate(nextpaydate);
                                    subList.setReceiptType(ReceiptType);
                                    subList.setPaymentDetails(PaymentDetails);
                                    subListArrayList.add(subList);

                                    adapter = new CollectionAdapter( subListArrayList,CollectionActivity.this);
                                    recyclerView.setAdapter(adapter);

                                }
                            }

                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    // nodata.setVisibility(View.VISIBLE);
                    Toast.makeText(CollectionActivity.this, "NO Record Found", Toast.LENGTH_SHORT).show();
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
        if(SharedPrefereneceUtil.getAuthority(CollectionActivity.this).equals("User")){
            MenuItem item = menu.findItem(R.id.action_filter);
            item.setVisible(false);
        }else{
            MenuItem item = menu.findItem(R.id.action_filter);
            item.setVisible(true);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_home) {
            Intent intent = new Intent(CollectionActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_filter) {
            Intent intent = new Intent(CollectionActivity.this, CollectionFilterActivity.class);
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
        Intent intent=new Intent(CollectionActivity.this,CollectionActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(CollectionActivity.this, MainActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CollectionActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
