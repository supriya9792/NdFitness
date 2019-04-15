package com.ndfitnessplus.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.ndfitnessplus.Model.AdSliderList;
import com.ndfitnessplus.R;

import java.util.ArrayList;
import java.util.List;

public class AdSliderAdapter  extends PagerAdapter  {
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

        // layoutInflater = (LayoutInflater)container (Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater=LayoutInflater.from(container.getContext());

        View view = layoutInflater.inflate(R.layout.ad_slider_list, container, false);
        final AdSliderList enq = signalList.get(position);

        ImageView placeholder  =(ImageView) view.findViewById(R.id.ad_img) ;
        TextView title=(TextView)view.findViewById(R.id.ad_title);
        TextView disc=(TextView)view.findViewById(R.id.ad_disc);



        title.setText(arraylist.get(position).getAdTitle());
        disc.setText(arraylist.get(position).getAdDisc());
        placeholder.setImageResource(arraylist.get(position).getImage());
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
}
