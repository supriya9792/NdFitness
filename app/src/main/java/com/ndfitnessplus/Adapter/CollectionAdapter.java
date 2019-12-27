package com.ndfitnessplus.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ndfitnessplus.Activity.CourseDetailsActivity;
import com.ndfitnessplus.Activity.FullImageActivity;
import com.ndfitnessplus.Activity.MemberDetailsActivity;
import com.ndfitnessplus.Model.CollectionList;
import com.ndfitnessplus.Model.CourseList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ndfitnessplus.Activity.EnquiryActivity.TAG;

public class CollectionAdapter  extends RecyclerView.Adapter<CollectionAdapter.BaseViewHolder> {
    ArrayList<CourseList> arrayList;
    private ArrayList<CourseList> subList;
    Context context;

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;

    public CollectionAdapter(ArrayList<CourseList> enquiryList, Context context) {
        //this.arrayList = enquiryList;
        this.subList = enquiryList;
        this.arrayList = new ArrayList<CourseList>();
        this.context = context;
        this.arrayList.addAll(enquiryList);
    }

    @Override
    public CollectionAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "view type: "+viewType );
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new CollectionAdapter.ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.collection_list, parent, false));
            case VIEW_TYPE_LOADING:
                return new CollectionAdapter.FooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(CollectionAdapter.BaseViewHolder holder, int position) {
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
    public void add(CourseList response) {
        arrayList.add(response);
        //subList.add(response);
        Log.d(TAG, "sublist size after add : "+String.valueOf(subList.size()) );
        notifyItemInserted(arrayList.size() - 1);
    }

    public void addAll(ArrayList<CourseList> postItems) {
        for (CourseList response : postItems) {
            add(response);
            subList.add(response);
        }

        Log.d(TAG, "arraylist size after adding new data: "+String.valueOf(arrayList.size()) );

    }


    private void remove(CourseList postItems) {
        int position = arrayList.indexOf(postItems);
        if (position > -1) {
            arrayList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addLoading() {
        isLoaderVisible = false;
        add(new CourseList());

    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = arrayList.size() - 1;
        CourseList item = getItem(position);
        if (item != null) {
            arrayList.remove(position);
            notifyItemRemoved(position);
            // notifyDataSetChanged();
        }
    }
    public void removeblank(){
        isLoaderVisible = false;
        int position = arrayList.size() - 1;
        CourseList item = getItem(position);
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
    CourseList getItem(int position) {
        return arrayList.get(position);
    }
    //filter for search
    public void filter(String charText) {
        // subList=arrayList;

        charText = charText.toLowerCase(Locale.getDefault());
        Log.d(TAG, "sublist size whentext  filter: "+String.valueOf(subList.size()) );
        arrayList.clear();
        if (charText.length() == 0) {

            arrayList.addAll(subList);
        } else {
            for (CourseList wp : subList) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText) || wp.getPaymentType().toLowerCase(Locale.getDefault()).contains(charText)||
                        wp.getBalance().toLowerCase(Locale.getDefault()).contains(charText)
                        ||wp.getPaid().toLowerCase(Locale.getDefault()).contains(charText)
                        ||wp.getContact().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrayList.add(wp);
                }
            }
        }
        Log.d(TAG, "sublist size filter: "+String.valueOf(subList.size()) );
        notifyDataSetChanged();
        //return  arrayList.size();
    }
    //filter for search
    public int search(String charTex,final ArrayList<CourseList> subList) {
        // subList=arrayList;

        final String charText = charTex.toLowerCase(Locale.getDefault());

        arrayList.clear();
        if (charText.length() == 0) {

            arrayList.addAll(subList);
            return 0;
        } else {
//            Handler handler = new Handler(Looper.getMainLooper());
//            handler.post(new Runnable() {
//                public void run() {
                    // UI code goes here
                    for (final CourseList wp : subList) {
                        if (wp.getName().toLowerCase(Locale.getDefault())
                                .contains(charText) || wp.getPaymentType().toLowerCase(Locale.getDefault()).contains(charText)||
                                wp.getBalance().toLowerCase(Locale.getDefault()).contains(charText)
                                ||wp.getPaid().toLowerCase(Locale.getDefault()).contains(charText)
                                ||wp.getContact().toLowerCase(Locale.getDefault()).contains(charText)) {
                            arrayList.add(wp);
                            Log.d(TAG, "sublist size whentext  filter: "+String.valueOf(subList.size()) );
                        }
                    }
//                }
//            });



        }
        Log.d(TAG, "sublist size search: "+String.valueOf(subList.size()) );
        notifyDataSetChanged();
        return arrayList.size();
    }
    //View for showing enquiry
    public class ViewHolder extends CollectionAdapter.BaseViewHolder implements View.OnClickListener {
        TextView nameTV,regdateTV,paymenttypeTv,paidTV,balanceTV,contactTV,executiveNameTV,invoiceidTv,paymentDetailsTv,receipttypeTV,nextPayDateTV;
        //ImageView contactIV;
        CircularImageView imageView;
        View layoutparent;
        public ViewHolder(View itemView) {
            super(itemView);

            contactTV = (TextView) itemView.findViewById(R.id.contactTV);
            nameTV = (TextView) itemView.findViewById(R.id.nameTV);
            invoiceidTv = (TextView) itemView.findViewById(R.id.invoice_idTV);
            paymentDetailsTv = (TextView) itemView.findViewById(R.id.paymentDtlTV);
            receipttypeTV = (TextView) itemView.findViewById(R.id.receiptTypeTV);
            nextPayDateTV = (TextView) itemView.findViewById(R.id.next_payment_dateTV);
            imageView=(CircularImageView) itemView.findViewById(R.id.input_image);
            // contactIV.setOnClickListener(this);
            regdateTV = (TextView) itemView.findViewById(R.id.reg_dateTV);
            paymenttypeTv = (TextView) itemView.findViewById(R.id.paymentTypeTV);
            paidTV = (TextView) itemView.findViewById(R.id.paidTV);
            executiveNameTV=(TextView)itemView.findViewById(R.id.excecutive_nameTV);
            balanceTV = (TextView) itemView.findViewById(R.id.balanceTV);
            layoutparent=(View)itemView.findViewById(R.id.lyt_parent);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.contactTV){
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:"+arrayList.get(getAdapterPosition()).getContact()));
                context.startActivity(dialIntent);
            }
        }
        @Override
        protected void clear() {

        }

        public void onBind(final int position) {
            super.onBind(position);
            final CourseList enq = arrayList.get(position);
            //Log.d(TAG, "enquiry name: " + enq.getName());
            // idTV.setText(enq.getID());
            nameTV.setText(enq.getName());
            //Log.d(TAG, "textview name: " + nameTV.getText().toString());
            contactTV.setText(enq.getContactEncrypt());
            regdateTV.setText(enq.getReceiptDate());
            paymenttypeTv.setText(enq.getPaymentType());
            executiveNameTV.setText(enq.getExecutiveName());
            paidTV.setText(enq.getPaid());
            balanceTV.setText(enq.getBalanceRuppe());
            invoiceidTv.setText(enq.getReceiptId());
            paymentDetailsTv.setText(enq.getPaymentDetails());
            receipttypeTV.setText(enq.getReceiptType());
            nextPayDateTV.setText(enq.getNextPaymentdate());
            String domainurl= SharedPrefereneceUtil.getDomainUrl((Activity)context);
            String url= domainurl+ServiceUrls.IMAGES_URL + enq.getImage();

           // Glide.with(context).load(url).placeholder(R.drawable.nouser).into(imageView);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.nouser);
            requestOptions.error(R.drawable.nouser);


            Glide.with(context)
                    .setDefaultRequestOptions(requestOptions)
                    .load(url).into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCustomDialog(position);
                }
            });
            layoutparent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String   enquiryId=enq.getID();
                    Intent intent=new Intent(context, CourseDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("filter_array_list", enq);
                    intent.putExtra("BUNDLE",bundle);
                    context.startActivity(intent);
                }
            });
        }
    }
    private void showCustomDialog(int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.item_grid_image_two_line_light);
        dialog.setCancelable(true);
        final CourseList enq = arrayList.get(position);
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(dialog.getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        ImageView imageView = (ImageView) dialog. findViewById(R.id.image);
        TextView name = (TextView) dialog. findViewById(R.id.name);
        ImageButton phone=(ImageButton)dialog.findViewById(R.id.phone_call);
        ImageView whatsapp=(ImageView)dialog.findViewById(R.id.whatsapp);
        String domainurl= SharedPrefereneceUtil.getDomainUrl((Activity) context);
        String url= domainurl+ServiceUrls.IMAGES_URL + enq.getImage();
        Log.d(TAG, "image: "+enq.getImage());
        Log.d(TAG, "name: "+enq.getName());
        //Glide.with(context).load(url).placeholder(R.drawable.nouser).into(imageView);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.nouser);
        requestOptions.error(R.drawable.nouser);


        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(url).into(imageView);
        name.setText(enq.getName());
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:"+enq.getContact()));
                context.startActivity(dialIntent);
            }
        });
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                PackageManager pm=context.getPackageManager();
                try {
                    // Uri uri = Uri.parse("smsto:" + Contact);
                    Uri uri = Uri.parse("whatsapp://send?phone=+91" + enq.getContact());
                    Intent waIntent = new Intent(Intent.ACTION_VIEW,uri);
                    //waIntent.setType("text/plain");
                    String text = "YOUR TEXT HERE";

                    PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                    //Check if package exists or not. If not then code
                    //in catch block will be called
                    waIntent.setPackage("com.whatsapp");

                    // waIntent.putExtra(Intent.EXTRA_TEXT, text);
                    context.startActivity(waIntent);

                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(context, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, FullImageActivity.class);
                intent.putExtra("image",enq.getImage());
                intent.putExtra("contact",enq.getContact());
                intent.putExtra("id",enq.getID());
                intent.putExtra("user","Member");
                context.startActivity(intent);
            }
        });
        dialog.show();
        //dialog.getWindow().setAttributes(lp);
    }
    //view for loading on swipe of recyclerview
    public class FooterHolder extends CollectionAdapter.BaseViewHolder {

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
