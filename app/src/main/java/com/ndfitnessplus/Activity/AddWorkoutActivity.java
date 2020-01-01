package com.ndfitnessplus.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.ndfitnessplus.Adapter.AddEnquirySpinnerAdapter;
import com.ndfitnessplus.Adapter.SearchContactAdapter;
import com.ndfitnessplus.Adapter.SearchNameAdapter;
import com.ndfitnessplus.Adapter.WorkoutLevelAdapter;
import com.ndfitnessplus.Adapter.WorkoutLevelDetailsAdapter;
import com.ndfitnessplus.MailUtility.Mail;
import com.ndfitnessplus.Model.Search_list;
import com.ndfitnessplus.Model.Spinner_List;
import com.ndfitnessplus.Model.WorkOutDayList;
import com.ndfitnessplus.Model.WorkOutDetailsList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.Utility;
import com.ndfitnessplus.Utility.ViewDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class AddWorkoutActivity extends AppCompatActivity {
    Search_list searchModel;
    ArrayList<Search_list> searchArrayList = new ArrayList<Search_list>();
    public SearchNameAdapter searchnameadapter;
    SearchContactAdapter searchcontactadapter;

    private TextInputLayout inputLayoutName,inputLayoutContact;
    AutoCompleteTextView inputContact ,inputName;
    public final String TAG = AddWorkoutActivity.class.getName();
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
    String workoutLevel;
        String instructorName="";
    TextView txtWorkoutLevel,txtInstructorName;

    WorkoutLevelDetailsAdapter adapter;
    ArrayList<WorkOutDetailsList> subListArrayList = new ArrayList<WorkOutDetailsList>();
    ArrayList<WorkOutDetailsList> subListArrayList1 = new ArrayList<WorkOutDetailsList>();
    WorkOutDetailsList subList;
    WorkOutDetailsList subList1;
    SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    ProgressBar progressBar;

    View nodata,frame;
    //Pdf generator
    private BaseFont bfBold,bfnormal;
    private File pdfFile;;
    String FilePath;
    String fname ="";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String LOG_TAG = "GeneratePDF";
    String Email_ID,Password,Header,Footer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);
        initToolbar();
        requestPermission();
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.v(LOG_TAG, "External Storage not available or you don't have permission to write");
        }
        else {
            //path for the PDF file in the external storage
            // pdfFile = new File(getExternalFilesDir(filepath), filename);
//            String root = Environment.getExternalStoragePublicDirectory().getAbsolutePath();
            String root = Environment.getExternalStorageDirectory().getPath();
            File myDir = new File(root + "/Workout");
            myDir.mkdirs();
            long n = System.currentTimeMillis() / 1000L;
            fname = "Workout" + n + ".pdf";
            FilePath = root + "/Workout/" + fname;
            pdfFile = new File(myDir, fname);
            if (pdfFile.exists())
                pdfFile.delete();

            try {
                pdfFile.createNewFile();
                FileOutputStream out = new FileOutputStream(pdfFile);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            MediaScannerConnection.scanFile(this, new String[]{pdfFile.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.assign_workout));
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
                        subListArrayList1.clear();
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

                // String selection = (String)parent.getItemAtPosition(position);
                // Toast.makeText(MainNavigationActivity.this,"this is autocomplete suggestions"+selection,Toast.LENGTH_SHORT).show();
                String countryName = searchcontactadapter.getItem(position).getCustName();
                String contact = searchcontactadapter.getItem(position).getCustContact();
                MemberID = searchcontactadapter.getItem(position).getMemberId();
                Email=searchcontactadapter.getItem(position).getEmail();
                gender=searchcontactadapter.getItem(position).getGender();

                inputName.setText(countryName);
                inputName.setError(null);
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
                inputContact.setError(null);
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
                    if(workoutLevel.equals(getResources().getString(R.string.workout_level)) ||
                            instructorName.equals(getResources().getString(R.string.hint_instructor))) {
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_LONG).show();
                    }else{
                        if(subListArrayList.size()>0){

                            AddWorkoutClass();
                        }else{
                            Toast.makeText(this, "Your workout level are blank.Please fill levels", Toast.LENGTH_LONG).show();
                        }
                    }
                }else{
                    Toast.makeText(this, "Validation Failed", Toast.LENGTH_LONG).show();
                }
    }
    public void  workoutLevelClass() {
        AddWorkoutActivity.WorkoutLevelTrackClass ru = new AddWorkoutActivity.WorkoutLevelTrackClass();
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

            WorkoutLevelDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //  Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> WorkoutLevelDetails = new HashMap<String, String>();
            WorkoutLevelDetails.put("action", "show_workout_level_master");
            WorkoutLevelDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(AddWorkoutActivity.this));
            String domainurl= SharedPrefereneceUtil.getDomainUrl(AddWorkoutActivity.this);
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
//
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                workoutLevellist = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String WorkoutplanName     = jsonObj.getString("WorkoutplanName");

                                    String id=jsonObj.getString("Auto_Id");
