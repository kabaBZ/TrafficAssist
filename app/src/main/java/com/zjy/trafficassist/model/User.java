package com.zjy.trafficassist.model;

import com.amap.api.maps.model.LatLng;

/**
 * Created by ZJY on 2016/4/14.
 */
public class User {

    private String username;
    private String password;
    private String realname;
    private LatLng location;

    private String driverNumber;
    private String driverType;
    private String carNumber;
    private String sex;

    public User(){}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.realname = username;

        this.driverNumber = "";
        this.driverType = "";
        this.carNumber = "";
    }

    public String getDriverNumber() {
        return driverNumber;
    }

    public void setDriverNumber(String driverNumber) {
        this.driverNumber = driverNumber;
    }

    public String getDriverType() {
        return driverType;
    }

    public void setDriverType(String driverType) {
        this.driverType = driverType;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRealname() {
        return realname;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
