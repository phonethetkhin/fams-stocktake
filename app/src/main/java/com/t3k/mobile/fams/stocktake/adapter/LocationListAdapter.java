package com.t3k.mobile.fams.stocktake.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.t3k.mobile.fams.stocktake.R;
import com.t3k.mobile.fams.stocktake.model.LocationsBean;

import java.util.List;

public class LocationListAdapter extends BaseAdapter {
    private List<LocationsBean> locationList;
    private Context context;

    public LocationListAdapter(List<LocationsBean> locationList, Context context) {
        this.locationList = locationList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return locationList.size();
    }

    @Override
    public Object getItem(int position) {
        return locationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        TextView txtLocationCode, txtLocationName;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.location_list_item, parent, false);
        }
        LocationsBean locations = (LocationsBean) getItem(position);
        txtLocationCode = view.findViewById(R.id.txt_locate_code);
        txtLocationName = view.findViewById(R.id.txt_locate_name);
        txtLocationCode.setText(locations.getCode());
        txtLocationName.setText(locations.getName());
        return view;
    }
}
