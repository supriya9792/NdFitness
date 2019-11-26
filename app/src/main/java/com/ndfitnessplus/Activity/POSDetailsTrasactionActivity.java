package com.ndfitnessplus.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ndfitnessplus.Adapter.BalanceTrasactionAdapter;
import com.ndfitnessplus.Adapter.ProductTrasanctionAdapter;
import com.ndfitnessplus.Model.BalanceTrasactionList;
import com.ndfitnessplus.Model.CourseList;
import com.ndfitnessplus.Model.POSSellList;
import com.ndfitnessplus.Model.ProductTrasanctionList;
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

public class POSDetailsTrasactionActivity extends AppCompatActivity {
    public static String TAG = POSDetailsTrasactionActivity.class.getName();

    String Email;

    String FinancialYear;

    TextView TotalTV;

    ProductTrasanctionAdapter adapter;

    TextView contactTV;

    TextView executiveNameTV;

    POSSellList filterArrayList;

    TextView invoic_ttlTV;

    String invoice_id;

    TextView invoice_idTV;

    private LinearLayoutManager layoutManager;

    String member_id;
    TextView regdateTV;
    TextView nameTV;

    View nodata;

    private ProgressDialog pd;

    ImageButton phone;

    ProgressBar progressBar;

    private RecyclerView recyclerView;



    ProductTrasanctionList subList;

    ArrayList<ProductTrasanctionList> subListArrayList = new ArrayList();

    ViewDialog viewDialog;

    ImageView whatsapp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_posdetails_trasaction);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.receipt_dtl));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){

        contactTV = (TextView) findViewById(R.id.contactTV);
        invoic_ttlTV = (TextView) findViewById(R.id.invoic_ttlTV);
        nameTV = (TextView) findViewById(R.id.nameTV);

        viewDialog = new ViewDialog(this);

        regdateTV = (TextView) findViewById(R.id.invoicedateTV);
        executiveNameTV=(TextView)findViewById(R.id.excecutive_nameTV);
        invoice_idTV = (TextView) findViewById(R.id.invoice_idTV);
        TotalTV = (TextView) findViewById(R.id.total);



        phone=findViewById(R.id.phone_call);
        whatsapp=findViewById(R.id.whatsapp);

        progressBar=findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        if (args != null) {
            filterArrayList = (POSSellList) args.getSerializable("filter_array_list");

            String cont=filterArrayList.getCustContact();
//                Log.v(TAG, String.format("Selected  ::contact= %s", cont));
//                Log.v(TAG, String.format("Selected  ::name= %s", filterArrayList.getName()));
            contactTV.setText(cont);
            nameTV.setText(filterArrayList.getCustName());
            String fpaid=filterArrayList.getTotalAmount();
            TotalTV.setText(fpaid);
            invoic_ttlTV.setText(fpaid);
            String idate=Utility.formatDate(filterArrayList.getInvoiceDate());
            regdateTV.setText(idate);
            this.executiveNameTV.setText(this.filterArrayList.getSaleExecutive());
            this.invoice_id = this.filterArrayList.getInvoiceId();
            this.invoice_idTV.setText(this.invoice_id);
            productTrasactionclass();
        }
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:"+contactTV.getText().toString()));
                startActivity(dialIntent);
            }
        });

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                PackageManager pm=getPackageManager();
                try {
                    // Uri uri = Uri.parse("smsto:" + Contact);
                    Uri uri = Uri.parse("whatsapp://send?phone=+91" + contactTV.getText().toString());
                    Intent waIntent = new Intent(Intent.ACTION_VIEW,uri);
                    //waIntent.setType("text/plain");
                    String text = "YOUR TEXT HERE";

                    PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                    //Check if package exists or not. If not then code
                    //in catch block will be called
                    waIntent.setPackage("com.whatsapp");
                    // waIntent.putExtra(Intent.EXTRA_TEXT, text);
                    startActivity(waIntent);

                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(POSDetailsTrasactionActivity.this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }
    private void productTrasactionclass() {
        POSDetailsTrasactionActivity.BalanceTrasactionTrackclass ru = new POSDetailsTrasactionActivity.BalanceTrasactionTrackclass();
        ru.execute("5");
    }

    class BalanceTrasactionTrackclass extends AsyncTask<String, Void, String> {


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
            Log.v(TAG, String.format("onPostExecute :: show_balance_trasaction_details = %s", response));
            //  dismissProgressDialog();
            viewDialog.hideDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            BalanceTrasactionDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> BalanceTrasactionDetails = new HashMap<String, String>();
            BalanceTrasactionDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(POSDetailsTrasactionActivity.this));
            BalanceTrasactionDetails.put("invoice_id",invoice_id );
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(POSDetailsTrasactionActivity.this)));
            Log.v(TAG, String.format("doInBackground :: invoice_id = %s", invoice_id));
            BalanceTrasactionDetails.put("action","show_sale_product_details_by_invoice_id");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(POSDetailsTrasactionActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, BalanceTrasactionDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }
    }

    private void BalanceTrasactionDetails(String jsonResponse) {

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
                        ArrayList<ProductTrasanctionList> item = new ArrayList<ProductTrasanctionList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new ProductTrasanctionList();
                                Log.d(TAG, "i: " + i);

                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Product_Code = jsonObj.getString("Product_Code");
                                    String Product_Name = jsonObj.getString("Product_Name");
                                    String Product_Quntity = jsonObj.getString("Product_Quntity");
                                    String Product_Rate = jsonObj.getString("Product_Rate");
                                    String Product_Total = jsonObj.getString("Product_Total");


                                    //  for (int j = 0; j < 5; j++) {

                                    subList.setProdCode(Product_Code);
                                    subList.setProdName(Product_Name);
                                    subList.setQuantity(Product_Quntity);
                                    subList.setRate(Product_Rate);
                                    subList.setProductFinalRate(Product_Total);


                                    item.add(subList);
                                    adapter = new ProductTrasanctionAdapter( item,POSDetailsTrasactionActivity.this);
                                    recyclerView.setAdapter(adapter);

                                }
                            }
                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    //nodata.setVisibility(View.VISIBLE);
                    // recyclerView.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(POSDetailsTrasactionActivity.this);
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
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
