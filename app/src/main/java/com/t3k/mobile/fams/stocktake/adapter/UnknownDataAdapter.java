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
import com.t3k.mobile.fams.stocktake.ui.UnknownRemarkActivity;

import java.util.List;

public class UnknownDataAdapter extends RecyclerView.Adapter<UnknownDataAdapter.ViewHolder> {
    private Context context;
    private List<AssetBean> assetBeanList;

    public UnknownDataAdapter(Context context, List<AssetBean> assetBeanList) {
        this.context = context;
        this.assetBeanList = assetBeanList;
    }

    @NonNull
    @Override
    public UnknownDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.unknown_asset_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        final AssetBean asset = assetBeanList.get(i);
        holder.txtAssetNumber.setText(asset.getFaNumber());
        if (asset.getScannedStatus() != 1) {
            holder.imgUnknownScan.setVisibility(View.VISIBLE);
        }
        if (asset.getRemark() != null) {
            holder.imgUnknownDescription.setVisibility(View.VISIBLE);
        }
        if (asset.getImagePath() != null) {
            holder.imgUnknownEvidence.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), UnknownRemarkActivity.class);
            intent.putExtra("primary_id", asset.getId());
            intent.putExtra("faId", asset.getAssetId());
            intent.putExtra("locationId", asset.getLocationId());
            intent.putExtra("faNo", asset.getFaNumber());
            intent.putExtra("remark", asset.getRemark());
            intent.putExtra("scanned", asset.getScannedStatus());
            intent.putExtra("path", asset.getImagePath());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return assetBeanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtAssetNumber;
        private ImageView imgUnknownScan, imgUnknownDescription, imgUnknownEvidence;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtAssetNumber = itemView.findViewById(R.id.txt_unknown_asset_number);
            imgUnknownScan = itemView.findViewById(R.id.img_unknown_scan);
            imgUnknownDescription = itemView.findViewById(R.id.img_unknown_description);
            imgUnknownEvidence = itemView.findViewById(R.id.img_unknown_evidence);

        }
    }
}
