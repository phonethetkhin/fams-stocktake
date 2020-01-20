package com.t3k.mobile.fams.stocktake.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.t3k.mobile.fams.stocktake.R;
import com.t3k.mobile.fams.stocktake.adapter.UploadedListAdapter;
import com.t3k.mobile.fams.stocktake.db.DBHelper;
import com.t3k.mobile.fams.stocktake.model.AssetBean;

import java.util.ArrayList;
import java.util.List;

public class UploadHistoryActivity extends AppCompatActivity {
    DBHelper helper;
    Toolbar toolbar;
    List<AssetBean> assetList = new ArrayList<>();
    UploadedListAdapter uploadAdapter;
    RecyclerView recyclerView;
    TextView txtNoUploaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_history);
        helper = new DBHelper(this);
        toolbar = findViewById(R.id.toolbar_data);
        setupToolbar();
        assetList = helper.getAllUploadAssets();
        recyclerView = findViewById(R.id.uploaded_list);
        txtNoUploaded = findViewById(R.id.txt_no_uploaded);
        uploadAdapter = new UploadedListAdapter(assetList, getApplicationContext());
        recyclerView.setAdapter(uploadAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        if (assetList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            txtNoUploaded.setVisibility(View.VISIBLE);
        }
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
        super.onBackPressed();
    }
}
