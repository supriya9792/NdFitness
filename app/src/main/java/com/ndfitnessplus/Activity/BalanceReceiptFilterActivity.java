package com.ndfitnessplus.Activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ndfitnessplus.Activity.Notification.TodaysEnrollmentActivity;
import com.ndfitnessplus.Adapter.AddEnquirySpinnerAdapter;
import com.ndfitnessplus.Adapter.SpinnerAdapter;
import com.ndfitnessplus.Model.CourseList;
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

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class BalanceReceiptFilterActivity extends AppCompatActivity {
    public final String TAG = BalanceReceiptFilterActivity.class.getName();
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
        setContentView(R.layout.activity_balance_receipt_filter);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.balance_filter));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent() {

        spinDateWise = findViewById(R.id.spinner_date_wise);
        spinPackageType = findViewById(R.id.spinner_package_type);
        spinPackageName = findViewById(R.id.spinner_package_name);
        spinInstructor=findViewById(R.id.spinner_instructor_name);
        spinSalesExecutive = findViewById(R.id.spinner_sales_executive);

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


                DatePickerDialog datePickerDialog = new DatePickerDialog(BalanceReceiptFilterActivity.this,
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


                DatePickerDialog datePickerDialog = new DatePickerDialog(BalanceReceiptFilterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String date=(year + "-"
                                        + (monthOfYear + 1) + "-" + dayOfMonth).toString();
                                String cdate=Utility.formatDateDB(date);
                                fromdate.setText(cdate);
                                CampareFronTwoDates();
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
            datewiseadapter = new SpinnerAdapter(BalanceReceiptFilterActivity.this, dateWiseArrayList) {
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
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }

            };
            spinDateWise.setAdapter(datewiseadapter);
        }
        spinDateWise.setSelection(1);
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

                    Datewise = tv.getText().toString();
                    if ((Datewise.equals(getResources().getString(R.string.prompt_mem_date))) ||
                            (Datewise.equals(getResources().getString(R.string.all)))) {
                        Datewise = "";
                    }

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
                    salesExecutiveName = tv.getText().toString();
                    if((salesExecutiveName.equals(getResources().getString(R.string.prompt_executive)))||
                            (salesExecutiveName.equals(getResources().getString(R.string.all)))){
                        salesExecutiveName="";
                    }
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
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy");
        Date convertedDate = new Date();
        Date convertedDate2 = new Date();
        try {
            convertedDate = dateFormat.parse(todate.getText().toString());
            convertedDate2 = dateFormat.parse(fromdate.getText().toString());
            if (convertedDate2.after(convertedDate) || convertedDate2.equals(convertedDate)) {
            } else {
                String firstday= Utility.getFirstDayofMonth();
                todate.setText(firstday);
                Toast.makeText(this, "From date should not be greater than to date: " , Toast.LENGTH_LONG).show();
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void CampareFronTwoDates(){
        //******************campare two dates****************
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy");
        Date convertedDate = new Date();
        Date convertedDate2 = new Date();
        try {
            convertedDate = dateFormat.parse(fromdate.getText().toString());
            convertedDate2 = dateFormat.parse(todate.getText().toString());
            if (convertedDate2.before(convertedDate) || convertedDate2.equals(convertedDate)) {

            } else {
                String firstday= Utility.getCurrentDate();
                fromdate.setText(firstday);
                Toast.makeText(this, "From date should not be less than to date: " , Toast.LENGTH_LONG).show();
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    // ************* Package Type spinner *******************
    public void  packageTypeClass() {
        BalanceReceiptFilterActivity.PackageTypeTrackClass ru = new BalanceReceiptFilterActivity.PackageTypeTrackClass();
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
            PackageTypeDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptFilterActivity.this));
            PackageTypeDetails.put("action", "show_package_type");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(BalanceReceiptFilterActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, PackageTypeDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void PackageTypeDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
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
                        packagetypelist.setName(getResources().getString(R.string.hint_packagetype_n));
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

                                    packagetypelist.setName(PackageType);

                                    packageTypeArrayList.add(packagetypelist);

                                    packagetypeadapter = new SpinnerAdapter(BalanceReceiptFilterActivity.this, packageTypeArrayList){
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
                    packageTypeArrayList.clear();
                    packagetypelist = new Spinner_List();
                    packagetypelist.setName(getResources().getString(R.string.hint_packagetype_n));
                    packageTypeArrayList.add(0,packagetypelist);
                    packagetypelist.setName(getResources().getString(R.string.all));

                    packageTypeArrayList.add(1,packagetypelist);
                    packagetypeadapter = new SpinnerAdapter(BalanceReceiptFilterActivity.this, packageTypeArrayList){
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
                            }
                            else {
                                tv.setTextColor(Color.BLACK);
                            }
                            return view;
                        }

                    };
                    spinPackageType.setAdapter(packagetypeadapter);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    // ************* Package Name spinner *******************
    public void  packageNameClass() {
        BalanceReceiptFilterActivity.PackageNameTrackClass ru = new BalanceReceiptFilterActivity.PackageNameTrackClass();
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
            PackageNameDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptFilterActivity.this));
            PackageNameDetails.put("action", "show_package_name");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(BalanceReceiptFilterActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, PackageNameDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void PackageNameDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
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
                        packageNamelist.setName(getResources().getString(R.string.hint_package_name_n));
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

                                    packagenameadapter = new SpinnerAdapter(BalanceReceiptFilterActivity.this, packagenameArrayList){
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
                    packagenameArrayList.clear();
                    packageNamelist = new Spinner_List();
                    packageNamelist.setName(getResources().getString(R.string.hint_package_name_n));
                    packagenameArrayList.add(0,packageNamelist);
                    packageNamelist.setName(getResources().getString(R.string.all));

                    packagenameArrayList.add(1,packageNamelist);
                    packagenameadapter = new SpinnerAdapter(BalanceReceiptFilterActivity.this, packagenameArrayList){
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
                            }
                            else {
                                tv.setTextColor(Color.BLACK);
                            }
                            return view;
                        }

                    };
                    spinPackageName.setAdapter(packagenameadapter);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    public void  executiveClass() {
        BalanceReceiptFilterActivity.ExecutiveNameTrackClass ru = new BalanceReceiptFilterActivity.ExecutiveNameTrackClass();
        ru.execute("5");
    }
    class ExecutiveNameTrackClass extends AsyncTask<String, Void, String> {

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
            ExecutiveNameDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> ExecutiveNameDetails = new HashMap<String, String>();
            String comp_name=SharedPrefereneceUtil.getCompanyName(BalanceReceiptFilterActivity.this);
            String location=SharedPrefereneceUtil.getSelectedBranch(BalanceReceiptFilterActivity.this);
            String compid=comp_name+"-"+location+",";
            ExecutiveNameDetails.put("comp_id", compid);
            ExecutiveNameDetails.put("action", "show_executive_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(BalanceReceiptFilterActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, ExecutiveNameDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void ExecutiveNameDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    if (object != null) {
                        JSONArray jsonArrayCountry = object.getJSONArray("result");
                        SalesExecutiveArrayList.clear();

                        saleExecutiveList = new Spinner_List();

                        saleExecutiveList.setName(getResources().getString(R.string.prompt_executive));

                        SalesExecutiveArrayList.add(0,saleExecutiveList);


                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            saleExecutiveList.setName(getResources().getString(R.string.all));

                            SalesExecutiveArrayList.add(1,saleExecutiveList);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                saleExecutiveList = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String EnquiryOwnerExecutive     = jsonObj.getString("EnquiryOwnerExecutive");



                                    saleExecutiveList.setName(EnquiryOwnerExecutive);

                                    SalesExecutiveArrayList.add(saleExecutiveList);

                                    salesExecutiveAdapter = new SpinnerAdapter(BalanceReceiptFilterActivity.this, SalesExecutiveArrayList){
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
                    SalesExecutiveArrayList.clear();

                    saleExecutiveList = new Spinner_List();

                    saleExecutiveList.setName(getResources().getString(R.string.prompt_executive));

                    SalesExecutiveArrayList.add(0,saleExecutiveList);
                    saleExecutiveList.setName(getResources().getString(R.string.all));

                    SalesExecutiveArrayList.add(1,saleExecutiveList);
                    salesExecutiveAdapter = new SpinnerAdapter(BalanceReceiptFilterActivity.this, SalesExecutiveArrayList){
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
                            }
                            else {
                                tv.setTextColor(Color.BLACK);
                            }
                            return view;
                        }

                    };
                    spinSalesExecutive.setAdapter(salesExecutiveAdapter);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    // ************* Instructor Name spinner *******************
    public void  instructorNameClass() {
        BalanceReceiptFilterActivity.InstructorNameTrackClass ru = new BalanceReceiptFilterActivity.InstructorNameTrackClass();
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
            InstructorNameDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptFilterActivity.this));
            InstructorNameDetails.put("action", "show_instructor_name_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(BalanceReceiptFilterActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, InstructorNameDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void InstructorNameDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
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

                            instructorList.setName(getResources().getString(R.string.all));
                            instructorArrayList.add(1,instructorList);

                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                instructorList = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Name     = jsonObj.getString("Name");


                                    instructorList.setName(Name);

                                    instructorArrayList.add(instructorList);

                                    instructoradapter = new SpinnerAdapter(BalanceReceiptFilterActivity.this, instructorArrayList){
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
                    instructoradapter = new SpinnerAdapter(BalanceReceiptFilterActivity.this, instructorArrayList){
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
                            }
                            else {
                                tv.setTextColor(Color.BLACK);
                            }
                            return view;
                        }

                    };
                    spinInstructor.setAdapter(instructoradapter);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    public void  SearchEnquiryClass() {
        BalanceReceiptFilterActivity.SearchEnquiryTrackClass ru = new BalanceReceiptFilterActivity.SearchEnquiryTrackClass();
        ru.execute("5");
    }


    class SearchEnquiryTrackClass extends AsyncTask<String, Void, String> {

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

            SearchEnquiryDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> SearchEnquiryDetails = new HashMap<String, String>();
            SearchEnquiryDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptFilterActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(BalanceReceiptFilterActivity.this)));
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
            SearchEnquiryDetails.put("action", "search_balance_receipt_filter");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(BalanceReceiptFilterActivity.this);
            String loginResult2 = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, SearchEnquiryDetails);

            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult2));
            return loginResult2;
        }
    }


    private void SearchEnquiryDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject object = null;
        try {
            object = new JSONObject(jsonResponse);
            String success = object.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {

                String col = object.getString("balance");

                double ttlcol=Double.parseDouble(col);
                DecimalFormat df = new DecimalFormat("##,##,##,##,##,##,##0.00");
                String rt= df.format(ttlcol);
                if (object != null) {
                    JSONArray jsonArrayResult = object.getJSONArray("result");

                    if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                        for (int i = 0; i < jsonArrayResult.length(); i++) {


                            subList = new CourseList();

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
                                String Next_payment_date = jsonObj.getString("Next_payment_date");
                                String Financial_Year = jsonObj.getString("Financial_Year");


                                subList.setName(name);
                                String sdate= Utility.formatDate(Start_Date);
                                String edate=Utility.formatDate(End_Date);
                                String todate=sdate+" to "+edate;
                                subList.setStartToEndDate(todate);
                                String cont=Utility.lastFour(Contact);
                                subList.setContact(Contact);
                                subList.setContactEncrypt(cont);
                                String pack=Package_Name;
                                subList.setPackageName(pack);
                                String dur_sess="Duration:"+Duration_Days+","+"Session:"+Session;
                                subList.setPackageNameWithDS(dur_sess);
                                subList.setExecutiveName(ExecutiveName);
                                subList.setTax(Tax);
                                String reg_date= Utility.formatDate(RegistrationDate);
                                subList.setRegistrationDate(reg_date);
                                subList.setID(Member_ID);
                                subList.setInvoiceID(Invoice_ID);
                                subList.setRate(Rate);
                                subList.setPaid(Final_paid);
                                if(Final_Balance.equals(".00")){
                                    Final_Balance="0.00";
                                }
                                subList.setBalance(Final_Balance);
                                Image.replace("\"", "");
                                subList.setImage(Image);
                                subList.setEmail(Member_Email_ID);
                                String nextpaydate=Utility.formatDate(Next_payment_date);
                                subList.setNextPaymentdate(nextpaydate);
                                subList.setFinancialYear(Financial_Year);
                                subList.setFollowuptype("Payment");
                                subListArrayList.add(subList);


                            }
                        }
                        Intent intent=new Intent(BalanceReceiptFilterActivity.this,BalanceReceiptDetailsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("filter_array_list", subListArrayList);
                        intent.putExtra("balance",rt);
                        intent.putExtra("BUNDLE",bundle);
                        startActivity(intent);

                    } else if (jsonArrayResult.length() == 0) {
                        System.out.println("No records found");
                    }
                }

            }


            else if (success.equalsIgnoreCase(getResources().getString(R.string.zero)))
            {

                Toast.makeText(BalanceReceiptFilterActivity.this,"No Records Found",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(BalanceReceiptFilterActivity.this, BalanceReceiptDetailsActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(BalanceReceiptFilterActivity.this,BalanceReceiptDetailsActivity.class);
        startActivity(intent);
    }
}
