package com.ndfitnessplus.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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

import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration;
import com.ndfitnessplus.Adapter.POSItemAdapter;
import com.ndfitnessplus.Adapter.POSSellAdapter;
import com.ndfitnessplus.Adapter.POSSellProductGridAdapter;
import com.ndfitnessplus.LocalDatabase.SQLiteDataBaseHelper;
import com.ndfitnessplus.Model.POSItemList;
import com.ndfitnessplus.Model.POSSellList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.Tools;
import com.ndfitnessplus.Utility.Utility;
import com.ndfitnessplus.Utility.ViewDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.ndfitnessplus.Utility.HTTPRequestQueue.isOnline;

public class POSProductListActivity extends AppCompatActivity {
    POSSellProductGridAdapter adapter;

    POSItemList subList;
    SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    ProgressBar progressBar;

    View nodata,frame;
    //No internet connectioin
    View noInternet;
    private ProgressBar progress_bar;
    private LinearLayout lyt_no_connection;

    public static String TAG = POSProductListActivity.class.getName();
    private ProgressDialog pd;

    //paginnation parameters
    public static final int PAGE_START = 1;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 2;
    private boolean isLoading = false;
    int itemCount = 0;
    int offset = 0;
     int cartcount=0;
    //search
    private EditText inputsearch;
    ImageView search;
    TextView total_present;
    //Loading gif
    ViewDialog viewDialog;
    //Cart Count
    SQLiteDataBaseHelper db;
    ArrayList<POSItemList> subArrayList = new ArrayList<POSItemList>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_posproduct_list);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.pos));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){
        progressBar=findViewById(R.id.progressBar);

        swipeRefresh=findViewById(R.id.swipeRefresh);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 8)));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        nodata=findViewById(R.id.nodata);
        frame=findViewById(R.id.main_frame);
        noInternet=findViewById(R.id.no_internet);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        lyt_no_connection = (LinearLayout) findViewById(R.id.lyt_no_connection);

        viewDialog = new ViewDialog(POSProductListActivity.this);
        db=new SQLiteDataBaseHelper(POSProductListActivity.this);
        if (isOnline(POSProductListActivity.this)) {
            POSItemclass();// check login details are valid or not from server
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
        inputsearch=(EditText)findViewById(R.id.inputsearchid);
        search=findViewById(R.id.search);

        subArrayList=db.getAllCartProduct();

        inputsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(final Editable arg0) {
                // TODO Auto-generated method stub
                if (adapter == null){
                    // some print statement saying it is null
//                   // Toast toast = Toast.makeText(AttendenceActivity.this,"no record found", Toast.LENGTH_SHORT);
//                    toast.setGravity(Gravity.CENTER, 0, 0);
//                    toast.show();
                }
                else
                {
                    isLoading = false;
                    int count=adapter.filter(String.valueOf(arg0));

                    //  total_present.setText(String.valueOf(count));-

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
                    POSItemclass();
                }

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pos_home, menu);
        MenuItem itemCart = menu.findItem(R.id.action_cart);
        LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
        db=new SQLiteDataBaseHelper(POSProductListActivity.this);
        subArrayList=db.getAllCartProduct();
        cartcount=subArrayList.size();
        Utility.setBadgeCount(this, icon, String.valueOf(cartcount));
        invalidateOptionsMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_home) {
            Intent intent = new Intent(POSProductListActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_pos_show_item) {
            Intent intent = new Intent(POSProductListActivity.this, POSShowItemActivity.class);
            startActivity(intent);
            return true;
        }else if (id == R.id.action_cart) {
            Intent intent = new Intent(POSProductListActivity.this, CartActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void POSItemclass() {
        POSProductListActivity.POSItemTrackclass ru = new POSProductListActivity.POSItemTrackclass();
        ru.execute("5");
    }
    class POSItemTrackclass extends AsyncTask<String, Void, String> {

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
            POSItemDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> POSItemDetails = new HashMap<String, String>();
            POSItemDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(POSProductListActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(POSProductListActivity.this)));
            POSItemDetails.put("action","show_pos_items");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(POSProductListActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, POSItemDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }

    }

    private void POSItemDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
//        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayoutPrabhagDetails);
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));

                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
//                    nodata.setVisibility(View.GONE);
//                    swipeRefresh.setVisibility(View.VISIBLE);
                    String ttl_enq = object.getString("total_product_count");
                    //total_present.setText(ttl_enq);
                    progressBar.setVisibility(View.GONE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }

                        ArrayList<POSItemList> item = new ArrayList<POSItemList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new POSItemList();
                                Log.d(TAG, "i: " + i);

                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Product_Code = jsonObj.getString("Product_Code");
                                    String Product_Name = jsonObj.getString("Product_Name");
                                    String Product_Dsription = jsonObj.getString("Product_Dsription");
                                    String Product_Image = jsonObj.getString("Product_Image");
                                    String Quantity = jsonObj.getString("Quantity");
                                    String Rate = jsonObj.getString("Rate");
                                    String Tax = jsonObj.getString("Tax");
                                    String MaxDiscount = jsonObj.getString("MaxDiscount");
                                    String Purches_Amount = jsonObj.getString("Purches_Amount");
                                    String Auto_Id = jsonObj.getString("Auto_Id");


                                    subList.setProductCode(Product_Code);
                                    subList.setProductName(Product_Name);
                                    subList.setProductDisc(Product_Dsription);
                                    subList.setProductImage(Product_Image);
                                    subList.setQuantity(Quantity);
                                    subList.setRate(Rate);
                                    subList.setTax(Tax);
                                    subList.setMaxDiscount(MaxDiscount);
                                    subList.setPurchaseAmount(Purches_Amount);
                                    subList.setAutoId(Auto_Id);
                                    for (POSItemList data : subArrayList) {
                                        if(Product_Code.equals(data.getProductCode())){
                                            subList.setAddtocart(true);
                                        }
                                    }
                                    item.add(subList);
                                    //Toast.makeText(AttendenceActivity.this, "followup date: "+next_foll_date, Toast.LENGTH_SHORT).show();

                                    //Toast.makeText(MainActivity.this, "j "+j, Toast.LENGTH_SHORT).show();

                                    adapter = new POSSellProductGridAdapter( item,POSProductListActivity.this);
                                    recyclerView.setAdapter(adapter);
                                    adapter.setOnMoreButtonClickListener(new POSSellProductGridAdapter.OnMoreButtonClickListener() {
                                        @Override
                                        public void onItemClick(View view, POSItemList obj, MenuItem item) {
                                            Snackbar.make(view, obj.getProductName() + " (" + item.getTitle() + ") clicked", Snackbar.LENGTH_SHORT).show();
                                        }
                                    });
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
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(POSProductListActivity.this);
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
        Intent intent=new Intent(POSProductListActivity.this,POSActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
       Intent intent=new Intent(POSProductListActivity.this,POSActivity.class);
       startActivity(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();

    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        Intent intent=new Intent(POSProductListActivity.this,POSProductListActivity.class);
//        startActivity(intent);
//    }
    @Override
    protected void onRestart() {
        if (subArrayList.size() != 0) {
            cartcount =subArrayList.size();
           // invalidateOptionsMenu();
            super.onRestart();
        }
    }
}
