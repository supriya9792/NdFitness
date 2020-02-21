package com.ndfitnessplus.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
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

public class RenewActivity extends AppCompatActivity {
    public EditText inputDuration,inputStartDate,inputPackageFees,inputDiscount,inputRegiFees,inputRate,inputDiscReason,
            inputPaymentDtl,inputPaid,inputNextFollDate,inputComment,inputSession,inputEndDate,inputBalance;
    public TextInputLayout inputLayoutContact,inputLayoutName,inputLayoutDuration,inputLayoutStartDate,inputLayoutPackageFees,inputLayoutDiscount,
            inputLayoutRegiFees,inputLayoutRate,inputLayoutDiscReason,inputLayoutPaymentDtl,inputLayoutPaid,inputLayoutNextFollDate,
            inputLayoutComment,inputLayoutSession,inputLayoutEnddate,inputLayoutBalance;

    public final String TAG = RenewActivity.class.getName();
    private ProgressDialog pd;
    private AwesomeValidation awesomeValidation;
    private int mYear, mMonth, mDay;
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    AutoCompleteTextView inputContact ,inputName;
    private static final int PERMISSION_REQUEST_CODE = 1;
    //Spinner Adapter
    public Spinner spinPackageType,spinPackageName,spinInstructor,spinTime,spinPaymentype;
    Spinner_List packagetypelist,packageNamelist,instructorList,timelist,paymentTypeList;
    ArrayList<Spinner_List> packageTypeArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> packagenameArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> instructorArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> timeArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> paymentTypeArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> occupationArraylist = new ArrayList<Spinner_List>();
    public AddEnquirySpinnerAdapter packagetypeadapter,packagenameadapter,instructoradapter,timeadapter,paymentTypeadapter;
    String packageType,packagename,paymentType;
    String instructorname="NA";
    String time="NA";
    TextView txtPackageType,txtPackageName,txtInstructorName,txtTime,txtPaymentType;
    View LinearLytPackageName;
    String Session,invoice_id,financial_yr,receipt_id;
    String Email="";
    String MaxDiscount="";
    double maxdisc;
    String EndDate;
    String MemberID;
    String Days;
    String Tax="0.0";
    String TaxAmount;
    String finalBalance;
    String subtotal;
    String afterEnquirySms,MemberName;
    //Loading gif
    ViewDialog viewDialog;
    private static final String LOG_TAG = "GeneratePDF";

    private EditText preparedBy;
    private File pdfFile;
    private String filename = "Sample.pdf";
    private String filepath = "MyInvoices";
    String FilePath;
    String fname ="";
    private BaseFont bfBold,bfnormal;
    String Email_ID,Password,Header,Footer;
    //Autocomplete suggestion of name
    Search_list searchModel;
    ArrayList<Search_list> searchArrayList = new ArrayList<Search_list>();
    public SearchNameAdapter searchnameadapter;
    SearchContactAdapter searchcontactadapter;


    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.WRITE_CONTACTS,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_renew);
        initToolbar();
