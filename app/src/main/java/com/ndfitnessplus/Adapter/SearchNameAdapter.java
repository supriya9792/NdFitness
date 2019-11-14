package com.ndfitnessplus.Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;


import com.ndfitnessplus.Model.Search_list;
import com.ndfitnessplus.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by admin on 10/25/2017.
 */

public class SearchNameAdapter extends ArrayAdapter implements Filterable {

    private LayoutInflater inflater = null;
    private Activity activity;
    private List<Search_list> itemList;
    private Context context;
    private ArrayList<Search_list> arraylist,tempsearch,suggestions;

    public SearchNameAdapter(Context context, ArrayList<Search_list> itemList) {
        super(context,android.R.layout.simple_list_item_1, itemList);
        this.itemList = itemList;
        this.context = context;
        this.arraylist = itemList;
        //this.arraylist.addAll(itemList);
       tempsearch = new ArrayList<Search_list>(itemList);
        suggestions=new ArrayList<Search_list>(itemList);

    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Search_list getItem(int position) {
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
            convertView = inflater.inflate(R.layout.search_list, null);

        final Search_list model = itemList.get(position);
        TextView employeeName = (TextView) convertView.findViewById(R.id.tv_Name);



        employeeName.setText(model.getCustName());


        return convertView;


    }

    @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                if (constraint != null) {
                    suggestions.clear();
                    try {
                        for (Search_list search_list : tempsearch) {
                            if (search_list.getCustName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                                suggestions.add(search_list);
                            }
                            //get data from the web
                            String term = constraint.toString();
                            // arraylist = new DownloadCountry().execute(term).get();
                        }
                    } catch (Exception e) {
                        Log.d("HUS", "EXCEPTION " + e);
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = suggestions;
                    filterResults.count = suggestions.size();
                    return filterResults;
                } else {
                    return new FilterResults();
                }
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                ArrayList<Search_list> filterList = (ArrayList<Search_list>) results.values;
                if (results != null && results.count > 0) {
                    clear();
                    synchronized (filterList) {
                        try {
                            for (Search_list people : filterList) {
                                add(people);
                                notifyDataSetChanged();
                            }
                        }catch (Exception e){
                            Log.d("HUS", "EXCEPTION " + e);
                        }

                    }
                }
            }
        };
        return myFilter;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        itemList.clear();
        if (charText.length() == 0) {
            itemList.addAll(arraylist);
        } else {
            for (Search_list wp : arraylist) {
                if (wp.getCustName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    itemList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }




}
