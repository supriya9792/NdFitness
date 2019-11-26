package com.ndfitnessplus.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.ndfitnessplus.Adapter.AddEnquirySpinnerAdapter;
import com.ndfitnessplus.Adapter.EnquiryAdapter;
import com.ndfitnessplus.Adapter.SearchContactAdapter;
import com.ndfitnessplus.Adapter.SearchNameAdapter;
import com.ndfitnessplus.Adapter.WorkOutDetailsAdapter;
import com.ndfitnessplus.Adapter.WorkoutLevelDetailsAdapter;
import com.ndfitnessplus.Model.EnquiryList;
import com.ndfitnessplus.Model.POSItemList;
import com.ndfitnessplus.Model.Search_list;
import com.ndfitnessplus.Model.Spinner_List;
import com.ndfitnessplus.Model.WorkOutDetailsList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.ViewDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class WorkoutActivity extends AppCompatActivity {
    Search_list searchModel;
    ArrayList<Search_list> searchArrayList = new ArrayList<Search_list>();
    public SearchNameAdapter searchnameadapter;
    SearchContactAdapter searchcontactadapter;

    private TextInputLayout inputLayoutName,inputLayoutContact;
    AutoCompleteTextView inputContact ,inputName;
    public final String TAG = WorkoutActivity.class.getName();
    private ProgressDialog pd;
    private AwesomeValidation awesomeValidation;
    String MemberID,Email,gender;
    //Loading gif
    ViewDialog viewDialog;
    public Spinner spinWorkoutLevel,spinInstructorName;
    Spinner_List workoutLevellist,instructorNamelist;
    ArrayList<Spinner_List> workoutLevelArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> instructorNameArrayList = new ArrayList<Spinner_List>();
    public AddEnquirySpinnerAdapter workoutLeveleadapter,instructorNameadapter;
    String workoutLevel,instructorName;
    TextView txtWorkoutLevel,txtInstructorName;

    WorkoutLevelDetailsAdapter adapter;
    ArrayList<WorkOutDetailsList> subListArrayList = new ArrayList<WorkOutDetailsList>();
    WorkOutDetailsList subList;
    SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    ProgressBar progressBar;

    View nodata,frame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.add_workout));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }

    private void initComponent() {
        //Contact number
        inputLayoutContact = (TextInputLayout) findViewById(R.id.input_layout_cont);
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);

        inputContact = (AutoCompleteTextView) findViewById(R.id.input_cont);
        inputName = (AutoCompleteTextView) findViewById(R.id.input_name);

        spinWorkoutLevel = (Spinner) findViewById(R.id.spinner_workout_level);
        spinInstructorName = (Spinner) findViewById(R.id.spinner_instructor_name);

        txtWorkoutLevel=findViewById(R.id.txt_workout_level);
        txtInstructorName=findViewById(R.id.txt_instructor_name);

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        awesomeValidation.addValidation(this, R.id.input_name, RegexTemplate.NOT_EMPTY, R.string.err_msg_name);
        awesomeValidation.addValidation(this, R.id.input_cont, RegexTemplate.NOT_EMPTY, R.string.err_msg_cont);

       // swipeRefresh=findViewById(R.id.swipeRefresh);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        viewDialog = new ViewDialog(this);
        nodata=findViewById(R.id.nodata);
        frame=findViewById(R.id.main_frame);

        workoutLevelClass();
        instructorNameClass();

        //spinWorkoutLevel.setSelection(1);
        //txtWorkoutLevel.setVisibility(View.VISIBLE);
        spinWorkoutLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if(view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    View layout=(View)view.findViewById(R.id.layout);
                    layout.setPadding(0,0,0,0);
                  if(index ==0){
                      tv.setTextColor((Color.GRAY));
                  }else{
                        tv.setTextColor((Color.BLACK));
                  }
                    workoutLevel = tv.getText().toString();
                    if(index!=0){
                        txtWorkoutLevel.setVisibility(View.VISIBLE);
                        subListArrayList.clear();
                        workout_detailsclass();
                    }
                    if(!workoutLevel.equals(getResources().getString(R.string.workout_level))){
                        //  awesomeValidation.addValidation(AddEnquiryActivity.this, R.id.spinner_enq_for,RegexTemplate.NOT_EMPTY, R.string.err_msg_next_foll_date);
                    }
                    // ((TextView) spinEnquiryType.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
                    // Showing selected spinner item
                    //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Toast.makeText(parent.getContext(), "Please Select Enquiry For ", Toast.LENGTH_LONG).show();
            }
        });
        spinWorkoutLevel.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputName.getWindowToken(), 0);
                return false;
            }
        }) ;
        spinInstructorName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                    instructorName = tv.getText().toString();
                    if (index != 0) {
                        txtInstructorName.setVisibility(View.VISIBLE);

                    }

                }
                // ((TextView) spinInstructorName.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
                // Showing selected spinner item
                //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(parent.getContext(), "Please Select Package Type ", Toast.LENGTH_LONG).show();
            }
        });
        spinInstructorName.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputContact.getWindowToken(), 0);
                return false;
            }
        }) ;
        inputContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // String selection = (String)parent.getItemAtPosition(position);
                // Toast.makeText(MainNavigationActivity.this,"this is autocomplete suggestions"+selection,Toast.LENGTH_SHORT).show();
                String countryName = searchcontactadapter.getItem(position).getCustName();
                String contact = searchcontactadapter.getItem(position).getCustContact();
                MemberID = searchcontactadapter.getItem(position).getMemberId();
                Email=searchcontactadapter.getItem(position).getEmail();
                gender=searchcontactadapter.getItem(position).getGender();

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
                    //radioGroup.clearCheck();
                }
            }
        });
        showSearchListClass();
        inputName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                // String selection = (String)parent.getItemAtPosition(position);
                // Toast.makeText(MainNavigationActivity.this,"this is autocomplete suggestions"+selection,Toast.LENGTH_SHORT).show();
                String countryName = searchnameadapter.getItem(position).getCustName();
                String contact = searchnameadapter.getItem(position).getCustContact();
                MemberID = searchnameadapter.getItem(position).getMemberId();

                Email=searchcontactadapter.getItem(position).getEmail();
                gender=searchcontactadapter.getItem(position).getGender();



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

    }
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
                    if(workoutLevel.equals(getResources().getString(R.string.workout_level)) || instructorName.equals(getResources().getString(R.string.hint_instructor))
                           ){
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_LONG).show();
                    }else{
                        AddWorkoutClass();
                    }
                }else{
                    Toast.makeText(this, "Validation Failed", Toast.LENGTH_LONG).show();
                }
    }
    public void  workoutLevelClass() {
        WorkoutActivity.WorkoutLevelTrackClass ru = new WorkoutActivity.WorkoutLevelTrackClass();
        ru.execute("5");
    }
    class WorkoutLevelTrackClass extends AsyncTask<String, Void, String> {

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
            WorkoutLevelDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //  Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> WorkoutLevelDetails = new HashMap<String, String>();
            WorkoutLevelDetails.put("action", "show_workout_level_master");
            WorkoutLevelDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(WorkoutActivity.this));
            String domainurl= SharedPrefereneceUtil.getDomainUrl(WorkoutActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, WorkoutLevelDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void WorkoutLevelDetails(String jsonResponse) {


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
                        workoutLevelArrayList.clear();
                        workoutLevellist = new Spinner_List();
                        workoutLevellist.setName(getResources().getString(R.string.workout_level));
                        workoutLevelArrayList.add(0,workoutLevellist);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
//                            workoutLevellist.setName(getResources().getString(R.string.na));
//                            workoutLevelArrayList.add(1,workoutLevellist);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                workoutLevellist = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String WorkoutplanName     = jsonObj.getString("WorkoutplanName");

                                    String id=jsonObj.getString("Auto_Id");
//                               if(i==0){
//                                   workoutLevellist.setName(getResources().getString(R.string.promt_country));
//                                   enqF.add(0,workoutLevellist);
//                               }
                                    workoutLevellist.setName(WorkoutplanName);
                                    workoutLevellist.setId(id);

                                    workoutLevelArrayList.add(workoutLevellist);

                                    workoutLeveleadapter = new AddEnquirySpinnerAdapter(WorkoutActivity.this, workoutLevelArrayList){
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
                                                tv.setText(getResources().getString(R.string.prompt_workout_level));
                                                // tv.setTextColor(Color.GRAY);
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinWorkoutLevel.setAdapter(workoutLeveleadapter);


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
    public void  instructorNameClass() {
        WorkoutActivity.InstructorNameTrackClass ru = new WorkoutActivity.InstructorNameTrackClass();
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
            InstructorNameDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(WorkoutActivity.this));
            InstructorNameDetails.put("action", "show_dietition_name_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(WorkoutActivity.this);
            //InstructorNameloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(InstructorNameloyee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, InstructorNameDetails);
            Log.v(TAG, String.format("doInBackground :: show_dietition_name_list= %s", loginResult));
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
                        instructorNameArrayList.clear();
                        instructorNamelist = new Spinner_List();
                        instructorNamelist.setName(getResources().getString(R.string.hint_instructor));
                        instructorNameArrayList.add(0,instructorNamelist);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                instructorNamelist = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Name     = jsonObj.getString("Name");

//                               if(i==0){
//                                   instructorNamelist.setName(getResources().getString(R.string.promt_country));
//                                   enqF.add(0,instructorNamelist);
//                               }
                                    instructorNamelist.setName(Name);

                                    instructorNameArrayList.add(instructorNamelist);

                                    instructorNameadapter = new AddEnquirySpinnerAdapter(WorkoutActivity.this, instructorNameArrayList){
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
                                    spinInstructorName.setAdapter(instructorNameadapter);


                                }
                            }
                        }else if(jsonArrayCountry.length()==0){
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WorkoutActivity.this);
                    builder.setMessage("Add Staff First");
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
    public void  showSearchListClass() {
        WorkoutActivity.SearchTrackClass ru = new WorkoutActivity.SearchTrackClass();
        ru.execute("5");
    }
    private   class SearchTrackClass extends AsyncTask<String, Void, String> {

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
            //dismissProgressDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            SearchDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            // Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> SearchDetails = new HashMap<String, String>();

            SearchDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(WorkoutActivity.this) );
            SearchDetails.put("action", "show_all_member_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(WorkoutActivity.this);
            //EmployeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(Employee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, SearchDetails);
            Log.v(TAG, String.format("doInBackground :: show_all_member_list= %s", loginResult));
            return loginResult;
        }


    }


    private void SearchDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
//        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayoutPrabhagDetails);
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                if (object != null) {
                    JSONArray jsonArrayResult = object.getJSONArray("result");

                    if (jsonArrayResult != null && jsonArrayResult.length() > 0){
                        for (int i = 0; i < jsonArrayResult.length(); i++) {
                            searchModel = new Search_list();
                            Log.v(TAG, "JsonResponseOpeartion ::");
                            JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                            if (jsonObj != null) {

                                String Name     = jsonObj.getString("Name");
                                String Contact     = jsonObj.getString("Contact");
                                String MemberID     = jsonObj.getString("MemberID");
                                String Email = jsonObj.getString("Email");
                                String Gender = jsonObj.getString("Gender");


                                //  String email = jsonObj.getString("email");
                                // String phn_no = jsonObj.getString("mobile");


                                searchModel.setCustName(Name);
                                searchModel.setCustContact(Contact);
                                searchModel.setMemberId(MemberID);
                                searchModel.setEmail(Email);
                                searchModel.setGender(Gender);

                                searchArrayList.add(searchModel);
                                searchnameadapter = new SearchNameAdapter(WorkoutActivity.this, searchArrayList);

                                inputName.setAdapter(searchnameadapter);
                                // inputName.setDropDownBackgroundResource(R.drawable.search_background);
                                inputName.setThreshold(1);

                                searchcontactadapter = new SearchContactAdapter(WorkoutActivity.this, searchArrayList);

                                inputContact.setAdapter(searchcontactadapter);
                                // textContact.setDropDownBackgroundResource(R.drawable.search_background);
                                inputContact.setThreshold(1);

                                //searchnameadapter = new SearchAdapter(MainNavigationActivity.this, searchArrayList);
                                //text.setAdapter(searchnameadapter);
                                // text.setDropDownBackgroundResource(R.drawable.layoutborder);
                                // text.setThreshold(1);


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

//    //*************** Add enquiry to database *************
//    public void  AddEnquiryClass() {
//        WorkoutActivity.AddEnquiryTrackClass ru = new WorkoutActivity.AddEnquiryTrackClass();
//        ru.execute("5");
//    }
//    private void showProgressDialog() {
//        Log.v(TAG, String.format("showProgressDialog"));
//        pd = new ProgressDialog(WorkoutActivity.this);
//        pd.setMessage("loading");
//        pd.setCancelable(false);
//        pd.show();
//    }
//
//    /**
//     * Dismiss Progress Dialog.
//     */
//    private void dismissProgressDialog() {
//        Log.v(TAG, String.format("dismissProgressDialog"));
//        pd.cancel();
//
//
//    }
//
//
//
//    class AddEnquiryTrackClass extends AsyncTask<String, Void, String> {
//
//        ServerClass ruc = new ServerClass();
//
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            Log.v(TAG, "onPreExecute");
//            // showProgressDialog();
//            viewDialog.showDialog();
//        }
//
//        @Override
//        protected void onPostExecute(String response) {
//            super.onPostExecute(response);
//            Log.v(TAG, String.format("onPostExecute :: response = %s", response));
//            //dismissProgressDialog();
//            viewDialog.hideDialog();
//            //Toast.makeText(CandiateListView.this, response, Toast.LENGTH_LONG).show();
//            //  Toast.makeText(NewCustomerActivity.this, response, Toast.LENGTH_LONG).show();
//            AddEnquiryDetails(response);
//
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
//            HashMap<String, String> AddEnquiryDetails = new HashMap<String, String>();
//            AddEnquiryDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(WorkoutActivity.this));
//            AddEnquiryDetails.put("name",inputName.getText().toString());
//            AddEnquiryDetails.put("contact",inputContact.getText().toString());
//            AddEnquiryDetails.put("add",inputAdd.getText().toString());
//            AddEnquiryDetails.put("email",inputEmail.getText().toString());
//            AddEnquiryDetails.put("location",inputLocation.getText().toString());
//            AddEnquiryDetails.put("gender", gender);
//            AddEnquiryDetails.put("occupation",occupation);
//            AddEnquiryDetails.put("enq_type",enquiryType);
//            AddEnquiryDetails.put("enq_source",enquirySource);
//            AddEnquiryDetails.put("enq_for",enquiryFor);
//            AddEnquiryDetails.put("comment",inputComment.getText().toString());
//            AddEnquiryDetails.put("foll_type","Enquiry");
//            AddEnquiryDetails.put("rating",Rating);
//            AddEnquiryDetails.put("call_res",callResponce);
//            AddEnquiryDetails.put("nextFoll_date",inputNextFollowupdate.getText().toString());
//            Log.v(TAG, String.format("doInBackground :: next followup date= %s", inputNextFollowupdate.getText().toString()));
//            AddEnquiryDetails.put("dob",inputDOB.getText().toString());
//            AddEnquiryDetails.put("budget",inputBudget.getText().toString());
//            AddEnquiryDetails.put("image_path",ConvertImage);
//            AddEnquiryDetails.put("exe_name",SharedPrefereneceUtil.getName(WorkoutActivity.this));
//            AddEnquiryDetails.put("mode","AdminApp");
//            Log.v(TAG, String.format("doInBackground :: executive name= %s", SharedPrefereneceUtil.getName(WorkoutActivity.this)));
//            AddEnquiryDetails.put("action", "add_enquiry");
//            String domainurl=SharedPrefereneceUtil.getDomainUrl(WorkoutActivity.this);
//            String loginResult2 = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, AddEnquiryDetails);
//
//            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult2));
//            return loginResult2;
//        }
//    }
//
//
//    private void AddEnquiryDetails(String jsonResponse) {
//
//        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));
//
//        JSONObject jsonObjLoginResponse = null;
//        try {
//            jsonObjLoginResponse = new JSONObject(jsonResponse);
//            String success = jsonObjLoginResponse.getString(getResources().getString(R.string.success));
//
//            if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
//                Toast.makeText(WorkoutActivity.this,"Enquiry added succesfully",Toast.LENGTH_SHORT).show();
//                inputName.getText().clear();
//
//            }
//
//
//            else if (success.equalsIgnoreCase(getResources().getString(R.string.one)))
//            {
//                Toast.makeText(WorkoutActivity.this,"Contact Already Exits",Toast.LENGTH_SHORT).show();
//                inputContact.getText().clear();
//                // Toast.makeText(WorkoutActivity.this,"Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

private void workout_detailsclass() {
    WorkoutActivity.WorkoutDetailsTrackclass ru = new WorkoutActivity.WorkoutDetailsTrackclass();
    ru.execute("5");
}
    class WorkoutDetailsTrackclass extends AsyncTask<String, Void, String> {

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
           //progressBar.setVisibility(View.GONE);
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            WorkoutDetailsDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> WorkoutDetailsDetails = new HashMap<String, String>();
            WorkoutDetailsDetails.put("level_name", workoutLevel);
            WorkoutDetailsDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(WorkoutActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getCompanyAutoId(WorkoutActivity.this)));
            WorkoutDetailsDetails.put("action","show_workout_level_details");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(WorkoutActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, WorkoutDetailsDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }

    private void WorkoutDetailsDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
//        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayoutPrabhagDetails);
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    nodata.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                   //subListArrayList.clear();
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");

                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                            for (int i = 0; i < jsonArrayResult.length(); i++) {
                                subList = new WorkOutDetailsList();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String LevelName = jsonObj.getString("LevelName");
                                    String Workoutname = jsonObj.getString("Workoutname");
                                    String Sets = jsonObj.getString("Sets");
                                    String Repetations = jsonObj.getString("Repetations");
                                    String Time = jsonObj.getString("Time");
                                    // String Weight = jsonObj.getString("Weight");
                                    String Days = jsonObj.getString("Days");
                                    String Bodypart = jsonObj.getString("Bodypart");
                                    String Description = jsonObj.getString("Description");
                                    String Image = jsonObj.getString("Image");
                                    String Vediolink = jsonObj.getString("Vediolink");


                                    subList.setLevelName(LevelName);
                                    subList.setWorkoutName(Workoutname);
                                    String sets="Sets : "+Sets;
                                    subList.setSet(Sets);
                                    String rep="Repetations : "+Repetations;
                                    subList.setRepitation(Repetations);
                                    subList.setTime(Time);
                                     subList.setDiscription(Description);
                                    subList.setDay(Days);
                                    subList.setBodyPart(Bodypart);
                                    subList.setVideoLink(Vediolink);
                                    subList.setWorkoutImage(Image);

                                    //Toast.makeText(MainActivity.this, "j "+j, Toast.LENGTH_SHORT).show();
                                    subListArrayList.add(subList);
                                    adapter = new WorkoutLevelDetailsAdapter(WorkoutActivity.this, subListArrayList);
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
            }
        }
    }
    public void  AddWorkoutClass() {

        WorkoutActivity.AddWorkoutTrackClass ru = new WorkoutActivity.AddWorkoutTrackClass();
        ru.execute("5");

    }

    class AddWorkoutTrackClass extends AsyncTask<String, Void, String> {

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
            // dismissProgressDialog();
            //viewDialog.hideDialog();
            //Toast.makeText(CandiateListView.this, response, Toast.LENGTH_LONG).show();
            //  Toast.makeText(NewCustomerActivity.this, response, Toast.LENGTH_LONG).show();
            AddWorkoutDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {

            JSONObject JSONproduct=null;
            JSONArray jsonArray = new JSONArray();
            for (WorkOutDetailsList data : subListArrayList)
            {
                JSONproduct = new JSONObject();
                try {

                    JSONproduct.put("level_name",data.getLevelName());
                    JSONproduct.put("days",data.getDay());
                    JSONproduct.put("sets",data.getSet());
                    JSONproduct.put("repitations",data.getRepitation());
                    JSONproduct.put("workout_name",data.getWorkoutName());
                    JSONproduct.put("time",data.getTime());
                    JSONproduct.put("bodypart",data.getBodyPart());
                    JSONproduct.put("instructor_name",instructorName);
                    JSONproduct.put("image",data.getWorkoutImage());
                    JSONproduct.put("video_link",data.getVideoLink());
                    JSONproduct.put("description",data.getDiscription());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(JSONproduct);

            }
            JSONObject finalobject = new JSONObject();
            try {
                finalobject.put("workout", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //  Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> AddWorkoutDetails = new HashMap<String, String>();
            AddWorkoutDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(WorkoutActivity.this));
            Log.v(TAG, String.format("doInBackground :: comp_id= %s",SharedPrefereneceUtil.getSelectedBranchId(WorkoutActivity.this)));
            AddWorkoutDetails.put("member_id",MemberID);
            Log.v(TAG, String.format("doInBackground :: member_id= %s",MemberID));
            AddWorkoutDetails.put("member_name", inputName.getText().toString());
            Log.v(TAG, String.format("doInBackground :: member_name _id= %s",inputName.getText().toString()));
            AddWorkoutDetails.put("contact", inputContact.getText().toString());
            Log.v(TAG, String.format("doInBackground :: contact _id= %s",inputContact.getText().toString()));
            AddWorkoutDetails.put("email_id", Email);
            Log.v(TAG, String.format("doInBackground :: email _id= %s",Email));
            AddWorkoutDetails.put("gender", gender);
            Log.v(TAG, String.format("doInBackground :: gender= %s",gender));
            AddWorkoutDetails.put("tbl_count", String.valueOf(subListArrayList.size()));
            Log.v(TAG, String.format("doInBackground :: tbl_count= %s",String.valueOf(subListArrayList.size())));
            AddWorkoutDetails.put("tbl_arr",finalobject.toString());
            Log.v(TAG, String.format("doInBackground :: tbl_arr= %s",finalobject.toString()));
            AddWorkoutDetails.put("action", "add_workout");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(WorkoutActivity.this);
            String loginResult2 = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, AddWorkoutDetails);

            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult2));
            return loginResult2;
        }
    }


    private void AddWorkoutDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject jsonObjLoginResponse = null;
        try {
            jsonObjLoginResponse = new JSONObject(jsonResponse);
            String success = jsonObjLoginResponse.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                Toast.makeText(WorkoutActivity.this,"Workout added succesfully",Toast.LENGTH_SHORT).show();

               // int result=db.remove_from_cart(cartarrayList);
                //Log.v(TAG, String.format("response :: result= %s",result));
                finish();
                Intent intent=new Intent(WorkoutActivity.this,WorkoutActivity.class);
                startActivity(intent);
                // showCustomDialog();

                //inputEmail, inputPhone,inputAdd,inputReq,inputFollowupdate;
            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.zero)))
            {
                Toast.makeText(WorkoutActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                //inputContact.getText().clear();
                // Toast.makeText(WorkoutActivity.this,"Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
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
