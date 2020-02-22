package com.ndfitnessplus.Activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.ndfitnessplus.Activity.Notification.TodaysEnrollmentActivity;
import com.ndfitnessplus.Adapter.AddEnquirySpinnerAdapter;
import com.ndfitnessplus.Adapter.SpinnerAdapter;
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

public class AddExpensesActivity extends AppCompatActivity  {
    private EditText inputExepnseDate, inputTitleExpense,inputPaymentDetails, inputAmount, inputDisc;
    private TextInputLayout inputLayoutExepnseDate,inputLayoutTitleExpense,inputLayoutPaymentDetails, inputLayoutAmount,inputLayoutDisc;
    private Button submit,close;
    public final String TAG = AddExpensesActivity.class.getName();
    private ProgressDialog pd;
    private ProgressBar progressBar;
    private int mYear, mMonth, mDay;
    //Spinner Adapter
    public Spinner spinExpenseGroup,spinPaymentype;
    Spinner_List expensegrplist,paymentTypeList;
    ArrayList<Spinner_List> expensegrpArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> paymentTypeArrayList = new ArrayList<Spinner_List>();
    public AddEnquirySpinnerAdapter expenseGroupeadapter,paymentTypeadapter;
    String expenseGroup,paymentType;
    TextView txtExpenseGrp,txtPaymentType;
    private AwesomeValidation awesomeValidation;
    //Loading gif
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_add_expenses);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.add_exepnses));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private  void initComponent(){
        inputLayoutExepnseDate = (TextInputLayout) findViewById(R.id.input_layout_exp_date);
        inputLayoutTitleExpense = (TextInputLayout) findViewById(R.id.input_layout_ttl_of_expenses);
        inputLayoutPaymentDetails = (TextInputLayout) findViewById(R.id.input_layout_payment_details);
        inputLayoutAmount = (TextInputLayout) findViewById(R.id.input_layout_amt);
        inputLayoutDisc = (TextInputLayout) findViewById(R.id.input_layout_payment_disc);

        inputExepnseDate = (EditText) findViewById(R.id.input_exp_date);
        inputTitleExpense = (EditText) findViewById(R.id.input_ttl_of_expenses);
        inputPaymentDetails = (EditText) findViewById(R.id.input_payment_details);
        inputAmount = (EditText) findViewById(R.id.input_amt);
        inputDisc=(EditText)findViewById(R.id.input_payment_disc);
        //spinners
        spinExpenseGroup = (Spinner) findViewById(R.id.spinner_exp_group);
        spinPaymentype = (Spinner) findViewById(R.id.spinner_payment_type);

        txtExpenseGrp=findViewById(R.id.txt_exp_group);
        txtPaymentType=findViewById(R.id.txt_payment_type);
        viewDialog = new ViewDialog(this);

        //defining AwesomeValidation object
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        awesomeValidation.addValidation(this, R.id.input_exp_date, RegexTemplate.NOT_EMPTY, R.string.err_msg_expe_date);
        awesomeValidation.addValidation(this, R.id.input_ttl_of_expenses,RegexTemplate.NOT_EMPTY, R.string.err_msg_ttl_of_expense);
        awesomeValidation.addValidation(this, R.id.input_payment_details,RegexTemplate.NOT_EMPTY, R.string.err_msg_payment_dtl);
        awesomeValidation.addValidation(this, R.id.input_amt,RegexTemplate.NOT_EMPTY, R.string.err_msg_amt);
        awesomeValidation.addValidation(this, R.id.input_payment_disc,RegexTemplate.NOT_EMPTY, R.string.err_msg_payment_disc);

        String curr_date = Utility.getCurrentDate();
        inputExepnseDate.setText(curr_date);
        inputExepnseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(AddExpensesActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                inputExepnseDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });


        //api for binding masters
        ExpenseGroupClass();
        PaymenttypeClass();

        //setting data to the spinners

        spinExpenseGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                    expenseGroup = tv.getText().toString();
                    if (index != 0) {
                        txtExpenseGrp.setVisibility(View.VISIBLE);
                    }
                    if (!expenseGroup.equals(getResources().getString(R.string.exepnses_group))) {
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //setting data to the spinners

        spinPaymentype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                    paymentType = tv.getText().toString();
                    if (index != 0) {
                        txtPaymentType.setVisibility(View.VISIBLE);
                    }
                    if (!paymentType.equals(getResources().getString(R.string.hint_pyment_mode))) {
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

            submitForm();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void  ExpenseGroupClass() {
        AddExpensesActivity.ExpenseGroupTrackClass ru = new AddExpensesActivity.ExpenseGroupTrackClass();
        ru.execute("5");
    }


    private void submitForm() {
        if (awesomeValidation.validate()) {
            if(expenseGroup.equals(getResources().getString(R.string.exepnses_group)) || paymentType.equals(getResources().getString(R.string.hint_pyment_mode))
            ){           Toast.makeText(this, "Please fill all fields", Toast.LENGTH_LONG).show();
            }else{
                AddExpenseClass();
            }
            //process the data further
        }else{
            Toast.makeText(this, "Validation Failed", Toast.LENGTH_LONG).show();

        }

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
            String domainurl=SharedPrefereneceUtil.getDomainUrl(AddExpensesActivity.this);
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
                        expensegrpArrayList.clear();
                        expensegrplist = new Spinner_List();
                        expensegrplist.setName(getResources().getString(R.string.exepnses_group));
                        expensegrpArrayList.add(0,expensegrplist);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                expensegrplist = new Spinner_List();

                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Exepense     = jsonObj.getString("Exepense");

                                    String id=jsonObj.getString("Auto_Id");
                                    expensegrplist.setName(Exepense);
                                    expensegrplist.setId(id);

                                    expensegrpArrayList.add(expensegrplist);

                                    expenseGroupeadapter = new AddEnquirySpinnerAdapter(AddExpensesActivity.this, expensegrpArrayList){
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
                                    spinExpenseGroup.setAdapter(expenseGroupeadapter);


                                }
                            }
                        }else if(jsonArrayCountry.length()==0){
                            System.out.println("No records found");
                        }
                    }
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    public void  PaymenttypeClass() {
        AddExpensesActivity.PaymentTypeTrackClass ru = new AddExpensesActivity.PaymentTypeTrackClass();
        ru.execute("5");
    }
    class PaymentTypeTrackClass extends AsyncTask<String, Void, String> {

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

            PaymentTypeDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {

            HashMap<String, String> PaymentTypeDetails = new HashMap<String, String>();
            PaymentTypeDetails.put("action", "show_payment_type_list");

            String domainurl=SharedPrefereneceUtil.getDomainUrl(AddExpensesActivity.this);
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
                        paymentTypeArrayList.clear();
                        paymentTypeList = new Spinner_List();
                        paymentTypeList.setName(getResources().getString(R.string.hint_pyment_mode));
                        paymentTypeArrayList.add(0,paymentTypeList);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                paymentTypeList = new Spinner_List();

                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String PaymentType     = jsonObj.getString("PaymentType");

                                    String id=jsonObj.getString("Auto_Id");

                                    paymentTypeList.setName(PaymentType);
                                    paymentTypeList.setId(id);

                                    paymentTypeArrayList.add(paymentTypeList);

                                    paymentTypeadapter = new AddEnquirySpinnerAdapter(AddExpensesActivity.this, paymentTypeArrayList){
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
                                    spinPaymentype.setAdapter(paymentTypeadapter);


                                }
                            }
                        }else if(jsonArrayCountry.length()==0){
                            System.out.println("No records found");
                        }
                    }
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }

    public void  AddExpenseClass() {
        AddExpensesActivity.AddExpenseTrackClass ru = new AddExpensesActivity.AddExpenseTrackClass();
        ru.execute("5");
    }





    class AddExpenseTrackClass extends AsyncTask<String, Void, String> {

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
            AddExpenseDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> AddExpenseDetails = new HashMap<String, String>();
            AddExpenseDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(AddExpensesActivity.this));
            AddExpenseDetails.put("expense_date",inputExepnseDate.getText().toString());
            AddExpenseDetails.put("ttl_of_expense",inputTitleExpense.getText().toString());
            AddExpenseDetails.put("expense_grp", expenseGroup);
            AddExpenseDetails.put("payment_dtl",inputPaymentDetails.getText().toString());
            AddExpenseDetails.put("amount",inputAmount.getText().toString());
            AddExpenseDetails.put("exe_name",SharedPrefereneceUtil.getUserNm(AddExpensesActivity.this));
            AddExpenseDetails.put("payment_type",paymentType);
            AddExpenseDetails.put("disc",inputDisc.getText().toString());
            AddExpenseDetails.put("action", "add_expenses");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(AddExpensesActivity.this);
            String loginResult2 = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, AddExpenseDetails);

            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult2));
            return loginResult2;
        }
    }


    private void AddExpenseDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject jsonObjLoginResponse = null;
        try {
            jsonObjLoginResponse = new JSONObject(jsonResponse);
            String success = jsonObjLoginResponse.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                Toast.makeText(AddExpensesActivity.this,"Expense added succesfully",Toast.LENGTH_SHORT).show();
                inputExepnseDate.getText().clear();
                inputTitleExpense.getText().clear();
                inputDisc.getText().clear();
                inputPaymentDetails.getText().clear();
                inputAmount.getText().clear();
                Intent intent=new Intent(AddExpensesActivity.this,ExpensesActivity.class);
                startActivity(intent);
            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.zero)))
            {
                Toast.makeText(AddExpensesActivity.this,"Somethind went wrong",Toast.LENGTH_SHORT).show();
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
