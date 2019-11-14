package com.ndfitnessplus.Activity;

import android.app.Activity;
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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ndfitnessplus.Activity.Notification.TodaysEnrollmentActivity;
import com.ndfitnessplus.Adapter.AddEnquirySpinnerAdapter;
import com.ndfitnessplus.Adapter.FollowupDetailsAdapter;
import com.ndfitnessplus.Adapter.MemberDetailsAdapter;
import com.ndfitnessplus.Model.CourseList;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.ndfitnessplus.Utility.HTTPRequestQueue.isOnline;

public class MemberDetailsActivity extends AppCompatActivity {
    MemberDetailsAdapter adapter;
    ArrayList<CourseList> subListArrayList = new ArrayList<CourseList>();
    CourseList subList;
    MemberDataList filterArrayList;
    SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    ProgressBar progressBar;
    String member_id="";
    String invoice_id ="";
    String Email="";
    String FinancialYear="";
    View nodata;

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

    String Image="";
    String Before_Photo="";
    String After_Photo="";

    public static String TAG = MemberDetailsActivity.class.getName();
    private ProgressDialog pd;
    TextView username,mobilenumber;
    CircularImageView imageView;
    ImageButton phone;
    ImageView whatsapp,message;
    String Contact="";
    String name;
    Button  renew,followup,before_after;
    //Loading gif
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
//                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_member_details);
        initToolbar();
        initComponent();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.member_details));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private  void initComponent(){
        progressBar=findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        nodata=findViewById(R.id.nodata);
        username=findViewById(R.id.username);
        mobilenumber=findViewById(R.id.mobilenumber);
        imageView=findViewById(R.id.image);
        phone=findViewById(R.id.phone_call);
        message=findViewById(R.id.message);
        whatsapp=findViewById(R.id.whatsapp);
        viewDialog = new ViewDialog(this);
        renew=findViewById(R.id.btn_renew);
        followup=findViewById(R.id.btn_followup);
        before_after=findViewById(R.id.btn_before_after);
//        adapter = new EnquiryAdapter( new ArrayList<EnquiryList>(),EnquiryFollowupDetailsActivity.this);
//        recyclerView.setAdapter(adapter);
        Intent intent = getIntent();
         Bundle args = intent.getBundleExtra("BUNDLE");
        if (args != null) {
            filterArrayList = (MemberDataList) args.getSerializable("filter_array_list");

            Contact=filterArrayList.getContact();
//                Log.v(TAG, String.format("Selected  ::contact= %s", cont));
//                Log.v(TAG, String.format("Selected  ::name= %s", filterArrayList.getName()));
            name=filterArrayList.getName();
            member_id=filterArrayList.getID();
            Email=filterArrayList.getEmail();
            FollowupType=filterArrayList.getFollowupType();
            if(FollowupType== null){
                FollowupType="";
            }
            Image=filterArrayList.getImage();
            Image.replace("\"", "");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(MemberDetailsActivity.this);
            String url= domainurl+ServiceUrls.IMAGES_URL + Image;
            username.setText(name);
            mobilenumber.setText(Contact);
           // Glide.with(this).load(url).placeholder(R.drawable.nouser).into(imageView);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.nouser);
            requestOptions.error(R.drawable.nouser);


            Glide.with(this)
                    .setDefaultRequestOptions(requestOptions)
                    .load(url).into(imageView);
            folldetailsclass();
            memberdetailsclass();
        }

        renew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MemberDetailsActivity.this,RenewActivity.class);
                intent.putExtra("member_id",member_id);
                intent.putExtra("name",name);
                intent.putExtra("contact",Contact);
                intent.putExtra("email",Email);
                startActivity(intent);
            }
        });
        //Toast.makeText(EnquiryFollowupDetailsActivity.this, enquiry_id+" rating :"+Rating+" call response" +callResponce, Toast.LENGTH_LONG).show();

        if (isOnline(MemberDetailsActivity.this)) {
            followupclass();// check login details are valid or not from server
        }
        else {
            //  Toast.makeText(EnquiryFollowupDetailsActivity.this, R.string.internet_unavailable, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MemberDetailsActivity.this);
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
                showFollowupDialog();
            }
        });
        followup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFollowupDialog();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MemberDetailsActivity.this, FullImageActivity.class);
                intent.putExtra("image",Image);
                intent.putExtra("contact",Contact);
                intent.putExtra("id",member_id);
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
                    Uri uri = Uri.parse("whatsapp://send?phone=+91" + Contact);
                    Intent waIntent = new Intent(Intent.ACTION_VIEW,uri);
                    //waIntent.setType("text/plain");
                    String text = "YOUR TEXT HERE";

                    PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);

                    waIntent.setPackage("com.whatsapp");

                    // waIntent.putExtra(Intent.EXTRA_TEXT, text);
                    startActivity(waIntent);

                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(MemberDetailsActivity.this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        before_after.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MemberDetailsActivity.this,MemberBeforeAfterActivity.class);
                intent.putExtra("member_id",member_id);
                intent.putExtra("name",name);
                intent.putExtra("contact",Contact);
                intent.putExtra("image",Image);
                intent.putExtra("After_Photo",After_Photo);
                intent.putExtra("Before_Photo",Before_Photo);
                startActivity(intent);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.member_info_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_home) {
            Intent intent = new Intent(MemberDetailsActivity.this, MainActivity.class);
            startActivity(intent);
        }else if(id== android.R.id.home){
          finish();
        }else if(id== R.id.action_measurement) {
            Intent intent = new Intent(MemberDetailsActivity.this, MemberMeasurementActivity.class);
            intent.putExtra("member_id",member_id);
            intent.putExtra("contact",Contact);
            startActivity(intent);
        }else if(id== R.id.action_diet){
            Intent intent = new Intent(MemberDetailsActivity.this, MemberDietActivity.class);
            intent.putExtra("member_id",member_id);
            startActivity(intent);
//            finish();
        }else if(id== R.id.action_workout){
                Intent intent = new Intent(MemberDetailsActivity.this, MemberWorkoutActivity.class);
                intent.putExtra("member_id",member_id);
                startActivity(intent);
        }

        return true;
    }
    private void showFollowupDialog() {
        final Dialog dialog = new Dialog(MemberDetailsActivity.this);
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
     //   inputNextFollowupdate.setText(NextFollowupDate);
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


                DatePickerDialog datePickerDialog = new DatePickerDialog(MemberDetailsActivity.this,
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
                if( callResponce.equals(getResources().getString(R.string.call_res)) || Rating.equals(getResources().getString(R.string.rating))||FollowupType.equals(getResources().getString(R.string.hint_foll_type))){
                    Toast.makeText(getApplicationContext(), "Please select Call Response or Rating or Follwup Type", Toast.LENGTH_SHORT).show();
                }else{
                    if(inputNextFollowupdate.getText().length()==0) {
                        if (!(Rating.equals("Not Interested") || Rating.equals("Converted")||FollowupType.equals("Member BirthDay"))) {
                            Toast.makeText(getApplicationContext(), "Please select Next Followup Date", Toast.LENGTH_SHORT).show();
                        }else{
                            if(inputfollComment.getText().length()>0) {
                                takefollowupclass();
                                dialog.dismiss();
                            }else{
                                Toast.makeText(getApplicationContext(), "Please enter comment" , Toast.LENGTH_SHORT).show();

                            }
                        }
                    }else{
                        ///takefollowupclass();
                        if(inputfollComment.getText().length()>0) {
                            takefollowupclass();
                            dialog.dismiss();
                        }else{
                            Toast.makeText(getApplicationContext(), "Please enter comment" , Toast.LENGTH_SHORT).show();

                        }
                    }
                }

                //  Toast.makeText(MemberDetailsActivity.this, "Mobile number verified successully", Toast.LENGTH_SHORT).show();


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
                        if(FollowupType.equals("Member BirthDay")) {
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
                        if(!FollowupType.equals("Member BirthDay")){
                            if (Rating.equals("Not Interested") || Rating.equals("Converted")) {
                                //Toast.makeText(parent.getContext(), "no interetsed: ", Toast.LENGTH_LONG).show();
                                inputNextFollowupdate.setText("");
                                inputNextFollowupdate.setEnabled(false);
                                inputNextFollowupdate.setKeyListener(null);
                            } else {
                                inputNextFollowupdate.setEnabled(true);
                            }
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
        pd = new ProgressDialog(MemberDetailsActivity.this);
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
    private void followupclass() {
        MemberDetailsActivity.FollowupTrackclass ru = new MemberDetailsActivity.FollowupTrackclass();
        ru.execute("5");
    }

    class FollowupTrackclass extends AsyncTask<String, Void, String> {

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
            //  dismissProgressDialog();
            viewDialog.hideDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            FollowupDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> FollowupDetails = new HashMap<String, String>();
            FollowupDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(MemberDetailsActivity.this));
            FollowupDetails.put("member_id",member_id );
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(MemberDetailsActivity.this)));
            Log.v(TAG, String.format("doInBackground :: member_id id = %s", member_id));
            FollowupDetails.put("action","show_member_details");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(MemberDetailsActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, FollowupDetails);
            Log.v(TAG, String.format("doInBackground :: show_member_details= %s", loginResult));
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
                    progressBar.setVisibility(View.GONE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }
                        ArrayList<CourseList> item = new ArrayList<CourseList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new CourseList();
                                Log.d(TAG, "i: " + i);

                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String name = jsonObj.getString("Name");
                                    String RegistrationDate = jsonObj.getString("RegistrationDate");
                                    Contact = jsonObj.getString("Contact");
                                    String Package_Name = jsonObj.getString("Package_Name");
                                    String ExecutiveName = jsonObj.getString("ExecutiveName");
                                    String Duration_Days = jsonObj.getString("Duration_Days");
                                    String Session = jsonObj.getString("Session");
                                    String Member_ID = jsonObj.getString("Member_ID");
                                    String Start_Date = jsonObj.getString("Start_Date");
                                    String End_Date = jsonObj.getString("End_Date");
                                    String Rate = jsonObj.getString("Rate");
                                    String Final_paid = jsonObj.getString("Final_paid");
                                    String Final_Balance = jsonObj.getString("Final_Balance");
                                    String Email = jsonObj.getString("Email");
                                    String Invoice_ID = jsonObj.getString("Invoice_ID");
                                    String Tax = jsonObj.getString("Tax");
                                    String Financial_Year = jsonObj.getString("Financial_Year");


                                    //  for (int j = 0; j < 5; j++) {

                                    subList.setName(name);
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

                                    subList.setContact(Contact);

                                    String pack=Package_Name+"(Duration:"+Duration_Days+","+"Session:"+Session+")";
                                    subList.setPackageNameWithDS(pack);
                                    subList.setPackageName(Package_Name);
                                    subList.setExecutiveName(ExecutiveName);

                                    String reg_date= Utility.formatDate(RegistrationDate);
                                    subList.setRegistrationDate(reg_date);
                                    subList.setID(Member_ID);

                                    subList.setRate(Rate);
                                    subList.setPaid(Final_paid);
                                    if(Final_Balance.equals(".00")){
                                        Final_Balance="0.00";
                                    }
                                    subList.setBalance(Final_Balance);
                                    subList.setEmail(Email);
                                    subList.setInvoiceID(Invoice_ID);
                                    subList.setTax(Tax);
                                    subList.setFinancialYear(Financial_Year);
                                    mobilenumber.setText(Contact);
                                    invoice_id=Invoice_ID;
                                    FinancialYear=Financial_Year;
                                    item.add(subList);
                                    adapter = new MemberDetailsAdapter( item,MemberDetailsActivity.this);
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
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MemberDetailsActivity.this);
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
    // ************* Call response Spinner ***************
    public void  callResponseClass() {
        MemberDetailsActivity.CallResponseTrackClass ru = new MemberDetailsActivity.CallResponseTrackClass();
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
           // Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> CallResponseDetails = new HashMap<String, String>();
            CallResponseDetails.put("action", "show_call_response_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(MemberDetailsActivity.this);
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
        MemberDetailsActivity.FollTypeTrackClass ru = new MemberDetailsActivity.FollTypeTrackClass();
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
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> FollTypeDetails = new HashMap<String, String>();
            FollTypeDetails.put("action", "show_master_followup_type_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(MemberDetailsActivity.this);
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
        MemberDetailsActivity.TakeFollowupTrackclass ru = new MemberDetailsActivity.TakeFollowupTrackclass();
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
           // Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> TakeFollowupDetails = new HashMap<String, String>();
            TakeFollowupDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(MemberDetailsActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(MemberDetailsActivity.this)));
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
            TakeFollowupDetails.put("exe_name",SharedPrefereneceUtil.getName(MemberDetailsActivity.this));
            TakeFollowupDetails.put("name",name);
            Log.v(TAG, String.format("doInBackground :: Name = %s", name));
            TakeFollowupDetails.put("contact",Contact);
            Log.v(TAG, String.format("doInBackground :: Contact = %s", Contact));
            TakeFollowupDetails.put("foll_type",FollowupType);
            Log.v(TAG, String.format("doInBackground :: Folltype = %s", FollowupType));
            TakeFollowupDetails.put("invoice_id",invoice_id);
            Log.v(TAG, String.format("doInBackground :: invoice_id = %s", invoice_id));
            TakeFollowupDetails.put("financial_yr",FinancialYear);
            Log.v(TAG, String.format("doInBackground :: FinancialYear = %s", FinancialYear));
            Log.v(TAG, String.format("doInBackground :: exe_name = %s", SharedPrefereneceUtil.getName(MemberDetailsActivity.this)));
            TakeFollowupDetails.put("action", "add_other_followup");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(MemberDetailsActivity.this);
            String loginResult2 = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, TakeFollowupDetails);

            Log.v(TAG, String.format("doInBackground :: add_other_followup= %s", loginResult2));
            return loginResult2;
        }
    }
    private void TakeFollowupDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject jsonObjLoginResponse = null;
        try {
            jsonObjLoginResponse = new JSONObject(jsonResponse);
            String success = jsonObjLoginResponse.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))||success.equalsIgnoreCase(getResources().getString(R.string.one))) {
                Toast.makeText(MemberDetailsActivity.this,"Followup added succesfully",Toast.LENGTH_LONG).show();
                //finish();
                overridePendingTransition(0, 0);
                Intent intent=new Intent(this, MemberDetailsActivity.class);
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
                Toast.makeText(MemberDetailsActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                // inputContact.getText().clear();
                //Toast.makeText(MemberDetailsActivity.this,"Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void folldetailsclass() {
        MemberDetailsActivity.FollowupDetailsTrackclass ru = new MemberDetailsActivity. FollowupDetailsTrackclass();
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
            FollowupDetailsDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(MemberDetailsActivity.this));
            FollowupDetailsDetails.put("member_id",member_id );
            FollowupDetailsDetails.put("foll_type",FollowupType );
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(MemberDetailsActivity.this)));
            Log.v(TAG, String.format("doInBackground :: member_id = %s", member_id));
            Log.v(TAG, String.format("doInBackground ::FollowupType = %s", FollowupType));
            String domainurl=SharedPrefereneceUtil.getDomainUrl(MemberDetailsActivity.this);
            FollowupDetailsDetails.put("action","show_other_followup_details");
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, FollowupDetailsDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }

    private void FollowupDetailsDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: show_other_followup_details = %s", jsonResponse));
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
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MemberDetailsActivity.this);
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
    private void memberdetailsclass() {
        MemberDetailsActivity.MemberDetailsTrackclass ru = new MemberDetailsActivity. MemberDetailsTrackclass();
        ru.execute("5");
    }

    class  MemberDetailsTrackclass extends AsyncTask<String, Void, String> {


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
            MemberDetailsDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> MemberDetailsDetails = new HashMap<String, String>();
            MemberDetailsDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(MemberDetailsActivity.this));
            MemberDetailsDetails.put("member_id",member_id );
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(MemberDetailsActivity.this)));
            Log.v(TAG, String.format("doInBackground :: member_id = %s", member_id));
            String domainurl=SharedPrefereneceUtil.getDomainUrl(MemberDetailsActivity.this);
            MemberDetailsDetails.put("action","show_before_after_photo_by_member_id");
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, MemberDetailsDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }

    private void MemberDetailsDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: show_before_after_photo_by_member_id = %s", jsonResponse));
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
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    After_Photo = jsonObj.getString("After_Photo");
                                    Before_Photo = jsonObj.getString("Before_Photo");

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
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MemberDetailsActivity.this);
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

    protected void onRestart() {
        super.onRestart();
        finish();
        Intent intent=new Intent(MemberDetailsActivity.this,MemberDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("filter_array_list", filterArrayList);
        intent.putExtra("BUNDLE",bundle);
        startActivity(intent);
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
