package com.t3k.mobile.fams.stocktake.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.t3k.mobile.fams.stocktake.R;
import com.t3k.mobile.fams.stocktake.adapter.StockLocationAdapter;
import com.t3k.mobile.fams.stocktake.db.DBHelper;
import com.t3k.mobile.fams.stocktake.model.DownloadInfo;
import com.t3k.mobile.fams.stocktake.model.LocationsBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StockTakingActivity extends AppCompatActivity {
    private DBHelper helper;
    private List<LocationsBean> locationsList;
    private Toolbar toolbar;
    private StockLocationAdapter stockLocationAdapter;

    private int locationId;
    private SharedPreferences prefs;
    private String prefSelector = "selector";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_taking);
        helper = new DBHelper(this);
        TextView txtTitleLocation = findViewById(R.id.txt_location_title);
        RecyclerView recyclerLocation = findViewById(R.id.recycler_stock_locate);
        Button btnStockTake = findViewById(R.id.btn_go_stock_take);

        toolbar = findViewById(R.id.toolbar);
        setupToolbar();

        txtTitleLocation.setText(R.string.title_downloaded_location);

        locationsList = new ArrayList<>();
        stockLocationAdapter = new StockLocationAdapter(getApplicationContext(), locationsList);
        recyclerLocation.setAdapter(stockLocationAdapter);
        recyclerLocation.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        getStockLocation();
    }


    private void getStockLocation() {
        if (helper.getInfoCount() != 0) {
            int id = 1;
            final DownloadInfo info = helper.getDownloadInfoById(id);

            final ArrayList<LocationsBean> locations = new ArrayList<>();
            ArrayList<String> items = new ArrayList<>(Arrays.asList(info.getLocationId().split(",")));

            for (int i = 0; i < items.size(); i++) {

                int locationId = Integer.parseInt(items.get(i));
                if (helper.getAssetCountByLocation(locationId) != 0) {
                    LocationsBean location = helper.getLocationById(locationId);
                    locations.add(location);
                }
            }

            locationsList.clear();
            locationsList.addAll(locations);
            stockLocationAdapter.notifyDataSetChanged();
        }
    }

    public void GoForStockTake(View view) {
        prefs = getSharedPreferences(prefSelector, MODE_PRIVATE);
        locationId = prefs.getInt("select_locationId", 0);
        boolean cancel = false;
        if (locationId <= 0) {
            cancel = true;
        }
        if (cancel) {
            Toast.makeText(getApplicationContext(), "please select location!!!", Toast.LENGTH_SHORT).show();
        } else {
            goToScan();
        }
    }

    private void goToScan() {
        Intent intent = new Intent(StockTakingActivity.this, ScanActivity.class);
        String prefName = "user";
        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("locationId", locationId);
        editor.apply();
        startActivity(intent);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getWindow().setWindowAnimations(0);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        prefs = getSharedPreferences(prefSelector, MODE_PRIVATE);
        SharedPreferences.Editor dataEditor = prefs.edit();
        dataEditor.clear().commit();
        finish();
    }
}
