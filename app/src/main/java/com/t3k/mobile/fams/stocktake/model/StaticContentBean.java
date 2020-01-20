package com.t3k.mobile.fams.stocktake.model;

public class StaticContentBean {
    private String fileName;
    private String filePath;
    private String fileSize;
    private FileType fileType;

    public enum FileType {
        IMAGE, TEXT, PDF, EXCEL, ZIP, UNKNOWN;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }
}
