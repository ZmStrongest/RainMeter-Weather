package com.example.xiaoming.rainmeterweather;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.xiaoming.rainmeterweather.database.Province;
import com.example.xiaoming.rainmeterweather.gson.Weather;

import org.litepal.crud.DataSupport;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Province> proList;  //省列表
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //判断数据库是否有地点列表的缓存，如果有，下次启动软件时就不需要再进入选择城市页面
        proList = DataSupport.findAll(Province.class);
        if(proList.size()>0){
            Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }
        setContentView(R.layout.activity_main);

    }
}
