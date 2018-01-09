package com.example.xiaoming.rainmeterweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 由于JSON中的一些字段可能不太适合直接作为Java字段来命名
 * 因此使用了@SerializedName来让JSON和java字段建立映射关系
 */
public class Forecast {
    public String date;

    @SerializedName("tmp")
    public Temperature temperature;
    @SerializedName("cond")
    public WeatherType weatherType;//天气类别
    @SerializedName("astro")
    public Sun sun;         //日出日落
    public String pop;      //降水概率
    public Wind wind;


    public class Temperature{
        @SerializedName("max")
        public String maxTemperature;
        @SerializedName("min")
        public String minTemperature;
    }

    public class WeatherType{
        @SerializedName("txt_d")
        public String info;
    }

    public class Sun {
        @SerializedName("sr")
        public String sunrise;
        @SerializedName("ss")
        public String sunset;
    }

    public class Wind {
        @SerializedName("sc")
        public String info;
    }
}
