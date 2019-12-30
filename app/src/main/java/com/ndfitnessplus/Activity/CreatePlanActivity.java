package com.ndfitnessplus.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.ndfitnessplus.Adapter.AddEnquirySpinnerAdapter;
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
import java.util.HashMap;

public class CreatePlanActivity extends AppCompatActivity {
    public EditText inputPackageName,inputDuration,inputSession,inputDescription,inputTax,inputMaxDiscount,inputRackAmount;
    public TextInputLayout inputLayoutPackageName,inputLayoutDuration,inputLayoutSession,inputLayoutDescription,inputLayoutTax,inputLayoutMaxDiscount,
            inputLayoutRackAmount;

    public final String TAG = CreatePlanActivity.class.getName();
    private ProgressDialog pd;
    private AwesomeValidation awesomeValidation;

    //Spinner Adapter
    public Spinner spinPackageType,spinPackageStatus;
    Spinner_List packagetypelist,packageStatuslist;
    ArrayList<Spinner_List> packageTypeArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> packageStatusArrayList = new ArrayList<Spinner_List>();
    public AddEnquirySpinnerAdapter packagetypeadapter,packageStatusadapter;
    String packageType,packageStatus;
    TextView txtPackageType,txtPackageStatus;
    CheckBox Sunday,Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,All;
    ViewDialog viewDialog;
    StringBuilder Days;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_create_plan);
        initToolbar();
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
        inputLayoutDuration = (TextInputLayout) findViewById(R.id.input_layout_duration);
        inputLayoutSession= (TextInputLayout) findViewById(R.id.input_layout_session);
        inputLayoutPackageName = (TextInputLayout) findViewById(R.id.input_layout_package_name);
        inputLayoutTax = (TextInputLayout) findViewById(R.id.input_layout_tax);
        inputLayoutMaxDiscount = (TextInputLayout) findViewById(R.id.input_layout_max_disc);
        inputLayoutDescription = (TextInputLayout) findViewById(R.id.input_layout_description);
        inputLayoutRackAmount = (TextInputLayout) findViewById(R.id.input_layout_rack_amt);


        inputPackageName = (EditText) findViewById(R.id.input_package_name);
        inputDuration = (EditText) findViewById(R.id.input_duration);
        inputSession = (EditText) findViewById(R.id.input_session);
        inputDescription = (EditText) findViewById(R.id.input_startdate);
        inputTax = (EditText) findViewById(R.id.input_tax);
        inputMaxDiscount = (EditText) findViewById(R.id.input_max_disc);
        inputRackAmount = (EditText) findViewById(R.id.input_rack_amt);

        //package spinners
        spinPackageType = (Spinner) findViewById(R.id.spinner_package_type);
        spinPackageStatus = (Spinner) findViewById(R.id.spinner_package_status);

        txtPackageStatus=findViewById(R.id.txt_package_status);
        txtPackageType=findViewById(R.id.txt_package_type);

        Sunday=findViewById(R.id.chk_sunday);
        Monday=findViewById(R.id.chk_monday);
        Tuesday=findViewById(R.id.chk_tuesday);
        Wednesday=findViewById(R.id.chk_wednesday);
        Thursday=findViewById(R.id.chk_thursday);
        Friday=findViewById(R.id.chk_friday);
        Saturday=findViewById(R.id.chk_saturday);
        All=findViewById(R.id.chk_all);
        viewDialog = new ViewDialog(this);
        packageTypeClass();

        // *********** validation *************
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);


        awesomeValidation.addValidation(this, R.id.input_package_name, RegexTemplate.NOT_EMPTY, R.string.err_msg_package_name);
        awesomeValidation.addValidation(this, R.id.input_duration, RegexTemplate.NOT_EMPTY, R.string.err_msg_duration);
        awesomeValidation.addValidation(this, R.id.input_session, RegexTemplate.NOT_EMPTY, R.string.err_msg_session);
        awesomeValidation.addValidation(this, R.id.input_rack_amt, RegexTemplate.NOT_EMPTY, R.string.err_msg_rack_amt);
        awesomeValidation.addValidation(this, R.id.input_max_disc, RegexTemplate.NOT_EMPTY, R.string.err_msg_max_disc);


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

                    }

                }
                // ((TextView) spinPackageType.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
                // Showing selected spinner item
                //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
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
                imm.hideSoftInputFromWindow(inputPackageName.getWindowToken(), 0);
                return false;
            }
        }) ;
        final  String[] package_statusArray = getResources().getStringArray(R.array.package_status_array);

        for(int i=0;i<package_statusArray.length;i++) {
            packageStatuslist = new Spinner_List();
            packageStatuslist.setName(package_statusArray[i]);
            packageStatusArrayList.add(packageStatuslist);
            packageStatusadapter = new AddEnquirySpinnerAdapter(CreatePlanActivity.this, packageStatusArrayList) {
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
                        tv.setText(getResources().getString(R.string.prompt_status));
                        // tv.setTextColor(Color.GRAY);
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }

            };
            spinPackageStatus.setAdapter(packageStatusadapter);
        }

        // *********************** Package Name **********************
        spinPackageStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                    packageStatus = tv.getText().toString();
                    if (index != 0) {
                        txtPackageStatus.setVisibility(View.VISIBLE);

                    }

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

        spinPackageStatus.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputPackageName.getWindowToken(), 0);
                return false;
            }
        }) ;
        All.setOnCheckedChangeListener(new
                         CompoundButton.OnCheckedChangeListener() {
                             @Override
                             public void onCheckedChanged(CompoundButton buttonView, boolean
                                     isChecked) {
                                 if (All.isChecked()) {
                                     Sunday.setChecked(true);
                                     Monday.setChecked(true);
                                     Tuesday.setChecked(true);
                                     Wednesday.setChecked(true);
                                     Thursday.setChecked(true);
                                     Friday.setChecked(true);
                                     Saturday.setChecked(true);
                                 }else {
                                     Sunday.setChecked(false);
                                     Monday.setChecked(false);
                                     Tuesday.setChecked(false);
                                     Wednesday.setChecked(false);
                                     Thursday.setChecked(false);
                                     Friday.setChecked(false);
                                     Saturday.setChecked(false);
                                 }
                             }
                         });

    }
    // ************* Package Type spinner *******************
    public void  packageTypeClass() {
        CreatePlanActivity.PackageTypeTrackClass ru = new CreatePlanActivity.PackageTypeTrackClass();
        ru.execute("5");
    }
    class PackageTypeTrackClass extends AsyncTask<String, Void, String> {

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
            PackageTypeDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            // Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> PackageTypeDetails = new HashMap<String, String>();
            PackageTypeDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(CreatePlanActivity.this));
            PackageTypeDetails.put("action", "show_package_type");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(CreatePlanActivity.this);
            //PackageTypeloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(PackageTypeloyee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, PackageTypeDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void PackageTypeDetails(String jsonResponse) {


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
                        packageTypeArrayList.clear();
                        packagetypelist = new Spinner_List();
                        packagetypelist.setName(getResources().getString(R.string.hint_packagetype));
                        packageTypeArrayList.add(0,packagetypelist);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                packagetypelist = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String PackageType     = jsonObj.getString("PackageType");

