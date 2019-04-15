package com.ndfitnessplus.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ndfitnessplus.Activity.Notification.TodaysEnquiryActivity;
import com.ndfitnessplus.Adapter.AdSliderAdapter;
import com.ndfitnessplus.CustomData.AdSliderData;
import com.ndfitnessplus.Main2Activity;
import com.ndfitnessplus.Model.AdSliderList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager viewPager;
    AdSliderAdapter adapter;
    private static ArrayList<AdSliderList> data;

    int currentPage=0;
    int NUM_PAGES=0;
    int MAX_STEP=5;
    Timer timer;
    //dashnoard menu
    LinearLayout notification,enquiry,add_member,renew,collections,expenses,member_info,quick_sms;
    TextView Username;
    String UName,companyname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        viewPager = (ViewPager) findViewById(R.id.viewpager);

        data = new ArrayList<AdSliderList>();
        for (int i = 0; i < AdSliderData.nameArray.length; i++) {
            data.add(new AdSliderList(
                    AdSliderData.nameArray[i],
                    AdSliderData.nameArray[i],
                    AdSliderData.drawableArray[i],
                    AdSliderData.id_[i]
            ));
        }

        adapter = new AdSliderAdapter(MainActivity.this,data);
        viewPager.setAdapter(adapter);
        setupAutoPager();

//intitlization
        notification=findViewById(R.id.notification);
        enquiry=findViewById(R.id.enquiry);
        add_member=findViewById(R.id.addmember);
        renew=findViewById(R.id.renew);
        collections=findViewById(R.id.collection);
        expenses=findViewById(R.id.expenses);
        member_info=findViewById(R.id.mem_info);
        quick_sms=findViewById(R.id.quicksms);

        Username=findViewById(R.id.user_name);
        UName=SharedPrefereneceUtil.getName(MainActivity.this);
        Username.setText(UName);
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });
        enquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, EnquiryActivity.class);
                startActivity(intent);
            }
        });
        add_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, AddMemberActivity.class);
                startActivity(intent);
            }
        });
        expenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, ExpensesActivity.class);
                startActivity(intent);
            }
        });
        renew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
            }
        });
        collections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, NewAddEnquiryActivity.class);
                startActivity(intent);
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.wel_user);

        TextView compname=(TextView)headerView.findViewById(R.id.company_name);
        String name=UName;
        companyname=SharedPrefereneceUtil.getCompanyName(MainActivity.this);
        navUsername.setText(name);
        compname.setText(companyname);
        // get menu from navigationView
        Menu menu = navigationView.getMenu();

        // find MenuItem you want to change
        MenuItem selcteorg = menu.findItem(R.id.selected_org);

        // set new title to the MenuItem
        selcteorg.setTitle(companyname);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupAutoPager() {
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
                if(currentPage == MAX_STEP){
                    currentPage=0;
                }
            }
        };
        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(Update);
            }
        }, 500, 2500);
    }
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        } else {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Exit Application?");
            alertDialogBuilder
                    .setMessage("Click yes to exit!")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    moveTaskToBack(true);
                                    //android.os.Process.killProcess(android.os.Process.myPid());
                                    //System.exit(1);
                                }
                            })

                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            //  super.onBackPressed();
        }


        /*new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);*/


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_coll) {

        } else if (id == R.id.nav_todays_enq) {
            Intent intent=new Intent(MainActivity.this, TodaysEnquiryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_followup) {

        } else if (id == R.id.nav_todays_admi) {

        } else if (id == R.id.nav_switch_branch) {
            Intent intent=new Intent(MainActivity.this,BranchSelectionActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_cont_us) {

        }else if (id == R.id.nav_terms) {

        }else if (id == R.id.nav_logout) {
            SharedPrefereneceUtil.LogOut(MainActivity.this);
            Intent intent=new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
