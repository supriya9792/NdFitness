package com.ndfitnessplus.Activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ndfitnessplus.Activity.Notification.StaffBirthdayFollowupActivity;
import com.ndfitnessplus.Activity.Notification.TodaysEnrollmentActivity;
import com.ndfitnessplus.Adapter.AddEnquirySpinnerAdapter;
import com.ndfitnessplus.Adapter.SpinnerAdapter;
import com.ndfitnessplus.Model.CourseList;
import com.ndfitnessplus.Model.EnquiryList;
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

public class CourseFilterActivity extends AppCompatActivity {
    public final String TAG = CourseFilterActivity.class.getName();
    private ProgressDialog pd;
    //Spinner Adapter
    public Spinner spinDateWise,spinPackageType,spinPackageName,spinInstructor,spinSalesExecutive;
    Spinner_List dateWiselist,packagetypelist,packageNamelist,instructorList,saleExecutiveList;
    ArrayList<Spinner_List> dateWiseArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> packageTypeArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> packagenameArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> instructorArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> SalesExecutiveArrayList = new ArrayList<Spinner_List>();


    public SpinnerAdapter datewiseadapter,packagetypeadapter,packagenameadapter,instructoradapter,salesExecutiveAdapter;
    String Datewise,packageType,packagename,instructorname,salesExecutiveName;

    TextView todate,fromdate;
    ImageButton toDatebtn,fromDateBtn;
    private int mYear, mMonth, mDay;
    String month,day;
    ArrayList<CourseList> subListArrayList = new ArrayList<CourseList>();
    CourseList subList;
    Button btn_applyFilter;

