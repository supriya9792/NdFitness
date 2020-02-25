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
     private QuantityAdapter quantityadapter;
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    ArrayList<POSItemList> cartArraylist = new ArrayList<POSItemList>();
    public CartAdapter(ArrayList<POSItemList> enquiryList, Context context,CustomItemClickListener listener) {
        subList = enquiryList;
        arrayList = new ArrayList<POSItemList>();
        this.context = context;
        arrayList.addAll(enquiryList);
        this.listener=listener;

    }

    @Override
    public CartAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        notifyItemInserted(arrayList.size() - 1);
    }

    public void addAll(ArrayList<POSItemList> postItems) {
        for (POSItemList response : postItems) {
            add(response);
            subList.add(response);
        }

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
        }
    }
    public void removeblank(){
        isLoaderVisible = false;
        int position = arrayList.size() - 1;
        POSItemList item = getItem(position);
        if (item != null) {
            arrayList.remove(position);
            notifyItemRemoved(position);
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

        charText = charText.toLowerCase(Locale.getDefault());
        arrayList.clear();
        if (charText.length() == 0) {

            arrayList.addAll(subList);
        } else {
            for (POSItemList wp : subList) {
                if (wp.getProductCode().toLowerCase(Locale.getDefault())
                        .contains(charText) || wp.getProductName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrayList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
        return arrayList.size();

    }
    //filter for search
    public int search( String charTex,final ArrayList<POSItemList> subList) {

        final String charText = charTex.toLowerCase(Locale.getDefault());
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
        notifyDataSetChanged();
        return arrayList.size();
    }
    //View for showing enquiry
    public class ViewHolder extends CartAdapter.BaseViewHolder  {
        TextView prodCodeTV, prodnameTV,quantityTV,priceTV;
        Spinner spinQty;
        ImageView imageView;
        View layoutparent;
        public RelativeLayout viewBackground, viewForeground;

        public ViewHolder(View itemView) {
            super(itemView);

            prodCodeTV = itemView.findViewById(R.id.prod_code);
            prodnameTV =itemView.findViewById(R.id.prod_name);
            spinQty=itemView.findViewById(R.id.spinner_quantity);
            imageView= itemView.findViewById(R.id.prodImage);

            priceTV = itemView.findViewById(R.id.price);
            layoutparent=itemView.findViewById(R.id.lyt_parent);
            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);
        }

        @Override
        protected void clear() {

        }

        public void onBind(final int position) {
            super.onBind(position);
            final POSItemList enq = arrayList.get(position);

            db=new SQLiteDataBaseHelper(context);
            prodCodeTV.setText(enq.getProductCode());

            prodnameTV.setText(enq.getProductName());

            priceTV.setText(enq.getRate());

            String domainurl= SharedPrefereneceUtil.getDomainUrl((Activity)context);
            String url= domainurl+ ServiceUrls.IMAGES_URL + enq.getProductImage();


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
                    ArrayList<Spinner_List> quantiytArrayList = new ArrayList<Spinner_List>();
                    int quanty=Integer.parseInt(enq.getQuantity());

                    for(int i=1;i <= quanty;i++) {
                        quantyilist = new Spinner_List();
                        quantyilist.setName(String.valueOf(i));
                        quantiytArrayList.add(quantyilist);
                    }
                        quantityadapter = new QuantityAdapter(context, quantiytArrayList) {
                            @Override
                            public boolean isEnabled(int position) {
                                return true;
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

            spinQty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                    int index = parent.getSelectedItemPosition();
                    if(view != null) {
                        TextView tv =  view.findViewById(R.id.tv_Name);
                        View layout =  view.findViewById(R.id.layout);
                        layout.setPadding(0, 0, 0, 0);

                        if (index == 0) {
                            qty=enq.getSelectedQuantity();
                        } else {
                            qty = tv.getText().toString();
                        }
                        tv.setTextColor((Color.BLACK));

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

                            listener.onSpinnerQty(spinQty,position);

                        }


                    }
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


        return cartArraylist;
    }
    public void removeItem(int position) {
        arrayList.remove(position);
        cartArraylist.remove(position);

        notifyItemRemoved(position);
    }

    public void restoreItem(POSItemList item, int position) {
        arrayList.add(position,item);
        notifyItemInserted(position);
    }

}


