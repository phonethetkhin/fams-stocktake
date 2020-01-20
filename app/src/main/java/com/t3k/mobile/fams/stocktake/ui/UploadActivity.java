package com.t3k.mobile.fams.stocktake.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
import com.t3k.mobile.fams.stocktake.R;
import com.t3k.mobile.fams.stocktake.adapter.AssetListAdapter;
import com.t3k.mobile.fams.stocktake.db.DBHelper;
import com.t3k.mobile.fams.stocktake.model.AssetBean;
import com.t3k.mobile.fams.stocktake.model.DeviceInfo;
import com.t3k.mobile.fams.stocktake.model.Evidence;
import com.t3k.mobile.fams.stocktake.model.LocationsBean;
import com.t3k.mobile.fams.stocktake.model.StaticContentBean;
import com.t3k.mobile.fams.stocktake.model.StockTakeBean;
import com.t3k.mobile.fams.stocktake.model.StockTakeItemBean;
import com.t3k.mobile.fams.stocktake.model.User;
import com.t3k.mobile.fams.stocktake.retrofit.ApiService;
import com.t3k.mobile.fams.stocktake.retrofit.Constant;
import com.t3k.mobile.fams.stocktake.retrofit.ServiceApi;
import com.t3k.mobile.fams.stocktake.retrofit.ServiceGenerator;
import com.t3k.mobile.fams.stocktake.utils.Event;
import com.t3k.mobile.fams.stocktake.utils.NetworkUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadActivity extends AppCompatActivity {
    private List<AssetBean> assetList;
    private DBHelper helper;
    private RecyclerView assetRecycler;
    private AssetListAdapter adapter;
    private TextView txtLocateTitle, txtTotalCount, txtRemainCount, txtTakenCount, txtUnknownCount, allUploaded;
    private Button btnUpload, btnBack;
    private Toolbar toolbar;
    private ProgressDialog dialog;

    private String deviceCode, licenseKey, token;
    private int userId;
    private int selectedLocationId;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        helper = new DBHelper(this);
        assetRecycler = findViewById(R.id.asset_upload_recycler);
        txtLocateTitle = findViewById(R.id.txt_locate_title);
        txtTotalCount = findViewById(R.id.txt_total_asset_count);
        txtRemainCount = findViewById(R.id.txt_remain_asset_count);
        txtTakenCount = findViewById(R.id.txt_taken_asset_count);
        txtUnknownCount = findViewById(R.id.txt_unknown_asset_count);
        allUploaded = findViewById(R.id.all_uploaded);
        btnUpload = findViewById(R.id.btn_upload);
        btnBack = findViewById(R.id.btn_back);

        toolbar = findViewById(R.id.toolbar);
        setupToolbar();

        selectedLocationId = getIntent().getIntExtra("select_location", 0);
        forShow();

        assetList = new ArrayList<>();
        adapter = new AssetListAdapter(getApplicationContext(), assetList);
        assetRecycler.setAdapter(adapter);
        assetRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        getUploadAsset();

        dialog = new ProgressDialog(UploadActivity.this);
        dialog.setTitle("Uploading Assets...");
        dialog.setMessage("Please...Wait...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        EventBus.getDefault().register(this);
    }

    private void forShow() {
        LocationsBean locations = helper.getLocationById(selectedLocationId);
        String name = locations.getCode() + " - " + locations.getName();
        txtLocateTitle.setText(name);
        int totalCount = helper.getAssetCountByLocation(selectedLocationId);
        int remainCount = helper.getRemainCountByLocation(selectedLocationId);
        int takenCount = helper.getTakenCountByLocation(selectedLocationId);
        int unKnownCount = helper.getUnknownCountByLocation(selectedLocationId);
        txtTotalCount.setText(String.valueOf(totalCount));
        txtRemainCount.setText(String.valueOf(remainCount));
        txtTakenCount.setText(String.valueOf(takenCount));
        txtUnknownCount.setText(String.valueOf(unKnownCount));
    }

    private void getUploadAsset() {

        assetList = helper.getAssetByLocationId(selectedLocationId);
        if (assetList.size() != 0) {

            Collections.sort(assetList, (fa1, fa2) -> {
                if (fa1.getFaNumber() != null && fa2.getFaNumber() != null) {
                    return fa1.getFaNumber().compareTo(fa2.getFaNumber());
                }
                return 0;
            });

            adapter = new AssetListAdapter(getApplicationContext(), assetList);
            assetRecycler.setAdapter(adapter);
            assetRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            adapter.notifyDataSetChanged();

        } else if (assetList.size() == 0) {

            List<AssetBean> assetEmptyList = new ArrayList<>();
            adapter = new AssetListAdapter(getApplicationContext(), assetEmptyList);
            assetRecycler.setAdapter(adapter);
            assetRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            adapter.notifyDataSetChanged();
        }
    }

    public void Upload(View view) {
        NetworkUtils utils = new NetworkUtils(this);
        if (utils.isConnectingToInternet()) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(R.string.title_upload_confirm);
            alertDialog.setMessage(R.string.warning_upload_confirm);
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton(R.string.btn_ok, (dialog, which) -> uploadStockTake());
            alertDialog.setNegativeButton(R.string.btn_cancel, (dialog, which) -> {
            });
            alertDialog.show();
        } else {
            Toast.makeText(getApplicationContext(), "Internet connection is offline!", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadStockTake() {
        dialog.show();
        DeviceInfo deviceInfo = helper.getDeviceInfoById(1);
        deviceCode = deviceInfo.getDeviceCode();
        String prefLicense = "licenseKey";
        prefs = Objects.requireNonNull(getSharedPreferences(prefLicense, MODE_PRIVATE));
        licenseKey = prefs.getString("license_key", null);
        String prefName = "user";
        prefs = Objects.requireNonNull(getSharedPreferences(prefName, MODE_PRIVATE));
        User user = helper.getUserByLoginId(1);
        userId = user.getUserId();
        token = prefs.getString("token", null);

        LocationsBean locationsBean = helper.getLocationById(selectedLocationId);
        Map<String, Object> request = new HashMap<>();
        request.put("appType", Constant.APP_TYPE);
        request.put("deviceCode", deviceCode);
        request.put("licenseKey", licenseKey);
        request.put("userId", userId);
        request.put("token", token);
        request.put("downloadId", locationsBean.getDownload());

        final ApiService service = new ApiService();
        service.uploadFileStart(request, new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Map<String, Object> result = response.body();
                    double uploadId = (double) result.get("uploadId");
                    String status = (String) result.get("status");
                    if (status.equals("SUCCESS")) {
                        StockTakeBean stockTakeBean = new StockTakeBean();
                        stockTakeBean.setDeviceUploadId((int) uploadId);
                        stockTakeBean.setLocationId(selectedLocationId);

                        Map<String, Object> request2 = new HashMap<>();
                        request2.put("appType", Constant.APP_TYPE);
                        request2.put("deviceCode", deviceCode);
                        request2.put("licenseKey", licenseKey);
                        request2.put("userId", userId);
                        request2.put("token", token);
                        request2.put("stockTake", stockTakeBean);
                        service.uploadStockTake(request2, new Callback<Map<String, Object>>() {
                            @Override
                            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                                if (response.isSuccessful()) {
                                    Map<String, Object> result2 = response.body();
                                    String status = (String) result2.get("status");
                                    if (status.equals("SUCCESS")) {
                                        double stockTakeId = (double) result2.get("stockTakeId");
                                        try {
                                            for (int i = 0; i < assetList.size(); i++) {

                                                final AssetBean asset = assetList.get(i);

                                                if (asset.getUnknown() == 1) {
                                                    uploadUnknownStockTake(uploadId, asset, stockTakeId);
                                                } else {
                                                    uploadKnownStockTake(uploadId, asset, stockTakeId);
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Upload Fail!!!", Toast.LENGTH_SHORT).show();
                    }
                } else if (response.code() == 408) {
                    dialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                    builder.setTitle(R.string.dialog_session_expired);
                    builder.setCancelable(false);
                    builder.setPositiveButton("Ok", (dialog, which) -> {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    });
                    builder.show();
                } else {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Upload Fail!!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadUnknownStockTake(double uploadId, AssetBean assetBean, double stockTakeId) {
        ApiService service = new ApiService();
        Map<String, Object> request1 = new HashMap<>();
        request1.put("appType", Constant.APP_TYPE);
        request1.put("deviceCode", deviceCode);
        request1.put("licenseKey", licenseKey);
        request1.put("token", token);
        request1.put("assetNo", assetBean.getFaNumber());

        service.assetSearchByFaNumber(request1, new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Map<String, Object> result1 = response.body();
                    String status = (String) result1.get("status");
                    if (status.equals("SUCCESS")) {
                        double assetId = (double) result1.get("assetId");

                        StockTakeItemBean stockItem = new StockTakeItemBean();
                        stockItem.setStockTakeId((int) stockTakeId);
                        stockItem.setAssetId((int) assetId);
                        stockItem.setTaken(true);
                        stockItem.setScanned(true);
                        stockItem.setUnknown(true);
                        stockItem.setRemark(assetBean.getRemark());

                        ExecutorService executorService = Executors.newSingleThreadExecutor();
                        Callable<StaticContentBean> callable1 = () -> {

                            Evidence image = helper.getImageByFaNumber(assetBean.getFaNumber());
                            StaticContentBean content = new StaticContentBean();
                            if (image != null) {
                                uploadImage(image.getImagePath(), content);
                            }
                            return content;
                        };

                        StaticContentBean staticContentBean = null;
                        Future<StaticContentBean> future1 = executorService.submit(callable1);
                        try {
                            Evidence image = helper.getImageByFaNumber(assetBean.getFaNumber());
                            if (image != null) {
                                staticContentBean = future1.get();
                            }
                        } catch (
                                Exception e) {
                            executorService.shutdown();
                            e.printStackTrace();
                        }

                        Map<String, Object> request3 = new HashMap<>();
                        request3.put("appType", Constant.APP_TYPE);
                        request3.put("deviceCode", deviceCode);
                        request3.put("licenseKey", licenseKey);
                        request3.put("userId", userId);
                        request3.put("token", token);
                        request3.put("stockTakeItem", stockItem);
                        request3.put("evidence", staticContentBean);
                        service.uploadStockItem(request3, new Callback<Map<String, Object>>() {
                            @Override
                            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                                if (response.isSuccessful()) {
                                    Map<String, Object> result3 = response.body();
                                    String status = (String) result3.get("status");
                                    if (status.equals("SUCCESS")) {
                                        int UploadId = (int) uploadId;
                                        Map<String, Object> request4 = new HashMap<>();
                                        request4.put("appType", Constant.APP_TYPE);
                                        request4.put("deviceCode", deviceCode);
                                        request4.put("licenseKey", licenseKey);
                                        request4.put("userId", userId);
                                        request4.put("token", token);
                                        request4.put("uploadId", UploadId);

                                        service.uploadFileEnd(request4, new Callback<Map<String, Object>>() {
                                            @Override
                                            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                                                if (response.isSuccessful()) {
                                                    Map<String, Object> result4 = response.body();
                                                    String status = (String) result4.get("status");
//                                                    if (status.equals("SUCCESS")) {
                                                        Calendar c = Calendar.getInstance();
                                                        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                                                        final String uploadStartDate = dateFormat.format(c.getTime());
                                                        assetBean.setUploadTime(uploadStartDate);
                                                        helper.updateAsset(assetBean);
                                                        helper.close();
                                                        EventBus.getDefault().post(Event.After_Upload);
                                                    }
//                                                } else {
//                                                    dialog.dismiss();
//                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                                                dialog.dismiss();
                                                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    dialog.dismiss();
                    helper.deleteAssetByPrimaryId(assetBean.getAssetId());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadKnownStockTake(double uploadId, AssetBean assetBean, double stockTakeId) {
        ApiService service = new ApiService();

        StockTakeItemBean stockItem = new StockTakeItemBean();
        stockItem.setStockTakeId((int) stockTakeId);
        stockItem.setAssetId(assetBean.getAssetId());
        if (assetBean.getTaken() != 0) {
            stockItem.setTaken(true);
        } else {
            stockItem.setTaken(false);
        }
        if (assetBean.getScannedStatus() != 0) {
            stockItem.setScanned(true);
        } else {
            stockItem.setScanned(false);
        }
        stockItem.setUnknown(false);
        stockItem.setRemark(assetBean.getRemark());

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Callable<StaticContentBean> callable1 = () -> {

            Evidence image = helper.getImageByFaNumber(assetBean.getFaNumber());
            StaticContentBean content = new StaticContentBean();
            if (image != null) {
                uploadImage(image.getImagePath(), content);
            }
            return content;
        };

        StaticContentBean staticContentBean = null;
        Future<StaticContentBean> future1 = executorService.submit(callable1);
        try {
            Evidence image = helper.getImageByFaNumber(assetBean.getFaNumber());
            if (image != null) {
                staticContentBean = future1.get();
            }
        } catch (Exception e) {
            executorService.shutdown();
            e.printStackTrace();
        }

        Map<String, Object> request3 = new HashMap<>();
        request3.put("appType", Constant.APP_TYPE);
        request3.put("deviceCode", deviceCode);
        request3.put("licenseKey", licenseKey);
        request3.put("userId", userId);
        request3.put("token", token);
        request3.put("stockTakeItem", stockItem);
        request3.put("evidence", staticContentBean);

        //*****************************************************************//
        service.uploadStockItem(request3, new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Map<String, Object> result3 = response.body();
                    String status = (String) result3.get("status");

                    int UploadId = (int) uploadId;
                    if (status.equals("SUCCESS")) {
                        Map<String, Object> request4 = new HashMap<>();
                        request4.put("appType", Constant.APP_TYPE);
                        request4.put("deviceCode", deviceCode);
                        request4.put("licenseKey", licenseKey);
                        request4.put("userId", userId);
                        request4.put("token", token);
                        request4.put("uploadId", UploadId);


                        //*****************************************************************//
                        service.uploadFileEnd(request4, new Callback<Map<String, Object>>() {
                            @Override
                            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                                if (response.isSuccessful()) {
                                    Map<String, Object> result4 = response.body();
                                    String status = (String) result4.get("status");

//                                    if (status.equals("SUCCESS")) {
                                        Calendar c = Calendar.getInstance();
                                        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                                        final String uploadStartDate = dateFormat.format(c.getTime());
                                        assetBean.setUploadTime(uploadStartDate);
                                        helper.updateAsset(assetBean);
                                        helper.close();
                                        EventBus.getDefault().post(Event.After_Upload);
//                                    } else {
//                                        dialog.dismiss();
//                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void Back(View view) {
        onBackPressed();
    }

    @Subscribe
    public void onEvent(Event event) {

        if (event.equals(Event.After_Upload)) {
            assetList = helper.getAssetByLocationId(selectedLocationId);
            if (assetList.size() != 0) {

                Collections.sort(assetList, (fa1, fa2) -> {
                    if (fa1.getFaNumber() != null && fa2.getFaNumber() != null) {
                        return fa1.getFaNumber().compareTo(fa2.getFaNumber());
                    }
                    return 0;
                });

                adapter = new AssetListAdapter(getApplicationContext(), assetList);
                assetRecycler.setAdapter(adapter);
                assetRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter.notifyDataSetChanged();

            } else if (assetList.size() == 0) {

                List<AssetBean> assetEmptyList = new ArrayList<>();
                adapter = new AssetListAdapter(getApplicationContext(), assetEmptyList);
                assetRecycler.setAdapter(adapter);
                assetRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter.notifyDataSetChanged();
                assetRecycler.setVisibility(View.GONE);
                allUploaded.setVisibility(View.VISIBLE);
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Upload Finish!", Toast.LENGTH_SHORT).show();
            }

            btnUpload.setVisibility(View.GONE);
            btnBack.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void uploadImage(String imagePath, StaticContentBean staticContentBean) {

        File file = new File(imagePath);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "android/stocktake");
        ServiceApi mApi = ServiceGenerator.createService(ServiceApi.class);
        Call<LinkedTreeMap> imageUpload = mApi.postImage(Constant.APP_TYPE, deviceCode, licenseKey, userId, token, body, name);
        try {
            LinkedTreeMap result = imageUpload.execute().body();

            Map<String, String> map = (Map<String, String>) ((List) result.get("uploadFiles")).get(0);
            staticContentBean.setFileName(map.get("fileName"));
            staticContentBean.setFileSize(map.get("fileSize"));
            staticContentBean.setFilePath(map.get("filePath"));
            staticContentBean.setFileType(StaticContentBean.FileType.valueOf(String.valueOf(map.get("fileType"))));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_upload);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        getWindow().setWindowAnimations(0);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), SelectUploadLocationActivity.class);
        startActivity(intent);
        String prefSelector = "selector";
        prefs = getSharedPreferences(prefSelector, MODE_PRIVATE);
        SharedPreferences.Editor dataEditor = prefs.edit();
        dataEditor.clear().commit();
        finish();
    }
}
