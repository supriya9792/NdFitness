package com.ndfitnessplus.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ndfitnessplus.Activity.AttendenceActivity;
import com.ndfitnessplus.Adapter.AttendanceAdapter;
import com.ndfitnessplus.Adapter.POSItemAdapter;
import com.ndfitnessplus.Model.AttendanceList;
import com.ndfitnessplus.Model.POSItemList;
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
import java.util.Date;
import java.util.HashMap;

import static com.ndfitnessplus.Utility.HTTPRequestQueue.isOnline;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ShowItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowItemFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    POSItemAdapter adapter;

    POSItemList subList;
    SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    ProgressBar progressBar;

    View nodata,frame;
    //No internet connectioin
    View noInternet;
    private ProgressBar progress_bar;
    private LinearLayout lyt_no_connection;

    public static String TAG = ShowItemFragment.class.getName();
    private ProgressDialog pd;

    //paginnation parameters
    public static final int PAGE_START = 1;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 2;
    private boolean isLoading = false;
    int itemCount = 0;
    int offset = 0;

    //search
    private EditText inputsearch;
    ImageView search;
    TextView total_present;
    //Loading gif
    ViewDialog viewDialog;

    public ShowItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ShowItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShowItemFragment newInstance() {
        ShowItemFragment fragment = new ShowItemFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_show_item, container, false);
        progressBar=view.findViewById(R.id.progressBar);
        swipeRefresh=view.findViewById(R.id.swipeRefresh);
        recyclerView = (RecyclerView)view. findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        viewDialog = new ViewDialog(getActivity());


        nodata=view.findViewById(R.id.nodata);
        frame=view.findViewById(R.id.main_frame);
        noInternet=view.findViewById(R.id.no_internet);
        progress_bar = (ProgressBar)view. findViewById(R.id.progress_bar);
        lyt_no_connection = (LinearLayout) view.findViewById(R.id.lyt_no_connection);

        progress_bar.setVisibility(View.GONE);
        lyt_no_connection.setVisibility(View.VISIBLE);

        if (isOnline(getActivity())) {
            POSItemclass();// check login details are valid or not from server
        }
        else {
            frame.setVisibility(View.GONE);
            noInternet.setVisibility(View.VISIBLE);
            lyt_no_connection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    progress_bar.setVisibility(View.VISIBLE);
                    lyt_no_connection.setVisibility(View.GONE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progress_bar.setVisibility(View.GONE);
                            lyt_no_connection.setVisibility(View.VISIBLE);
                            getActivity().finish();
                            getActivity().overridePendingTransition(0, 0);
                            getActivity().startActivity(getActivity().getIntent());
                            getActivity().overridePendingTransition(0, 0);
                           getActivity(). moveTaskToBack(false);

                        }
                    }, 1000);
                }
            });

        }
        inputsearch=(EditText)view.findViewById(R.id.inputsearchid);
        search=view.findViewById(R.id.search);



        inputsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(final Editable arg0) {
                // TODO Auto-generated method stub
                if (adapter == null){
                    // some print statement saying it is null
//                   // Toast toast = Toast.makeText(AttendenceActivity.this,"no record found", Toast.LENGTH_SHORT);
//                    toast.setGravity(Gravity.CENTER, 0, 0);
//                    toast.show();
                }
                else
                {
                    isLoading = false;
                    int count=adapter.filter(String.valueOf(arg0));

                  //  total_present.setText(String.valueOf(count));

                }
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
                if(inputsearch.getText().length()==0) {
                    //do your work here
                    // Toast.makeText(AddEnquiryActivity.this ,"Text vhanged count  is 10 then: " , Toast.LENGTH_LONG).show();
                    POSItemclass();
                }

            }
        });
        return  view;
    }

    // Asycc class for loading data for database
    private void POSItemclass() {
        POSItemTrackclass ru = new POSItemTrackclass();
        ru.execute("5");
    }
    class POSItemTrackclass extends AsyncTask<String, Void, String> {

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
            POSItemDetails(response);

        }

        @Override
        protected String doInBackground(String... params) {
            //Log.v(TAG, String.format("doInBackground ::  params= %s", params));
            HashMap<String, String> POSItemDetails = new HashMap<String, String>();
            POSItemDetails.put("comp_id", SharedPrefereneceUtil.getSelectedBranchId((Activity)getActivity()));
            Log.v(TAG, String.format("doInBackground :: company id = %s", SharedPrefereneceUtil.getSelectedBranchId((Activity)getActivity())));
            POSItemDetails.put("action","show_pos_items");
            String domainurl=SharedPrefereneceUtil.getDomainUrl((Activity)getActivity());
            String loginResult = ruc.sendPostRequest(domainurl+ ServiceUrls.LOGIN_URL, POSItemDetails);
            //Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
            return loginResult;
        }

    }

    private void POSItemDetails(String jsonResponse) {

        Log.v(TAG, String.format("JsonResponseOperation :: jsonResponse = %s", jsonResponse));
//        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayoutPrabhagDetails);
        if (jsonResponse != null) {


            try {
                Log.v(TAG, "JsonResponseOpeartion :: test");
                JSONObject object = new JSONObject(jsonResponse);
                String success = object.getString(getResources().getString(R.string.success));

                if (success.equalsIgnoreCase(getResources().getString(R.string.two))) {
//                    nodata.setVisibility(View.GONE);
//                    swipeRefresh.setVisibility(View.VISIBLE);
                    String ttl_enq = object.getString("total_product_count");
                    //total_present.setText(ttl_enq);
                    //progressBar.setVisibility(View.GONE);
                    if (object != null) {
                        JSONArray jsonArrayResult = object.getJSONArray("result");
//                        if(jsonArrayResult.length() >10){
//                            totalPage=jsonArrayResult.length()/10;
//                        }

                        ArrayList<POSItemList> item = new ArrayList<POSItemList>();
                        if (jsonArrayResult != null && jsonArrayResult.length() > 0) {

                            for (int i = 0; i < jsonArrayResult.length(); i++) {


                                subList = new POSItemList();
                                Log.d(TAG, "i: " + i);

                                Log.v(TAG, "JsonResponseOpeartion ::");
                                JSONObject jsonObj = jsonArrayResult.getJSONObject(i);
                                if (jsonObj != null) {

                                    String Product_Code = jsonObj.getString("Product_Code");
                                    String Product_Name = jsonObj.getString("Product_Name");
                                    String Product_Dsription = jsonObj.getString("Product_Dsription");
                                    String Product_Image = jsonObj.getString("Product_Image");
                                    String Quantity = jsonObj.getString("Quantity");
                                    String Rate = jsonObj.getString("Rate");
                                    String Tax = jsonObj.getString("Tax");
                                    String MaxDiscount = jsonObj.getString("MaxDiscount");
                                    String Purches_Amount = jsonObj.getString("Purches_Amount");


                                    subList.setProductCode(Product_Code);
                                    subList.setProductName(Product_Name);
                                    subList.setProductDisc(Product_Dsription);
                                    subList.setProductImage(Product_Image);
                                    subList.setQuantity(Quantity);
                                    subList.setRate(Rate);
                                    subList.setTax(Tax);
                                    subList.setMaxDiscount(MaxDiscount);
                                    subList.setPurchaseAmount(Purches_Amount);

                                    item.add(subList);
                                    //Toast.makeText(AttendenceActivity.this, "followup date: "+next_foll_date, Toast.LENGTH_SHORT).show();

                                    //Toast.makeText(MainActivity.this, "j "+j, Toast.LENGTH_SHORT).show();

                                    adapter = new POSItemAdapter( item,(Activity)getContext());
                                    recyclerView.setAdapter(adapter);

                                }
                            }
                        } else if (jsonArrayResult.length() == 0) {
                            System.out.println("No records found");
                        }
                    }
                }else if (success.equalsIgnoreCase(getResources().getString(R.string.zero))){
                    nodata.setVisibility(View.VISIBLE);
                    swipeRefresh.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v(TAG, "JsonResponseOpeartion :: catch");
                e.printStackTrace();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder((Activity)getActivity());
                builder.setMessage(R.string.server_exception);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                android.app.AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();
            }
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

}
