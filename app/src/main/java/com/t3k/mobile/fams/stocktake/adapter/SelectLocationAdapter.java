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
import com.t3k.mobile.fams.stocktake.model.LocationsBean;

import java.util.List;

public class SelectLocationAdapter extends RecyclerView.Adapter<SelectLocationAdapter.ViewHolder> {
    private Context context;
    private List<LocationsBean> locationsBeanList;

    public SelectLocationAdapter(Context context, List<LocationsBean> locationsBeanList) {
        this.context = context;
        this.locationsBeanList = locationsBeanList;
    }

    @NonNull
    @Override
    public SelectLocationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_location_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectLocationAdapter.ViewHolder viewHolder, int position) {
        final LocationsBean locationsBean = locationsBeanList.get(position);
        viewHolder.txtLocationCode.setText(locationsBean.getCode());
        viewHolder.txtLocationName.setText(locationsBean.getName());
        viewHolder.downloadProgress.setVisibility(View.GONE);
        viewHolder.txtAssetCount.setVisibility(View.GONE);
        viewHolder.viewLocation.setVisibility(View.GONE);
        viewHolder.txtLocationCheck.setVisibility(View.GONE);
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
