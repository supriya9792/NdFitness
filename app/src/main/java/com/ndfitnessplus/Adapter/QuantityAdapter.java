package com.ndfitnessplus.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ndfitnessplus.Model.Spinner_List;
import com.ndfitnessplus.R;

import java.util.ArrayList;
import java.util.List;

public class QuantityAdapter extends BaseAdapter {

    private LayoutInflater inflater = null;
    private Activity activity;
    private List<Spinner_List> itemList;
    private Context context;
    private ArrayList<Spinner_List> arraylist;

    public QuantityAdapter(Context context, ArrayList<Spinner_List> itemList) {
        this.itemList = itemList;
        this.context = context;
        this.arraylist = new ArrayList<Spinner_List>();
        this.arraylist.addAll(itemList);
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Spinner_List getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.quantity_list, null);

        final Spinner_List model = itemList.get(position);

        TextView employeeName = (TextView) convertView.findViewById(R.id.tv_Name);
        View parentview = (View) convertView.findViewById(R.id.layout);

        employeeName.setText(model.getName());

        return convertView;

    }

}