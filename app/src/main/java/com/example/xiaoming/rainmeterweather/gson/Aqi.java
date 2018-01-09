package com.example.xiaoming.rainmeterweather.gson;


import com.google.gson.annotations.SerializedName;

/**
 * 由于JSON中的一些字段不太适合直接作为Java字段来命名
 * 因此使用了@SerializedName来让JSON和java字段建立映射关系
 */
public class Aqi {
    public AQIcity city;

    public class AQIcity{
        public String aqi;
        public String pm25;
        public String qlty;
    }
}
