package com.ndfitnessplus.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ndfitnessplus.Activity.Notification.TodaysEnrollmentActivity;
import com.ndfitnessplus.Adapter.AddEnquirySpinnerAdapter;
import com.ndfitnessplus.Adapter.BalanceTrasactionAdapter;
import com.ndfitnessplus.Adapter.MemberAdapter;
import com.ndfitnessplus.Adapter.MemberDetailsAdapter;
import com.ndfitnessplus.MailUtility.Mail;
import com.ndfitnessplus.Model.BalanceTrasactionList;
import com.ndfitnessplus.Model.CourseList;
import com.ndfitnessplus.Model.MemberDataList;
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

public class CourseDetailsActivity extends AppCompatActivity {

    public static String TAG = CourseDetailsActivity.class.getName();
    private ProgressDialog pd;
    TextView nameTV,regdateTV,packagenameTV,start_to_end_dateTV,rateTV,paidTV,balanceTV,contactTV,executiveNameTV,durationTv,invoice_idTV;
    ImageView contactIV;
    CircularImageView imageView;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    ProgressBar progressBar;
    String member_id,invoice_id,Email,FinancialYear;
    View nodata;
    BalanceTrasactionAdapter adapter;
    ArrayList<BalanceTrasactionList> subListArrayList = new ArrayList<BalanceTrasactionList>();
    BalanceTrasactionList subList;
    String NextFollowupDate;
    Button balance,followup,attendence;
    String Tax;
    String TaxAmount;
    String finalBalance;
    String subtotal;
    String afterEnquirySms;
    private AwesomeValidation awesomeValidation;
    //pop up content
    //Spinner Adapter
    public Spinner spinPaymentype;
    Spinner_List paymentTypeList;
    public AddEnquirySpinnerAdapter paymentTypeadapter;
    ArrayList<Spinner_List> paymentTypeArrayList = new ArrayList<Spinner_List>();
    public EditText inputPaymentDtl,inputPaid,inputNextFollDate,inputComment,inputBalance;
    private int mYear, mMonth, mDay;
    String paymentType;
    TextView txtPaymentType;
    CourseList filterArrayList;
    ImageButton phone,message;
    ImageView whatsapp;

    //setting followup popup
    EditText inputNextFollowupdate,inputfollComment;
    Spinner spinCallResponce,spinRating,spinFollType;
    Spinner_List spinCallReslist,ratingList,spinFollTypeList;


