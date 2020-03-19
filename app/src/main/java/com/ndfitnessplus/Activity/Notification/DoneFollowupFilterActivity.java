package com.ndfitnessplus.Activity.Notification;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ndfitnessplus.Activity.EnquiryActivity;
import com.ndfitnessplus.Activity.MainActivity;
import com.ndfitnessplus.Adapter.SpinnerAdapter;
import com.ndfitnessplus.Model.FollowupList;
import com.ndfitnessplus.Model.FollowupList;
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

public class DoneFollowupFilterActivity extends AppCompatActivity {
    public final String TAG = DoneFollowupFilterActivity.class.getName();
    private ProgressDialog pd;
    //Spinner Adapter
    public Spinner spinDateWise,spinFollowupType,spinCallResponce,spinRating,spinExecutive;
    Spinner_List datewiselist,followupTypelist,CallReslist,ratingList,ExecutiveNameList;
    ArrayList<Spinner_List> datewiseArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> followupTypeArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> CallResArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> ratingArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> ExecutiveNameArrayList = new ArrayList<Spinner_List>();
    public SpinnerAdapter datewiseadapter,followupTypeadapter,callresadapter,ratingadapter,executivenameadapter;
    String dateWise,FollowupType,callResponce,Rating,executiveName;

    TextView todate,fromdate;
    ImageButton toDatebtn,fromDateBtn;
    private int mYear, mMonth, mDay;

