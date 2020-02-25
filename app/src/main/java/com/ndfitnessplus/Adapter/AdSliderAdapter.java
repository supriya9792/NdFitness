package com.ndfitnessplus.Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ndfitnessplus.Activity.MainActivity;
import com.ndfitnessplus.Model.AdSliderList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.ServiceUrls;
import com.ndfitnessplus.Utility.SharedPrefereneceUtil;

import java.util.ArrayList;
import java.util.List;

import static com.pchmn.materialchips.R2.attr.logo;

public class AdSliderAdapter extends PagerAdapter implements LoopingPagerAdapter {
  private LayoutInflater layoutInflater;
  private Button btnNext;
  private Context mContext;
  private List<AdSliderList> signalList;
  private ArrayList<AdSliderList> arraylist;

  public AdSliderAdapter(Context mContext, ArrayList<AdSliderList> albumList) {
    this.mContext = mContext;
    this.signalList = albumList;
    this.arraylist = new ArrayList<AdSliderList>();
    this.arraylist.addAll(albumList);
    this.arraylist=albumList;
  }
  @Override
  public Object instantiateItem(ViewGroup container, int position) {

    layoutInflater=LayoutInflater.from(container.getContext());

    View view = layoutInflater.inflate(R.layout.ad_slider_list, container, false);
    final AdSliderList enq = signalList.get(position);

    ImageView placeholder  =(ImageView) view.findViewById(R.id.ad_img) ;
    RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.add_gymtime1);
                requestOptions.error(R.drawable.add_gymtime1);
                String domainurl= SharedPrefereneceUtil.getDomainUrl((Activity)mContext);
                String url= domainurl+ ServiceUrls.IMAGES_URL + arraylist.get(position).getBannerImage();

                Glide.with((Activity)mContext)
                        .setDefaultRequestOptions(requestOptions)
                        .load(url).into(placeholder);
    container.addView(view);
    return view;
  }

  @Override
  public int getCount() {
    return signalList.size();
  }

  @Override
  public boolean isViewFromObject(View view, Object obj) {
    return view == obj;
  }


  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    View view = (View) object;
    container.removeView(view);
  }
  @Override
  public Parcelable saveState()
  {
    return null;
  }
  @Override
  public int getRealCount() {
    return signalList.size();
  }
}
