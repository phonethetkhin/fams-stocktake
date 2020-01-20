package com.t3k.mobile.fams.stocktake.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.t3k.mobile.fams.stocktake.R;
import com.t3k.mobile.fams.stocktake.adapter.UnknownDataAdapter;
import com.t3k.mobile.fams.stocktake.db.DBHelper;
import com.t3k.mobile.fams.stocktake.model.AssetBean;
import com.t3k.mobile.fams.stocktake.utils.Event;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnknownActivity extends AppCompatActivity {
    private DBHelper helper;
    private List<AssetBean> assetList;
    private RecyclerView unknownRecycler;
    private UnknownDataAdapter unknownDataAdapter;

    private SharedPreferences prefs;
    private String prefName = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unknown);
        helper = new DBHelper(this);
        unknownRecycler = findViewById(R.id.recycler_unknown_list);
        getUnknown();
        EventBus.getDefault().register(this);
    }

    private void getUnknown() {
        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        int locationId = prefs.getInt("locationId", 0);

        if (locationId == -1) {

            assetList = helper.getUnKnown();

            Collections.sort(assetList, (fa1, fa2) -> {
                if (fa1.getFaNumber() != null && fa2.getFaNumber() != null) {
                    return fa1.getFaNumber().compareTo(fa2.getFaNumber());
                }
                return 0;
            });

            unknownDataAdapter = new UnknownDataAdapter(getApplicationContext(), assetList);
            unknownRecycler.setAdapter(unknownDataAdapter);
            unknownRecycler.setLayoutManager(new LinearLayoutManager(getApplication()));
            unknownDataAdapter.notifyDataSetChanged();

        } else {
            assetList = helper.getUnKnownByLocation(locationId);
            if (assetList.size() != 0) {

                Collections.sort(assetList, (fa1, fa2) -> {
                    if (fa1.getStockTakeTime() != null && fa2.getStockTakeTime() != null) {
                        return fa2.getStockTakeTime().compareTo(fa1.getStockTakeTime());
                    }
                    return 0;
                });

                unknownDataAdapter = new UnknownDataAdapter(getApplicationContext(), assetList);
                unknownRecycler.setAdapter(unknownDataAdapter);
                unknownRecycler.setLayoutManager(new LinearLayoutManager(getApplication()));
                unknownDataAdapter.notifyDataSetChanged();

            } else if (assetList.size() == 0) {

                List<AssetBean> assetEmptyList = new ArrayList<>();
                unknownDataAdapter = new UnknownDataAdapter(getApplicationContext(), assetEmptyList);
                unknownRecycler.setAdapter(unknownDataAdapter);
                unknownRecycler.setLayoutManager(new LinearLayoutManager(getApplication()));
                unknownDataAdapter.notifyDataSetChanged();

            }
        }
    }

    @Subscribe
    public void onEvent(Event event) {

        if (event.equals(Event.Request_Data)) {

            prefs = getSharedPreferences(prefName, MODE_PRIVATE);
            int locationId = prefs.getInt("locationId", 0);

            if (locationId == -1) {

                assetList = helper.getUnKnown();

                Collections.sort(assetList, (fa1, fa2) -> {
                    if (fa1.getStockTakeTime() != null && fa2.getStockTakeTime() != null) {
                        return fa2.getStockTakeTime().compareTo(fa1.getStockTakeTime());
                    }
                    return 0;
                });

                unknownDataAdapter = new UnknownDataAdapter(getApplicationContext(), assetList);
                unknownRecycler.setAdapter(unknownDataAdapter);
                unknownRecycler.setLayoutManager(new LinearLayoutManager(getApplication()));
                unknownDataAdapter.notifyDataSetChanged();

            } else {

                assetList = helper.getUnKnownByLocation(locationId);

                if (assetList.size() != 0) {

                    Collections.sort(assetList, (fa1, fa2) -> {
                        if (fa1.getStockTakeTime() != null && fa2.getStockTakeTime() != null) {
                            return fa2.getStockTakeTime().compareTo(fa1.getStockTakeTime());
                        }
                        return 0;
                    });

                    unknownDataAdapter = new UnknownDataAdapter(getApplicationContext(), assetList);
                    unknownRecycler.setAdapter(unknownDataAdapter);
                    unknownRecycler.setLayoutManager(new LinearLayoutManager(getApplication()));
                    unknownDataAdapter.notifyDataSetChanged();

                } else if (assetList.size() == 0) {

                    List<AssetBean> assetEmptyList = new ArrayList<>();

                    unknownDataAdapter = new UnknownDataAdapter(getApplicationContext(), assetEmptyList);
                    unknownRecycler.setAdapter(unknownDataAdapter);
                    unknownRecycler.setLayoutManager(new LinearLayoutManager(getApplication()));
                    unknownDataAdapter.notifyDataSetChanged();

                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        this.getParent().onBackPressed();
        finish();
    }
}
