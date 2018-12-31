package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DELL on 2018/12/29.
 */
public class Basic {

    @SerializedName("city")
    public String cityName;//城市名

    @SerializedName("id")
    public String id;//城市天气对应的id

    public Update update;//天气的更新时间
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