//
                                    workoutLevellist.setName(WorkoutplanName);
                                    workoutLevellist.setId(id);

                                    workoutLevelArrayList.add(workoutLevellist);

                                    workoutLeveleadapter = new AddEnquirySpinnerAdapter(AddWorkoutActivity.this, workoutLevelArrayList){
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

                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    public void  instructorNameClass() {
        AddWorkoutActivity.InstructorNameTrackClass ru = new AddWorkoutActivity.InstructorNameTrackClass();
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
            InstructorNameDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(AddWorkoutActivity.this));
            InstructorNameDetails.put("action", "show_instructor_name_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(AddWorkoutActivity.this);
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
                        instructorNamelist.setName(getResources().getString(R.string.hint_instructor_name));
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

                                    instructorNameadapter = new AddEnquirySpinnerAdapter(AddWorkoutActivity.this, instructorNameArrayList){
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
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AddWorkoutActivity.this);
                    builder.setMessage("Add Staff First");
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                          Intent intent=new Intent(AddWorkoutActivity.this,WorkoutActivity.class);
                          startActivity(intent);
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
        AddWorkoutActivity.SearchTrackClass ru = new AddWorkoutActivity.SearchTrackClass();
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

            SearchDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(AddWorkoutActivity.this) );
            SearchDetails.put("action", "show_all_member_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(AddWorkoutActivity.this);
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

                               String namec=Name+"-"+Contact;
                                searchModel.setCustName(Name);
                                searchModel.setCustContact(Contact);
                                searchModel.setMemberId(MemberID);
                                searchModel.setEmail(Email);
                                searchModel.setGender(Gender);
                                searchModel.setNameContact(namec);

                                searchArrayList.add(searchModel);
                                searchnameadapter = new SearchNameAdapter(AddWorkoutActivity.this, searchArrayList);

                                inputName.setAdapter(searchnameadapter);
                                // inputName.setDropDownBackgroundResource(R.drawable.search_background);
                                inputName.setThreshold(1);

                                searchcontactadapter = new SearchContactAdapter(AddWorkoutActivity.this, searchArrayList);

                                inputContact.setAdapter(searchcontactadapter);
                                // textContact.setDropDownBackgroundResource(R.drawable.search_background);
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

    //   ************** Check Contact number already exist or not **************
    public void  CheckContactClass() {
        AddWorkoutActivity.CheckContactTrackClass ru = new AddWorkoutActivity.CheckContactTrackClass();
        ru.execute("5");
    }

    class CheckContactTrackClass extends AsyncTask<String, Void, String> {

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
            CheckContactDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //  Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> EnquiryForDetails = new HashMap<String, String>();

            EnquiryForDetails.put("mobileno",inputContact.getText().toString() );
            EnquiryForDetails.put("user","Member" );
            EnquiryForDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(AddWorkoutActivity.this) );
            EnquiryForDetails.put("action", "check_mobile_already_exist_or_not");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(AddWorkoutActivity.this);
            //EnquiryForloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(EnquiryForloyee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, EnquiryForDetails);
            Log.v(TAG, String.format("doInBackground :: check_mobile_already_exist_or_not= %s", loginResult));
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
                Toast.makeText(AddWorkoutActivity.this,"Member is not registred. Please register Member first",Toast.LENGTH_SHORT).show();
                //inputContact.getText().clear();
                // showCustomDialog();
                finish();
                Intent intent=new Intent(AddWorkoutActivity.this,AddMemberActivity.class);
                intent.putExtra("contact",inputContact.getText().toString());
                startActivity(intent);
                //inputEmail, inputPhone,inputAdd,inputReq,inputFollowupdate;
            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.two)))
            {
                JSONArray jsonArrayResult = object.getJSONArray("Data");

                if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                    for (int i = 0; i < jsonArrayResult.length(); i++) {

                        Log.v(TAG, "JsonResponseOpeartion ::");
                        JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                        if (jsonObj != null) {

                            MemberID = jsonObj.getString("MemberID");
                            String Name = jsonObj.getString("Name");
                            Email = jsonObj.getString("Email");
                            inputName.setText(Name);
                            //invoiceRefIdClass();

                        }
                    }
                } else if (jsonArrayResult.length() == 0) {
                    System.out.println("No records found");
                }
                // Toast.makeText(AddEnquiryActivity.this,"Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
            }else if(success.equalsIgnoreCase(getResources().getString(R.string.one))){
                Toast.makeText(AddWorkoutActivity.this,"member has no active course.Please add course first",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(AddWorkoutActivity.this,RenewActivity.class);
                intent.putExtra("contact",inputContact.getText().toString());
                startActivity(intent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

private void workout_detailsclass() {
    AddWorkoutActivity.WorkoutDetailsTrackclass ru = new AddWorkoutActivity.WorkoutDetailsTrackclass();
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
            WorkoutDetailsDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(AddWorkoutActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getCompanyAutoId(AddWorkoutActivity.this)));
            WorkoutDetailsDetails.put("action","show_workout_level_details");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(AddWorkoutActivity.this);
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
                                subList1 = new WorkOutDetailsList();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Days = jsonObj.getString("Days");

                                    subList1.setDay(Days);
                                    subList1.setSection(true);
                                    subListArrayList.add(subList1);
                                    JSONArray jsonArrayday_info = jsonObj.getJSONArray("day_info");
                                    if (jsonArrayday_info != null && jsonArrayday_info.length() > 0) {

                                        for (int j = 0; j < jsonArrayday_info.length(); j++) {
                                            subList = new WorkOutDetailsList();
                                            Log.v(TAG, "JsonResponseOpeartion ::");
                                            JSONObject jsonObjdiet = jsonArrayday_info.getJSONObject(j);
                                            if (jsonObj != null) {

                                                String Workoutname = jsonObjdiet.getString("Workoutname");
                                                String Sets = jsonObjdiet.getString("Sets");
                                                String Repetations = jsonObjdiet.getString("Repetations");
                                                String Time = jsonObjdiet.getString("Time");
                                                String Bodypart = jsonObjdiet.getString("Bodypart");
                                                String Description = jsonObjdiet.getString("Description");
                                                String Image = jsonObjdiet.getString("Image");
                                                String Vediolink = jsonObjdiet.getString("Vediolink");

                                                subList.setWorkoutName(Workoutname);
                                                subList.setSet(Sets);
                                                subList.setRepitation(Repetations);
                                                subList.setTime(Time);
                                                subList.setBodyPart(Bodypart);
                                                subList.setDiscription(Description);
                                                subList.setWorkoutImage(Image);
                                                subList.setVideoLink(Vediolink);
                                                subList.setSection(false);
                                                subList.setDay(Days);

                                                //Toast.makeText(MainActivity.this, "j "+j, Toast.LENGTH_SHORT).show();
                                                subListArrayList.add(subList);
                                                subListArrayList1.add(subList);


                                            }
                                        }

                                        adapter = new WorkoutLevelDetailsAdapter(AddWorkoutActivity.this, subListArrayList);
                                        recyclerView.setAdapter(adapter);
                                    }
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
//                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    public void  AddWorkoutClass() {

        AddWorkoutActivity.AddWorkoutTrackClass ru = new AddWorkoutActivity.AddWorkoutTrackClass();
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

            AddWorkoutDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {

            JSONObject JSONproduct=null;
            JSONArray jsonArray = new JSONArray();
            for (WorkOutDetailsList data : subListArrayList1)
            {
                JSONproduct = new JSONObject();
                try {

                    JSONproduct.put("level_name",workoutLevel);
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
            AddWorkoutDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(AddWorkoutActivity.this));
            Log.v(TAG, String.format("doInBackground :: comp_id= %s",SharedPrefereneceUtil.getSelectedBranchId(AddWorkoutActivity.this)));
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
            AddWorkoutDetails.put("tbl_count", String.valueOf(subListArrayList1.size()));
            Log.v(TAG, String.format("doInBackground :: tbl_count= %s",String.valueOf(subListArrayList1.size())));
            AddWorkoutDetails.put("tbl_arr",finalobject.toString());
            Log.v(TAG, String.format("doInBackground :: tbl_arr= %s",finalobject.toString()));
            AddWorkoutDetails.put("action", "add_workout");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(AddWorkoutActivity.this);
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

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))||success.equalsIgnoreCase(getResources().getString(R.string.one))) {
                Toast.makeText(AddWorkoutActivity.this,"Workout added succesfully",Toast.LENGTH_SHORT).show();

                EmailLoginClass();
                //finish();
                Intent intent=new Intent(AddWorkoutActivity.this, AddWorkoutActivity.class);
                startActivity(intent);
            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.zero)))
            {
                Toast.makeText(AddWorkoutActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void  EmailLoginClass() {
        AddWorkoutActivity.EmailLoginTrackClass ru = new AddWorkoutActivity.EmailLoginTrackClass();
        ru.execute("5");
    }

    class EmailLoginTrackClass extends AsyncTask<String, Void, String> {

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
            // dismissProgressDialog();
            //Toast.makeText(CandiateListView.this, response, Toast.LENGTH_LONG).show();
            //  Toast.makeText(NewCustomerActivity.this, response, Toast.LENGTH_LONG).show();
            EmailLoginDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> EmailLoginDetails = new HashMap<String, String>();
            EmailLoginDetails.put("comp_id",SharedPrefereneceUtil.getSelectedBranchId(AddWorkoutActivity.this) );
            EmailLoginDetails.put("action", "show_email_login");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(AddWorkoutActivity.this);
            //EnquiryForloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(EnquiryForloyee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, EmailLoginDetails);

            Log.v(TAG, String.format("doInBackground :: show_email_login= %s", loginResult));
            return loginResult;
        }
    }
    private void EmailLoginDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject object = null;
        try {
            object = new JSONObject(jsonResponse);
            String success = object.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.zero))) {

                // showCustomDialog();

                //inputEmail, inputPhone,inputAdd,inputReq,inputFollowupdate;
            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.two)))
            {
                if (object != null) {
                    JSONArray jsonArrayCountry = object.getJSONArray("result");

                    if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                        for (int i = 0; i < jsonArrayCountry.length(); i++) {
                            Log.v(TAG, "JsonResponseOpeartion ::");
                            JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                            if (jsonObj != null) {

                                Email_ID     = jsonObj.getString("Email_ID");
                                Password=jsonObj.getString("Password");
                                String Email_Status=jsonObj.getString("Email_Status");
                                Header=jsonObj.getString("Header");
                                Footer=jsonObj.getString("Footer");
                                if(Email_Status.equals("ON")){
                                    if(!Email.equals("")){
                                        companydatalass();
                                    }
                                }else {
                                    System.out.println("Email Status Is Off");
                                }
                            }
                        }
                    }else if(jsonArrayCountry.length()==0){
                        System.out.println("No records found");
                    }
                }
            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void companydatalass() {
        AddWorkoutActivity.ReceiptDataTrackclass ru = new AddWorkoutActivity.ReceiptDataTrackclass();
        ru.execute("5");
    }
    class ReceiptDataTrackclass extends AsyncTask<String, Void, String> {

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
            Log.v(TAG, String.format("onPostExecute :: show_receipt_data = %s", response));
            //dismissProgressDialog();
            viewDialog.hideDialog();
            //Toast.makeText(Employee.this, response, Toast.LENGTH_LONG).show();
            ReceiptDataDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> ReceiptDataDetails = new HashMap<String, String>();
            ReceiptDataDetails.put("Company_Id", SharedPrefereneceUtil.getSelectedBranchId(AddWorkoutActivity.this));
            Log.v(TAG, String.format("doInBackground ::  company id = %s", SharedPrefereneceUtil.getSelectedBranchId(AddWorkoutActivity.this)));
            ReceiptDataDetails.put("action","show_branch_details");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(AddWorkoutActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, ReceiptDataDetails);
            Log.v(TAG, String.format("doInBackground :: show_branch_details= %s", loginResult));
            return loginResult;
        }


    }

    private void ReceiptDataDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
