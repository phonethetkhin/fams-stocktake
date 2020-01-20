package com.t3k.mobile.fams.stocktake.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.t3k.mobile.fams.stocktake.R;
import com.t3k.mobile.fams.stocktake.db.DBHelper;
import com.t3k.mobile.fams.stocktake.model.DeviceInfo;
import com.t3k.mobile.fams.stocktake.model.User;
import com.t3k.mobile.fams.stocktake.retrofit.ApiService;
import com.t3k.mobile.fams.stocktake.retrofit.Constant;
import com.t3k.mobile.fams.stocktake.utils.EncryptionDecryption;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    DBHelper helper;
    Button btnLogin;
    ImageView imgSetting, imgVisibleIcon;
    EditText edLoginId, edPassword;

    private SharedPreferences prefs;
    private String prefName = "user";

    Boolean passwordStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        helper = new DBHelper(this);

        edLoginId = findViewById(R.id.ed_loginId);
        edPassword = findViewById(R.id.ed_password);
        btnLogin = findViewById(R.id.btn_login);
        imgSetting = findViewById(R.id.img_setting);
        imgVisibleIcon = findViewById(R.id.img_visible);

        imgSetting.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        imgVisibleIcon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_login:
                if (helper.getSettingCount() != 0) {
                    getLogin();
                } else {
                    Toast.makeText(getApplicationContext(), "There is no sever connection!Please configure server connection", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.img_setting:
                Intent intent = new Intent(LoginActivity.this, ServerChangeActivity.class);
                intent.putExtra("state", 1);
                startActivity(intent);
                break;
            case R.id.img_visible:
                if (passwordStatus.equals(true)) {
                    imgVisibleIcon.setImageResource(R.drawable.ic_invisible);
                    edPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordStatus = false;
                } else {
                    imgVisibleIcon.setImageResource(R.drawable.ic_visible);
                    edPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordStatus = true;
                }
            default:
                break;
        }
    }


    private void getLogin() {
        DeviceInfo deviceInfo = helper.getDeviceInfoById(1);
        String deviceCode = deviceInfo.getDeviceCode();
        String prefLicense = "licenseKey";
        prefs = getSharedPreferences(prefLicense, MODE_PRIVATE);
        String licenseKey = prefs.getString("license_key", null);
        String prefDeviceAppId = "deviceAppId";
        prefs = getSharedPreferences(prefDeviceAppId, MODE_PRIVATE);
        int deviceAppId = prefs.getInt("device_app_id", 0);
        boolean cancel = false;
        View focusView = null;

        final String loginId = edLoginId.getText().toString();
        final String password = edPassword.getText().toString();

        if (loginId.equals("") && TextUtils.isEmpty(loginId)) {
            edLoginId.setError(getString(R.string.empty_login_id));
            focusView = edLoginId;
            cancel = true;
        }
        if (password.equals("") && TextUtils.isEmpty(password)) {
            edPassword.setError(getString(R.string.empty_password));
            focusView = edPassword;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
            imgVisibleIcon.setVisibility(View.GONE);
        } else {

            Map<String, Object> request = new HashMap<>();
            request.put("appType", Constant.APP_TYPE);
            request.put("loginId", loginId);
            request.put("password", password);
            request.put("deviceCode", deviceCode);
            request.put("licenseKey", licenseKey);
            request.put("deviceAppId", deviceAppId);

            ApiService service = new ApiService();
            service.login(request, new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful()) {
                        int userStatus = 0;
                        Map<String, Object> result = response.body();
                        double userId = (double) result.get("userId");
                        String token = (String) result.get("token");
                        String name = String.valueOf(result.get("userName"));

                        Calendar c = Calendar.getInstance();
                        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
                        String joinDate = dateFormat.format(c.getTime());
                        if (helper.userExists(1)) {
                            try {
                                User users = helper.getUserByLoginId(1);
                                if (!users.getLoginId().equals(loginId)) {
                                    userStatus = 1;
                                }
                                users.setUserId((int) userId);
                                users.setLoginId(loginId);
                                users.setPassword(EncryptionDecryption.encrypt(password));
                                users.setName(name);
                                users.setLastLoginDate(joinDate);
                                helper.updateUser(users);
                                helper.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                User users = new User();
                                users.setUserId((int) userId);
                                users.setLoginId(loginId);
                                users.setPassword(EncryptionDecryption.encrypt(password));
                                users.setName(name);
                                users.setJoinedDate(joinDate);
                                helper.insertUser(users);
                                helper.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.putExtra("userStatus", userStatus);
                        intent.putExtra("download", 1);
                        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("loginId", loginId);
                        editor.putString("token", token);
                        editor.apply();
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "loginID and Password is invalid!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
//                    String storePassword = helper.getSingleEntry(loginId);
//                    try {
//                        String getPwd = EncryptionDecryption.decrypt(storePassword);
//                        if (password.equals(getPwd)) {
//                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//
//                            prefs = getSharedPreferences(prefName, MODE_PRIVATE);
//                            SharedPreferences.Editor editor = prefs.edit();
//
//                            User user = helper.getUserByLoginId(1);
//                            editor.putString("loginId", user.getLoginId());
//                            editor.apply();
//
//                            startActivity(intent);
//                            finish();
//                        } else {
//                            Toast.makeText(getApplicationContext(), "This user is not exists or serverSetting fail.Please,check or login with online!", Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    Toast.makeText(getApplicationContext(), "login fail!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAffinity(LoginActivity.this);
        System.exit(0);
    }
}
