package com.t3k.mobile.fams.stocktake.ui;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.t3k.mobile.fams.stocktake.R;
import com.t3k.mobile.fams.stocktake.db.DBHelper;
import com.t3k.mobile.fams.stocktake.model.AssetBean;
import com.t3k.mobile.fams.stocktake.model.LocationsBean;
import com.t3k.mobile.fams.stocktake.utils.Event;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ScanActivity extends TabActivity {

    DBHelper helper;
    TabHost tabHost;
    TextView total, tvCount, locationName;
    ImageView imgScan,imgActivateScanner;

    private SharedPreferences prefs;

    private String prefName = "user";

    int taken = 0;
    int remain = 0;
    int unknown = 0;
    int count = 0;
    int scanned;
    int locationId;
    //for scan broadcast using service
    ScanBroadcast mScanBroadcast;
    String StartScan = "com.jbservice.action.START_SCAN";
    String OpenScan = "com.jbservice.action.OPEN_SCAN";
    String CloseScan = "com.jbservice.action.STOP_SCAN";

    String ScanResult = "com.jbservice.action.GET_SCANDATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        helper = new DBHelper(this);
        total = findViewById(R.id.total_count);
        locationName = findViewById(R.id.location_name);
        imgScan = findViewById(R.id.img_scan);
        imgActivateScanner=findViewById(R.id.img_activatescanner);


        setupToolbar();

        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        locationId = prefs.getInt("locationId", 0);

        taken = helper.getScannedCountByLocation(locationId);
        remain = helper.getRemainCountByLocation(locationId);
        unknown = helper.getUnknownCountByLocation(locationId);
        count = helper.getAssetCountByLocation(locationId);

        LocationsBean location = helper.getLocationById(locationId);
        locationName.setText(location.getName());
        total.setText(String.valueOf(count));

        tabHost = getTabHost();
        this.setNewTab(this, tabHost, "tab1", R.string.textTabTitle1, remain, R.drawable.remain_asset_circle, RemainActivity.class);
        this.setNewTab(this, tabHost, "tab2", R.string.textTabTitle2, taken, R.drawable.taken_asset_circle, TakenActivity.class);
        this.setNewTab(this, tabHost, "tab3", R.string.textTabTitle3, unknown, R.drawable.unknown_asset_circle, UnknownActivity.class);

        scanned = getIntent().getIntExtra("scanned", 0);

        if (scanned == 1) {
            tabHost.setCurrentTab(0);
        } else {
            tabHost.setCurrentTab(1);
        }

        imgScan.setOnClickListener(v -> new IntentIntegrator(ScanActivity.this).setCaptureActivity(QRCodeScanActivity.class).initiateScan());
        EventBus.getDefault().register(this);

imgActivateScanner.setOnClickListener(view -> {
        recreate();
});

    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiverScanBroadcast();
        Intent i=new Intent(OpenScan);
        sendBroadcast(i);

    }

    public void registerReceiverScanBroadcast() {
        mScanBroadcast = new ScanBroadcast();
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(ScanResult);
        registerReceiver(mScanBroadcast, intentFilter);
    }
    public class ScanBroadcast extends BroadcastReceiver {
        /**
         * Receiving the scan and bind to adapter
         *
         * @param context
         * @param intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(ScanResult)) {
                String datas = intent.getStringExtra("data");
                //Toast.makeText(context, datas, Toast.LENGTH_SHORT).show();
showResultDialogue(datas);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {

                Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_SHORT).show();
            } else {


                showResultDialogue(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //method to construct dialogue with scan results
    public void showResultDialogue(final String result) {
        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        locationId = prefs.getInt("locationId", 0);
        List<AssetBean> assetList = helper.getScanByLocation(locationId);
        for (final AssetBean asset : assetList) {
            if (result.equals(asset.getFaNumber())) {

                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                asset.setScannedStatus(1);
                Calendar c = Calendar.getInstance();
                final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault());
                String scanTime = dateFormat.format(c.getTime());
                asset.setStockTakeTime(scanTime);
                asset.setTaken(1);
                asset.setScannedStatus(1);
                helper.updateAsset(asset);
                helper.close();

                EventBus.getDefault().post(Event.Request_Data);
            }
        }

        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        locationId = prefs.getInt("locationId", 0);
        AlertDialog.Builder builder;

        if (!helper.checkAssetNumberExistsByLocation(result, locationId) && locationId != -1) {
            String stockValue = "AST";
            if (result.matches("^(" + stockValue + "-[0-9]{4}-[0-9]{2}-[0-9]{6})$")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(this);
                }
                builder.setTitle("Scan Result")
                        .setMessage("Scanned result is " + result)
                        .setPositiveButton("Ok", (dialog, which) -> {
                            Intent intent = new Intent(getApplicationContext(), UnknownRemarkActivity.class);
                            intent.putExtra("locationId", locationId);
                            intent.putExtra("faNo", result);
                            startActivity(intent);
                            finish();

                        })
                        .setNegativeButton("Cancel", (dialog, which) ->


                                dialog.dismiss())
                        .show();

            }

        }
        else
        {
builder =new AlertDialog.Builder(this,R.style.Theme_MaterialComponents_Dialog);
builder.setTitle("Already Added!")
.setMessage("The Result You Scanned is Already Scanned and Added")
        .setPositiveButton("Ok",(dialog, which) -> {
            dialog.dismiss();
        })
        .show();


        }
    }

    @Subscribe
    public void onEvent(Event event) {
        if (event.equals(Event.Request_Data)) {
            prefs = getSharedPreferences(prefName, MODE_PRIVATE);
            locationId = prefs.getInt("locationId", 0);

            taken = helper.getScannedCountByLocation(locationId);
            remain = helper.getRemainCountByLocation(locationId);
            unknown = helper.getUnknownCountByLocation(locationId);
            count = helper.getAssetCountByLocation(locationId);

            LocationsBean location = helper.getLocationById(locationId);
            locationName.setText(location.getName());
            total.setText(String.valueOf(count));

            getTabHost().clearAllTabs();
            this.setNewTab(this, tabHost, "tab1", R.string.textTabTitle1, remain, R.drawable.remain_asset_circle, RemainActivity.class);
            this.setNewTab(this, tabHost, "tab2", R.string.textTabTitle2, taken, R.drawable.taken_asset_circle, TakenActivity.class);
            this.setNewTab(this, tabHost, "tab3", R.string.textTabTitle3, unknown, R.drawable.unknown_asset_circle, UnknownActivity.class);

            scanned = getIntent().getIntExtra("scanned", 0);

            if (scanned == 1) {
                tabHost.setCurrentTab(0);
            } else {
                tabHost.setCurrentTab(1);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(mScanBroadcast);
        Intent i=new Intent(CloseScan);
        sendBroadcast(i);
    }

    private void setNewTab(Context context, TabHost tabHost, String tag, int title, int size, int background, Class<?> content) {
        TabHost.TabSpec tabSpec = tabHost.newTabSpec(tag);
        tabSpec.setIndicator(getTabIndicator(tabHost.getContext(), title, size, background));
        tabSpec.setContent(new Intent(this, content));
        tabHost.addTab(tabSpec);
    }

    private View getTabIndicator(Context context, int title, int size, int background) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        View viewCount = view.findViewById(R.id.view_count);
        TextView tv = view.findViewById(R.id.tab_label);

        tv.setText(title);
        tv.setTextColor(Color.BLACK);
        tvCount = view.findViewById(R.id.count);
        tvCount.setText(Integer.toString(size));
        viewCount.setBackgroundResource(background);

        return view;
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Stock Taking");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getWindow().setWindowAnimations(0);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), StockTakingActivity.class);
        startActivity(intent);
        String prefSelector = "selector";
        prefs = getSharedPreferences(prefSelector, MODE_PRIVATE);
        SharedPreferences.Editor dataEditor = prefs.edit();
        dataEditor.clear().commit();
        finish();
    }


}
