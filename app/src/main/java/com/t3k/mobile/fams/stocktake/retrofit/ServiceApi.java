package com.t3k.mobile.fams.stocktake.retrofit;

import com.google.gson.internal.LinkedTreeMap;
import com.t3k.mobile.fams.stocktake.model.DeviceInfo;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ServiceApi {

    @GET("con/check")
    Call<Void> serverConnection(@Query("appType") String appType);

    @POST("dev/check")
    Call<Map<String, Object>> checkRegister(@Query("appType") String appType, @Body DeviceInfo device);

    @POST("dev/register")
    Call<Map<String, Object>> deviceRegister(@Query("appType") String appType, @Query("loginId") String loginId,
                                             @Query("password") String password, @Body DeviceInfo requestInfo);

    @POST("license/check")
    Call<Map<String, Object>> checkLicense(@Query("appType") String appType, @Query("deviceAppId") int deviceAppId,
                                           @Query("isRequested") boolean isRequested, @Query("loginId") String loginId,
                                           @Query("password") String password, @Body DeviceInfo requestedInfo);

    @POST("license/request")
    Call<Map<String, Object>> licenseRequest(@Query("appType") String appType, @Query("deviceAppId") int deviceAppId,
                                             @Query("loginId") String loginId, @Query("password") String password,
                                             @Body DeviceInfo requestedInfo);

    @POST("usr/login")
    Call<Map<String, Object>> login(@Body Map<String, Object> request);

    @POST("loc/search")
    Call<Map<String, Object>> locationSearch(@Body Map<String, Object> request);

    @POST("ast/stocktake/search")
    Call<Map<String, Object>> getAssetByLocation(@Body Map<String, Object> request);

    @POST("ast/id")
    Call<Map<String, Object>> searchByFaNumber(@Body Map<String, Object> request);

    @Multipart
    @POST("file/upload")
    Call<LinkedTreeMap> postImage(@Query("appType") String appType, @Query("deviceCode") String deviceCode, @Query("licenseKey") String licenseKey,
                                  @Query("userId") Integer userId, @Query("token") String token, @Part MultipartBody.Part image, @Part("name") RequestBody name);

    @POST("stocktake/item")
    Call<Map<String, Object>> stockItem(@Body Map<String, Object> request);

    @POST("upload/data/start")
    Call<Map<String, Object>> uploadStart(@Body Map<String, Object> request);

    @POST("stocktake")
    Call<Map<String, Object>> stockTake(@Body Map<String, Object> request);

    @POST("upload/data/end")
    Call<Map<String, Object>> uploadEnd(@Body Map<String, Object> request);
}
