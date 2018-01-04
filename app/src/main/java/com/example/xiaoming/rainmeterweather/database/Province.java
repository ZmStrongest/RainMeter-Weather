package com.example.xiaoming.rainmeterweather.database;


import org.litepal.crud.DataSupport;



public class Province extends DataSupport {
    private int id;
    private String proName;
    private int proCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public int getProCode() {
        return proCode;
    }

    public void setProCode(int proCode) {
        this.proCode = proCode;
    }
}
