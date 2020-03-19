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
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.ndfitnessplus.Activity.Notification.OtherFollowupActivity;
import com.ndfitnessplus.Activity.Notification.TodaysEnrollmentActivity;
import com.ndfitnessplus.Adapter.AddEnquirySpinnerAdapter;
import com.ndfitnessplus.Adapter.EnquiryAdapter;
import com.ndfitnessplus.Adapter.SpinnerAdapter;
import com.ndfitnessplus.Model.EnquiryList;
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

public class EnquiryFilterActivity extends AppCompatActivity {
    public final String TAG = EnquiryFilterActivity.class.getName();
    private ProgressDialog pd;
    //Spinner Adapter
    public Spinner spinEnquiryType,spinEnquirySource,spinEnqFor,spinCallResponce,spinRating,spinEnqDate,spinGender,spinLocation,spinOccupation,spinExecutive;
    Spinner_List enquirytypelist,enquirySourcelist,enqForList,spinCallReslist,ratingList,EnqdateList,GenderList,Occupationlist,ExecutiveNameList,LocationList;
    ArrayList<Spinner_List> enquiryTypeArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> enquirySourceArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> enqForArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> CallResArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> ratingArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> EnqdateArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> GenderArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> OccupationArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> ExecutiveNameArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> LocationArrayList = new ArrayList<Spinner_List>();
    public SpinnerAdapter enquirytypeadapter,enquirySourceadapter,enqforadapter,callresadapter,ratingadapter,enqdateadapter,genderadapter,locationadapter,
                          occupationadpater,executivenameadapter;
    String enquiryType,enquirySource,callResponce,Rating,enquiryFor,gender,enquiryDate,occupation,location,executiveName;

    TextView todate,fromdate;
    ImageButton toDatebtn,fromDateBtn;
    private int mYear, mMonth, mDay;

