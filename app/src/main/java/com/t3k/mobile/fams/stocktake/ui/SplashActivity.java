package com.t3k.mobile.fams.stocktake.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.t3k.mobile.fams.stocktake.R;
import com.t3k.mobile.fams.stocktake.db.DBHelper;
import com.t3k.mobile.fams.stocktake.model.DeviceInfo;
import com.t3k.mobile.fams.stocktake.retrofit.ApiService;
import com.t3k.mobile.fams.stocktake.utils.AppPermission;
import com.t3k.mobile.fams.stocktake.utils.NetworkUtils;
import com.t3k.mobile.fams.stocktake.utils.TelephonyInfo;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 0;
    private static final int REQUEST_PHONE_PERMISSION_CODE = 1;
    private static final int REQUEST_STORAGE_PERMISSION_CODE = 2;
    DeviceInfo info;
    Animation animation;
    ImageView famsLogo, reloadIcon;
    TextView txtStockTaker;
    private ProgressBar pBar;
    private DBHelper helper;
    private String deviceId2 = "";
    private SharedPreferences prefs;
    private String prefLicense = "licenseKey";
    private String prefDeviceAppId = "deviceAppId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        helper = new DBHelper(this);
        famsLogo = findViewById(R.id.fams_logo);
        reloadIcon = findViewById(R.id.img_reload);
        txtStockTaker = findViewById(R.id.txt_stock_taker);
        pBar = findViewById(R.id.progressBar);
        animation = AnimationUtils.loadAnimation(this, R.anim.splash_anim);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(400);
        txtStockTaker.setAnimation(animation);
        checkPermission();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                showScreen();
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                        shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION_CODE);
                }
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
                    ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PHONE_PERMISSION_CODE);
                }
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
            }
        } else {
            showScreen();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Application need permission", Toast.LENGTH_SHORT).show();
            } else {
                showScreen();
            }
        } else if (requestCode == REQUEST_PHONE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Application need read phone permission", Toast.LENGTH_SHORT).show();
            } else {
                showScreen();
            }
        } else if (requestCode == REQUEST_STORAGE_PERMISSION_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Application need storage permission", Toast.LENGTH_SHORT).show();
            } else {
                showScreen();
            }
        }
    }

    private void showScreen() {
        final Handler handler = new Handler();
        final int SPLASH_DISPLAY_LENGTH = 3000;
        final NetworkUtils networkUtils = new NetworkUtils(this);
        reloadIcon.setVisibility(View.GONE);
        pBar.setVisibility(View.VISIBLE);
        pBar.setProgress(300);

        handler.postDelayed(() -> {
            if (networkUtils.isConnectingToInternet()) {
                if (helper.getSettingCount() != 0) {
                    Connection();
                } else {
                    Intent intent = new Intent(SplashActivity.this, ServerChangeActivity.class);
                    intent.putExtra("state", 0);
                    startActivity(intent);
                    finish();
                }
            } else {
                pBar.setVisibility(View.GONE);
                reloadIcon.setVisibility(View.VISIBLE);
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void Connection() {
        ApiService service = new ApiService();
        service.connectionTest(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    saveDeviceInfo();
                    checkRegister();
                    Intent intent = new Intent(SplashActivity.this, ServerChangeActivity.class);
                    intent.putExtra("state", 1);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, ServerChangeActivity.class);
                    intent.putExtra("state", 0);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SplashActivity.this, ServerChangeActivity.class);
                intent.putExtra("state", 0);
                startActivity(intent);
                finish();
            }
        });
    }

    private void saveDeviceInfo() {
        if (helper.getDeviceCount() == 0) {
            deviceInfo();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkRegister() {
        DeviceInfo deviceInfo = helper.getDeviceInfoById(1);
        info = new DeviceInfo();
        info.setDeviceCode(deviceInfo.getDeviceCode());
        info.setBrand(deviceInfo.getBrand());
        info.setModel(deviceInfo.getModel());
        info.setDeviceId1(deviceInfo.getDeviceId1());
        info.setSerialNo(deviceInfo.getSerialNo());
        info.setAndroidId(deviceInfo.getAndroidId());
        info.setFingerPrint(deviceInfo.getFingerPrint());
        ApiService service = new ApiService();
        service.checkRegister(deviceInfo, new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.code() == 200) {
                    Map<String, Object> result = response.body();
                    String deviceState = (String) result.get("deviceState");
                    double deviceAppId = (double) result.get("deviceAppId");
                    prefs = getSharedPreferences(prefDeviceAppId, MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("device_app_id", (int) deviceAppId);
                    editor.apply();
                    DeviceInfo deviceInfo1 = helper.getDeviceInfoById(1);
                    deviceInfo1.setDeviceCode((String) result.get("deviceCode"));
                    helper.updateDeviceInfo(deviceInfo1);
                    if (deviceState.equals("ACTIVE")) {
                        checkLicense();
                    } else if (deviceState.equals("IN_ACTIVE")) {
                        Intent intent = new Intent(SplashActivity.this, GetRegisterActivity.class);
                        intent.putExtra("device_status", 1);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Intent intent = new Intent(SplashActivity.this, GetRegisterActivity.class);
                    intent.putExtra("device_status", 0);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                pBar.setVisibility(View.GONE);
                reloadIcon.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkLicense() {
        prefs = getSharedPreferences(prefDeviceAppId, MODE_PRIVATE);
        int deviceAppId = prefs.getInt("device_app_id", 0);
        String prefName = "user";
        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        String loginId = prefs.getString("loginId", null);
        String password = prefs.getString("password", null);
        if (deviceAppId == 0) {
            Intent intent = new Intent(SplashActivity.this, GetRegisterActivity.class);
            intent.putExtra("device_status", 0);
            startActivity(intent);
            finish();
        } else {
            if (loginId == null || password == null) {
                loginId = "";
                password = "";
            }
            DeviceInfo deviceInfo = helper.getDeviceInfoById(1);
            info = new DeviceInfo();
            info.setDeviceCode(deviceInfo.getDeviceCode());
            info.setBrand(deviceInfo.getBrand());
            info.setModel(deviceInfo.getModel());
            info.setDeviceId1(deviceInfo.getDeviceId1());
            info.setSerialNo(deviceInfo.getSerialNo());
            info.setAndroidId(deviceInfo.getAndroidId());
            info.setFingerPrint(deviceInfo.getFingerPrint());
            ApiService service = new ApiService();
            service.checkLicense(deviceAppId, loginId, password, info, new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.code() == 200) {
                        Map<String, Object> result = response.body();

                        if (result != null) {
                            String licenseKey = String.valueOf(result.get("licenseKey"));
                            String appStatus = String.valueOf(result.get("appStatus"));
                            if (appStatus.equals("REQUEST_PENDING")) {
                                Intent intent = new Intent(SplashActivity.this, GetLicenseActivity.class);
                                intent.putExtra("license_status", 1);
                                startActivity(intent);
                                finish();
                            } else if (appStatus.equals("NO_LICENSED")) {
                                Intent intent = new Intent(SplashActivity.this, GetLicenseActivity.class);
                                intent.putExtra("license_status", 0);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                prefs = getSharedPreferences(prefLicense, MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("license_key", licenseKey);
                                editor.apply();
                                startActivity(intent);
                                finish();
                            }
                        }
                    } else if (response.code() == 406) {
                        Intent intent = new Intent(SplashActivity.this, GetLicenseActivity.class);
                        intent.putExtra("license_status", 2);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(SplashActivity.this, GetLicenseActivity.class);
                        intent.putExtra("license_status", 0);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    pBar.setVisibility(View.GONE);
                    reloadIcon.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void Reload(View view) {
        showScreen();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void deviceInfo() {
        AppPermission.checkPermission(this);

        String brand = Build.BRAND.toUpperCase();
        String model = Build.MODEL;
        String osVersion = Build.VERSION.RELEASE;
        String device = Build.DEVICE;
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macWifi = wInfo.getMacAddress();
        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);
        AppPermission.checkPermission(this);

        String deviceId1 = "";
        if (telephonyInfo.isSIM2Ready() && telephonyInfo.isSIM1Ready()) {
            deviceId1 = TelephonyMgr.getDeviceId(0);
            deviceId2 = TelephonyMgr.getDeviceId(1);
        } else {
            deviceId1 = TelephonyMgr.getDeviceId();
        }
        String serialNo = Build.SERIAL;
        String androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String fingerPrint = Build.FINGERPRINT;

        String imei = "";
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            imei = TelephonyMgr.getImei();
        } else {
            imei = TelephonyMgr.getDeviceId();
        }

        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setBrand(brand);
        deviceInfo.setModel(model);
        deviceInfo.setOsVersion(osVersion);
        deviceInfo.setDevice(device);
        deviceInfo.setMacWifi(macWifi);
        deviceInfo.setDeviceId1(deviceId1);
        deviceInfo.setDeviceId2(deviceId2);
        deviceInfo.setSerialNo(serialNo);
        deviceInfo.setAndroidId(androidId);
        deviceInfo.setFingerPrint(fingerPrint);
        deviceInfo.setImei(imei);

        helper.addDeviceInfo(deviceInfo);
        helper.close();
    }
}
