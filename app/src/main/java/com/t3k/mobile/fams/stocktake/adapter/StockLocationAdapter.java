package com.t3k.mobile.fams.stocktake.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.t3k.mobile.fams.stocktake.R;
import com.t3k.mobile.fams.stocktake.db.DBHelper;
import com.t3k.mobile.fams.stocktake.model.LocationsBean;
import com.t3k.mobile.fams.stocktake.utils.MyApplication;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class StockLocationAdapter extends RecyclerView.Adapter<StockLocationAdapter.ViewHolder> {
    private Context context;
    private List<LocationsBean> locationList;
    private DBHelper helper;
    private int selectedPosition = -1;
    private SharedPreferences prefs;
    private String prefSelector = "selector";

    public StockLocationAdapter(Context context, List<LocationsBean> locationList) {
        this.context = context;
        this.locationList = locationList;
    }

    @NonNull
    @Override
    public StockLocationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_location_item, parent, false);
        helper = new DBHelper(MyApplication.getContext());
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockLocationAdapter.ViewHolder holder, int position) {
        final LocationsBean locationsBean = locationList.get(position);
        String name = locationsBean.getCode() + " - " + locationsBean.getName();

        int assetCount = helper.getAssetCountByLocation(locationsBean.getLocationId());
        int remainCount = helper.getRemainCountByLocation(locationsBean.getLocationId());
        int takenCount = helper.getTakenCountByLocation(locationsBean.getLocationId());
        int unknownCount = helper.getUnknownCountByLocation(locationsBean.getLocationId());

        holder.txtLocate.setText(name);
        holder.txtTotalCount.setText(String.valueOf(assetCount));
        holder.txtRemainCount.setText(String.valueOf(remainCount));
        holder.txtTakenCount.setText(String.valueOf(takenCount));
        holder.txtUnknownCount.setText(String.valueOf(unknownCount));
        holder.btnRadioBtn.setTag(position);
        holder.itemView.setTag(position);

        if (position == selectedPosition) {
            holder.btnRadioBtn.setChecked(true);
        } else {
            holder.btnRadioBtn.setChecked(false);
        }

        holder.btnRadioBtn.setOnClickListener(onStateChangedListener(holder.btnRadioBtn, position));
        holder.btnRadioBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                prefs = context.getSharedPreferences(prefSelector, MODE_PRIVATE);
                SharedPreferences.Editor selectEditor = prefs.edit();
                selectEditor.clear();
                selectEditor.commit();
                selectEditor.putInt("select_locationId", locationsBean.getLocationId());
                selectEditor.apply();
            } else {
                holder.btnRadioBtn.setChecked(false);
            }
        });
    }

    private View.OnClickListener onStateChangedListener(final RadioButton radioButton, final int position) {
        return v -> {
            if (radioButton.isChecked()) {
                selectedPosition = position;
            } else {
                selectedPosition = -1;
            }
            notifyDataSetChanged();
        };
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtLocate, txtTotalCount, txtRemainCount, txtTakenCount, txtUnknownCount;
        private RadioButton btnRadioBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtLocate = itemView.findViewById(R.id.txt_locate);
            txtTotalCount = itemView.findViewById(R.id.txt_total_asset_count);
            txtRemainCount = itemView.findViewById(R.id.txt_remain_asset_count);
            txtTakenCount = itemView.findViewById(R.id.txt_taken_asset_count);
            txtUnknownCount = itemView.findViewById(R.id.txt_unknown_asset_count);
            btnRadioBtn = itemView.findViewById(R.id.radio_select_locate);

        }
    }
}
