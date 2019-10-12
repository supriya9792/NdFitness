package com.ndfitnessplus.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

                Log.d(TAG, "User List: " + subArrayList);
                adapter = new CartAdapter(subArrayList, this, new CustomItemClickListener() {
                    @Override
                    public void onSpinnerQty(View v, int position) {

                        subArrayList = adapter.selctedProduct();
                        TotalCalculation();
//                    for (POSItemList data : subArrayList) {
//                       final POSItemList data = cartarrayList.get(position);
//                        String quantity = data.getQuantity();
//                        String totl = data.getRate();
//                        String sp[] = totl.split(" ");
//                        if (!totl.equals("null")) {
//                            if (!quantity.equals("null")) {
//                                double quantityst = Double.parseDouble(quantity);
//                                double totaal = Double.parseDouble(sp[1]);
//                                double chetotal = totaal * quantityst;
//                                String prate="₹ "+chetotal;
//                                data.setProductFinalRate(prate);
//                                //cartarrayList.add(data);
//
////                                double chttl = 0;
////                                if (quantityst == 1) {
////                                    chttl = chetotal - 0;
////
////                                } else {
////                                    chttl = chetotal - totaal;
////                                }
////
////                                total += chttl;
//                            }
//                        }
                        //Log.v(TAG, String.format(" :: total adapter = %s", totl));
                        //Log.v(TAG, String.format(" :: Total = %s", total));
                        //Log.v(TAG, String.format(" :: selected prod id *********************** %s",));
//                    }

//                        calculate(position);
                    }

                });

                progressBar.setVisibility(View.GONE);
                recyclerView.setAdapter(adapter);
                subArrayList = adapter.selctedProduct();
            }else{
                nodata.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }

//
//            String totlaprice="₹ "+String.valueOf(total);
//            TotalTv.setText(totlaprice);
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
            Log.d(TAG, "Size: " + subArrayList.size());
            final POSItemList data = subArrayList.get(i);
//            subList =new POSItemList();
//            subList.setProductCode(data.getProductCode());
//            subList.setProductName(data.getProductName());
//            subList.setQuantity(data.getQuantity());
//            subList.setRate(data.getRate());
//            subList.setProductImage(data.getProductImage());
            String quantity=data.getQuantity();
            String totl=data.getRate();
            String sp[]=totl.split(" ");
            if(!totl.equals("null")){
                if(!(quantity.equals("null")|| quantity.equals(""))){
                    double quantityst=Double.parseDouble(quantity);
                    double totaal=Double.parseDouble(sp[1]);
                    double chetotal=totaal*quantityst;
                    String ttl="₹ "+chetotal;
//                    subList.setProductFinalRate(ttl);
                    total +=chetotal;

                }
            }
            //subArrayList.add(subList);

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
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction,int position) {
        if (viewHolder instanceof CartAdapter.ViewHolder) {
            // get the removed item name to display it in snack bar
            String name = subArrayList.get(position).getProductName();

            // backup of removed item for undo purpose
            final POSItemList deletedItem = subArrayList.get(position);
            final int deletedIndex = position;


          final  POSItemList data = subArrayList.get(position);

            double total=0;
            String sp=TotalTv.getText().toString();
            String split[]=sp.split(" ");
            double totalprice=Double.parseDouble(split[1]);
            final  String ratesp[]=subArrayList.get(position).getRate().split(" ");
            final  String quantitysp=subArrayList.get(position).getQuantity();
            double rate=Double.parseDouble(ratesp[1]);
            double quantity=Double.parseDouble(quantitysp);
            total=totalprice-(rate*quantity);
            String totlaprice="₹ "+total;
            TotalTv.setText(totlaprice);
            // remove the item from recycler view
            db.remove_on_swipe(subArrayList.get(position).getProductCode());
            adapter.removeItem(position);
            //adapter.notifyDataSetChanged();


            // showing snack bar with Undo option
          final   String compid= SharedPrefereneceUtil.getSelectedBranchId(this);
            Snackbar snackbar = Snackbar
                    .make(linearLayout1, name + " removed from cart!", Snackbar.LENGTH_LONG);
//            snackbar.setAction("UNDO", new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    // undo is selected, restore the deleted item
//                    adapter.restoreItem(deletedItem, deletedIndex);
//
//                    //Log.v(TAG, String.format(" :: product rate = %s", subArrayList.get(position).getRate()));
//                   // String ratesp[]=subArrayList.get(position).getRate().split(" ");
//                    long returnid=  db.insertCartes(data.getAutoId(),data.getProductCode(),data.getProductName(),data.getProductDisc(),
//                            data.getProductImage(),compid,data.getQuantity(),data.getSelectedQuantity(),ratesp[1],data.getTax(),data.getMaxDiscount(),data.getPurchaseAmount());
//
//                    if(returnid > 0){
////                        double total=0;
////                        String sp=TotalTv.getText().toString();
////                        String split[]=sp.split(" ");
////                        double totalprice=Double.parseDouble(split[1]);
////                       // String ratesp[]=subArrayList.get(position).getRate().split(" ");
////                        double rate=Double.parseDouble(ratesp[1]);
////                        total=totalprice+rate;
////                        String ttlprice="₹ "+total;
////                        TotalTv.setText(ttlprice);
//                        String  userId = String.valueOf(returnid);
//                        Log.v(TAG, String.format(" :: product id = %s", userId));
//                        Snackbar.make(view, "Product Added Into Cart Successfully", Snackbar.LENGTH_SHORT).show();
//                    }
//
//                }
//            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

            snackbar.addCallback(new Snackbar.Callback() {

                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    //see Snackbar.Callback docs for event details

                }

                @Override
                public void onShown(Snackbar snackbar) {

                }
            });
//            overridePendingTransition( 0, 0);
//            startActivity(getIntent());
        }
    }
}
