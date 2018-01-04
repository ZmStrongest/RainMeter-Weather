package com.example.xiaoming.rainmeterweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 *用于从服务器中获取数据请求
 */

public class HttpUtil {
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
