package com.ndfitnessplus.Activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.ndfitnessplus.Activity.Notification.TodaysEnrollmentActivity;
import com.ndfitnessplus.Adapter.AddEnquirySpinnerAdapter;
import com.ndfitnessplus.MailUtility.Mail;
import com.ndfitnessplus.Model.FollowupList;
import com.ndfitnessplus.Model.Spinner_List;
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
import java.util.Calendar;
import java.util.HashMap;

public class BalanceReceiptActivity extends AppCompatActivity {
    public EditText inputContact,inputName,inputTtlCourseFees,inputPrePaid,inputRemBal,
            inputPaymentDtl,inputPaid,inputNextFollDate,inputComment,inputBalance;
    public TextInputLayout inputLayoutContact,inputLayoutName,inputLayoutTtlCourseFees,inputLayoutPrePaid,inputLayoutRemBal,
            inputLayoutPaymentDtl,inputLayoutPaid,inputLayoutNextFollDate,
            inputLayoutComment,inputLayoutBalance;

    public final String TAG = BalanceReceiptActivity.class.getName();
    private ProgressDialog pd;
    private AwesomeValidation awesomeValidation;
    private int mYear, mMonth, mDay;

    //Spinner Adapter
    public Spinner spinInvoiceRefid,spinPaymentype;
    Spinner_List invoiceRefIdlist,paymentTypeList;
    ArrayList<Spinner_List>invoiceRefidArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> paymentTypeArrayList = new ArrayList<Spinner_List>();

