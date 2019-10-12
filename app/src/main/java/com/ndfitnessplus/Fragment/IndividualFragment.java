package com.ndfitnessplus.Fragment;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ndfitnessplus.Activity.AddEnquiryActivity;
import com.ndfitnessplus.Activity.POSActivity;
import com.ndfitnessplus.Adapter.SearchContactAdapter;
import com.ndfitnessplus.Adapter.SearchNameAdapter;
import com.ndfitnessplus.Model.Search_list;
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
public class IndividualFragment extends Fragment {

    Search_list searchModel;
    ArrayList<Search_list> searchArrayList = new ArrayList<Search_list>();
    public SearchNameAdapter searchnameadapter;
    SearchContactAdapter searchcontactadapter;
    AutoCompleteTextView textName,textContact;
    String member_id;
    Button Send;
    EditText Message;
    public IndividualFragment() {
        // Required empty public constructor
    }

    public static IndividualFragment newInstance() {
        IndividualFragment fragment = new IndividualFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
     View rootview= inflater.inflate(R.layout.fragment_individual, container, false);
        textName=(AutoCompleteTextView)rootview.findViewById(R.id.search_name);
        textContact=(AutoCompleteTextView)rootview.findViewById(R.id.search_contact);

           Send=(Button)rootview.findViewById(R.id.send);
           Message=(EditText)rootview.findViewById(R.id.message) ;

        textName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                // String selection = (String)parent.getItemAtPosition(position);
                // Toast.makeText(MainNavigationActivity.this,"this is autocomplete suggestions"+selection,Toast.LENGTH_SHORT).show();
                String countryName = searchnameadapter.getItem(position).getCustName();
                String contact = searchnameadapter.getItem(position).getCustContact();
                member_id = searchnameadapter.getItem(position).getMemberId();
                textName.setText(countryName);
                textContact.setText(contact);

            }
        });
        textName.addTextChangedListener(new TextWatcher() {
//
          public void onTextChanged(CharSequence s, int start, int before,
                                    int count) {
          }



          public void beforeTextChanged(CharSequence s, int start, int count,
                                        int after) {

          }

          public void afterTextChanged(Editable s) {

          }
      });

        textContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // String selection = (String)parent.getItemAtPosition(position);
                // Toast.makeText(MainNavigationActivity.this,"this is autocomplete suggestions"+selection,Toast.LENGTH_SHORT).show();
                String countryName = searchcontactadapter.getItem(position).getCustName();
                String contact = searchcontactadapter.getItem(position).getCustContact();
                member_id = searchcontactadapter.getItem(position).getMemberId();
                textName.setText(countryName);
                textContact.setText(contact);


            }
        });
        textContact.addTextChangedListener(new TextWatcher() {
            //
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }



            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {
                if(textContact.getText().length()==0){
                    textName.setText("");
                }
            }
        });
        showSearchListClass();


        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendClass();
            }
        });
     return rootview;
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
            Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> SearchDetails = new HashMap<String, String>();

            SearchDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(getActivity()) );
            SearchDetails.put("action", "show_all_member_list");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(getActivity());
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

                                //  String email = jsonObj.getString("email");
                                // String phn_no = jsonObj.getString("mobile");


                                searchModel.setCustName(Name);
                                searchModel.setCustContact(Contact);
                                searchModel.setMemberId(MemberID);

                                searchArrayList.add(searchModel);
                                searchnameadapter = new SearchNameAdapter(getActivity(), searchArrayList);

                                textName.setAdapter(searchnameadapter);
                               // textName.setDropDownBackgroundResource(R.drawable.search_background);
                                textName.setThreshold(1);

                                searchcontactadapter = new SearchContactAdapter(getActivity(), searchArrayList);

                                textContact.setAdapter(searchcontactadapter);
                               // textContact.setDropDownBackgroundResource(R.drawable.search_background);
                                textContact.setThreshold(1);

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
            SendNotiDetails.put("title", "Gymtime Message" );
            SendNotiDetails.put("message", Message.getText().toString() );
            SendNotiDetails.put("member_id", member_id );
            Log.v(TAG, String.format("doInBackground :: member_id= %s", member_id));
            SendNotiDetails.put("user", "Member" );
            SendNotiDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(getActivity()) );
            SendNotiDetails.put("action", "send_individual_push_notification");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(getActivity());
            //EmployeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(Employee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ServiceUrls.LOGIN_URL, SendNotiDetails);
            Log.v(TAG, String.format("doInBackground :: send_push_notification_single= %s", loginResult));
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
                textName.getText().clear();
                textContact.getText().clear();
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
