package com.t3k.mobile.fams.stocktake.model;

public class DeviceInfo {
    private int id;
    private String deviceCode;
    private String deviceName;
    private String brand;
    private String model;
    private String osVersion;
    private String device;
    private String macWifi;
    private String deviceId1;
    private String deviceId2;
    private String serialNo;
    private String androidId;
    private String fingerPrint;
    private String imei;

    public DeviceInfo() {
    }

    public DeviceInfo(int id, String deviceCode, String deviceName, String brand, String model, String osVersion, String device,
                      String macWifi, String deviceId1, String deviceId2, String serialNo, String androidId, String fingerPrint,
                      String imei) {
        this.id = id;
        this.deviceCode = deviceCode;
        this.deviceName = deviceName;
        this.brand = brand;
        this.model = model;
        this.osVersion = osVersion;
        this.device = device;
        this.macWifi = macWifi;
        this.deviceId1 = deviceId1;
        this.deviceId2 = deviceId2;
        this.serialNo = serialNo;
        this.androidId = androidId;
        this.fingerPrint = fingerPrint;
        this.imei = imei;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getMacWifi() {
        return macWifi;
    }

    public void setMacWifi(String macWifi) {
        this.macWifi = macWifi;
    }

    public String getDeviceId1() {
        return deviceId1;
    }

    public void setDeviceId1(String deviceId1) {
        this.deviceId1 = deviceId1;
    }

    public String getDeviceId2() {
        return deviceId2;
    }

    public void setDeviceId2(String deviceId2) {
        this.deviceId2 = deviceId2;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String getFingerPrint() {
        return fingerPrint;
    }

    public void setFingerPrint(String fingerPrint) {
        this.fingerPrint = fingerPrint;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
