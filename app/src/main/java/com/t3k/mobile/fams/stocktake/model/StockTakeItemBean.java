package com.t3k.mobile.fams.stocktake.model;

public class StockTakeItemBean {
    private int stockTakeId;
    private int assetId;
    private boolean taken;
    private boolean scanned;
    private boolean unknown;
    private String remark;

    public int getStockTakeId() {
        return stockTakeId;
    }

    public void setStockTakeId(int stockTakeId) {
        this.stockTakeId = stockTakeId;
    }

    public int getAssetId() {
        return assetId;
    }

    public void setAssetId(int assetId) {
        this.assetId = assetId;
    }

    public boolean isTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }

    public boolean isScanned() {
        return scanned;
    }

    public void setScanned(boolean scanned) {
        this.scanned = scanned;
    }

    public boolean isUnknown() {
        return unknown;
    }

    public void setUnknown(boolean unknown) {
        this.unknown = unknown;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
