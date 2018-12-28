package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by DELL on 2018/12/28.
 */
public class County extends DataSupport{

    private int id;
    private String countyName;//县名
    private String weatherId;//县所对应的天气
    private int cityId;//县所对应的市

    public void setId(int id)
    {
        this.id=id;
    }
    public int getId()
    {
        return id;
    }

    public void setCountyName(String countyName)
    {
        this.countyName=countyName;
    }
    public String getCountyName()
    {
        return countyName;
    }

    public void setWeatherId(String weatherId)
    {
        this.weatherId=weatherId;
    }
    public String getWeatherId()
    {
        return weatherId;
    }

    public void setCityId(int cityId)
    {
        this.cityId=cityId;
    }
    public int getCityId()
    {
        return cityId;
    }
}
