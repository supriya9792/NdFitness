package com.ndfitnessplus.Activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.ndfitnessplus.Adapter.SearchContactAdapter;
import com.ndfitnessplus.Adapter.SearchNameAdapter;
import com.ndfitnessplus.Model.Search_list;
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
import java.util.Locale;

public class AddMeasurementActivity extends AppCompatActivity {
    public EditText  inputWeight, inputHeight, inputAge, inputBMI, inputFat, inputNeck, inputShoulder,
            inputChest, inputArmsR, inputNextFollDate, inputArmsL, inputForarms, inputWaist, inputHips, inputThighR, inputThighL, inputCalfR, inputCalfL;
    public TextInputLayout inputLayoutContact, inputLayoutName, inputLayoutWeight, inputLayoutHeight, inputLayoutAge, inputLayoutBMI,
            inputLayoutFat, inputLayoutNeck, inputLayoutShoulder, inputLayoutChest, inputLayoutArmsR, inputLayoutNextFollDate,
            inputLayoutArmsL, inputLayoutForarms, inputLayoutWaist, inputLayoutHips, inputLayoutThighR, inputLayoutThighL, inputLayoutCalfR, inputLayoutCalfL;

    public final String TAG = AddMeasurementActivity.class.getName();
    private ProgressDialog pd;
    private AwesomeValidation awesomeValidation;
    private int mYear, mMonth, mDay;
    String afterEnquirySms, MemberName;
    String MemberID;
    //Loading gif
    ViewDialog viewDialog;
     double heightm;
    Search_list searchModel;
    ArrayList<Search_list> searchArrayList = new ArrayList<Search_list>();
    public SearchNameAdapter searchnameadapter;
    SearchContactAdapter searchcontactadapter;
    AutoCompleteTextView inputContact ,inputName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_add_measurement);
        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.add_measurement));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }

    private void initComponent() {
        //Contact number
        inputLayoutContact = (TextInputLayout) findViewById(R.id.input_layout_cont);
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutWeight = (TextInputLayout) findViewById(R.id.input_layout_weight);
        inputLayoutHeight = (TextInputLayout) findViewById(R.id.input_layout_height);
        inputLayoutAge = (TextInputLayout) findViewById(R.id.input_layout_age);
        inputLayoutBMI = (TextInputLayout) findViewById(R.id.input_layout_bmi);
        inputLayoutFat = (TextInputLayout) findViewById(R.id.input_layout_fat);
        inputLayoutNeck = (TextInputLayout) findViewById(R.id.input_layout_neck);
        inputLayoutShoulder = (TextInputLayout) findViewById(R.id.input_layout_shoulder);
        inputLayoutChest = (TextInputLayout) findViewById(R.id.input_layout_chest);
        inputLayoutArmsR = (TextInputLayout) findViewById(R.id.input_layout_armsr);
        inputLayoutArmsL = (TextInputLayout) findViewById(R.id.input_layout_armsl);
        inputLayoutForarms = (TextInputLayout) findViewById(R.id.input_layout_forarms);
        inputLayoutWaist = (TextInputLayout) findViewById(R.id.input_layout_waist);
        inputLayoutHips = (TextInputLayout) findViewById(R.id.input_layout_hips);
        inputLayoutThighR = (TextInputLayout) findViewById(R.id.input_layout_thighr);
        inputLayoutThighL = (TextInputLayout) findViewById(R.id.input_layout_thighl);
        inputLayoutCalfR = (TextInputLayout) findViewById(R.id.input_layout_calfr);
        inputLayoutCalfL = (TextInputLayout) findViewById(R.id.input_layout_calfl);
        inputLayoutNextFollDate = (TextInputLayout) findViewById(R.id.input_layout_next_foll_date);

        inputContact = (AutoCompleteTextView) findViewById(R.id.input_cont);
        inputName = (AutoCompleteTextView) findViewById(R.id.input_name);
        inputWeight = (EditText) findViewById(R.id.input_weight);
        inputHeight = (EditText) findViewById(R.id.input_height);
        inputAge = (EditText) findViewById(R.id.input_age);
        inputBMI = (EditText) findViewById(R.id.input_bmi);
        inputFat = (EditText) findViewById(R.id.input_fat);
        inputNeck = (EditText) findViewById(R.id.input_neck);
        inputShoulder = (EditText) findViewById(R.id.input_shoulder);
        inputChest = (EditText) findViewById(R.id.input_chest);
        inputArmsR = (EditText) findViewById(R.id.input_armsr);
        inputArmsL = (EditText) findViewById(R.id.input_armsl);
        inputForarms = (EditText) findViewById(R.id.input_forarms);
        inputWaist = (EditText) findViewById(R.id.input_waist);
        inputHips = (EditText) findViewById(R.id.input_hips);
        inputThighR = (EditText) findViewById(R.id.input_thighr);
        inputThighL = (EditText) findViewById(R.id.input_thighl);
        inputCalfR = (EditText) findViewById(R.id.input_calfr);
        inputCalfL = (EditText) findViewById(R.id.input_calfl);
        inputNextFollDate = (EditText) findViewById(R.id.input_nextfollDate);

        inputBMI.setKeyListener(null);
        viewDialog = new ViewDialog(this);


        // *********** validation *************
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);


        awesomeValidation.addValidation(this, R.id.input_cont, RegexTemplate.NOT_EMPTY, R.string.err_msg_cont);
        awesomeValidation.addValidation(this, R.id.input_name, RegexTemplate.NOT_EMPTY, R.string.err_msg_name);
        awesomeValidation.addValidation(this, R.id.input_nextfollDate, RegexTemplate.NOT_EMPTY, R.string.err_msg_next_foll_date);

        Intent intent = getIntent();
        if (intent != null) {
            String contact=intent.getStringExtra("contact");
            inputContact.setText(contact);


        }
        inputContact.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(inputContact.getText().length()>0){
                    CheckContactClass();
                }


            }
        });
        inputContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String countryName = searchcontactadapter.getItem(position).getCustName();
                String contact = searchcontactadapter.getItem(position).getCustContact();
                MemberID = searchcontactadapter.getItem(position).getMemberId();

                inputName.setText(countryName);
                inputContact.setText(contact);


            }
        });
        inputContact.addTextChangedListener(new TextWatcher() {
            //
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }



            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {
                if(inputContact.getText().length()==0){
                    inputName.getText().clear();
                }
            }
        });
        showSearchListClass();
        inputName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                String countryName = searchnameadapter.getItem(position).getCustName();
                String contact = searchnameadapter.getItem(position).getCustContact();
                MemberID = searchnameadapter.getItem(position).getMemberId();


                inputName.setText(countryName);
                inputContact.setText(contact);

            }
        });
        inputName.addTextChangedListener(new TextWatcher() {
            //
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }



            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {
                if(inputName.getText().length()==0){
                    inputContact.getText().clear();
                }
            }
        });
        String curr_date = Utility.getCurrentDate();
        inputNextFollDate.setText(curr_date);

        inputNextFollDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                awesomeValidation.clear();
                InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputNextFollDate.getWindowToken(), 0);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddMeasurementActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                inputNextFollDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                // tiemPicker();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        // ************** BMI Calculation ***************
        inputHeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(inputHeight.getText().length()>0){
                   String heightstr= inputHeight.getText().toString();
                       double height=Double.parseDouble(heightstr);

                      heightm= height/100;
                    double weight=0;
                      if(inputWeight.getText().length() >0){
                          String weightstr= inputWeight.getText().toString();
                           weight=Double.parseDouble(weightstr);
                      }


                      double BMI =weight/(heightm*heightm);
                     String bmi =String.format(Locale.CANADA,"%.2f", BMI);
                      inputBMI.setText(bmi);
                }


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
    private void submitForm() {

            if (awesomeValidation.validate()) {
                    AddMeasurementClass();
                }
            }
    //   ************** Check Contact number already exist or not **************
    public void  CheckContactClass() {
        AddMeasurementActivity.CheckContactTrackClass ru = new AddMeasurementActivity.CheckContactTrackClass();
        ru.execute("5");
    }

    class CheckContactTrackClass extends AsyncTask<String, Void, String> {

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

            CheckContactDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> EnquiryForDetails = new HashMap<String, String>();

            EnquiryForDetails.put("mobileno",inputContact.getText().toString() );
            EnquiryForDetails.put("user","Member" );
            EnquiryForDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(AddMeasurementActivity.this) );
            EnquiryForDetails.put("action", "check_mobile_already_exist_or_not");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(AddMeasurementActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, EnquiryForDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }
    }
    private void CheckContactDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject object = null;
        try {
            object = new JSONObject(jsonResponse);
            String success = object.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.zero))) {
                Toast.makeText(AddMeasurementActivity.this,"Member is not registred. Please register Member first",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(AddMeasurementActivity.this,AddMemberActivity.class);
                intent.putExtra("contact",inputContact.getText().toString());
                startActivity(intent);

            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.two)))
            {
                JSONArray jsonArrayResult = object.getJSONArray("Data");

                if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                    for (int i = 0; i < jsonArrayResult.length(); i++) {

                        JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                        if (jsonObj != null) {

                            MemberID = jsonObj.getString("MemberID");
                            String Name = jsonObj.getString("Name");
                            inputName.setText(Name);


                        }
                    }
                } else if (jsonArrayResult.length() == 0) {
                    System.out.println("No records found");
                }
            }else if (success.equalsIgnoreCase(getResources().getString(R.string.one)))
            {
                JSONArray jsonArrayResult = object.getJSONArray("Data");

                if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                    for (int i = 0; i < jsonArrayResult.length(); i++) {

                        JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                        if (jsonObj != null) {

                            MemberID = jsonObj.getString("MemberID");
                            String Name = jsonObj.getString("Name");
                            inputName.setText(Name);


                        }
                    }
                } else if (jsonArrayResult.length() == 0) {
                    System.out.println("No records found");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void  AddMeasurementClass() {
        AddMeasurementActivity.AddMeasurementTrackClass ru = new AddMeasurementActivity.AddMeasurementTrackClass();
        ru.execute("5");
    }
    class AddMeasurementTrackClass extends AsyncTask<String, Void, String> {

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
            AddMeasurementDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {

            HashMap<String, String> AddMeasurementDetails = new HashMap<String, String>();
            AddMeasurementDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(AddMeasurementActivity.this));
            AddMeasurementDetails.put("member_id",MemberID);
            AddMeasurementDetails.put("name",inputName.getText().toString());
            AddMeasurementDetails.put("contact",inputContact.getText().toString());
            AddMeasurementDetails.put("weight",inputWeight.getText().toString() );
            AddMeasurementDetails.put("height",inputHeight.getText().toString());
            AddMeasurementDetails.put("age",inputAge.getText().toString());
            AddMeasurementDetails.put("bmi", inputBMI.getText().toString());
            AddMeasurementDetails.put("fat",inputFat.getText().toString());
            AddMeasurementDetails.put("neck",inputNeck.getText().toString());
            AddMeasurementDetails.put("shoulder",inputShoulder.getText().toString());
            AddMeasurementDetails.put("chest",inputChest.getText().toString());
            AddMeasurementDetails.put("arms_r",inputArmsR.getText().toString());
            AddMeasurementDetails.put("arms_l",inputArmsL.getText().toString());
            AddMeasurementDetails.put("forarms",inputForarms.getText().toString());
            AddMeasurementDetails.put("waist",inputWaist.getText().toString());
            Log.v(TAG, String.format("doInBackground :: discount = %s", inputWaist.getText().toString()));
            AddMeasurementDetails.put("hips",inputHips.getText().toString());
            AddMeasurementDetails.put("thigh_r",inputThighR.getText().toString());

            AddMeasurementDetails.put("thigh_l",inputThighL.getText().toString());
            AddMeasurementDetails.put("calf_r",inputCalfR.getText().toString());
            AddMeasurementDetails.put("calf_l",inputCalfL.getText().toString());
            AddMeasurementDetails.put("next_foll_date",inputNextFollDate.getText().toString());
            AddMeasurementDetails.put("exe_name",SharedPrefereneceUtil.getName(AddMeasurementActivity.this));
            Log.v(TAG, String.format("doInBackground :: executive name= %s", SharedPrefereneceUtil.getName(AddMeasurementActivity.this)));
            AddMeasurementDetails.put("action", "add_measurement");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(AddMeasurementActivity.this);
            String loginResult2 = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, AddMeasurementDetails);
            return loginResult2;
        }
    }


    private void AddMeasurementDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject jsonObjLoginResponse = null;
        try {
            jsonObjLoginResponse = new JSONObject(jsonResponse);
            String success = jsonObjLoginResponse.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                Toast.makeText(AddMeasurementActivity.this,"Measurement added succesfully",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(AddMeasurementActivity.this,MeasurementActivity.class);
                startActivity(intent);
            }


            else if (success.equalsIgnoreCase(getResources().getString(R.string.zero)))
            {
                Toast.makeText(AddMeasurementActivity.this,"Something Went wrong",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void  showSearchListClass() {
        AddMeasurementActivity.SearchTrackClass ru = new AddMeasurementActivity.SearchTrackClass();
        ru.execute("5");
    }
    private   class SearchTrackClass extends AsyncTask<String, Void, String> {

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

            SearchDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> SearchDetails = new HashMap<String, String>();

            SearchDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(AddMeasurementActivity.this) );
            SearchDetails.put("action", "show_all_member_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(AddMeasurementActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, SearchDetails);
            Log.v(TAG, String.format("doInBackground :: show_all_member_list= %s", loginResult));
            return loginResult;
        }


    }


    private void SearchDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {


            try {
                JSONObject object = new JSONObject(jsonResponse);
                if (object != null) {
                    JSONArray jsonArrayResult = object.getJSONArray("result");

                    if (jsonArrayResult != null && jsonArrayResult.length() > 0){
                        for (int i = 0; i < jsonArrayResult.length(); i++) {
                            searchModel = new Search_list();
                            JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                            if (jsonObj != null) {

                                String Name     = jsonObj.getString("Name");
                                String Contact     = jsonObj.getString("Contact");
                                String MemberID     = jsonObj.getString("MemberID");
                                String Email = jsonObj.getString("Email");
                                String Gender = jsonObj.getString("Gender");


                                String namec=Name+"-"+Contact;
                                searchModel.setCustName(Name);
                                searchModel.setCustContact(Contact);
                                searchModel.setMemberId(MemberID);
                                searchModel.setEmail(Email);
                                searchModel.setGender(Gender);
                                searchModel.setNameContact(namec);

                                searchArrayList.add(searchModel);
                                searchnameadapter = new SearchNameAdapter(AddMeasurementActivity.this, searchArrayList);

                                inputName.setAdapter(searchnameadapter);
                                inputName.setThreshold(1);

                                searchcontactadapter = new SearchContactAdapter(AddMeasurementActivity.this, searchArrayList);

                                inputContact.setAdapter(searchcontactadapter);
                                inputContact.setThreshold(1);



                            }
                        }}else if(jsonArrayResult.length()==0){
                        System.out.println("No records found");
                    }
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(AddMeasurementActivity.this, MeasurementActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(AddMeasurementActivity.this,MeasurementActivity.class);
        startActivity(intent);
    }

}
