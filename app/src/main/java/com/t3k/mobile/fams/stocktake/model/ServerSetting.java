package com.t3k.mobile.fams.stocktake.model;

public class ServerSetting {
    private int id;
    private String settingGroup;
    private String settingName;
    private String settingValue;

    public ServerSetting() {
    }

    public ServerSetting(int id, String settingGroup, String settingName, String settingValue) {
        this.id = id;
        this.settingGroup = settingGroup;
        this.settingName = settingName;
        this.settingValue = settingValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSettingGroup() {
        return settingGroup;
    }

    public void setSettingGroup(String settingGroup) {
        this.settingGroup = settingGroup;
    }

    public String getSettingName() {
        return settingName;
    }

    public void setSettingName(String settingName) {
        this.settingName = settingName;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }
}