    AddEnquirySpinnerAdapter callresponceadapter,ratingadapter;
    String callResponce="";
    String Rating="";
    String FollowupType="";
    TextView txtcallres,txtrating,txtFollType;
    String[] callresponce ;
    String[] folltype ;
    //Loading gif
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);
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
        rateTV = (TextView) findViewById(R.id.rateTV);
        nameTV = (TextView) findViewById(R.id.nameTV);
        contactIV = (ImageView) findViewById(R.id.contactIV);
        imageView=(CircularImageView) findViewById(R.id.input_image);

        viewDialog = new ViewDialog(this);

        regdateTV = (TextView) findViewById(R.id.reg_dateTV);
        packagenameTV = (TextView) findViewById(R.id.package_nameTV);
        start_to_end_dateTV = (TextView) findViewById(R.id.start_to_end_date_TV);
        paidTV = (TextView) findViewById(R.id.paidTV);
        executiveNameTV=(TextView)findViewById(R.id.excecutive_nameTV);
        balanceTV = (TextView) findViewById(R.id.balanceTV);
        durationTv = (TextView) findViewById(R.id.duration);
        invoice_idTV = (TextView) findViewById(R.id.invoice_idTV);

        phone=findViewById(R.id.phone_call);
        message=findViewById(R.id.message);
        whatsapp=findViewById(R.id.whatsapp);

        progressBar=findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        balance=findViewById(R.id.btn_balance);
        followup=findViewById(R.id.btn_followup);
        attendence=findViewById(R.id.btn_attendence);
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        if (args != null) {
           filterArrayList = (CourseList) args.getSerializable("filter_array_list");

                 String cont=filterArrayList.getContact();
//                Log.v(TAG, String.format("Selected  ::contact= %s", cont));
//                Log.v(TAG, String.format("Selected  ::name= %s", filterArrayList.getName()));
                 contactTV.setText(cont);
            nameTV.setText(filterArrayList.getName());
            String fpaid="₹ "+filterArrayList.getPaid();
            String ttl="₹ "+filterArrayList.getRate();
            rateTV.setText(ttl);
            String regdate=Utility.formatDate(filterArrayList.getRegistrationDate());
            regdateTV.setText(regdate);
            packagenameTV.setText(filterArrayList.getPackageName());
            String dur="Duration: "+filterArrayList.getPackageNameWithDS();
            durationTv.setText(dur);
            start_to_end_dateTV.setText(filterArrayList.getStartToEndDate());
            paidTV.setText(fpaid);
            executiveNameTV.setText(filterArrayList.getExecutiveName());
            balanceTV.setText(filterArrayList.getBalance());
            invoice_id=filterArrayList.getInvoiceID();
            FinancialYear=filterArrayList.getFinancialYear();
            invoice_idTV.setText(invoice_id);
            member_id=filterArrayList.getID();
            Tax=filterArrayList.getTax();
            Email=filterArrayList.getEmail();
            FollowupType=filterArrayList.getFollowuptype();
            if(FollowupType ==null){
                FollowupType="";
            }

            Log.v(TAG, String.format("Selected  ::invoice_id= %s", invoice_id));
            Log.v(TAG, String.format("Selected  ::Paid= %s", fpaid));
            Log.v(TAG, String.format("Selected  ::Balance= %s", filterArrayList.getBalance()));
            String domainurl= SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
            String url= domainurl+ServiceUrls.IMAGES_URL + filterArrayList.getImage();

            Glide.with(this).load(url).placeholder(R.drawable.nouser).into(imageView);
           folldetailsclass();
        }
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:"+contactTV.getText().toString()));
                startActivity(dialIntent);
            }
        });
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFollowupDialog();
            }
        });
        followup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFollowupDialog();
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
                    Toast.makeText(CourseDetailsActivity.this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseDetailsActivity.this, FullImageActivity.class);
                intent.putExtra("image",filterArrayList.getImage());
                intent.putExtra("contact",filterArrayList.getContact());
                intent.putExtra("id",member_id);
                intent.putExtra("user","Member");
                startActivity(intent);
            }
        });

        balanceTrasactionclass();
        coursedetailsclass();
        if(!(balanceTV.getText().toString().equals("0.00"))){
            balance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCustomDialog();
                }
            });
        }else{
            Toast.makeText(CourseDetailsActivity.this, "No Outstanding Remaining", Toast.LENGTH_SHORT).show();
        }
        attendence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CourseDetailsActivity.this,AttendenceActivity.class);
                startActivity(intent);
            }
        });

    }
    private void showCustomDialog() {
        final Dialog dialog = new Dialog(CourseDetailsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.balance_payment_popup);
        dialog.setCancelable(true);

//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(dialog.getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        spinPaymentype = (Spinner)dialog. findViewById(R.id.spinner_payment_type);
        inputComment = (EditText)dialog. findViewById(R.id.input_enquiry_comment);
        //final EditText veri_otp=(EditText)dialog.findViewById(R.id.et_otp);
        inputNextFollDate=(EditText)dialog.findViewById(R.id.input_next_foll_date);
        inputPaymentDtl = (EditText)dialog. findViewById(R.id.input_payment_details);
        inputPaid = (EditText)dialog. findViewById(R.id.input_paid);
        inputNextFollDate = (EditText)dialog. findViewById(R.id.input_nextfollDate);
        inputComment = (EditText)dialog. findViewById(R.id.input_comment);
        inputBalance = (EditText)dialog. findViewById(R.id.input_balance);
        txtPaymentType=dialog.findViewById(R.id.txt_payment_type);
        inputBalance.setText("0");
        // *********** validation *************
        //awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);


      //  awesomeValidation.addValidation(this, R.id.input_paid, RegexTemplate.NOT_EMPTY, R.string.err_msg_paid);
        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //  if(i==0){
        SetSpinner();
        //i++;
        //}
        PaymenttypeClass();
        // ************* rate and paid same then next followup date is null ***********
        inputPaid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(inputPaid.getText().length()>0){
                    double rate=Double.parseDouble(balanceTV.getText().toString());
                    double paid=Double.parseDouble(inputPaid.getText().toString());
                    double tax=Double.parseDouble(Tax);

                    double finalbal=rate-paid;
                    finalBalance=String.valueOf(finalbal);
                    inputBalance.setText(finalBalance);
                    double i=(paid/((tax/100)+1));
                    double tax_amt=paid-i;
                    TaxAmount=String.valueOf(tax_amt);
                    subtotal=String.valueOf(i);
                    // Log.v(TAG, String.format("Max Discount  :: souble max discout= %s", maxdisc));
                    if(rate == paid){
                        inputNextFollDate.getText().clear();
                        inputNextFollDate.setEnabled(false);
                        inputNextFollDate.setKeyListener(null);
                     //   awesomeValidation.clear();
                    } else{
                        inputNextFollDate.setEnabled(true);
                       // awesomeValidation.addValidation(CourseDetailsActivity.this,R.id.input_nextfollDate, RegexTemplate.NOT_EMPTY,R.string.err_msg_next_foll_date);
                    }
                    if(paid>rate){
                        Toast.makeText(CourseDetailsActivity.this,"Your paying more than your fees",Toast
                                .LENGTH_SHORT).show();
                        //awesomeValidation.clear();
                        inputNextFollDate.getText().clear();
                        inputNextFollDate.setEnabled(false);
                        inputNextFollDate.setKeyListener(null);
                    }
                }else{
                    inputBalance.setText(balanceTV.getText().toString());
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
                    double rate=Double.parseDouble(balanceTV.getText().toString());
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
                       // awesomeValidation.clear();
                    } else{
                        inputNextFollDate.setEnabled(true);

                    }
                    if(paid>rate){
                        Toast.makeText(CourseDetailsActivity.this,"Your paying more than your fees",Toast
                                .LENGTH_SHORT).show();
                       // awesomeValidation.clear();
                        inputNextFollDate.getText().clear();
                        inputNextFollDate.setEnabled(false);
                        inputNextFollDate.setKeyListener(null);
                    }
                }else{
                    inputBalance.setText("");
                }

            }
        });
        inputNextFollDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(CourseDetailsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                inputNextFollDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                //tiemPicker();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        ((Button) dialog.findViewById(R.id.btn_submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(inputBalance.getText().toString().equals("0.0"))){
                    // Toast.makeText(this, "Please fill Payment type or Package type", Toast.LENGTH_LONG).show();
                    //awesomeValidation.addValidation(CourseDetailsActivity.this,R.id.input_nextfollDate,RegexTemplate.NOT_EMPTY,R.string.err_msg_next_payment_date);
                    if (inputNextFollDate.getText().length()>0) {
                        // Log.v(TAG, String.format("Remaining balance= %s", inputBalance.getText().toString()));
                        double paid = Double.parseDouble(inputPaid.getText().toString());
                        if(paymentType.equals(getResources().getString(R.string.payment_type)) ){
                            Toast.makeText(CourseDetailsActivity.this, "Please fill Payment type or Package type", Toast.LENGTH_LONG).show();
                        }else{
                            if(paid <=0){
                                inputPaid.getText().clear();
                                inputBalance.getText().clear();
                                inputPaid.requestFocus();
                                Toast.makeText(CourseDetailsActivity.this, "Please pay some amount", Toast
                                        .LENGTH_SHORT).show();
                            }else{
                                AddBalanceReceiptClass();
                                dialog.dismiss();
                            }

                            Log.v(TAG, String.format("calling function= %s", "Add Balnce receipt"));
                        }

                    }else{
                        Toast.makeText(CourseDetailsActivity.this, "Please select next payment date", Toast.LENGTH_LONG).show();
                    }
                }else{
                        // Log.v(TAG, String.format("Remaining balance= %s", inputBalance.getText().toString()));
                        double paid = Double.parseDouble(inputPaid.getText().toString());
                        if(paymentType.equals(getResources().getString(R.string.payment_type)) ){
                            Toast.makeText(CourseDetailsActivity.this, "Please fill Payment type or Package type", Toast.LENGTH_LONG).show();
                        }else{
                            if(paid <=0){
                                Toast.makeText(CourseDetailsActivity.this, "Please pay some amount", Toast
                                        .LENGTH_SHORT).show();
                                inputPaid.getText().clear();
                                inputBalance.getText().clear();
                                inputPaid.requestFocus();

                            }else{
                                AddBalanceReceiptClass();
                                dialog.dismiss();
                            }
                            Log.v(TAG, String.format("calling function= %s", "Add Balnce receipt"));
                        }

                }


            }
        });

        dialog.show();
        //dialog.getWindow().setAttributes(lp);
    }
    public  void SetSpinner(){
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
    private void showFollowupDialog() {
        final Dialog dialog = new Dialog(CourseDetailsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.take_all_followup_popup);
        dialog.setCancelable(true);

//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(dialog.getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        spinCallResponce = (Spinner)dialog. findViewById(R.id.spinner_call_res);
        spinRating = (Spinner)dialog. findViewById(R.id.spinner_rating);
        spinFollType= (Spinner)dialog. findViewById(R.id.spinner_folltype);
        txtcallres=(TextView)dialog.findViewById(R.id.txt_callres);
        txtrating=(TextView)dialog.findViewById(R.id.txt_rating);
        txtFollType=(TextView)dialog.findViewById(R.id.txt_folltype);

        inputfollComment = (EditText)dialog. findViewById(R.id.input_enquiry_comment);
        //final EditText veri_otp=(EditText)dialog.findViewById(R.id.et_otp);
        inputNextFollowupdate=(EditText)dialog.findViewById(R.id.input_next_foll_date);

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //  if(i==0){
        SetFollowupSpinner();
        //i++;
        //}
        callResponseClass();
        follTypeClass();
        inputNextFollowupdate.setText(NextFollowupDate);
        inputNextFollowupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(CourseDetailsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                inputNextFollowupdate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                //tiemPicker();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        ((Button) dialog.findViewById(R.id.btn_submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( callResponce.equals(getResources().getString(R.string.call_res)) || Rating.equals(getResources().getString(R.string.rating))){
                    Toast.makeText(getApplicationContext(), "Please select Call Response or Rating", Toast.LENGTH_SHORT).show();
                }else{
                    if(inputNextFollowupdate.getText().length()==0) {
                        if (!(Rating.equals("Not Interested") || Rating.equals("Converted")||FollowupType.equals("Member BirthDay"))) {
                            Toast.makeText(getApplicationContext(), "Please select Next Followup Date" , Toast.LENGTH_SHORT).show();
                        }else{
                            if(inputfollComment.getText().length()>0) {
                                takefollowupclass();
                                dialog.dismiss();
                            }else{
                                Toast.makeText(getApplicationContext(), "Please enter comment" , Toast.LENGTH_SHORT).show();

                            }
                        }
                    }else{
                        if(inputfollComment.getText().length()>0) {
                            takefollowupclass();
                            dialog.dismiss();
                        }else{
                            Toast.makeText(getApplicationContext(), "Please enter comment" , Toast.LENGTH_SHORT).show();

                        }
                    }
                }

                //  Toast.makeText(CourseDetailsActivity.this, "Mobile number verified successully", Toast.LENGTH_SHORT).show();


                //Toast.makeText(getApplicationContext(), "Subcribe Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        //dialog.getWindow().setAttributes(lp);
    }
    public  void SetFollowupSpinner(){

        spinCallResponce.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    view.setPadding(0, 0, 0,0);
                    tv.setTextColor(getResources().getColor(R.color.black));
                    if(!(callResponce==null)) {
                        if (callResponce.equals(tv.getText().toString())) {
                            spinCallResponce.setSelection(index);
                            txtcallres.setVisibility(View.VISIBLE);
                        }
                        callResponce = tv.getText().toString();
                    }
                    if(index==0){
                        txtcallres.setVisibility(View.VISIBLE);
                        tv.setText(callResponce);
                    }
                    // ((TextView) spinEnquiryType.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
                    // Showing selected spinner item
                    //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinFollType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    view.setPadding(0, 0, 0, 0);
                    tv.setTextColor(getResources().getColor(R.color.black));
                    if(!(FollowupType==null)) {
                        if (FollowupType.equals(tv.getText().toString())) {
                            spinFollType.setSelection(index);
                            txtFollType.setVisibility(View.VISIBLE);
                        }
                        FollowupType = tv.getText().toString();
                    }
                    if(index==0){
                        txtFollType.setVisibility(View.VISIBLE);
                        tv.setText(FollowupType);
                    }
                    if (!(FollowupType == null)) {
                        if (FollowupType.equals("Member BirthDay")) {
                            //Toast.makeText(parent.getContext(), "no interetsed: ", Toast.LENGTH_LONG).show();
                            inputNextFollowupdate.setText("");
                            inputNextFollowupdate.setEnabled(false);
                            inputNextFollowupdate.setKeyListener(null);
                        } else {
                            inputNextFollowupdate.setEnabled(true);
                        }
                    }
                    // ((TextView) spinEnquiryType.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
                    // Showing selected spinner item
                    //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Call Responce spinner adapter setting
        final  String[] ratingarray = getResources().getStringArray(R.array.rating_array);
        ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(
                this,R.layout.spinner_item,ratingarray ){
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
                if(view !=null){
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
//                    View layView = super.getView(position, convertView, parent);
//                    layView.setPadding(0, 0, layView.getPaddingRight(), 0);
                    if(position == 0){
                        // Set the hint text color gray
                        tv.setTextColor(Color.GRAY);
                        tv.setText(getResources().getString(R.string.rating));
                        // tv.setTextColor(Color.GRAY);
                    }
                    else {
                        tv.setTextColor(Color.BLACK);
                    }
                }

                return view;
            }
        };
        spinnerArrayAdapter1.setDropDownViewResource(R.layout.spinner_item);
        spinRating.setAdapter(spinnerArrayAdapter1);
        int spinnerPositionrating = spinnerArrayAdapter1.getPosition(Rating);
        //Toast.makeText(this, "position: " + spinnerPositionrating, Toast.LENGTH_LONG).show();
        spinRating.setSelection(spinnerPositionrating);
        ArrayList<Spinner_List> ratingArrayList = new ArrayList<Spinner_List>();
//
        spinRating.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    //View layView = super.getView(position, convertView, parent);
                    view.setPadding(0, 0, 0, 0);
                    tv.setTextColor(getResources().getColor(R.color.black));
                    if (!(Rating == null)) {
                        if (Rating.equals(tv.getText().toString())) {
                            spinRating.setSelection(index);
                            txtrating.setVisibility(View.VISIBLE);
                        }
                        Rating = tv.getText().toString();
                    }
                    if (!(Rating == null)) {
                        if (Rating.equals("Not Interested") || Rating.equals("Converted")) {
                            //Toast.makeText(parent.getContext(), "no interetsed: ", Toast.LENGTH_LONG).show();
                            inputNextFollowupdate.setText("");
                            inputNextFollowupdate.setEnabled(false);
                            inputNextFollowupdate.setKeyListener(null);
                        } else {
                            inputNextFollowupdate.setEnabled(true);
                        }
                    }
                    if(index==0){
                        txtrating.setVisibility(View.VISIBLE);
                        tv.setText(Rating);
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

    private void showProgressDialog() {
        Log.v(TAG, String.format("showProgressDialog"));
        pd = new ProgressDialog(CourseDetailsActivity.this);
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
    //*********** Asycc class for loading data for database **************
    private void balanceTrasactionclass() {
        CourseDetailsActivity.BalanceTrasactionTrackclass ru = new CourseDetailsActivity.BalanceTrasactionTrackclass();
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
            BalanceTrasactionDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this));
            BalanceTrasactionDetails.put("member_id",member_id );
            BalanceTrasactionDetails.put("invoice_id",invoice_id );
            BalanceTrasactionDetails.put("financial_yr",FinancialYear );
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this)));
            Log.v(TAG, String.format("doInBackground :: member_id = %s", member_id));
            Log.v(TAG, String.format("doInBackground :: invoice_id = %s", invoice_id));
            Log.v(TAG, String.format("doInBackground :: FinancialYear = %s", FinancialYear));
            BalanceTrasactionDetails.put("action","show_balance_trasaction_details");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
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
                        ArrayList<BalanceTrasactionList> item = new ArrayList<BalanceTrasactionList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new BalanceTrasactionList();
                                Log.d(TAG, "i: " + i);

                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Paid = jsonObj.getString("Paid");
                                    String RemainingBalance = jsonObj.getString("RemainingBalance");
                                    String PaymentType = jsonObj.getString("PaymentType");
                                    String ReceiptOwnerExecutive = jsonObj.getString("ReceiptOwnerExecutive");
                                    String ReceiptDate = jsonObj.getString("ReceiptDate");
                                    String Receipt_Id = jsonObj.getString("Receipt_Id");


                                    //  for (int j = 0; j < 5; j++) {

                                    subList.setPaid(Paid);
                                    subList.setBalance(RemainingBalance);
                                    subList.setPaymentType(PaymentType);
                                    subList.setExecutiveName(ReceiptOwnerExecutive);
                                    String rdate=Utility.formatDate(ReceiptDate);
                                    subList.setPaymentDate(rdate);
                                    subList.setID(Receipt_Id);

                                    item.add(subList);
                                    adapter = new BalanceTrasactionAdapter( item,CourseDetailsActivity.this);
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
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CourseDetailsActivity.this);
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
    // *************** Payment Mode *******************
    public void  PaymenttypeClass() {
        CourseDetailsActivity.PaymentTypeTrackClass ru = new CourseDetailsActivity.PaymentTypeTrackClass();
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
            String domainurl=SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
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

                                    paymentTypeadapter = new AddEnquirySpinnerAdapter(CourseDetailsActivity.this, paymentTypeArrayList){
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
    // ************* Call response Spinner ***************
    public void  callResponseClass() {
        CourseDetailsActivity.CallResponseTrackClass ru = new CourseDetailsActivity.CallResponseTrackClass();
        ru.execute("5");
    }
    class CallResponseTrackClass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
            // showProgressDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            // dismissProgressDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            CallResponseDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> CallResponseDetails = new HashMap<String, String>();
            CallResponseDetails.put("action", "show_call_response_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
            //CallResponseloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(CallResponseloyee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, CallResponseDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }
    private void CallResponseDetails(String jsonResponse) {


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
                        callresponce=new String[ jsonArrayCountry.length()+1];
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            callresponce[0]=getResources().getString(R.string.call_res);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                spinCallReslist = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String CallResponse     = jsonObj.getString("CallResponse");

                                    String id=jsonObj.getString("Auto_Id");
//                               if(i==0){
//                                   spinCallReslist.setName(getResources().getString(R.string.promt_country));
//                                   enqF.add(0,spinCallReslist);
//                               }
                                    spinCallReslist.setName(CallResponse);
                                    spinCallReslist.setId(id);


                                    callresponce[i+1]=CallResponse;



                                }
                            }
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                                    this,R.layout.spinner_item,callresponce ){
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
//                                    View layView = super.getView(position, convertView, parent);
//                                    layView.setPadding(0, 0, layView.getPaddingRight(), 0);
                                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                                    if(position == 0){
                                        // Set the hint text color gray
                                        tv.setTextColor(Color.GRAY);
                                        tv.setText(getResources().getString(R.string.call_res));
                                        // tv.setTextColor(Color.GRAY);
                                    }
                                    else {
                                        tv.setTextColor(Color.BLACK);
                                    }
                                    return view;
                                }
                            };

                            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                            spinCallResponce.setAdapter(spinnerArrayAdapter);
                            int spinnerPosition = spinnerArrayAdapter.getPosition(callResponce);
                            // Toast.makeText(this, "position: " + spinnerPosition, Toast.LENGTH_LONG).show();
                            spinCallResponce.setSelection(spinnerPosition);
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
    // ************* Follwup Type Spinner ***************
    public void follTypeClass() {
        CourseDetailsActivity.FollTypeTrackClass ru = new CourseDetailsActivity.FollTypeTrackClass();
        ru.execute("5");
    }
    class FollTypeTrackClass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
            // showProgressDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            // dismissProgressDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            FollTypeDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> FollTypeDetails = new HashMap<String, String>();
            FollTypeDetails.put("action", "show_master_followup_type_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
            //FollTypeloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(FollTypeloyee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, FollTypeDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }
    private void FollTypeDetails(String jsonResponse) {


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
                        folltype=new String[ jsonArrayCountry.length()+1];
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            folltype[0]=getResources().getString(R.string.hint_foll_type);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                spinFollTypeList = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Followup     = jsonObj.getString("Followup");

                                    String id=jsonObj.getString("Auto_Id");
//                               if(i==0){
//                                   spinFollTypeList.setName(getResources().getString(R.string.promt_country));
//                                   enqF.add(0,spinFollTypeList);
//                               }
                                    spinFollTypeList.setName(Followup);
                                    spinFollTypeList.setId(id);


                                    folltype[i+1]=Followup;



                                }
                            }
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                                    this,R.layout.spinner_item,folltype ){
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
//                                    View layView = super.getView(position, convertView, parent);
//                                    layView.setPadding(0, 0, layView.getPaddingRight(), 0);
                                    if(position == 0){
                                        // Set the hint text color gray
                                        tv.setTextColor(Color.GRAY);
                                        tv.setText(getResources().getString(R.string.hint_foll_type));
                                        // tv.setTextColor(Color.GRAY);
                                    }
                                    else {
                                        tv.setTextColor(Color.BLACK);
                                    }
                                    return view;
                                }
                            };

                            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                            spinFollType.setAdapter(spinnerArrayAdapter);
                            int spinnerPosition = spinnerArrayAdapter.getPosition(FollowupType);
                            // Toast.makeText(this, "position: " + spinnerPosition, Toast.LENGTH_LONG).show();
                            spinFollType.setSelection(spinnerPosition);
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
    // ************* take followup ************************
    private void takefollowupclass() {
        CourseDetailsActivity.TakeFollowupTrackclass ru = new CourseDetailsActivity.TakeFollowupTrackclass();
        ru.execute("5");
    }
    class TakeFollowupTrackclass extends AsyncTask<String, Void, String> {

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
          //  dismissProgressDialog();
            viewDialog.hideDialog();
            //Toast.makeText(CandiateListView.this, response, Toast.LENGTH_LONG).show();
            //  Toast.makeText(NewCustomerActivity.this, response, Toast.LENGTH_LONG).show();
            TakeFollowupDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> TakeFollowupDetails = new HashMap<String, String>();
            TakeFollowupDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this)));
            TakeFollowupDetails.put("id",member_id);
            Log.v(TAG, String.format("doInBackground :: enquiry_id = %s", member_id));
            TakeFollowupDetails.put("comment",inputfollComment.getText().toString());
            Log.v(TAG, String.format("doInBackground :: comment = %s", inputfollComment.getText().toString()));
            TakeFollowupDetails.put("rating",Rating);
            Log.v(TAG, String.format("doInBackground :: Rating = %s", Rating));
            TakeFollowupDetails.put("call_res",callResponce);
            Log.v(TAG, String.format("doInBackground :: callResponce = %s", callResponce));
            TakeFollowupDetails.put("next_foll_date",inputNextFollowupdate.getText().toString());
            Log.v(TAG, String.format("doInBackground :: next_foll_date = %s", inputNextFollowupdate.getText().toString()));
            TakeFollowupDetails.put("exe_name",SharedPrefereneceUtil.getName(CourseDetailsActivity.this));
            TakeFollowupDetails.put("name",nameTV.getText().toString());
            TakeFollowupDetails.put("contact",contactTV.getText().toString());
            TakeFollowupDetails.put("foll_type",FollowupType);
            TakeFollowupDetails.put("invoice_id",invoice_id);
            TakeFollowupDetails.put("financial_yr",FinancialYear);
            Log.v(TAG, String.format("doInBackground :: exe_name = %s", SharedPrefereneceUtil.getName(CourseDetailsActivity.this)));
            TakeFollowupDetails.put("action", "add_other_followup");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
            String loginResult2 = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, TakeFollowupDetails);

            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult2));
            return loginResult2;
        }
    }
    private void TakeFollowupDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject jsonObjLoginResponse = null;
        try {
            jsonObjLoginResponse = new JSONObject(jsonResponse);
            String success = jsonObjLoginResponse.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                Toast.makeText(CourseDetailsActivity.this,"Followup added succesfully",Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(0, 0);
                Intent intent=new Intent(this, CourseDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("filter_array_list", filterArrayList);
                intent.putExtra("BUNDLE",bundle);
                startActivity(intent);
                overridePendingTransition(0, 0);
                moveTaskToBack(false);
                // showCustomDialog();

                //inputEmail, inputPhone,inputAdd,inputReq,inputFollowupdate;
            }

            else if (success.equalsIgnoreCase(getResources().getString(R.string.zero)))
            {
                Toast.makeText(CourseDetailsActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                // inputContact.getText().clear();
                //Toast.makeText(CourseDetailsActivity.this,"Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void  AddBalanceReceiptClass() {
        CourseDetailsActivity.AddBalanceReceiptTrackClass ru = new CourseDetailsActivity.AddBalanceReceiptTrackClass();
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
            AddBalanceReceiptDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this));
            AddBalanceReceiptDetails.put("member_id",member_id);
            AddBalanceReceiptDetails.put("name",nameTV.getText().toString());
            AddBalanceReceiptDetails.put("contact",contactTV.getText().toString());
            AddBalanceReceiptDetails.put("email",Email);
            AddBalanceReceiptDetails.put("invoice_id",invoice_id);
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
            AddBalanceReceiptDetails.put("mem_own_exe",SharedPrefereneceUtil.getName(CourseDetailsActivity.this));
            Log.v(TAG, String.format("doInBackground :: executive name= %s", SharedPrefereneceUtil.getName(CourseDetailsActivity.this)));
            AddBalanceReceiptDetails.put("financial_year",FinancialYear);
            Log.v(TAG, String.format("doInBackground :: financial_year= %s", FinancialYear));

            AddBalanceReceiptDetails.put("action", "add_balance_receipt");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
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
                Toast.makeText(CourseDetailsActivity.this,"Balance Paid succesfully",Toast.LENGTH_SHORT).show();
               // inputName.getText().clear();
                //inputContact.getText().clear();
                SendEnquirySmsClass();
                if(!Email.equals("")){
                    receiptdatalass();
                }
                // imageView.setImageResource(R.drawable.add_photo);

                // showCustomDialog();

                //inputEmail, inputPhone,inputAdd,inputReq,inputFollowupdate;
            }


            else if (success.equalsIgnoreCase(getResources().getString(R.string.one)))
            {
                Toast.makeText(CourseDetailsActivity.this,"Mobile Number Already Exits ,Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
               // inputContact.getText().clear();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void receiptdatalass() {
        CourseDetailsActivity.ReceiptDataTrackclass ru = new CourseDetailsActivity.ReceiptDataTrackclass();
        ru.execute("5");
    }
    class ReceiptDataTrackclass extends AsyncTask<String, Void, String> {

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
            ReceiptDataDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this));
            ReceiptDataDetails.put("invoice_id", invoice_id);
            Log.v(TAG, String.format("doInBackground :: receipt data invoice_id= %s", invoice_id));
            ReceiptDataDetails.put("financial_yr", FinancialYear);
            Log.v(TAG, String.format("doInBackground :: receipt data company id = %s", FinancialYear));
            Log.v(TAG, String.format("doInBackground :: receipt data company id = %s", SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this)));
            ReceiptDataDetails.put("action","show_receipt_data");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
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

                                    String Name = nameTV.getText().toString();
                                    String Member_Contact = contactTV.getText().toString();
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
                                    String Final_Balance =  jsonObj.getString("Final_Balance");
                                    if(Final_Balance.equals(".00")){
                                        Final_Balance="0.00";
                                    }
                                    String Invoice_ID = invoice_id;
