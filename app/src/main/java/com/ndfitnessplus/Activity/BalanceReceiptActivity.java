package com.ndfitnessplus.Activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.ndfitnessplus.Adapter.AddEnquirySpinnerAdapter;
import com.ndfitnessplus.Adapter.SearchContactAdapter;
import com.ndfitnessplus.Adapter.SearchNameAdapter;
import com.ndfitnessplus.MailUtility.Mail;
import com.ndfitnessplus.Model.FollowupList;
import com.ndfitnessplus.Model.Search_list;
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class BalanceReceiptActivity extends AppCompatActivity {
    public EditText inputTtlCourseFees,inputPrePaid,inputRemBal,
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
    private BaseFont bfBold,bfnormal;
    private File pdfFile;
    private String filename = "Sample.pdf";
    private String filepath = "MyInvoices";
    String FilePath;
    String fname ="";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String LOG_TAG = "GeneratePDF";
    String Email_ID,Password,Header,Footer;
    //Autocomplete suggestion of name
    Search_list searchModel;
    ArrayList<Search_list> searchArrayList = new ArrayList<Search_list>();
    public SearchNameAdapter searchnameadapter;
    SearchContactAdapter searchcontactadapter;
    AutoCompleteTextView inputContact ,inputName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_balance_receipt);
        initToolbar();
        requestPermission();
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.v(LOG_TAG, "External Storage not available or you don't have permission to write");
        }
        else {
            String root = Environment.getExternalStorageDirectory().getPath();
            File myDir = new File(root + "/MyInvoices");
            myDir.mkdirs();
            long n = System.currentTimeMillis() / 1000L;
            fname = "Invoice" + n + ".pdf";
            FilePath = root + "/MyInvoices/" + fname;
            pdfFile = new File(myDir, fname);
            if (pdfFile.exists())
                pdfFile.delete();

            try {
                pdfFile.createNewFile();
                FileOutputStream out = new FileOutputStream(pdfFile);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            MediaScannerConnection.scanFile(this, new String[]{pdfFile.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
        }
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

        inputContact = (AutoCompleteTextView) findViewById(R.id.input_cont);
        inputName = (AutoCompleteTextView) findViewById(R.id.input_name);
        inputTtlCourseFees = (EditText) findViewById(R.id.input_ttl_coursefee);
        inputPrePaid = (EditText) findViewById(R.id.input_prepaid);
        inputRemBal = (EditText) findViewById(R.id.input_rembalance);
        inputPaymentDtl = (EditText) findViewById(R.id.input_payment_details);
        inputPaid = (EditText) findViewById(R.id.input_paid);
        inputNextFollDate = (EditText) findViewById(R.id.input_nextfollDate);
        inputComment = (EditText) findViewById(R.id.input_comment);
        inputBalance = (EditText) findViewById(R.id.input_balance);



        Intent intent = getIntent();
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
        inputContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String countryName = searchcontactadapter.getItem(position).getCustName();
                String contact = searchcontactadapter.getItem(position).getCustContact();
                MemberID = searchcontactadapter.getItem(position).getMemberId();
                Email=searchcontactadapter.getItem(position).getEmail();

                inputName.setText(countryName);
                inputContact.setText(contact);
                invoiceRefIdClass();

            }
        });
        inputContact.addTextChangedListener(new TextWatcher() {
            //
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }



            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {
                if(inputContact.getText().length()==0){
                    inputName.getText().clear();
                }
            }
        });
        showSearchListClass();
        inputName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String countryName = searchnameadapter.getItem(position).getCustName();
                String contact = searchnameadapter.getItem(position).getCustContact();
                MemberID = searchnameadapter.getItem(position).getMemberId();

                Email=searchcontactadapter.getItem(position).getEmail();

                inputName.setText(countryName);
                inputContact.setText(contact);

            }
        });
        inputName.addTextChangedListener(new TextWatcher() {
            //
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }



            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {
                if(inputName.getText().length()==0){
                    inputContact.getText().clear();
                }
            }
        });

        String curr_date = Utility.getCurrentDate();
        inputNextFollDate.setText(curr_date);
        //package spinners
        spinInvoiceRefid = (Spinner) findViewById(R.id.spinner_invoice_ref);
        spinPaymentype = (Spinner) findViewById(R.id.spinner_payment_type);

        txtinvoiceRefId=findViewById(R.id.txt_invoice_ref);
        txtPaymentType=findViewById(R.id.txt_payment_type);

        //api's for spinners

        PaymenttypeClass();
        inputNextFollDate.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }



            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {
                if(inputNextFollDate.getText().length()>0) {
                    //do your work here
                    // Toast.makeText(AddEnquiryActivity.this ,"Text vhanged count  is 10 then: " , Toast.LENGTH_LONG).show();
                    awesomeValidation.clear();
                }
            }
        });


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



                    if(pack_name==null){
                        Log.v(TAG, String.format("Selected  ::Invoice Ref ID= %s", InvoiceRefID));
                    }else{
                        String selectedpack=InvoiceRefID+","+pack_name;
                        txtinvoiceRefId.setVisibility(View.VISIBLE);
                        tv.setText(selectedpack);
                        tv.setTextColor((Color.BLACK));
                    }


                    Log.v(TAG, String.format("Selected  ::Invoice Ref ID= %s", InvoiceRefID));
                    if (index == 0) {
                    } else {
                        tv.setTextColor((Color.BLACK));
                    }
                    invoiceRefId = tv.getText().toString();

                    if (index != 0) {
                        txtinvoiceRefId.setVisibility(View.VISIBLE);
                        InvoiceRefID=model.getId();
                        FinancialYear=model.getFinicialyear();
                        inputPaid.setText("");
                        inputBalance.setText("");
                        if(inputContact.getText().length()==0){
                            Toast.makeText(parent.getContext(), "Please Select Member First ", Toast.LENGTH_LONG).show();

                        }

                    }
                    balanceDetailsclass();
                }
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

                    double finalbal = rate - paid;
                    finalBalance = String.valueOf(finalbal);
                    inputBalance.setText(finalBalance);
                    double i = (paid / ((tax / 100) + 1));
                    double tax_amt = paid - i;
                    TaxAmount = String.valueOf(tax_amt);
                    subtotal = String.valueOf(i);
                    if (rate == paid) {
                        inputNextFollDate.getText().clear();
                        inputNextFollDate.setEnabled(false);
                        inputNextFollDate.setKeyListener(null);
                        awesomeValidation.clear();
                    } else {
                        inputNextFollDate.setEnabled(true);
                    }
                    if (paid > rate) {
                        Toast.makeText(BalanceReceiptActivity.this, "Your paying more than your fees,focus chnga", Toast
                                .LENGTH_SHORT).show();
                        inputBalance.setText(inputRemBal.getText().toString());
                        inputPaid.setText("");
                        inputNextFollDate.getText().clear();
                        inputNextFollDate.setEnabled(false);
                        inputNextFollDate.setKeyListener(null);
                        awesomeValidation.clear();
                    }
                }else{
                    inputBalance.setText(inputRemBal.getText().toString());
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
                    double rate=0;
                    if(inputRemBal.getText().length()>0){
                        rate=Double.parseDouble(inputRemBal.getText().toString());
                    }

                    double paid=Double.parseDouble(inputPaid.getText().toString());
                    if(Tax != null){
                    double tax=Double.parseDouble(Tax);

                    double finalbal=rate-paid;
                    finalBalance=String.valueOf(finalbal);
                    inputBalance.setText(finalBalance);
                    double i=(paid/((tax/100)+1));
                    double tax_amt=paid-i;
                    TaxAmount=String.valueOf(tax_amt);
                    subtotal=String.valueOf(i);
                    }
                    if(rate == paid){
                        inputNextFollDate.getText().clear();
                        inputNextFollDate.setEnabled(false);
                        inputNextFollDate.setKeyListener(null);
                        awesomeValidation.clear();
                        inputNextFollDate.setError(null);
                    } else{
                        inputNextFollDate.setEnabled(true);

                    }
                    if(paid>rate){
                        Toast.makeText(BalanceReceiptActivity.this,"Your paying more than your fees",Toast
                                .LENGTH_SHORT).show();
                        awesomeValidation.clear();
                         Log.v(TAG, String.format("Remianing balance  :: = %s", inputRemBal.getText().toString()));

                        inputBalance.setText(inputRemBal.getText().toString());
                        inputPaid.setText("");
                        inputNextFollDate.getText().clear();
                        inputNextFollDate.setEnabled(false);
                        inputNextFollDate.setKeyListener(null);
                    }
                }else{
                    inputBalance.setText(inputRemBal.getText().toString());
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
                                inputNextFollDate.setError(null);
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
            submitForm();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void submitForm() {

        if(!(inputBalance.getText().toString().equals("0.0"))){
            if (awesomeValidation.validate()) {
                double paid = Double.parseDouble(inputPaid.getText().toString());
                if(invoiceRefId.equals(getResources().getString(R.string.hint_packagetype))
                        || paymentType.equals(getResources().getString(R.string.hint_pyment_mode))){
                    Toast.makeText(this, "Please fill Payment type or Package type", Toast.LENGTH_LONG).show();
                }else{
                    if(paid <=0){
                        inputPaid.getText().clear();
                        inputBalance.getText().clear();
                        inputPaid.requestFocus();
                        Toast.makeText(BalanceReceiptActivity.this, "Please pay some amount", Toast
                                .LENGTH_SHORT).show();
                    }else{
                        if(inputNextFollDate.getText().length()>0) {
                            inputNextFollDate.setError(getResources().getString(R.string.err_msg_next_foll_date));
                            AddBalanceReceiptClass();
                        }else{
                            inputNextFollDate.setError(null);
                            Toast.makeText(this, "Please enter next followup date", Toast.LENGTH_LONG).show();
                        }
                    }

                    Log.v(TAG, String.format("calling function= %s", "Add Balnce receipt"));
                }

            }
        }else{
            awesomeValidation.clear();
            inputNextFollDate.setError(null);
        if (awesomeValidation.validate()) {
            if(invoiceRefId.equals(getResources().getString(R.string.hint_packagetype))
                    || paymentType.equals(getResources().getString(R.string.hint_pyment_mode)) ){
                Toast.makeText(this, "Please fill Payment type or Package type", Toast.LENGTH_LONG).show();
            }else{

                    AddBalanceReceiptClass();

                Log.v(TAG, String.format("calling function= %s", "Add Balnce receipt"));
            }

          }else{
            Toast.makeText(this, "Please verify validation", Toast.LENGTH_LONG).show();
          }
        }

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
            viewDialog.showDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            viewDialog.hideDialog();
            CheckContactDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> EnquiryForDetails = new HashMap<String, String>();

            EnquiryForDetails.put("mobileno",inputContact.getText().toString() );
            EnquiryForDetails.put("user","Member" );
            EnquiryForDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptActivity.this) );
            EnquiryForDetails.put("action", "check_mobile_already_exist_or_not");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(BalanceReceiptActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, EnquiryForDetails);
            Log.v(TAG, String.format("doInBackground :: check_mobile_already_exist_or_not= %s", loginResult));
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

                Intent intent=new Intent(BalanceReceiptActivity.this, AddMemberActivity.class);
                intent.putExtra("contact",inputContact.getText().toString());
                startActivity(intent);
            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.two)))
            {
                JSONArray jsonArrayResult = object.getJSONArray("Data");

                if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                    for (int i = 0; i < jsonArrayResult.length(); i++) {

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
            }else if(success.equalsIgnoreCase(getResources().getString(R.string.one))){
                Toast.makeText(BalanceReceiptActivity.this,"member has no active course.Please add course first",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(BalanceReceiptActivity.this, RenewActivity.class);
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
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            InvoiceRefDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> InvoiceRefDetails = new HashMap<String, String>();
            InvoiceRefDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptActivity.this));
            InvoiceRefDetails.put("member_id", MemberID);
            InvoiceRefDetails.put("action", "show_balance_package_name_list");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(BalanceReceiptActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, InvoiceRefDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void InvoiceRefDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
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

                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Package_Name     = jsonObj.getString("Package_Name");
                                    String Invoice_ID     = jsonObj.getString("Invoice_ID");
                                    String Financial_Year     = jsonObj.getString("Financial_Year");

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
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinInvoiceRefid.setAdapter(invoiceRefidadapter);
                                    spinInvoiceRefid.setSelection(1);

                                }
                            }
                        }else if(jsonArrayCountry.length()==0){
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){

                    Toast.makeText(BalanceReceiptActivity.this, "No Outstanding Remaining", Toast.LENGTH_LONG).show();
                   Intent intent=new Intent(BalanceReceiptActivity.this, BalanceReceiptDetailsActivity.class);
                   startActivity(intent);
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
            viewDialog.showDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            viewDialog.hideDialog();
            BalanceDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> BalanceDetails = new HashMap<String, String>();
            BalanceDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptActivity.this));
            BalanceDetails.put("invoice_id",InvoiceRefID );
            BalanceDetails.put("member_id",MemberID );
            BalanceDetails.put("financial_year",FinancialYear );
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptActivity.this)));
            BalanceDetails.put("action","show_balance_details_by_invoice_id");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(BalanceReceiptActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, BalanceDetails);
            return loginResult;
        }


    }

    private void BalanceDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {

                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
                        ArrayList<FollowupList> item = new ArrayList<FollowupList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {

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

                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
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
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            PaymentTypeDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> PaymentTypeDetails = new HashMap<String, String>();
            PaymentTypeDetails.put("action", "show_payment_type_list");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(BalanceReceiptActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, PaymentTypeDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void PaymentTypeDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
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
                        paymentTypeList.setName(getResources().getString(R.string.hint_pyment_mode));
                        paymentTypeArrayList.add(0,paymentTypeList);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                paymentTypeList = new Spinner_List();
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String PaymentType     = jsonObj.getString("PaymentType");

                                    String id=jsonObj.getString("Auto_Id");
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
            viewDialog.showDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            viewDialog.hideDialog();
            AddBalanceReceiptDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {


            HashMap<String, String> AddBalanceReceiptDetails = new HashMap<String, String>();
            AddBalanceReceiptDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptActivity.this));
            AddBalanceReceiptDetails.put("member_id",MemberID);
            AddBalanceReceiptDetails.put("name",inputName.getText().toString());
            AddBalanceReceiptDetails.put("contact",inputContact.getText().toString());
            AddBalanceReceiptDetails.put("email",Email);
            Log.v(TAG, String.format("doInBackground :: Email= %s", Email));
            AddBalanceReceiptDetails.put("invoice_id",InvoiceRefID);
            AddBalanceReceiptDetails.put("subtotal",subtotal);
            Log.v(TAG, String.format("doInBackground :: subtotal= %s", subtotal));
            AddBalanceReceiptDetails.put("tax",Tax);
            Log.v(TAG, String.format("doInBackground :: Tax= %s", Tax));
            AddBalanceReceiptDetails.put("tax_amount",TaxAmount);
            Log.v(TAG, String.format("doInBackground :: TaxAmount= %s", TaxAmount));
            AddBalanceReceiptDetails.put("payment_type",paymentType);
            Log.v(TAG, String.format("doInBackground :: payment_type= %s", paymentType));
            AddBalanceReceiptDetails.put("payment_details",inputPaymentDtl.getText().toString());
            AddBalanceReceiptDetails.put("paid",inputPaid.getText().toString());
            Log.v(TAG, String.format("doInBackground :: paid= %s", inputPaid.getText().toString()));
            AddBalanceReceiptDetails.put("balance",finalBalance);
            Log.v(TAG, String.format("doInBackground :: finalBalance= %s", finalBalance));
            AddBalanceReceiptDetails.put("comment",inputComment.getText().toString());
            AddBalanceReceiptDetails.put("next_payment_date",inputNextFollDate.getText().toString());
            Log.v(TAG, String.format("doInBackground :: next_payment_date= %s", inputNextFollDate.getText().toString()));
            AddBalanceReceiptDetails.put("mem_own_exe", SharedPrefereneceUtil.getName(BalanceReceiptActivity.this));
            AddBalanceReceiptDetails.put("financial_year",FinancialYear);
            Log.v(TAG, String.format("doInBackground :: financial_year= %s", FinancialYear));
            Log.v(TAG, String.format("doInBackground :: executive name= %s", SharedPrefereneceUtil.getName(BalanceReceiptActivity.this)));
            AddBalanceReceiptDetails.put("mode", "AdminApp");
            AddBalanceReceiptDetails.put("action", "add_balance_receipt");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(BalanceReceiptActivity.this);
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
                EmailLoginClass();
                SendEnquirySmsClass();
            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.zero)))
            {
                Toast.makeText(BalanceReceiptActivity.this,"Your Balance is Already Paid",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(BalanceReceiptActivity.this, BalanceReceiptActivity.class);
                startActivity(intent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void  EmailLoginClass() {
        BalanceReceiptActivity.EmailLoginTrackClass ru = new BalanceReceiptActivity.EmailLoginTrackClass();
        ru.execute("5");
    }

    class EmailLoginTrackClass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            EmailLoginDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> EmailLoginDetails = new HashMap<String, String>();
            EmailLoginDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptActivity.this) );
            EmailLoginDetails.put("action", "show_email_login");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(BalanceReceiptActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, EmailLoginDetails);

            Log.v(TAG, String.format("doInBackground :: show_email_login= %s", loginResult));
            return loginResult;
        }
    }
    private void EmailLoginDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject object = null;
        try {
            object = new JSONObject(jsonResponse);
            String success = object.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.zero))) {
            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.two)))
            {
                if (object != null) {
                    JSONArray jsonArrayCountry = object.getJSONArray("result");

                    if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                        for (int i = 0; i < jsonArrayCountry.length(); i++) {
                            Log.v(TAG, "JsonResponseOpeartion ::");
                            JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                            if (jsonObj != null) {

                                Email_ID     = jsonObj.getString("Email_ID");
                                Password=jsonObj.getString("Password");
                                String Email_Status=jsonObj.getString("Email_Status");
                                Header=jsonObj.getString("Header");
                                Footer=jsonObj.getString("Footer");
                                if(Email_Status.equals("ON")){
                                    if(!Email.equals("")){
                                        receiptdatalass();
                                    }
                                }else {
                                    System.out.println("Email Status Is Off");
                                }
                            }
                        }
                    }else if(jsonArrayCountry.length()==0){
                        System.out.println("No records found");
                    }
                }
            }

        }
        catch (JSONException e) {
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
            viewDialog.showDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: show_receipt_data = %s", response));
            viewDialog.hideDialog();
            ReceiptDataDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> ReceiptDataDetails = new HashMap<String, String>();
            ReceiptDataDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptActivity.this));
            ReceiptDataDetails.put("invoice_id", InvoiceRefID);
            Log.v(TAG, String.format("doInBackground :: receipt data invoice_id= %s", InvoiceRefID));
            ReceiptDataDetails.put("financial_yr", FinancialYear);
            ReceiptDataDetails.put("member_id", MemberID);
            Log.v(TAG, String.format("doInBackground :: receipt data company id = %s", FinancialYear));
            Log.v(TAG, String.format("doInBackground :: receipt data company id = %s", SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptActivity.this)));
            ReceiptDataDetails.put("action","show_receipt_data");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(BalanceReceiptActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, ReceiptDataDetails);
            Log.v(TAG, String.format("doInBackground :: show_receipt_data= %s", loginResult));
            return loginResult;
        }


    }

    private void ReceiptDataDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
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
                                    String Invoice_date = jsonObj.getString("Invoice_date");
                                    String invoice_date= Utility.formatDate(Invoice_date);
                                    String Package_Name = jsonObj.getString("Package_Name");
                                    String Duration_Days =jsonObj.getString("Duration_Days");
                                    String Session = jsonObj.getString("Session");
                                    String Start_Date = jsonObj.getString("Start_Date");
                                    String start_date= Utility.formatDateDB(Start_Date);
                                    String End_Date = jsonObj.getString("End_Date");
                                    String end_date= Utility.formatDateDB(End_Date);
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


                                    String Company_Name = SharedPrefereneceUtil.getCompanyName(BalanceReceiptActivity.this);
                                    String Branch=SharedPrefereneceUtil.getSelectedBranch(BalanceReceiptActivity.this);
                                    String Address = jsonObj.getString("Address");
                                    String Contact = jsonObj.getString("Contact");
                                    String MemberGST_No = jsonObj.getString("MemberGST_No");
                                    String GST_No = jsonObj.getString("GST_No");
                                    String TermsAndConditions = jsonObj.getString("TermsAndConditions");
                                    TermsAndConditions = TermsAndConditions.replace("\r\n", "<br />");
                                    String Logo = jsonObj.getString("Logo");
                                    String l=Logo.replaceAll("\\s+","%20");

                                    String domainurl= SharedPrefereneceUtil.getDomainUrl(BalanceReceiptActivity.this);
                                    final  String imgurl=domainurl+ServiceUrls.IMAGES_URL+l;
                                    Log.d(TAG, "imgurl: " +imgurl);

                                    String textBody = "";
                                    String CGST="";
                                    JSONArray jsonArrayPayTrasa = jsonObj.getJSONArray("payment_transa");
                                    if (jsonArrayPayTrasa != null && jsonArrayPayTrasa.length() > 0) {
                                        for (int loopCount = 0; loopCount < jsonArrayPayTrasa.length(); loopCount++)
                                        {
                                            JSONObject jsonObj1 = jsonArrayPayTrasa.getJSONObject(loopCount);
                                            if (jsonObj1 != null) {
                                                String Receipt_Id = jsonObj1.getString("Receipt_Id");
                                                String ReceiptDate = jsonObj1.getString("ReceiptDate");
                                                String receipt_date= Utility.formatDateDB(ReceiptDate);
                                                String Tax = jsonObj1.getString("Tax");
                                                if(Tax.equals(".00")){
                                                    Tax="0.00";
                                                }
                                                String TaxAmount = jsonObj1.getString("TaxAmount");
                                                if(TaxAmount.equals(".00")){
                                                    TaxAmount="0.00";
                                                }
                                                String Paid =  jsonObj1.getString("Paid");
                                                String PaymentType =  jsonObj1.getString("PaymentType");
                                                String PaymentDetails =  jsonObj1.getString("PaymentDetails");
                                                String ReceiptOwnerExecutive =  jsonObj1.getString("ReceiptOwnerExecutive");

                                                String SubTotal =  jsonObj1.getString("SubTotal");
                                                double cgst =Double.parseDouble(Tax)/2;
                                                CGST=String.valueOf(cgst);
                                                double sgst=Double.parseDouble(TaxAmount)/2;
                                                String SGST=String.valueOf(sgst);
                                                textBody += "  <tr >\n \n" +
                                                        "    <td width='10%'>"+Receipt_Id+"</td>\n \n" +
                                                        "     <td width='15%'>"+receipt_date+"</td>\n\n" +
                                                        "     <td width='12%'>"+SubTotal+"</td>\n\n" +
                                                        "    <td width='8%'>"+Tax+"</td> \n\n" +
                                                        "    <td width='9%'>"+SGST+"</td> \n\n" +
                                                        "    <td width='9%'>"+SGST+"</td> \n\n" +
                                                        "    <td width='12.5%'>"+TaxAmount+"</td>\n\n" +
                                                        "    <td width='12.5%'>"+Paid+"</td>\n\n" +
                                                        "    <td width='12.5%'>"+PaymentType+"</td>\n\n" +
                                                        "    <td width='12.5%'>"+PaymentDetails+"</td>\n\n" +
                                                        "    <td width='13%'>"+ReceiptOwnerExecutive+"</td>\n\n" +
                                                        "    </tr>\n";
//
                                            }
                                        }

                                    }


                                    final Document document = new Document(PageSize.A4);

                                    try {

                                        PdfWriter docWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
                                        document.open();

                                        PdfContentByte cb = docWriter.getDirectContent();
                                        //initialize fonts for text printing
                                        initializeFonts();

                                        //Add Image from some URL
                                        Thread thread = new Thread(new Runnable() {
                                            //
                                            @Override
                                            public void run() {
                                                try  {
                                                    Image image = Image.getInstance(new URL(imgurl));
                                                    image.setAbsolutePosition(50,730);
                                                    image.scalePercent(10);
                                                    image.scaleToFit(100, 70);
                                                    document.add(image);
                                                    //Your code goes here
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                                        thread.start();

                                        String delimiter = " ";
                                        int partitionSize = 6;
                                        String add="";

                                        for (Iterable<String> iterable : Iterables.partition(Splitter.on(delimiter).split(Address), partitionSize)) {
                                            System.out.println(Joiner.on(delimiter).join(iterable));
                                            add+=Joiner.on(delimiter).join(iterable)+"<br/>";
                                        }
                                        String csstext=" .text-right { text-align: right;} .p{border: 1px solid black;font-family: Helvetica,Arial,sans-serif;font-size: 12px;}" +
                                                "        .addr {" +
                                                "            margin-bottom: 10px;" +
                                                "            font-style: normal;" +
                                                "            margin-left: 20px;font-size: 12px;" +
                                                "            line-height: 1.428571429;" +
                                                "        }" +
                                                "        .table > thead > tr > td {" +
                                                "            padding: 1px;" +
                                                "            line-height: 1.428571429;" +
                                                "            vertical-align: top;" +
                                                "            border-top: 1px solid #ddd;" +
                                                "            margin-left: 10px;" +
                                                "font-family: Helvetica,Arial,sans-serif;font-size: 12px;" +
                                                "        }\n" +
                                                "        .column {\n" +
                                                "          float: left;\n" +
                                                "          width: 30.33%;\n" +
                                                "          padding: 10px;" +
                                                "        }\n" +
                                                "        .col-3{\n" +
                                                "            float: left;\n" +
                                                "          width: 20%;\n" +
                                                "          padding: 10px;\n" +
                                                "        }\n" +
                                                "        .col-6{\n" +
                                                "            float: left;\n" +
                                                "          width: 70%;\n" +
                                                "        }\n" +
                                                "         .col-4{\n" +
                                                "            float: left;\n" +
                                                "          width: 10%;\n" +
                                                "text-align: right;" +
                                                "        }\n" +
                                                "        .id{\n" +
                                                "            float: left;\n" +
                                                "          width: 50%;\n" +
                                                "          margin-left: 5px;\n" +
                                                "        }\n" +
                                                "        .email{\n" +
                                                "            float: left;\n" +
                                                "          width: 49%;\n" +
                                                "        }" +
                                                "        .row:after {\n" +
                                                "          content: \"\";\n" +
                                                "          display: table;\n" +
                                                "          clear: both;\n" +
                                                "border-bottom: 1px solid black;margin-top: 13px;" +
                                                "        }\n" +
                                                "        .col-md-3{\n" +
                                                "             float: left;\n" +
                                                "           width: 28%;\n" +
                                                "          height: 100%;\n" +
                                                "    overflow: hidden;\n" +
                                                "        } .col-md-4{" +
                                                "    float: left; width: 20%;height: 100%; overflow: hidden;}\n" +
                                                "        .table > thead > tr > td {\n" +
                                                "            line-height: 1.428571429;\n" +
                                                "            vertical-align: top;\n" +
                                                "            border: 1px solid #ddd;\n" +
                                                "            margin-left: 0px;\n" +
                                                "             table-layout: fixed;\n" +
                                                "           }\n" +
                                                "           .table-bordered {\n" +
                                                "            empty-cells: show;\n" +
                                                "        }\n" +
                                                "        table {\n" +
                                                "        border-spacing: 0;\n" +
                                                "         empty-cells: show;\n" +
                                                "         }\n" +
                                                "        table td[class*=col-], table th[class*=col-] {\n" +
                                                "            position: static;\n" +
                                                "            float: none;\n" +
                                                "            display: table-cell;\n" +
                                                "        }\n" +
                                                "        .container {\n" +
                                                "          display: flex;\n" +
                                                "          flex-wrap: wrap;\n" +
                                                "        }\n" +
                                                "        thead {\n" +
                                                "    display: table-header-group;\n" +
                                                "    vertical-align: middle;\n" +
                                                "    border-color: inherit;\n" +
                                                "}" +
                                                " td{font-family: Helvetica,Arial,sans-serif;font-size: 12px; }";

                                        String HTML = "<html><body style=\"font-family: Helvetica,Arial,sans-serif;color: #333\">\n" +
                                                "        <div class=\"container\" >\n" +
                                                "            <div class=\"row\">\n" +
                                                "                    <div class=\"row\" style=\"border-bottom: 1px solid black;margin-top: 13px;\">\n" +
                                                "                        <div class=\"col-3\"> \n" +
                                                "                                <img src=\"http://demo.gymtime.in/CompanyLogo/NavkarDemo_2020125141355.png\" style=\"width: 120px; height: 100px;\" />                                                                                   \n" +
                                                "                        </div>\n" +
                                                "                        <div class=\"col-6\" style=\"margin-left: 0px; padding :5px;\">\n" +
                                                "                            <div class=\"addr\">\n" +
                                                "                                 <table cellpadding=\"4\">\n" +
                                                "                                <thead>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td ><strong>"+Company_Name+"</strong></td>\n" +
                                                "                                    </tr>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td  >"+add+"</td>\n" +
                                                "                                    </tr>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td  >"+Branch+"</td>\n" +
                                                "                                    </tr>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td  >"+Contact+"</td>\n" +
                                                "                                    </tr>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td  >"+GST_No+"</td>\n" +
                                                "                                    </tr>\n" +
                                                "                                </thead>\n" +
                                                "                            </table>\n" +
                                                "                               \n" +
                                                "                            </div>\n" +
                                                "                        </div>\n" +
                                                "                        <div class=\"col-4\" style=\"text-align: right;\">\n" +
                                                "                               <table cellpadding=\"4\">\n" +
                                                "                                <thead>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td  ><strong>Invoice No : </strong>"+Invoice_ID+"</td>\n" +
                                                "                                    </tr>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td  ><strong>Invoice Date :</strong>"+invoice_date+"</td>\n" +
                                                "                                    </tr>\n" +
                                                "                                   \n" +
                                                "                                </thead>\n" +
                                                "                            </table>\n" +
                                                "                        </div>\n" +
                                                "                    </div> " +
                                                "  <hr/>  " +
                                                "<div class=\"row\" style=\"margin-top: 9px; border-bottom: 1px solid black\">\n" +
                                                "                        <div class=\"id\" style=\"text-align: inherit\">\n" +
                                                "                            <table cellpadding=\"4\">\n" +
                                                "                                <thead>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td  ><strong>ID:</strong></td>\n" +
                                                "                                        <td > "+MemberID+"</td>\n" +
                                                "                                       \n" +
                                                "                                    </tr>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td  ><strong>Name:</strong></td>\n" +
                                                "                                        <td  >"+Name+"</td>\n" +
                                                "                                        \n" +
                                                "                                    </tr>\n" +
                                                "                                    <tr>\n" +
                                                "                                        <td  ><strong>Contact:</strong></td>\n" +
                                                "                                        <td  >"+Member_Contact+"</td>\n" +
                                                "                                    </tr>\n" +
                                                "                                </thead>\n" +
                                                "                            </table>\n" +
                                                "                        </div>\n" +
                                                "                         <div class=\"email\" style=\"margin-left: 0px;\">\n" +
                                                "                              <table cellpadding=\"4\">\n" +
                                                "                                <thead>\n" +
                                                "                                    <tr>\n" +
                                                "                                         <td ><strong>Email:</strong></td>\n" +
                                                "                                        <td >"+Email+"</td>\n" +
                                                "                                     </tr>\n" +
                                                "                                     <tr>\n" +
                                                "                                          <td class=\"text-right\" ><strong>GST No:</strong></td>\n" +
                                                "                                        <td >"+MemberGST_No+"</td>\n" +
                                                "                                     </tr>\n" +
                                                "                                </thead>\n" +
                                                "                            </table>\n" +
                                                "                         </div>\n" +
                                                "                    </div>" +
                                                " <hr/>" +
                                                "<div class=\"row\">                                                                                       \n" +
                                                "                        <div  style=\"text-align: inherit;\">\n" +
                                                "                            <div class=\"container\" style=\"width:100%; overflow: hidden;padding: 0px 5px 5px 5px;\">\n" +
                                                "                                <table cellpadding=\"1\" class=\"table table-bordered\" style=\"margin-bottom: 0px;width:100%;padding: 5px;\">\n" +
                                                "                                    <thead >\n" +
                                                "                                        <tr  style=\"width:100%;height:10px;border: thick; border-color: black;\">\n" +
                                                "                                            <td class=\"col-md-3\"><strong>Package :</strong></td>\n" +
                                                "                                            <td  class=\"col-md-3\">"+Package_Name+"</td>\n" +
                                                "                                            <td  class=\"col-md-4\"><strong>Amount</strong></td>\n" +
                                                "                                            <td  class=\"col-md-4\" style=\"text-align: right; \">"+Package_Fees+"</td>\n" +
                                                "                                        </tr>\n" +
                                                "                                        <tr style=\"width:100%;height:10px;border: thick; border-color: black;\">\n" +
                                                "                                            <td class=\"col-md-3\"><strong>Duration/Sessions :</strong></td>\n" +
                                                "                                             <td class=\"col-md-3\">"+Duration_Days+"/"+Session+"</td>\n" +
                                                "                                            <td class=\"col-md-4\"><strong>Discount</strong></td>\n" +
                                                "                                            <td class=\"col-md-4\" style=\"text-align: right; \">"+Discount+"</td>\n" +
                                                "                                        </tr>\n" +
                                                "                                        <tr style=\"width:100%;height:10px;border: thick; border-color: black;\">\n" +
                                                "\n" +
                                                "                                            <td class=\"col-md-3\"><strong>Date :</strong></td>\n" +
                                                "                                            <td class=\"col-md-3\">"+start_date+" to "+end_date+"</td>\n" +
                                                "                                            <td class=\"col-md-4\"><strong>Reg Fees</strong></td>\n" +
                                                "                                            <td class=\"col-md-4\" style=\"text-align: right; \">"+Registration_Fees+"</td>\n" +
                                                "                                        </tr>\n" +
                                                "                                        <tr style=\"width:100%;height:10px;border: thick; border-color: black;\">\n" +
                                                "\n" +
                                                "                                            <td class=\"col-md-3\"><strong>Time  :</strong></td>\n" +
                                                "                                            <td class=\"col-md-3\">"+Time+"</td>\n" +
                                                "                                            <td class=\"col-md-4\"><strong>Total Amount</strong></td>\n" +
                                                "                                            <td class=\"col-md-4\" style=\"text-align: right; \">"+Rate+"</td>\n" +
                                                "                                        </tr>\n" +
                                                "                                        <tr style=\"width:100%;height:10px;border: thick; border-color: black;\">\n" +
                                                "\n" +
                                                "                                            <td class=\"col-md-3\"><strong>Instructor:</strong></td>\n" +
                                                "                                             <td class=\"col-md-3\">"+Instructor_Name+"</td>\n" +
                                                "                                            <td class=\"col-md-4\"><strong>Paid Amount</strong></td>\n" +
                                                "                                            <td class=\"col-md-4\" style=\"text-align: right; \">"+Final_paid+"</td>\n" +
                                                "                                        </tr>\n" +
                                                "                                        <tr style=\"width:100%;height:10px;border: thick; border-color: black;\">\n" +
                                                "                                            <td class=\"col-md-3\"><strong>&nbsp;  </strong></td>                          \n" +
                                                "                                            <td class=\"col-md-3\"><strong>&nbsp; </strong></td>\n" +
                                                "                                            <td class=\"col-md-4\"><strong>Balance</strong></td>\n" +
                                                "                                            <td class=\"col-md-4\" style=\"text-align: right; \">"+Final_Balance+"</td>\n" +
                                                "                                        </tr>                                                                             \n" +
                                                "                                    </thead>\n" +
                                                "                                </table>\n" +
                                                "                            </div>\n" +
                                                "                        </div>\n" +
                                                "                    </div>" +
                                                " <div class=\"row\">\n" +
                                                "                                <div style=\"padding-left: 5px;\">\n" +
                                                "                                    <h3 ><strong>Payment Transaction:</strong></h3>\n" +
                                                "                                </div><br/>\n" +
                                                "                                <div  style=\"width:99%; overflow: hidden;padding: 0px 5px 5px 5px;\">\n" +
                                                "                                      <font size=\"2\" face=\"Courier New\" >  <table cellpadding=\"1\" class=\"table table-bordered\" style=\"margin-bottom: 0px;width:100%;\" >\n" +
                                                "                                            <thead>\n" +
                                                "                                                <tr style=\"width:100%;height:10px;border: thick; border-color: black;\">\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>#RNo</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>Pay Date</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>Subtotal</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>Tax</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>CGST ("+CGST+"%)</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>SGST ("+CGST+"%)</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>Tax Amount</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>Paid Amount</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>Payment Mode</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>Payment Details</strong></td>\n" +
                                                "                                                    <td style=\"padding: 1px;\"><strong>Executive</strong></td>\n" +
                                                "                                                </tr>\n" +
                                                textBody+

                                                "                                            </thead>\n" +
                                                "                                        </table></font>\n" +
                                                "                                    </div>\n" +
                                                "                        </div>           \n" +
                                                "                    <div class=\"row\">\n" +
                                                "                                <div style=\"margin-top: 4px; margin-bottom: 4px;padding: 5px;\">\n" +
                                                "                                    <h3  style=\"margin-bottom: 6px\"><strong>Terms And Conditions : </strong></h3>\n" +
                                                "                                    <div>                                          \n" +
                                                "                                        <div  ><table cellpadding=\"4\" style=\"width:100%\">\n" +
                                                "  <tr><td class=\"p\">"+TermsAndConditions+"</td> </tr>\n" +
                                                "</table>\n" +
                                                "                                        </div>\n" +
                                                "                                    </div>\n" +
                                                "                                </div>\n" +
                                                "                    </div>" +
                                                "                </div>\n" +
                                                "            </div>\n" +
                                                "</body></html>";
                                        InputStream is = new ByteArrayInputStream(HTML.getBytes());
                                        InputStream cssggh = new ByteArrayInputStream(csstext.getBytes());
                                        XMLWorkerHelper.getInstance().parseXHtml(docWriter, document, is, cssggh, Charset.forName("UTF-8"));
                                        document.close();

                                    }
                                    catch(Exception e){
                                        e.printStackTrace();
                                    }
                                    final String subject=Company_Name+" Receipt";
                                    final String message=Header+"\nPlease find the attachment of Your Package Details\n\n"+Footer;
                                    BalanceReceiptActivity.this.runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            //second async stared within a asynctask but on the main thread
                                            (new AsyncTask<String, String, Void>() {
                                                ServerClass ruc = new ServerClass();

                                                @Override
                                                protected Void doInBackground(String... params) {
                                                    Mail m = new Mail(Email_ID, Password);

                                                    String[] toArr = { Email, "tulsababar01@gmail.com"};
                                                   // Log.v(TAG, String.format(" Email array to send = %s", toArr));
                                                    m.setTo(toArr);
                                                    m.setFrom("tulsababar.ndsoft@gmail.com");
                                                    m.setSubject(subject);
                                                    m.setBody(message);
                                                    if (Build.VERSION.SDK_INT >= 23)
                                                    {
                                                        if (checkPermission())
                                                        {
                                                            // final File  file = BitmapSaver.saveImageToExternalStorage(RenewActivity.this, bmpqr);
                                                            m.setAttachment(pdfFile);
                                                            m.setAttachmentName(FilePath);
                                                            m.setAttachmentNamePath("BalanceDetails.pdf");
                                                            // Code for above or equal 23 API Oriented Device
                                                            // Your Permission granted already .Do next code
                                                        } else {
                                                            requestPermission(); // Code for permission
                                                        }
                                                    }else{
                                                        // final File  file = BitmapSaver.saveImageToExternalStorage(RenewActivity.this, bmpqr);
                                                        m.setAttachment(pdfFile);
                                                        m.setAttachmentName(FilePath);
                                                        m.setAttachmentNamePath("BalanceDetails.pdf");
                                                    }
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
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    private void initializeFonts(){


        try {
            bfBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            bfnormal = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
            viewDialog.showDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            viewDialog.hideDialog();
            SendEnquirySmsDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> EnquiryForDetails = new HashMap<String, String>();

            EnquiryForDetails.put("type","balancepaid" );
            EnquiryForDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptActivity.this) );
            EnquiryForDetails.put("action", "sms_for_add_enquiry");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(BalanceReceiptActivity.this);
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
                Intent intent = new Intent(BalanceReceiptActivity.this, BalanceReceiptDetailsActivity.class);
                startActivity(intent);
            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.two)))
            {
                if (object != null) {
                    JSONArray jsonArrayResult = object.getJSONArray("result");


                    if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                        for (int i = 0; i < jsonArrayResult.length(); i++) {

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
    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(BalanceReceiptActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(BalanceReceiptActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(BalanceReceiptActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(BalanceReceiptActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
    public void  showSearchListClass() {
        BalanceReceiptActivity.SearchTrackClass ru = new BalanceReceiptActivity.SearchTrackClass();
        ru.execute("5");
    }
    private   class SearchTrackClass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            SearchDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> SearchDetails = new HashMap<String, String>();

            SearchDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptActivity.this) );
            SearchDetails.put("action", "show_all_member_list");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(BalanceReceiptActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, SearchDetails);
            Log.v(TAG, String.format("doInBackground :: show_all_member_list= %s", loginResult));
            return loginResult;
        }


    }


    private void SearchDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {
            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                if (object != null) {
                    JSONArray jsonArrayResult = object.getJSONArray("result");

                    if (jsonArrayResult != null && jsonArrayResult.length() > 0){
                        for (int i = 0; i < jsonArrayResult.length(); i++) {
                            searchModel = new Search_list();
                            JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                            if (jsonObj != null) {

                                String Name     = jsonObj.getString("Name");
                                String Contact     = jsonObj.getString("Contact");
                                String MemberID     = jsonObj.getString("MemberID");
                                String Email = jsonObj.getString("Email");
                                String Gender = jsonObj.getString("Gender");

                                String namec=Name+"-"+Contact;
                                searchModel.setCustName(Name);
                                searchModel.setCustContact(Contact);
                                searchModel.setMemberId(MemberID);
                                searchModel.setEmail(Email);
                                searchModel.setGender(Gender);
                                searchModel.setNameContact(namec);

                                searchArrayList.add(searchModel);
                                searchnameadapter = new SearchNameAdapter(BalanceReceiptActivity.this, searchArrayList);

                                inputName.setAdapter(searchnameadapter);
                                inputName.setThreshold(1);

                                searchcontactadapter = new SearchContactAdapter(BalanceReceiptActivity.this, searchArrayList);

                                inputContact.setAdapter(searchcontactadapter);
                                inputContact.setThreshold(1);


                            }
                        }}else if(jsonArrayResult.length()==0){
                        System.out.println("No records found");
                    }
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
}

