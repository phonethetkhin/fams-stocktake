package com.t3k.mobile.fams.stocktake.model;

public class AssetBean {
    private int id;
    private int locationId;
    private int assetId;
    private String costCenter;
    private String faNumber;
    private String itemName;
    private String Condition;
    private String category;
    private String brand;
    private String model;
    private int locationFoundId;
    private int unknown;
    private String remark;
    private int taken;
    private int scannedStatus;
    private String stockTakeTime;
    private String uploadTime;
    private String imagePath;

    public AssetBean() {
    }

    public AssetBean(int id, int locationId, int assetId, String costCenter, String faNumber, String itemName, String condition,
                     String category, String brand, String model, int locationFoundId, int unknown, String remark, int taken,
                     int scannedStatus, String stockTakeTime, String uploadTime, String imagePath) {
        this.id = id;
        this.locationId = locationId;
        this.assetId = assetId;
        this.costCenter = costCenter;
        this.faNumber = faNumber;
        this.itemName = itemName;
        Condition = condition;
        this.category = category;
        this.brand = brand;
        this.model = model;
        this.locationFoundId = locationFoundId;
        this.unknown = unknown;
        this.remark = remark;
        this.taken = taken;
        this.scannedStatus = scannedStatus;
        this.stockTakeTime = stockTakeTime;
        this.uploadTime = uploadTime;
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public int getAssetId() {
        return assetId;
    }

    public void setAssetId(int assetId) {
        this.assetId = assetId;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public String getFaNumber() {
        return faNumber;
    }

    public void setFaNumber(String faNumber) {
        this.faNumber = faNumber;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCondition() {
        return Condition;
    }

    public void setCondition(String condition) {
        Condition = condition;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public int getLocationFoundId() {
        return locationFoundId;
    }

    public void setLocationFoundId(int locationFoundId) {
        this.locationFoundId = locationFoundId;
    }

    public int getUnknown() {
        return unknown;
    }

    public void setUnknown(int unknown) {
        this.unknown = unknown;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getTaken() {
        return taken;
    }

    public void setTaken(int taken) {
        this.taken = taken;
    }

    public int getScannedStatus() {
        return scannedStatus;
    }

    public void setScannedStatus(int scannedStatus) {
        this.scannedStatus = scannedStatus;
    }

    public String getStockTakeTime() {
        return stockTakeTime;
    }

    public void setStockTakeTime(String stockTakeTime) {
        this.stockTakeTime = stockTakeTime;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}