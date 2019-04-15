package com.ndfitnessplus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;

import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration;
import com.ndfitnessplus.Activity.MainActivity;
import com.ndfitnessplus.Adapter.BottomMenuAdapter;
import com.ndfitnessplus.Adapter.TopMenuAdapter;
import com.ndfitnessplus.CustomData.BottomMenuData;
import com.ndfitnessplus.CustomData.TopMenuData;
import com.ndfitnessplus.Model.BottomMenuList;
import com.ndfitnessplus.Model.TopMenuList;

import java.util.ArrayList;

import static com.pchmn.materialchips.util.ViewUtil.dpToPx;

public class Main2Activity extends AppCompatActivity {
    private static RecyclerView.Adapter adapter;
    private static BottomMenuAdapter botadapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerViewTop,recyclerViewbot;
    private static ArrayList<TopMenuList> data;
    private static ArrayList<BottomMenuList> botdata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

         initToolbar();

    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.noti));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(Main2Activity.this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
