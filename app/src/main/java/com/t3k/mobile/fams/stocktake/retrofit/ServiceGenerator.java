package com.t3k.mobile.fams.stocktake.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.t3k.mobile.fams.stocktake.db.DBHelper;
import com.t3k.mobile.fams.stocktake.model.ServerSetting;
import com.t3k.mobile.fams.stocktake.utils.MyApplication;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static DBHelper helper = new DBHelper(MyApplication.getContext());
    private static ServerSetting serverProtocol = helper.getSettingById(1);
    private static ServerSetting serverAddress = helper.getSettingById(2);
    private static ServerSetting serverContactPath = helper.getSettingById(3);
    private static String API_BASE_URL = serverProtocol.getSettingValue() + serverAddress.getSettingValue() + "/" + serverContactPath.getSettingValue() + "/";

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'valuet'HH:mm:ss.SSSZ").create();
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson));

    public static void changeApiBaseUrl(String newApiBaseUrl) {
        API_BASE_URL = newApiBaseUrl;

        builder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(API_BASE_URL);
    }

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(httpClient.readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build()).build();
        return retrofit.create(serviceClass);
    }
}
