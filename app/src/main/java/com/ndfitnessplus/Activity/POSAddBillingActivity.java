package com.ndfitnessplus.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.ndfitnessplus.Adapter.AddEnquirySpinnerAdapter;
import com.ndfitnessplus.Adapter.CartAdapter;
import com.ndfitnessplus.Adapter.EnquiryAdapter;
import com.ndfitnessplus.Adapter.SelectedCartAdapter;
import com.ndfitnessplus.Adapter.SellProductAdapter;
import com.ndfitnessplus.LocalDatabase.SQLiteDataBaseHelper;
import com.ndfitnessplus.Model.FollowupList;
import com.ndfitnessplus.Model.POSItemList;
import com.ndfitnessplus.Model.SellProductList;
import com.ndfitnessplus.Model.Spinner_List;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.CustomItemClickListener;
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

public class POSAddBillingActivity extends AppCompatActivity {
    private EditText inputName,inputContact, inputPaymentDtl;
    private TextInputLayout inputLayoutName,inputLayoutContact, inputLayoutPaymentDtl;


    public final String TAG = POSAddBillingActivity.class.getName();
    private ProgressDialog pd;
    private AwesomeValidation awesomeValidation;


    //Spinner Adapter
    public Spinner spinPaymentType;
    Spinner_List paymentTypelist;

    ArrayList<Spinner_List> paymentTypeArrayList = new ArrayList<Spinner_List>();

    public AddEnquirySpinnerAdapter paymentTypeadapter;
    String paymentType;
    TextView txtpaymentType;
    String TaxAmount;
    TextView totalTV;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    double total=0;
    double totalMaxDisc=0;
    Button Checkout;
    TextView TotalTv;
    //Loading gif
    ViewDialog viewDialog;
    int k=0;
    SelectedCartAdapter adapter;
    POSItemList subList;
    ArrayList<POSItemList> cartarrayList=new ArrayList<POSItemList>();
    ArrayList<POSItemList> filterArrayList=new ArrayList<POSItemList>();
    SQLiteDataBaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_posadd_billing);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.pos_billing));
        // getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutContact = (TextInputLayout) findViewById(R.id.input_layout_cont);
        inputLayoutPaymentDtl = (TextInputLayout) findViewById(R.id.input_layout_payment_details);


        viewDialog = new ViewDialog(this);

        inputName = (EditText) findViewById(R.id.input_name);
        inputContact = (EditText) findViewById(R.id.input_cont);
        inputPaymentDtl = (EditText) findViewById(R.id.input_payment_details);

        Checkout=findViewById(R.id.btn_checkout);

        TotalTv=findViewById(R.id.totalTV);
        db=new SQLiteDataBaseHelper(POSAddBillingActivity.this);

        spinPaymentType = (Spinner) findViewById(R.id.spinner_payment_type);

        txtpaymentType=findViewById(R.id.txt_payment_type);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);


