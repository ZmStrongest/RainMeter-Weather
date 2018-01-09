package com.example.xiaoming.rainmeterweather.gson;


import com.google.gson.annotations.SerializedName;

/**
 * 由于JSON中的一些字段可能不太适合直接作为Java字段来命名
 * 因此使用了@SerializedName来让JSON和java字段建立映射关系
 */
public class Now {
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond")
    public WeatherType weatherType;
    @SerializedName("fl")
    public String bobyTemp;   //体感温度
    public String hum;        //相对湿度
    public String pcpn;      //降水量
    public String pres;      //大气压强
    public String vis;       //能见度

    @SerializedName("wind")
    public Wind wind;


    public class Wind{
        @SerializedName("dir")
        public String dir;
        @SerializedName("sc")
        public String sc;
        @SerializedName("spd")
        public String spd;
    }

    public class WeatherType{
        @SerializedName("txt")
        public String info;
    }

}
