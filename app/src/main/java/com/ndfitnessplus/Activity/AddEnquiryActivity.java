package com.ndfitnessplus.Activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ndfitnessplus.Adapter.AddEnquirySpinnerAdapter;
import com.ndfitnessplus.Model.Spinner_List;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.NetworkUtils;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddEnquiryActivity extends AppCompatActivity  {

    private EditText inputName,inputContact, inputAdd, inputComment,
            inputEmail,inputLocation,inputNextFollowupdate,inputBudget,inputDOB;
    private TextInputLayout inputLayoutName,inputLayoutContact, inputLayoutAdd,inputLayoutComment,
        inputLayoutEmail,inputLayoutLocation,inputLayoutNextFollowupDate,inputLayoutBudget,inputLayoutDOB;
    private Button register,close,btn_verify;
    //CircularImageView imageView;
    FloatingActionButton imageView;
    public final String TAG = AddEnquiryActivity.class.getName();
    private ProgressDialog pd;
    private AwesomeValidation awesomeValidation;
    private int mYear, mMonth, mDay;
    int mHour;
    int mMinute;
    int mSec;
     String nextfolltime;
    RadioButton genderradioButton;
    RadioGroup radioGroup;
    //Camera
    String imgPath, fileName;
            String gender="";
    public  static final int RequestPermissionCode  = 1 ;
    static final int REQUEST_IMAGE_CAPTURE = 1;
  ;
    // Bitmap bitmap;
    String ConvertImage="";
    ImageView CapturedImage;
    private Uri uri;
    private Uri filePath;
    Bitmap bitmap;
    public int otp;

    //Spinner Adapter
    public Spinner spinEnquiryType,spinEnquirySource,spinEnqFor,spinCallResponce,spinRating,spinOccupation;
    Spinner_List enquirytypelist,enquirySourcelist,enqForList,spinCallReslist,ratingList,occupationList;
    ArrayList<Spinner_List> enquiryTypeArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> enquirySourceArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> enqForArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> CallResArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> ratingArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> occupationArraylist = new ArrayList<Spinner_List>();
    public AddEnquirySpinnerAdapter enquirytypeadapter,enquirySourceadapter,enqforadapter,callresadapter,ratingadapter,occupationAdapter;
    String enquiryType,enquirySource,callResponce,Rating,enquiryFor,occupation;
    TextView txtEnqtype,txtEnqfor,txtEnqsrc,txtcallres,txtrating,txtoccupation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_enquiry);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.add_enquiry));
       // getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
  private void initComponent(){
      inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
      inputLayoutContact = (TextInputLayout) findViewById(R.id.input_layout_cont);
      inputLayoutAdd = (TextInputLayout) findViewById(R.id.input_layout_add);
      inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
      inputLayoutLocation = (TextInputLayout) findViewById(R.id.input_layout_location);

      inputLayoutComment = (TextInputLayout) findViewById(R.id.input_layout_enquiry_comment);
      inputLayoutDOB = (TextInputLayout) findViewById(R.id.input_layout_dob);

      inputLayoutNextFollowupDate = (TextInputLayout) findViewById(R.id.input_layout_next_foll_date);
      inputLayoutBudget = (TextInputLayout) findViewById(R.id.input_layout_budget);

      imageView=findViewById(R.id.input_image);
      CapturedImage=findViewById(R.id.captured_image);
      radioGroup=(RadioGroup)findViewById(R.id.radioGroup);
      inputName = (EditText) findViewById(R.id.input_name);
      inputContact = (EditText) findViewById(R.id.input_cont);
      inputAdd = (EditText) findViewById(R.id.input_add);
      inputEmail = (EditText) findViewById(R.id.input_email);
      inputLocation = (EditText) findViewById(R.id.input_location);
      inputDOB = (EditText) findViewById(R.id.input_dob);

      inputComment = (EditText) findViewById(R.id.input_enquiry_comment);
      inputNextFollowupdate=(EditText)findViewById(R.id.input_next_foll_date);
      inputBudget=(EditText)findViewById(R.id.input_budget);

     //spinners
      spinEnquiryType = (Spinner) findViewById(R.id.spinner_enq_type);
      spinEnquirySource = (Spinner) findViewById(R.id.spinner_enq_source);
      spinCallResponce = (Spinner) findViewById(R.id.spinner_call_res);
      spinRating = (Spinner) findViewById(R.id.spinner_rating);
      spinEnqFor = (Spinner) findViewById(R.id.spinner_enq_for);
      spinOccupation = (Spinner) findViewById(R.id.spinner_occupation);

      txtEnqtype=findViewById(R.id.txt_enqtype);
      txtEnqsrc=findViewById(R.id.txt_enqsrc);
      txtEnqfor=findViewById(R.id.txt_enqfor);
      txtcallres=findViewById(R.id.txt_callres);
      txtrating=findViewById(R.id.txt_rating);
      txtoccupation=findViewById(R.id.txt_occupation);


      //defining AwesomeValidation object
      awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

      awesomeValidation.addValidation(this, R.id.input_name, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.err_msg_name);
      awesomeValidation.addValidation(this, R.id.input_cont, "^[1-9]{1}[0-9]{9}$", R.string.err_msg_cont);
      awesomeValidation.addValidation(this, R.id.input_enquiry_comment, RegexTemplate.NOT_EMPTY, R.string.err_msg_comment);
      awesomeValidation.addValidation(this, R.id.input_next_foll_date,RegexTemplate.NOT_EMPTY, R.string.err_msg_next_foll_date);
     // awesomeValidation.addValidation(this, R.id.input_budget,RegexTemplate.NOT_EMPTY, R.string.err_msg_budget);

      //api's for spinners
      occupationClass();
      enqforClass();
      enqtypeClass();
      enqsourceClass();
      callResponseClass();

      inputContact.addTextChangedListener(new TextWatcher() {

          public void onTextChanged(CharSequence s, int start, int before,
                                    int count) {
          }



          public void beforeTextChanged(CharSequence s, int start, int count,
                                        int after) {

          }

          public void afterTextChanged(Editable s) {
              if(inputContact.getText().length()==10) {
                  //do your work here
                 // Toast.makeText(AddEnquiryActivity.this ,"Text vhanged count  is 10 then: " , Toast.LENGTH_LONG).show();
                  CheckContactClass();
              }
          }
      });

     //**************setting data to the spinners******************

      spinEnquiryType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

              int index = parent.getSelectedItemPosition();
              TextView tv = (TextView) view.findViewById(R.id.tv_Name);
              View layout=(View)view.findViewById(R.id.layout);
              layout.setPadding(0,0,0,0);
              if(index ==0){
                  tv.setTextColor((Color.GRAY));
              }else{
                  tv.setTextColor((Color.BLACK));
              }
                enquiryType = tv.getText().toString();
              if(index!=0){
                 txtEnqtype.setVisibility(View.VISIBLE);
              }
              if(enquiryType.equals(getResources().getString(R.string.enquiry_type))){
                  //awesomeValidation.addValidation(AddEnquiryActivity.this, R.id.spinner_enq_type,RegexTemplate.NOT_EMPTY, R.string.err_msg_next_foll_date);
              }
             // ((TextView) spinEnquiryType.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
              // Showing selected spinner item
              //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {
              Toast.makeText(parent.getContext(), "Please Select Enquiry Type ", Toast.LENGTH_LONG).show();
          }
      });

      //*************Enquiry source spinner adapter setting ****************

      spinEnquirySource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

              int index = parent.getSelectedItemPosition();
              TextView tv = (TextView) view.findViewById(R.id.tv_Name);
              View layout=(View)view.findViewById(R.id.layout);
              layout.setPadding(0,0,0,0);
              if(index ==0){
                  tv.setTextColor((Color.GRAY));
              }else{
                  tv.setTextColor((Color.BLACK));
              }
              enquirySource = tv.getText().toString();
              if(index!=0){
                  txtEnqsrc.setVisibility(View.VISIBLE);
              }
              if(enquirySource.equals(getResources().getString(R.string.enquiry_source))){
                  //awesomeValidation.addValidation(AddEnquiryActivity.this, R.id.spinner_enq_source,RegexTemplate.NOT_EMPTY, R.string.err_msg_next_foll_date);
              }
              // ((TextView) spinEnquiryType.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
              // Showing selected spinner item
              //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {
              Toast.makeText(parent.getContext(), "Please Select Enquiry Source ", Toast.LENGTH_LONG).show();
          }
      });


      //Toast.makeText(MainActivity.this,genderradioButton.getText(), Toast.LENGTH_SHORT).show();
      spinCallResponce.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

              int index = parent.getSelectedItemPosition();
              TextView tv = (TextView) view.findViewById(R.id.tv_Name);
              View layout=(View)view.findViewById(R.id.layout);
              layout.setPadding(0,0,0,0);
              if(index ==0){
                  tv.setTextColor((Color.GRAY));
              }else{
                  tv.setTextColor((Color.BLACK));
              }
              callResponce = tv.getText().toString();
              if(index!=0){
                  txtcallres.setVisibility(View.VISIBLE);
              }
              if(callResponce.equals(getResources().getString(R.string.call_res))){
                //  awesomeValidation.addValidation(AddEnquiryActivity.this, R.id.spinner_call_res,RegexTemplate.NOT_EMPTY, R.string.err_msg_next_foll_date);
              }
              // ((TextView) spinEnquiryType.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
              // Showing selected spinner item
              //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {
              Toast.makeText(parent.getContext(), "Please Select Call Response ", Toast.LENGTH_LONG).show();
          }
      });
      // ********************Call Responce spinner adapter setting**************
      final  String[] ratingarray = getResources().getStringArray(R.array.rating_array);

      for(int i=0;i<ratingarray.length;i++) {
          ratingList = new Spinner_List();
          ratingList.setName(ratingarray[i]);
          ratingArrayList.add(ratingList);
          ratingadapter = new AddEnquirySpinnerAdapter(AddEnquiryActivity.this, ratingArrayList) {
              @Override
              public boolean isEnabled(int position) {
                  if (position == 0) {
                      // Disable the first item from Spinner
                      // First item will be use for hint
                      return false;
                  } else {
                      return true;
                  }
              }

              @Override
              public View getDropDownView(int position, View convertView,
                                          ViewGroup parent) {
                  View view = super.getDropDownView(position, convertView, parent);
                  TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                  if (position == 0) {
                      // Set the hint text color gray
                      tv.setTextColor(Color.GRAY);
                      tv.setText(getResources().getString(R.string.rating));
                      // tv.setTextColor(Color.GRAY);
                  } else {
                      tv.setTextColor(Color.BLACK);
                  }
                  return view;
              }

          };
          spinRating.setAdapter(ratingadapter);
      }
      //Toast.makeText(MainActivity.this,genderradioButton.getText(), Toast.LENGTH_SHORT).show();
      spinRating.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

              int index = parent.getSelectedItemPosition();
              TextView tv = (TextView) view.findViewById(R.id.tv_Name);
              View layout=(View)view.findViewById(R.id.layout);
              layout.setPadding(0,0,0,0);

              if(index ==0){
                  tv.setTextColor((Color.GRAY));
              }else{
                  tv.setTextColor((Color.BLACK));
              }
              Rating = tv.getText().toString();
              if(index!=0){
                  txtrating.setVisibility(View.VISIBLE);
              }
              if(Rating.equals(getResources().getString(R.string.rating))){
                 // Toast.makeText(parent.getContext(), "Please Select Rating ", Toast.LENGTH_LONG).show();
                  //awesomeValidation.addValidation(AddEnquiryActivity.this, R.id.spinner_rating,RegexTemplate.NOT_EMPTY, R.string.err_msg_next_foll_date);
              }
              // ((TextView) spinEnquiryType.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
              // Showing selected spinner item
              //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {
              Toast.makeText(parent.getContext(), "Please Select Rating ", Toast.LENGTH_LONG).show();
          }
      });
      spinEnqFor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

              int index = parent.getSelectedItemPosition();
              TextView tv = (TextView) view.findViewById(R.id.tv_Name);
              View layout=(View)view.findViewById(R.id.layout);
              layout.setPadding(0,0,0,0);
              if(index ==0){
                  tv.setTextColor((Color.GRAY));
              }else{
                  tv.setTextColor((Color.BLACK));
              }
              enquiryFor = tv.getText().toString();
              if(index!=0){
                  txtEnqfor.setVisibility(View.VISIBLE);
              }
              if(!enquiryFor.equals(getResources().getString(R.string.enq_for))){
                //  awesomeValidation.addValidation(AddEnquiryActivity.this, R.id.spinner_enq_for,RegexTemplate.NOT_EMPTY, R.string.err_msg_next_foll_date);
              }
              // ((TextView) spinEnquiryType.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
              // Showing selected spinner item
              //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {
              //Toast.makeText(parent.getContext(), "Please Select Enquiry For ", Toast.LENGTH_LONG).show();
          }
      });
      //*************** Occupation listner **************
      spinOccupation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

              int index = parent.getSelectedItemPosition();
              TextView tv = (TextView) view.findViewById(R.id.tv_Name);
              View layout=(View)view.findViewById(R.id.layout);
              layout.setPadding(0,0,0,0);
              if(index ==0){
                  tv.setTextColor((Color.GRAY));
              }else{
                  tv.setTextColor((Color.BLACK));
              }
              occupation = tv.getText().toString();
              if(index!=0){
                  txtoccupation.setVisibility(View.VISIBLE);
              }
              if(!occupation.equals(getResources().getString(R.string.occupation))){
                  //awesomeValidation.addValidation(AddEnquiryActivity.this, R.id.spinner_occupation,RegexTemplate.NOT_EMPTY, R.string.err_msg_next_foll_date);
              }
              // ((TextView) spinEnquiryType.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
              // Showing selected spinner item
              //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {
             // Toast.makeText(parent.getContext(), "Please Select Occupation ", Toast.LENGTH_LONG).show();
          }
      });

      inputNextFollowupdate.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              // Get Current Date
              final Calendar c = Calendar.getInstance();
              mYear = c.get(Calendar.YEAR);
              mMonth = c.get(Calendar.MONTH);
              mDay = c.get(Calendar.DAY_OF_MONTH);
                awesomeValidation.clear();


              DatePickerDialog datePickerDialog = new DatePickerDialog(AddEnquiryActivity.this,
                      new DatePickerDialog.OnDateSetListener() {

                          @Override
                          public void onDateSet(DatePicker view, int year,
                                                int monthOfYear, int dayOfMonth) {

                              inputNextFollowupdate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                              tiemPicker();
                          }
                      }, mYear, mMonth, mDay);
              datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
              datePickerDialog.show();
          }
      });
      inputDOB.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              // Get Current Date
              final Calendar c = Calendar.getInstance();
              mYear = c.get(Calendar.YEAR);
              mMonth = c.get(Calendar.MONTH);
              mDay = c.get(Calendar.DAY_OF_MONTH);


              DatePickerDialog datePickerDialog = new DatePickerDialog(AddEnquiryActivity.this,
                      new DatePickerDialog.OnDateSetListener() {

                          @Override
                          public void onDateSet(DatePicker view, int year,
                                                int monthOfYear, int dayOfMonth) {

                              inputDOB.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                          }
                      }, mYear, mMonth, mDay);

              datePickerDialog.show();
          }
      });