    public AddEnquirySpinnerAdapter invoiceRefidadapter,paymentTypeadapter;
    String invoiceRefId,paymentType,FinancialYear;
    TextView txtinvoiceRefId,txtPaymentType;
    //View LinearLytPackageName;
    String Session;
    String Email;
    String MaxDiscount="";
    double maxdisc;
    String EndDate;
    String MemberID;
    String Days;
    String Tax;
    String TaxAmount;
    String finalBalance;
    String subtotal;
    String afterEnquirySms;
    String InvoiceRefID="";
    String pack_name=" ";
    //Loading gif
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_receipt);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.add_balance));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){
        //Contact number
        inputLayoutContact = (TextInputLayout) findViewById(R.id.input_layout_cont);
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutTtlCourseFees = (TextInputLayout) findViewById(R.id.input_layout_ttl_coursefee);
        inputLayoutPrePaid = (TextInputLayout) findViewById(R.id.input_layout_prepaid);
        inputLayoutRemBal = (TextInputLayout) findViewById(R.id.input_layout_rembalance);
        inputLayoutPaymentDtl = (TextInputLayout) findViewById(R.id.input_layout_payment_details);
        inputLayoutPaid = (TextInputLayout) findViewById(R.id.input_layout_paid);
        inputLayoutNextFollDate = (TextInputLayout) findViewById(R.id.input_layout_next_foll_date);
        inputLayoutComment= (TextInputLayout) findViewById(R.id.input_layout_comment);
        inputLayoutBalance= (TextInputLayout) findViewById(R.id.input_layout_balance);

        viewDialog = new ViewDialog(this);

        inputContact = (EditText) findViewById(R.id.input_cont);
        inputName = (EditText) findViewById(R.id.input_name);
        inputTtlCourseFees = (EditText) findViewById(R.id.input_ttl_coursefee);
        inputPrePaid = (EditText) findViewById(R.id.input_prepaid);
        inputRemBal = (EditText) findViewById(R.id.input_rembalance);
        inputPaymentDtl = (EditText) findViewById(R.id.input_payment_details);
        inputPaid = (EditText) findViewById(R.id.input_paid);
        inputNextFollDate = (EditText) findViewById(R.id.input_nextfollDate);
        inputComment = (EditText) findViewById(R.id.input_comment);
        inputBalance = (EditText) findViewById(R.id.input_balance);



        Intent intent = getIntent();
        // Bundle args = intent.getBundleExtra("BUNDLE");
        if (intent != null) {
            MemberID=intent.getStringExtra("member_id");
            String  name=intent.getStringExtra("name");
            String  Contact=intent.getStringExtra("contact");
            Email=intent.getStringExtra("email");
            InvoiceRefID=intent.getStringExtra("invoice_id");
            pack_name=intent.getStringExtra("pack_name");
            inputContact.setText(Contact);
            inputName.setText(name);
            Log.v(TAG, String.format("Selected  ::Invoice Ref ID= %s", InvoiceRefID));
        }else{
            InvoiceRefID="";
            Log.v(TAG, String.format("Selected  ::Invoice Ref ID= %s", InvoiceRefID));
        }

        // *********** validation *************
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);


        awesomeValidation.addValidation(this, R.id.input_cont, RegexTemplate.NOT_EMPTY, R.string.err_msg_cont);
        awesomeValidation.addValidation(this, R.id.input_paid, RegexTemplate.NOT_EMPTY, R.string.err_msg_paid);


        inputContact.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(inputContact.getText().length()>0){
                    CheckContactClass();
                }


            }
        });
        //package spinners
        spinInvoiceRefid = (Spinner) findViewById(R.id.spinner_invoice_ref);
        spinPaymentype = (Spinner) findViewById(R.id.spinner_payment_type);

        txtinvoiceRefId=findViewById(R.id.txt_invoice_ref);
        txtPaymentType=findViewById(R.id.txt_payment_type);

        //api's for spinners

        PaymenttypeClass();



        //************** Setting data to spinner seletced item Package Type ***************
        spinInvoiceRefid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if(view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    View layout = (View) view.findViewById(R.id.layout);
                    layout.setPadding(0, 0, 0, 0);
                    final Spinner_List model = invoiceRefidArrayList.get(position);

                    //country_parent_id=model.getId();

                    //if(!(InvoiceRefID.equals("")||InvoiceRefID.equals("null"))){
                    if(pack_name==null){
                        Log.v(TAG, String.format("Selected  ::Invoice Ref ID= %s", InvoiceRefID));
                    }else{
                        String selectedpack=InvoiceRefID+","+pack_name;
                        txtinvoiceRefId.setVisibility(View.VISIBLE);
                        tv.setText(selectedpack);
                        tv.setTextColor((Color.BLACK));
                    }

                   // }

                    Log.v(TAG, String.format("Selected  ::Invoice Ref ID= %s", InvoiceRefID));
                    if (index == 0) {
                        //tv.setTextColor((Color.GRAY));
                    } else {
                        tv.setTextColor((Color.BLACK));
                    }
                    invoiceRefId = tv.getText().toString();

                    if (index != 0) {
                        txtinvoiceRefId.setVisibility(View.VISIBLE);
                        InvoiceRefID=model.getId();
                        FinancialYear=model.getFinicialyear();
                        if(inputContact.getText().length()==0){
                            Toast.makeText(parent.getContext(), "Please Select Member First ", Toast.LENGTH_LONG).show();

                        }

                    }
                    balanceDetailsclass();
                }
                // ((TextView) spinPackageType.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
                // Showing selected spinner item
                //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(parent.getContext(), "Please Select Enquiry Type ", Toast.LENGTH_LONG).show();
            }
        });

        // ************* rate and paid same then next followup date is null ***********
        inputPaid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(inputPaid.getText().length()>0) {
                    double rate = Double.parseDouble(inputRemBal.getText().toString());
                    double paid = Double.parseDouble(inputPaid.getText().toString());
                    double tax = Double.parseDouble(Tax);
                   // if (paid > 0) {

                    double finalbal = rate - paid;
                    finalBalance = String.valueOf(finalbal);
                    inputBalance.setText(finalBalance);
                    double i = (paid / ((tax / 100) + 1));
                    double tax_amt = paid - i;
                    TaxAmount = String.valueOf(tax_amt);
                    subtotal = String.valueOf(i);
                    // Log.v(TAG, String.format("Max Discount  :: souble max discout= %s", maxdisc));
                    if (rate == paid) {
                        inputNextFollDate.getText().clear();
                        inputNextFollDate.setEnabled(false);
                        inputNextFollDate.setKeyListener(null);
                        awesomeValidation.clear();
                    } else {
                        inputNextFollDate.setEnabled(true);
                        awesomeValidation.addValidation(BalanceReceiptActivity.this, R.id.input_nextfollDate, RegexTemplate.NOT_EMPTY, R.string.err_msg_next_foll_date);
                    }
                    if (paid > rate) {
                        Toast.makeText(BalanceReceiptActivity.this, "Your paying more than your fees", Toast
                                .LENGTH_SHORT).show();
                        inputNextFollDate.getText().clear();
                        inputNextFollDate.setEnabled(false);
                        inputNextFollDate.setKeyListener(null);
                    }
//                    }else{
//                        Toast.makeText(BalanceReceiptActivity.this, "Please pay some amount", Toast
//                                .LENGTH_SHORT).show();
//                    }
                }else{
                    inputBalance.setText("");
                }


            }
        });
        inputPaid.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }



            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {
                if(inputPaid.getText().length()>0) {
                    double rate=Double.parseDouble(inputRemBal.getText().toString());
                    double paid=Double.parseDouble(inputPaid.getText().toString());
                    double tax=Double.parseDouble(Tax);

                    double finalbal=rate-paid;
                    finalBalance=String.valueOf(finalbal);
                    inputBalance.setText(finalBalance);
                    double i=(paid/((tax/100)+1));
                    double tax_amt=paid-i;
                    TaxAmount=String.valueOf(tax_amt);
                    subtotal=String.valueOf(i);
                    if(rate == paid){
                        inputNextFollDate.getText().clear();
                        inputNextFollDate.setEnabled(false);
                        inputNextFollDate.setKeyListener(null);
                        awesomeValidation.clear();
                    } else{
                        inputNextFollDate.setEnabled(true);

                    }
                    if(paid>rate){
                        Toast.makeText(BalanceReceiptActivity.this,"Your paying more than your fees",Toast
                                .LENGTH_SHORT).show();
                        awesomeValidation.clear();
                        inputNextFollDate.getText().clear();
                        inputNextFollDate.setEnabled(false);
                        inputNextFollDate.setKeyListener(null);
                    }
                }else{
                    inputBalance.setText("");
                }

            }
        });

        // *********** Next Followup date calender ****************
        inputNextFollDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(BalanceReceiptActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                inputNextFollDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        spinPaymentype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                        txtPaymentType.setVisibility(View.VISIBLE);
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
    }
    //************ Submit button on action bar ***********
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_enquiry_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save_enquiry) {
            // sendEmail();
            submitForm();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void submitForm() {
        //first validate the form then move ahead
        //if this becomes true that means validation is successfull
        //if(inputPassword.getText().toString().equals(inputCfmPassword.getText().toString())){

        if(!(inputBalance.getText().toString().equals("0.0"))){
           // Toast.makeText(this, "Please fill Payment type or Package type", Toast.LENGTH_LONG).show();
            awesomeValidation.addValidation(BalanceReceiptActivity.this,R.id.input_nextfollDate,RegexTemplate.NOT_EMPTY,R.string.err_msg_next_payment_date);
            if (awesomeValidation.validate()) {
                // Log.v(TAG, String.format("Remaining balance= %s", inputBalance.getText().toString()));
                double paid = Double.parseDouble(inputPaid.getText().toString());
                if(invoiceRefId.equals(getResources().getString(R.string.hint_packagetype))
                        || paymentType.equals(getResources().getString(R.string.payment_type))){
                    Toast.makeText(this, "Please fill Payment type or Package type", Toast.LENGTH_LONG).show();
                }else{
                    if(paid <=0){
                        inputPaid.getText().clear();
                        inputBalance.getText().clear();
                        inputPaid.requestFocus();
                        Toast.makeText(BalanceReceiptActivity.this, "Please pay some amount", Toast
                                .LENGTH_SHORT).show();
                    }else{
                        AddBalanceReceiptClass();
                    }

                    Log.v(TAG, String.format("calling function= %s", "Add Balnce receipt"));
                }

            }
        }else{
        if (awesomeValidation.validate()) {
           // Log.v(TAG, String.format("Remaining balance= %s", inputBalance.getText().toString()));
            if(invoiceRefId.equals(getResources().getString(R.string.hint_packagetype))
                    || paymentType.equals(getResources().getString(R.string.payment_type)) ){
                Toast.makeText(this, "Please fill Payment type or Package type", Toast.LENGTH_LONG).show();
            }else{
                AddBalanceReceiptClass();
                Log.v(TAG, String.format("calling function= %s", "Add Balnce receipt"));
            }

          }
        }

    }
    private void showProgressDialog() {
        Log.v(TAG, String.format("showProgressDialog"));
        pd = new ProgressDialog(BalanceReceiptActivity.this);
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
    //   ************** Check Contact number already exist or not **************
    public void  CheckContactClass() {
        BalanceReceiptActivity.CheckContactTrackClass ru = new BalanceReceiptActivity.CheckContactTrackClass();
        ru.execute("5");
    }

    class CheckContactTrackClass extends AsyncTask<String, Void, String> {

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
            //dismissProgressDialog();
            viewDialog.hideDialog();
            //Toast.makeText(CandiateListView.this, response, Toast.LENGTH_LONG).show();
            //  Toast.makeText(NewCustomerActivity.this, response, Toast.LENGTH_LONG).show();
            CheckContactDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> EnquiryForDetails = new HashMap<String, String>();

            EnquiryForDetails.put("mobileno",inputContact.getText().toString() );
            EnquiryForDetails.put("user","Member" );
            EnquiryForDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptActivity.this) );
            EnquiryForDetails.put("action", "check_mobile_already_exist_or_not");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(BalanceReceiptActivity.this);
            //EnquiryForloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(EnquiryForloyee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, EnquiryForDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }
    }
    private void CheckContactDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject object = null;
        try {
            object = new JSONObject(jsonResponse);
            String success = object.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.zero))) {
                Toast.makeText(BalanceReceiptActivity.this,"Member is not registred. Please register Member first",Toast.LENGTH_SHORT).show();
                //inputContact.getText().clear();
                // showCustomDialog();
                Intent intent=new Intent(BalanceReceiptActivity.this,AddMemberActivity.class);
                intent.putExtra("contact",inputContact.getText().toString());
                startActivity(intent);
                //inputEmail, inputPhone,inputAdd,inputReq,inputFollowupdate;
            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.two)))
            {
                JSONArray jsonArrayResult = object.getJSONArray("Data");

                if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                    for (int i = 0; i < jsonArrayResult.length(); i++) {

                        Log.v(TAG, "JsonResponseOpeartion ::");
                        JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                        if (jsonObj != null) {

                            MemberID = jsonObj.getString("MemberID");
                            String Name = jsonObj.getString("Name");
                            Email = jsonObj.getString("Email");
                            inputName.setText(Name);
                            invoiceRefIdClass();

                        }
                    }
                } else if (jsonArrayResult.length() == 0) {
                    System.out.println("No records found");
                }
                // Toast.makeText(AddEnquiryActivity.this,"Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
            }else if(success.equalsIgnoreCase(getResources().getString(R.string.one))){
                Toast.makeText(BalanceReceiptActivity.this,"member has no active course.Please add course first",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(BalanceReceiptActivity.this,RenewActivity.class);
                intent.putExtra("contact",inputContact.getText().toString());
                startActivity(intent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // ************* Package Type spinner *******************
    public void  invoiceRefIdClass() {
        BalanceReceiptActivity.InvoiceRefTrackClass ru = new BalanceReceiptActivity.InvoiceRefTrackClass();
        ru.execute("5");
    }
    class InvoiceRefTrackClass extends AsyncTask<String, Void, String> {

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
            InvoiceRefDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> InvoiceRefDetails = new HashMap<String, String>();
            InvoiceRefDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptActivity.this));
            InvoiceRefDetails.put("member_id", MemberID);
            InvoiceRefDetails.put("action", "show_balance_package_name_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(BalanceReceiptActivity.this);
            //InvoiceRefloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(InvoiceRefloyee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, InvoiceRefDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void InvoiceRefDetails(String jsonResponse) {


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
                        invoiceRefidArrayList.clear();
                        invoiceRefIdlist = new Spinner_List();
                        invoiceRefIdlist.setName(getResources().getString(R.string.invoice_ref_id));
                        invoiceRefidArrayList.add(0,invoiceRefIdlist);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                invoiceRefIdlist = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Package_Name     = jsonObj.getString("Package_Name");
                                    String Invoice_ID     = jsonObj.getString("Invoice_ID");
                                    String Financial_Year     = jsonObj.getString("Financial_Year");

//                               if(i==0){
//                                   invoiceRefIdlist.setName(getResources().getString(R.string.promt_country));
//                                   enqF.add(0,invoiceRefIdlist);
//                               }
                                    String invoi_pack_name=Invoice_ID+","+Package_Name;
                                    invoiceRefIdlist.setName(invoi_pack_name);
                                    invoiceRefIdlist.setId(Invoice_ID);
                                    invoiceRefIdlist.setFinicialyear(Financial_Year);


                                    invoiceRefidArrayList.add(invoiceRefIdlist);

                                    invoiceRefidadapter = new AddEnquirySpinnerAdapter(BalanceReceiptActivity.this, invoiceRefidArrayList){
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
                                                tv.setText(getResources().getString(R.string.hint_invoice_ref_id));
                                                // tv.setTextColor(Color.GRAY);
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinInvoiceRefid.setAdapter(invoiceRefidadapter);


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

    //****************** package More information by Package Name *****************
    private void balanceDetailsclass() {
        BalanceReceiptActivity.BalanceTrackclass ru = new BalanceReceiptActivity.BalanceTrackclass();
        ru.execute("5");
    }

    class BalanceTrackclass extends AsyncTask<String, Void, String> {

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
            BalanceDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> BalanceDetails = new HashMap<String, String>();
            BalanceDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptActivity.this));
            BalanceDetails.put("invoice_id",InvoiceRefID );
            BalanceDetails.put("member_id",MemberID );
            BalanceDetails.put("financial_year",FinancialYear );
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptActivity.this)));
            BalanceDetails.put("action","show_balance_details_by_invoice_id");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(BalanceReceiptActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, BalanceDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }

    private void BalanceDetails(String jsonResponse) {

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
                        int count=0;
                        ArrayList<FollowupList> item = new ArrayList<FollowupList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {

                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Rate = jsonObj.getString("Rate");
                                    String Final_paid = jsonObj.getString("Final_paid");
                                    String Final_Balance = jsonObj.getString("Final_Balance");
                                    Tax = jsonObj.getString("Tax");
                                    inputTtlCourseFees.setText(Rate);
                                    inputPrePaid.setText(Final_paid);
                                    inputRemBal.setText(Final_Balance);




                                }
                            }
                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    //nodata.setVisibility(View.VISIBLE);

                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
