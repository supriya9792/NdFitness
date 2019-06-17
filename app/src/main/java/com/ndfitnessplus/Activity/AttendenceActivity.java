package com.ndfitnessplus.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ViewDialog;

public class AttendenceActivity extends AppCompatActivity {
    //Loading gif
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence);
    }
}
