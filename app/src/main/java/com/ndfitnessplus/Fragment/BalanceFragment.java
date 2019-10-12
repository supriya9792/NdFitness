package com.ndfitnessplus.Fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class BalanceFragment extends Fragment {
    public final String TAG = PackageWiseFragment.class.getName();
    String member_id;
    Button Send;
    EditText Message;
    public BalanceFragment() {
        // Required empty public constructor
    }
    public static BalanceFragment newInstance() {
        BalanceFragment fragment = new BalanceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview= inflater.inflate(R.layout.fragment_balance, container, false);

        Send=(Button)rootview.findViewById(R.id.send);
        Message=(EditText)rootview.findViewById(R.id.message) ;
        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendClass();
            }
        });
        return rootview;
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
            SendNotiDetails.put("message", Message.getText().toString() );
            SendNotiDetails.put("user", "Member" );
            SendNotiDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId(getActivity()) );
            SendNotiDetails.put("action", "send_balance_push_notification");
            String domainurl=SharedPrefereneceUtil.getDomainUrl(getActivity());
            //EmployeeDetails.put("admin_id", SharedPrefereneceUtil.getadminId(Employee.this));
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, SendNotiDetails);
            Log.v(TAG, String.format("doInBackground :: send_balance_push_notification= %s", loginResult));
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
