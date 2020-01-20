package com.t3k.mobile.fams.stocktake.retrofit;


import com.google.gson.internal.LinkedTreeMap;
import com.t3k.mobile.fams.stocktake.model.DeviceInfo;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Callback;

public class ApiService {
    ServiceApi api = ServiceGenerator.createService(ServiceApi.class);

    //server Connection Test
    public void connectionTest(Callback<Void> callback) {
        api.serverConnection(Constant.APP_TYPE).enqueue(callback);
    }

    //login
    public void login(Map<String, Object> request, Callback<Map<String, Object>> callback) {
        if (request != null) {
            api.login(request).enqueue(callback);
        }
    }

    //check register device
    public void checkRegister(DeviceInfo device, Callback<Map<String, Object>> callback) {
        if (device != null) {
            api.checkRegister(Constant.APP_TYPE, device).enqueue(callback);
        }
    }

    //register device info
    public void register(String loginId, String password, DeviceInfo device, Callback<Map<String, Object>> callback) {
        if (device != null && loginId != null && password != null) {
            api.deviceRegister(Constant.APP_TYPE, loginId, password, device).enqueue(callback);
        }
    }

    //check license
    public void checkLicense(int deviceAppId, String loginId, String password,
                             DeviceInfo device, Callback<Map<String, Object>> callback) {
        if (deviceAppId != 0 && loginId != null && password != null && device != null) {
            api.checkLicense(Constant.APP_TYPE, deviceAppId, true, loginId, password, device).enqueue(callback);
        }
    }

    //license request
    public void license(int deviceAppId, String loginId, String password, DeviceInfo device, Callback<Map<String, Object>> callback) {
        if (deviceAppId != 0 && loginId != null && password != null && device != null) {
            api.licenseRequest(Constant.APP_TYPE, deviceAppId, loginId, password, device).enqueue(callback);
        }
    }

    //location search
    public void locationSearch(Map<String, Object> request, Callback<Map<String, Object>> callback) {
        if (request != null) {
            api.locationSearch(request).enqueue(callback);
        }
    }

    //get asset by location
    public void assetSearch(Map<String, Object> request, Callback<Map<String, Object>> callback) {
        if (request != null) {
            api.getAssetByLocation(request).enqueue(callback);
        }
    }

    //search asset by asset number
    public void assetSearchByFaNumber(Map<String, Object> request, Callback<Map<String, Object>> callback) {
        if (request != null) {
            api.searchByFaNumber(request).enqueue(callback);
        }
    }

    //upload stock item
    public void uploadStockItem(Map<String, Object> request, Callback<Map<String, Object>> callback) {
        if (request != null) {
            api.stockItem(request).enqueue(callback);
        }
    }

    //upload file start
    public void uploadFileStart(Map<String, Object> request, Callback<Map<String, Object>> callback) {
        if (request != null) {
            api.uploadStart(request).enqueue(callback);
        }
    }

    //upload photo
    public void uploadPhoto(String appType, String deviceCode, String licenseKey, Integer userId, String token, MultipartBody.Part body, RequestBody name, Callback<LinkedTreeMap> callback) {
        api.postImage(appType, deviceCode, licenseKey, userId, token, body, name).enqueue(callback);
    }

    //upload stockTake
    public void uploadStockTake(Map<String, Object> request, Callback<Map<String, Object>> callback) {
        if (request != null) {
            api.stockTake(request).enqueue(callback);
        }
    }

    //upload file end
    public void uploadFileEnd(Map<String, Object> request, Callback<Map<String, Object>> callback) {
        if (request != null) {
            api.uploadEnd(request).enqueue(callback);
        }
    }
}
