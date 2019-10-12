package com.ndfitnessplus.Activity.Notification;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ndfitnessplus.Activity.EnquiryFollowupDetailsActivity;
import com.ndfitnessplus.Activity.FullImageActivity;
import com.ndfitnessplus.Activity.MainActivity;

import com.ndfitnessplus.Activity.MemberDetailsActivity;
import com.ndfitnessplus.Activity.MoreInfoActivity;
import com.ndfitnessplus.Activity.RenewActivity;
import com.ndfitnessplus.Adapter.AddEnquirySpinnerAdapter;
import com.ndfitnessplus.Model.FollowupList;
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

import static com.ndfitnessplus.Utility.HTTPRequestQueue.isOnline;

public class StaffBirthdayFollowupActivity extends AppCompatActivity {
    //setting followup popup
    EditText inputNextFollowupdate,inputfollComment;
    Spinner spinCallResponce,spinRating,spinFollType;
    private int mYear, mMonth, mDay;
    Spinner_List spinCallReslist,ratingList,spinFollTypeList;
    AddEnquirySpinnerAdapter callresponceadapter,ratingadapter;
    String callResponce="";
    String Rating="";
    String FollowupType="";
    TextView txtcallres,txtrating,txtFollType;
    String[] callresponce ;
    String[] folltype ;
    String NextFollowupDate;
    String staff_id;
    String Image="";

