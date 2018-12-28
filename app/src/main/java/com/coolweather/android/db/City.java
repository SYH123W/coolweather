package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by DELL on 2018/12/28.
 */
public class City extends DataSupport{

    private int id;
    private String cityName;//市名
    private int cityCode;//市的代号
    private int provinceId;//市所属省的id值

    public void setId(int id)
    {
        this.id=id;
    }
    public int getId()
    {
        return id;
    }

    public void setCityName(String cityName)
    {
        this.cityName=cityName;
    }
    public String getCityName()
    {
        return cityName;
    }

    public void setCityCode(int cityCode)
    {
        this.cityCode=cityCode;
    }
    public int getCityCode()
    {
        return cityCode;
    }

    public void setProvinceId(int provinceId)
    {
        this.provinceId=provinceId;
    }
    public int getProvinceId()
    {
        return provinceId;
    }
}
