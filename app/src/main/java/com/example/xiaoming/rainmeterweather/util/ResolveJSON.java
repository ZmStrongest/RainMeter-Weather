package com.example.xiaoming.rainmeterweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.xiaoming.rainmeterweather.database.City;
import com.example.xiaoming.rainmeterweather.database.County;
import com.example.xiaoming.rainmeterweather.database.Province;
import com.example.xiaoming.rainmeterweather.gson.BingPic;
import com.example.xiaoming.rainmeterweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 解析和处理服务器返回的省级数据
 */

public class ResolveJSON {

    public static boolean resolveProResData(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i=0;i<allProvinces.length();i++){
                    JSONObject proObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProName(proObject.getString("name"));
                    province.setProCode(proObject.getInt("id"));
                    Log.i("FDSA",proObject.getString("name"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean resolveCityResData(String response,int proId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i=0;i<allCities.length();i++){
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProId(proId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean resolveCouResData(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i=0;i<allCounties.length();i++){
                    JSONObject couObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(couObject.getString("name"));
                    county.setWeatherId(couObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 用GSON解析和处理服务器返回的天气信息数据
     */
    public static Weather resolveWeaResData(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherData = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherData,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 用GSON解析和处理服务器返回的必应每日一图的数据
     */
    public static BingPic resolveBingResData(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("images");
            String bingData = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(bingData,BingPic.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
