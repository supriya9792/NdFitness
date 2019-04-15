package com.ndfitnessplus.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ndfitnessplus.Model.BottomMenuList;
import com.ndfitnessplus.Model.TopMenuList;
import com.ndfitnessplus.R;

import java.util.ArrayList;

public class BottomMenuAdapter extends RecyclerView.Adapter<BottomMenuAdapter.MyViewHolder> {

    private ArrayList<BottomMenuList> dataSet;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        ImageButton imageViewIcon;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.menuname);
            this.imageViewIcon = (ImageButton) itemView.findViewById(R.id.icon);
        }
    }

    public BottomMenuAdapter(ArrayList<BottomMenuList> data) {
        this.dataSet = data;
    }

    @Override
    public BottomMenuAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bottom_menu_list, parent, false);



        BottomMenuAdapter.MyViewHolder myViewHolder = new BottomMenuAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final BottomMenuAdapter.MyViewHolder holder, final int listPosition) {

        TextView textViewName = holder.textViewName;
        ImageButton imageView = holder.imageViewIcon;

        textViewName.setText(dataSet.get(listPosition).getName());
        imageView.setImageResource(dataSet.get(listPosition).getImage());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
