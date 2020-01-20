package com.t3k.mobile.fams.stocktake.model;

public class LocationsBean {
    private int locationId;
    private String parentName;
    private String code;
    private String name;
    private int download;
    private int upload;


    public LocationsBean() {
    }

    public LocationsBean(int locationId, String parentName, String code, String name, int download, int upload) {
        this.locationId = locationId;
        this.parentName = parentName;
        this.code = code;
        this.name = name;
        this.download = download;
        this.upload = upload;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDownload() {
        return download;
    }

    public void setDownload(int download) {
        this.download = download;
    }

    public int getUpload() {
        return upload;
    }

    public void setUpload(int upload) {
        this.upload = upload;
    }
}