//                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(BalanceReceiptActivity.this);
//                builder.setMessage(R.string.server_exception);
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
    }


    // *************** Payment Mode *******************
    public void  PaymenttypeClass() {
        BalanceReceiptActivity.PaymentTypeTrackClass ru = new BalanceReceiptActivity.PaymentTypeTrackClass();
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
            //PaymentTypeloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(PaymentTypeloyee.this));
            String domainurl=SharedPrefereneceUtil.getDomainUrl(BalanceReceiptActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, PaymentTypeDetails);
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
                        paymentTypeList = new Spinner_List();
                        paymentTypeList.setName(getResources().getString(R.string.payment_type));
                        paymentTypeArrayList.add(0,paymentTypeList);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                paymentTypeList = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String PaymentType     = jsonObj.getString("PaymentType");

                                    String id=jsonObj.getString("Auto_Id");
//                               if(i==0){
//                                   paymentTypeList.setName(getResources().getString(R.string.promt_country));
//                                   enqF.add(0,paymentTypeList);
//                               }
                                    paymentTypeList.setName(PaymentType);
                                    paymentTypeList.setId(id);

                                    paymentTypeArrayList.add(paymentTypeList);

                                    paymentTypeadapter = new AddEnquirySpinnerAdapter(BalanceReceiptActivity.this, paymentTypeArrayList){
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
                                    spinPaymentype.setAdapter(paymentTypeadapter);


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
    public void  AddBalanceReceiptClass() {
        BalanceReceiptActivity.AddBalanceReceiptTrackClass ru = new BalanceReceiptActivity.AddBalanceReceiptTrackClass();
        ru.execute("5");
    }
    class AddBalanceReceiptTrackClass extends AsyncTask<String, Void, String> {

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
            //dismissProgressDialog();
            viewDialog.hideDialog();
            //Toast.makeText(CandiateListView.this, response, Toast.LENGTH_LONG).show();
            //  Toast.makeText(NewCustomerActivity.this, response, Toast.LENGTH_LONG).show();
            AddBalanceReceiptDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            Log.v(TAG, String.format("doInBackground ::  params= %s", params));


            HashMap<String, String> AddBalanceReceiptDetails = new HashMap<String, String>();
            AddBalanceReceiptDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptActivity.this));
            AddBalanceReceiptDetails.put("member_id",MemberID);
            AddBalanceReceiptDetails.put("name",inputName.getText().toString());
            AddBalanceReceiptDetails.put("contact",inputContact.getText().toString());
            AddBalanceReceiptDetails.put("email",Email);
            AddBalanceReceiptDetails.put("invoice_id",InvoiceRefID);
            AddBalanceReceiptDetails.put("subtotal",subtotal);
            AddBalanceReceiptDetails.put("tax",Tax);
            AddBalanceReceiptDetails.put("tax_amount",TaxAmount);
            AddBalanceReceiptDetails.put("payment_type",paymentType);
            Log.v(TAG, String.format("doInBackground :: payment_type= %s", paymentType));
            AddBalanceReceiptDetails.put("payment_details",inputPaymentDtl.getText().toString());
            AddBalanceReceiptDetails.put("paid",inputPaid.getText().toString());
            AddBalanceReceiptDetails.put("balance",finalBalance);
            AddBalanceReceiptDetails.put("comment",inputComment.getText().toString());
            AddBalanceReceiptDetails.put("next_payment_date",inputNextFollDate.getText().toString());
            AddBalanceReceiptDetails.put("mem_own_exe",SharedPrefereneceUtil.getName(BalanceReceiptActivity.this));
            AddBalanceReceiptDetails.put("financial_year",FinancialYear);
            Log.v(TAG, String.format("doInBackground :: executive name= %s", SharedPrefereneceUtil.getName(BalanceReceiptActivity.this)));
            AddBalanceReceiptDetails.put("action", "add_balance_receipt");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(BalanceReceiptActivity.this);
            String loginResult2 = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, AddBalanceReceiptDetails);

            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult2));
            return loginResult2;
        }
    }


    private void AddBalanceReceiptDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject jsonObjLoginResponse = null;
        try {
            jsonObjLoginResponse = new JSONObject(jsonResponse);
            String success = jsonObjLoginResponse.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                Toast.makeText(BalanceReceiptActivity.this,"Balance Paid succesfully",Toast.LENGTH_SHORT).show();
                //inputName.getText().clear();
                //inputContact.getText().clear();
                receiptdatalass();
                SendEnquirySmsClass();
                //if(!Email.equals("")){

              //  }
                // imageView.setImageResource(R.drawable.add_photo);

                // showCustomDialog();

                //inputEmail, inputPhone,inputAdd,inputReq,inputFollowupdate;
            }


            else if (success.equalsIgnoreCase(getResources().getString(R.string.one)))
            {
                Toast.makeText(BalanceReceiptActivity.this,"Mobile Number Already Exits ,Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
                inputContact.getText().clear();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void receiptdatalass() {
        BalanceReceiptActivity.ReceiptDataTrackclass ru = new BalanceReceiptActivity.ReceiptDataTrackclass();
        ru.execute("5");
    }
    class ReceiptDataTrackclass extends AsyncTask<String, Void, String> {

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
            Log.v(TAG, String.format("onPostExecute :: show_receipt_data = %s", response));
            //dismissProgressDialog();
            viewDialog.hideDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            ReceiptDataDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> ReceiptDataDetails = new HashMap<String, String>();
            ReceiptDataDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptActivity.this));
            ReceiptDataDetails.put("invoice_id", InvoiceRefID);
            Log.v(TAG, String.format("doInBackground :: receipt data invoice_id= %s", InvoiceRefID));
            ReceiptDataDetails.put("financial_yr", FinancialYear);
            Log.v(TAG, String.format("doInBackground :: receipt data company id = %s", FinancialYear));
            Log.v(TAG, String.format("doInBackground :: receipt data company id = %s", SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptActivity.this)));
            ReceiptDataDetails.put("action","show_receipt_data");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(BalanceReceiptActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, ReceiptDataDetails);
            Log.v(TAG, String.format("doInBackground :: show_receipt_data= %s", loginResult));
            return loginResult;
        }


    }

    private void ReceiptDataDetails(String jsonResponse) {

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

                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                            for (int i = 0; i < jsonArrayResult.length(); i++) {

                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Name = inputName.getText().toString();
                                    String Member_Contact = inputContact.getText().toString();
                                    //String Invoice_date = jsonObj.getString("Invoice_date");
                                    String invoice_date=Utility.getCurrentDate();
                                    String Package_Name = jsonObj.getString("Package_Name");
                                    String Duration_Days =jsonObj.getString("Duration_Days");
                                    String Session = jsonObj.getString("Session");
                                    String Start_Date = jsonObj.getString("Start_Date");
                                    String start_date=Utility.formatDateDB(Start_Date);
                                    String End_Date = jsonObj.getString("End_Date");
                                    String end_date=Utility.formatDateDB(End_Date);
                                    String Rate = jsonObj.getString("Rate");
                                    String Final_paid = jsonObj.getString("Final_paid");;
                                    String Final_Balance =  jsonObj.getString("Final_Balance");;
                                    if(Final_Balance.equals(".00")){
                                        Final_Balance="0.00";
                                    }
                                    String Invoice_ID = InvoiceRefID;
                                    String tax = Tax;
                                    if(tax.equals(".00")){
                                        tax="0.00";
                                    }
                                    String Member_Email_ID = Email;
                                    String Time =  jsonObj.getString("Time");;
                                    String Instructor_Name = jsonObj.getString("Instructor_Name");;
                                    String Package_Fees =  jsonObj.getString("Package_Fees");;
                                    String Discount =  jsonObj.getString("Discount");;
                                    if(Discount.equals(".00")){
                                        Discount="0.00";
                                    }
                                    String Registration_Fees =  jsonObj.getString("Registration_Fees");;
                                    if(Registration_Fees.equals(".00")){
                                        Registration_Fees="0.00";
                                    }

                                    String Company_Name = SharedPrefereneceUtil.getCompanyName(BalanceReceiptActivity.this)+"-"+SharedPrefereneceUtil.getSelectedBranch(BalanceReceiptActivity.this);
                                    String Address = jsonObj.getString("Address");
                                    String Contact = jsonObj.getString("Contact");
                                    String TermsAndConditions = jsonObj.getString("TermsAndConditions");
                                    TermsAndConditions = TermsAndConditions.replace("\r\n", "<br />");
                                    String Logo = jsonObj.getString("Logo");
                                    String l=Logo.replaceAll("\\s+","%20");
                                    // Logo.replace(" ","%20");
                                    String imgurl=ServiceUrls.IMAGES_URL+l;
                                    Log.d(TAG, "imgurl: " +imgurl);
                                    // String Auto_Id = jsonObj.getString("Auto_Id");
                                    //String Receipt_Id =  jsonObj.getString("Receipt_Id");;
                                   // String ReceiptDate = Utility.getCurrentDate();
                                    //String receipt_date=Utility.formatDateDB(ReceiptDate);
//                                    String Taxamount = TaxAmount;
//                                    if(Taxamount.equals(".00")){
//                                        Taxamount="0.00";
//                                    }
//                                    String Paid = inputPaid.getText().toString();
//                                    String PaymentType = paymentType;
//                                    String PaymentDetails = inputPaymentDtl.getText().toString();
//                                    String ReceiptOwnerExecutive = SharedPrefereneceUtil.getName(BalanceReceiptActivity.this);

                                    String textBody = "";

                                    JSONArray jsonArrayPayTrasa = jsonObj.getJSONArray("payment_transa");
                                    if (jsonArrayPayTrasa != null && jsonArrayPayTrasa.length() > 0) {
                                        for (int loopCount = 0; loopCount < jsonArrayPayTrasa.length(); loopCount++)
                                        {
                                            JSONObject jsonObj1 = jsonArrayPayTrasa.getJSONObject(loopCount);
                                            if (jsonObj1 != null) {
                                                String Receipt_Id = jsonObj1.getString("Receipt_Id");
                                                // String start_date=Utility.formatDateDB(Start_Date);
                                                String ReceiptDate = jsonObj1.getString("ReceiptDate");
                                                String receipt_date=Utility.formatDateDB(ReceiptDate);
                                                String Tax = jsonObj1.getString("Tax");
                                                if(Tax.equals(".00")){
                                                    Tax="0.00";
                                                }
                                                String TaxAmount = jsonObj1.getString("TaxAmount");
                                               // String Taxamount = TaxAmount;
                                                if(TaxAmount.equals(".00")){
                                                    TaxAmount="0.00";
                                                }
                                                String Paid =  jsonObj1.getString("Paid");
                                                String PaymentType =  jsonObj1.getString("PaymentType");
                                                String PaymentDetails =  jsonObj1.getString("PaymentDetails");
                                                String ReceiptOwnerExecutive =  jsonObj1.getString("ReceiptOwnerExecutive");

                                                textBody += "  <tr style\"display: table-row; \n \n" +
                                                        "            vertical-align: inherit;border-spacing: 0px; \n\n" +
                                                        "             border-color: grey;border: thick;border: 1px solid #ddd;" +
                                                        "            border-color: black;\">\n \n" +
                                                        "    <td style=\"padding: 8px;\n\n" +
                                                        "     line-height: 1.428571429;\n\n" +
                                                        "     vertical-align: top;\n\n" +
                                                        "     border: 1px solid #ddd; display: table-cell; \n \n\n" +
                                                        "        text-align: center;\">"+Receipt_Id+"</td>\n \n" +
                                                        "     <td style=\"padding: 8px;\n" +
                                                        "    line-height: 1.428571429;\n" +
                                                        "    vertical-align: top;\n" +
                                                        "    border: 1px solid #ddd; display: table-cell; \n \n" +
                                                        "       text-align: center;\">"+receipt_date+"</td>\n\n" +
                                                        "    <td style=\"padding: 8px;\n\n" +
                                                        "     line-height: 1.428571429;\n\n" +
                                                        "     vertical-align: top;\n\n" +
                                                        "     border: 1px solid #ddd; display: table-cell; \n \n\n" +
                                                        "        text-align: center;\">"+Tax+"</td> \n\n" +
                                                        "    <td style=\"padding: 8px;\n\n" +
                                                        "    line-height: 1.428571429;\n\n" +
                                                        "    vertical-align: top;\n\n" +
                                                        "    border: 1px solid #ddd; display: table-cell; \n \n" +
                                                        "       text-align: center;\">"+TaxAmount+"</td>\n\n" +
                                                        "    <td style=\"padding: 8px;\n\n" +
                                                        "    line-height: 1.428571429;\n\n" +
                                                        "    vertical-align: top;\n\n" +
                                                        "    border: 1px solid #ddd; display: table-cell; \n \n\n" +
                                                        "       text-align: center;\">"+Paid+"</td>\n\n" +
                                                        "    <td style=\"padding: 8px;\n\n" +
                                                        "    line-height: 1.428571429;\n\n" +
                                                        "    vertical-align: top;\n\n" +
                                                        "    border: 1px solid #ddd; display: table-cell; \n \n" +
                                                        "       text-align: center;\">"+PaymentType+"</td>\n\n" +
                                                        "    <td style=\" padding: 8px;\n \n" +
                                                        "     line-height: 1.428571429;\n \n" +
                                                        "     vertical-align: top;\n \n" +
                                                        "     border: 1px solid #ddd; display: table-cell; \n\n" +
                                                        "        text-align: center;\">"+PaymentDetails+"</td>\n\n" +
                                                        "    <td style=\" padding: 5px;\n\n" +
                                                        "    line-height: 1.428571429;\n\n" +
                                                        "    vertical-align: top;\n\n" +
                                                        "    border: 1px solid #ddd; display: table-cell; \n\n" +
                                                        "       text-align: center;\">"+ReceiptOwnerExecutive+"</td>\n\n" +
                                                        "    </tr>\n";
                                            }
                                        }

                                    }





                                    final String message = "<!DOCTYPE html>\n" +
                                            "\n" +
                                            "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                                            "<head runat=\"server\">\n" +
                                            "    <title></title>\n" +
                                            "</head>\n" +
                                            "\n" +
//                                            "<link href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css\" rel=\"stylesheet\" id=\"bootstrap-css\">\n" +
//                                            "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/js/bootstrap.min.js\"></script>\n" +
////                                            "<script src=\"//code.jquery.com/jquery-1.11.1.min.js\"></script>\n" +
                                            "\n" +
                                            "\n" +
                                            "\n" +
                                            "\n" +
                                            "\n" +
                                            "\n" +
                                            "<!------ Include the above in your HEAD tag ---------->\n" +
                                            "<body>\n" +
                                            "    <form runat=\"server\">\n" +
                                            "\n" +
                                            "    <div id=\"divprint\" style=\"  width: 990;\n" +
                                            "       margin-right: auto;\n" +
                                            "       margin-left: auto;\n" +
                                            "       padding-left: 15px;\n" +
                                            "       padding-right: 15px;\">\n" +
                                            "\n" +
                                            " <div style=\" margin-left: -15px;margin-right: -15px;\">\n"  +
                                            " <div style=\"   width: 100%;\n" +
                                            "   min-width: 992px;\n" +
                                            "   position: relative; margin-bottom:15px;\n" +
                                            "   min-height: 1px;\n" +
                                            "   padding-left: 15px;\n" +
                                            "   padding-right: 15px;\">\n" +
                                            "\n" +
                                            "                <div style=\"width: 100%;display: flex; margin-left: -15px;margin-right: -15px;\">\n" +
                                            "                    <div style=\" flex: 50%;  \">\n" +
                                            "                        <address>\n" +
                                            "                            <strong style=\"font: 700;\">\n" +Company_Name+
                                            "                               </strong><br />" +Address+
                                            "                           <br />" +Contact+
                                            "                            <br />" +
                                            "                        </address>" +
                                            "                    </div>" +
                                            "                    <div style=\"  flex: 50%; width: 100%;  text-align: right;\">" +
                                            "                        <address>" +
                                            "                        " +
                                            "                            <img src="+ imgurl +">" +
                                            "                        </address>\n" +
                                            "                    </div>" +
                                            "                </div>" +
                                            "                <div style=\" display: flex; margin-left: -15px;margin-right: -15px;\">\n" +
                                            "                    <div  style=\" flex: 50%;  \">\n" +
                                            "                        <address>\n" +
                                            "                            <strong>Bill To:</strong><br />\n" +
                                            "                            <strong style=\"font: 900;\">"+Name +"</strong><br>\n" + Member_Email_ID+
                                            "                           <br>\n" +Member_Contact+
                                            "                        </address>\n" +
                                            "                    </div>" +
                                            "                    <div style=\" flex: 50%;  width: 100%;  text-align: right;\">" +
                                            "                        <address>" +
                                            "                            <strong>Invoice Date :"+invoice_date+"</strong><br>\n" +
                                            "\n" +
                                            "                            <strong>Invoice No : "+Invoice_ID+"</strong>\n" +
                                            "\n" +
                                            "                        </address>\n" +
                                            "                    </div>\n" +
                                            "                </div>\n" +
                                            "            </div>\n" +
                                            "        </div>\n" +
                                            "\n" +
                                            "        <div  style=\" margin-left: -15px;margin-right: -15px;\">\n" +
                                            "\n" +
                                            "            <div style=\"   width: 100%;\n" +
                                            "   min-width: 992px;\n" +
                                            "   position: relative;\n" +
                                            "   min-height: 1px;\n" +
                                            "   padding-left: 15px;\n" +
                                            "   padding-right: 15px; \">\n" +
                                            "                <div style=\"  margin-bottom: 20px;\n" +
                                            "   background-color: #fff;\n" +
                                            "   border: 1px solid transparent;\n" +
                                            "   border-radius: 4px;\n" +
                                            "   -webkit-box-shadow: 0 1px 1px rgba(0,0,0,.05);\n" +
                                            "   box-shadow: 0 1px 1px rgba(0,0,0,.05); border-color: #ddd;\">\n" +
                                            "                    <div  style=\" padding: 10px 15px;\n" +
                                            "   border-bottom: 1px solid transparent;\n" +
                                            "   border-top-right-radius: 3px;\n" +
                                            "   border-top-left-radius: 3px; background-color: darkgrey; border-color: black;\">\n" +
                                            "                        <h3 style=\"  margin-top: 0;\n" +
                                            "   margin-bottom: 0;\n" +
                                            "   font-size: 16px;\n" +
                                            "   color: inherit; \"><strong>Package Summary</strong></h3>\n" +
                                            "                    </div>\n" +
                                            "                    <div  style=\" padding: 15px;\n" +
                                            "   content: \" \";\n" +
                                            "      display: table;\n" +
                                            "      clear: both;padding: 0px; border-color: black;\">\n" +
                                            "                        <div  style=\"min-height:.01%;overflow-x:auto; width:100%;" +
                                            "overflow-y:hidden;-ms-overflow-style:-ms-autohiding-scrollbar;border:1px solid #ddd;" +
                                            "overflow: hidden;\">\n" +
                                            "                            <table  style=\"margin-bottom: 0px; border: thick; width: 100%;  max-width: 100%;\n" +
                                            "   background-color: transparent;\n" +
                                            "   border-collapse: collapse;\n" +
                                            "   border-spacing: 0;\">\n" +
                                            "                                <thead style=\"  display: table-header-group;\n" +
                                            "   vertical-align: middle;\n" +
                                            "   border-color: inherit;\">\n" +
                                            "                                    <tr style=\"display: table-row;\n" +
                                            "   vertical-align: inherit;\n" +
                                            "   border-color: inherit;border: thick; border-color: black;\">\n" +
                                            "                                      <td style=\"display: table-cell;\n" +
                                            "                                                vertical-align: inherit;\n" +
                                            "                                                border: 1px solid #ddd;\n" +
                                            "                                                padding: 8px;\n" +
                                            "                                                line-height: 1.428571429;\n" +
                                            "    vertical-align: top; \"><strong>Package</strong></td>\n" +
                                            "                                       <td style=\" display: table-cell;\n" +
                                            "                                                   vertical-align: inherit;\n" +
                                            "                                                   border: 1px solid #ddd;\n" +
                                            "                                                   padding: 8px;\n" +
                                            "                                                   line-height: 1.428571429;\n" +
                                            "                                                   vertical-align: top;  text-align: center;\" ><strong>Duration</strong></td>\n" +
                                            "                                        <td style=\" display: table-cell;\n " +
                                            "                                                vertical-align: inherit;\n" +
                                            "                                              border: 1px solid #ddd;\n" +
                                            "                                                padding: 8px; \n" +
                                            "                                               line-height: 1.428571429;\n \n" +
                                            "                                              vertical-align: top;  text-align: center;\"><strong>Session</strong></td>                                    \n" +
                                            "                                        <td style=\" display: table-cell;\n \n" +
                                            "                                               vertical-align: inherit;\n \n" +
                                            "                                              border: 1px solid #ddd;\n" +
                                            "                                               padding: 8px;\n" +
                                            "                                               line-height: 1.428571429; \n" +
                                            "                                               vertical-align: top;  text-align: center;\"><strong>StartDate</strong></td>\n" +
                                            "                                        <td style=\" display: table-cell; \n" +
                                            "                                               vertical-align: inherit; \n" +
                                            "                                              border: 1px solid #ddd;\n" +
                                            "                                               padding: 8px;\n" +
                                            "                                               line-height: 1.428571429; \n" +
                                            "                                                vertical-align: top;  text-align: center;\"><strong>EndDate</strong></td>\n" +
                                            "                                        <td style=\" display: table-cell;\n \n" +
                                            "                                               vertical-align: inherit;\n \n" +
                                            "                                               border: 1px solid #ddd;\n \n" +
                                            "                                               padding: 8px;\n \n" +
                                            "                                               line-height: 1.428571429;\n \n" +
                                            "                                            vertical-align: top;  text-align: center;\"><strong>Time</strong></td>\n" +
                                            "                                        <td style=\" display: table-cell;\n \n" +
                                            "                                               vertical-align: inherit;\n \n" +
                                            "                                              border: 1px solid #ddd;\n \n" +
                                            "                                               padding: 8px;\n \n" +
                                            "                                               line-height: 1.428571429;\n \n" +
                                            "                                                vertical-align: top;  text-align: center;\"><strong>Instructor</strong></td>\n" +
                                            "                                        <td style=\" display: table-cell;\n \n" +
                                            "                                               vertical-align: inherit;\n \n" +
                                            "                                              border: 1px solid #ddd;\n" +
                                            "                                               padding: 8px; \n" +
                                            "                                               line-height: 1.428571429;\n \n" +
                                            "                                                vertical-align: top;  text-align: right;\"><strong>Package Fees</strong></td>\n" +
                                            "\n" +
                                            "                                    </tr>\n" +
                                            "                                </thead>\n" +
                                            "                                <tbody style=\"     display: table-row-group;\n\n" +
                                            "                                              vertical-align: middle;\n\n" +
                                            "                                              border-color: inherit;\">\n" +
                                            "                                    <!-- foreach ($order->lineItems as $line) or some such thing here -->\n" +
                                            "                                    <tr style=\"display: table-row; \n" +
                                            "                                               vertical-align: inherit; \n" +
                                            "                                               border-color: inherit;border: thick; border-color: black;\">\n" +
                                            "                                        <td style=\"display: table-cell; \n" +
                                            "                                               vertical-align: inherit;\n \n" +
                                            "                                              border: 1px solid #ddd;\n" +
                                            "                                               padding: 8px; \n" +
                                            "                                               line-height: 1.428571429; \n" +
                                            "                                                vertical-align: top; \">"+Package_Name+"</td>\n" +
                                            "                                         <td style=\" display: table-cell;\n \n" +
                                            "                                               vertical-align: inherit;\n \n" +
                                            "                                               border: 1px solid #ddd;\n" +
                                            "                                               padding: 8px; \n" +
                                            "                                               line-height: 1.428571429;\n \n" +
                                            "                                                vertical-align: top;  text-align: center;\">"+Duration_Days+"</td>\n" +
                                            "                                        <td style=\" display: table-cell; \n" +
                                            "                                               vertical-align: inherit; \n" +
                                            "                                              border: 1px solid #ddd;\n" +
                                            "                                               padding: 8px; \n" +
                                            "                                               line-height: 1.428571429;\n" +
                                            "                                                vertical-align: top;  text-align: center;\">"+Session+"</td>                                       \n" +
                                            "                                        <td style=\" display: table-cell; \n" +
                                            "                                               vertical-align: inherit; \n" +
                                            "                                              border: 1px solid #ddd;\n" +
                                            "                                               padding: 8px; \n" +
                                            "                                               line-height: 1.428571429; \n" +
                                            "                                                vertical-align: top;  text-align: center;\">"+start_date+"</td>\n" +
                                            "                                        <td style=\" display: table-cell; \n" +
                                            "                                               vertical-align: inherit; \n" +
                                            "                                              border: 1px solid #ddd;\n" +
                                            "                                               padding: 8px; \n" +
                                            "                                               line-height: 1.428571429; \n" +
                                            "                                                vertical-align: top;  text-align: center;\">"+end_date+"</td>\n" +
                                            "                                        <td style=\" display: table-cell; \n" +
                                            "                                               vertical-align: inherit; \n" +
                                            "                                              border: 1px solid #ddd;\n" +
                                            "                                               padding: 8px; \n" +
                                            "                                               line-height: 1.428571429; \n" +
                                            "                                                vertical-align: top;  text-align: center;\">"+Time+"</td>\n" +
                                            "                                        <td style=\" display: table-cell; \n" +
                                            "                                               vertical-align: inherit; \n" +
                                            "                                              border: 1px solid #ddd;\n" +
                                            "                                               padding: 8px; \n" +
                                            "                                               line-height: 1.428571429; \n" +
                                            "                                                vertical-align: top;  text-align: center;\">"+Instructor_Name+"</td>\n" +
                                            "                                        <td style=\" display: table-cell; \n\n" +
                                            "                                            vertical-align: inherit; \n\n" +
                                            "                                           border: 1px solid #ddd;\n" +
                                            "                                            padding: 8px; \n\n" +
                                            "                                            line-height: 1.428571429; \n\n" +
                                            "                                             vertical-align: top; text-align: right;\">"+Package_Fees+"</td>\n" +
                                            "                                    </tr>\n" +
                                            "\n" +
                                            "                                </tbody>\n" +
                                            "                            </table>\n" +
                                            "                        </div>\n" +
                                            "                    </div>\n" +
                                            "\n" +
                                            "                    <div style=\""+
                                            "      display: table;\n" +
                                            "      clear: both; padding: 15px;\n" +
                                            "   content: \" \";\n" +
                                            "      display: table;\n" +
                                            "      \" >\n" +
                                            "                        <div  style=\"min-height:.01%;overflow-x:auto; width:100%;\" \n" +
                                            "                                            overflow-y:hidden;-ms-overflow-style:-ms-autohiding-scrollbar;border:1px solid #ddd;\n" +
                                            "                                            overflow: hidden;\">\n" +
                                            "                            <table  style=\" width: 100%;   max-width: 100%;\n" +
                                            "   background-color: transparent;\n" +
                                            "   border-collapse: collapse;\n" +
                                            "   border-spacing: 0;margin-bottom: 0px;\">\n" +
                                            "                                <thead>\n" +
                                            "                                    <tr style=\"display: table-row; \n" +
                                            "                                              vertical-align: inherit; \n" +
                                            "                                            \t    border-color: inherit;border: thick; border-color: black;\">\n" +
                                            "                                        <td style=\"display: table-cell; \n" +
                                            "                                            \t    vertical-align: inherit; \n" +
                                            "                                            \t     border: 1px solid #ddd; \n" +
                                            "                                            \t    padding:8px; \n" +
                                            "                                            \t    line-height: 1.428571429; \n" +
                                            "                                             vertical-align: top; \"><strong>Discount</strong></td>\n" +
                                            "                                        <td style=\" display: table-cell; \n" +
                                            "                                            \t    vertical-align: inherit; \n" +
                                            "                                                border: 1px solid #ddd; \n" +
                                            "                                               padding:8px; \n" +
                                            "                                               line-height: 1.428571429; \n" +
                                            "                                                vertical-align: top;  text-align: center;\"><strong>Reg Fees</strong></td>\n" +
                                            "                                        <td style=\" display: table-cell; \n" +
                                            "                                               vertical-align: inherit; \n" +
                                            "                                                border: 1px solid #ddd; \n" +
                                            "                                               padding:8px; \n" +
                                            "                                               line-height: 1.428571429; \n" +
                                            "                                                vertical-align: top;  text-align: center;\"><strong>Total Amount</strong></td>\n" +
                                            "                                        <td style=\" display: table-cell; \n" +
                                            "                                              vertical-align: inherit; \n" +
                                            "                                               border: 1px solid #ddd; \n" +
                                            "                                              padding:8px; \n" +
                                            "                                              line-height: 1.428571429; \n" +
                                            "                                                vertical-align: top;  text-align: center;\"><strong>Paid Amount</strong></td>\n" +
                                            "                                        <td style=\" display: table-cell; \n\n \n" +
                                            "                                           vertical-align: inherit; \n\n" +
                                            "                                            border: 1px solid #ddd; \n" +
                                            "                                           padding:8px; \n\n\n" +
                                            "                                           line-height: 1.428571429; \n\n\n" +
                                            "                                             vertical-align: top; text-align: right;\"><strong>Balance</strong></td>\n" +
                                            "\n" +
                                            "                                    </tr>\n" +
                                            "                                </thead>\n" +
                                            "                                <tbody style=\"     display: table-row-group;\n\n" +
                                            "                                              vertical-align: middle;\n" +
                                            "                                              border-color: inherit;\">\n" +
                                            "                                    <!-- foreach ($order->lineItems as $line) or some such thing here -->\n" +
                                            "                                    <tr>\n" +
                                            "                                        <td style=\" display: table-cell; \n\n" +
                                            "                                            vertical-align: inherit; \n \n" +
                                            "                                             border: 1px solid #ddd;\n \n" +
                                            "                                            padding:8px; \n\n" +
                                            "                                            line-height: 1.428571429; \n\n" +
                                            "                                            vertical-align: top;  \">"+Discount+"</td>\n" +
                                            "                                        <td style=\" display: table-cell; \n" +
                                            "                                              vertical-align: inherit; \n" +
                                            "                                               border: 1px solid #ddd;\n" +
                                            "                                              padding:8px; \n" +
                                            "                                              line-height: 1.428571429; \n" +
                                            "                                              vertical-align: top;  text-align: center;\">"+Registration_Fees+"</td>\n" +
                                            "                                       \n" +
                                            "                                        <td style=\" display: table-cell; \n" +
                                            "                                              vertical-align: inherit; \n" +
                                            "                                               border: 1px solid #ddd;\n" +
                                            "                                              padding:8px; \n" +
                                            "                                              line-height: 1.428571429; \n" +
                                            "                                               vertical-align: top;  text-align: center;\">"+Rate+"</td>\n" +
                                            "                                        <td style=\" display: table-cell; \n" +
                                            "                                              vertical-align: inherit; \n" +
                                            "                                               border: 1px solid #ddd;\n" +
                                            "                                              padding:8px; \n" +
                                            "                                              line-height: 1.428571429; \n" +
                                            "                                                vertical-align: top;  text-align: center;\">"+Final_paid+"</td>\n" +
                                            "                                        <td style=\" display: table-cell; \n\n" +
                                            "                                             vertical-align: inherit; \n" +
                                            "                                              border: 1px solid #ddd;\n" +
                                            "                                             padding:8px; \n\n" +
                                            "                                             line-height: 1.428571429; \n\n" +
                                            "                                               vertical-align: top; text-align: right;\">"+Final_Balance+"</td>\n" +
                                            "                                    </tr>\n" +
                                            "\n" +
                                            "                                </tbody>\n" +
                                            "                            </table>\n" +
                                            "                        </div>\n" +
                                            "                    </div>\n" +
                                            "\n" +
                                            "                </div>\n" +
                                            "            </div>\n" +
                                            "        </div>\n" +
                                            "\n" +
                                            "       \n" +
                                            " <div  style=\" margin-left: -15px;margin-right: -15px;\">\n" +
                                            "\n" +
                                            "    <div style=\"   width: 100%;\n" +
                                            "   min-width: 992px;\n" +
                                            "   position: relative;\n" +
                                            "   min-height: 1px;\n" +
                                            "   padding-left: 15px;\n" +
                                            "   padding-right: 15px; \">\n" +

                                            "            <div style=\" \n" +
                                            "    background-color: #fff;\n" +
                                            "    border: 1px solid transparent; \n" +
                                            "    border-radius: 4px;\n" +
                                            "    -webkit-box-shadow: 0 1px 1px rgba(0,0,0,.05);\n" +
                                            "    box-shadow: 0 1px 1px rgba(0,0,0,.05);\n" +
                                            "    border-color: #ddd;\">\n" +
                                            "                <div  style=\"  padding: 10px 15px;\n" +
                                            "    border-bottom: 1px solid transparent;\n" +
                                            "   border-top-right-radius: 3px;\n" +
                                            "    border-top-left-radius: 3px; background-color: darkgrey; border-color: black;\">\n" +
                                            "                        <h3 style=\"  margin-top: 0;\n" +
                                            "    margin-bottom: 0;\n" +
                                            "    font-size: 16px;\n" +
                                            "    color: inherit;\"><strong>Payment Transaction</strong></h3>\n" +
                                            "                    </div>\n" +
                                            "                <div style=\""+
                                            "                   content: \" \";\n" +
                                            "                   display: table;\n" +
                                            "                   padding: 15px;\n" +
                                            "                   display: table;\n" +
                                            "                   clear: both;\" >\n" +
                                            "                        <div style=\"min-height:.01%;overflow-x:auto; width:100%; \n" +
                                            "                                           overflow-y:hidden;-ms-overflow-style:-ms-autohiding-scrollbar;border:1px solid #ddd;\n" +
                                            "                                            overflow: hidden;\">\n" +
                                            "                            <table  style=\"margin-bottom: 0px; border: thick;width: 100%;  max-width: 100%;\n\n" +
                                            "                                            background-color: transparent;\n\n" +
                                            "                                            border-collapse: collapse;\n\n" +
                                            "                                            border-spacing: 0;\">\n" +
                                            "                                <thead>\n" +
                                            "                                    <tr style=\"display: table-row; \n" +
                                            "                                                vertical-align: inherit; \n" +
                                            "                                               border-color: inherit;border: thick; border-color: black;\">\n" +
                                            "                                        <td style=\" padding: 5px;\n\n" +
                                            "                                            line-height: 1.428571429;\n\n" +
                                            "                                            vertical-align: top;\n\n" +
                                            "                                            border: 1px solid #ddd; display: table-cell; \n\n" +
                                            "                                               text-align: center;\"><strong>#RNo</strong></td>\n" +
                                            "                                        <td style=\"padding: 5px;\n\n" +
                                            "                                                    line-height: 1.428571429;\n\n\n" +
                                            "                                                    vertical-align: top;\n\n\n" +
                                            "                                                    border: 1px solid #ddd; display: table-cell; \n\n" +
                                            "                                                       text-align: center;\"><strong>Date</strong></td>\n" +
                                            "                                        <td style=\"padding: 8px;\n\n \n" +
                                            "                                                    line-height: 1.428571429;\n\n\n" +
                                            "                                                    vertical-align: top;\n\n \n" +
                                            "                                                    border: 1px solid #ddd; display: table-cell; \n \n" +
                                            "                                                       text-align: center;\"><strong>Tax</strong></td>                                    \n" +
                                            "                                        <td style=\"padding: 8px;\n\n\n" +
                                            "                                                    line-height: 1.428571429;\n\n" +
                                            "                                                    vertical-align: top;\n\n" +
                                            "                                                    border: 1px solid #ddd; display: table-cell; \n \n" +
                                            "                                                       text-align: center;\"><strong>Tax Amount</strong></td>\n" +
                                            "                                        <td style=\"padding: 8px;\n\n" +
                                            "                                                    line-height: 1.428571429;\n\n" +
                                            "                                                    vertical-align: top;\n\n" +
                                            "                                                    border: 1px solid #ddd; display: table-cell; \n\n" +
                                            "                                                       text-align: center;\"><strong>Paid Amount</strong></td>\n" +
                                            "                                        <td style=\"padding: 8px;\n \n" +
                                            "                                                    line-height: 1.428571429;\n\n" +
                                            "                                                    vertical-align: top;\n\n" +
                                            "                                                    border: 1px solid #ddd; display: table-cell; \n\n" +
                                            "                                                       text-align: center;\"><strong>Payment Mode</strong></td>\n" +
                                            "                                        <td style=\"padding: 8px;\n \n" +
                                            "                                                    line-height: 1.428571429;\n \n" +
                                            "                                                    vertical-align: top;\n\n \n" +
                                            "                                                    border: 1px solid #ddd; display: table-cell; \n\n" +
                                            "                                                       text-align: center;\"><strong>Payment Details</strong></td>\n" +
                                            "                                        <td style=\"padding: 5px;\n\n" +
                                            "                                                    line-height: 1.428571429;\n\n" +
                                            "                                                    vertical-align: top;\n \n" +
                                            "                                                    border: 1px solid #ddd; display: table-cell; \n\n" +
                                            "                                                       text-align: center;\"><strong>Executive</strong></td>\n" +
                                            "                                    </tr>\n" +
                                            "                                </thead>\n" +
                                            "                                <tbody style=\"display: table-row-group;\n" +
                                            "                                                 vertical-align: middle;\n" +
                                            "                                                     border-color: inherit;\">\n" +
                                            "                                    <!-- foreach ($order->lineItems as $line) or some such thing here -->\n" +
                                            textBody+
                                            "\n" +
                                            "                                </tbody>\n" +
                                            "                            </table>\n" +
                                            "                        </div>\n" +
                                            "                    </div>\n" +
                                            "                     \n" +
                                            "            </div>\n" +
                                            "           </div>\n" +
                                            "        </div>\n" +
                                            "\n" +
                                            "      \n" +
                                            "\n" +
                                            "        <div  style=\" margin-left: -15px;margin-right: -15px;\">\n" +
                                            "            <div style=\"   width: 100%;\n" +
                                            "   min-width: 992px;\n" +
                                            "   position: relative;\n" +
                                            "   min-height: 1px;\n" +
                                            "   padding-left: 15px;\n" +
                                            "   padding-right: 15px;\">\n" +
                                            "                                 <div  style=\"  padding: 10px 15px;\n" +
                                            "   border-bottom: 1px solid transparent;\n" +
                                            "   border-top-right-radius: 3px;\n" +
                                            "   border-top-left-radius: 3px; background-color: darkgray; border-color: black;\">\n" +
                                            "                        <h3 style=\"  margin-top: 0;\n" +
                                            "   margin-bottom: 0;\n" +
                                            "   font-size: 16px;\n" +
                                            "   color: inherit;\"><strong>Terms And Conditions</strong></h3>\n" +
                                            "                    </div>\n" +
                                            "                  \n" +
                                            "                           "+TermsAndConditions+"\n" +
                                            "                     \n" +
                                            "    \n" +
                                            "                \n" +
                                            "            </div>\n" +
                                            "        </div>\n" +
                                            "\n" +
                                            "    </div>\n" +
                                            "        </form>\n" +
                                            "</body>\n" +
                                            "</html>";
                                    final String subject=Company_Name+" Receipt";

                                    BalanceReceiptActivity.this.runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            //second async stared within a asynctask but on the main thread
                                            (new AsyncTask<String, String, Void>() {
                                                ServerClass ruc = new ServerClass();

                                                @Override
                                                protected Void doInBackground(String... params) {
                                                    Mail m = new Mail("tulsababar.ndsoft@gmail.com", "Tulsa@2019");

                                                    String[] toArr = { Email, "tulsababar01@gmail.com"};
                                                    Log.v(TAG, String.format(" Email array to send = %s", toArr));
                                                    m.setTo(toArr);
                                                    m.setFrom("tulsababar.ndsoft@gmail.com");
                                                    m.setSubject(subject);
                                                    m.setBody(message);

                                                    try {
                                                        if(m.send()) {
                                                            runOnUiThread(new Runnable() {
                                                                public void run() {

                                                                    Toast.makeText(BalanceReceiptActivity.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();

                                                                }
                                                            });
                                                        } else {
                                                            Toast.makeText(BalanceReceiptActivity.this, "Email was not sent.", Toast.LENGTH_LONG).show();
                                                        }
                                                    } catch(Exception e) {
                                                        Log.e("MailApp", "Could not send email", e);
                                                    }
                                                    return null;
                                                }

                                                @Override
                                                protected void onPostExecute(Void aVoid) {
                                                    super.onPostExecute(aVoid);


                                                }
                                            }).execute();

                                        }
                                    });

                                }
                            }
                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    //nodata.setVisibility(View.VISIBLE);
                    //.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    // sending email in android
    protected void sendEmail() {
        Log.i("Send email", "");
        String[] TO = {Email};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(BalanceReceiptActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
    // ******************* send sms for add enquiry **************
    public void  SendEnquirySmsClass() {
        BalanceReceiptActivity.SendEnquirySmsTrackClass ru = new BalanceReceiptActivity.SendEnquirySmsTrackClass();
        ru.execute("5");
    }

    class SendEnquirySmsTrackClass extends AsyncTask<String, Void, String> {

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
            //dismissProgressDialog();
            viewDialog.hideDialog();
            SendEnquirySmsDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> EnquiryForDetails = new HashMap<String, String>();

            EnquiryForDetails.put("type","balancepaid" );
            EnquiryForDetails.put("comp_id",SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptActivity.this) );
            EnquiryForDetails.put("action", "sms_for_add_enquiry");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(BalanceReceiptActivity.this);
            //EnquiryForloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(EnquiryForloyee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, EnquiryForDetails);
            Log.v(TAG, String.format("doInBackground :: sms_for_add_enquiry= %s", loginResult));
            return loginResult;
        }
    }
    private void SendEnquirySmsDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject object = null;
        try {
            object = new JSONObject(jsonResponse);
            String success = object.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.zero))) {

                // showCustomDialog();

                //inputEmail, inputPhone,inputAdd,inputReq,inputFollowupdate;
            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.two)))
            {
                if (object != null) {
                    JSONArray jsonArrayResult = object.getJSONArray("result");


                    if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                        for (int i = 0; i < jsonArrayResult.length(); i++) {

                            Log.v(TAG, "JsonResponseOpeartion ::");
                            JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                            if (jsonObj != null) {

                                afterEnquirySms = jsonObj.getString("balancepaid");
                                afterEnquirySms = afterEnquirySms.replace(".", "");
                                afterEnquirySms = afterEnquirySms.replace("#REC#", inputPaid.getText().toString());
                                afterEnquirySms = afterEnquirySms.replace("#RemBal#", inputBalance.getText().toString());
                                afterEnquirySms = afterEnquirySms.replace("#NextBalanceDate#", inputNextFollDate.getText().toString());

                                       if(!afterEnquirySms.equals("")) {
                                           BalanceReceiptActivity.this.runOnUiThread(new Runnable() {

                                               @Override
                                               public void run() {
                                                   //second async stared within a asynctask but on the main thread
                                                   (new AsyncTask<String, String, String>() {
                                                       ServerClass ruc = new ServerClass();

                                                       @Override
                                                       protected String doInBackground(String... params) {
                                                           String loginResult2 = ruc.SendSMS(inputContact.getText().toString(), afterEnquirySms,
                                                                   SharedPrefereneceUtil.getSmsUsername(BalanceReceiptActivity.this),
                                                                   SharedPrefereneceUtil.getSmsPassword(BalanceReceiptActivity.this),
                                                                   SharedPrefereneceUtil.getSmsRoute(BalanceReceiptActivity.this),
                                                                   SharedPrefereneceUtil.getSmsSenderid(BalanceReceiptActivity.this));
                                                           Log.v(TAG, String.format("doInBackground :: Send Sms after enquiry= %s", loginResult2));
                                                           return loginResult2;
                                                       }

                                                       @Override
                                                       protected void onPostExecute(String response) {
                                                           super.onPostExecute(response);
                                                           Log.v(TAG, String.format("onPostExecute :: response = %s", response));
                                                           Intent intent = new Intent(BalanceReceiptActivity.this, BalanceReceiptDetailsActivity.class);
                                                           startActivity(intent);

                                                       }
                                                   }).execute();

                                               }
                                           });
                                       }else{
                                           Intent intent = new Intent(BalanceReceiptActivity.this, BalanceReceiptDetailsActivity.class);
                                           startActivity(intent);
                                       }
                            }
                        }
                    }
                }
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