    ArrayList<FollowupList> subListArrayList = new ArrayList<FollowupList>();
    FollowupList subList;
    Button btn_applyFilter;
    //Loading gif
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done_followup_filter);
        initToolbar();
    }private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.done_followup_filters));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){

        //spinners
        spinDateWise = (Spinner) findViewById(R.id.spinner_date_wise);
        spinFollowupType = (Spinner) findViewById(R.id.spinner_folltype);
        spinCallResponce = (Spinner) findViewById(R.id.spinner_call_res);
        spinRating = (Spinner) findViewById(R.id.spinner_rating);
        spinExecutive = (Spinner) findViewById(R.id.spinner_executive);

        viewDialog = new ViewDialog(this);
        todate=findViewById(R.id.to_date);
        fromdate=findViewById(R.id.from_date);
        fromDateBtn=findViewById(R.id.btn_from_date);
        toDatebtn=findViewById(R.id.btn_to_date);
        btn_applyFilter=findViewById(R.id.btn_apply_filters);

        String firstday= Utility.getFirstDayofMonth();
        todate.setText(firstday);
        String curr_date=Utility.getCurrentDate();
        fromdate.setText(curr_date);


        //date pickers for to date and from date
        toDatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(DoneFollowupFilterActivity.this,
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


                DatePickerDialog datePickerDialog = new DatePickerDialog(DoneFollowupFilterActivity.this,
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
        //api's for spinners
        FollowupTypeClass();
        callResponseClass();
        executiveClass();


        //setting data to the spinners

        spinFollowupType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if(view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);
                    if(index==0){
                        tv.setText(getResources().getString(R.string.prompt_foll_type));
                    }

                    FollowupType = tv.getText().toString();

                    if((FollowupType.equals(getResources().getString(R.string.prompt_foll_type)))||
                            (FollowupType.equals(getResources().getString(R.string.all)))){
                        FollowupType="";
                    }
                }}

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinCallResponce.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if(view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);
                    if(index==0){
                        tv.setText(getResources().getString(R.string.prompt_call_res));
                    }
                    callResponce = tv.getText().toString();
                    if((callResponce.equals(getResources().getString(R.string.prompt_call_res)))||
                            (callResponce.equals(getResources().getString(R.string.all)))){
                        callResponce="";
                    }
                }}

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Call Responce spinner adapter setting
        final  String[] ratingarray = getResources().getStringArray(R.array.rating_array_convertd);
        ratingList = new Spinner_List();
        ratingList.setName(getResources().getString(R.string.prompt_rating));
        ratingArrayList.add(0,ratingList);
        ratingList.setName(getResources().getString(R.string.all));
        ratingArrayList.add(1,ratingList);
        for(int i=1;i<ratingarray.length;i++) {
            ratingList = new Spinner_List();
            ratingList.setName(ratingarray[i]);
            ratingArrayList.add(ratingList);
            ratingadapter = new SpinnerAdapter(DoneFollowupFilterActivity.this, ratingArrayList) {
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
                        tv.setText(getResources().getString(R.string.prompt_rating));
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }

            };
            spinRating.setAdapter(ratingadapter);
        }
        spinRating.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);
                    if (index == 0) {
                        tv.setText(getResources().getString(R.string.prompt_rating));
                    }
                    Rating = tv.getText().toString();
                    if ((Rating.equals(getResources().getString(R.string.prompt_rating))) ||
                            (Rating.equals(getResources().getString(R.string.all)))) {
                        Rating = "";
                    }

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Enquiry date type spinners
        final  String[] enquirydatearray = getResources().getStringArray(R.array.done_followup_date_array);

        for(int i=0;i<enquirydatearray.length;i++) {
            datewiselist = new Spinner_List();
            datewiselist.setName(enquirydatearray[i]);
            datewiseArrayList.add(datewiselist);
            datewiseadapter = new SpinnerAdapter(DoneFollowupFilterActivity.this, datewiseArrayList) {
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
                        tv.setText(getResources().getString(R.string.prompt_enq_date));
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
                    if(index==0){
                        tv.setText(getResources().getString(R.string.prompt_enq_date));
                    }

                    dateWise = tv.getText().toString();
                    if((dateWise.equals(getResources().getString(R.string.prompt_enq_date)))||
                            (dateWise.equals(getResources().getString(R.string.all)))){
                        dateWise="";
                    }
                }}

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
                SearchEnquiryClass();
            }
        });

    }
    //******************campare two dates****************
    public void CampareTwoDates(){

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_action_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                Intent intent = new Intent(DoneFollowupFilterActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void  FollowupTypeClass() {
        DoneFollowupFilterActivity.FollowupTypeTrackClass ru = new DoneFollowupFilterActivity.FollowupTypeTrackClass();
        ru.execute("5");
    }
    class FollowupTypeTrackClass extends AsyncTask<String, Void, String> {

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
            FollowupTypeDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> FollowupTypeDetails = new HashMap<String, String>();
            FollowupTypeDetails.put("action", "show_all_followup_type_list");
            String domainurl= SharedPrefereneceUtil.getDomainUrl(DoneFollowupFilterActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.root_path_web, FollowupTypeDetails);
            Log.v(TAG, String.format("doInBackground :: show_all_followup_type_list= %s", loginResult));
            return loginResult;
        }

    }


    private void FollowupTypeDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {


            try {
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    if (object != null) {
                        JSONArray jsonArrayCountry = object.getJSONArray("result");
                        followupTypeArrayList.clear();
                        followupTypelist = new Spinner_List();
                        followupTypelist.setName(getResources().getString(R.string.prompt_foll_type));
                        followupTypeArrayList.add(0,followupTypelist);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            followupTypelist.setName(getResources().getString(R.string.all));
                            followupTypeArrayList.add(1,followupTypelist);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                followupTypelist = new Spinner_List();
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Followup     = jsonObj.getString("Followup");

                                    String id=jsonObj.getString("Auto_Id");

                                    followupTypelist.setName(Followup);
                                    followupTypelist.setId(id);

                                    followupTypeArrayList.add(followupTypelist);

                                    followupTypeadapter = new SpinnerAdapter(DoneFollowupFilterActivity.this, followupTypeArrayList){
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
                                                tv.setText(getResources().getString(R.string.prompt_foll_type));
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinFollowupType.setAdapter(followupTypeadapter);


                                }
                            }
                        }else if(jsonArrayCountry.length()==0){
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    followupTypeArrayList.clear();
                    followupTypelist = new Spinner_List();
                    followupTypelist.setName(getResources().getString(R.string.prompt_foll_type));
                    followupTypeArrayList.add(0,followupTypelist);
                    followupTypelist.setName(getResources().getString(R.string.all));
                    followupTypeArrayList.add(1,followupTypelist);
                    followupTypeadapter = new SpinnerAdapter(DoneFollowupFilterActivity.this, followupTypeArrayList){
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
                                tv.setText(getResources().getString(R.string.prompt_foll_type));
                            }
                            else {
                                tv.setTextColor(Color.BLACK);
                            }
                            return view;
                        }

                    };
                    spinFollowupType.setAdapter(followupTypeadapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void  executiveClass() {
        DoneFollowupFilterActivity.ExecutiveNameTrackClass ru = new DoneFollowupFilterActivity.ExecutiveNameTrackClass();
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
            String comp_name=SharedPrefereneceUtil.getCompanyName(DoneFollowupFilterActivity.this);
            String location=SharedPrefereneceUtil.getSelectedBranch(DoneFollowupFilterActivity.this);
            String compid=comp_name+"-"+location+",";
            ExecutiveNameDetails.put("comp_id", compid);
            ExecutiveNameDetails.put("action", "show_executive_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(DoneFollowupFilterActivity.this);
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

                                    executivenameadapter = new SpinnerAdapter(DoneFollowupFilterActivity.this, ExecutiveNameArrayList){
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
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    ExecutiveNameArrayList.clear();
                    ExecutiveNameList = new Spinner_List();
                    ExecutiveNameList.setName(getResources().getString(R.string.prompt_executive));
                    ExecutiveNameArrayList.add(0,ExecutiveNameList);
                    ExecutiveNameList.setName(getResources().getString(R.string.all));

                    ExecutiveNameArrayList.add(1,ExecutiveNameList);
                    executivenameadapter = new SpinnerAdapter(DoneFollowupFilterActivity.this, ExecutiveNameArrayList){
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
    // *********************** Call response spinner **************************
    public void  callResponseClass() {
        DoneFollowupFilterActivity.CallResponseTrackClass ru = new DoneFollowupFilterActivity.CallResponseTrackClass();
        ru.execute("5");
    }
    class CallResponseTrackClass extends AsyncTask<String, Void, String> {

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
            CallResponseDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> CallResponseDetails = new HashMap<String, String>();
            CallResponseDetails.put("action", "show_call_response_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(DoneFollowupFilterActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, CallResponseDetails);
            return loginResult;
        }


    }
    private void CallResponseDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {


            try {
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    if (object != null) {
                        JSONArray jsonArrayCountry = object.getJSONArray("result");
                        CallResArrayList.clear();
                        CallReslist = new Spinner_List();
                        CallReslist.setName(getResources().getString(R.string.prompt_call_res));
                        CallResArrayList.add(0,CallReslist);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            CallReslist.setName(getResources().getString(R.string.all));
                            CallResArrayList.add(1,CallReslist);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                CallReslist = new Spinner_List();
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String CallResponse     = jsonObj.getString("CallResponse");

                                    String id=jsonObj.getString("Auto_Id");

                                    CallReslist.setName(CallResponse);
                                    CallReslist.setId(id);

                                    CallResArrayList.add(CallReslist);
                                    callresadapter = new SpinnerAdapter(DoneFollowupFilterActivity.this, CallResArrayList){
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
                                                tv.setText(getResources().getString(R.string.prompt_call_res));
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinCallResponce.setAdapter(callresadapter);


                                }
                            }
                        }else if(jsonArrayCountry.length()==0){
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    CallResArrayList.clear();
                    CallReslist = new Spinner_List();
                    CallReslist.setName(getResources().getString(R.string.prompt_call_res));
                    CallResArrayList.add(0,CallReslist);
                    CallReslist.setName(getResources().getString(R.string.all));
                    CallResArrayList.add(1,CallReslist);
                    callresadapter = new SpinnerAdapter(DoneFollowupFilterActivity.this, CallResArrayList){
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
                                tv.setText(getResources().getString(R.string.prompt_call_res));
                            }
                            else {
                                tv.setTextColor(Color.BLACK);
                            }
                            return view;
                        }

                    };
                    spinCallResponce.setAdapter(callresadapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void  SearchEnquiryClass() {
        DoneFollowupFilterActivity.SearchEnquiryTrackClass ru = new DoneFollowupFilterActivity.SearchEnquiryTrackClass();
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
            SearchEnquiryDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(DoneFollowupFilterActivity.this));
            SearchEnquiryDetails.put("to_date",fromdate.getText().toString());
            SearchEnquiryDetails.put("from_date",todate.getText().toString());
            SearchEnquiryDetails.put("date_wise", dateWise);
            SearchEnquiryDetails.put("rating",Rating);
            SearchEnquiryDetails.put("call_res",callResponce);
            SearchEnquiryDetails.put("exe_name",executiveName);
            SearchEnquiryDetails.put("foll_type",FollowupType);
            SearchEnquiryDetails.put("offset","0");
            SearchEnquiryDetails.put("pagesize","800");
            SearchEnquiryDetails.put("action", "search_done_followup_filter");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(DoneFollowupFilterActivity.this);
            String loginResult2 = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, SearchEnquiryDetails);
            return loginResult2;
        }
    }


    private void SearchEnquiryDetails(String jsonResponse) {

        Log.v(TAG, String.format("search_done_followup_filter :: response = %s", jsonResponse));

        JSONObject object = null;
        try {
            object = new JSONObject(jsonResponse);
            String success = object.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                if (object != null) {
                    JSONArray jsonArrayResult = object.getJSONArray("result");

                    if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                        for (int i = 0; i < jsonArrayResult.length(); i++) {

                            subList = new FollowupList();

                            JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                            if (jsonObj != null) {

                                String name = jsonObj.getString("Name");
                                String Rating = jsonObj.getString("Rating");
                                String Contact = jsonObj.getString("Contact");
                                String CallResponse = jsonObj.getString("CallResponse");
                                String ExecutiveName = jsonObj.getString("ExecutiveName");
                                String Comment = jsonObj.getString("Comment");
                                String FollowupType = jsonObj.getString("FollowupType");
                                String NextFollowup_Date = jsonObj.getString("NextFollowupDate");
                                String Auto_Id = jsonObj.getString("Member_ID");
                                String Followup_Date = jsonObj.getString("FollowupDate");
                                String Image = jsonObj.getString("Image");


                                subList.setName(name);
                                subList.setRating(Rating);
                                String cont=Utility.lastFour(Contact);
                                subList.setContactEncrypt(cont);
                                subList.setContact(Contact);
                                subList.setCallRespond(CallResponse);
                                subList.setExecutiveName(ExecutiveName);
                                subList.setComment(Comment);
                                subList.setFollowupType(FollowupType);
                                if(!NextFollowup_Date.equals("")){
                                    String next_foll_date= Utility.formatDate(NextFollowup_Date);
                                    subList.setNextFollowupDate(next_foll_date);
                                }
                                String foll_date= Utility.formatDate(Followup_Date);

                                subList.setFollowupDate(foll_date);
                                subList.setID(Auto_Id);
                                subList.setImage(Image);
                                subListArrayList.add(subList);


                            }
                        }
                        Intent intent=new Intent(DoneFollowupFilterActivity.this, DoneFollowupActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("filter_array_list", subListArrayList);
                        intent.putExtra("BUNDLE",bundle);
                        startActivity(intent);

                    }
                }
            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.zero)))
            {
                Toast.makeText(DoneFollowupFilterActivity.this,"No Records Found",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(DoneFollowupFilterActivity.this, DoneFollowupActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(DoneFollowupFilterActivity.this,DoneFollowupActivity.class);
        startActivity(intent);
    }
}

