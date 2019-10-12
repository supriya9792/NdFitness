package com.ndfitnessplus.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ndfitnessplus.Activity.AddEnquiryActivity;
import com.ndfitnessplus.Activity.CartActivity;

import com.ndfitnessplus.Activity.CourseActivity;
import com.ndfitnessplus.LocalDatabase.SQLiteDataBaseHelper;
import com.ndfitnessplus.Model.CourseList;
import com.ndfitnessplus.Model.POSItemList;
import com.ndfitnessplus.Model.Spinner_List;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.CustomItemClickListener;
import com.ndfitnessplus.Utility.ServerClass;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;
import com.ndfitnessplus.Utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ndfitnessplus.Activity.CartActivity.TAG;

public class CartAdapter  extends RecyclerView.Adapter<CartAdapter.BaseViewHolder> {
    ArrayList<POSItemList> arrayList;
    private ArrayList<POSItemList> subList;
    Context context;
    Spinner_List quantyilist;
    String qty="";
    SQLiteDataBaseHelper db;
    public   CustomItemClickListener listener;
    QuantityAdapter quantityadapter;
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    ArrayList<POSItemList> cartArraylist = new ArrayList<POSItemList>();
    public CartAdapter(ArrayList<POSItemList> enquiryList, Context context,CustomItemClickListener listener) {
        //this.arrayList = enquiryList;
        subList = enquiryList;
        arrayList = new ArrayList<POSItemList>();
        this.context = context;
        arrayList.addAll(enquiryList);
        this.listener=listener;

    }

