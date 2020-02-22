package com.ndfitnessplus.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ndfitnessplus.Adapter.CartAdapter;
import com.ndfitnessplus.Adapter.POSSellAdapter;
import com.ndfitnessplus.Listeners.RecyclerItemTouchHelper;
import com.ndfitnessplus.LocalDatabase.SQLiteDataBaseHelper;
import com.ndfitnessplus.Model.POSItemList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.CustomItemClickListener;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.ViewDialog;

import java.util.ArrayList;
import java.util.HashMap;

import static com.ndfitnessplus.Activity.POSProductListActivity.TAG;
import static com.ndfitnessplus.Utility.HTTPRequestQueue.isOnline;

public class CartActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    CartAdapter adapter;

    POSItemList subList;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    ProgressBar progressBar;

    View nodata,frame;
    //No internet connectioin
    View noInternet;
    private ProgressBar progress_bar;
    private LinearLayout lyt_no_connection;

    public static String TAG = CartActivity.class.getName();
    private ProgressDialog pd;

    TextView TotalTv;
    //Loading gif
    ViewDialog viewDialog;
    ArrayList<POSItemList> cartarrayList=new ArrayList<POSItemList>();
    SQLiteDataBaseHelper db;
    ArrayList<POSItemList> subArrayList = new ArrayList<POSItemList>();
    LinearLayout linearLayout1;
    Button Checkout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_cart);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.cart));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){

        progressBar=findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(CartActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        viewDialog = new ViewDialog(CartActivity.this);

        nodata=findViewById(R.id.no_item);
        noInternet=findViewById(R.id.no_internet);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        lyt_no_connection = (LinearLayout) findViewById(R.id.lyt_no_connection);
         linearLayout1 =(LinearLayout)findViewById(R.id.linearlayout) ;
        Checkout=findViewById(R.id.btn_checkout);

        nodata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CartActivity.this,POSProductListActivity.class);
                startActivity(intent);
            }
        });
        TotalTv=findViewById(R.id.totalTV);
        db=new SQLiteDataBaseHelper(CartActivity.this);
        //progress_bar.setVisibility(View.GONE);
        lyt_no_connection.setVisibility(View.VISIBLE);

        if (isOnline(CartActivity.this)) {
            // check login details are valid or not from server

            subArrayList=db.getAllCartProduct();

            if(subArrayList.size()>0) {

                adapter = new CartAdapter(subArrayList, this, new CustomItemClickListener() {
                    @Override
                    public void onSpinnerQty(View v, int position) {

                        subArrayList = adapter.selctedProduct();
                        TotalCalculation();
                    }

                });

                progressBar.setVisibility(View.GONE);
                recyclerView.setAdapter(adapter);
                subArrayList = adapter.selctedProduct();
            }else{
                nodata.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }

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
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
                            overridePendingTransition(0, 0);
                            moveTaskToBack(false);

                        }
                    }, 1000);
                }
            });

        }
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback1 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Row is swiped from recycler view
                // remove it from adapter
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        // attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(recyclerView);
        Checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(subArrayList.size()>0) {
                    Intent intent = new Intent(CartActivity.this, POSAddBillingActivity.class);
                    intent.putExtra("total", TotalTv.getText().toString());
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("filter_array_list", subArrayList);
                    intent.putExtra("BUNDLE", bundle);
                    startActivity(intent);
                }
            }
        });


    }

    public void TotalCalculation(){
        double total=0;
        for (int i =0;i<subArrayList.size(); i++) {
            final POSItemList data = subArrayList.get(i);

            String quantity=data.getQuantity();
            String totl=data.getRate();
            String sp[]=totl.split(" ");
            if(!totl.equals("null")){
                if(!(quantity.equals("null")|| quantity.equals(""))){
                    double quantityst=Double.parseDouble(quantity);
                    double totaal=Double.parseDouble(sp[1]);
                    double chetotal=totaal*quantityst;
                    String ttl="₹ "+chetotal;

                    total +=chetotal;

                }
            }

        }
        String totlaprice="₹ "+String.valueOf(total);
        TotalTv.setText(totlaprice);
    }
    @Override
    public boolean onSupportNavigateUp(){
        Intent intent=new Intent(CartActivity.this,POSProductListActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(CartActivity.this,POSProductListActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction,final int position) {
        final POSItemList deletedItem = subArrayList.get(position);
        final int deletedIndex = viewHolder.getAdapterPosition();
                                if (viewHolder instanceof CartAdapter.ViewHolder) {
                                    // get the removed item name to display it in snack bar
                                    String name = deletedItem.getProductName();

                                    double total=0;
                                    String sp=TotalTv.getText().toString();
                                    String split[]=sp.split(" ");
                                    double totalprice=Double.parseDouble(split[1]);
                                    final  String ratesp[]=subArrayList.get(deletedIndex).getRate().split(" ");
                                    final  String quantitysp=subArrayList.get(deletedIndex).getQuantity();
                                    double rate=Double.parseDouble(ratesp[1]);
                                    double quantity=Double.parseDouble(quantitysp);
                                    total=totalprice-(rate*quantity);
                                    String totlaprice="₹ "+total;
                                    TotalTv.setText(totlaprice);
                                    // remove the item from recycler view
                                    db.remove_on_swipe(subArrayList.get(deletedIndex).getProductCode());
                                    adapter.removeItem(position);



                                    // showing snack bar with Undo option
                                    final   String compid= SharedPrefereneceUtil.getSelectedBranchId(CartActivity.this);
                                    Snackbar snackbar = Snackbar
                                            .make(linearLayout1, name + " removed from cart!", Snackbar.LENGTH_LONG);

                                    snackbar.setActionTextColor(Color.YELLOW);
                                    snackbar.show();

                                    snackbar.addCallback(new Snackbar.Callback() {

                                        @Override
                                        public void onDismissed(Snackbar snackbar, int event) {

                                        }

                                        @Override
                                        public void onShown(Snackbar snackbar) {

                                        }
                                    });

                                }


    }
}
