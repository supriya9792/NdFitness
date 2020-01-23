package com.ndfitnessplus.Activity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.ndfitnessplus.Adapter.AddEnquirySpinnerAdapter;
import com.ndfitnessplus.Adapter.SearchContactAdapter;
import com.ndfitnessplus.Adapter.SearchNameAdapter;
import com.ndfitnessplus.Model.MealEditTextList;
import com.ndfitnessplus.Model.Search_list;
import com.ndfitnessplus.Model.Spinner_List;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.Utility;
import com.ndfitnessplus.Utility.ViewAnimation;
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
import java.util.List;

public class AddDietActivity extends AppCompatActivity {
    public final String TAG = AddDietActivity.class.getName();
    private List<View> view_list = new ArrayList<>();
    private List<RelativeLayout> step_view_list = new ArrayList<>();
    private int success_step = 0;
    private int current_step = 0;
    private View parent_view;
    ProgressBar step1process, step2process,step3process;

    //Step One Intialization
    private AutoCompleteTextView inputName;
    AutoCompleteTextView inputContact;
    private TextInputLayout inputLayoutName,inputLayoutContact;
    RadioGroup radioGroup;
    String gender="";

    //step two intialization
    TextInputLayout inputLayoutDays, inputLayoutEndDate, inputLayoutWeight, inputLayoutPurpose,inputLayoutNoOfMeals;
    EditText inputDays,inputEndDate,inputWeight,inputPurpose,inputNoOfMeals;
    Spinner_List dietitionNamelist;
    ArrayList<Spinner_List> dietitionNameArrayList = new ArrayList<Spinner_List>();
    AddEnquirySpinnerAdapter dietitionNameAdapter;
    Spinner spinDietitionName;
    TextView txtDietitionName;
    String dietition_name;

    //step three intialization
    EditText inputMeal, inputTime;
    TextInputLayout inputLayoutMeal, inputLayoutTime,inputLayoutMessage;
    AppCompatEditText message;
    LinearLayout parentLinearLayout;

    //Step four intialization
    EditText inputAdvice, inputCharges,inputPayDetails;
    TextInputLayout inputLayoutAdvice, inputLayoutCharges,inputLayoutPayDetails;

    private AwesomeValidation stepOneValidation, stepTwoValidation,stepThreeValation,stepFourValation;

    int mHour;
    int mMinute;
    int mSec;
    String MemberID;
    String Email="";
    ViewDialog viewDialog;
    List<MealEditTextList> allEds;
    String[] strings;
    Search_list searchModel;
    ArrayList<Search_list> searchArrayList = new ArrayList<Search_list>();
    public SearchNameAdapter searchnameadapter;
    SearchContactAdapter searchcontactadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_add_diet);

        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.add_diet));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){
        viewDialog = new ViewDialog(this);

        view_list.add(findViewById(R.id.step_one));
        view_list.add(findViewById(R.id.step_two));
        view_list.add(findViewById(R.id.step_three));
        view_list.add(findViewById(R.id.step_four));

        step1process = findViewById(R.id.progress);
        step2process = findViewById(R.id.progress1);
        step3process = findViewById(R.id.progress2);

        parentLinearLayout = (LinearLayout) findViewById(R.id.step_three);

        // populate view step (circle in left)
        step_view_list.add(((RelativeLayout) findViewById(R.id.step_title)));
        step_view_list.add(((RelativeLayout) findViewById(R.id.step_description)));
        step_view_list.add(((RelativeLayout) findViewById(R.id.step_time)));
        step_view_list.add(((RelativeLayout) findViewById(R.id.step_details)));

        //Step One
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutContact = (TextInputLayout) findViewById(R.id.input_layout_cont);
        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);
        inputName = (AutoCompleteTextView) findViewById(R.id.input_name);
        inputContact = (AutoCompleteTextView) findViewById(R.id.input_cont);

        //Step Two
        inputLayoutDays = (TextInputLayout) findViewById(R.id.input_layout_days);
        inputLayoutEndDate = (TextInputLayout) findViewById(R.id.input_layout_end_date);
        inputLayoutWeight= (TextInputLayout) findViewById(R.id.input_layout_weight);
        inputLayoutPurpose = (TextInputLayout) findViewById(R.id.input_layout_purpose);
        inputLayoutNoOfMeals = (TextInputLayout) findViewById(R.id.input_layout_no_of_meals);
        inputDays = (EditText) findViewById(R.id.input_days);
        inputEndDate = (EditText) findViewById(R.id.input_end_date);
        inputWeight = (EditText) findViewById(R.id.input_weight);
        inputPurpose = (EditText) findViewById(R.id.input_purpose);
        inputNoOfMeals = (EditText) findViewById(R.id.input_no_of_meals);
        spinDietitionName=(Spinner)findViewById(R.id.spinner_dietition_name);
        txtDietitionName=(TextView)findViewById(R.id.txt_dietition_name);

        //step Three
