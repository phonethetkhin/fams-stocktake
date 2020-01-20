package com.t3k.mobile.fams.stocktake.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.t3k.mobile.fams.stocktake.R;
import com.t3k.mobile.fams.stocktake.adapter.RemainDataAdapter;
import com.t3k.mobile.fams.stocktake.db.DBHelper;
import com.t3k.mobile.fams.stocktake.model.AssetBean;
import com.t3k.mobile.fams.stocktake.utils.Event;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TakenActivity extends AppCompatActivity {

    private DBHelper helper;
    private List<AssetBean> assetList;
    private RecyclerView takenRecycler;
    private RemainDataAdapter remainDataAdapter;

    private SharedPreferences prefs;
    private String prefName = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taken);
        helper = new DBHelper(this);
        takenRecycler = findViewById(R.id.recycler_taken_list);
        getTaken();
        EventBus.getDefault().register(this);
    }

    private void getTaken() {
        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        int locationId = prefs.getInt("locationId", 0);
        if (locationId == -1) {

            assetList = helper.getTaken();

            Collections.sort(assetList, (fa1, fa2) -> {
                if (fa1.getFaNumber() != null && fa2.getFaNumber() != null) {
                    return fa1.getFaNumber().compareTo(fa2.getFaNumber());
                }
                return 0;
            });

            remainDataAdapter = new RemainDataAdapter(getApplicationContext(), assetList);
            takenRecycler.setAdapter(remainDataAdapter);
            takenRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            remainDataAdapter.notifyDataSetChanged();

        } else {
            assetList = helper.getTakenByLocation(locationId);
            if (assetList.size() != 0) {

                Collections.sort(assetList, (fa1, fa2) -> {
                    if (fa1.getStockTakeTime() != null && fa2.getStockTakeTime() != null) {
                        return fa2.getStockTakeTime().compareTo(fa1.getStockTakeTime());
                    }
                    return 0;
                });

                remainDataAdapter = new RemainDataAdapter(getApplicationContext(), assetList);
                takenRecycler.setAdapter(remainDataAdapter);
                takenRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                remainDataAdapter.notifyDataSetChanged();

            } else if (assetList.size() == 0) {

                List<AssetBean> assetEmptyList = new ArrayList<>();
                remainDataAdapter = new RemainDataAdapter(getApplicationContext(), assetEmptyList);
                takenRecycler.setAdapter(remainDataAdapter);
                takenRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                remainDataAdapter.notifyDataSetChanged();

            }
        }
    }

    @Subscribe
    public void onEvent(Event event) {

        if (event.equals(Event.Request_Data)) {

            prefs = getSharedPreferences(prefName, MODE_PRIVATE);
            int locationId = prefs.getInt("locationId", 0);

            if (locationId == -1) {

                assetList = helper.getTaken();

                Collections.sort(assetList, (fa1, fa2) -> {
                    if (fa1.getStockTakeTime() != null && fa2.getStockTakeTime() != null) {
                        return fa2.getStockTakeTime().compareTo(fa1.getStockTakeTime());
                    }
                    return 0;
                });

                remainDataAdapter = new RemainDataAdapter(getApplicationContext(), assetList);
                takenRecycler.setAdapter(remainDataAdapter);
                takenRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                remainDataAdapter.notifyDataSetChanged();

            } else {

                assetList = helper.getTakenByLocation(locationId);

                if (assetList.size() != 0) {

                    Collections.sort(assetList, (fa1, fa2) -> {
                        if (fa1.getStockTakeTime() != null && fa2.getStockTakeTime() != null) {
                            return fa2.getStockTakeTime().compareTo(fa1.getStockTakeTime());
                        }
                        return 0;
                    });

                    remainDataAdapter = new RemainDataAdapter(getApplicationContext(), assetList);
                    takenRecycler.setAdapter(remainDataAdapter);
                    takenRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    remainDataAdapter.notifyDataSetChanged();

                } else {
                    assetList.size();

                    List<AssetBean> assetEmptyList = new ArrayList<>();

                    remainDataAdapter = new RemainDataAdapter(getApplicationContext(), assetEmptyList);
                    takenRecycler.setAdapter(remainDataAdapter);
                    takenRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    remainDataAdapter.notifyDataSetChanged();

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
