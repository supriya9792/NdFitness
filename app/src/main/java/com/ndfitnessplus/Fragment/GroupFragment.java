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

import com.ndfitnessplus.Activity.MemberFilterActivity;
import com.ndfitnessplus.Adapter.SpinnerAdapter;
import com.ndfitnessplus.Model.Spinner_List;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.ndfitnessplus.Activity.SelectDomainActivity.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {
    String member_id;
    Button Send;
    EditText Message;
    //Spinner Adapter
    public Spinner spinGender,spinStatus;
    Spinner_List GenderList,statusList;

    ArrayList<Spinner_List> GenderArrayList = new ArrayList<Spinner_List>();
    ArrayList<Spinner_List> StatusArrayList = new ArrayList<Spinner_List>();

    public SpinnerAdapter genderadapter,statusAdapter;
    String gender,status;

    public GroupFragment() {
        // Required empty public constructor
    }

    public static GroupFragment newInstance() {
        GroupFragment fragment = new GroupFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview= inflater.inflate(R.layout.fragment_group, container, false);
        spinGender = (Spinner)rootview.findViewById(R.id.spinner_enq_gender);
        spinStatus = (Spinner)rootview. findViewById(R.id.spinner_status);

        Send=(Button)rootview.findViewById(R.id.send);
        Message=(EditText)rootview.findViewById(R.id.message) ;
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
            genderadapter = new SpinnerAdapter(getActivity(), GenderArrayList) {
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
                        // tv.setTextColor(Color.GRAY);
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }

            };
            spinGender.setAdapter(genderadapter);
        }
        //Toast.makeText(MainActivity.this,genderradioButton.getText(), Toast.LENGTH_SHORT).show();
        spinGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                    tv.setTextSize(10);
                    if (index == 0) {
                        tv.setText(getResources().getString(R.string.prompt_gender));
                    }
//                tv.setTextColor(getResources().getColor(R.color.black));
                    gender = tv.getText().toString();
                    if ((gender.equals(getResources().getString(R.string.prompt_gender))) ||
                            (gender.equals(getResources().getString(R.string.all)))) {
                        gender = "";
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
        // *********** status spinner
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
        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendClass();
            }
        });
        return  rootview;
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
            SendNotiDetails.put("gender", gender);
            Log.v(TAG, String.format("doInBackground :: gender= %s", gender));
            SendNotiDetails.put("user", "Member" );
            SendNotiDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(getActivity()) );
            SendNotiDetails.put("action", "send_group_push_notification");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(getActivity());
            //EmployeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(Employee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, SendNotiDetails);
            Log.v(TAG, String.format("doInBackground :: send_group_push_notification= %s", loginResult));
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
               spinGender.setSelection(0);
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
