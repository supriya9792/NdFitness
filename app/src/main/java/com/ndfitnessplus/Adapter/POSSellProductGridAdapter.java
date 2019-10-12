package com.ndfitnessplus.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ndfitnessplus.Activity.MainActivity;
import com.ndfitnessplus.Activity.POSProductDetailsActivity;
import com.ndfitnessplus.Activity.POSProductListActivity;
import com.ndfitnessplus.LocalDatabase.SQLiteDataBaseHelper;
import com.ndfitnessplus.Model.POSItemList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.ndfitnessplus.Activity.POSProductListActivity.TAG;

public class POSSellProductGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

private List<POSItemList> items = new ArrayList<>();
    ArrayList<POSItemList> arrayList;
    private ArrayList<POSItemList> subList;
private Context ctx;
private OnItemClickListener mOnItemClickListener;
private OnMoreButtonClickListener onMoreButtonClickListener;
    SQLiteDataBaseHelper db;
public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
        }

public void setOnMoreButtonClickListener(final OnMoreButtonClickListener onMoreButtonClickListener) {
        this.onMoreButtonClickListener = onMoreButtonClickListener;
        }

public POSSellProductGridAdapter( ArrayList<POSItemList> enquiryList,Context context) {
    this.subList = enquiryList;
    this.arrayList = new ArrayList<POSItemList>();
    this.ctx = context;
    this.arrayList.addAll(enquiryList);
        }

public class OriginalViewHolder extends RecyclerView.ViewHolder {
    public ImageView image;
    public TextView prod_name,prod_code;
    public TextView price;
    public Button more;
    public View lyt_parent;

    public OriginalViewHolder(View v) {
        super(v);
        image = (ImageView) v.findViewById(R.id.image);
        prod_name = (TextView) v.findViewById(R.id.prod_name);
        prod_code = (TextView) v.findViewById(R.id.prodcodeTV);
        price = (TextView) v.findViewById(R.id.price);
        more = (Button) v.findViewById(R.id.more);
        lyt_parent = (View) v.findViewById(R.id.lyt_parent);
    }
}

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_product_card, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
           final OriginalViewHolder view = (OriginalViewHolder) holder;

            final POSItemList p = arrayList.get(position);

            view.prod_name.setText(p.getProductName());
            String pr="₹ "+p.getRate();
            view.price.setText(pr);

            view.prod_code.setText(p.getProductCode());
            String domainurl= SharedPrefereneceUtil.getDomainUrl((Activity)ctx);
            String url= domainurl+ ServiceUrls.IMAGES_URL + p.getProductImage();
            Log.d(TAG, "product image: "+p.getProductImage() );
           // Glide.with(ctx).load(url).placeholder(R.drawable.noproduct).into(view.image);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.noproduct);
            requestOptions.error(R.drawable.noproduct);


            Glide.with(ctx)
                    .setDefaultRequestOptions(requestOptions)
                    .load(url).into(view.image);
            //Tools.displayImageOriginal(ctx, view.image, p.image);
            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                        Intent intent=new Intent(ctx,POSProductDetailsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("filter_array_list", p);
                        intent.putExtra("BUNDLE",bundle);
                        ctx.startActivity(intent);


                }
            });
            if (!p.isAddtocart()){
            view.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    p.setAddtocart(true);
                    p.addtocart=true;
                   notifyDataSetChanged();
                    String quantity;
                   if(p.getQuantity().equals("")||p.getQuantity().equals("null")){
                       quantity="1";
                   }else{
                        quantity=p.getQuantity();
                   }

                    db=new SQLiteDataBaseHelper((Activity) ctx);
                    final  String compid=SharedPrefereneceUtil.getSelectedBranchId((Activity) ctx);
                    long returnid=  db.insertCartes(p.getAutoId(),p.getProductCode(),p.getProductName(),p.getProductDisc(),
                            p.getProductImage(),compid,quantity,"1",p.getRate(),p.getTax(),p.getMaxDiscount(),p.getPurchaseAmount());

                    if(returnid > 0){
                        String  userId = String.valueOf(returnid);
                        Log.v(TAG, String.format(" :: product id = %s", userId));
                        Snackbar.make(v, "Product Added Into Cart Successfully", Snackbar.LENGTH_SHORT).show();

//                        ((Activity) ctx).finish();
//                        ((Activity) ctx).overridePendingTransition( 0, 0);
//                        ctx.startActivity(((Activity) ctx).getIntent());
                    }
                }
            });
        }
            if (p.addtocart) {
                view.more.setText("Added");
            }else{
                view.more.setText("Add");
            }

        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

public interface OnItemClickListener {
    void onItemClick(View view, POSItemList obj, int pos);
}

public interface OnMoreButtonClickListener {
    void onItemClick(View view, POSItemList obj, MenuItem item);
}
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
}
