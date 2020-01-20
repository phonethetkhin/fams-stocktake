package com.t3k.mobile.fams.stocktake.model;

public class User {
    private int id;
    private int userId;
    private String loginId;
    private String name;
    private String password;
    private String joinedDate;
    private String lastLoginDate;

    public User() {
    }

    public User(int id, int userId, String loginId, String name, String password, String joinedDate, String lastLoginDate) {
        this.id = id;
        this.userId = userId;
        this.loginId = loginId;
        this.name = name;
        this.password = password;
        this.joinedDate = joinedDate;
        this.lastLoginDate = lastLoginDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(String joinedDate) {
        this.joinedDate = joinedDate;
    }

    public String getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }
}