    @Override
    public CartAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "view type: "+viewType );
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new CartAdapter.ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_list, parent, false));
            case VIEW_TYPE_LOADING:
                return new CartAdapter.FooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(CartAdapter.BaseViewHolder holder, int position) {
        holder.onBind(position);

        Log.d(TAG, "call onbind method of viewholder: " );
    }
    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == arrayList.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return arrayList == null ? 0 : arrayList.size();
    }
    public void add(POSItemList response) {
        arrayList.add(response);
        //subList.add(response);
        Log.d(TAG, "sublist size after add : "+String.valueOf(subList.size()) );
        notifyItemInserted(arrayList.size() - 1);
    }

    public void addAll(ArrayList<POSItemList> postItems) {
        for (POSItemList response : postItems) {
            add(response);
            subList.add(response);
        }

        Log.d(TAG, "arraylist size after adding new data: "+String.valueOf(arrayList.size()) );

    }


    private void remove(POSItemList postItems) {
        int position = arrayList.indexOf(postItems);
        if (position > -1) {
            arrayList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addLoading() {
        isLoaderVisible = false;
        add(new POSItemList());

    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = arrayList.size() - 1;
        POSItemList item = getItem(position);
        if (item != null) {
            arrayList.remove(position);
            notifyItemRemoved(position);
            // notifyDataSetChanged();
        }
    }
    public void removeblank(){
        isLoaderVisible = false;
        int position = arrayList.size() - 1;
        POSItemList item = getItem(position);
        if (item != null) {
            arrayList.remove(position);
            notifyItemRemoved(position);
            //notifyDataSetChanged();
        }
    }
    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }
    POSItemList getItem(int position) {
        return arrayList.get(position);
    }
    //filter for search
    public int filter(String charText) {
        // subList=arrayList;

        charText = charText.toLowerCase(Locale.getDefault());
        Log.d(TAG, "sublist size whentext  filter: "+String.valueOf(subList.size()) );
        arrayList.clear();
        if (charText.length() == 0) {

            arrayList.addAll(subList);
        } else {
            for (POSItemList wp : subList) {
                if (wp.getProductCode().toLowerCase(Locale.getDefault())
                        .contains(charText) || wp.getProductName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrayList.add(wp);
                    // return arrayList.size();
                }
            }
        }
        notifyDataSetChanged();
        return arrayList.size();
        //Log.d(TAG, "sublist size filter: "+String.valueOf(subList.size()) );

    }
    //filter for search
    public int search( String charTex,final ArrayList<POSItemList> subList) {
        // subList=arrayList;

        final String charText = charTex.toLowerCase(Locale.getDefault());
        Log.d(TAG, "sublist size whentext  filter: "+String.valueOf(subList.size()) );
        arrayList.clear();
        if (charText.length() == 0) {

            arrayList.addAll(subList);
            return 0;
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    // UI code goes here
                    for (final POSItemList wp : subList) {
                        if (wp.getProductCode().toLowerCase(Locale.getDefault())
                                .contains(charText) || wp.getProductName().toLowerCase(Locale.getDefault()).contains(charText)) {
                            arrayList.add(wp);

                        }
                    }
                }
            });



        }
        Log.d(TAG, "sublist size search: "+String.valueOf(subList.size()) );
        notifyDataSetChanged();
        return arrayList.size();
    }
    //View for showing enquiry
    public class ViewHolder extends CartAdapter.BaseViewHolder  {
        TextView prodCodeTV, prodnameTV,quantityTV,priceTV;
        Spinner spinQty;
        //  ImageView contactIV;
        ImageView imageView;
        View layoutparent;
        public RelativeLayout viewBackground, viewForeground;

        public ViewHolder(View itemView) {
            super(itemView);

            prodCodeTV = (TextView) itemView.findViewById(R.id.prod_code);
            prodnameTV = (TextView) itemView.findViewById(R.id.prod_name);
            spinQty=(Spinner)itemView.findViewById(R.id.spinner_quantity);
            // contactIV = (ImageView) itemView.findViewById(R.id.contactIV);
            imageView=(ImageView) itemView.findViewById(R.id.prodImage);
            // contactIV.setOnClickListener(this);
            //quantityTV = (TextView) itemView.findViewById(R.id.quantityTV);
            priceTV = (TextView) itemView.findViewById(R.id.price);
            layoutparent=(View)itemView.findViewById(R.id.lyt_parent);
            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);
        }

        @Override
        protected void clear() {

        }

        public void onBind(final int position) {
            super.onBind(position);
            final POSItemList enq = arrayList.get(position);
            //Log.d(TAG, "enquiry name: " + enq.getName());
            // idTV.setText(enq.getID());
            db=new SQLiteDataBaseHelper(context);
            Log.d(TAG, "product id: "+enq.getAutoId() );
            prodCodeTV.setText(enq.getProductCode());
            //Log.d(TAG, "textview name: " + nameTV.getText().toString());
            prodnameTV.setText(enq.getProductName());
            //String ttl="₹ "+enq.getRate();
            priceTV.setText(enq.getRate());
//            quantityTV.setText(enq.getQuantity());
            String domainurl= SharedPrefereneceUtil.getDomainUrl((Activity)context);
            String url= domainurl+ ServiceUrls.IMAGES_URL + enq.getProductImage();
            Log.d(TAG, "product image: "+enq.getProductImage() );

            if(!(enq.getProductImage().equals("null")||enq.getProductImage().equals(""))) {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.noproduct);
                requestOptions.error(R.drawable.noproduct);
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(url)
                        .into(imageView);
            }
            final   POSItemList cartitem = new POSItemList();
//            Handler handler = new Handler(Looper.getMainLooper());
//            handler.post(new Runnable() {
//                public void run() {
                    // UI code goes here

                    ArrayList<Spinner_List> quantiytArrayList = new ArrayList<Spinner_List>();
                    int quanty=Integer.parseInt(enq.getQuantity());
                    Log.d(TAG, "product Quantity: "+enq.getQuantity() );
                    for(int i=1;i <= quanty;i++) {
                        quantyilist = new Spinner_List();
                        quantyilist.setName(String.valueOf(i));
                        quantiytArrayList.add(quantyilist);
                    }
                        quantityadapter = new QuantityAdapter(context, quantiytArrayList) {
                            @Override
                            public boolean isEnabled(int position) {
//                        if (position == 0) {
//                            // Disable the first item from Spinner
//                            // First item will be use for hint
//                            return false;
//                        } else {
                                return true;
//                        }
                            }

                            @Override
                            public View getDropDownView(int position, View convertView,
                                                        ViewGroup parent) {
                                View view = super.getDropDownView(position, convertView, parent);
                                TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                                tv.setTextColor(Color.BLACK);

                                return view;
                            }

                        };
                        spinQty.setAdapter(quantityadapter);


