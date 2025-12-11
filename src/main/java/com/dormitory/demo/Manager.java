package com.dormitory.demo;

public class Manager {
    private String managerId;
    private String mName;
    private String phone;
    private String role;

    public Manager(String managerId, String mName, String phone, String role) {
        this.managerId = managerId;
        this.mName = mName;
        this.phone = phone;
        this.role = role;
    }

    public String getManagerId() { return managerId; }
    public String getMName() { return mName; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }
}