//        InputStream license = this.getResources().openRawResource(R.raw.itextkey);
//        LicenseKey.loadLicenseFile(license);

        //check if external storage is available so that we can dump our PDF file there
        requestContactPermission();
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.v(LOG_TAG, "External Storage not available or you don't have permission to write");
        }
        else {
            //path for the PDF file in the external storage
           // pdfFile = new File(getExternalFilesDir(filepath), filename);
//            String root = Environment.getExternalStoragePublicDirectory().getAbsolutePath();
            String root = Environment.getExternalStorageDirectory().getPath();
            File myDir = new File(root + "/MyInvoices");
            myDir.mkdirs();
            long n  = System.currentTimeMillis() / 1000L;
             fname = "Invoice" + n + ".pdf";
            FilePath=root+"/MyInvoices/"+fname;
             pdfFile = new File(myDir, fname);
            if (pdfFile.exists())
                pdfFile.delete();

            try {
                pdfFile.createNewFile();
                FileOutputStream out = new FileOutputStream(pdfFile);
                out.flush();
                out.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            MediaScannerConnection.scanFile(this, new String[]{pdfFile.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
            //generatePDF("Tulsa");
        }
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.course_reg));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){
        //Contact number
        inputLayoutContact = (TextInputLayout) findViewById(R.id.input_layout_cont);
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutDuration = (TextInputLayout) findViewById(R.id.input_layout_duration);
        inputLayoutStartDate = (TextInputLayout) findViewById(R.id.input_layout_startdate);
        inputLayoutPackageFees = (TextInputLayout) findViewById(R.id.input_layout_pack_fees);
        inputLayoutDiscount = (TextInputLayout) findViewById(R.id.input_layout_discount);
        inputLayoutRegiFees = (TextInputLayout) findViewById(R.id.input_layout_reg_fees);
        inputLayoutRate = (TextInputLayout) findViewById(R.id.input_layout_rate);
        inputLayoutDiscReason = (TextInputLayout) findViewById(R.id.input_layout_disc_reason);
        inputLayoutPaymentDtl = (TextInputLayout) findViewById(R.id.input_layout_payment_details);
        inputLayoutPaid = (TextInputLayout) findViewById(R.id.input_layout_paid);
        inputLayoutNextFollDate = (TextInputLayout) findViewById(R.id.input_layout_next_foll_date);
        inputLayoutComment= (TextInputLayout) findViewById(R.id.input_layout_comment);
        inputLayoutSession= (TextInputLayout) findViewById(R.id.input_layout_session);
        inputLayoutEnddate= (TextInputLayout) findViewById(R.id.input_layout_end_date);
        inputLayoutBalance= (TextInputLayout) findViewById(R.id.input_layout_balance);

        inputContact = (AutoCompleteTextView) findViewById(R.id.input_cont);
        inputName = (AutoCompleteTextView) findViewById(R.id.input_name);
        inputDuration = (EditText) findViewById(R.id.input_duration);
        inputStartDate = (EditText) findViewById(R.id.input_startdate);
        inputPackageFees = (EditText) findViewById(R.id.input_pack_fees);
        inputDiscount = (EditText) findViewById(R.id.input_discount);
        inputRegiFees = (EditText) findViewById(R.id.input_reg_fees);
        inputRate = (EditText) findViewById(R.id.input_rate);
        inputDiscReason = (EditText) findViewById(R.id.input_disc_reason);
        inputPaymentDtl = (EditText) findViewById(R.id.input_payment_details);
        inputPaid = (EditText) findViewById(R.id.input_paid);
        inputNextFollDate = (EditText) findViewById(R.id.input_nextfollDate);
        inputComment = (EditText) findViewById(R.id.input_comment);
        inputSession = (EditText) findViewById(R.id.input_session);
        inputEndDate = (EditText) findViewById(R.id.input_end_date);
        inputBalance = (EditText) findViewById(R.id.input_balance);
        viewDialog = new ViewDialog(this);


        Intent intent = getIntent();
        // Bundle args = intent.getBundleExtra("BUNDLE");
        if (intent != null) {
            MemberID=intent.getStringExtra("member_id");
          String  name=intent.getStringExtra("name");
          String  Contact=intent.getStringExtra("contact");
          Email=intent.getStringExtra("email");

            inputContact.setText(Contact);
            inputName.setText(name);

        }

        // *********** validation *************
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);


        awesomeValidation.addValidation(this, R.id.input_cont, RegexTemplate.NOT_EMPTY, R.string.err_msg_cont);
        awesomeValidation.addValidation(this, R.id.input_startdate, RegexTemplate.NOT_EMPTY, R.string.err_msg_startdate);
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
        spinPackageType = (Spinner) findViewById(R.id.spinner_package_type);
        spinPackageName = (Spinner) findViewById(R.id.spinner_package_name);
        spinInstructor=(Spinner)findViewById(R.id.spinner_instructor_name);
        spinTime=(Spinner)findViewById(R.id.spinner_time);
        spinPaymentype = (Spinner) findViewById(R.id.spinner_payment_type);

        txtPackageName=findViewById(R.id.txt_package_name);
        txtPackageType=findViewById(R.id.txt_package_type);
        txtInstructorName=findViewById(R.id.txt_instructor_name);
        txtTime=findViewById(R.id.txt_time);
        txtPaymentType=findViewById(R.id.txt_payment_type);

        LinearLytPackageName=findViewById(R.id.packagename_lyt);

        //api's for spinners
        packageTypeClass();
        instructorNameClass();
        timeClass();
        PaymenttypeClass();

        requestPermission();

        //************** Setting data to spinner seletced item Package Type ***************
        spinPackageType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                    packageType = tv.getText().toString();
                    if (index != 0) {
                        txtPackageType.setVisibility(View.VISIBLE);
                        if(inputContact.getText().length()==0){
                            Toast.makeText(parent.getContext(), "Please Select Member First ", Toast.LENGTH_LONG).show();

                        }
                    }
                    packageNameClass();
                    if(!packageType.equals(getResources().getString(R.string.hint_packagetype))){
                        LinearLytPackageName.setVisibility(View.VISIBLE);

                    }
                    inputDuration.setText("");
                    inputPackageFees.setText("");
                    inputRate.setText("");
                    inputSession.setText("");
                    inputStartDate.setText("");
                    inputEndDate.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(parent.getContext(), "Please Select Package Type ", Toast.LENGTH_LONG).show();
            }
        });
        spinPackageType.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputContact.getWindowToken(), 0);
                return false;
            }
        }) ;
        // *********************** Package Name **********************
        spinPackageName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if(view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    View layout = (View) view.findViewById(R.id.layout);
                    layout.setPadding(0, 0, 0, 0);

                    packageDetailsclass();
                    if (index == 0) {
                        tv.setTextColor((Color.GRAY));
                    } else {
                        tv.setTextColor((Color.BLACK));
                    }
                    packagename = tv.getText().toString();
                    if (index != 0) {
                        txtPackageName.setVisibility(View.VISIBLE);
                        CheckPackageNameClass();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(parent.getContext(), "Please Select Enquiry Type ", Toast.LENGTH_LONG).show();
            }
        });

        spinPackageName.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputContact.getWindowToken(), 0);
                return false;
            }
        }) ;
        //*********** Discount Validation ****************

        // ************* Discount focus up listner ***********
        inputDiscount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                String disc=inputDiscount.getText().toString();
                    if(inputDiscount.getText().length()>0){
                        double discount=Double.parseDouble(disc);

                        if(discount > maxdisc){
                              inputDiscount.setText("");
                            Toast.makeText(RenewActivity.this,"Discount Should not be greater than Max Discount",Toast
                                    .LENGTH_SHORT).show();
                            inputRate.setText(inputPackageFees.getText().toString());
                        }else{
                            double packfees=Double.parseDouble(inputPackageFees.getText().toString());
                            if(inputRegiFees.getText().length()>0){
                                double regfees=Double.parseDouble(inputRegiFees.getText().toString());
                                double rate =(packfees+regfees)-discount;
                                inputRate.setText(String.valueOf(rate));
                            }else{
                                double rate =packfees-discount;
                                inputRate.setText(String.valueOf(rate));
                            }
                            if(inputPaid.getText().length()>0){
                                if(inputRegiFees.getText().length()>0){
                                    double regfees=Double.parseDouble(inputRegiFees.getText().toString());
                                    double rate =(packfees+regfees)-discount;
                                    inputRate.setText(String.valueOf(rate));
                                    double paid=Double.parseDouble(inputPaid.getText().toString());

                                    double finalbal=rate-paid;
                                    finalBalance=String.valueOf(finalbal);
                                    inputBalance.setText(finalBalance);
                                    if(paid>rate){
                                        inputPaid.setText("");
                                        inputBalance.setText("");
                                    }
                                }else{
                                    double rate =packfees-discount;
                                    double paid=Double.parseDouble(inputPaid.getText().toString());

                                    inputRate.setText(String.valueOf(rate));
                                    double finalbal=rate-paid;
                                    finalBalance=String.valueOf(finalbal);
                                    inputBalance.setText(finalBalance);
                                    if(paid>rate){
                                        inputPaid.setText("");
                                        inputBalance.setText("");
                                    }
                                }

                            }
                        }
                    }else{
                        double packfees=0;
                        if(inputPackageFees.getText().length()>0){
                             packfees=Double.parseDouble(inputPackageFees.getText().toString());
                        }

                        double rate =packfees-0;
                        inputRate.setText(String.valueOf(rate));
                    }

            }
        });
        inputDiscount.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }



            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {
                if(inputDiscount.getText().length()>0) {
                    String disc=inputDiscount.getText().toString();
                    double discount=Double.parseDouble(disc);

                    if(discount > maxdisc){
                        inputDiscount.setText("");
                        Toast.makeText(RenewActivity.this,"Discount Should not be greater than Max Discount",Toast
                                .LENGTH_SHORT).show();
                        inputRate.setText(inputPackageFees.getText().toString());
                    }else{
                        double packfees=Double.parseDouble(inputPackageFees.getText().toString());
                        if(inputRegiFees.getText().length()>0){
                            double regfees=Double.parseDouble(inputRegiFees.getText().toString());
                            double rate =(packfees+regfees)-discount;
                            inputRate.setText(String.valueOf(rate));
                        }else{
                            double rate =packfees-discount;
                            inputRate.setText(String.valueOf(rate));
                        }
                        if(inputPaid.getText().length()>0){
                            if(inputRegiFees.getText().length()>0){
                                double regfees=Double.parseDouble(inputRegiFees.getText().toString());
                                double rate =(packfees+regfees)-discount;
                                inputRate.setText(String.valueOf(rate));
                                double paid=Double.parseDouble(inputPaid.getText().toString());

                                double finalbal=rate-paid;
                                finalBalance=String.valueOf(finalbal);
                                inputBalance.setText(finalBalance);
                                if(paid>rate){
                                    inputPaid.setText("");
                                    inputBalance.setText("");
                                }
                            }else{
                                double rate =packfees-discount;
                                double paid=Double.parseDouble(inputPaid.getText().toString());

                                inputRate.setText(String.valueOf(rate));
                                double finalbal=rate-paid;
                                finalBalance=String.valueOf(finalbal);
                                inputBalance.setText(finalBalance);
                                if(paid>rate){
                                    inputPaid.setText("");
                                    inputBalance.setText("");
                                }
                            }
                        }

                    }
                }else{
                    double packfees=0;
                    if(inputPackageFees.getText().length()>0){
                        packfees=Double.parseDouble(inputPackageFees.getText().toString());
                    }

                    if(inputRegiFees.getText().length()>0){
                        double regfees=Double.parseDouble(inputRegiFees.getText().toString());
                        double rate =(packfees-0)+regfees;
                        inputRate.setText(String.valueOf(rate));
                    }else{
                        double rate =packfees-0;
                        inputRate.setText(String.valueOf(rate));
                    }
                    if(inputPaid.getText().length()>0){
                        if(inputRegiFees.getText().length()>0){
                            double regfees=Double.parseDouble(inputRegiFees.getText().toString());
                            double rate =(packfees-0)+regfees;
                            inputRate.setText(String.valueOf(rate));
                            double paid=Double.parseDouble(inputPaid.getText().toString());

                            double finalbal=rate-paid;
                            finalBalance=String.valueOf(finalbal);
                            inputBalance.setText(finalBalance);
                            if(paid>rate){
                                inputPaid.setText("");
                                inputBalance.setText("");
                            }
                        }else{
                            double rate =packfees-0;
                            double paid=Double.parseDouble(inputPaid.getText().toString());

                            inputRate.setText(String.valueOf(rate));
                            double finalbal=rate-paid;
                            finalBalance=String.valueOf(finalbal);
                            inputBalance.setText(finalBalance);
                            if(paid>rate){
                                inputPaid.setText("");
                                inputBalance.setText("");
                            }
                        }
                    }
                }
            }
        });
        // ************************** add registration fees into rate **********************
        inputRegiFees.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(inputRegiFees.getText().length()>0){
                    double regfees=Double.parseDouble(inputRegiFees.getText().toString());
                    double packfees=0;
                    double discount=0;
                    double rate=0;
                    if(inputPackageFees.getText().length()>0){
                        packfees=Double.parseDouble(inputPackageFees.getText().toString());
                    }

                    if(inputDiscount.getText().length()>0) {
                         discount = Double.parseDouble(inputDiscount.getText().toString());

                    }
                    rate= (packfees+regfees)-discount;
                    inputRate.setText(String.valueOf(rate));
                    Log.v(TAG, String.format("Discount  :: souble reg fees= %s", regfees));

                }else{
                    double packfees=0;
                    if(inputPackageFees.getText().length()>0){
                        packfees=Double.parseDouble(inputPackageFees.getText().toString());
                    }
                    double discount=0;
                    if(inputDiscount.getText().length()>0) {
                        discount = Double.parseDouble(inputDiscount.getText().toString());
                    }
                    double rate= (packfees+0)-discount;

                    inputRate.setText(String.valueOf(rate));
                }


            }
        });
        inputRegiFees.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }
            public void afterTextChanged(Editable s) {
                if(inputRegiFees.getText().length()>0) {
                    double regfees=Double.parseDouble(inputRegiFees.getText().toString());
                    double packfees=0;
                    if(inputPackageFees.getText().length()>0){
                        packfees=Double.parseDouble(inputPackageFees.getText().toString());
                    }

                    double discount=0;
                    double rate=0;
                    if(inputDiscount.getText().length()>0) {
                        discount = Double.parseDouble(inputDiscount.getText().toString());

                        Log.v(TAG, String.format("Discount  :: rate = %s", rate));
                    }
                    rate= (packfees+regfees)-discount;
                    inputRate.setText(String.valueOf(rate));

                    if(inputPaid.getText().length()>0){
                         rate= (packfees+regfees)-discount;

                            inputRate.setText(String.valueOf(rate));
                            double paid=Double.parseDouble(inputPaid.getText().toString());

                            double finalbal=rate-paid;
                            finalBalance=String.valueOf(finalbal);
                            inputBalance.setText(finalBalance);

                            if(paid>rate){
                                inputPaid.setText("");
                                inputBalance.setText("");
                            }

                    }else{
                        inputBalance.setText("");
                    }
                }else{
                    double packfees=0;
                    if(inputPackageFees.getText().length()>0){
                        packfees=Double.parseDouble(inputPackageFees.getText().toString());
                    }
                    double discount=0;
                    if(inputDiscount.getText().length()>0) {
                        discount = Double.parseDouble(inputDiscount.getText().toString());
                    }
                    double rate= (packfees+0)-discount;

                    inputRate.setText(String.valueOf(rate));
                    double paid =0;
                    if(inputPaid.getText().length()>0) {
                         paid = Double.parseDouble(inputPaid.getText().toString());
                    }
                    inputRate.setText(String.valueOf(rate));
                    double finalbal=rate-paid;
                    finalBalance=String.valueOf(finalbal);
                    inputBalance.setText(finalBalance);
                }
            }
        });
         // ************* rate and paid same then next followup date is null ***********
        inputPaid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(inputPaid.getText().length()>0){
                    if(inputRegiFees.getText().length()>0) {
                        double regfees=Double.parseDouble(inputRegiFees.getText().toString());
                        double packfees=0;
                        if(inputPackageFees.getText().length()>0){
                            packfees=Double.parseDouble(inputPackageFees.getText().toString());
                        }

                        double discount=0;
                        if(inputDiscount.getText().length()>0) {
                            discount = Double.parseDouble(inputDiscount.getText().toString());
                            double rate= (packfees-discount)+regfees;

                            inputRate.setText(String.valueOf(rate));
                        }
                        Log.v(TAG, String.format("Discount  :: souble reg fees= %s", regfees));
                        if(inputPaid.getText().length()>0){
                            double rate= (packfees-discount)+regfees;

                            inputRate.setText(String.valueOf(rate));
                            double paid=Double.parseDouble(inputPaid.getText().toString());

                            double finalbal=rate-paid;
                            finalBalance=String.valueOf(finalbal);
                            inputBalance.setText(finalBalance);

                        }
                    }
                    double rate=0;
                    if(inputRate.getText().length()>0){

                        rate=Double.parseDouble(inputRate.getText().toString());
                    }
                    double paid=Double.parseDouble(inputPaid.getText().toString());
                    double tax=Double.parseDouble(Tax);

                    double finalbal=rate-paid;
                    finalBalance=String.valueOf(finalbal);
                    inputBalance.setText(finalBalance);
                    double i=(paid/((tax/100)+1));
                    double tax_amt=paid-i;
                    TaxAmount=String.valueOf(tax_amt);
                    subtotal=String.valueOf(i);
                    Log.v(TAG, String.format("Discount  ::Tax amount= %s", TaxAmount));
                    Log.v(TAG, String.format("Discount  ::Tax = %s", Tax));
                   if(rate == paid){
                       awesomeValidation.clear();
                       inputNextFollDate.getText().clear();
                       inputNextFollDate.setEnabled(false);
                       inputNextFollDate.setKeyListener(null);

                   } else{
                       inputNextFollDate.setEnabled(true);
                       String curr_date = Utility.getCurrentDate();
                       inputNextFollDate.setText(curr_date);
                   }
                    if(paid>rate){
                        Toast.makeText(RenewActivity.this,"Your paying more than your fees",Toast
                                .LENGTH_SHORT).show();
                         inputBalance.setText(String.valueOf(rate));
                        inputPaid.setText("");
                        inputNextFollDate.getText().clear();
                        inputNextFollDate.setEnabled(false);
                        inputNextFollDate.setKeyListener(null);
                    }
                }else {
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
                    double rate=0;
                    if(inputRate.getText().length()>0){

                        rate=Double.parseDouble(inputRate.getText().toString());
                    }
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
                        awesomeValidation.clear();
                        inputNextFollDate.getText().clear();
                        inputNextFollDate.setEnabled(false);
                        inputNextFollDate.setKeyListener(null);

                    } else{
                        inputNextFollDate.setEnabled(true);
                        String curr_date = Utility.getCurrentDate();
                        inputNextFollDate.setText(curr_date);
                    }
                    if(paid>rate){
                        Toast.makeText(RenewActivity.this,"Your paying more than your fees",Toast
                                .LENGTH_SHORT).show();
                        inputBalance.setText(String.valueOf(rate));
                        inputPaid.setText("");
                        inputNextFollDate.getText().clear();
                        inputNextFollDate.setEnabled(false);
                        inputNextFollDate.setKeyListener(null);
                        awesomeValidation.clear();
                    }
                }else{
                    inputBalance.setText("");
                }

            }
        });

        // *********** start date calender ****************
        inputStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                awesomeValidation.clear();

                DatePickerDialog datePickerDialog = new DatePickerDialog(RenewActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String date=(year + "-"
                                        + (monthOfYear + 1) + "-" + dayOfMonth).toString();
                                String cdate= Utility.formatDateDB(date);
                                inputStartDate.setText(cdate);
                                if(inputDuration.getText().length()>0) {
                                    int duration = Integer.parseInt(inputDuration.getText().toString());
                                    EndDate = Utility.CalulateDateFromGivenDays(inputStartDate.getText().toString(), (duration));
                                }
                                inputEndDate.setText(EndDate);
                                Log.v(TAG, String.format("End Date  :: End date= %s", EndDate));
                            }
                        }, mYear, mMonth, mDay);
                //datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                datePickerDialog.show();

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


                DatePickerDialog datePickerDialog = new DatePickerDialog(RenewActivity.this,
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
        spinInstructor.setSelection(1);
        // *********************** Instructor Name **********************
        spinInstructor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if(view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    View layout = (View) view.findViewById(R.id.layout);
                    layout.setPadding(0, 0, 0, 0);
                        tv.setTextColor((Color.BLACK));
                    txtInstructorName.setVisibility(View.VISIBLE);
//                    }
                    instructorname = tv.getText().toString();
                    if (index != 0) {
                        txtInstructorName.setVisibility(View.VISIBLE);
                    }
                     if(instructorname.equals(getResources().getString(R.string.hint_instructor))){
                         instructorname="NA";
                     }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(parent.getContext(), "Please Select Enquiry Type ", Toast.LENGTH_LONG).show();
            }
        });
        spinInstructor.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputContact.getWindowToken(), 0);
                return false;
            }
        }) ;
        spinTime.setSelection(1);
        // *********************** Time **********************
        spinTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if(view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    View layout = (View) view.findViewById(R.id.layout);
                    layout.setPadding(0, 0, 0, 0);

                        tv.setTextColor((Color.BLACK));
                    txtTime.setVisibility(View.VISIBLE);
//                    }
                    time = tv.getText().toString();
                    if (index != 0) {
                        txtTime.setVisibility(View.VISIBLE);
                    }
                    if(time.equals(getResources().getString(R.string.time))){
                        time="NA";
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(parent.getContext(), "Please Select Enquiry Type ", Toast.LENGTH_LONG).show();
            }
        });
        spinTime.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputContact.getWindowToken(), 0);
                return false;
            }
        }) ;
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
           // sendEmail();
            submitForm();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void submitForm() {
            if(!inputBalance.getText().toString().equals("0.0")){
                awesomeValidation.addValidation(RenewActivity.this, R.id.input_nextfollDate,RegexTemplate.NOT_EMPTY, R.string.err_msg_next_foll_date);
                if (awesomeValidation.validate()) {

                    double paid = Double.parseDouble(inputPaid.getText().toString());
                    if(packageType.equals(getResources().getString(R.string.hint_packagetype)) || packagename.equals(getResources().getString(R.string.hint_package_name))
                            || paymentType.equals(getResources().getString(R.string.payment_type)) ){
                        Toast.makeText(this, "Please fill Payment type or Package type", Toast.LENGTH_LONG).show();
                    }else{
                        if(paid <=0){
                            inputPaid.getText().clear();
                            inputBalance.getText().clear();
                            inputPaid.requestFocus();
                            Toast.makeText(RenewActivity.this, "Please pay some amount", Toast
                                    .LENGTH_SHORT).show();
                        }else{
                            AddCourseClass();
                        }

                    }
                }
            }else{
                if (awesomeValidation.validate()) {

                    double paid = Double.parseDouble(inputPaid.getText().toString());
                    if(packageType.equals(getResources().getString(R.string.hint_packagetype)) || packagename.equals(getResources().getString(R.string.hint_package_name))
                            || paymentType.equals(getResources().getString(R.string.payment_type)) ){
                        Toast.makeText(this, "Please fill Payment type or Package type", Toast.LENGTH_LONG).show();
                    }else{
                        if(paid <=0){
                            inputPaid.getText().clear();
                            inputBalance.getText().clear();
                            inputPaid.requestFocus();
                            Toast.makeText(RenewActivity.this, "Please pay some amount", Toast
                                    .LENGTH_SHORT).show();
                        }else{
                            AddCourseClass();
                        }
                    }
                }
            }
    }
    //   ************** Check Contact number already exist or not **************
    public void  CheckContactClass() {
        RenewActivity.CheckContactTrackClass ru = new RenewActivity.CheckContactTrackClass();
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
            EnquiryForDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(RenewActivity.this) );
            EnquiryForDetails.put("action", "check_mobile_already_exist_or_not");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(RenewActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, EnquiryForDetails);
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
                Toast.makeText(RenewActivity.this,"Member is not registred. Please register Member first",Toast.LENGTH_SHORT).show();
              Intent intent=new Intent(RenewActivity.this, AddMemberActivity.class);
              intent.putExtra("contact",inputContact.getText().toString());
              startActivity(intent);

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


                        }
                    }
                } else if (jsonArrayResult.length() == 0) {
                    System.out.println("No records found");
                }
            }else if (success.equalsIgnoreCase(getResources().getString(R.string.one)))
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


                        }
                    }
                } else if (jsonArrayResult.length() == 0) {
                    System.out.println("No records found");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // ************* Package Type spinner *******************
    public void  packageTypeClass() {
        RenewActivity.PackageTypeTrackClass ru = new RenewActivity.PackageTypeTrackClass();
        ru.execute("5");
    }
    class PackageTypeTrackClass extends AsyncTask<String, Void, String> {

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
            PackageTypeDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> PackageTypeDetails = new HashMap<String, String>();
            PackageTypeDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(RenewActivity.this));
            PackageTypeDetails.put("action", "show_package_type");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(RenewActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, PackageTypeDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void PackageTypeDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {


            try {
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    if (object != null) {
                        JSONArray jsonArrayCountry = object.getJSONArray("result");
                        packageTypeArrayList.clear();
                        packagetypelist = new Spinner_List();
                        packagetypelist.setName(getResources().getString(R.string.hint_packagetype));
                        packageTypeArrayList.add(0,packagetypelist);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                packagetypelist = new Spinner_List();
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String PackageType     = jsonObj.getString("PackageType");

                                    packagetypelist.setName(PackageType);

                                    packageTypeArrayList.add(packagetypelist);

                                    packagetypeadapter = new AddEnquirySpinnerAdapter(RenewActivity.this, packageTypeArrayList){
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
                                                tv.setText(getResources().getString(R.string.prompt_packagetype));
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinPackageType.setAdapter(packagetypeadapter);


                                }
                            }
                        }else if(jsonArrayCountry.length()==0){
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(RenewActivity.this);
                    builder.setMessage("Create Package First");
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
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    // ************* Package Name spinner *******************
    public void  packageNameClass() {
        RenewActivity.PackageNameTrackClass ru = new RenewActivity.PackageNameTrackClass();
        ru.execute("5");
    }
    class PackageNameTrackClass extends AsyncTask<String, Void, String> {

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
            PackageNameDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> PackageNameDetails = new HashMap<String, String>();
            PackageNameDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(RenewActivity.this));
            PackageNameDetails.put("pack_type", packageType);
            PackageNameDetails.put("action", "show_packages_details");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(RenewActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, PackageNameDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void PackageNameDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {


            try {
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    if (object != null) {
                        JSONArray jsonArrayCountry = object.getJSONArray("result");
                        packagenameArrayList.clear();
                        packageNamelist = new Spinner_List();
                        packageNamelist.setName(getResources().getString(R.string.hint_package_name));
                        packagenameArrayList.add(0,packageNamelist);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                packageNamelist = new Spinner_List();
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String PackageName     = jsonObj.getString("PackageName");



                                    packageNamelist.setName(PackageName);

                                    packagenameArrayList.add(packageNamelist);

                                    packagenameadapter = new AddEnquirySpinnerAdapter(RenewActivity.this, packagenameArrayList){
                                        @Override
                                        public boolean isEnabled(int position){
                                            if(position == 0)
                                            {
                                                // Disable the first item from Spinner
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
                                                tv.setText(getResources().getString(R.string.prompt_package_name));

                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinPackageName.setAdapter(packagenameadapter);


                                }
                            }
                        }else if(jsonArrayCountry.length()==0){
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
//****************** package More information by Package Name *****************
    private void packageDetailsclass() {
        RenewActivity.PackageTrackclass ru = new RenewActivity.PackageTrackclass();
        ru.execute("5");
    }

    class PackageTrackclass extends AsyncTask<String, Void, String> {

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
            PackageDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> PackageDetails = new HashMap<String, String>();
            PackageDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(RenewActivity.this));
            PackageDetails.put("pack_name",packagename );
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(RenewActivity.this)));
            PackageDetails.put("action","show_package_details_by_name");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(RenewActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, PackageDetails);
            return loginResult;
        }


    }

    private void PackageDetails(String jsonResponse) {

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

                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Package_ID = jsonObj.getString("Package_ID");
                                    String PackageName = jsonObj.getString("PackageName");
                                    String Description = jsonObj.getString("Description");
                                    String Duration = jsonObj.getString("Duration");
                                     Session = jsonObj.getString("Session");
                                     Tax = jsonObj.getString("Tax");
                                    String RackAmount = jsonObj.getString("RackAmount");
                                     MaxDiscount = jsonObj.getString("MaxDiscount");
                                    String PackageType = jsonObj.getString("PackageType");
                                     Days = jsonObj.getString("Days");

                                        inputSession.setText(Session);
                                        inputDuration.setText(Duration);
                                        inputPackageFees.setText(RackAmount);
                                        inputRate.setText(RackAmount);
                                        String start_date = Utility.getCurrentDate();
                                        inputStartDate.setText(start_date);
                                        maxdisc=Double.parseDouble(MaxDiscount);
                                        if(inputDuration.getText().length()>0) {
                                            int duration = Integer.parseInt(inputDuration.getText().toString());
                                            EndDate = Utility.CalulateDateFromGivenDays(inputStartDate.getText().toString(), (duration));
                                        }
                                        inputEndDate.setText(EndDate);
                                        inputPaid.setText("");
                                        inputBalance.setText("");
                                        inputRegiFees.setText("");
                                        inputDiscount.setText("");


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
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(RenewActivity.this);
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
    // ************* Instructor Name spinner *******************
    public void  instructorNameClass() {
        RenewActivity.InstructorNameTrackClass ru = new RenewActivity.InstructorNameTrackClass();
        ru.execute("5");
    }
    class InstructorNameTrackClass extends AsyncTask<String, Void, String> {

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
            InstructorNameDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> InstructorNameDetails = new HashMap<String, String>();
            InstructorNameDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(RenewActivity.this));
            InstructorNameDetails.put("action", "show_instructor_name_list");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(RenewActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, InstructorNameDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }
    }


    private void InstructorNameDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {


            try {
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    if (object != null) {
                        JSONArray jsonArrayCountry = object.getJSONArray("result");
                        instructorArrayList.clear();
                        instructorList = new Spinner_List();
                        instructorList.setName(getResources().getString(R.string.hint_instructor));
                        instructorArrayList.add(0,instructorList);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            instructorList.setName("NA");
                            instructorArrayList.add(1,instructorList);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                instructorList = new Spinner_List();
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Name     = jsonObj.getString("Name");


                                    instructorList.setName(Name);

                                    instructorArrayList.add(instructorList);

                                    instructoradapter = new AddEnquirySpinnerAdapter(RenewActivity.this, instructorArrayList){
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
                                                tv.setText(getResources().getString(R.string.prompt_instructor));
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinInstructor.setAdapter(instructoradapter);


                                }
                            }
                        }else if(jsonArrayCountry.length()==0){
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    instructorArrayList.clear();
                    instructorList = new Spinner_List();
                    instructorList.setName(getResources().getString(R.string.hint_instructor));
                    instructorArrayList.add(0,instructorList);
                    instructorList.setName("NA");
                    instructorArrayList.add(1,instructorList);
                    instructoradapter = new AddEnquirySpinnerAdapter(RenewActivity.this, instructorArrayList){
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
                                tv.setText(getResources().getString(R.string.prompt_instructor));
                            }
                            else {
                                tv.setTextColor(Color.BLACK);
                            }
                            return view;
                        }

                    };
                    spinInstructor.setAdapter(instructoradapter);
                        txtInstructorName.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    // ************* Time spinner *******************
    public void  timeClass() {
        RenewActivity.TimeTrackClass ru = new RenewActivity.TimeTrackClass();
        ru.execute("5");
    }
    class TimeTrackClass extends AsyncTask<String, Void, String> {

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
            TimeDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> TimeDetails = new HashMap<String, String>();
            TimeDetails.put("action", "show_master_time");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(RenewActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, TimeDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void TimeDetails(String jsonResponse) {


        if (jsonResponse != null) {


            try {
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    if (object != null) {
                        JSONArray jsonArrayCountry = object.getJSONArray("result");
                        timeArrayList.clear();
                        timelist = new Spinner_List();
                        timelist.setName(getResources().getString(R.string.time));
                        timeArrayList.add(0,timelist);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            timelist.setName("NA");
                            timeArrayList.add(1,timelist);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                timelist = new Spinner_List();
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Time     = jsonObj.getString("Time");


                                    timelist.setName(Time);

                                    timeArrayList.add(timelist);

                                    timeadapter = new AddEnquirySpinnerAdapter(RenewActivity.this, timeArrayList){
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
                                                tv.setText(getResources().getString(R.string.prompt_time));
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinTime.setAdapter(timeadapter);


                                }
                            }
                        }else if(jsonArrayCountry.length()==0){
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    timeArrayList.clear();
                    timelist = new Spinner_List();
                    timelist.setName(getResources().getString(R.string.time));
                    timeArrayList.add(0,timelist);
                    timelist.setName("NA");
                    timeArrayList.add(1,timelist);
                    timeadapter = new AddEnquirySpinnerAdapter(RenewActivity.this, timeArrayList){
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
                                tv.setText(getResources().getString(R.string.prompt_time));
                            }
                            else {
                                tv.setTextColor(Color.BLACK);
                            }
                            return view;
                        }

                    };
                    spinTime.setAdapter(timeadapter);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    // *************** Payment Mode *******************
    public void  PaymenttypeClass() {
        RenewActivity.PaymentTypeTrackClass ru = new RenewActivity.PaymentTypeTrackClass();
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
            String domainurl= SharedPrefereneceUtil.getDomainUrl(RenewActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, PaymentTypeDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void PaymentTypeDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {


            try {
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
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String PaymentType     = jsonObj.getString("PaymentType");

                                    String id=jsonObj.getString("Auto_Id");
                                    paymentTypeList.setName(PaymentType);
                                    paymentTypeList.setId(id);

                                    paymentTypeArrayList.add(paymentTypeList);

                                    paymentTypeadapter = new AddEnquirySpinnerAdapter(RenewActivity.this, paymentTypeArrayList){
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
    public void  AddCourseClass() {
        RenewActivity.AddCourseTrackClass ru = new RenewActivity.AddCourseTrackClass();
        ru.execute("5");
    }
    class AddCourseTrackClass extends AsyncTask<String, Void, String> {

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
            AddCourseDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {

            if(inputStartDate.getText().length()>0 && inputDuration.getText().length()>0){
                int duration =Integer.parseInt(inputDuration.getText().toString());
                EndDate= Utility.CalulateDateFromGivenDays(inputStartDate.getText().toString(),duration);
                Log.v(TAG, String.format("End Date  :: End date= %s", EndDate));
            }
            HashMap<String, String> AddCourseDetails = new HashMap<String, String>();
            AddCourseDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(RenewActivity.this));
            Log.v(TAG, String.format("doInBackground :: comp_id = %s", SharedPrefereneceUtil.getSelectedBranchId(RenewActivity.this)));
            AddCourseDetails.put("member_id",MemberID);
            Log.v(TAG, String.format("doInBackground :: member_id = %s", MemberID));
            AddCourseDetails.put("name",inputName.getText().toString());
            Log.v(TAG, String.format("doInBackground :: name = %s", inputName.getText().toString()));
            AddCourseDetails.put("contact",inputContact.getText().toString());
            Log.v(TAG, String.format("doInBackground :: contact = %s", inputContact.getText().toString()));
            AddCourseDetails.put("email",Email);
            Log.v(TAG, String.format("doInBackground :: Email = %s", Email));
            AddCourseDetails.put("package_type",packageType );
            Log.v(TAG, String.format("doInBackground :: package_type = %s", packageType));
            AddCourseDetails.put("package_name",packagename);
            Log.v(TAG, String.format("doInBackground :: package_type = %s", packageType));
            AddCourseDetails.put("duration",inputDuration.getText().toString());
            Log.v(TAG, String.format("doInBackground :: duration = %s", inputDuration.getText().toString()));
            AddCourseDetails.put("session", Session);
            Log.v(TAG, String.format("doInBackground :: package_type = %s", packageType));
            AddCourseDetails.put("start_date",inputStartDate.getText().toString());
            Log.v(TAG, String.format("doInBackground :: package_type = %s", packageType));
            AddCourseDetails.put("end_date",EndDate);
            Log.v(TAG, String.format("doInBackground :: package_type = %s", packageType));
            Log.v(TAG, String.format("doInBackground :: package_type = %s", packageType));
            AddCourseDetails.put("instructor_name",instructorname);
            Log.v(TAG, String.format("doInBackground :: package_type = %s", packageType));
            AddCourseDetails.put("time",time);
            Log.v(TAG, String.format("doInBackground :: package_type = %s", packageType));
            AddCourseDetails.put("days",Days);
            Log.v(TAG, String.format("doInBackground :: package_type = %s", packageType));
            AddCourseDetails.put("registration_fees",inputRegiFees.getText().toString());
            Log.v(TAG, String.format("doInBackground :: package_type = %s", packageType));
            AddCourseDetails.put("package_fees",inputPackageFees.getText().toString());
            Log.v(TAG, String.format("doInBackground :: package_type = %s", packageType));
            AddCourseDetails.put("discount",inputDiscount.getText().toString());
            Log.v(TAG, String.format("doInBackground :: package_type = %s", packageType));
            AddCourseDetails.put("rate",inputRate.getText().toString());
            Log.v(TAG, String.format("doInBackground :: package_type = %s", packageType));
            AddCourseDetails.put("discount_reason",inputDiscReason.getText().toString());
            Log.v(TAG, String.format("doInBackground :: package_type = %s", packageType));
            AddCourseDetails.put("tax",Tax);
            Log.v(TAG, String.format("doInBackground :: package_type = %s", packageType));
            AddCourseDetails.put("tax_amount",TaxAmount);
            Log.v(TAG, String.format("doInBackground :: package_type = %s", packageType));
            AddCourseDetails.put("payment_type",paymentType);
            Log.v(TAG, String.format("doInBackground :: payment_type= %s", paymentType));
            AddCourseDetails.put("payment_details",inputPaymentDtl.getText().toString());
            Log.v(TAG, String.format("doInBackground :: package_type = %s", packageType));
            AddCourseDetails.put("paid",inputPaid.getText().toString());
            Log.v(TAG, String.format("doInBackground :: package_type = %s", packageType));
            AddCourseDetails.put("balance",finalBalance);
            Log.v(TAG, String.format("doInBackground :: discount = %s", inputDiscount.getText().toString()));
            AddCourseDetails.put("comment",inputComment.getText().toString());
            Log.v(TAG, String.format("doInBackground :: discount = %s", inputDiscount.getText().toString()));
            AddCourseDetails.put("next_payment_date",inputNextFollDate.getText().toString());
            Log.v(TAG, String.format("doInBackground :: discount = %s", inputDiscount.getText().toString()));
            AddCourseDetails.put("mem_own_exe", SharedPrefereneceUtil.getName(RenewActivity.this));
            AddCourseDetails.put("mode","AdminApp");
            Log.v(TAG, String.format("doInBackground :: executive name= %s", SharedPrefereneceUtil.getName(RenewActivity.this)));
            AddCourseDetails.put("subtotal",subtotal);
            Log.v(TAG, String.format("doInBackground :: subtotal = %s", subtotal));

            AddCourseDetails.put("action", "add_course");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(RenewActivity.this);
            String loginResult2 = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, AddCourseDetails);

            Log.v(TAG, String.format("doInBackground :: add_course= %s", loginResult2));
            return loginResult2;
        }
    }


    private void AddCourseDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject jsonObjLoginResponse = null;
        try {
            jsonObjLoginResponse = new JSONObject(jsonResponse);
            String success = jsonObjLoginResponse.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                invoice_id=jsonObjLoginResponse.getString("invoice_id");
                receipt_id=jsonObjLoginResponse.getString("receipt_id");
                financial_yr=jsonObjLoginResponse.getString("financial_yr");
                SendEnquirySmsClass();
                submitAction();
                EmailLoginClass();
            }


            else if (success.equalsIgnoreCase(getResources().getString(R.string.one)))
            {
                Toast.makeText(RenewActivity.this,"Mobile Number Already Exits ,Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
                inputContact.getText().clear();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void  EmailLoginClass() {
        RenewActivity.EmailLoginTrackClass ru = new RenewActivity.EmailLoginTrackClass();
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
            EmailLoginDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(RenewActivity.this) );
            EmailLoginDetails.put("action", "show_email_login");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(RenewActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, EmailLoginDetails);

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
        RenewActivity.ReceiptDataTrackclass ru = new RenewActivity.ReceiptDataTrackclass();
        ru.execute("5");
    }
    class ReceiptDataTrackclass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: show_receipt_data = %s", response));
            ReceiptDataDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> ReceiptDataDetails = new HashMap<String, String>();
            ReceiptDataDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(RenewActivity.this));
            ReceiptDataDetails.put("invoice_id", invoice_id);
            ReceiptDataDetails.put("financial_yr", financial_yr);
            ReceiptDataDetails.put("member_id", MemberID);
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(RenewActivity.this)));
            ReceiptDataDetails.put("action","show_receipt_data");

            String domainurl= SharedPrefereneceUtil.getDomainUrl(RenewActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, ReceiptDataDetails);
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

                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Name = inputName.getText().toString();
                                    String Member_Contact = inputContact.getText().toString();
                                    String Invoice_date = jsonObj.getString("Invoice_date");
                                    String invoice_date= Utility.formatDate(Invoice_date);
                                    String Package_Name = packagename;
                                    String Duration_Days =inputDuration.getText().toString();
                                    String Session = inputSession.getText().toString();
                                    String start_date = inputStartDate.getText().toString();
                                    String end_date = EndDate;
                                    String Rate = inputRate.getText().toString();
                                    String Final_paid = inputPaid.getText().toString();
                                    String Final_Balance = jsonObj.getString("Final_Balance");
                                    if(Final_Balance.equals(".00")){
                                        Final_Balance="0.00";
                                    }
                                    String Invoice_ID = invoice_id;
                                    String Member_Email_ID = Email;
                                    String Time = time;
                                    String Instructor_Name = instructorname;
                                    String Package_Fees = inputPackageFees.getText().toString();
                                    String Discount =  jsonObj.getString("Discount");;
                                    if(Discount.equals(".00")){
                                        Discount="0.00";
                                    }
                                    String Registration_Fees =  jsonObj.getString("Registration_Fees");;
                                    if(Registration_Fees.equals(".00")){
                                        Registration_Fees="0.00";
                                    }

                                    String Company_Name = SharedPrefereneceUtil.getCompanyName(RenewActivity.this)+"-"+ SharedPrefereneceUtil.getSelectedBranch(RenewActivity.this);
                                    String Branch=SharedPrefereneceUtil.getSelectedBranch(RenewActivity.this);
                                    String Address = jsonObj.getString("Address");

                                    String ad[]=Address.split(",");

                                    String Contact = jsonObj.getString("Contact");
                                    String TermsAndConditions = jsonObj.getString("TermsAndConditions");
                                    String MemberGST_No = jsonObj.getString("MemberGST_No");
                                    String GST_No = jsonObj.getString("GST_No");
                                    TermsAndConditions = TermsAndConditions.replace("\r\n", "<br />");
                                    String Logo = jsonObj.getString("Logo");
                                    String l=Logo.replaceAll("\\s+","%20");
                                    String domainurl= SharedPrefereneceUtil.getDomainUrl(RenewActivity.this);
                                  final  String imgurl=domainurl+ ServiceUrls.IMAGES_URL+l;

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
                                                textBody = "  <tr >\n \n" +
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


                                  final  Document document = new Document();

                                    try {

                                        PdfWriter docWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
                                        document.open();
//

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
                                        int x=50;
                                        int y=765;
                                        for (Iterable<String> iterable : Iterables.partition(Splitter.on(delimiter).split(Address), partitionSize)) {
                                            System.out.println(Joiner.on(delimiter).join(iterable));
                                            add+=Joiner.on(delimiter).join(iterable)+"<br/>";

                                         y= y-10;
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
                                                "                                        <td  ><strong>Invoice No : </strong>"+invoice_id+"</td>\n" +
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
                                    RenewActivity.this.runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            //second async stared within a asynctask but on the main thread
                                            (new AsyncTask<String, String, Void>() {
                                                ServerClass ruc = new ServerClass();

                                                @Override
                                                protected Void doInBackground(String... params) {
                                                    Mail m = new Mail(Email_ID, Password);

                                                    String[] toArr = { Email, "tulsababar01@gmail.com"};
                                                  //  Log.v(TAG, String.format(" Email array to send = %s", toArr));
                                                    m.setTo(toArr);
                                                    m.setFrom(Email_ID);
                                                    m.setSubject(subject);
                                                    m.setBody(message);
                                                    if (Build.VERSION.SDK_INT >= 23)
                                                    {
                                                        if (checkPermission())
                                                        {
                                                           // final File  file = BitmapSaver.saveImageToExternalStorage(RenewActivity.this, bmpqr);
                                                            m.setAttachment(pdfFile);
                                                            m.setAttachmentName(FilePath);
                                                            m.setAttachmentNamePath("PackageDetails.pdf");
                                                            // Code for above or equal 23 API Oriented Device
                                                            // Your Permission granted already .Do next code
                                                        } else {
                                                            requestPermission(); // Code for permission
                                                        }
                                                    }else{
                                                       // final File  file = BitmapSaver.saveImageToExternalStorage(RenewActivity.this, bmpqr);
                                                        m.setAttachment(pdfFile);
                                                        m.setAttachmentName(FilePath);
                                                        m.setAttachmentNamePath("PackageDetails.pdf");
                                                    }

                                                    try {
                                                        if(m.send()) {
                                                            runOnUiThread(new Runnable() {
                                                                public void run() {

                                                                    Toast.makeText(RenewActivity.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();

                                                                }
                                                            });
                                                        } else {
                                                          //  Toast.makeText(RenewActivity.this, "Email was not sent.", Toast.LENGTH_LONG).show();
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(RenewActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(RenewActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(RenewActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(RenewActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
    public void  CheckPackageNameClass() {
        RenewActivity.CheckPackageNameTrackClass ru = new RenewActivity.CheckPackageNameTrackClass();
        ru.execute("5");
    }

    class CheckPackageNameTrackClass extends AsyncTask<String, Void, String> {

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
            CheckPackageNameDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> EnquiryForDetails = new HashMap<String, String>();

            EnquiryForDetails.put("package_name",packagename );
            EnquiryForDetails.put("member_id",MemberID );
            EnquiryForDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(RenewActivity.this) );
            EnquiryForDetails.put("action", "check_package_already_assigned_to_member");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(RenewActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, EnquiryForDetails);

            Log.v(TAG, String.format("doInBackground :: check_package_already_assigned_to_member= %s", loginResult));
            return loginResult;
        }
    }
    private void CheckPackageNameDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject jsonObjLoginResponse = null;
        try {
            jsonObjLoginResponse = new JSONObject(jsonResponse);
            String success = jsonObjLoginResponse.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.zero))) {

            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.two)))
            {
                String enddate = jsonObjLoginResponse.getString("end_date");
                if(!enddate.equals("NULL")){
                    inputStartDate.setText(Utility.getNextDate(enddate));
                    int duration=0;
                    if(inputDuration.getText().length()>0){
                         duration =Integer.parseInt(inputDuration.getText().toString());
                    }
                    EndDate = Utility.CalulateDateFromGivenDays(inputStartDate.getText().toString(),duration-1);
                    inputEndDate.setText(EndDate);
                    inputRegiFees.setText("");
                    inputPaid.setText("");
                    inputBalance.setText("");
                    Log.v(TAG, String.format("End Date  :: End date= %s", EndDate));
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // ******************* send sms for add enquiry **************
    public void  SendEnquirySmsClass() {
        RenewActivity.SendEnquirySmsTrackClass ru = new RenewActivity.SendEnquirySmsTrackClass();
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

            EnquiryForDetails.put("type","Renew" );
            EnquiryForDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(RenewActivity.this) );
            EnquiryForDetails.put("action", "sms_for_add_enquiry");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(RenewActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, EnquiryForDetails);
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

            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.two)))
            {
                if (object != null) {
                    JSONArray jsonArrayResult = object.getJSONArray("result");


                    if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                        for (int i = 0; i < 1; i++) {

                            JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                            if (jsonObj != null) {

                                afterEnquirySms = jsonObj.getString("Renew");
                                afterEnquirySms = afterEnquirySms.replace(".", "");
                                afterEnquirySms = afterEnquirySms.replace("#MID#", MemberID);
                                String coursename="Your CourseName:"+packageType;
                                afterEnquirySms = afterEnquirySms.replace("#CourseN#", coursename);
                                String pack="Your Package:"+packagename;
                                afterEnquirySms = afterEnquirySms.replace("#plan#", pack);
                                String ttlfees="Total Fees:"+inputRate.getText().toString();
                                afterEnquirySms = afterEnquirySms.replace("#TFess#", ttlfees);
                                String pfees="Paid Fees:"+inputPaid.getText().toString();
                                afterEnquirySms = afterEnquirySms.replace("#REC#", pfees);
                                String remfees="Remaining Fees:"+inputBalance.getText().toString();
                                afterEnquirySms = afterEnquirySms.replace("#RemBal#", remfees);
                                String paymode="Payment Mode:"+paymentType;
                                afterEnquirySms = afterEnquirySms.replace("#PayMode#", paymode);
                                if(paymentType.equals("Cheque")){
                                    String cno="Cheque No.:";
                                    afterEnquirySms = afterEnquirySms.replace("#ChequeNo#", cno);
                                }
                                String iname="Instructor Name:"+instructorname;
                                afterEnquirySms = afterEnquirySms.replace("#Instructor#", iname);
                                String paydate="Payment Date:"+ Utility.getCurrentDate();
                                afterEnquirySms = afterEnquirySms.replace("#PayDate#", paydate);
                                String startdate="Start Date:"+inputStartDate.getText().toString();
                                afterEnquirySms = afterEnquirySms.replace("#SDate#", startdate);
                                String endate="End Date:"+inputEndDate.getText().toString();
                                afterEnquirySms = afterEnquirySms.replace("#EDate#", endate);
                                String nextbalan="Next Balance Date:"+inputNextFollDate.getText().toString();
                                afterEnquirySms = afterEnquirySms.replace("#NextBalanceDate#", nextbalan);
                                String exe="Executive:"+ SharedPrefereneceUtil.getName(RenewActivity.this);
                                afterEnquirySms = afterEnquirySms.replace("#Executive#", exe);
                                String tpaid="Total Paid:"+inputPaid.getText().toString();
                                afterEnquirySms = afterEnquirySms.replace("#TPaid#", tpaid);


                                final String message="Dear "+inputName.getText().toString()+" Thanks For Joining Us." +afterEnquirySms;
                                if(!afterEnquirySms.equals("")) {
                                    RenewActivity.this.runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            //second async stared within a asynctask but on the main thread
                                            (new AsyncTask<String, String, String>() {
                                                ServerClass ruc = new ServerClass();

                                                @Override
                                                protected String doInBackground(String... params) {
                                                    String loginResult2 = ruc.SendSMS(inputContact.getText().toString(), message, SharedPrefereneceUtil.getSmsUsername(RenewActivity.this),
                                                            SharedPrefereneceUtil.getSmsPassword(RenewActivity.this),
                                                            SharedPrefereneceUtil.getSmsRoute(RenewActivity.this),
                                                            SharedPrefereneceUtil.getSmsSenderid(RenewActivity.this));
                                                    Log.v(TAG, String.format("doInBackground :: Send Sms after enquiry= %s", loginResult2));
                                                    return loginResult2;
                                                }

                                                @Override
                                                protected void onPostExecute(String response) {
                                                    super.onPostExecute(response);
                                                    Log.v(TAG, String.format("onPostExecute :: response = %s", response));


                                                }
                                            }).execute();

                                        }
                                    });
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
    private void submitAction() {
        finish();
        Intent intent=new Intent(RenewActivity.this, CourseCongratulationActivity.class);
        intent.putExtra("member_id",MemberID);
        intent.putExtra("invoice_id", invoice_id);
        intent.putExtra("financial_yr",financial_yr);
        intent.putExtra("filepath",pdfFile);
        startActivity(intent);

    }



    @Override
    public boolean onSupportNavigateUp(){
       Intent intent=new Intent(RenewActivity.this, CourseActivity.class);
       startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(RenewActivity.this, CourseActivity.class);
        startActivity(intent);
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
    public void  showSearchListClass() {
        RenewActivity.SearchTrackClass ru = new RenewActivity.SearchTrackClass();
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

            SearchDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(RenewActivity.this) );
            SearchDetails.put("action", "show_all_member_list");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(RenewActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, SearchDetails);
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
                                searchnameadapter = new SearchNameAdapter(RenewActivity.this, searchArrayList);

                                inputName.setAdapter(searchnameadapter);
                                inputName.setThreshold(1);

                                searchcontactadapter = new SearchContactAdapter(RenewActivity.this, searchArrayList);

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
    public void requestContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }

        }
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}
