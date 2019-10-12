package com.ndfitnessplus.Fragment;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ndfitnessplus.Activity.CourseFilterActivity;
import com.ndfitnessplus.Adapter.SpinnerAdapter;
import com.ndfitnessplus.Model.Spinner_List;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.ndfitnessplus.Activity.SelectDomainActivity.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class PackageWiseFragment extends Fragment {
    public final String TAG = PackageWiseFragment.class.getName();
    String member_id;
    Button Send;
    EditText Message;
    //Spinner Adapter
    public Spinner spinPackageName,spinPackageType,spinStatus;
    Spinner_List PackageNameList,PackageTypeList,statusList;

    ArrayList<Spinner_List> packageTypeArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> packagenameArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> StatusArrayList = new ArrayList<Spinner_List>();

    public SpinnerAdapter pack_nameadapter,pack_typeadapter,statusAdapter;
    String packageName,status;
    String packageType="All";

    public PackageWiseFragment() {
        // Required empty public constructor
    }
    public static PackageWiseFragment newInstance() {
        PackageWiseFragment fragment = new PackageWiseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview= inflater.inflate(R.layout.fragment_package_wise, container, false);
        Send=(Button)rootview.findViewById(R.id.send);
        Message=(EditText)rootview.findViewById(R.id.message) ;

        spinPackageName = (Spinner)rootview.findViewById(R.id.spinner_package_name);
        spinPackageType = (Spinner)rootview.findViewById(R.id.spinner_package_type);
        spinStatus = (Spinner)rootview. findViewById(R.id.spinner_status);
        final  String[] memberstatusArray = getResources().getStringArray(R.array.status_array);

        for(int i=0;i<4;i++) {
            statusList = new Spinner_List();
            statusList.setName(memberstatusArray[i]);
            StatusArrayList.add(statusList);
            statusAdapter = new SpinnerAdapter(getActivity(), StatusArrayList) {
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
                        tv.setText(getResources().getString(R.string.prompt_status));
                        // tv.setTextColor(Color.GRAY);
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }

            };
            spinStatus.setAdapter(statusAdapter);
        }
        //Toast.makeText(MainActivity.this,genderradioButton.getText(), Toast.LENGTH_SHORT).show();
        spinStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);
                    if (index == 0) {
                        tv.setText(getResources().getString(R.string.prompt_status));
                    }
