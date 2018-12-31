package com.coolweather.android;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import java.io.IOException;
import java.util.StringTokenizer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;
    private TextView titleCity;//城市名
    private TextView titleUpdateTime;//更新时间
    private TextView degreeText;//当前气温
    private TextView weatherInfoText;//天气概况
    private LinearLayout forecastLayout;//未来几天天气的布局，根据服务器返回的数据在代码中动态添加的
    private TextView aqiText;//aqi指数
    private TextView pm25Text;//pm2.5指数
    private TextView comfortText;//舒适度
    private TextView carWashText;//洗车指数
    private TextView sportText;//运动建议

    private ImageView bingPicImg;//天气界面的背景图片

    public SwipeRefreshLayout swipeRefresh;//下拉刷新功能
    private String mweatherId;

    public DrawerLayout drawerLayout;//滑动菜单
    private Button navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=21)
        {
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);

        weatherLayout=(ScrollView)findViewById(R.id.weather_layout);
        titleCity=(TextView)findViewById(R.id.title_city);
        titleUpdateTime=(TextView)findViewById(R.id.title_update_time);
        degreeText=(TextView)findViewById(R.id.degree_text);
        weatherInfoText=(TextView)findViewById(R.id.weather_info_text);
        forecastLayout=(LinearLayout)findViewById(R.id.forecast_layout);
        aqiText=(TextView)findViewById(R.id.aqi_text);
        pm25Text=(TextView)findViewById(R.id.pm25_text);
        comfortText=(TextView)findViewById(R.id.confort_text);
        carWashText=(TextView)findViewById(R.id.car_wash_text);
        sportText=(TextView)findViewById(R.id.sport_text);
        bingPicImg=(ImageView)findViewById(R.id.bing_pic_img);

        swipeRefresh=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);//设置进度动画中使用的颜色资源

        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        navButton=(Button)findViewById(R.id.nav_button);

        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
        String weatherString=prefs.getString("weather",null);
        if (weatherString!=null)
        {
            //有缓存时直接解析天气数据
            Weather weather=Utility.handleWeatherResponse(weatherString);
            mweatherId=weather.basic.weatherId;
            showWeatherInfo(weather);
        }
        else
        {
            //无缓存时去服务器请求数据
            mweatherId=getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);//请求数据时，将scrollview隐藏起来
            requestWeather(mweatherId);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mweatherId);
            }
        });

        String bingPic=prefs.getString("bing_pic",null);
        if (bingPic!=null)
        {
            Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
        }
        else
        {
            loadBingPic();//加载图片
        }

        //使用按钮来打开滑动菜单
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    /*根据天气id请求城市天气信息*/
    public void requestWeather(final String weatherId)
    {
        String weatherUrl="https://free-api.heweather.com/s6/weather/forecast?location="+weatherId+"&key=93fdfc5fe4da47538116f169d7af4393";
        HttpUtil.sendOkHttpRequest(weatherUrl,new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);//通知更新结束
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String responseText=response.body().string();
                final Weather weather= Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (weather!=null&&"ok".equals(weather.status))
                        {
                            SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            mweatherId=weather.basic.weatherId;
                            showWeatherInfo(weather);
                        }
                        else {

                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);//通知更新结束
                    }
                });
            }
        });

        loadBingPic();
    }

    /*加载必应每日一图*/
    private void loadBingPic()
    {
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                //更新界面
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }
    /*处理并展示Weather实体类中的数据*/
    private void showWeatherInfo(Weather weather)
    {
        String cityName=weather.basic.cityName;
        String updateTime=weather.basic.update.updateTime.split(" ")[1];//取出字符串解析成数组后的第二个元素的值
        String degree=weather.now.temperature+"℃";
        String weatherInfo=weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();//天气预报布局中的所有子布局都移除
        for (Forecast forecast:weather.forecastList)
        {
            //在天气预报布局中动态的引入子布局
            View view= LayoutInflater.from(WeatherActivity.this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText=(TextView)findViewById(R.id.date_text);//天气预报的日期
            TextView infoText=(TextView)findViewById(R.id.info_text);//天气概况
            TextView maxText=(TextView)findViewById(R.id.max_text);//最高气温
            TextView minText=(TextView)findViewById(R.id.min_text);//最低气温
            forecastLayout.addView(view);//添加子视图
        }
        if (weather.aqi!=null)
        {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort="舒适度: "+weather.suggestion.comfort.info;
        String casrWash="洗车指数: "+weather.suggestion.carWash.info;
        String sport="运动建议: "+weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(casrWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);//让scrollciew显示出来
    }
}
