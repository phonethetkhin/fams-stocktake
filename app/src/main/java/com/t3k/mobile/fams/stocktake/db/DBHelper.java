package com.t3k.mobile.fams.stocktake.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.widget.Toast;

import com.t3k.mobile.fams.stocktake.model.AssetBean;
import com.t3k.mobile.fams.stocktake.model.DeviceInfo;
import com.t3k.mobile.fams.stocktake.model.DownloadInfo;
import com.t3k.mobile.fams.stocktake.model.Evidence;
import com.t3k.mobile.fams.stocktake.model.LocationsBean;
import com.t3k.mobile.fams.stocktake.model.ServerSetting;
import com.t3k.mobile.fams.stocktake.model.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.t3k.mobile.fams.stocktake.utils.MyApplication.getContext;

public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper mInstance = null;
    private static final String DB_NAME = "fams_stock_take.db";
    private static final int DB_VERSION = 4;

    private static final String FILE_DIR = "StockTake";
    private static final String DB_PATH = Environment.getExternalStorageDirectory() + File.separator + FILE_DIR + File.separator;


    //for server port
    private static final String TABLE_SERVER_SETTING = "tbl_setting";
    private static final String SERVER_ID = "id";
    private static final String SETTING_GROUP = "setting_group";
    private static final String SETTING_NAME = "setting_name";
    private static final String SETTING_VALUE = "setting_value";

    //for user
    private static final String TABLE_ACCOUNT = "tbl_account";
    private static final String KEY_ACCOUNT_ID = "id";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_LOGIN_ID = "loginId";
    private static final String KEY_USER_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_JOIN_DATE = "joined_date";
    private static final String KEY_LAST_LOGIN_DATE = "last_login_date";

    //for device Info
    private static final String TABLE_DEVICE_INFO = "tbl_device_info";
    private static final String DEV_ID = "id";
    private static final String DEV_CODE = "device_code";
    private static final String DEV_NAME = "device_name";
    private static final String DEV_BRAND = "brand";
    private static final String DEV_MODEL = "model";
    private static final String DEV_OS_VERSION = "os_Version";
    private static final String DEV_DEVICE = "device";
    private static final String DEV_MAC_WIFI = "mac_wifi";
    private static final String DEV_ID_ONE = "device_id_one";
    private static final String DEV_ID_TWO = "device_id_two";
    private static final String DEV_SERIAL_NO = "serial_no";
    private static final String DEV_ANDROID_ID = "android_id";
    private static final String DEV_FINGERPRINT = "fingerPrint";
    private static final String DEV_IMEI = "imei";

    //for locations
    private static final String TABLE_LOCATION = "tbl_location";
    private static final String KEY_LOCATION_ID = "location_id";
    private static final String KEY_PARENT_NAME = "parent_name";
    private static final String KEY_LOCATION_CODE = "code";
    private static final String KEY_LOCATION_NAME = "name";
    private static final String KEY_LOCATION_DOWNLOAD = "download";
    private static final String KEY_LOCATION_UPLOAD = "upload";

    //for download info
    private static final String TABLE_DOWNLOAD_INFO = "tbl_download_info";
    private static final String DOWNLOAD_ID = "id";
    private static final String DOWNLOAD_USER_ID = "user_id";
    private static final String DOWNLOAD_DATE = "download_date";
    private static final String DOWNLOAD_LOCATION_ID = "location_id";

    //for Assets
    private static final String TABLE_ASSET = "tbl_asset";
    private static final String AST_ID = "id";
    private static final String AST_LOCATION_ID = "location_id";
    private static final String AST_ASSET_ID = "asset_id";
    private static final String AST_COST_CENTER = "cost_center";
    private static final String AST_NUMBER = "fa_number";
    private static final String AST_ITEM_NAME = "item_name";
    private static final String AST_CONDITION = "condition";
    private static final String AST_CATEGORY = "category";
    private static final String AST_BRAND = "brand";
    private static final String AST_MODEL = "model";
    private static final String AST_LOCATION_FOUND_ID = "location_found_id";
    private static final String AST_UNKNOWN = "unknown";
    private static final String AST_REMARK = "remark";
    private static final String AST_TAKEN = "taken";
    private static final String AST_SCAN_STATUS = "scan_status";
    private static final String AST_STOCK_TAKE_TIME = "stock_take_time";
    private static final String AST_UPLOAD_TIME = "upload_time";
    private static final String AST_IMG_PATH = "img_path";

    //for image
    private static final String TABLE_PHOTO = "tbl_evidence";
    private static final String PHOTO_ID = "id";
    private static final String PHOTO_ASSET_NUMBER = "asset_no";
    private static final String PHOTO_PATH = "file_path";


    public static DBHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DBHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    public DBHelper getInstance() {
        return mInstance;
    }

    public DBHelper(Context context) {
        super(context, DB_PATH + DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //query for port
        String CREATE_TABLE_SERVER = "CREATE TABLE " + TABLE_SERVER_SETTING + " (" + SERVER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + SETTING_GROUP + " TEXT," + SETTING_NAME + " TEXT," + SETTING_VALUE + " TEXT)";
        db.execSQL(CREATE_TABLE_SERVER);
        //query for user
        String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_ACCOUNT + " (" + KEY_ACCOUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_USER_ID + " INTEGER," +
                KEY_LOGIN_ID + " TEXT," + KEY_USER_NAME + " TEXT," + KEY_PASSWORD + " TEXT," + KEY_JOIN_DATE + " TEXT," + KEY_LAST_LOGIN_DATE + " TEXT)";
        db.execSQL(CREATE_TABLE_USER);
        //query for deviceInfo
        String CREATE_TABLE_DEVICE = "CREATE TABLE " + TABLE_DEVICE_INFO + " (" + DEV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + DEV_CODE + " TEXT," + DEV_NAME + " TEXT," + DEV_BRAND + " TEXT," +
                DEV_MODEL + " TEXT," + DEV_OS_VERSION + " TEXT," + DEV_DEVICE + " TEXT," + DEV_MAC_WIFI + " TEXT," + DEV_ID_ONE + " TEXT," +
                DEV_ID_TWO + " TEXT," + DEV_SERIAL_NO + " TEXT," + DEV_ANDROID_ID + " TEXT," + DEV_FINGERPRINT + " TEXT," + DEV_IMEI + " TEXT)";
        db.execSQL(CREATE_TABLE_DEVICE);
        //query for location
        String CREATE_TABLE_LOCATION = "CREATE TABLE " + TABLE_LOCATION + " (" + KEY_LOCATION_ID + " INTEGER PRIMARY KEY," + KEY_PARENT_NAME + " TEXT," +
                KEY_LOCATION_CODE + " TEXT," + KEY_LOCATION_NAME + " TEXT," + KEY_LOCATION_DOWNLOAD + " INTEGER," + KEY_LOCATION_UPLOAD + " INTEGER)";
        db.execSQL(CREATE_TABLE_LOCATION);
        //query for downloadInfo
        String CREATE_TABLE_DOWNLOAD = "CREATE TABLE " + TABLE_DOWNLOAD_INFO + " (" + DOWNLOAD_ID + " INTEGER PRIMARY KEY," + DOWNLOAD_USER_ID +
                " INTEGER," + DOWNLOAD_DATE + " TEXT," + DOWNLOAD_LOCATION_ID + " TEXT)";
        db.execSQL(CREATE_TABLE_DOWNLOAD);
        //query for asset
        String CREATE_TABLE_ASSET = "CREATE TABLE " + TABLE_ASSET + " (" + AST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + AST_LOCATION_ID + " INTEGER," + AST_ASSET_ID + " INTEGER," +
                AST_COST_CENTER + " TEXT," + AST_NUMBER + " TEXT," + AST_ITEM_NAME + " TEXT," + AST_CONDITION + " TEXT," + AST_CATEGORY + " TEXT," + AST_BRAND + " TEXT," + AST_MODEL +
                " TEXT," + AST_LOCATION_FOUND_ID + " INTEGER," + AST_UNKNOWN + " INTEGER," + AST_REMARK + " TEXT," + AST_TAKEN + " INTEGER," + AST_SCAN_STATUS + " INTEGER," + AST_STOCK_TAKE_TIME + " TEXT," +
                AST_UPLOAD_TIME + " TEXT," + AST_IMG_PATH + " TEXT)";
        db.execSQL(CREATE_TABLE_ASSET);
        //query for image
        String CREATE_TABLE_IMAGE = "CREATE TABLE " + TABLE_PHOTO + " (" + PHOTO_ID + " INTEGER PRIMARY KEY," + PHOTO_ASSET_NUMBER + " TEXT," +
                PHOTO_PATH + " TEXT DEFAULT 'Null')";
        db.execSQL(CREATE_TABLE_IMAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_TABLE_SERVER = "DROP TABLE IF EXISTS " + TABLE_SERVER_SETTING;
        db.execSQL(DROP_TABLE_SERVER);
        String DROP_TABLE_USER = "DROP TABLE IF EXISTS " + TABLE_ACCOUNT;
        db.execSQL(DROP_TABLE_USER);
        String DROP_TABLE_DEVICE = "DROP TABLE IF EXISTS " + TABLE_DEVICE_INFO;
        db.execSQL(DROP_TABLE_DEVICE);
        String DROP_TABLE_LOCATION = "DROP TABLE IF EXISTS " + TABLE_LOCATION;
        db.execSQL(DROP_TABLE_LOCATION);
        String DROP_TABLE_DOWNLOAD = "DROP TABLE IF EXISTS " + TABLE_DOWNLOAD_INFO;
        db.execSQL(DROP_TABLE_DOWNLOAD);
        String DROP_TABLE_ASSET = "DROP TABLE IF EXISTS " + TABLE_ASSET;
        db.execSQL(DROP_TABLE_ASSET);
        String DROP_TABLE_IMAGE = "DROP TABLE IF EXISTS " + TABLE_PHOTO;
        db.execSQL(DROP_TABLE_IMAGE);
        onCreate(db);
    }

    //-----------------------------------------------------------------------------------------------------------------------------
    //serverSetting
    //insert port
    public void addSetting(ServerSetting serverSetting) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(SETTING_GROUP, serverSetting.getSettingGroup());
            values.put(SETTING_NAME, serverSetting.getSettingName());
            values.put(SETTING_VALUE, serverSetting.getSettingValue());
            db.insert(TABLE_SERVER_SETTING, null, values);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get setting by id
    public ServerSetting getSettingById(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SERVER_SETTING, new String[]{SERVER_ID, SETTING_GROUP, SETTING_NAME, SETTING_VALUE}, SERVER_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        return new ServerSetting(cursor.getInt(0),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));
    }

    //update setting
    public void updateSetting(ServerSetting serverSetting) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SETTING_GROUP, serverSetting.getSettingGroup());
        values.put(SETTING_NAME, serverSetting.getSettingName());
        values.put(SETTING_VALUE, serverSetting.getSettingValue());

        db.update(TABLE_SERVER_SETTING, values, SERVER_ID + "=" + serverSetting.getId(), null);
    }

    //count of setting
    public int getSettingCount() {

        int num;
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            String QUERY = "SELECT * FROM " + TABLE_SERVER_SETTING;
            Cursor cursor = db.rawQuery(QUERY, null);
            num = cursor.getCount();
            db.close();
            return num;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    //--------------------------------------------------------------------------------------------------------------------------

    //user
    //insert user
    public void insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_ID, user.getUserId());
            values.put(KEY_LOGIN_ID, user.getLoginId());
            values.put(KEY_USER_NAME, user.getName());
            values.put(KEY_PASSWORD, user.getPassword());
            values.put(KEY_JOIN_DATE, user.getJoinedDate());
            values.put(KEY_LAST_LOGIN_DATE, user.getLastLoginDate());
            db.insert(TABLE_ACCOUNT, null, values);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //update user
    public void updateUser(User user) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, user.getUserId());
        values.put(KEY_LOGIN_ID, user.getLoginId());
        values.put(KEY_USER_NAME, user.getName());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_JOIN_DATE, user.getJoinedDate());
        values.put(KEY_LAST_LOGIN_DATE, user.getLastLoginDate());

        db.update(TABLE_ACCOUNT, values, KEY_ACCOUNT_ID + "=" + user.getId(), null);
    }

    //get user by loginId
    public User getUserByLoginId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ACCOUNT, new String[]{KEY_ACCOUNT_ID, KEY_USER_ID, KEY_LOGIN_ID
                        , KEY_USER_NAME, KEY_PASSWORD, KEY_JOIN_DATE, KEY_LAST_LOGIN_DATE}, KEY_ACCOUNT_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        return new User(cursor.getInt(0),
                cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                cursor.getString(5), cursor.getString(6));
    }

    //get user by loginId for login
    public String getSingleEntry(String loginId) {

        SQLiteDatabase db = this.getReadableDatabase();

        {
            Cursor cursor = db.query(TABLE_ACCOUNT, null, KEY_LOGIN_ID + "=?", new String[]{loginId}, null, null, null);
            if (cursor.getCount() < 1) {
                cursor.close();

                return "NOT EXIST";
            }
            cursor.moveToFirst();
            String password = cursor.getString(cursor.getColumnIndex(KEY_PASSWORD));
            cursor.close();

            return password;
        }
    }

    //check user exists
    public boolean userExists(int id) {

        SQLiteDatabase db;
        db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ACCOUNT + " WHERE " + KEY_ACCOUNT_ID + "=" + id, null);
        boolean exists = false;
        if (cursor.moveToFirst()) {
            exists = (cursor.getCount() > 0);
        }
        cursor.close();

        return exists;
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    //device info
    //insert deviceInfo
    public void addDeviceInfo(DeviceInfo deviceInfo) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(DEV_CODE, deviceInfo.getDeviceCode());
            values.put(DEV_NAME, deviceInfo.getDeviceName());
            values.put(DEV_BRAND, deviceInfo.getBrand());
            values.put(DEV_MODEL, deviceInfo.getModel());
            values.put(DEV_OS_VERSION, deviceInfo.getOsVersion());
            values.put(DEV_DEVICE, deviceInfo.getDevice());
            values.put(DEV_MAC_WIFI, deviceInfo.getMacWifi());
            values.put(DEV_ID_ONE, deviceInfo.getDeviceId1());
            values.put(DEV_ID_TWO, deviceInfo.getDeviceId2());
            values.put(DEV_SERIAL_NO, deviceInfo.getSerialNo());
            values.put(DEV_ANDROID_ID, deviceInfo.getAndroidId());
            values.put(DEV_FINGERPRINT, deviceInfo.getFingerPrint());
            values.put(DEV_IMEI, deviceInfo.getImei());
            db.insert(TABLE_DEVICE_INFO, null, values);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //update device info
    public void updateDeviceInfo(DeviceInfo deviceInfo) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DEV_CODE, deviceInfo.getDeviceCode());
        values.put(DEV_NAME, deviceInfo.getDeviceName());
        values.put(DEV_BRAND, deviceInfo.getBrand());
        values.put(DEV_MODEL, deviceInfo.getModel());
        values.put(DEV_OS_VERSION, deviceInfo.getOsVersion());
        values.put(DEV_DEVICE, deviceInfo.getDevice());
        values.put(DEV_MAC_WIFI, deviceInfo.getMacWifi());
        values.put(DEV_ID_ONE, deviceInfo.getDeviceId1());
        values.put(DEV_ID_TWO, deviceInfo.getDeviceId2());
        values.put(DEV_SERIAL_NO, deviceInfo.getSerialNo());
        values.put(DEV_ANDROID_ID, deviceInfo.getAndroidId());
        values.put(DEV_FINGERPRINT, deviceInfo.getFingerPrint());
        values.put(DEV_IMEI, deviceInfo.getImei());

        db.update(TABLE_DEVICE_INFO, values, DEV_ID + "=" + deviceInfo.getId(), null);
    }

    //get user by loginId
    public DeviceInfo getDeviceInfoById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DEVICE_INFO, new String[]{DEV_ID, DEV_CODE, DEV_NAME, DEV_BRAND, DEV_MODEL, DEV_OS_VERSION,
                DEV_DEVICE, DEV_MAC_WIFI, DEV_ID_ONE, DEV_ID_TWO, DEV_SERIAL_NO, DEV_ANDROID_ID, DEV_FINGERPRINT,
                DEV_IMEI}, DEV_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        return new DeviceInfo(cursor.getInt(0),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8),
                cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12),
                cursor.getString(13));
    }

    //count of device
    public int getDeviceCount() {

        int num;
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            String QUERY = "SELECT * FROM " + TABLE_DEVICE_INFO;
            Cursor cursor = db.rawQuery(QUERY, null);
            num = cursor.getCount();
            db.close();
            return num;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    //----------------------------------------------------------------------------------------------------------------------------
    //Location
    //insert location
    public void addLocation(LocationsBean locations) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_LOCATION_ID, locations.getLocationId());
            values.put(KEY_PARENT_NAME, locations.getParentName());
            values.put(KEY_LOCATION_CODE, locations.getCode());
            values.put(KEY_LOCATION_NAME, locations.getName());
            values.put(KEY_LOCATION_DOWNLOAD, locations.getDownload());
            values.put(KEY_LOCATION_UPLOAD, locations.getUpload());
            db.insert(TABLE_LOCATION, null, values);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //update location
    public void updateLocation(LocationsBean locations) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PARENT_NAME, locations.getParentName());
        values.put(KEY_LOCATION_CODE, locations.getCode());
        values.put(KEY_LOCATION_NAME, locations.getName());
        values.put(KEY_LOCATION_DOWNLOAD, locations.getDownload());
        values.put(KEY_LOCATION_UPLOAD, locations.getUpload());

        db.update(TABLE_LOCATION, values, KEY_LOCATION_ID + "=" + locations.getLocationId(), null);
    }


    //get all location
    public ArrayList<LocationsBean> getAllLocation() {

        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<LocationsBean> locations = null;
        try {
            locations = new ArrayList<>();
            String QUERY = "SELECT * FROM " + TABLE_LOCATION;
            Cursor cursor = db.rawQuery(QUERY, null);
            if (!cursor.isLast()) {
                while (cursor.moveToNext()) {
                    LocationsBean location = new LocationsBean();
                    location.setLocationId(cursor.getInt(0));
                    location.setParentName(cursor.getString(1));
                    location.setCode(cursor.getString(2));
                    location.setName(cursor.getString(3));
                    location.setDownload(cursor.getInt(4));
                    location.setUpload(cursor.getInt(5));
                    locations.add(location);
                }
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return locations;
    }

    //get location by Id
    public LocationsBean getLocationById(int locationId) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_LOCATION, new String[]{KEY_LOCATION_ID, KEY_PARENT_NAME,
                        KEY_LOCATION_CODE, KEY_LOCATION_NAME, KEY_LOCATION_DOWNLOAD, KEY_LOCATION_UPLOAD}, KEY_LOCATION_ID + "=?",
                new String[]{String.valueOf(locationId)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        return new LocationsBean(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getInt(4), cursor.getInt(5));
    }

    //get location by name
    public LocationsBean getLocationByName(String name) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_LOCATION, new String[]{KEY_LOCATION_ID, KEY_PARENT_NAME,
                        KEY_LOCATION_CODE, KEY_LOCATION_NAME, KEY_LOCATION_DOWNLOAD, KEY_LOCATION_UPLOAD}, KEY_LOCATION_NAME + "=?",
                new String[]{name}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        return new LocationsBean(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getInt(4), cursor.getInt(5));
    }

    //check location exists
    public boolean checkLocationExists(int locationId) {

        SQLiteDatabase db;
        db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LOCATION + " WHERE " + KEY_LOCATION_ID + "='" + locationId + "'", null);
        boolean exists = false;
        if (cursor.moveToFirst()) {
            exists = (cursor.getCount() > 0);
        }
        cursor.close();

        return exists;
    }

    //count of location
    public int getLocationCount() {

        int num;
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            String QUERY = "SELECT * FROM " + TABLE_LOCATION;
            Cursor cursor = db.rawQuery(QUERY, null);
            num = cursor.getCount();
            db.close();
            return num;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    //------------------------------------------------------------------------------------------------------
    //insert downloadInfo
    public void addInfo(DownloadInfo info) {

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(DOWNLOAD_USER_ID, info.getDownloadUserId());
            values.put(DOWNLOAD_DATE, getDateTime());
            values.put(DOWNLOAD_LOCATION_ID, info.getLocationId());
            db.insert(TABLE_DOWNLOAD_INFO, null, values);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get info by id
    public DownloadInfo getDownloadInfoById(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_DOWNLOAD_INFO, new String[]{DOWNLOAD_ID,
                        DOWNLOAD_USER_ID, DOWNLOAD_DATE, DOWNLOAD_LOCATION_ID}, DOWNLOAD_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        DownloadInfo downloadInfo = new DownloadInfo(cursor.getInt(0),
                cursor.getInt(1), cursor.getString(2), cursor.getString(3));

        return downloadInfo;
    }

    //count of info
    public int getInfoCount() {

        int num = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            String QUERY = "SELECT * FROM " + TABLE_DOWNLOAD_INFO;
            Cursor cursor = db.rawQuery(QUERY, null);
            num = cursor.getCount();
            db.close();
            return num;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    //-------------------------------------------------------------------------------------------------------------------------------------

    //Assets
    //insert Assets
    public void addAsset(AssetBean assets) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(AST_LOCATION_ID, assets.getLocationId());
            values.put(AST_ASSET_ID, assets.getAssetId());
            values.put(AST_COST_CENTER, assets.getCostCenter());
            values.put(AST_NUMBER, assets.getFaNumber());
            values.put(AST_ITEM_NAME, assets.getItemName());
            values.put(AST_CONDITION, assets.getCondition());
            values.put(AST_CATEGORY, assets.getCategory());
            values.put(AST_BRAND, assets.getBrand());
            values.put(AST_MODEL, assets.getModel());
            values.put(AST_LOCATION_FOUND_ID, assets.getLocationFoundId());
            values.put(AST_UNKNOWN, assets.getUnknown());
            values.put(AST_REMARK, assets.getRemark());
            values.put(AST_TAKEN, assets.getTaken());
            values.put(AST_SCAN_STATUS, assets.getScannedStatus());
            values.put(AST_STOCK_TAKE_TIME, assets.getStockTakeTime());
            values.put(AST_UPLOAD_TIME, assets.getUploadTime());
            values.put(AST_IMG_PATH, assets.getImagePath());
            db.insert(TABLE_ASSET, null, values);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //update asset
    public void updateAsset(AssetBean assets) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AST_LOCATION_ID, assets.getLocationId());
        values.put(AST_ASSET_ID, assets.getAssetId());
        values.put(AST_COST_CENTER, assets.getCostCenter());
        values.put(AST_NUMBER, assets.getFaNumber());
        values.put(AST_ITEM_NAME, assets.getItemName());
        values.put(AST_CONDITION, assets.getCondition());
        values.put(AST_CATEGORY, assets.getCategory());
        values.put(AST_BRAND, assets.getBrand());
        values.put(AST_MODEL, assets.getModel());
        values.put(AST_LOCATION_FOUND_ID, assets.getLocationFoundId());
        values.put(AST_UNKNOWN, assets.getUnknown());
        values.put(AST_REMARK, assets.getRemark());
        values.put(AST_TAKEN, assets.getTaken());
        values.put(AST_SCAN_STATUS, assets.getScannedStatus());
        values.put(AST_STOCK_TAKE_TIME, assets.getStockTakeTime());
        values.put(AST_UPLOAD_TIME, assets.getUploadTime());
        values.put(AST_IMG_PATH, assets.getImagePath());
        db.update(TABLE_ASSET, values, AST_ID + "=" + assets.getId(), null);
    }

    //get scan assets by location
    public ArrayList<AssetBean> getScanByLocation(int locationId) {

        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<AssetBean> assetList = null;
        try {
            assetList = new ArrayList<>();
            String QUERY = "SELECT * FROM " + TABLE_ASSET + " WHERE " + AST_UPLOAD_TIME + " IS NULL AND " + AST_ASSET_ID + "!=0 AND " + AST_UNKNOWN + "=0 AND " + AST_LOCATION_ID + "=" + locationId;
            Cursor cursor = db.rawQuery(QUERY, null);
            if (!cursor.isLast()) {

                while (cursor.moveToNext()) {

                    AssetBean fixedAsset = new AssetBean();
                    fixedAsset.setId(cursor.getInt(0));
                    fixedAsset.setLocationId(cursor.getInt(1));
                    fixedAsset.setAssetId(cursor.getInt(2));
                    fixedAsset.setCostCenter(cursor.getString(3));
                    fixedAsset.setFaNumber(cursor.getString(4));
                    fixedAsset.setItemName(cursor.getString(5));
                    fixedAsset.setCondition(cursor.getString(6));
                    fixedAsset.setCategory(cursor.getString(7));
                    fixedAsset.setBrand(cursor.getString(8));
                    fixedAsset.setModel(cursor.getString(9));
                    fixedAsset.setLocationFoundId(cursor.getInt(10));
                    fixedAsset.setUnknown(cursor.getInt(11));
                    fixedAsset.setRemark(cursor.getString(12));
                    fixedAsset.setTaken(cursor.getInt(13));
                    fixedAsset.setScannedStatus(cursor.getInt(14));
                    fixedAsset.setStockTakeTime(cursor.getString(15));
                    fixedAsset.setUploadTime(cursor.getString(16));
                    fixedAsset.setImagePath(cursor.getString(17));
                    assetList.add(fixedAsset);
                }
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return assetList;
    }

    //get remain assets
    public ArrayList<AssetBean> getRemain() {

        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<AssetBean> assetList = null;
        try {
            assetList = new ArrayList<>();
            String QUERY = "SELECT * FROM " + TABLE_ASSET + " WHERE " + AST_STOCK_TAKE_TIME + " IS NULL AND " + AST_UPLOAD_TIME + " IS NULL AND " + AST_UNKNOWN + "=0 AND " + AST_ASSET_ID + "!=0";
            Cursor cursor = db.rawQuery(QUERY, null);
            if (!cursor.isLast()) {

                while (cursor.moveToNext()) {

                    AssetBean fixedAsset = new AssetBean();
                    fixedAsset.setId(cursor.getInt(0));
                    fixedAsset.setLocationId(cursor.getInt(1));
                    fixedAsset.setAssetId(cursor.getInt(2));
                    fixedAsset.setCostCenter(cursor.getString(3));
                    fixedAsset.setFaNumber(cursor.getString(4));
                    fixedAsset.setItemName(cursor.getString(5));
                    fixedAsset.setCondition(cursor.getString(6));
                    fixedAsset.setCategory(cursor.getString(7));
                    fixedAsset.setBrand(cursor.getString(8));
                    fixedAsset.setModel(cursor.getString(9));
                    fixedAsset.setLocationFoundId(cursor.getInt(10));
                    fixedAsset.setUnknown(cursor.getInt(11));
                    fixedAsset.setRemark(cursor.getString(12));
                    fixedAsset.setTaken(cursor.getInt(13));
                    fixedAsset.setScannedStatus(cursor.getInt(14));
                    fixedAsset.setStockTakeTime(cursor.getString(15));
                    fixedAsset.setUploadTime(cursor.getString(16));
                    fixedAsset.setImagePath(cursor.getString(17));
                    assetList.add(fixedAsset);
                }
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return assetList;
    }

    //get remain assets by location
    public ArrayList<AssetBean> getRemainByLocation(int locationId) {

        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<AssetBean> assetList = null;
        try {
            assetList = new ArrayList<>();
            String QUERY = "SELECT * FROM " + TABLE_ASSET + " WHERE " + AST_STOCK_TAKE_TIME + " IS NULL AND " + AST_UPLOAD_TIME + " IS NULL AND " + AST_ASSET_ID + "!=0 AND " + AST_UNKNOWN + "=0 AND " + AST_LOCATION_ID + "=" + locationId;
            Cursor cursor = db.rawQuery(QUERY, null);
            if (!cursor.isLast()) {

                while (cursor.moveToNext()) {

                    AssetBean fixedAsset = new AssetBean();
                    fixedAsset.setId(cursor.getInt(0));
                    fixedAsset.setLocationId(cursor.getInt(1));
                    fixedAsset.setAssetId(cursor.getInt(2));
                    fixedAsset.setCostCenter(cursor.getString(3));
                    fixedAsset.setFaNumber(cursor.getString(4));
                    fixedAsset.setItemName(cursor.getString(5));
                    fixedAsset.setCondition(cursor.getString(6));
                    fixedAsset.setCategory(cursor.getString(7));
                    fixedAsset.setBrand(cursor.getString(8));
                    fixedAsset.setModel(cursor.getString(9));
                    fixedAsset.setLocationFoundId(cursor.getInt(10));
                    fixedAsset.setUnknown(cursor.getInt(11));
                    fixedAsset.setRemark(cursor.getString(12));
                    fixedAsset.setTaken(cursor.getInt(13));
                    fixedAsset.setScannedStatus(cursor.getInt(14));
                    fixedAsset.setStockTakeTime(cursor.getString(15));
                    fixedAsset.setUploadTime(cursor.getString(16));
                    fixedAsset.setImagePath(cursor.getString(17));
                    assetList.add(fixedAsset);
                }
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return assetList;
    }

    //get scanned assets
    public ArrayList<AssetBean> getTaken() {

        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<AssetBean> assetList = null;

        try {
            assetList = new ArrayList<>();
            String QUERY = "SELECT * FROM " + TABLE_ASSET + " WHERE " + AST_STOCK_TAKE_TIME + " IS NOT NULL AND " + AST_UPLOAD_TIME + " IS NULL AND " + AST_UNKNOWN + "=0 AND " + AST_ASSET_ID + "!=0";
            Cursor cursor = db.rawQuery(QUERY, null);
            if (!cursor.isLast()) {
                while (cursor.moveToNext()) {
                    AssetBean fixedAsset = new AssetBean();
                    fixedAsset.setId(cursor.getInt(0));
                    fixedAsset.setLocationId(cursor.getInt(1));
                    fixedAsset.setAssetId(cursor.getInt(2));
                    fixedAsset.setCostCenter(cursor.getString(3));
                    fixedAsset.setFaNumber(cursor.getString(4));
                    fixedAsset.setItemName(cursor.getString(5));
                    fixedAsset.setCondition(cursor.getString(6));
                    fixedAsset.setCategory(cursor.getString(7));
                    fixedAsset.setBrand(cursor.getString(8));
                    fixedAsset.setModel(cursor.getString(9));
                    fixedAsset.setLocationFoundId(cursor.getInt(10));
                    fixedAsset.setUnknown(cursor.getInt(11));
                    fixedAsset.setRemark(cursor.getString(12));
                    fixedAsset.setTaken(cursor.getInt(13));
                    fixedAsset.setScannedStatus(cursor.getInt(14));
                    fixedAsset.setStockTakeTime(cursor.getString(15));
                    fixedAsset.setUploadTime(cursor.getString(16));
                    fixedAsset.setImagePath(cursor.getString(17));
                    assetList.add(fixedAsset);
                }
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return assetList;
    }

    //get taken assets by locationId
    public ArrayList<AssetBean> getTakenByLocation(int locationId) {

        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<AssetBean> assetList = null;
        try {
            assetList = new ArrayList<>();
            String QUERY = "SELECT * FROM " + TABLE_ASSET + " WHERE " + AST_STOCK_TAKE_TIME + " IS NOT NULL AND " + AST_UPLOAD_TIME + " IS NULL AND " + AST_ASSET_ID + "!=0 AND " + AST_UNKNOWN + "=0 AND " + AST_LOCATION_ID + "=" + locationId;
            Cursor cursor = db.rawQuery(QUERY, null);
            if (!cursor.isLast()) {
                while (cursor.moveToNext()) {
                    AssetBean fixedAsset = new AssetBean();
                    fixedAsset.setId(cursor.getInt(0));
                    fixedAsset.setLocationId(cursor.getInt(1));
                    fixedAsset.setAssetId(cursor.getInt(2));
                    fixedAsset.setCostCenter(cursor.getString(3));
                    fixedAsset.setFaNumber(cursor.getString(4));
                    fixedAsset.setItemName(cursor.getString(5));
                    fixedAsset.setCondition(cursor.getString(6));
                    fixedAsset.setCategory(cursor.getString(7));
                    fixedAsset.setBrand(cursor.getString(8));
                    fixedAsset.setModel(cursor.getString(9));
                    fixedAsset.setLocationFoundId(cursor.getInt(10));
                    fixedAsset.setUnknown(cursor.getInt(11));
                    fixedAsset.setRemark(cursor.getString(12));
                    fixedAsset.setTaken(cursor.getInt(13));
                    fixedAsset.setScannedStatus(cursor.getInt(14));
                    fixedAsset.setStockTakeTime(cursor.getString(15));
                    fixedAsset.setUploadTime(cursor.getString(16));
                    fixedAsset.setImagePath(cursor.getString(17));
                    assetList.add(fixedAsset);
                }
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return assetList;
    }

    //get scanned assets
    public ArrayList<AssetBean> getUnKnown() {

        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<AssetBean> assetList = null;

        try {
            assetList = new ArrayList<>();
            String QUERY = "SELECT * FROM " + TABLE_ASSET + " WHERE " + AST_STOCK_TAKE_TIME + " IS NOT NULL AND " + AST_UPLOAD_TIME + " IS NULL AND " + AST_UNKNOWN + "=1 AND " + AST_ASSET_ID + "=0";
            Cursor cursor = db.rawQuery(QUERY, null);
            if (!cursor.isLast()) {
                while (cursor.moveToNext()) {
                    AssetBean fixedAsset = new AssetBean();
                    fixedAsset.setId(cursor.getInt(0));
                    fixedAsset.setLocationId(cursor.getInt(1));
                    fixedAsset.setAssetId(cursor.getInt(2));
                    fixedAsset.setCostCenter(cursor.getString(3));
                    fixedAsset.setFaNumber(cursor.getString(4));
                    fixedAsset.setItemName(cursor.getString(5));
                    fixedAsset.setCondition(cursor.getString(6));
                    fixedAsset.setCategory(cursor.getString(7));
                    fixedAsset.setBrand(cursor.getString(8));
                    fixedAsset.setModel(cursor.getString(9));
                    fixedAsset.setLocationFoundId(cursor.getInt(10));
                    fixedAsset.setUnknown(cursor.getInt(11));
                    fixedAsset.setRemark(cursor.getString(12));
                    fixedAsset.setTaken(cursor.getInt(13));
                    fixedAsset.setScannedStatus(cursor.getInt(14));
                    fixedAsset.setStockTakeTime(cursor.getString(15));
                    fixedAsset.setUploadTime(cursor.getString(16));
                    fixedAsset.setImagePath(cursor.getString(17));
                    assetList.add(fixedAsset);
                }
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return assetList;
    }

    //get scanned assets by locationId
    public ArrayList<AssetBean> getUnKnownByLocation(int locationId) {

        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<AssetBean> assetList = null;
        try {
            assetList = new ArrayList<>();
            String QUERY = "SELECT * FROM " + TABLE_ASSET + " WHERE " + AST_STOCK_TAKE_TIME + " IS NOT NULL AND " + AST_UPLOAD_TIME + " IS NULL AND " + AST_ASSET_ID + "=0 AND " + AST_UNKNOWN + "=1 AND " + AST_LOCATION_ID + "=" + locationId;
            Cursor cursor = db.rawQuery(QUERY, null);
            if (!cursor.isLast()) {
                while (cursor.moveToNext()) {
                    AssetBean fixedAsset = new AssetBean();
                    fixedAsset.setId(cursor.getInt(0));
                    fixedAsset.setLocationId(cursor.getInt(1));
                    fixedAsset.setAssetId(cursor.getInt(2));
                    fixedAsset.setCostCenter(cursor.getString(3));
                    fixedAsset.setFaNumber(cursor.getString(4));
                    fixedAsset.setItemName(cursor.getString(5));
                    fixedAsset.setCondition(cursor.getString(6));
                    fixedAsset.setCategory(cursor.getString(7));
                    fixedAsset.setBrand(cursor.getString(8));
                    fixedAsset.setModel(cursor.getString(9));
                    fixedAsset.setLocationFoundId(cursor.getInt(10));
                    fixedAsset.setUnknown(cursor.getInt(11));
                    fixedAsset.setRemark(cursor.getString(12));
                    fixedAsset.setTaken(cursor.getInt(13));
                    fixedAsset.setScannedStatus(cursor.getInt(14));
                    fixedAsset.setStockTakeTime(cursor.getString(15));
                    fixedAsset.setUploadTime(cursor.getString(16));
                    fixedAsset.setImagePath(cursor.getString(17));
                    assetList.add(fixedAsset);
                }
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return assetList;
    }

    //get asset by faNumber
    public AssetBean getFaByFaNumber(String faNumber) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ASSET, new String[]{AST_ID, AST_LOCATION_ID, AST_ASSET_ID, AST_COST_CENTER, AST_NUMBER, AST_ITEM_NAME,
                        AST_CONDITION, AST_CATEGORY, AST_BRAND, AST_MODEL, AST_LOCATION_FOUND_ID, AST_UNKNOWN, AST_REMARK, AST_SCAN_STATUS,
                        AST_TAKEN, AST_STOCK_TAKE_TIME, AST_UPLOAD_TIME, AST_IMG_PATH}, AST_NUMBER + "=?",
                new String[]{faNumber}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return new AssetBean(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2),
                cursor.getString(3), cursor.getString(4), cursor.getString(5),
                cursor.getString(6), cursor.getString(7), cursor.getString(8),
                cursor.getString(9), cursor.getInt(10), cursor.getInt(11),
                cursor.getString(12), cursor.getInt(13), cursor.getInt(14),
                cursor.getString(15), cursor.getString(16), cursor.getString(17));
    }

    //get asset by locationId
    public ArrayList<AssetBean> getAssetByLocationId(int locationId) {

        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<AssetBean> assetList = null;
        try {
            assetList = new ArrayList<>();
            String QUERY = "SELECT * FROM " + TABLE_ASSET + " WHERE " + AST_UPLOAD_TIME + " IS NULL AND " + AST_LOCATION_ID + "=" + locationId;
            Cursor cursor = db.rawQuery(QUERY, null);
            if (!cursor.isLast()) {

                while (cursor.moveToNext()) {

                    AssetBean fixedAsset = new AssetBean();
                    fixedAsset.setId(cursor.getInt(0));
                    fixedAsset.setLocationId(cursor.getInt(1));
                    fixedAsset.setAssetId(cursor.getInt(2));
                    fixedAsset.setCostCenter(cursor.getString(3));
                    fixedAsset.setFaNumber(cursor.getString(4));
                    fixedAsset.setItemName(cursor.getString(5));
                    fixedAsset.setCondition(cursor.getString(6));
                    fixedAsset.setCategory(cursor.getString(7));
                    fixedAsset.setBrand(cursor.getString(8));
                    fixedAsset.setModel(cursor.getString(9));
                    fixedAsset.setLocationFoundId(cursor.getInt(10));
                    fixedAsset.setUnknown(cursor.getInt(11));
                    fixedAsset.setRemark(cursor.getString(12));
                    fixedAsset.setTaken(cursor.getInt(13));
                    fixedAsset.setScannedStatus(cursor.getInt(14));
                    fixedAsset.setStockTakeTime(cursor.getString(15));
                    fixedAsset.setUploadTime(cursor.getString(16));
                    fixedAsset.setImagePath(cursor.getString(17));
                    assetList.add(fixedAsset);
                }
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return assetList;
    }

    //get all uploaded assets
    public ArrayList<AssetBean> getAllUploadAssets() {

        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<AssetBean> assetList = null;
        try {
            assetList = new ArrayList<>();
            String QUERY = "SELECT * FROM " + TABLE_ASSET + " WHERE " + AST_UPLOAD_TIME + " IS NOT NULL ";
            Cursor cursor = db.rawQuery(QUERY, null);
            if (!cursor.isLast()) {
                while (cursor.moveToNext()) {
                    AssetBean fixedAsset = new AssetBean();
                    fixedAsset.setId(cursor.getInt(0));
                    fixedAsset.setLocationId(cursor.getInt(1));
                    fixedAsset.setAssetId(cursor.getInt(2));
                    fixedAsset.setCostCenter(cursor.getString(3));
                    fixedAsset.setFaNumber(cursor.getString(4));
                    fixedAsset.setItemName(cursor.getString(5));
                    fixedAsset.setCondition(cursor.getString(6));
                    fixedAsset.setCategory(cursor.getString(7));
                    fixedAsset.setBrand(cursor.getString(8));
                    fixedAsset.setModel(cursor.getString(9));
                    fixedAsset.setLocationFoundId(cursor.getInt(10));
                    fixedAsset.setUnknown(cursor.getInt(11));
                    fixedAsset.setRemark(cursor.getString(12));
                    fixedAsset.setTaken(cursor.getInt(13));
                    fixedAsset.setScannedStatus(cursor.getInt(14));
                    fixedAsset.setStockTakeTime(cursor.getString(15));
                    fixedAsset.setUploadTime(cursor.getString(16));
                    fixedAsset.setImagePath(cursor.getString(17));
                    assetList.add(fixedAsset);
                }
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return assetList;
    }

    //count of assets by location
    public int getAssetCountByLocation(int locationId) {

        int num = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            String QUERY = "SELECT * FROM " + TABLE_ASSET + " WHERE " + AST_UPLOAD_TIME + " IS NULL AND " + AST_LOCATION_ID + "=" + locationId;
            Cursor cursor = db.rawQuery(QUERY, null);
            num = cursor.getCount();
            db.close();
            return num;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    //count of remain assets by location
    public int getRemainCountByLocation(int locationId) {

        int num = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            String QUERY = "SELECT * FROM " + TABLE_ASSET + " WHERE " + AST_STOCK_TAKE_TIME + " IS NULL AND " + AST_UPLOAD_TIME + " IS NULL AND " + AST_ASSET_ID + "!=0 AND " + AST_LOCATION_ID + "=" + locationId;
            Cursor cursor = db.rawQuery(QUERY, null);
            num = cursor.getCount();
            db.close();
            return num;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    //count of scanned assets by location
    public int getScannedCountByLocation(int locationId) {

        int num = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            String QUERY = "SELECT * FROM " + TABLE_ASSET + " WHERE " + AST_STOCK_TAKE_TIME + " IS NOT NULL AND " + AST_UPLOAD_TIME + " IS NULL AND " + AST_ASSET_ID + "!=0 AND " + AST_LOCATION_ID + "=" + locationId;
            Cursor cursor = db.rawQuery(QUERY, null);
            num = cursor.getCount();
            db.close();
            return num;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    //count of taken assets by location
    public int getTakenCountByLocation(int locationId) {

        int num = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            String QUERY = "SELECT * FROM " + TABLE_ASSET + " WHERE " + AST_STOCK_TAKE_TIME + " IS NOT NULL AND " + AST_UPLOAD_TIME + " IS NULL AND " + AST_ASSET_ID + "!=0 AND " + AST_LOCATION_ID + "=" + locationId;
            Cursor cursor = db.rawQuery(QUERY, null);
            num = cursor.getCount();
            db.close();
            return num;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    //count of unknown assets by location
    public int getUnknownCountByLocation(int locationId) {

        int num = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            String QUERY = "SELECT * FROM " + TABLE_ASSET + " WHERE " + AST_STOCK_TAKE_TIME + " IS NOT NULL AND " + AST_UPLOAD_TIME + " IS NULL AND " + AST_ASSET_ID + "=0 AND " + AST_UNKNOWN + "=1 AND " + AST_LOCATION_ID + "=" + locationId;
            Cursor cursor = db.rawQuery(QUERY, null);
            num = cursor.getCount();
            db.close();
            return num;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    //check asset by asset number
    public boolean checkAssetNumberExistsByLocation(String assetNumber, int locationId) {

        SQLiteDatabase db;
        db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ASSET + " WHERE " + AST_NUMBER + "='" + assetNumber + "' AND " + AST_LOCATION_ID + "=" + locationId, null);
        boolean exists = false;
        if (cursor.moveToFirst()) {
            exists = (cursor.getCount() > 0);
        }
        cursor.close();

        return exists;
    }


    //check asset by asset number
    public boolean checkAssetNumberExists(String assetNumber) {

        SQLiteDatabase db;
        db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ASSET + " WHERE " + AST_STOCK_TAKE_TIME + " IS NOT NULL AND " + AST_UPLOAD_TIME + " IS NULL AND " + AST_NUMBER + "='" + assetNumber + "'", null);
        boolean exists = false;
        if (cursor.moveToFirst()) {
            exists = (cursor.getCount() > 0);
        }
        cursor.close();

        return exists;
    }

    //-----------------------------------------------------------------------------------------------------
    //insert evidence
    public void addImage(Evidence evidence) {

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(PHOTO_ASSET_NUMBER, evidence.getFaNumber());
            values.put(PHOTO_PATH, evidence.getImagePath());
            db.insert(TABLE_PHOTO, null, values);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get image by faNumber
    public Evidence getImageByFaNumber(String faNumber) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PHOTO, new String[]{PHOTO_ID, PHOTO_ASSET_NUMBER, PHOTO_PATH}, PHOTO_ASSET_NUMBER + "=?",
                new String[]{faNumber}, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Evidence myEvidence = cursorToMyImage(cursor);
            cursor.moveToNext();

            return myEvidence;
        }
        cursor.close();

        return null;
    }

    private Evidence cursorToMyImage(Cursor cursor) {

        Evidence evidence = new Evidence();

        evidence.setId(cursor.getString(cursor.getColumnIndex(PHOTO_ID)));
        evidence.setFaNumber(
                cursor.getString(cursor.getColumnIndex(PHOTO_ASSET_NUMBER)));
        evidence.setImagePath(cursor.getString(
                cursor.getColumnIndex(PHOTO_PATH)));

        return evidence;
    }

    //update evidence
    public int updatePhoto(Evidence evidence) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PHOTO_ASSET_NUMBER, evidence.getFaNumber());
        values.put(PHOTO_PATH, evidence.getImagePath());
        return db.update(TABLE_PHOTO, values, PHOTO_ID + "=" + evidence.getId(), null);
    }


    //get current date
    private String getDateTime() {

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date date = new Date();

        return dateFormat.format(date);
    }

    //delete all locations
    public void deleteAllLocations() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LOCATION, null);
        db.delete(TABLE_LOCATION, null, null);
        cursor.close();
    }

    //delete all assets
    public void deleteAllAssets() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ASSET, null);
        db.delete(TABLE_ASSET, null, null);
        cursor.close();
    }

    //delete assets by primaryId
    public void deleteAssetByPrimaryId(int primaryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ASSET, AST_ASSET_ID + "=" + primaryId, null);
    }

    //delete download info
    public void deleteInfo() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DOWNLOAD_INFO, null);
        db.delete(TABLE_DOWNLOAD_INFO, null, null);
        cursor.close();
    }

    //delete photo
    public void deleteAllPhotos() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PHOTO, null);
        db.delete(TABLE_PHOTO, null, null);
        cursor.close();
    }

    //delete photo by assetNo
    public Evidence deletePhotoByAssetNo(String assetNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PHOTO + " WHERE " + PHOTO_ASSET_NUMBER + "='" + assetNo + "'", null);
        db.delete(TABLE_PHOTO, null, null);
        cursor.close();
        return null;
    }

    //check photo exists
    public boolean checkPhotoExists(String faNumber) {

        SQLiteDatabase db;
        db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PHOTO + " WHERE " + PHOTO_ASSET_NUMBER + "='" + faNumber + "'", null);
        boolean exists = false;
        if (cursor.moveToFirst()) {
            exists = (cursor.getCount() > 0);
        }
        cursor.close();

        return exists;
    }


    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    public void exportDB() {

        Calendar c = Calendar.getInstance();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String backupDate = dateFormat.format(c.getTime());

        try {
            String filePath = Environment.getExternalStorageDirectory() + "/STOCK_TAKE_Backup/";
            File sd = new File(filePath);
            if (!sd.exists()) {
                sd.mkdirs();
            }
            if (sd.canWrite()) {
                String currentDBPath = DB_PATH + DB_NAME;
                String backupDBPath = backupDate + "_stock_take.db";
                File currentDB = new File(currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Backup DB,ERROR", Toast.LENGTH_SHORT).show();
        }
    }
}
