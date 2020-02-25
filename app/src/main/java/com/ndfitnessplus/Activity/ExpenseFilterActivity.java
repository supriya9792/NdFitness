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
import com.ndfitnessplus.Model.ExpensesList;
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

public class ExpenseFilterActivity extends AppCompatActivity {

    public final String TAG = ExpenseFilterActivity.class.getName();
    private ProgressDialog pd;
    //Spinner Adapter
    public Spinner spinTitleExpense,spinGroupofExpense,spinPaymentType,spinExecutive;
    Spinner_List ttlExpenselist,group_of_Expenselist,paymentTypeList,ExecutiveNameList;
    ArrayList<Spinner_List> ttlExpenseArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> groupofExpenseArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> PaymenttypeArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> ExecutiveNameArrayList = new ArrayList<Spinner_List>();

    public SpinnerAdapter ttlExpenseadapter,GroupOfExpenseadapter,paymenttypeadapter,executivenameadapter;
    String ttlExpense,GroupofExpense,PaymentType,executiveName;

    TextView todate,fromdate;
    ImageButton toDatebtn,fromDateBtn;
    private int mYear, mMonth, mDay;

    ArrayList<ExpensesList> subListArrayList = new ArrayList<ExpensesList>();
    ExpensesList subList;
    Button btn_applyFilter;
    //Loading gif
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_expense_filter);

        initToolbar();
   }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.expense_filters));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent() {
        spinTitleExpense = (Spinner) findViewById(R.id.spinner_ttl_expense);
        spinGroupofExpense = (Spinner) findViewById(R.id.spinner_exp_group);
        spinPaymentType = (Spinner) findViewById(R.id.spinner_payment_type);
        spinExecutive = (Spinner) findViewById(R.id.spinner_executive);
        todate=findViewById(R.id.to_date);
        fromdate=findViewById(R.id.from_date);
        fromDateBtn=findViewById(R.id.btn_from_date);
        toDatebtn=findViewById(R.id.btn_to_date);
        btn_applyFilter=findViewById(R.id.btn_apply_filters);
        viewDialog = new ViewDialog(this);
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


                DatePickerDialog datePickerDialog = new DatePickerDialog(ExpenseFilterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                todate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
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


                DatePickerDialog datePickerDialog = new DatePickerDialog(ExpenseFilterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                fromdate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                CampareFronTwoDates();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        //api's for spinners
        ttlOfExpenseClass();
        PaymenttypeClass();
        executiveClass();
        ExpenseGroupClass();

        spinTitleExpense.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);
                    if (index == 0) {
                        tv.setText(getResources().getString(R.string.prompt_ttl_expense));
                    }
                    ttlExpense = tv.getText().toString();
                    if ((ttlExpense.equals(getResources().getString(R.string.prompt_ttl_expense))) ||
                            (ttlExpense.equals(getResources().getString(R.string.all)))) {
                        ttlExpense = "";
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinGroupofExpense.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);
                    if (index == 0) {
                        tv.setText(getResources().getString(R.string.prompt_expense_grp));
                    }
                    GroupofExpense = tv.getText().toString();
                    if ((GroupofExpense.equals(getResources().getString(R.string.prompt_expense_grp))) ||
                            (GroupofExpense.equals(getResources().getString(R.string.all)))) {
                        GroupofExpense = "";
                    }
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
                    PaymentType = tv.getText().toString();
                    if ((PaymentType.equals(getResources().getString(R.string.payment_type))) ||
                            (PaymentType.equals(getResources().getString(R.string.all)))) {
                        PaymentType = "";
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinExecutive.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if(view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);
                    if(index==0){
                        tv.setText(getResources().getString(R.string.prompt_executive));
                    }
                    executiveName = tv.getText().toString();
                    if((executiveName.equals(getResources().getString(R.string.prompt_executive)))||
                            (executiveName.equals(getResources().getString(R.string.all)))){
                        executiveName="";
                    }
                }}

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btn_applyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchExpenseClass();
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
    // *************** Payment Mode *******************
    public void  PaymenttypeClass() {
        ExpenseFilterActivity.PaymentTypeTrackClass ru = new ExpenseFilterActivity.PaymentTypeTrackClass();
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
            HashMap<String, String> PaymentTypeDetails = new HashMap<String, String>();
            PaymentTypeDetails.put("action", "show_payment_type_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(ExpenseFilterActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, PaymentTypeDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void PaymentTypeDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {


            try {
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    if (object != null) {
                        JSONArray jsonArrayCountry = object.getJSONArray("result");
                        PaymenttypeArrayList.clear();
                        paymentTypeList = new Spinner_List();
                        paymentTypeList.setName(getResources().getString(R.string.payment_type));
                        PaymenttypeArrayList.add(0,paymentTypeList);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            paymentTypeList.setName(getResources().getString(R.string.all));
                            PaymenttypeArrayList.add(1,paymentTypeList);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                paymentTypeList = new Spinner_List();

                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String PaymentType     = jsonObj.getString("PaymentType");

                                    String id=jsonObj.getString("Auto_Id");

                                    paymentTypeList.setName(PaymentType);
                                    paymentTypeList.setId(id);

                                    PaymenttypeArrayList.add(paymentTypeList);

                                    paymenttypeadapter = new SpinnerAdapter(ExpenseFilterActivity.this, PaymenttypeArrayList){
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
                    PaymenttypeArrayList.clear();
                    paymentTypeList = new Spinner_List();
                    paymentTypeList.setName(getResources().getString(R.string.payment_type));
                    PaymenttypeArrayList.add(0,paymentTypeList);
                    paymentTypeList.setName(getResources().getString(R.string.all));
                    PaymenttypeArrayList.add(1,paymentTypeList);
                    paymenttypeadapter = new SpinnerAdapter(ExpenseFilterActivity.this, PaymenttypeArrayList){
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
        ExpenseFilterActivity.ExecutiveNameTrackClass ru = new ExpenseFilterActivity.ExecutiveNameTrackClass();
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
            String comp_name= SharedPrefereneceUtil.getCompanyName(ExpenseFilterActivity.this);
            String location=SharedPrefereneceUtil.getSelectedBranch(ExpenseFilterActivity.this);
            String compid=comp_name+"-"+location+",";
            ExecutiveNameDetails.put("comp_id", compid);
            ExecutiveNameDetails.put("action", "show_executive_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(ExpenseFilterActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, ExecutiveNameDetails);
            return loginResult;
        }


    }


    private void ExecutiveNameDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {

            try {
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    if (object != null) {
                        JSONArray jsonArrayCountry = object.getJSONArray("result");
                        ExecutiveNameArrayList.clear();

                        ExecutiveNameList = new Spinner_List();
                        ExecutiveNameList.setName(getResources().getString(R.string.prompt_executive));
                        ExecutiveNameArrayList.add(0,ExecutiveNameList);

                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            ExecutiveNameList.setName(getResources().getString(R.string.all));

                            ExecutiveNameArrayList.add(1,ExecutiveNameList);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                ExecutiveNameList = new Spinner_List();

                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String EnquiryOwnerExecutive     = jsonObj.getString("EnquiryOwnerExecutive");

                                    ExecutiveNameList.setName(EnquiryOwnerExecutive);
                                    ExecutiveNameArrayList.add(ExecutiveNameList);
                                    executivenameadapter = new SpinnerAdapter(ExpenseFilterActivity.this, ExecutiveNameArrayList){
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
                                    spinExecutive.setAdapter(executivenameadapter);


                                }
                            }
                        }else if(jsonArrayCountry.length()==0){
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    ExecutiveNameArrayList.clear();
                    ExecutiveNameList = new Spinner_List();

                    ExecutiveNameList.setName(getResources().getString(R.string.prompt_executive));

                    ExecutiveNameArrayList.add(0,ExecutiveNameList);
                    ExecutiveNameList.setName(getResources().getString(R.string.all));

                    ExecutiveNameArrayList.add(1,ExecutiveNameList);
                    executivenameadapter = new SpinnerAdapter(ExpenseFilterActivity.this, ExecutiveNameArrayList){
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
                    spinExecutive.setAdapter(executivenameadapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void  ExpenseGroupClass() {
        ExpenseFilterActivity.ExpenseGroupTrackClass ru = new ExpenseFilterActivity.ExpenseGroupTrackClass();
        ru.execute("5");
    }
    class ExpenseGroupTrackClass extends AsyncTask<String, Void, String> {

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
            ExpenseGroupDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> ExpenseGroupDetails = new HashMap<String, String>();
            ExpenseGroupDetails.put("action", "show_expense_group_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(ExpenseFilterActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, ExpenseGroupDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void ExpenseGroupDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {


            try {
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    if (object != null) {
                        JSONArray jsonArrayCountry = object.getJSONArray("result");
                        groupofExpenseArrayList.clear();
                        group_of_Expenselist = new Spinner_List();
                        group_of_Expenselist.setName(getResources().getString(R.string.prompt_expense_grp));
                        groupofExpenseArrayList.add(0,group_of_Expenselist);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            group_of_Expenselist.setName(getResources().getString(R.string.all));

                            groupofExpenseArrayList.add(1,group_of_Expenselist);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                group_of_Expenselist = new Spinner_List();

                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Exepense     = jsonObj.getString("Exepense");

                                    String id=jsonObj.getString("Auto_Id");
                                    group_of_Expenselist.setName(Exepense);
                                    group_of_Expenselist.setId(id);

                                    groupofExpenseArrayList.add(group_of_Expenselist);

                                    GroupOfExpenseadapter = new SpinnerAdapter(ExpenseFilterActivity.this, groupofExpenseArrayList){
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
                                                tv.setText(getResources().getString(R.string.prompt_expense_grp));
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinGroupofExpense.setAdapter(GroupOfExpenseadapter);


                                }
                            }
                        }else if(jsonArrayCountry.length()==0){
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    groupofExpenseArrayList.clear();
                    group_of_Expenselist = new Spinner_List();
                    group_of_Expenselist.setName(getResources().getString(R.string.prompt_expense_grp));
                    groupofExpenseArrayList.add(0,group_of_Expenselist);
                    group_of_Expenselist.setName(getResources().getString(R.string.all));

                    groupofExpenseArrayList.add(1,group_of_Expenselist);
                    GroupOfExpenseadapter = new SpinnerAdapter(ExpenseFilterActivity.this, groupofExpenseArrayList){
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
                                tv.setText(getResources().getString(R.string.prompt_expense_grp));
                            }
                            else {
                                tv.setTextColor(Color.BLACK);
                            }
                            return view;
                        }

                    };
                    spinGroupofExpense.setAdapter(GroupOfExpenseadapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void ttlOfExpenseClass() {
        ExpenseFilterActivity.TitleExpenseTrackClass ru = new ExpenseFilterActivity.TitleExpenseTrackClass();
        ru.execute("5");
    }
    class TitleExpenseTrackClass extends AsyncTask<String, Void, String> {

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
            TitleExpenseDetails(response);
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> TitleExpenseDetails = new HashMap<String, String>();
            TitleExpenseDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(ExpenseFilterActivity.this));
            TitleExpenseDetails.put("action", "show_title_expense_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(ExpenseFilterActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, TitleExpenseDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void TitleExpenseDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {


            try {
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    if (object != null) {
                        JSONArray jsonArrayCountry = object.getJSONArray("result");
                        ttlExpenseArrayList.clear();
                        ttlExpenselist = new Spinner_List();
                        ttlExpenselist.setName(getResources().getString(R.string.prompt_ttl_expense));
                        ttlExpenseArrayList.add(0,ttlExpenselist);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            ttlExpenselist.setName(getResources().getString(R.string.all));

                            ttlExpenseArrayList.add(1,ttlExpenselist);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                ttlExpenselist = new Spinner_List();

                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String TitleExpences     = jsonObj.getString("TitleExpences");

                                    ttlExpenselist.setName(TitleExpences);

                                    ttlExpenseArrayList.add(ttlExpenselist);

                                    ttlExpenseadapter = new SpinnerAdapter(ExpenseFilterActivity.this, ttlExpenseArrayList){
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
                                                tv.setText(getResources().getString(R.string.prompt_ttl_expense));
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinTitleExpense.setAdapter(ttlExpenseadapter);


                                }
                            }
                        }else if(jsonArrayCountry.length()==0){
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    ttlExpenseArrayList.clear();
                    ttlExpenselist = new Spinner_List();
                    ttlExpenselist.setName(getResources().getString(R.string.prompt_ttl_expense));
                    ttlExpenseArrayList.add(0,ttlExpenselist);
                    ttlExpenselist.setName(getResources().getString(R.string.all));

                    ttlExpenseArrayList.add(1,ttlExpenselist);
                    ttlExpenseadapter = new SpinnerAdapter(ExpenseFilterActivity.this, ttlExpenseArrayList){
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
                                tv.setText(getResources().getString(R.string.prompt_ttl_expense));
                            }
                            else {
                                tv.setTextColor(Color.BLACK);
                            }
                            return view;
                        }

                    };
                    spinTitleExpense.setAdapter(ttlExpenseadapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void  SearchExpenseClass() {
        ExpenseFilterActivity.SearchExpenseTrackClass ru = new ExpenseFilterActivity.SearchExpenseTrackClass();
        ru.execute("5");
    }


    class SearchExpenseTrackClass extends AsyncTask<String, Void, String> {

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
            SearchExpenseDetails(response);

        }
        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> SearchExpenseDetails = new HashMap<String, String>();
            SearchExpenseDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(ExpenseFilterActivity.this));
            SearchExpenseDetails.put("to_date",todate.getText().toString());
            SearchExpenseDetails.put("from_date",fromdate.getText().toString());
            SearchExpenseDetails.put("ttl_of_expense", ttlExpense);
            SearchExpenseDetails.put("payment_type",PaymentType);
            SearchExpenseDetails.put("exe_name",executiveName);
            SearchExpenseDetails.put("group_of_expenses",GroupofExpense);
            SearchExpenseDetails.put("action", "search_expense_filter");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(ExpenseFilterActivity.this);
            String loginResult2 = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, SearchExpenseDetails);

            return loginResult2;
        }
    }


    private void SearchExpenseDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject object = null;
        try {
            object = new JSONObject(jsonResponse);
            String success = object.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                String col = object.getString("ttl_expense");
                double ttlcol=Double.parseDouble(col);
                DecimalFormat df = new DecimalFormat("##,##,##,##,##,##,##0.00");
                String rt= df.format(ttlcol);

                if (object != null) {
                    JSONArray jsonArrayResult = object.getJSONArray("result");

                    if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                        for (int i = 0; i < jsonArrayResult.length(); i++) {

                            subList = new ExpensesList();
                            JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                            if (jsonObj != null) {

                                String Expence_Date = jsonObj.getString("Expence_Date");
                                String TitleExpences = jsonObj.getString("TitleExpences");
                                String Description = jsonObj.getString("Description");
                                String GroupOfExpences = jsonObj.getString("GroupOfExpences");
                                String ExecutiveName = jsonObj.getString("ExecutiveName");
                                String Amount = jsonObj.getString("amount");
                                String Payment_Type = jsonObj.getString("Payment_Type");
                                String PaymentDetails = jsonObj.getString("PaymentDetails");
                                String Expence_ID = jsonObj.getString("Expence_ID");

                                subList.setTtl_of_expenses(TitleExpences);
                                subList.setDisc(Description);
                                subList.setExpenses_group(GroupOfExpences);
                                subList.setExecutive_name(ExecutiveName);
                                subList.setAmount(Amount);
                                String exp_date= Utility.formatDate(Expence_Date);
                                subList.setExpenses_date(exp_date);
                                subList.setExpensesId(Expence_ID);
                                subList.setPayment_type(Payment_Type);
                                subList.setPayment_dtl(PaymentDetails);
                                subListArrayList.add(subList);


                            }
                        }
                        Intent intent=new Intent(ExpenseFilterActivity.this, ExpensesActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("filter_array_list", subListArrayList);
                        intent.putExtra("BUNDLE",bundle);
                        intent.putExtra("expense",rt);
                        startActivity(intent);

                    } else if (jsonArrayResult.length() == 0) {
                        System.out.println("No records found");
                    }
                }
            }


            else if (success.equalsIgnoreCase(getResources().getString(R.string.zero)))
            {
                Toast.makeText(ExpenseFilterActivity.this,"No Records Found",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(ExpenseFilterActivity.this, ExpensesActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(ExpenseFilterActivity.this,ExpensesActivity.class);
        startActivity(intent);
    }
}
