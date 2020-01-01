package com.ndfitnessplus.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ndfitnessplus.Activity.Notification.EnquiryFollowupActivity;
import com.ndfitnessplus.Activity.Notification.TodaysEnrollmentActivity;
import com.ndfitnessplus.Adapter.AddEnquirySpinnerAdapter;
import com.ndfitnessplus.Adapter.EnquiryAdapter;
import com.ndfitnessplus.Adapter.FollowupAdapter;
import com.ndfitnessplus.Adapter.FollowupDetailsAdapter;
import com.ndfitnessplus.Model.EnquiryList;
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

import static com.ndfitnessplus.Activity.EnquiryActivity.TAG;
import static com.ndfitnessplus.Utility.HTTPRequestQueue.isOnline;

public class EnquiryFollowupDetailsActivity extends AppCompatActivity {
    FollowupDetailsAdapter adapter;
    ArrayList<FollowupList> subListArrayList = new ArrayList<FollowupList>();
    FollowupList subList;
    SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    ProgressBar progressBar;
    String enquiry_id;
    String Image;
    View nodata;

    //setting popup
    EditText inputNextFollowupdate,inputComment;
    Spinner spinCallResponce,spinRating;
    private int mYear, mMonth, mDay;
    int mHour;
    int mMinute;
    Spinner_List spinCallReslist,ratingList;


    AddEnquirySpinnerAdapter callresponceadapter,ratingadapter;
    String callResponce="";
    String Rating="";
    TextView txtcallres,txtrating;
    String[] callresponce ;
    public static String TAG = EnquiryFollowupDetailsActivity.class.getName();
    private ProgressDialog pd;
    TextView username,mobilenumber;
    CircularImageView imageView;
    ImageButton phone;
    ImageView whatsapp,message;
    String Contact;
    //Loading gif
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_enquiry_followup_details);
        initToolbar();
        initComponent();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.enquiry_followup_details));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private  void initComponent(){
        progressBar=findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
       // FloatingActionButton take_followup=findViewById(R.id.fab);
        viewDialog = new ViewDialog(this);

        nodata=findViewById(R.id.nodata);
        username=findViewById(R.id.username);
        mobilenumber=findViewById(R.id.mobilenumber);
        imageView=findViewById(R.id.image);
        phone=findViewById(R.id.phone_call);
        message=findViewById(R.id.message);
        whatsapp=findViewById(R.id.whatsapp);
//        adapter = new EnquiryAdapter( new ArrayList<EnquiryList>(),EnquiryFollowupDetailsActivity.this);
//        recyclerView.setAdapter(adapter);
        Intent intent = getIntent();
       // Bundle args = intent.getBundleExtra("BUNDLE");
        if (intent != null) {
            enquiry_id=intent.getStringExtra("enquiry_id");
            Rating=intent.getStringExtra("rating");
            callResponce=intent.getStringExtra("call_response");

        }
        //Toast.makeText(EnquiryFollowupDetailsActivity.this, enquiry_id+" rating :"+Rating+" call response" +callResponce, Toast.LENGTH_LONG).show();

        if (isOnline(EnquiryFollowupDetailsActivity.this)) {
            followupclass();// check login details are valid or not from server
        }
        else {
          //  Toast.makeText(EnquiryFollowupDetailsActivity.this, R.string.internet_unavailable, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EnquiryFollowupDetailsActivity.this);
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
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnquiryFollowupDetailsActivity.this, FullImageActivity.class);
                intent.putExtra("image",Image);
                intent.putExtra("contact",Contact);
                intent.putExtra("id",enquiry_id);
                intent.putExtra("user","Enquiry");
                startActivity(intent);
            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:"+Contact));
                startActivity(dialIntent);
            }
        });
