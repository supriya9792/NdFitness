package com.ndfitnessplus.Fragment;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ndfitnessplus.Adapter.SpinnerAdapter;
import com.ndfitnessplus.Model.Spinner_List;
import com.ndfitnessplus.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class EnquiryFragment extends Fragment {

    String member_id;
    Button Send;
    EditText Message;
    //Spinner Adapter
    public Spinner spinStatus;
    Spinner_List statusList;

    ArrayList<Spinner_List> StatusArrayList = new ArrayList<Spinner_List>();

    public SpinnerAdapter statusAdapter;
    String status;
    public EnquiryFragment() {
        // Required empty public constructor
    }
    public static EnquiryFragment newInstance() {
        EnquiryFragment fragment = new EnquiryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview= inflater.inflate(R.layout.fragment_enquiry, container, false);
        spinStatus = (Spinner)rootview. findViewById(R.id.spinner_status);

        Send=(Button)rootview.findViewById(R.id.send);
        Message=(EditText)rootview.findViewById(R.id.message) ;
        // *********** status spinner
        final  String[] statusArray = getResources().getStringArray(R.array.rating_array);

        for(int i=0;i<6;i++) {
            statusList = new Spinner_List();
            statusList.setName(statusArray[i]);
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
        spinStatus.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm=(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
               // imm.hideSoftInputFromWindow(inputAdd.getWindowToken(), 0);
                return false;
            }
        }) ;
        return rootview;
    }

}
