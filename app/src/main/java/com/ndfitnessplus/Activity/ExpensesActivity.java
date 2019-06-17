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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ndfitnessplus.Activity.Notification.ExpenseFilterActivity;
import com.ndfitnessplus.Activity.Notification.TodaysEnrollmentActivity;
import com.ndfitnessplus.Adapter.CourseAdapter;
import com.ndfitnessplus.Adapter.EnquiryAdapter;
import com.ndfitnessplus.Adapter.ExpensesAdapter;
import com.ndfitnessplus.Listeners.PaginationScrollListener;
import com.ndfitnessplus.Model.CourseList;
import com.ndfitnessplus.Model.EnquiryList;
import com.ndfitnessplus.Model.ExpensesList;
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

public class ExpensesActivity extends AppCompatActivity {
    ExpensesAdapter adapter;
    ArrayList<ExpensesList> subListArrayList = new ArrayList<ExpensesList>();
    ExpensesList subList;
    SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    ProgressBar progressBar;

    View nodata,frame;
    //No internet connectioin
    View noInternet;
    private ProgressBar progress_bar;
    private LinearLayout lyt_no_connection;
    ArrayList<ExpensesList> branchList;
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
    TextView expenses;
    private EditText inputsearch;
    //Loading gif
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.exi_expenses));
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
        frame=findViewById(R.id.main_frame);
        noInternet=findViewById(R.id.no_internet);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        lyt_no_connection = (LinearLayout) findViewById(R.id.lyt_no_connection);
        expenses=findViewById(R.id.ttl_expense);
        progress_bar.setVisibility(View.GONE);
        lyt_no_connection.setVisibility(View.VISIBLE);
//        adapter = new EnquiryAdapter( new ArrayList<EnquiryList>(),EnquiryActivity.this);
//        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        if (args != null) {
            ArrayList<ExpensesList> filterArrayList = (ArrayList<ExpensesList>) args.getSerializable("filter_array_list");
            progressBar.setVisibility(View.GONE);
            String coll=intent.getStringExtra("expense");
            progressBar.setVisibility(View.GONE);
            expenses.setText(coll);
            //total_courses.setText(String.valueOf(length));
            adapter = new ExpensesAdapter( filterArrayList,ExpensesActivity.this);
            recyclerView.setAdapter(adapter);
        }else {

            if (isOnline(ExpensesActivity.this)) {
                expenseclass();// check login details are valid or not from server
            } else {
                swipeRefresh.setVisibility(View.GONE);
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
//                Toast.makeText(ExpensesActivity.this, R.string.internet_unavailable, Toast.LENGTH_LONG).show();
//                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ExpensesActivity.this);
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
                Intent intent=new Intent(ExpensesActivity.this,AddExpensesActivity.class);
                startActivity(intent);
            }
        });

        inputsearch=(EditText)findViewById(R.id.inputsearchid);

        inputsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                if (ExpensesActivity.this.adapter == null){
                    // some print statement saying it is null
//                    Toast toast = Toast.makeText(ExpensesActivity.this,"no record found", Toast.LENGTH_SHORT);
//                    toast.setGravity(Gravity.CENTER, 0, 0);
//                    toast.show();
                }
                else
                {
                    ExpensesActivity.this.adapter.filter(String.valueOf(arg0));

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter_action_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_home) {
            Intent intent = new Intent(ExpensesActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        }else if (id == R.id.action_filter) {
            Intent intent = new Intent(ExpensesActivity.this, ExpenseFilterActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//Showing progress dialog
    private void showProgressDialog() {
        Log.v(TAG, String.format("showProgressDialog"));
        pd = new ProgressDialog(ExpensesActivity.this);
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
    private void expenseclass() {
        ExpensesActivity.ExpenseTrackclass ru = new ExpensesActivity.ExpenseTrackclass();
        ru.execute("5");
    }

    class ExpenseTrackclass extends AsyncTask<String, Void, String> {

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
           // dismissProgressDialog();
            viewDialog.hideDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            ExpenseDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> ExpenseDetails = new HashMap<String, String>();
            ExpenseDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(ExpensesActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(ExpensesActivity.this)));
            ExpenseDetails.put("action","show_expenses_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(ExpensesActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, ExpenseDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }

    private void ExpenseDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
//        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayoutPrabhagDetails);
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    String col = object.getString("ttl_expense");
                    // total_balance.setText(ttl_enq);
                    double ttlcol=Double.parseDouble(col);
                    DecimalFormat df = new DecimalFormat("##,##,##,##,##,##,##0.00");
                    String rt= df.format(ttlcol);

                    expenses.setText(rt);
                    progressBar.setVisibility(View.GONE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }
                        int count=0;
                        ArrayList<ExpensesList> item = new ArrayList<ExpensesList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                            if(jsonArrayResult.length()<10){
                                count=jsonArrayResult.length();
                            }else{
                                count=10;
                            }
                            for (int i = 0; i < count; i++) {


                                subList = new ExpensesList();
                                Log.d(TAG, "i: " + i);

                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Expence_Date = jsonObj.getString("Expence_Date");
                                    String TitleExpences = jsonObj.getString("TitleExpences");
                                    String Description = jsonObj.getString("Description");
                                    String GroupOfExpences = jsonObj.getString("GroupOfExpences");
                                    String ExecutiveName = jsonObj.getString("ExecutiveName");
                                    String Amount = jsonObj.getString("amount");
                                    String Payment_Type = jsonObj.getString("Payment_Type");
                                    String PaymentDetails = jsonObj.getString("PaymentDetails");
                                    String Expence_ID = jsonObj.getString("Expence_ID");

                                    //  for (int j = 0; j < 5; j++) {
                                    itemCount++;
                                    Log.d(TAG, "run: " + itemCount);

                                    subList.setTtl_of_expenses(TitleExpences);
                                    subList.setDisc(Description);
                                    subList.setExpenses_group(GroupOfExpences);
                                    subList.setExecutive_name(ExecutiveName);
                                    subList.setAmount(Amount);
                                    String exp_date= Utility.formatDate(Expence_Date);
                                    subList.setExpenses_date(exp_date);
                                    subList.setExpensesId(Expence_ID);
                                    subList.setPayment_type(Payment_Type);
                                    subList.setPayment_dtl(PaymentDetails);
                                    //Toast.makeText(ExpensesActivity.this, "followup date: "+next_foll_date, Toast.LENGTH_SHORT).show();

                                    //Toast.makeText(MainActivity.this, "j "+j, Toast.LENGTH_SHORT).show();
                                    item.add(subList);
                                    adapter = new ExpensesAdapter( item,ExpensesActivity.this);
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
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ExpensesActivity.this);
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
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(ExpensesActivity.this, MainActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(ExpensesActivity.this,MainActivity.class);
        startActivity(intent);
    }
}