    public static String TAG = StaffBirthdayFollowupActivity.class.getName();
    private ProgressDialog pd;
    TextView username,mobilenumber;
    CircularImageView imageView;
    ImageButton phone,message;
    ImageView whatsapp;
    String Contact;
    String name;
    TextView Birthday,Email,BloodGroup,Gender,Designation,Budget,EnquiryFor,EnquirySource,EnquiryType,Address;
    View nodata;
    //Loading gif
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_staff_birthday_followup);
        initToolbar();
        initComponent();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.staff_details));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private  void initComponent(){
//        progressBar=findViewById(R.id.progressBar);
//        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setHasFixedSize(true);
//
        viewDialog = new ViewDialog(this);
        username=findViewById(R.id.username);
        mobilenumber=findViewById(R.id.mobilenumber);
        Birthday=findViewById(R.id.birthday);
        Email=findViewById(R.id.email);
        BloodGroup=findViewById(R.id.bloodgroup);
        Gender=findViewById(R.id.gender);
        Designation=findViewById(R.id.designation);
        Budget=findViewById(R.id.budget);
        EnquiryFor=findViewById(R.id.enq_for);
        EnquirySource=findViewById(R.id.enq_src);
        EnquiryType=findViewById(R.id.enquiry_type);
        Address=findViewById(R.id.address);
        nodata=findViewById(R.id.nodata);
        imageView=findViewById(R.id.image);
        phone=findViewById(R.id.phone_call);
        message=findViewById(R.id.message);
        whatsapp=findViewById(R.id.whatsapp);


//        adapter = new EnquiryAdapter( new ArrayList<EnquiryList>(),EnquiryFollowupDetailsActivity.this);
//        recyclerView.setAdapter(adapter);
        Intent intent = getIntent();
        if (intent != null) {
           staff_id=intent.getStringExtra("staff_id");
            FollowupType=intent.getStringExtra("followup_type");
            name=intent.getStringExtra("name");
            Contact=intent.getStringExtra("contact");
            username.setText(name);
            mobilenumber.setText(Contact);
        }


        //Toast.makeText(EnquiryFollowupDetailsActivity.this, enquiry_id+" rating :"+Rating+" call response" +callResponce, Toast.LENGTH_LONG).show();


        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:"+Contact));
                startActivity(dialIntent);
            }
        });
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StaffBirthdayFollowupActivity.this, FullImageActivity.class);
                intent.putExtra("image",Image);
                intent.putExtra("contact",Contact);
               // intent.putExtra("id",member_id);
                intent.putExtra("user","Member");
                startActivity(intent);
            }
        });
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                PackageManager pm=getPackageManager();
                try {
                    // Uri uri = Uri.parse("smsto:" + Contact);
                    Uri uri = Uri.parse("whatsapp://send?phone=+" + Contact);
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
                    Toast.makeText(StaffBirthdayFollowupActivity.this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        if (isOnline(StaffBirthdayFollowupActivity.this)) {
            followupclass();// check login details are valid or not from server
        }
        else {
            //  Toast.makeText(EnquiryFollowupDetailsActivity.this, R.string.internet_unavailable, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(StaffBirthdayFollowupActivity.this);
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

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_action_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_home) {
            Intent intent = new Intent(StaffBirthdayFollowupActivity.this, MainActivity.class);
            startActivity(intent);
        }else if(id== android.R.id.home){
            //Toast.makeText(this,"Navigation back pressed",Toast.LENGTH_SHORT).show();
            // NavUtils.navigateUpFromSameTask(this);
            finish();
        }

        return true;
    }
//    private void showFollowupDialog() {
//        final Dialog dialog = new Dialog(StaffBirthdayFollowupActivity.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
//        dialog.setContentView(R.layout.take_all_followup_popup);
//        dialog.setCancelable(true);
//
////        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
////        lp.copyFrom(dialog.getWindow().getAttributes());
////        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
////        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        spinCallResponce = (Spinner)dialog. findViewById(R.id.spinner_call_res);
//        spinRating = (Spinner)dialog. findViewById(R.id.spinner_rating);
//        spinFollType= (Spinner)dialog. findViewById(R.id.spinner_folltype);
//        txtcallres=(TextView)dialog.findViewById(R.id.txt_callres);
//        txtrating=(TextView)dialog.findViewById(R.id.txt_rating);
//        txtFollType=(TextView)dialog.findViewById(R.id.txt_folltype);
//
//        inputfollComment = (EditText)dialog. findViewById(R.id.input_enquiry_comment);
//        //final EditText veri_otp=(EditText)dialog.findViewById(R.id.et_otp);
//        inputNextFollowupdate=(EditText)dialog.findViewById(R.id.input_next_foll_date);
//
//        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        //  if(i==0){
//        SetFollowupSpinner();
//        //i++;
//        //}
//        callResponseClass();
//        follTypeClass();
//        inputNextFollowupdate.setText(NextFollowupDate);
//        inputNextFollowupdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Get Current Date
//                final Calendar c = Calendar.getInstance();
//                mYear = c.get(Calendar.YEAR);
//                mMonth = c.get(Calendar.MONTH);
//                mDay = c.get(Calendar.DAY_OF_MONTH);
//
//
//                DatePickerDialog datePickerDialog = new DatePickerDialog(StaffBirthdayFollowupActivity.this,
//                        new DatePickerDialog.OnDateSetListener() {
//
//                            @Override
//                            public void onDateSet(DatePicker view, int year,
//                                                  int monthOfYear, int dayOfMonth) {
//
//                                inputNextFollowupdate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
//                                //tiemPicker();
//                            }
//                        }, mYear, mMonth, mDay);
//                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
//                datePickerDialog.show();
//            }
//        });
//
//        ((Button) dialog.findViewById(R.id.btn_submit)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if( callResponce.equals(getResources().getString(R.string.call_res)) || Rating.equals(getResources().getString(R.string.rating))){
//                    Toast.makeText(getApplicationContext(), "Please select Call Response or Rating", Toast.LENGTH_SHORT).show();
//                }else{
//                    if(inputNextFollowupdate.getText().length()==0) {
//                        if (!(Rating.equals("Not Interested") || Rating.equals("Converted")||FollowupType.equals("Staff BirthDay"))) {
//                            Toast.makeText(getApplicationContext(), "Please select Next Followup Date", Toast.LENGTH_SHORT).show();
//                        }else{
//                            takefollowupclass();
//                            dialog.dismiss();
//                        }
//                    }else{
//                        takefollowupclass();
//                        dialog.dismiss();
//                    }
//                }
//
//                //  Toast.makeText(StaffBirthdayFollowupActivity.this, "Mobile number verified successully", Toast.LENGTH_SHORT).show();
//
//
//                //Toast.makeText(getApplicationContext(), "Subcribe Clicked", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        dialog.show();
//        //dialog.getWindow().setAttributes(lp);
//    }
//    public  void SetFollowupSpinner(){
//
//        spinCallResponce.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                int index = parent.getSelectedItemPosition();
//                if (view != null) {
//                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
//                    view.setPadding(0, 0, 0,0);
//                    tv.setTextColor(getResources().getColor(R.color.black));
//                    if(!(callResponce==null)) {
//                        if (callResponce.equals(tv.getText().toString())) {
//                            spinCallResponce.setSelection(index);
//                        }
//                        callResponce = tv.getText().toString();
//                    }
//                    if(index==0){
//                        txtcallres.setVisibility(View.VISIBLE);
//                        tv.setText(callResponce);
//                    }
//                    // ((TextView) spinEnquiryType.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
//                    // Showing selected spinner item
//                    //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//        spinFollType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                int index = parent.getSelectedItemPosition();
//                if (view != null) {
//                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
//                    view.setPadding(0, 0, 0, 0);
//                    tv.setTextColor(getResources().getColor(R.color.black));
//                    if(!(FollowupType==null)) {
//                        if (FollowupType.equals(tv.getText().toString())) {
//                            spinFollType.setSelection(index);
//                            txtFollType.setVisibility(View.VISIBLE);
//                        }
//                        FollowupType = tv.getText().toString();
//                    }
//                    if(index==0){
//                        txtFollType.setVisibility(View.VISIBLE);
//                        tv.setText(FollowupType);
//                    }
//                    if (!(FollowupType == null)) {
//                        if(FollowupType.equals("Staff BirthDay")) {
//                            //Toast.makeText(parent.getContext(), "no interetsed: ", Toast.LENGTH_LONG).show();
//                            inputNextFollowupdate.setText("");
//                            inputNextFollowupdate.setEnabled(false);
//                            inputNextFollowupdate.setKeyListener(null);
//                        } else {
//                            inputNextFollowupdate.setEnabled(true);
//                        }
//                    }
//                    // ((TextView) spinEnquiryType.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
//                    // Showing selected spinner item
//                    //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//        //Call Responce spinner adapter setting
//        final  String[] ratingarray = getResources().getStringArray(R.array.rating_array);
//        ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(
//                this,R.layout.spinner_item,ratingarray ){
//            @Override
//            public boolean isEnabled(int position){
//                if(position == 0)
//                {
//                    // Disable the first item from Spinner
//                    // First item will be use for hint
//                    return false;
//                }
//                else
//                {
//                    return true;
//                }
//            }
//            @Override
//            public View getDropDownView(int position, View convertView,
//                                        ViewGroup parent) {
//                View view = super.getDropDownView(position, convertView, parent);
//                if(view !=null){
//                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
////                    View layView = super.getView(position, convertView, parent);
////                    layView.setPadding(0, 0, layView.getPaddingRight(), 0);
//                    if(position == 0){
//                        // Set the hint text color gray
//                        tv.setTextColor(Color.GRAY);
//                        tv.setText(getResources().getString(R.string.rating));
//                        // tv.setTextColor(Color.GRAY);
//                    }
//                    else {
//                        tv.setTextColor(Color.BLACK);
//                    }
//                }
//
//                return view;
//            }
//        };
//        spinnerArrayAdapter1.setDropDownViewResource(R.layout.spinner_item);
//        spinRating.setAdapter(spinnerArrayAdapter1);
//        int spinnerPositionrating = spinnerArrayAdapter1.getPosition(Rating);
//        //Toast.makeText(this, "position: " + spinnerPositionrating, Toast.LENGTH_LONG).show();
//        spinRating.setSelection(spinnerPositionrating);
//        ArrayList<Spinner_List> ratingArrayList = new ArrayList<Spinner_List>();
////
//        spinRating.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                int index = parent.getSelectedItemPosition();
//                if (view != null) {
//                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
//                    //View layView = super.getView(position, convertView, parent);
//                    view.setPadding(0, 0, 0, 0);
//                    tv.setTextColor(getResources().getColor(R.color.black));
//                    if (!(Rating == null)) {
//                        if (Rating.equals(tv.getText().toString())) {
//                            spinRating.setSelection(index);
//                            txtrating.setVisibility(View.VISIBLE);
//                        }
//                        Rating = tv.getText().toString();
//                    }
////                    if (!(Rating == null)) {
////                        if (Rating.equals("Not Interested") || Rating.equals("Converted")) {
////                            //Toast.makeText(parent.getContext(), "no interetsed: ", Toast.LENGTH_LONG).show();
////                            inputNextFollowupdate.setText("");
////                            inputNextFollowupdate.setEnabled(false);
////                            inputNextFollowupdate.setKeyListener(null);
////                        } else {
////                            inputNextFollowupdate.setEnabled(true);
////                        }
////                    }
//                    if(index==0){
//                        txtrating.setVisibility(View.VISIBLE);
//                        tv.setText(Rating);
//                    }
//                }
//
//                // ((TextView) spinEnquiryType.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
//                // Showing selected spinner item
//                //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//    }
private void showCustomDialog() {
    final Dialog dialog = new Dialog(StaffBirthdayFollowupActivity.this);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
    dialog.setContentView(R.layout.take_followup_popup);
    dialog.setCancelable(true);

//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(dialog.getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
    spinCallResponce = (Spinner)dialog. findViewById(R.id.spinner_call_res);
    spinRating = (Spinner)dialog. findViewById(R.id.spinner_rating);
    txtcallres=(TextView)dialog.findViewById(R.id.txt_callres);
    txtrating=(TextView)dialog.findViewById(R.id.txt_rating);

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
    SetSpinner();
    //i++;
    //}
    callResponseClass();
    String curr_date = Utility.getCurrentDate();
    inputNextFollowupdate.setText(curr_date);

    inputNextFollowupdate.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(StaffBirthdayFollowupActivity.this,
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
            if( callResponce.equals(getResources().getString(R.string.call_res)) || Rating.equals(getResources().getString(R.string.rating)) ){
                Toast.makeText(getApplicationContext(), "Please select Call Response or Rating", Toast.LENGTH_SHORT).show();
            }else{
                if(inputfollComment.getText().length()==0){
                    Toast.makeText(getApplicationContext(), "Please enter Comment", Toast.LENGTH_SHORT).show();
                }else{
                    if(inputNextFollowupdate.getText().length()==0) {
                        if (!(Rating.equals("Not Interested") || Rating.equals("Converted"))) {
                            Toast.makeText(getApplicationContext(), "Please select Next Followup Date " , Toast.LENGTH_SHORT).show();
                        }else{
                            takefollowupclass();
                            dialog.dismiss();
                        }
                    }else{
                        takefollowupclass();
                        dialog.dismiss();
                    }
                }
            }

            //  Toast.makeText(EnquiryFollowupDetailsActivity.this, "Mobile number verified successully", Toast.LENGTH_SHORT).show();


            //Toast.makeText(getApplicationContext(), "Subcribe Clicked", Toast.LENGTH_SHORT).show();
        }
    });

    dialog.show();
    //dialog.getWindow().setAttributes(lp);
}
    public  void SetSpinner(){

        spinCallResponce.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    view.setPadding(0, 0, 0, 0);
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
        pd = new ProgressDialog(StaffBirthdayFollowupActivity.this);
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
    // ************* Call response Spinner ***************
    public void  callResponseClass() {
        StaffBirthdayFollowupActivity.CallResponseTrackClass ru = new StaffBirthdayFollowupActivity.CallResponseTrackClass();
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
            String domainurl=SharedPrefereneceUtil.getDomainUrl(StaffBirthdayFollowupActivity.this);
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
        StaffBirthdayFollowupActivity.FollTypeTrackClass ru = new StaffBirthdayFollowupActivity.FollTypeTrackClass();
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
            String domainurl=SharedPrefereneceUtil.getDomainUrl(StaffBirthdayFollowupActivity.this);
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
        StaffBirthdayFollowupActivity.TakeFollowupTrackclass ru = new StaffBirthdayFollowupActivity.TakeFollowupTrackclass();
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
           // dismissProgressDialog();
            viewDialog.hideDialog();
            //Toast.makeText(CandiateListView.this, response, Toast.LENGTH_LONG).show();
            //  Toast.makeText(NewCustomerActivity.this, response, Toast.LENGTH_LONG).show();
            TakeFollowupDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> TakeFollowupDetails = new HashMap<String, String>();
            TakeFollowupDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(StaffBirthdayFollowupActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(StaffBirthdayFollowupActivity.this)));
            TakeFollowupDetails.put("id",staff_id);
           // Log.v(TAG, String.format("doInBackground :: enquiry_id = %s", member_id));
            TakeFollowupDetails.put("comment",inputfollComment.getText().toString());
            Log.v(TAG, String.format("doInBackground :: comment = %s", inputfollComment.getText().toString()));
            TakeFollowupDetails.put("rating",Rating);
            Log.v(TAG, String.format("doInBackground :: Rating = %s", Rating));
            TakeFollowupDetails.put("call_res",callResponce);
            Log.v(TAG, String.format("doInBackground :: callResponce = %s", callResponce));
            TakeFollowupDetails.put("next_foll_date",inputNextFollowupdate.getText().toString());
            Log.v(TAG, String.format("doInBackground :: next_foll_date = %s", inputNextFollowupdate.getText().toString()));
            TakeFollowupDetails.put("exe_name",SharedPrefereneceUtil.getName(StaffBirthdayFollowupActivity.this));
            TakeFollowupDetails.put("name",name);
            Log.v(TAG, String.format("doInBackground :: Name = %s", name));
            TakeFollowupDetails.put("contact",Contact);
            Log.v(TAG, String.format("doInBackground :: Contact = %s", Contact));
            TakeFollowupDetails.put("foll_type","Staff Birthday");
            Log.v(TAG, String.format("doInBackground :: Folltype = %s", FollowupType));
            TakeFollowupDetails.put("invoice_id","");
            //Log.v(TAG, String.format("doInBackground :: invoice_id = %s", invoice_id));
            TakeFollowupDetails.put("financial_yr","");
            //Log.v(TAG, String.format("doInBackground :: FinancialYear = %s", FinancialYear));
            Log.v(TAG, String.format("doInBackground :: exe_name = %s", SharedPrefereneceUtil.getName(StaffBirthdayFollowupActivity.this)));
            TakeFollowupDetails.put("action", "add_other_followup");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(StaffBirthdayFollowupActivity.this);
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
                Toast.makeText(StaffBirthdayFollowupActivity.this,"Followup added succesfully",Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(0, 0);
                Intent intent=new Intent(this, StaffBirthdayFollowupActivity.class);
                intent.putExtra("staff_id",staff_id);
                intent.putExtra("followup_type","Staff BirthDay");
                intent.putExtra("name",name);
                intent.putExtra("contact",Contact);
                startActivity(intent);
                overridePendingTransition(0, 0);
                moveTaskToBack(false);
                // showCustomDialog();

                //inputEmail, inputPhone,inputAdd,inputReq,inputFollowupdate;
            }

            else if (success.equalsIgnoreCase(getResources().getString(R.string.zero)))
            {
                Toast.makeText(StaffBirthdayFollowupActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                // inputContact.getText().clear();
                //Toast.makeText(StaffBirthdayFollowupActivity.this,"Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    //*********** Asycc class for loading data for database **************
    private void followupclass() {
        StaffBirthdayFollowupActivity.FollowupTrackclass ru = new StaffBirthdayFollowupActivity.FollowupTrackclass();
        ru.execute("5");
    }

    class FollowupTrackclass extends AsyncTask<String, Void, String> {

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
            FollowupDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> FollowupDetails = new HashMap<String, String>();
            FollowupDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(StaffBirthdayFollowupActivity.this));
            FollowupDetails.put("staff_id",staff_id );
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(StaffBirthdayFollowupActivity.this)));
            FollowupDetails.put("action","show_staff_details");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(StaffBirthdayFollowupActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, FollowupDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }

    private void FollowupDetails(String jsonResponse) {

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

                                    String name = jsonObj.getString("Name");
                                    String address = jsonObj.getString("Address");
                                    String Contact = jsonObj.getString("Contact");
                                    String email = jsonObj.getString("Email");
                                    String blood_Group = jsonObj.getString("B_Group");
                                    String designation = jsonObj.getString("Designation");
                                    String dOB = jsonObj.getString("DOB");
                                    String gender = jsonObj.getString("Gender");
                                    String Image = jsonObj.getString("Image");



                                    String foll_date= Utility.formatDate(dOB);

                                    username.setText(name);
                                    mobilenumber.setText(Contact);
                                    Image.replace("\"", "");
                                    String domainurl= SharedPrefereneceUtil.getDomainUrl(StaffBirthdayFollowupActivity.this);
                                    String url= domainurl+ServiceUrls.IMAGES_URL + Image;

                                   // Glide.with(this).load(url).placeholder(R.drawable.nouser).into(imageView);
                                    RequestOptions requestOptions = new RequestOptions();
                                    requestOptions.placeholder(R.drawable.nouser);
                                    requestOptions.error(R.drawable.nouser);


                                    Glide.with(this)
                                            .setDefaultRequestOptions(requestOptions)
                                            .load(url).into(imageView);
                                    Email.setText(email);
                                    Gender.setText(gender);
                                    Address.setText(address);
                                    Log.d(TAG, "converted DOB: " + foll_date);
                                    Birthday.setText(foll_date);
                                    BloodGroup.setText(blood_Group);
                                    Designation.setText(designation);

                                }
                            }
                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    nodata.setVisibility(View.VISIBLE);

                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(StaffBirthdayFollowupActivity.this);
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

}
