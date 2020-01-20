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

public class RemainActivity extends AppCompatActivity{
    List<AssetBean> assetList = new ArrayList<>();

    DBHelper helper;
    RemainDataAdapter adapter;
    RecyclerView recyclerView;

    private SharedPreferences prefs;
    private String prefName = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remain);
        helper = new DBHelper(this);

        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        int locationId = prefs.getInt("locationId", 0);

        if (locationId == -1) {

            assetList = helper.getRemain();

            Collections.sort(assetList, (fa1, fa2) -> {
                if (fa1.getFaNumber() != null && fa2.getFaNumber() != null) {
                    return fa1.getFaNumber().compareTo(fa2.getFaNumber());
                }
                return 0;
            });

            recyclerView = findViewById(R.id.recycler_remain_list);
            adapter = new RemainDataAdapter(getApplicationContext(), assetList);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));

        } else {

            assetList = helper.getRemainByLocation(locationId);
            if (assetList.size() != 0) {

                Collections.sort(assetList, (fa1, fa2) -> {
                    if (fa1.getFaNumber() != null && fa2.getFaNumber() != null) {
                        return fa1.getFaNumber().compareTo(fa2.getFaNumber());
                    }
                    return 0;
                });

                recyclerView = findViewById(R.id.recycler_remain_list);
                adapter = new RemainDataAdapter(getApplicationContext(), assetList);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
                adapter.notifyDataSetChanged();

            } else if (assetList.size() == 0) {

                List<AssetBean> assetEmptyList = new ArrayList<>();
                recyclerView = findViewById(R.id.recycler_remain_list);
                adapter = new RemainDataAdapter(getApplicationContext(), assetEmptyList);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
                adapter.notifyDataSetChanged();

            }
        }

        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onEvent(Event event) {
        if (event.equals(Event.Request_Data)) {
            prefs = getSharedPreferences(prefName, MODE_PRIVATE);
            int locationId = prefs.getInt("locationId", 0);

            if (locationId == -1) {

                assetList = helper.getRemain();

                Collections.sort(assetList, (fa1, fa2) -> {
                    if (fa1.getFaNumber() != null && fa2.getFaNumber() != null) {
                        return fa1.getFaNumber().compareTo(fa2.getFaNumber());
                    }
                    return 0;
                });

                recyclerView = findViewById(R.id.recycler_remain_list);
                adapter = new RemainDataAdapter(getApplicationContext(), assetList);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
                adapter.notifyDataSetChanged();

            } else {

                assetList = helper.getRemainByLocation(locationId);

                if (assetList.size() != 0) {

                    Collections.sort(assetList, (fa1, fa2) -> {
                        if (fa1.getFaNumber() != null && fa2.getFaNumber() != null) {
                            return fa1.getFaNumber().compareTo(fa2.getFaNumber());
                        }
                        return 0;
                    });

                    recyclerView = findViewById(R.id.recycler_remain_list);
                    adapter = new RemainDataAdapter(getApplicationContext(), assetList);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
                    adapter.notifyDataSetChanged();

                } else if (assetList.size() == 0) {

                    List<AssetBean> assetEmptyList = new ArrayList<>();
                    recyclerView = findViewById(R.id.recycler_remain_list);
                    adapter = new RemainDataAdapter(getApplicationContext(), assetEmptyList);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
                    adapter.notifyDataSetChanged();

                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        this.getParent().onBackPressed();
        finish();
    }
}
