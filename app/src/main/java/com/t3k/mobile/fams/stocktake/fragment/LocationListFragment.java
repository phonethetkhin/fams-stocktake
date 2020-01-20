package com.t3k.mobile.fams.stocktake.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.t3k.mobile.fams.stocktake.R;
import com.t3k.mobile.fams.stocktake.adapter.LocationListAdapter;
import com.t3k.mobile.fams.stocktake.db.DBHelper;
import com.t3k.mobile.fams.stocktake.model.DeviceInfo;
import com.t3k.mobile.fams.stocktake.model.LocationsBean;
import com.t3k.mobile.fams.stocktake.model.User;
import com.t3k.mobile.fams.stocktake.retrofit.ApiService;
import com.t3k.mobile.fams.stocktake.retrofit.Constant;
import com.t3k.mobile.fams.stocktake.utils.Event;
import com.t3k.mobile.fams.stocktake.utils.NetworkUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class LocationListFragment extends Fragment {

    DBHelper helper;
    private List<LocationsBean> locationsBeanList;
    private ListView locationView;
    private TextView txtNoLocation;
    private LocationListAdapter locationListAdapter;
    private Animation animation;

    public LocationListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        helper = new DBHelper(getActivity());
        View view = inflater.inflate(R.layout.fragment_location_list, container, false);
        locationView = view.findViewById(R.id.location_view);
        txtNoLocation = view.findViewById(R.id.txt_no_location);
        locationsBeanList = new ArrayList<>();
        locationListAdapter = new LocationListAdapter(locationsBeanList, getActivity());
        locationView.setAdapter(locationListAdapter);
        getLocationList();
        locationListAdapter.notifyDataSetChanged();

        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.load_anim);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setRepeatCount(Animation.RELATIVE_TO_PARENT);
        animation.setDuration(800);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_reload, menu);
        ImageView syncLocation = (ImageView) menu.findItem(R.id.menu_reload).getActionView();
        syncLocation.setImageResource(R.drawable.ic_sync);
        syncLocation.setPadding(20, 20, 20, 20);
        if (syncLocation != null) {
            syncLocation.setOnClickListener(v -> {
                v.startAnimation(animation);
                Toast.makeText(getActivity(), "Location Sync!!!", Toast.LENGTH_SHORT).show();
                NetworkUtils utils = new NetworkUtils(v.getContext());
                if (utils.isConnectingToInternet()) {
                    OnLocationSearch(v);
                } else {
                    Toast.makeText(getActivity(), "Internet connection is offline!", Toast.LENGTH_SHORT).show();
                }
            });
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    private void getLocationList() {
        if (helper.getLocationCount() != 0) {
            List<LocationsBean> locations = helper.getAllLocation();
            locationsBeanList.clear();
            locationsBeanList.addAll(locations);
            txtNoLocation.setVisibility(View.GONE);
        } else {
            txtNoLocation.setVisibility(View.VISIBLE);
        }
        locationListAdapter.notifyDataSetChanged();
    }

    private void OnLocationSearch(View view) {
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
                    helper.deleteAllLocations();
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
                    view.clearAnimation();
                    Toast.makeText(getActivity(), "fail!!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                view.clearAnimation();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Subscribe
    public void onEvent(Event event) {
        if (event.equals(Event.Location)) {

            locationListAdapter = new LocationListAdapter(locationsBeanList, getActivity());
            locationView.setAdapter(locationListAdapter);
            locationListAdapter.notifyDataSetChanged();
            getLocationList();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
