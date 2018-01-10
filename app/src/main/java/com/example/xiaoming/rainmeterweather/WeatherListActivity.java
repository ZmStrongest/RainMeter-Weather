package com.example.xiaoming.rainmeterweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xiaoming.rainmeterweather.database.weatherList;
import com.example.xiaoming.rainmeterweather.gson.Weather;
import com.example.xiaoming.rainmeterweather.util.HttpUtil;
import com.example.xiaoming.rainmeterweather.util.ResolveJSON;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class WeatherListActivity extends AppCompatActivity {
    public ListView listView;
    public FloatingActionButton fbtn_add;
    public List<weatherList> data = new ArrayList<>();
    public DrawerLayout layout_drawer;
    private String WeatherId;
    public WeatherAdapter adapter;
    private List<weatherList> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //简易实现状态栏与背景图片融合在一起，此方法仅限于安卓5.0以上的用户才能实现
        if(Build.VERSION.SDK_INT>=21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);  //设置状态栏为透明色
        }
        setContentView(R.layout.activity_weather_list);
        listView = (ListView) findViewById(R.id.list_view);
        layout_drawer = (DrawerLayout) findViewById(R.id.layout_drawers);
        fbtn_add = (FloatingActionButton) findViewById(R.id.fbtn_add);
        initList();


        //设置listview监听事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                weatherList weatherlist  = data.get(position);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WeatherListActivity.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.putString("weather_id",weatherlist.getWeatherId());
                editor.commit();
                Intent intent = new Intent(WeatherListActivity.this,WeatherActivity.class);
                startActivity(intent);
                WeatherListActivity.this.finish();
            }
        });
        //设置添加按钮的监听事件
        fbtn_add.setOnClickListener(new View.OnClickListener() {
         @Override
            public void onClick(View v) {
               layout_drawer.openDrawer(GravityCompat.START);
               fbtn_add.setVisibility(View.GONE);
         }
     });
    }


    private void initList() {
        list = new ArrayList<>();
        list = DataSupport.findAll(weatherList.class);
        data.clear();
        for(weatherList weatherList : list){
            if (!data.contains(weatherList)){
                data.add(weatherList);
            }
        }
        adapter = new WeatherAdapter(WeatherListActivity.this,R.layout.list_item,data);
        listView.setAdapter(adapter);
    }

    /**
     * 自定义Listview适配器
     */

    public class WeatherAdapter extends ArrayAdapter<weatherList>{
        private int resourceId;
        public WeatherAdapter(Context context,int textViewResourseId,List<weatherList> objects){
            super(context,textViewResourseId,objects);
            resourceId = textViewResourseId;
        }

        public View getView(int position, View convertView, ViewGroup parent){
            weatherList weatherList = getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            TextView tv_city = (TextView) view.findViewById(R.id.tv_city);
            TextView tv_updateTime = (TextView) view.findViewById(R.id.tv_updateTime);
            TextView tv_weather = (TextView) view.findViewById(R.id.tv_weather);
            tv_city.setText(weatherList.getCity());
            tv_updateTime.setText(weatherList.getUpdateTime().split(" ")[1]);
            tv_weather.setText(weatherList.getTemperature() + "℃/" + weatherList.getWeatherType());
            return view;
        }
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
                        Toast.makeText(WeatherListActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                final Weather weather = ResolveJSON.resolveWeaResData(responseData);
                list = new ArrayList<weatherList>();
                list = DataSupport.findAll(weatherList.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather !=null && weather.status.equals("ok")){
                            WeatherId = weather.basic.weatherId;
                            String city = weather.basic.cityName;
                            String updateTime = weather.basic.update.updateTime;
                            String temperature = weather.now.temperature;
                            String weatherType = weather.now.weatherType.info;
                            weatherList weatherList = new weatherList();
                            weatherList.setCity(city);
                            weatherList.setUpdateTime(updateTime);
                            weatherList.setTemperature(temperature);
                            weatherList.setWeatherType(weatherType);
                            weatherList.setWeatherId(WeatherId);
                            weatherList.save();
                            initList();
                        }else {
                            Toast.makeText(WeatherListActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
