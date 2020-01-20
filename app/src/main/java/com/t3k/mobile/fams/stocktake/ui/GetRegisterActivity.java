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

public class GetRegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private DBHelper helper;
    private RelativeLayout noRegisterLayout;
    private RelativeLayout registerLayout;
    private RelativeLayout registerFailLayout;
    EditText edDeviceName, edLoginId, edPassword;
    Button buttonRegister, btnRegister;
    private ProgressDialog pDialog;
    String deviceName, loginId, password;
    private SharedPreferences prefs;
    private String prefName = "user";
    private String prefDeviceAppId = "deviceAppId";
    View focusView;
    boolean cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_register);
        helper = new DBHelper(this);
        noRegisterLayout = findViewById(R.id.app_no_register_layout);
        registerLayout = findViewById(R.id.app_register_layout);
        registerFailLayout = findViewById(R.id.app_register_fail_layout);
        RelativeLayout deviceInActiveLayout = findViewById(R.id.device_inactive_layout);
        edDeviceName = findViewById(R.id.register_device_name);
        edLoginId = findViewById(R.id.register_loginId);
        edPassword = findViewById(R.id.register_password);
        buttonRegister = findViewById(R.id.button_register);
        btnRegister = findViewById(R.id.btn_register);

        int appStatus = getIntent().getIntExtra("device_status", 0);
        if (appStatus == 0) {
            noRegisterLayout.setVisibility(View.VISIBLE);
            registerLayout.setVisibility(View.GONE);
            registerFailLayout.setVisibility(View.GONE);
            deviceInActiveLayout.setVisibility(View.GONE);
        } else if (appStatus == 1) {
            noRegisterLayout.setVisibility(View.GONE);
            registerLayout.setVisibility(View.GONE);
            registerFailLayout.setVisibility(View.GONE);
            deviceInActiveLayout.setVisibility(View.VISIBLE);
        }

        buttonRegister.setOnClickListener(this);
    }

    public void GetRegister(View view) {

        cancel = false;
        focusView = null;
        deviceName = edDeviceName.getText().toString();
        loginId = edLoginId.getText().toString();
        password = edPassword.getText().toString();
        if (deviceName.equals("") && TextUtils.isEmpty(deviceName)) {
            edDeviceName.setError(getString(R.string.error_empty_device_name));
            focusView = edDeviceName;
            cancel = true;
        } else if (!maxDeviceName(deviceName)) {
            edDeviceName.setError(getString(R.string.error_max_device_name));
            focusView = edDeviceName;
            cancel = true;
        }
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

            final DeviceInfo deviceInfo = helper.getDeviceInfoById(1);
            final DeviceInfo info = new DeviceInfo();
            info.setDeviceName(deviceName);
            info.setBrand(deviceInfo.getBrand());
            info.setModel(deviceInfo.getModel());
            info.setOsVersion(deviceInfo.getOsVersion());
            info.setDevice(deviceInfo.getDevice());
            info.setMacWifi(deviceInfo.getMacWifi());
            info.setDeviceId1(deviceInfo.getDeviceId1());
            info.setDeviceId2(deviceInfo.getDeviceId2());
            info.setSerialNo(deviceInfo.getSerialNo());
            info.setAndroidId(deviceInfo.getAndroidId());
            info.setFingerPrint(deviceInfo.getFingerPrint());
            info.setImei(deviceInfo.getImei());


            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Please wait While Registration...");
            pDialog.setCancelable(false);
            pDialog.setInverseBackgroundForced(false);
            pDialog.show();

            ApiService apiService = new ApiService();
            apiService.register(loginId, password, info, new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.code() == 200 || response.code() == 201) {
                        pDialog.dismiss();
                        Map<String, Object> result = response.body();
                        double deviceAppId = (double) result.get("deviceAppId");
                        DeviceInfo deviceInfos = helper.getDeviceInfoById(1);
                        deviceInfos.setDeviceCode((String) result.get("deviceCode"));
                        deviceInfos.setDeviceName(deviceName);
                        helper.updateDeviceInfo(deviceInfos);
                        helper.close();

                        Intent intent = new Intent(getApplicationContext(), GetLicenseActivity.class);
                        intent.putExtra("app_status", 0);
                        prefs = getSharedPreferences(prefDeviceAppId, MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("device_app_id", (int) deviceAppId);
                        editor.apply();
                        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.putString("loginId", loginId);
                        edit.putString("password", password);
                        edit.apply();
                        startActivity(intent);
                        finish();
                    } else if (response.code() == 400) {
                        pDialog.dismiss();
                        edLoginId.setError(getString(R.string.error_invalid_credentials));
                        focusView = edLoginId;
                        edPassword.setError(getString(R.string.error_invalid_credentials));
                        focusView = edPassword;
                        cancel = true;
                    } else {
                        pDialog.dismiss();
                        noRegisterLayout.setVisibility(View.GONE);
                        registerLayout.setVisibility(View.GONE);
                        registerFailLayout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    pDialog.dismiss();
                    noRegisterLayout.setVisibility(View.GONE);
                    registerLayout.setVisibility(View.GONE);
                    registerFailLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private boolean minPassword(String password) {
        return password.length() >= 4;
    }

    private boolean maxDeviceName(String deviceName) {
        return deviceName.length() <= 30;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_register) {
            noRegisterLayout.setVisibility(View.GONE);
            registerLayout.setVisibility(View.VISIBLE);
            registerFailLayout.setVisibility(View.GONE);
        }

    }
}
