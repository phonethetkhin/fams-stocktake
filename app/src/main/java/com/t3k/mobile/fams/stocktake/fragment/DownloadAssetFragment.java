package com.t3k.mobile.fams.stocktake.fragment;


import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.t3k.mobile.fams.stocktake.R;
import com.t3k.mobile.fams.stocktake.adapter.SelectLocationAdapter;
import com.t3k.mobile.fams.stocktake.adapter.SelectedLocationAdapter;
import com.t3k.mobile.fams.stocktake.db.DBHelper;
import com.t3k.mobile.fams.stocktake.model.AssetBean;
import com.t3k.mobile.fams.stocktake.model.DeviceInfo;
import com.t3k.mobile.fams.stocktake.model.DownloadInfo;
import com.t3k.mobile.fams.stocktake.model.LocationsBean;
import com.t3k.mobile.fams.stocktake.model.User;
import com.t3k.mobile.fams.stocktake.retrofit.ApiService;
import com.t3k.mobile.fams.stocktake.retrofit.Constant;
import com.t3k.mobile.fams.stocktake.ui.HomeActivity;
import com.t3k.mobile.fams.stocktake.ui.LoginActivity;
import com.t3k.mobile.fams.stocktake.utils.Event;
import com.t3k.mobile.fams.stocktake.utils.NetworkUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class DownloadAssetFragment extends Fragment implements View.OnClickListener {

    private DBHelper helper;
    private List<LocationsBean> locationsList;
    ArrayList<String> titleList;
    ArrayList<String> locateIdList;
    ArrayList<Integer> locationIdList = new ArrayList<>();
    ArrayList<LocationsBean> selectedLocations = null;
    boolean[] checkedItem;
    private RecyclerView recyclerLocation;
    private SelectedLocationAdapter selectedLocationAdapter;
    private SelectLocationAdapter locationAdapter;
    private Button btnDownloadAsset;
    private Button btnDownloadBack;

    public DownloadAssetFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download_asset, container, false);
        helper = new DBHelper(getActivity());
        recyclerLocation = view.findViewById(R.id.recycler_location);
        Button btnChooseLocation = view.findViewById(R.id.btn_choose_location);
        btnDownloadAsset = view.findViewById(R.id.btn_download_asset);
        btnDownloadBack = view.findViewById(R.id.btn_download_back);

        locationsList = new ArrayList<>();
        selectedLocationAdapter = new SelectedLocationAdapter(getActivity(), locationsList);
        recyclerLocation.setAdapter(selectedLocationAdapter);
        recyclerLocation.setLayoutManager(new LinearLayoutManager(getActivity()));
        getLocationList();

        btnChooseLocation.setOnClickListener(this);
        btnDownloadAsset.setOnClickListener(this);
        btnDownloadBack.setOnClickListener(this);
        EventBus.getDefault().register(this);
        return view;
    }

    private void getLocationList() {
        if (helper.getInfoCount() != 0) {
            int id = 1;
            final DownloadInfo info = helper.getDownloadInfoById(id);

            final ArrayList<LocationsBean> locations = new ArrayList<>();
            ArrayList<String> items = new ArrayList<>(Arrays.asList(info.getLocationId().split(",")));

            for (int i = 0; i < items.size(); i++) {

                int locationId = Integer.parseInt(items.get(i));
                if (locationId != 0) {
                    LocationsBean location = helper.getLocationById(locationId);
                    locations.add(location);
                }
            }

            Collections.sort(locations, (location1, location2) -> {
                if (location1.getCode() != null && location2.getCode() != null) {
                    return location1.getCode().compareTo(location2.getCode());
                }
                return 0;
            });

            selectedLocationAdapter = new SelectedLocationAdapter(getContext(), locations);
            recyclerLocation.setAdapter(selectedLocationAdapter);
            recyclerLocation.setLayoutManager(new LinearLayoutManager(getActivity()));
            selectedLocationAdapter.notifyDataSetChanged();
        }
    }

    private void SelectLocation() {
        titleList = new ArrayList<>();
        locationIdList = new ArrayList<>();
        locationsList = helper.getAllLocation();
        for (LocationsBean locationsBean : locationsList) {
            titleList.add(locationsBean.getName());
        }
        Collections.sort(titleList);
        String[] titles = titleList.toArray(new String[0]);
        checkedItem = new boolean[titles.length];

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_location_title);
        builder.setCancelable(false);
        builder.setMultiChoiceItems(titles, checkedItem, (dialogInterface, i, b) -> {
            if (b) {
                locationIdList.add(i);
            } else if (locationIdList.contains(i)) {
                locationIdList.remove(Integer.valueOf(i));
            }
        });
        builder.setPositiveButton(R.string.btn_done, (dialog, which) -> {
            String item;
            selectedLocations = new ArrayList<>();
            for (int i = 0; i < locationIdList.size(); i++) {
                item = titles[locationIdList.get(i)];
                LocationsBean location = helper.getLocationByName(item);
                selectedLocations.add(location);
            }

            Collections.sort(selectedLocations, (location1, location2) -> {
                if (location1.getCode() != null && location2.getCode() != null) {
                    return location1.getCode().compareTo(location2.getCode());
                }
                return 0;
            });

            locationAdapter = new SelectLocationAdapter(getContext(), selectedLocations);
            recyclerLocation.setAdapter(locationAdapter);
            recyclerLocation.setLayoutManager(new LinearLayoutManager(getActivity()));
            locationAdapter.notifyDataSetChanged();
            EventBus.getDefault().post(Event.Selected_Location);
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_choose_location) {
            SelectLocation();
        } else if (id == R.id.btn_download_asset) {
            if (selectedLocations != null) {
                if (selectedLocations.size() != 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.title_download_confirm);
                    builder.setMessage(R.string.warning_down_confirm);
                    builder.setCancelable(false);
                    builder.setPositiveButton(R.string.btn_done, (dialog, which) -> {
                        NetworkUtils utils = new NetworkUtils(v.getContext());
                        if (utils.isConnectingToInternet()) {
                            DownloadAsset();
                        } else {
                            Toast.makeText(getActivity(), "Internet connection is offline!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton(R.string.btn_cancel, (dialog, which) -> {
                    });
                    builder.show();

                } else {
                    Toast.makeText(getActivity(), "please select location!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "please select location!", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.btn_download_back) {
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            getActivity().startActivity(intent);
            getActivity().finish();
        }
    }

    private void DownloadAsset() {
        btnDownloadAsset.setVisibility(View.GONE);
        DeviceInfo deviceInfo = helper.getDeviceInfoById(1);
        String deviceCode = deviceInfo.getDeviceCode();
        String prefLicense = "licenseKey";
        SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences(prefLicense, MODE_PRIVATE);
        String licenseKey = prefs.getString("license_key", null);
        String prefName = "user";
        prefs = Objects.requireNonNull(getActivity()).getSharedPreferences(prefName, MODE_PRIVATE);
        User user = helper.getUserByLoginId(1);
        int userId = user.getUserId();
        String token = prefs.getString("token", null);
        String prefDeviceAppId = "deviceAppId";
        prefs = getActivity().getSharedPreferences(prefDeviceAppId, MODE_PRIVATE);
        int deviceAppId = prefs.getInt("device_app_id", 0);

        LocationsBean location;
        String locationIds = "";

        locateIdList = new ArrayList<>();
        for (LocationsBean locationsBean : selectedLocations) {
            location = helper.getLocationById(locationsBean.getLocationId());
            location.setDownload(-1);

            helper.updateLocation(location);
            locateIdList.add(String.valueOf(locationsBean.getLocationId()));
            if (locationIds != "") {
                locationIds = locationIds + ",";
            }
            locationIds = locationIds + locationsBean.getLocationId();

            Map<String, Object> request = new HashMap<>();
            request.put("appType", Constant.APP_TYPE);
            request.put("deviceCode", deviceCode);
            request.put("licenseKey", licenseKey);
            request.put("userId", userId);
            request.put("token", token);
            request.put("deviceAppId", deviceAppId);
            request.put("locationIds", String.valueOf(locationsBean.getLocationId()));

            helper.deleteAllAssets();
            helper.deleteInfo();
            helper.deleteAllPhotos();
            helper.exportDB();

            EventBus.getDefault().post(Event.Selected_Progress);

            final LocationsBean finalLocation = location;
            ApiService apiService = new ApiService();
            apiService.assetSearch(request, new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            ArrayList<Object> assetsBeanArrayList;
                            Map<String, Object> result = response.body();
                            double downloadId = (double) result.get("downloadId");
                            assetsBeanArrayList = (ArrayList<Object>) result.get("assetList");
                            if (assetsBeanArrayList != null) {
                                for (Object object : assetsBeanArrayList) {
                                    Map<String, Object> s = (Map<String, Object>) object;
                                    double id = (double) s.get("assetId");
                                    double locationId = (double) s.get("locationId");
                                    AssetBean newAsset = new AssetBean();
                                    newAsset.setLocationId((int) locationId);
                                    newAsset.setAssetId((int) id);
                                    newAsset.setCostCenter((String) s.get("costCenter"));
                                    newAsset.setFaNumber((String) s.get("assetNo"));
                                    newAsset.setItemName((String) s.get("itemName"));
                                    newAsset.setCondition((String) s.get("condition"));
                                    newAsset.setCategory((String) s.get("category"));
                                    newAsset.setBrand((String) s.get("brand"));
                                    newAsset.setModel((String) s.get("model"));
                                    helper.addAsset(newAsset);
                                    finalLocation.setDownload((int) downloadId);
                                    helper.updateLocation(finalLocation);
                                }
                                EventBus.getDefault().post(Event.Select_Done);
                            }
                            btnDownloadBack.setVisibility(View.VISIBLE);
                        } else {
                            btnDownloadBack.setVisibility(View.VISIBLE);
                            finalLocation.setDownload(0);
                            helper.updateLocation(finalLocation);
                            EventBus.getDefault().post(Event.Select_Empty);
                        }
                    } else if (response.code() == 408) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.dialog_session_expired);
                        builder.setCancelable(false);
                        builder.setPositiveButton("Ok", (dialog, which) -> {
                            finalLocation.setDownload(0);
                            helper.updateLocation(finalLocation);
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            getActivity().startActivity(intent);
                            getActivity().finish();
                        });
                        builder.show();
                    } else if (response.code() == 403) {
                        btnDownloadBack.setVisibility(View.VISIBLE);
                        finalLocation.setDownload(0);
                        helper.updateLocation(finalLocation);
                        EventBus.getDefault().post(Event.Select_Empty);
                    } else {
                        btnDownloadBack.setVisibility(View.VISIBLE);
                        finalLocation.setDownload(0);
                        helper.updateLocation(finalLocation);
                        EventBus.getDefault().post(Event.Select_Empty);
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    btnDownloadBack.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        Calendar c = Calendar.getInstance();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
        String downloadDate = dateFormat.format(c.getTime());

        DownloadInfo info = new DownloadInfo();
        info.setDownloadUserId(user.getId());
        info.setDownloadDate(downloadDate);
        info.setLocationId(locationIds);
        helper.addInfo(info);
        helper.close();
        getLocationList();
    }

    @Subscribe
    public void onEvent(Event event) {
        if (event.equals(Event.Selected_Progress) || event.equals(Event.Select_Done) || event.equals(Event.Select_Empty)) {
            getLocationList();
        } else if (event.equals(Event.Selected_Location)) {
            locationAdapter = new SelectLocationAdapter(getContext(), selectedLocations);
            recyclerLocation.setAdapter(locationAdapter);
            recyclerLocation.setLayoutManager(new LinearLayoutManager(getActivity()));
            locationAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