    //Loading gif
    ViewDialog viewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_course_filter);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.course_filters));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent() {

        //spinners
        spinDateWise = (Spinner) findViewById(R.id.spinner_date_wise);
        spinPackageType = (Spinner) findViewById(R.id.spinner_package_type);
        spinPackageName = (Spinner) findViewById(R.id.spinner_package_name);
        spinInstructor=(Spinner)findViewById(R.id.spinner_instructor_name);
        spinSalesExecutive = (Spinner) findViewById(R.id.spinner_sales_executive);

        viewDialog = new ViewDialog(this);

        todate = findViewById(R.id.to_date);
        fromdate = findViewById(R.id.from_date);
        fromDateBtn = findViewById(R.id.btn_from_date);
        toDatebtn = findViewById(R.id.btn_to_date);
        btn_applyFilter = findViewById(R.id.btn_apply_filters);

        String firstday = Utility.getFirstDayofMonth();
        todate.setText(firstday);
        String curr_date = Utility.getCurrentDate();
        fromdate.setText(curr_date);


        //date pickers for to date and from date
        toDatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(CourseFilterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String date=(year + "-"
                                        + (monthOfYear + 1) + "-" + dayOfMonth).toString();
                                String cdate=Utility.formatDateDB(date);
                                todate.setText(cdate);
                                CampareTwoDates();

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        fromDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(CourseFilterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String date=(year + "-"
                                        + (monthOfYear + 1) + "-" + dayOfMonth).toString();
                                String cdate=Utility.formatDateDB(date);
                                fromdate.setText(cdate);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        //Date Wise date type spinners
        final  String[] memberDateArray = getResources().getStringArray(R.array.course_date_array);

        for(int i=0;i<memberDateArray.length;i++) {
            dateWiselist = new Spinner_List();
            dateWiselist.setName(memberDateArray[i]);
            dateWiseArrayList.add(dateWiselist);
            datewiseadapter = new SpinnerAdapter(CourseFilterActivity.this, dateWiseArrayList) {
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
                        tv.setText(getResources().getString(R.string.prompt_mem_date));
                        // tv.setTextColor(Color.GRAY);
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }

            };
            spinDateWise.setAdapter(datewiseadapter);
        }
        spinDateWise.setSelection(1);
        //Toast.makeText(MainActivity.this,genderradioButton.getText(), Toast.LENGTH_SHORT).show();
        spinDateWise.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);
                    if (index == 0) {
                        tv.setText(getResources().getString(R.string.prompt_mem_date));
                    }

//                tv.setTextColor(getResources().getColor(R.color.black));
                    Datewise = tv.getText().toString();
                    if ((Datewise.equals(getResources().getString(R.string.prompt_mem_date))) ||
                            (Datewise.equals(getResources().getString(R.string.all)))) {
                        Datewise = "";
                    }
                    // ((TextView) spinEnquiryType.getSelectedView()).setTextColor(getResources().getColor(R.color.white));
                    // Showing selected spinner item
                    //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
       //api of spinners
        packageTypeClass();
        packageNameClass();
        executiveClass();
        instructorNameClass();

        spinPackageType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if(view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);
                    if(index==0){
                        tv.setText(getResources().getString(R.string.hint_packagetype_n));
                    }
                    packageType = tv.getText().toString();
                    if ((packageType.equals(getResources().getString(R.string.hint_packagetype_n))) ||
                            (packageType.equals(getResources().getString(R.string.all)))) {
                        packageType = "";
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
        // *********************** Package Name **********************
        spinPackageName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if(view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);

                    if(index==0){
                        tv.setText(getResources().getString(R.string.hint_package_name_n));
                    }
                    packagename = tv.getText().toString();
                    if ((packagename.equals(getResources().getString(R.string.hint_package_name_n))) ||
                            (packagename.equals(getResources().getString(R.string.all)))) {
                        packagename = "";
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
        spinSalesExecutive.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if(view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);
                    if(index==0){
                        tv.setText(getResources().getString(R.string.prompt_executive));
                    }
//                tv.setTextColor(getResources().getColor(R.color.black));
                    salesExecutiveName = tv.getText().toString();
                    if((salesExecutiveName.equals(getResources().getString(R.string.prompt_executive)))||
                            (salesExecutiveName.equals(getResources().getString(R.string.all)))){
                        salesExecutiveName="";
                    }
                    // ((TextView) spinEnquiryType.getSelectedView()).setTextColor(getResources().getColor(R.color.white));
                    // Showing selected spinner item
                    //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                }}

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // *********************** Instructor Name **********************
        spinInstructor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if(view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);
                    instructorname = tv.getText().toString();
                    if(index==0){
                        tv.setText(getResources().getString(R.string.hint_instructor));
                    }
                    if(instructorname.equals(getResources().getString(R.string.hint_instructor))||
                            (instructorname.equals(getResources().getString(R.string.all)))){
                        instructorname="";
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


        btn_applyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchEnquiryClass();
            }
        });
    }
    public void CampareTwoDates(){
        //******************campare two dates****************
//        String date = "03/26/2012 11:00:00";
//        String dateafter = "03/26/2012 11:59:00";
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy");
        Date convertedDate = new Date();
        Date convertedDate2 = new Date();
        try {
            convertedDate = dateFormat.parse(todate.getText().toString());
            convertedDate2 = dateFormat.parse(fromdate.getText().toString());
            if (convertedDate2.after(convertedDate) || convertedDate2.equals(convertedDate)) {
                //.setText("true");
            } else {
                Toast.makeText(this, "From date should be greater than to date: " , Toast.LENGTH_LONG).show();
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private void showProgressDialog() {
        Log.v(TAG, String.format("showProgressDialog"));
        pd = new ProgressDialog(CourseFilterActivity.this);
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
    // ************* Package Type spinner *******************
    public void  packageTypeClass() {
        CourseFilterActivity.PackageTypeTrackClass ru = new CourseFilterActivity.PackageTypeTrackClass();
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
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> PackageTypeDetails = new HashMap<String, String>();
            PackageTypeDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(CourseFilterActivity.this));
            PackageTypeDetails.put("action", "show_package_type");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(CourseFilterActivity.this);
            //PackageTypeloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(PackageTypeloyee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, PackageTypeDetails);
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
                        packagetypelist.setName(getResources().getString(R.string.hint_package_name_n));
                        packageTypeArrayList.add(0,packagetypelist);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            packagetypelist.setName(getResources().getString(R.string.all));

                            packageTypeArrayList.add(1,packagetypelist);
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

                                    packagetypeadapter = new SpinnerAdapter(CourseFilterActivity.this, packageTypeArrayList){
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

                    //forumCount.setVisibility(View.INVISBLE);
                    // queCount.setVisibility(View.INVISIBLE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    // ************* Package Name spinner *******************
    public void  packageNameClass() {
        CourseFilterActivity.PackageNameTrackClass ru = new CourseFilterActivity.PackageNameTrackClass();
        ru.execute("5");
    }
    class PackageNameTrackClass extends AsyncTask<String, Void, String> {

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
            PackageNameDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
           // Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> PackageNameDetails = new HashMap<String, String>();
            PackageNameDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(CourseFilterActivity.this));
            PackageNameDetails.put("action", "show_package_name");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(CourseFilterActivity.this);
            //PackageNameloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(PackageNameloyee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, PackageNameDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void PackageNameDetails(String jsonResponse) {


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
                        packagenameArrayList.clear();
                        packageNamelist = new Spinner_List();
                        packageNamelist.setName(getResources().getString(R.string.hint_package_name));
                        packagenameArrayList.add(0,packageNamelist);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            packageNamelist.setName(getResources().getString(R.string.all));

                            packagenameArrayList.add(1,packageNamelist);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                packageNamelist = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String PackageName     = jsonObj.getString("Package_Name");



                                    packageNamelist.setName(PackageName);

                                    packagenameArrayList.add(packageNamelist);

                                    packagenameadapter = new SpinnerAdapter(CourseFilterActivity.this, packagenameArrayList){
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
                                                tv.setText(getResources().getString(R.string.prompt_package_name));
                                                // tv.setTextColor(Color.GRAY);
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

                    //forumCount.setVisibility(View.INVISBLE);
                    // queCount.setVisibility(View.INVISIBLE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    public void  executiveClass() {
        CourseFilterActivity.ExecutiveNameTrackClass ru = new CourseFilterActivity.ExecutiveNameTrackClass();
        ru.execute("5");
    }
    class ExecutiveNameTrackClass extends AsyncTask<String, Void, String> {

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
            ExecutiveNameDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
           // Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> ExecutiveNameDetails = new HashMap<String, String>();
            String comp_name=SharedPrefereneceUtil.getCompanyName(CourseFilterActivity.this);
            String location=SharedPrefereneceUtil.getSelectedBranch(CourseFilterActivity.this);
            String compid=comp_name+"-"+location+",";
            ExecutiveNameDetails.put("comp_id", compid);
            ExecutiveNameDetails.put("action", "show_executive_list");
            //ExecutiveNameloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(ExecutiveNameloyee.this));
            String domainurl=SharedPrefereneceUtil.getDomainUrl(CourseFilterActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, ExecutiveNameDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void ExecutiveNameDetails(String jsonResponse) {


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
                        SalesExecutiveArrayList.clear();
                        //for(int j=0;j<2;j++){
                        saleExecutiveList = new Spinner_List();

                        saleExecutiveList.setName(getResources().getString(R.string.prompt_executive));

                        SalesExecutiveArrayList.add(0,saleExecutiveList);

                        // }
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            saleExecutiveList.setName(getResources().getString(R.string.all));

                            SalesExecutiveArrayList.add(1,saleExecutiveList);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                saleExecutiveList = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String EnquiryOwnerExecutive     = jsonObj.getString("EnquiryOwnerExecutive");

//                               if(i==0){
//                                   saleExecutiveList.setName(getResources().getString(R.string.promt_country));
//                                   enqF.add(0,saleExecutiveList);
//                               }


                                    saleExecutiveList.setName(EnquiryOwnerExecutive);

                                    SalesExecutiveArrayList.add(saleExecutiveList);

                                    salesExecutiveAdapter = new SpinnerAdapter(CourseFilterActivity.this, SalesExecutiveArrayList){
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
                                                tv.setText(getResources().getString(R.string.prompt_executive));
                                                // tv.setTextColor(Color.GRAY);
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinSalesExecutive.setAdapter(salesExecutiveAdapter);


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
    // ************* Instructor Name spinner *******************
    public void  instructorNameClass() {
        CourseFilterActivity.InstructorNameTrackClass ru = new CourseFilterActivity.InstructorNameTrackClass();
        ru.execute("5");
    }
    class InstructorNameTrackClass extends AsyncTask<String, Void, String> {

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
            InstructorNameDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
           // Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> InstructorNameDetails = new HashMap<String, String>();
            InstructorNameDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(CourseFilterActivity.this));
            InstructorNameDetails.put("action", "show_instructor_name_list");
            //InstructorNameloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(InstructorNameloyee.this));
            String domainurl=SharedPrefereneceUtil.getDomainUrl(CourseFilterActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, InstructorNameDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void InstructorNameDetails(String jsonResponse) {


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
                        instructorArrayList.clear();
                        instructorList = new Spinner_List();
                        instructorList.setName(getResources().getString(R.string.hint_instructor));
                        instructorArrayList.add(0,instructorList);

                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                           // instructorList.setName("NA");
                            instructorList.setName(getResources().getString(R.string.all));
                            instructorArrayList.add(1,instructorList);
//                            instructorList.setName("NA");
//                            instructorArrayList.add(2,instructorList);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                instructorList = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Name     = jsonObj.getString("Name");


                                    instructorList.setName(Name);

                                    instructorArrayList.add(instructorList);

                                    instructoradapter = new SpinnerAdapter(CourseFilterActivity.this, instructorArrayList){
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
                                                tv.setText(getResources().getString(R.string.prompt_instructor));
                                                // tv.setTextColor(Color.GRAY);
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
                    instructorList.setName(getResources().getString(R.string.all));
                    instructorArrayList.add(1,instructorList);
                    instructoradapter = new SpinnerAdapter(CourseFilterActivity.this, instructorArrayList){
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
                                tv.setText(getResources().getString(R.string.prompt_instructor));
                                // tv.setTextColor(Color.GRAY);
                            }
                            else {
                                tv.setTextColor(Color.BLACK);
                            }
                            return view;
                        }

                    };
                    spinInstructor.setAdapter(instructoradapter);
                    //forumCount.setVisibility(View.INVISBLE);
                    // queCount.setVisibility(View.INVISIBLE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    public void  SearchEnquiryClass() {
        CourseFilterActivity.SearchEnquiryTrackClass ru = new CourseFilterActivity.SearchEnquiryTrackClass();
        ru.execute("5");
    }


    class SearchEnquiryTrackClass extends AsyncTask<String, Void, String> {

        ServerClass ruc = new ServerClass();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
         //   showProgressDialog();
           // viewDialog.showDialog();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
           // dismissProgressDialog();
          //  viewDialog.hideDialog();
            //Toast.makeText(CandiateListView.this, response, Toast.LENGTH_LONG).show();
            //  Toast.makeText(NewCustomerActivity.this, response, Toast.LENGTH_LONG).show();
            SearchEnquiryDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
           // Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> SearchEnquiryDetails = new HashMap<String, String>();
            SearchEnquiryDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(CourseFilterActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(CourseFilterActivity.this)));
            SearchEnquiryDetails.put("to_date",todate.getText().toString());
            Log.v(TAG, String.format("doInBackground :: to_date = %s",todate.getText().toString() ));
            SearchEnquiryDetails.put("from_date",fromdate.getText().toString());
            Log.v(TAG, String.format("doInBackground :: from_date = %s", fromdate.getText().toString()));
            SearchEnquiryDetails.put("date_wise", Datewise);
            Log.v(TAG, String.format("doInBackground :: Datewise = %s", Datewise));
            SearchEnquiryDetails.put("package_type",packageType);
            Log.v(TAG, String.format("doInBackground :: package_type = %s", packageType));
            SearchEnquiryDetails.put("package_name",packagename);
            Log.v(TAG, String.format("doInBackground :: package_name = %s",packagename));
            SearchEnquiryDetails.put("instructor_name",instructorname);
            Log.v(TAG, String.format("doInBackground :: instructorname = %s",instructorname));
            SearchEnquiryDetails.put("sales_executive",salesExecutiveName);
            Log.v(TAG, String.format("doInBackground :: salesExecutiveName = %s",salesExecutiveName));
            SearchEnquiryDetails.put("action", "search_course_filter");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(CourseFilterActivity.this);
            String loginResult2 = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, SearchEnquiryDetails);

            Log.v(TAG, String.format("doInBackground :: search_course_filter= %s", loginResult2));
            return loginResult2;
        }
    }


    private void SearchEnquiryDetails(String jsonResponse) {

        Log.v(TAG, String.format("search_course_filter :: response = %s", jsonResponse));

        JSONObject object = null;
        try {
            object = new JSONObject(jsonResponse);
            String success = object.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                //Toast.makeText(CourseFilterActivity.this,"Enquiry added succesfully",Toast.LENGTH_SHORT).show();

                if (object != null) {
                    JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }
                    if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                        for (int i = 0; i < jsonArrayResult.length(); i++) {


                            subList = new CourseList();
                            Log.d(TAG, "i: " + i);
                            // Log.d(TAG, "run: " + itemCount);
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
                                String Start_Date = jsonObj.getString("Start_Date");
                                String End_Date = jsonObj.getString("End_Date");
                                String Rate = jsonObj.getString("Rate");
                                String Final_paid = jsonObj.getString("Final_paid");
                                String Final_Balance = jsonObj.getString("Final_Balance");
                                String Image = jsonObj.getString("Image");
                                String Invoice_ID = jsonObj.getString("Invoice_ID");
                                String Tax = jsonObj.getString("Tax");
                                String Member_Email_ID = jsonObj.getString("Member_Email_ID");
                                String Financial_Year = jsonObj.getString("Financial_Year");


                                //  for (int j = 0; j < 5; j++) {
                                //itemCount++;
                               // Log.d(TAG, "run: " + itemCount);
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

                                String cont=Utility.lastFour(Contact);
                                subList.setContact(Contact);
                                subList.setContactEncrypt(cont);
//                                    String pack=Package_Name;
                                subList.setPackageName(Package_Name);
                                subList.setExecutiveName(ExecutiveName);
                                subList.setTax(Tax);
                                String dur_sess="Duration:"+Duration_Days+","+"Session:"+Session;
                                subList.setPackageNameWithDS(dur_sess);
                                String reg_date= Utility.formatDate(RegistrationDate);
                                subList.setRegistrationDate(reg_date);
                                subList.setID(Member_ID);
                                subList.setInvoiceID(Invoice_ID);

                                subList.setRate(Rate);
                                // String fpaid="₹ "+Final_paid;
                                subList.setPaid(Final_paid);
                                if(Final_Balance.equals(".00")){
                                    Final_Balance="0.00";
                                }
                                //String fbalance="₹ "+Final_Balance;
                                subList.setBalance(Final_Balance);
                                Image.replace("\"", "");
                                subList.setImage(Image);
                                subList.setEmail(Member_Email_ID);
                                subList.setFinancialYear(Financial_Year);

                                subListArrayList.add(subList);
                            }
                        }
                        Intent intent=new Intent(CourseFilterActivity.this,CourseActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("filter_array_list", subListArrayList);
                        intent.putExtra("BUNDLE",bundle);
                        startActivity(intent);

                    } else if (jsonArrayResult.length() == 0) {
                        System.out.println("No records found");
                    }
                }

                // showCustomDialog();

                //inputEmail, inputPhone,inputAdd,inputReq,inputFollowupdate;
            }


            else if (success.equalsIgnoreCase(getResources().getString(R.string.zero)))
            {
                //Toast.makeText(CourseFilterActivity.this,"Mobile Number Already Exits",Toast.LENGTH_SHORT).show();
                //inputContact.getText().clear();
                Toast.makeText(CourseFilterActivity.this,"No Records Found",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(CourseFilterActivity.this, CourseActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(CourseFilterActivity.this,CourseActivity.class);
        startActivity(intent);
    }
}
