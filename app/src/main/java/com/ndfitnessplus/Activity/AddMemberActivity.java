package com.ndfitnessplus.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
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
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ndfitnessplus.Activity.Notification.OtherFollowupActivity;
import com.ndfitnessplus.Activity.Notification.TodaysEnrollmentActivity;
import com.ndfitnessplus.Adapter.AddEnquirySpinnerAdapter;
import com.ndfitnessplus.Model.Spinner_List;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.Utility;
import com.ndfitnessplus.Utility.ViewDialog;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.ndfitnessplus.Activity.EnquiryActivity.TAG;

public class AddMemberActivity extends AppCompatActivity implements View.OnClickListener{
    private Uri mCropImageUri;
    public EditText inputName,inputContact, inputEmail,inputAdd,inputEmContact,inputDob,inputGstNo;
    public TextInputLayout inputLayoutName,inputLayoutContact,inputLayoutEmail,
            inputLayoutAdd,inputLayoutEmContact,inputLayoutDob,inputLayoutGstNo;

    FloatingActionButton cameraBtn;
    public final String TAG = AddMemberActivity.class.getName();
    private ProgressDialog pd;
    private AwesomeValidation awesomeValidation;
    private int mYear, mMonth, mDay;
    RadioButton maleradioButton,femaleRB;
    RadioGroup radioGroup;
    String Occupation;
    String enq_owner_exe ="NA";
    //Camera
    private ProgressBar progressBar;
    private ImageButton btnUpload;
    String imgPath, fileName;
    String gender="";
    public  static final int RequestPermissionCode  = 1 ;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView CapturedImage;
    // Bitmap bitmap;
    String ConvertImage="";
    private Uri uri;
    private Uri filePath;
    Bitmap bitmap;
    String afterEnquirySms;
    //Spinner Adapter
    public Spinner spinBloodGroup,spinOccupation;
    Spinner_List bloodgroupList,occupationList;
    ArrayList<Spinner_List>  bloodgroupArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> occupationArraylist = new ArrayList<Spinner_List>();
    public AddEnquirySpinnerAdapter bloodgroupadapter,occupationAdapter;
    String bloodgroup;
    String occupation="";
    TextView txtbloodgroup,txtoccupation;
    //Loading gif
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_add_member);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.add_member));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutContact = (TextInputLayout) findViewById(R.id.input_layout_cont);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutAdd = (TextInputLayout) findViewById(R.id.input_layout_add);
        inputLayoutEmContact= (TextInputLayout) findViewById(R.id.input_layout_em_contact);
        inputLayoutDob= (TextInputLayout) findViewById(R.id.input_layout_dob);
        inputLayoutGstNo= (TextInputLayout) findViewById(R.id.input_layout_gst_no);

        viewDialog = new ViewDialog(this);

        cameraBtn=findViewById(R.id.input_image);
        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);
        maleradioButton=findViewById(R.id.radioButton);
        femaleRB=findViewById(R.id.radioButton2);
        inputName = (EditText) findViewById(R.id.input_name);
        inputContact = (EditText) findViewById(R.id.input_cont);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputAdd=(EditText)findViewById(R.id.input_add);
        inputEmContact=(EditText)findViewById(R.id.input_em_contact);
        inputDob=(EditText)findViewById(R.id.input_dob);
        inputGstNo=(EditText)findViewById(R.id.input_gst_no);

        CapturedImage=findViewById(R.id.captured_image);

        //spinners
        spinBloodGroup = (Spinner) findViewById(R.id.spinner_blood_grp);

        spinOccupation = (Spinner) findViewById(R.id.spinner_occupation);

        txtbloodgroup=findViewById(R.id.txt_blood_grp);
        txtoccupation=findViewById(R.id.txt_occupation);
        //
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //defining AwesomeValidation object
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        awesomeValidation.addValidation(this, R.id.input_name, RegexTemplate.NOT_EMPTY, R.string.err_msg_name);
        awesomeValidation.addValidation(this, R.id.input_cont, RegexTemplate.NOT_EMPTY, R.string.err_msg_cont);


        Intent intent = getIntent();
        // Bundle args = intent.getBundleExtra("BUNDLE");
        if (intent != null) {
           String contact=intent.getStringExtra("contact");
           inputContact.setText(contact);


        }
        //awesomeValidation.addValidation(this, R.id.input_next_foll_date,RegexTemplate.NOT_EMPTY, R.string.err_msg_next_foll_date);

        //api's for spinners
        occupationClass();
