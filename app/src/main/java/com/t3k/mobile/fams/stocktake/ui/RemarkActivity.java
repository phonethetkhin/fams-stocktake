package com.t3k.mobile.fams.stocktake.ui;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.t3k.mobile.fams.stocktake.R;
import com.t3k.mobile.fams.stocktake.db.DBHelper;
import com.t3k.mobile.fams.stocktake.model.AssetBean;
import com.t3k.mobile.fams.stocktake.model.Evidence;
import com.t3k.mobile.fams.stocktake.model.LocationsBean;
import com.t3k.mobile.fams.stocktake.utils.Event;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RemarkActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CAMERA_PERMISSION_CODE = 3;
    private DBHelper helper;
    private Toolbar toolbar;
    View knownLayout;
    private TextView txtAssetNumber, txtAssetScan, txtItemName,
            txtCondition, txtLocation, txtCostCenter, txtCategory;
    private ImageView imgCamera, imgAssetPhoto, imgCam, imgDelete;
    private EditText edDescription;

    int primaryId, faId, locationId, locationFoundId, unknown, scanned, taken, toSelect;
    String faNo, itemName, condition, costCenter, category, brand, model, remark, path;


    private Uri mCapturedImageURI;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private SharedPreferences prefs;
    private String prefSelection = "selection";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remark);
        helper = new DBHelper(this);
        toolbar = findViewById(R.id.toolbar);
        knownLayout = findViewById(R.id.known_content);
        txtAssetNumber = findViewById(R.id.txt_asset_number);
        txtAssetScan = findViewById(R.id.txt_scan);
        txtItemName = findViewById(R.id.txt_item_name);
        txtCondition = findViewById(R.id.txt_condition);
        txtLocation = findViewById(R.id.txt_location);
        txtCostCenter = findViewById(R.id.txt_cost_center);
        txtCategory = findViewById(R.id.txt_category);
        imgCamera = findViewById(R.id.img_camera);
        imgAssetPhoto = findViewById(R.id.img_asset_photo);
        imgCam = findViewById(R.id.img_cam);
        imgDelete = findViewById(R.id.img_delete);
        edDescription = findViewById(R.id.ed_description);
        Button btnCancel = findViewById(R.id.btn_cancel_asset);
        Button btnSave = findViewById(R.id.btn_save_asset);

        setupToolbar();

        prefs = getSharedPreferences(prefSelection, MODE_PRIVATE);
        toSelect = prefs.getInt("toSelect", 0);
        forShow();
        if (remark != null) {
            edDescription.setText(remark);
        }
        imgCamera.setOnClickListener(this);
        imgCam.setOnClickListener(this);
        imgDelete.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    private void forShow() {
        faNo = getIntent().getStringExtra("faNo");
        scanned = getIntent().getIntExtra("scanned", 0);
        primaryId = getIntent().getIntExtra("primary_id", 0);
        faId = getIntent().getIntExtra("faId", 0);
        locationId = getIntent().getIntExtra("locationId", 0);
        itemName = getIntent().getStringExtra("item_name");
        condition = getIntent().getStringExtra("condition");
        costCenter = getIntent().getStringExtra("cost_center");
        category = getIntent().getStringExtra("category");
        brand = getIntent().getStringExtra("brand");
        model = getIntent().getStringExtra("model");
        locationFoundId = getIntent().getIntExtra("locate_found_id", 0);
        unknown = getIntent().getIntExtra("unknown", 0);
        remark = getIntent().getStringExtra("remark");
        taken = getIntent().getIntExtra("taken", 0);
        path = getIntent().getStringExtra("path");

        txtAssetNumber.setText(faNo);
        if (scanned == 0 && remark == null) {
            txtAssetScan.setText(R.string.txt_not_taken);
        } else if (scanned == 1) {
            txtAssetScan.setText(R.string.txt_scanned);
            txtAssetScan.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else if (scanned == 0) {
            txtAssetScan.setText(R.string.txt_noted);
            txtAssetScan.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        txtItemName.setText(itemName);
        if (condition.equals("OPR")) {
            txtCondition.setText(R.string.txt_opr);
        }
        LocationsBean locationsBean = helper.getLocationById(locationId);
        txtLocation.setText(locationsBean.getCode() + " - " + locationsBean.getName());
        txtCostCenter.setText(costCenter);
        txtCategory.setText(category);
        AssetBean assetBean = helper.getFaByFaNumber(faNo);
        if (assetBean.getImagePath() != null) {
            Glide.with(this).load(path).into(imgAssetPhoto);
            imgCamera.setVisibility(View.GONE);
            imgCam.setVisibility(View.VISIBLE);
            imgDelete.setVisibility(View.VISIBLE);
        } else {
            if (path != null) {
                Glide.with(this).load(path).into(imgAssetPhoto);
                imgCamera.setVisibility(View.GONE);
                imgCam.setVisibility(View.VISIBLE);
                imgDelete.setVisibility(View.VISIBLE);
            } else {
                Glide.with(this).load("").into(imgAssetPhoto);
                imgCamera.setVisibility(View.VISIBLE);
                imgCam.setVisibility(View.GONE);
                imgDelete.setVisibility(View.GONE);
            }
        }

        EventBus.getDefault().register(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.img_camera) {
            selectImage();
        } else if (id == R.id.img_cam) {
            selectImage();
        } else if (id == R.id.img_delete) {
            helper.deletePhotoByAssetNo(faNo);
            if (path != null) {
                Glide.with(this).load("").into(imgAssetPhoto);
                imgCamera.setVisibility(View.VISIBLE);
                imgCam.setVisibility(View.GONE);
                imgDelete.setVisibility(View.GONE);
            } else {
                EventBus.getDefault().post(Event.Add_Photo);
            }

        } else if (id == R.id.btn_save_asset) {
            String edRemark = edDescription.getText().toString();

            if (!edRemark.isEmpty()) {
                AssetBean assetBean = new AssetBean();
                assetBean.setId(primaryId);
                assetBean.setLocationId(locationId);
                assetBean.setAssetId(faId);
                assetBean.setCostCenter(costCenter);
                assetBean.setFaNumber(faNo);
                assetBean.setItemName(itemName);
                assetBean.setCondition(condition);
                assetBean.setCategory(category);
                assetBean.setBrand(brand);
                assetBean.setModel(model);
                assetBean.setLocationFoundId(locationId);
                assetBean.setRemark(edRemark);
                assetBean.setTaken(1);
                if (scanned == 0) {
                    assetBean.setScannedStatus(0);
                } else if (scanned == 1) {
                    assetBean.setScannedStatus(1);
                }
                Calendar c = Calendar.getInstance();
                final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault());
                String stockTakingTime = dateFormat.format(c.getTime());
                assetBean.setStockTakeTime(stockTakingTime);
                Evidence image = helper.getImageByFaNumber(faNo);

                if (image != null) {
                    assetBean.setImagePath(image.getImagePath());
                } else {
                    assetBean.setImagePath(null);
                }

                helper.updateAsset(assetBean);
                helper.close();

                prefs = getSharedPreferences(prefSelection, MODE_PRIVATE);
                int toSelect = prefs.getInt("toSelect", 0);

                Intent intent = new Intent(RemarkActivity.this, ScanActivity.class);
                intent.putExtra("scanned", scanned);
                intent.putExtra("toSelect", toSelect);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(getApplicationContext(), "Write remark... It must not be empty!", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.btn_cancel_asset) {
            onBackPressed();
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(RemarkActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Take Photo")) {
                activeTakePhoto();
            } else if (items[item].equals("Choose from Gallery")) {
                activeGallery();
            } else {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void activeTakePhoto() {
        checkPermission();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                goToCam();
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_CODE);
            }
        } else {
            goToCam();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Application need camera permission", Toast.LENGTH_SHORT).show();
            } else {
                goToCam();
            }
        }
    }

    private void goToCam() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            String fileName = "temp.jpg";
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, fileName);
            mCapturedImageURI = getContentResolver()
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values);
            takePictureIntent
                    .putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void activeGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case RESULT_LOAD_IMAGE:
                if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver()
                            .query(selectedImage, filePathColumn, null, null,
                                    null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    Glide.with(this).load(picturePath).into(imgAssetPhoto);
                    imgCamera.setVisibility(View.GONE);
                    imgCam.setVisibility(View.VISIBLE);
                    imgDelete.setVisibility(View.VISIBLE);
                    if (!helper.checkPhotoExists(faNo)) {
                        Evidence image = new Evidence();
                        image.setFaNumber(faNo);
                        image.setImagePath(picturePath);
                        helper.addImage(image);
                    } else {
                        Evidence faImage = helper.getImageByFaNumber(faNo);
                        faImage.setImagePath(picturePath);
                        helper.updatePhoto(faImage);
                    }
                    helper.close();
                }
            case REQUEST_IMAGE_CAPTURE:
                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

                    String[] projection = {MediaStore.Images.Media.DATA};
                    Cursor cursor = managedQuery(mCapturedImageURI, projection, null, null, null);
                    int column_index_data = cursor.getColumnIndexOrThrow(
                            MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String picturePath = cursor.getString(column_index_data);
                    Glide.with(this).load(picturePath).into(imgAssetPhoto);
                    imgCamera.setVisibility(View.GONE);
                    imgCam.setVisibility(View.VISIBLE);
                    imgDelete.setVisibility(View.VISIBLE);
                    if (!helper.checkPhotoExists(faNo)) {
                        Evidence image = new Evidence();
                        image.setFaNumber(faNo);
                        image.setImagePath(picturePath);
                        helper.addImage(image);
                    } else {
                        Evidence faImage = helper.getImageByFaNumber(faNo);
                        faImage.setImagePath(picturePath);
                        helper.updatePhoto(faImage);
                    }
                    helper.close();
                }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mCapturedImageURI != null) {
            outState.putString("mCapturedImageURI", mCapturedImageURI.toString());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("mCapturedImageURI")) {
            mCapturedImageURI = Uri.parse(
                    savedInstanceState.getString("mCapturedImageURI"));
        }
    }

    @Subscribe
    public void onEvent(Event event) {
        if (event.equals(Event.Add_Photo)) {
            forShow();
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Asset Detail");
        getWindow().setWindowAnimations(0);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RemarkActivity.this, ScanActivity.class);
        intent.putExtra("scanned", scanned);
        intent.putExtra("toSelect", toSelect);
        startActivity(intent);
        finish();
    }
}
