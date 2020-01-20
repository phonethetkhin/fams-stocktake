package com.t3k.mobile.fams.stocktake.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.t3k.mobile.fams.stocktake.R;
import com.t3k.mobile.fams.stocktake.db.DBHelper;
import com.t3k.mobile.fams.stocktake.model.ServerSetting;
import com.t3k.mobile.fams.stocktake.utils.NetworkUtils;
import com.t3k.mobile.fams.stocktake.utils.ValidateIPAddress;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServerChangeActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    DBHelper helper = new DBHelper(this);
    List<ServerSetting> settingList;
    final int timeout = 10000;
    Message msg;
    Handler handler;
    ProgressDialog pDialog;
    Toolbar toolbar;

    private LinearLayout pinLayout, serverChangeLayout;
    private PinView pinCode;
    Spinner spnProtocol;
    EditText edIP, edDomainName, edContactsPath;
    RadioGroup radioGroup;
    RadioButton radioBtnDomain, radioBtnIP;
    Button btnContinue, btnTestConnection, btnSave;

    String protocol, ip, contactsPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_change);

        toolbar = findViewById(R.id.server_toolbar);

        pinLayout = findViewById(R.id.pin_linear);
        serverChangeLayout = findViewById(R.id.server_linear);
        pinCode = findViewById(R.id.pinView);
        spnProtocol = findViewById(R.id.spn_protocol);
        edIP = findViewById(R.id.ed_ip);
        edDomainName = findViewById(R.id.ed_domain);
        edContactsPath = findViewById(R.id.ed_contacts);
        radioGroup = findViewById(R.id.radio_group);
        radioBtnDomain = findViewById(R.id.radio_domain);
        radioBtnIP = findViewById(R.id.radio_ip);
        btnContinue = findViewById(R.id.btn_continue);
        btnTestConnection = findViewById(R.id.btn_testCon);
        btnSave = findViewById(R.id.btn_save);

        setupToolbar(getString(R.string.text_setting));