//        productList=findViewById(R.id.sell_product_list);
//        productList.setVisibility(View.GONE);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        awesomeValidation.addValidation(this, R.id.input_name, RegexTemplate.NOT_EMPTY, R.string.err_msg_name);
        awesomeValidation.addValidation(this, R.id.input_cont, RegexTemplate.NOT_EMPTY, R.string.err_msg_cont);

        // awesomeValidation.addValidation(this, R.id.input_budget,RegexTemplate.NOT_EMPTY, R.string.err_msg_budget);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        if (args != null) {
            filterArrayList = (ArrayList<POSItemList>) args.getSerializable("filter_array_list");
            for (POSItemList data : filterArrayList) {
                subList =new POSItemList();
                subList.setAutoId(data.getAutoId());
                subList.setProductCode(data.getProductCode());
                subList.setProductName(data.getProductName());
                subList.setQuantity(data.getQuantity());
                subList.setRate(data.getRate());
                subList.setProductImage(data.getProductImage());
                String quantity=data.getQuantity();
                String totl=data.getRate();
                String maxdiscount=data.getMaxDiscount();
                String sp[]=totl.split(" ");
                if(!totl.equals("null")){
                    if(!(quantity.equals("null")||quantity.equals(""))){
                        double quantityst=Double.parseDouble(quantity);
                        double maxdisc=Double.parseDouble(maxdiscount);
                        double totaal=Double.parseDouble(sp[1]);
                        double chetotal=totaal*quantityst;
                        double chediscout=maxdisc*quantityst;
                        double tax=Double.parseDouble(data.getTax());

                        double i=(chetotal/((tax/100)+1));
                        double tax_amt=chetotal-i;
                        TaxAmount=String.valueOf(tax_amt);
                        String ttl="₹ "+chetotal;
                        subList.setProductFinalRate(String.valueOf(chetotal));
                        subList.setTax(TaxAmount);
                        total +=chetotal;
                        totalMaxDisc+=chediscout;
                        Log.v(TAG, String.format(" ::Total  max discount ******* %s",totalMaxDisc));
                    }
                }
                cartarrayList.add(subList);

            }
            String totlaprice="₹ "+String.valueOf(total);
            TotalTv.setText(totlaprice);
            adapter = new SelectedCartAdapter(filterArrayList, POSAddBillingActivity.this);
            recyclerView.setAdapter(adapter);
            String domainurl= SharedPrefereneceUtil.getDomainUrl(POSAddBillingActivity.this);
            inputContact.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    if(inputContact.getText().length()>0){

                        CheckContactClass();

                    }

                }
            });
        }

        PaymenttypeClass();

        spinPaymentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if(view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    View layout = (View) view.findViewById(R.id.layout);
                    layout.setPadding(0, 0, 0, 0);


                    if (index == 0) {
                        tv.setTextColor((Color.GRAY));
                    } else {
                        tv.setTextColor((Color.BLACK));
                    }
                    paymentType = tv.getText().toString();
                    if (index != 0) {
                        txtpaymentType.setVisibility(View.VISIBLE);
                    }


                }
                // ((TextView) spinEnquiryType.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
                // Showing selected spinner item
                //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // ************* Discount focus up listner ***********


        Checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // for ( k =0;k< cartarrayList.size(); k++) {
               submitForm();
                //}
            }
        });


    }
    private void submitForm() {
        //first validate the form then move ahead
        //if this becomes true that means validation is successfull
        //if(inputPassword.getText().toString().equals(inputCfmPassword.getText().toString())){
            if (awesomeValidation.validate()) {

                if( paymentType.equals(getResources().getString(R.string.hint_pyment_mode)) ){
                    Toast.makeText(this, "Please fill Payment Mode", Toast.LENGTH_LONG).show();
                }else{
                    AddPosBillingClass();

                }
            }


    }
    // *************** Payment Mode *******************
    public void  PaymenttypeClass() {
        POSAddBillingActivity.PaymentTypeTrackClass ru = new POSAddBillingActivity.PaymentTypeTrackClass();
        ru.execute("5");
    }
    class PaymentTypeTrackClass extends AsyncTask<String, Void, String> {

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
            PaymentTypeDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> PaymentTypeDetails = new HashMap<String, String>();
            PaymentTypeDetails.put("action", "show_payment_type_list");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(POSAddBillingActivity.this);
            //PaymentTypeloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(PaymentTypeloyee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, PaymentTypeDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void PaymentTypeDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
//        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayoutPrabhagDetails);
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    if (object != null) {
                        JSONArray jsonArrayCountry = object.getJSONArray("result");
                        paymentTypeArrayList.clear();
                        paymentTypelist = new Spinner_List();
                        paymentTypelist.setName(getResources().getString(R.string.hint_pyment_mode));
                        paymentTypeArrayList.add(0,paymentTypelist);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                paymentTypelist = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String PaymentType     = jsonObj.getString("PaymentType");

                                    String id=jsonObj.getString("Auto_Id");
//                               if(i==0){
//                                   paymentTypelist.setName(getResources().getString(R.string.promt_country));
//                                   enqF.add(0,paymentTypelist);
//                               }
                                    paymentTypelist.setName(PaymentType);
                                    paymentTypelist.setId(id);

                                    paymentTypeArrayList.add(paymentTypelist);

                                    paymentTypeadapter = new AddEnquirySpinnerAdapter(POSAddBillingActivity.this, paymentTypeArrayList){
                                        @Override
                                        public boolean isEnabled(int position){
                                            if(position == 0)
                                            {
                                                // Disable the first item from Spinner
                                                // First item will be use for hint
                                                return false;
                                            }
                                            else
                                            {
                                                return true;
                                            }
                                        }
                                        @Override
                                        public View getDropDownView(int position, View convertView,
                                                                    ViewGroup parent) {
                                            View view = super.getDropDownView(position, convertView, parent);
                                            TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                                            if(position == 0){
                                                // Set the hint text color gray
                                                tv.setTextColor(Color.GRAY);
                                                tv.setText(getResources().getString(R.string.prompt_payment_type));
                                                // tv.setTextColor(Color.GRAY);
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinPaymentType.setAdapter(paymentTypeadapter);


                                }
                            }
                        }else if(jsonArrayCountry.length()==0){
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){

                    //forumCount.setVisibility(View.INVISBLE);
                    // queCount.setVisibility(View.INVISIBLE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    public void  AddPosBillingClass() {

        POSAddBillingActivity.AddPosBillingTrackClass ru = new POSAddBillingActivity.AddPosBillingTrackClass();
        ru.execute("5");

    }

    class AddPosBillingTrackClass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
            //showProgressDialog();
            // viewDialog.showDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            // dismissProgressDialog();
            //viewDialog.hideDialog();
            //Toast.makeText(CandiateListView.this, response, Toast.LENGTH_LONG).show();
            //  Toast.makeText(NewCustomerActivity.this, response, Toast.LENGTH_LONG).show();
            AddPosBillingDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {

            JSONObject JSONproduct=null;
            JSONArray jsonArray = new JSONArray();
            for (POSItemList data : cartarrayList)
            {
                JSONproduct = new JSONObject();
                try {
                    String totl=data.getRate();
                    String sp[]=totl.split(" ");
                    String paid=TotalTv.getText().toString();
                    String sp1[]=paid.split(" ");

                    JSONproduct.put("prod_code",data.getProductCode());
                    JSONproduct.put("prod_name",data.getProductName());
                    JSONproduct.put("prod_qty",data.getQuantity());
                    JSONproduct.put("prod_rate",sp[1]);
                    JSONproduct.put("prod_total",data.getProductFinalRate());
                    JSONproduct.put("prod_tax",data.getTax());
                    JSONproduct.put("prod_discount","0");
                    JSONproduct.put("prod_final_amt",sp1[1]);
                    JSONproduct.put("prod_balance","0");
                    JSONproduct.put("prod_paid",sp1[1]);
                    JSONproduct.put("prod_invoice_ttl",sp1[1]);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(JSONproduct);

            }
            JSONObject finalobject = new JSONObject();
            try {
                finalobject.put("product", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> AddPosBillingDetails = new HashMap<String, String>();
            AddPosBillingDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(POSAddBillingActivity.this));
            AddPosBillingDetails.put("cust_name", inputName.getText().toString());
            AddPosBillingDetails.put("cust_cont", inputContact.getText().toString());
            AddPosBillingDetails.put("payment_type",paymentType);
            AddPosBillingDetails.put("payment_dtl", inputPaymentDtl.getText().toString());
            AddPosBillingDetails.put("sales_executive",SharedPrefereneceUtil.getName(POSAddBillingActivity.this));
            AddPosBillingDetails.put("tbl_count",String.valueOf(cartarrayList.size()));
            Log.v(TAG, String.format("doInBackground :: tbl_count= %s",String.valueOf(cartarrayList.size())));
            AddPosBillingDetails.put("tbl_arr",finalobject.toString());
            Log.v(TAG, String.format("doInBackground :: tbl_arr= %s",finalobject.toString()));
            AddPosBillingDetails.put("action", "add_pos_billing");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(POSAddBillingActivity.this);
            String loginResult2 = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, AddPosBillingDetails);

            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult2));
            return loginResult2;
        }
    }


    private void AddPosBillingDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject jsonObjLoginResponse = null;
        try {
            jsonObjLoginResponse = new JSONObject(jsonResponse);
            String success = jsonObjLoginResponse.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                Toast.makeText(POSAddBillingActivity.this,"POS Billing added succesfully",Toast.LENGTH_SHORT).show();

                int result=db.remove_from_cart(cartarrayList);
                Log.v(TAG, String.format("response :: result= %s",result));
                Intent intent=new Intent(POSAddBillingActivity.this,POSActivity.class);
                startActivity(intent);
                // showCustomDialog();

                //inputEmail, inputPhone,inputAdd,inputReq,inputFollowupdate;
            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.zero)))
            {
                Toast.makeText(POSAddBillingActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                //inputContact.getText().clear();
                // Toast.makeText(POSAddBillingActivity.this,"Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // ************** Check Contact number already exist or not **************
    public void  CheckContactClass() {
        POSAddBillingActivity.CheckContactTrackClass ru = new POSAddBillingActivity.CheckContactTrackClass();
        ru.execute("5");
    }

    class CheckContactTrackClass extends AsyncTask<String, Void, String> {

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
            //Toast.makeText(CandiateListView.this, response, Toast.LENGTH_LONG).show();
            //  Toast.makeText(NewCustomerActivity.this, response, Toast.LENGTH_LONG).show();
            CheckContactDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> EnquiryForDetails = new HashMap<String, String>();

            EnquiryForDetails.put("contact",inputContact.getText().toString() );
            EnquiryForDetails.put("comp_id",SharedPrefereneceUtil.getSelectedBranchId(POSAddBillingActivity.this) );
            EnquiryForDetails.put("action", "check_contact_already_exist_in_pos");
            //EnquiryForloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(EnquiryForloyee.this));
            String domainurl=SharedPrefereneceUtil.getDomainUrl(POSAddBillingActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, EnquiryForDetails);
            Log.v(TAG, String.format("doInBackground :: check_mobile_already_exist_or_not= %s", loginResult));
            return loginResult;
        }
    }
    private void CheckContactDetails(String jsonResponse) {

        Log.v(TAG, String.format("check_mobile_already_exist_or_not :: response = %s", jsonResponse));

        JSONObject jsonObjLoginResponse = null;
        try {
            jsonObjLoginResponse = new JSONObject(jsonResponse);
            String success = jsonObjLoginResponse.getString(getResources().getString(R.string.success));


            if (success.equalsIgnoreCase(getResources().getString(R.string.zero))) {
                // showCustomDialog();

                //inputEmail, inputPhone,inputAdd,inputReq,inputFollowupdate;
            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.two)))
            {
                String Coustomer_Name = jsonObjLoginResponse.getString("Coustomer_Name");
                //Toast.makeText(POSAddBillingActivity.this,"Contact Already Exits",Toast.LENGTH_SHORT).show();
                inputName.setText(Coustomer_Name);
                // Toast.makeText(POSAddBillingActivity.this,"Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
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
