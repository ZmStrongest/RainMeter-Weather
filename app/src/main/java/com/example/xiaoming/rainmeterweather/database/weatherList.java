package com.example.xiaoming.rainmeterweather.database;


import org.litepal.crud.DataSupport;

public class weatherList extends DataSupport {
    private int id;
    private String city;
    private String updateTime;
    private String temperature;
    private String weatherType;
    private String weatherId;

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setWeatherType(String weatherType) {
        this.weatherType = weatherType;
    }


    public String getCity() {
        return city;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getWeatherType() {
        return weatherType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        weatherList that = (weatherList) o;
        if (city != null ? !city.equals(that.city) : that.city != null) return false;
        return true;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        result = 31 * result + (temperature != null ? temperature.hashCode() : 0);
        result = 31 * result + (weatherType != null ? weatherType.hashCode() : 0);
        result = 31 * result + (weatherId != null ? weatherId.hashCode() : 0);
        return result;
    }
}