//        enqforClass();

        inputContact.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(inputContact.getText().length()>0){
                    CheckContactClass();
                }
//                else{
//                    inputName.setText("");
//                    inputEmail.setText("");
//                    inputAdd.setText("");
//
////                  RadioButton btnm=findViewById(R.id.radioButton);
////                  RadioButton btnf=findViewById(R.id.radioButton2);
////                  btnf.setChecked(false);
//                    //btnm.setChecked(true);
//                    CapturedImage.setImageDrawable(getResources().getDrawable(R.drawable.nouser));
//                    CapturedImage.setVisibility(View.GONE);
//            }


            }
        });

//        inputContact.addTextChangedListener(new TextWatcher() {
//
//            public void onTextChanged(CharSequence s, int start, int before,
//                                      int count) {
//                CheckContactClass();
//            }
//
//
//
//            public void beforeTextChanged(CharSequence s, int start, int count,
//                                          int after) {
//
//            }
//
//            public void afterTextChanged(Editable s) {
//                if(inputContact.getText().length()>0) {
//                    //do your work here
//                    // Toast.makeText(AddEnquiryActivity.this ,"Text vhanged count  is 10 then: " , Toast.LENGTH_LONG).show();
//                    CheckContactClass();
//                }
//            }
//        });
        inputName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(inputContact.getText().length()>0){
                    CheckContactClass();
                }
