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

public class   CollectionAdapter  extends RecyclerView.Adapter<CollectionAdapter.BaseViewHolder> {
   private ArrayList<CourseList> arrayList;
    private ArrayList<CourseList> subList;
    private Context context;

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;

    public CollectionAdapter(ArrayList<CourseList> enquiryList, Context context) {
        this.subList = enquiryList;
        this.arrayList = new ArrayList<CourseList>();
        this.context = context;
        this.arrayList.addAll(enquiryList);
    }

    @Override
    public CollectionAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        notifyItemInserted(arrayList.size() - 1);
    }

    public void addAll(ArrayList<CourseList> postItems) {
        for (CourseList response : postItems) {
            add(response);
            subList.add(response);
        }
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
    }
    }
    public void removeblank(){
        isLoaderVisible = false;
        int position = arrayList.size() - 1;
        CourseList item = getItem(position);
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
    CourseList getItem(int position) {
        return arrayList.get(position);
    }
    //filter for search
    public void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
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
        notifyDataSetChanged();
    }
    //filter for search
    public int search(String charTex,final ArrayList<CourseList> subList) {

        final String charText = charTex.toLowerCase(Locale.getDefault());

        arrayList.clear();
        if (charText.length() == 0) {

            arrayList.addAll(subList);
            return 0;
        } else {
                    for (final CourseList wp : subList) {
                        if (wp.getName().toLowerCase(Locale.getDefault())
                                .contains(charText) || wp.getPaymentType().toLowerCase(Locale.getDefault()).contains(charText)||
                                wp.getBalance().toLowerCase(Locale.getDefault()).contains(charText)
                                ||wp.getPaid().toLowerCase(Locale.getDefault()).contains(charText)
                                ||wp.getContact().toLowerCase(Locale.getDefault()).contains(charText)) {
                            arrayList.add(wp);
                        }
                    }
        }
        notifyDataSetChanged();
        return arrayList.size();
    }
    //View for showing enquiry
    public class ViewHolder extends CollectionAdapter.BaseViewHolder implements View.OnClickListener {
        TextView nameTV,regdateTV,paymenttypeTv,paidTV,balanceTV,contactTV,executiveNameTV,invoiceidTv,paymentDetailsTv,receipttypeTV,nextPayDateTV;
        CircularImageView imageView;
        View layoutparent;
        public ViewHolder(View itemView) {
            super(itemView);

            contactTV =  itemView.findViewById(R.id.contactTV);
            nameTV =  itemView.findViewById(R.id.nameTV);
            invoiceidTv =  itemView.findViewById(R.id.invoice_idTV);
            paymentDetailsTv =  itemView.findViewById(R.id.paymentDtlTV);
            receipttypeTV =  itemView.findViewById(R.id.receiptTypeTV);
            nextPayDateTV =  itemView.findViewById(R.id.next_payment_dateTV);
            imageView= itemView.findViewById(R.id.input_image);
            regdateTV =  itemView.findViewById(R.id.reg_dateTV);
            paymenttypeTv =  itemView.findViewById(R.id.paymentTypeTV);
            paidTV =  itemView.findViewById(R.id.paidTV);
            executiveNameTV=itemView.findViewById(R.id.excecutive_nameTV);
            balanceTV =  itemView.findViewById(R.id.balanceTV);
            layoutparent=itemView.findViewById(R.id.lyt_parent);
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

            nameTV.setText(enq.getName());
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

        ImageView imageView = dialog. findViewById(R.id.image);
        TextView name =  dialog. findViewById(R.id.name);
        ImageButton phone=dialog.findViewById(R.id.phone_call);
        ImageView whatsapp=dialog.findViewById(R.id.whatsapp);
        String domainurl= SharedPrefereneceUtil.getDomainUrl((Activity) context);
        String url= domainurl+ServiceUrls.IMAGES_URL + enq.getImage();

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

                    Uri uri = Uri.parse("whatsapp://send?phone=+91" + enq.getContact());
                    Intent waIntent = new Intent(Intent.ACTION_VIEW,uri);
                    PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                    waIntent.setPackage("com.whatsapp");
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
