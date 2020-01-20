package com.t3k.mobile.fams.stocktake.model;

public class DownloadInfo {
    private int id;
    private int downloadUserId;
    private String downloadDate;
    private String locationId;

    public DownloadInfo() {
    }

    public DownloadInfo(int id, int downloadUserId, String downloadDate, String locationId) {
        this.id = id;
        this.downloadUserId = downloadUserId;
        this.downloadDate = downloadDate;
        this.locationId = locationId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDownloadUserId() {
        return downloadUserId;
    }

    public void setDownloadUserId(int downloadUserId) {
        this.downloadUserId = downloadUserId;
    }

    public String getDownloadDate() {
        return downloadDate;
    }

    public void setDownloadDate(String downloadDate) {
        this.downloadDate = downloadDate;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }
}