//        inputLayoutMeal = (TextInputLayout) findViewById(R.id.input_layout_meal);
//        inputLayoutTime = (TextInputLayout) findViewById(R.id.input_layout_time);
//        inputLayoutMessage = (TextInputLayout) findViewById(R.id.input_layout_message);
//        inputMeal = (EditText) findViewById(R.id.input_meal);
//        inputTime = (EditText) findViewById(R.id.input_time);
//        message = (AppCompatEditText) findViewById(R.id.input_message);

        //Step Four
        inputLayoutAdvice = (TextInputLayout) findViewById(R.id.input_layout_advice);
        inputLayoutCharges = (TextInputLayout) findViewById(R.id.input_layout_charges);
        inputLayoutPayDetails = (TextInputLayout) findViewById(R.id.input_layout_pay_details);
        inputAdvice = (EditText) findViewById(R.id.input_advice);
        inputCharges= (EditText) findViewById(R.id.input_charges);
        inputPayDetails = (EditText) findViewById(R.id.input_pay_details);


        stepOneValidation = new AwesomeValidation(ValidationStyle.BASIC);
        stepTwoValidation = new AwesomeValidation(ValidationStyle.BASIC);
        stepFourValation = new AwesomeValidation(ValidationStyle.BASIC);
        stepThreeValation = new AwesomeValidation(ValidationStyle.BASIC);

        stepOneValidation.addValidation(this, R.id.input_cont, RegexTemplate.NOT_EMPTY, R.string.err_msg_cont);
        stepOneValidation.addValidation(this, R.id.input_name, RegexTemplate.NOT_EMPTY, R.string.err_msg_name);

        stepTwoValidation.addValidation(this, R.id.input_days, RegexTemplate.NOT_EMPTY, R.string.err_msg_days);
        stepTwoValidation.addValidation(this, R.id.input_no_of_meals, RegexTemplate.NOT_EMPTY, R.string.err_msg_no_of_meals);


        stepFourValation.addValidation(this, R.id.input_charges, RegexTemplate.NOT_EMPTY, R.string.err_msg_charges);
        stepFourValation.addValidation(this, R.id.input_pay_details, RegexTemplate.NOT_EMPTY, R.string.err_msg_pay_details);
        dietitionNameClass();
        for (View v : view_list) {
            v.setVisibility(View.GONE);
        }

        view_list.get(0).setVisibility(View.VISIBLE);

        inputContact.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(inputContact.getText().length()>0){
                    CheckContactClass();
                }


            }
        });
        inputDays.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String EndDate="";
                if(inputDays.getText().length()>0) {
                    int duration = Integer.parseInt(inputDays.getText().toString());
                   EndDate = Utility.CalulateDateFromGivenDays(Utility.getCurrentDate(), duration);
                }
                inputEndDate.setText(EndDate);


            }
        });
        inputNoOfMeals.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }



            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {
                if(inputNoOfMeals.getText().length()>0) {
                    String disc=inputNoOfMeals.getText().toString();
                    double discount=Double.parseDouble(disc);

                    Log.v(TAG, String.format("Discount  :: souble discout= %s", discount));
                    if(discount > 10){
                        inputNoOfMeals.setText("");
                        Toast.makeText(AddDietActivity.this,"No of Meals should not be greater than 10",Toast
                                .LENGTH_SHORT).show();

                    }else if(discount == 0){
                        Toast.makeText(AddDietActivity.this,"No of Meals Must be Greater than zero",Toast
                                .LENGTH_SHORT).show();
                        inputNoOfMeals.setError(null);
                        inputNoOfMeals.getText().clear();
                    }
                }
            }
        });

        spinDietitionName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                    dietition_name = tv.getText().toString();
                    if (index != 0) {
                        txtDietitionName.setVisibility(View.VISIBLE);

                    }

                }
                // ((TextView) spinDietitionName.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
                // Showing selected spinner item
                //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(parent.getContext(), "Please Select Package Type ", Toast.LENGTH_LONG).show();
            }
        });
        spinDietitionName.setOnTouchListener(new View.OnTouchListener() {

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

                if(gender.equals("Male"))
                    radioGroup.check(R.id.radioButton);
                else  if(gender.equals("Female"))
                    radioGroup.check(R.id.radioButton2);
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
                    radioGroup.clearCheck();
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

                if(gender.equals("Male"))
                    radioGroup.check(R.id.radioButton);
                else  if(gender.equals("Female"))
                    radioGroup.check(R.id.radioButton2);

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
                    radioGroup.clearCheck();
                }
            }
        });

    }
    public void clickAction(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_next1:
                if (stepOneValidation.validate()) {
                    if(radioGroup.getCheckedRadioButtonId()!=-1){
                        int id1= radioGroup.getCheckedRadioButtonId();
                        View radioButton = radioGroup.findViewById(id1);
                        int radioId = radioGroup.indexOfChild(radioButton);
                        RadioButton btn = (RadioButton) radioGroup.getChildAt(radioId);
                        gender= (String) btn.getText();
                        //  Toast.makeText(AddEnquiryActivity.this, gender, Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(AddDietActivity.this, "Please Select Gender", Toast.LENGTH_SHORT).show();
                    }
                step1process.setProgress(100);
                        collapseAndContinue(0);

                }
                break;
            case R.id.btn_next2:
                if (stepTwoValidation.validate()) {
                    step2process.setProgress(100);
                    if(dietition_name.equals(getResources().getString(R.string.dietition_name)) ){
                        Toast.makeText(this, "Please select Dietitian Name", Toast.LENGTH_LONG).show();
                    }else{
                    collapseAndContinue(1);
                    if(inputNoOfMeals.getText().length()>0) {
                        int count = Integer.parseInt(inputNoOfMeals.getText().toString());
                        allEds = new ArrayList<MealEditTextList>();
                        strings = new String[allEds.size()];
                        int k=11;
                        for (int i = 0; i < count; i++) {
                            MealEditTextList editTextList=new MealEditTextList();
                            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            final View rowView = inflater.inflate(R.layout.add_meals, null);
                            // Add the new row before the add field button.
                            // inputMeal.setHint((getResources().getString(R.string.meals))+i);
                            inputLayoutMeal = rowView.findViewById(R.id.input_layout_meal);
                            inputLayoutTime = rowView.findViewById(R.id.input_layout_time);
                            inputLayoutMessage = (TextInputLayout)rowView.findViewById(R.id.input_layout_message);
                            inputMeal = (EditText)rowView. findViewById(R.id.input_meal);
                            inputTime = (EditText)rowView. findViewById(R.id.input_time);
                            message = (AppCompatEditText) rowView.findViewById(R.id.input_message);
                            inputTime.setId(i);

                            inputMeal.setId(k);
                            String mhint = getResources().getString(R.string.meals) + " " + (i + 1);
                            inputLayoutMeal.setHint(mhint);
                            inputMeal.setHint(mhint);
                            inputLayoutMessage.setHint(mhint);
                            inputLayoutTime.setHint(getResources().getString(R.string.time) + " " + (i + 1));
//                            for (int j = 0; j < 10; j++) {
                                editTextList.setInputTime(inputTime);
                                editTextList.setInputMeal(inputMeal);
                                editTextList.setInputMessage(message);
                                allEds.add(editTextList);
//                            }
                            //inputTime.setText(10 + ":" + 19);
                            inputTime.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(final View v) {
                                  timePicker(rowView,v.getId());

                                }
                            });
                            final  int j=i;
                            Log.v(TAG, String.format("meal id = %s", allEds.get(i).getInputMeal().getId()));

                            allEds.get(i).getInputMeal().addTextChangedListener(new TextWatcher() {
                                //
                                public void onTextChanged(CharSequence s, int start, int before,
                                                          int count) {
                                    if(count>0){
                                        String ml=  allEds.get(j).getInputMeal().getHint().toString();
                                        String mmll[]=ml.split(" ");
                                        int idd=0;
                                        if(mmll.length>0){
                                            idd=Integer.parseInt(mmll[1]);
                                            Log.v(TAG, String.format("id = %s", idd));
                                            //inputTime.setError("please enter time");
                                            stepThreeValation.addValidation(AddDietActivity.this,(idd-1), RegexTemplate.NOT_EMPTY, R.string.err_msg_time);
                                        }
                                    }else{
                                        stepThreeValation.addValidation(AddDietActivity.this, allEds.get(j).getInputMeal().getId(), RegexTemplate.NOT_EMPTY, R.string.err_msg_meal);
                                    }

                                }

                                public void beforeTextChanged(CharSequence s, int start, int count,
                                                              int after) {

                                }

                                public void afterTextChanged(Editable s) {

                                }
                            });

                           // stepThreeValation.addValidation(AddDietActivity.this,i, RegexTemplate.NOT_EMPTY, R.string.err_msg_charges);
                            k++;
                            parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);
                            stepThreeValation.addValidation(AddDietActivity.this, allEds.get(j).getInputMeal().getId(), RegexTemplate.NOT_EMPTY, R.string.err_msg_meal);
                            stepThreeValation.addValidation(AddDietActivity.this, allEds.get(j).getInputTime().getId(), RegexTemplate.NOT_EMPTY, R.string.err_msg_meal);
                        }

                    }
                    }
                }

                break;
            case R.id.btn_next3:

                if (stepThreeValation.validate()) {
                      step3process.setProgress(100);
                     collapseAndContinue(2);
                }

                break;
            case R.id.btn_submit:
                // validate input user here
                if (stepFourValation.validate()) {

                        AddDietClass();

                    //step2process.setProgress(100);

                    //Toast.makeText(AddDietActivity.this ,"Text then: "+inputTime.getText().toString() , Toast.LENGTH_LONG).show();
                    //collapseAll();
                    //stepregisterUser();
                }

                break;
            case R.id.btn_back1:
                // validate input user here

                collapseAndBack(1);
                break;
