package com.t3k.mobile.fams.stocktake.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.t3k.mobile.fams.stocktake.R;
import com.t3k.mobile.fams.stocktake.db.DBHelper;
import com.t3k.mobile.fams.stocktake.fragment.DownloadAssetFragment;
import com.t3k.mobile.fams.stocktake.fragment.HomeFragment;
import com.t3k.mobile.fams.stocktake.fragment.LocationListFragment;
import com.t3k.mobile.fams.stocktake.model.DeviceInfo;
import com.t3k.mobile.fams.stocktake.model.DownloadInfo;
import com.t3k.mobile.fams.stocktake.model.LocationsBean;
import com.t3k.mobile.fams.stocktake.model.User;
import com.t3k.mobile.fams.stocktake.retrofit.ApiService;
import com.t3k.mobile.fams.stocktake.retrofit.Constant;
import com.t3k.mobile.fams.stocktake.utils.Event;
import com.t3k.mobile.fams.stocktake.utils.HomeListener;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, HomeListener {

    private DBHelper helper;
    private DrawerLayout drawer;
    private Stage mStage = Stage.HOME;

    private SharedPreferences prefs;
    private String prefName = "user";
    TextView loginUser, lastSync;

    enum Stage {
        HOME,
        DOWNLOAD,
        LOCATION
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        helper = new DBHelper(this);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);
        loginUser = hView.findViewById(R.id.txt_login_user);
        lastSync = hView.findViewById(R.id.txt_last_sync);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        mStage = Stage.HOME;

        int download = getIntent().getIntExtra("download", 0);
        int userStatus = getIntent().getIntExtra("userStatus", 0);
        if (userStatus == 1) {
            helper.deleteAllLocations();
            helper.deleteAllAssets();
            helper.deleteInfo();
            helper.deleteAllPhotos();
        }
        if (download == 1) {
            OnLocationSearch();
        }
        getWindow().setWindowAnimations(0);

        User user = helper.getUserByLoginId(1);
        loginUser.setText(user.getName());
        int id = 1;
        if (helper.getInfoCount() != 0) {
            final DownloadInfo info = helper.getDownloadInfoById(id);
            String downloadDate = convertStringDateToAnotherStringDate(info.getDownloadDate(), "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd HH:mm:ss");
            lastSync.setVisibility(View.VISIBLE);
            lastSync.setText(downloadDate);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.nav_home:
                onHome();
                break;
            case R.id.nav_download:
                if (helper.getLocationCount() != 0) {
                    onDownload();
                } else {
                    Toast.makeText(getApplicationContext(), "Locations are not exists!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_scan:
                Intent intentStokeTake = new Intent(HomeActivity.this, StockTakingActivity.class);
                startActivity(intentStokeTake);
                finish();
                break;
            case R.id.nav_upload:
                Intent intentUpload = new Intent(HomeActivity.this, SelectUploadLocationActivity.class);
                startActivity(intentUpload);
                finish();
                break;
            case R.id.nav_location:
                onLocation();
                break;
            case R.id.nav_uploaded:
                Intent intent = new Intent(HomeActivity.this, UploadHistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_exit:
                this.finishAffinity();
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            default:
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void OnLocationSearch() {
        DeviceInfo deviceInfo = helper.getDeviceInfoById(1);
        String deviceCode = deviceInfo.getDeviceCode();
        String prefLicense = "licenseKey";
        prefs = getSharedPreferences(prefLicense, MODE_PRIVATE);
        String licenseKey = prefs.getString("license_key", null);
        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        User user = helper.getUserByLoginId(1);
        int userId = user.getUserId();
        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        String token = prefs.getString("token", null);

        Map<String, Object> request = new HashMap<>();
        request.put("appType", Constant.APP_TYPE);
        request.put("deviceCode", deviceCode);
        request.put("licenseKey", licenseKey);
        request.put("userId", userId);
        request.put("token", token);
        ApiService apiService = new ApiService();
        apiService.locationSearch(request, new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Object> locationsBeanArrayList;
                    Map<String, Object> result = response.body();
                    locationsBeanArrayList = (ArrayList<Object>) result.get("locationList");
                    String status = (String) result.get("status");
                    if (status.equals("SUCCESS")) {
                        if (locationsBeanArrayList != null) {
                            for (Object object : locationsBeanArrayList) {
                                Map<String, Object> s = (Map<String, Object>) object;
                                double id = (double) s.get("id");
                                if (helper.checkLocationExists((int) id)) {
                                    LocationsBean updateLocation = helper.getLocationById((int) id);
                                    updateLocation.setLocationId((int) id);
                                    updateLocation.setParentName((String) s.get("parentName"));
                                    updateLocation.setCode((String) s.get("code"));
                                    updateLocation.setName((String) s.get("name"));
                                    helper.updateLocation(updateLocation);
                                } else {
                                    LocationsBean newLocation = new LocationsBean();
                                    newLocation.setLocationId((int) id);
                                    newLocation.setParentName((String) s.get("parentName"));
                                    newLocation.setCode((String) s.get("code"));
                                    newLocation.setName((String) s.get("name"));
                                    helper.addLocation(newLocation);
                                }
                            }
                            EventBus.getDefault().post(Event.Location);
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "fail!!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onHome() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        HomeFragment homeFragment = new HomeFragment();
        transaction.replace(R.id.fragment_container, homeFragment);
        transaction.addToBackStack(null);
        getSupportActionBar().setTitle("Home");
        mStage = Stage.HOME;
        transaction.commit();
    }

    @Override
    public void onDownload() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        DownloadAssetFragment assetFragment = new DownloadAssetFragment();
        transaction.replace(R.id.fragment_container, assetFragment);
        transaction.addToBackStack(null);
        mStage = Stage.DOWNLOAD;
        getSupportActionBar().setTitle("Download Asset");
        transaction.commit();
    }

    @Override
    public void onLocation() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        LocationListFragment locationFragment = new LocationListFragment();
        transaction.replace(R.id.fragment_container, locationFragment);
        transaction.addToBackStack(null);
        getSupportActionBar().setTitle("Location List");
        mStage = Stage.LOCATION;
        transaction.commit();
    }

    private String convertStringDateToAnotherStringDate(String stringDate, String stringDateFormat, String returnDateFormat) {

        try {
            Date date = new SimpleDateFormat(stringDateFormat).parse(stringDate);
            String returnDate = new SimpleDateFormat(returnDateFormat).format(date);
            return returnDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void onBackPressed() {
        switch (mStage) {
            case HOME:
                ActivityCompat.finishAffinity(this);
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case DOWNLOAD:
                drawer.closeDrawer(GravityCompat.START);
                onHome();
                break;
            case LOCATION:
                drawer.closeDrawer(GravityCompat.START);
                onHome();
            default:
                drawer.closeDrawer(GravityCompat.START);
                onHome();
                break;
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