radioBtnIP.setChecked(true);

        if (helper.getSettingCount() != 0) {
                if(radioBtnIP.isChecked()) {
                    edDomainName.setVisibility(View.GONE);
                    edIP.setVisibility(View.VISIBLE);
                    ServerSetting serverAddress = helper.getSettingById(2);
                    edIP.setText(serverAddress.getSettingValue());
                    ServerSetting serverContactPath = helper.getSettingById(3);
                    edContactsPath.setText(serverContactPath.getSettingValue());
                }
        } else {
            radioBtnDomain.setChecked(true);
            edIP.setText("");
            edContactsPath.setText("");
        }

        btnSave.setEnabled(false);
        radioGroup.setOnCheckedChangeListener(this);
        btnContinue.setOnClickListener(this);
        btnTestConnection.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radio_domain:
                edDomainName.setVisibility(View.VISIBLE);
                edIP.setVisibility(View.GONE);
                break;
            case R.id.radio_ip:
                edIP.setVisibility(View.VISIBLE);
                edDomainName.setVisibility(View.GONE);
                break;
            default:
        }
    }

    @Override
    public void onClick(View v) {
        String secret_pwd = "12345";
        final NetworkUtils networkUtils = new NetworkUtils(this);
        if (v.getId() == R.id.btn_continue) {
            String verificationCode = pinCode.getText().toString();
            if (verificationCode.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please,enter Secret Code!", Toast.LENGTH_SHORT).show();
            } else if (verificationCode.equals(secret_pwd)) {
                pinLayout.setVisibility(View.GONE);
                serverChangeLayout.setVisibility(View.VISIBLE);
            } else if (!verificationCode.equals(secret_pwd)) {
                Toast.makeText(getApplicationContext(), "Secret Code is invalid!", Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.btn_testCon) {
            if (networkUtils.isConnectingToInternet()) {
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Connecting to Server...");
                pDialog.setCancelable(false);
                pDialog.setInverseBackgroundForced(false);
                pDialog.show();
                getConnectingServer();
            } else {
                Toast.makeText(getApplicationContext(), "Connection is Offline!", Toast.LENGTH_SHORT).show();
            }

        } else if (v.getId() == R.id.btn_save) {
            saveUrl();
        }
    }

    private void getConnectingServer() {
        boolean cancel = false;
        View focusView = null;
        ValidateIPAddress validateIPAddress = new ValidateIPAddress();
        String status = String.valueOf(spnProtocol.getSelectedItemId());
        if (status.equals("0")) {
            protocol = "http://";
        } else if (status.equals("1")) {
            protocol = "https://";
        }
        if (radioBtnDomain.isChecked()) {
            ip = edDomainName.getText().toString();
            if ("".equals(ip.trim()) || edDomainName.getText() == null || TextUtils.isEmpty(ip)) {
                edDomainName.setError(getString(R.string.error_empty_domain_name));
                focusView = edDomainName;
                cancel = true;
            } else if (!validateIPAddress.isDomainName(ip)) {
                edDomainName.setError(getString(R.string.error_invalid_domain_name));
                focusView = edDomainName;
                cancel = true;
            }
        } else if (radioBtnIP.isChecked()) {
            ip = edIP.getText().toString();
            if ("".equals(ip.trim()) || edIP.getText() == null || TextUtils.isEmpty(ip)) {
                edIP.setError(getString(R.string.error_empty_ip_address));
                focusView = edIP;
                cancel = true;
            } else if (!validateIPAddress.isIPAddress(ip)) {
                edIP.setError(getString(R.string.error_invalid_ip_address));
                focusView = edIP;
                cancel = true;
            }
        }

        contactsPath = edContactsPath.getText().toString();
        if ("".equals(contactsPath.trim()) || edContactsPath.getText() == null || TextUtils.isEmpty(contactsPath)) {
            edContactsPath.setError(getString(R.string.error_empty_contacts_path));
            focusView = edContactsPath;
            cancel = true;
        }
        final String url = protocol + ip + "/" + contactsPath + "/";

        if (cancel) {
            focusView.requestFocus();
            pDialog.dismiss();
        } else {
            if (helper.getSettingCount() != 0) {
                new Thread() {
                    @Override
                    public void run() {
                        msg = new Message();
                        try {
                            URL myUrl = new URL(url);
                            URLConnection connection = myUrl.openConnection();
                            connection.setConnectTimeout(timeout);
                            connection.connect();
                            msg.arg1 = 1;
                            handler.sendMessage(msg);
                        } catch (UnknownHostException uhe) {
                            msg.arg1 = 2;
                            handler.sendMessage(msg);
                        } catch (IOException ioe) {
                            msg.arg1 = 3;
                            handler.sendMessage(msg);
                        }
                    }
                }.start();
            } else {
                new Thread() {
                    @Override
                    public void run() {
                        msg = new Message();
                        try {
                            URL myUrl = new URL(url);
                            URLConnection connection = myUrl.openConnection();
                            connection.setConnectTimeout(timeout);
                            connection.connect();
                            msg.arg1 = 1;
                            handler.sendMessage(msg);
                        } catch (UnknownHostException uhe) {
                            msg.arg1 = 2;
                            handler.sendMessage(msg);
                        } catch (IOException ioe) {
                            msg.arg1 = 3;
                            handler.sendMessage(msg);
                        }
                    }

                }.start();
            }

            handler = new Handler(msg -> {
                if (msg.arg1 == 1) {
                    pDialog.dismiss();
                    btnSave.setEnabled(true);
                    Snackbar.make(pinLayout, "ServerSetting connection is successful!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else if (msg.arg1 == 2) {
                    pDialog.dismiss();
                    Snackbar.make(pinLayout, "Couldn't resolve the ServerSetting host provided!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    pinLayout.setVisibility(View.GONE);
                    serverChangeLayout.setVisibility(View.VISIBLE);

                    if (helper.getSettingCount() != 0) {
                        ServerSetting serverAddress = helper.getSettingById(2);
                        edIP.setText(serverAddress.getSettingValue());
                        ServerSetting serverContactPath = helper.getSettingById(3);
                        edContactsPath.setText(serverContactPath.getSettingValue());
                    } else {
                        edIP.setText("");
                        edContactsPath.setText("");
                    }
                } else if (msg.arg1 == 3) {
                    pDialog.dismiss();
                    Snackbar.make(pinLayout, "Can't Connect to the server!!!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    pinLayout.setVisibility(View.GONE);
                    serverChangeLayout.setVisibility(View.VISIBLE);
//here displaying from last login result if can't connect

                    if (helper.getSettingCount() != 0) {
                        ServerSetting serverAddress = helper.getSettingById(2);

                        // edIP.setText(serverAddress.getSettingValue());

                        ServerSetting serverContactPath = helper.getSettingById(3);
                        //edContactsPath.setText(serverContactPath.getSettingValue());
                    } else {
                        edIP.setText("");
                        edContactsPath.setText("");
                    }
                }
                return false;
            });
        }
    }

    private void saveUrl() {

        if (helper.getSettingCount() != 0) {
            settingList = new ArrayList<>();

            ServerSetting settingProtocol = helper.getSettingById(1);
            settingProtocol.setSettingValue(protocol);

            ServerSetting settingAddress = helper.getSettingById(2);
            settingAddress.setSettingValue(ip);

            ServerSetting settingContactPath = helper.getSettingById(3);
            settingContactPath.setSettingValue(contactsPath);

            settingList.add(settingProtocol);
            settingList.add(settingAddress);
            settingList.add(settingContactPath);
            for (ServerSetting remainServerSetting : settingList) {
  //saving
                helper.updateSetting(remainServerSetting);
            }

        } else {
            settingList = new ArrayList<>();

            ServerSetting serverSetting = new ServerSetting();
            serverSetting.setSettingGroup("server");
            serverSetting.setSettingName("protocol");
            serverSetting.setSettingValue(protocol);

            ServerSetting serverSetting1 = new ServerSetting();
            serverSetting1.setSettingGroup("server");
            serverSetting1.setSettingName("domain");
            serverSetting1.setSettingValue(edIP.getText().toString());

            ServerSetting serverSetting2 = new ServerSetting();
            serverSetting2.setSettingGroup("server");
            serverSetting2.setSettingName("context");
            serverSetting2.setSettingValue(edContactsPath.getText().toString());

            settingList.add(serverSetting);
            settingList.add(serverSetting1);
            settingList.add(serverSetting2);

            for (ServerSetting setting : settingList) {
                helper.addSetting(setting);
            }
        }

        int state = getIntent().getIntExtra("state", 0);
        if (state == 1) {

            Intent i=new Intent(this,SplashActivity.class);
            ProcessPhoenix.triggerRebirth(this,i);
        } else {
            this.finishAffinity();
            android.os.Process.killProcess(android.os.Process.myPid());
        }

    }

    private void setupToolbar(String title) {
        setSupportActionBar(toolbar);

        int state = getIntent().getIntExtra("state", 0);
        if (state == 1) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(false);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
        getSupportActionBar().setTitle(title);
        getWindow().setWindowAnimations(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
