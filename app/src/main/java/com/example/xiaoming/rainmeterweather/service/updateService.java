package com.example.xiaoming.rainmeterweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.example.xiaoming.rainmeterweather.gson.BingPic;
import com.example.xiaoming.rainmeterweather.gson.Weather;
import com.example.xiaoming.rainmeterweather.util.HttpUtil;
import com.example.xiaoming.rainmeterweather.util.ResolveJSON;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class updateService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent,int flags,int startId){
        updateWeather();
        updateBingPic();

        //利用安卓的Alarm机制来设置定时任务，让软件进行每4小时后台更新一次天气信息
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int Hour = 4 * 60 * 60 * 1000;  //4小时毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + Hour;
        Intent alarmIntent = new Intent(this,updateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,alarmIntent,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);
    }

    /**
     * 后台自动更新天气数据
     */
    private void updateWeather() {
        //如果SharedPreferences中有缓存的话，就直接解析天气数据JSON数组，并且直接显示在界面
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = sharedPreferences.getString("weather",null);
        //如果没有缓存的话，则需要根据用户选择的城市，用API中的weather_id去服务器请求获取天气数据
        if(weatherString != null){
            final Weather weather = ResolveJSON.resolveWeaResData(weatherString);
            final String WeatherId = weather.basic.weatherId;
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + WeatherId + "&key=4e7f961f49e249da8fcb3afc0055f23e";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                  String responseData = response.body().string();
                  Weather weather = ResolveJSON.resolveWeaResData(responseData);
                  if(weather != null && weather.status.equals("ok")){
                      SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(updateService.this).edit();
                      editor.putString("weather",responseData);
                      editor.apply();
                  }
                }
            });
        }
    }

    /**
     * 后台自动更新必应每日一图
     */
    private void updateBingPic() {
        String bingAddress = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
        HttpUtil.sendOkHttpRequest(bingAddress, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                BingPic bingObject = ResolveJSON.resolveBingResData(responseData);
                String bingPic = "https://cn.bing.com"+bingObject.BingUrl;
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(updateService.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
            }
        });
    }
}
