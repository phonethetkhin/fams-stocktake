package com.t3k.mobile.fams.stocktake.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.t3k.mobile.fams.stocktake.R;
import com.t3k.mobile.fams.stocktake.db.DBHelper;
import com.t3k.mobile.fams.stocktake.model.LocationsBean;
import com.t3k.mobile.fams.stocktake.utils.MyApplication;

import java.util.List;

public class SelectedLocationAdapter extends RecyclerView.Adapter<SelectedLocationAdapter.ViewHolder> {
    private Context context;
    private List<LocationsBean> locationsBeanList;
    private DBHelper helper;

    public SelectedLocationAdapter(Context context, List<LocationsBean> locationsBeanList) {
        this.context = context;
        this.locationsBeanList = locationsBeanList;
    }

    @NonNull
    @Override
    public SelectedLocationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_location_item, parent, false);
        helper = new DBHelper(MyApplication.getContext());
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedLocationAdapter.ViewHolder viewHolder, int position) {
        final LocationsBean locationsBean = locationsBeanList.get(position);
        viewHolder.txtLocationCode.setText(locationsBean.getCode());
        viewHolder.txtLocationName.setText(locationsBean.getName());
        int assetCount = helper.getAssetCountByLocation(locationsBean.getLocationId());
        if (locationsBean.getDownload() == -1) {
            viewHolder.downloadProgress.setVisibility(View.VISIBLE);
            viewHolder.txtAssetCount.setVisibility(View.GONE);
            viewHolder.viewLocation.setVisibility(View.GONE);
            viewHolder.txtLocationCheck.setVisibility(View.GONE);
        } else if (locationsBean.getDownload() != 0) {
            viewHolder.downloadProgress.setVisibility(View.GONE);
            viewHolder.txtAssetCount.setVisibility(View.VISIBLE);
            viewHolder.viewLocation.setVisibility(View.VISIBLE);
            viewHolder.txtAssetCount.setText(String.valueOf(assetCount));
            viewHolder.txtLocationCheck.setVisibility(View.VISIBLE);
            viewHolder.txtLocationCheck.setText("SUCCESS");
            viewHolder.txtLocationCheck.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        } else if (locationsBean.getDownload() == 0) {
            viewHolder.downloadProgress.setVisibility(View.GONE);
            viewHolder.txtAssetCount.setVisibility(View.GONE);
            viewHolder.viewLocation.setVisibility(View.GONE);
            viewHolder.txtLocationCheck.setVisibility(View.VISIBLE);
            viewHolder.txtLocationCheck.setText("NO DATA");
            viewHolder.txtLocationCheck.setTextColor(context.getResources().getColor(R.color.colorRed));
        } else {
            viewHolder.downloadProgress.setVisibility(View.GONE);
            viewHolder.txtAssetCount.setVisibility(View.GONE);
            viewHolder.viewLocation.setVisibility(View.GONE);
            viewHolder.txtLocationCheck.setVisibility(View.VISIBLE);
            viewHolder.txtLocationCheck.setText("NO DATA");
            viewHolder.txtLocationCheck.setTextColor(context.getResources().getColor(R.color.colorRed));
        }
    }

    @Override
    public int getItemCount() {
        return locationsBeanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtLocationCode, txtLocationName, txtLocationCheck, txtAssetCount;
        private ProgressBar downloadProgress;
        private View viewLocation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtLocationCode = itemView.findViewById(R.id.txt_location_code);
            txtLocationName = itemView.findViewById(R.id.txt_location_name);
            txtLocationCheck = itemView.findViewById(R.id.txt_check_location);
            txtAssetCount = itemView.findViewById(R.id.download_asset_count);
            downloadProgress = itemView.findViewById(R.id.pBar_location);
            viewLocation = itemView.findViewById(R.id.view_location);
        }
    }
}
