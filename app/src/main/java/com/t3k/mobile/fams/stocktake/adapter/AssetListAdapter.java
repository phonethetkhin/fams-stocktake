package com.t3k.mobile.fams.stocktake.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.t3k.mobile.fams.stocktake.R;
import com.t3k.mobile.fams.stocktake.model.AssetBean;

import java.util.List;

public class AssetListAdapter extends RecyclerView.Adapter<AssetListAdapter.ViewHolder> {
    private Context context;
    private List<AssetBean> assetBeanList;

    public AssetListAdapter(Context context, List<AssetBean> assetBeanList) {
        this.context = context;
        this.assetBeanList = assetBeanList;
    }

    @NonNull
    @Override
    public AssetListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.asset_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssetListAdapter.ViewHolder holder, int position) {
        final AssetBean assets = assetBeanList.get(position);
        holder.txtAssetNo.setText(assets.getFaNumber());
        if (assets.getItemName() != null && assets.getCategory() != null && assets.getCostCenter() != null) {
            holder.txtItemName.setText(assets.getItemName());
            holder.txtCategory.setText(assets.getCategory());
            holder.txtCostCenter.setText(assets.getCostCenter());
        } else {
            holder.txtItemName.setVisibility(View.GONE);
            holder.txtCategory.setVisibility(View.GONE);
            holder.txtCostCenter.setVisibility(View.GONE);
        }

        if (assets.getScannedStatus() == 0 && assets.getUnknown() == 0 && assets.getTaken() == 0) {
            holder.txtStatus.setText(R.string.txt_not_taken);
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.textColor));
        } else if (assets.getScannedStatus() == 1 && assets.getUnknown() == 0) {
            holder.txtStatus.setText(R.string.txt_scanned);
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        } else if (assets.getScannedStatus() == 0 && assets.getUnknown() == 0 && assets.getRemark() != null && assets.getTaken() == 1) {
            holder.txtStatus.setText(R.string.txt_noted);
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        } else if (assets.getScannedStatus() == 1 && assets.getUnknown() == 1) {
            holder.txtStatus.setText(R.string.txt_unknown);
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.colorRed));
        }
    }

    @Override
    public int getItemCount() {
        return assetBeanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtAssetNo, txtItemName, txtCategory, txtCostCenter, txtStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtAssetNo = itemView.findViewById(R.id.txt_asset_number);
            txtItemName = itemView.findViewById(R.id.txt_item_name);
            txtCategory = itemView.findViewById(R.id.txt_category);
            txtCostCenter = itemView.findViewById(R.id.txt_cost_center);
            txtStatus = itemView.findViewById(R.id.txt_asset_status);
        }
    }
}