//                                    String tax = Tax;
//                                    if(tax.equals(".00")){
//                                        tax="0.00";
//                                    }
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

                                    String Company_Name = SharedPrefereneceUtil.getCompanyName(CourseDetailsActivity.this)+"-"+SharedPrefereneceUtil.getSelectedBranch(CourseDetailsActivity.this);
                                    String Address = jsonObj.getString("Address");
                                    String Contact = jsonObj.getString("Contact");
                                    String TermsAndConditions = jsonObj.getString("TermsAndConditions");
                                    TermsAndConditions = TermsAndConditions.replace("\r\n", "<br />");
                                    String Logo = jsonObj.getString("Logo");
                                    String l=Logo.replaceAll("\\s+","%20");
                                    // Logo.replace(" ","%20");
                                    String domainurl=SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
                                    String imgurl=domainurl+ServiceUrls.IMAGES_URL+l;
                                    Log.d(TAG, "imgurl: " +imgurl);
                                    // String Auto_Id = jsonObj.getString("Auto_Id");
                                    //String Receipt_Id =  jsonObj.getString("Receipt_Id");;
                                   // String ReceiptDate = Utility.getCurrentDate();
                                   // String receipt_date=Utility.formatDateDB(ReceiptDate);
//                                    String Taxamount = TaxAmount;
//                                    if(Taxamount.equals(".00")){
//                                        Taxamount="0.00";
//                                    }
                                    //String Paid = inputPaid.getText().toString();
                                    //String PaymentType = paymentType;
                                   // String PaymentDetails = inputPaymentDtl.getText().toString();
                                    //String ReceiptOwnerExecutive = SharedPrefereneceUtil.getName(CourseDetailsActivity.this);
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
                                            "   position: relative;  margin-bottom:15px;\n" +
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

                                    CourseDetailsActivity.this.runOnUiThread(new Runnable() {

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

                                                                    Toast.makeText(CourseDetailsActivity.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();

                                                                }
                                                            });
                                                        } else {
                                                            Toast.makeText(CourseDetailsActivity.this, "Email was not sent.", Toast.LENGTH_LONG).show();
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
    // ******************* send sms for add enquiry **************
    public void  SendEnquirySmsClass() {
        CourseDetailsActivity.SendEnquirySmsTrackClass ru = new CourseDetailsActivity.SendEnquirySmsTrackClass();
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
           // dismissProgressDialog();
            viewDialog.hideDialog();
            SendEnquirySmsDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> EnquiryForDetails = new HashMap<String, String>();

            EnquiryForDetails.put("type","balancepaid" );
            EnquiryForDetails.put("comp_id",SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this) );
            EnquiryForDetails.put("action", "sms_for_add_enquiry");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
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
                                    CourseDetailsActivity.this.runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            //second async stared within a asynctask but on the main thread
                                            (new AsyncTask<String, String, String>() {
                                                ServerClass ruc = new ServerClass();

                                                @Override
                                                protected String doInBackground(String... params) {
                                                    String loginResult2 = ruc.SendSMS(contactTV.getText().toString(), afterEnquirySms,
                                                            SharedPrefereneceUtil.getSmsUsername(CourseDetailsActivity.this),
                                                            SharedPrefereneceUtil.getSmsPassword(CourseDetailsActivity.this),
                                                            SharedPrefereneceUtil.getSmsRoute(CourseDetailsActivity.this),
                                                            SharedPrefereneceUtil.getSmsSenderid(CourseDetailsActivity.this));
                                                    Log.v(TAG, String.format("doInBackground :: Send Sms after enquiry= %s", loginResult2));
                                                    return loginResult2;
                                                }

                                                @Override
                                                protected void onPostExecute(String response) {
                                                    super.onPostExecute(response);
                                                    Log.v(TAG, String.format("onPostExecute :: response = %s", response));
                                                    coursedetailsclass();
                                                    finish();
                                                    //startActivity(getIntent());
                                                    //overridePendingTransition(0, 0);
                                                    Intent intent=new Intent(CourseDetailsActivity.this, CourseDetailsActivity.class);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putSerializable("filter_array_list", filterArrayList);
                                                    intent.putExtra("BUNDLE",bundle);
                                                    startActivity(intent);
                                                    overridePendingTransition(0, 0);
                                                   // moveTaskToBack(false);
                                                }
                                            }).execute();

                                        }
                                    });
                                }else{
                                    coursedetailsclass();
                                    finish();
                                    startActivity(getIntent());
                                    //Intent intent=new Intent(CourseDetailsActivity.this, CourseDetailsActivity.class);
//                                    Bundle bundle = new Bundle();
//                                    bundle.putSerializable("filter_array_list", filterArrayList);
//                                    intent.putExtra("BUNDLE",bundle);
                                    //startActivity(intent);
                                    overridePendingTransition(0, 0);
                                   // moveTaskToBack(false);
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
    private void coursedetailsclass() {
        CourseDetailsActivity. CourseDetailsTrackclass ru = new CourseDetailsActivity. CourseDetailsTrackclass();
        ru.execute("5");
    }

    class  CourseDetailsTrackclass extends AsyncTask<String, Void, String> {


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
             CourseDetailsDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String>  CourseDetailsDetails = new HashMap<String, String>();
             CourseDetailsDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this));
             CourseDetailsDetails.put("member_id",member_id );
             CourseDetailsDetails.put("invoice_id",invoice_id );
             CourseDetailsDetails.put("financial_yr",FinancialYear );
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this)));
            Log.v(TAG, String.format("doInBackground :: member_id id = %s", member_id));
            String domainurl=SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
             CourseDetailsDetails.put("action","show_course_details_by_member_id");
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL,  CourseDetailsDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }

    private void  CourseDetailsDetails(String jsonResponse) {

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
                        ArrayList<BalanceTrasactionList> item = new ArrayList<BalanceTrasactionList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                filterArrayList = new CourseList();
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
                                    String Image = jsonObj.getString("Image");
                                    String Start_Date = jsonObj.getString("Start_Date");
                                    String End_Date = jsonObj.getString("End_Date");
                                    String Rate = jsonObj.getString("Rate");
                                    String Final_paid = jsonObj.getString("Final_paid");
                                    String Final_Balance = jsonObj.getString("Final_Balance");
                                    String Invoice_ID = jsonObj.getString("Invoice_ID");
                                    String Tax = jsonObj.getString("Tax");
                                    String Member_Email_ID = jsonObj.getString("Member_Email_ID");
                                    String Financial_Year = jsonObj.getString("Financial_Year");

                                    //  for (int j = 0; j < 5; j++) {
                                    filterArrayList.setName(name);
                                    String sdate=Utility.formatDate(Start_Date);
                                    String edate=Utility.formatDate(End_Date);
                                    String todate=sdate+" to "+edate;
                                    filterArrayList.setStartToEndDate(todate);
                                    filterArrayList.setContact(Contact);
//                                    String pack=Package_Name;
                                    filterArrayList.setPackageName(Package_Name);
                                    filterArrayList.setExecutiveName(ExecutiveName);
                                    filterArrayList.setTax(Tax);
                                    String dur_sess="Duration:"+Duration_Days+","+"Session:"+Session;
                                    filterArrayList.setPackageNameWithDS(dur_sess);
                                    String reg_date= Utility.formatDate(RegistrationDate);
                                    filterArrayList.setRegistrationDate(reg_date);
                                    filterArrayList.setID(Member_ID);
                                    filterArrayList.setInvoiceID(Invoice_ID);

                                    filterArrayList.setRate(Rate);
                                    // String fpaid="₹ "+Final_paid;
                                    filterArrayList.setPaid(Final_paid);
                                    if(Final_Balance.equals(".00")){
                                        Final_Balance="0.00";
                                    }
                                    //String fbalance="₹ "+Final_Balance;
                                    filterArrayList.setBalance(Final_Balance);
                                    Image.replace("\"", "");
                                    filterArrayList.setImage(Image);
                                    filterArrayList.setEmail(Member_Email_ID);
                                    filterArrayList.setFinancialYear(Financial_Year);
//                                    String domainurl= SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
//                                    String url= domainurl+ServiceUrls.IMAGES_URL + filterArrayList.getImage();
//
//                                    Glide.with(this).load(url).placeholder(R.drawable.nouser).into(imageView);
                                    contactTV.setText(Contact);
                                    nameTV.setText(name);
                                    String fpaid="₹ "+Final_paid;
                                    String ttl="₹ "+Rate;
                                    rateTV.setText(ttl);
                                    regdateTV.setText(reg_date);
                                    packagenameTV.setText(Package_Name);
                                    durationTv.setText(dur_sess);

//                                    String sdate=Utility.formatDate(Start_Date);
//                                    String edate=Utility.formatDate(End_Date);
//                                    String todate=sdate+" to "+edate;
//                                    start_to_end_dateTV.setText(todate);
                                    paidTV.setText(fpaid);
                                    executiveNameTV.setText(ExecutiveName);
                                    balanceTV.setText(Final_Balance);
                                    invoice_id=Invoice_ID;
                                    invoice_idTV.setText(invoice_id);
                                    member_id=Member_ID;
                                    Tax=Tax;
                                    Email=Member_Email_ID;


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
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CourseDetailsActivity.this);
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
    private void folldetailsclass() {
        CourseDetailsActivity.FollowupDetailsTrackclass ru = new CourseDetailsActivity. FollowupDetailsTrackclass();
        ru.execute("5");
    }

    class  FollowupDetailsTrackclass extends AsyncTask<String, Void, String> {


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
            FollowupDetailsDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String>  FollowupDetailsDetails = new HashMap<String, String>();
            FollowupDetailsDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this));
           FollowupDetailsDetails.put("member_id",member_id );
           FollowupDetailsDetails.put("foll_type",FollowupType );
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(CourseDetailsActivity.this)));
            Log.v(TAG, String.format("doInBackground :: member_id id = %s", member_id));
            Log.v(TAG, String.format("doInBackground :: foll_type = %s", FollowupType));
            String domainurl=SharedPrefereneceUtil.getDomainUrl(CourseDetailsActivity.this);
           FollowupDetailsDetails.put("action","show_other_followup_details");
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, FollowupDetailsDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }

    private void FollowupDetailsDetails(String jsonResponse) {

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
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                filterArrayList = new CourseList();
                                Log.d(TAG, "i: " + i);

                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                     NextFollowupDate = jsonObj.getString("NextFollowupDate");
                                     NextFollowupDate=Utility.formatDateDB(NextFollowupDate);
                                     String Call_Response = jsonObj.getString("Call_Response");
                                     Rating = jsonObj.getString("Rating");
                                     callResponce=Call_Response;

//


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
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CourseDetailsActivity.this);
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
        //dismissProgressDialog();
        finish();
    }
}
