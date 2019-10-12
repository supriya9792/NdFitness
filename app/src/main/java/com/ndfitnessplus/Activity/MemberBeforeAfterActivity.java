package com.ndfitnessplus.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ndfitnessplus.Fragment.AfterFragment;
import com.ndfitnessplus.Fragment.BeforeFragment;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.Utility;
import com.ndfitnessplus.Utility.ViewDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemberBeforeAfterActivity extends AppCompatActivity {
    private TabLayout tab_layout;
    ViewDialog viewDialog;
    private ViewPager view_pager;
    private Uri mCropImageUri;
    String member_id,name,contact;
    String Image="";
    String Before_Photo="";
    String After_Photo="";
    TextView username,mobilenumber;
    CircularImageView imageView;
    SectionsPagerAdapter sectionsPagerAdapter;
    public static String TAG = MemberBeforeAfterActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_before_after);
        initToolbar();
        initComponent();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.member_before_after));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private  void initComponent(){
        Intent intent = getIntent();
        // Bundle args = intent.getBundleExtra("BUNDLE");
        if (intent != null) {
            member_id=intent.getStringExtra("member_id");
            name=intent.getStringExtra("name");
            contact=intent.getStringExtra("contact");
            Image=intent.getStringExtra("image");
            After_Photo=intent.getStringExtra("After_Photo");
            Before_Photo=intent.getStringExtra("Before_Photo");
           //memberdetailsclass();
        }
        username=findViewById(R.id.username);
        mobilenumber=findViewById(R.id.mobilenumber);
        imageView=findViewById(R.id.image);

        username.setText(name);
        mobilenumber.setText(contact);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.nouser);
        requestOptions.error(R.drawable.nouser);
        Image.replace("\"", "");
        String domainurl= SharedPrefereneceUtil.getDomainUrl(MemberBeforeAfterActivity.this);
        String url= domainurl+ ServiceUrls.IMAGES_URL + Image;
        Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(url).into(imageView);

        viewDialog = new ViewDialog(this);
        view_pager = (ViewPager)findViewById(R.id.view_pager);
        setupViewPager(view_pager);
        tab_layout = (TabLayout)findViewById(R.id.tab_layout);
        tab_layout.setupWithViewPager(view_pager);
    }
    private void setupViewPager(ViewPager paramViewPager) {
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        sectionsPagerAdapter.addFragment(BeforeFragment.newInstance(), getResources().getString(R.string.tab_before));
        sectionsPagerAdapter.addFragment(AfterFragment.newInstance(), getResources().getString(R.string.tab_after));
        paramViewPager.setAdapter(sectionsPagerAdapter);
    }
    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList();

        private final List<String> mFragmentTitleList = new ArrayList();

        public SectionsPagerAdapter(FragmentManager param1FragmentManager) { super(param1FragmentManager); }

        public void addFragment(Fragment param1Fragment, String param1String) {
            Bundle bundle = new Bundle();
            bundle.putString("member_id",member_id);
            bundle.putString("After_Photo",After_Photo);
            bundle.putString("Before_Photo",Before_Photo);
            param1Fragment.setArguments(bundle);
            this.mFragmentList.add(param1Fragment);
            this.mFragmentTitleList.add(param1String);
        }

        public int getCount() { return this.mFragmentList.size(); }

        public Fragment getItem(int param1Int) { return (Fragment)this.mFragmentList.get(param1Int); }

        public CharSequence getPageTitle(int param1Int) { return (CharSequence)this.mFragmentTitleList.get(param1Int); }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int tab_position=tab_layout.getSelectedTabPosition();
        if(tab_position == 0) {
            BeforeFragment fragment = (BeforeFragment) sectionsPagerAdapter.getItem(0);
            // Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_before);
            fragment.onActivityResult(requestCode, resultCode, data);
        }else if(tab_position == 1) {
            AfterFragment fragment1 = (AfterFragment) sectionsPagerAdapter.getItem(1);
            // Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_before);
            fragment1.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public boolean onSupportNavigateUp(){
       finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