//                tv.setTextColor(getResources().getColor(R.color.black));
                    status = tv.getText().toString();
                    if ((status.equals(getResources().getString(R.string.prompt_status))) ||
                            (status.equals(getResources().getString(R.string.all)))) {
                        status = "";
                    }
                    // ((TextView) spinEnquiryType.getSelectedView()).setTextColor(getResources().getColor(R.color.white));
                    // Showing selected spinner item
                    //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        packageTypeClass();
        packageNameClass();
        spinPackageType.setSelection(1);
        spinPackageType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if(view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);
                    if(index==0){
                        tv.setText(getResources().getString(R.string.hint_packagetype));
                    }
                    packageType = tv.getText().toString();
                    packageNameClass();
                    if ((packageType.equals(getResources().getString(R.string.hint_packagetype))) ||
                            (packageType.equals(getResources().getString(R.string.all)))) {
                        packageType = "";
                    }
                }
                // ((TextView) spinPackageType.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
                // Showing selected spinner item
                //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(parent.getContext(), "Please Select Enquiry Type ", Toast.LENGTH_LONG).show();
            }
        });
        // *********************** Package Name **********************
        spinPackageName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if(view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);

                    if(index==0){
                        tv.setText(getResources().getString(R.string.hint_package_name));
                    }
                    packageName = tv.getText().toString();
                    if ((packageName.equals(getResources().getString(R.string.hint_package_name))) ||
                            (packageName.equals(getResources().getString(R.string.all)))) {
                        packageName = "";
                    }
                }
                // ((TextView) spinPackageType.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
                // Showing selected spinner item
                //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(parent.getContext(), "Please Select Enquiry Type ", Toast.LENGTH_LONG).show();
            }
        });
        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendClass();
            }
        });
        return  rootview;
    }
    // ************* Package Type spinner *******************
    public void  packageTypeClass() {
        PackageTypeTrackClass ru = new PackageTypeTrackClass();
        ru.execute("5");
    }
    class PackageTypeTrackClass extends AsyncTask<String, Void, String> {

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
            PackageTypeDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> PackageTypeDetails = new HashMap<String, String>();
            PackageTypeDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(getActivity()));
            PackageTypeDetails.put("action", "show_package_type");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(getActivity());
            //PackageTypeloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(PackageTypeloyee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, PackageTypeDetails);
            Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }


    }


    private void PackageTypeDetails(String jsonResponse) {


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
                        packageTypeArrayList.clear();
                        PackageTypeList = new Spinner_List();
                        PackageTypeList.setName(getResources().getString(R.string.hint_packagetype));
                        packageTypeArrayList.add(0,PackageTypeList);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            PackageTypeList.setName(getResources().getString(R.string.all));

                            packageTypeArrayList.add(1,PackageTypeList);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                PackageTypeList = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String PackageType     = jsonObj.getString("PackageType");

//                               if(i==0){
//                                   PackageTypeList.setName(getResources().getString(R.string.promt_country));
//                                   enqF.add(0,PackageTypeList);
//                               }
                                    PackageTypeList.setName(PackageType);

                                    packageTypeArrayList.add(PackageTypeList);

                                    pack_typeadapter = new SpinnerAdapter(getActivity(), packageTypeArrayList){
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
                                                tv.setText(getResources().getString(R.string.prompt_packagetype));
                                                // tv.setTextColor(Color.GRAY);
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinPackageType.setAdapter(pack_typeadapter);


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
    // ************* Package Name spinner *******************
    public void  packageNameClass() {
        PackageNameTrackClass ru = new PackageNameTrackClass();
        ru.execute("5");
    }
    class PackageNameTrackClass extends AsyncTask<String, Void, String> {

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
            PackageNameDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> PackageNameDetails = new HashMap<String, String>();
            PackageNameDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(getActivity()));
            PackageNameDetails.put("pack_type", packageType);
            PackageNameDetails.put("action", "show_packages_details");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(getActivity());
            //PackageNameloyeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(PackageNameloyee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, PackageNameDetails);
            Log.v(TAG, String.format("doInBackground :: show_packages_details= %s", loginResult));
            return loginResult;
        }


    }


    private void PackageNameDetails(String jsonResponse) {


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
                        packagenameArrayList.clear();
                        PackageNameList = new Spinner_List();
                        PackageNameList.setName(getResources().getString(R.string.hint_package_name));
                        packagenameArrayList.add(0,PackageNameList);
                        if (jsonArrayCountry != null && jsonArrayCountry.length() > 0){
                            PackageNameList.setName(getResources().getString(R.string.all));

                            packagenameArrayList.add(1,PackageNameList);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                PackageNameList = new Spinner_List();
                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayCountry.getJSONObject(i);
                                if (jsonObj != null) {

                                    String PackageName     = jsonObj.getString("PackageName");



                                    PackageNameList.setName(PackageName);

                                    packagenameArrayList.add(PackageNameList);

                                    pack_nameadapter = new SpinnerAdapter(getActivity(), packagenameArrayList){
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
                                                tv.setText(getResources().getString(R.string.prompt_package_name));
                                                // tv.setTextColor(Color.GRAY);
                                            }
                                            else {
                                                tv.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }

                                    };
                                    spinPackageName.setAdapter(pack_nameadapter);


                                }
                            }
                        }else if(jsonArrayCountry.length()==0){
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    packagenameArrayList.clear();
                    PackageNameList = new Spinner_List();
                    PackageNameList.setName(getResources().getString(R.string.all));
                    packagenameArrayList.add(0,PackageNameList);
                    PackageNameList.setName(getResources().getString(R.string.all));

                    packagenameArrayList.add(1,PackageNameList);
                    pack_nameadapter = new SpinnerAdapter(getActivity(), packagenameArrayList){
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
                                tv.setText(getResources().getString(R.string.prompt_package_name));
                                // tv.setTextColor(Color.GRAY);
                            }
                            else {
                                tv.setTextColor(Color.BLACK);
                            }
                            return view;
                        }

                    };
                    spinPackageName.setAdapter(pack_nameadapter);
                    //forumCount.setVisibility(View.INVISBLE);
                    // queCount.setVisibility(View.INVISIBLE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
            }
        }
    }
    public void  sendClass() {
        SendNotiTrackClass ru = new SendNotiTrackClass();
        ru.execute("5");
    }
    private   class SendNotiTrackClass extends AsyncTask<String, Void, String> {

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
            SendNotiDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> SendNotiDetails = new HashMap<String, String>();
            SendNotiDetails.put("status", status);
            SendNotiDetails.put("message", Message.getText().toString() );
            SendNotiDetails.put("package_type", packageType);
            SendNotiDetails.put("package_name", packageName);
            Log.v(TAG, String.format("doInBackground :: package_name= %s", packageName));
            SendNotiDetails.put("user", "Member" );
            SendNotiDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(getActivity()) );
            SendNotiDetails.put("action", "send_package_wise_push_notification");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(getActivity());
            //EmployeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(Employee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, SendNotiDetails);
            Log.v(TAG, String.format("doInBackground :: send_package_wise_push_notification= %s", loginResult));
            return loginResult;
        }

    }


    private void SendNotiDetails(String jsonResponse) {

        Log.v(TAG, String.format("loginServerResponse :: response = %s", jsonResponse));

        JSONObject jsonObjLoginResponse = null;
        try {
            jsonObjLoginResponse = new JSONObject(jsonResponse);
            String success = jsonObjLoginResponse.getString(getResources().getString(R.string.success));

            if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
                Toast.makeText(getActivity(),"Message send succesfully",Toast.LENGTH_SHORT).show();
                Message.getText().clear();
                spinPackageName.setSelection(0);
                spinPackageType.setSelection(0);
                spinStatus.setSelection(0);
                // showCustomDialog();

                //inputEmail, inputPhone,inputAdd,inputReq,inputFollowupdate;
            }


            else if (success.equalsIgnoreCase(getResources().getString(R.string.one)))
            {

                // Toast.makeText(AddEnquiryActivity.this,"Please Enter New Mobile Number",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
