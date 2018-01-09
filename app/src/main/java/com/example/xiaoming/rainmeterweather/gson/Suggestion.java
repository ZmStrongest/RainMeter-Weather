package com.example.xiaoming.rainmeterweather.gson;


import com.google.gson.annotations.SerializedName;

/**
 * 由于JSON中的一些字段可能不太适合直接作为Java字段来命名
 * 因此使用了@SerializedName来让JSON和java字段建立映射关系
 */
public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;  //舒适指数
    @SerializedName("cw")
    public CarWash carWash;   //洗车指数;
    public Sport sport;       //运动指数
    @SerializedName("drsg")    //穿衣指数
    public Dress dress;
    public Flu flu;            //感冒指数
    @SerializedName("trav")
    public Travel travel;      //旅游指数
    @SerializedName("uv")
    public Ult_ray ult_ray;      //紫外线指数
    public Air air;            //空气扩散条件指数


    public class Comfort{
        @SerializedName("txt")
        public String info;
        @SerializedName("brf")
        public String level;
    }

    public class CarWash{
        @SerializedName("txt")
        public String info;
        @SerializedName("brf")
        public String level;
    }

    public class Sport{
        @SerializedName("txt")
        public String info;
        @SerializedName("brf")
        public String level;
    }

    public class Dress {
        @SerializedName("txt")
        public String info;
        @SerializedName("brf")
        public String level;
    }

    public class Flu {
        @SerializedName("txt")
        public String info;
        @SerializedName("brf")
        public String level;
    }

    public class Travel {
        @SerializedName("txt")
        public String info;
        @SerializedName("brf")
        public String level;
    }

    public class Ult_ray {
        @SerializedName("txt")
        public String info;
        @SerializedName("brf")
        public String level;
    }

    public class Air {
        @SerializedName("txt")
        public String info;
        @SerializedName("brf")
        public String level;
    }
}
