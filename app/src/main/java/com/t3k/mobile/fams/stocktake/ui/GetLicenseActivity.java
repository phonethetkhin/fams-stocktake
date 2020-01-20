package com.t3k.mobile.fams.stocktake.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.t3k.mobile.fams.stocktake.R;
import com.t3k.mobile.fams.stocktake.db.DBHelper;
import com.t3k.mobile.fams.stocktake.model.DeviceInfo;
import com.t3k.mobile.fams.stocktake.retrofit.ApiService;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetLicenseActivity extends AppCompatActivity implements View.OnClickListener {

    private DBHelper helper;
    private RelativeLayout noLicenseLayout, licenseRequestLayout, requestPendingLayout, requestFailLayout, noExtraLicenseLayout;
    private EditText edLoginId, edPassword;
    private ProgressDialog pDialog;
    private SharedPreferences prefs;
    private String prefLicense = "licenseKey";
    boolean cancel;
    View focusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_license);
        helper = new DBHelper(this);

        noLicenseLayout = findViewById(R.id.app_no_license_layout);
        licenseRequestLayout = findViewById(R.id.license_request_layout);
        requestPendingLayout = findViewById(R.id.license_request_pending_layout);
        requestFailLayout = findViewById(R.id.license_request_fail_layout);
        noExtraLicenseLayout = findViewById(R.id.no_extra_license_layout);
        edLoginId = findViewById(R.id.license_loginId);
        edPassword = findViewById(R.id.license_password);
        Button buttonLicense = findViewById(R.id.button_license);
        Button btnGetLicense = findViewById(R.id.btn_license);

        int appStatus = getIntent().getIntExtra("license_status", 0);
        if (appStatus == 0) {
            noLicenseLayout.setVisibility(View.VISIBLE);
            licenseRequestLayout.setVisibility(View.GONE);
            requestPendingLayout.setVisibility(View.GONE);
            requestFailLayout.setVisibility(View.GONE);
            noExtraLicenseLayout.setVisibility(View.GONE);
        } else if (appStatus == 1) {
            noLicenseLayout.setVisibility(View.GONE);
            licenseRequestLayout.setVisibility(View.GONE);
            requestPendingLayout.setVisibility(View.VISIBLE);
            requestFailLayout.setVisibility(View.GONE);
            noExtraLicenseLayout.setVisibility(View.GONE);
        } else if (appStatus == 2) {
            noLicenseLayout.setVisibility(View.GONE);
            licenseRequestLayout.setVisibility(View.GONE);
            requestPendingLayout.setVisibility(View.GONE);
            requestFailLayout.setVisibility(View.GONE);
            noExtraLicenseLayout.setVisibility(View.VISIBLE);
        }
        buttonLicense.setOnClickListener(this);
    }

    public void GetLicense(View view) {

        cancel = false;
        focusView = null;
        String loginId = edLoginId.getText().toString();
        String password = edPassword.getText().toString();
        String prefDeviceAppId = "deviceAppId";
        prefs = getSharedPreferences(prefDeviceAppId, MODE_PRIVATE);
        int deviceAppId = prefs.getInt("device_app_id", 0);
        if (loginId.equals("") && TextUtils.isEmpty(loginId)) {
            edLoginId.setError(getString(R.string.error_empty_login_id));
            focusView = edLoginId;
            cancel = true;
        }
        if (password.equals("") && TextUtils.isEmpty(password)) {
            edPassword.setError(getString(R.string.error_empty_password));
            focusView = edPassword;
            cancel = true;
        } else if (!minPassword(password)) {
            edPassword.setError(getString(R.string.error_min_password));
            focusView = edPassword;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {

            DeviceInfo deviceInfo = helper.getDeviceInfoById(1);
            DeviceInfo info = new DeviceInfo();
            info.setDeviceCode(deviceInfo.getDeviceCode());
            info.setBrand(deviceInfo.getBrand());
            info.setModel(deviceInfo.getModel());
            info.setDeviceId1(deviceInfo.getDeviceId1());
            info.setSerialNo(deviceInfo.getSerialNo());
            info.setAndroidId(deviceInfo.getAndroidId());
            info.setFingerPrint(deviceInfo.getFingerPrint());

            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Please wait While License Request...");
            pDialog.setCancelable(false);
            pDialog.setInverseBackgroundForced(false);
            pDialog.show();

            ApiService apiService = new ApiService();
            apiService.license(deviceAppId, loginId, password, info, new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.code() == 200 || response.code() == 202) {
                        Map<String, Object> result = response.body();
                        if (result != null) {
                            String status = String.valueOf(result.get("status"));
                            pDialog.dismiss();
                            if (status.equals("REQUEST_PENDING")) {
                                noLicenseLayout.setVisibility(View.GONE);
                                licenseRequestLayout.setVisibility(View.GONE);
                                requestPendingLayout.setVisibility(View.VISIBLE);
                                requestFailLayout.setVisibility(View.GONE);
                                noExtraLicenseLayout.setVisibility(View.GONE);
                            } else {
                                String licenseKey = String.valueOf(result.get("licenseKey"));
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                prefs = getSharedPreferences(prefLicense, MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("license_key", licenseKey);
                                editor.apply();
                                startActivity(intent);
                                finish();
                            }
                        }
                    } else if (response.code() == 400) {
                        pDialog.dismiss();
                        edLoginId.setError(getString(R.string.error_invalid_credentials));
                        focusView = edLoginId;
                        edPassword.setError(getString(R.string.error_invalid_credentials));
                        focusView = edPassword;
                        cancel = true;
                    } else {
                        pDialog.dismiss();
                        noLicenseLayout.setVisibility(View.GONE);
                        licenseRequestLayout.setVisibility(View.GONE);
                        requestPendingLayout.setVisibility(View.GONE);
                        requestFailLayout.setVisibility(View.VISIBLE);
                        noExtraLicenseLayout.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "fail!!!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    pDialog.dismiss();
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean minPassword(String password) {
        return password.length() >= 4;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_license) {
            noLicenseLayout.setVisibility(View.GONE);
            licenseRequestLayout.setVisibility(View.VISIBLE);
            requestPendingLayout.setVisibility(View.GONE);
            requestFailLayout.setVisibility(View.GONE);
            noExtraLicenseLayout.setVisibility(View.GONE);
        }
    }
}
