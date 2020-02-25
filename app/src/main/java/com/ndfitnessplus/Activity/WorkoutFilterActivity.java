package com.ndfitnessplus.Activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ndfitnessplus.Adapter.AddEnquirySpinnerAdapter;
import com.ndfitnessplus.Adapter.SpinnerAdapter;
import com.ndfitnessplus.Model.CollectionList;
import com.ndfitnessplus.Model.ExpensesList;
import com.ndfitnessplus.Model.Spinner_List;
import com.ndfitnessplus.Model.WorkOutDayList;
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

public class WorkoutFilterActivity extends AppCompatActivity {
    public final String TAG = WorkoutFilterActivity.class.getName();
    private ProgressDialog pd;
    //Spinner Adapter
    public Spinner spinLevel,spinInstructorName;
    Spinner_List levelList,instructorNamelist;
    ArrayList<Spinner_List> levelArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> instructorNameArrayList = new ArrayList<Spinner_List>();

    public SpinnerAdapter leveladapter,instructorNameadapter;
    String Level,InstructorName;

    TextView todate,fromdate;
    ImageButton toDatebtn,fromDateBtn;
    private int mYear, mMonth, mDay;

    ArrayList<WorkOutDayList> subListArrayList = new ArrayList<WorkOutDayList>();
    WorkOutDayList subList;
    Button btn_applyFilter;
    //Loading gif
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_workout_filter);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.workout_filters));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent() {

        //spinners
        spinLevel = (Spinner) findViewById(R.id.spinner_level);
        spinInstructorName = (Spinner) findViewById(R.id.spinner_instructor_name);


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


                DatePickerDialog datePickerDialog = new DatePickerDialog(WorkoutFilterActivity.this,
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


                DatePickerDialog datePickerDialog = new DatePickerDialog(WorkoutFilterActivity.this,
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
       workoutLevelClass();
       instructorNameClass();

        spinLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);
                    if (index == 0) {
                        tv.setText(getResources().getString(R.string.workout_level_filters));
                    }
                    Level = tv.getText().toString();
                    if ((Level.equals(getResources().getString(R.string.workout_level_filters))) ||
                            (Level.equals(getResources().getString(R.string.all)))) {
                        Level = "";
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinInstructorName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if(view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);
                    if(index==0){
                        tv.setText(getResources().getString(R.string.prompt_instructor));
                    }
                    InstructorName = tv.getText().toString();
                    if((InstructorName.equals(getResources().getString(R.string.prompt_instructor)))||
                            (InstructorName.equals(getResources().getString(R.string.all)))){
                        InstructorName="";
                    }
                }}

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btn_applyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchWorkoutClass();
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
    public void  workoutLevelClass() {
        WorkoutFilterActivity.WorkoutLevelTrackClass ru = new WorkoutFilterActivity.WorkoutLevelTrackClass();
        ru.execute("5");
    }
    class WorkoutLevelTrackClass extends AsyncTask<String, Void, String> {

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
            WorkoutLevelDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> WorkoutLevelDetails = new HashMap<String, String>();
            WorkoutLevelDetails.put("action", "show_workout_level_master");
            WorkoutLevelDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(WorkoutFilterActivity.this));
            String domainurl= SharedPrefereneceUtil.getDomainUrl(WorkoutFilterActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, WorkoutLevelDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void WorkoutLevelDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {


            try {
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    if (object != null) {
                        JSONArray jsonArrayCountry = object.getJSONArray("result");
                        levelArrayList.clear();
                        levelList = new Spinner_List();
                        levelList.setName(getResources().getString(R.string.workout_level_filters));
                        levelArrayList.add(0,levelList);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            levelList.setName(getResources().getString(R.string.all));
                            levelArrayList.add(1,levelList);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                levelList = new Spinner_List();
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String WorkoutplanName     = jsonObj.getString("WorkoutplanName");

                                    String id=jsonObj.getString("Auto_Id");

                                    levelList.setName(WorkoutplanName);
                                    levelList.setId(id);

                                    levelArrayList.add(levelList);

                                    leveladapter = new SpinnerAdapter(WorkoutFilterActivity.this, levelArrayList){
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
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinLevel.setAdapter(leveladapter);


                                }
                            }
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    levelArrayList.clear();
                    levelList = new Spinner_List();
                    levelList.setName(getResources().getString(R.string.workout_level_filters));
                    levelArrayList.add(0,levelList);
                    levelList.setName(getResources().getString(R.string.all));
                    levelArrayList.add(1,levelList);
                    leveladapter = new SpinnerAdapter(WorkoutFilterActivity.this, levelArrayList){
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
                            }
                            else {
                                tv.setTextColor(Color.BLACK);
                            }
                            return view;
                        }

                    };
                    spinLevel.setAdapter(leveladapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void  instructorNameClass() {
        WorkoutFilterActivity.InstructorNameTrackClass ru = new WorkoutFilterActivity.InstructorNameTrackClass();
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
            InstructorNameDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(WorkoutFilterActivity.this));
            InstructorNameDetails.put("action", "show_dietition_name_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(WorkoutFilterActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, InstructorNameDetails);
            Log.v(TAG, String.format("doInBackground :: show_dietition_name_list= %s", loginResult));
            return loginResult;
        }


    }


    private void InstructorNameDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {


            try {
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
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Name     = jsonObj.getString("Name");

                                    instructorNamelist.setName(Name);

                                    instructorNameArrayList.add(instructorNamelist);

                                    instructorNameadapter = new SpinnerAdapter(WorkoutFilterActivity.this, instructorNameArrayList){
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
                                    spinInstructorName.setAdapter(instructorNameadapter);


                                }
                            }
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    instructorNameArrayList.clear();
                    instructorNamelist = new Spinner_List();
                    instructorNamelist.setName(getResources().getString(R.string.hint_instructor));
                    instructorNameArrayList.add(0,instructorNamelist);
                    instructorNameadapter = new SpinnerAdapter(WorkoutFilterActivity.this, instructorNameArrayList){
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
                    spinInstructorName.setAdapter(instructorNameadapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void  SearchWorkoutClass() {
        WorkoutFilterActivity.SearchWorkoutTrackClass ru = new WorkoutFilterActivity.SearchWorkoutTrackClass();
        ru.execute("5");
    }


    class SearchWorkoutTrackClass extends AsyncTask<String, Void, String> {

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
            SearchWorkoutDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> SearchWorkoutDetails = new HashMap<String, String>();
            SearchWorkoutDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(WorkoutFilterActivity.this));
            SearchWorkoutDetails.put("to_date",todate.getText().toString());
            SearchWorkoutDetails.put("from_date",fromdate.getText().toString());
            SearchWorkoutDetails.put("level_name",Level);
            SearchWorkoutDetails.put("instructor_name",InstructorName);
            SearchWorkoutDetails.put("action", "search_workout_filter");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(WorkoutFilterActivity.this);
            String loginResult2 = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, SearchWorkoutDetails);

            return loginResult2;
        }
    }


    private void SearchWorkoutDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject object = null;
        try {
            object = new JSONObject(jsonResponse);
            String success = object.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {

                if (object != null) {
                    JSONArray jsonArrayResult = object.getJSONArray("result");
                    if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                        for (int i = 0; i < jsonArrayResult.length(); i++) {

                            subList = new WorkOutDayList();

                            JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                            if (jsonObj != null) {

                                String name = jsonObj.getString("Name");
                                String Contact = jsonObj.getString("Contact");
                                String Date = jsonObj.getString("Date");
                                String Exercise_Id = jsonObj.getString("Exercise_Id");
                                String Email_Id = jsonObj.getString("Email_Id");
                                String LevelName = jsonObj.getString("LevelName");
                                String Member_Id = jsonObj.getString("Member_Id");
                                String Image = jsonObj.getString("Image");
                                String Instructarname = jsonObj.getString("Instructarname");

                                subList.setMemberName(name);

                                String cont= Utility.lastFour(Contact);
                                subList.setMemberContact(Contact);
                                subList.setExerciseId(Exercise_Id);
                                subList.setEmailId(Email_Id);
                                String next_foll_date= Utility.formatDate(Date);
                                subList.setAssignDate(next_foll_date);
                                subList.setMemberId(Member_Id);
                                Image.replace("\"", "");
                                subList.setMemberImage(Image);
                                subList.setLevel(LevelName);
                                subList.setInstructorName(Instructarname);
                                subListArrayList.add(subList);


                            }
                        }
                        Intent intent=new Intent(WorkoutFilterActivity.this, WorkoutActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("filter_array_list", subListArrayList);
                        intent.putExtra("BUNDLE",bundle);

                        startActivity(intent);

                    }
                }
            }


            else if (success.equalsIgnoreCase(getResources().getString(R.string.zero)))
            {
                Toast.makeText(WorkoutFilterActivity.this,"No Records Found",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(WorkoutFilterActivity.this, WorkoutActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(WorkoutFilterActivity.this,WorkoutActivity.class);
        startActivity(intent);
    }
}
