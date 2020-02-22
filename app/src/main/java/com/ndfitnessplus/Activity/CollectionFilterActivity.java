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

public class CollectionFilterActivity extends AppCompatActivity {
    public final String TAG = CollectionFilterActivity.class.getName();
    private ProgressDialog pd;
    //Spinner Adapter
    public Spinner spinDateWise,spinPaymentType,spinSalesExecutive;
    Spinner_List dateWiselist,paymenttypelist,saleExecutiveList;
    ArrayList<Spinner_List> dateWiseArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> paymentTypeArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> SalesExecutiveArrayList = new ArrayList<Spinner_List>();


    public SpinnerAdapter datewiseadapter,paymenttypeadapter,salesExecutiveAdapter;
    String Datewise,paymentType,salesExecutiveName;

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
        setContentView(R.layout.activity_collection_filter);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.coll_filters));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent() {

        //spinners
        spinDateWise = (Spinner) findViewById(R.id.spinner_date_wise);
        spinPaymentType = (Spinner) findViewById(R.id.spinner_payment_type);
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


                DatePickerDialog datePickerDialog = new DatePickerDialog(CollectionFilterActivity.this,
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


                DatePickerDialog datePickerDialog = new DatePickerDialog(CollectionFilterActivity.this,
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
        //api of spinners
        executiveClass();
        PaymenttypeClass();

        final  String[] memberDateArray = getResources().getStringArray(R.array.balance_date_array);

        for(int i=0;i<memberDateArray.length;i++) {
            dateWiselist = new Spinner_List();
            dateWiselist.setName(memberDateArray[i]);
            dateWiseArrayList.add(dateWiselist);
            datewiseadapter = new SpinnerAdapter(CollectionFilterActivity.this, dateWiseArrayList) {
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
        spinPaymentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);
                    if (index == 0) {
                        tv.setText(getResources().getString(R.string.payment_type));
                    }
//                tv.setTextColor(getResources().getColor(R.color.black));
                    paymentType = tv.getText().toString();
                    if ((paymentType.equals(getResources().getString(R.string.payment_type))) ||
                            (paymentType.equals(getResources().getString(R.string.all)))) {
                        paymentType = "";
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
        btn_applyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchCollectionClass();
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
//        String date = "03/26/2012 11:00:00";
//        String dateafter = "03/26/2012 11:59:00";
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy");
        Date convertedDate = new Date();
        Date convertedDate2 = new Date();
        try {
            convertedDate = dateFormat.parse(fromdate.getText().toString());
            convertedDate2 = dateFormat.parse(todate.getText().toString());
            if (convertedDate2.before(convertedDate) || convertedDate2.equals(convertedDate)) {
                //.setText("true");
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
    private void showProgressDialog() {
        Log.v(TAG, String.format("showProgressDialog"));
        pd = new ProgressDialog(CollectionFilterActivity.this);
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
    // *************** Payment Mode *******************
    public void  PaymenttypeClass() {
        CollectionFilterActivity.PaymentTypeTrackClass ru = new CollectionFilterActivity.PaymentTypeTrackClass();
        ru.execute("5");
    }
    class PaymentTypeTrackClass extends AsyncTask<String, Void, String> {

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
            PaymentTypeDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
          //  Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> PaymentTypeDetails = new HashMap<String, String>();
            PaymentTypeDetails.put("action", "show_payment_type_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(CollectionFilterActivity.this);
            //PaymentTypeloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(PaymentTypeloyee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, PaymentTypeDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void PaymentTypeDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    if (object != null) {
                        JSONArray jsonArrayCountry = object.getJSONArray("result");
                        paymentTypeArrayList.clear();
                        paymenttypelist = new Spinner_List();
                        paymenttypelist.setName(getResources().getString(R.string.payment_type));
                        paymentTypeArrayList.add(0,paymenttypelist);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            paymenttypelist.setName(getResources().getString(R.string.all));
                            paymentTypeArrayList.add(1,paymenttypelist);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                paymenttypelist = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String PaymentType     = jsonObj.getString("PaymentType");

                                    String id=jsonObj.getString("Auto_Id");

                                    paymenttypelist.setName(PaymentType);
                                    paymenttypelist.setId(id);

                                    paymentTypeArrayList.add(paymenttypelist);

                                    paymenttypeadapter = new SpinnerAdapter(CollectionFilterActivity.this,
                                            paymentTypeArrayList){
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
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinPaymentType.setAdapter(paymenttypeadapter);


                                }
                            }
                        }else if(jsonArrayCountry.length()==0){
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){

                    paymentTypeArrayList.clear();
                    paymenttypelist = new Spinner_List();
                    paymenttypelist.setName(getResources().getString(R.string.payment_type));
                    paymentTypeArrayList.add(0,paymenttypelist);
                    paymenttypelist.setName(getResources().getString(R.string.all));
                    paymentTypeArrayList.add(1,paymenttypelist);
                    paymenttypeadapter = new SpinnerAdapter(CollectionFilterActivity.this,
                            paymentTypeArrayList){
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
                            }
                            else {
                                tv.setTextColor(Color.BLACK);
                            }
                            return view;
                        }

                    };
                    spinPaymentType.setAdapter(paymenttypeadapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void  executiveClass() {
        CollectionFilterActivity.ExecutiveNameTrackClass ru = new CollectionFilterActivity.ExecutiveNameTrackClass();
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
            String comp_name= SharedPrefereneceUtil.getCompanyName(CollectionFilterActivity.this);
            String location=SharedPrefereneceUtil.getSelectedBranch(CollectionFilterActivity.this);
            String compid=comp_name+"-"+location+",";
            ExecutiveNameDetails.put("comp_id", compid);
            ExecutiveNameDetails.put("action", "show_executive_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(CollectionFilterActivity.this);
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

                                    salesExecutiveAdapter = new SpinnerAdapter(CollectionFilterActivity.this, SalesExecutiveArrayList){
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
                    //for(int j=0;j<2;j++){
                    saleExecutiveList = new Spinner_List();

                    saleExecutiveList.setName(getResources().getString(R.string.prompt_executive));

                    SalesExecutiveArrayList.add(0,saleExecutiveList);
                    saleExecutiveList.setName(getResources().getString(R.string.all));

                    SalesExecutiveArrayList.add(1,saleExecutiveList);
                    salesExecutiveAdapter = new SpinnerAdapter(CollectionFilterActivity.this, SalesExecutiveArrayList){
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
    public void  SearchCollectionClass() {
        CollectionFilterActivity.SearchCollectionTrackClass ru = new CollectionFilterActivity.SearchCollectionTrackClass();
        ru.execute("5");
    }


    class SearchCollectionTrackClass extends AsyncTask<String, Void, String> {

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
            SearchCollectionDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> SearchCollectionDetails = new HashMap<String, String>();
            SearchCollectionDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(CollectionFilterActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(CollectionFilterActivity.this)));
            SearchCollectionDetails.put("to_date",todate.getText().toString());
            Log.v(TAG, String.format("doInBackground :: to_date = %s",todate.getText().toString() ));
            SearchCollectionDetails.put("from_date",fromdate.getText().toString());
            Log.v(TAG, String.format("doInBackground :: from_date = %s", fromdate.getText().toString()));
            SearchCollectionDetails.put("date_wise", Datewise);
            Log.v(TAG, String.format("doInBackground :: date_wise = %s", Datewise));
            SearchCollectionDetails.put("payment_type",paymentType);
            Log.v(TAG, String.format("doInBackground :: paymentType = %s", paymentType));
            SearchCollectionDetails.put("exe_name",salesExecutiveName);
            SearchCollectionDetails.put("offset","0");
            Log.v(TAG, String.format("doInBackground :: exe_name = %s",salesExecutiveName));
            SearchCollectionDetails.put("action", "search_collection_filter");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(CollectionFilterActivity.this);
            String loginResult2 = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, SearchCollectionDetails);

            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult2));
            return loginResult2;
        }
    }


    private void SearchCollectionDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject object = null;
        try {
            object = new JSONObject(jsonResponse);
            String success = object.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                String col = object.getString("collection");
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
                                String ReceiptDate = jsonObj.getString("ReceiptDate");
                                String Contact = jsonObj.getString("Contact");
                                String PaymentType = jsonObj.getString("PaymentType");
                                String ExecutiveName = jsonObj.getString("ExecutiveName");
                                String Member_ID = jsonObj.getString("Member_ID");
                                String Final_paid = jsonObj.getString("Paid");
                                String Final_Balance = jsonObj.getString("RemainingBalance");
                                String Image = jsonObj.getString("Image");
                                String Invoice_ID = jsonObj.getString("Invoice_ID");
                                String Receipt_Id = jsonObj.getString("Receipt_Id");
                                String Tax = jsonObj.getString("Tax");
                                String Member_Email_ID = jsonObj.getString("Member_Email_ID");
                                String Next_payment_date = jsonObj.getString("NextPaymentDate");
                                String ReceiptType = jsonObj.getString("ReceiptType");
                                String PaymentDetails = jsonObj.getString("PaymentDetails");

                                String Start_Date = jsonObj.getString("Start_Date");
                                String End_Date = jsonObj.getString("End_Date");
                                String Rate = jsonObj.getString("Rate");
                                String RegistrationDate = jsonObj.getString("RegistrationDate");
                                String Package_Name = jsonObj.getString("Package_Name");
                                String Duration_Days = jsonObj.getString("Duration_Days");
                                String Session = jsonObj.getString("Session");
                                String Financial_Year = jsonObj.getString("Financial_Year");


                                subList.setName(name);

                                String cont=Utility.lastFour(Contact);
                                subList.setContact(Contact);
                                subList.setContactEncrypt(cont);
                                subList.setPaymentType(PaymentType);
                                subList.setExecutiveName(ExecutiveName);
                                subList.setTax(Tax);
                                String rec_date= Utility.formatDate(ReceiptDate);
                                subList.setReceiptDate(rec_date);
                                subList.setID(Member_ID);
                                subList.setInvoiceID(Invoice_ID);
                                subList.setReceiptId(Receipt_Id);
                                if(Final_paid.equals(".00")){
                                    Final_paid="0.00";
                                }
                                String paid="₹ "+Final_paid;
                                subList.setPaid(paid);
                                if(Final_Balance.equals(".00")){
                                    Final_Balance="0.00";
                                }
                                String nextpaydate=Utility.formatDate(Next_payment_date);
                                String balance="₹ "+Final_Balance;
                                subList.setBalance(Final_Balance);
                                subList.setBalanceRuppe(balance);
                                Image.replace("\"", "");
                                subList.setImage(Image);
                                subList.setEmail(Member_Email_ID);

                                subList.setNextPaymentdate(nextpaydate);
                                subList.setReceiptType(ReceiptType);
                                subList.setPaymentDetails(PaymentDetails);

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

                                subList.setPackageName(Package_Name);
                                String dur_sess="Duration:"+Duration_Days+","+"Session:"+Session;
                                subList.setPackageNameWithDS(dur_sess);
                                String reg_date= Utility.formatDate(RegistrationDate);
                                subList.setRegistrationDate(reg_date);
                                subList.setRate(Rate);
                                subList.setFinancialYear(Financial_Year);
                                subListArrayList.add(subList);


                            }
                        }
                        Intent intent=new Intent(CollectionFilterActivity.this, CollectionActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("filter_array_list", subListArrayList);
                        intent.putExtra("BUNDLE",bundle);
                        intent.putExtra("collection",rt);
                        startActivity(intent);

                    } else if (jsonArrayResult.length() == 0) {
                        System.out.println("No records found");
                    }
                }
            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.zero)))
            {
                Toast.makeText(CollectionFilterActivity.this,"No Records Found",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(CollectionFilterActivity.this, CollectionActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(CollectionFilterActivity.this,CollectionActivity.class);
        startActivity(intent);
    }
}
