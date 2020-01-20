package com.t3k.mobile.fams.stocktake.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.t3k.mobile.fams.stocktake.R;
import com.t3k.mobile.fams.stocktake.db.DBHelper;
import com.t3k.mobile.fams.stocktake.ui.SelectUploadLocationActivity;
import com.t3k.mobile.fams.stocktake.ui.StockTakingActivity;
import com.t3k.mobile.fams.stocktake.utils.HomeListener;


public class HomeFragment extends Fragment implements View.OnClickListener {

    DBHelper helper;
    HomeListener mListener;
    CardView cardDownload, cardStockTake, cardUpload;

    public HomeFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof HomeListener) {
            mListener = (HomeListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        helper = new DBHelper(getActivity());

        cardDownload = view.findViewById(R.id.download_card);
        cardStockTake = view.findViewById(R.id.stock_take_card);
        cardUpload = view.findViewById(R.id.upload_card);

        cardDownload.setOnClickListener(this);
        cardStockTake.setOnClickListener(this);
        cardUpload.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.download_card:
                if (helper.getLocationCount() != 0) {
                    if (mListener != null) mListener.onDownload();
                } else {
                    Toast.makeText(getContext(), "Locations are not exists!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.stock_take_card:
                Intent intentStockTake = new Intent(getActivity(), StockTakingActivity.class);
                startActivity(intentStockTake);
                break;
            case R.id.upload_card:
                Intent intentUpload = new Intent(getActivity(), SelectUploadLocationActivity.class);
                startActivity(intentUpload);
                break;
        }
    }
}
