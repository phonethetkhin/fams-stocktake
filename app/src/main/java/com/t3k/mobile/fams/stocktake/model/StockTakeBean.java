package com.t3k.mobile.fams.stocktake.model;

public class StockTakeBean {
    private String stockTakeNo;
    private int deviceUploadId;
    private int locationId;

    public String getStockTakeNo() {
        return stockTakeNo;
    }

    public void setStockTakeNo(String stockTakeNo) {
        this.stockTakeNo = stockTakeNo;
    }

    public int getDeviceUploadId() {
        return deviceUploadId;
    }

    public void setDeviceUploadId(int deviceUploadId) {
        this.deviceUploadId = deviceUploadId;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }
}
