package com.ndfitnessplus.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ndfitnessplus.Adapter.MemberAdapter;
import com.ndfitnessplus.Adapter.SearchContactAdapter;
import com.ndfitnessplus.Adapter.SearchNameAdapter;
import com.ndfitnessplus.Adapter.SearchStaffIdAdaper;
import com.ndfitnessplus.Model.Search_list;
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

import static com.ndfitnessplus.Activity.EnquiryActivity.TAG;

public class AddStaffAttendanceActivity extends AppCompatActivity {
    public final String TAG = AddStaffAttendanceActivity.class.getName();
    private ProgressDialog pd;
    private AwesomeValidation awesomeValidation;
    ViewDialog viewDialog;
    public AutoCompleteTextView inputName,inputContact, inputStaffId;
    public TextInputLayout inputLayoutName,inputLayoutContact,inputLayoutStaffId;

    Search_list searchModel;
    ArrayList<Search_list> searchArrayList = new ArrayList<Search_list>();
    public SearchNameAdapter searchnameadapter;
    SearchContactAdapter searchcontactadapter;
    SearchStaffIdAdaper searchstaffidtadapter;
    ImageView CapturedImage;
    String StaffId,Email;
    Button Present;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff_attendance);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.add_staff_attendance));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutContact = (TextInputLayout) findViewById(R.id.input_layout_cont);
        inputLayoutStaffId = (TextInputLayout) findViewById(R.id.input_layout_id);

        viewDialog = new ViewDialog(this);

        inputName = (AutoCompleteTextView) findViewById(R.id.input_name);
        inputContact = (AutoCompleteTextView) findViewById(R.id.input_cont);
        inputStaffId = (AutoCompleteTextView) findViewById(R.id.input_id);
        CapturedImage=findViewById(R.id.captured_image);
        Present=findViewById(R.id.btn_present);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        awesomeValidation.addValidation(this, R.id.input_name, RegexTemplate.NOT_EMPTY, R.string.err_msg_name);
        awesomeValidation.addValidation(this, R.id.input_cont, RegexTemplate.NOT_EMPTY, R.string.err_msg_cont);
        awesomeValidation.addValidation(this, R.id.input_id, RegexTemplate.NOT_EMPTY, R.string.err_msg_id);
        showSearchListClass();

        inputContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // String selection = (String)parent.getItemAtPosition(position);
                // Toast.makeText(MainNavigationActivity.this,"this is autocomplete suggestions"+selection,Toast.LENGTH_SHORT).show();
                String countryName = searchcontactadapter.getItem(position).getCustName();
                String contact = searchcontactadapter.getItem(position).getCustContact();
                StaffId = searchcontactadapter.getItem(position).getMemberId();
                Email=searchcontactadapter.getItem(position).getEmail();
                String Image = searchcontactadapter.getItem(position).getImage();
                inputName.setText(countryName);
                inputName.setError(null);
                inputContact.setText(contact);
                inputStaffId.setText(StaffId);
                CapturedImage.setVisibility(View.VISIBLE);
                String img=Image.replace("\"", "");
                String domainurl=SharedPrefereneceUtil.getDomainUrl(AddStaffAttendanceActivity.this);
                final String url= domainurl+ServiceUrls.IMAGES_URL + img;

                if(!(img.equals("null")||img.equals(""))) {
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.nouser);
                    requestOptions.error(R.drawable.nouser);


                    Glide.with(AddStaffAttendanceActivity.this)
                            .setDefaultRequestOptions(requestOptions)
                            .load(url).into(CapturedImage);
                }

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
                    inputStaffId.getText().clear();
                }
            }
        });

        inputName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                // String selection = (String)parent.getItemAtPosition(position);
                // Toast.makeText(MainNavigationActivity.this,"this is autocomplete suggestions"+selection,Toast.LENGTH_SHORT).show();
                String countryName = searchnameadapter.getItem(position).getCustName();
                String contact = searchnameadapter.getItem(position).getCustContact();
                StaffId = searchnameadapter.getItem(position).getMemberId();

                Email=searchcontactadapter.getItem(position).getEmail();
                String Image = searchcontactadapter.getItem(position).getImage();

                inputName.setText(countryName);
                inputContact.setError(null);
                inputContact.setText(contact);
                inputStaffId.setText(StaffId);
                CapturedImage.setVisibility(View.VISIBLE);

                String img=Image.replace("\"", "");
                String domainurl=SharedPrefereneceUtil.getDomainUrl(AddStaffAttendanceActivity.this);
                final String url= domainurl+ServiceUrls.IMAGES_URL + img;

                if(!(img.equals("null")||img.equals(""))) {
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.nouser);
                    requestOptions.error(R.drawable.nouser);


                    Glide.with(AddStaffAttendanceActivity.this)
                            .setDefaultRequestOptions(requestOptions)
                            .load(url).into(CapturedImage);
                }

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
                    inputStaffId.getText().clear();
                }
            }
        });
        inputStaffId.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                // String selection = (String)parent.getItemAtPosition(position);
                // Toast.makeText(MainNavigationActivity.this,"this is autocomplete suggestions"+selection,Toast.LENGTH_SHORT).show();
                String countryName = searchnameadapter.getItem(position).getCustName();
                String contact = searchnameadapter.getItem(position).getCustContact();
                StaffId = searchnameadapter.getItem(position).getMemberId();

                Email=searchcontactadapter.getItem(position).getEmail();
                String Image = searchcontactadapter.getItem(position).getImage();

                inputName.setText(countryName);
                inputContact.setError(null);
                inputContact.setText(contact);
                inputStaffId.setText(StaffId);
                CapturedImage.setVisibility(View.VISIBLE);

                String img=Image.replace("\"", "");
                String domainurl=SharedPrefereneceUtil.getDomainUrl(AddStaffAttendanceActivity.this);
                final String url= domainurl+ServiceUrls.IMAGES_URL + img;

                if(!(img.equals("null")||img.equals(""))) {
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.nouser);
                    requestOptions.error(R.drawable.nouser);


                    Glide.with(AddStaffAttendanceActivity.this)
                            .setDefaultRequestOptions(requestOptions)
                            .load(url).into(CapturedImage);
                }

            }
        });
        inputStaffId.addTextChangedListener(new TextWatcher() {
            //
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }



            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {
                if(inputStaffId.getText().length()==0){
                    inputContact.getText().clear();
                    inputName.getText().clear();
                }
            }
        });

        Present.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(awesomeValidation.validate()){
                    makeattendanceclass();
                }
            }
        });

    }

    public void  showSearchListClass() {
        AddStaffAttendanceActivity.SearchTrackClass ru = new AddStaffAttendanceActivity.SearchTrackClass();
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

            SearchDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(AddStaffAttendanceActivity.this) );
            SearchDetails.put("action", "show_all_staff_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(AddStaffAttendanceActivity.this);
            //EmployeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(Employee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, SearchDetails);
            Log.v(TAG, String.format("doInBackground :: show_all_staff_list= %s", loginResult));
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
                                String Staff_Id     = jsonObj.getString("Staff_Id");
                                String Email = jsonObj.getString("Email");
                                String Gender = jsonObj.getString("Gender");
                                String Image = jsonObj.getString("Image");


                                //  String email = jsonObj.getString("email");
                                // String phn_no = jsonObj.getString("mobile");

                                String namec=Name+"-"+Contact;
                                searchModel.setCustName(Name);
                                searchModel.setCustContact(Contact);
                                searchModel.setMemberId(Staff_Id);
                                searchModel.setEmail(Email);
                                searchModel.setGender(Gender);
                                searchModel.setNameContact(namec);
                                searchModel.setImage(Image);

                                searchArrayList.add(searchModel);
                                searchnameadapter = new SearchNameAdapter(AddStaffAttendanceActivity.this, searchArrayList);

                                inputName.setAdapter(searchnameadapter);
                                // inputName.setDropDownBackgroundResource(R.drawable.search_background);
                                inputName.setThreshold(1);

                                searchcontactadapter = new SearchContactAdapter(AddStaffAttendanceActivity.this, searchArrayList);

                                inputContact.setAdapter(searchcontactadapter);
                                // textContact.setDropDownBackgroundResource(R.drawable.search_background);
                                inputContact.setThreshold(1);

                                searchstaffidtadapter = new SearchStaffIdAdaper(AddStaffAttendanceActivity.this, searchArrayList);

                                inputStaffId.setAdapter(searchstaffidtadapter);
                                // textContact.setDropDownBackgroundResource(R.drawable.search_background);
                                inputStaffId.setThreshold(1);

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
    private void makeattendanceclass() {
        AddStaffAttendanceActivity.MakeAttendanceTrackclass ru = new AddStaffAttendanceActivity.MakeAttendanceTrackclass();
        ru.execute("5");
    }
    class MakeAttendanceTrackclass extends AsyncTask<String, Void, String> {

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
            // dismissProgressDialog();
            viewDialog.hideDialog();
            //Toast.makeText(CandiateListView.this, response, Toast.LENGTH_LONG).show();
            //  Toast.makeText(NewCustomerActivity.this, response, Toast.LENGTH_LONG).show();
            MakeAttendanceDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> MakeAttendanceDetails = new HashMap<String, String>();
            MakeAttendanceDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(AddStaffAttendanceActivity.this));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId(AddStaffAttendanceActivity.this)));
            MakeAttendanceDetails.put("staff_id",StaffId);
            Log.v(TAG, String.format("doInBackground :: staff_id = %s", StaffId));
            MakeAttendanceDetails.put("name",inputName.getText().toString());
            Log.v(TAG, String.format("doInBackground :: name = %s", inputName.getText().toString()));
            MakeAttendanceDetails.put("contact",inputContact.getText().toString());
            Log.v(TAG, String.format("doInBackground :: contact = %s", inputContact.getText().toString()));
            MakeAttendanceDetails.put("mode", "AdminApp");
            MakeAttendanceDetails.put("action", "mark_staff_attendance");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(AddStaffAttendanceActivity.this);
            String loginResult2 = ruc.sendPostRequest( domainurl+ServiceUrls.LOGIN_URL, MakeAttendanceDetails);

            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult2));
            return loginResult2;
        }
    }
    private void MakeAttendanceDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject jsonObjLoginResponse = null;
        try {
            jsonObjLoginResponse = new JSONObject(jsonResponse);
            String success = jsonObjLoginResponse.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                Toast.makeText(AddStaffAttendanceActivity.this,"Your Attendance Marked Successfully",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(AddStaffAttendanceActivity.this,StaffAttendanceActivity.class);
                startActivity(intent);
            }

            else if (success.equalsIgnoreCase(getResources().getString(R.string.one)))
            {
                Toast.makeText(AddStaffAttendanceActivity.this,"Please try after 1 hour ,Your attendance is already marked",Toast.LENGTH_SHORT).show();
                // inputContact.getText().clear();
                //Toast.makeText(AttendanceActivity.this,"Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(AddStaffAttendanceActivity.this, MainActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(AddStaffAttendanceActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
