package com.t3k.mobile.fams.stocktake.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.t3k.mobile.fams.stocktake.R;
import com.t3k.mobile.fams.stocktake.model.AssetBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UploadedListAdapter extends RecyclerView.Adapter<UploadedListAdapter.ViewHolder> {
    private List<AssetBean> itemList;
    Context context;

    public UploadedListAdapter(List<AssetBean> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public UploadedListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.uploaded_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UploadedListAdapter.ViewHolder holder, int position) {
        final AssetBean item = itemList.get(position);

        holder.faNo.setText(item.getFaNumber());
        holder.itemName.setText(item.getItemName());
        holder.category.setText(item.getCategory());
        if (item.getCondition().equals("OPR")) {
            holder.condition.setText(R.string.txt_opr);
        }
        if (item.getStockTakeTime() != null) {
            holder.scanTime.setText(item.getStockTakeTime());
        } else {
            holder.scanTime.setText(" ----:----:---- ");
        }
        if (item.getScannedStatus() != 0) {
            holder.imgScan.setVisibility(View.VISIBLE);
        } else {
            holder.imgScan.setVisibility(View.GONE);
        }
        if (item.getRemark() != null) {
            holder.imgDescription.setVisibility(View.VISIBLE);
        } else {
            holder.imgDescription.setVisibility(View.GONE);
        }
        if (item.getImagePath() != null) {
            holder.imgImage.setVisibility(View.VISIBLE);
        } else {
            holder.imgImage.setVisibility(View.GONE);
        }
        String uploadTime = convertStringDateToAnotherStringDate(item.getUploadTime(), "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd hh:mm:ss a");
        holder.uploadTime.setText(uploadTime);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView faNo, itemName, category, condition, uploadTime, scanTime;
        ImageView imgScan, imgDescription, imgImage;

        public ViewHolder(@NonNull View view) {
            super(view);
            faNo = view.findViewById(R.id.uploaded_number);
            itemName = view.findViewById(R.id.uploaded_name);
            category = view.findViewById(R.id.uploaded_category);
            condition = view.findViewById(R.id.uploaded_status);
            uploadTime = view.findViewById(R.id.upload_time);
            scanTime = view.findViewById(R.id.scanned_time);
            imgScan = view.findViewById(R.id.scan_icon);
            imgDescription = view.findViewById(R.id.remark_icon);
            imgImage = view.findViewById(R.id.image_icon);
        }
    }

    private String convertStringDateToAnotherStringDate(String stringDate, String stringDateFormat, String returnDateFormat) {

        try {
            Date date = new SimpleDateFormat(stringDateFormat).parse(stringDate);
            String returnDate = new SimpleDateFormat(returnDateFormat).format(date);
            return returnDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

}
