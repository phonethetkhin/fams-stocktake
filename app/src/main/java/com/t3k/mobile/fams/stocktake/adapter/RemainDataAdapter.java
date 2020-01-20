package com.t3k.mobile.fams.stocktake.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.t3k.mobile.fams.stocktake.R;
import com.t3k.mobile.fams.stocktake.model.AssetBean;
import com.t3k.mobile.fams.stocktake.ui.RemarkActivity;

import java.util.List;

public class RemainDataAdapter extends RecyclerView.Adapter<RemainDataAdapter.ViewHolder> {
    private Context context;
    private List<AssetBean> assetList;

    public RemainDataAdapter(Context context, List<AssetBean> assetList) {
        this.context = context;
        this.assetList = assetList;
    }

    @NonNull
    @Override
    public RemainDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.known_asset_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final AssetBean asset = assetList.get(position);
        holder.txtKnownAssetNo.setText(asset.getFaNumber());
        if (asset.getItemName() != null && asset.getCategory() != null && asset.getCostCenter() != null) {
            holder.txtKnownItemName.setText(asset.getItemName());
            holder.txtKnownCategory.setText(asset.getCategory());
            holder.txtKnownCostCenter.setText(asset.getCostCenter());
        } else {
            holder.txtKnownItemName.setVisibility(View.GONE);
            holder.txtKnownCategory.setVisibility(View.GONE);
            holder.txtKnownCostCenter.setVisibility(View.GONE);
        }

        if (asset.getScannedStatus() == 1) {
            holder.imgKnownScan.setVisibility(View.VISIBLE);
        }
        if (asset.getRemark() != null) {
            holder.imgKnownDescription.setVisibility(View.VISIBLE);
        }
        if (asset.getImagePath() != null) {
            holder.imgKnownEvidence.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RemarkActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra("primary_id", asset.getId());
            intent.putExtra("faId", asset.getAssetId());
            intent.putExtra("locationId", asset.getLocationId());
            intent.putExtra("faNo", asset.getFaNumber());
            intent.putExtra("item_name", asset.getItemName());
            intent.putExtra("condition", asset.getCondition());
            intent.putExtra("cost_center", asset.getCostCenter());
            intent.putExtra("category", asset.getCategory());
            intent.putExtra("brand", asset.getBrand());
            intent.putExtra("model", asset.getModel());
            intent.putExtra("locate_found_id", asset.getLocationFoundId());
            intent.putExtra("unknown", asset.getUnknown());
            intent.putExtra("remark", asset.getRemark());
            intent.putExtra("taken", asset.getTaken());
            intent.putExtra("scanned", asset.getScannedStatus());
            intent.putExtra("path", asset.getImagePath());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return assetList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtKnownAssetNo, txtKnownItemName, txtKnownCategory, txtKnownCostCenter;
        ImageView imgKnownScan, imgKnownDescription, imgKnownEvidence;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtKnownAssetNo = itemView.findViewById(R.id.txt_known_asset_no);
            txtKnownItemName = itemView.findViewById(R.id.txt_known_item_name);
            txtKnownCategory = itemView.findViewById(R.id.txt_known_category);
            txtKnownCostCenter = itemView.findViewById(R.id.txt_known_cost_center);
            imgKnownScan = itemView.findViewById(R.id.img_known_scan);
            imgKnownDescription = itemView.findViewById(R.id.img_known_description);
            imgKnownEvidence = itemView.findViewById(R.id.img_known_evidence);
        }
    }
}