//        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayoutPrabhagDetails);
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");

                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                            for (int i = 0; i < jsonArrayResult.length(); i++) {

                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {


                                    String Company_Name = SharedPrefereneceUtil.getCompanyName(AddWorkoutActivity.this)+"-"+SharedPrefereneceUtil.getSelectedBranch(AddWorkoutActivity.this);
                                    String Address = jsonObj.getString("Address");
                                    String Contact = jsonObj.getString("Contact");
                                    String GST_No = jsonObj.getString("GST_No");
                                    String TermsAndConditions = jsonObj.getString("TermsAndConditions");
                                    TermsAndConditions = TermsAndConditions.replace("\r\n", "<br />");
                                    String Logo = jsonObj.getString("Logo");
                                    String l=Logo.replaceAll("\\s+","%20");

                                    String domainurl=SharedPrefereneceUtil.getDomainUrl(AddWorkoutActivity.this);
                                    final  String imgurl=domainurl+ServiceUrls.IMAGES_URL+l;
                                    Log.d(TAG, "imgurl: " +imgurl);

                                    String textBody = "";

                                    String currentdate=Utility.getCurrentDate();
                                    //JSONArray jsonArrayPayTrasa = jsonObj.getJSONArray("payment_transa");
                                    //if (jsonArrayPayTrasa != null && jsonArrayPayTrasa.length() > 0) {
                                        for (WorkOutDetailsList data : subListArrayList1)
                                        {


                                                textBody += "  <tr >\n \n" +
                                                        "    <td width='14%'>"+workoutLevel+"</td>\n \n" +
                                                        "     <td width='14%'>"+data.getDay()+"</td>\n\n" +
                                                        "    <td width='14%'>"+data.getBodyPart()+"</td> \n\n" +
                                                        "    <td width='14%'>"+data.getWorkoutName()+"</td>\n\n" +
                                                        "    <td width='10%'>"+data.getSet()+"</td>\n\n" +
                                                        "    <td width='18%'>"+data.getRepitation()+"</td>\n\n" +
                                                        "    <td width='14%'>"+data.getTime()+"</td>\n\n" +
                                                        "    </tr>\n";
                                          //  }
                                        }

                                    final String messagehtml = "<!DOCTYPE html>\n" +
                                            "\n" +
                                            "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                                            "<head runat=\"server\">\n" +
                                            "    <title></title>\n" +
                                            "</head>\n" +
                                            "<body>\n" +
                                            "    <form runat=\"server\">\n" +
                                            "\n" +
                                            " <div class=\"row\">\n" +
                                            "                <div class=\"column\" >\n" +
                                            "                    <div >\n" +
                                            "                        <address>\n" +
                                            "                            <strong style=\"font: 700;\">\n" +
                                            "                               </strong><br></br>" +
                                            "                          <br></br>" +
                                            "                           <br></br>" +
                                            "                        </address>" +
                                            "                    </div>" +
                                            "                    <div >" +
                                            "                        <address>" +
                                            "                        " +
//                                            "                            <img src="+ imgurl +">" +
                                            "                        </address>\n" +
                                            "                    </div>" +
                                            "                </div>" +
                                            "                <div class=\"column\" >\n" +
                                            "                    <div  >\n" +
                                            "                        <address>\n" +
                                            "                            <strong></strong><br></br>\n" +
                                            "                            <strong style=\"font: 900;\">"+"</strong><br></br>\n" +
                                            "                           <br></br>\n" +
                                            "                        </address>\n" +
                                            "                    </div>" +
                                            "                    <div >" +
                                            "                        <address>" +
                                            "                            <strong>"+"</strong><br></br>\n" +
                                            "\n" +
                                            "                            <strong> "+"</strong><br></br>\n" +
                                            "                            <strong>  "+"</strong>\n" +
                                            "                        </address>\n" +
                                            "                    </div>\n" +
                                            "                    <div >" +
                                            "                        <address>" +
                                            "                            <strong>"+"</strong><br></br>\n" +
                                            "\n" +
                                            "                            <strong> "+"</strong><br></br>\n" +
                                            "                            <strong>  "+"</strong>\n" +
                                            "                        </address>\n" +
                                            "                    </div>\n" +
                                            "                </div>\n" +
                                            "      </div>\n" +
                                            "\n" +
                                            "\n" +
                                            "       \n" +
                                            " <div  >\n" +
                                            "\n" +
                                            "    <div >\n" +

                                            "            <div >\n" +
                                            "                <div  >\n" +
                                            "                        <h3 ><strong>Workout Details</strong></h3>\n" +
                                            "                    </div>\n" +
                                            "                <div >\n" +
                                            "              <div >\n" +
                                            "             <table border = '1' cellpadding=\"6\"  width=\"100%\" >\n" +
                                            "             <thead height=\"100\">\n" +
                                            "                   <tr height=\"100\" >\n" +
                                            "                      <th width='14%' ><strong>Level Name</strong></th>\n" +
                                            "                      <th width='14%'><strong>Day</strong></th>\n" +
                                            "                      <th width='14%'><strong>Muscular Group</strong></th>                                    \n" +
                                            "                      <th width='14%'><strong>Workout Name</strong></th>\n" +
                                            "                       <th width='10%'><strong>Set</strong></th>\n" +
                                            "                       <th width='20%'><strong>Repitations</strong></th>\n" +
                                            "                       <th width='14%'><strong>Time</strong></th>\n" +
                                            "                    </tr>\n" +
                                            "             </thead>\n" +
                                            "           <tbody >\n" +
                                            textBody+
                                            "          </tbody>\n" +
                                            "       </table>\n" +
                                            "       </div>\n" +
                                            "       </div>\n" +
                                            "                     \n" +
                                            "      </div>\n" +
                                            "     </div>\n" +
                                            "  </div>\n" +
                                            "\n" +
                                            "      \n" +
                                            "\n" +
                                            "  <div  >\n" +
                                            "    <div >\n" +
                                            "       <div  >\n" +
                                            "          <h3 ><strong>Terms And Conditions</strong></h3>\n" +
                                            "       </div>\n" +
                                            "                  \n" +
                                            "                           "+TermsAndConditions+"\n" +
                                            "                     \n" +
                                            "    \n" +
                                            "                \n" +
                                            "            </div>\n" +
                                            "        </div>\n" +
                                            "\n" +
                                            "    </div>\n" +
                                            "        </form>\n" +
                                            "</body>\n" +
                                            "</html>";
                                    final Document document = new Document(PageSize.A4);

                                    try {

                                        PdfWriter docWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
                                        document.open();
//

                                        PdfContentByte cb = docWriter.getDirectContent();
                                        //initialize fonts for text printing
                                        initializeFonts();

                                        //the company logo is stored in the assets which is read only
                                        //get the logo and print on the document
                                        String urlOfImage = "https://lh5.googleusercontent.com/E3eX_"
                                                + "hgl-eK9cX6j6XMyM6eOkCPvYs9Us5ySKIu60_fYFGlKywKP9pGfNcTj"
                                                + "7WDSnDb4zrHubFRLHGK4DqBiLBa4HzRAWx728iHpDrL21HxzsEXSHAa"
                                                + "lK49-rBzvU3DlmGURrwg";

                                        //Add Image from some URL
                                        Thread thread = new Thread(new Runnable() {
                                            //
                                            @Override
                                            public void run() {
                                                try  {
                                                    Image image = Image.getInstance(new URL(imgurl));
                                                    image.setAbsolutePosition(510,750);
                                                    image.scalePercent(50);
//                                                    //Set absolute position for image in PDF (or fixed)
//                                                    image.setAbsolutePosition(100, 500);
//                                                    //Scale image's width and height
//                                                    image.scaleAbsolute(200, 200);
//                                                    //Scale image's height
//                                                    image.scaleAbsoluteWidth(200);
//                                                    //Scale image's width
//                                                    image.scaleAbsoluteHeight(200);
                                                    document.add(image);
                                                    //Your code goes here
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                                        thread.start();
//
                                        // creating a sample invoice with some customer data
                                        createHeadings(cb,50,780,Company_Name);
                                        createText(cb,50,765,Address);
                                        createText(cb,50,750,Contact);
                                        createText(cb,50,735,GST_No);
                                        createHeadings(cb,50,715,"Workout To");
                                        createText(cb,50,700,inputName.getText().toString());
                                        createText(cb,50,685,Email);
                                        createText(cb,50,670,inputContact.getText().toString());
                                       // createText(cb,50,655,MemberGST_No);
                                        createHeadings(cb,455,715,"Date :"+currentdate);
                                        createText(cb,455,700,"Member Id : "+MemberID);
                                        createText(cb,455,685,"Programmer : "+instructorName);


                                        HTMLWorker htmlWorker = new HTMLWorker(document);
                                        htmlWorker.parse(new StringReader(messagehtml));
                                        document.close();
                                        // document.close();
                                    }
                                    catch(Exception e){
                                        e.printStackTrace();
                                    }
                                    final String subject=Company_Name+" Receipt";
                                    final String message=Header+"\nPlease find the attachment of Your Package Details\n\n"+Footer;
                                    AddWorkoutActivity.this.runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            //second async stared within a asynctask but on the main thread
                                            (new AsyncTask<String, String, Void>() {
                                                ServerClass ruc = new ServerClass();

                                                @Override
                                                protected Void doInBackground(String... params) {
                                                    Mail m = new Mail(Email_ID, Password);

                                                    String[] toArr = { Email, "tulsababar01@gmail.com"};
                                                    // Log.v(TAG, String.format(" Email array to send = %s", toArr));
                                                    m.setTo(toArr);
                                                    m.setFrom("tulsababar.ndsoft@gmail.com");
                                                    m.setSubject(subject);
                                                    m.setBody(message);
                                                    if (Build.VERSION.SDK_INT >= 23)
                                                    {
                                                        if (checkPermission())
                                                        {
                                                            // final File  file = BitmapSaver.saveImageToExternalStorage(RenewActivity.this, bmpqr);
                                                            m.setAttachment(pdfFile);
                                                            m.setAttachmentName(FilePath);
                                                            m.setAttachmentNamePath("WorkoutDetails.pdf");
                                                            // Code for above or equal 23 API Oriented Device
                                                            // Your Permission granted already .Do next code
                                                        } else {
                                                            requestPermission(); // Code for permission
                                                        }
                                                    }else{
                                                        // final File  file = BitmapSaver.saveImageToExternalStorage(RenewActivity.this, bmpqr);
                                                        m.setAttachment(pdfFile);
                                                        m.setAttachmentName(FilePath);
                                                        m.setAttachmentNamePath("WorkoutDetails.pdf");
                                                    }
                                                    try {
                                                        if(m.send()) {
                                                            runOnUiThread(new Runnable() {
                                                                public void run() {

                                                                    Toast.makeText(AddWorkoutActivity.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();

                                                                }
                                                            });
                                                        } else {
                                                            Toast.makeText(AddWorkoutActivity.this, "Email was not sent.", Toast.LENGTH_LONG).show();
                                                        }
                                                    } catch(Exception e) {
                                                        Log.e("MailApp", "Could not send email", e);
                                                    }
                                                    return null;
                                                }

                                                @Override
                                                protected void onPostExecute(Void aVoid) {
                                                    super.onPostExecute(aVoid);


                                                }
                                            }).execute();

                                        }
                                    });

                                }
                            }
                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    //nodata.setVisibility(View.VISIBLE);
                    //.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    private void createHeadings(PdfContentByte cb, float x, float y, String text){

        cb.beginText();
        cb.setFontAndSize(bfBold, 11);
        cb.setTextMatrix(x,y);
        cb.showText(text.trim());
        cb.endText();

    }
    private void createText(PdfContentByte cb, float x, float y, String text){

        cb.beginText();
        cb.setFontAndSize(bfnormal, 8);
        cb.setTextMatrix(x,y);
        cb.showText(text.trim());
        cb.endText();

    }
    private void initializeFonts(){


        try {
            bfBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            bfnormal = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Pdf Generator
    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(AddWorkoutActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(AddWorkoutActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(AddWorkoutActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(AddWorkoutActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent = new Intent(AddWorkoutActivity.this, WorkoutActivity.class);
        startActivity(intent);
        return true;
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddWorkoutActivity.this, WorkoutActivity.class);
        startActivity(intent);
    }
}
