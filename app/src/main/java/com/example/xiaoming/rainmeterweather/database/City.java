package com.example.xiaoming.rainmeterweather.database;


import org.litepal.crud.DataSupport;

/**
 * Created by xiaoming on 2018/1/3.
 */

public class City extends DataSupport {
    private int id;
    private String cityName;
    private int cityCode;
    private int proId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProId() {
        return proId;
    }

    public void setProId(int proId) {
        this.proId = proId;
    }
}
