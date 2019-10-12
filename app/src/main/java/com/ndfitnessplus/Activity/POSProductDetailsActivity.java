package com.ndfitnessplus.Activity;

import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ndfitnessplus.Activity.Notification.ActiveMemberActivity;
import com.ndfitnessplus.LocalDatabase.SQLiteDataBaseHelper;
import com.ndfitnessplus.Model.CourseList;
import com.ndfitnessplus.Model.POSItemList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.Utility;

import java.util.ArrayList;


public class POSProductDetailsActivity extends AppCompatActivity {
    public static String TAG = POSProductDetailsActivity.class.getName();
    ImageView prodImage;
    TextView prodName,prodCode,Price,description,quantity;
    POSItemList filterArrayList;
    FloatingActionButton add,sub;
    AppCompatButton addToCart;
    SQLiteDataBaseHelper db;
    //Cart Count
    ArrayList<POSItemList> subArrayList = new ArrayList<POSItemList>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_posproduct_details);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.pos));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){

        prodImage=findViewById(R.id.image);
        prodName=findViewById(R.id.prod_name);
        prodCode=findViewById(R.id.prodcodeTV);
        Price=findViewById(R.id.price);
        description=findViewById(R.id.disc);
        addToCart=findViewById(R.id.bt_add_to_cart);


        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        if (args != null) {
            filterArrayList = (POSItemList) args.getSerializable("filter_array_list");

           // String image=filterArrayList.getProductImage();

            prodName.setText(filterArrayList.getProductName());
            prodCode.setText(filterArrayList.getProductCode());
            description.setText(filterArrayList.getProductDisc());
            String pr="₹ "+filterArrayList.getRate();
            Price.setText(pr);
            String domainurl= SharedPrefereneceUtil.getDomainUrl(POSProductDetailsActivity.this);
            String url= domainurl+ ServiceUrls.IMAGES_URL + filterArrayList.getProductImage();

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.nouser);
            requestOptions.error(R.drawable.nouser);


            Glide.with(this)
                    .setDefaultRequestOptions(requestOptions)
                    .load(url).into(prodImage);

            if (filterArrayList.addtocart) {
                addToCart.setText("Added");
            }else{
                addToCart.setText("Add to Cart");
            }
          //  Glide.with(this).load(url).placeholder(R.drawable.nouser).into(prodImage);
        }
      final  String compid=SharedPrefereneceUtil.getSelectedBranchId(POSProductDetailsActivity.this);
              db=new SQLiteDataBaseHelper(POSProductDetailsActivity.this);
        subArrayList=db.getAllCartProduct();
        if (!filterArrayList.isAddtocart()) {
            addToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filterArrayList.setAddtocart(true);
                    filterArrayList.addtocart=true;
                    long id = db.insertCartes(filterArrayList.getAutoId(), filterArrayList.getProductCode(), filterArrayList.getProductName(), filterArrayList.getProductDisc(),
                            filterArrayList.getProductImage(), compid, filterArrayList.getQuantity(), "1", filterArrayList.getRate(), filterArrayList.getTax(), filterArrayList.getMaxDiscount(), filterArrayList.getPurchaseAmount());

                    if (id > -1) {
                        String userId = String.valueOf(id);
                        addToCart.setText("Added");
                        Log.v(TAG, String.format("loginServerResponse :: product id = %s", userId));
                        //Toast.makeText(POSProductDetailsActivity.this,"Product Added Into Cart Successfully",Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(POSProductDetailsActivity.this, POSProductDetailsActivity.class);
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("filter_array_list", filterArrayList);
//                        intent.putExtra("BUNDLE", bundle);
//                        startActivity(intent);
                        Snackbar.make(v, "Product Added Into Cart Successfully", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pos_home, menu);
        MenuItem itemCart = menu.findItem(R.id.action_cart);
        LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
        Utility.setBadgeCount(this, icon, String.valueOf(subArrayList.size()));
        invalidateOptionsMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_home) {
            Intent intent = new Intent(POSProductDetailsActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_pos_show_item) {
            Intent intent = new Intent(POSProductDetailsActivity.this, POSShowItemActivity.class);
            startActivity(intent);
            return true;
        }else if (id == R.id.action_cart) {
            Intent intent = new Intent(POSProductDetailsActivity.this, CartActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent=new Intent(POSProductDetailsActivity.this,POSProductDetailsActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(POSProductDetailsActivity.this,POSProductListActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(POSProductDetailsActivity.this,POSProductListActivity.class);
        startActivity(intent);
    }
}