//                else{
//                    inputName.setText("");
//                    inputEmail.setText("");
//                    inputAdd.setText("");
//
////                  RadioButton btnm=findViewById(R.id.radioButton);
////                  RadioButton btnf=findViewById(R.id.radioButton2);
////                  btnf.setChecked(false);
//                    //btnm.setChecked(true);
//                    CapturedImage.setImageDrawable(getResources().getDrawable(R.drawable.nouser));
//                    CapturedImage.setVisibility(View.GONE);
//            }


            }
        });
        inputName.addTextChangedListener(new TextWatcher() {
            int mStart = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mStart = start + count;
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                String capitalizedText;
                if (input.length() < 1)
                    capitalizedText = input;
                else if (input.length() > 1 && input.contains(" ")) {
                    String fstr = input.substring(0, input.lastIndexOf(" ") + 1);
                    if (fstr.length() == input.length()) {
                        capitalizedText = fstr;
                    } else {
                        String sstr = input.substring(input.lastIndexOf(" ") + 1);
                        sstr = sstr.substring(0, 1).toUpperCase() + sstr.substring(1);
                        capitalizedText = fstr + sstr;
                    }
                } else
                    capitalizedText = input.substring(0, 1).toUpperCase() + input.substring(1);

                if (!capitalizedText.equals(inputName.getText().toString())) {
                    inputName.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            inputName.setSelection(mStart);
                            inputName.removeTextChangedListener(this);
                        }
                    });
                    inputName.setText(capitalizedText);
                }
            }});

        final  String[] bloodgrouparray = getResources().getStringArray(R.array.blood_group_array);

        for(int i=0;i<bloodgrouparray.length;i++) {
            bloodgroupList = new Spinner_List();
            bloodgroupList.setName(bloodgrouparray[i]);
            bloodgroupArrayList.add(bloodgroupList);
            bloodgroupadapter = new AddEnquirySpinnerAdapter(AddMemberActivity.this, bloodgroupArrayList) {
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
                        tv.setText(getResources().getString(R.string.prompt_blood_group));
                        // tv.setTextColor(Color.GRAY);
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }

            };
            spinBloodGroup.setAdapter(bloodgroupadapter);
        }

        //setting data to the spinners

        spinBloodGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                    bloodgroup = tv.getText().toString();
                    if (index != 0) {
                        txtbloodgroup.setVisibility(View.VISIBLE);
                    }

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinOccupation.setSelection(1);
        //Occupation listner
        spinOccupation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if(view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    View layout = (View) view.findViewById(R.id.layout);
                    layout.setPadding(0, 0, 0, 0);
//                    if (index == 0) {

//                        tv.setTextColor((Color.GRAY));
//                    } else {
                        tv.setTextColor((Color.BLACK));
//                    }
                    occupation = tv.getText().toString();

                        txtoccupation.setVisibility(View.VISIBLE);

                    if (!occupation.equals(getResources().getString(R.string.occupation))) {
                        //awesomeValidation.addValidation(AddEnquiryActivity.this, R.id.spinner_occupation,RegexTemplate.NOT_EMPTY, R.string.err_msg_next_foll_date);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        inputDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(AddMemberActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                inputDob.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                datePickerDialog.show();
            }
        });



        EnableRuntimePermissionToAccessCamera();

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.startPickImageActivity(AddMemberActivity.this);
            }
        });

    }

    public void EnableRuntimePermissionToAccessCamera(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(AddMemberActivity.this,
                Manifest.permission.CAMERA))
        {

            // Printing toast message after enabling runtime permission.
            //Toast.makeText(AddMemberActivity.this,"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(AddMemberActivity.this,new String[]{Manifest.permission.CAMERA}, RequestPermissionCode);

        }
    }
    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                CapturedImage.setVisibility(View.VISIBLE);
                // ((ImageButton) findViewById(R.id.quick_start_cropped_image)).setImageURI(result.getUri());
                CapturedImage.setImageURI(result.getUri());
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getUri());
                    uploadimageClass();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // required permissions granted, start crop image activity
            startCropImageActivity(mCropImageUri);
        } else {
            //  Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAspectRatio(1,1)
                .setFixAspectRatio(true)
                .start(this);
    }

    private void uploadimageClass() {

        ByteArrayOutputStream byteArrayOutputStreamObject ;

        byteArrayOutputStreamObject = new ByteArrayOutputStream();
        if(bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStreamObject);
        }

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

        ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);
        Log.v(TAG, String.format(" ConvertImage= %s", ConvertImage));

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
            }else{
                Toast.makeText(AddMemberActivity.this, "Please Select Gender", Toast.LENGTH_SHORT).show();
            }
            submitForm();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public void onClick(View v) {
        if(radioGroup.getCheckedRadioButtonId()!=-1){
            int id= radioGroup.getCheckedRadioButtonId();
            View radioButton = radioGroup.findViewById(id);
            int radioId = radioGroup.indexOfChild(radioButton);
            RadioButton btn = (RadioButton) radioGroup.getChildAt(radioId);
            gender= (String) btn.getText();
            //Toast.makeText(AddEnquiryActivity.this, gender, Toast.LENGTH_SHORT).show();
        }
        submitForm();
    }

    private void showProgressDialog() {
        Log.v(TAG, String.format("showProgressDialog"));
        pd = new ProgressDialog(AddMemberActivity.this);
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
    public void  occupationClass() {
        AddMemberActivity.OccupationTrackClass ru = new AddMemberActivity.OccupationTrackClass();
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

            OccupationDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
           // Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> OccupationDetails = new HashMap<String, String>();
            OccupationDetails.put("action", "show_occupation_list");
            //OccupationloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(Occupationloyee.this));
            String domainurl=SharedPrefereneceUtil.getDomainUrl(AddMemberActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, OccupationDetails);
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
                            occupationList.setName(getResources().getString(R.string.na));
                            occupationArraylist.add(1,occupationList);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                occupationList = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Occupation     = jsonObj.getString("Occupation");

                                    String id=jsonObj.getString("Auto_Id");
//
                                    occupationList.setName(Occupation);
                                    occupationList.setId(id);

                                    occupationArraylist.add(occupationList);

                                    occupationAdapter = new AddEnquirySpinnerAdapter(AddMemberActivity.this, occupationArraylist){
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


                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    public void  CheckContactClass() {
        AddMemberActivity.CheckContactTrackClass ru = new AddMemberActivity.CheckContactTrackClass();
        ru.execute("5");
    }

    class CheckContactTrackClass extends AsyncTask<String, Void, String> {

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
            CheckContactDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
          //  Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> EnquiryForDetails = new HashMap<String, String>();

            EnquiryForDetails.put("mobileno",inputContact.getText().toString() );
            EnquiryForDetails.put("user","Member");
            EnquiryForDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(AddMemberActivity.this) );
            EnquiryForDetails.put("action", "check_mobile_already_exist_or_not");
            //EnquiryForloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(EnquiryForloyee.this));
            String domainurl=SharedPrefereneceUtil.getDomainUrl(AddMemberActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, EnquiryForDetails);
            Log.v(TAG, String.format("doInBackground :: check_mobile_already_exist_or_not= %s", loginResult));
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
                    CheckContactInEnquiryClass();
                // showCustomDialog();

                //inputEmail, inputPhone,inputAdd,inputReq,inputFollowupdate;
            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.two))||success.equalsIgnoreCase(getResources().getString(R.string.one)))
            {
                Toast.makeText(AddMemberActivity.this,"Contact Already Exits. Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
                inputContact.getText().clear();
                //Toast.makeText(AddMemberActivity.this,"Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //  ********************** Check Email Already Exist or Not *********************
    public void  CheckEmailClass() {
        AddMemberActivity.CheckEmailTrackClass ru = new AddMemberActivity.CheckEmailTrackClass();
        ru.execute("5");
    }

    class CheckEmailTrackClass extends AsyncTask<String, Void, String> {

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
            CheckEmailDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
           // Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> EnquiryForDetails = new HashMap<String, String>();

            EnquiryForDetails.put("email",inputEmail.getText().toString() );
            EnquiryForDetails.put("user","Member");
            EnquiryForDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(AddMemberActivity.this) );
            EnquiryForDetails.put("action", "check_email_already_exist_or_not");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(AddMemberActivity.this);
            //EnquiryForloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(EnquiryForloyee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, EnquiryForDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }
    }
    private void CheckEmailDetails(String jsonResponse) {

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
                Toast.makeText(AddMemberActivity.this,"Email Already Exits. Please Enter New Email",Toast.LENGTH_SHORT).show();
                inputEmail.getText().clear();
                //Toast.makeText(AddMemberActivity.this,"Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void  CheckContactInEnquiryClass() {
        AddMemberActivity.CheckContactInEnquiryTrackClass ru = new AddMemberActivity.CheckContactInEnquiryTrackClass();
        ru.execute("5");
    }

    class CheckContactInEnquiryTrackClass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
           // showProgressDialog();
//            viewDialog.showDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));

            CheckContactInEnquiryDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
           // Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> EnquiryForDetails = new HashMap<String, String>();

            EnquiryForDetails.put("mobileno",inputContact.getText().toString() );
            EnquiryForDetails.put("user","Enquiry" );
            EnquiryForDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(AddMemberActivity.this) );
            EnquiryForDetails.put("action", "check_mobile_already_exist_in_enquiry_or_not");
            //EnquiryForloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(EnquiryForloyee.this));
            String domainurl=SharedPrefereneceUtil.getDomainUrl(AddMemberActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, EnquiryForDetails);
            Log.v(TAG, String.format("doInBackground :: check_mobile_already_exist_in_enquiry_or_not= %s", loginResult));
            return loginResult;
        }
    }
    private void CheckContactInEnquiryDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject jsonObjLoginResponse = null;
        try {
            jsonObjLoginResponse = new JSONObject(jsonResponse);
            String success = jsonObjLoginResponse.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.zero))) {

            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.two)))
            {
                JSONArray jsonArrayResult = jsonObjLoginResponse.getJSONArray("Data");

                if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                    for (int i = 0; i < jsonArrayResult.length(); i++) {

                        Log.v(TAG, "JsonResponseOpeartion ::");
                        JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                        if (jsonObj != null) {

                            String Name = jsonObj.getString("Name");
                            String Email = jsonObj.getString("Email");
                            String Address = jsonObj.getString("Address");
                            gender = jsonObj.getString("Gender");
                            String DOB = jsonObj.getString("DOB");
                             Occupation = jsonObj.getString("Occupation");
                             enq_owner_exe = jsonObj.getString("EnquiryOwnerExecutive");
                             bloodgroup = jsonObj.getString("Blood_Group");
                             String   Image = jsonObj.getString("Image");

                            inputName.setText(Name);
                            inputEmail.setText(Email);
                            inputAdd.setText(Address);
                            String bday= Utility.formatDateDB(DOB);
                            inputDob.setText(bday);
                            String img=Image.replace("\"", "");
                            String domainurl=SharedPrefereneceUtil.getDomainUrl(AddMemberActivity.this);
                            final String url= domainurl+ServiceUrls.IMAGES_URL + img;
                            Log.d(TAG, "img url: "+url);
                            CapturedImage.setVisibility(View.VISIBLE);

                            if(!(img.equals("null")||img.equals(""))) {
                                RequestOptions requestOptions = new RequestOptions();
                                requestOptions.placeholder(R.drawable.nouser);
                                requestOptions.error(R.drawable.nouser);


                                Glide.with(this)
                                        .setDefaultRequestOptions(requestOptions)
                                        .load(url).into(CapturedImage);
                               // Glide.with(AddMemberActivity.this).load(url).placeholder(R.drawable.nouser).into(CapturedImage);
                            }
                            AddMemberActivity.this.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    (new AsyncTask<String, String, Bitmap>() {
                                        ServerClass ruc = new ServerClass();
                                        @Override
                                        protected Bitmap doInBackground(String... params) {
                                            try {

                                                URL urlb = new URL(url);
                                                bitmap = BitmapFactory.decodeStream(urlb.openConnection().getInputStream());
                                                uploadimageClass();
                                            } catch (IOException e) {
                                                System.out.println(e);
                                            }

                                            return bitmap;
                                        }
                                        @Override
                                        protected void onPostExecute(Bitmap response) {
                                            super.onPostExecute(response);
                                            Log.v(TAG, String.format("onPostExecute :: image bitmap = %s", response));

                                       }
                                    }).execute();
                                }});

                            if(gender.equals("Male"))
                                radioGroup.check(R.id.radioButton);
                            else
                                radioGroup.check(R.id.radioButton2);

                            for (int j=0;j<occupationArraylist.size();j++){

                                String occ = occupationArraylist.get(j).getName();
                                if(Occupation.equals(occ)){
                                    spinOccupation.setSelection(j);
                                }

                            }
                            for (int j=0;j<bloodgroupArrayList.size();j++){

                                String occ = bloodgroupArrayList.get(j).getName();
                                if(bloodgroup.equals(occ)){
                                    spinBloodGroup.setSelection(j);
                                }

                            }


                        }
                    }
                } else if (jsonArrayResult.length() == 0) {
                    System.out.println("No records found");
                }
                //Toast.makeText(AddMemberActivity.this,"Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // **************** Add Member Class **************
    public void  addMemberClass() {
        AddMemberActivity.AddMemberTrackClass ru = new AddMemberActivity.AddMemberTrackClass();
        ru.execute("5");
    }
    class AddMemberTrackClass extends AsyncTask<String, Void, String> {

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
            //Toast.makeText(CandiateListView.this, response, Toast.LENGTH_LONG).show();
            //  Toast.makeText(NewCustomerActivity.this, response, Toast.LENGTH_LONG).show();
            AddMemberDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> AddMemberDetails = new HashMap<String, String>();
            AddMemberDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(AddMemberActivity.this));
            Log.v(TAG, String.format("doInBackground :: Comp_id= %s", SharedPrefereneceUtil.getSelectedBranchId(AddMemberActivity.this)));
            AddMemberDetails.put("name",inputName.getText().toString());
            Log.v(TAG, String.format("doInBackground :: name= %s", inputName.getText().toString()));
            AddMemberDetails.put("contact",inputContact.getText().toString());
            Log.v(TAG, String.format("doInBackground :: contact= %s", inputContact.getText().toString()));
            AddMemberDetails.put("add",inputAdd.getText().toString());
            AddMemberDetails.put("email",inputEmail.getText().toString());
            AddMemberDetails.put("gender", gender);
            Log.v(TAG, String.format("doInBackground :: gender= %s", gender));
            AddMemberDetails.put("occupation",occupation);
            Log.v(TAG, String.format("doInBackground :: occupation= %s", occupation));
            AddMemberDetails.put("emr_contact",inputEmContact.getText().toString());
            AddMemberDetails.put("dob",inputDob.getText().toString());
            AddMemberDetails.put("blood_group",bloodgroup);
            Log.v(TAG, String.format("doInBackground :: blood_group= %s", bloodgroup));
            AddMemberDetails.put("image_path",ConvertImage);
            Log.v(TAG, String.format("doInBackground :: image_path= %s", ConvertImage));
            AddMemberDetails.put("member_gst_no",inputGstNo.getText().toString());
            AddMemberDetails.put("enq_own_exe",enq_owner_exe);
            AddMemberDetails.put("mem_own_exe",SharedPrefereneceUtil.getName(AddMemberActivity.this));
            AddMemberDetails.put("mode","AdminApp");
            Log.v(TAG, String.format("doInBackground :: mem_own_exe= %s", SharedPrefereneceUtil.getName(AddMemberActivity.this)));
            AddMemberDetails.put("action", "add_member");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(AddMemberActivity.this);
            String loginResult2 = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, AddMemberDetails);

            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult2));
            return loginResult2;
        }
    }

    private void AddMemberDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject jsonObjLoginResponse = null;
        try {
            jsonObjLoginResponse = new JSONObject(jsonResponse);
            String success = jsonObjLoginResponse.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                Toast.makeText(AddMemberActivity.this,"Member added succesfully",Toast.LENGTH_SHORT).show();

                finish();
                String member_id=jsonObjLoginResponse.getString("member_id");
                Intent intent=new Intent(AddMemberActivity.this,RenewActivity.class);
                intent.putExtra("member_id",member_id);
                intent.putExtra("name",inputName.getText().toString());
                intent.putExtra("contact",inputContact.getText().toString());
                intent.putExtra("email",inputEmail.getText().toString());
                startActivity(intent);

            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.one)))
            {
                Toast.makeText(AddMemberActivity.this,"Mobile Number Already Exits.Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
                inputContact.getText().clear();
              //Toast.makeText(AddMemberActivity.this,"Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void submitForm() {
        if(occupation.equals(getResources().getString(R.string.occupation)) ){
            occupation="NA";
        }
        if(bloodgroup.equals(getResources().getString(R.string.blood_group)) ){
            bloodgroup="NA";
        }

        if((inputEmail.length()>0) ){
            awesomeValidation.addValidation(this, R.id.input_email, Patterns.EMAIL_ADDRESS, R.string.err_msg_email);
            if (awesomeValidation.validate()) {
                addMemberClass();
            }
        }else{
            inputEmail.setError(null);
            if (awesomeValidation.validate()) {
                addMemberClass();
            }
        }

    }

}
