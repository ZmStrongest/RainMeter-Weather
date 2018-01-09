package com.example.xiaoming.rainmeterweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.xiaoming.rainmeterweather.gson.BingPic;
import com.example.xiaoming.rainmeterweather.gson.Forecast;
import com.example.xiaoming.rainmeterweather.gson.Weather;
import com.example.xiaoming.rainmeterweather.service.updateService;
import com.example.xiaoming.rainmeterweather.util.HttpUtil;
import com.example.xiaoming.rainmeterweather.util.ResolveJSON;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView sv_weather;
    private TextView tv_city;
    private TextView tv_updateTime;
    private TextView tv_degree;
    private TextView tv_weatherinfo;
    private LinearLayout layout_forecase;
    private TextView tv_aqi;
    private TextView tv_pm25;
    private TextView tv_comfort;
    private TextView tv_carwash;
    private TextView tv_drsg;
    private TextView tv_flu;
    private TextView tv_sport;
    private TextView tv_trav;
    private TextView tv_uv;
    private TextView tv_air;
    private ImageView img_bing;
    public SwipeRefreshLayout refresh;
    private String WeatherId;  //用来记录刷新后的天气ID
    public DrawerLayout layout_drawer;
    private TextView tv_today;
    private TextView tv_sunrise;
    private TextView tv_sunset;
    private TextView tv_rainpercent;
    private TextView tv_bodytemp;
    private TextView tv_wind;
    private TextView tv_pressure;
    private TextView tv_visible;
    private TextView tv_rainnum;
    private TextView tv_hum;
    private TextView tv_airqty;
    private FloatingActionButton fbtn_add;
    private ImageView img_icon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //简易实现状态栏与背景图片融合在一起，此方法仅限于安卓5.0以上的用户才能实现
        if(Build.VERSION.SDK_INT>=21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);  //设置状态栏为透明色
        }
        setContentView(R.layout.activity_weather);

        sv_weather = (ScrollView) findViewById(R.id.sv_weather);
        tv_city = (TextView) findViewById(R.id.tv_city);
        tv_updateTime = (TextView) findViewById(R.id.tv_updateTime);
        tv_degree = (TextView) findViewById(R.id.tv_degree);
        tv_weatherinfo = (TextView) findViewById(R.id.tv_weatherinfo);
        layout_forecase = (LinearLayout) findViewById(R.id.layout_forecast);
        tv_aqi = (TextView) findViewById(R.id.tv_aqi);
        tv_pm25 = (TextView) findViewById(R.id.tv_pm25);
        tv_comfort = (TextView) findViewById(R.id.tv_comfort);
        tv_carwash = (TextView) findViewById(R.id.tv_carwash);
        tv_drsg = (TextView) findViewById(R.id.tv_drsg);
        tv_flu = (TextView) findViewById(R.id.tv_flu);
        tv_sport = (TextView) findViewById(R.id.tv_sport);
        tv_trav = (TextView) findViewById(R.id.tv_trav);
        tv_uv = (TextView) findViewById(R.id.tv_uv);
        tv_air = (TextView) findViewById(R.id.tv_air);
        img_bing = (ImageView) findViewById(R.id.img_bing);
        layout_drawer = (DrawerLayout) findViewById(R.id.layout_drawer);
        tv_today = (TextView) findViewById(R.id.tv_today);
        tv_sunrise = (TextView) findViewById(R.id.tv_sunrise);
        tv_sunset = (TextView) findViewById(R.id.tv_sunset);
        tv_bodytemp = (TextView) findViewById(R.id.tv_bodytemp);
        tv_rainnum = (TextView) findViewById(R.id.tv_rainnum);
        tv_rainpercent = (TextView) findViewById(R.id.tv_rainpercent);
        tv_visible = (TextView) findViewById(R.id.tv_visible);
        tv_hum = (TextView) findViewById(R.id.tv_hum);
        tv_pressure = (TextView) findViewById(R.id.tv_pressure);
        tv_airqty = (TextView) findViewById(R.id.tv_airquality);
        tv_wind = (TextView) findViewById(R.id.tv_wind);
        fbtn_add = (FloatingActionButton) findViewById(R.id.fbtn_add);
        img_icon = (ImageView) findViewById(R.id.img_icon);

        refresh = (SwipeRefreshLayout)findViewById(R.id.refesh);
        refresh.setColorSchemeResources(R.color.colorPrimary);    //设置下拉刷新按钮的颜色

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = sharedPreferences.getString("weather",null);
        //如果SharedPreferences中有缓存的话，就直接解析天气数据JSON数组，并且直接显示在界面
        if(weatherString != null){
            Weather weather = ResolveJSON.resolveWeaResData(weatherString);
            WeatherId = weather.basic.weatherId;
            showWeather(weather);
        }else {
        //如果没有缓存的话，则需要根据用户选择的城市，用API中的weather_id去服务器请求获取天气数据
            WeatherId = getIntent().getStringExtra("weather_id");
            requestWeather(WeatherId);
        }
        //从必应每日一图的API中获取背景图
        String bingPic = sharedPreferences.getString("bing_pic",null);
        if(bingPic != null){
            Glide.with(this).load(bingPic).into(img_bing);
        }else {
            //如果没有缓存的话，则需要向服务器获取必应背景图片
            LoadBingPic();
        }
        //下拉刷新进度条监听事件
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(WeatherId);
            }
        });
        //切换城市模块的监听事件
        tv_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_drawer.openDrawer(GravityCompat.START);
            }
        });
        //悬浮按钮监听事件

    }


    /**
     * 从服务器中获取背景图片
     */
    private void LoadBingPic() {
        String bingAddress = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
        HttpUtil.sendOkHttpRequest(bingAddress, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPicUrl = response.body().string();
                final BingPic bingObject = ResolveJSON.resolveBingResData(bingPicUrl);
                final String bingPic = "https://cn.bing.com"+bingObject.BingUrl;
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(img_bing);
                    }
                });
            }
        });
    }

    /**
     * 根据weather_id请求返回天气数据
     */
    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=4e7f961f49e249da8fcb3afc0055f23e";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sv_weather.setVisibility(View.INVISIBLE); //无天气信息时隐藏滚动条
                        refresh.setRefreshing(false);//刷新事件结束 隐藏刷新条
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                final Weather weather = ResolveJSON.resolveWeaResData(responseData);
                runOnUiThread(new Runnable() {
                 @Override
                    public void run() {
                     if(weather !=null && weather.status.equals("ok")){
                         SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();//缓存到sharepreference
                         editor.putString("weather",responseData);
                         editor.apply();
                         WeatherId = weather.basic.weatherId;
                         showWeather(weather);
                     }else {
                         Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                     }
                     refresh.setRefreshing(false);//刷新事件结束 隐藏刷新条b
                 }
             });
            }
        });
    }

    /**
     *  显示天气信息到界面上
     */
    private void showWeather(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature;
        String weatherInfo = weather.now.weatherType.info;
        String today = "  今天：天气"+weather.now.weatherType.info+"。气温"+weather.now.temperature+"℃；预计最高气温"
                       +weather.forecastsList.get(0).temperature.maxTemperature+"℃，最低气温"+weather.forecastsList.get(0).temperature.minTemperature+"℃。";
        String sunrise = weather.forecastsList.get(0).sun.sunrise;
        String sunset = weather.forecastsList.get(0).sun.sunset;
        String bobytmp = weather.now.bobyTemp+"℃";
        String hum = weather.now.hum+"%";
        String wind = weather.now.wind.dir+" "+weather.now.wind.spd+"公里/小时 "
                     +weather.now.wind.sc+"\n"+"风力："+weather.forecastsList.get(0).wind.info+"级";
        String pcpn = weather.now.pcpn+"毫米";
        String pop = weather.forecastsList.get(0).pop+"%";
        String pres = weather.now.pres+"百帕";
        String vis = weather.now.vis+"公里";
        String airqty = weather.aqi.city.qlty;
        tv_city.setText(cityName);
        tv_updateTime.setText(updateTime);
        tv_degree.setText(degree+"℃");
        tv_weatherinfo.setText(weatherInfo);
        layout_forecase.removeAllViews();
        tv_today.setText(today);
        tv_sunrise.setText(sunrise);
        tv_sunset.setText(sunset);
        tv_bodytemp.setText(bobytmp);
        tv_hum.setText(hum);
        tv_wind.setText(wind);
        tv_rainnum.setText(pcpn);
        tv_rainpercent.setText(pop);
        tv_pressure.setText(pres);
        tv_visible.setText(vis);
        tv_airqty.setText(airqty);

            img_icon.setImageResource(R.drawable.icons8sun);

        //此模块实现一星期的天气预报，循环输出数据显示在界面上
        for(Forecast forecast : weather.forecastsList){
        View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,layout_forecase,false);
            TextView tv_date = (TextView) view.findViewById(R.id.tv_date);
            TextView tv_info = (TextView) view.findViewById(R.id.tv_info);
            TextView tv_maxTemp = (TextView) view.findViewById(R.id.tv_max);
            TextView tv_minTemp = (TextView) view.findViewById(R.id.tv_min);
            TextView tv_line = (TextView) view.findViewById(R.id.tv_line);

            tv_date.setText(forecast.date);
            tv_info.setText(forecast.weatherType.info);
            tv_maxTemp.setText(forecast.temperature.maxTemperature+"℃");
            tv_line.setText("～");
            tv_minTemp.setText(forecast.temperature.minTemperature+"℃");
            layout_forecase.addView(view);
        }
        if (weather.aqi != null){
            tv_aqi.setText(weather.aqi.city.aqi);
            tv_pm25.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度["+weather.suggestion.comfort.level+"]：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数["+weather.suggestion.carWash.level+"]：" + weather.suggestion.carWash.info;
        String dress = "穿衣指数["+weather.suggestion.dress.level+"]：" + weather.suggestion.dress.info;
        String flu = "感冒指数["+weather.suggestion.flu.level+"]：" + weather.suggestion.flu.info;
        String sport = "运动指数["+weather.suggestion.sport.level+"]：" + weather.suggestion.sport.info;
        String travel = "旅游指数["+weather.suggestion.travel.level+"]：" + weather.suggestion.travel.info;
        String uv = "紫外线指数["+weather.suggestion.ult_ray.level+"]：" + weather.suggestion.ult_ray.info;
        String air = "空气污染扩散条件指数["+weather.suggestion.air.level+"]：" + weather.suggestion.air.info;
        tv_comfort.setText(comfort);
        tv_carwash.setText(carWash);
        tv_drsg.setText(dress);
        tv_flu.setText(flu);
        tv_sport.setText(sport);
        tv_trav.setText(travel);
        tv_uv.setText(uv);
        tv_air.setText(air);
        layout_forecase.setVisibility(View.VISIBLE);

        //加入后台更新的服务
        Intent intent = new Intent(this,updateService.class);
        startService(intent);
    }
}