//        message.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Uri smsUri = Uri.parse("tel:"+Contact);
////                Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
////                intent.putExtra("sms_body", "Hi welcome to ndfitness");
////                intent.setType("vnd.android-dir/mms-sms");
////                startActivity(intent);
//
//                String defaultSmsPackage = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
//                        ? Telephony.Sms.getDefaultSmsPackage(EnquiryFollowupDetailsActivity.this)
//                        : Settings.Secure.getString(getContentResolver(), "sms_default_application");
//
//                Intent smsIntent = getPackageManager().getLaunchIntentForPackage(defaultSmsPackage);
//
//                if (smsIntent == null) {
//                    Uri smsUri = Uri.parse("smsto:"+Contact);
//                     smsIntent = new Intent(Intent.ACTION_SENDTO,smsUri);
//// Invokes only SMS/MMS clients
//                    smsIntent.setType("vnd.android-dir/mms-sms");
//// Specify the Phone Number
//                    smsIntent.putExtra("address", Contact);
//// Specify the Message
//                    smsIntent.putExtra("sms_body", "Hi welcome to Ndfitness");
//
//// Shoot!
//                    //startActivity(smsIntent);
//                    //smsIntent = new Intent(Intent.ACTION_SENDTO,smsUri);
//                    //smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
//                   // smsIntent.putExtra("sms_body", "Hi welcome to ndfitness");
//                   // smsIntent.setType("vnd.android-dir/mms-sms");
//                }
//
//                try {
//                    startActivity(smsIntent);
//                } catch (Exception e) {
//                    Log.w(TAG, "Could not open SMS app", e);
//
//                    // Inform user
//                    Toast.makeText(EnquiryFollowupDetailsActivity.this,
//                            "Your SMS app could not be opened. Please open it manually.",
//                            Toast.LENGTH_LONG
//                    ).show();
//                }
//            }
//        });
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
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
                  //  PackageInfo info1=pm.getPackageInfo("com.whatsapp.w4b", PackageManager.GET_META_DATA);
                    //Check if package exists or not. If not then code
                    //in catch block will be called
                    //Log.e(TAG, String.format("package Name  :: = %s", info1));
                    waIntent.setPackage("com.whatsapp");
                   // waIntent.setPackage("com.whatsapp.w4b");

                   // waIntent.putExtra(Intent.EXTRA_TEXT, text);
                    startActivity(waIntent);

                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(TAG, String.format("Name Not found execeptiaon  :: = %s", ""));
                    e.printStackTrace();
                    Toast.makeText(EnquiryFollowupDetailsActivity.this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
//        take_followup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showCustomDialog();
//            }
//        });
    }
    //Popup dialog
    //int i=0;
    private void showCustomDialog() {
        final Dialog dialog = new Dialog(EnquiryFollowupDetailsActivity.this);
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

        inputComment = (EditText)dialog. findViewById(R.id.input_enquiry_comment);
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


                DatePickerDialog datePickerDialog = new DatePickerDialog(EnquiryFollowupDetailsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                inputNextFollowupdate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                               // tiemPicker();
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
                  if(inputComment.getText().length()==0){
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
    private void tiemPicker(){
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        mHour = hourOfDay;
                        mMinute = minute;
                        //nextfolltime=hourOfDay + ":" + minute;
                        inputNextFollowupdate.setText(inputNextFollowupdate.getText().toString()+" "+hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.more_info_and_home, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_home) {
            Intent intent = new Intent(EnquiryFollowupDetailsActivity.this, MainActivity.class);
            startActivity(intent);
        }else if(id ==R.id.action_more_info){
            Intent intent = new Intent(EnquiryFollowupDetailsActivity.this, MoreInfoActivity.class);
            intent.putExtra("enquiry_id",enquiry_id);
            startActivity(intent);
        }else if(id== android.R.id.home){
        //Toast.makeText(this,"Navigation back pressed",Toast.LENGTH_SHORT).show();
        // NavUtils.navigateUpFromSameTask(this);
           finish();
    }

        return true;
    }
    private void showProgressDialog() {
        Log.v(TAG, String.format("showProgressDialog"));
        pd = new ProgressDialog(EnquiryFollowupDetailsActivity.this);
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
        EnquiryFollowupDetailsActivity.FollowupTrackclass ru = new EnquiryFollowupDetailsActivity.FollowupTrackclass();
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
            FollowupDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(EnquiryFollowupDetailsActivity.this));
            FollowupDetails.put("enquiry_id",enquiry_id );
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(EnquiryFollowupDetailsActivity.this)));
            Log.v(TAG, String.format("doInBackground :: enquiry_id = %s", enquiry_id));
            FollowupDetails.put("action","show_enquiry_followup_details");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(EnquiryFollowupDetailsActivity.this);
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
                    progressBar.setVisibility(View.GONE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }
                        int count=0;
                        ArrayList<FollowupList> item = new ArrayList<FollowupList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                            if(jsonArrayResult.length()<10){
                                count=jsonArrayResult.length();
                            }else{
                                count=10;
                            }
                            for (int i = 0; i < count; i++) {


                                subList = new FollowupList();
                                Log.d(TAG, "i: " + i);

                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String name = jsonObj.getString("Name");
                                    String Rating = jsonObj.getString("Rating");
                                     Contact = jsonObj.getString("Contact");
                                    String CallResponse = jsonObj.getString("CallResponse");
                                    String ExecutiveName = jsonObj.getString("ExecutiveName");
                                    String Comment = jsonObj.getString("Comment");
                                    String FollowupType = jsonObj.getString("FollowupType");
                                    String NextFollowup_Date = jsonObj.getString("NextFollowupDate");
                                    String Auto_Id = jsonObj.getString("Enquiry_ID");
                                    String Followup_Date = jsonObj.getString("FollowupDate");
                                     Image = jsonObj.getString("Image");
                                    //  for (int j = 0; j < 5; j++) {
                                    Log.d(TAG, "next followup date: " + NextFollowup_Date);
                                    Log.d(TAG, "Followup date: " + Followup_Date);

                                    //  for (int j = 0; j < 5; j++) {

                                    subList.setName(name);
                                    subList.setRating(Rating);
                                    subList.setContact(Contact);
                                    subList.setCallRespond(CallResponse);
                                    subList.setExecutiveName(ExecutiveName);
                                   // String cmt="Comment: "+Comment;
                                    subList.setComment(Comment);
                                    subList.setFollowupType(FollowupType);
                                    String next_foll_date= Utility.formatDate(NextFollowup_Date);
                                    Log.d(TAG, "converted next followup date: " + next_foll_date);
                                    subList.setNextFollowupDate(next_foll_date);
                                    String foll_date= Utility.formatDate(Followup_Date);

                                    username.setText(name);
                                    mobilenumber.setText(Contact);
                                    Image.replace("\"", "");
                                    String domainurl= SharedPrefereneceUtil.getDomainUrl(EnquiryFollowupDetailsActivity.this);
                                    String url= domainurl+ServiceUrls.IMAGES_URL + Image;
                                    Log.d(TAG, "image url: "+url );
                                   // Glide.with(this).load(url).placeholder(R.drawable.nouser).into(imageView);
                                    RequestOptions requestOptions = new RequestOptions();
                                    requestOptions.placeholder(R.drawable.nouser);
                                    requestOptions.error(R.drawable.nouser);


                                    Glide.with(this)
                                            .setDefaultRequestOptions(requestOptions)
                                            .load(url).into(imageView);
                                    Log.d(TAG, "converted Followup date: " + foll_date);
                                    subList.setFollowupDate(foll_date);
                                    subList.setID(Auto_Id);
                                    item.add(subList);
                                    adapter = new FollowupDetailsAdapter( item,EnquiryFollowupDetailsActivity.this);
                                    recyclerView.setAdapter(adapter);

                                }
                            }
                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    nodata.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EnquiryFollowupDetailsActivity.this);
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

    // ************* take followup ************************
    private void takefollowupclass() {
        EnquiryFollowupDetailsActivity.TakeFollowupTrackclass ru = new EnquiryFollowupDetailsActivity.TakeFollowupTrackclass();
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
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> TakeFollowupDetails = new HashMap<String, String>();
            TakeFollowupDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(EnquiryFollowupDetailsActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(EnquiryFollowupDetailsActivity.this)));
            TakeFollowupDetails.put("enquiry_id",enquiry_id);
            Log.v(TAG, String.format("doInBackground :: enquiry_id = %s", enquiry_id));
            TakeFollowupDetails.put("comment",inputComment.getText().toString());
            Log.v(TAG, String.format("doInBackground :: comment = %s", inputComment.getText().toString()));
            TakeFollowupDetails.put("rating",Rating);
            Log.v(TAG, String.format("doInBackground :: Rating = %s", Rating));
            TakeFollowupDetails.put("call_res",callResponce);
            Log.v(TAG, String.format("doInBackground :: callResponce = %s", callResponce));
            TakeFollowupDetails.put("next_foll_date",inputNextFollowupdate.getText().toString());
            Log.v(TAG, String.format("doInBackground :: next_foll_date = %s", inputNextFollowupdate.getText().toString()));
            TakeFollowupDetails.put("exe_name",SharedPrefereneceUtil.getName(EnquiryFollowupDetailsActivity.this));
            Log.v(TAG, String.format("doInBackground :: exe_name = %s", SharedPrefereneceUtil.getName(EnquiryFollowupDetailsActivity.this)));
            TakeFollowupDetails.put("action", "update_enquiry_followup");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(EnquiryFollowupDetailsActivity.this);
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
                Toast.makeText(EnquiryFollowupDetailsActivity.this,"Followup added succesfully",Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(0, 0);
                Intent intent=new Intent(this, EnquiryFollowupDetailsActivity.class);
                intent.putExtra("enquiry_id",enquiry_id);
                intent.putExtra("rating",Rating);
                intent.putExtra("call_response",callResponce);
                startActivity(intent);
                overridePendingTransition(0, 0);
                moveTaskToBack(false);
                // showCustomDialog();

                //inputEmail, inputPhone,inputAdd,inputReq,inputFollowupdate;
            }

            else if (success.equalsIgnoreCase(getResources().getString(R.string.zero)))
            {
                Toast.makeText(EnquiryFollowupDetailsActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
               // inputContact.getText().clear();
                //Toast.makeText(EnquiryFollowupDetailsActivity.this,"Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // ************* Call response Spinner ***************
    public void  callResponseClass() {
        EnquiryFollowupDetailsActivity.CallResponseTrackClass ru = new EnquiryFollowupDetailsActivity.CallResponseTrackClass();
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
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> CallResponseDetails = new HashMap<String, String>();
            CallResponseDetails.put("action", "show_call_response_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(EnquiryFollowupDetailsActivity.this);
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
                            callresponce[0]=getResources().getString(R.string.na);
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
    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
       // swipeRefresh.setRefreshing(false);
//        Intent intent=new Intent(EnquiryFollowupDetailsActivity.this,EnquiryFollowupDetailsActivity.class);
//        intent.putExtra("enquiry_id",enquiry_id);
//        intent.putExtra("rating",Rating);
//        intent.putExtra("call_response",callResponce);
//        startActivity(intent);
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