    ArrayList<EnquiryList> subListArrayList = new ArrayList<EnquiryList>();
    EnquiryList subList;
    Button btn_applyFilter;
    //Loading gif
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_enquiry_filter);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.enquiry_filters));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){

        //spinners
        spinEnquiryType = (Spinner) findViewById(R.id.spinner_enq_type);
        spinEnquirySource = (Spinner) findViewById(R.id.spinner_enq_source);
        spinCallResponce = (Spinner) findViewById(R.id.spinner_call_res);
        spinRating = (Spinner) findViewById(R.id.spinner_rating);
        spinEnqFor = (Spinner) findViewById(R.id.spinner_enq_for);
        spinGender = (Spinner) findViewById(R.id.spinner_enq_gender);
        spinEnqDate = (Spinner) findViewById(R.id.spinner_enq_date);
        spinOccupation = (Spinner) findViewById(R.id.spinner_occupation);
        spinLocation = (Spinner) findViewById(R.id.spinner_location);
        spinExecutive = (Spinner) findViewById(R.id.spinner_executive);
        viewDialog = new ViewDialog(this);
        todate=findViewById(R.id.to_date);
        fromdate=findViewById(R.id.from_date);
        fromDateBtn=findViewById(R.id.btn_from_date);
        toDatebtn=findViewById(R.id.btn_to_date);
        btn_applyFilter=findViewById(R.id.btn_apply_filters);

        String firstday=Utility.getFirstDayofMonth();
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


                DatePickerDialog datePickerDialog = new DatePickerDialog(EnquiryFilterActivity.this,
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


                DatePickerDialog datePickerDialog = new DatePickerDialog(EnquiryFilterActivity.this,
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
        enqforClass();
        enqtypeClass();
        enqsourceClass();
        callResponseClass();
        locationClass();
        occupationClass();
        executiveClass();


        //setting data to the spinners

        spinEnquiryType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if(view != null) {
                TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                tv.setTextSize(10);
                if(index==0){
                    tv.setText(getResources().getString(R.string.prompt_enquiry_type));
                }

                enquiryType = tv.getText().toString();

                if((enquiryType.equals(getResources().getString(R.string.prompt_enquiry_type)))||
                        (enquiryType.equals(getResources().getString(R.string.all)))){
                   enquiryType="";
                }
            }}

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Enquiry source spinner adapter setting

        spinEnquirySource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);
                    if (index == 0) {
                        tv.setText(getResources().getString(R.string.prompt_enquiry_source));
                    }
//               // tv.setTextColor(getResources().getColor(R.color.black));
                    enquirySource = tv.getText().toString();
                    if ((enquirySource.equals(getResources().getString(R.string.prompt_enquiry_source))) ||
                            (enquirySource.equals(getResources().getString(R.string.all)))) {
                        enquirySource = "";
                    }

                }
            }
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
            ratingadapter = new SpinnerAdapter(EnquiryFilterActivity.this, ratingArrayList) {
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
        spinEnqFor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if(view != null) {
                TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                tv.setTextSize(10);
                if(index==0){
                    tv.setText(getResources().getString(R.string.prompt_enq_for));
                }
                enquiryFor = tv.getText().toString();
                if((enquiryFor.equals(getResources().getString(R.string.prompt_enq_for)))||
                        (enquiryFor.equals(getResources().getString(R.string.all)))){
                    enquiryFor="";
                }
            }}

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Gender spinners
        final  String[] genderarray = getResources().getStringArray(R.array.gender_array);
        GenderList = new Spinner_List();
        GenderList.setName(getResources().getString(R.string.prompt_gender));
        GenderArrayList.add(0,GenderList);
        GenderList.setName(getResources().getString(R.string.all));
        GenderArrayList.add(1,GenderList);
        for(int i=1;i<genderarray.length;i++) {
            GenderList = new Spinner_List();
            GenderList.setName(genderarray[i]);
            GenderArrayList.add(GenderList);
            genderadapter = new SpinnerAdapter(EnquiryFilterActivity.this, GenderArrayList) {
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
                        tv.setText(getResources().getString(R.string.prompt_gender));
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }

            };
            spinGender.setAdapter(genderadapter);
        }
        spinGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if(view != null) {
                TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                tv.setTextSize(10);
                if(index==0){
                    tv.setText(getResources().getString(R.string.prompt_gender));
                }
                gender = tv.getText().toString();
                if((gender.equals(getResources().getString(R.string.prompt_gender)))||
                        (gender.equals(getResources().getString(R.string.all)))){
                    gender="";
                }
            }}

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Enquiry date type spinners
        final  String[] enquirydatearray = getResources().getStringArray(R.array.enq_date_array);

        for(int i=0;i<enquirydatearray.length;i++) {
            EnqdateList = new Spinner_List();
            EnqdateList.setName(enquirydatearray[i]);
            EnqdateArrayList.add(EnqdateList);
            enqdateadapter = new SpinnerAdapter(EnquiryFilterActivity.this, EnqdateArrayList) {
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
            spinEnqDate.setAdapter(enqdateadapter);
        }
        spinEnqDate.setSelection(1);
        spinEnqDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if (view != null) {
                TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                tv.setTextSize(10);
                if(index==0){
                    tv.setText(getResources().getString(R.string.prompt_enq_date));
                }

                enquiryDate = tv.getText().toString();
                if((enquiryDate.equals(getResources().getString(R.string.prompt_enq_date)))||
                        (enquiryDate.equals(getResources().getString(R.string.all)))){
                    enquiryDate="";
                }
            }}

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinOccupation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);
                    if (index == 0) {
                        tv.setText(getResources().getString(R.string.prompt_occupation));
                    }
                    occupation = tv.getText().toString();
                    if ((occupation.equals(getResources().getString(R.string.prompt_occupation))) ||
                            (occupation.equals(getResources().getString(R.string.all)))) {
                        occupation = "";
                    }

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Location item selection listener
        spinLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);
                    if (index == 0) {
                        tv.setText(getResources().getString(R.string.prompt_location));
                    }
                    location = tv.getText().toString();
                    if ((location.equals(getResources().getString(R.string.prompt_location))) ||
                            (location.equals(getResources().getString(R.string.all)))) {
                        location = "";
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
                Intent intent = new Intent(EnquiryFilterActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void  enqforClass() {
        EnquiryFilterActivity.EnquiryForTrackClass ru = new EnquiryFilterActivity.EnquiryForTrackClass();
        ru.execute("5");
    }
    class EnquiryForTrackClass extends AsyncTask<String, Void, String> {

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
            EnquiryForDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> EnquiryForDetails = new HashMap<String, String>();
            EnquiryForDetails.put("action", "show_enq_for_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(EnquiryFilterActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, EnquiryForDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }

    }


    private void EnquiryForDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {


            try {
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    if (object != null) {
                        JSONArray jsonArrayCountry = object.getJSONArray("result");
                        enqForArrayList.clear();
                        enqForList = new Spinner_List();
                        enqForList.setName(getResources().getString(R.string.prompt_enq_for));
                        enqForArrayList.add(0,enqForList);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            enqForList.setName(getResources().getString(R.string.all));
                            enqForArrayList.add(1,enqForList);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                enqForList = new Spinner_List();
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Enquiry_For     = jsonObj.getString("Enquiry_For");

                                    String id=jsonObj.getString("Auto_Id");

                                    enqForList.setName(Enquiry_For);
                                    enqForList.setId(id);

                                    enqForArrayList.add(enqForList);

                                    enqforadapter = new SpinnerAdapter(EnquiryFilterActivity.this, enqForArrayList){
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
                                                tv.setText(getResources().getString(R.string.prompt_enq_for));
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinEnqFor.setAdapter(enqforadapter);


                                }
                            }
                        }else if(jsonArrayCountry.length()==0){
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    enqForArrayList.clear();
                    enqForList = new Spinner_List();
                    enqForList.setName(getResources().getString(R.string.prompt_enq_for));
                    enqForArrayList.add(0,enqForList);
                    enqForList.setName(getResources().getString(R.string.all));
                    enqForArrayList.add(1,enqForList);
                    enqforadapter = new SpinnerAdapter(EnquiryFilterActivity.this, enqForArrayList){
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
                                tv.setText(getResources().getString(R.string.prompt_enq_for));
                            }
                            else {
                                tv.setTextColor(Color.BLACK);
                            }
                            return view;
                        }

                    };
                    spinEnqFor.setAdapter(enqforadapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void  enqtypeClass() {
        EnquiryFilterActivity.EnquiryTypeTrackClass ru = new EnquiryFilterActivity.EnquiryTypeTrackClass();
        ru.execute("5");
    }
    class EnquiryTypeTrackClass extends AsyncTask<String, Void, String> {

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
            EnquiryTypeDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> EnquiryTypeDetails = new HashMap<String, String>();
            EnquiryTypeDetails.put("action", "show_enq_type_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(EnquiryFilterActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, EnquiryTypeDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void EnquiryTypeDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {


            try {
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    if (object != null) {
                        JSONArray jsonArrayCountry = object.getJSONArray("result");
                        enquiryTypeArrayList.clear();
                        enquirytypelist = new Spinner_List();
                        enquirytypelist.setName(getResources().getString(R.string.prompt_enquiry_type));
                        enquiryTypeArrayList.add(0,enquirytypelist);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            enquirytypelist.setName(getResources().getString(R.string.all));
                            enquiryTypeArrayList.add(1,enquirytypelist);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                enquirytypelist = new Spinner_List();
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Enquiry_Type     = jsonObj.getString("Enquiry_type");

                                    String id=jsonObj.getString("Auto_Id");

                                    enquirytypelist.setName(Enquiry_Type);
                                    enquirytypelist.setId(id);

                                    enquiryTypeArrayList.add(enquirytypelist);

                                    enquirytypeadapter = new SpinnerAdapter(EnquiryFilterActivity.this, enquiryTypeArrayList){
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
                                                tv.setText(getResources().getString(R.string.prompt_enquiry_type));
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinEnquiryType.setAdapter(enquirytypeadapter);


                                }
                            }
                        }else if(jsonArrayCountry.length()==0){
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    enquiryTypeArrayList.clear();
                    enquirytypelist = new Spinner_List();
                    enquirytypelist.setName(getResources().getString(R.string.prompt_enquiry_type));
                    enquiryTypeArrayList.add(0,enquirytypelist);
                    enquirytypelist.setName(getResources().getString(R.string.all));
                    enquiryTypeArrayList.add(1,enquirytypelist);
                    enquirytypeadapter = new SpinnerAdapter(EnquiryFilterActivity.this, enquiryTypeArrayList){
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
                                tv.setText(getResources().getString(R.string.prompt_enquiry_type));
                            }
                            else {
                                tv.setTextColor(Color.BLACK);
                            }
                            return view;
                        }

                    };
                    spinEnquiryType.setAdapter(enquirytypeadapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void  enqsourceClass() {
        EnquiryFilterActivity.EnquirySourceTrackClass ru = new EnquiryFilterActivity.EnquirySourceTrackClass();
        ru.execute("5");
    }
    class EnquirySourceTrackClass extends AsyncTask<String, Void, String> {

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
            EnquirySourceDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> EnquirySourceDetails = new HashMap<String, String>();
            EnquirySourceDetails.put("action", "show_enq_source_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(EnquiryFilterActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, EnquirySourceDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }

    }


    private void EnquirySourceDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {


            try {
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    if (object != null) {
                        JSONArray jsonArrayCountry = object.getJSONArray("result");
                        enquirySourceArrayList.clear();
                        enquirySourcelist = new Spinner_List();
                        enquirySourcelist.setName(getResources().getString(R.string.prompt_enquiry_source));
                        enquirySourceArrayList.add(0,enquirySourcelist);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            enquirySourcelist.setName(getResources().getString(R.string.all));
                            enquirySourceArrayList.add(1,enquirySourcelist);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                enquirySourcelist = new Spinner_List();
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String SourceOf_Enquiry     = jsonObj.getString("SourceOf_Enquiry");

                                    String id=jsonObj.getString("Auto_Id");

                                    enquirySourcelist.setName(SourceOf_Enquiry);
                                    enquirySourcelist.setId(id);

                                    enquirySourceArrayList.add(enquirySourcelist);

                                    enquirySourceadapter = new SpinnerAdapter(EnquiryFilterActivity.this, enquirySourceArrayList){
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
                                                tv.setText(getResources().getString(R.string.prompt_enquiry_source));
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinEnquirySource.setAdapter(enquirySourceadapter);


                                }
                            }
                        }else if(jsonArrayCountry.length()==0){
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    enquirySourceArrayList.clear();
                    enquirySourcelist = new Spinner_List();
                    enquirySourcelist.setName(getResources().getString(R.string.prompt_enquiry_source));
                    enquirySourceArrayList.add(0,enquirySourcelist);
                    enquirySourcelist.setName(getResources().getString(R.string.all));
                    enquirySourceArrayList.add(1,enquirySourcelist);
                    enquirySourceadapter = new SpinnerAdapter(EnquiryFilterActivity.this, enquirySourceArrayList){
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
                                tv.setText(getResources().getString(R.string.prompt_enquiry_source));
                            }
                            else {
                                tv.setTextColor(Color.BLACK);
                            }
                            return view;
                        }

                    };
                    spinEnquirySource.setAdapter(enquirySourceadapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void  occupationClass() {
        EnquiryFilterActivity.OccupationTrackClass ru = new EnquiryFilterActivity.OccupationTrackClass();
        ru.execute("5");
    }
    class OccupationTrackClass extends AsyncTask<String, Void, String> {

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
            OccupationDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> OccupationDetails = new HashMap<String, String>();
            OccupationDetails.put("action", "show_occupation_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(EnquiryFilterActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, OccupationDetails);
            return loginResult;
        }


    }


    private void OccupationDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {


            try {
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    if (object != null) {
                        JSONArray jsonArrayCountry = object.getJSONArray("result");
                        OccupationArrayList.clear();
                        Occupationlist = new Spinner_List();
                        Occupationlist.setName(getResources().getString(R.string.prompt_occupation));
                        OccupationArrayList.add(0,Occupationlist);

                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            Occupationlist.setName(getResources().getString(R.string.all));

                            OccupationArrayList.add(1,Occupationlist);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                Occupationlist = new Spinner_List();
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Occupation     = jsonObj.getString("Occupation");

                                    String id=jsonObj.getString("Auto_Id");
                                    Occupationlist.setName(Occupation);
                                    Occupationlist.setId(id);
                                    OccupationArrayList.add(Occupationlist);

                                    occupationadpater = new SpinnerAdapter(EnquiryFilterActivity.this, OccupationArrayList){
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
                                                tv.setText(getResources().getString(R.string.prompt_occupation));
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinOccupation.setAdapter(occupationadpater);


                                }
                            }
                        }else if(jsonArrayCountry.length()==0){
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){

                    OccupationArrayList.clear();
                    Occupationlist = new Spinner_List();

                    Occupationlist.setName(getResources().getString(R.string.prompt_occupation));

                    OccupationArrayList.add(0,Occupationlist);
                    Occupationlist.setName(getResources().getString(R.string.all));

                    OccupationArrayList.add(1,Occupationlist);
                    occupationadpater = new SpinnerAdapter(EnquiryFilterActivity.this, OccupationArrayList){
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
                                tv.setText(getResources().getString(R.string.prompt_occupation));
                            }
                            else {
                                tv.setTextColor(Color.BLACK);
                            }
                            return view;
                        }

                    };
                    spinOccupation.setAdapter(occupationadpater);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void  locationClass() {
        EnquiryFilterActivity.LocationTrackClass ru = new EnquiryFilterActivity.LocationTrackClass();
        ru.execute("5");
    }
    class LocationTrackClass extends AsyncTask<String, Void, String> {

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
            LocationDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> LocationDetails = new HashMap<String, String>();
            LocationDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(EnquiryFilterActivity.this));
            LocationDetails.put("action", "show_location_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(EnquiryFilterActivity.this);
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, LocationDetails);
            return loginResult;
        }


    }


    private void LocationDetails(String jsonResponse) {


        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
        if (jsonResponse != null) {


            try {
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));
                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                    if (object != null) {
                        JSONArray jsonArrayCountry = object.getJSONArray("result");
                        LocationArrayList.clear();
                        LocationList = new Spinner_List();

                        LocationList.setName(getResources().getString(R.string.prompt_location));
                        LocationArrayList.add(0,LocationList);

                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            LocationList.setName(getResources().getString(R.string.all));

                            LocationArrayList.add(1,LocationList);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                LocationList = new Spinner_List();
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Location     = jsonObj.getString("Location");

                                    LocationList.setName(Location);
                                    LocationArrayList.add(LocationList);

                                    locationadapter = new SpinnerAdapter(EnquiryFilterActivity.this, LocationArrayList){
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
                                                tv.setText(getResources().getString(R.string.prompt_location));
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinLocation.setAdapter(locationadapter);


                                }
                            }
                        }else if(jsonArrayCountry.length()==0){
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    LocationArrayList.clear();
                    LocationList = new Spinner_List();

                    LocationList.setName(getResources().getString(R.string.prompt_location));

                    LocationArrayList.add(0,LocationList);
                    LocationList.setName(getResources().getString(R.string.all));

                    LocationArrayList.add(1,LocationList);
                    locationadapter = new SpinnerAdapter(EnquiryFilterActivity.this, LocationArrayList){
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
                                tv.setText(getResources().getString(R.string.prompt_location));
                            }
                            else {
                                tv.setTextColor(Color.BLACK);
                            }
                            return view;
                        }

                    };
                    spinLocation.setAdapter(locationadapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void  executiveClass() {
        EnquiryFilterActivity.ExecutiveNameTrackClass ru = new EnquiryFilterActivity.ExecutiveNameTrackClass();
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
            String comp_name=SharedPrefereneceUtil.getCompanyName(EnquiryFilterActivity.this);
            String location=SharedPrefereneceUtil.getSelectedBranch(EnquiryFilterActivity.this);
            String compid=comp_name+"-"+location+",";
            ExecutiveNameDetails.put("comp_id", compid);
            ExecutiveNameDetails.put("action", "show_executive_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(EnquiryFilterActivity.this);
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

                                    executivenameadapter = new SpinnerAdapter(EnquiryFilterActivity.this, ExecutiveNameArrayList){
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
                    executivenameadapter = new SpinnerAdapter(EnquiryFilterActivity.this, ExecutiveNameArrayList){
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
        EnquiryFilterActivity.CallResponseTrackClass ru = new EnquiryFilterActivity.CallResponseTrackClass();
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
            String domainurl=SharedPrefereneceUtil.getDomainUrl(EnquiryFilterActivity.this);
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
                        spinCallReslist = new Spinner_List();
                        spinCallReslist.setName(getResources().getString(R.string.prompt_call_res));
                        CallResArrayList.add(0,spinCallReslist);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            spinCallReslist.setName(getResources().getString(R.string.all));
                            CallResArrayList.add(1,spinCallReslist);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                spinCallReslist = new Spinner_List();
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String CallResponse     = jsonObj.getString("CallResponse");

                                    String id=jsonObj.getString("Auto_Id");

                                    spinCallReslist.setName(CallResponse);
                                    spinCallReslist.setId(id);

                                    CallResArrayList.add(spinCallReslist);
                                    callresadapter = new SpinnerAdapter(EnquiryFilterActivity.this, CallResArrayList){
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
                    spinCallReslist = new Spinner_List();
                    spinCallReslist.setName(getResources().getString(R.string.prompt_call_res));
                    CallResArrayList.add(0,spinCallReslist);
                    spinCallReslist.setName(getResources().getString(R.string.all));
                    CallResArrayList.add(1,spinCallReslist);
                    callresadapter = new SpinnerAdapter(EnquiryFilterActivity.this, CallResArrayList){
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
        EnquiryFilterActivity.SearchEnquiryTrackClass ru = new EnquiryFilterActivity.SearchEnquiryTrackClass();
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
            SearchEnquiryDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(EnquiryFilterActivity.this));
            SearchEnquiryDetails.put("to_date",todate.getText().toString());
            SearchEnquiryDetails.put("from_date",fromdate.getText().toString());
            SearchEnquiryDetails.put("gender", gender);
            SearchEnquiryDetails.put("occupation",occupation);
            SearchEnquiryDetails.put("enq_type",enquiryType);
            SearchEnquiryDetails.put("enq_src",enquirySource);
            SearchEnquiryDetails.put("enq_for",enquiryFor);
            SearchEnquiryDetails.put("rating",Rating);
            SearchEnquiryDetails.put("call_res",callResponce);
            SearchEnquiryDetails.put("exe_name",executiveName);
            SearchEnquiryDetails.put("location",location);
            SearchEnquiryDetails.put("enq_date_type",enquiryDate);
            SearchEnquiryDetails.put("action", "search_enquiry_filter");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(EnquiryFilterActivity.this);
            String loginResult2 = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, SearchEnquiryDetails);
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
                String tt_budget = object.getString("ttl_budget");
                if (object != null) {
                    JSONArray jsonArrayResult = object.getJSONArray("result");

                    if (jsonArrayResult != null && jsonArrayResult.length() > 0) {
                        for (int i = 0; i < jsonArrayResult.length(); i++) {

                            subList = new EnquiryList();

                            JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                            if (jsonObj != null) {

                                String name = jsonObj.getString("Name");
                                String gender = jsonObj.getString("Gender");
                                String Contact = jsonObj.getString("Contact");
                                String address = jsonObj.getString("Address");
                                String ExecutiveName = jsonObj.getString("ExecutiveName");
                                String Comment = jsonObj.getString("Comment");
                                String NextFollowup_Date = jsonObj.getString("NextFollowupDate");
                                String Enquiry_ID = jsonObj.getString("Enquiry_ID");
                                String Image = jsonObj.getString("Image");
                                String CallResponse = jsonObj.getString("CallResponse");
                                String Rating = jsonObj.getString("Rating");
                                String Followup_Date = jsonObj.getString("FollowupDate");
                                String Budget = jsonObj.getString("Budget");

                                subList.setName(name);
                                subList.setGender(gender);
                                String cont=Utility.lastFour(Contact);
                                subList.setContact(Contact);
                                subList.setContactEncrypt(cont);
                                subList.setAddress(address);
                                subList.setExecutiveName(ExecutiveName);
                                subList.setComment(Comment);
                                String next_foll_date= Utility.formatDate(NextFollowup_Date);
                                subList.setNextFollowUpDate(next_foll_date);
                                subList.setID(Enquiry_ID);
                                Image.replace("\"", "");
                                subList.setImage(Image);
                                subList.setRating(Rating);
                                subList.setCallResponse(CallResponse);
                                String foll_date= Utility.formatDate(Followup_Date);
                                subList.setFollowupdate(foll_date);
                                if(Budget.equals(".00")){
                                    Budget="0.00";
                                }
                                subList.setBudget(Budget);
                                subListArrayList.add(subList);


                            }
                        }
                        Intent intent=new Intent(EnquiryFilterActivity.this,EnquiryActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("filter_array_list", subListArrayList);
                        intent.putExtra("BUNDLE",bundle);
                        intent.putExtra("ttl_budget",tt_budget);
                        startActivity(intent);

                    }
                }
            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.zero)))
            {
                Toast.makeText(EnquiryFilterActivity.this,"No Records Found",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(EnquiryFilterActivity.this, EnquiryActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(EnquiryFilterActivity.this,EnquiryActivity.class);
        startActivity(intent);
    }
}
