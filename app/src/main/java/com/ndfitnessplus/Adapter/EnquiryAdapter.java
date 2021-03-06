package com.ndfitnessplus.Adapter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ndfitnessplus.Activity.EnquiryActivity;
import com.ndfitnessplus.Activity.EnquiryFollowupDetailsActivity;
import com.ndfitnessplus.Activity.FullImageActivity;
import com.ndfitnessplus.Activity.MemberDetailsActivity;
import com.ndfitnessplus.Model.BranchList;
import com.ndfitnessplus.Model.EnquiryList;
import com.ndfitnessplus.Model.POSItemList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ndfitnessplus.Activity.EnquiryActivity.TAG;

public class EnquiryAdapter extends RecyclerView.Adapter<EnquiryAdapter.BaseViewHolder> {
  public   ArrayList<EnquiryList> arrayList;
    private ArrayList<EnquiryList> subList;
    public Context context;

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;

    public EnquiryAdapter(ArrayList<EnquiryList> enquiryList, Context context) {
        this.subList = enquiryList;
        this.arrayList = new ArrayList<EnquiryList>();
        this.context = context;
        this.arrayList.addAll(enquiryList);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.enquiry_list_item, parent, false));
            case VIEW_TYPE_LOADING:
                return new FooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
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
    public void add(EnquiryList response) {
        arrayList.add(response);
        notifyItemInserted(arrayList.size() - 1);
    }

    public void addAll(ArrayList<EnquiryList> postItems) {
        for (EnquiryList response : postItems) {
            add(response);
            subList.add(response);
        }

    }


    private void remove(EnquiryList postItems) {
        int position = arrayList.indexOf(postItems);
        if (position > -1) {
            arrayList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addLoading() {
        isLoaderVisible = false;
        add(new EnquiryList());

    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = arrayList.size() - 1;
        EnquiryList item = getItem(position);
        if (item != null) {
            arrayList.remove(position);
            notifyItemRemoved(position);
        }
    }
    public void removeblank(){
        isLoaderVisible = false;
         int position = arrayList.size() - 1;
        EnquiryList item = getItem(position);
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
    EnquiryList getItem(int position) {
        return arrayList.get(position);
    }
    //filter for search
    public ArrayList<EnquiryList> filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());

        arrayList.clear();
        if (charText.length() == 0) {
            arrayList.addAll(subList);
        } else {
            for (EnquiryList wp : subList) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText) || wp.getGender().toLowerCase(Locale.getDefault()).contains(charText)||
                         wp.getAddress().toLowerCase(Locale.getDefault()).contains(charText)
                        ||wp.getComment().toLowerCase(Locale.getDefault()).contains(charText)
                        ||wp.getContact().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrayList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
        return arrayList;

    }
    //filter for search
    public int search( String charTex,final ArrayList<EnquiryList> subList) {

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
                    for (final EnquiryList wp : subList) {
                        if (wp.getName().toLowerCase(Locale.getDefault())
                                .contains(charText) || wp.getGender().toLowerCase(Locale.getDefault()).contains(charText)||
                                wp.getAddress().toLowerCase(Locale.getDefault()).contains(charText)||wp.getExecutiveName().toLowerCase(Locale.getDefault()).contains(charText)
                                ||wp.getComment().toLowerCase(Locale.getDefault()).contains(charText)) {
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
    public class ViewHolder extends BaseViewHolder implements View.OnClickListener {
        TextView nameTV,excecutive_nameTV,commentTV,nextFollowupdateTV,followup_dateTV,ratingTV,callRespondTV,contactTV;
        CircularImageView imageView;
        View layoutparent;
        public ViewHolder(View itemView) {
            super(itemView);

            contactTV =  itemView.findViewById(R.id.contactTV);
            followup_dateTV =  itemView.findViewById(R.id.followup_dateTV);
            nameTV =  itemView.findViewById(R.id.nameTV);
            imageView= itemView.findViewById(R.id.input_image);
            excecutive_nameTV =  itemView.findViewById(R.id.excecutive_nameTV);
            commentTV =  itemView.findViewById(R.id.commentTV);
            nextFollowupdateTV =  itemView.findViewById(R.id.nextFollowupdateTV);
            ratingTV =  itemView.findViewById(R.id.ratingTV);
            callRespondTV =  itemView.findViewById(R.id.callRespondTV);
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
           final EnquiryList enq = arrayList.get(position);
            nameTV.setText(enq.getName());

             contactTV.setText(enq.getContactEncrypt());
            excecutive_nameTV.setText(enq.getExecutiveName());
            commentTV.setText(enq.getComment());
            nextFollowupdateTV.setText(enq.getNextFollowUpDate());
            ratingTV.setText(enq.getRating());
            followup_dateTV.setText(enq.getFollowupdate());
            callRespondTV.setText(enq.getCallResponse());
            String domainurl= SharedPrefereneceUtil.getDomainUrl((Activity)context);
            String url= domainurl+ServiceUrls.IMAGES_URL + enq.getImage();

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.nouser);
            requestOptions.error(R.drawable.nouser);


            Glide.with(context)
                    .setDefaultRequestOptions(requestOptions)
                    .load(url).into(imageView);

            final android.view.animation.Animation animation_1 = android.view.animation.AnimationUtils.loadAnimation(context,R.anim.zoom_out);
            final android.view.animation.Animation animation_2 = android.view.animation.AnimationUtils.loadAnimation(context,R.anim.zoom_in);

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
                    if (!enq.getRating().equals("Converted")) {
                        Intent intent = new Intent(context, EnquiryFollowupDetailsActivity.class);
                        intent.putExtra("enquiry_id", enquiryId);
                        intent.putExtra("rating", enq.getRating());
                        intent.putExtra("call_response", enq.getCallResponse());
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
    private void showCustomDialog(int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.item_grid_image_two_line_light);
        dialog.setCancelable(true);
        final EnquiryList enq = arrayList.get(position);

        ImageView imageView =  dialog. findViewById(R.id.image);
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
                intent.putExtra("user","Enquiry");
                context.startActivity(intent);
            }
        });
        dialog.show();
    }
    //view for loading on swipe of recyclerview
    public class FooterHolder extends BaseViewHolder {

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