//            case R.id.btn_back2:
//                // validate input user here
//                collapseAndBack(2);
//                break;
            case R.id.btn_back3:
                // validate input user here
                collapseAndBack(3);
                break;
        }
    }
    private void collapseAndContinue(int index) {
        ViewAnimation.collapse(view_list.get(index));
        setCheckedStep(index);
        index++;
        current_step = index;
        success_step = index > success_step ? index : success_step;
        ViewAnimation.expand(view_list.get(index));
    }
    private void collapseAndBack(int index) {
        ViewAnimation.collapse(view_list.get(index));
        //setCheckedStep(index);
        index--;
        current_step = index;
        success_step = index > success_step ? index : success_step;
        ViewAnimation.expand(view_list.get(index));
    }

    private void collapseAll() {
        for (View v : view_list) {
            ViewAnimation.collapse(v);
        }
    }

    public void hideSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
    private void setCheckedStep(int index) {
        RelativeLayout relative = step_view_list.get(index);
        relative.removeAllViews();
        ImageButton img = new ImageButton(this);
        img.setImageResource(R.drawable.ic_done);
        img.setBackgroundColor(Color.TRANSPARENT);
        img.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        relative.addView(img);
    }

    private void timePicker(final View row,final  int pos){
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        mHour = hourOfDay;
                        mMinute = minute;
                        //nextfolltime=hourOfDay + ":" + minute;
                        inputTime = (EditText)row. findViewById(pos);
                        try {
                            SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
                            Date date = fmt.parse(hourOfDay + ":" + minute);
                            SimpleDateFormat fmtOut = new SimpleDateFormat("HH:mm");
                            fmtOut.format(date);
                            inputTime.setText(fmtOut.format(date));
                            inputTime.setError(null);
                        } catch (ParseException e) {

                        }

                       // stepThreeValation.clear();
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();

    }

    public void  CheckContactClass() {
        AddDietActivity.CheckContactTrackClass ru = new AddDietActivity.CheckContactTrackClass();
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
            // dismissProgressDialog();
            viewDialog.hideDialog();
            //Toast.makeText(CandiateListView.this, response, Toast.LENGTH_LONG).show();
            //  Toast.makeText(NewCustomerActivity.this, response, Toast.LENGTH_LONG).show();
            CheckContactDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            // Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> EnquiryForDetails = new HashMap<String, String>();

            EnquiryForDetails.put("mobileno",inputContact.getText().toString() );
            EnquiryForDetails.put("user","Member" );
            EnquiryForDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(AddDietActivity.this) );
            EnquiryForDetails.put("action", "check_mobile_already_exist_or_not");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(AddDietActivity.this);
            //EnquiryForloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(EnquiryForloyee.this));
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
                Toast.makeText(AddDietActivity.this,"Member is not registred. Please register Member first",Toast.LENGTH_SHORT).show();
                //inputContact.getText().clear();
                // showCustomDialog();
                Intent intent=new Intent(AddDietActivity.this,AddMemberActivity.class);
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
                            gender = jsonObj.getString("Gender");
                            inputName.setText(Name);

                            if(gender.equals("Male"))
                                radioGroup.check(R.id.radioButton);
                            else
                                radioGroup.check(R.id.radioButton2);

                        }
                    }
                } else if (jsonArrayResult.length() == 0) {
                    System.out.println("No records found");
                }
                // Toast.makeText(AddEnquiryActivity.this,"Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
            }else if (success.equalsIgnoreCase(getResources().getString(R.string.one)))
            {
                Toast.makeText(AddDietActivity.this,"member has no active course.Please add course first",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(AddDietActivity.this,RenewActivity.class);
                intent.putExtra("contact",inputContact.getText().toString());
                startActivity(intent);
                // Toast.makeText(AddEnquiryActivity.this,"Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void  AddDietClass() {
        AddDietActivity.AddDietTrackClass ru = new AddDietActivity.AddDietTrackClass();
        ru.execute("5");
    }
    class AddDietTrackClass extends AsyncTask<String, Void, String> {

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
            //Toast.makeText(CandiateListView.this, response, Toast.LENGTH_LONG).show();
            //  Toast.makeText(NewCustomerActivity.this, response, Toast.LENGTH_LONG).show();
            AddDietDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));

            HashMap<String, String> AddDietDetails = new HashMap<String, String>();
            AddDietDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(AddDietActivity.this));
            Log.v(TAG, String.format("doInBackground :: comp_id = %s", SharedPrefereneceUtil.getSelectedBranchId(AddDietActivity.this)));
            AddDietDetails.put("member_id",MemberID);
            Log.v(TAG, String.format("doInBackground :: member_id = %s", MemberID));
            AddDietDetails.put("member_name",inputName.getText().toString());
            Log.v(TAG, String.format("doInBackground :: name = %s", inputName.getText().toString()));
            AddDietDetails.put("contact",inputContact.getText().toString());
            Log.v(TAG, String.format("doInBackground :: contact = %s", inputContact.getText().toString()));
            AddDietDetails.put("email",Email);
            Log.v(TAG, String.format("doInBackground :: Email = %s", Email));
            AddDietDetails.put("days",inputDays.getText().toString() );
            Log.v(TAG, String.format("doInBackground :: days = %s", inputDays.getText().toString()));
            AddDietDetails.put("gender",gender);
            Log.v(TAG, String.format("doInBackground :: gender = %s", gender));
            AddDietDetails.put("end_date",inputEndDate.getText().toString());
            Log.v(TAG, String.format("doInBackground :: endate = %s", inputEndDate.getText().toString()));
            AddDietDetails.put("dietition_name", dietition_name);
            Log.v(TAG, String.format("doInBackground :: dietition_name = %s", dietition_name));
            AddDietDetails.put("no_of_meals",inputNoOfMeals.getText().toString());
            Log.v(TAG, String.format("doInBackground :: no_of_meals = %s", inputNoOfMeals.getText().toString()));
            AddDietDetails.put("weight",inputWeight.getText().toString());
            Log.v(TAG, String.format("doInBackground :: weight = %s", inputWeight.getText().toString()));
            AddDietDetails.put("purpose",inputPurpose.getText().toString());
            Log.v(TAG, String.format("doInBackground :: purpose = %s", inputPurpose.getText().toString()));
            if(allEds.size()<=10) {
                for (int j = 0; j < allEds.size(); j++) {
                    String mealtype="meal_type" + (j+1);
                    String meal="meal_"+(j+1);
                    String time="time_"+(j+1);
                    AddDietDetails.put(mealtype, allEds.get(j).getInputMeal().getText().toString());
                    Log.v(TAG, String.format("doInBackground :: meal_type\"  + (j+1)+\" = %s", allEds.get(j).getInputMeal().getText().toString()));
                    AddDietDetails.put(meal, allEds.get(j).getInputMessage().getText().toString());
                    Log.v(TAG, String.format("doInBackground :: meal_1 = %s", allEds.get(j).getInputMessage().getText().toString()));
                    AddDietDetails.put(time, allEds.get(j).getInputTime().getText().toString());
                }
                int mm=10-allEds.size();
                for (int j = allEds.size(); j < 10; j++) {
                    String mealtype="meal_type" + (j+1);
                    String meal="meal_"+(j+1);
                    String time="time_"+(j+1);
                    AddDietDetails.put(mealtype, "");
                    Log.v(TAG, String.format("doInBackground ::  meal_type"  + (j+1)+" = %s", ""));
                    AddDietDetails.put(meal, "");
                    Log.v(TAG, String.format("doInBackground :: meal_1 = %s", ""));
                    AddDietDetails.put(time, "");
                }
            }else{
                int mm=10-allEds.size();
                for (int j = allEds.size(); j < 10; j++) {
                    String mealtype="meal_type" + (j+1);
                    String meal="meal_"+(j+1);
                    String time="time_"+(j+1);
                    AddDietDetails.put(mealtype, "");
                    Log.v(TAG, String.format("doInBackground :: meal_type"  + (j+1)+" = %s", ""));
                    AddDietDetails.put(meal, "");
                    Log.v(TAG, String.format("doInBackground :: meal_1 = %s", ""));
                    AddDietDetails.put(time, "");
                }
            }

            AddDietDetails.put("advice",inputAdvice.getText().toString());
            AddDietDetails.put("charges",inputCharges.getText().toString());
            Log.v(TAG, String.format("doInBackground :: executive name= %s", SharedPrefereneceUtil.getName(AddDietActivity.this)));
            AddDietDetails.put("pay_details",inputPayDetails.getText().toString());
            Log.v(TAG, String.format("doInBackground :: subtotal = %s", inputPayDetails.getText().toString()));

            AddDietDetails.put("action", "add_diet");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(AddDietActivity.this);
            String loginResult2 = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, AddDietDetails);

            Log.v(TAG, String.format("doInBackground :: add_course= %s", loginResult2));
            return loginResult2;
        }
    }


    private void AddDietDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject jsonObjLoginResponse = null;
        try {
            jsonObjLoginResponse = new JSONObject(jsonResponse);
            String success = jsonObjLoginResponse.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                 Toast.makeText(AddDietActivity.this,"Diet added succesfully",Toast.LENGTH_SHORT).show();
                 finish();
                 Intent intent=new Intent(AddDietActivity.this,DietActivity.class);
                 startActivity(intent);
            }
            else if (success.equalsIgnoreCase(getResources().getString(R.string.one)))
            {
                Toast.makeText(AddDietActivity.this,"Mobile Number Already Exits ,Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
                inputContact.getText().clear();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void  dietitionNameClass() {
        AddDietActivity.DietitionNameTrackClass ru = new AddDietActivity.DietitionNameTrackClass();
        ru.execute("5");
    }
    class DietitionNameTrackClass extends AsyncTask<String, Void, String> {

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
            DietitionNameDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            // Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> DietitionNameDetails = new HashMap<String, String>();
            DietitionNameDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(AddDietActivity.this));
            DietitionNameDetails.put("action", "show_dietition_name_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(AddDietActivity.this);
            //DietitionNameloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(DietitionNameloyee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, DietitionNameDetails);
            Log.v(TAG, String.format("doInBackground :: show_dietition_name_list= %s", loginResult));
            return loginResult;
        }


    }


    private void DietitionNameDetails(String jsonResponse) {


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
                        dietitionNameArrayList.clear();
                        dietitionNamelist = new Spinner_List();
                        dietitionNamelist.setName(getResources().getString(R.string.dietition_name));
                        dietitionNameArrayList.add(0,dietitionNamelist);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                dietitionNamelist = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Name     = jsonObj.getString("Name");

//                               if(i==0){
//                                   dietitionNamelist.setName(getResources().getString(R.string.promt_country));
//                                   enqF.add(0,dietitionNamelist);
//                               }
                                    dietitionNamelist.setName(Name);

                                    dietitionNameArrayList.add(dietitionNamelist);

                                    dietitionNameAdapter = new AddEnquirySpinnerAdapter(AddDietActivity.this, dietitionNameArrayList){
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
                                                tv.setText(getResources().getString(R.string.prompt_dietition_name));
                                                // tv.setTextColor(Color.GRAY);
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinDietitionName.setAdapter(dietitionNameAdapter);


                                }
                            }
                        }else if(jsonArrayCountry.length()==0){
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AddDietActivity.this);
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
        SearchTrackClass ru = new SearchTrackClass();
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

            SearchDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(AddDietActivity.this) );
            SearchDetails.put("action", "show_all_member_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(AddDietActivity.this);
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

                                String namec=Name+"-"+Contact;
                                searchModel.setCustName(Name);
                                searchModel.setCustContact(Contact);
                                searchModel.setMemberId(MemberID);
                                searchModel.setEmail(Email);
                                searchModel.setGender(Gender);
                                searchModel.setNameContact(namec);

                                searchArrayList.add(searchModel);
                                searchnameadapter = new SearchNameAdapter(AddDietActivity.this, searchArrayList);

                                inputName.setAdapter(searchnameadapter);
                               // inputName.setDropDownBackgroundResource(R.drawable.search_background);
                                inputName.setThreshold(1);

                                searchcontactadapter = new SearchContactAdapter(AddDietActivity.this, searchArrayList);

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