//      register=(Button)findViewById(R.id.btn_register);
//      close=(Button)findViewById(R.id.btn_close);
      btn_verify=(Button)findViewById(R.id.btn_verify);
//      close.setOnClickListener(new View.OnClickListener() {
//          @Override
//          public void onClick(View view) {
//              finish();
//          }
//      });

      btn_verify.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
             SensSMSClass();
               //SendSMS(inputContact.getText().toString(),msg);
          }
      });
     // register.setOnClickListener(this);
      EnableRuntimePermissionToAccessCamera();
      imageView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
              startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
          }
      });

  }
  //*********** time picker **********
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
             if(radioGroup.getCheckedRadioButtonId()!=-1){
                 int id1= radioGroup.getCheckedRadioButtonId();
                 View radioButton = radioGroup.findViewById(id1);
                 int radioId = radioGroup.indexOfChild(radioButton);
                 RadioButton btn = (RadioButton) radioGroup.getChildAt(radioId);
                 gender= (String) btn.getText();
               //  Toast.makeText(AddEnquiryActivity.this, gender, Toast.LENGTH_SHORT).show();
             }
             submitForm();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // *********** camera permission ***********
    public void EnableRuntimePermissionToAccessCamera(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(AddEnquiryActivity.this,
                Manifest.permission.CAMERA))
        {

            // Printing toast message after enabling runtime permission.
            //Toast.makeText(AddEnquiryActivity.this,"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(AddEnquiryActivity.this,new String[]{Manifest.permission.CAMERA}, RequestPermissionCode);

        }
    }
    @Override
    protected void onActivityResult(int RC, int RQC, Intent I) {

        super.onActivityResult(RC, RQC, I);
           if (RC == REQUEST_IMAGE_CAPTURE ) {


//            uri = I.getData();
//            Log.v(TAG, String.format("camera request "));
//            Log.v(TAG, String.format("camera capture :: uri= %s", uri));
//            try {
             if(I !=null){
            Bundle extras = I.getExtras();
            if (extras != null) {
                bitmap = (Bitmap) extras.get("data");
                CapturedImage.setImageBitmap(bitmap);
                CapturedImage.setVisibility(View.VISIBLE);

                uploadimageClass();
            }
             }
//                imgPreview.setImageBitmap(imageBitmap);
            //  bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//               Toast.makeText(UploadImageActivity.this,bitmap.toString(),Toast.LENGTH_SHORT).show();

//            } catch (IOException e) {
//
//                e.printStackTrace();
//            }
        }
    }
    //*************** Add enquiry to database *************
    public void  AddEnquiryClass() {
        AddEnquiryActivity.AddEnquiryTrackClass ru = new AddEnquiryActivity.AddEnquiryTrackClass();
        ru.execute("5");
    }
    private void showProgressDialog() {
        Log.v(TAG, String.format("showProgressDialog"));
        pd = new ProgressDialog(AddEnquiryActivity.this);
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



    class AddEnquiryTrackClass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
             showProgressDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            dismissProgressDialog();
            //Toast.makeText(CandiateListView.this, response, Toast.LENGTH_LONG).show();
            //  Toast.makeText(NewCustomerActivity.this, response, Toast.LENGTH_LONG).show();
            AddEnquiryDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> AddEnquiryDetails = new HashMap<String, String>();
            AddEnquiryDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(AddEnquiryActivity.this));
            AddEnquiryDetails.put("name",inputName.getText().toString());
            AddEnquiryDetails.put("contact",inputContact.getText().toString());
            AddEnquiryDetails.put("add",inputAdd.getText().toString());
            AddEnquiryDetails.put("email",inputEmail.getText().toString());
            AddEnquiryDetails.put("location",inputLocation.getText().toString());
            AddEnquiryDetails.put("gender", gender);
            AddEnquiryDetails.put("occupation",occupation);
            AddEnquiryDetails.put("enq_type",enquiryType);
            AddEnquiryDetails.put("enq_source",enquirySource);
            AddEnquiryDetails.put("enq_for",enquiryFor);
            AddEnquiryDetails.put("comment",inputComment.getText().toString());
            AddEnquiryDetails.put("foll_type","Enquiry");
            AddEnquiryDetails.put("rating",Rating);
            AddEnquiryDetails.put("call_res",callResponce);
            AddEnquiryDetails.put("nextFoll_date",inputNextFollowupdate.getText().toString());
            AddEnquiryDetails.put("dob",inputDOB.getText().toString());
            AddEnquiryDetails.put("budget",inputBudget.getText().toString());
            AddEnquiryDetails.put("image_path",ConvertImage);
            AddEnquiryDetails.put("exe_name",SharedPrefereneceUtil.getUserNm(AddEnquiryActivity.this));
            Log.v(TAG, String.format("doInBackground :: executive name= %s", SharedPrefereneceUtil.getUserNm(AddEnquiryActivity.this)));
            AddEnquiryDetails.put("action", "add_enquiry");
            String loginResult2 = ruc.sendPostRequest(ServiceUrls.LOGIN_URL, AddEnquiryDetails);

            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult2));
            return loginResult2;
        }
    }


    private void AddEnquiryDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject jsonObjLoginResponse = null;
        try {
            jsonObjLoginResponse = new JSONObject(jsonResponse);
            String success = jsonObjLoginResponse.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                Toast.makeText(AddEnquiryActivity.this,"Enquiry added succesfully",Toast.LENGTH_SHORT).show();
                inputName.getText().clear();
                inputContact.getText().clear();
                inputComment.getText().clear();
                inputNextFollowupdate.getText().clear();
                inputBudget.getText().clear();
                imageView.setImageResource(R.drawable.add_photo);
                Intent intent=new Intent(AddEnquiryActivity.this,EnquiryActivity.class);
                startActivity(intent);
               // showCustomDialog();

                //inputEmail, inputPhone,inputAdd,inputReq,inputFollowupdate;
            }


            else if (success.equalsIgnoreCase(getResources().getString(R.string.one)))
            {
                Toast.makeText(AddEnquiryActivity.this,"Mobile Number Already Exits",Toast.LENGTH_SHORT).show();
                inputContact.getText().clear();
                Toast.makeText(AddEnquiryActivity.this,"Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
 // *********** upload image to server ************
    private void uploadimageClass() {

        ByteArrayOutputStream byteArrayOutputStreamObject ;

        byteArrayOutputStreamObject = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

           ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);
        Log.v(TAG, String.format(" ConvertImage= %s", ConvertImage));

    }

    private void submitForm() {
        //first validate the form then move ahead
        //if this becomes true that means validation is successfull
        //if(inputPassword.getText().toString().equals(inputCfmPassword.getText().toString())){
        if(inputEmail.length()>0){
            awesomeValidation.addValidation(this, R.id.input_email, Patterns.EMAIL_ADDRESS, R.string.err_msg_email);
        if (awesomeValidation.validate()) {
                    if(enquiryType.equals(getResources().getString(R.string.enquiry_type)) || enquirySource.equals(getResources().getString(R.string.enquiry_source))
                            || callResponce.equals(getResources().getString(R.string.call_res)) || Rating.equals(getResources().getString(R.string.rating))
                            ||enquiryFor.equals(getResources().getString(R.string.enq_for)) ||occupation.equals(getResources().getString(R.string.occupation)) ){
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_LONG).show();
                    }else{
                        AddEnquiryClass();
                    }
                }


//            if(enquiryType.equals(getResources().getString(R.string.enquiry_type)) || enquirySource.equals(getResources().getString(R.string.enquiry_source))
//                    || callResponce.equals(getResources().getString(R.string.call_res)) || Rating.equals(getResources().getString(R.string.rating))
//            ||enquiryFor.equals(getResources().getString(R.string.enq_for)) ||occupation.equals(getResources().getString(R.string.occupation)) ){
//                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_LONG).show();
//            }else{
//                AddEnquiryClass();
//            }
            // Toast.makeText(this, "Validation Successfull", Toast.LENGTH_LONG).show();

           // uploadimageClass();
            //process the data further
        }else{
            if (awesomeValidation.validate()) {
                if(enquiryType.equals(getResources().getString(R.string.enquiry_type)) || enquirySource.equals(getResources().getString(R.string.enquiry_source))
                        || callResponce.equals(getResources().getString(R.string.call_res)) || Rating.equals(getResources().getString(R.string.rating))
                        ||enquiryFor.equals(getResources().getString(R.string.enq_for)) ||occupation.equals(getResources().getString(R.string.occupation)) ){
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_LONG).show();
                }else{
                    AddEnquiryClass();
                }
            }else{
                Toast.makeText(this, "Validation Failed", Toast.LENGTH_LONG).show();
            }

            // awesomeValidation.addValidation(this, R.id.input_cfn_password,RegexTemplate.NOT_EMPTY,R.string.err_msg_cfm_password);

        }

    }
     // ************* enquiry  for spinner *******************
    public void  enqforClass() {
        AddEnquiryActivity.EnquiryForTrackClass ru = new AddEnquiryActivity.EnquiryForTrackClass();
        ru.execute("5");
    }
    class EnquiryForTrackClass extends AsyncTask<String, Void, String> {

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
            EnquiryForDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> EnquiryForDetails = new HashMap<String, String>();
            EnquiryForDetails.put("action", "show_enq_for_list");
            //EnquiryForloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(EnquiryForloyee.this));
            String loginResult = ruc.sendPostRequest(ServiceUrls.LOGIN_URL, EnquiryForDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void EnquiryForDetails(String jsonResponse) {


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
                        enqForArrayList.clear();
                        enqForList = new Spinner_List();
                        enqForList.setName(getResources().getString(R.string.enq_for));
                        enqForArrayList.add(0,enqForList);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                enqForList = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Enquiry_For     = jsonObj.getString("Enquiry_For");

                                    String id=jsonObj.getString("Auto_Id");
//                               if(i==0){
//                                   enqForList.setName(getResources().getString(R.string.promt_country));
//                                   enqF.add(0,enqForList);
//                               }
                                    enqForList.setName(Enquiry_For);
                                    enqForList.setId(id);

                                    enqForArrayList.add(enqForList);

                                    enqforadapter = new AddEnquirySpinnerAdapter(AddEnquiryActivity.this, enqForArrayList){
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
                                                tv.setText(getResources().getString(R.string.prompt_enq_for));
                                                // tv.setTextColor(Color.GRAY);
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinEnqFor.setAdapter(enqforadapter);


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

    // *********************** Enquiry type spinner **************************
    public void  enqtypeClass() {
        AddEnquiryActivity.EnquiryTypeTrackClass ru = new AddEnquiryActivity.EnquiryTypeTrackClass();
        ru.execute("5");
    }
    class EnquiryTypeTrackClass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
            showProgressDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            dismissProgressDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            EnquiryTypeDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> EnquiryTypeDetails = new HashMap<String, String>();
            EnquiryTypeDetails.put("action", "show_enq_type_list");
            //EnquiryTypeloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(EnquiryTypeloyee.this));
            String loginResult = ruc.sendPostRequest(ServiceUrls.LOGIN_URL, EnquiryTypeDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void EnquiryTypeDetails(String jsonResponse) {


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
                        enquiryTypeArrayList.clear();
                        enquirytypelist = new Spinner_List();
                        enquirytypelist.setName(getResources().getString(R.string.enquiry_type));
                        enquiryTypeArrayList.add(0,enquirytypelist);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                enquirytypelist = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Enquiry_Type     = jsonObj.getString("Enquiry_type");

                                    String id=jsonObj.getString("Auto_Id");
//                               if(i==0){
//                                   enquirytypelist.setName(getResources().getString(R.string.promt_country));
//                                   enqF.add(0,enquirytypelist);
//                               }
                                    enquirytypelist.setName(Enquiry_Type);
                                    enquirytypelist.setId(id);

                                    enquiryTypeArrayList.add(enquirytypelist);

                                    enquirytypeadapter = new AddEnquirySpinnerAdapter(AddEnquiryActivity.this, enquiryTypeArrayList){
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
                                                tv.setText(getResources().getString(R.string.prompt_enquiry_type));
                                                // tv.setTextColor(Color.GRAY);
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinEnquiryType.setAdapter(enquirytypeadapter);


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
    // *********************** Enquiry Source spinner **************************
    public void  enqsourceClass() {
        AddEnquiryActivity.EnquirySourceTrackClass ru = new AddEnquiryActivity.EnquirySourceTrackClass();
        ru.execute("5");
    }

    class EnquirySourceTrackClass extends AsyncTask<String, Void, String> {

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
            EnquirySourceDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> EnquirySourceDetails = new HashMap<String, String>();
            EnquirySourceDetails.put("action", "show_enq_source_list");
            //EnquirySourceloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(EnquirySourceloyee.this));
            String loginResult = ruc.sendPostRequest(ServiceUrls.LOGIN_URL, EnquirySourceDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void EnquirySourceDetails(String jsonResponse) {


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
                        enquirySourceArrayList.clear();
                        enquirySourcelist = new Spinner_List();
                        enquirySourcelist.setName(getResources().getString(R.string.enquiry_source));
                        enquirySourceArrayList.add(0,enquirySourcelist);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                enquirySourcelist = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String SourceOf_Enquiry     = jsonObj.getString("SourceOf_Enquiry");

                                    String id=jsonObj.getString("Auto_Id");
//                               if(i==0){
//                                   enquirySourcelist.setName(getResources().getString(R.string.promt_country));
//                                   enqF.add(0,enquirySourcelist);
//                               }
                                    enquirySourcelist.setName(SourceOf_Enquiry);
                                    enquirySourcelist.setId(id);

                                    enquirySourceArrayList.add(enquirySourcelist);

                                    enquirySourceadapter = new AddEnquirySpinnerAdapter(AddEnquiryActivity.this, enquirySourceArrayList){
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
                                                tv.setText(getResources().getString(R.string.prompt_enquiry_source));
                                                // tv.setTextColor(Color.GRAY);
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinEnquirySource.setAdapter(enquirySourceadapter);


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
    // *********************** Occupation spinner **************************
    public void  occupationClass() {
        AddEnquiryActivity.OccupationTrackClass ru = new AddEnquiryActivity.OccupationTrackClass();
        ru.execute("5");
    }
    class OccupationTrackClass extends AsyncTask<String, Void, String> {

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
            OccupationDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> OccupationDetails = new HashMap<String, String>();
            OccupationDetails.put("action", "show_occupation_list");
            //OccupationloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(Occupationloyee.this));
            String loginResult = ruc.sendPostRequest(ServiceUrls.LOGIN_URL, OccupationDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }
    private void OccupationDetails(String jsonResponse) {


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
                        occupationArraylist.clear();
                        occupationList = new Spinner_List();
                        occupationList.setName(getResources().getString(R.string.occupation));
                        occupationArraylist.add(0,occupationList);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                occupationList = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Occupation     = jsonObj.getString("Occupation");

                                    String id=jsonObj.getString("Auto_Id");
//                               if(i==0){
//                                   occupationList.setName(getResources().getString(R.string.promt_country));
//                                   enqF.add(0,occupationList);
//                               }
                                    occupationList.setName(Occupation);
                                    occupationList.setId(id);

                                    occupationArraylist.add(occupationList);

                                    occupationAdapter = new AddEnquirySpinnerAdapter(AddEnquiryActivity.this, occupationArraylist){
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
                                                tv.setText(getResources().getString(R.string.prompt_occupation));
                                                // tv.setTextColor(Color.GRAY);
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinOccupation.setAdapter(occupationAdapter);


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
    // *********************** Call response spinner **************************
    public void  callResponseClass() {
        AddEnquiryActivity.CallResponseTrackClass ru = new AddEnquiryActivity.CallResponseTrackClass();
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
            //CallResponseloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(CallResponseloyee.this));
            String loginResult = ruc.sendPostRequest(ServiceUrls.LOGIN_URL, CallResponseDetails);
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
                        CallResArrayList.clear();
                        spinCallReslist = new Spinner_List();
                        spinCallReslist.setName(getResources().getString(R.string.call_res));
                        CallResArrayList.add(0,spinCallReslist);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
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

                                    CallResArrayList.add(spinCallReslist);

                                    callresadapter = new AddEnquirySpinnerAdapter(AddEnquiryActivity.this, CallResArrayList){
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
                                                tv.setText(getResources().getString(R.string.prompt_call_res));
                                                // tv.setTextColor(Color.GRAY);
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinCallResponce.setAdapter(callresadapter);


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


    // *********************** Send otp  **************************
    public void  SensSMSClass() {
        AddEnquiryActivity.SensSMSTrackClass ru = new AddEnquiryActivity.SensSMSTrackClass();
        ru.execute("5");
    }

    class SensSMSTrackClass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
            showProgressDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            dismissProgressDialog();
            //Toast.makeText(CandiateListView.this, response, Toast.LENGTH_LONG).show();
            //  Toast.makeText(NewCustomerActivity.this, response, Toast.LENGTH_LONG).show();
            SensSMSDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            Log.v(TAG, String.format("doInBackground ::  params= %s", params));
             otp= NetworkUtils.generateOtp();
            String msg="your OTP to verify contact number is :"+otp;
            String loginResult2 = ruc.SendSMS(inputContact.getText().toString(),msg);

            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult2));
            return loginResult2;
        }
    }


    private void SensSMSDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));
        showCustomDialog();

    }
    //******** ****** Popup dialog *****************************
    private void showCustomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.verify_otp_popup);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText veri_otp=(EditText)dialog.findViewById(R.id.et_otp);

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ((Button) dialog.findViewById(R.id.btn_verify)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 int verifyotp=Integer.parseInt(veri_otp.getText().toString());
                if(otp== verifyotp){
                    btn_verify.setText("Verified");
                    Toast.makeText(getApplicationContext(), "Mobile number verified successully", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Mobile number verification failed", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
                //Toast.makeText(getApplicationContext(), "Subcribe Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
//   ************** Check Contact number already exist or not **************
    public void  CheckContactClass() {
        AddEnquiryActivity.CheckContactTrackClass ru = new AddEnquiryActivity.CheckContactTrackClass();
        ru.execute("5");
    }

    class CheckContactTrackClass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
            showProgressDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
            dismissProgressDialog();
            //Toast.makeText(CandiateListView.this, response, Toast.LENGTH_LONG).show();
            //  Toast.makeText(NewCustomerActivity.this, response, Toast.LENGTH_LONG).show();
            CheckContactDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> EnquiryForDetails = new HashMap<String, String>();

            EnquiryForDetails.put("mobileno",inputContact.getText().toString() );
            EnquiryForDetails.put("user","Enquiry" );
            EnquiryForDetails.put("comp_id",SharedPrefereneceUtil.getSelectedBranchId(AddEnquiryActivity.this) );
            EnquiryForDetails.put("action", "check_mobile_already_exist_or_not");
            //EnquiryForloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(EnquiryForloyee.this));
            String loginResult = ruc.sendPostRequest(ServiceUrls.LOGIN_URL, EnquiryForDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }
    }
    private void CheckContactDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

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
                Toast.makeText(AddEnquiryActivity.this,"Mobile Number Already Exits",Toast.LENGTH_SHORT).show();
                inputContact.getText().clear();
                Toast.makeText(AddEnquiryActivity.this,"Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
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
