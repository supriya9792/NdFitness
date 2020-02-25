package com.ndfitnessplus.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ndfitnessplus.Model.POSItemList;
import com.ndfitnessplus.Model.Spinner_List;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.CustomItemClickListener;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ndfitnessplus.Activity.CartActivity.TAG;

public class SelectedCartAdapter extends RecyclerView.Adapter<SelectedCartAdapter.BaseViewHolder> {
   public ArrayList<POSItemList> arrayList;
    private ArrayList<POSItemList> subList;
   public Context context;
    Spinner_List quantyilist;
    String qty="";
    public CustomItemClickListener listener;
    QuantityAdapter quantityadapter;
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    ArrayList<POSItemList> cartArraylist = new ArrayList<POSItemList>();
    public SelectedCartAdapter(ArrayList<POSItemList> enquiryList, Context context) {
        this.subList = enquiryList;
        this.arrayList = new ArrayList<POSItemList>();
        this.context = context;
        this.arrayList.addAll(enquiryList);

    }

    @Override
    public SelectedCartAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new SelectedCartAdapter.ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_cart_product, parent, false));
            case VIEW_TYPE_LOADING:
                return new SelectedCartAdapter.FooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(SelectedCartAdapter.BaseViewHolder holder, int position) {
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
    public class ViewHolder extends SelectedCartAdapter.BaseViewHolder  {
        TextView prodCodeTV, prodnameTV,quantityTV,priceTV;
        ImageView imageView;
        View layoutparent;
        public ViewHolder(View itemView) {
            super(itemView);

            prodCodeTV = itemView.findViewById(R.id.prod_code);
            prodnameTV = itemView.findViewById(R.id.prod_name);

            imageView= itemView.findViewById(R.id.prodImage);
            quantityTV = itemView.findViewById(R.id.quantityTV);
            priceTV = itemView.findViewById(R.id.price);
            layoutparent=itemView.findViewById(R.id.lyt_parent);
        }

        @Override
        protected void clear() {

        }

        public void onBind(final int position) {
            super.onBind(position);
            final POSItemList enq = arrayList.get(position);
            prodCodeTV.setText(enq.getProductCode());
            prodnameTV.setText(enq.getProductName());

            priceTV.setText(enq.getProductFinalRate());
            String qtyyy="Qty: "+enq.getQuantity();
            quantityTV.setText(qtyyy);
            String domainurl= SharedPrefereneceUtil.getDomainUrl((Activity)context);
            String url= domainurl+ ServiceUrls.IMAGES_URL + enq.getProductImage();

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.nouser);
            requestOptions.error(R.drawable.nouser);


            Glide.with(context)
                    .setDefaultRequestOptions(requestOptions)
                    .load(url).into(imageView);
            final   POSItemList cartitem = new POSItemList();
            ArrayList<Spinner_List> quantiytArrayList = new ArrayList<Spinner_List>();
            if(!(enq.getQuantity().equals("null")||enq.getQuantity().equals(""))){

                int quanty=Integer.parseInt(enq.getQuantity());
            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });



        }
    }

    //view for loading on swipe of recyclerview
    public class FooterHolder extends SelectedCartAdapter.BaseViewHolder {

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
}
