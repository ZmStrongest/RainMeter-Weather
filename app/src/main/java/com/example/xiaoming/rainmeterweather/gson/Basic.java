package com.example.xiaoming.rainmeterweather.gson;


import com.google.gson.annotations.SerializedName;

public class Basic {
    /**
     * 由于JSON中的一些字段可能不太适合直接作为Java字段来命名
     * 因此使用了@SerializedName来让JSON和java字段建立映射关系
     */
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