//                               if(i==0){
//                                   packagetypelist.setName(getResources().getString(R.string.promt_country));
//                                   enqF.add(0,packagetypelist);
//                               }
                                    packagetypelist.setName(PackageType);

                                    packageTypeArrayList.add(packagetypelist);

                                    packagetypeadapter = new AddEnquirySpinnerAdapter(CreatePlanActivity.this, packageTypeArrayList){
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
                                                tv.setText(getResources().getString(R.string.prompt_packagetype));
                                                // tv.setTextColor(Color.GRAY);
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
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CreatePlanActivity.this);
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
                    //forumCount.setVisibility(View.INVISBLE);
                    // queCount.setVisibility(View.INVISIBLE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    public void  AddCourseClass() {
        CreatePlanActivity.AddCourseTrackClass ru = new CreatePlanActivity.AddCourseTrackClass();
        ru.execute("5");
    }
    class AddCourseTrackClass extends AsyncTask<String, Void, String> {

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
            AddCourseDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {

            HashMap<String, String> AddCourseDetails = new HashMap<String, String>();
            AddCourseDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(CreatePlanActivity.this));
            Log.v(TAG, String.format("doInBackground :: comp_id = %s", SharedPrefereneceUtil.getSelectedBranchId(CreatePlanActivity.this)));
            AddCourseDetails.put("package_type",packageType);
            Log.v(TAG, String.format("doInBackground :: packageType = %s", packageType));
            AddCourseDetails.put("package_name",inputPackageName.getText().toString());
            Log.v(TAG, String.format("doInBackground :: name = %s", inputPackageName.getText().toString()));
            AddCourseDetails.put("duration",inputDuration.getText().toString());
            Log.v(TAG, String.format("doInBackground :: contact = %s", inputDuration.getText().toString()));
            AddCourseDetails.put("session",inputSession.getText().toString());
            Log.v(TAG, String.format("doInBackground :: Email = %s", inputSession.getText().toString()));
            AddCourseDetails.put("description",inputDescription.getText().toString() );
            Log.v(TAG, String.format("doInBackground :: package_type = %s", packageType));
            AddCourseDetails.put("tax",inputTax.getText().toString());
            Log.v(TAG, String.format("doInBackground :: package_type = %s", inputTax.getText().toString()));
            AddCourseDetails.put("rack_amout",inputRackAmount.getText().toString());
            Log.v(TAG, String.format("doInBackground :: duration = %s", inputRackAmount.getText().toString()));
            AddCourseDetails.put("max_discount", inputMaxDiscount.getText().toString());
            Log.v(TAG, String.format("doInBackground :: package_type = %s", packageType));
            AddCourseDetails.put("days",Days.toString());
            Log.v(TAG, String.format("doInBackground :: package_type = %s", packageType));
            AddCourseDetails.put("package_status",packageStatus);
            Log.v(TAG, String.format("doInBackground :: package_type = %s", packageType));
            AddCourseDetails.put("mode","AdminApp");
            Log.v(TAG, String.format("doInBackground :: executive name= %s", SharedPrefereneceUtil.getName(CreatePlanActivity.this)));
            AddCourseDetails.put("action", "add_package");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(CreatePlanActivity.this);
            String loginResult2 = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, AddCourseDetails);

            Log.v(TAG, String.format("doInBackground :: add_package= %s", loginResult2));
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
                 Toast.makeText(CreatePlanActivity.this,"Package Created succesfully",Toast.LENGTH_SHORT).show();
        }


            else if (success.equalsIgnoreCase(getResources().getString(R.string.one)))
            {
                Toast.makeText(CreatePlanActivity.this,"Mobile Number Already Exits ,Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
