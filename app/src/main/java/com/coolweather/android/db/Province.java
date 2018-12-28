package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by DELL on 2018/12/28.
 */
public class Province extends DataSupport{

    private int id;
    private String provinceName;//省的名字
    private int provinceCode;//省的代号

    public void setId(int id)
    {
        this.id=id;
    }
    public int getId()
    {
        return id;
    }

    public void setProvinceName(String provinceName)
    {
        this.provinceName=provinceName;
    }
    public String getProvinceName()
    {
        return provinceName;
    }

    public void setProvinceCode(int provinceCode)
    {
        this.provinceCode=provinceCode;
    }
    public int getProvinceCode()
    {
        return provinceCode;
    }
}