//                }
//            });
            spinQty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                    int index = parent.getSelectedItemPosition();
                    if(view != null) {
                        TextView tv = (TextView) view.findViewById(R.id.tv_Name);
                        View layout = (View) view.findViewById(R.id.layout);
                        layout.setPadding(0, 0, 0, 0);

                        if (index == 0) {
                            // tv.setTextColor((Color.GRAY));
                            qty=enq.getSelectedQuantity();
                        } else {
                            //

                            qty = tv.getText().toString();
                        }
                        tv.setTextColor((Color.BLACK));
                        Log.d(TAG, "qty****: "+qty);
                        Log.d(TAG, "product selected quantity: "+enq.getSelectedQuantity() );
//                        if (enq.getSelectedQuantity().equals("1")) {
//                            qty = tv.getText().toString();
//                        }else{
//                            qty=enq.getSelectedQuantity();
//                        }
                        //tv.setText(enq.getSelectedQuantity());

                        cartitem.setQuantity(qty);
                        String qtyyy="Qty: "+qty;
                        tv.setText(qtyyy);

                        if(!(qty.equals("null")||qty.equals(""))){
                            int qnty=Integer.parseInt(qty);
                            String totl=enq.getRate();
                            String sp[]=totl.split(" ");
                            double rate=Double.parseDouble(sp[1]);
                            double ttlrate=rate*qnty;

                            String ttl="₹ "+ttlrate;
                            priceTV.setText(ttl);

                            db.updateQuantity(qty,enq.getProductCode());
                            cartitem.setSelectedQuantity(qty);

//                            if (listener != null) {
                            listener.onSpinnerQty(spinQty,position);
                            //notifyDataSetChanged();
//                            }
                            //  Log.d(TAG, "product total rate: "+ttl );

                        }


                    }
                    // ((TextView) spinEnquiryType.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
                    // Showing selected spinner item
                    //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Toast.makeText(parent.getContext(), "Please Select Rating ", Toast.LENGTH_LONG).show();
                }
            });
            cartitem.setAutoId(enq.getAutoId());
            cartitem.setProductName(enq.getProductName());
            cartitem.setProductCode(enq.getProductCode());
            cartitem.setProductDisc(enq.getProductDisc());
            cartitem.setTax(enq.getTax());
            cartitem.setMaxDiscount(enq.getMaxDiscount());
            cartitem.setProductFinalRate(priceTV.getText().toString());
            cartitem.setQuantity(qty);
            cartitem.setSelectedQuantity(qty);
            cartitem.setRate(priceTV.getText().toString());
            cartitem.setPurchaseAmount(enq.getPurchaseAmount());
            cartitem.setProductImage(enq.getProductImage());
            cartArraylist.add(cartitem);
            selctedProduct();

        }
    }

    //view for loading on swipe of recyclerview
    public class FooterHolder extends CartAdapter.BaseViewHolder {

        @BindView(R.id.progressBar)
        ProgressBar mProgressBar;


        FooterHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {

        }

    }
    //base class for both the views
    public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

        private int mCurrentPosition;

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        protected abstract void clear();

        public void onBind(int position) {
            mCurrentPosition = position;
            clear();
        }

        public int getCurrentPosition() {
            return mCurrentPosition;
        }

    }

    public ArrayList<POSItemList> selctedProduct(){
        //Log.v(TAG, String.format(" :: Select product array= %s", String.valueOf(cartArraylist)));

        return cartArraylist;
    }
    public void removeItem(int position) {
        arrayList.remove(position);
        cartArraylist.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(POSItemList item, int position) {
        arrayList.add(position,item);
        //cartArraylist.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

}


