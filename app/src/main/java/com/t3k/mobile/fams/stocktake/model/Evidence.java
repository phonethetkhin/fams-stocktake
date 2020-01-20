package com.t3k.mobile.fams.stocktake.model;

public class Evidence {
    private String id;
    private String faNumber;
    private String imagePath;

    public Evidence() {
    }

    public Evidence(String id, String faNumber, String imagePath) {
        this.id = id;
        this.faNumber = faNumber;
        this.imagePath = imagePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFaNumber() {
        return faNumber;
    }

    public void setFaNumber(String faNumber) {
        this.faNumber = faNumber;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
