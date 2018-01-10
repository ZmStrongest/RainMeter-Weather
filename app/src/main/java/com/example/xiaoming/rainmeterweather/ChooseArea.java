package com.example.xiaoming.rainmeterweather;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xiaoming.rainmeterweather.database.City;
import com.example.xiaoming.rainmeterweather.database.County;
import com.example.xiaoming.rainmeterweather.database.Province;
import com.example.xiaoming.rainmeterweather.util.HttpUtil;
import com.example.xiaoming.rainmeterweather.util.ResolveJSON;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ChooseArea extends Fragment {
    /**
     * 用静态数据来表示当前选择城市菜单的层数
     */
    public static final int level_province = 0;
    public static final int level_city = 1;
    public static final int level_conuty = 2;

    private ProgressDialog progressDialog;
    private TextView tv_title;
    private Button btn_back;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> data = new ArrayList<>();

    private List<Province> proList;  //省列表
    private List<City> cityList;     //市列表
    private List<County> countyList; //县级列表
    private Province selectedProvince;   //被选中的省份
    private City selectedCity;           //被选中的城市
    private int currentLevel;           //当前层数

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_area,container,false);
        tv_title = (TextView)view.findViewById(R.id.tv_title);
        btn_back = (Button)view.findViewById(R.id.btn_back);
        listView = (ListView)view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,data);
        listView.setAdapter(adapter);
        return view;
    }

    public void onActivityCreated(Bundle saveInstanceState){
        super.onActivityCreated(saveInstanceState);

        /**
         * 设置listview的监听事件
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel == level_province){
                    selectedProvince = proList.get(position);
                    showCities();
                }
                else if (currentLevel == level_city){
                    selectedCity = cityList.get(position);
                    showCounties();
                }else if (currentLevel == level_conuty){
                    String weatherId = countyList.get(position).getWeatherId();
                    //使用java关键字instanceof来判断一个对象是否属于某个类的实例，判断选择城市是通过主活动选择还是滑动菜单选择
                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if (getActivity() instanceof WeatherActivity){
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.layout_drawer.closeDrawers();
                        activity.refresh.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }else if(getActivity() instanceof WeatherListActivity){
                        WeatherListActivity activity = (WeatherListActivity) getActivity();
                        activity.layout_drawer.closeDrawers();
                        activity.fbtn_add.setVisibility(View.VISIBLE);
                        activity.requestWeather(weatherId);
                        Intent intent = new Intent(getActivity(),WeatherListActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
        /**
         * 设置返回按钮的监听事件
         */
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == level_city){
                    showProvince();
                }
                else if (currentLevel == level_conuty){
                    showCities();
                }
            }
        });
        showProvince();
    }
    /**
     * 从数据库或者服务器中查询全国省份
     */
    private void showProvince(){
        tv_title.setText("选择城市");
        btn_back.setVisibility(View.GONE);  //在选择省份的时候，隐藏返回按钮
        proList = DataSupport.findAll(Province.class);
        if(proList.size()>0){
            data.clear();
            for(Province province : proList){
                data.add(province.getProName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = level_province;
        }else {
            String address = "http://guolin.tech/api/china";
            requestFromServer(address,"province");
        }
    }

    /**
     * 从数据库或者服务器中查询全国城市
     */
    private void showCities(){
        tv_title.setText(selectedProvince.getProName());
        btn_back.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("proId = ?",String.valueOf(selectedProvince.getId())).find(City.class);  //litepal的SQL语句 查找数据库中是否存在信息
        if(cityList.size()>0){
            data.clear();
            for(City city : cityList){
                data.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = level_city;
        }else {
            int provinceCode = selectedProvince.getProCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            requestFromServer(address,"city");
        }
    }

    /**
     * 从数据库或者服务器中查询全国市县
     */
    private void showCounties(){
        tv_title.setText(selectedCity.getCityName());
        btn_back.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityId = ?",String.valueOf(selectedCity.getId())).find(County.class);
        if(countyList.size()>0){
            data.clear();
            for(County county : countyList){
                data.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = level_conuty;
        }else {
            int provinceCode = selectedProvince.getProCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            requestFromServer(address,"county");
        }
    }

    /**
     * 根据传入的地址和查询的级别从服务器中查询数据
     */
    private void requestFromServer(String address, final String level) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                boolean result = false;
                if (level.equals("province")){
                    result = ResolveJSON.resolveProResData(responseData);
                }else if(level.equals("city")){
                    result = ResolveJSON.resolveCityResData(responseData,selectedProvince.getId());
                }else if (level.equals("county")){
                    result = ResolveJSON.resolveCouResData(responseData,selectedCity.getId());
                }
                //如果从服务器返回的boolean值是true，说明接收到了服务器传输的数据，在页面上显示
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if (level.equals("province")){
                                showProvince();
                            }else if(level.equals("city")){
                                showCities();
                            }else if (level.equals("county")){
                                showCounties();
                            }
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call call, IOException e) {
                //通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("提示");
            progressDialog.setMessage("正在加载城市...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
